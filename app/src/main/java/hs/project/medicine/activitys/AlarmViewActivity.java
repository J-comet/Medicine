package hs.project.medicine.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;

import hs.project.medicine.databinding.ActivityAlarmViewBinding;

public class AlarmViewActivity extends AppCompatActivity {

    private ActivityAlarmViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        super.onCreate(savedInstanceState);
        binding = ActivityAlarmViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}