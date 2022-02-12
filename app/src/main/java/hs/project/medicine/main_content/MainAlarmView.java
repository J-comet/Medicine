package hs.project.medicine.main_content;

import static hs.project.medicine.HttpRequest.getRequest;
import static hs.project.medicine.activitys.MainActivity.mainActivityContext;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
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
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
import hs.project.medicine.activitys.AddAlarmActivity;
import hs.project.medicine.activitys.MainActivity;
import hs.project.medicine.adapter.AlarmAdapter;
import hs.project.medicine.databinding.LayoutMainAlarmViewBinding;
import hs.project.medicine.datas.Alarm;
import hs.project.medicine.datas.Pharmacy;
import hs.project.medicine.dialog.ModifyAlarmDialog;
import hs.project.medicine.util.LogUtil;
import hs.project.medicine.util.NetworkUtil;
import hs.project.medicine.util.PreferenceUtil;

public class MainAlarmView extends ConstraintLayout implements View.OnClickListener {
    private LayoutMainAlarmViewBinding binding;
    private Context context;

    private ArrayList<Alarm> alarmArrayList;
    private AlarmAdapter alarmAdapter;
    private ArrayList<String> strAlarmList;

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
    }

    public void setUpUI() {
        changeColorLottieView();

        binding.clWeatherLoading.setVisibility(View.VISIBLE);
        binding.clAlarmList.setVisibility(View.INVISIBLE);
        binding.clNone.setVisibility(View.INVISIBLE);

        getWeatherData();
        getAlarmList();



    }

    private void changeColorLottieView() {
        int lotteColor = ContextCompat.getColor(getContext(),R.color.white);
        SimpleColorFilter filter = new SimpleColorFilter(lotteColor);
        KeyPath keyPath = new KeyPath("**");
        LottieValueCallback<ColorFilter> callback = new LottieValueCallback<ColorFilter>(filter);
        binding.lottieView.addValueCallback(keyPath, LottieProperty.COLOR_FILTER, callback);
    }

    private void getWeatherData() {

        String todayDate = "20220212";
        String currentTime = "1400";

        String strNx = "55";
        String strNy = "127";

        if (NetworkUtil.checkConnectedNetwork(context)) {

            Runnable runnable = new Runnable() {
                @Override
                public void run() {

                    Map<String, Object> parameter = new HashMap<>();
                    parameter.put("serviceKey", getResources().getString(R.string.api_key_easy_drug));
                    parameter.put("dataType", "JSON");
                    parameter.put("base_date", todayDate);
                    parameter.put("base_time", currentTime);
                    parameter.put("nx", strNx);
                    parameter.put("ny", strNy);
                    parameter.put("pageNo", 1);
                    parameter.put("numOfRows", 1000);


                    String response = getRequest(Config.URL_GET_VILLAGE_FCST, HttpRequest.HttpType.GET, parameter);
                    LogUtil.e(response);


                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.clWeatherLoading.setVisibility(View.GONE);
                        }
                    }, 1500);

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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cl_none:
            case R.id.li_add_alarm:
                Intent intent = new Intent(context, AddAlarmActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(intent);
                break;
        }
    }
}
