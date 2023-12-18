package project.finalterm.quizapp.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import project.finalterm.quizapp.Data.Folder;
import project.finalterm.quizapp.Data.Topic;
import project.finalterm.quizapp.Repository.FolderRepository;

public class FolderViewModel extends ViewModel {
    private FolderRepository folderRepository;

    public FolderViewModel() {
        folderRepository = new FolderRepository();
    }

    public LiveData<String> addFolder(Folder folder) {
        return folderRepository.addFolder(folder);
    }

    public LiveData<Folder> getFolderByUserIdAndFolderId(String userId, String folderId) {
        return folderRepository.getFolderByUserIdAndFolderId(userId, folderId);
    }

    public LiveData<ArrayList<Folder>> getFoldersByUserId(String userId) {
        return folderRepository.getFoldersByUserId(userId);
    }

    public LiveData<Boolean> addTopicsToFolder(String userId, String folderId, ArrayList<Topic> topics) {
        return folderRepository.addTopicsToFolder(userId, folderId, topics);
    }

    public void removeTopicFromFolder(String userId, String folderId, String topicId, DatabaseReference.CompletionListener listener) {
        folderRepository.removeTopicFromFolder(userId, folderId, topicId, listener);
    }

    public void deleteFolder(String userId, String folderId, DatabaseReference.CompletionListener listener) {
        folderRepository.deleteFolder(userId, folderId, listener);
    }

    public void updateFolderTitle(String userId, String folderId, String newTitle, DatabaseReference.CompletionListener listener) {
        folderRepository.updateFolderTitle(userId, folderId, newTitle, listener);
    }

    public void updateFolderDescription(String userId, String folderId, String newDescription, DatabaseReference.CompletionListener listener) {
        folderRepository.updateFolderDescription(userId, folderId, newDescription, listener);
    }

}