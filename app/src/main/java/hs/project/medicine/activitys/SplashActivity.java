package hs.project.medicine.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;

import hs.project.medicine.Config;
import hs.project.medicine.MediApplication;
import hs.project.medicine.R;
import hs.project.medicine.databinding.ActivitySplashBinding;
import hs.project.medicine.util.LogUtil;
import hs.project.medicine.util.NetworkUtil;
import hs.project.medicine.util.PreferenceUtil;

public class SplashActivity extends BaseActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        getDebugHashKey();
//        getReleaseHashKey();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 인터넷 연결 체크 후 앱 실행
        if (NetworkUtil.checkConnectedNetwork(this)) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {

                    binding.lottieView.addAnimatorListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            setDayOfWeek();
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    });
                }
            }, 1500);
        } else {
            NetworkUtil.networkErrorDialogShow(SplashActivity.this, true);
        }
    }

    /* 요일 값 세팅 */
    private void setDayOfWeek() {
        if (PreferenceUtil.getSharedPreference(SplashActivity.this, Config.PREFERENCE_KEY.DAY_OF_WEEK) == null
                || PreferenceUtil.getSharedPreference(SplashActivity.this, Config.PREFERENCE_KEY.DAY_OF_WEEK).length() < 1) {

            PreferenceUtil.putSharedPreference(this,Config.PREFERENCE_KEY.DAY_OF_WEEK, doDayOfWeek());
        }
    }

    public String doDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        String week = "";

        int calendarWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (calendarWeek) {
            case 1:
                week = "일";
                break;
            case 2:
                week = "월";
                break;
            case 3:
                week = "화";
                break;
            case 4:
                week = "수";
                break;
            case 5:
                week = "목";
                break;
            case 6:
                week = "금";
                break;
            case 7:
                week = "토";
                break;
        }

        return week;
    }

    /* debug hash key */
    private void getDebugHashKey() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            LogUtil.e("KeyHash/KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                LogUtil.d("KeyHash/" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                LogUtil.e("KeyHash" + "Unable to get MessageDigest. signature=" + signature + e);
            }
        }
    }

    /* release hash key */
    private void getReleaseHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest messageDigest;
                messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                String something = new String(Base64.encode(messageDigest.digest(), 0));
                binding.tvHashkey.setText(something);
            }

        } catch (Exception e) {
            LogUtil.e("not found/" + e.toString());
        }
    }
}