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
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import project.finalterm.quizapp.Activity.EditTopicActivity;
import project.finalterm.quizapp.Activity.TopicDetailActivity;
import project.finalterm.quizapp.Data.Topic;
import project.finalterm.quizapp.R;
import project.finalterm.quizapp.ViewModel.FolderViewModel;
import project.finalterm.quizapp.ViewModel.TopicViewModel;

public class TopicsFolderAdapter extends RecyclerView.Adapter<TopicsFolderAdapter.ViewHolder> {
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
    private String folderId;
    private FolderViewModel folderViewModel;
    public TopicsFolderAdapter(Context context, ArrayList<Topic> topics, String userId, String folderId, FolderViewModel folderViewModel) {
        this.context = context;
        this.topics = topics;
        this.userId = userId;
        this.folderId = folderId;
        this.folderViewModel = folderViewModel;
    }

    @NonNull
    @Override
    public TopicsFolderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View topicView = inflater.inflate(R.layout.topic_item, parent, false);
        TopicsFolderAdapter.ViewHolder viewHolder = new TopicsFolderAdapter.ViewHolder(topicView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TopicsFolderAdapter.ViewHolder holder, int position) {
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
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Confirm Remove");
            builder.setMessage("Are you sure you want to remove this topic from folder?");

            builder.setPositiveButton("Yes", (dialog, which) -> {
                String topicId = topics.get(position).getId();

                folderViewModel.removeTopicFromFolder(userId, folderId, topicId, (databaseError, databaseReference) -> {
                    if (databaseError == null) {
                        Toast.makeText(context, "Topic removed from folder", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to remove topic", Toast.LENGTH_SHORT).show();
                    }
                });

                topics.remove(topic);
                notifyDataSetChanged();
            });

            builder.setNegativeButton("No", (dialog, which) -> {
                dialog.dismiss();
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

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
