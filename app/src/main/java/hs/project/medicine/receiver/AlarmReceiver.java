package hs.project.medicine.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.PowerManager;

import hs.project.medicine.activitys.AlarmViewActivity;
import hs.project.medicine.service.RingtonePlayingService;
import hs.project.medicine.util.LogUtil;


/**
 * 알람
 */
public class AlarmReceiver extends BroadcastReceiver {

    private static PowerManager.WakeLock sCpuWakeLock;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

//        Alarm alarm = (Alarm) intent.getSerializableExtra("alarm");

        // intent 로부터 전달받은 string
        String strState = intent.getExtras().getString("state");
        String strRingtoneUri = intent.getExtras().getString("uri");

        LogUtil.d("strState=" + strState + "/" + "uri=" + strRingtoneUri);

        // RingtonePlayingService 서비스 intent 생성
        Intent serviceIntent = new Intent(context, RingtonePlayingService.class);
        serviceIntent.putExtra("state", strState);
        serviceIntent.putExtra("uri", strRingtoneUri);

        // start the ringtone service
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            this.context.startForegroundService(serviceIntent);
        } else {
            this.context.startService(serviceIntent);
        }

        if (sCpuWakeLock != null) {
            return;
        }

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        sCpuWakeLock = pm.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE, "hi");

        sCpuWakeLock.acquire();

        if (sCpuWakeLock != null) {
            sCpuWakeLock.release();
            sCpuWakeLock = null;
        }



        Intent alarmIntent = new Intent("android.intent.action.sec");
        alarmIntent.setClass(context, AlarmViewActivity.class);
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Start the popup activity
        context.startActivity(alarmIntent);

    }

}
