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

public class SettingProfileAdapter extends BaseAdapter {
    private ArrayList<String> settingsList;
    private Context context;

    public SettingProfileAdapter(Context context, ArrayList<String> settingsList) {
        this.context = context;
        this.settingsList = settingsList;
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

        TextView titleTextView = convertView.findViewById(R.id.settingTitle);

        titleTextView.setText(setting);

        return convertView;
    }


}
