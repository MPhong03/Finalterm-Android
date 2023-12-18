package project.finalterm.quizapp.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import project.finalterm.quizapp.Adapter.FlashCardAdapter;
import project.finalterm.quizapp.Auth.LoginActivity;
import project.finalterm.quizapp.R;
import project.finalterm.quizapp.Utils.SettingUtils;
import project.finalterm.quizapp.Utils.TextSpeechUtil;
import project.finalterm.quizapp.ViewModel.AuthViewModel;
import project.finalterm.quizapp.ViewModel.TopicViewModel;

public class FlashCardActivity extends AppCompatActivity {
    private AuthViewModel authViewModel;
    private TopicViewModel topicViewModel;
    private ViewPager2 viewPager;
    private FlashCardAdapter adapter;
    private String userId;
    private String topicId;
    private Button auto;
    private boolean isAutoLearning = false;
    private boolean isTransitioning = false;
    private int currentPosition = 0;
    private Handler handler = new Handler();
    private TextSpeechUtil util;
    private long DELAY_BEFORE_READING = 5000;
    private long DELAY_BEFORE_FLIP = 5000;
    private long DELAY_BETWEEN_CARDS = 5000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_card);

        if (SettingUtils.settingExists(this, SettingUtils.KEY_READ_FLASHCARD_TIME)) {
            DELAY_BEFORE_READING = SettingUtils.getReadFlashcardTime(this);
        }

        if (SettingUtils.settingExists(this, SettingUtils.KEY_FLASHCARD_FLIP_TIME)) {
            DELAY_BEFORE_FLIP = SettingUtils.getFlashcardFlipTime(this);
        }

        if (SettingUtils.settingExists(this, SettingUtils.KEY_FLASHCARD_INTERVAL_TIME)) {
            DELAY_BETWEEN_CARDS = SettingUtils.getFlashcardIntervalTime(this);
        }

        Log.d("TIME", DELAY_BEFORE_READING + " - " + DELAY_BEFORE_FLIP + " - " + DELAY_BETWEEN_CARDS);

        userId = getIntent().getStringExtra("userId");
        topicId = getIntent().getStringExtra("topicId");
        util = new TextSpeechUtil(this);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        topicViewModel = new ViewModelProvider(this).get(TopicViewModel.class);

        authViewModel.getCurrentUser().observe(this, firebaseUser -> {
            if (firebaseUser == null) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                fetchTopic();
            }
        });

        auto = findViewById(R.id.autoLearn);
        auto.setOnClickListener(v -> {
            if (!isAutoLearning) {
                startAutoLearning();
            } else {
                stopAutoLearning();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (util != null) {
            util.stopSpeech();
        }
    }
    private void fetchTopic() {
        topicViewModel.getUserTopicByTopicIdAndUserId(userId, topicId).observe(this, topic -> {
            if (topic != null) {

                viewPager = findViewById(R.id.flashCardFullViewPager);
                adapter = new FlashCardAdapter(this, topic.getWordsAsMap());
                viewPager.setAdapter(adapter);

                Log.d("RETRIEVED_TOPIC", topic.getTitle());
            } else {
                Log.e("ERROR", "Failed to fetch topic");
            }
        });
    }

    private void startAutoLearning() {
        isAutoLearning = true;
        currentPosition = 0;
        auto.setEnabled(false);
        startAutoLearningAtPosition(currentPosition);
    }

    private void stopAutoLearning() {
        isAutoLearning = false;
        handler.removeCallbacksAndMessages(null);
    }

    private void startAutoLearningAtPosition(int position) {

        handler.postDelayed(() -> {
            if (!isAutoLearning || isTransitioning || position >= adapter.getItemCount()) {
                stopAutoLearning();
                showCompletionDialog();
                return;
            }

            isTransitioning = true;

            FlashCardAdapter.CardViewHolder viewHolder = adapter.getViewHolderAtPosition(position);
            if (viewHolder != null) {
                util.readText(viewHolder.getFrontTextView().getText().toString());

                handler.postDelayed(() -> {
                    if (!isAutoLearning) {
                        return;
                    }

                    viewHolder.flipCard();

                    handler.postDelayed(() -> {
                        if (!isAutoLearning) {
                            return;
                        }

                        util.readText(viewHolder.getBackTextView().getText().toString());

                        handler.postDelayed(() -> {
                            currentPosition++;
                            viewPager.setCurrentItem(currentPosition, true);

                            isTransitioning = false;
                            startAutoLearningAtPosition(currentPosition);
                        }, DELAY_BETWEEN_CARDS);
                    }, DELAY_BEFORE_FLIP);
                }, DELAY_BEFORE_FLIP);
            }
        }, DELAY_BEFORE_READING);

        if (position != adapter.getItemCount()) {
            auto.setText((position + 1) + "/" + adapter.getItemCount() + " words");
        }

    }

    private void showCompletionDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.CustomMaterialAlertDialog);
        builder.setTitle("Congratulations!")
                .setMessage("You've completed your study of this topic")
                .setPositiveButton("OK", (dialog, which) -> {
                    resetViews();
                    auto.setEnabled(true);
                })
                .setCancelable(false)
                .show();
    }

    private void resetViews() {
        viewPager.setCurrentItem(0);
    }
}