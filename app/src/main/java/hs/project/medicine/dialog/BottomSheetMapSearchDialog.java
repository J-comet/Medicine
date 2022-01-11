package hs.project.medicine.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import hs.project.medicine.R;
import hs.project.medicine.databinding.LayoutMapSearchSheetBinding;

public class BottomSheetMapSearchDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private LayoutMapSearchSheetBinding binding;
    private Context context;

    public BottomSheetMapSearchDialog(Context context) {
        this.context = context;
    }

    public interface BottomSheetListener {
        void onBtnClicked(String text);
    }

    public void setBottomSheetListener(BottomSheetListener listener) {
        this.eventlistener = listener;
    }

    private BottomSheetListener eventlistener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutMapSearchSheetBinding.inflate(LayoutInflater.from(context), container, false);

        init();

        return binding.getRoot();
    }

    private void init() {
        binding.tvLocation.setOnClickListener(this);
        binding.tvLocationDetail.setOnClickListener(this);
        binding.liSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_location:
                Toast.makeText(context, "시 도", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_location_detail:
                Toast.makeText(context, "시 군 구", Toast.LENGTH_SHORT).show();
                break;
            case R.id.li_search:
                eventlistener.onBtnClicked("숨겨짐");
                dismiss();
                break;
        }
    }

}
