package hs.project.medicine.activitys;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;

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

        String[] arrAmPm = getResources().getStringArray(R.array.arr_am_pm);

        binding.npAmPm.setMinValue(0);
        binding.npAmPm.setMaxValue(1);
        binding.npAmPm.setDisplayedValues(arrAmPm);

        binding.npHour.setMinValue(1);
        binding.npHour.setMaxValue(12);
        binding.npHour.setWrapSelectorWheel(false);

        binding.npMinute.setMinValue(0);
        binding.npMinute.setMaxValue(11);
        binding.npMinute.setWrapSelectorWheel(false);
        binding.npMinute.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                switch(value){
                    case 0:
                        return "00";
                    case 1:
                        return "05";
                    case 2:
                        return "10";
                    case 3:
                        return "15";
                    case 4:
                        return "20";
                    case 5:
                        return "25";
                    case 6:
                        return "30";
                    case 7:
                        return "35";
                    case 8:
                        return "40";
                    case 9:
                        return "45";
                    case 10:
                        return "50";
                    case 11:
                        return "55";
                }
                return null;
            }
        });

//        binding.npMinute.setDisplayedValues(arrMinute);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.li_back:
//                finish();
//                break;
        }
    }
}