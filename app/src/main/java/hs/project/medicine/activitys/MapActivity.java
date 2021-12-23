package hs.project.medicine.activitys;

import static hs.project.medicine.HttpRequest.getRequest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.HashMap;
import java.util.Map;

import hs.project.medicine.Config;
import hs.project.medicine.HttpRequest;
import hs.project.medicine.R;
import hs.project.medicine.databinding.ActivityMapBinding;
import hs.project.medicine.util.LocationUtil;
import hs.project.medicine.util.LogUtil;

public class MapActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 처음 Activity 진입할 때 권한체크
     */

    private ActivityMapBinding binding;

    private String[] arrGpsPermissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private String[] arrGpsPermissionsHighVersion = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION};

    private static final int CODE_GPS_PERMISSION_ALL_GRANTED = 300;  // 모두허용
    private static final int CODE_GPS_PERMISSION_FINE_DENIED = 200;  // 대략허용
    private static final int CODE_GPS_PERMISSION_DENIED_TRUE = 100;  // 허용 거부한적 있는 유저
    private static final int CODE_GPS_PERMISSION_FIRST = 1000;  // 처음 권한 요청하는 유저

    private LocationManager locationManager;
    private LocationListener locationListener;

    /* 위치서비스 꺼져있을 때 요청할 launcher */
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

    /* 권한 획득 launcher */
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

                        /**
                         * Android 12 이상 위치권한 동작방식 변경
                         * 1) 정밀한 위치일 때 ACCESS_FINE_LOCATION , ACCESS_COARSE_LOCATION 두가지 권한 다 필요.
                         * 2) 대략적인 위치일 때 ACCESS_COARSE_LOCATION 권한만 필요.
                         * 3) 안드로이드 버전 11 이상부터 BackGroundLocation 권한 추가로 해줘야함
                         */
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

                            LogUtil.e("fineLocationGranted :" + fineLocationGranted);
                            LogUtil.e("coarseLocationGranted :" + coarseLocationGranted);
//                            LogUtil.e("backGroundLocationGranted :" + backGroundLocationGranted);

                            if (fineLocationGranted != null && fineLocationGranted
                                    && coarseLocationGranted != null && coarseLocationGranted ) {
//
                                // Precise location access granted.
                                // 정밀한 위치 사용에 대해서 위치권한 허용
                                LogUtil.e("정밀한위치");

                                /* 위치권한 허용했을 때 실행할 코드 */
                                myLocationON();

                            } else if (coarseLocationGranted != null && coarseLocationGranted && fineLocationGranted == false) {
                                // Only approximate location access granted.
                                // 대략적인 위치 사용에 대해서만 위치권한 허용
                                LogUtil.e("대략적인위치");

                                /* 위치권한 허용했을 때 실행할 코드 */
                                myLocationON();

                            } else {
                                // No location access granted.
                                // 권한획득 거부
                                Toast.makeText(this, "현재위치를 가져오려면\n위치 권한은 필수 입니다", Toast.LENGTH_SHORT).show();
                                LogUtil.e("위치권한거부");
                            }

                        } else {
                            /* api 30 미만일 때 실행 */

                            LogUtil.e("fineLocationGranted :" + fineLocationGranted);
                            LogUtil.e("coarseLocationGranted :" + coarseLocationGranted);

                            if (fineLocationGranted != null && fineLocationGranted && coarseLocationGranted != null && coarseLocationGranted) {
                                // Precise location access granted.
                                // 정밀한 위치 사용에 대해서 위치권한 허용
                                LogUtil.e("위치권한허용");

                                /* 위치권한 허용했을 때 실행할 코드 */
                                myLocationON();
                            } else {
                                // No location access granted.
                                // 권한획득 거부
                                Toast.makeText(this, "현재위치를 가져오려면\n위치 권한은 필수 입니다", Toast.LENGTH_SHORT).show();
                                LogUtil.e("위치권한거부");
                            }
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        // 중심점 이동
//        binding.mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633), true);
        // 줌 레벨 변경
        binding.mapView.setZoomLevel(7, true);
        // 중심점 변경 + 줌 레벨 변경
//        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(33.41, 126.52), 9, true);
        // 줌 인
        binding.mapView.zoomIn(true);
        // 줌 아웃
        binding.mapView.zoomOut(true);


        /**
         * 트래킹모드 현재 안드로이드 11 버전 에러 발생
         */
        /*new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
            }
        }, 3000);*/


        /*binding.mapView.setCurrentLocationEventListener(new MapView.CurrentLocationEventListener() {
            @Override
            public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
                binding.mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633), true);
            }

            @Override
            public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

            }

            @Override
            public void onCurrentLocationUpdateFailed(MapView mapView) {

            }

            @Override
            public void onCurrentLocationUpdateCancelled(MapView mapView) {

            }
        });

        binding.mapView.setMapViewEventListener(new MapView.MapViewEventListener() {
            @Override
            public void onMapViewInitialized(MapView mapView) {

            }

            @Override
            public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

            }

            @Override
            public void onMapViewZoomLevelChanged(MapView mapView, int i) {

            }

            @Override
            public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

            }

            @Override
            public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

            }

            @Override
            public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

            }

            @Override
            public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

            }

            @Override
            public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

            }

            @Override
            public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

            }
        });*/

        /* 권한체크 */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionResultAction(gpsPermissionCheck());

            LogUtil.e("권한 없음");
        } else {

            /* 권한 획득한 사용자는 GPS 활성화 했는지 체크 */

            if (checkLocationServicesStatus()) {
                myLocationON();

                LogUtil.e("GPS ON");

            } else {
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
                        Toast.makeText(MapActivity.this, "위치서비스 비활성화 상태", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        }
    }

    private void init() {
        binding.liBack.setOnClickListener(this);
    }

    /**
     * 사용자가 GPS 활성화 시켰는지 확인
     */
    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /**
     * 권한 얻은 후 행동할 메서드
     */
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

                AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
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
                        Toast.makeText(MapActivity.this, "위치권한 거부", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();

                break;

            case CODE_GPS_PERMISSION_FIRST:  // 처음 실행
                gpsPermissionRequest.launch(arrGpsPermissions);
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    gpsPermissionRequest.launch(arrGpsPermissionsHighVersion);
                } else {
                    gpsPermissionRequest.launch(arrGpsPermissions);
                }*/

                break;
        }
    }

    private int gpsPermissionCheck() {

        int isPermission = -1;

        // 권한 허용한 사용자인지 체크
        if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            /**
             * 위치권한 모두 허용
             */
            isPermission = CODE_GPS_PERMISSION_ALL_GRANTED;

        } else if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {

            /**
             * 위치권한 'ACCESS_COARSE_LOCATION' 만 허용
             */
            isPermission = CODE_GPS_PERMISSION_FINE_DENIED;

        } else {

            /**
             * 위치권한 거부
             * 1. 명시적으로 거부한 유저인지
             * 2. 권한을 요청한 적 없는 유저인지
             */

            // 사용자가 권한요청을 명시적으로 거부했던 적 있는 경우 true 를 반환
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                isPermission = CODE_GPS_PERMISSION_DENIED_TRUE;
            } else {
                isPermission = CODE_GPS_PERMISSION_FIRST;
            }
        }
        return isPermission;

    }

    /* 내 위치 띄우는 코드 */
    @SuppressLint("MissingPermission")
    private void myLocationON() {
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//        locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(@NonNull Location location) {
//                double lat = location.getLatitude();
//                double lng = location.getLongitude();
//                // 중심점 이동
//                binding.mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(lat, lng), true);
//
//                LogUtil.e(LocationUtil.changeForAddress(MapActivity.this, lat, lng));
//            }
//
//            @Override
//            public void onFlushComplete(int requestCode) {
//
//            }
//
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(@NonNull String provider) {
//
//            }
//
//            @Override
//            public void onProviderDisabled(@NonNull String provider) {
//
//            }
//        };
//
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

//        binding.mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
    }

    private void getStoreData(String Q0, String Q1, int pageNo, int numOfRows) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                LogUtil.e("current pageNo:" + pageNo + "current numOfRows" + numOfRows);

                Map<String, Object> parameter = new HashMap<>();
                parameter.put("serviceKey", getResources().getString(R.string.api_key_easy_drug));
                parameter.put("Q0", Q0);  // ex) 서울특별시
                parameter.put("Q1", Q1);  // ex) 강남구
//                parameter.put("QT", pageNo);  // ex) 진료요일
//                parameter.put("QN", pageNo);  // ex) 기관명
//                parameter.put("ORD", pageNo);  // ex) 순서
                parameter.put("pageNo", pageNo);
                parameter.put("numOfRows", numOfRows);


                //http://apis.data.go.kr/B552657/ErmctInsttInfoInqireService/getParmacyListInfoInqire?
                // serviceKey=9kam%2BiI3ibjMnJs3msCIG%2BP7tblP9JT8113lnL25tQ1MMoDrJ%2Fml3q6uvcNgVMBJI%2FSWGQtiy70VHymbS17bgw%3D%3D&
                // Q0=%EC%84%9C%EC%9A%B8%ED%8A%B9%EB%B3%84%EC%8B%9C&
                // Q1=%EA%B0%95%EB%82%A8%EA%B5%AC&
                // QT=1&
                // QN=%EC%82%BC%EC%84%B1%EC%95%BD%EA%B5%AD&
                // ORD=NAME&
                // pageNo=1&
                // numOfRows=10

                String response = getRequest(Config.URL_GET_MEDICINE_STORE, HttpRequest.HttpType.GET, parameter);
                Log.e("result", response);

            }
        };

        new Thread(runnable).start();
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