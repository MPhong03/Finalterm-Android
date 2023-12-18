package project.finalterm.quizapp.Dto;

import android.net.Uri;

public class TopicPublic {
    private String id;
    private String title;
    private String numOfWords;
    private String avt;
    private String authorName;
    private String userId;

    public TopicPublic() {
    }

    public TopicPublic(String id, String title, String numOfWords, String avt, String authorName, String userId) {
        this.id = id;
        this.title = title;
        this.numOfWords = numOfWords;
        this.avt = avt;
        this.authorName = authorName;
        this.userId = userId;
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

    public String getNumOfWords() {
        return numOfWords;
    }

    public void setNumOfWords(String numOfWords) {
        this.numOfWords = numOfWords;
    }

    public String getAvt() {
        return avt;
    }

    public void setAvt(String avt) {
        this.avt = avt;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
