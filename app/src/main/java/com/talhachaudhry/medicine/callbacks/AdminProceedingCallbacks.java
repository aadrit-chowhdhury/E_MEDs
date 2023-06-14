package com.talhachaudhry.medicine.callbacks;

import com.talhachaudhry.medicine.models.OrderModel;

public interface AdminProceedingCallbacks {
    void onCancelClicked(OrderModel model);

    void onDispatchClicked(OrderModel model);

    void onPendingClicked(OrderModel model);

    void onClickedToView(OrderModel model);
}
