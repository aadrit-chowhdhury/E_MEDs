package com.talhachaudhry.medicine.callbacks;

import com.talhachaudhry.medicine.models.OrderModel;

public interface AdminPendingOrderCallbacks {
    void onProceedOrderClicker(OrderModel model);

    void onDispatchOrderClicker(OrderModel model);

    void onCancelOrderClicker(OrderModel model);

    void onClickedToView(OrderModel orderModel);
}
