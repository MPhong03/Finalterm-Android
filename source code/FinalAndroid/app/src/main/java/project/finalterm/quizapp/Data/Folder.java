package project.finalterm.quizapp.Data;

import java.util.ArrayList;

public class Folder {
    private String id;
    private String title;
    private String description;
    private ArrayList<Topic> topics;
    private String userId;

    public Folder() {
    }

    public Folder(String id, String title, String description, ArrayList<Topic> topics, String userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.topics = topics;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Topic> getTopics() {
        return topics;
    }

    public void setTopics(ArrayList<Topic> topics) {
        this.topics = topics;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
