package project.finalterm.quizapp.ViewModel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import project.finalterm.quizapp.Data.Topic;

public class CreateTopicViewModel extends ViewModel {
    private MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();
    private DatabaseReference topicsRef = FirebaseDatabase.getInstance().getReference().child("topics");
    public void setCurrentUser(FirebaseUser user) {
        currentUser.setValue(user);
    }

    public LiveData<FirebaseUser> getCurrentUser() {
        return currentUser;
    }

    public DatabaseReference getTopicsRef() {
        return topicsRef;
    }

    public void saveTopic(Topic topic, DatabaseReference.CompletionListener listener) {
        String topicKey = topicsRef.push().getKey();
        topic.setId(topicKey);

        topicsRef.child(topicKey).setValue(topic, listener);
    }
    public void updateTopic(String userId, String topicId, Topic updatedTopic, DatabaseReference.CompletionListener listener) {
        if (updatedTopic.getUserId().equals(userId)) {
            DatabaseReference topicRef = topicsRef.child(topicId);
            topicRef.setValue(updatedTopic, listener);
        } else {
            Log.e("ErrorUpdateTopic", "Something wrong here!");
        }
    }

    public void deleteTopic(String userId, String topicId, DatabaseReference.CompletionListener listener) {

    }
}

