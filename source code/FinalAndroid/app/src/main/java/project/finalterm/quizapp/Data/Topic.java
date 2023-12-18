package project.finalterm.quizapp.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Topic implements Serializable {
    private String id;
    private String title;
    private String subtitle;
    private String description;
    private ArrayList<Word> words;
    private String userId;
    private boolean publicState;
    public Topic() {
    }

    public Topic(String id, String title, String subtitle, String description, ArrayList<Word> words, String userId, boolean publicState) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.words = words;
        this.userId = userId;
        this.publicState = publicState;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Word> getWords() {
        return words;
    }

    public void setWords(ArrayList<Word> words) {
        this.words = words;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isPublicState() {
        return publicState;
    }

    public void setPublicState(boolean publicState) {
        this.publicState = publicState;
    }

    public Map<String, String> getWordsAsMap() {
        Map<String, String> wordsMap = new HashMap<>();
        for (Word word : words) {
            wordsMap.put(word.getTitle(), word.getSubtitle());
        }
        return wordsMap;
    }
}
