package hs.project.medicine.custom_view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import hs.project.medicine.Config;
import hs.project.medicine.MediApplication;
import hs.project.medicine.R;

public class MainBottomView extends ConstraintLayout implements View.OnClickListener{

    private ConstraintLayout clMenuHome;
    private ConstraintLayout clMenuSearch;
    private ConstraintLayout clMenuTv;

    private ImageView ivHome;
    private ImageView ivSearch;
    private ImageView ivTv;

    private TextView tvHome;
    private TextView tvSearch;
    private TextView tvTelevision;

    public MainBottomView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public MainBottomView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MainBottomView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.layout_main_bottom_view, this, false);
        addView(v);

        clMenuHome = findViewById(R.id.cl_bottom_menu_home);
        clMenuSearch = findViewById(R.id.cl_bottom_menu_search);
        clMenuTv = findViewById(R.id.cl_bottom_menu_tv);
        ivHome = findViewById(R.id.iv_home);
        ivSearch = findViewById(R.id.iv_search);
        ivTv = findViewById(R.id.iv_tv);
        tvHome = findViewById(R.id.tv_home);
        tvSearch = findViewById(R.id.tv_search);
        tvTelevision = findViewById(R.id.tv_telev);

        clMenuHome.setOnClickListener(this);
        clMenuSearch.setOnClickListener(this);
        clMenuTv.setOnClickListener(this);

        menuStatus(Config.MAIN_BOTTOM_MENU.HOME);
    }

    private void menuStatus(String selectedMenu) {
        // 메뉴들 초기화
        ivHome.setColorFilter(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_e2e2e2));
        ivSearch.setColorFilter(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_e2e2e2));
        ivTv.setColorFilter(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_e2e2e2));

        tvHome.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_e2e2e2));
        tvSearch.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_e2e2e2));
        tvTelevision.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_e2e2e2));


        switch (selectedMenu) {
            case Config.MAIN_BOTTOM_MENU.HOME:
                ivHome.setColorFilter(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_main_red));
                tvHome.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_main_red));
                break;
            case Config.MAIN_BOTTOM_MENU.SEARCH:
                ivSearch.setColorFilter(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_main_red));
                tvSearch.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_main_red));
                break;
            case Config.MAIN_BOTTOM_MENU.TV:
                ivTv.setColorFilter(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_main_red));
                tvTelevision.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_main_red));
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cl_bottom_menu_home:
                menuStatus(Config.MAIN_BOTTOM_MENU.HOME);
                break;
            case R.id.cl_bottom_menu_search:
                menuStatus(Config.MAIN_BOTTOM_MENU.SEARCH);
                break;
            case R.id.cl_bottom_menu_tv:
                menuStatus(Config.MAIN_BOTTOM_MENU.TV);
                break;
        }
    }
}
