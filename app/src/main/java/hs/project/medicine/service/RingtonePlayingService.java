package hs.project.medicine.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import hs.project.medicine.R;
import hs.project.medicine.util.LogUtil;


public class RingtonePlayingService extends Service {

    private MediaPlayer mediaPlayer;
    private int startId;
    private boolean isRunning;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "default";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("알람")
                    .setContentText("알람음이 재생됩니다.")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();

            startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String getState = intent.getExtras().getString("state");
        LogUtil.d("state=" + getState);

        String getRingtoneUri = intent.getExtras().getString("uri");
        LogUtil.d("uri=" + getRingtoneUri);

//        assert getState != null;
        switch (getState) {
            case "ON":
                startId = 1;
                break;
            case "OFF":
                startId = 0;
                break;
            default:
                startId = 0;
                break;
        }

        // 알람음 재생 X , 알람음 시작 클릭
        if (!isRunning && startId == 1) {

//            mediaPlayer = MediaPlayer.create(this, R.raw.ouu);
            mediaPlayer = MediaPlayer.create(this, Uri.parse(getRingtoneUri));
            mediaPlayer.start();

            isRunning = true;
            startId = 0;
        }

        // 알람음 재생 O , 알람음 종료 버튼 클릭
        else if (isRunning && startId == 0) {

            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();

            isRunning = false;
            startId = 0;
        }

        // 알람음 재생 X , 알람음 종료 버튼 클릭
        else if (!isRunning && startId == 0) {
            isRunning = false;
            startId = 0;
        }

        // 알람음 재생 O , 알람음 시작 버튼 클릭
        else if (isRunning && startId == 1) {

            isRunning = true;
            startId = 1;
        } else {
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d("서비스 제거");
    }
}
