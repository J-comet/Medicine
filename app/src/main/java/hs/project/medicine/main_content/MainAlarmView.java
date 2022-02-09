package hs.project.medicine.main_content;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hs.project.medicine.Config;
import hs.project.medicine.R;
import hs.project.medicine.activitys.AddAlarmActivity;
import hs.project.medicine.adapter.AlarmAdapter;
import hs.project.medicine.databinding.LayoutMainAlarmViewBinding;
import hs.project.medicine.activitys.UserDetailActivity;
import hs.project.medicine.adapter.UserListAdapter;
import hs.project.medicine.datas.Alarm;
import hs.project.medicine.datas.User;
import hs.project.medicine.util.LogUtil;
import hs.project.medicine.util.PreferenceUtil;

public class MainAlarmView extends ConstraintLayout implements View.OnClickListener {
    private LayoutMainAlarmViewBinding binding;
    private Context context;

    private ArrayList<Alarm> alarmArrayList;
    private AlarmAdapter alarmAdapter;

    public MainAlarmView(@NonNull Context context) {
        super(context);
        this.context = context;
        initView(context);
    }

    public MainAlarmView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
    }

    public MainAlarmView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView(context);
    }

    public MainAlarmView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initView(Context context) {
        binding = LayoutMainAlarmViewBinding.inflate(LayoutInflater.from(context), this, true);

        alarmAdapter = new AlarmAdapter(context);

        binding.rvAlarmList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        binding.rvAlarmList.setAdapter(alarmAdapter);

        binding.clNone.setOnClickListener(this);
        binding.liAddAlarm.setOnClickListener(this);
    }

    public void setUpUI() {
        binding.clAlarmList.setVisibility(View.INVISIBLE);
        binding.clNone.setVisibility(View.INVISIBLE);

        getAlarmList();

    }

    private void getAlarmList() {
        alarmArrayList = new ArrayList<>();

        /* 저장되어 있는 알람리스트 가져오기 */
        if (PreferenceUtil.getJSONArrayPreference(context, "alarmKey") != null
                && PreferenceUtil.getJSONArrayPreference(context, "alarmKey").size() > 0) {

            JSONArray jsonArray = new JSONArray(PreferenceUtil.getJSONArrayPreference(context, "alarmKey"));

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

                    alarmArrayList.add(alarm);
                }

                alarmAdapter.addAll(alarmArrayList);
                binding.clAlarmList.setVisibility(View.VISIBLE);
                binding.clNone.setVisibility(View.GONE);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            binding.clNone.setVisibility(View.VISIBLE);
            binding.clAlarmList.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cl_none:
            case R.id.li_add_alarm:
                Intent intent = new Intent(context, AddAlarmActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(intent);
                break;
        }
    }
}
