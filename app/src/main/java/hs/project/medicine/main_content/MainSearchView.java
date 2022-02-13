package hs.project.medicine.main_content;

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


        initRecyclerView(context);

        binding.nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    Log.e("hs", "제일하단");

                    binding.pagingProgressBar.setVisibility(View.VISIBLE);

                    if (numOfRows < 100) {
                        numOfRows = numOfRows + 20;
                    } else {
                        pageNo++;
                        numOfRows = 20;
                    }

                    if (binding.etSearchCompany.getText().length() > 0 || binding.etSearchMedicine.getText().length() > 0) {
                        getData(binding.etSearchCompany.getText().toString(), binding.etSearchMedicine.getText().toString(), pageNo, numOfRows);
                    } else {
                        getData("", "", pageNo, numOfRows);
                    }

                }
            }
        });

        binding.btnSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * 검색 버튼 누를 때 pageNo, numOfRows, list 초기화
                 */
                pageNo = 1;
                numOfRows = 20;
                itemArrayList = new ArrayList<>();

                binding.clLoading.setVisibility(View.VISIBLE);
                getData(binding.etSearchCompany.getText().toString(), binding.etSearchMedicine.getText().toString(), pageNo, numOfRows);
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


                    if (bodyObject.has("items")) {
                        itemArray = bodyObject.getJSONArray("items");
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

//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                AlertDialog.Builder builder = new AlertDialog.Builder(SearchMedicineActivity.this);
//                                builder.setTitle("검색");
//                                builder.setMessage("검색된 데이터가 없습니다");
//                                builder.setCancelable(false);
//                                builder.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                });
//                                builder.show();
//                            }
//                        });
                    }

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

                    Log.e("body", medicineBody.toString());

                    // totalCnt - 현재리스트 > 0 클 때만 데이터 가져오도록
                    totalCnt = totalCnt - itemArrayList.size();

                    Log.e("totalCnt", totalCnt + "");

                    if (totalCnt > 0) {
                        for (int i = 0; i < totalCnt; i++) {
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
                                new AlertDialog.Builder(context)
                                        .setTitle("검색결과")
                                        .setMessage("더이상 검색할 데이터가 없습니다")
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                            }
                        }, 1000);

//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                new AlertDialog.Builder(SearchMedicineActivity.this)
//                                        .setTitle("검색결과")
//                                        .setMessage("더이상 검색할 데이터가 없습니다")
//                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                dialog.dismiss();
//                                            }
//                                        }).show();
//                            }
//                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // UI 변경이 필요할 때 메인스레드 사용.
                        binding.clLoading.setVisibility(View.GONE);
                        medicineAdapter.addAll(itemArrayList);
                        binding.pagingProgressBar.setVisibility(View.GONE);

                        binding.tvCurrentSearch.setText("업체 검색명 : " + searchCompany + "\n제품 검색명 : " + searchMedicine + "\n조회 데이터 갯수(" + itemArrayList.size() + "/" + displaySearchTotalCnt + ")");

                        Log.e("current_list", itemArrayList.size() + "");
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
