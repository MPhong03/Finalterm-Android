package project.finalterm.quizapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import project.finalterm.quizapp.Adapter.FlashCardAdapter;
import project.finalterm.quizapp.Adapter.WordAdapter;
import project.finalterm.quizapp.Auth.LoginActivity;
import project.finalterm.quizapp.Data.Topic;
import project.finalterm.quizapp.Data.Word;
import project.finalterm.quizapp.R;
import project.finalterm.quizapp.Utils.ExcelUtil;
import project.finalterm.quizapp.ViewModel.AuthViewModel;
import project.finalterm.quizapp.ViewModel.CreateTopicViewModel;
import project.finalterm.quizapp.ViewModel.TopicViewModel;
import project.finalterm.quizapp.ViewModel.WordViewModel;

public class EditTopicActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_EXCEL_IMPORT = 101;
    private CreateTopicViewModel createTopicViewModel;
    private AuthViewModel authViewModel;
    private WordViewModel wordViewModel;
    private TopicViewModel topicViewModel;
    private TextInputEditText topicTitle, topicSubtitle, topicDescription;
    private RecyclerView recyclerView;
    private WordAdapter adapter;
    private ArrayList<Word> words;
    private Button saveTopicButton;
    private Switch topicStatus;
    private CircularProgressIndicator progressIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_topic);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        authViewModel.getCurrentUser().observe(this, firebaseUser -> {
            if (firebaseUser == null) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        createTopicViewModel = new ViewModelProvider(this).get(CreateTopicViewModel.class);
        wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        topicViewModel = new ViewModelProvider(this).get(TopicViewModel.class);

        topicTitle = findViewById(R.id.titleEditTopic);
        topicSubtitle = findViewById(R.id.subtitleEditTopic);
        topicDescription = findViewById(R.id.descriptionEditTopic);
        topicStatus = findViewById(R.id.statusEditTopic);

        recyclerView = findViewById(R.id.editWordRecyclerView);
        words = new ArrayList<Word>();
        adapter = new WordAdapter(this, words, authViewModel.getCurrentUserId());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        saveTopicButton = findViewById(R.id.saveTopic);
        saveTopicButton.setOnClickListener(v -> createTopic());

        loadTopic();

        observeWords();
        observeCurrentUser();

        topicStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                topicStatus.setText("Public");
            } else {
                topicStatus.setText("Private");
            }
        });
    }

    private void loadTopic() {
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        String topicId = intent.getStringExtra("topicId");
        topicViewModel.getUserTopicByTopicIdAndUserId(userId, topicId).observe(this, topic -> {
            if (topic != null) {
                topicTitle.setText(topic.getTitle());
                topicSubtitle.setText(topic.getSubtitle());
                topicDescription.setText(topic.getDescription());
                topicStatus.setChecked(topic.isPublicState());

//                wordViewModel.setInitialWords(topic.getWords());
                adapter.setWords(topic.getWords());
                Log.d("SIZE_WORDS", topic.getWords().size() + "");
            } else {
                Log.e("ERROR", "Failed to fetch topic");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_topic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.newWordItemEdit) {
            showAddWordDialog();
            return true;
        } else if (item.getItemId() == R.id.importMoreWordItem) {
            // IMPORT WORD BY CSV
            openFilePicker();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select Excel File"), REQUEST_CODE_EXCEL_IMPORT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EXCEL_IMPORT && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                String mimeType = getContentResolver().getType(data.getData());
                Log.d("MIME_TYPE", "Selected file MIME type: " + mimeType);

                if (mimeType != null && (mimeType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))) {

                    importWordsFromFile(data.getData());
                } else {
                    Toast.makeText(this, "Please select an Excel file", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private void importWordsFromFile(Uri fileUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);

            if (inputStream != null) {
                ArrayList<Word> importedWords = ExcelUtil.importExcelFile(inputStream, authViewModel.getCurrentUserId());

                for (Word word : importedWords) {
                    Log.d("WORD", word.getTitle());
                    adapter.addWord(word);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAddWordDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.CustomMaterialAlertDialog);
        builder.setTitle("Add New Word");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_word, null);
        builder.setView(dialogView);

        EditText etTitle = dialogView.findViewById(R.id.editTextTitle);
        EditText etSubtitle = dialogView.findViewById(R.id.editTextSubtitle);
        EditText etDescription = dialogView.findViewById(R.id.editTextDescription);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            String subtitle = etSubtitle.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (!title.isEmpty() && !subtitle.isEmpty()) {
                Word newWord = new Word(title, subtitle, description, authViewModel.getCurrentUserId());

                adapter.addWord(newWord);
            } else {
                Toast.makeText(this, "Title and subtitle cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void observeCurrentUser() {
        createTopicViewModel.getCurrentUser().observe(this, firebaseUser -> {
            if (firebaseUser == null) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void observeWords() {
        wordViewModel.getWords().observe(this, observedWords -> {
            if (observedWords != null) {
                int previousSize = words.size();
                words.clear();
                words.addAll(observedWords);

                int newSize = words.size();
                if (newSize > previousSize) {
                    int insertedItemCount = newSize - previousSize;
                    adapter.notifyItemRangeInserted(previousSize, insertedItemCount);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void createTopic() {
        progressIndicator = findViewById(R.id.editTopicLoading);
        progressIndicator.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        String topicId = intent.getStringExtra("topicId");

        String title = topicTitle.getText().toString().trim();
        String subtitle = topicSubtitle.getText().toString().trim();
        String description = topicDescription.getText().toString().trim();
        boolean status = topicStatus.isChecked();

        ArrayList<Word> words = adapter.getWords();

        Topic updatedTopic = new Topic(topicId, title, subtitle, description, words, authViewModel.getCurrentUserId(), status);

        createTopicViewModel.updateTopic(userId, topicId, updatedTopic, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                progressIndicator.setVisibility(View.GONE);

                Toast.makeText(this, "Successfully updated topic", Toast.LENGTH_SHORT).show();

                Intent direct = new Intent(this, TopicDetailActivity.class);
                direct.putExtra("userId", userId);
                direct.putExtra("topicId", topicId);
                startActivity(direct);
                finish();
            } else {
                Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }
}