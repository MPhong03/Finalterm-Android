package project.finalterm.quizapp.Repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import project.finalterm.quizapp.Data.Progress;
import project.finalterm.quizapp.Data.User;
import project.finalterm.quizapp.Data.UserRank;

public class RankRepository {
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("progresses");
    public LiveData<ArrayList<UserRank>> getUsersRankedByScore() {
        MutableLiveData<ArrayList<UserRank>> userRanksLiveData = new MutableLiveData<>();

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<UserRank> userRanks = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    String userId = user.getUid();

                    dbRef.child(userId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot progressSnapshot) {
                            if (progressSnapshot.exists()) {
                                Progress progress = progressSnapshot.getValue(Progress.class);

                                if (progress != null && progress.getScoreQuiz() != null && progress.getScoreMatch() != null) {
                                    int totalScore = calculateTotalScore(progress);

                                    UserRank userRank = new UserRank(
                                            "",
                                            user.getDisplayName(),
                                            String.valueOf(totalScore),
                                            user.getPhotoUrl()
                                    );

                                    userRanks.add(userRank);
                                }

                                Collections.sort(userRanks, (u1, u2) -> Integer.compare(Integer.parseInt(u2.getScore()), Integer.parseInt(u1.getScore())));
                                setRanks(userRanks);
                                userRanksLiveData.setValue(userRanks);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle onCancelled event
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event
            }
        });

        return userRanksLiveData;
    }

    private int calculateTotalScore(Progress progress) {
        int scoreQuizSum = progress.getScoreQuiz().values().stream().mapToInt(Integer::intValue).sum();
        int scoreMatchSum = progress.getScoreMatch().values().stream().mapToInt(Integer::intValue).sum();
        return scoreQuizSum + scoreMatchSum;
    }

    private void setRanks(ArrayList<UserRank> userRanks) {
        for (int i = 0; i < userRanks.size(); i++) {
            userRanks.get(i).setRank(String.valueOf(i + 1));
        }
    }
}
