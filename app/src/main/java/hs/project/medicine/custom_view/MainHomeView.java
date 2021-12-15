package hs.project.medicine.custom_view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import hs.project.medicine.Config;
import hs.project.medicine.MediApplication;
import hs.project.medicine.R;
import hs.project.medicine.activitys.AddUserActivity;
import hs.project.medicine.activitys.MainActivity;
import hs.project.medicine.activitys.UserListActivity;
import hs.project.medicine.datas.User;
import hs.project.medicine.util.LogUtil;
import hs.project.medicine.util.PreferenceUtil;

public class MainHomeView extends ConstraintLayout implements View.OnClickListener {

    private ConstraintLayout clCurrentUser;
    private LinearLayout liAddUser;
    private ImageView ivGender;
    private TextView tvName;
    private TextView tvAge;

    private User currentUser;

    private Context context;

    private CardView cvNearbyMedicineStore;
    private CardView cvAlarm;
    private CardView cvMyInfo;
    private CardView cvTest;

    public MainHomeView(@NonNull Context context) {
        super(context);
        this.context = context;
        initView(context);
    }

    public MainHomeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
    }

    public MainHomeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView(context);
    }

    public MainHomeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.layout_main_home_view, this, false);
        addView(v);

        liAddUser = findViewById(R.id.li_add_user);
        clCurrentUser = findViewById(R.id.cl_current_user);
        ivGender = findViewById(R.id.iv_gender);
        tvName = findViewById(R.id.tv_name);
        tvAge = findViewById(R.id.tv_age);

        cvNearbyMedicineStore = findViewById(R.id.cv_nearby_medicine_store);
        cvAlarm = findViewById(R.id.cv_alarm);
        cvMyInfo = findViewById(R.id.cv_my_info);
        cvTest = findViewById(R.id.cv_example);

        liAddUser.setOnClickListener(this);
        clCurrentUser.setOnClickListener(this);

        cvNearbyMedicineStore.setOnClickListener(this);
        cvAlarm.setOnClickListener(this);
        cvMyInfo.setOnClickListener(this);
        cvTest.setOnClickListener(this);

        setupUI();
    }

    public void setupUI() {
        checkCurrentUser();
        initCurUserData();
    }

    // 현재 선택된 유저가 있는지 체크
    private void checkCurrentUser() {

        liAddUser.setVisibility(View.VISIBLE);
        clCurrentUser.setVisibility(View.INVISIBLE);

        if (PreferenceUtil.getJSONArrayPreference(context, Config.PREFERENCE_KEY.USER_LIST) != null
                && PreferenceUtil.getJSONArrayPreference(context, Config.PREFERENCE_KEY.USER_LIST).size() > 0) {

            JSONArray jsonArray = new JSONArray(PreferenceUtil.getJSONArrayPreference(context, Config.PREFERENCE_KEY.USER_LIST));

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

        switch (v.getId()){
            case R.id.li_add_user:
                intent = new Intent(context, AddUserActivity.class);
                context.startActivity(intent);
                break;
            case R.id.cl_current_user:
                intent = new Intent(context, UserListActivity.class);
                context.startActivity(intent);
                break;
            case R.id.cv_nearby_medicine_store:
                Toast.makeText(context, "주변약국" , Toast.LENGTH_SHORT).show();
                break;
            case R.id.cv_alarm:
                Toast.makeText(context, "알람설정" , Toast.LENGTH_SHORT).show();
                break;
            case R.id.cv_my_info:
                Toast.makeText(context, "내 정보" , Toast.LENGTH_SHORT).show();
                break;
            case R.id.cv_example:
                Toast.makeText(context, "테스트" , Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
