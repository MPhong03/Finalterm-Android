package project.finalterm.quizapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import project.finalterm.quizapp.Adapter.TopicSelectionAdapter;
import project.finalterm.quizapp.Auth.LoginActivity;
import project.finalterm.quizapp.Data.Topic;
import project.finalterm.quizapp.R;
import project.finalterm.quizapp.ViewModel.AuthViewModel;
import project.finalterm.quizapp.ViewModel.FolderViewModel;
import project.finalterm.quizapp.ViewModel.TopicViewModel;

public class AddTopicsToFolderActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_ADD_TOPICS = 123;
    private AuthViewModel authViewModel;
    private TopicViewModel topicViewModel;
    private FolderViewModel folderViewModel;
    private RecyclerView recyclerView;
    private TopicSelectionAdapter adapter;
    private ArrayList<Topic> topics = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_topics_to_folder);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        topicViewModel = new ViewModelProvider(this).get(TopicViewModel.class);
        folderViewModel = new ViewModelProvider(this).get(FolderViewModel.class);

        authViewModel.getCurrentUser().observe(this, firebaseUser -> {
            if (firebaseUser == null) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        recyclerView = findViewById(R.id.topicSelection);
        adapter = new TopicSelectionAdapter(this, topics);
        fetchData();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_topics_selection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.addTopicToFolderItem) {
            // HANDLE ADD ALL SELECTION TOPIC TO FOLDER
            addSelectedTopicsToFolder();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void fetchData() {
        String folderId = getIntent().getStringExtra("folderId");
        String userId = getIntent().getStringExtra("userId");

        topicViewModel.getUserTopics(userId).observe(this, fetchedTopics -> {
            if (fetchedTopics != null) {
                folderViewModel.getFolderByUserIdAndFolderId(userId, folderId).observe(this, folder -> {
                    if (folder != null) {
                        if (folder.getTopics() != null && !folder.getTopics().isEmpty()) {
                            ArrayList<Topic> filteredTopics = new ArrayList<>(fetchedTopics);

                            for (Topic folderTopic : folder.getTopics()) {
                                for (Topic fetchedTopic : fetchedTopics) {
                                    if (fetchedTopic.getId().equals(folderTopic.getId())) {
                                        filteredTopics.remove(fetchedTopic);
                                        break;
                                    }
                                }
                            }

                            topics = filteredTopics;
                            adapter.setTopics(topics);
                        } else {
                            topics = fetchedTopics;
                            adapter.setTopics(topics);
                        }
                    } else {
                        Log.e("ERROR", "Folder is null");
                        topics = fetchedTopics;
                        adapter.setTopics(topics);
                    }
                });
            } else {
                Log.e("ERROR", "Cannot fetch data");
                topics = new ArrayList<>();
                adapter.setTopics(topics);
            }
        });
    }

    private void addSelectedTopicsToFolder() {
        String folderId = getIntent().getStringExtra("folderId");
        String userId = getIntent().getStringExtra("userId");

        folderViewModel.addTopicsToFolder(userId, folderId, adapter.getSelectedTopics()).observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, "Topics added to folder successfully", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Failed to add topics to folder", Toast.LENGTH_SHORT).show();
            }
        });
    }
}