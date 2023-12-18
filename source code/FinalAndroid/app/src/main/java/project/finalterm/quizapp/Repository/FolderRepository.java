package project.finalterm.quizapp.Repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import project.finalterm.quizapp.Data.Folder;
import project.finalterm.quizapp.Data.Topic;

public class FolderRepository {
    private final DatabaseReference foldersRef;

    public FolderRepository() {
        foldersRef = FirebaseDatabase.getInstance().getReference("folders");
    }

    public LiveData<String> addFolder(Folder folder) {
        MutableLiveData<String> folderAdded = new MutableLiveData<>();

        String folderId = foldersRef.push().getKey();

        folder.setId(folderId);

        if (folderId != null) {
            foldersRef.child(folderId).setValue(folder)
                    .addOnSuccessListener(aVoid -> folderAdded.setValue(folderId))
                    .addOnFailureListener(e -> {
                        folderAdded.setValue(null);
                        Log.e("FOLDER", "Folder addition failed: " + e.getMessage());
                    });
        }

        return folderAdded;
    }

    public LiveData<Folder> getFolderByUserIdAndFolderId(String userId, String folderId) {
        MutableLiveData<Folder> folderLiveData = new MutableLiveData<>();

        foldersRef.child(folderId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Folder folder = snapshot.getValue(Folder.class);
                    if (folder != null && folder.getUserId().equals(userId)) {
                        folderLiveData.setValue(folder);
                    } else {
                        folderLiveData.setValue(null);
                    }
                } else {
                    folderLiveData.setValue(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                folderLiveData.setValue(null);
                Log.e("FOLDER", "Folder retrieval failed: " + error.getMessage());
            }
        });

        return folderLiveData;
    }

    public LiveData<ArrayList<Folder>> getFoldersByUserId(String userId) {
        MutableLiveData<ArrayList<Folder>> folderLiveData = new MutableLiveData<>();

        foldersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Folder> userFolders = new ArrayList<>();
                for (DataSnapshot folderSnapshot : snapshot.getChildren()) {
                    Folder folder = folderSnapshot.getValue(Folder.class);
                    if (folder != null && folder.getUserId().equals(userId)) {
                        userFolders.add(folder);
                        Log.d("FOLDER_ID", folder.getId());
                    }
                }
                folderLiveData.setValue(userFolders);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                folderLiveData.setValue(null);
                Log.e("FOLDER", "Folder retrieval failed: " + error.getMessage());
            }
        });

        return folderLiveData;
    }

    public LiveData<Boolean> addTopicsToFolder(String userId, String folderId, ArrayList<Topic> topics) {
        MutableLiveData<Boolean> topicsToFolder = new MutableLiveData<>();

        foldersRef.child(folderId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Folder folder = dataSnapshot.getValue(Folder.class);
                    if (folder != null) {
                        ArrayList<Topic> existingTopics = folder.getTopics();

                        if (existingTopics == null) {
                            existingTopics = new ArrayList<>();
                        }

                        existingTopics.addAll(topics);

                        foldersRef.child(folderId).child("topics").setValue(existingTopics)
                                .addOnSuccessListener(aVoid -> {
                                    topicsToFolder.setValue(true);
                                })
                                .addOnFailureListener(e -> {
                                    topicsToFolder.setValue(false);
                                });
                    }
                } else {
                    topicsToFolder.setValue(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                topicsToFolder.setValue(null);
                Log.e("FOLDER", "Folder retrieval failed: " + error.getMessage());
            }
        });

        return topicsToFolder;
    }

    public void removeTopicFromFolder(String userId, String folderId, String topicId, DatabaseReference.CompletionListener listener) {
        DatabaseReference folderRef = FirebaseDatabase.getInstance().getReference().child("folders").child(folderId);

        folderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Folder folder = dataSnapshot.getValue(Folder.class);
                    if (folder != null && folder.getUserId().equals(userId)) {
                        ArrayList<Topic> existingTopics = folder.getTopics();
                        if (existingTopics != null) {
                            ArrayList<Topic> updatedTopics = new ArrayList<>(existingTopics);
                            boolean topicRemoved = false;

                            for (Topic topic : existingTopics) {
                                if (topic.getId().equals(topicId)) {
                                    updatedTopics.remove(topic);
                                    topicRemoved = true;
                                    break;
                                }
                            }

                            if (topicRemoved) {
                                folderRef.child("topics").setValue(updatedTopics, listener);
                            } else {
                                listener.onComplete(DatabaseError.fromException(new Exception("Topic not found in folder")), folderRef);
                            }
                        } else {
                            listener.onComplete(DatabaseError.fromException(new Exception("Folder topics not found")), folderRef);
                        }
                    } else {
                        listener.onComplete(DatabaseError.fromException(new Exception("Folder doesn't belong to this user")), folderRef);
                    }
                } else {
                    listener.onComplete(DatabaseError.fromException(new Exception("Folder doesn't exist")), folderRef);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onComplete(error, folderRef);
            }
        });
    }

    public void deleteFolder(String userId, String folderId, DatabaseReference.CompletionListener listener) {
        DatabaseReference folderRef = FirebaseDatabase.getInstance().getReference().child("folders").child(folderId);

        folderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Folder folder = dataSnapshot.getValue(Folder.class);
                    if (folder != null && folder.getUserId().equals(userId)) {
                        folderRef.removeValue(listener);
                    } else {
                        listener.onComplete(DatabaseError.fromException(new Exception("Folder doesn't belong to this user")), folderRef);
                    }
                } else {
                    listener.onComplete(DatabaseError.fromException(new Exception("Folder doesn't exist")), folderRef);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
                listener.onComplete(databaseError, folderRef);
            }
        });
    }

    public void updateFolderTitle(String userId, String folderId, String newTitle, DatabaseReference.CompletionListener listener) {
        DatabaseReference folderRef = FirebaseDatabase.getInstance().getReference().child("folders").child(folderId);

        folderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Folder folder = dataSnapshot.getValue(Folder.class);
                    if (folder != null && folder.getUserId().equals(userId)) {
                        folder.setTitle(newTitle);
                        folderRef.setValue(folder, listener);
                    } else {
                        listener.onComplete(DatabaseError.fromException(new Exception("Folder doesn't belong to this user")), folderRef);
                    }
                } else {
                    listener.onComplete(DatabaseError.fromException(new Exception("Folder doesn't exist")), folderRef);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
                listener.onComplete(databaseError, folderRef);
            }
        });
    }

    public void updateFolderDescription(String userId, String folderId, String newDescription, DatabaseReference.CompletionListener listener) {
        DatabaseReference folderRef = FirebaseDatabase.getInstance().getReference().child("folders").child(folderId);

        folderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Folder folder = dataSnapshot.getValue(Folder.class);
                    if (folder != null && folder.getUserId().equals(userId)) {
                        folder.setDescription(newDescription);
                        folderRef.setValue(folder, listener);
                    } else {
                        listener.onComplete(DatabaseError.fromException(new Exception("Folder doesn't belong to this user")), folderRef);
                    }
                } else {
                    listener.onComplete(DatabaseError.fromException(new Exception("Folder doesn't exist")), folderRef);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled
                listener.onComplete(databaseError, folderRef);
            }
        });
    }

}
