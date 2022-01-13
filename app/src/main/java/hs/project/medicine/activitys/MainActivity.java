package hs.project.medicine.activitys;


import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import android.widget.Toast;


import hs.project.medicine.Config;
import hs.project.medicine.MediApplication;
import hs.project.medicine.R;
import hs.project.medicine.custom_view.LeftSlideView;
import hs.project.medicine.custom_view.MainBottomView;
import hs.project.medicine.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity implements View.OnClickListener {

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

        /* 하단메뉴클릭 리스너 */
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
            public void onMapClick() {
                changeMainContent(Config.MAIN_BOTTOM_MENU.MAP);
            }

            @Override
            public void onTVClick() {
                changeMainContent(Config.MAIN_BOTTOM_MENU.TV);
            }
        });

        /* LeftSlide 메뉴클릭 리스너 */
        binding.leftSlideView.setLeftMenuEventListener(new LeftSlideView.LeftSlideListener() {
            @Override
            public void onHomeClick() {
                if (isDrawerOpen()) {
                    binding.dlSlide.closeDrawer(Gravity.LEFT);
                }
                changeMainContent(Config.MAIN_BOTTOM_MENU.HOME);
                binding.mainBottomView.menuStatus(Config.MAIN_BOTTOM_MENU.HOME);
            }

            @Override
            public void onSearchClick() {
                if (isDrawerOpen()) {
                    binding.dlSlide.closeDrawer(Gravity.LEFT);
                }
                changeMainContent(Config.MAIN_BOTTOM_MENU.SEARCH);
                binding.mainBottomView.menuStatus(Config.MAIN_BOTTOM_MENU.SEARCH);
            }

            @Override
            public void onTVClick() {
                if (isDrawerOpen()) {
                    binding.dlSlide.closeDrawer(Gravity.LEFT);
                }
                changeMainContent(Config.MAIN_BOTTOM_MENU.TV);
                binding.mainBottomView.menuStatus(Config.MAIN_BOTTOM_MENU.TV);
            }
        });

        /* LeftSlideView 나와있을 때 하위 뷰 터치 막는 코드 */
        binding.leftSlideView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    private void changeMainContent(String selectedContent) {
        binding.mainContentHome.setVisibility(View.GONE);
        binding.mainContentSearch.setVisibility(View.GONE);
        binding.mainContentTv.setVisibility(View.GONE);
        binding.mainContentMap.setVisibility(View.GONE);

        switch (selectedContent) {
            case Config.MAIN_BOTTOM_MENU.HOME:
                binding.mainContentHome.setVisibility(View.VISIBLE);
                binding.mainContentHome.setupUI();
                break;
            case Config.MAIN_BOTTOM_MENU.SEARCH:
                binding.mainContentSearch.setVisibility(View.VISIBLE);
                break;
            case Config.MAIN_BOTTOM_MENU.MAP:
                binding.mainContentMap.setVisibility(View.VISIBLE);
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
                binding.dlSlide.openDrawer(Gravity.LEFT);
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

    private boolean isDrawerOpen() {
        return binding.dlSlide.isDrawerOpen(Gravity.LEFT);
    }

    @Override
    public void onBackPressed() {
        if (isDrawerOpen()) {
            binding.dlSlide.closeDrawer(Gravity.LEFT);
            return;
        }

        super.onBackPressed();
    }

}