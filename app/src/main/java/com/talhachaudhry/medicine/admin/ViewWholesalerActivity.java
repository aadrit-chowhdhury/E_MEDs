package com.talhachaudhry.medicine.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;

import com.talhachaudhry.medicine.adapter.ViewWholesalerRecyclerAdapter;
import com.talhachaudhry.medicine.admin.bottom_sheet.ViewWholesalersBottomFragment;
import com.talhachaudhry.medicine.callbacks.OnViewWholesalerClicked;
import com.talhachaudhry.medicine.databinding.ActivityViewWholesalerBinding;
import com.talhachaudhry.medicine.models.UserModel;
import com.talhachaudhry.medicine.view_models.ViewWholesalersViewModel;

import java.util.Objects;

public class ViewWholesalerActivity extends AppCompatActivity implements OnViewWholesalerClicked {

    ActivityViewWholesalerBinding binding;
    ViewWholesalersViewModel viewModel;
    ViewWholesalerRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewWholesalerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();
        adapter = new ViewWholesalerRecyclerAdapter(this, this);
        binding.viewWholesalerRv.setAdapter(adapter);
        binding.backBtn.setOnClickListener(view -> onBackPressed());
        viewModel = new ViewModelProvider(this).get(ViewWholesalersViewModel.class);
        viewModel.getUsersModel().observe(this, userModels -> {
            if (userModels.isEmpty()) {
                binding.animation.setVisibility(View.VISIBLE);
            } else if (binding.animation.getVisibility() == View.VISIBLE) {
                binding.animation.setVisibility(View.INVISIBLE);
            }
            adapter.submitList(userModels);
        });
    }

    @Override
    public void onViewWholesalerClicked(UserModel model) {
        ViewWholesalersBottomFragment bottomSheet = ViewWholesalersBottomFragment.newInstance(model);
        bottomSheet.show(getSupportFragmentManager(),
                "UserModalBottomSheet");
    }
}