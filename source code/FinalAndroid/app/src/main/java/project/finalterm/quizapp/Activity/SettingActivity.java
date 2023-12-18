package project.finalterm.quizapp.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import project.finalterm.quizapp.Adapter.SettingAdapter;
import project.finalterm.quizapp.Adapter.SettingProfileAdapter;
import project.finalterm.quizapp.Auth.LoginActivity;
import project.finalterm.quizapp.Auth.VerifyEmailActivity;
import project.finalterm.quizapp.R;
import project.finalterm.quizapp.Utils.SettingUtils;
import project.finalterm.quizapp.ViewModel.AuthViewModel;

public class SettingActivity extends AppCompatActivity {
    private static final String KEY_FLASHCARD_FLIP_TIME = "flashcard_flip_time";
    private static final String KEY_READ_FLASHCARD_TIME = "read_flashcard_time";
    private static final String KEY_FLASHCARD_INTERVAL_TIME = "flashcard_interval_time";

    private AuthViewModel authViewModel;
    private ListView listView;
    private ListView listViewProfile;
    private SettingAdapter adapter;
    private SettingProfileAdapter profileAdapter;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.getCurrentUser().observe(this, firebaseUser -> {
            if (firebaseUser == null) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        listView = findViewById(R.id.settingListView);
        listViewProfile = findViewById(R.id.settingProfileListView);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        getSetting();
        getProfileSetting();
    }
    private void getSetting() {
        ArrayList<String> settingsList = new ArrayList<>();
        settingsList.add("Time before flashcard flip");
        settingsList.add("Time before read flashcard");
        settingsList.add("Time between flashcards");

        HashMap<String, Long> settingsValues = new HashMap<>();
        if (SettingUtils.settingExists(this, KEY_FLASHCARD_FLIP_TIME)) {
            settingsValues.put(settingsList.get(0), sharedPreferences.getLong(KEY_FLASHCARD_FLIP_TIME, TimeUnit.SECONDS.toMillis(5)));
        } else {
            settingsValues.put(settingsList.get(0), TimeUnit.SECONDS.toMillis(5));
        }

        if (SettingUtils.settingExists(this, KEY_READ_FLASHCARD_TIME)) {
            settingsValues.put(settingsList.get(1), sharedPreferences.getLong(KEY_READ_FLASHCARD_TIME, TimeUnit.SECONDS.toMillis(5)));
        } else {
            settingsValues.put(settingsList.get(1), TimeUnit.SECONDS.toMillis(5));
        }

        if (SettingUtils.settingExists(this, KEY_FLASHCARD_INTERVAL_TIME)) {
            settingsValues.put(settingsList.get(2), sharedPreferences.getLong(KEY_FLASHCARD_INTERVAL_TIME, TimeUnit.SECONDS.toMillis(5)));
        } else {
            settingsValues.put(settingsList.get(2), TimeUnit.SECONDS.toMillis(5));
        }


        adapter = new SettingAdapter(this, settingsList, settingsValues);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String setting = settingsList.get(position);
            String key = getKey(setting);

            showEditDialog(setting, key);
        });
    }

    private void getProfileSetting() {
        ArrayList<String> profileSettingsList = new ArrayList<>();
        profileSettingsList.add("Change Profile");
        profileSettingsList.add("Change Password");

        profileAdapter = new SettingProfileAdapter(this, profileSettingsList);
        listViewProfile.setAdapter(profileAdapter);

        listViewProfile.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0:
                    startActivity(new Intent(this, ProfileActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(this, VerifyEmailActivity.class));
                    break;
                default:
                    break;
            }
        });
    }

    private void showEditDialog(String settingTitle, String settingKey) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.CustomMaterialAlertDialog);
        builder.setTitle(settingTitle);

        EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("Enter time in seconds");

        long storedValueInSeconds = sharedPreferences.getLong(settingKey, TimeUnit.SECONDS.toMillis(5));
        editText.setText(String.valueOf(storedValueInSeconds / 1000)); // Hiển thị giây

        builder.setView(editText);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String input = editText.getText().toString();
            if (!TextUtils.isEmpty(input)) {
                long timeInSeconds = Long.parseLong(input);
                long timeInMilliseconds = TimeUnit.SECONDS.toMillis(timeInSeconds);
                sharedPreferences.edit().putLong(settingKey, timeInMilliseconds).apply();
                adapter.updateSettingValue(settingTitle, timeInMilliseconds);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private String getKey(String setting) {
        switch (setting) {
            case "Time before flashcard flip":
                return KEY_FLASHCARD_FLIP_TIME;
            case "Time before read flashcard":
                return KEY_READ_FLASHCARD_TIME;
            case "Time between flashcards":
                return KEY_FLASHCARD_INTERVAL_TIME;
            default:
                return "";
        }
    }

}