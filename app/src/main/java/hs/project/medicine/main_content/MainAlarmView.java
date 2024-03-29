package hs.project.medicine.main_content;

import static android.content.Context.LOCATION_SERVICE;
import static hs.project.medicine.HttpRequest.getRequest;
import static hs.project.medicine.activitys.MainActivity.mainActivityContext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ColorFilter;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.SimpleColorFilter;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieValueCallback;
import com.google.android.gms.internal.location.zzz;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hs.project.medicine.Config;
import hs.project.medicine.HttpRequest;
import hs.project.medicine.MediApplication;
import hs.project.medicine.R;
import hs.project.medicine.activitys.AddAlarmActivity;
import hs.project.medicine.activitys.MainActivity;
import hs.project.medicine.adapter.AlarmAdapter;
import hs.project.medicine.databinding.LayoutMainAlarmViewBinding;
import hs.project.medicine.datas.Alarm;
import hs.project.medicine.datas.medicine.MedicineHeader;
import hs.project.medicine.datas.weather.WeatherBody;
import hs.project.medicine.datas.weather.WeatherHeader;
import hs.project.medicine.datas.weather.WeatherItem;
import hs.project.medicine.datas.weather.WeatherItems;
import hs.project.medicine.datas.weather.WeatherResponse;
import hs.project.medicine.dialog.ModifyAlarmDialog;
import hs.project.medicine.service.DayOfWeekCheckService;
import hs.project.medicine.util.LocationUtil;
import hs.project.medicine.util.LogUtil;
import hs.project.medicine.util.NetworkUtil;
import hs.project.medicine.util.PreferenceUtil;
import hs.project.medicine.util.TransLocationUtil;


public class MainAlarmView extends ConstraintLayout implements View.OnClickListener {

    private LayoutMainAlarmViewBinding binding;
    private Context context;
    private ArrayList<Alarm> alarmArrayList;
    private AlarmAdapter alarmAdapter;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location location;

    private String strNx = "";
    private String strNy = "";
    private String currentLocation = "";

    private final String[] arrGpsPermissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    private static final int CODE_GPS_PERMISSION_ALL_GRANTED = 300;  // 모두허용
    private static final int CODE_GPS_PERMISSION_FINE_DENIED = 200;  // 대략허용
    private static final int CODE_GPS_PERMISSION_DENIED_TRUE = 100;  // 허용 거부한적 있는 유저
    private static final int CODE_GPS_PERMISSION_FIRST = 1000;  // 처음 권한 요청하는 유저

    public boolean hasPermission = false;

    public MainAlarmView(@NonNull Context context) {
        super(context);
        this.context = context;
        initView(context);
    }

    public MainAlarmView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
    }

    public MainAlarmView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView(context);
    }

    public MainAlarmView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initView(Context context) {
        binding = LayoutMainAlarmViewBinding.inflate(LayoutInflater.from(context), this, true);

        alarmAdapter = new AlarmAdapter(context);
        alarmAdapter.setOnEventListener(new AlarmAdapter.OnEventListener() {
            @Override
            public void onRemoveClick(View view, int position) {
                /* 1. 기존에 저장되어 있는 알람의 이름으로 비교 후 Preference 에서 삭제
                 *  2. RecyclerView 아이템 position 으로 삭제 */

                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("알람")
                        .setMessage("정말 삭제하시겠습니까?")
                        .setCancelable(false)
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                ArrayList<String> strAlarmList = new ArrayList<>();
                                alarmArrayList.remove(position);

                                for (int i = 0; i < alarmArrayList.size(); i++) {
                                    Alarm alarm = new Alarm();
                                    alarm.setName(alarmArrayList.get(i).getName());
                                    alarm.setAmPm(alarmArrayList.get(i).getAmPm());
                                    alarm.setHour(alarmArrayList.get(i).getHour());
                                    alarm.setMinute(alarmArrayList.get(i).getMinute());
                                    alarm.setVolume(alarmArrayList.get(i).getVolume());
                                    alarm.setRingtoneName(alarmArrayList.get(i).getRingtoneName());
                                    alarm.setRingtoneUri(alarmArrayList.get(i).getRingtoneUri());
                                    alarm.setDayOfWeek(alarmArrayList.get(i).getDayOfWeek());
                                    alarm.setAlarmON(alarmArrayList.get(i).isAlarmON());

                                    strAlarmList.add(alarm.toJSON());
                                }

                                PreferenceUtil.setJSONArrayPreference(context, Config.PREFERENCE_KEY.ALARM_LIST, strAlarmList);

                                alarmAdapter.removeAt(position);

                                if (alarmArrayList.size() < 1) {
                                    binding.clNone.setVisibility(View.VISIBLE);
                                    binding.clAlarmList.setVisibility(View.GONE);

                                    context.stopService(new Intent(MediApplication.ApplicationContext(), DayOfWeekCheckService.class));

                                } else {
                                    binding.clNone.setVisibility(View.GONE);
                                    binding.clAlarmList.setVisibility(View.VISIBLE);
                                }

                                dialog.dismiss();
                            }
                        }).create();

                dialog.show();
                dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.black));
                dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.black));

            }

            @Override
            public void onModifyClick(View view, int position) {
                ModifyAlarmDialog modifyAlarmDialog = new ModifyAlarmDialog(context, alarmArrayList.get(position));
                modifyAlarmDialog.setModifyAlarmListener(new ModifyAlarmDialog.ModifyAlarmListener() {
                    @Override
                    public void onComplete() {
                        getAlarmList();
                    }
                });
                modifyAlarmDialog.show(((MainActivity) MainActivity.mainActivityContext).getSupportFragmentManager(), "modifyAlarmDialog");
            }

            /*@Override
            public void onSwitchViewClick(boolean isChecked, int position) {

                strAlarmList = new ArrayList<>();

                if (isChecked) {
                    Toast.makeText(context, alarmArrayList.get(position).getName() + " 알람 활성", Toast.LENGTH_SHORT).show();
                }

                for (int i = 0; i < alarmArrayList.size(); i++) {
                    Alarm alarm = new Alarm();
                    alarm.setName(alarmArrayList.get(i).getName());
                    alarm.setAmPm(alarmArrayList.get(i).getAmPm());
                    alarm.setDayOfWeek(alarmArrayList.get(i).getDayOfWeek());
                    alarm.setHour(alarmArrayList.get(i).getHour());
                    alarm.setMinute(alarmArrayList.get(i).getMinute());
                    alarm.setVolume(alarmArrayList.get(i).getVolume());
                    alarm.setRingtoneName(alarmArrayList.get(i).getRingtoneName());
                    alarm.setRingtoneUri(alarmArrayList.get(i).getRingtoneUri());

                    if (position == i) {
                        if (isChecked) {
                            alarm.setAlarmON(true);
                        } else {
                            alarm.setAlarmON(false);
                        }
                    } else {
                        alarm.setAlarmON(alarmArrayList.get(i).isAlarmON());
                    }

//                    if (position == i) {
//                        alarm.setAlarmON(isChecked);
//                    }

                    strAlarmList.add(alarm.toJSON());
                }

                // Preference 에 저장
                PreferenceUtil.setJSONArrayPreference(context, Config.PREFERENCE_KEY.ALARM_LIST, strAlarmList);
                ((MainActivity) mainActivityContext).startDayOfWeekService();
            }*/
        });

        binding.rvAlarmList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        binding.rvAlarmList.setAdapter(alarmAdapter);

        binding.liAddAlarm02.setOnClickListener(this);
        binding.liAddAlarm.setOnClickListener(this);
        binding.liWeatherUpdate.setOnClickListener(this);
        binding.clWeatherRetry.setOnClickListener(this);

    }

    public void setUpUI() {
        changeColorLottieView();

        binding.clAlarmList.setVisibility(View.INVISIBLE);
        binding.clNone.setVisibility(View.INVISIBLE);
        binding.clWeatherRetry.setVisibility(View.GONE);

        if (hasPermission) {
            getWeatherData();
        } else {
            setWeatherData("61", "125");
            currentLocation = "서울특별시 강남구";
        }

        getAlarmList();
    }

    /* 사용자가 GPS 활성화 시켰는지 확인 */
    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void changeColorLottieView() {
        int lotteColor = ContextCompat.getColor(getContext(), R.color.white);
        SimpleColorFilter filter = new SimpleColorFilter(lotteColor);
        KeyPath keyPath = new KeyPath("**");
        LottieValueCallback<ColorFilter> callback = new LottieValueCallback<ColorFilter>(filter);
        binding.lottieView.addValueCallback(keyPath, LottieProperty.COLOR_FILTER, callback);
    }

    private void getWeatherData() {

        binding.clWeatherLoading.setVisibility(View.VISIBLE);
        binding.clWeatherRetry.setVisibility(View.GONE);
        binding.liWeatherUpdate.setVisibility(View.GONE);

        if (NetworkUtil.checkConnectedNetwork(context)) {

            getNxNy();

        } else {
            NetworkUtil.networkErrorDialogShow((MainActivity) mainActivityContext, false);
            binding.clWeatherLoading.setVisibility(View.GONE);
            binding.liWeatherUpdate.setVisibility(View.VISIBLE);
        }
    }

    private void setWeatherData(String nX, String nY) {
        LogUtil.e("Nx =" + nX + "Ny =" + nY);

        String todayDate = getTodayDate();
        String currentTime = getBaseTime();

        LogUtil.e("todayDate=" + todayDate + " currentTime=" + currentTime);

        if (currentTime.equals("2300")) {
            todayDate = getYesterday();
        }

        String finalTodayDate = todayDate;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> parameter = new HashMap<>();
                parameter.put("serviceKey", getResources().getString(R.string.api_key_public_data));
                parameter.put("dataType", "JSON");
                parameter.put("base_date", finalTodayDate);
                parameter.put("base_time", currentTime);
                parameter.put("nx", nX);
                parameter.put("ny", nY);
                parameter.put("pageNo", 1);
                parameter.put("numOfRows", 100);


                String response = getRequest(Config.URL_GET_VILLAGE_FCST, HttpRequest.HttpType.GET, parameter);
//                LogUtil.e(response);

                if (response.contains("HTTP_ERROR")) {

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            binding.clWeatherRetry.setVisibility(View.VISIBLE);
                            binding.clWeatherLoading.setVisibility(View.GONE);
                            binding.liWeatherUpdate.setVisibility(View.GONE);
                        }
                    });

                } else {

                    try {

                        JSONObject resultObject = new JSONObject(response);

                        JSONObject responseObject = resultObject.getJSONObject("response");
                        JSONObject headerObject = responseObject.getJSONObject("header");
                        JSONObject bodyObject = responseObject.getJSONObject("body");
                        JSONObject itemArrayObject = bodyObject.getJSONObject("items");

                        WeatherHeader header = new WeatherHeader();
                        header.setResultCode(headerObject.getString("resultCode"));
                        header.setResultMsg(headerObject.getString("resultMsg"));

                        LogUtil.e(header.getResultCode());

                        WeatherBody body = new WeatherBody();
                        body.setDataType(bodyObject.getString("dataType"));
                        body.setTotalCount(Integer.valueOf(bodyObject.getString("totalCount")));
                        body.setPageNo(Integer.valueOf(bodyObject.getString("pageNo")));
                        body.setNumOfRows(Integer.valueOf(bodyObject.getString("numOfRows")));

                        LogUtil.e("[" + body.getTotalCount() + "]");

                        JSONArray itemArray = itemArrayObject.getJSONArray("item");
//                        List<WeatherItem> weatherItems = new ArrayList<>();
//                        WeatherItems items = new WeatherItems();
//                        items.setItem(weatherItems);
//                        body.setItems(items);

                        /* 통신 성공 */
                        if (header.getResultCode().equals("00")) {

                            String resultSky = "";
                            String resultREH = "";
                            String resultPOP = "";
                            String resultPTY = "";
                            String resultTMP = "";

//                            LogUtil.e("todayDate= " + getTodayDate());
//                            LogUtil.e("baseTime= " + getBaseTime());

                            String splitTime = getBaseTime().substring(0, 2);
//                            LogUtil.e("splitTime= " + splitTime);

                            int searchTime = Integer.parseInt(splitTime) + 1;

                            String resultSearchTime = String.valueOf(searchTime) + "00";
                            LogUtil.e("resultSearchTime= " + resultSearchTime);

                            /* 24시일 때 표기법 수정 */
                            if (resultSearchTime.equals("2400")) {
                                resultSearchTime = "0000";
                            }

                            for (int i = 0; i < itemArray.length(); i++) {

                                JSONObject object = itemArray.getJSONObject(i);

                                if (object.getString("fcstDate").equals(getTodayDate()) && object.getString("fcstTime").contains(resultSearchTime)) {

                                    String category = object.getString("category");

                                    switch (category) {
                                        case "SKY":
                                            resultSky = object.getString("fcstValue");
                                            break;
                                        case "REH":
                                            resultREH = object.getString("fcstValue");
                                            break;
                                        case "POP":
                                            resultPOP = object.getString("fcstValue");
                                            break;
                                        case "PTY":
                                            resultPTY = object.getString("fcstValue");
                                            break;
                                        case "TMP":
                                            resultTMP = object.getString("fcstValue");
                                            break;
                                    }
                                }
                            }


                            /*for (int i = 0; i < itemArray.length(); i++) {

                                JSONObject object = itemArray.getJSONObject(i);

                                WeatherItem weatherItem = new WeatherItem();

                                weatherItem.setBaseDate(object.getString("baseDate"));
                                weatherItem.setBaseTime(object.getString("baseTime"));
                                weatherItem.setCategory(object.getString("category"));
                                weatherItem.setFcstDate(object.getString("fcstDate"));
                                weatherItem.setFcstTime(object.getString("fcstTime"));
                                weatherItem.setFcstValue(object.getString("fcstValue"));
                                weatherItem.setNx(object.getInt("nx"));
                                weatherItem.setNy(object.getInt("ny"));

                                weatherItems.add(weatherItem);
                            }*/

                            String finalResultREH = resultREH;
                            String finalResultPOP = resultPOP;
                            String finalResultTMP = resultTMP;


                            Drawable weatherResource = null;

                            /* 강수형태 */
                            switch (resultPTY) {
                                case "0": // 0 = 없음

                                    /* 하늘상태 */
                                    switch (resultSky) {
                                        case "1":
//                                                resultSky = "맑음";
                                            weatherResource = ContextCompat.getDrawable(context, R.drawable.ic_weather_sunny);
                                            break;
                                        case "3":
//                                                resultSky = "구름 많음";
                                            weatherResource = ContextCompat.getDrawable(context, R.drawable.ic_weather_cloudy);
                                            break;
                                        case "4":
//                                                resultSky = "흐림";
                                            weatherResource = ContextCompat.getDrawable(context, R.drawable.ic_weather_blur);
                                            break;
                                        default:
                                            weatherResource = ContextCompat.getDrawable(context, R.drawable.ic_weather_sunny);
                                            break;
                                    }

//                                        String finalResultSky = resultSky;
                                    break;

                                case "1":
//                                        resultPTY = "비";
                                    weatherResource = ContextCompat.getDrawable(context, R.drawable.ic_weather_rain);
                                    break;
                                case "2":
//                                        resultPTY = "비/눈";
                                    weatherResource = ContextCompat.getDrawable(context, R.drawable.ic_weather_rain_snow);
                                    break;
                                case "3":
//                                        resultPTY = "눈";
                                    weatherResource = ContextCompat.getDrawable(context, R.drawable.ic_weather_snow);
                                    break;
                                case "4":
//                                        resultPTY = "소나기";
                                    weatherResource = ContextCompat.getDrawable(context, R.drawable.ic_weather_shower);
                                    break;
                            }

                            Drawable finalResource = weatherResource;

                            LogUtil.d(currentLocation);
                            LogUtil.d(finalResultREH);
                            LogUtil.d(finalResultPOP);
                            LogUtil.d(finalResultTMP);

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    binding.tvLocation.setText(currentLocation);
//                                        binding.tvSky.setText(finalResultSky);
                                    binding.ivWeather.setImageDrawable(finalResource);
                                    binding.tvReh.setText("습도 : " + finalResultREH);
                                    binding.tvPop.setText("강수 확률 : " + finalResultPOP);
//                                        binding.tvPty.setText("강수 형태 : " + finalSetResultPTY);
                                    binding.tvTmp.setText("기온 : " + finalResultTMP);
                                }
                            });

                        } else {
                            /* 통신 실패 */
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "공공데이터 포털사이트 점검 중 입니다", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        LogUtil.e("파싱 에러");

                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                binding.clWeatherLoading.setVisibility(View.GONE);
                                binding.liWeatherUpdate.setVisibility(View.GONE);
                                binding.clWeatherRetry.setVisibility(View.VISIBLE);
                            }
                        }, 300);
                    }
                }

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.clWeatherLoading.setVisibility(View.GONE);
                        binding.liWeatherUpdate.setVisibility(View.VISIBLE);
                    }
                }, 500);
            }
        }).start();
    }

    private void getNxNy() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    binding.clWeatherRetry.setVisibility(View.VISIBLE);
                    binding.clWeatherLoading.setVisibility(View.GONE);
                    binding.liWeatherUpdate.setVisibility(View.GONE);
                }
            });
            return;
        }

        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                strNx = String.valueOf(TransLocationUtil.convertGRID_GPS(TransLocationUtil.TO_GRID, location.getLatitude(), location.getLongitude()).x).replace(".0", "");
                strNy = String.valueOf(TransLocationUtil.convertGRID_GPS(TransLocationUtil.TO_GRID, location.getLatitude(), location.getLongitude()).y).replace(".0", "");
                currentLocation = LocationUtil.changeForAddress(context, location.getLatitude(), location.getLongitude());

                String[] results = currentLocation.split("\\s");
                LogUtil.e("results[0]=" + results[0]);
                LogUtil.e("results[1]=" + results[1]);
                LogUtil.e("results[2]=" + results[2]);

                currentLocation = results[1] + " " + results[2];

                setWeatherData(strNx, strNy);
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1000, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 1000, locationListener);
    }

    private void getAlarmList() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                alarmArrayList = new ArrayList<>();

                /* 저장되어 있는 알람리스트 가져오기 */
                if (PreferenceUtil.getJSONArrayPreference(context, Config.PREFERENCE_KEY.ALARM_LIST) != null
                        && PreferenceUtil.getJSONArrayPreference(context, Config.PREFERENCE_KEY.ALARM_LIST).size() > 0) {

                    JSONArray jsonArray = new JSONArray(PreferenceUtil.getJSONArrayPreference(context, Config.PREFERENCE_KEY.ALARM_LIST));

                    try {

                        for (int i = 0; i < jsonArray.length(); i++) {
                            Alarm alarm = new Alarm();
                            JSONObject object = new JSONObject(jsonArray.getString(i));
                            alarm.setName(object.getString("name"));
                            alarm.setAmPm(object.getString("amPm"));
                            alarm.setHour(object.getString("hour"));
                            alarm.setMinute(object.getString("minute"));
                            alarm.setVolume(object.getInt("volume"));
                            alarm.setRingtoneName(object.getString("ringtoneName"));
                            alarm.setRingtoneUri(Uri.parse(object.getString("ringtoneUri")));
                            alarm.setDayOfWeek(object.getString("dayOfWeek"));
                            alarm.setAlarmON(object.getBoolean("alarmON"));

                            LogUtil.d("alarm /" + alarm.getName());

                            alarmArrayList.add(alarm);
                        }

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                alarmAdapter.addAll(alarmArrayList);
                                binding.clAlarmList.setVisibility(View.VISIBLE);
                                binding.clNone.setVisibility(View.GONE);
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            binding.clNone.setVisibility(View.VISIBLE);
                            binding.clAlarmList.setVisibility(View.GONE);

                            context.stopService(new Intent(MediApplication.ApplicationContext(), DayOfWeekCheckService.class));
                        }
                    });
                }
            }
        }).start();
    }

    private String getYesterday() {
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd");
        calendar.add(Calendar.DATE, -1);
        String chkDate = SDF.format(calendar.getTime());

        return chkDate;
    }

    private String getTodayDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

        return dateFormat.format(date);
    }

    private String getBaseTime() {

        /* 단기예보 base_time */
//        Base_time : 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 (1일 8회)

        String baseTime = "";

        /* 현재시간과 비교해서 baseTime 요청 값 결정  */
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("kk");

        LogUtil.e(dateFormat.format(date) + "777777777777");

        switch (dateFormat.format(date)) {
            case "24":
            case "01":
            case "02":
                baseTime = "2300";
                break;
            case "03":
            case "04":
            case "05":
                baseTime = "0200";
                break;
            case "06":
            case "07":
            case "08":
                baseTime = "0500";
                break;
            case "09":
            case "10":
            case "11":
                baseTime = "0800";
                break;
            case "12":
            case "13":
            case "14":
                baseTime = "1100";
                break;
            case "15":
            case "16":
            case "17":
                baseTime = "1400";
                break;
            case "18":
            case "19":
            case "20":
                baseTime = "1700";
                break;
            case "21":
            case "22":
            case "23":
                baseTime = "2000";
                break;
        }
        return baseTime;
    }

    private void permissionRequestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("필수권한")
                .setMessage("현재 위치의 날씨정보를 얻기 위해\n위치권한을 허용해주세요")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + context.getPackageName()));
                        context.startActivity(intent);
                    }
                });
        builder.show();
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
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("필수권한")
                        .setMessage("현재 위치의 날씨정보를 얻기 위해\n위치권한을 허용해주세요")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                ((MainActivity) mainActivityContext).gpsPermissionRequest.launch(arrGpsPermissions);
                            }
                        });
                builder.show();

                break;
        }
    }

    private void checkPermission() {

//        권한체크
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionResultAction(((MainActivity) mainActivityContext).gpsPermissionCheck());

            hasPermission = false;
            LogUtil.e("권한 없음");
        } else {

//            권한 획득한 사용자는 GPS 활성화 했는지 체크
            if (checkLocationServicesStatus()) {

                hasPermission = true;
                LogUtil.e("GPS ON");
                getWeatherData();

            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("위치서비스");
                builder.setMessage("위치서비스를 활성화 해주세요");
                builder.setCancelable(false);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });

                builder.show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.li_add_alarm_02:
            case R.id.li_add_alarm:
                Intent intent = new Intent(context, AddAlarmActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(intent);
                break;
            case R.id.li_weather_update:
            case R.id.cl_weather_retry:
                checkPermission();
                break;
        }
    }
}
