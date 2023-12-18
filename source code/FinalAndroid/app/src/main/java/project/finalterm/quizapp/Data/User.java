package project.finalterm.quizapp.Data;

import android.net.Uri;

public class User {
    private String Uid;
    private String DisplayName;
    private String Email;
    private String PhotoUrl;

    public User() {
    }

    public User(String uid, String displayName, String email, String photoUrl) {
        Uid = uid;
        DisplayName = displayName;
        Email = email;
        PhotoUrl = photoUrl;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhotoUrl() {
        return PhotoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        PhotoUrl = photoUrl;
    }
}
