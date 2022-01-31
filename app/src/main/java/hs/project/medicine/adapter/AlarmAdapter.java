package hs.project.medicine.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hs.project.medicine.MediApplication;
import hs.project.medicine.R;
import hs.project.medicine.activitys.UserDetailActivity;
import hs.project.medicine.databinding.ItemAlarmBinding;
import hs.project.medicine.datas.Alarm;
import hs.project.medicine.datas.User;
import hs.project.medicine.dialog.ModifyAlarmDialog;
import hs.project.medicine.dialog.ModifyUserDialog;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Alarm> items = new ArrayList<>();

    public AlarmAdapter(Context context) {
        this.context = context;
    }

    public interface OnEventListener {
        void onRemoveClick(View view, int position);

        void onModifyClick(View view, int position);
    }

    public void setOnEventListener(OnEventListener eventListener) {
        this.eventListener = eventListener;
    }

    private OnEventListener eventListener;

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

    public void removeAt(int position) {
        items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size());
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
                itemBinding.ivRemove.setSelected(true);
            } else {
                itemBinding.tvName.setSelected(false);
                itemBinding.tvAmPm.setSelected(false);
                itemBinding.tvDayWeek.setSelected(false);
                itemBinding.tvTime.setSelected(false);
                itemBinding.ivAlarm.setSelected(false);
                itemBinding.ivRemove.setSelected(false);
            }

            itemBinding.clContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        eventListener.onModifyClick(v, position);
                    }
                }
            });

            itemBinding.switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        itemBinding.tvName.setSelected(true);
                        itemBinding.tvAmPm.setSelected(true);
                        itemBinding.tvDayWeek.setSelected(true);
                        itemBinding.tvTime.setSelected(true);
                        itemBinding.ivAlarm.setSelected(true);
                        itemBinding.ivRemove.setSelected(true);
                    } else {
                        itemBinding.tvName.setSelected(false);
                        itemBinding.tvAmPm.setSelected(false);
                        itemBinding.tvDayWeek.setSelected(false);
                        itemBinding.tvTime.setSelected(false);
                        itemBinding.ivAlarm.setSelected(false);
                        itemBinding.ivRemove.setSelected(false);
                    }
                }
            });

            itemBinding.liRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        eventListener.onRemoveClick(v, position);
                    }
                }
            });
        }
    }
}
