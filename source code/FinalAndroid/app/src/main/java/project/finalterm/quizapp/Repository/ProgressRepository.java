package project.finalterm.quizapp.Repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import project.finalterm.quizapp.Data.Progress;
import project.finalterm.quizapp.Data.UserRank;

public class ProgressRepository {
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("progresses");
    public void updateQuizScore(String userId, String topicId, int score) {
        DatabaseReference progressRef = dbRef.child(userId);

        progressRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Progress progress = dataSnapshot.getValue(Progress.class);
                    if (progress != null) {
                        Map<String, Integer> scoreQuiz = progress.getScoreQuiz();
                        if (scoreQuiz == null) {
                            scoreQuiz = new HashMap<>();
                        }

                        scoreQuiz.put(topicId, score);
                        progress.setScoreQuiz(scoreQuiz);

                        progressRef.setValue(progress);
                    }
                } else {
                    Map<String, Integer> scoreQuiz = new HashMap<>();
                    scoreQuiz.put(topicId, score);

                    Progress progress = new Progress(userId, scoreQuiz, new HashMap<>());

                    progressRef.setValue(progress);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event
            }
        });
    }

    public void updateMatchScore(String userId, String topicId, int score) {
        DatabaseReference progressRef = dbRef.child(userId);

        progressRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Progress progress = dataSnapshot.getValue(Progress.class);
                    if (progress != null) {
                        Map<String, Integer> scoreMatch = progress.getScoreMatch();
                        if (scoreMatch == null) {
                            scoreMatch = new HashMap<>();
                        }

                        scoreMatch.put(topicId, score);
                        progress.setScoreMatch(scoreMatch);

                        progressRef.setValue(progress);
                    }
                } else {
                    Map<String, Integer> scoreMatch = new HashMap<>();
                    scoreMatch.put(topicId, score);

                    Progress progress = new Progress(userId, new HashMap<>(), scoreMatch);

                    progressRef.setValue(progress);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event
            }
        });
    }

    public LiveData<Progress> getProgressByUserId(String userId) {
        MutableLiveData<Progress> progressLiveData = new MutableLiveData<>();
        DatabaseReference progressRef = dbRef.child(userId);

        progressRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Progress progress = dataSnapshot.getValue(Progress.class);
                    progressLiveData.setValue(progress);
                } else {
                    progressLiveData.setValue(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressLiveData.setValue(null);
            }
        });

        return progressLiveData;
    }

}
