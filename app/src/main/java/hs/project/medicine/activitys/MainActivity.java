package hs.project.medicine.activitys;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


import hs.project.medicine.Config;
import hs.project.medicine.MediApplication;
import hs.project.medicine.R;
import hs.project.medicine.main_content.LeftSlideView;
import hs.project.medicine.main_content.MainBottomView;
import hs.project.medicine.main_content.MapFragment;
import hs.project.medicine.databinding.ActivityMainBinding;
import hs.project.medicine.service.DayOfWeekCheckService;
import hs.project.medicine.util.LogUtil;

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

        binding.digitalClock.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
//                LogUtil.d(s.toString());
                String result = String.valueOf(s);
                String[] splitText = result.split(":");

                int hour = -1;
                int minute = -1;
                int seconds = -1;
                String amPm = "-1";


                /*  24시간 형식을 사용 중인 사용자 일때 */
                if (DateFormat.is24HourFormat(MediApplication.ApplicationContext())) {

                    for (int i = 0; i < splitText.length; i++) {
                        hour = Integer.parseInt(splitText[0]);
                        minute = Integer.parseInt(splitText[1]);
                        seconds = Integer.parseInt(splitText[2]);
//                        amPm = splitText[3];
                    }

//                    LogUtil.d("hour =[" + hour + "]" + " minute =[" + minute + "]" + " seconds =[" + seconds + "]");

                    if (hour == 0 && minute == 0 && seconds == 0) {
                        LogUtil.d("밤열두시");
                        startDayOfWeekService();
                    }
                } else {

                    for (int i = 0; i < splitText.length; i++) {
                        hour = Integer.parseInt(splitText[0]);
                        minute = Integer.parseInt(splitText[1]);
                        seconds = Integer.parseInt(splitText[2]);
                        amPm = splitText[3];
                    }

//                    LogUtil.d("hour =[" + hour + "]" + " minute =[" + minute + "]" + " seconds =[" + seconds + "]" + " amPm =[" + amPm + "]");

                    if (hour == 12 && minute == 0 && seconds == 0 && amPm.equals("오전") || amPm.equals("AM")) {
                        LogUtil.d("밤열두시");
                        startDayOfWeekService();
                    }
                }




            }
        });

        /*binding.digitalClock.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = String.valueOf(s);
                String[] splitText = result.split(":");

                *//*if (result.contains("오후") || result.contains("PM")) {

                    for (int i = 0; i < splitText.length; i++) {
                        int time = -1;
                        String splitResult = splitText[0].replace("오후", "").trim();
                        splitResult = splitResult.replace("PM", "").trim();
                        time = Integer.parseInt(splitResult) + 12;
                        LogUtil.d("time = [" + time + "]");
                    }

                } else *//*


                if (result.contains("오전") || result.contains("AM")) {
                    int time = -1;
                    boolean isSetAlarm = false;

                    for (int i = 0; i < splitText.length; i++) {

                        String splitResult = splitText[0].replace("오전", "").trim();
                        splitResult = splitResult.replace("AM", "").trim();
                        time = Integer.parseInt(splitResult);
                        LogUtil.d("time = [" + time + "]");
                    }

                    if (time == 0) {
                        isSetAlarm = true;
                    } else {
                        isSetAlarm = false;
                    }

                    if (time == 12) {
                        LogUtil.d("밤열두시");
//                        ((MainActivity) mainActivityContext).startDayOfWeekService();
                    }

                } else {
                    *//*  24시간 형식 사용할 때 *//*

                    int hour = -1;
                    int minute = -1;

                    for (int i = 0; i < splitText.length; i++) {
                        hour = Integer.parseInt(splitText[0]);
                        minute = Integer.parseInt(splitText[1]);
                        LogUtil.d("hour = [" + hour + "]" + "minute = [" + minute + "]");
                    }

                    if (hour == 0 && minute == 0) {
                        isSetAlarm = true;
                    } else {
                        isSetAlarm = false;
                    }

                    *//*  24 일 때 계속 실행됨 한번만 실행되도록 수정 필요  *//*
                    if (isSetAlarm) {

                        LogUtil.d("밤열두시");
//                        ((MainActivity) mainActivityContext).startDayOfWeekService();
                    }

                }
            }
        });*/
    }

    @Override
    protected void onStart() {
        super.onStart();

        binding.mainContentAlarm.setUpUI();
//        binding.mainContentUserList.setUpUI();

//        if (getIntent().getStringExtra("MAIN_BOTTOM_MENU") != null
//                && getIntent().getStringExtra("MAIN_BOTTOM_MENU").equals(Config.MAIN_BOTTOM_MENU.USER_LIST)) {
//            changeMainContent(Config.MAIN_BOTTOM_MENU.USER_LIST);
//        }
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