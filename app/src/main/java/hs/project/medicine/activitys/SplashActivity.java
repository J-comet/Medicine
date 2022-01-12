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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import hs.project.medicine.MediApplication;
import hs.project.medicine.R;
import hs.project.medicine.databinding.ActivitySplashBinding;
import hs.project.medicine.databinding.ActivityUserListBinding;
import hs.project.medicine.util.LogUtil;
import hs.project.medicine.util.NetworkUtil;

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
    protected void onStart() {
        super.onStart();

        // 인터넷 연결 체크 후 앱 실행
        if (NetworkUtil.checkConnectedNetwork(this)) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {

                    binding.lottieView.addAnimatorListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) { }
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                            finish();
                        }
                        @Override
                        public void onAnimationCancel(Animator animation) { }
                        @Override
                        public void onAnimationRepeat(Animator animation) { }
                    });
                }
            }, 1500);
        } else {
            NetworkUtil.networkErrorDialogShow(SplashActivity.this, true);
        }
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