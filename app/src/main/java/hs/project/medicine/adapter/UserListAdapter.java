package hs.project.medicine.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hs.project.medicine.R;
import hs.project.medicine.activitys.MedicineDetailActivity;
import hs.project.medicine.datas.Item;
import hs.project.medicine.datas.User;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<User> items = new ArrayList<>();

    public UserListAdapter(Context context) {
        this.context = context;
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
                break;
            case "여자":
                break;
        }


        if (userModel.isCurrent()) {
            holder.ivSelected.setVisibility(View.VISIBLE);
        } else {
            holder.ivSelected.setVisibility(View.GONE);
        }

        /**
         * 누르면 현재 선택된 유저로 저장하기
         */
        holder.clContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 저장된 Preference 값 가져와서

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
        ImageView ivGender;
        ImageView ivSelected;
        TextView tvName;
        TextView tvAge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            clContent = itemView.findViewById(R.id.cl_content);
            ivGender = itemView.findViewById(R.id.iv_gender);
            ivSelected = itemView.findViewById(R.id.iv_selected);
            tvName = itemView.findViewById(R.id.tv_name);
            tvAge = itemView.findViewById(R.id.tv_age);
        }
    }
}
