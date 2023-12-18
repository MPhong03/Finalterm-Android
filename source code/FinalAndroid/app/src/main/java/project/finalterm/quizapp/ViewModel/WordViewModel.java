package project.finalterm.quizapp.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import project.finalterm.quizapp.Data.Word;

public class WordViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Word>> wordsLiveData;

    public WordViewModel() {
        wordsLiveData = new MutableLiveData<>();
        wordsLiveData.setValue(new ArrayList<>());
    }

    public void setInitialWords(ArrayList<Word> words) {
        wordsLiveData.setValue(words);
    }

    public LiveData<ArrayList<Word>> getWords() {
        return wordsLiveData;
    }

    public void addWord(Word word) {
        ArrayList<Word> currentWords = wordsLiveData.getValue();
        if (currentWords != null) {
            currentWords.add(word);
            wordsLiveData.setValue(currentWords);
        }
    }
}
