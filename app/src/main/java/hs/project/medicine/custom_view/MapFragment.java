package hs.project.medicine.custom_view;

import static android.content.Context.LOCATION_SERVICE;

import static hs.project.medicine.HttpRequest.getRequest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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

import hs.project.medicine.Config;
import hs.project.medicine.HttpRequest;
import hs.project.medicine.MediApplication;
import hs.project.medicine.R;
import hs.project.medicine.databinding.FragmentMapBinding;
import hs.project.medicine.databinding.LayoutMainHomeViewBinding;
import hs.project.medicine.datas.NaverGeocodingResult;
import hs.project.medicine.datas.Pharmacy;
import hs.project.medicine.dialog.BottomSheetMapSearchDialog;
import hs.project.medicine.util.LocationUtil;
import hs.project.medicine.util.LogUtil;
import hs.project.medicine.util.NetworkUtil;

public class MapFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private FragmentMapBinding binding;
    private final String[] arrGpsPermissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    private static final int CODE_GPS_PERMISSION_ALL_GRANTED = 300;  // 모두허용
    private static final int CODE_GPS_PERMISSION_FINE_DENIED = 200;  // 대략허용
    private static final int CODE_GPS_PERMISSION_DENIED_TRUE = 100;  // 허용 거부한적 있는 유저
    private static final int CODE_GPS_PERMISSION_FIRST = 1000;  // 처음 권한 요청하는 유저

    /* 단일 권한 */
//    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
//        if (isGranted) {
//            // Permission is granted. Continue the action or workflow in your
//            // app.
//        } else {
//            // Explain to the user that the feature is unavailable because the
//            // features requires a permission that the user has denied. At the
//            // same time, respect the user's decision. Don't link to system
//            // settings in an effort to convince the user to change their
//            // decision.
//        }
//    });

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

                                Toast.makeText(requireActivity(), "현재위치를 가져오려면\n위치 권한은 필수 입니다", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(requireActivity(), "현재위치를 가져오려면\n위치 권한은 필수 입니다", Toast.LENGTH_SHORT).show();
                                LogUtil.e("위치권한거부");
                                permissionRequestDialog();
                            }
                        }
                    }
            );

    private NaverMap map;
    private FusedLocationSource locationSource;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 999;
    private InfoWindow infoWindow;
    private BottomSheetMapSearchDialog bottomSheetMapSearchDialog;
    private ArrayList<Marker> markerArrayList;
    private double cameraZoomLevel = 12;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String curAddress = "";
    private int pageNo = 1;
    private double getTotalCnt = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.mapView.onStart();
        binding.clLoading.setVisibility(View.VISIBLE);

        /* 권한체크 */
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionResultAction(gpsPermissionCheck());

            LogUtil.e("권한 없음");
        } else {

            /* 권한 획득한 사용자는 GPS 활성화 했는지 체크 */

            if (checkLocationServicesStatus()) {
                myLocationON();
                LogUtil.e("GPS ON");

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
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
                        Toast.makeText(requireActivity(), "위치서비스 비활성화 상태", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        binding.mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
        binding.mapView.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapView.onLowMemory();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.getMapAsync(this);
        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        binding.liSearch.setOnClickListener(this);
        binding.liMyLocation.setOnClickListener(this);
        binding.clLoading.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // ignore all touch events
                return true;
            }
        });

        bottomSheetMapSearchDialog = new BottomSheetMapSearchDialog(requireActivity());
        bottomSheetMapSearchDialog.setBottomSheetListener(new BottomSheetMapSearchDialog.BottomSheetListener() {
            @Override
            public void onBtnClicked(String location, String locationDetail) {
                LogUtil.e("dialog/ location:" + location + "locationDetail:" + locationDetail);

                binding.clLoading.setVisibility(View.VISIBLE);

                if (NetworkUtil.checkConnectedNetwork(requireActivity())) {

                    /* 세종특별자치시는 네이버 지오코더로 해야함 */
                    if (location.equals("세종특별자치시")) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                LogUtil.e("result =" + HttpRequest.searchNaverGeocode(location).toString());

                                NaverGeocodingResult geocodingResult;
                                geocodingResult = HttpRequest.searchNaverGeocode(location);

                                requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(new LatLng(geocodingResult.getY(), geocodingResult.getX()), cameraZoomLevel)
                                                .animate(CameraAnimation.Fly, 1500)
                                                .finishCallback(() -> {
                                                    getTotalStoreData(location, "");
                                                    binding.tvCurPlace.setText(location);
                                                })
                                                .cancelCallback(() -> {
                                                    LogUtil.d("카메라 이동 취소");
                                                });

                                        map.moveCamera(cameraUpdate);
                                    }
                                });
                            }
                        }).start();

                    } else {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                LocationUtil.changeForLatLng(requireActivity(), location + " " + locationDetail);
                                LogUtil.e("result =" + LocationUtil.changeForLatLng(requireActivity(), location + " " + locationDetail));

                                String str = LocationUtil.changeForLatLng(requireActivity(), location + " " + locationDetail);
                                String[] arrResult = str.split("%");

                                double lat = Double.parseDouble(arrResult[0]);
                                double lng = Double.parseDouble(arrResult[1]);

                                requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(new LatLng(lat, lng), cameraZoomLevel)
                                                .animate(CameraAnimation.Fly, 1500)
                                                .finishCallback(() -> {
                                                    getTotalStoreData(location, locationDetail);
                                                    binding.tvCurPlace.setText(location + " " + locationDetail);
                                                })
                                                .cancelCallback(() -> {
                                                    LogUtil.d("카메라 이동 취소");
                                                });
                                        map.moveCamera(cameraUpdate);
                                    }
                                });
                            }
                        }).start();

                    }

                } else {
                    NetworkUtil.networkErrorDialogShow(requireActivity(), false);
                    binding.clLoading.setVisibility(View.GONE);
                }
            }
        });
    }

    /* 내 위치 띄우는 코드 */
    @SuppressLint("MissingPermission")
    private void myLocationON() {

//        circleOverlay = new CircleOverlay();
        locationManager = (LocationManager) requireActivity().getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                double lat = location.getLatitude();
                double lng = location.getLongitude();

//                int color = ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_main_red);

//                circleOverlay.setCenter(new LatLng(lat, lng));
//                circleOverlay.setRadius(1000);
//                circleOverlay.setColor(ColorUtils.setAlphaComponent(color, 31));
//                circleOverlay.setOutlineColor(color);
//                circleOverlay.setOutlineWidth(3);
//                circleOverlay.setMap(map);


//                Log.e("hs", "getCenter/"+circleOverlay.getCenter());
//                Log.e("hs", "getBounds/"+circleOverlay.getBounds());
//                Log.e("hs", "getGlobalZIndex/"+circleOverlay.getGlobalZIndex());

                if (NetworkUtil.checkConnectedNetwork(requireActivity())) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            curAddress = LocationUtil.changeForAddress(requireActivity(), lat, lng);

                            String[] results = curAddress.split("\\s");
                            LogUtil.e("results[0]=" + results[0]);
                            LogUtil.e("results[1]=" + results[1]);
                            LogUtil.e("results[2]=" + results[2]);

                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.tvCurPlace.setText(results[1] + " " + results[2]);

                                    CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(new LatLng(lat, lng), cameraZoomLevel)
                                            .animate(CameraAnimation.Fly, 1500)
                                            .finishCallback(() -> {
                                                getTotalStoreData(results[1], results[2]);
                                                binding.tvCurPlace.setText(results[1] + " " + results[2]);
                                            })
                                            .cancelCallback(() -> {
                                                LogUtil.d("카메라 이동 취소");
                                            });

                                    map.moveCamera(cameraUpdate);
                                }
                            });
                        }
                    }).start();

                } else {
                    NetworkUtil.networkErrorDialogShow(requireActivity(), false);
                    binding.clLoading.setVisibility(View.GONE);
                }
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

        // minTime = 1시간
        // minDistance = 1km
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3600000, 1000, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3600000, 1000, locationListener);

    }

    private void permissionRequestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("필수권한")
                .setMessage("위치정보를 얻기 위해\n위치권한을 허용해주세요")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + requireActivity().getPackageName()));
                        startActivity(intent);
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(requireActivity(), "위치권한 거부", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    private int gpsPermissionCheck() {

        int isPermission = -1;

        // 권한 허용한 사용자인지 체크
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            /**
             * 위치권한 모두 허용
             */
            isPermission = CODE_GPS_PERMISSION_ALL_GRANTED;

        } else if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {

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
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    || ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
                isPermission = CODE_GPS_PERMISSION_DENIED_TRUE;
            } else {
                isPermission = CODE_GPS_PERMISSION_FIRST;
            }
        }
        return isPermission;
    }

    /**
     * 사용자가 GPS 활성화 시켰는지 확인
     */
    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(LOCATION_SERVICE);

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

    /* 총 데이터 개수 가져올 메서드 */
    private void getTotalStoreData(String Q0, String Q1) {

        if (NetworkUtil.checkConnectedNetwork(requireActivity())) {
            /* 기존에 생성된 마커 삭제 후 새로운 list 객체 생성 */
            if (markerArrayList != null && markerArrayList.size() > 0) {
                removeAllMarker(markerArrayList);
            }

            markerArrayList = new ArrayList<>();

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
                        Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);

                        LogUtil.d("현재 노드 이름 : " + node.getNodeName());
                        LogUtil.d("현재 노드 값 : " + node.getTextContent());

                        String cnt = node.getTextContent();
                        getTotalCnt = Integer.parseInt(cnt);
                        getTotalCnt = Math.ceil(getTotalCnt / 100);
                        pageNo = (int) getTotalCnt;

                        LogUtil.d("getTotalCnt / 100=" + getTotalCnt / 100);
                        LogUtil.d("pageNo=" + pageNo);

                        // pageNo 만큼 데이터 요청
                        for (int i = 1; i <= pageNo; i++) {
                            getStoreData(Q0, Q1, i, 100);
                        }

                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.clLoading.setVisibility(View.GONE);
                            }
                        });

                    } catch (ParserConfigurationException | IOException | SAXException | XPathExpressionException e) {
                        e.printStackTrace();
                    }

                }
            };

            new Thread(runnable).start();

        } else {
            NetworkUtil.networkErrorDialogShow(requireActivity(), false);
            binding.clLoading.setVisibility(View.GONE);
        }


    }

    /* 현재위치의 주변 약국 정보 데이터 가져올 메서드 */
    private void getStoreData(String Q0, String Q1, int pageNo, int numOfRows) {

        if (NetworkUtil.checkConnectedNetwork(requireActivity())) {
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
//                LogUtil.e("result/" + response);

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
                            infoWindow = new InfoWindow();

                            for (int j = 0; j < child.getLength(); j++) {
                                Node node = child.item(j);

//                            LogUtil.d("현재 노드 이름 : "+ node.getNodeName());
//                            LogUtil.d("현재 노드 값 : "+ node.getTextContent());

                                switch (node.getNodeName()) {
                                    case "dutyAddr":  // 주소
                                        pharmacy.setDutyAddr(node.getTextContent());
                                        break;
                                    case "dutyName":  // 기관명
                                        pharmacy.setDutyName(node.getTextContent());
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

                                requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        setMarker(pharmacyMarker, pharmacy.getWgs84Lat(), pharmacy.getWgs84Lon());
                                        pharmacyMarker.setOnClickListener(new Overlay.OnClickListener() {
                                            @Override
                                            public boolean onClick(@NonNull Overlay overlay) {

                                                CameraUpdate cameraUpdate2 = CameraUpdate.scrollTo(new LatLng(pharmacy.getWgs84Lat(), pharmacy.getWgs84Lon()))
                                                        .animate(CameraAnimation.Easing, 350)
                                                        .finishCallback(() -> {
                                                            infoWindow.setAnchor(new PointF(0, 1));
                                                            infoWindow.setOffsetX(getResources().getDimensionPixelSize(R.dimen.custom_info_window_offset_x));
                                                            infoWindow.setOffsetY(getResources().getDimensionPixelSize(R.dimen.custom_info_window_offset_y));
                                                            LogUtil.d(pharmacy.getDutyName() + "/" + pharmacy.getDutyTel1() + "/" + pharmacy.getDutyAddr());
                                                            infoWindow.setAdapter(new InfoWindowAdapter(requireActivity(), pharmacy.getDutyName(), pharmacy.getDutyTel1(), pharmacy.getDutyAddr()));
                                                            infoWindow.setOnClickListener(new Overlay.OnClickListener() {
                                                                @Override
                                                                public boolean onClick(@NonNull Overlay overlay) {
                                                                    infoWindow.close();
                                                                    return true;
                                                                }
                                                            });

                                                            infoWindow.open(pharmacyMarker);
                                                        })
                                                        .cancelCallback(() -> {
                                                            LogUtil.d("카메라 이동 취소");
                                                        });

                                                map.moveCamera(cameraUpdate2);

                                                return false;
                                            }

                                        });

                                    }
                                });

                            }

                            /* 다른지역 검색 후 이전 marker 전체 삭제하기위해 List 에 추가 */
                            markerArrayList.add(pharmacyMarker);
                        }
                        LogUtil.d("리스트 개수 : " + markerArrayList.size());

                    } catch (ParserConfigurationException | IOException | SAXException | XPathExpressionException e) {
                        e.printStackTrace();
                    }

                }
            };
            new Thread(runnable).start();

        } else {
            NetworkUtil.networkErrorDialogShow(requireActivity(), false);
            binding.clLoading.setVisibility(View.GONE);
        }
    }

    /* 전체마커 삭제 */
    private void removeAllMarker(ArrayList<Marker> markers) {
        for (int i = 0; i < markers.size(); i++) {
            markers.get(i).setMap(null);
        }
    }

    /* 마커 하나씩 생성 */
    private void setMarker(Marker marker, double lat, double lng) {
        //원근감 표시
        marker.setIconPerspectiveEnabled(true);

        //아이콘 지정
        marker.setIcon(OverlayImage.fromResource(R.drawable.ic_place_red));

        //마커의 투명도
//        marker.setAlpha(0.1f);

        //마커 위치
        marker.setPosition(new LatLng(lat, lng));

        //마커 우선순위
//        marker.setZIndex(zIndex);

        //마커 표시
        marker.setMap(map);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        map = naverMap;

        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.li_search:
                bottomSheetMapSearchDialog.show(getActivity().getSupportFragmentManager(), "mapSearchDialog");
                break;
            case R.id.li_my_location:
                if (NetworkUtil.checkConnectedNetwork(getActivity())) {
                    binding.clLoading.setVisibility(View.VISIBLE);
                    myLocationON();
                } else {
                    NetworkUtil.networkErrorDialogShow(getActivity(), false);
                }
                break;
        }
    }

    /* InfoWindowAdapter */
    private class InfoWindowAdapter extends InfoWindow.ViewAdapter {
        @NonNull
        private final Context context;
        private View rootView;
        //        private ImageView icon;
        private TextView tvName;
        private TextView tvTell;
        private TextView tvAdd;
        //        private Pharmacy mPharmacy;
        private final String name;
        private final String phone;
        private final String address;

        public InfoWindowAdapter(@NonNull Context context, String dutyName, String dutyTel1, String add) {
            this.context = context;
            this.name = dutyName;
            this.phone = dutyTel1;
            this.address = add;
        }

        @NonNull
        @Override
        public View getView(@NonNull InfoWindow infoWindow) {
            if (rootView == null) {
                rootView = View.inflate(context, R.layout.view_map_info_window, null);
                tvName = rootView.findViewById(R.id.tv_name);
                tvTell = rootView.findViewById(R.id.tv_tell);
                tvAdd = rootView.findViewById(R.id.tv_add);
            }

            if (infoWindow.getMarker() != null) {
                tvName.setText(name);
                tvTell.setText(phone);
                tvAdd.setText(address);
//                tvTell.setText((String)infoWindow.getMarker().getTag());
            } /*else {
                icon.setImageResource(R.drawable.ic_my_location_black_24dp);
                text.setText(context.getString(
                        R.string.format_coord, infoWindow.getPosition().latitude, infoWindow.getPosition().longitude));
            }*/

            return rootView;
        }
    }

}