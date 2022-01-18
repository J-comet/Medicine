package hs.project.medicine.main_content;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hs.project.medicine.Config;
import hs.project.medicine.R;
import hs.project.medicine.adapter.GridUserAdapter;
import hs.project.medicine.databinding.LayoutMainHomeViewBinding;
import hs.project.medicine.datas.User;
import hs.project.medicine.util.LogUtil;
import hs.project.medicine.util.PreferenceUtil;

public class MainHomeView extends ConstraintLayout implements View.OnClickListener {

    private LayoutMainHomeViewBinding binding;
    private Context context;
    private GridUserAdapter userAdapter;
    private ArrayList<User> userArrayList;

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
        setupUI();
    }

    public void setupUI() {
//        binding.tvUserList.setOnClickListener(this);
        setGridRecyclerView();
        checkRegisterUser();
    }

    private void setGridRecyclerView() {
        userAdapter = new GridUserAdapter(context);
        binding.rvUser.setLayoutManager(new GridLayoutManager(context, 2));
        binding.rvUser.setAdapter(userAdapter);
    }

    // 현재 등록된 유저가 있는지 체크
    private void checkRegisterUser() {

        userArrayList = new ArrayList<>();

        if (PreferenceUtil.getJSONArrayPreference(context, Config.PREFERENCE_KEY.USER_LIST) != null
                && PreferenceUtil.getJSONArrayPreference(context, Config.PREFERENCE_KEY.USER_LIST).size() > 0) {

            JSONArray jsonArray = new JSONArray(PreferenceUtil.getJSONArrayPreference(context, Config.PREFERENCE_KEY.USER_LIST));

            try {

                /* 마지막에 유저등록할 뷰 추가하기 위해 +1 */
                for (int i = 0; i < jsonArray.length() + 1; i++) {

                    User user = new User();

                    /* 가짜데이터 추가 */
                    if (i == jsonArray.length()) {
                        user.setName("등록하기");
                        user.setAge("null");
//                        user.setGender("null");
                        user.setRelation("null");
//                        user.setCurrent(false);
                    } else {
                        JSONObject object = new JSONObject(jsonArray.getString(i));
                        user.setName(object.getString("name"));
                        user.setAge(object.getString("age"));
//                        user.setGender(object.getString("gender"));
                        user.setRelation(object.getString("relation"));
//                        user.setCurrent(object.getBoolean("isCurrent"));

                        LogUtil.d("user /" + user.getName());
//                        LogUtil.d("user /" + user.getGender());
                        LogUtil.d("user /" + user.getAge());
                        LogUtil.d("user /" + user.getRelation());

//                        LogUtil.d("user /" + user.isCurrent());
                    }

                    userArrayList.add(user);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            /* 등록된 유저 없을 때 가짜 데이터 */
            User user = new User();
            user.setName("등록하기");
            user.setAge("null");
//            user.setGender("null");
            user.setRelation("null");
//            user.setCurrent(false);

            userArrayList.add(user);
        }

        userAdapter.addAll(userArrayList);
    }

    @Override
    public void onClick(View v) {

        Intent intent;

        switch (v.getId()) {
//            case R.id.tv_user_list:
//                intent = new Intent(context, UserListActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                context.startActivity(intent);
//                break;
        }
    }
}

