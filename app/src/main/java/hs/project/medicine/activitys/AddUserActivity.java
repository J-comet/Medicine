package hs.project.medicine.activitys;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.w3c.dom.Text;

import java.util.ArrayList;

import hs.project.medicine.Config;
import hs.project.medicine.MediApplication;
import hs.project.medicine.R;
import hs.project.medicine.datas.User;
import hs.project.medicine.util.LogUtil;
import hs.project.medicine.util.PreferenceUtil;

public class AddUserActivity extends AppCompatActivity implements View.OnClickListener {

//    private TextInputLayout tilName;

    private EditText etName;
    private TextView tvGender;
    private TextView tvAge;
    private LinearLayout liBack;
    private LinearLayout liComplete;

    private boolean isGender = false;
    private boolean isAge = false;

    private ArrayList<String> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        init();

    }

    private void init() {
        // userList 초기화
        // 다음 해야할 일
        // Preference 에 저장된 UserList 가 있다면 불러와서 저장시키는 코드 필요
        userList = new ArrayList<>();

        etName = findViewById(R.id.et_name);
        tvGender = findViewById(R.id.tv_gender);
        tvAge = findViewById(R.id.tv_age);
        liBack = findViewById(R.id.li_back);
        liComplete = findViewById(R.id.li_complete);

        tvGender.setOnClickListener(this);
        tvAge.setOnClickListener(this);
        liBack.setOnClickListener(this);
        liComplete.setOnClickListener(this);

//        tilName = findViewById(R.id.til_name);
//        tilName.setStartIconTintList(ContextCompat.getColorStateList(MediApplication.ApplicationContext(), R.color.selector_starticon));
    }

    private void complete() {
        /**
         * 1. 사용자 정보 JSON String 으로 저장 후 ArrayList<String> 에 저장
         * 2. 만약 Preference 에 저장된 User List 가 없다면 setCurrent = true 로 변경하는 코드 추가 필요
         */
        User user = new User();
        user.setName(etName.getText().toString());
        user.setGender(tvGender.getText().toString());
        user.setAge(tvAge.getText().toString());
        user.setCurrent(false);

        LogUtil.d("user /"+ user.getName());
        LogUtil.d("user /"+ user.getGender());
        LogUtil.d("user /"+ user.getAge());
        LogUtil.d("user /"+ user.isCurrent());


        /**
         * userList 에 user 를 JSON String 으로 변환 후 추가
         */
        LogUtil.d("user /"+ user.toJSON());
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
                tvGender.setText(genderList[which]);
                tvGender.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.black));
                isGender = true;
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
                        tvAge.setText(ageList[which]);
                        tvAge.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.black));
                        isAge = true;
                        dialog.dismiss();
                    }
                }).show();
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
            case R.id.li_complete:

                /**
                 * 모든 정보 입력 완료했을 때 실행할 수 있도록
                 */
                if (etName.getText().toString().length() > 0 && isGender && isAge) {
                    complete();
                } else {
                    Toast.makeText(this, "모든 정보를 입력해주세요", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
}