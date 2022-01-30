package hs.project.medicine.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;

import hs.project.medicine.MediApplication;
import hs.project.medicine.R;
import hs.project.medicine.databinding.DialogModifyAlarmBinding;
import hs.project.medicine.datas.Alarm;

public class ModifyAlarmDialog extends DialogFragment implements View.OnClickListener {

    private DialogModifyAlarmBinding binding;
    private Context context;
    private Alarm alarm;
    private ArrayList<String> strAlarmList;
    private ArrayList<Alarm> alarmArrayList; // Preference 에 저장할 알람 정보
    private Fragment fragment;
    private ModifyAlarmListener eventListener;

    public ModifyAlarmDialog(Context context, Alarm alarmItem) {
        this.context = context;
        this.alarm = alarmItem;
    }

    public void setModifyAlarmListener(ModifyAlarmListener dialogResult) {
        eventListener = dialogResult;
    }

    public interface ModifyAlarmListener {
        void onComplete(Alarm alarm);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.dialog_fullscreen);
        fragment = requireActivity().getSupportFragmentManager().findFragmentByTag("modifyAlarmDialog");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogModifyAlarmBinding.inflate(LayoutInflater.from(context), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        setData();
    }

    private void init() {

    }

    private void setData() {
        if (alarm != null) {
            binding.etName.setText(alarm.getName());


        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.li_back:
                dismiss();
                break;
        }
    }

}
