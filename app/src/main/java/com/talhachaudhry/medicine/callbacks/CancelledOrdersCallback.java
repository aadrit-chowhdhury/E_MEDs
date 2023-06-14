package com.talhachaudhry.medicine.callbacks;

import com.talhachaudhry.medicine.models.OrderModel;

public interface CancelledOrdersCallback {
    void onItemClicked(OrderModel model);
}
