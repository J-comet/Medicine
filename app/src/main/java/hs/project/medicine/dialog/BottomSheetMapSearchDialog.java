package hs.project.medicine.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import hs.project.medicine.MediApplication;
import hs.project.medicine.R;
import hs.project.medicine.databinding.LayoutMapSearchSheetBinding;

public class BottomSheetMapSearchDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private LayoutMapSearchSheetBinding binding;
    private Context context;
    private boolean isLocation = false;
    private boolean isLocationDetail = false;
    private boolean selectedSejong = false;

    public BottomSheetMapSearchDialog(Context context) {
        this.context = context;
    }

    public interface BottomSheetListener {
        void onBtnClicked(String location, String locationDetail);
    }

    public void setBottomSheetListener(BottomSheetListener listener) {
        this.eventlistener = listener;
    }

    private BottomSheetListener eventlistener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutMapSearchSheetBinding.inflate(LayoutInflater.from(context), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        binding.tvLocation.setOnClickListener(this);
        binding.tvLocationDetail.setOnClickListener(this);
        binding.liSearch.setOnClickListener(this);

        binding.clLocationDetail.setFocusable(false);
        binding.clLocationDetail.setEnabled(false);
        binding.clLocationDetail.setClickable(false);
    }

    private void displayLocationDialog() {

        String[] locationList = getResources().getStringArray(R.array.arr_location);

        new AlertDialog.Builder(context)
                .setTitle("시·도 선택")
                .setItems(locationList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        binding.tvLocationDetail.setText("시·군·구");
                        binding.tvLocationDetail.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_a09d9d));

                        binding.tvLocation.setText(locationList[which]);
                        binding.tvLocation.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.black));
                        isLocation = true;

                        if (locationList[which].equals("세종특별자치시")) {
                            binding.clLocationDetail.setVisibility(View.GONE);
                            selectedSejong = true;
                            isLocationDetail = true;
                        } else {
                            binding.clLocationDetail.setVisibility(View.VISIBLE);
                            binding.clLocationDetail.setFocusable(true);
                            binding.clLocationDetail.setEnabled(true);
                            binding.clLocationDetail.setClickable(true);
                            selectedSejong = false;
                            isLocationDetail = false;

                        }

                        dialog.dismiss();
                    }
                }).

                show();

    }

    private void displayLocationDetailDialog(String location) {

        String[] detailList = null;

        switch (location) {
            case "서울특별시":
                detailList = getResources().getStringArray(R.array.arr_seoul);
                break;
            case "부산광역시":
                detailList = getResources().getStringArray(R.array.arr_busan);
                break;
            case "대구광역시":
                detailList = getResources().getStringArray(R.array.arr_daegu);
                break;
            case "인천광역시":
                detailList = getResources().getStringArray(R.array.arr_incheon);
                break;
            case "광주광역시":
                detailList = getResources().getStringArray(R.array.arr_gwangju);
                break;
            case "대전광역시":
                detailList = getResources().getStringArray(R.array.arr_daejeon);
                break;
            case "울산광역시":
                detailList = getResources().getStringArray(R.array.arr_ulsan);
                break;
            case "세종특별자치시":
                break;
            case "경기도":
                detailList = getResources().getStringArray(R.array.arr_gyunggi);
                break;
            case "강원도":
                detailList = getResources().getStringArray(R.array.arr_gwangwon);
                break;
            case "충청북도":
                detailList = getResources().getStringArray(R.array.arr_chungbuk);
                break;
            case "충청남도":
                detailList = getResources().getStringArray(R.array.arr_chungnam);
                break;
            case "전라북도":
                detailList = getResources().getStringArray(R.array.arr_jeonbuk);
                break;
            case "전라남도":
                detailList = getResources().getStringArray(R.array.arr_jeonnam);
                break;
            case "경상북도":
                detailList = getResources().getStringArray(R.array.arr_gyeongbuk);
                break;
            case "경상남도":
                detailList = getResources().getStringArray(R.array.arr_gyeongnam);
                break;
            case "제주특별자치도":
                detailList = getResources().getStringArray(R.array.arr_jeju);
                break;
        }

        String[] finalDetailList = detailList;

        new AlertDialog.Builder(context)
                .setTitle("시·도 선택")
                .setItems(finalDetailList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        isLocationDetail = true;
                        binding.tvLocationDetail.setText(finalDetailList[which]);
                        binding.tvLocationDetail.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.black));


                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_location:
                displayLocationDialog();
                break;
            case R.id.tv_location_detail:
                if (isLocation) {
                    displayLocationDetailDialog(binding.tvLocation.getText().toString());
                } else {
                    Toast.makeText(context, "상위 지역을 먼저 선택해주세요", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.li_search:
                if (isLocation && isLocationDetail) {
                    if (selectedSejong) {
                        eventlistener.onBtnClicked(binding.tvLocation.getText().toString(), "");
                    } else {
                        eventlistener.onBtnClicked(binding.tvLocation.getText().toString(), binding.tvLocationDetail.getText().toString());
                    }

                    dismiss();

                } else {
                    Toast.makeText(context, "모든 지역명을 선택해주세요", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}
