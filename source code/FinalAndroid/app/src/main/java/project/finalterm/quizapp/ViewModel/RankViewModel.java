package project.finalterm.quizapp.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import project.finalterm.quizapp.Data.UserRank;
import project.finalterm.quizapp.Repository.RankRepository;

public class RankViewModel extends ViewModel {
    private RankRepository rankRepository = new RankRepository();
    private LiveData<ArrayList<UserRank>> rankedUsersLiveData;

    public LiveData<ArrayList<UserRank>> getRankedUsers() {
        if (rankedUsersLiveData == null) {
            rankedUsersLiveData = rankRepository.getUsersRankedByScore();
        }
        return rankedUsersLiveData;
    }
}
