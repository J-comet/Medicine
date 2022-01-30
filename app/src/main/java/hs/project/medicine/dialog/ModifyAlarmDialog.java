package hs.project.medicine.dialog;

import static android.content.Context.AUDIO_SERVICE;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import hs.project.medicine.MediApplication;
import hs.project.medicine.R;
import hs.project.medicine.databinding.DialogModifyAlarmBinding;
import hs.project.medicine.datas.Alarm;
import hs.project.medicine.util.LogUtil;

public class ModifyAlarmDialog extends DialogFragment implements View.OnClickListener {

    private DialogModifyAlarmBinding binding;
    private Context context;
    private Alarm alarm;
    private ArrayList<String> strAlarmList;
    private ArrayList<Alarm> alarmArrayList; // Preference 에 저장할 알람 정보
    private Fragment fragment;
    private ModifyAlarmListener eventListener;

    private Ringtone mRtCurrent;
    private AudioManager audioManager;
    private String[] arrAmPm;
    private String[] arrMinute;
    private ArrayList<String> integerArrayList;

    public ModifyAlarmDialog(Context context, Alarm alarmItem) {
        this.context = context;
        this.alarm = alarmItem;
    }

    public void setModifyAlarmListener(ModifyAlarmListener dialogResult) {
        eventListener = dialogResult;
    }

    public interface ModifyAlarmListener {
        void onComplete(Alarm alarm);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.dialog_fullscreen);
        fragment = requireActivity().getSupportFragmentManager().findFragmentByTag("modifyAlarmDialog");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogModifyAlarmBinding.inflate(LayoutInflater.from(context), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        setData();
    }

    private void init() {
        binding.tvSunday.setOnClickListener(this);
        binding.tvMonday.setOnClickListener(this);
        binding.tvTuesday.setOnClickListener(this);
        binding.tvWednesday.setOnClickListener(this);
        binding.tvThursday.setOnClickListener(this);
        binding.tvFriday.setOnClickListener(this);
        binding.tvSaturday.setOnClickListener(this);

        audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);

        // 음량값 받기
        int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        binding.sbVolume.setMax(maxVol);

        binding.sbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                NotificationManager notificationManager;
                notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!notificationManager.isNotificationPolicyAccessGranted()) {
                        Toast.makeText(context.getApplicationContext(), "방해금지 권한을 허용해주세요", Toast.LENGTH_LONG).show();
                        context.startActivity(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS));
                    } else {
                        //음악 음량 변경
                        audioManager.setStreamVolume(AudioManager.STREAM_RING, progress, 0);
                    }

                } else {
                    audioManager.setStreamVolume(AudioManager.STREAM_RING, progress, 0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mRtCurrent.play();
                binding.ivPlayStop.setImageResource(R.drawable.ic_stop);
            }
        });

    }

    private void setData() {
        arrAmPm = getResources().getStringArray(R.array.arr_am_pm);

        integerArrayList = new ArrayList<>();
        for (int i = 0; i < 61; i++) {
            integerArrayList.add(String.format("%02d", i));
        }
        arrMinute = integerArrayList.toArray(new String[integerArrayList.size()]);

        setNumberPicker();

        if (alarm != null) {
            binding.etName.setText(alarm.getName());
            mRtCurrent = RingtoneManager.getRingtone(context, alarm.getRingtoneUri());
            binding.tvRingtoneTitle.setText(mRtCurrent.getTitle(context));
            binding.tvWeek.setText(alarm.getDayOfWeek());
            binding.sbVolume.setProgress(alarm.getVolume());

            if (alarm.getAmPm().equals("오전")) {
                binding.npAmPm.setValue(0);
            } else {
                binding.npAmPm.setValue(1);
            }
            binding.npHour.setValue(Integer.parseInt(alarm.getHour()));

            for (int i = 0; i < integerArrayList.size(); i++) {
                if (alarm.getMinute().equals(integerArrayList.get(i))) {
                    binding.npMinute.setValue(Integer.parseInt(String.format("%02d", i)));
                    break;
                }
            }

            switch (alarm.getDayOfWeek()) {
                case "매일":
                    binding.tvSunday.setSelected(true);
                    binding.tvMonday.setSelected(true);
                    binding.tvTuesday.setSelected(true);
                    binding.tvWednesday.setSelected(true);
                    binding.tvThursday.setSelected(true);
                    binding.tvFriday.setSelected(true);
                    binding.tvSaturday.setSelected(true);
                    break;

                case "주중":
                    binding.tvSunday.setSelected(false);
                    binding.tvMonday.setSelected(true);
                    binding.tvTuesday.setSelected(true);
                    binding.tvWednesday.setSelected(true);
                    binding.tvThursday.setSelected(true);
                    binding.tvFriday.setSelected(true);
                    binding.tvSaturday.setSelected(false);
                    break;

                case "주말":
                    binding.tvSunday.setSelected(true);
                    binding.tvMonday.setSelected(false);
                    binding.tvTuesday.setSelected(false);
                    binding.tvWednesday.setSelected(false);
                    binding.tvThursday.setSelected(false);
                    binding.tvFriday.setSelected(false);
                    binding.tvSaturday.setSelected(true);
                    break;

                default:
                    /* String 을 split 으로 변환 후 배열로 저장 */
                    String[] arrDayOfWeek = alarm.getDayOfWeek().split(" ");

                    for (int i = 0; i < arrDayOfWeek.length; i++) {

                        switch (arrDayOfWeek[i]) {
                            case "일":
                                dayOfWeekStatus(true, binding.tvSunday);
                                break;
                            case "월":
                                dayOfWeekStatus(true, binding.tvMonday);
                                break;
                            case "화":
                                dayOfWeekStatus(true, binding.tvTuesday);
                                break;
                            case "수":
                                dayOfWeekStatus(true, binding.tvWednesday);
                                break;
                            case "목":
                                dayOfWeekStatus(true, binding.tvThursday);
                                break;
                            case "금":
                                dayOfWeekStatus(true, binding.tvFriday);
                                break;
                            case "토":
                                dayOfWeekStatus(true, binding.tvSaturday);
                                break;
                        }
                    }
                    break;
            }

        }
    }

    private void setNumberPicker() {

        binding.npAmPm.setMinValue(0);
        binding.npAmPm.setMaxValue(1);
        binding.npAmPm.setDisplayedValues(arrAmPm);

        binding.npHour.setMinValue(1);
        binding.npHour.setMaxValue(12);
        binding.npHour.setWrapSelectorWheel(true);

        binding.npMinute.setMinValue(0);
        binding.npMinute.setMaxValue(60);
        binding.npMinute.setWrapSelectorWheel(true);

        binding.npMinute.setDisplayedValues(arrMinute);
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
                    result = result + arrayList.get(0) + " ";
                    sunday = false;
                }

                if (monday) {
                    result = result + arrayList.get(1) + " ";
                    monday = false;
                }

                if (tuesday) {
                    result = result + arrayList.get(2) + " ";
                    tuesday = false;
                }

                if (wednesday) {
                    result = result + arrayList.get(3) + " ";
                    wednesday = false;
                }

                if (thursday) {
                    result = result + arrayList.get(4) + " ";
                    thursday = false;
                }

                if (friday) {
                    result = result + arrayList.get(5) + " ";
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
                dismiss();
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


