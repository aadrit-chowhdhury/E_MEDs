package com.talhachaudhry.medicine.wholesaler.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.talhachaudhry.medicine.R;
import com.talhachaudhry.medicine.adapter.CartAdapter;
import com.talhachaudhry.medicine.callbacks.CartCallback;
import com.talhachaudhry.medicine.databinding.FragmentCartBinding;
import com.talhachaudhry.medicine.models.CartModel;
import com.talhachaudhry.medicine.view_models.ManageMedicineViewModel;
import com.talhachaudhry.medicine.view_models.OrdersDetailViewModel;
import com.talhachaudhry.medicine.view_models.PlaceOrderViewModel;
import com.talhachaudhry.medicine.wholesaler.EditOrderActivity;
import com.talhachaudhry.medicine.wholesaler.PlaceOrderActivity;

import java.util.List;

public class CartFragment extends Fragment implements CartCallback {

    FragmentCartBinding binding;
    CartAdapter adapter;
    PlaceOrderViewModel viewModel;
    OrdersDetailViewModel viewModel1;
    List<CartModel> list;
    boolean inEdit;

    public static CartFragment newInstance() {
        return new CartFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof PlaceOrderActivity) {
            viewModel = new ViewModelProvider(requireActivity()).get(PlaceOrderViewModel.class);
            inEdit = false;
        } else if (context instanceof EditOrderActivity) {
            viewModel1 = new ViewModelProvider(requireActivity()).get(OrdersDetailViewModel.class);
            inEdit = true;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        if (inEdit) {
            binding.title.setText("Update Order");
            binding.placeOrderBtn.setText("Update");
        }
        adapter = new CartAdapter(requireActivity(), this);
        binding.backBtn.setOnClickListener(view -> requireActivity().onBackPressed());
        binding.cartRv.setAdapter(adapter);
        if (inEdit) {
            viewModel1.getCurrentlyActiveOrder().observe(getViewLifecycleOwner(), orderModel -> {
                list = orderModel.getOrdersList();
                adapter.submitList(orderModel.getOrdersList());
            });
        } else {
            viewModel.getCartList().observe(getViewLifecycleOwner(), cartModels -> {
                list = cartModels;
                adapter.submitList(cartModels);
            });
        }
        binding.placeOrderBtn.setOnClickListener(view -> {
            if (!list.isEmpty()) {
                if (inEdit) {
                    viewModel1.editOrder();
                    Toast.makeText(requireActivity(), "Order Edited", Toast.LENGTH_SHORT).show();
                } else {
                    viewModel.placeOrder();
                    ManageMedicineViewModel vm = new ViewModelProvider(requireActivity()).get(ManageMedicineViewModel.class);
                    Toast.makeText(requireActivity(), "Order Placed", Toast.LENGTH_SHORT).show();
                    vm.needReload();
                }
                requireActivity().onBackPressed();
            } else {
                Toast.makeText(requireActivity(), "Cart cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onDeleteClicked(CartModel model) {
        if (requireActivity() instanceof PlaceOrderActivity)
            viewModel.removeFromCart(model);
    }
}