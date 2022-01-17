package hs.project.medicine.activitys;

import android.os.Bundle;
import android.view.View;

import hs.project.medicine.R;
import hs.project.medicine.databinding.ActivityUserDetailBinding;
import hs.project.medicine.datas.User;
import hs.project.medicine.dialog.ModifyUserDialog;
import hs.project.medicine.util.LogUtil;

public class UserDetailActivity extends BaseActivity implements View.OnClickListener {

    private ActivityUserDetailBinding binding;
    private User getIntentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getIntentUser = (User) getIntent().getSerializableExtra("user");
        setData(getIntentUser);
    }


    private void init() {
        binding.liBack.setOnClickListener(this);
        binding.tvProfileSetting.setOnClickListener(this);
    }

    private void setData(User user) {
        if (user != null) {
            binding.tvName.setText(user.getName());
            binding.tvRelation.setText("(" + user.getRelation() + ")");
            binding.tvAge.setText(user.getAge());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_profile_setting:
                ModifyUserDialog dialog = new ModifyUserDialog(this, getIntentUser);
                dialog.setModifyUserListener(new ModifyUserDialog.ModifyUserListener() {
                    @Override
                    public void onComplete(User user) {
                        setData(user);
                    }
                });
                dialog.show(getSupportFragmentManager(),"modifyUserDialog");
                /*getFragmentManager().executePendingTransactions();
                dialog.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                    }
                });*/
                break;
            case R.id.li_back:
                finish();
                break;
        }
    }
}