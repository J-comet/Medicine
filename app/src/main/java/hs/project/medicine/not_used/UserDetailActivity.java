package hs.project.medicine.not_used;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hs.project.medicine.Config;
import hs.project.medicine.MediApplication;
import hs.project.medicine.R;
import hs.project.medicine.activitys.AddAlarmActivity;
import hs.project.medicine.activitys.BaseActivity;
import hs.project.medicine.adapter.AlarmAdapter;
import hs.project.medicine.databinding.ActivityUserDetailBinding;
import hs.project.medicine.datas.Alarm;
import hs.project.medicine.datas.User;
import hs.project.medicine.dialog.ModifyAlarmDialog;
import hs.project.medicine.util.LogUtil;
import hs.project.medicine.util.PreferenceUtil;

public class UserDetailActivity extends BaseActivity implements View.OnClickListener {

    private ActivityUserDetailBinding binding;
//    private User user;
    private AlarmAdapter alarmAdapter;
    private ArrayList<Alarm> alarmList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        user = (User) getIntent().getSerializableExtra("user");
//        setData(user);
        getAlarmList();
    }

    private void init() {
        alarmAdapter = new AlarmAdapter(this);
        alarmAdapter.setOnEventListener(new AlarmAdapter.OnEventListener() {
            @Override
            public void onRemoveClick(View view, int position) {
                /* 1. 기존에 저장되어 있는 알람의 이름으로 비교 후 Preference 에서 삭제
                 *  2. RecyclerView 아이템 position 으로 삭제 */

                AlertDialog dialog = new AlertDialog.Builder(UserDetailActivity.this)
                        .setTitle("알람")
                        .setMessage("정말 삭제하시겠습니까?")
                        .setCancelable(false)
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                ArrayList<String> strAlarmList = new ArrayList<>();
                                alarmList.remove(position);

                                for (int i = 0; i < alarmList.size(); i++) {
                                    Alarm alarm = new Alarm();
                                    alarm.setName(alarmList.get(i).getName());
                                    alarm.setAmPm(alarmList.get(i).getAmPm());
                                    alarm.setHour(alarmList.get(i).getHour());
                                    alarm.setMinute(alarmList.get(i).getMinute());
                                    alarm.setVolume(alarmList.get(i).getVolume());
                                    alarm.setRingtoneName(alarmList.get(i).getRingtoneName());
                                    alarm.setRingtoneUri(alarmList.get(i).getRingtoneUri());
                                    alarm.setDayOfWeek(alarmList.get(i).getDayOfWeek());
                                    alarm.setAlarmON(alarmList.get(i).isAlarmON());

                                    strAlarmList.add(alarm.toJSON());
                                }

                                PreferenceUtil.setJSONArrayPreference(UserDetailActivity.this, Config.PREFERENCE_KEY.ALARM_LIST, strAlarmList);

                                alarmAdapter.removeAt(position);

                                if (alarmList.size() < 1) {
                                    binding.tvNone.setVisibility(View.VISIBLE);
                                    binding.rvUserAlarm.setVisibility(View.GONE);
                                } else {
                                    binding.tvNone.setVisibility(View.GONE);
                                    binding.rvUserAlarm.setVisibility(View.VISIBLE);
                                }

                                dialog.dismiss();
                            }
                        }).create();

                dialog.show();
                dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.black));
                dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.black));

            }

            @Override
            public void onModifyClick(View view, int position) {
                ModifyAlarmDialog modifyAlarmDialog = new ModifyAlarmDialog(UserDetailActivity.this, alarmList.get(position));
                modifyAlarmDialog.setModifyAlarmListener(new ModifyAlarmDialog.ModifyAlarmListener() {
                    @Override
                    public void onComplete() {
                        getAlarmList();
                    }
                });
                modifyAlarmDialog.show(getSupportFragmentManager(), "modifyAlarmDialog");
            }

            /*@Override
            public void onSwitchViewClick(boolean isChecked, int position) {

            }*/
        });

        binding.rvUserAlarm.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.rvUserAlarm.setAdapter(alarmAdapter);

        binding.liBack.setOnClickListener(this);
        binding.tvProfileSetting.setOnClickListener(this);
        binding.tvDelete.setOnClickListener(this);
        binding.clAddAlarm.setOnClickListener(this);
    }

    private void getAlarmList() {
        alarmList = new ArrayList<>();

        /* 해당 유저에 저장되어 있는 알람리스트 가져오기 */
        if (PreferenceUtil.getJSONArrayPreference(this, Config.PREFERENCE_KEY.ALARM_LIST) != null
                && PreferenceUtil.getJSONArrayPreference(this, Config.PREFERENCE_KEY.ALARM_LIST).size() > 0) {

            JSONArray jsonArray = new JSONArray(PreferenceUtil.getJSONArrayPreference(this, Config.PREFERENCE_KEY.ALARM_LIST));

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

                    alarmList.add(alarm);
                }

                alarmAdapter.addAll(alarmList);
                binding.rvUserAlarm.setVisibility(View.VISIBLE);
                binding.tvNone.setVisibility(View.GONE);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            binding.tvNone.setVisibility(View.VISIBLE);
            binding.rvUserAlarm.setVisibility(View.GONE);
        }
    }

    private void setData(User user) {
        if (user != null) {
            binding.tvName.setText(user.getName());
            binding.tvRelation.setText(user.getRelation());
            binding.tvAge.setText(user.getAge());
        }
    }

    private ArrayList<String> getRemovePreferenceList(User userItem) {

        ArrayList<String> strUserList = new ArrayList<>();

        if (PreferenceUtil.getJSONArrayPreference(this, Config.PREFERENCE_KEY.USER_LIST) != null
                && PreferenceUtil.getJSONArrayPreference(this, Config.PREFERENCE_KEY.USER_LIST).size() > 0) {

            JSONArray jsonArray = new JSONArray(PreferenceUtil.getJSONArrayPreference(this, Config.PREFERENCE_KEY.USER_LIST));

            try {

                for (int i = 0; i < jsonArray.length(); i++) {
                    User user = new User();
                    JSONObject object = new JSONObject(jsonArray.getString(i));

                    //  선택한 값 빼고 userArrayList 에 저장
                    if (!userItem.getName().equals(object.getString("name"))) {
                        user.setName(object.getString("name"));
                        user.setAge(object.getString("age"));
                        user.setRelation(object.getString("relation"));
//                        user.setGender(object.getString("gender"));
//                        user.setCurrent(object.getBoolean("isCurrent"));

//                        userArrayList.add(user);
                        strUserList.add(user.toJSON());
                        LogUtil.e("userArrayList[" + i + "]/ " + user.getName());
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return strUserList;
    }

    private void displayDeleteUserDialog(User userItem) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("경고")
                .setMessage("정말 삭제하시겠습니까?")
                .setCancelable(false)
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // preference 에서 데이터 삭제한 후 다시 저장
                        PreferenceUtil.setJSONArrayPreference(UserDetailActivity.this, Config.PREFERENCE_KEY.USER_LIST, getRemovePreferenceList(userItem));
                        Toast.makeText(UserDetailActivity.this, userItem.getName() + " 의 정보가 삭제되었습니다", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        finish();
                    }
                }).create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_a09d9d));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.black));
    }

    @Override
    public void onClick(View v) {

        User userItem = new User();
        userItem.setName(binding.tvName.getText().toString());
        userItem.setAge(binding.tvAge.getText().toString());
        userItem.setRelation(binding.tvRelation.getText().toString());

        switch (v.getId()) {
            case R.id.tv_profile_setting:
                ModifyUserDialog modifyUserDialog = new ModifyUserDialog(this, userItem);
                modifyUserDialog.setModifyUserListener(new ModifyUserDialog.ModifyUserListener() {
                    @Override
                    public void onComplete(User user) {
                        setData(user);
                    }
                });
                modifyUserDialog.show(getSupportFragmentManager(), "modifyUserDialog");
                /*getFragmentManager().executePendingTransactions();
                dialog.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                    }
                });*/
                break;
            case R.id.cl_add_alarm:
                Intent intent = new Intent(UserDetailActivity.this, AddAlarmActivity.class);
                intent.putExtra("user", userItem);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
            case R.id.tv_delete:
                displayDeleteUserDialog(userItem);
                break;
            case R.id.li_back:
                finish();
                break;
        }
    }
}