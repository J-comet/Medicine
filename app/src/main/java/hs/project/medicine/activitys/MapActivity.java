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
import androidx.core.graphics.ColorUtils;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.CircleOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import hs.project.medicine.Config;
import hs.project.medicine.HttpRequest;
import hs.project.medicine.MediApplication;
import hs.project.medicine.R;
import hs.project.medicine.databinding.ActivityMapBinding;
import hs.project.medicine.datas.Pharmacy;
import hs.project.medicine.util.LocationUtil;
import hs.project.medicine.util.LogUtil;

public class MapActivity extends BaseActivity implements View.OnClickListener, OnMapReadyCallback {

    /**
     * 처음 Activity 진입할 때 권한체크
     */

    private ActivityMapBinding binding;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 999;

    private String[] arrGpsPermissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    private static final int CODE_GPS_PERMISSION_ALL_GRANTED = 300;  // 모두허용
    private static final int CODE_GPS_PERMISSION_FINE_DENIED = 200;  // 대략허용
    private static final int CODE_GPS_PERMISSION_DENIED_TRUE = 100;  // 허용 거부한적 있는 유저
    private static final int CODE_GPS_PERMISSION_FIRST = 1000;  // 처음 권한 요청하는 유저

    private LocationManager locationManager;
    private LocationListener locationListener;

    private FusedLocationSource locationSource;
    private NaverMap map;

    boolean isFirst = true; // 처음에만 중심으로 이동할 플래그

    private CircleOverlay circleOverlay;
    private String curAddress = "";
    private ArrayList<Pharmacy> pharmacyList;  // 약국리스트
    private int pageNo = 1;
    private double getTotalCnt = -1;

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
                                    && coarseLocationGranted != null && coarseLocationGranted) {
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
                                permissionRequestDialog();
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
                                permissionRequestDialog();

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

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance(new NaverMapOptions().locationButtonEnabled(true));
            getSupportFragmentManager().beginTransaction().add(R.id.map_fragment, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) {
                map.setLocationTrackingMode(LocationTrackingMode.None);
                permissionRequestDialog();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                permissionRequestDialog();
                break;

            case CODE_GPS_PERMISSION_FIRST:  // 처음 실행
                gpsPermissionRequest.launch(arrGpsPermissions);
                break;
        }
    }

    private void permissionRequestDialog() {
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

        circleOverlay = new CircleOverlay();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                double lat = location.getLatitude();
                double lng = location.getLongitude();

                int color = ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_main_red);

                circleOverlay.setCenter(new LatLng(lat, lng));
                circleOverlay.setRadius(1000);
                circleOverlay.setColor(ColorUtils.setAlphaComponent(color, 31));
                circleOverlay.setOutlineColor(color);
                circleOverlay.setOutlineWidth(3);
                circleOverlay.setMap(map);


                Log.e("hs", "getCenter/"+circleOverlay.getCenter());
                Log.e("hs", "getBounds/"+circleOverlay.getBounds());
                Log.e("hs", "getGlobalZIndex/"+circleOverlay.getGlobalZIndex());

                curAddress = LocationUtil.changeForAddress(MapActivity.this, lat, lng);
                LogUtil.e("curAddress="+curAddress);
                String[] results = curAddress.split("\\s");
                LogUtil.e("results[0]="+results[0]);
                LogUtil.e("results[1]="+results[1]);
                LogUtil.e("results[2]="+results[2]);

                pharmacyList = new ArrayList<>();
                getTotalStoreData(results[1], results[2]);

            }

            @Override
            public void onFlushComplete(int requestCode) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 1, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 1, locationListener);

    }

    /* 총 데이터 개수 가져올 메서드 */
    private void getTotalStoreData(String Q0, String Q1) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                Map<String, Object> parameter = new HashMap<>();
                parameter.put("serviceKey", getResources().getString(R.string.api_key_easy_drug));
                parameter.put("Q0", Q0);  // ex) 서울특별시
                parameter.put("Q1", Q1);  // ex) 강남구

                String response = getRequest(Config.URL_GET_MEDICINE_STORE, HttpRequest.HttpType.GET, parameter);
                LogUtil.e("result/" + response);

                BufferedReader br = null;
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(true);
                DocumentBuilder builder = null;
                Document doc = null;

                InputSource is = new InputSource(new StringReader(response));
                try {
                    builder = factory.newDocumentBuilder();
                    doc = builder.parse(is);
                    XPathFactory xpathFactory = XPathFactory.newInstance();
                    XPath xpath = xpathFactory.newXPath();
                    XPathExpression expr = xpath.compile("//totalCount");
                    Node node = (Node) expr.evaluate(doc,XPathConstants.NODE);

                    LogUtil.d("현재 노드 이름 : "+ node.getNodeName());
                    LogUtil.d("현재 노드 값 : "+ node.getTextContent());

                    String cnt = node.getTextContent();
                    getTotalCnt = Integer.parseInt(cnt);
                    getTotalCnt = Math.ceil(getTotalCnt / 100);
                    pageNo = (int) getTotalCnt;

                    LogUtil.d("getTotalCnt / 100="+getTotalCnt / 100);
                    LogUtil.d("pageNo="+pageNo);


                    // pageNo 만큼 데이터 요청
                    for (int i = 1; i <= pageNo; i++) {
                        getStoreData(Q0, Q1, i, 100);
                    }


                } catch (ParserConfigurationException | IOException | SAXException | XPathExpressionException e) {
                    e.printStackTrace();
                }

            }
        };

        new Thread(runnable).start();

    }

    /* 현재위치의 주변 약국 정보 데이터 가져올 메서드 */
    private void getStoreData(String Q0, String Q1, int pageNo, int numOfRows) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                Map<String, Object> parameter = new HashMap<>();
                parameter.put("serviceKey", getResources().getString(R.string.api_key_easy_drug));
                parameter.put("Q0", Q0);  // ex) 서울특별시
                parameter.put("Q1", Q1);  // ex) 강남구
//                parameter.put("QT", pageNo);  // ex) 진료요일
//                parameter.put("QN", pageNo);  // ex) 기관명
//                parameter.put("ORD", pageNo);  // ex) 순서
                parameter.put("pageNo", pageNo);
                parameter.put("numOfRows", numOfRows);


                String response = getRequest(Config.URL_GET_MEDICINE_STORE, HttpRequest.HttpType.GET, parameter);
                LogUtil.e("result/" + response);

                BufferedReader br = null;
                //DocumentBuilderFactory 생성
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setNamespaceAware(true);
                DocumentBuilder builder = null;
                Document doc = null;
                
                InputSource is = new InputSource(new StringReader(response));
                try {
                    builder = factory.newDocumentBuilder();
                    doc = builder.parse(is);
                    XPathFactory xpathFactory = XPathFactory.newInstance();
                    XPath xpath = xpathFactory.newXPath();
                    XPathExpression expr = xpath.compile("//items/item");
                    NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

                    for (int i = 0; i < nodeList.getLength(); i++) {
                        NodeList child = nodeList.item(i).getChildNodes();
                        Pharmacy pharmacy = new Pharmacy();
                        Marker pharmacyMarker = new Marker();

                        for (int j = 0; j < child.getLength(); j++) {
                            Node node = child.item(j);

//                            LogUtil.d("현재 노드 이름 : "+ node.getNodeName());
//                            LogUtil.d("현재 노드 값 : "+ node.getTextContent());

                            switch (node.getNodeName()) {
                                case "dutyAddr":  // 주소
                                    pharmacy.setDutyAddr(node.getTextContent());
                                    break;
                                case "dutyNamel":  // 기관명
                                    pharmacy.setDutyNamel(node.getTextContent());
                                    break;
                                case "dutyTel1":  // 대표전화
                                    pharmacy.setDutyTel1(node.getTextContent());
                                    break;
                                case "wgs84Lon":  // 병원경도
                                    pharmacy.setWgs84Lon(Double.parseDouble(node.getTextContent()));
                                    break;
                                case "wgs84Lat":  // 병원위도
                                    pharmacy.setWgs84Lat(Double.parseDouble(node.getTextContent()));
                                    break;
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setMarker(pharmacyMarker, pharmacy.getWgs84Lat(), pharmacy.getWgs84Lon());
                                }
                            });

                        }
                        pharmacyList.add(pharmacy);
                    }
                    LogUtil.d("리스트 개수 : "+ pharmacyList.size());

                    /**
                     * 마크 추가해주기
                     */

                } catch (ParserConfigurationException | IOException | SAXException | XPathExpressionException e) {
                    e.printStackTrace();
                }

            }
        };

        new Thread(runnable).start();
    }

    private void setMarker(Marker marker, double lat, double lng)
    {
        //원근감 표시
        marker.setIconPerspectiveEnabled(true);

        //아이콘 지정
        marker.setIcon(OverlayImage.fromResource(R.drawable.ic_place));

        //마커의 투명도
        marker.setAlpha(0.5f);

        //마커 위치
        marker.setPosition(new LatLng(lat, lng));

        //마커 우선순위
//        marker.setZIndex(zIndex);

        //마커 표시
        marker.setMap(map);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.li_back:
                finish();
                break;
        }
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        map = naverMap;

        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

    }
}