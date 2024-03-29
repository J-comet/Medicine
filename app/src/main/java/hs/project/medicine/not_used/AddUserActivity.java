package hs.project.medicine.not_used;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import hs.project.medicine.Config;
import hs.project.medicine.MediApplication;
import hs.project.medicine.R;
import hs.project.medicine.activitys.BaseActivity;
import hs.project.medicine.activitys.MainActivity;
import hs.project.medicine.databinding.ActivityAddUserBinding;
import hs.project.medicine.datas.User;
import hs.project.medicine.util.LogUtil;
import hs.project.medicine.util.PreferenceUtil;

public class AddUserActivity extends BaseActivity implements View.OnClickListener {

    private ActivityAddUserBinding binding;

    //    private boolean isGender = false;
    private boolean isAge = false;
    private boolean isRelation = false;
    private boolean isDirect = false;

    private ArrayList<String> userList;

    private boolean isFirst = false;  // 처음 등록하는 유저인지 확인용 플래그

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

    }

    private void init() {
        // userList 초기화
        // Preference 에 저장된 UserList 가 있다면 불러옴

        if (PreferenceUtil.getJSONArrayPreference(AddUserActivity.this, Config.PREFERENCE_KEY.USER_LIST) != null
                && PreferenceUtil.getJSONArrayPreference(AddUserActivity.this, Config.PREFERENCE_KEY.USER_LIST).size() > 0) {
            userList = PreferenceUtil.getJSONArrayPreference(AddUserActivity.this, Config.PREFERENCE_KEY.USER_LIST);
        } else {
            userList = new ArrayList<>();
            isFirst = true;
        }

        LogUtil.d("userList.size()=" + userList.size());

//        binding.tvGender.setOnClickListener(this);
        binding.tvAge.setOnClickListener(this);
        binding.tvRelation.setOnClickListener(this);
        binding.liBack.setOnClickListener(this);
        binding.liComplete.setOnClickListener(this);

//        tilName = findViewById(R.id.til_name);
//        tilName.setStartIconTintList(ContextCompat.getColorStateList(MediApplication.ApplicationContext(), R.color.selector_starticon));
    }

    private void complete() {
        /**
         * 1. 사용자 정보 JSON String 으로 저장 후 ArrayList<String> 에 저장
         * 2. 만약 Preference 에 저장된 User List 가 없다면 setCurrent = true 로 변경하는 코드 추가 필요
         */
        User user = new User();
        user.setName(binding.etName.getText().toString());
//        user.setGender(binding.tvGender.getText().toString());
        user.setAge(binding.tvAge.getText().toString());

        user.setRelation(binding.tvRelation.getText().toString());

        /* 관계 직접입력일 때 */
        if (isDirect) {
            user.setRelation(binding.etRelation.getText().toString());
        }


        LogUtil.d("user /" + user.getName());
//        LogUtil.d("user /" + user.getGender());
        LogUtil.d("user /" + user.getAge());
        LogUtil.d("user /" + user.getRelation());


        /**
         * userList 에 user 를 JSON String 으로 변환 후 추가
         */
        LogUtil.d("user /" + user.toJSON());
        userList.add(user.toJSON());

        // Preference 에 저장
        PreferenceUtil.setJSONArrayPreference(this, Config.PREFERENCE_KEY.USER_LIST, userList);


//        PreferenceUtil.putSharedPreference();

        Toast.makeText(this, "등록완료", Toast.LENGTH_SHORT).show();
    }

    private void displayGenderDialog() {

        String[] genderList = getResources().getStringArray(R.array.arr_gender);

        new AlertDialog.Builder(this)
                .setTitle("성별 선택")
                .setItems(genderList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        binding.tvGender.setText(genderList[which]);
//                        binding.tvGender.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.black));
//                        isGender = true;
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
                        isAge = true;
                        dialog.dismiss();
                    }
                }).show();
    }

    private void displayRelationDialog() {

        String[] arrList = getResources().getStringArray(R.array.arr_relation);

        new AlertDialog.Builder(this)
                .setTitle("관계 선택")
                .setItems(arrList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (arrList[which].equals("직접입력")) {
                            binding.groupRelationDirect.setVisibility(View.VISIBLE);
                            binding.tvRelation.setText(arrList[which]);
                            binding.tvRelation.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.black));
                            isDirect = true;
                        } else {
                            binding.groupRelationDirect.setVisibility(View.GONE);
                            binding.tvRelation.setText(arrList[which]);
                            binding.tvRelation.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.black));
                            isDirect = false;
                        }

                        isRelation = true;
                        dialog.dismiss();
                    }
                }).show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.tv_gender:
//                displayGenderDialog();
//                break;
            case R.id.tv_age:
                displayAgeDialog();
                break;
            case R.id.tv_relation:
                displayRelationDialog();
                break;
            case R.id.li_back:
                finish();
                break;
            case R.id.li_complete:

                /**
                 * 모든 정보 입력 완료했을 때 실행할 수 있도록
                 */
                if (binding.etName.getText().toString().length() > 0 && isAge && isRelation) {
                    complete();
                    Intent intent = new Intent(AddUserActivity.this, MainActivity.class);
                    intent.putExtra("MAIN_BOTTOM_MENU", Config.MAIN_BOTTOM_MENU.USER_LIST);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "모든 정보를 입력해주세요", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
}