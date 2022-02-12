package hs.project.medicine.main_content;

import static android.content.Context.LOCATION_SERVICE;
import static hs.project.medicine.HttpRequest.getRequest;
import static hs.project.medicine.activitys.MainActivity.mainActivityContext;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.SimpleColorFilter;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieValueCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import hs.project.medicine.datas.weather.WeatherHeader;
import hs.project.medicine.datas.weather.WeatherResponse;
import hs.project.medicine.dialog.ModifyAlarmDialog;
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

    private String strNxNy = "null";
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location location;

    private String strNx = "";
    private String strNy = "";

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

        binding.clNone.setOnClickListener(this);
        binding.liAddAlarm.setOnClickListener(this);
        binding.tvWeatherUpdate.setOnClickListener(this);

        binding.clWeatherLoading.setVisibility(View.VISIBLE);

        getWeatherData();
    }

    public void setUpUI() {
        changeColorLottieView();

        binding.clAlarmList.setVisibility(View.INVISIBLE);
        binding.clNone.setVisibility(View.INVISIBLE);

        getAlarmList();
    }

    private void changeColorLottieView() {
        int lotteColor = ContextCompat.getColor(getContext(), R.color.white);
        SimpleColorFilter filter = new SimpleColorFilter(lotteColor);
        KeyPath keyPath = new KeyPath("**");
        LottieValueCallback<ColorFilter> callback = new LottieValueCallback<ColorFilter>(filter);
        binding.lottieView.addValueCallback(keyPath, LottieProperty.COLOR_FILTER, callback);
    }

    private void getWeatherData() {

        getNxNy();

        LogUtil.e("Nx ="+ strNx + "Ny =" + strNy);

        String todayDate = getTodayDate();
        String currentTime = getBaseTime();
        String nX = strNx;
        String nY = strNy;

        LogUtil.e("todayDate=" + todayDate + " currentTime=" + currentTime);

        if (NetworkUtil.checkConnectedNetwork(context)) {

            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    Map<String, Object> parameter = new HashMap<>();
                    parameter.put("serviceKey", getResources().getString(R.string.api_key_easy_drug));
                    parameter.put("dataType", "JSON");
                    parameter.put("base_date", todayDate);
                    parameter.put("base_time", currentTime);
                    parameter.put("nx", nX);
                    parameter.put("ny", nY);
                    parameter.put("pageNo", 1);
                    parameter.put("numOfRows", 1000);


                    String response = getRequest(Config.URL_GET_VILLAGE_FCST, HttpRequest.HttpType.GET, parameter);
                    LogUtil.e(response);

                    try {
                        JSONObject resultObject = new JSONObject(response);
                        LogUtil.e("resultObject.toString() =" + resultObject.toString());

                        JSONObject headerObject = resultObject.getJSONObject("header");
//                        JSONObject bodyObject = resultObject.getJSONObject("body");

                        WeatherHeader header = new WeatherHeader();
                        header.setResultCode(headerObject.getString("resultCode"));
                        header.setResultMsg(headerObject.getString("resultMsg"));

                        LogUtil.e("header =" + header.toString());

                        /* 통신 성공 */
                        if (header.getResultCode().equals("00")) {

                        } else {
                            /* 통신 실패 */
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "현재 공공데이터 포털사이트 점검 중", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.clWeatherLoading.setVisibility(View.GONE);
                        }
                    }, 500);

//                    _MAIN_ACTIVITY.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            binding.clWeatherLoading.setVisibility(View.GONE);
//                        }
//                    });

                }
            };
            new Thread(runnable).start();

        } else {
            NetworkUtil.networkErrorDialogShow((MainActivity) mainActivityContext, false);
            binding.clWeatherLoading.setVisibility(View.GONE);
        }
    }

    @SuppressLint("MissingPermission")
    private void getNxNy() {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        strNx = String.valueOf(TransLocationUtil.convertGRID_GPS(TransLocationUtil.TO_GRID, location.getLatitude(), location.getLongitude()).x).replace(".0", "");
        strNy = String.valueOf(TransLocationUtil.convertGRID_GPS(TransLocationUtil.TO_GRID, location.getLatitude(), location.getLongitude()).y).replace(".0", "");
    }

    private void getAlarmList() {
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

                alarmAdapter.addAll(alarmArrayList);
                binding.clAlarmList.setVisibility(View.VISIBLE);
                binding.clNone.setVisibility(View.GONE);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            binding.clNone.setVisibility(View.VISIBLE);
            binding.clAlarmList.setVisibility(View.GONE);
        }
    }

    /*private void readExcel(String localName) {

        try {
            InputStream is = context.getResources().getAssets().open("weather_location_info.xlsx");
            Workbook wb = Workbook.getWorkbook(is);

            if (wb != null) {
                Sheet sheet = wb.getSheet(0);   // 시트 불러오기
                if (sheet != null) {
                    int colTotal = sheet.getColumns();    // 전체 컬럼
                    int rowIndexStart = 1;                  // row 인덱스 시작
                    int rowTotal = sheet.getColumn(colTotal - 1).length;

                    for (int row = rowIndexStart; row < rowTotal; row++) {
                        String contents = sheet.getCell(0, row).getContents();

                        if (contents.contains(localName)) {
                            strNx = sheet.getCell(1, row).getContents();
                            strNy = sheet.getCell(2, row).getContents();
                            row = rowTotal;
                        }
                    }
                }
            }
        } catch (IOException e) {
            LogUtil.d("READ_EXCEL1 = " + e.getMessage());
            e.printStackTrace();
        } catch (BiffException e) {
            LogUtil.d("READ_EXCEL1 = " + e.getMessage());
            e.printStackTrace();
        }

        LogUtil.e("격자값/ x = " + strNx + "  y = " + strNy);
    }*/

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

        switch (dateFormat.format(date)) {
            case "00":
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cl_none:
            case R.id.li_add_alarm:
                Intent intent = new Intent(context, AddAlarmActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(intent);
                break;
            case R.id.tv_weather_update:
                Toast.makeText(context, "날씨 업데이트", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
