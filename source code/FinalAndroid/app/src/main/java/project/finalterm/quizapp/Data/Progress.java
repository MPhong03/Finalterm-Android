package project.finalterm.quizapp.Data;

import java.util.Map;

public class Progress {
    private String userId;
    private Map<String, Integer> scoreQuiz;
    private Map<String, Integer> scoreMatch;

    public Progress() {
    }

    public Progress(String userId, Map<String, Integer> scoreQuiz, Map<String, Integer> scoreMatch) {
        this.userId = userId;
        this.scoreQuiz = scoreQuiz;
        this.scoreMatch = scoreMatch;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Map<String, Integer> getScoreQuiz() {
        return scoreQuiz;
    }

    public void setScoreQuiz(Map<String, Integer> scoreQuiz) {
        this.scoreQuiz = scoreQuiz;
    }

    public Map<String, Integer> getScoreMatch() {
        return scoreMatch;
    }

    public void setScoreMatch(Map<String, Integer> scoreMatch) {
        this.scoreMatch = scoreMatch;
    }
}
