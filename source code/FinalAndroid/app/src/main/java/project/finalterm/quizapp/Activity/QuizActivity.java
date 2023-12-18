package project.finalterm.quizapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import project.finalterm.quizapp.Adapter.ChoiceAdapter;
import project.finalterm.quizapp.Adapter.FlashCardAdapter;
import project.finalterm.quizapp.Auth.LoginActivity;
import project.finalterm.quizapp.Data.Word;
import project.finalterm.quizapp.Fragment.MatchFragment;
import project.finalterm.quizapp.Fragment.QuizFragment;
import project.finalterm.quizapp.Fragment.StartFragment;
import project.finalterm.quizapp.Fragment.StartMatchFragment;
import project.finalterm.quizapp.R;
import project.finalterm.quizapp.ViewModel.AuthViewModel;
import project.finalterm.quizapp.ViewModel.TopicViewModel;

public class QuizActivity extends AppCompatActivity {
    private AuthViewModel authViewModel;
    private TopicViewModel topicViewModel;
    private ArrayList<Word> words;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        topicViewModel = new ViewModelProvider(this).get(TopicViewModel.class);

        authViewModel.getCurrentUser().observe(this, firebaseUser -> {
            if (firebaseUser == null) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if (savedInstanceState == null) {
            fetchTopicData();
        }

    }
    private void fetchTopicData() {
        String topicId = getIntent().getStringExtra("topicId");

        topicViewModel.getTopicById(topicId).observe(this, topic -> {
            if (topic != null) {

                authViewModel.getUserById(topic.getUserId()).observe(this, userData -> {
                    if (userData != null) {
                        if (getIntent().getStringExtra("TYPE").equals("QUIZ")) {
                            openStartFragment(topic.getTitle(), topic.getWords().size(), userData.getDisplayName());
                        } else {
                            openStartMatchFragment(topic.getTitle(), topic.getWords().size(), userData.getDisplayName());
                        }

                        Log.d("RETRIEVED_TOPIC", topic.getTitle());
                    } else {
                        Log.e("ERROR", "Failed to fetch author");
                    }
                });
            } else {
                Log.e("ERROR", "Failed to fetch topic");
            }
        });
    }

    public void openStartFragment(String title, int size, String userName) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayoutQuiz, new StartFragment().newInstance(title, size, userName))
                .commit();
    }

    public void openStartMatchFragment(String title, int size, String userName) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayoutQuiz, new StartMatchFragment().newInstance(title, size, userName))
                .commit();
    }

    public void openQuizFragment(int timeGap) {
        QuizFragment quizFragment = new QuizFragment();

        Bundle bundle = new Bundle();
        bundle.putString("topicId", getIntent().getStringExtra("topicId"));
        bundle.putInt("timeGap", timeGap * 1000);

        quizFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayoutQuiz, quizFragment)
                .commit();
    }

    public void openMatchFragment(int timeGap) {
        MatchFragment matchFragment = new MatchFragment();

        Bundle bundle = new Bundle();
        bundle.putString("topicId", getIntent().getStringExtra("topicId"));
        bundle.putInt("timeGap", timeGap * 1000);

        matchFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayoutQuiz, matchFragment)
                .commit();
    }
}