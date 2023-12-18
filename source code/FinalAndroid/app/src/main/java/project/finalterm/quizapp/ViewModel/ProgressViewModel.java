package project.finalterm.quizapp.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import project.finalterm.quizapp.Data.Progress;
import project.finalterm.quizapp.Repository.ProgressRepository;

public class ProgressViewModel extends ViewModel {
    private ProgressRepository progressRepository = new ProgressRepository();
    private MutableLiveData<Progress> progressLiveData = new MutableLiveData<>();
    public void updateQuizScore(String userId, String topicId, int score) {
        progressRepository.updateQuizScore(userId, topicId, score);
    }

    public void updateMatchScore(String userId, String topicId, int score) {
        progressRepository.updateMatchScore(userId, topicId, score);
    }

    public LiveData<Progress> getProgressByUserId(String userId) {
        progressRepository.getProgressByUserId(userId).observeForever(progress -> {
            progressLiveData.postValue(progress);
        });
        return progressLiveData;
    }
}
