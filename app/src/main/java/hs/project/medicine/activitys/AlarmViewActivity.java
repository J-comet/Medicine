package hs.project.medicine.activitys;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import hs.project.medicine.MediApplication;
import hs.project.medicine.R;
import hs.project.medicine.databinding.ActivityAlarmViewBinding;
//import hs.project.medicine.service.RingtonePlayingService;
import hs.project.medicine.util.LogUtil;

public class AlarmViewActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAlarmViewBinding binding;
    private String getRingtoneUri;
    private String getName;
    private int getRingtoneVol = -1;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlarmViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /* 전체화면 코드 시작 */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars());
            }
        } else {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
        }

        /* 하단 네비게이션바 (유저가 터치시 네비바 올라옴)
        투명색 적용 및 숨김처리 */
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        /* 잠금해제 코드 */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(this.KEYGUARD_SERVICE);
            keyguardManager.requestDismissKeyguard(this, null);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        init();
    }

    private void init() {
        binding.clAlarmClear.setOnClickListener(this);

//        SimpleDateFormat simpleTime = new SimpleDateFormat("hh : mm");
//        String getTime = simpleTime.format(date);

        Locale locale = getResources().getConfiguration().locale;

        binding.digitalClock.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                binding.tvTime.setText(s.toString());

                String result = String.valueOf(s);
                String[] splitText = result.split(":");

                int hour = -1;
                int minute = -1;
                int seconds = -1;
                String amPm = "-1";

                /*  24시간 형식을 사용 중인 사용자 일때 */
                if (DateFormat.is24HourFormat(MediApplication.ApplicationContext())) {

                    for (int i = 0; i < splitText.length; i++) {
                        hour = Integer.parseInt(splitText[0]);
                        minute = Integer.parseInt(splitText[1]);
                        seconds = Integer.parseInt(splitText[2]);
                    }

//                    minute = Integer.parseInt(String.format("%02d", minute));
//                    binding.tvTime.setText(hour + " : " + minute);
                    binding.tvTime.setText(String.format(locale, "%01d : %02d", hour, minute));

                } else {

                    for (int i = 0; i < splitText.length; i++) {
                        hour = Integer.parseInt(splitText[0]);
                        minute = Integer.parseInt(splitText[1]);
                        seconds = Integer.parseInt(splitText[2]);
                        amPm = splitText[3];
                    }

//                    minute = Integer.parseInt(String.format("%02d", minute));

                    binding.tvTime.setText(String.format(locale, "%01d : %02d", hour, minute));
                    binding.tvAmPm.setText(amPm);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        setData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAdView();
    }

    private void setData() {
        // 음량값 받기
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        getRingtoneVol = getIntent().getExtras().getInt("vol");

        // 노래 URI 받기
        getRingtoneUri = getIntent().getExtras().getString("uri");

        // 알람이름 받기
        getName = getIntent().getExtras().getString("name");
        LogUtil.d("name=" + getName + " uri=" + getRingtoneUri + " vol=" + getRingtoneVol);

//        startMediaPlayer(Uri.parse("content://media/internal/audio/media/37"));

        long now = System.currentTimeMillis();
        Date date = new Date(now);

        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy년 MM월 dd일");
        String getDate = simpleDate.format(date);

        if (DateFormat.is24HourFormat(MediApplication.ApplicationContext())) {
            binding.tvAmPm.setVisibility(View.GONE);
        } else {
            binding.tvAmPm.setVisibility(View.VISIBLE);
        }

        binding.tvDate.setText(getDate);
        binding.tvName.setText(getName);
        startMediaPlayer(Uri.parse(getRingtoneUri));
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, getRingtoneVol, 0);
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

    private void startAdView() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);

        binding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                LogUtil.e("배너광고 FAIL");
                LogUtil.e(loadAdError.toString());
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                LogUtil.e("배너광고 SUCCESS");
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }
        });

    }

    private void clear() {
        /*Intent serviceIntent = new Intent(AlarmViewActivity.this, RingtonePlayingService.class);
        serviceIntent.putExtra("state", "OFF");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }*/
        stopMediaPlayer();

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