package project.finalterm.quizapp.ViewModel;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import project.finalterm.quizapp.Data.User;
import project.finalterm.quizapp.Dto.UserPublic;
import project.finalterm.quizapp.Interface.AuthCallback;
import project.finalterm.quizapp.Repository.AuthRepository;

public class AuthViewModel extends ViewModel {
    private AuthRepository authRepository = new AuthRepository();
    private MutableLiveData<FirebaseUser> currentUser = new MutableLiveData<>();
    private MutableLiveData<Boolean> profileUpdateSuccess = new MutableLiveData<>();
    public void registerUser(String email, String password, AuthCallback callback) {
        authRepository.registerUser(email, password, callback);
    }

    public void loginUser(String email, String password, AuthCallback callback) {
        authRepository.loginUser(email, password, callback);
    }

    public void sendPasswordResetEmail(String email, AuthCallback callback) {
        authRepository.sendPasswordResetEmail(email, callback);
    }

    public void logout() {
        authRepository.logout();
    }

    public LiveData<FirebaseUser> getCurrentUser() {
        currentUser.setValue(authRepository.getCurrentUser());
        return currentUser;
    }

    public LiveData<Boolean> isUserDataExists(String userId) {
        return authRepository.isUserDataExists(userId);
    }

    public LiveData<User> getUserById(String userId) {
        return authRepository.getUserById(userId);
    }

    public String getCurrentUserId() {
        return authRepository.getCurrentUserId();
    }
    public LiveData<Boolean> getProfileUpdateStatus() {
        return profileUpdateSuccess;
    }

    public void updateUserProfile(Uri imageUri, String username, String email) {
        authRepository.updateUserProfile(imageUri, username, email, profileUpdateSuccess);
    }

    public LiveData<ArrayList<UserPublic>> getUsersByKeyword(String keywords) {
        return authRepository.getUsersByName(keywords);
    }


}
