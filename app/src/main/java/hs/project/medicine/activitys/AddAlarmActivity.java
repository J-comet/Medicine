package hs.project.medicine.activitys;

import static hs.project.medicine.activitys.MainActivity.mainActivityContext;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import hs.project.medicine.Config;
import hs.project.medicine.R;
import hs.project.medicine.databinding.ActivityAddAlarmBinding;
import hs.project.medicine.datas.Alarm;
import hs.project.medicine.util.LogUtil;
import hs.project.medicine.util.PreferenceUtil;


public class AddAlarmActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAddAlarmBinding binding;

    private String strRingtoneUri;
    private final static int REQUEST_CODE_RINGTONE_PICKER = 1000;
    private final static int ON_DO_NOT_DISTURB_CALLBACK_CODE = 1001;

    private Ringtone mRtCurrent; // 노래 제목받아올 용도로 사용

    private MediaPlayer mediaPlayer;

    private AudioManager audioManager;

    private Uri firstUriRingtone;
//    private User currentUser;

    private ArrayList<String> alarmList;
    private boolean isSuccess = false;

    private NotificationManager notificationManager;

    /* 알람등록 관련 변수 */
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    int getHourTimePicker;
    int getMinuteTimePicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();


    }

    private void init() {
//        currentUser = (User) getIntent().getSerializableExtra("user");

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);


        /* 기존에 저장되어 있는 Preference 가 있는지 확인하기 */
        if (PreferenceUtil.getJSONArrayPreference(AddAlarmActivity.this, Config.PREFERENCE_KEY.ALARM_LIST) != null
                && PreferenceUtil.getJSONArrayPreference(AddAlarmActivity.this, Config.PREFERENCE_KEY.ALARM_LIST).size() > 0) {
            alarmList = PreferenceUtil.getJSONArrayPreference(AddAlarmActivity.this, Config.PREFERENCE_KEY.ALARM_LIST);
        } else {
            alarmList = new ArrayList<>();
        }


        strRingtoneUri = "content://media/internal/audio/media/37";

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        firstUriRingtone = Uri.parse("content://media/internal/audio/media/37");  // 기본벨소리 설정
        mediaPlayer = MediaPlayer.create(this, firstUriRingtone);
//        mediaPlayer = MediaPlayer.create(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
        mRtCurrent = RingtoneManager.getRingtone(this, firstUriRingtone);
        binding.tvRingtoneTitle.setText(mRtCurrent.getTitle(this));

        // 음량값 받기
        int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        binding.sbVolume.setMax(maxVol);
        binding.sbVolume.setProgress(maxVol);

        binding.sbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                /*switch (audioManager.getRingerMode()) {
                    case AudioManager.RINGER_MODE_NORMAL:  // 벨소리모드
                        mRtCurrent.play();
                        binding.ivPlayStop.setImageResource(R.drawable.ic_stop);
                        break;
                    case AudioManager.RINGER_MODE_VIBRATE: // 진동모드
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL); //벨소리모드로 변경
                        mRtCurrent.play();
                        binding.ivPlayStop.setImageResource(R.drawable.ic_stop);
                        break;
                    case AudioManager.RINGER_MODE_SILENT:  // 무음모드
//                                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE); //진동모드로 변경
//                                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT); //무음모드로 변경
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL); //벨소리모드로 변경
                        mRtCurrent.play();
                        binding.ivPlayStop.setImageResource(R.drawable.ic_stop);
                        break;
                }*/

//                mRtCurrent.play();

                /* 실행중이라면 startMediaPlayer 실행 안하도록 수정 */
                if (mediaPlayer.isPlaying()) {
                    stopMediaPlayer();

                    if (strRingtoneUri == null) {
                        startMediaPlayer(firstUriRingtone);
                    } else {
                        startMediaPlayer(Uri.parse(strRingtoneUri));
                    }

                } else {
                    if (strRingtoneUri == null) {
                        startMediaPlayer(firstUriRingtone);
                    } else {
                        startMediaPlayer(Uri.parse(strRingtoneUri));
                    }
                }



                binding.ivPlayStop.setImageResource(R.drawable.ic_stop);

            }
        });

        binding.liBack.setOnClickListener(this);
        binding.clBellChoice.setOnClickListener(this);
        binding.liPlayStop.setOnClickListener(this);
        binding.liComplete.setOnClickListener(this);

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
        binding.npMinute.setMaxValue(59);
        binding.npMinute.setWrapSelectorWheel(true);

        ArrayList<String> integerArrayList = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            integerArrayList.add(String.format("%02d", i));
        }
        String[] arrMinute = integerArrayList.toArray(new String[integerArrayList.size()]);
        binding.npMinute.setDisplayedValues(arrMinute);

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatHour = new SimpleDateFormat("HH");
        SimpleDateFormat formatMinute = new SimpleDateFormat("mm");
        SimpleDateFormat formatAmPm = new SimpleDateFormat("a");

        String strCurHour = formatHour.format(date);
        String strCurMinute = formatMinute.format(date);
        String strCurAmPm = formatAmPm.format(date);

        LogUtil.d(strCurHour);
        LogUtil.d(strCurMinute);
        LogUtil.d(strCurAmPm);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (strCurAmPm.equals("AM") || strCurAmPm.equals("오전")) {
                    binding.npAmPm.setValue(0);
                    binding.npHour.setValue(Integer.parseInt(strCurHour));
                } else {
                    binding.npAmPm.setValue(1);
                    binding.npHour.setValue(Integer.parseInt(strCurHour) - 12);
                }

                binding.npMinute.setValue(Integer.parseInt(strCurMinute));
            }
        });


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
        } else if (!sunday && !saturday && monday && tuesday && wednesday && thursday && friday) {
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

    // 미디어플레이어 재생
    private void startMediaPlayer(Uri uri) {
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
    }

    // 미디어플레이어 멈춤
    private void stopMediaPlayer() {
        mediaPlayer.stop();
        mediaPlayer.reset();
    }

    //-- 링톤을 재생하는 함수
 /*   private void startRingtone(Uri uriRingtone) {
        this.stopRingtone();
        try {
            mRtCurrent = RingtoneManager.getRingtone(this, uriRingtone);
            if (mRtCurrent == null) {
                throw new Exception("Can't play ringtone");
            }
//            m_tvRingtoneUri.setText(uriRingtone.toString());
            binding.tvRingtoneTitle.setText(mRtCurrent.getTitle(this));
            mRtCurrent.play();
        } catch (Exception e) {
//            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            LogUtil.e("AddAlarmActivity" + e.getMessage());
            e.printStackTrace();
        }
    }*/

    //-- 재생중인 링톤을 중지 하는 함수
/*    private void stopRingtone() {
        if (mRtCurrent != null) {
            if (mRtCurrent.isPlaying()) {
                mRtCurrent.stop();
//                mRtCurrent = null;
            }
        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
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

    //-- 알림선택창에서 넘어온 데이터를 처리하는 코드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_RINGTONE_PICKER) {
            if (resultCode == RESULT_OK) {
                Uri ring = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                if (ring != null) {
                    mediaPlayer = MediaPlayer.create(this, ring);
                    strRingtoneUri = ring.toString();
                    LogUtil.d("strRingtoneUri=" + strRingtoneUri);

                    try {
                        mRtCurrent = RingtoneManager.getRingtone(this, ring);
                        if (mRtCurrent == null) {
                            throw new Exception("Can't play ringtone");
                        }
                        binding.tvRingtoneTitle.setText(mRtCurrent.getTitle(this));
                    } catch (Exception e) {
                        Toast.makeText(this, "오류 발생", Toast.LENGTH_SHORT).show();
                        LogUtil.e("AddAlarmActivity" + e.getMessage());
                        e.printStackTrace();
                    }

                } else {
                    binding.tvRingtoneTitle.setText(mRtCurrent.getTitle(this));
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

//        stopRingtone();
        stopMediaPlayer();
        binding.ivPlayStop.setImageResource(R.drawable.ic_play);
    }

    /*
     *   1. Preference 에 Alarm 을 저장 (유저의 이름 + 관계 값으로 알람을 비교)
     *   2. RecyclerView 에 추가
     *   3. RecyclerView 출력
     * */
    private void registerAlarm(Alarm alarm) {
        alarmList.add(alarm.toJSON());

        // Preference 에 저장
        PreferenceUtil.setJSONArrayPreference(this, Config.PREFERENCE_KEY.ALARM_LIST, alarmList);
    }

    private boolean checkAlarmName() {
        /* 해당 유저의 알람중에 같은 알람이 있는지 검사하는 코드 */
        if (PreferenceUtil.getJSONArrayPreference(this, Config.PREFERENCE_KEY.ALARM_LIST) != null
                && PreferenceUtil.getJSONArrayPreference(this, Config.PREFERENCE_KEY.ALARM_LIST).size() > 0) {

            JSONArray jsonArray = new JSONArray(PreferenceUtil.getJSONArrayPreference(this, Config.PREFERENCE_KEY.ALARM_LIST));

            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject object = new JSONObject(jsonArray.getString(i));

                    if (object.getString("name").equals(binding.etName.getText().toString())) {
                        isSuccess = false;
                    } else {
                        isSuccess = true;
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


    //  버튼으로 음량조절
//  현재 seekBar 와 안맞음
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        int currentVol = audioManager
//                .getStreamVolume(AudioManager.STREAM_MUSIC);
//
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_VOLUME_UP :
//                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
//                        AudioManager.ADJUST_RAISE,
//                        AudioManager.FLAG_SHOW_UI);
//
//                binding.sbVolume.setProgress(currentVol);
//                return true;
//            case KeyEvent.KEYCODE_VOLUME_DOWN:
//                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
//                        AudioManager.ADJUST_LOWER,
//                        AudioManager.FLAG_SHOW_UI);
//
//                binding.sbVolume.setProgress(currentVol);
//                return true;
//            case KeyEvent.KEYCODE_BACK:
//                return true;
//        }
//
//        return false;
//    }
//
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        int currentVol = audioManager
//                .getStreamVolume(AudioManager.STREAM_MUSIC);
//
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_VOLUME_UP :
//                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
//                        AudioManager.ADJUST_SAME,
//                        AudioManager.FLAG_SHOW_UI);
//
//                binding.sbVolume.setProgress(currentVol);
//                return true;
//            case KeyEvent.KEYCODE_VOLUME_DOWN:
//                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
//                        AudioManager.ADJUST_SAME,
//                        AudioManager.FLAG_SHOW_UI);
//
//                binding.sbVolume.setProgress(currentVol);
//                return true;
//            case KeyEvent.KEYCODE_BACK:
//                finish();
//                return true;
//        }
//        return false;
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.li_back:
                finish();
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
                        startMediaPlayer(firstUriRingtone);
                    }
                    binding.ivPlayStop.setImageResource(R.drawable.ic_stop);
                }


               /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!notificationManager.isNotificationPolicyAccessGranted()) {
                        Toast.makeText(getApplicationContext(), "방해금지 권한을 허용해주세요", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS));
                    } else {
                        if (mRtCurrent != null) {
                            if (mRtCurrent.isPlaying()) {
                                stopRingtone();
                                binding.ivPlayStop.setImageResource(R.drawable.ic_play);
                            } else {

                                mRtCurrent.play();
                                binding.ivPlayStop.setImageResource(R.drawable.ic_stop);

                                switch (audioManager.getRingerMode()) {
                                    case AudioManager.RINGER_MODE_NORMAL:  // 벨소리모드
                                        mRtCurrent.play();
                                        binding.ivPlayStop.setImageResource(R.drawable.ic_stop);
                                        break;
                                    case AudioManager.RINGER_MODE_VIBRATE: // 진동모드
                                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL); //벨소리모드로 변경
                                        mRtCurrent.play();
                                        binding.ivPlayStop.setImageResource(R.drawable.ic_stop);
                                        break;
                                    case AudioManager.RINGER_MODE_SILENT:  // 무음모드
//                                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE); //진동모드로 변경
//                                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT); //무음모드로 변경
                                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL); //벨소리모드로 변경
                                        mRtCurrent.play();
                                        binding.ivPlayStop.setImageResource(R.drawable.ic_stop);
                                        break;
                                }
                            }
                        }
                    }

                } else {
                    if (mRtCurrent != null) {
                        if (mRtCurrent.isPlaying()) {
                            stopRingtone();
                            binding.ivPlayStop.setImageResource(R.drawable.ic_play);
                        } else {
                            mRtCurrent.play();
                            binding.ivPlayStop.setImageResource(R.drawable.ic_stop);
                        }
                    }
                }*/


                break;
            case R.id.li_complete:

                if (checkAlarmName()) {

                    if (binding.etName.getText().length() > 0) {
                        Alarm alarm = new Alarm();
                        alarm.setName(binding.etName.getText().toString());

                        alarm.setAmPm(binding.npAmPm.getDisplayedValues()[binding.npAmPm.getValue()]);
                        alarm.setHour(String.valueOf(binding.npHour.getValue()));
                        alarm.setMinute(binding.npMinute.getDisplayedValues()[binding.npMinute.getValue()]);

//                        if (binding.npHour.getValue() > 12) {
//                                alarm.setHour(String.valueOf(binding.npHour.getValue() - 12));
//                            } else {
//                                alarm.setHour(String.valueOf(binding.npHour.getValue()));
//                            }

//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            alarm.setHour(String.valueOf(binding.timePicker.getHour()));
//                            alarm.setMinute(String.valueOf(binding.timePicker.getMinute()));
//
//                            if (binding.timePicker.getHour() > 12) {
//                                alarm.setAmPm("오후");
//                                alarm.setHour(String.valueOf(binding.timePicker.getHour() - 12));
//                            } else {
//                                alarm.setAmPm("오전");
//                                alarm.setHour(String.valueOf(binding.timePicker.getCurrentHour()));
//                            }
//
//                        } else {
//
//                            if (binding.timePicker.getCurrentHour() > 12) {
//                                alarm.setAmPm("오후");
//                                alarm.setHour(String.valueOf(binding.timePicker.getCurrentHour() - 12));
//                            } else {
//                                alarm.setAmPm("오전");
//                                alarm.setHour(String.valueOf(binding.timePicker.getCurrentHour()));
//                            }
//                            alarm.setMinute(String.valueOf(binding.timePicker.getCurrentMinute()));
//                        }

                        alarm.setVolume(binding.sbVolume.getProgress());
                        alarm.setRingtoneName(binding.tvRingtoneTitle.getText().toString());
                        alarm.setRingtoneUri(Uri.parse(strRingtoneUri));
                        alarm.setDayOfWeek(binding.tvWeek.getText().toString());
                        alarm.setAlarmON(true);

                        ((MainActivity) mainActivityContext).startDayOfWeekService();

                        registerAlarm(alarm);
                        finish();

                    } else {
                        Toast.makeText(this, "알람 이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, "이미 저장되어 있는 알람 이름 입니다", Toast.LENGTH_SHORT).show();
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