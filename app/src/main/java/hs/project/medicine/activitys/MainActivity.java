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
import hs.project.medicine.custom_view.MainHomeView;
import hs.project.medicine.custom_view.MainSearchView;
import hs.project.medicine.custom_view.MainTvView;
import hs.project.medicine.datas.User;
import hs.project.medicine.util.LogUtil;
import hs.project.medicine.util.PreferenceUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MainBottomView mainBottomView;

    private LinearLayout liMenu;
    private MainHomeView mainHomeView;
    private MainSearchView mainSearchView;
    private MainTvView mainTvView;

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
        mainHomeView = findViewById(R.id.main_content_home);
        mainSearchView = findViewById(R.id.main_content_search);
        mainTvView = findViewById(R.id.main_content_tv);

        liMenu.setOnClickListener(this);

        mainBottomView.setMainMenuEventListener(new MainBottomView.MainBottomListener() {
            @Override
            public void onHomeClick() {
                changeMainContent(Config.MAIN_BOTTOM_MENU.HOME);
            }

            @Override
            public void onSearchClick() {
                changeMainContent(Config.MAIN_BOTTOM_MENU.SEARCH);
            }

            @Override
            public void onTVClick() {
                changeMainContent(Config.MAIN_BOTTOM_MENU.TV);
            }
        });
    }

    private void changeMainContent(String selectedContent) {
        mainHomeView.setVisibility(View.GONE);
        mainSearchView.setVisibility(View.GONE);
        mainTvView.setVisibility(View.GONE);

        switch (selectedContent) {
            case Config.MAIN_BOTTOM_MENU.HOME:
                mainHomeView.setVisibility(View.VISIBLE);
                mainHomeView.setupUI();
                break;
            case Config.MAIN_BOTTOM_MENU.SEARCH:
                mainSearchView.setVisibility(View.VISIBLE);
                break;
            case Config.MAIN_BOTTOM_MENU.TV:
                mainTvView.setVisibility(View.VISIBLE);
                break;
        }
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