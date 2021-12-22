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
import hs.project.medicine.databinding.ActivityMainBinding;
import hs.project.medicine.datas.User;
import hs.project.medicine.util.LogUtil;
import hs.project.medicine.util.PreferenceUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();

        /**
         *  UserListActivity 에서 현재 유저 변경 후 값 가져오기 위해 onStart 에서 실행
         */
        binding.mainContentHome.setupUI();
    }

    private void init() {
        binding.liMenu.setOnClickListener(this);

        binding.mainBottomView.setMainMenuEventListener(new MainBottomView.MainBottomListener() {
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
        binding.mainContentHome.setVisibility(View.GONE);
        binding.mainContentSearch.setVisibility(View.GONE);
        binding.mainContentTv.setVisibility(View.GONE);

        switch (selectedContent) {
            case Config.MAIN_BOTTOM_MENU.HOME:
                binding.mainContentHome.setVisibility(View.VISIBLE);
                binding.mainContentHome.setupUI();
                break;
            case Config.MAIN_BOTTOM_MENU.SEARCH:
                binding.mainContentSearch.setVisibility(View.VISIBLE);
                break;
            case Config.MAIN_BOTTOM_MENU.TV:
                binding.mainContentTv.setVisibility(View.VISIBLE);
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