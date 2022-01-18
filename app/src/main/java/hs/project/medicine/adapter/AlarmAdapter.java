package hs.project.medicine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hs.project.medicine.R;
import hs.project.medicine.databinding.ItemAlarmBinding;
import hs.project.medicine.datas.Alarm;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Alarm> items = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_alarm, parent, false);
        return new AlarmAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Alarm alarmModel = items.get(position);
        holder.bindItem(alarmModel);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemAlarmBinding itemBinding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemBinding = ItemAlarmBinding.bind(itemView);
        }

        void bindItem(Alarm alarmItem) {

        }
    }
}
