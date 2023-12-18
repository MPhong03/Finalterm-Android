package project.finalterm.quizapp.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import project.finalterm.quizapp.Data.Topic;
import project.finalterm.quizapp.Data.Word;
import project.finalterm.quizapp.R;
import project.finalterm.quizapp.ViewModel.AuthViewModel;
import project.finalterm.quizapp.ViewModel.ProgressViewModel;
import project.finalterm.quizapp.ViewModel.TopicViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MatchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MatchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TOPIC_ID = "topicId";
    private static final String ARG_TIME_GAP = "timeGap";

    // TODO: Rename and change types of parameters
    private String topicId;
    private Topic topic;
    private int timeGap;
    private TopicViewModel topicViewModel;
    private AuthViewModel authViewModel;
    private ProgressViewModel progressViewModel;
    private ArrayList<Word> words = new ArrayList<>();
    private TextView question;
    private TextInputEditText answer;
    private TextView messageMatch;
    private TextView explainText;
    private Button submit;
    private int score = 0;
    private int currentWordIndex = 0;
    private Handler handler = new Handler();
    public MatchFragment() {
        // Required empty public constructor
    }

    public static MatchFragment newInstance(String topicId, int timeGap) {
        MatchFragment fragment = new MatchFragment();
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
                        displayQuestion();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_match, container, false);
        question = view.findViewById(R.id.topicTitlteMatch);
        answer = view.findViewById(R.id.definationInputMatch);
        messageMatch = view.findViewById(R.id.messageMatch);
        explainText = view.findViewById(R.id.explainMatchText);
        submit = view.findViewById(R.id.submitMatch);

        submit.setOnClickListener(v -> checkAnswer());

        if (!words.isEmpty()) {
            displayQuestion();
        }

        return view;
    }

    private void displayQuestion() {
        Word currentWord = words.get(currentWordIndex);
        question.setText(currentWord.getTitle());

        submit.setEnabled(true);
        messageMatch.setVisibility(View.GONE);
        explainText.setVisibility(View.GONE);
        answer.getText().clear();
    }

    private void checkAnswer() {
        Word currentWord = words.get(currentWordIndex);
        String userAnswer = answer.getText().toString().toLowerCase().trim();
        String correctAnswer = currentWord.getSubtitle().toLowerCase().trim();

        submit.setEnabled(false);

        if (userAnswer.equals(correctAnswer)) {
            messageMatch.setText("CORRECT");
            messageMatch.setTextColor(ContextCompat.getColor(requireContext(), R.color.success));

            score++;
        } else {
            messageMatch.setText("INCORRECT");
            messageMatch.setTextColor(ContextCompat.getColor(requireContext(), R.color.danger));

        }

        explainText.setText("ANSWER: " + currentWord.getSubtitle() + "\nDESCRIPTION: " + currentWord.getDescription());

        messageMatch.setVisibility(View.VISIBLE);
        explainText.setVisibility(View.VISIBLE);

        handler.postDelayed(() -> {
            if (currentWordIndex < words.size() - 1) {
                currentWordIndex++;
                displayQuestion();
            } else {
                showResultDialog();
            }
        }, timeGap);
    }

    private void showResultDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.CustomMaterialAlertDialog);

        builder.setTitle("Quiz Result")
                .setMessage("Number of correct answers: " + score + "/" + words.size())
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    double totalScore = ((double) score / words.size()) * 100;

                    String userId = authViewModel.getCurrentUserId();

                    progressViewModel.updateMatchScore(userId, topicId, (int) Math.round(totalScore));

                    getActivity().finish();
                })
                .setCancelable(false)
                .show();

    }
}