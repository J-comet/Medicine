package hs.project.medicine.dialog;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.AUDIO_SERVICE;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hs.project.medicine.Config;
import hs.project.medicine.MediApplication;
import hs.project.medicine.R;
import hs.project.medicine.databinding.DialogModifyAlarmBinding;
import hs.project.medicine.datas.Alarm;
import hs.project.medicine.datas.User;
import hs.project.medicine.util.LogUtil;
import hs.project.medicine.util.PreferenceUtil;

public class ModifyAlarmDialog extends DialogFragment implements View.OnClickListener {

    private DialogModifyAlarmBinding binding;
    private Context context;
    private Alarm alarm;
    private User user;
    private ArrayList<String> strAlarmList;
    private ArrayList<Alarm> alarmArrayList; // Preference 에 저장할 알람 정보
    private Fragment fragment;
    private ModifyAlarmListener eventListener;

    private Ringtone mRtCurrent;
    private AudioManager audioManager;
    private String[] arrAmPm;
    private String[] arrMinute;
    private ArrayList<String> integerArrayList;

    private MediaPlayer mediaPlayer;
    private String strRingtoneUri;

    private final static int REQUEST_CODE_RINGTONE_PICKER = 1000;
    private boolean isSuccess = false;

    public ModifyAlarmDialog(Context context, Alarm alarmItem, User userItem) {
        this.context = context;
        this.alarm = alarmItem;
        this.user = userItem;
    }

    public void setModifyAlarmListener(ModifyAlarmListener dialogResult) {
        eventListener = dialogResult;
    }

    public interface ModifyAlarmListener {
        void onComplete();
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
        binding.liBack.setOnClickListener(this);
        binding.liPlayStop.setOnClickListener(this);
        binding.clBellChoice.setOnClickListener(this);
        binding.liComplete.setOnClickListener(this);

        binding.tvSunday.setOnClickListener(this);
        binding.tvMonday.setOnClickListener(this);
        binding.tvTuesday.setOnClickListener(this);
        binding.tvWednesday.setOnClickListener(this);
        binding.tvThursday.setOnClickListener(this);
        binding.tvFriday.setOnClickListener(this);
        binding.tvSaturday.setOnClickListener(this);

        /* 기존에 저장되어 있는 리스트 가져오기 */
        alarmArrayList = new ArrayList<>();

        if (PreferenceUtil.getJSONArrayPreference(context, user.alarmKey()) != null
                && PreferenceUtil.getJSONArrayPreference(context, user.alarmKey()).size() > 0) {

            JSONArray jsonArray = new JSONArray(PreferenceUtil.getJSONArrayPreference(context, user.alarmKey()));

            try {

                for (int i = 0; i < jsonArray.length(); i++) {
                    Alarm alarm = new Alarm();
                    JSONObject object = new JSONObject(jsonArray.getString(i));
                    alarm.setName(object.getString("name"));
                    alarm.setAmPm(object.getString("amPm"));
                    alarm.setDayOfWeek(object.getString("dayOfWeek"));
                    alarm.setHour(object.getString("hour"));
                    alarm.setMinute(object.getString("minute"));
                    alarm.setVolume(object.getInt("volume"));
                    alarm.setRingtoneName(object.getString("ringtoneName"));
                    alarm.setRingtoneUri(Uri.parse(object.getString("ringtoneUri")));
                    alarm.setAlarmON(object.getBoolean("alarmON"));

                    alarmArrayList.add(alarm);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }




        audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        mediaPlayer = MediaPlayer.create(context, alarm.getRingtoneUri());

        // 음량값 받기
        int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        binding.sbVolume.setMax(maxVol);

        binding.sbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //음악 음량 변경
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, progress, 0);

                /*NotificationManager notificationManager;
                notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!notificationManager.isNotificationPolicyAccessGranted()) {
                        Toast.makeText(context.getApplicationContext(), "방해금지 권한을 허용해주세요", Toast.LENGTH_LONG).show();
                        context.startActivity(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS));
                    } else {
                        //음악 음량 변경
                        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, progress, 0);
                    }

                } else {
                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, progress, 0);
                }*/
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (strRingtoneUri == null) {
                    startMediaPlayer(alarm.getRingtoneUri());
                } else {
                    startMediaPlayer(Uri.parse(strRingtoneUri));
                }

                binding.ivPlayStop.setImageResource(R.drawable.ic_stop);
            }
        });

    }


    // 미디어플레이어 재생
    private void startMediaPlayer(Uri uri) {
        mediaPlayer = MediaPlayer.create(context, uri);
        mediaPlayer.start();
        mediaPlayer.setLooping(false);
    }

    // 미디어플레이어 멈춤
    private void stopMediaPlayer() {
        mediaPlayer.stop();
        mediaPlayer.reset();
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
//            mRtCurrent = RingtoneManager.getRingtone(context, alarm.getRingtoneUri());
            binding.tvRingtoneTitle.setText(alarm.getRingtoneName());
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

    //-- 재생중인 링톤을 중지 하는 함수
    /*private void stopRingtone() {
        if (mRtCurrent != null) {
            if (mRtCurrent.isPlaying()) {
                mRtCurrent.stop();
//                mRtCurrent = null;
            }
        }
    }*/

    private void modifyAlarmComplete() {

        strAlarmList = new ArrayList<>();

        /**
         * 1. Preference 에 저장된 AlarmList 의 Alarm 업데이트
         */
        for (int i = 0; i < alarmArrayList.size(); i++) {
            Alarm addAlarm = new Alarm();
            addAlarm.setName(alarmArrayList.get(i).getName());
            addAlarm.setAmPm(alarmArrayList.get(i).getAmPm());
            addAlarm.setHour(alarmArrayList.get(i).getHour());
            addAlarm.setMinute(alarmArrayList.get(i).getMinute());
            addAlarm.setVolume(alarmArrayList.get(i).getVolume());
            addAlarm.setRingtoneName(alarmArrayList.get(i).getRingtoneName());
            addAlarm.setRingtoneUri(alarmArrayList.get(i).getRingtoneUri());
            addAlarm.setDayOfWeek(alarmArrayList.get(i).getDayOfWeek());
            addAlarm.setAlarmON(alarmArrayList.get(i).isAlarmON());

            if (alarm.getName().equals(alarmArrayList.get(i).getName())) {
                addAlarm.setName(binding.etName.getText().toString());
                addAlarm.setAmPm(binding.npAmPm.getDisplayedValues()[binding.npAmPm.getValue()]);
                addAlarm.setHour(String.valueOf(binding.npHour.getValue()));
                addAlarm.setMinute(binding.npMinute.getDisplayedValues()[binding.npMinute.getValue()]);
                addAlarm.setVolume(binding.sbVolume.getProgress());
                addAlarm.setRingtoneName(binding.tvRingtoneTitle.getText().toString());

                if (strRingtoneUri == null) {
                    addAlarm.setRingtoneUri(alarm.getRingtoneUri());
                } else {
                    addAlarm.setRingtoneUri(Uri.parse(strRingtoneUri));
                }
                addAlarm.setDayOfWeek(binding.tvWeek.getText().toString());
                addAlarm.setAlarmON(alarmArrayList.get(i).isAlarmON());
            }

            strAlarmList.add(addAlarm.toJSON());
        }

        // Preference 에 저장
        PreferenceUtil.setJSONArrayPreference(context, user.alarmKey(), strAlarmList);
        Toast.makeText(context, "수정완료", Toast.LENGTH_SHORT).show();
    }

    // 벨소리 선택
    private void showRingtoneChooser() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "알람소리 선택");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE); //-- 알림 선택창이 떴을 때, 기본값으로 선택되어질 ringtone 설정

        if (strRingtoneUri != null && strRingtoneUri.isEmpty()) {
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(strRingtoneUri));
        }
        startActivityForResult(intent, REQUEST_CODE_RINGTONE_PICKER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_RINGTONE_PICKER) {
            if (resultCode == RESULT_OK) {
                Uri ring = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                mediaPlayer = MediaPlayer.create(context, ring);

                if (ring != null) {
                    strRingtoneUri = ring.toString();
                    LogUtil.d("strRingtoneUri=" + strRingtoneUri);

                    try {
                        mRtCurrent = RingtoneManager.getRingtone(context, ring);
                        if (mRtCurrent == null) {
                            throw new Exception("Can't play ringtone");
                        }
                        binding.tvRingtoneTitle.setText(mRtCurrent.getTitle(context));
                    } catch (Exception e) {
                        Toast.makeText(context, "오류 발생", Toast.LENGTH_SHORT).show();
                        LogUtil.e("AddAlarmActivity" + e.getMessage());
                        e.printStackTrace();
                    }

                } else {
                    binding.tvRingtoneTitle.setText(mRtCurrent.getTitle(context));
                }
            }
        }
    }

    private boolean checkAlarmName() {
        /* 해당 유저의 알람중에 같은 알람이 있는지 검사하는 코드 */
        if (PreferenceUtil.getJSONArrayPreference(context, user.alarmKey()) != null
                && PreferenceUtil.getJSONArrayPreference(context, user.alarmKey()).size() > 0) {

            JSONArray jsonArray = new JSONArray(PreferenceUtil.getJSONArrayPreference(context, user.alarmKey()));

            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject object = new JSONObject(jsonArray.getString(i));

                    /* 기존과 동일한 이름은 저장 가능 */
                    if (alarm.getName().equals(binding.etName.getText().toString())){
                        isSuccess = true;
                    } else {
                        if (object.getString("name").equals(binding.etName.getText().toString())) {
                            isSuccess = false;
                        } else {
                            isSuccess = true;
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            isSuccess = true;
        }
        return isSuccess;
    }

    @Override
    public void onStart() {
        super.onStart();
        stopMediaPlayer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.li_back:
                dismiss();
                break;
            case R.id.cl_bell_choice:
                stopMediaPlayer();
                binding.ivPlayStop.setImageResource(R.drawable.ic_play);
                showRingtoneChooser();
                break;
            case R.id.li_play_stop:
                if (mediaPlayer.isPlaying()) {
                    stopMediaPlayer();
                    binding.ivPlayStop.setImageResource(R.drawable.ic_play);
                } else {
                    if (strRingtoneUri != null) {
                        startMediaPlayer(Uri.parse(strRingtoneUri));
                    } else {
                        startMediaPlayer(alarm.getRingtoneUri());
                    }
                    binding.ivPlayStop.setImageResource(R.drawable.ic_stop);
                }
                break;
            case R.id.li_complete:
                /* (유저/관계) 로 된 Preference 안의 리스트 키 중에서 알람이름이 같은 것을 리스트에서 찾은 후 수정 후 다시 저장 */
                if (checkAlarmName()) {
                    if (binding.etName.getText().toString().length() > 0) {
                        modifyAlarmComplete();
                        eventListener.onComplete();
                        dismiss();
//                            DialogFragment dialogFragment = (DialogFragment) fragment;
//                            dialogFragment.dismiss();
                    } else {
                        Toast.makeText(context, "모든 정보를 입력해주세요", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "이미 저장되어 있는 알람 이름 입니다", Toast.LENGTH_SHORT).show();
                }



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


