package project.finalterm.quizapp.Repository;

import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import project.finalterm.quizapp.Activity.ProfileActivity;
import project.finalterm.quizapp.Data.User;
import project.finalterm.quizapp.Dto.UserPublic;
import project.finalterm.quizapp.Interface.AuthCallback;

public class AuthRepository {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
    public void registerUser(String email, String password, final AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onError(task.getException().getMessage());
                    }
                });
    }

    public void loginUser(String email, String password, final AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onError(task.getException().getMessage());
                    }
                });
    }

    public void sendPasswordResetEmail(String email, final AuthCallback callback) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onError(task.getException().getMessage());
                    }
                });
    }

    public void updateUserProfile(Uri imageUri, String username, String email, MutableLiveData<Boolean> profileUpdateSuccess) {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            if (imageUri != null) {
                StorageReference imageRef = storageReference.child("images/" + firebaseUser.getUid() + "/profile.jpg");
                imageRef.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();
                                updateUserProfileData(firebaseUser, username, email, imageUrl, profileUpdateSuccess);
                            });
                        })
                        .addOnFailureListener(e -> {
                            profileUpdateSuccess.setValue(false);
                        });
            } else {
                updateUserProfileData(firebaseUser, username, email, firebaseUser.getPhotoUrl().toString(), profileUpdateSuccess);
            }
        } else {
            profileUpdateSuccess.setValue(false);
        }
    }

    private void updateUserProfileData(FirebaseUser firebaseUser, String username, String email, String imageUrl, MutableLiveData<Boolean> profileUpdateSuccess) {
        UserProfileChangeRequest.Builder profileUpdatesBuilder = new UserProfileChangeRequest.Builder();

        if (!TextUtils.isEmpty(username)) {
            profileUpdatesBuilder.setDisplayName(username);
        }
        if (!TextUtils.isEmpty(imageUrl)) {
            profileUpdatesBuilder.setPhotoUri(Uri.parse(imageUrl));
        }

        UserProfileChangeRequest profileUpdates = profileUpdatesBuilder.build();

        firebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!TextUtils.isEmpty(email)) {
                            firebaseUser.updateEmail(email).addOnCompleteListener(emailTask -> {
                                if (emailTask.isSuccessful()) {
                                    User user = new User(firebaseUser.getUid(), username, email, imageUrl);
                                    usersRef.child(firebaseUser.getUid()).setValue(user);
                                    profileUpdateSuccess.setValue(true);
                                } else {
                                    profileUpdateSuccess.setValue(false);
                                }
                            });
                        } else {
                            User user = new User(firebaseUser.getUid(), username, firebaseUser.getEmail(), imageUrl);
                            usersRef.child(firebaseUser.getUid()).setValue(user);
                            profileUpdateSuccess.setValue(true);
                        }
                    } else {
                        profileUpdateSuccess.setValue(false);
                    }
                });
    }

    public LiveData<Boolean> isUserDataExists(String userId) {
        MutableLiveData<Boolean> userExistsLiveData = new MutableLiveData<>();

        usersRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userExistsLiveData.setValue(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                userExistsLiveData.setValue(false);
            }
        });

        return userExistsLiveData;
    }

    public LiveData<User> getUserById(String userId) {
        MutableLiveData<User> userLiveData = new MutableLiveData<>();

        usersRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userLiveData.setValue(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                userLiveData.setValue(null);
            }
        });

        return userLiveData;
    }

    public LiveData<ArrayList<UserPublic>> getUsersByName(String keyword) {
        MutableLiveData<ArrayList<UserPublic>> usersLiveData = new MutableLiveData<>();

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<UserPublic> userList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null && (user.getDisplayName().toLowerCase()).contains(keyword.toLowerCase())) {
                        UserPublic dto = new UserPublic(
                                user.getUid(),
                                user.getDisplayName(),
                                user.getPhotoUrl()
                        );
                        userList.add(dto);
                    }
                }

                usersLiveData.setValue(userList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                usersLiveData.setValue(null);
            }
        });

        return usersLiveData;
    }

    public void logout() {
        firebaseAuth.signOut();
    }
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }
    public String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        } else {
            return null;
        }
    }

}
