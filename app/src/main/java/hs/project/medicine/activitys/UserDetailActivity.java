package hs.project.medicine.activitys;

import android.content.DialogInterface;
import android.content.Intent;
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
import hs.project.medicine.adapter.AlarmAdapter;
import hs.project.medicine.databinding.ActivityUserDetailBinding;
import hs.project.medicine.datas.Alarm;
import hs.project.medicine.datas.User;
import hs.project.medicine.dialog.ModifyUserDialog;
import hs.project.medicine.util.LogUtil;
import hs.project.medicine.util.PreferenceUtil;

public class UserDetailActivity extends BaseActivity implements View.OnClickListener {

    private ActivityUserDetailBinding binding;
    private User user;
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
        user = (User) getIntent().getSerializableExtra("user");
        setData(user);

        binding.rvUserAlarm.setVisibility(View.VISIBLE);

        Alarm alarm = new Alarm();
        alarm.setName("이름");
        alarm.setAmPm("오전");
        alarm.setTime("12:00");
        alarm.setDayOfWeek("월,화,수,목,금,토,일");
        alarm.setAlarmON(true);
        alarm.setOk(true);

        alarmList = new ArrayList<>();
        alarmList.add(alarm);

        alarmAdapter = new AlarmAdapter(this);
        binding.rvUserAlarm.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        binding.rvUserAlarm.setAdapter(alarmAdapter);

        alarmAdapter.addAll(alarmList);
    }


    private void init() {
        binding.liBack.setOnClickListener(this);
        binding.tvProfileSetting.setOnClickListener(this);
        binding.tvDelete.setOnClickListener(this);
        binding.clAddAlarm.setOnClickListener(this);
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
                ModifyUserDialog dialog = new ModifyUserDialog(this, userItem);
                dialog.setModifyUserListener(new ModifyUserDialog.ModifyUserListener() {
                    @Override
                    public void onComplete(User user) {
                        setData(user);
                    }
                });
                dialog.show(getSupportFragmentManager(),"modifyUserDialog");
                /*getFragmentManager().executePendingTransactions();
                dialog.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                    }
                });*/
                break;
            case R.id.cl_add_alarm:
                Intent intent = new Intent(UserDetailActivity.this, AddAlarmActivity.class);
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