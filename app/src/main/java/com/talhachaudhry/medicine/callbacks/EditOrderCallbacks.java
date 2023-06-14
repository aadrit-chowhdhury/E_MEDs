package com.talhachaudhry.medicine.callbacks;

import com.talhachaudhry.medicine.models.OrderModel;

public interface EditOrderCallbacks {

    void onViewOrderClicked(OrderModel model);

    void onDeleteOrderClicked(OrderModel model);

    void onUpdateOrderClicked(OrderModel model);
}
