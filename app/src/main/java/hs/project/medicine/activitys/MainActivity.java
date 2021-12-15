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

    private LinearLayout liMenu;
//    private ConstraintLayout clCurrentUser;
//    private LinearLayout liAddUser;
//    private ImageView ivGender;
//    private TextView tvName;
//    private TextView tvAge;
//
//    private User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void init() {
        mainBottomView = findViewById(R.id.main_bottom_view);
        liMenu = findViewById(R.id.li_menu);
        liMenu.setOnClickListener(this);

        mainBottomView.setMainMenuEventListener(new MainBottomView.MainBottomListener() {
            @Override
            public void onHomeClick() {

            }

            @Override
            public void onSearchClick() {

            }

            @Override
            public void onTVClick() {

            }
        });
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.li_menu:
                Toast.makeText(this, "메뉴클릭", Toast.LENGTH_SHORT).show();
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