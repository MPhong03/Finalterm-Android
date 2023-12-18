package project.finalterm.quizapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import project.finalterm.quizapp.Activity.FolderDetailActivity;
import project.finalterm.quizapp.Data.Folder;
import project.finalterm.quizapp.Data.Topic;
import project.finalterm.quizapp.R;

public class ChoiceAdapter extends RecyclerView.Adapter<ChoiceAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {
        private Button choice;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            choice = itemView.findViewById(R.id.choiceButton);
        }
    }
    private Context context;
    private ArrayList<String> choices;
    private OnItemClickListener mListener;
    private int correctAnswerPosition = -1;
    private int selectedPosition = -1;
    private boolean buttonsEnabled = true;
    public ChoiceAdapter(Context context, ArrayList<String> choices) {
        this.context = context;
        this.choices = choices;
    }
    public void disableAllButtonsDuringDelay(int timeGap) {
        buttonsEnabled = false;
        notifyDataSetChanged();
        new Handler().postDelayed(() -> {
            buttonsEnabled = true;
            notifyDataSetChanged();
        }, timeGap);
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setCorrectAnswerPosition(String choice) {
        correctAnswerPosition = choices.indexOf(choice);
    }
    public void updateButtonColor(int position) {
        selectedPosition = position;
        notifyItemChanged(position);

        if (position == correctAnswerPosition) {
            notifyItemChanged(correctAnswerPosition);
        }
    }
    public void resetButtonColors() {
        selectedPosition = -1;
        correctAnswerPosition = -1;
        notifyDataSetChanged();
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ChoiceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View choiceView = inflater.inflate(R.layout.choice_item, parent, false);
        ChoiceAdapter.ViewHolder viewHolder = new ChoiceAdapter.ViewHolder(choiceView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChoiceAdapter.ViewHolder holder, int position) {
        String choice = choices.get(position);

        holder.choice.setEnabled(buttonsEnabled);

        holder.choice.setText(choice);

        if (selectedPosition == position) {
            if (position == correctAnswerPosition) {
                holder.choice.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.success)));
            } else {
                holder.choice.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.danger)));
            }
        } else if (position == correctAnswerPosition) {
            holder.choice.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.success)));
        } else {
            holder.choice.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.purple)));
        }

        holder.choice.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onItemClick(position);
                updateButtonColor(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return choices.size();
    }

    public String getChoice(int position) {
        if (position >= 0 && position < choices.size()) {
            return choices.get(position);
        } else {
            return null;
        }
    }

    public void setChoices(ArrayList<String> newChoices) {
        if (newChoices != null) {
            choices.clear();
            choices.addAll(newChoices);
            notifyDataSetChanged();
        }
    }

}
