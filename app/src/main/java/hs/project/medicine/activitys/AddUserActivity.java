package hs.project.medicine.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import org.w3c.dom.Text;

import hs.project.medicine.MediApplication;
import hs.project.medicine.R;

public class AddUserActivity extends AppCompatActivity implements View.OnClickListener{

//    private TextInputLayout tilName;

    private EditText etName;
    private TextView tvGender;
    private TextView tvAge;
    private LinearLayout liBack;
    private LinearLayout liComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        init();

    }

    private void init() {
        etName = findViewById(R.id.et_name);
        tvGender = findViewById(R.id.tv_gender);
        tvAge = findViewById(R.id.tv_age);
        liBack = findViewById(R.id.li_back);
        liComplete = findViewById(R.id.li_complete);

        tvGender.setOnClickListener(this);
        tvAge.setOnClickListener(this);
        liBack.setOnClickListener(this);
        liComplete.setOnClickListener(this);

//        tilName = findViewById(R.id.til_name);
//        tilName.setStartIconTintList(ContextCompat.getColorStateList(MediApplication.ApplicationContext(), R.color.selector_starticon));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_gender:
                Toast.makeText(this, "성별", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_age:
                Toast.makeText(this, "나이", Toast.LENGTH_SHORT).show();
                break;
            case R.id.li_back:
                finish();
                break;
            case R.id.li_complete:
                Toast.makeText(this, "등록하기", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}