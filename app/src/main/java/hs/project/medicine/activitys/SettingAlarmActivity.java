package hs.project.medicine.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import hs.project.medicine.databinding.ActivitySettingAlarmBinding;

public class SettingAlarmActivity extends BaseActivity {

    private ActivitySettingAlarmBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}