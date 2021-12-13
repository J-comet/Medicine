package hs.project.medicine.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hs.project.medicine.Config;
import hs.project.medicine.MediApplication;
import hs.project.medicine.R;
import hs.project.medicine.custom_view.MainBottomView;
import hs.project.medicine.datas.User;
import hs.project.medicine.util.LogUtil;
import hs.project.medicine.util.PreferenceUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MainBottomView mainBottomView;

    private LinearLayout liAddUser;
    private LinearLayout liMenu;
    private ConstraintLayout clCurrentUser;
    private ImageView ivGender;
    private TextView tvName;
    private TextView tvAge;

    private User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkCurrentUser();
        initCurUserData();
    }

    private void init() {
        mainBottomView = findViewById(R.id.main_bottom_view);
        liMenu = findViewById(R.id.li_menu);
        liAddUser = findViewById(R.id.li_add_user);
        clCurrentUser = findViewById(R.id.cl_current_user);
        ivGender = findViewById(R.id.iv_gender);
        tvName = findViewById(R.id.tv_name);
        tvAge = findViewById(R.id.tv_age);

        liMenu.setOnClickListener(this);
        liAddUser.setOnClickListener(this);
        clCurrentUser.setOnClickListener(this);
    }

    // 현재 선택된 유저가 있는지 체크
    private void checkCurrentUser() {

        liAddUser.setVisibility(View.VISIBLE);
        clCurrentUser.setVisibility(View.INVISIBLE);

        if (PreferenceUtil.getJSONArrayPreference(MainActivity.this, Config.PREFERENCE_KEY.USER_LIST) != null
                && PreferenceUtil.getJSONArrayPreference(MainActivity.this, Config.PREFERENCE_KEY.USER_LIST).size() > 0) {
//            userArrayList = PreferenceUtil.getJSONArrayPreference(UserListActivity.this, Config.PREFERENCE_KEY.USER_LIST);

            JSONArray jsonArray = new JSONArray(PreferenceUtil.getJSONArrayPreference(MainActivity.this, Config.PREFERENCE_KEY.USER_LIST));

            currentUser = new User();

            try {

                for (int i = 0; i < jsonArray.length(); i++) {
                    User user = new User();
                    JSONObject object = new JSONObject(jsonArray.getString(i));
                    user.setName(object.getString("name"));
                    user.setAge(object.getString("age"));
                    user.setGender(object.getString("gender"));
                    user.setCurrent(object.getBoolean("isCurrent"));

                    LogUtil.d("current_user /" + user.isCurrent());

                    if (user.isCurrent()) {
                        currentUser.setName(object.getString("name"));
                        currentUser.setAge(object.getString("age"));
                        currentUser.setGender(object.getString("gender"));
                        currentUser.setCurrent(object.getBoolean("isCurrent"));
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            liAddUser.setVisibility(View.INVISIBLE);
            clCurrentUser.setVisibility(View.VISIBLE);
        }
    }

    private void initCurUserData() {
        if (currentUser != null) {

            switch (currentUser.getGender()) {
                case "남자":
                    ivGender.setImageResource(R.drawable.male);
                    ivGender.setColorFilter(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.blue));
                    break;
                case "여자":
                    ivGender.setImageResource(R.drawable.female);
                    ivGender.setColorFilter(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.red));
                    break;
            }

            tvName.setText("이름  :  " + currentUser.getName());
            tvAge.setText("연령대  :  " + currentUser.getAge());
        }
    }

    @Override
    public void onClick(View v) {

        Intent intent;

        switch (v.getId()) {
            case R.id.li_menu:
                Toast.makeText(this, "메뉴클릭", Toast.LENGTH_SHORT).show();
                break;
            case R.id.li_add_user:
                intent = new Intent(MainActivity.this, AddUserActivity.class);
                startActivity(intent);
                break;
            case R.id.cl_current_user:
                intent = new Intent(MainActivity.this, UserListActivity.class);
                startActivity(intent);
                break;

           /* case R.id.cl_search_medic:
                Intent intentMoveSearchMedic = new Intent(MainActivity.this, SearchMedicineActivity.class);
                intentMoveSearchMedic.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentMoveSearchMedic);
                break;

            case R.id.tv_alert:
                Intent intentMoveSetting = new Intent(MainActivity.this, SettingAlarmActivity.class);
                intentMoveSetting.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentMoveSetting);
                break;*/
        }
    }

}