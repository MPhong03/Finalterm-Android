package project.finalterm.quizapp.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import project.finalterm.quizapp.Data.Topic;
import project.finalterm.quizapp.Dto.TopicPublic;
import project.finalterm.quizapp.Repository.TopicRepository;

public class TopicViewModel extends ViewModel {
    private TopicRepository topicRepository;
    private MutableLiveData<ArrayList<Topic>> topicsLiveData;

    public TopicViewModel() {
        topicRepository = new TopicRepository();
        topicsLiveData = new MutableLiveData<>();
    }

    public LiveData<ArrayList<Topic>> getAllTopics() {
        return topicRepository.getAllTopics();
    }

    public LiveData<ArrayList<TopicPublic>> filterTopicsByKeyword(String keyword) {
        return topicRepository.filterTopicsByKeyword(keyword);
    }

    public LiveData<Topic> getTopicById(String topicId) {
        return topicRepository.getTopicById(topicId);
    }

    public LiveData<ArrayList<Topic>> getUserTopics(String userId) {
        topicRepository.getUserTopics(userId).observeForever(topics -> {
            topicsLiveData.postValue(new ArrayList<>(topics));
        });
        return topicsLiveData;
    }

    public LiveData<ArrayList<Topic>> getUserPublicTopics(String userId) {
        topicRepository.getUserPublicTopics(userId).observeForever(topics -> {
            topicsLiveData.postValue(new ArrayList<>(topics));
        });
        return topicsLiveData;
    }

    public LiveData<Topic> getUserTopicByTopicIdAndUserId(String userId, String topicId) {
        return topicRepository.getUserTopicByTopicIdAndUserId(userId, topicId);
    }

    public void deleteTopic(String userId, String topicId, DatabaseReference.CompletionListener listener) {
        topicRepository.deleteTopic(userId, topicId, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    ArrayList<Topic> currentTopics = topicsLiveData.getValue();
                    if (currentTopics != null) {
                        for (Topic topic : currentTopics) {
                            if (topic.getId().equals(topicId)) {
                                currentTopics.remove(topic);
                                break;
                            }
                        }
                        topicsLiveData.postValue(currentTopics);
                    }
                }
                listener.onComplete(databaseError, databaseReference);
            }
        });
    }
}


