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
import hs.project.medicine.databinding.LayoutMainHomeViewBinding;
import hs.project.medicine.datas.User;
import hs.project.medicine.util.LogUtil;
import hs.project.medicine.util.PreferenceUtil;

public class MainHomeView extends ConstraintLayout implements View.OnClickListener {

    private LayoutMainHomeViewBinding binding;

    private User currentUser;
    private Context context;

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
        binding = LayoutMainHomeViewBinding.inflate(LayoutInflater.from(context), this, true);

        binding.liAddUser.setOnClickListener(this);
        binding.clCurrentUser.setOnClickListener(this);

        binding.cvNearbyMedicineStore.setOnClickListener(this);
        binding.cvAlarm.setOnClickListener(this);
        binding.cvMyInfo.setOnClickListener(this);
        binding.cvExample.setOnClickListener(this);

        setupUI();
    }

    public void setupUI() {
        checkCurrentUser();
        initCurUserData();
    }

    // 현재 선택된 유저가 있는지 체크
    private void checkCurrentUser() {

        binding.liAddUser.setVisibility(View.VISIBLE);
        binding.clCurrentUser.setVisibility(View.INVISIBLE);

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

            binding.liAddUser.setVisibility(View.INVISIBLE);
            binding.clCurrentUser.setVisibility(View.VISIBLE);
        }
    }

    private void initCurUserData() {
        if (currentUser != null) {

            switch (currentUser.getGender()) {
                case "남자":
                    binding.ivGender.setImageResource(R.drawable.male);
                    binding.ivGender.setColorFilter(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.blue));
                    break;
                case "여자":
                    binding.ivGender.setImageResource(R.drawable.female);
                    binding.ivGender.setColorFilter(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.red));
                    break;
            }

            binding.tvName.setText("이름  :  " + currentUser.getName());
            binding.tvAge.setText("연령대  :  " + currentUser.getAge());
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
