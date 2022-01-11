package hs.project.medicine.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import hs.project.medicine.R;

public class BottomSheetMapSearchDialog extends BottomSheetDialogFragment {

    public interface BottomSheetListener {
        void onBtnClicked(String text);
    }

    public void setBottomSheetListener(BottomSheetListener listener) {
        this.eventlistener = listener;
    }

    private View view;
    private BottomSheetListener eventlistener;
    private Button btnHide;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_map_search_sheet, container, false);
//        listener = (BottomSheetListener) getContext();
        btnHide = view.findViewById(R.id.btn_hide_bt_sheet);
        btnHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventlistener.onBtnClicked("숨겨짐");
                dismiss();
            }
        });


        return view;
    }
}
