package hs.project.medicine.activitys;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;

import java.util.ArrayList;

import hs.project.medicine.R;
import hs.project.medicine.databinding.ActivityAddAlarmBinding;
import hs.project.medicine.util.LogUtil;


public class AddAlarmActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAddAlarmBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        binding.liBack.setOnClickListener(this);

        String[] arrAmPm = getResources().getStringArray(R.array.arr_am_pm);

        binding.npAmPm.setMinValue(0);
        binding.npAmPm.setMaxValue(1);
        binding.npAmPm.setDisplayedValues(arrAmPm);

        binding.npHour.setMinValue(1);
        binding.npHour.setMaxValue(12);
        binding.npHour.setWrapSelectorWheel(true);

        binding.npMinute.setMinValue(0);
        binding.npMinute.setMaxValue(60);
        binding.npMinute.setWrapSelectorWheel(true);

        ArrayList<String> integerArrayList = new ArrayList<>();
        for (int i = 0; i < 61; i++) {
            integerArrayList.add(String.format("%02d", i));
        }
        String[] array = integerArrayList.toArray(new String[integerArrayList.size()]);
        binding.npMinute.setDisplayedValues(array);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.li_back:
                finish();
                break;
        }
    }
}