package hs.project.medicine.not_used;

import android.content.Context;
import android.content.Intent;
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
import hs.project.medicine.not_used.AddUserActivity;
import hs.project.medicine.activitys.UserDetailActivity;
import hs.project.medicine.adapter.UserListAdapter;
import hs.project.medicine.databinding.LayoutMainUserListViewBinding;
import hs.project.medicine.datas.User;
import hs.project.medicine.util.LogUtil;
import hs.project.medicine.util.PreferenceUtil;

public class MainUserListView extends ConstraintLayout implements View.OnClickListener {
    private LayoutMainUserListViewBinding binding;
    private Context context;

    private ArrayList<User> userArrayList;
    private UserListAdapter userListAdapter;

    public MainUserListView(@NonNull Context context) {
        super(context);
        this.context = context;
        initView(context);
    }

    public MainUserListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
    }

    public MainUserListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView(context);
    }

    public MainUserListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initView(Context context) {
        binding = LayoutMainUserListViewBinding.inflate(LayoutInflater.from(context), this, true);

        userListAdapter = new UserListAdapter(context);

        binding.rvUserList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        binding.rvUserList.setAdapter(userListAdapter);

        binding.clNone.setOnClickListener(this);
        binding.liAddUser.setOnClickListener(this);
    }

    public void setUpUI() {
        binding.clUserList.setVisibility(View.INVISIBLE);
        binding.clNone.setVisibility(View.INVISIBLE);

        userArrayList = new ArrayList<>();

        if (PreferenceUtil.getJSONArrayPreference(context, Config.PREFERENCE_KEY.USER_LIST) != null
                && PreferenceUtil.getJSONArrayPreference(context, Config.PREFERENCE_KEY.USER_LIST).size() > 0) {

            JSONArray jsonArray = new JSONArray(PreferenceUtil.getJSONArrayPreference(context, Config.PREFERENCE_KEY.USER_LIST));

            try {

                for (int i = 0; i < jsonArray.length(); i++) {
                    User user = new User();
                    JSONObject object = new JSONObject(jsonArray.getString(i));
                    user.setName(object.getString("name"));
                    user.setAge(object.getString("age"));
//                    user.setGender(object.getString("gender"));
                    user.setRelation(object.getString("relation"));
//                    user.setCurrent(object.getBoolean("isCurrent"));

                    LogUtil.d("user /" + user.getName());
//                    LogUtil.d("user /" + user.getGender());
                    LogUtil.d("user /" + user.getAge());
                    LogUtil.d("user /" + user.getRelation());
//                    LogUtil.d("user /" + user.isCurrent());

                    userArrayList.add(user);
                }

                userListAdapter.addAll(userArrayList);
                binding.clUserList.setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            binding.clNone.setVisibility(View.VISIBLE);
        }

        userListAdapter.setOnMemberClickListener(new UserListAdapter.OnUserListClickListener() {
            @Override
            public void onUserClick(View v, int pos) {
                /*Toast.makeText(UserListActivity.this, userArrayList.get(pos).getName(), Toast.LENGTH_SHORT).show();
                LogUtil.d("selected user :" + userArrayList.get(pos).getName());

                strUserList = new ArrayList<>();
                newUserList = new ArrayList<>();

                for (int i = 0; i < userArrayList.size(); i++) {
                    User user = new User();
                    user.setName(userArrayList.get(i).getName());
                    user.setAge(userArrayList.get(i).getAge());
                    user.setGender(userArrayList.get(i).getGender());
                    user.setCurrent(false);

                    // 현재 선택한 위치의 값만 true 로 설정
                    if (i == pos) {
                        user.setCurrent(true);
                    }
                    newUserList.add(user);  // adapter 와 연결시킬 ArrayList
                    strUserList.add(user.toJSON());  // Preference 에 저장시킬 ArrayList
                }

                userListAdapter.addAll(newUserList);
                PreferenceUtil.setJSONArrayPreference(UserListActivity.this, Config.PREFERENCE_KEY.USER_LIST, strUserList);*/
                Intent intent = new Intent(context, UserDetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cl_none:
            case R.id.li_add_user:
                Intent intent = new Intent(context, AddUserActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(intent);
                break;
        }
    }
}
