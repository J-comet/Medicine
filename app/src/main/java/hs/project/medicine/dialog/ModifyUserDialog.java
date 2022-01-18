package hs.project.medicine.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hs.project.medicine.Config;
import hs.project.medicine.MediApplication;
import hs.project.medicine.R;
import hs.project.medicine.databinding.DialogModifyUserBinding;
import hs.project.medicine.datas.User;
import hs.project.medicine.util.PreferenceUtil;

public class ModifyUserDialog extends DialogFragment implements View.OnClickListener {

    private DialogModifyUserBinding binding;
    private Context context;
    private User user;
    private ArrayList<String> strUserList;
    private ArrayList<User> userArrayList; // Preference 에 저장할 유저 정보
    private Fragment fragment;
    private ModifyUserListener eventListener;
    private boolean isDirect = false;

    public ModifyUserDialog(Context context, User userItem) {
        this.context = context;
        this.user = userItem;
    }

    public void setModifyUserListener(ModifyUserListener dialogResult) {
        eventListener = dialogResult;
    }

    public interface ModifyUserListener {
        void onComplete(User user);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.dialog_fullscreen);
        fragment = requireActivity().getSupportFragmentManager().findFragmentByTag("modifyUserDialog");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogModifyUserBinding.inflate(LayoutInflater.from(context), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        setData();
    }

    private void init() {
//        binding.tvGender.setOnClickListener(this);
        binding.tvAge.setOnClickListener(this);
        binding.tvRelation.setOnClickListener(this);
        binding.liBack.setOnClickListener(this);
        binding.liModifyComplete.setOnClickListener(this);

        userArrayList = new ArrayList<>();

        if (PreferenceUtil.getJSONArrayPreference(context, Config.PREFERENCE_KEY.USER_LIST) != null
                && PreferenceUtil.getJSONArrayPreference(context, Config.PREFERENCE_KEY.USER_LIST).size() > 0) {

            JSONArray jsonArray = new JSONArray(PreferenceUtil.getJSONArrayPreference(context, Config.PREFERENCE_KEY.USER_LIST));

            try {

                for (int i = 0; i < jsonArray.length(); i++) {
                    User user = new User();
                    JSONObject object = new JSONObject(jsonArray.getString(i));
                    user.setName(object.getString("name"));
                    user.setAge(object.getString("age"));
//                    user.setGender(object.getString("gender"));
                    user.setRelation(object.getString("relation"));
//                    user.setCurrent(object.getBoolean("isCurrent"));

//                    LogUtil.d("user /" + user.getName());
//                    LogUtil.d("user /" + user.getGender());
//                    LogUtil.d("user /" + user.getAge());
//                    LogUtil.d("user /" + user.getRelation());

                    userArrayList.add(user);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void setData() {

        if (user != null) {
            binding.etName.setText(user.getName());
//            binding.tvGender.setText(user.getGender());
//            binding.tvGender.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.black));
            binding.tvAge.setText(user.getAge());
            binding.tvAge.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.black));
            binding.tvRelation.setText(user.getRelation());
            binding.tvRelation.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.black));
        }

    }

    private void displayGenderDialog() {

        String[] genderList = getResources().getStringArray(R.array.arr_gender);

        new AlertDialog.Builder(context)
                .setTitle("성별 선택")
                .setItems(genderList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        binding.tvGender.setText(genderList[which]);
//                        binding.tvGender.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.black));
                        dialog.dismiss();
                    }
                }).show();
    }

    private void displayAgeDialog() {

        String[] ageList = getResources().getStringArray(R.array.arr_age);

        new AlertDialog.Builder(context)
                .setTitle("연령대 선택")
                .setItems(ageList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        binding.tvAge.setText(ageList[which]);
                        binding.tvAge.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.black));
                        dialog.dismiss();
                    }
                }).show();
    }

    private void displayRelationDialog() {

        String[] arrList = getResources().getStringArray(R.array.arr_relation);

        new AlertDialog.Builder(context)
                .setTitle("관계 선택")
                .setItems(arrList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (arrList[which].equals("직접입력")) {
                            binding.groupRelationDirect.setVisibility(View.VISIBLE);
                            binding.tvRelation.setText(arrList[which]);
                            binding.tvRelation.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.black));
                            isDirect = true;
                        } else {
                            binding.groupRelationDirect.setVisibility(View.GONE);
                            binding.tvRelation.setText(arrList[which]);
                            binding.tvRelation.setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.black));
                            isDirect = false;
                        }
                        dialog.dismiss();
                    }
                }).show();
    }

    private void modifyComplete() {

        strUserList = new ArrayList<>();

        String strRelation = binding.tvRelation.getText().toString();

        /* 관계 직접입력일 때 */
        if (isDirect) {
            strRelation = binding.etRelation.getText().toString();
        }

        /**
         * 1. Preference 에 저장된 UserList 의 User 를 업데이트
         */
        for (int i = 0; i < userArrayList.size(); i++) {
            User addUser = new User();
            addUser.setName(userArrayList.get(i).getName());
//            addUser.setGender(userArrayList.get(i).getGender());
            addUser.setAge(userArrayList.get(i).getAge());
            addUser.setRelation(userArrayList.get(i).getRelation());

            if (user.getName().equals(userArrayList.get(i).getName())) {
                addUser.setName(binding.etName.getText().toString());
//                addUser.setGender(binding.tvGender.getText().toString());
                addUser.setAge(binding.tvAge.getText().toString());
                addUser.setRelation(strRelation);
            }

            strUserList.add(addUser.toJSON());
        }

        // Preference 에 저장
        PreferenceUtil.setJSONArrayPreference(context, Config.PREFERENCE_KEY.USER_LIST, strUserList);

        Toast.makeText(context, "수정완료", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.tv_gender:
//                displayGenderDialog();
//                break;
            case R.id.tv_age:
                displayAgeDialog();
                break;
            case R.id.tv_relation:
                displayRelationDialog();
                break;
            case R.id.li_back:
                dismiss();
                break;
            case R.id.li_modify_complete:

                String strRelation = binding.tvRelation.getText().toString();

                /* 관계 직접입력일 때 */
                if (isDirect) {
                    strRelation = binding.etRelation.getText().toString();
                }

                if (fragment != null) {

                    if (eventListener != null) {
                        User userItem = new User();
                        userItem.setName(binding.etName.getText().toString());
//                        userItem.setGender(binding.tvGender.getText().toString());
                        userItem.setAge(binding.tvAge.getText().toString());
                        userItem.setRelation(strRelation);

                        if (binding.etName.getText().toString().length() > 0) {
                            modifyComplete();
                            eventListener.onComplete(userItem);

                            dismiss();
//                            DialogFragment dialogFragment = (DialogFragment) fragment;
//                            dialogFragment.dismiss();
                        } else {
                            Toast.makeText(context, "모든 정보를 입력해주세요", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                /**
                 * 모든 정보 입력 완료했을 때 실행할 수 있도록
                 */
                    /*if (binding.etName.getText().toString().length() > 0) {
                        modifyComplete();
                        dismiss();
                    } else {
                        Toast.makeText(context, "모든 정보를 입력해주세요", Toast.LENGTH_SHORT).show();
                    }*/

                break;
        }
    }

}
