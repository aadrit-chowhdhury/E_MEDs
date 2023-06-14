package com.talhachaudhry.medicine.callbacks;

import com.talhachaudhry.medicine.models.OrderModel;

public interface AdminDispatchOrdersCallback {
    void onCancelClicked(OrderModel model);

    void onCompleteClicked(OrderModel model);

    void onClickedToView(OrderModel model);

    void putInProceeding(OrderModel orderModel);
}
