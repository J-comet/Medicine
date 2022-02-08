package hs.project.medicine.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import java.text.SimpleDateFormat;
import java.util.Date;

import hs.project.medicine.R;
import hs.project.medicine.databinding.ActivityAlarmViewBinding;
import hs.project.medicine.service.RingtonePlayingService;

public class AlarmViewActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAlarmViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        super.onCreate(savedInstanceState);
        binding = ActivityAlarmViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        binding.clAlarmClear.setOnClickListener(this);

        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy년 MM월 dd일");
        String getDate = simpleDate.format(date);

        SimpleDateFormat simpleTime = new SimpleDateFormat("hh : mm");
        String getTime = simpleTime.format(date);

        binding.tvDate.setText(getDate);
        binding.tvTime.setText(getTime);
    }

    private void clear() {
        Intent serviceIntent = new Intent(AlarmViewActivity.this, RingtonePlayingService.class);
        serviceIntent.putExtra("state", "OFF");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

        Intent intent = new Intent(AlarmViewActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cl_alarm_clear:
                clear();
                break;
        }
    }
}