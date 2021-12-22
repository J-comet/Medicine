package hs.project.medicine.custom_view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import hs.project.medicine.databinding.LeftSlideViewBinding;

public class LeftSlideView extends ConstraintLayout {

    private LeftSlideViewBinding binding;
    private Context context;

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
    }
}
