package project.finalterm.quizapp.Repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import project.finalterm.quizapp.Data.Topic;
import project.finalterm.quizapp.Data.User;
import project.finalterm.quizapp.Dto.TopicPublic;
import project.finalterm.quizapp.Dto.UserPublic;
import project.finalterm.quizapp.Interface.UserPublicListener;

public class TopicRepository {
    private DatabaseReference databaseReference;

    public TopicRepository() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference("topics");
    }

    public LiveData<ArrayList<Topic>> getAllTopics() {
        MutableLiveData<ArrayList<Topic>> allTopicsLiveData = new MutableLiveData<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Topic> allTopics = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Topic topic = snapshot.getValue(Topic.class);
                    if (topic != null && topic.isPublicState()) {
                        allTopics.add(topic);
                    }
                }
                allTopicsLiveData.setValue(allTopics);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR", databaseError.getMessage());
            }
        });

        return allTopicsLiveData;
    }

    public LiveData<Topic> getTopicById(String topicId) {
        MutableLiveData<Topic> topicLiveData = new MutableLiveData<>();

        DatabaseReference topicsRef = databaseReference.child(topicId);

        topicsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Topic topic = dataSnapshot.getValue(Topic.class);
                topicLiveData.setValue(topic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR", databaseError.getMessage());
            }
        });

        return topicLiveData;
    }

    public LiveData<ArrayList<Topic>> getUserTopics(String userId) {
        MutableLiveData<ArrayList<Topic>> topicsLiveData = new MutableLiveData<>();

        Log.d("USERID", userId);

        Query userTopicsQuery = databaseReference.orderByChild("userId").equalTo(userId);
        userTopicsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Topic> topics = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Topic topic = snapshot.getValue(Topic.class);
                    topics.add(topic);
                    Log.d("TOPIC", topic.getTitle());
                }
                topicsLiveData.setValue(topics);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR", databaseError.getMessage());
            }
        });

        return topicsLiveData;
    }

    public LiveData<ArrayList<Topic>> getUserPublicTopics(String userId) {
        MutableLiveData<ArrayList<Topic>> topicsLiveData = new MutableLiveData<>();

        Log.d("USERID", userId);

        Query userTopicsQuery = databaseReference.orderByChild("userId").equalTo(userId);
        userTopicsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Topic> topics = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Topic topic = snapshot.getValue(Topic.class);
                    if (topic.isPublicState()) {
                        topics.add(topic);
                    }

                    Log.d("TOPIC", topic.getTitle());
                }
                topicsLiveData.setValue(topics);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR", databaseError.getMessage());
            }
        });

        return topicsLiveData;
    }

    public LiveData<Topic> getUserTopicByTopicIdAndUserId(String userId, String topicId) {
        MutableLiveData<Topic> topicLiveData = new MutableLiveData<>();

        Query userTopicsQuery = databaseReference.orderByChild("userId").equalTo(userId);

        userTopicsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Topic topic = snapshot.getValue(Topic.class);
                    if (topic != null && topic.getId().equals(topicId)) {
                        topicLiveData.setValue(topic);
                        Log.d("TOPIC", topic.getTitle());
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR", databaseError.getMessage());
            }
        });

        return topicLiveData;
    }

    public void deleteTopic(String userId, String topicId, DatabaseReference.CompletionListener listener) {
        DatabaseReference topicRef = databaseReference.child(topicId);

        topicRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Topic topic = dataSnapshot.getValue(Topic.class);
                    if (topic != null && topic.getUserId().equals(userId)) {
                        topicRef.removeValue(listener);
                    } else {
                        listener.onComplete(DatabaseError.fromException(new Exception("Topic doesn't belong to this user")), topicRef);
                    }
                } else {
                    listener.onComplete(DatabaseError.fromException(new Exception("Topic doesn't exist")), topicRef);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
                listener.onComplete(databaseError, topicRef);
            }
        });
    }

    public LiveData<ArrayList<TopicPublic>> filterTopicsByKeyword(String keyword) {
        MutableLiveData<ArrayList<TopicPublic>> allTopicsLiveData = new MutableLiveData<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<TopicPublic> allTopics = new ArrayList<>();
                AtomicInteger tasksCount = new AtomicInteger(0);

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Topic topic = snapshot.getValue(Topic.class);
                    if (topic != null && topic.getTitle().toLowerCase().contains(keyword.toLowerCase()) && topic.isPublicState()) {
                        tasksCount.incrementAndGet();

                        getUserPublicByID(topic.getUserId(), new UserPublicListener() {
                            @Override
                            public void onUserReceived(UserPublic user) {
                                TopicPublic dto = new TopicPublic(
                                        topic.getId(),
                                        topic.getTitle(),
                                        topic.getWords().size() + " words",
                                        user.getAvt(),
                                        user.getUserName(),
                                        user.getUserId()
                                );
                                allTopics.add(dto);

                                if (tasksCount.decrementAndGet() == 0) {
                                    allTopicsLiveData.setValue(allTopics);
                                }
                            }
                        });
                    }
                }

                if (tasksCount.get() == 0) {
                    allTopicsLiveData.setValue(allTopics);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR", databaseError.getMessage());
            }
        });

        return allTopicsLiveData;
    }


    private void getUserPublicByID(String userId, final UserPublicListener listener) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");

        userRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                UserPublic dto = new UserPublic(
                        user.getUid(),
                        user.getDisplayName(),
                        user.getPhotoUrl()
                );
                Log.d("USERPUBLIC ", dto.getUserName());
                listener.onUserReceived(dto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onUserReceived(null);
            }
        });
    }
}
