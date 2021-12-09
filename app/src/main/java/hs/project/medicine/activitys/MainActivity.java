package hs.project.medicine.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import hs.project.medicine.R;
import hs.project.medicine.custom_view.MainBottomView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MainBottomView mainBottomView;

    private LinearLayout liAddUser;
    private LinearLayout liMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

    }

    private void init() {
        mainBottomView = findViewById(R.id.main_bottom_view);
        liMenu = findViewById(R.id.li_menu);
        liAddUser = findViewById(R.id.li_add_user);

        liMenu.setOnClickListener(this);
        liAddUser.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.li_menu:
                Toast.makeText(this,"메뉴클릭", Toast.LENGTH_SHORT).show();
                break;
            case R.id.li_add_user:
                Intent intent = new Intent(MainActivity.this, AddUserActivity.class);
                startActivity(intent);
                break;

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