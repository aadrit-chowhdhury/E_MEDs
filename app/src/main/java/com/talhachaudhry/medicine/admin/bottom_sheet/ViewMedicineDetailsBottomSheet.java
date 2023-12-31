package com.talhachaudhry.medicine.admin.bottom_sheet;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.talhachaudhry.medicine.R;
import com.talhachaudhry.medicine.databinding.FragmentBottomSheetDialogBinding;
import com.talhachaudhry.medicine.models.ManageMedicineModel;

import java.text.MessageFormat;

public class ViewMedicineDetailsBottomSheet extends BottomSheetDialogFragment {

    FragmentBottomSheetDialogBinding binding;
    private static final String ARG_PARAM1 = "model";
    private ManageMedicineModel model;

    public static ViewMedicineDetailsBottomSheet newInstance(ManageMedicineModel model) {
        ViewMedicineDetailsBottomSheet bottomSheetFragment = new ViewMedicineDetailsBottomSheet();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, model);
        bottomSheetFragment.setArguments(args);
        return bottomSheetFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            model = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBottomSheetDialogBinding.inflate(inflater,
                container, false);
        binding.nameTv.setText(model.getName());
        binding.priceTv.setText(MessageFormat.format("{0}", model.getPrice()));
        binding.mgTv.setText(model.getMg());
        binding.detailTv.setText(model.getDetail());
        Glide.with(requireActivity()).
                load(Uri.parse(model.getImagePath())).
                placeholder(requireActivity().getDrawable(R.drawable.sample_image)).
                into(binding.medicineImageView);
        return binding.getRoot();
    }
}
