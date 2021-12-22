package hs.project.medicine.custom_view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import hs.project.medicine.Config;
import hs.project.medicine.MediApplication;
import hs.project.medicine.R;
import hs.project.medicine.activitys.AddUserActivity;
import hs.project.medicine.activitys.UserListActivity;
import hs.project.medicine.databinding.LayoutMainHomeViewBinding;
import hs.project.medicine.databinding.LayoutMainSearchViewBinding;
import hs.project.medicine.datas.User;
import hs.project.medicine.util.LogUtil;
import hs.project.medicine.util.PreferenceUtil;

public class MainSearchView extends ConstraintLayout implements View.OnClickListener {
    private LayoutMainSearchViewBinding binding;
    private Context context;

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
    }

    @Override
    public void onClick(View v) {

    }
}
