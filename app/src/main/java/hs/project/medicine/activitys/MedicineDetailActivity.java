package hs.project.medicine.activitys;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.nativead.NativeAd;

import org.jsoup.Jsoup;

import hs.project.medicine.R;
import hs.project.medicine.databinding.ActivityMedicineDetailBinding;
import hs.project.medicine.datas.medicine.MedicineItem;
import hs.project.medicine.util.LogUtil;

public class MedicineDetailActivity extends BaseActivity {

    private ActivityMedicineDetailBinding binding;

    private MedicineItem medicineItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMedicineDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        startNativeAdview();
        init();
        setData();
    }

    private void init() {
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void startNativeAdview() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdLoader.Builder builder = new AdLoader.Builder(
                this, getResources().getString(R.string.test_admob_native_unit_id));

        builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                binding.myTemplate.setNativeAd(nativeAd);
            }
        });

        AdLoader adLoader = builder.build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private void setData() {
        medicineItem = (MedicineItem) getIntent().getSerializableExtra("item");

        if (medicineItem.getEntpName() != null && !medicineItem.getEntpName().equals("null")) {
            LogUtil.d(medicineItem.getEntpName());
            binding.tvEntpName.setText(medicineItem.getEntpName());
        }

        if (medicineItem.getItemName() != null && !medicineItem.getItemName().equals("null")) {
            LogUtil.d(medicineItem.getItemName());
            binding.tvItemName.setText(medicineItem.getItemName());
        }

        if (medicineItem.getItemSeq() != null && !medicineItem.getItemSeq().equals("null")) {
            LogUtil.d(medicineItem.getItemSeq());
            binding.tvItemSeq.setText(medicineItem.getItemSeq());
        }

        if (medicineItem.getEfcyQesitm() != null && !medicineItem.getEfcyQesitm().equals("null")) {
            LogUtil.d(medicineItem.getEfcyQesitm());
            binding.tvEfcyQesitm.setText(Jsoup.parse(medicineItem.getEfcyQesitm()).wholeText());
        }

        if (medicineItem.getUseMethodQesitm() != null && !medicineItem.getUseMethodQesitm().equals("null")) {
            LogUtil.d(medicineItem.getUseMethodQesitm());
            binding.tvUseMethodQesitm.setText(Jsoup.parse(medicineItem.getUseMethodQesitm()).wholeText());
        }

        if (medicineItem.getAtpnWarnQesitm() != null && !medicineItem.getAtpnWarnQesitm().equals("null")) {
            LogUtil.d(medicineItem.getAtpnWarnQesitm().toString());
            binding.tvAtpnWarnQesitm.setText(medicineItem.getAtpnWarnQesitm().toString());
//            tvAtpnWarnQesitm.setText(Jsoup.parse(medicineItem.getAtpnWarnQesitm()).wholeText());
        }

        if (medicineItem.getAtpnQesitm() != null && !medicineItem.getAtpnQesitm().equals("null")) {
            LogUtil.d(medicineItem.getAtpnQesitm());
            binding.tvAtpnQesitm.setText(Jsoup.parse(medicineItem.getAtpnQesitm()).wholeText());
        }

        if (medicineItem.getIntrcQesitm() != null && !medicineItem.getIntrcQesitm().equals("null")) {
            LogUtil.d(medicineItem.getIntrcQesitm());
            binding.tvIntrcQesitm.setText(Jsoup.parse(medicineItem.getIntrcQesitm()).wholeText());
        }

        if (medicineItem.getSeQesitm() != null && !medicineItem.getSeQesitm().equals("null")) {
            LogUtil.d(medicineItem.getSeQesitm());
            binding.tvSeQesitm.setText(Jsoup.parse(medicineItem.getSeQesitm()).wholeText());
        }

        if (medicineItem.getDepositMethodQesitm() != null && !medicineItem.getDepositMethodQesitm().equals("null")) {
            LogUtil.d(medicineItem.getDepositMethodQesitm());
            binding.tvDepositMethodQesitm.setText(Jsoup.parse(medicineItem.getDepositMethodQesitm()).wholeText());
        }

        if (medicineItem.getOpenDe() != null && !medicineItem.getOpenDe().equals("null")) {
            LogUtil.d(medicineItem.getOpenDe());
            String openDate = medicineItem.getOpenDe().replaceAll("-","");
            String openYear = openDate.substring(0,4);
            String openMonth = openDate.substring(4,6);
            String openDay = openDate.substring(6,8);

            binding.tvOpenDe.setText(openYear + "." + openMonth + "." + openDay);
        }

        if (medicineItem.getUpdateDe() != null && !medicineItem.getUpdateDe().equals("null")) {
            LogUtil.d(medicineItem.getUpdateDe());

            String openYear = medicineItem.getUpdateDe().substring(0,4);
            String openMonth = medicineItem.getUpdateDe().substring(4,6);
            String openDay = medicineItem.getUpdateDe().substring(6,8);

            binding.tvUpdateDe.setText(openYear + "." + openMonth + "." + openDay);
        }

        if (medicineItem.getItemImage() != null && !medicineItem.getItemImage().equals("null")) {
            LogUtil.d(medicineItem.getItemImage());
            Glide.with(this)
                    .load(medicineItem.getItemImage())
                    .placeholder(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_image_search, null))
                    .apply(new RequestOptions().transform(new RoundedCorners(32)))
                    .into(binding.ivItemImg);
        } else {
            binding.ivItemImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_medication, null));
            binding.ivItemImg.setColorFilter(getResources().getColor(R.color.color_main_red), PorterDuff.Mode.SRC_IN);
        }


    }
}