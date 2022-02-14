package hs.project.medicine.receiver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;

import hs.project.medicine.activitys.AlarmViewActivity;
import hs.project.medicine.util.LogUtil;

public class AlarmReceiver extends BroadcastReceiver {

    Context mContext;

    PowerManager powerManager;
    private static PowerManager.WakeLock wakeLock;
    //    String get_state;
    private MediaPlayer mediaPlayer;
    private String strRingtoneUri;
    private int ringtoneVol = -1;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        LogUtil.d("onReceive");
        ringtoneVol =  intent.getExtras().getInt("vol");
        strRingtoneUri = intent.getExtras().getString("uri");
        
        Intent dataIntent = new Intent(context, AlarmViewActivity.class);
        dataIntent.putExtra("uri", strRingtoneUri);
        dataIntent.putExtra("vol", ringtoneVol);
        dataIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(dataIntent);

//        AlarmReceiverChk(context, intent);
    }

    @SuppressLint("InvalidWakeLockTag")
    private void AlarmReceiverChk(final Context context, final Intent intent) {
        LogUtil.d("Alarm Receiver started!");

        powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

//        if (wakeLock != null) {
//            return;
//        }

        wakeLock = powerManager.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE, "WAKELOCK");

        wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);

        /* 잠금화면 위에 액티비티가 안뜨는 중 손으로 잠금화면 풀어줘야 해제됨 */
//        Intent alarmIntent = new Intent("android.intent.action.sec");
//        alarmIntent.setClass(context, AlarmViewActivity.class);
//        alarmIntent.putExtra("uri", strRingtoneUri);
//        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(alarmIntent);


        Intent dataIntent = new Intent(context, AlarmViewActivity.class);
        dataIntent.putExtra("uri", strRingtoneUri);
        dataIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(dataIntent);

        LogUtil.d("Alarm Receiver Intent!");

        if (wakeLock != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    wakeLock.release();
                    wakeLock = null;
                }
            }, 1000 * 60 * 3);

        }

        /* 데이터 보내기 */
//        Intent dataIntent = new Intent(context, AlarmViewActivity.class);
////        dataIntent.putExtra("state", strState);
//        dataIntent.putExtra("uri", strRingtoneUri);
//        dataIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(dataIntent);


//        releaseCpuLock();


        /*switch (get_state) {
            case "ALARM_ON":
                acquireCPUWakeLock(context, intent);
                // RingtoneService 서비스 intent 생성
                *//*Intent serviceIntent = new Intent(mContext, RingtoneService.class);
                serviceIntent.putExtra("state", get_state);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                    context.startForegroundService(serviceIntent);
                } else {
                    context.startService(serviceIntent);
                }*//*
                break;
            case "ALARM_OFF": // stopService 가 동작하지 않아서 startService 로 처리하고 나서....
                releaseCpuLock();
                *//*Intent stopIntent = new Intent(context, RingtoneService.class);
                stopIntent.putExtra("state", get_state);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                    context.startForegroundService(stopIntent);
                } else {
                    context.startService(stopIntent);
                }*//*
                break;
        }*/
    }

    @SuppressLint("InvalidWakeLockTag")
    private void acquireCPUWakeLock(Context context, Intent intent) {
        // 잠든 화면 깨우기
        if (wakeLock != null) {
            return;
        }
        powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.ON_AFTER_RELEASE, "WAKELOCK");
        wakeLock.acquire();
        LogUtil.d("Acquire cpu WakeLock = " + wakeLock);
    }

    private void releaseCpuLock() {
        LogUtil.d("Releasing cpu WakeLock = " + wakeLock);

        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
    }
}

