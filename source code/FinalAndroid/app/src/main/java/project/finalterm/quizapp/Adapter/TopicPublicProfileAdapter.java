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

import java.util.ArrayList;

import project.finalterm.quizapp.Activity.EditTopicActivity;
import project.finalterm.quizapp.Activity.TopicDetailActivity;
import project.finalterm.quizapp.Data.Topic;
import project.finalterm.quizapp.R;
import project.finalterm.quizapp.ViewModel.TopicViewModel;

public class TopicPublicProfileAdapter extends RecyclerView.Adapter<TopicPublicProfileAdapter.ViewHolder> {
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
    private String userId;
    public TopicPublicProfileAdapter(Context context, ArrayList<Topic> topics, String userId) {
        this.context = context;
        this.topics = topics;
        this.userId = userId;
    }

    @NonNull
    @Override
    public TopicPublicProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View topicView = inflater.inflate(R.layout.topic_item, parent, false);
        TopicPublicProfileAdapter.ViewHolder viewHolder = new TopicPublicProfileAdapter.ViewHolder(topicView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TopicPublicProfileAdapter.ViewHolder holder, int position) {
        Topic topic = topics.get(position);

        holder.title.setText(topic.getTitle());
        holder.subtitle.setText(topic.getSubtitle());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TopicDetailActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("topicId", topic.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public void setTopics(ArrayList<Topic> newTopics) {
        if (newTopics != null) {
            topics.clear();
            topics.addAll(newTopics);
            notifyDataSetChanged();
        }
    }
}