package hs.project.medicine.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hs.project.medicine.Config;
import hs.project.medicine.R;
import hs.project.medicine.adapter.UserListAdapter;
import hs.project.medicine.datas.User;
import hs.project.medicine.util.LogUtil;
import hs.project.medicine.util.PreferenceUtil;

public class UserListActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout liBack;
    private RecyclerView rvUserList;
    private ArrayList<User> userArrayList;
    private UserListAdapter userListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        init();
    }

    private void init() {
        liBack = findViewById(R.id.li_back);
        liBack.setOnClickListener(this);

        rvUserList = findViewById(R.id.rv_user_list);

        userArrayList = new ArrayList<>();
        userListAdapter = new UserListAdapter(this);
        rvUserList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvUserList.setAdapter(userListAdapter);

        if (PreferenceUtil.getJSONArrayPreference(UserListActivity.this, Config.PREFERENCE_KEY.USER_LIST) != null) {
//            userArrayList = PreferenceUtil.getJSONArrayPreference(UserListActivity.this, Config.PREFERENCE_KEY.USER_LIST);

            JSONArray jsonArray = new JSONArray(PreferenceUtil.getJSONArrayPreference(UserListActivity.this, Config.PREFERENCE_KEY.USER_LIST));

            try {

                for (int i = 0; i < jsonArray.length(); i++) {
                    User user = new User();
                    JSONObject object = new JSONObject(jsonArray.getString(i));
                    user.setName(object.getString("name"));
                    user.setAge(object.getString("age"));
                    user.setGender(object.getString("gender"));
                    user.setCurrent(object.getBoolean("isCurrent"));

                    LogUtil.d("user /"+ user.getName());
                    LogUtil.d("user /"+ user.getGender());
                    LogUtil.d("user /"+ user.getAge());
                    LogUtil.d("user /"+ user.isCurrent());

                    userArrayList.add(user);
                }

                userListAdapter.addAll(userArrayList);

            } catch (JSONException e) {
                e.printStackTrace();
            }


//            userListAdapter.addAll(userArrayList);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.li_back:
                finish();
                break;
        }
    }
}