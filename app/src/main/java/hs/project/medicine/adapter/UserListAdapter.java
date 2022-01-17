package hs.project.medicine.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hs.project.medicine.Config;
import hs.project.medicine.MediApplication;
import hs.project.medicine.R;
import hs.project.medicine.activitys.ModifyUserActivity;
import hs.project.medicine.databinding.ItemUserBinding;
import hs.project.medicine.datas.User;
import hs.project.medicine.util.LogUtil;
import hs.project.medicine.util.PreferenceUtil;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<User> items = new ArrayList<>();
    private int selectedPosition = -100;

    public UserListAdapter(Context context) {
        this.context = context;
    }

    private OnUserListClickListener userListClickListener = null;

    public interface OnUserListClickListener {
        void onUserClick(View v, int pos);
    }

    public void setOnMemberClickListener(OnUserListClickListener listener) {
        this.userListClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_user, parent, false);
        return new UserListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User userModel = items.get(position);
        holder.bindItem(userModel);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addAll(ArrayList<User> itemArrayList) {
        if (items != null) {
            items.clear();
        }
        this.items.addAll(itemArrayList);
        notifyDataSetChanged();
    }

    private ArrayList<String> getRemovePreferenceList(int pos) {

        ArrayList<String> strUserList = new ArrayList<>();

        if (PreferenceUtil.getJSONArrayPreference(context, Config.PREFERENCE_KEY.USER_LIST) != null
                && PreferenceUtil.getJSONArrayPreference(context, Config.PREFERENCE_KEY.USER_LIST).size() > 0) {

            JSONArray jsonArray = new JSONArray(PreferenceUtil.getJSONArrayPreference(context, Config.PREFERENCE_KEY.USER_LIST));

            try {

                for (int i = 0; i < jsonArray.length(); i++) {
                    User user = new User();
                    JSONObject object = new JSONObject(jsonArray.getString(i));

                    //  선택한 값 빼고 userArrayList 에 저장
                    if (pos != i) {
                        user.setName(object.getString("name"));
                        user.setAge(object.getString("age"));
                        user.setGender(object.getString("gender"));
//                        user.setCurrent(object.getBoolean("isCurrent"));

//                        userArrayList.add(user);
                        strUserList.add(user.toJSON());
                        LogUtil.e("userArrayList[" + i + "]/ " + user.getName());
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return strUserList;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemUserBinding itemBinding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemBinding = ItemUserBinding.bind(itemView);
        }

        void bindItem(User userItem) {
            itemBinding.tvName.setText(userItem.getName());
            itemBinding.tvAge.setText(userItem.getAge());

            itemBinding.clContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (userListClickListener != null) {
                            userListClickListener.onUserClick(v, position);
                        }
                    }
                }
            });

            /* 성별 */
            switch (userItem.getGender()) {
                case "남자":
                    itemBinding.ivGender.setImageResource(R.drawable.male);
                    itemBinding.ivGender.setColorFilter(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.blue));
                    break;
                case "여자":
                    itemBinding.ivGender.setImageResource(R.drawable.female);
                    itemBinding.ivGender.setColorFilter(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.red));
                    break;
            }

            /* 현재 사용중인 유저 */
            /*if (userItem.isCurrent()) {
                itemBinding.ivSelected.setVisibility(View.VISIBLE);
            } else {
                itemBinding.ivSelected.setVisibility(View.GONE);
            }*/

            /* 수정버튼 */
            itemBinding.liModify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ModifyUserActivity.class);
                    intent.putExtra("user", userItem);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    Toast.makeText(context, userItem.getName(), Toast.LENGTH_SHORT).show();
                }
            });

            /* 삭제버튼 */
            /*itemBinding.liDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (userItem.isCurrent()) {

                        Toast.makeText(context, "사용중인 유저는 삭제할 수 없습니다", Toast.LENGTH_SHORT).show();

                    } else {

                        *//**
                         *  1. 해당 포지션 user  ArrayList 에서 remove
                         *  2. Preference 에 저장되어 있는 userList 에서 remove
                         *//*

                        AlertDialog dialog = new AlertDialog.Builder(context)
                                .setTitle("경고")
                                .setMessage("정말 삭제하시겠습니까?")
                                .setCancelable(false)
                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Toast.makeText(context, "취소", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        selectedPosition = getAdapterPosition();
                                        LogUtil.e("holder.getAdapterPosition()/" + selectedPosition);

                                        // adapter 에서 데이터 삭제
                                        items.remove(userItem);
                                        notifyItemRemoved(selectedPosition);

                                        // preference 에서 데이터 삭제한 후 다시 저장
                                        PreferenceUtil.setJSONArrayPreference(context, Config.PREFERENCE_KEY.USER_LIST, getRemovePreferenceList(selectedPosition));
                                        Toast.makeText(context, userItem.getName() + " 의 정보가 삭제되었습니다", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                }).create();

                        dialog.show();

                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.color_a09d9d));
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.black));
                    }
                }
            });*/

        }
    }

}
