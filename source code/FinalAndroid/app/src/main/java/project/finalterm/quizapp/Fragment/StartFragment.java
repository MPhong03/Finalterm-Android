package project.finalterm.quizapp.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import project.finalterm.quizapp.Activity.QuizActivity;
import project.finalterm.quizapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TOPIC_NAME = "topicName";
    private static final String ARG_NUM_OF_QUES = "numOfQues";
    private static final String ARG_AUTHOR_NAME = "authorName";
    // TODO: Rename and change types of parameters
    private String topicName;
    private String author;
    private int numOfQues;
    private EditText timeGap;

    public StartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StartFragment newInstance(String topicName, int numOfQues, String author) {
        StartFragment fragment = new StartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TOPIC_NAME, topicName);
        args.putInt(ARG_NUM_OF_QUES, numOfQues);
        args.putString(ARG_AUTHOR_NAME, author);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            topicName = getArguments().getString(ARG_TOPIC_NAME);
            numOfQues = getArguments().getInt(ARG_NUM_OF_QUES);
            author = getArguments().getString(ARG_AUTHOR_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_start, container, false);

        TextView topicNameTextView = view.findViewById(R.id.topicNameQuiz);
        TextView numOfQuestionsTextView = view.findViewById(R.id.numOfQuestionQuiz);
        TextView authorTextView = view.findViewById(R.id.authorNameQuiz);

        topicNameTextView.setText(topicName);
        numOfQuestionsTextView.setText(String.valueOf(numOfQues));
        authorTextView.setText(author);

        Button startButton = view.findViewById(R.id.startTheQuizExam);
        startButton.setOnClickListener(v -> {
            timeGap = view.findViewById(R.id.timeGap);
            if (timeGap.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Please enter time gap", Toast.LENGTH_SHORT).show();
            } else {
                ((QuizActivity) getActivity()).openQuizFragment(Integer.parseInt(timeGap.getText().toString()));
            }

        });

        return view;
    }
}