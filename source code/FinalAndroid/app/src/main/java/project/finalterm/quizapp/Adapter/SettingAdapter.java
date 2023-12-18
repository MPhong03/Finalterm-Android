package project.finalterm.quizapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import project.finalterm.quizapp.R;

public class SettingAdapter extends BaseAdapter {
    private ArrayList<String> settingsList;
    private HashMap<String, Long> settingsValues;
    private Context context;

    public SettingAdapter(Context context, ArrayList<String> settingsList, HashMap<String, Long> settingsValues) {
        this.context = context;
        this.settingsList = settingsList;
        this.settingsValues = settingsValues;
    }

    @Override
    public int getCount() {
        return settingsList.size();
    }

    @Override
    public Object getItem(int position) {
        return settingsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.setting_item, parent, false);
        }

        String setting = settingsList.get(position);
        long valueInMilliseconds = settingsValues.get(setting);
        long valueInSeconds = TimeUnit.MILLISECONDS.toSeconds(valueInMilliseconds);

        TextView titleTextView = convertView.findViewById(R.id.settingTitle);
        TextView valueTextView = convertView.findViewById(R.id.settingValue);

        titleTextView.setText(setting);
        valueTextView.setText(valueInSeconds + "");

        return convertView;
    }

    public void updateSettingValue(String setting, long newValueInMilliseconds) {
        settingsValues.put(setting, newValueInMilliseconds);
        notifyDataSetChanged();
    }


}

