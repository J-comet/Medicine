package hs.project.medicine.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import hs.project.medicine.MediApplication;
import hs.project.medicine.R;
import hs.project.medicine.util.NetworkUtil;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 인터넷 연결 체크 후 앱 실행
       if (NetworkUtil.checkConnectedNetwork(this)) {
           new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
               @Override
               public void run() {

                   Intent intent = new Intent(SplashActivity.this, MainActivity.class);
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
}