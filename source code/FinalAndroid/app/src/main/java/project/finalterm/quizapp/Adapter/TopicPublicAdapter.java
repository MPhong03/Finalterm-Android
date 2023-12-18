package project.finalterm.quizapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import project.finalterm.quizapp.Activity.TopicDetailActivity;
import project.finalterm.quizapp.Data.Topic;
import project.finalterm.quizapp.Dto.TopicPublic;
import project.finalterm.quizapp.R;

public class TopicPublicAdapter extends RecyclerView.Adapter<TopicPublicAdapter.ViewHolder> {
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView authorName;
        private Chip numOfWords;
        private CircleImageView avt;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleTopicPublicItem);
            authorName = itemView.findViewById(R.id.authorNameTopicPublicItem);
            numOfWords = itemView.findViewById(R.id.numOfWordTopicPublicItem);
            avt = itemView.findViewById(R.id.authorAvatarTopicPublicItem);
        }
    }

    private Context context;
    private ArrayList<TopicPublic> topics;

    public TopicPublicAdapter(Context context, ArrayList<TopicPublic> topics) {
        this.context = context;
        this.topics = topics;
    }

    @NonNull
    @Override
    public TopicPublicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View topicView = inflater.inflate(R.layout.topic_public_item, parent, false);
        TopicPublicAdapter.ViewHolder viewHolder = new TopicPublicAdapter.ViewHolder(topicView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TopicPublicAdapter.ViewHolder holder, int position) {
        TopicPublic topic = topics.get(position);

        holder.title.setText(topic.getTitle());
        holder.numOfWords.setText(topic.getNumOfWords());
        holder.authorName.setText(topic.getAuthorName());
        Glide.with(context)
                .load(Uri.parse(topic.getAvt()))
                .placeholder(R.drawable.handle_profile)
                .into(holder.avt);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TopicDetailActivity.class);
            intent.putExtra("topicId", topic.getId());
            intent.putExtra("userId", topic.getUserId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public void setTopics(ArrayList<TopicPublic> newTopics) {
        if (newTopics != null) {
            topics.clear();
            topics.addAll(newTopics);
            notifyDataSetChanged();
        }
    }
}
