package com.talhachaudhry.medicine.callbacks;

import com.talhachaudhry.medicine.models.ManageMedicineModel;

public interface OnViewMedicineDetail {
    void onViewMedicineDetailClicked(ManageMedicineModel model);

    void deleteMedicine(ManageMedicineModel model);

    void updateMedicine(ManageMedicineModel model);
}
