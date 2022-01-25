package hs.project.medicine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hs.project.medicine.R;
import hs.project.medicine.databinding.ItemAlarmBinding;
import hs.project.medicine.datas.Alarm;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Alarm> items = new ArrayList<>();

    public AlarmAdapter(Context context) {
        this.context = context;
    }

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


    public void addAll(ArrayList<Alarm> itemArrayList) {
        if (items != null) {
            items.clear();
        }
        this.items.addAll(itemArrayList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemAlarmBinding itemBinding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemBinding = ItemAlarmBinding.bind(itemView);
        }

        void bindItem(Alarm alarmItem) {
            itemBinding.tvName.setText(alarmItem.getName());
            itemBinding.tvAmPm.setText(alarmItem.getAmPm());
            itemBinding.tvDayWeek.setText(alarmItem.getDayOfWeek());
            itemBinding.tvTime.setText(alarmItem.getHour() + " : " + alarmItem.getMinute());

            if (alarmItem.isAlarmON()) {
                itemBinding.tvName.setSelected(true);
                itemBinding.tvAmPm.setSelected(true);
                itemBinding.tvDayWeek.setSelected(true);
                itemBinding.tvTime.setSelected(true);
                itemBinding.ivAlarm.setSelected(true);
            } else {
                itemBinding.tvName.setSelected(false);
                itemBinding.tvAmPm.setSelected(false);
                itemBinding.tvDayWeek.setSelected(false);
                itemBinding.tvTime.setSelected(false);
                itemBinding.ivAlarm.setSelected(false);
            }

            itemBinding.switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        itemBinding.tvName.setSelected(true);
                        itemBinding.tvAmPm.setSelected(true);
                        itemBinding.tvDayWeek.setSelected(true);
                        itemBinding.tvTime.setSelected(true);
                        itemBinding.ivAlarm.setSelected(true);
                    } else {
                        itemBinding.tvName.setSelected(false);
                        itemBinding.tvAmPm.setSelected(false);
                        itemBinding.tvDayWeek.setSelected(false);
                        itemBinding.tvTime.setSelected(false);
                        itemBinding.ivAlarm.setSelected(false);
                    }
                }
            });
        }
    }
}
