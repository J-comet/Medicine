package hs.project.medicine.activitys;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
//    public static Activity MAIN_ACTIVITY;

    private final String[] arrGpsPermissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    private static final int CODE_GPS_PERMISSION_ALL_GRANTED = 300;  // 모두허용
    private static final int CODE_GPS_PERMISSION_FINE_DENIED = 200;  // 대략허용
    private static final int CODE_GPS_PERMISSION_DENIED_TRUE = 100;  // 허용 거부한적 있는 유저
    private static final int CODE_GPS_PERMISSION_FIRST = 1000;  // 처음 권한 요청하는 유저

    private boolean isLocationPermission = false;

    //    위치서비스 꺼져있을 때 요청할 launcher
    ActivityResultLauncher<Intent> gpsSettingRequest = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        LogUtil.e("intent :" + intent.toString());
                    }
                }
            });

    //    권한 획득 launcher
    ActivityResultLauncher<String[]> gpsPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {

                        Boolean fineLocationGranted = null;
                        Boolean coarseLocationGranted = null;

                        LogUtil.e("result :" + result.toString());

                        // API 24 이상에서 동작
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION, false);

                        } else {
                            // API 24 이하에서 동작
                            fineLocationGranted = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                            coarseLocationGranted = result.get(Manifest.permission.ACCESS_COARSE_LOCATION);
                        }

                        /** Android 12 이상 위치권한 동작방식 변경
                         * 1) 정밀한 위치일 때 ACCESS_FINE_LOCATION , ACCESS_COARSE_LOCATION 두가지 권한 다 필요.
                         * 2) 대략적인 위치일 때 ACCESS_COARSE_LOCATION 권한만 필요.
                         * 3) 안드로이드 버전 11 이상부터 BackGroundLocation 권한 추가로 해줘야함
                         * */

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

                            LogUtil.e("fineLocationGranted :" + fineLocationGranted);
                            LogUtil.e("coarseLocationGranted :" + coarseLocationGranted);
//                            LogUtil.e("backGroundLocationGranted :" + backGroundLocationGranted);

                            if (fineLocationGranted != null && fineLocationGranted
                                    && coarseLocationGranted != null && coarseLocationGranted) {
//
                                // Precise location access granted.
                                // 정밀한 위치 사용에 대해서 위치권한 허용
                                LogUtil.e("정밀한위치");

//                                위치권한 허용했을 때 실행할 코드
                                isLocationPermission = true;
                                binding.mainContentAlarm.setUpUI(isLocationPermission);

                            } else if (coarseLocationGranted != null && coarseLocationGranted && fineLocationGranted == false) {
                                // Only approximate location access granted.
                                // 대략적인 위치 사용에 대해서만 위치권한 허용
                                LogUtil.e("대략적인위치");

//                                위치권한 허용했을 때 실행할 코드
                                isLocationPermission = true;
                                binding.mainContentAlarm.setUpUI(isLocationPermission);

                            } else {
                                // No location access granted.
                                // 권한획득 거부

                                isLocationPermission = false;
                                binding.mainContentAlarm.setUpUI(isLocationPermission);
                                Toast.makeText(this, "현재위치를 가져오려면\n위치 권한은 필수 입니다", Toast.LENGTH_SHORT).show();
                                LogUtil.e("위치권한거부");
                                permissionRequestDialog();
                            }

                        } else {
//                            api 30 미만일 때 실행


                            LogUtil.e("fineLocationGranted :" + fineLocationGranted);
                            LogUtil.e("coarseLocationGranted :" + coarseLocationGranted);

                            if (fineLocationGranted != null && fineLocationGranted && coarseLocationGranted != null && coarseLocationGranted) {
                                // Precise location access granted.
                                // 정밀한 위치 사용에 대해서 위치권한 허용
                                LogUtil.e("위치권한허용");

//                                위치권한 허용했을 때 실행할 코드
                                isLocationPermission = true;
                                binding.mainContentAlarm.setUpUI(isLocationPermission);
                            } else {
                                // No location access granted.
                                // 권한획득 거부
                                isLocationPermission = false;
                                binding.mainContentAlarm.setUpUI(isLocationPermission);
                                Toast.makeText(this, "현재위치를 가져오려면\n위치 권한은 필수 입니다", Toast.LENGTH_SHORT).show();
                                LogUtil.e("위치권한거부");
                                permissionRequestDialog();
                            }
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mainActivityContext = this;
//        MAIN_ACTIVITY = this;

        init();

        startDayOfWeekService();

        binding.digitalClock.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

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
    }

    @Override
    protected void onStart() {
        super.onStart();

//        권한체크
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionResultAction(gpsPermissionCheck());
            isLocationPermission = false;

            LogUtil.e("권한 없음");
        } else {

//            권한 획득한 사용자는 GPS 활성화 했는지 체크
            if (checkLocationServicesStatus()) {
                isLocationPermission = true;

                LogUtil.e("GPS ON");

            } else {
                isLocationPermission = true;

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("위치서비스");
                builder.setMessage("위치서비스를 활성화 해주세요");
                builder.setCancelable(false);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        gpsSettingRequest.launch(intent);
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "위치서비스 비활성화 상태", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        }

        binding.mainContentAlarm.setUpUI(isLocationPermission);
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

//            @Override
//            public void onUserListClick() {
//                if (isDrawerOpen()) {
//                    binding.dlSlide.closeDrawer(Gravity.LEFT);
//                }
//                changeMainContent(Config.MAIN_BOTTOM_MENU.USER_LIST);
//            }
        });

        /* LeftSlideView 나와있을 때 하위 뷰 터치 막는 코드 */
        binding.leftSlideView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    /* 사용자가 GPS 활성화 시켰는지 확인 */
    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /* 권한 얻은 후 행동할 메서드 */
    private void permissionResultAction(int permissionResult) {

        switch (permissionResult) {
            case CODE_GPS_PERMISSION_ALL_GRANTED:  // 모두허용
                LogUtil.d("위치권한 모두허용");
//                Toast.makeText(MapActivity.this, "위치권한 모두허용", Toast.LENGTH_SHORT).show();
                break;

            case CODE_GPS_PERMISSION_FINE_DENIED:  // 대략허용
                LogUtil.d("위치권한 대략허용");
//                Toast.makeText(MapActivity.this, "위치권한 대략허용", Toast.LENGTH_SHORT).show();
                break;

            case CODE_GPS_PERMISSION_DENIED_TRUE:  // 권한 거부한적 있는 유저
                permissionRequestDialog();
                break;

            case CODE_GPS_PERMISSION_FIRST:  // 처음 실행
                gpsPermissionRequest.launch(arrGpsPermissions);
                break;
        }
    }

    public void permissionRequestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("필수권한")
                .setMessage("위치정보를 얻기 위해\n위치권한을 허용해주세요")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "위치권한 거부", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    private int gpsPermissionCheck() {

        int isPermission = -1;

        // 권한 허용한 사용자인지 체크
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            /* 위치권한 모두 허용 */

            isPermission = CODE_GPS_PERMISSION_ALL_GRANTED;

        } else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {

            /* 위치권한 'ACCESS_COARSE_LOCATION' 만 허용 */
            isPermission = CODE_GPS_PERMISSION_FINE_DENIED;

        } else {


            /** 위치권한 거부
             * 1. 명시적으로 거부한 유저인지
             * 2. 권한을 요청한 적 없는 유저인지
             * */

            // 사용자가 권한요청을 명시적으로 거부했던 적 있는 경우 true 를 반환
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                isPermission = CODE_GPS_PERMISSION_DENIED_TRUE;
            } else {
                isPermission = CODE_GPS_PERMISSION_FIRST;
            }
        }
        return isPermission;
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
                binding.mainContentAlarm.setUpUI(isLocationPermission);
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