package hs.project.medicine.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.view.View;

import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import hs.project.medicine.Config;
import hs.project.medicine.MediApplication;
import hs.project.medicine.R;
import hs.project.medicine.datas.Alarm;
import hs.project.medicine.datas.User;
import hs.project.medicine.util.LogUtil;
import hs.project.medicine.util.PreferenceUtil;

/**
 * 처음 앱 실행시 서비스 Start
 */
public class WeekCheckService extends Service {

    private ArrayList<Alarm> allAlarmList;
    private ArrayList<User> userArrayList;

    private ArrayList<Alarm> playAlarmList;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "medicine";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_LOW);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("알람")
                    .setContentText("알람기능 활성화")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();

            startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        playAlarmList = new ArrayList<>();
        allAlarmList = new ArrayList<>();

        /**
         * 유저리스트 가져오기
         * 유저리스트들의 알람리스트 가져오기
         */
        getUserList();

        for (int i = 0; i < userArrayList.size(); i++) {
            LogUtil.d(userArrayList.get(i).getName());
            getAlarmList(userArrayList.get(i));
            LogUtil.d("--------------------------------------------------------------------------------------------");
        }



        /**
         * 오늘 요일 가져온 후 Preference 에 저장된 알람리스트 가져와서 오늘 요일이 포함되어 있는 알람이 있는지 체크
         */

        String today = "";
        Calendar calendar = Calendar.getInstance();
        int week = calendar.get(Calendar.DAY_OF_WEEK);

        switch (week) {
            case Calendar.SUNDAY:
                today = "일";
                break;
            case Calendar.MONDAY:
                today = "월";
                break;
            case Calendar.TUESDAY:
                today = "화";
                break;
            case Calendar.WEDNESDAY:
                today = "수";
                break;
            case Calendar.THURSDAY:
                today = "목";
                break;
            case Calendar.FRIDAY:
                today = "금";
                break;
            case Calendar.SATURDAY:
                today = "토";
                break;
        }


        for (int i = 0; i < allAlarmList.size(); i++) {

            if (allAlarmList.get(i).getDayOfWeek().equals("매일")) {
                playAlarmList.add(allAlarmList.get(i));
            } else if (allAlarmList.get(i).getDayOfWeek().equals("주말")) {

                if (today.equals("토") || today.equals("일")) {
                    playAlarmList.add(allAlarmList.get(i));
                }

            } else {

                if (allAlarmList.get(i).getDayOfWeek().contains("일") && today.equals("일")) {
                    playAlarmList.add(allAlarmList.get(i));
                }
                if (allAlarmList.get(i).getDayOfWeek().contains("월") && today.equals("월")) {
                    playAlarmList.add(allAlarmList.get(i));
                }
                if (allAlarmList.get(i).getDayOfWeek().contains("화") && today.equals("화")) {
                    playAlarmList.add(allAlarmList.get(i));
                }
                if (allAlarmList.get(i).getDayOfWeek().contains("수") && today.equals("수")) {
                    playAlarmList.add(allAlarmList.get(i));
                }
                if (allAlarmList.get(i).getDayOfWeek().contains("목") && today.equals("목")) {
                    playAlarmList.add(allAlarmList.get(i));
                }
                if (allAlarmList.get(i).getDayOfWeek().contains("금") && today.equals("금")) {
                    playAlarmList.add(allAlarmList.get(i));
                }
                if (allAlarmList.get(i).getDayOfWeek().contains("토") && today.equals("토")) {
                    playAlarmList.add(allAlarmList.get(i));
                }

            }
        }

        for (int i =0; i < playAlarmList.size(); i++) {
            LogUtil.d(playAlarmList.get(i).getDayOfWeek());
        }


        return START_NOT_STICKY;
    }


    private void getUserList() {
        userArrayList = new ArrayList<>();

        if (PreferenceUtil.getJSONArrayPreference(MediApplication.ApplicationContext(), Config.PREFERENCE_KEY.USER_LIST) != null
                && PreferenceUtil.getJSONArrayPreference(MediApplication.ApplicationContext(), Config.PREFERENCE_KEY.USER_LIST).size() > 0) {

            JSONArray jsonArray = new JSONArray(PreferenceUtil.getJSONArrayPreference(MediApplication.ApplicationContext(), Config.PREFERENCE_KEY.USER_LIST));

            try {

                for (int i = 0; i < jsonArray.length(); i++) {
                    User user = new User();
                    JSONObject object = new JSONObject(jsonArray.getString(i));
                    user.setName(object.getString("name"));
                    user.setAge(object.getString("age"));
                    user.setRelation(object.getString("relation"));

                    userArrayList.add(user);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {

        }
    }

    private void getAlarmList(User user) {

        /* 해당 유저에 저장되어 있는 알람리스트 가져오기 */
        if (PreferenceUtil.getJSONArrayPreference(MediApplication.ApplicationContext(), user.alarmKey()) != null
                && PreferenceUtil.getJSONArrayPreference(MediApplication.ApplicationContext(), user.alarmKey()).size() > 0) {

            JSONArray jsonArray = new JSONArray(PreferenceUtil.getJSONArrayPreference(MediApplication.ApplicationContext(), user.alarmKey()));

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

                    LogUtil.d("alarm /" + alarm.getName());

                    allAlarmList.add(alarm);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {

        }
    }

    /* 앱이 종료되는 시점에 서비스도 중지 시킬때 사용 */
    /*@Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
    }*/

}