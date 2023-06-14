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
import com.talhachaudhry.medicine.adapter.CancelledOrdersAdapter;
import com.talhachaudhry.medicine.admin.ManageOrdersActivity;
import com.talhachaudhry.medicine.admin.bottom_sheet.CancelledOrdersBottomSheet;
import com.talhachaudhry.medicine.callbacks.CancelledOrdersCallback;
import com.talhachaudhry.medicine.databinding.FragmentCancelledOrdersBinding;
import com.talhachaudhry.medicine.models.OrderModel;
import com.talhachaudhry.medicine.view_models.ManageOrdersViewModel;
import com.talhachaudhry.medicine.view_models.OrdersDetailViewModel;
import com.talhachaudhry.medicine.wholesaler.PendingOrdersActivity;

public class CancelledOrdersFragment extends Fragment implements CancelledOrdersCallback {

    FragmentCancelledOrdersBinding binding;
    ManageOrdersViewModel viewModel;
    OrdersDetailViewModel viewModel1;
    CancelledOrdersAdapter adapter;
    boolean inAdmin;

    public static CancelledOrdersFragment newInstance() {
        return new CancelledOrdersFragment();
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
        binding = FragmentCancelledOrdersBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(ManageOrdersViewModel.class);
        adapter = new CancelledOrdersAdapter(requireActivity(), this);
        binding.cancelOrderRv.setAdapter(adapter);
        if (inAdmin) {
            viewModel.getCancelOrdersListLiveData().observe(getViewLifecycleOwner(), orderModels -> {
                        if (orderModels.isEmpty()) {
                            requireActivity().runOnUiThread(() -> binding.animation.setVisibility(View.VISIBLE));
                        } else if (binding.animation.getVisibility() != View.INVISIBLE) {
                            requireActivity().runOnUiThread(() -> binding.animation.setVisibility(View.INVISIBLE));
                        }
                        adapter.submitList(orderModels);});
        } else {
            viewModel1.getCancelledOrdersLiveData().observe(getViewLifecycleOwner(), orderModels -> {
                        if (orderModels.isEmpty()) {
                            requireActivity().runOnUiThread(() -> binding.animation.setVisibility(View.VISIBLE));
                        } else if (binding.animation.getVisibility() != View.INVISIBLE) {
                            requireActivity().runOnUiThread(() -> binding.animation.setVisibility(View.INVISIBLE));
                        }
                        adapter.submitList(orderModels);});
        }
        return binding.getRoot();
    }

    @Override
    public void onItemClicked(OrderModel model) {
        CancelledOrdersBottomSheet bottomSheet = CancelledOrdersBottomSheet.newInstance(model, 1);
        bottomSheet.show(requireActivity().getSupportFragmentManager(),
                "CancelOrdersBottomSheet");
    }
}