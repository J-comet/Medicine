package hs.project.medicine.activitys;

import android.os.Bundle;

import hs.project.medicine.R;
import hs.project.medicine.databinding.ActivityUserDetailBinding;

public class UserDetailActivity extends BaseActivity {

    private ActivityUserDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}