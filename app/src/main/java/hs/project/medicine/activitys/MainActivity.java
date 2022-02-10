package hs.project.medicine.activitys;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;


import hs.project.medicine.Config;
import hs.project.medicine.MediApplication;
import hs.project.medicine.R;
import hs.project.medicine.main_content.LeftSlideView;
import hs.project.medicine.main_content.MainBottomView;
import hs.project.medicine.main_content.MapFragment;
import hs.project.medicine.databinding.ActivityMainBinding;
import hs.project.medicine.service.DayOfWeekCheckService;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private ActivityMainBinding binding;

    public static Context mainActivityContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mainActivityContext = this;

        init();

        startDayOfWeekService();
//        Intent intent = new Intent(MediApplication.ApplicationContext(), .class);
//        intent.putExtra("command", "show");
//        intent.putExtra("text", text);
//        startService(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        /**
         *  UserListActivity 에서 현재 유저 변경 후 값 가져오기 위해 onStart 에서 실행
         */
        binding.mainContentAlarm.setUpUI();
        binding.mainContentUserList.setUpUI();

        if (getIntent().getStringExtra("MAIN_BOTTOM_MENU") != null
                && getIntent().getStringExtra("MAIN_BOTTOM_MENU").equals(Config.MAIN_BOTTOM_MENU.USER_LIST)) {
            changeMainContent(Config.MAIN_BOTTOM_MENU.USER_LIST);
        }
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

//            @Override
//            public void onUserListClick() {
//                changeMainContent(Config.MAIN_BOTTOM_MENU.USER_LIST);
//            }
        });

        /* LeftSlide 메뉴클릭 리스너 */
        binding.leftSlideView.setLeftMenuEventListener(new LeftSlideView.LeftSlideListener() {
            @Override
            public void onHomeClick() {
                if (isDrawerOpen()) {
                    binding.dlSlide.closeDrawer(Gravity.LEFT);
                }
                changeMainContent(Config.MAIN_BOTTOM_MENU.HOME);
            }

            @Override
            public void onSearchClick() {
                if (isDrawerOpen()) {
                    binding.dlSlide.closeDrawer(Gravity.LEFT);
                }
                changeMainContent(Config.MAIN_BOTTOM_MENU.SEARCH);
            }

            @Override
            public void onMapClick() {
                if (isDrawerOpen()) {
                    binding.dlSlide.closeDrawer(Gravity.LEFT);
                }
                changeMainContent(Config.MAIN_BOTTOM_MENU.MAP);
            }

            @Override
            public void onUserListClick() {
                if (isDrawerOpen()) {
                    binding.dlSlide.closeDrawer(Gravity.LEFT);
                }
                changeMainContent(Config.MAIN_BOTTOM_MENU.USER_LIST);
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

    public void startDayOfWeekService() {
        // start service
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(new Intent(MediApplication.ApplicationContext(), DayOfWeekCheckService.class));
        } else {
            startService(new Intent(MediApplication.ApplicationContext(), DayOfWeekCheckService.class));
        }
    }

    private void changeMainContent(String selectedContent) {
        binding.mainContentAlarm.setVisibility(View.GONE);
        binding.mainContentSearch.setVisibility(View.GONE);
//        binding.mainContentUserList.setVisibility(View.GONE);
        binding.mainContentMap.setVisibility(View.GONE);

        switch (selectedContent) {
            case Config.MAIN_BOTTOM_MENU.HOME:
                binding.mainContentAlarm.setVisibility(View.VISIBLE);
                binding.mainContentAlarm.setUpUI();
                binding.mainBottomView.menuStatus(Config.MAIN_BOTTOM_MENU.HOME);
                break;
            case Config.MAIN_BOTTOM_MENU.SEARCH:
                binding.mainContentSearch.setVisibility(View.VISIBLE);
                binding.mainBottomView.menuStatus(Config.MAIN_BOTTOM_MENU.SEARCH);
                break;
            case Config.MAIN_BOTTOM_MENU.MAP:
                MapFragment mapFragment = new MapFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.main_content_map, mapFragment).commit();

                binding.mainContentMap.setVisibility(View.VISIBLE);
                binding.mainBottomView.menuStatus(Config.MAIN_BOTTOM_MENU.MAP);
                break;
            /*case Config.MAIN_BOTTOM_MENU.USER_LIST:
                binding.mainContentUserList.setVisibility(View.VISIBLE);
                binding.mainContentUserList.setUpUI();
                binding.mainBottomView.menuStatus(Config.MAIN_BOTTOM_MENU.USER_LIST);
                break;*/
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