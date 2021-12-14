package hs.project.medicine.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hs.project.medicine.MediApplication;
import hs.project.medicine.R;
import hs.project.medicine.activitys.MedicineDetailActivity;
import hs.project.medicine.activitys.ModifyUserActivity;
import hs.project.medicine.datas.Item;
import hs.project.medicine.datas.User;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<User> items = new ArrayList<>();

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
        holder.tvName.setText(userModel.getName());
        holder.tvAge.setText(userModel.getAge());

        /**
         *  남자 or 여자에 따라 다른 이미지 출력하기
         */
        switch (userModel.getGender()) {
            case "남자":
                holder.ivGender.setImageResource(R.drawable.male);
                holder.ivGender.setColorFilter(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.blue));
                break;
            case "여자":
                holder.ivGender.setImageResource(R.drawable.female);
                holder.ivGender.setColorFilter(ContextCompat.getColor(MediApplication.ApplicationContext(), R.color.red));
                break;
        }

        if (userModel.isCurrent()) {
            holder.ivSelected.setVisibility(View.VISIBLE);
        } else {
            holder.ivSelected.setVisibility(View.GONE);
        }

        holder.liModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ModifyUserActivity.class);
                intent.putExtra("user", userModel);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(intent);
                Toast.makeText(context, userModel.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.liDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 *  1. 해당 포지션 user  ArrayList 에서 remove
                 *  2. Preference 에 저장되어 있는 userList 에서 remove
                 */

            }
        });

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

    public class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout clContent;
        LinearLayout liModify;
        LinearLayout liDelete;
        ImageView ivGender;
        ImageView ivSelected;
        TextView tvName;
        TextView tvAge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            clContent = itemView.findViewById(R.id.cl_content);
            liModify = itemView.findViewById(R.id.li_modify);
            liDelete = itemView.findViewById(R.id.li_delete);
            ivGender = itemView.findViewById(R.id.iv_gender);
            ivSelected = itemView.findViewById(R.id.iv_selected);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAge = itemView.findViewById(R.id.tv_age);

            clContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (userListClickListener != null) {
                            userListClickListener.onUserClick(v,position);
                        }
                    }
                }
            });
        }
    }
}
