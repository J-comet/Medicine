package hs.project.medicine.not_used;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hs.project.medicine.R;
import hs.project.medicine.databinding.ItemGridUserBinding;
import hs.project.medicine.datas.User;

public class GridUserAdapter extends RecyclerView.Adapter<GridUserAdapter.ViewHolder> {

    private Context context;
    private ArrayList<User> items = new ArrayList<>();

    public GridUserAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_grid_user, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User itemModel = items.get(position);
        holder.bindItem(itemModel);

        /* 등록한 유저가 없을 때 item.size() == 1 */
        if (items.size() == 1) {
            holder.itemBinding.cvAddUser.setVisibility(View.VISIBLE);
        }
        /* item.size() 의 마지막은 항상 유저 등록 버튼 보이도록  */
        else if (items.size()  == position + 1) {
            holder.itemBinding.cvAddUser.setVisibility(View.VISIBLE);
        } else {
            holder.itemBinding.cvUserCard.setVisibility(View.VISIBLE);
        }
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

        ItemGridUserBinding itemBinding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemBinding = ItemGridUserBinding.bind(itemView);
        }

        void bindItem(User userItem) {
            itemBinding.tvName.setText(userItem.getName());

            itemBinding.cvUserCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, UserDetailActivity.class);
                    intent.putExtra("user", userItem);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                }
            });

            itemBinding.cvAddUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AddUserActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                }
            });
        }

    }

}

