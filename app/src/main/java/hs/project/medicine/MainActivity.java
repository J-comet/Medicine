package hs.project.medicine;

import static hs.project.medicine.HttpRequest.getRequest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hs.project.medicine.datas.Body;
import hs.project.medicine.datas.Header;
import hs.project.medicine.datas.Item;
import hs.project.medicine.datas.MedicineResponse;

public class MainActivity extends AppCompatActivity {

    private ConstraintLayout clLoading;
    private LinearLayout btnSearch;
    private EditText etSearchMedicine, etSearchCompany;
    private RecyclerView rvMedicine;
    private NestedScrollView nestedScrollView;
    private ProgressBar pbPaging;
    private TextView tvCurrentSearch;

    private String url = "http://apis.data.go.kr/1471000/DrbEasyDrugInfoService/getDrbEasyDrugList";
    private String key = "";
    private int pageNo = 1;   // pageNum
    private int numOfRows = 20;  // 몇개씩 보여줄지
    private String type = "json";  // 전달 받을 type
//    private String entpName = "";  // 회사 이름
//    private String itemName = "";  // 약 이름

    private int totalCnt = -1;  // total 조회 값
    private String displaySearchTotalCnt = ""; // 화면에 표시될 현재 데이터 총 갯수

    private Runnable runnable;

    private ArrayList<Item> itemArrayList;
    private MedicineAdapter medicineAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

//        entpName = etSearchCompany.getText().toString();
//        itemName = etSearchMedicine.getText().toString();
    }

    private void init() {

        btnSearch = findViewById(R.id.btn_search);
        etSearchMedicine = findViewById(R.id.et_search_medicine);
        etSearchCompany = findViewById(R.id.et_search_company);
        rvMedicine = findViewById(R.id.rv_medicine);
        clLoading = findViewById(R.id.cl_loading);
        nestedScrollView = findViewById(R.id.nested_scroll_view);
        pbPaging = findViewById(R.id.paging_progress_bar);
        tvCurrentSearch = findViewById(R.id.tv_current_search);

        itemArrayList = new ArrayList<>();
        medicineAdapter = new MedicineAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvMedicine.setLayoutManager(layoutManager);
        rvMedicine.setAdapter(medicineAdapter);

        clLoading.setVisibility(View.VISIBLE);
        getData("", "", pageNo, numOfRows);


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * 검색 버튼 누를 때 pageNo, numOfRows, list 초기화
                 */

                pageNo = 1;
                numOfRows = 20;
                itemArrayList = new ArrayList<>();

                clLoading.setVisibility(View.VISIBLE);
                getData(etSearchCompany.getText().toString(), etSearchMedicine.getText().toString(), pageNo, numOfRows);
            }
        });

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    Log.e("hs", "제일하단");
//                    pageNo++;
//                    numOfRows = numOfRows + 20;
                    pbPaging.setVisibility(View.VISIBLE);

                    if (numOfRows < 100) {
                        numOfRows = numOfRows + 20;
                    } else {
                        pageNo++;
                        numOfRows = 20;
                    }

                    if (etSearchCompany.getText().length() > 0 || etSearchMedicine.getText().length() > 0) {
                        getData(etSearchCompany.getText().toString(), etSearchMedicine.getText().toString(), pageNo, numOfRows);
                    } else {
                        getData("", "", pageNo, numOfRows);
                    }

                }
            }
        });


    }

    private void getData(String searchCompany, String searchMedicine, int pageNo, int numOfRows) {

        runnable = new Runnable() {
            @Override
            public void run() {

                Log.e("hs", "current pageNo:" + pageNo + "current numOfRows" + numOfRows);

                Map<String, Object> parameter = new HashMap<>();
                parameter.put("serviceKey", key);
                parameter.put("pageNo", pageNo);
                parameter.put("numOfRows", numOfRows);
                parameter.put("type", type);
                parameter.put("entpName", searchCompany);
                parameter.put("itemName", searchMedicine);

                String response = getRequest(url, HttpRequest.HttpType.GET, parameter);
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

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                        });
                    }

                    Header header = new Header();
                    header.setResultCode(headerObject.getString("resultCode"));
                    header.setResultMsg(headerObject.getString("resultMsg"));

                    Log.e("header", header.toString());

                    Body body = new Body();
                    body.setTotalCount(Integer.valueOf(bodyObject.getString("totalCount")));
                    body.setNumOfRows(Integer.valueOf(bodyObject.getString("numOfRows")));
                    body.setPageNo(Integer.valueOf(bodyObject.getString("pageNo")));

                    totalCnt = body.getTotalCount();
                    displaySearchTotalCnt = String.valueOf(totalCnt);

                    Log.e("body", body.toString());

                    // totalCnt - 현재리스트 > 0 클 때만 데이터 가져오도록
                    totalCnt = totalCnt - itemArrayList.size();

                    Log.e("totalCnt", totalCnt + "");

                    if (totalCnt > 0) {
                        for (int i = 0; i < totalCnt; i++) {
                            JSONObject item = itemArray.getJSONObject(i);
                            Item arrItem = new Item();
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

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("검색결과")
                                        .setMessage("더이상 검색할 데이터가 없습니다")
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    public void run() {
                        // UI 변경이 필요할 때 메인스레드 사용.
                        clLoading.setVisibility(View.GONE);
                        medicineAdapter.addAll(itemArrayList);
                        pbPaging.setVisibility(View.GONE);

                        tvCurrentSearch.setText("업체명 : " + searchCompany + "\n제품명 : " + searchMedicine + "\n(" + itemArrayList.size() + "/" + displaySearchTotalCnt + ")");

                        Log.e("current_list", itemArrayList.size() + "");
                    }
                });

            }
        };
        new Thread(runnable).start();
    }
}