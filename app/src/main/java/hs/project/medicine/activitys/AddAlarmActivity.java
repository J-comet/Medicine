package hs.project.medicine.activitys;

import android.os.Bundle;

import hs.project.medicine.databinding.ActivityAddAlarmBinding;


public class AddAlarmActivity extends BaseActivity {

    private ActivityAddAlarmBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}