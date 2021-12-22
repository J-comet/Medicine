package hs.project.medicine.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hs.project.medicine.R;
import hs.project.medicine.activitys.MedicineDetailActivity;
import hs.project.medicine.databinding.ItemMedicineBinding;
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
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_medicine, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item itemModel = items.get(position);
        holder.bindItem(itemModel);
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

        ItemMedicineBinding itemBinding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemBinding = ItemMedicineBinding.bind(itemView);
        }

        void bindItem(Item medicineItem) {
            itemBinding.tvEntpName.setText(medicineItem.getEntpName());
            itemBinding.tvItemName.setText(medicineItem.getItemName());
            itemBinding.tvItemSeq.setText(medicineItem.getItemSeq());

            itemBinding.clContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, MedicineDetailActivity.class);
                    intent.putExtra("item", medicineItem);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    Toast.makeText(context, medicineItem.getItemName(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

}
