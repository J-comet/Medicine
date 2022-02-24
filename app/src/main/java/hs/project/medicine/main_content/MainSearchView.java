package hs.project.medicine.main_content;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static hs.project.medicine.HttpRequest.getRequest;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hs.project.medicine.Config;
import hs.project.medicine.HttpRequest;
import hs.project.medicine.R;
import hs.project.medicine.adapter.MedicineAdapter;
import hs.project.medicine.databinding.LayoutMainSearchViewBinding;
import hs.project.medicine.datas.medicine.MedicineBody;
import hs.project.medicine.datas.medicine.MedicineHeader;
import hs.project.medicine.datas.medicine.MedicineItem;
import hs.project.medicine.util.LogUtil;

public class MainSearchView extends ConstraintLayout {
    private LayoutMainSearchViewBinding binding;
    private Context context;

    private int pageNo = 1;   // pageNum
    private int numOfRows = 20;  // 몇개씩 보여줄지
    private String type = "json";  // 전달 받을 type
//    private String entpName = "";  // 회사 이름
//    private String itemName = "";  // 약 이름

    private int totalCnt = -1;  // total 조회 값
    private String displaySearchTotalCnt = ""; // 화면에 표시될 현재 데이터 총 갯수

    private Runnable runnable;

    private ArrayList<MedicineItem> itemArrayList;
    private MedicineAdapter medicineAdapter;

    private InputMethodManager inputMethodManager;

    public MainSearchView(@NonNull Context context) {
        super(context);
        this.context = context;
        initView(context);
    }

    public MainSearchView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
    }

    public MainSearchView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView(context);
    }

    public MainSearchView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initView(Context context) {
        binding = LayoutMainSearchViewBinding.inflate(LayoutInflater.from(context), this, true);
        binding.clLoading.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // ignore all touch events
                return true;
            }
        });

        inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);

        initRecyclerView(context);

        binding.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView nestedScrollView, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1) != null) {
                    if ((scrollY >= (nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1).getMeasuredHeight() - nestedScrollView.getMeasuredHeight()))
                            && scrollY > oldScrollY) {
                        LogUtil.d("BOTTOM");

                        binding.pagingProgressBar.setVisibility(View.VISIBLE);

                        /* 터치 차단 */
                        binding.nestedScrollView.setOnTouchListener(new OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                return true;
                            }
                        });

                        pageNo++;

                        LogUtil.d("pageNo = " + pageNo);

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (binding.etSearchCompany.getText().length() > 0 || binding.etSearchMedicine.getText().length() > 0) {
                                    getData(binding.etSearchCompany.getText().toString().trim(), binding.etSearchMedicine.getText().toString().trim(), pageNo, numOfRows);

                                    /* 터치 가능 */
                                    binding.nestedScrollView.setOnTouchListener(new OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {
                                            return false;
                                        }
                                    });

                                } else {
                                    getData("", "", pageNo, numOfRows);
                                }
                            }
                        });

                    }

                }


                /*if (scrollY == nestedScrollView.getChildAt(0).getMeasuredHeight() - nestedScrollView.getMeasuredHeight()) {
                    Log.e("hs", "제일하단");


                }*/
            }
        });


        binding.btnSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * 검색 버튼 누를 때 pageNo, list 초기화
                 */
                pageNo = 1;

                itemArrayList = new ArrayList<>();

                /* 키보드 hide */
                inputMethodManager.hideSoftInputFromWindow(binding.etSearchMedicine.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                inputMethodManager.hideSoftInputFromWindow(binding.etSearchCompany.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                binding.clLoading.setVisibility(View.VISIBLE);
                getData(binding.etSearchCompany.getText().toString().trim(), binding.etSearchMedicine.getText().toString().trim(), pageNo, numOfRows);
            }
        });
    }

    private void initRecyclerView(Context context) {
        itemArrayList = new ArrayList<>();
        medicineAdapter = new MedicineAdapter(context);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        binding.rvMedicine.setLayoutManager(layoutManager);
        binding.rvMedicine.setAdapter(medicineAdapter);

//        binding.clLoading.setVisibility(View.VISIBLE);

    }

    private void getData(String searchCompany, String searchMedicine, int pageNo, int numOfRows) {

        runnable = new Runnable() {
            @Override
            public void run() {

                Log.e("hs", "current pageNo:" + pageNo + "current numOfRows" + numOfRows);

                Map<String, Object> parameter = new HashMap<>();
                parameter.put("serviceKey", getResources().getString(R.string.api_key_public_data));
                parameter.put("pageNo", pageNo);
                parameter.put("numOfRows", numOfRows);
                parameter.put("type", type);
                parameter.put("entpName", searchCompany);
                parameter.put("itemName", searchMedicine);

                String response = getRequest(Config.URL_GET_EASY_DRUG, HttpRequest.HttpType.GET, parameter);
                Log.e("result", response);

                try {
                    JSONObject resultObject = new JSONObject(response);
                    Log.e("resultObject.toString()", resultObject.toString());

                    JSONObject headerObject = resultObject.getJSONObject("header");
                    JSONObject bodyObject = resultObject.getJSONObject("body");
                    JSONArray itemArray = null;

                    MedicineHeader medicineHeader = new MedicineHeader();
                    medicineHeader.setResultCode(headerObject.getString("resultCode"));
                    medicineHeader.setResultMsg(headerObject.getString("resultMsg"));

                    Log.e("header", medicineHeader.toString());

                    MedicineBody medicineBody = new MedicineBody();
                    medicineBody.setTotalCount(Integer.valueOf(bodyObject.getString("totalCount")));
                    medicineBody.setNumOfRows(Integer.valueOf(bodyObject.getString("numOfRows")));
                    medicineBody.setPageNo(Integer.valueOf(bodyObject.getString("pageNo")));

                    totalCnt = medicineBody.getTotalCount();
                    displaySearchTotalCnt = String.valueOf(totalCnt);

                    if (bodyObject.has("items")) {
                        itemArray = bodyObject.getJSONArray("items");


                        for (int i = 0; i < itemArray.length(); i++) {
                            JSONObject item = itemArray.getJSONObject(i);
                            MedicineItem arrItem = new MedicineItem();
                            arrItem.setEntpName(item.getString("entpName"));
                            arrItem.setItemName(item.getString("itemName"));
                            arrItem.setItemSeq(item.getString("itemSeq"));
                            arrItem.setEfcyQesitm(item.getString("efcyQesitm"));
                            arrItem.setUseMethodQesitm(item.getString("useMethodQesitm"));
                            arrItem.setAtpnQesitm(item.getString("atpnQesitm"));
                            arrItem.setIntrcQesitm(item.getString("intrcQesitm"));
                            arrItem.setSeQesitm(item.getString("seQesitm"));
                            arrItem.setDepositMethodQesitm(item.getString("depositMethodQesitm"));
                            arrItem.setOpenDe(item.getString("openDe"));
                            arrItem.setUpdateDe(item.getString("updateDe"));
                            arrItem.setAtpnWarnQesitm(item.getString("atpnWarnQesitm"));
                            arrItem.setItemImage(item.getString("itemImage"));

                            Log.e("arrItem", arrItem.toString());
                            itemArrayList.add(arrItem);
                        }


                    } else {

                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("검색");
                                builder.setMessage("검색된 데이터가 없습니다");
                                builder.setCancelable(false);
                                builder.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                            }
                        }, 1000);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "알수없는 오류가 발생했습니다.\n다시 시도해주세요",Toast.LENGTH_LONG).show();
                        }
                    });

                }

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // UI 변경이 필요할 때 메인스레드 사용.
                        binding.clLoading.setVisibility(View.GONE);
                        medicineAdapter.addAll(itemArrayList);
                        binding.pagingProgressBar.setVisibility(View.GONE);

                        binding.tvCurrentSearch.setText("업체 검색명 : " + searchCompany + "\n제품 검색명 : " + searchMedicine + "\n조회 데이터 갯수(" + itemArrayList.size() + "/" + displaySearchTotalCnt + ")");

                        LogUtil.e("current_list="+ itemArrayList.size() + "");
                    }
                }, 1000);

//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        // UI 변경이 필요할 때 메인스레드 사용.
//                        binding.clLoading.setVisibility(View.GONE);
//                        medicineAdapter.addAll(itemArrayList);
//                        binding.pagingProgressBar.setVisibility(View.GONE);
//
//                        binding.tvCurrentSearch.setText("업체 검색명 : " + searchCompany + "\n제품 검색명 : " + searchMedicine + "\n조회 데이터 갯수(" + itemArrayList.size() + "/" + displaySearchTotalCnt + ")");
//
//                        Log.e("current_list", itemArrayList.size() + "");
//                    }
//                });

            }
        };
        new Thread(runnable).start();
    }


}
