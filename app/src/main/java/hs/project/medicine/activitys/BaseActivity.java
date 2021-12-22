package hs.project.medicine.activitys;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {


    /* 애니매이션 제거 */
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    /* 애니매이션 제거 */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}
