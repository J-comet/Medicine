package hs.project.medicine.main_content;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import hs.project.medicine.Config;
import hs.project.medicine.MediApplication;
import hs.project.medicine.R;
import hs.project.medicine.databinding.LayoutMainBottomViewBinding;

public class MainBottomView extends ConstraintLayout implements View.OnClickListener {

    private LayoutMainBottomViewBinding binding;

    private MainBottomListener eventListener;

    public interface MainBottomListener {
        void onHomeClick();

        void onSearchClick();

        void onTVClick();

        void onMapClick();
    }

    public void setMainMenuEventListener(MainBottomListener listener) {
        this.eventListener = listener;
    }

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
        binding = LayoutMainBottomViewBinding.inflate(LayoutInflater.from(context), this, true);

        binding.clBottomMenuHome.setOnClickListener(this);
        binding.clBottomMenuSearch.setOnClickListener(this);
        binding.clBottomMenuTv.setOnClickListener(this);
        binding.clBottomMenuMap.setOnClickListener(this);

        menuStatus(Config.MAIN_BOTTOM_MENU.HOME);
    }

    public void menuStatus(String selectedMenu) {
        // 메뉴들 초기화
        binding.ivHome.setColorFilter(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_e2e2e2));
        binding.ivSearch.setColorFilter(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_e2e2e2));
        binding.ivMap.setColorFilter(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_e2e2e2));
        binding.ivTv.setColorFilter(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_e2e2e2));

        binding.tvHome.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_e2e2e2));
        binding.tvSearch.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_e2e2e2));
        binding.tvMap.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_e2e2e2));
        binding.tvTelev.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_e2e2e2));


        switch (selectedMenu) {
            case Config.MAIN_BOTTOM_MENU.HOME:
                binding.ivHome.setColorFilter(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_main_red));
                binding.tvHome.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_main_red));
                break;
            case Config.MAIN_BOTTOM_MENU.SEARCH:
                binding.ivSearch.setColorFilter(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_main_red));
                binding.tvSearch.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_main_red));
                break;
            case Config.MAIN_BOTTOM_MENU.MAP:
                binding.ivMap.setColorFilter(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_main_red));
                binding.tvMap.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_main_red));
                break;
            case Config.MAIN_BOTTOM_MENU.TV:
                binding.ivTv.setColorFilter(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_main_red));
                binding.tvTelev.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_main_red));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cl_bottom_menu_home:
                menuStatus(Config.MAIN_BOTTOM_MENU.HOME);
                eventListener.onHomeClick();
                break;
            case R.id.cl_bottom_menu_search:
                menuStatus(Config.MAIN_BOTTOM_MENU.SEARCH);
                eventListener.onSearchClick();
                break;
            case R.id.cl_bottom_menu_tv:
                menuStatus(Config.MAIN_BOTTOM_MENU.TV);
                eventListener.onTVClick();
                break;
            case R.id.cl_bottom_menu_map:
                menuStatus(Config.MAIN_BOTTOM_MENU.MAP);
                eventListener.onMapClick();
                break;
        }
    }
}
