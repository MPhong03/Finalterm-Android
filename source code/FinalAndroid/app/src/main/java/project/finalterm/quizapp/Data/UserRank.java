package project.finalterm.quizapp.Data;

public class UserRank {
    private String rank;
    private String name;
    private String score;
    private String photo;

    public UserRank(String rank, String name, String score, String photo) {
        this.rank = rank;
        this.name = name;
        this.score = score;
        this.photo = photo;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
