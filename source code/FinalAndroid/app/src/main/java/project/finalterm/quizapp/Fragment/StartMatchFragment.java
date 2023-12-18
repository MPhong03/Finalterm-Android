package project.finalterm.quizapp.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import project.finalterm.quizapp.Activity.QuizActivity;
import project.finalterm.quizapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StartMatchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartMatchFragment extends Fragment {

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
    public StartMatchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StartMatchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StartMatchFragment newInstance(String topicName, int numOfQues, String author) {
        StartMatchFragment fragment = new StartMatchFragment();
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
        View view = inflater.inflate(R.layout.fragment_start_match, container, false);

        TextView topicNameTextView = view.findViewById(R.id.topicNameMatch);
        TextView numOfQuestionsTextView = view.findViewById(R.id.numOfMatchWord);
        TextView authorTextView = view.findViewById(R.id.authorNameMatch);

        topicNameTextView.setText(topicName);
        numOfQuestionsTextView.setText(String.valueOf(numOfQues));
        authorTextView.setText(author);

        Button startButton = view.findViewById(R.id.startTheMatchExam);
        startButton.setOnClickListener(v -> {
            timeGap = view.findViewById(R.id.timeGapMatch);
            if (timeGap.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Please enter time gap", Toast.LENGTH_SHORT).show();
            } else {
                ((QuizActivity) getActivity()).openMatchFragment(Integer.parseInt(timeGap.getText().toString()));
            }

        });

        return view;
    }
}