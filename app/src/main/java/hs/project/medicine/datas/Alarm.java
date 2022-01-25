package hs.project.medicine.datas;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

public class Alarm {
    String name;    // 두통
    String amPm;    // 오전 or 오후
    String dayOfWeek;   // 요일
    String time;    // 6:00
    Uri ringtoneUri;  // Uri.parse("content://media/internal/audio/media/37")
    boolean alarmON;    // 스위치 ON or OFF
//    boolean isOk;   // 오늘 약 먹었는지 체크

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmPm() {
        return amPm;
    }

    public void setAmPm(String amPm) {
        this.amPm = amPm;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isAlarmON() {
        return alarmON;
    }

    public void setAlarmON(boolean alarmON) {
        this.alarmON = alarmON;
    }

    public Uri getRingtoneUri() {
        return ringtoneUri;
    }

    public void setRingtoneUri(Uri ringtoneUri) {
        this.ringtoneUri = ringtoneUri;
    }

//    public boolean isOk() {
//        return isOk;
//    }
//
//    public void setOk(boolean ok) {
//        isOk = ok;
//    }


    public String toJSON() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", getName());
            jsonObject.put("amPm", getAmPm());
            jsonObject.put("dayOfWeek", getDayOfWeek());
            jsonObject.put("time", getTime());
            jsonObject.put("ringtoneUri", getRingtoneUri());
            jsonObject.put("alarmON", isAlarmON());
            return jsonObject.toString();

        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}
