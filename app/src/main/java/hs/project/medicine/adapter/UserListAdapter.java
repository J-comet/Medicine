package hs.project.medicine.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        TextView tvEntpName;  // 업체 이름
        TextView tvItemName; // 약 이름
        TextView tvItemSeq; // 품목코드

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            clContent = itemView.findViewById(R.id.cl_content);
            tvEntpName = itemView.findViewById(R.id.tv_entp_name);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvItemSeq = itemView.findViewById(R.id.tv_item_seq);
        }
    }
}
