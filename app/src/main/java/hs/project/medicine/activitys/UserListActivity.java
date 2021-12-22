package hs.project.medicine.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hs.project.medicine.Config;
import hs.project.medicine.R;
import hs.project.medicine.adapter.UserListAdapter;
import hs.project.medicine.databinding.ActivityMainBinding;
import hs.project.medicine.databinding.ActivityUserListBinding;
import hs.project.medicine.datas.User;
import hs.project.medicine.util.LogUtil;
import hs.project.medicine.util.PreferenceUtil;

public class UserListActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityUserListBinding binding;

    private ArrayList<User> userArrayList;
    private ArrayList<User> newUserList;

    private ArrayList<String> strUserList;

    private UserListAdapter userListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();

        userArrayList = new ArrayList<>();

        if (PreferenceUtil.getJSONArrayPreference(UserListActivity.this, Config.PREFERENCE_KEY.USER_LIST) != null
                && PreferenceUtil.getJSONArrayPreference(UserListActivity.this, Config.PREFERENCE_KEY.USER_LIST).size() > 0) {

            JSONArray jsonArray = new JSONArray(PreferenceUtil.getJSONArrayPreference(UserListActivity.this, Config.PREFERENCE_KEY.USER_LIST));

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

                userListAdapter.addAll(userArrayList);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        userListAdapter.setOnMemberClickListener(new UserListAdapter.OnUserListClickListener() {
            @Override
            public void onUserClick(View v, int pos) {
                Toast.makeText(UserListActivity.this, userArrayList.get(pos).getName(), Toast.LENGTH_SHORT).show();
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
                PreferenceUtil.setJSONArrayPreference(UserListActivity.this, Config.PREFERENCE_KEY.USER_LIST, strUserList);
            }
        });
    }

    private void init() {
        binding.liBack.setOnClickListener(this);
        binding.liAdd.setOnClickListener(this);

        userListAdapter = new UserListAdapter(this);

        binding.rvUserList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.rvUserList.setAdapter(userListAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.li_back:
                finish();
                break;
            case R.id.li_add:
                Intent intent = new Intent(UserListActivity.this, AddUserActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
                break;
        }
    }
}