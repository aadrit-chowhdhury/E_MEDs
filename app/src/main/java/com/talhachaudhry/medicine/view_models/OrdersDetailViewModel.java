package com.talhachaudhry.medicine.view_models;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.talhachaudhry.medicine.models.OrderModel;
import com.talhachaudhry.medicine.utils.NodesNames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OrdersDetailViewModel extends ViewModel {
    MutableLiveData<List<OrderModel>> proceedingOrdersLiveData;
    MutableLiveData<List<OrderModel>> dispatchOrdersLiveData;
    MutableLiveData<List<OrderModel>> pendingOrdersLiveData;
    MutableLiveData<List<OrderModel>> completeOrdersLiveData;
    MutableLiveData<List<OrderModel>> cancelledOrdersLiveData;
    MutableLiveData<OrderModel> currentlyActiveOrder;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    public MutableLiveData<List<OrderModel>> getCompleteOrdersLiveData() {
        if (completeOrdersLiveData == null) {
            completeOrdersLiveData = new MutableLiveData<>();
            completeOrdersLiveData.setValue(new ArrayList<>());
            getOrders(NodesNames.COMPLETE_NODE_NAME.getName(), completeOrdersLiveData);
        }
        return completeOrdersLiveData;
    }

    public MutableLiveData<OrderModel> getCurrentlyActiveOrder() {
        if (currentlyActiveOrder == null) {
            currentlyActiveOrder = new MutableLiveData<>();
        }
        return currentlyActiveOrder;
    }

    public MutableLiveData<List<OrderModel>> getPendingOrdersLiveData() {
        if (pendingOrdersLiveData == null) {
            pendingOrdersLiveData = new MutableLiveData<>();
            pendingOrdersLiveData.setValue(new ArrayList<>());
            getOrders(NodesNames.PENDING_NODE_NAME.getName(), pendingOrdersLiveData);
        }
        return pendingOrdersLiveData;
    }

    public MutableLiveData<List<OrderModel>> getCancelledOrdersLiveData() {
        if (cancelledOrdersLiveData == null) {
            cancelledOrdersLiveData = new MutableLiveData<>();
            cancelledOrdersLiveData.setValue(new ArrayList<>());
            getOrders(NodesNames.CANCEL_NODE_NAME.getName(), cancelledOrdersLiveData);
        }
        return cancelledOrdersLiveData;
    }

    public MutableLiveData<List<OrderModel>> getDispatchOrdersLiveData() {
        if (dispatchOrdersLiveData == null) {
            dispatchOrdersLiveData = new MutableLiveData<>();
            dispatchOrdersLiveData.setValue(new ArrayList<>());
            getOrders(NodesNames.DISPATCH_NODE_NAME.getName(), dispatchOrdersLiveData);
        }
        return dispatchOrdersLiveData;
    }

    public MutableLiveData<List<OrderModel>> getProceedingOrdersLiveData() {
        if (proceedingOrdersLiveData == null) {
            proceedingOrdersLiveData = new MutableLiveData<>();
            proceedingOrdersLiveData.setValue(new ArrayList<>());
            getOrders(NodesNames.PROCEEDING_NODE_NAME.getName(), proceedingOrdersLiveData);
        }
        return proceedingOrdersLiveData;
    }

    public void setCurrentlyActiveOrder(OrderModel orderModel) {
        if (getCurrentlyActiveOrder() != null) {
            currentlyActiveOrder.setValue(orderModel);
        }
    }

    private void getOrders(String nodeName, MutableLiveData<List<OrderModel>> listName) {
        try {
            database.getReference().
                    child(NodesNames.MAIN_NODE_NAME.getName()).
                    child(auth.getUid()).
                    child(nodeName).
                    addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snapshot2 : snapshot.getChildren()) {
                                OrderModel model = snapshot2.getValue(OrderModel.class);
                                updateOrdersList(listName, model);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // do nothing
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void editOrder() {
        OrderModel orderModel = getCurrentlyActiveOrder().getValue();
        database.getReference()
                .child(NodesNames.MAIN_NODE_NAME.getName())
                .child(auth.getUid())
                .child(NodesNames.PENDING_NODE_NAME.getName())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            OrderModel currentModel = snapshot1.getValue(OrderModel.class);
                            if (currentModel.getOrderId().equals(orderModel.getOrderId())) {
                                Map<String, Object> values = new HashMap<>();
                                values.put("ordersList", orderModel.getOrdersList());
                                database.getReference()
                                        .child(NodesNames.MAIN_NODE_NAME.getName())
                                        .child(auth.getUid())
                                        .child(NodesNames.PENDING_NODE_NAME.getName())
                                        .child(Objects.requireNonNull(snapshot1.getKey()))
                                        .updateChildren(values);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // do nothing
                    }
                });
    }

    public void deleteOrder(OrderModel orderModel) {
        database.getReference()
                .child(NodesNames.MAIN_NODE_NAME.getName())
                .child(auth.getUid())
                .child(NodesNames.PENDING_NODE_NAME.getName())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            OrderModel currentModel = snapshot1.getValue(OrderModel.class);
                            if (currentModel.getOrderId().equals(orderModel.getOrderId())) {
                                database.getReference()
                                        .child(NodesNames.MAIN_NODE_NAME.getName())
                                        .child(auth.getUid())
                                        .child(NodesNames.PENDING_NODE_NAME.getName()).
                                        child(Objects.requireNonNull(snapshot1.getKey())).
                                        removeValue().addOnCompleteListener(runnable ->
                                                onOrderDeleted(orderModel));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // do nothing
                    }
                });
    }

    private void onOrderDeleted(OrderModel orderModel) {
        if (pendingOrdersLiveData != null) {
            List<OrderModel> list =
                    new ArrayList<>(Objects.
                            requireNonNull(pendingOrdersLiveData.
                                    getValue()));
            list.remove(orderModel);
            orderModel.setStatus(NodesNames.CANCEL_NODE_NAME.getName());
            pendingOrdersLiveData.setValue(list);
            database.getReference()
                    .child(NodesNames.MAIN_NODE_NAME.getName())
                    .child(auth.getUid())
                    .child(NodesNames.CANCEL_NODE_NAME.getName())
                    .push()
                    .setValue(orderModel).
                    addOnCompleteListener(runnable -> updateOrdersList(cancelledOrdersLiveData, orderModel));
        }
    }

    private void updateOrdersList(MutableLiveData<List<OrderModel>> listName, OrderModel orderModel) {
        if (listName != null) {
            List<OrderModel> list = new ArrayList<>(Objects.requireNonNull(listName.getValue()));
            list.add(orderModel);
            listName.setValue(list);
        }
    }
}
