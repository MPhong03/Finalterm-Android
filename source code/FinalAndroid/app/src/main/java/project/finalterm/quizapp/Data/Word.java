package project.finalterm.quizapp.Data;

import java.io.Serializable;

public class Word implements Serializable {
    private String title;
    private String subtitle;
    private String description;
    private String userId;

    public Word() {
    }

    public Word(String title, String subtitle, String description, String userId) {
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.userId = userId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
