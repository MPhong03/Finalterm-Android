package project.finalterm.quizapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import project.finalterm.quizapp.Activity.EditTopicActivity;
import project.finalterm.quizapp.Activity.TopicDetailActivity;
import project.finalterm.quizapp.Data.Topic;
import project.finalterm.quizapp.Data.Word;
import project.finalterm.quizapp.R;
import project.finalterm.quizapp.ViewModel.TopicViewModel;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder> {
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
    private TopicViewModel topicViewModel;
    public TopicAdapter(Context context, ArrayList<Topic> topics, String userId, TopicViewModel topicViewModel) {
        this.context = context;
        this.topics = topics;
        this.userId = userId;
        this.topicViewModel = topicViewModel;
    }

    @NonNull
    @Override
    public TopicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View topicView = inflater.inflate(R.layout.topic_item, parent, false);
        TopicAdapter.ViewHolder viewHolder = new TopicAdapter.ViewHolder(topicView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TopicAdapter.ViewHolder holder, int position) {
        Topic topic = topics.get(position);

        holder.title.setText(topic.getTitle());
        holder.subtitle.setText(topic.getSubtitle());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TopicDetailActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("topicId", topic.getId());
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, v);
            popup.getMenuInflater().inflate(R.menu.topics_context_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.editTopic) {
                    Intent intent = new Intent(context, EditTopicActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("topicId", topic.getId());
                    context.startActivity(intent);

                    return true;
                } else if (item.getItemId() == R.id.deleteTopic) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.CustomMaterialAlertDialog);
                    builder.setTitle("Confirm Delete");
                    builder.setMessage("Are you sure you want to delete this topic?");

                    builder.setPositiveButton("Yes", (dialog, which) -> {
                        String topicId = topics.get(position).getId();

                        topicViewModel.deleteTopic(userId, topicId, (databaseError, databaseReference) -> {
                            if (databaseError == null) {
                                Toast.makeText(context, "Successfully deleted topic", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });

                    builder.setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                    });

                    builder.show();

                    return true;
                } else {
                    return false;
                }
            });


            popup.show();
            return true;
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
