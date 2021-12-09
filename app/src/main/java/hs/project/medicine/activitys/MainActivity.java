package hs.project.medicine.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import hs.project.medicine.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

//    private ConstraintLayout clSearchMedic;
//    private TextView tvAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

    }

    private void init() {
        /*clSearchMedic = findViewById(R.id.cl_search_medic);
        clSearchMedic.setOnClickListener(this);
        tvAlert = findViewById(R.id.tv_alert);
        tvAlert.setOnClickListener(this);*/
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
           /* case R.id.cl_search_medic:
                Intent intentMoveSearchMedic = new Intent(MainActivity.this, SearchMedicineActivity.class);
                intentMoveSearchMedic.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentMoveSearchMedic);
                break;

            case R.id.tv_alert:
                Intent intentMoveSetting = new Intent(MainActivity.this, SettingAlarmActivity.class);
                intentMoveSetting.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentMoveSetting);
                break;*/
        }
    }

}