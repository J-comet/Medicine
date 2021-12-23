package hs.project.medicine.activitys;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import net.daum.mf.map.api.MapPoint;

import hs.project.medicine.R;
import hs.project.medicine.databinding.ActivityMapBinding;
import hs.project.medicine.util.LogUtil;

public class MapActivity extends BaseActivity {

    /**
     * 처음 Activity 진입할 때 권한체크
     */

    private ActivityMapBinding binding;

    private String[] arrGpsPermissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    private static final int CODE_GPS_PERMISSION_ALL_GRANTED = 300;  // 모두허용
    private static final int CODE_GPS_PERMISSION_FINE_DENIED = 200;  // 대략허용
    private static final int CODE_GPS_PERMISSION_DENIED_TRUE = 100;  // 허용 거부한적 있는 유저
    private static final int CODE_GPS_PERMISSION_FIRST = 1000;  // 처음 권한 요청하는 유저

    // 권한 획득 launcher
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
                         */
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

                            LogUtil.e("fineLocationGranted :" + fineLocationGranted);
                            LogUtil.e("coarseLocationGranted :" + coarseLocationGranted);

                            if (fineLocationGranted != null && fineLocationGranted && coarseLocationGranted != null && coarseLocationGranted) {
                                // Precise location access granted.
                                // 정밀한 위치 사용에 대해서 위치권한 허용
                                LogUtil.e("정밀한위치");

                            } else if (coarseLocationGranted != null && coarseLocationGranted && fineLocationGranted == false) {
                                // Only approximate location access granted.
                                // 대략적인 위치 사용에 대해서만 위치권한 허용
                                LogUtil.e("대략적인위치");

                            } else {
                                // No location access granted.
                                // 권한획득 거부
                                Toast.makeText(this, "현재위치를 가져오려면 위치 권한은 필수 입니다", Toast.LENGTH_SHORT).show();
                                LogUtil.e("위치권한거부");
                            }

                        } else {
                            Toast.makeText(this, "현재위치를 가져오려면 위치 권한은 필수 입니다", Toast.LENGTH_SHORT).show();
                            LogUtil.e("위치권한거부");
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /* 권한체크 */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionResultAction(gpsPermissionCheck());
            Toast.makeText(this, "위치권한 허용 필요", Toast.LENGTH_SHORT).show();

            LogUtil.e("권한 없음");
            return;
        }

        // 중심점 이동
        binding.mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633), true);

        // 줌 레벨 변경
        binding.mapView.setZoomLevel(7, true);

        // 중심점 변경 + 줌 레벨 변경
//        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(33.41, 126.52), 9, true);

        // 줌 인
        binding.mapView.zoomIn(true);

        // 줌 아웃
        binding.mapView.zoomOut(true);

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


}