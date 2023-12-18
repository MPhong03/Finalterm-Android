package project.finalterm.quizapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Map;

import project.finalterm.quizapp.Data.Information;
import project.finalterm.quizapp.Data.Topic;
import project.finalterm.quizapp.R;

public class InformationAdapter extends RecyclerView.Adapter<InformationAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private Chip chip;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.infoProfileTitle);
            chip = itemView.findViewById(R.id.infoProfileChip);
        }
    }
    private Context context;
    private ArrayList<Information> values;

    public InformationAdapter(Context context, ArrayList<Information> values) {
        this.context = context;
        this.values = values;
    }

    @NonNull
    @Override
    public InformationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.infromation_profile_item, parent, false);
        InformationAdapter.ViewHolder viewHolder = new InformationAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull InformationAdapter.ViewHolder holder, int position) {
        Information value = values.get(position);

        holder.title.setText(value.getKey());
        holder.chip.setText(value.getValue());
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

}
