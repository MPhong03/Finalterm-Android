package project.finalterm.quizapp.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import project.finalterm.quizapp.Adapter.InformationAdapter;
import project.finalterm.quizapp.Auth.LoginActivity;
import project.finalterm.quizapp.Data.Information;
import project.finalterm.quizapp.Data.User;
import project.finalterm.quizapp.R;
import project.finalterm.quizapp.ViewModel.AuthViewModel;
import project.finalterm.quizapp.ViewModel.ProgressViewModel;

public class ProfileActivity extends AppCompatActivity {
    private int IMAGE_PICKER_REQUEST_CODE = 101;
    private AuthViewModel authViewModel;
    private ProgressViewModel progressViewModel;
    private CircleImageView photo;
    private TextView username, uid;
    private TextInputEditText email;
    private Button saveProfile;
    private User user;
    private Uri currentPhoto;
    private RecyclerView recyclerView;
    private InformationAdapter adapter;
    private ArrayList<Information> informations = new ArrayList<>();
    private TextView message;
    private CircularProgressIndicator progressIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        progressViewModel = new ViewModelProvider(this).get(ProgressViewModel.class);

        photo = findViewById(R.id.photoProfile);
        username = findViewById(R.id.usernameProfile);
        email = findViewById(R.id.emailProfile);
        saveProfile = findViewById(R.id.saveProfile);
        uid = findViewById(R.id.uidProfile);
        recyclerView = findViewById(R.id.infoRecyclerView);
        message = findViewById(R.id.messageOfInformation);
        progressIndicator = findViewById(R.id.editProfileLoading);

        authViewModel.getCurrentUser().observe(this, firebaseUser -> {
            if (firebaseUser == null) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                bindUserProfile(firebaseUser);
            }
        });

        photo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, IMAGE_PICKER_REQUEST_CODE);
        });

        username.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.CustomMaterialAlertDialog);
            builder.setTitle("Change Username");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", (dialog, which) -> {
                String newUsername = input.getText().toString().trim();
                username.setText(newUsername);
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();
        });

        saveProfile.setOnClickListener(v -> {
            progressIndicator.setVisibility(View.VISIBLE);

            authViewModel.updateUserProfile(
                    currentPhoto,
                    username.getText().toString(),
                    email.getText().toString()
            );

        });

        authViewModel.getProfileUpdateStatus().observe(this, isSuccessful -> {
            if (isSuccessful != null) {
                if (isSuccessful) {
                    progressIndicator.setVisibility(View.GONE);
                } else {
                    progressIndicator.setVisibility(View.GONE);
                }
            }
        });

        observeProfileUpdateStatus();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri selectedImageUri = data.getData();
                currentPhoto = selectedImageUri;
                Glide.with(this)
                        .load(selectedImageUri)
                        .placeholder(R.drawable.handle_profile)
                        .into(photo);
            }
        }
    }
    private void bindUserProfile(FirebaseUser user) {
        Glide.with(this)
                .load(user.getPhotoUrl())
                .placeholder(R.drawable.handle_profile)
                .into(photo);
        if (user.getDisplayName().isEmpty()) {
            username.setText("Click to set name");
        } else {
            username.setText(user.getDisplayName());
        }

        email.setText(user.getEmail());
        uid.setText("UID: " + user.getUid());

        fetchUserData(user.getUid());
    }
    private void fetchUserData(String userId) {
        progressViewModel.getProgressByUserId(userId).observe(this, progress -> {
            if (progress != null) {
                if (progress.getScoreQuiz() != null) {
                    Information infoA = new Information(
                            "Multiple choices done",
                            progress.getScoreQuiz().size() + ((progress.getScoreQuiz().size() > 1) ? " tests" : " test")
                    );
                    Information infoB = new Information(
                            "Multiple choices accumulated",
                            calculateScore(progress.getScoreQuiz()) + " points"
                    );
                    informations.add(infoA);
                    informations.add(infoB);
                }

                if (progress.getScoreMatch() != null) {
                    Information infoC = new Information(
                            "Words matching done",
                            progress.getScoreMatch().size() + ((progress.getScoreMatch().size() > 1) ? " tests" : " test")
                    );

                    Information infoD = new Information(
                            "Words matching accumulated",
                            calculateScore(progress.getScoreMatch()) + " points"
                    );
                    informations.add(infoC);
                    informations.add(infoD);
                }

                if ((progress.getScoreQuiz() != null) && (progress.getScoreMatch() != null)) {
                    Information infoE = new Information(
                            "Ranking points",
                            ((calculateScore(progress.getScoreQuiz()) + calculateScore(progress.getScoreMatch()))) + " points"
                    );
                    informations.add(infoE);
                }

                adapter = new InformationAdapter(this, informations);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
            } else {
                message.setVisibility(View.VISIBLE);
            }
        });
    }
    private void observeProfileUpdateStatus() {
        authViewModel.getProfileUpdateStatus().observe(this, isSuccessful -> {
            if (isSuccessful) {
                Toast.makeText(this, "Successfully updated profile", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static int calculateScore(Map<String, Integer> map) {
        int sum = 0;
        for (int value : map.values()) {
            sum += value;
        }
        return sum;
    }
}