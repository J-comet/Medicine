package hs.project.medicine.adapter;

import static hs.project.medicine.activitys.MainActivity.mainActivityContext;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import hs.project.medicine.Config;
import hs.project.medicine.R;
import hs.project.medicine.activitys.MainActivity;
import hs.project.medicine.databinding.ItemAlarmBinding;
import hs.project.medicine.datas.Alarm;
import hs.project.medicine.util.LogUtil;
import hs.project.medicine.util.PreferenceUtil;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Alarm> items = new ArrayList<>();
    private ArrayList<String> strAlarmList;

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

        holder.itemBinding.switchView.setOnCheckedChangeListener(null);
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

            Locale locale = context.getResources().getConfiguration().locale;
            String strTime = String.format(locale, "%02d : %02d", Integer.parseInt(alarmItem.getHour()), Integer.parseInt(alarmItem.getMinute()));
            itemBinding.tvTime.setText(strTime);

            LogUtil.d(alarmItem.getName() + "/" + alarmItem.isAlarmON());

            if (alarmItem.isAlarmON()) {
                itemBinding.switchView.setChecked(true);
                itemBinding.tvName.setSelected(true);
                itemBinding.tvAmPm.setSelected(true);
                itemBinding.tvDayWeek.setSelected(true);
                itemBinding.tvTime.setSelected(true);
                itemBinding.ivAlarm.setSelected(true);
                itemBinding.ivRemove.setSelected(true);
            } else {
                itemBinding.switchView.setChecked(false);
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

                    /**
                     * 1. 스위치 ON or OFF 일 때마다 Preference 갱신
                     */
                    final int position = getAdapterPosition();

                    strAlarmList = new ArrayList<>();

                    if (isChecked) {
                        Toast.makeText(context, items.get(position).getName() + " 알람 활성", Toast.LENGTH_SHORT).show();
                    }

                    for (int i = 0; i < items.size(); i++) {
                        Alarm alarm = new Alarm();
                        alarm.setName(items.get(i).getName());
                        alarm.setAmPm(items.get(i).getAmPm());
                        alarm.setDayOfWeek(items.get(i).getDayOfWeek());
                        alarm.setHour(items.get(i).getHour());
                        alarm.setMinute(items.get(i).getMinute());
                        alarm.setVolume(items.get(i).getVolume());
                        alarm.setRingtoneName(items.get(i).getRingtoneName());
                        alarm.setRingtoneUri(items.get(i).getRingtoneUri());

                        if (position == i) {
                            if (isChecked) {
                                alarm.setAlarmON(true);
                            } else {
                                alarm.setAlarmON(false);
                            }
                        } else {
                            alarm.setAlarmON(items.get(i).isAlarmON());
                        }

//                    if (position == i) {
//                        alarm.setAlarmON(isChecked);
//                    }

                        strAlarmList.add(alarm.toJSON());
                    }

                    // Preference 에 저장
                    PreferenceUtil.setJSONArrayPreference(context, Config.PREFERENCE_KEY.ALARM_LIST, strAlarmList);
                    ((MainActivity) mainActivityContext).startDayOfWeekService();

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
