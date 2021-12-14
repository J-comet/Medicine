package hs.project.medicine.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import hs.project.medicine.R;
import hs.project.medicine.datas.Item;
import hs.project.medicine.datas.User;
import hs.project.medicine.util.LogUtil;

public class ModifyUserActivity extends AppCompatActivity implements View.OnClickListener {

    private User user;

    private EditText etName;
    private TextView tvGender;
    private TextView tvAge;
    private LinearLayout liBack;
    private LinearLayout liModifyComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user);

        init();

        user = (User) getIntent().getSerializableExtra("user");
        LogUtil.d("modify_user:" + user.getName());
    }

    private void init () {
        etName = findViewById(R.id.et_name);
        tvGender = findViewById(R.id.tv_gender);
        tvAge = findViewById(R.id.tv_age);
        liBack = findViewById(R.id.li_back);
        liModifyComplete = findViewById(R.id.li_complete);
    }

    @Override
    public void onClick(View v) {

    }
}