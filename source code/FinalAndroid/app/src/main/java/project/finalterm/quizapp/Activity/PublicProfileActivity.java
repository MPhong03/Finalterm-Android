package project.finalterm.quizapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import project.finalterm.quizapp.Adapter.TopicPublicAdapter;
import project.finalterm.quizapp.Adapter.TopicPublicProfileAdapter;
import project.finalterm.quizapp.R;
import project.finalterm.quizapp.ViewModel.AuthViewModel;
import project.finalterm.quizapp.ViewModel.TopicViewModel;

public class PublicProfileActivity extends AppCompatActivity {
    private String userId;
    private TopicViewModel topicViewModel;
    private AuthViewModel authViewModel;
    private CircleImageView avatar;
    private TextView username;
    private RecyclerView recyclerView;
    private TopicPublicProfileAdapter adapter;
    private TextView message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_profile);

        userId = getIntent().getStringExtra("userId");

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        topicViewModel = new ViewModelProvider(this).get(TopicViewModel.class);

        avatar = findViewById(R.id.photoPublicProfile);
        username = findViewById(R.id.userPublicProfile);
        recyclerView = findViewById(R.id.recyclerViewTopicPublicProfile);
        message = findViewById(R.id.messageEmptyTopicsPublicProfile);

        fetchProfile(userId);
    }
    private void fetchProfile(String userId) {
        authViewModel.getUserById(userId).observe(this, userData -> {
            if (userData != null) {
                username.setText(userData.getDisplayName());

                Glide.with(this)
                        .load(Uri.parse(userData.getPhotoUrl()))
                        .placeholder(R.drawable.handle_profile)
                        .into(avatar);

                topicViewModel.getUserPublicTopics(userId).observe(this, topics -> {
                    if (topics != null && !topics.isEmpty()) {
                        adapter = new TopicPublicProfileAdapter(this, topics, userId);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));

                        adapter.notifyDataSetChanged();

                        Log.d("TOPICS", "Fetched!");
                    } else {
                        message.setVisibility(View.VISIBLE);
                        message.setText(userData.getDisplayName() + " has not added any topic yet.");
                        Log.d("TOPICS", "Empty!");
                    }
                });

            } else {
                Log.e("ERROR", "Failed to fetch user");
            }
        });
    }
}