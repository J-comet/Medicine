package hs.project.medicine.activitys;

import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import hs.project.medicine.R;
import hs.project.medicine.databinding.ActivityAddAlarmBinding;
import hs.project.medicine.util.LogUtil;


public class AddAlarmActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAddAlarmBinding binding;

    private String strRingtoneUri;
    private final static int REQUEST_CODE_RINGTONE_PICKER = 1000;
    private RingtoneManager mRtm; // 현재 재생중인 링톤
    private Ringtone mRtCurrent;

    private AudioManager audioManager;

    private Uri firstUriRingtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();


    }

    private void init() {
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        firstUriRingtone = Uri.parse("content://media/internal/audio/media/33?title=Bubble&canonical=1");  // Bubble 로 처음 유저 설정
        mRtCurrent = RingtoneManager.getRingtone(this, firstUriRingtone);

        // 음량값 받기
        int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        binding.sbVolume.setMax(maxVol);
        binding.sbVolume.setProgress(maxVol);

        binding.sbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //음악 음량 변경
                audioManager.setStreamVolume(AudioManager.STREAM_RING, progress, 0);
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

        binding.liBack.setOnClickListener(this);
        binding.clBellChoice.setOnClickListener(this);
        binding.liPlayStop.setOnClickListener(this);

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

    //-- 링톤을 재생하는 함수
    private void startRingtone(Uri uriRingtone) {
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
    }

    //-- 재생중인 링톤을 중지 하는 함수
    private void stopRingtone() {
        if (mRtCurrent != null) {
            if (mRtCurrent.isPlaying()) {
                mRtCurrent.stop();
//                mRtCurrent = null;
            }
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
                    strRingtoneUri = ring.toString();
                    LogUtil.d("strRingtoneUri=" + strRingtoneUri);

                    try {
                        mRtCurrent = RingtoneManager.getRingtone(this, ring);
                        if (mRtCurrent == null) {
                            throw new Exception("Can't play ringtone");
                        }
                        binding.tvRingtoneTitle.setText(mRtCurrent.getTitle(this));
//                        mRtCurrent.play();
                    } catch (Exception e) {
                        Toast.makeText(this, "오류 발생", Toast.LENGTH_SHORT).show();
                        LogUtil.e("AddAlarmActivity" + e.getMessage());
                        e.printStackTrace();
                    }

                } else if (!binding.tvRingtoneTitle.getText().equals("Bubble")) {
                    /* 이전에 다른 소리를 선택한 후 다음 선택할 때 아무것도 선택하지 않은 유저라면 이전에 선택했던 소리로 세팅 */

                } else {
                    strRingtoneUri = null;
                    binding.tvRingtoneTitle.setText("Bubble");
                }
            }
        }
    }


//  버튼으로 음량조절
//  현재 seekBar 와 안맞음
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        int currentVol = audioManager
//                .getStreamVolume(AudioManager.STREAM_MUSIC);
//
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_VOLUME_UP :
//                audioManager.adjustStreamVolume(AudioManager.STREAM_RING,
//                        AudioManager.ADJUST_RAISE,
//                        AudioManager.FLAG_SHOW_UI);
//
//                binding.sbVolume.setProgress(currentVol);
//                return true;
//            case KeyEvent.KEYCODE_VOLUME_DOWN:
//                audioManager.adjustStreamVolume(AudioManager.STREAM_RING,
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
//                audioManager.adjustStreamVolume(AudioManager.STREAM_RING,
//                        AudioManager.ADJUST_SAME,
//                        AudioManager.FLAG_SHOW_UI);
//
//                binding.sbVolume.setProgress(currentVol);
//                return true;
//            case KeyEvent.KEYCODE_VOLUME_DOWN:
//                audioManager.adjustStreamVolume(AudioManager.STREAM_RING,
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
    protected void onPause() {
        super.onPause();

        stopRingtone();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.li_back:
                finish();
                break;
            case R.id.cl_bell_choice:
                stopRingtone();
                showRingtoneChooser();
                break;
            case R.id.li_play_stop:
                if (mRtCurrent != null) {
                    if (mRtCurrent.isPlaying()) {
                        stopRingtone();
                        binding.ivPlayStop.setImageResource(R.drawable.ic_play);
                    } else {
                        mRtCurrent.play();
                        binding.ivPlayStop.setImageResource(R.drawable.ic_stop);
                    }
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