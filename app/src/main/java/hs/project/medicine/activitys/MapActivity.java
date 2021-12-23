package hs.project.medicine.activitys;

import android.os.Bundle;
import android.view.ViewGroup;


import net.daum.mf.map.api.MapView;

import hs.project.medicine.R;
import hs.project.medicine.databinding.ActivityMapBinding;

public class MapActivity extends BaseActivity {

    private ActivityMapBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MapView mapView = new MapView(this);

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

    }
}