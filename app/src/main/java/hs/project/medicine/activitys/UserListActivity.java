package hs.project.medicine.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.LinearLayout;

import hs.project.medicine.R;

public class UserListActivity extends AppCompatActivity {

    private LinearLayout liBack;
    private RecyclerView rvUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        init();
    }

    private void init() {
        liBack = findViewById(R.id.li_back);
        rvUserList = findViewById(R.id.rv_user_list);
    }
}