package hs.project.medicine.activitys;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

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
        binding.tvSunday.setOnClickListener(this);
        binding.tvMonday.setOnClickListener(this);
        binding.tvTuesday.setOnClickListener(this);
        binding.tvWednesday.setOnClickListener(this);
        binding.tvThursday.setOnClickListener(this);
        binding.tvFriday.setOnClickListener(this);
        binding.tvSaturday.setOnClickListener(this);

        binding.tvSunday.setSelected(true);
        binding.tvMonday.setSelected(true);
        binding.tvTuesday.setSelected(true);
        binding.tvWednesday.setSelected(true);
        binding.tvThursday.setSelected(true);
        binding.tvFriday.setSelected(true);
        binding.tvSaturday.setSelected(true);

        binding.tvWeek.setText("매일");

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

    private void dayOfWeekStatus(boolean isSelected, TextView textView) {
        if (isSelected) {
            textView.setSelected(true);
        } else {
            textView.setSelected(false);
        }
    }

    private String resultDayOfWeek(boolean sunday, boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday) {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(binding.tvSunday.getText().toString());
        arrayList.add(binding.tvMonday.getText().toString());
        arrayList.add(binding.tvTuesday.getText().toString());
        arrayList.add(binding.tvWednesday.getText().toString());
        arrayList.add(binding.tvThursday.getText().toString());
        arrayList.add(binding.tvFriday.getText().toString());
        arrayList.add(binding.tvSaturday.getText().toString());

        String result = "";

        if (sunday && monday && tuesday && wednesday && thursday && friday && saturday) {
            result = "매일";
        } else if (!sunday && !saturday) {
            result = "주중";
        } else if (sunday && !monday && !tuesday && !wednesday && !thursday && !friday && saturday) {
            result = "주말";
        } else {

            for (int i = 0; i < arrayList.size(); i++) {
                if (sunday) {
                    result = result + arrayList.get(0);
                    sunday = false;
                }

                if (monday) {
                    result = result + arrayList.get(1);
                    monday = false;
                }

                if (tuesday) {
                    result = result + arrayList.get(2);
                    tuesday = false;
                }

                if (wednesday) {
                    result = result + arrayList.get(3);
                    wednesday = false;
                }

                if (thursday) {
                    result = result + arrayList.get(4);
                    thursday = false;
                }

                if (friday) {
                    result = result + arrayList.get(5);
                    friday = false;
                }

                if (saturday) {
                    result = result + arrayList.get(6);
                    saturday = false;
                }
            }
        }

        return result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.li_back:
                finish();
                break;
            case R.id.tv_sunday:
                if (binding.tvSunday.isSelected()) {
                    dayOfWeekStatus(false, binding.tvSunday);
                } else {
                    dayOfWeekStatus(true, binding.tvSunday);
                }
                binding.tvWeek.setText(
                        resultDayOfWeek(binding.tvSunday.isSelected(), binding.tvMonday.isSelected(), binding.tvTuesday.isSelected()
                                , binding.tvWednesday.isSelected(), binding.tvThursday.isSelected(), binding.tvFriday.isSelected(), binding.tvSaturday.isSelected()));
                break;
            case R.id.tv_monday:
                if (binding.tvMonday.isSelected()) {
                    dayOfWeekStatus(false, binding.tvMonday);
                } else {
                    dayOfWeekStatus(true, binding.tvMonday);
                }
                binding.tvWeek.setText(
                        resultDayOfWeek(binding.tvSunday.isSelected(), binding.tvMonday.isSelected(), binding.tvTuesday.isSelected()
                                , binding.tvWednesday.isSelected(), binding.tvThursday.isSelected(), binding.tvFriday.isSelected(), binding.tvSaturday.isSelected()));
                break;
            case R.id.tv_tuesday:
                if (binding.tvTuesday.isSelected()) {
                    dayOfWeekStatus(false, binding.tvTuesday);
                } else {
                    dayOfWeekStatus(true, binding.tvTuesday);
                }
                binding.tvWeek.setText(
                        resultDayOfWeek(binding.tvSunday.isSelected(), binding.tvMonday.isSelected(), binding.tvTuesday.isSelected()
                                , binding.tvWednesday.isSelected(), binding.tvThursday.isSelected(), binding.tvFriday.isSelected(), binding.tvSaturday.isSelected()));
                break;
            case R.id.tv_wednesday:
                if (binding.tvWednesday.isSelected()) {
                    dayOfWeekStatus(false, binding.tvWednesday);
                } else {
                    dayOfWeekStatus(true, binding.tvWednesday);
                }
                binding.tvWeek.setText(
                        resultDayOfWeek(binding.tvSunday.isSelected(), binding.tvMonday.isSelected(), binding.tvTuesday.isSelected()
                                , binding.tvWednesday.isSelected(), binding.tvThursday.isSelected(), binding.tvFriday.isSelected(), binding.tvSaturday.isSelected()));
                break;
            case R.id.tv_thursday:
                if (binding.tvThursday.isSelected()) {
                    dayOfWeekStatus(false, binding.tvThursday);
                } else {
                    dayOfWeekStatus(true, binding.tvThursday);
                }
                binding.tvWeek.setText(
                        resultDayOfWeek(binding.tvSunday.isSelected(), binding.tvMonday.isSelected(), binding.tvTuesday.isSelected()
                                , binding.tvWednesday.isSelected(), binding.tvThursday.isSelected(), binding.tvFriday.isSelected(), binding.tvSaturday.isSelected()));
                break;
            case R.id.tv_friday:
                if (binding.tvFriday.isSelected()) {
                    dayOfWeekStatus(false, binding.tvFriday);
                } else {
                    dayOfWeekStatus(true, binding.tvFriday);
                }
                binding.tvWeek.setText(
                        resultDayOfWeek(binding.tvSunday.isSelected(), binding.tvMonday.isSelected(), binding.tvTuesday.isSelected()
                                , binding.tvWednesday.isSelected(), binding.tvThursday.isSelected(), binding.tvFriday.isSelected(), binding.tvSaturday.isSelected()));
                break;
            case R.id.tv_saturday:
                if (binding.tvSaturday.isSelected()) {
                    dayOfWeekStatus(false, binding.tvSaturday);
                } else {
                    dayOfWeekStatus(true, binding.tvSaturday);
                }
                binding.tvWeek.setText(
                        resultDayOfWeek(binding.tvSunday.isSelected(), binding.tvMonday.isSelected(), binding.tvTuesday.isSelected()
                                , binding.tvWednesday.isSelected(), binding.tvThursday.isSelected(), binding.tvFriday.isSelected(), binding.tvSaturday.isSelected()));
                break;
        }
    }
}