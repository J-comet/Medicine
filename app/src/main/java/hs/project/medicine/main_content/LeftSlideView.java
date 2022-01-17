package hs.project.medicine.main_content;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import hs.project.medicine.R;
import hs.project.medicine.databinding.LeftSlideViewBinding;

public class LeftSlideView extends ConstraintLayout implements View.OnClickListener {

    private LeftSlideViewBinding binding;
    private Context context;

    private LeftSlideView.LeftSlideListener eventListener;

    public interface LeftSlideListener {
        void onHomeClick();
        void onSearchClick();
        void onTVClick();
    }

    public void setLeftMenuEventListener(LeftSlideView.LeftSlideListener listener) {
        this.eventListener = listener;
    }

    public LeftSlideView(@NonNull Context context) {
        super(context);
        this.context = context;
        initView(context);
    }

    public LeftSlideView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
    }

    public LeftSlideView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView(context);
    }

    private void initView(Context context) {
        binding = LeftSlideViewBinding.inflate(LayoutInflater.from(context), this, true);

        binding.llHome.setOnClickListener(this);
        binding.llSearch.setOnClickListener(this);
        binding.llTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_home:
                eventListener.onHomeClick();
                break;
            case R.id.ll_search:
                eventListener.onSearchClick();
                break;
            case R.id.ll_tv:
                eventListener.onTVClick();
                break;
        }

    }
}
