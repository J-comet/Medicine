package hs.project.medicine;

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

import hs.project.medicine.datas.Item;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Item> items = new ArrayList<>();

    public MedicineAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_medicine, parent, false);
        return new MedicineAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item itemModel = items.get(position);
        holder.tvEntpName.setText(itemModel.getEntpName());
        holder.tvItemName.setText(itemModel.getItemName());
        holder.tvItemSeq.setText(itemModel.getItemSeq());

        holder.clContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, MedicineDetailActivity.class);
                intent.putExtra("item", itemModel);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(intent);
                Toast.makeText(context, itemModel.getItemName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addAll(ArrayList<Item> itemArrayList) {
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
