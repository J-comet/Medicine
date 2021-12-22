package hs.project.medicine.activitys;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hs.project.medicine.Config;
import hs.project.medicine.MediApplication;
import hs.project.medicine.R;
import hs.project.medicine.databinding.ActivityModifyUserBinding;
import hs.project.medicine.datas.User;
import hs.project.medicine.util.LogUtil;
import hs.project.medicine.util.PreferenceUtil;

public class ModifyUserActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityModifyUserBinding binding;

    private User user;

//    private EditText etName;
//    private TextView tvGender;
//    private TextView tvAge;
//    private LinearLayout liBack;
//    private LinearLayout liModifyComplete;

    private ArrayList<String> strUserList;

    private ArrayList<User> userArrayList; // Preference 에 저장할 유저 정보

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModifyUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setData();

    }

    private void init() {

        binding.tvGender.setOnClickListener(this);
        binding.tvAge.setOnClickListener(this);
        binding.liBack.setOnClickListener(this);
        binding.liModifyComplete.setOnClickListener(this);

        userArrayList = new ArrayList<>();

        if (PreferenceUtil.getJSONArrayPreference(ModifyUserActivity.this, Config.PREFERENCE_KEY.USER_LIST) != null
                && PreferenceUtil.getJSONArrayPreference(ModifyUserActivity.this, Config.PREFERENCE_KEY.USER_LIST).size() > 0) {

            JSONArray jsonArray = new JSONArray(PreferenceUtil.getJSONArrayPreference(ModifyUserActivity.this, Config.PREFERENCE_KEY.USER_LIST));

            try {

                for (int i = 0; i < jsonArray.length(); i++) {
                    User user = new User();
                    JSONObject object = new JSONObject(jsonArray.getString(i));
                    user.setName(object.getString("name"));
                    user.setAge(object.getString("age"));
                    user.setGender(object.getString("gender"));
                    user.setCurrent(object.getBoolean("isCurrent"));

                    LogUtil.d("user /" + user.getName());
                    LogUtil.d("user /" + user.getGender());
                    LogUtil.d("user /" + user.getAge());
                    LogUtil.d("user /" + user.isCurrent());

                    userArrayList.add(user);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setData() {
        user = (User) getIntent().getSerializableExtra("user");

        if (user != null) {
            binding.etName.setText(user.getName());
            binding.tvGender.setText(user.getGender());
            binding.tvGender.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.black));
            binding.tvAge.setText(user.getAge());
            binding.tvAge.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.black));
        }

    }

    private void displayGenderDialog() {

        String[] genderList = getResources().getStringArray(R.array.arr_gender);

        new AlertDialog.Builder(this)
                .setTitle("성별 선택")
                .setItems(genderList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        binding.tvGender.setText(genderList[which]);
                        binding.tvGender.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.black));
                        dialog.dismiss();
                    }
                }).show();
    }

    private void displayAgeDialog() {

        String[] ageList = getResources().getStringArray(R.array.arr_age);

        new AlertDialog.Builder(this)
                .setTitle("연령대 선택")
                .setItems(ageList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        binding.tvAge.setText(ageList[which]);
                        binding.tvAge.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.black));
                        dialog.dismiss();
                    }
                }).show();
    }

    private void modifyComplete() {

        strUserList = new ArrayList<>();

        /**
         * 1. Preference 에 저장된 UserList 의 User 를 업데이트
         */
        for (int i = 0; i < userArrayList.size(); i++) {
            User addUser = new User();
            addUser.setName(userArrayList.get(i).getName());
            addUser.setGender(userArrayList.get(i).getGender());
            addUser.setAge(userArrayList.get(i).getAge());
            addUser.setCurrent(userArrayList.get(i).isCurrent());

            if (user.getName().equals(userArrayList.get(i).getName())) {
                addUser.setName(binding.etName.getText().toString());
                addUser.setGender(binding.tvGender.getText().toString());
                addUser.setAge(binding.tvAge.getText().toString());
                addUser.setCurrent(userArrayList.get(i).isCurrent());
            }

            strUserList.add(addUser.toJSON());
        }

        // Preference 에 저장
        PreferenceUtil.setJSONArrayPreference(this, Config.PREFERENCE_KEY.USER_LIST, strUserList);

        Toast.makeText(this, "수정완료", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_gender:
                displayGenderDialog();
                break;
            case R.id.tv_age:
                displayAgeDialog();
                break;
            case R.id.li_back:
                finish();
                break;
            case R.id.li_modify_complete:

                /**
                 * 모든 정보 입력 완료했을 때 실행할 수 있도록
                 */
                if (binding.etName.getText().toString().length() > 0) {
                    modifyComplete();
                    finish();
                } else {
                    Toast.makeText(this, "모든 정보를 입력해주세요", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
}