package hs.project.medicine.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;

import com.google.android.material.textfield.TextInputLayout;

import hs.project.medicine.MediApplication;
import hs.project.medicine.R;

public class AddUserActivity extends AppCompatActivity {

    private TextInputLayout tilName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        init();

    }

    private void init() {
        tilName = findViewById(R.id.til_name);
        tilName.setStartIconTintList(ContextCompat.getColorStateList(MediApplication.ApplicationContext(), R.color.selector_starticon));
    }
}