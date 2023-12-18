package project.finalterm.quizapp.Dto;

public class UserPublic {
    private String userId;
    private String userName;
    private String avt;

    public UserPublic() {
    }

    public UserPublic(String userId, String userName, String avt) {
        this.userId = userId;
        this.userName = userName;
        this.avt = avt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvt() {
        return avt;
    }

    public void setAvt(String avt) {
        this.avt = avt;
    }
}
