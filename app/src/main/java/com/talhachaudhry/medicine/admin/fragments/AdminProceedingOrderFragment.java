package com.talhachaudhry.medicine.admin.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.talhachaudhry.medicine.R;
import com.talhachaudhry.medicine.adapter.AdminProceedingOrderAdapter;
import com.talhachaudhry.medicine.admin.ManageOrdersActivity;
import com.talhachaudhry.medicine.admin.bottom_sheet.CancelledOrdersBottomSheet;
import com.talhachaudhry.medicine.callbacks.AdminProceedingCallbacks;
import com.talhachaudhry.medicine.databinding.FragmentAdminProceedingOrderBinding;
import com.talhachaudhry.medicine.models.OrderModel;
import com.talhachaudhry.medicine.view_models.ManageOrdersViewModel;
import com.talhachaudhry.medicine.view_models.OrdersDetailViewModel;
import com.talhachaudhry.medicine.wholesaler.PendingOrdersActivity;

public class AdminProceedingOrderFragment extends Fragment implements AdminProceedingCallbacks {

    FragmentAdminProceedingOrderBinding binding;
    ManageOrdersViewModel viewModel;
    OrdersDetailViewModel viewModel1;
    AdminProceedingOrderAdapter adapter;
    boolean inAdmin;

    public static AdminProceedingOrderFragment newInstance() {
        return new AdminProceedingOrderFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ManageOrdersActivity) {
            inAdmin = true;
            viewModel = new ViewModelProvider(requireActivity()).get(ManageOrdersViewModel.class);
        } else if (context instanceof PendingOrdersActivity) {
            inAdmin = false;
            viewModel1 = new ViewModelProvider(requireActivity()).get(OrdersDetailViewModel.class);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminProceedingOrderBinding.inflate(inflater, container, false);
        adapter = new AdminProceedingOrderAdapter(requireActivity(), this, inAdmin);
        binding.pendingOrderRv.setAdapter(adapter);
        if (inAdmin) {
            viewModel.getProceedingOrdersListLiveData().observe(getViewLifecycleOwner(), orderModels -> {
                if (orderModels.isEmpty()) {
                    requireActivity().runOnUiThread(() -> binding.animation.setVisibility(View.VISIBLE));
                } else if (binding.animation.getVisibility() != View.INVISIBLE) {
                    requireActivity().runOnUiThread(() -> binding.animation.setVisibility(View.INVISIBLE));
                }
                adapter.submitList(orderModels);
            });
        } else {
            viewModel1.getProceedingOrdersLiveData().observe(getViewLifecycleOwner(), orderModels -> {
                if (orderModels.isEmpty()) {
                    requireActivity().runOnUiThread(() -> binding.animation.setVisibility(View.VISIBLE));
                } else if (binding.animation.getVisibility() != View.INVISIBLE) {
                    requireActivity().runOnUiThread(() -> binding.animation.setVisibility(View.INVISIBLE));
                }
                adapter.submitList(orderModels);
            });
        }
        return binding.getRoot();
    }

    @Override
    public void onCancelClicked(OrderModel model) {
        viewModel.performOperation(ManageOrdersViewModel.OrderOperationsEnums.PROCEEDING_TO_CANCEL, model);
    }

    @Override
    public void onDispatchClicked(OrderModel model) {
        viewModel.performOperation(ManageOrdersViewModel.OrderOperationsEnums.PROCEEDING_TO_DISPATCH, model);
    }

    @Override
    public void onPendingClicked(OrderModel model) {
        viewModel.performOperation(ManageOrdersViewModel.OrderOperationsEnums.PROCEEDING_TO_PENDING, model);
    }

    @Override
    public void onClickedToView(OrderModel model) {
        CancelledOrdersBottomSheet bottomSheet = CancelledOrdersBottomSheet.newInstance(model, 0);
        bottomSheet.show(requireActivity().getSupportFragmentManager(),
                "CancelOrdersBottomSheet");
    }
}