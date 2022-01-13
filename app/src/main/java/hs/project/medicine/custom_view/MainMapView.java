package hs.project.medicine.custom_view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import hs.project.medicine.databinding.LayoutMainMapViewBinding;

public class MainMapView extends ConstraintLayout implements View.OnClickListener {
    private LayoutMainMapViewBinding binding;
    private Context context;

    public MainMapView(@NonNull Context context) {
        super(context);
        this.context = context;
        initView(context);
    }

    public MainMapView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
    }

    public MainMapView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView(context);
    }

    public MainMapView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initView(Context context) {
        binding = LayoutMainMapViewBinding.inflate(LayoutInflater.from(context), this, true);
    }

    @Override
    public void onClick(View v) {

    }
}
