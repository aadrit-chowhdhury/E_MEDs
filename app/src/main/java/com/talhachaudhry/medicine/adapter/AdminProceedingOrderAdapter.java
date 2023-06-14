package com.talhachaudhry.medicine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;

import com.talhachaudhry.medicine.callbacks.AdminProceedingCallbacks;
import com.talhachaudhry.medicine.databinding.SampleAdminDispatchedOrdersItemBinding;
import com.talhachaudhry.medicine.databinding.SampleProceedingItemBinding;
import com.talhachaudhry.medicine.models.CartModel;
import com.talhachaudhry.medicine.models.OrderModel;
import com.talhachaudhry.medicine.utils.OrderDiffUtils;

import java.text.MessageFormat;

public class AdminProceedingOrderAdapter extends ListAdapter<OrderModel, RecyclerViewViewHolderBoilerPlate> {
    Context context;
    AdminProceedingCallbacks callback;
    boolean inAdmin;

    public AdminProceedingOrderAdapter(Context context, AdminProceedingCallbacks callback, boolean inAdmin) {
        super(new OrderDiffUtils());
        this.context = context;
        this.callback = callback;
        this.inAdmin = inAdmin;
    }

    @NonNull
    @Override
    public RecyclerViewViewHolderBoilerPlate onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewViewHolderBoilerPlate(SampleProceedingItemBinding.
                inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewViewHolderBoilerPlate holder, int position) {
        SampleProceedingItemBinding binding = (SampleProceedingItemBinding) holder.binding;
        binding.orderIdTv.setText(getItem(position).getOrderId());
        binding.dateTv.setText(getItem(position).getDateAndTime());
        binding.shopAddressTv.setText(getItem(position).getUserModel().getAddress());
        int total = 0;
        for (CartModel cartModel : getItem(position).getOrdersList()) {
            total += cartModel.getQuantity() * cartModel.getModel().getPrice();
        }
        binding.shopNameTv.setText(MessageFormat.format("{0}", total));
        if (inAdmin) {
            binding.cancelTv.setOnClickListener(view -> callback.onCancelClicked(getItem(holder.getAdapterPosition())));
            binding.dispatchTv.setOnClickListener(view -> callback.onDispatchClicked(getItem(holder.getAdapterPosition())));
            binding.pendingOrderRv.setOnClickListener(view -> callback.onPendingClicked(getItem(holder.getAdapterPosition())));
        } else {
            binding.shopAddressTag.setVisibility(View.GONE);
            binding.shopNameTv.setVisibility(View.GONE);
            binding.dispatchTv.setVisibility(View.INVISIBLE);
            binding.pendingOrderRv.setVisibility(View.INVISIBLE);
            binding.cancelTv.setVisibility(View.INVISIBLE);
        }
        holder.itemView.setOnClickListener(view -> callback.onClickedToView(getItem(holder.getAdapterPosition())));
    }
}