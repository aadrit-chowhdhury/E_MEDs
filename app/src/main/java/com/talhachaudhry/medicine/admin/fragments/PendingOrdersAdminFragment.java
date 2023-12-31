package com.talhachaudhry.medicine.admin.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.talhachaudhry.medicine.R;
import com.talhachaudhry.medicine.adapter.AdminPendingOrdersAdapter;
import com.talhachaudhry.medicine.admin.bottom_sheet.CancelledOrdersBottomSheet;
import com.talhachaudhry.medicine.callbacks.AdminPendingOrderCallbacks;
import com.talhachaudhry.medicine.databinding.FragmentPendingOrdersAdminBinding;
import com.talhachaudhry.medicine.models.OrderModel;
import com.talhachaudhry.medicine.view_models.ManageOrdersViewModel;

public class PendingOrdersAdminFragment extends Fragment implements AdminPendingOrderCallbacks {

    FragmentPendingOrdersAdminBinding binding;
    ManageOrdersViewModel viewModel;
    AdminPendingOrdersAdapter adapter;

    public static PendingOrdersAdminFragment newInstance() {
        return new PendingOrdersAdminFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPendingOrdersAdminBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(ManageOrdersViewModel.class);
        adapter = new AdminPendingOrdersAdapter(requireActivity(), this);
        binding.pendingOrderRv.setAdapter(adapter);
        viewModel.getPendingOrdersListLiveData().observe(getViewLifecycleOwner(), orderModels -> {
            if (orderModels.isEmpty()) {
                requireActivity().runOnUiThread(() -> binding.animation.setVisibility(View.VISIBLE));
            } else if (binding.animation.getVisibility() != View.INVISIBLE) {
                requireActivity().runOnUiThread(() -> binding.animation.setVisibility(View.INVISIBLE));
            }
            adapter.submitList(orderModels);
        });
        return binding.getRoot();
    }

    @Override
    public void onProceedOrderClicker(OrderModel model) {
        viewModel.performOperation(ManageOrdersViewModel.OrderOperationsEnums.PENDING_TO_PROCEEDING, model);
    }

    @Override
    public void onDispatchOrderClicker(OrderModel model) {
        viewModel.performOperation(ManageOrdersViewModel.OrderOperationsEnums.PENDING_TO_DISPATCH, model);
    }

    @Override
    public void onCancelOrderClicker(OrderModel model) {
        viewModel.performOperation(ManageOrdersViewModel.OrderOperationsEnums.PENDING_TO_CANCEL, model);
    }

    @Override
    public void onClickedToView(OrderModel orderModel) {
        CancelledOrdersBottomSheet bottomSheet = CancelledOrdersBottomSheet.newInstance(orderModel, 0);
        bottomSheet.show(requireActivity().getSupportFragmentManager(),
                "CancelOrdersBottomSheet");
    }
}