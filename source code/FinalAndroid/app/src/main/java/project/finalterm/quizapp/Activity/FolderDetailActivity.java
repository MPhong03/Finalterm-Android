package project.finalterm.quizapp.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.FirebaseDatabase;

import project.finalterm.quizapp.Adapter.TopicAdapter;
import project.finalterm.quizapp.Adapter.TopicsFolderAdapter;
import project.finalterm.quizapp.Auth.LoginActivity;
import project.finalterm.quizapp.R;
import project.finalterm.quizapp.ViewModel.AuthViewModel;
import project.finalterm.quizapp.ViewModel.FolderViewModel;

public class FolderDetailActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_ADD_TOPICS = 123;
    private AuthViewModel authViewModel;
    private FolderViewModel folderViewModel;
    private TextView title, description;
    private RecyclerView recyclerView;
    private TopicsFolderAdapter adapter;
    private TextView emptyMessage;
    private Button addTopic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_detail);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        folderViewModel = new ViewModelProvider(this).get(FolderViewModel.class);

        title = findViewById(R.id.titleFolder);
        description = findViewById(R.id.descriptionFolder);
        recyclerView = findViewById(R.id.topicsFolderDetail);
        emptyMessage = findViewById(R.id.emptyMessage);

        authViewModel.getCurrentUser().observe(this, firebaseUser -> {
            if (firebaseUser == null) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                fetchFolderDetail();
            }
        });

        addTopic = findViewById(R.id.addTopicToFolderButton);
        addTopic.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddTopicsToFolderActivity.class);
            intent.putExtra("userId", getIntent().getStringExtra("userId"));
            intent.putExtra("folderId", getIntent().getStringExtra("folderId"));
            startActivityForResult(intent, REQUEST_CODE_ADD_TOPICS);
        });

        title.setOnClickListener(v -> {
            openEditDialog("title");
        });

        description.setOnClickListener(v -> {
            openEditDialog("description");
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchFolderDetail();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ADD_TOPICS) {
            if (resultCode == RESULT_OK) {
                fetchFolderDetail();
            }
        }
    }

    private void fetchFolderDetail() {
        String userId = getIntent().getStringExtra("userId");
        String folderId = getIntent().getStringExtra("folderId");
        
        folderViewModel.getFolderByUserIdAndFolderId(userId, folderId).observe(this, folder -> {
            if (folder != null) {
                title.setText(folder.getTitle());
                description.setText(folder.getDescription());

                if (folder.getTopics() != null && !folder.getTopics().isEmpty()) {
                    adapter = new TopicsFolderAdapter(this, folder.getTopics(), userId, folderId, folderViewModel);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    recyclerView.setAdapter(adapter);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    emptyMessage.setVisibility(View.VISIBLE);
                    emptyMessage.setText("Folder hasn't added any topics yet");
                }
            } else {
                Toast.makeText(this, "Folder not found!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    private void openEditDialog(String fieldType) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.CustomMaterialAlertDialog);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_folder, null);
        builder.setTitle("Update " + fieldType)
                .setView(dialogView);

        TextInputEditText editText;
        TextInputLayout inputLayout;

        if (fieldType.equals("title")) {
            editText = dialogView.findViewById(R.id.titleEditFolder);
            editText.setVisibility(View.VISIBLE);
            inputLayout = dialogView.findViewById(R.id.editFolderTitleLayout);
            editText.setText(title.getText());
        } else {
            editText = dialogView.findViewById(R.id.descriptionEditFolder);
            editText.setVisibility(View.VISIBLE);
            inputLayout = dialogView.findViewById(R.id.editFolderDescripLayout);
            editText.setText(description.getText());
        }

        String userId = getIntent().getStringExtra("userId");
        String folderId = getIntent().getStringExtra("folderId");

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newText = editText.getText().toString().trim();
            if (!newText.isEmpty()) {
                if (fieldType.equals("title")) {
                    folderViewModel.updateFolderTitle(userId, folderId, newText, (databaseError, databaseReference) -> {
                        if (databaseError == null) {
                            Toast.makeText(this, "Successfully update folder", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
                        }
                    });
                    title.setText(newText);
                } else {
                    folderViewModel.updateFolderDescription(userId, folderId, newText, (databaseError, databaseReference) -> {
                        if (databaseError == null) {
                            Toast.makeText(this, "Successfully update folder", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
                        }
                    });
                    description.setText(newText);
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}