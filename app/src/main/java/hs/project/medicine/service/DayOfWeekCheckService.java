package hs.project.medicine.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import hs.project.medicine.Config;
import hs.project.medicine.MediApplication;
import hs.project.medicine.R;
import hs.project.medicine.datas.Alarm;
import hs.project.medicine.receiver.AlarmReceiver;
import hs.project.medicine.util.LogUtil;
import hs.project.medicine.util.PreferenceUtil;

public class DayOfWeekCheckService extends Service {

    private String strDoDayOfWeek;
    private ArrayList<Alarm> allAlarmList;

    private ArrayList<Alarm> playAlarmList;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private int getHourTimePicker;
    private int getMinuteTimePicker;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d("DayOfWeekCheckService create");

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "medicine";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "메디케어",
                    NotificationManager.IMPORTANCE_LOW);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("알람")
                    .setContentText("활성화")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();

            startForeground(1, notification);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        new Handler().post(new Runnable() {
            @Override
            public void run() {

                /* 밤 12시마다 요일 변경 */
                PreferenceUtil.putSharedPreference(MediApplication.ApplicationContext(), Config.PREFERENCE_KEY.DAY_OF_WEEK, doDayOfWeek());

                strDoDayOfWeek = PreferenceUtil.getSharedPreference(MediApplication.ApplicationContext(), Config.PREFERENCE_KEY.DAY_OF_WEEK);

                /* 알람목록 가져오기 */
                getAlarmList();
                LogUtil.d("allAlarmList.size=" + allAlarmList.size());

                /* 오늘 요일이 포함되어 있는 알람들만 추려서 새로운 알람리스트 생성 */
                setPlayAlarmList();

                /* 알람목록 제거 */
                removeAlarmList();

                // 알람 등록후 알람제거 후 다시 등록해주기
                if (playAlarmList.size() > 0) {
                    for (int i = 0; i < playAlarmList.size(); i++) {

                        /* 알람 ON 인 것만 알람등록 */
                        if (playAlarmList.get(i).isAlarmON()) {
                            setAlarm(playAlarmList.get(i), i);
                            LogUtil.d("setAlarm=" + playAlarmList.get(i).getName());
                        }

                    }
                }
            }
        });

        //
//        /* 알람목록 가져오기 */
//        getAlarmList();
//        LogUtil.d("allAlarmList.size=" + allAlarmList.size());
//
//        /* 오늘 요일이 포함되어 있는 알람들만 추려서 새로운 알람리스트 생성 */
//        setPlayAlarmList();
//
//
//        // 알람 등록후 알람제거 후 다시 등록해주기
//        if (playAlarmList.size() > 0) {
//            for (int i = 0; i < playAlarmList.size(); i++) {
//
//                /* 알람 ON 인 것만 알람등록 */
//                if (playAlarmList.get(i).isAlarmON()) {
//                    setAlarm(playAlarmList.get(i), i);
//                    LogUtil.d("setAlarm=" + playAlarmList.get(i).getName());
//                }
//
//            }
//        }

        return START_STICKY;
    }

    private void setPlayAlarmList() {

        playAlarmList = new ArrayList<>();
        if (allAlarmList.size() > 0) {
            for (int i = 0; i < allAlarmList.size(); i++) {

                if (allAlarmList.get(i).getDayOfWeek().equals("매일")) {
                    playAlarmList.add(allAlarmList.get(i));
                    LogUtil.d(allAlarmList.get(i).getName());
                } else if (allAlarmList.get(i).getDayOfWeek().equals("주말")) {

                    if (strDoDayOfWeek.equals("토") || strDoDayOfWeek.equals("일")) {
                        playAlarmList.add(allAlarmList.get(i));
                        LogUtil.d(allAlarmList.get(i).getName());
                    }

                } else {

                    if (allAlarmList.get(i).getDayOfWeek().contains("일") && strDoDayOfWeek.equals("일")) {
                        playAlarmList.add(allAlarmList.get(i));
                        LogUtil.d(allAlarmList.get(i).getName());
                    }
                    if (allAlarmList.get(i).getDayOfWeek().contains("월") && strDoDayOfWeek.equals("월")) {
                        playAlarmList.add(allAlarmList.get(i));
                        LogUtil.d(allAlarmList.get(i).getName());
                    }
                    if (allAlarmList.get(i).getDayOfWeek().contains("화") && strDoDayOfWeek.equals("화")) {
                        playAlarmList.add(allAlarmList.get(i));
                        LogUtil.d(allAlarmList.get(i).getName());
                    }
                    if (allAlarmList.get(i).getDayOfWeek().contains("수") && strDoDayOfWeek.equals("수")) {
                        playAlarmList.add(allAlarmList.get(i));
                        LogUtil.d(allAlarmList.get(i).getName());
                    }
                    if (allAlarmList.get(i).getDayOfWeek().contains("목") && strDoDayOfWeek.equals("목")) {
                        playAlarmList.add(allAlarmList.get(i));
                        LogUtil.d(allAlarmList.get(i).getName());
                    }
                    if (allAlarmList.get(i).getDayOfWeek().contains("금") && strDoDayOfWeek.equals("금")) {
                        playAlarmList.add(allAlarmList.get(i));
                        LogUtil.d(allAlarmList.get(i).getName());
                    }
                    if (allAlarmList.get(i).getDayOfWeek().contains("토") && strDoDayOfWeek.equals("토")) {
                        playAlarmList.add(allAlarmList.get(i));
                        LogUtil.d(allAlarmList.get(i).getName());
                    }
                }
            }
        }
    }


    private void removeAlarmList() {

        if (playAlarmList != null && playAlarmList.size() > 0) {

            for (int i = 0; i < playAlarmList.size(); i++) {
                Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                PendingIntent removeIntent = PendingIntent.getBroadcast(getApplicationContext(), i, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                LogUtil.d(playAlarmList.get(i).getName() + " 제거");
                alarmManager.cancel(removeIntent);
                removeIntent.cancel();
            }

        }

    }

    private void setAlarm(Alarm alarm, int requestCode) {

        /* 요일이 같으면 알람매니저에 등록  */

        // Calendar 객체 생성
        final Calendar calendar = Calendar.getInstance();

        // 시간 가져옴
        if (alarm.getAmPm().equals("오후")) {
            getHourTimePicker = Integer.parseInt(alarm.getHour()) + 12;
        } else {
            getHourTimePicker = Integer.parseInt(alarm.getHour());
        }

        getMinuteTimePicker = Integer.parseInt(alarm.getMinute());


        // 현재 지정된 시간으로 알람 시간 설정
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, getHourTimePicker);
        calendar.set(Calendar.MINUTE, getMinuteTimePicker);
        calendar.set(Calendar.SECOND, 0);

        // reveiver에 string 값 넘겨주기
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("uri", alarm.getRingtoneUri().toString());

        // receiver를 동작하게 하기 위해 PendingIntent의 인스턴스를 생성할 때, getBroadcast 라는 메소드를 사용
        // requestCode는 나중에 Alarm을 해제 할때 어떤 Alarm을 해제할지를 식별하는 코드
        pendingIntent = PendingIntent.getBroadcast(this, requestCode, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long currentTime = System.currentTimeMillis(); // 현재 시간
        //long triggerTime = SystemClock.elapsedRealtime() + 1000*60;
        long triggerTime = calendar.getTimeInMillis(); // 알람을 울릴 시간
        long interval = 1000 * 60 * 60 * 24; // 하루의 시간

        while (currentTime > triggerTime) { // 현재 시간보다 작다면
            triggerTime += interval; // 다음날 울리도록 처리
        }

        // 알림 세팅 : AlarmManager 인스턴스에서 set 메소드를 실행시키는 것은 단발성 Alarm을 생성하는 것
        // RTC_WAKEUP : UTC 표준시간을 기준으로 하는 명시적인 시간에 intent를 발생, 장치를 깨움
        if (Build.VERSION.SDK_INT < 23) {
            if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            } else {
                // 알람셋팅
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
        } else {  // 23 이상

//            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), pendingIntent);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            //alarm_manager.set(AlarmManager.RTC_WAKEUP, triggerTime,pendingIntent);
            //알람 매니저를 통한 반복알람 설정
            //alarm_manager.setRepeating(AlarmManager.RTC, triggerTime, interval, pendingIntent);
            // interval : 다음 알람이 울리기까지의 시간
        }
    }

    private void getAlarmList() {

        allAlarmList = new ArrayList<>();

        /* 저장되어 있는 알람리스트 가져오기 */
        if (PreferenceUtil.getJSONArrayPreference(MediApplication.ApplicationContext(), Config.PREFERENCE_KEY.ALARM_LIST) != null
                && PreferenceUtil.getJSONArrayPreference(MediApplication.ApplicationContext(), Config.PREFERENCE_KEY.ALARM_LIST).size() > 0) {

            JSONArray jsonArray = new JSONArray(PreferenceUtil.getJSONArrayPreference(MediApplication.ApplicationContext(), Config.PREFERENCE_KEY.ALARM_LIST));

            try {

                for (int i = 0; i < jsonArray.length(); i++) {
                    Alarm alarm = new Alarm();
                    JSONObject object = new JSONObject(jsonArray.getString(i));
                    alarm.setName(object.getString("name"));
                    alarm.setAmPm(object.getString("amPm"));
                    alarm.setHour(object.getString("hour"));
                    alarm.setMinute(object.getString("minute"));
                    alarm.setVolume(object.getInt("volume"));
                    alarm.setRingtoneName(object.getString("ringtoneName"));
                    alarm.setRingtoneUri(Uri.parse(object.getString("ringtoneUri")));
                    alarm.setDayOfWeek(object.getString("dayOfWeek"));
                    alarm.setAlarmON(object.getBoolean("alarmON"));

                    allAlarmList.add(alarm);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            LogUtil.d("저장된 알람 목록 없음");
        }
    }

    private String doDayOfWeek() {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d("DayOfWeekCheckService destroy");
    }
}