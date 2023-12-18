package project.finalterm.quizapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import project.finalterm.quizapp.Activity.EditTopicActivity;
import project.finalterm.quizapp.Activity.TopicDetailActivity;
import project.finalterm.quizapp.Data.Topic;
import project.finalterm.quizapp.R;

public class TopicSelectionAdapter extends RecyclerView.Adapter<TopicSelectionAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView subtitle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleTopicItem);
            subtitle = itemView.findViewById(R.id.subtitleTopicItem);
        }
    }
    private Context context;
    private ArrayList<Topic> topics;
    private ArrayList<Topic> selectedTopics = new ArrayList<>();
    public TopicSelectionAdapter(Context context, ArrayList<Topic> topics) {
        this.context = context;
        this.topics = topics;
    }

    @NonNull
    @Override
    public TopicSelectionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View topicView = inflater.inflate(R.layout.topic_item, parent, false);
        TopicSelectionAdapter.ViewHolder viewHolder = new TopicSelectionAdapter.ViewHolder(topicView);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull TopicSelectionAdapter.ViewHolder holder, int position) {
        Topic topic = topics.get(position);

        holder.title.setText(topic.getTitle());
        holder.subtitle.setText(topic.getSubtitle());

        holder.itemView.setOnClickListener(view -> {
            if (selectedTopics.contains(topic)) {
                selectedTopics.remove(topic);
                // Update UI
                deselectTopic(holder);
            } else {
                selectedTopics.add(topic);
                // Update UI
                selectTopic(holder);
            }
        });
    }

    private void deselectTopic(ViewHolder holder) {
//        holder.itemView.setBackgroundResource(R.drawable.deselected_border);
        MaterialCardView card = (MaterialCardView) holder.itemView;
//        card.setStrokeWidth((int) (context.getResources().getDisplayMetrics().density * 2));
        card.setChecked(false);
    }

    private void selectTopic(ViewHolder holder) {
//        holder.itemView.setBackgroundResource(R.drawable.selected_border);
        MaterialCardView card = (MaterialCardView) holder.itemView;
//        card.setStrokeWidth(0);
        card.setChecked(true);
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public ArrayList<Topic> getSelectedTopics() {
        return selectedTopics;
    }

    public void setTopics(ArrayList<Topic> newTopics) {
        if (newTopics != null) {
            topics.clear();
            topics.addAll(newTopics);
            notifyDataSetChanged();
        }
    }
}
