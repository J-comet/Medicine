package hs.project.medicine;

import static hs.project.medicine.HttpRequest.getRequest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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

    private String url = "http://apis.data.go.kr/1471000/DrbEasyDrugInfoService/getDrbEasyDrugList";
    private String key = "";
    private int pageNo = 1;   // pageNum
    private int numOfRows = 20;  // 몇개씩 보여줄지
    private String type = "json";  // 전달 받을 type
//    private String entpName = "";  // 회사 이름
//    private String itemName = "";  // 약 이름

    private int totalCnt = -1;  // total 조회 값

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

        itemArrayList = new ArrayList<>();
        medicineAdapter = new MedicineAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvMedicine.setLayoutManager(layoutManager);
        rvMedicine.setAdapter(medicineAdapter);


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
                new Thread(runnable).start();
            }
        });

        runnable = new Runnable() {
            @Override
            public void run() {

                Log.e("hs", "current pageNo:" + pageNo + "current numOfRows" + numOfRows);

                Map<String, Object> parameter = new HashMap<>();
                parameter.put("serviceKey", key);
                parameter.put("pageNo", pageNo);
                parameter.put("numOfRows", numOfRows);
                parameter.put("type", type);
                parameter.put("entpName", etSearchCompany.getText().toString());
                parameter.put("itemName", etSearchMedicine.getText().toString());

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

                    Log.e("body", body.toString());

                    /**
                     * total_count 로 해야됨.
                     * 아니면 다른 값들도 같이 출력
                     *
                     * 만약 total_count 가 많다면
                     *
                     */
                    if (body.getTotalCount() > 0) {
                        for (int i = 0; i < body.getTotalCount(); i++) {
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
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    public void run() {
                        // UI 변경이 필요할 때 메인스레드 사용.
                        clLoading.setVisibility(View.GONE);
                        medicineAdapter.addAll(itemArrayList);
                    }
                });

            }
        }

        ;
    }
}