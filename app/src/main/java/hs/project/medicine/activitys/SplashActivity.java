package hs.project.medicine.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

        getHashKey();

        // 인터넷 연결 체크 후 앱 실행
       if (NetworkUtil.checkConnectedNetwork(this)) {
           new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
               @Override
               public void run() {

                   Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                   intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                   startActivity(intent);
                   finish();
               }
           }, 1500);
        } else {
           AlertDialog dialog = new AlertDialog.Builder(SplashActivity.this)
                   .setTitle("네트워크 경고")
                   .setMessage("네트워크에 연결되지 않았습니다")
                   .setCancelable(false)
                   .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i) {
                           dialogInterface.dismiss();
                           finish();
                       }
                   }).create();

           dialog.show();
           dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.black));
       }



    }

    private void getHashKey(){
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
                LogUtil.d("KeyHash/"+Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                LogUtil.e("KeyHash"+ "Unable to get MessageDigest. signature=" + signature + e);
            }
        }
    }
}