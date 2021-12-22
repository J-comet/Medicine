package hs.project.medicine.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import org.jsoup.Jsoup;

import hs.project.medicine.R;
import hs.project.medicine.datas.MedicineItem;

public class MedicineDetailActivity extends AppCompatActivity {

    private MedicineItem medicineItem;

    private LinearLayout btnBack;
    private ImageView ivImg;
    private TextView tvEntpName;
    private TextView tvItemName;
    private TextView tvItemSeq;
    private TextView tvEfcyQesitm;
    private TextView tvUseMethodQesitm;
    private TextView tvAtpnWarnQesitm;
    private TextView tvAtpnQesitm;
    private TextView tvIntrcQesitm;
    private TextView tvSeQesitm;
    private TextView tvDepositMethodQesitm;
    private TextView tvOpenDe;
    private TextView tvUpdateDe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_detail);

        init();
        setData();

    }

    private void init() {
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ivImg = findViewById(R.id.iv_item_img);
        tvEntpName = findViewById(R.id.tv_entp_name);
        tvItemName = findViewById(R.id.tv_item_name);
        tvItemSeq = findViewById(R.id.tv_item_seq);
        tvEfcyQesitm = findViewById(R.id.tv_efcy_qesitm);
        tvUseMethodQesitm = findViewById(R.id.tv_use_method_qesitm);
        tvAtpnWarnQesitm = findViewById(R.id.tv_atpn_warn_qesitm);
        tvAtpnQesitm = findViewById(R.id.tv_atpn_qesitm);
        tvIntrcQesitm = findViewById(R.id.tv_intrc_qesitm);
        tvSeQesitm = findViewById(R.id.tv_se_qesitm);
        tvDepositMethodQesitm = findViewById(R.id.tv_deposit_method_qesitm);
        tvOpenDe = findViewById(R.id.tv_open_de);
        tvUpdateDe = findViewById(R.id.tv_update_de);
    }

    private void setData() {
        medicineItem = (MedicineItem) getIntent().getSerializableExtra("item");

        if (medicineItem.getEntpName() != null && !medicineItem.getEntpName().equals("null")) {
            Log.e("hs", medicineItem.getEntpName());
            tvEntpName.setText(medicineItem.getEntpName());
        }

        if (medicineItem.getItemName() != null && !medicineItem.getItemName().equals("null")) {
            Log.e("hs", medicineItem.getItemName());
            tvItemName.setText(medicineItem.getItemName());
        }

        if (medicineItem.getItemSeq() != null && !medicineItem.getItemSeq().equals("null")) {
            Log.e("hs", medicineItem.getItemSeq());
            tvItemSeq.setText(medicineItem.getItemSeq());
        }

        if (medicineItem.getEfcyQesitm() != null && !medicineItem.getEfcyQesitm().equals("null")) {
            Log.e("hs", medicineItem.getEfcyQesitm());
//            Document document = Jsoup.parse(medicineItem.getEfcyQesitm());
//            String text = new HtmlToPlainText().getPlainText(document);
            tvEfcyQesitm.setText(Jsoup.parse(medicineItem.getEfcyQesitm()).wholeText());
        }

        if (medicineItem.getUseMethodQesitm() != null && !medicineItem.getUseMethodQesitm().equals("null")) {
            Log.e("hs", medicineItem.getUseMethodQesitm());
            tvUseMethodQesitm.setText(Jsoup.parse(medicineItem.getUseMethodQesitm()).wholeText());
        }

        if (medicineItem.getAtpnWarnQesitm() != null && !medicineItem.getAtpnWarnQesitm().equals("null")) {
            Log.e("hs", medicineItem.getAtpnWarnQesitm().toString());
            tvAtpnWarnQesitm.setText(medicineItem.getAtpnWarnQesitm().toString());
//            tvAtpnWarnQesitm.setText(Jsoup.parse(medicineItem.getAtpnWarnQesitm()).wholeText());
        }

        if (medicineItem.getAtpnQesitm() != null && !medicineItem.getAtpnQesitm().equals("null")) {
            Log.e("hs", medicineItem.getAtpnQesitm());
            tvAtpnQesitm.setText(Jsoup.parse(medicineItem.getAtpnQesitm()).wholeText());
        }

        if (medicineItem.getIntrcQesitm() != null && !medicineItem.getIntrcQesitm().equals("null")) {
            Log.e("hs", medicineItem.getIntrcQesitm());
            tvIntrcQesitm.setText(Jsoup.parse(medicineItem.getIntrcQesitm()).wholeText());
        }

        if (medicineItem.getSeQesitm() != null && !medicineItem.getSeQesitm().equals("null")) {
            Log.e("hs", medicineItem.getSeQesitm());
            tvSeQesitm.setText(Jsoup.parse(medicineItem.getSeQesitm()).wholeText());
        }

        if (medicineItem.getDepositMethodQesitm() != null && !medicineItem.getDepositMethodQesitm().equals("null")) {
            Log.e("hs", medicineItem.getDepositMethodQesitm());
            tvDepositMethodQesitm.setText(Jsoup.parse(medicineItem.getDepositMethodQesitm()).wholeText());
        }

        if (medicineItem.getOpenDe() != null && !medicineItem.getOpenDe().equals("null")) {
            Log.e("hs", medicineItem.getOpenDe());
            String openDate = medicineItem.getOpenDe().replaceAll("-","");
            String openYear = openDate.substring(0,4);
            String openMonth = openDate.substring(4,6);
            String openDay = openDate.substring(6,8);

            tvOpenDe.setText(openYear + "." + openMonth + "." + openDay);
        }

        if (medicineItem.getUpdateDe() != null && !medicineItem.getUpdateDe().equals("null")) {
            Log.e("hs", medicineItem.getUpdateDe());

            String openYear = medicineItem.getUpdateDe().substring(0,4);
            String openMonth = medicineItem.getUpdateDe().substring(4,6);
            String openDay = medicineItem.getUpdateDe().substring(6,8);

            tvUpdateDe.setText(openYear + "." + openMonth + "." + openDay);
        }

        if (medicineItem.getItemImage() != null && !medicineItem.getItemImage().equals("null")) {
            Log.e("hs", medicineItem.getItemImage());
            Glide.with(this)
                    .load(medicineItem.getItemImage())
                    .apply(new RequestOptions().transform(new RoundedCorners(32)))
                    .into(ivImg);
        } else {
            ivImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_medication, null));
            ivImg.setColorFilter(getResources().getColor(R.color.color_main_red), PorterDuff.Mode.SRC_IN);
        }


    }
}