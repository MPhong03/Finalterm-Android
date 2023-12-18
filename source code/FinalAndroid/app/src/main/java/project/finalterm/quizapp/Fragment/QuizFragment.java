package project.finalterm.quizapp.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import project.finalterm.quizapp.Adapter.ChoiceAdapter;
import project.finalterm.quizapp.Adapter.FlashCardAdapter;
import project.finalterm.quizapp.Data.Topic;
import project.finalterm.quizapp.Data.Word;
import project.finalterm.quizapp.R;
import project.finalterm.quizapp.ViewModel.AuthViewModel;
import project.finalterm.quizapp.ViewModel.ProgressViewModel;
import project.finalterm.quizapp.ViewModel.TopicViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuizFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuizFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TOPIC_ID = "topicId";
    private static final String ARG_TIME_GAP = "timeGap";

    // TODO: Rename and change types of parameters
    private String topicId;
    private int timeGap;
    private Topic topic;
    private TopicViewModel topicViewModel;
    private AuthViewModel authViewModel;
    private ProgressViewModel progressViewModel;
    private ArrayList<Word> words = new ArrayList<>();
    private ChoiceAdapter adapter;
    private RecyclerView choicesRecyclerView;
    private TextView questionText;
    private int score = 0;
    private int currentQuestion = 0;
    private Handler handler = new Handler();
    public QuizFragment() {
        // Required empty public constructor
    }

    public static QuizFragment newInstance(String topicId, int timeGap) {
        QuizFragment fragment = new QuizFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TOPIC_ID, topicId);
        args.putInt(ARG_TIME_GAP, timeGap);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            topicId = getArguments().getString(ARG_TOPIC_ID);
            timeGap = getArguments().getInt(ARG_TIME_GAP);
            topicViewModel = new ViewModelProvider(this).get(TopicViewModel.class);
            authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
            progressViewModel = new ViewModelProvider(this).get(ProgressViewModel.class);

            topicViewModel.getTopicById(topicId).observe(this, topicData -> {
                if (topicData != null) {
                    topic = topicData;
                    words = topicData.getWords();

                    Log.d("RETRIEVED_TOPIC", topicData.getTitle());

                    if (!words.isEmpty()) {
                        displayQuestion(words.get(currentQuestion).getTitle(), getRandomChoices(currentQuestion));
                    }
                } else {
                    Log.e("ERROR", "Failed to fetch topic");
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);
        questionText = view.findViewById(R.id.questionCard);
        choicesRecyclerView = view.findViewById(R.id.choicesRecyclerView);
        return view;
    }

    private Runnable nextQuestionRunnable = new Runnable() {
        @Override
        public void run() {
            if (currentQuestion < words.size()) {
                displayQuestion(words.get(currentQuestion).getTitle(), getRandomChoices(currentQuestion));
            } else {
                showResultDialog();
            }
        }
    };

    private ArrayList<String> getRandomChoices(int currentIndex) {
        ArrayList<String> choices = new ArrayList<>();
        choices.add(words.get(currentIndex).getSubtitle());

        int remainingChoices = words.size() - 1;

        ArrayList<String> otherChoices = new ArrayList<>();

        for (int i = 0; i < words.size(); i++) {
            if (i != currentIndex) {
                otherChoices.add(words.get(i).getSubtitle());
            }
        }

        Collections.shuffle(otherChoices);

        for (int i = 0; i < Math.min(otherChoices.size(), 3); i++) {
            choices.add(otherChoices.get(i));
        }

        Collections.shuffle(choices);

        return choices;
    }


    private void displayQuestion(String question, ArrayList<String> choices) {
        questionText.setText(question);
        adapter = new ChoiceAdapter(getContext(), choices);
        choicesRecyclerView.setAdapter(adapter);
        choicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter.setOnItemClickListener(position -> {
            if ((words.get(currentQuestion).getSubtitle()).equals(adapter.getChoice(position))) {
                score++;
            }

            adapter.setCorrectAnswerPosition(words.get(currentQuestion).getSubtitle());
            Log.d("CORRECTANSWER", words.get(currentQuestion).getSubtitle());
            adapter.updateButtonColor(position);

            questionText.setText(words.get(currentQuestion).getDescription());

            adapter.disableAllButtonsDuringDelay(timeGap);
            choicesRecyclerView.setEnabled(false);

            handler.postDelayed(() -> {
                currentQuestion++;
                adapter.resetButtonColors();
                choicesRecyclerView.setEnabled(true);

                if (currentQuestion < words.size()) {
                    displayQuestion(words.get(currentQuestion).getTitle(), getRandomChoices(currentQuestion));
                } else {
                    showResultDialog();
                }
            }, timeGap);

        });
    }

    private void showResultDialog() {
        int totalQuestions = words.size();
        String resultMessage = "Number of correct answers: " + score + "/" + totalQuestions;

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.CustomMaterialAlertDialog);
        builder.setTitle("Quiz Result")
                .setMessage(resultMessage)
                .setCancelable(false)
                .setPositiveButton("OK", (dialogInterface, which) -> {

                    // Calculate score for user
                    double totalScore = ((double) score / totalQuestions) * 100;

                    Log.d("SCORE", totalScore + "");

                    String userId = authViewModel.getCurrentUserId();

                    progressViewModel.updateQuizScore(userId, topicId, (int) Math.round(totalScore));

                    getActivity().finish();
                })
                .show();
    }

}