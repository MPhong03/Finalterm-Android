package project.finalterm.quizapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.jakewharton.processphoenix.ProcessPhoenix;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import project.finalterm.quizapp.Activity.CreateTopicActivity;
import project.finalterm.quizapp.Activity.FolderDetailActivity;
import project.finalterm.quizapp.Activity.ProfileActivity;
import project.finalterm.quizapp.Activity.QRScanActivity;
import project.finalterm.quizapp.Activity.RankActivity;
import project.finalterm.quizapp.Activity.SearchResultActivity;
import project.finalterm.quizapp.Activity.SettingActivity;
import project.finalterm.quizapp.Activity.TopicDetailActivity;
import project.finalterm.quizapp.Adapter.FolderAdapter;
import project.finalterm.quizapp.Adapter.TopicAdapter;
import project.finalterm.quizapp.Auth.LoginActivity;
import project.finalterm.quizapp.Data.Folder;
import project.finalterm.quizapp.Data.Topic;
import project.finalterm.quizapp.Utils.NetworkUtil;
import project.finalterm.quizapp.ViewModel.AuthViewModel;
import project.finalterm.quizapp.ViewModel.FolderViewModel;
import project.finalterm.quizapp.ViewModel.TopicViewModel;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 52100987;
    private static final int REQUEST_CODE_QR_SCAN = 52100033;
    private AuthViewModel authViewModel;
    private TopicViewModel topicViewModel;
    private FolderViewModel folderViewModel;
    private BottomAppBar appBar;
    private FloatingActionButton add;
    private TopicAdapter adapterTopic;
    private FolderAdapter adapterFolder;
    private RecyclerView recyclerViewTopic;
    private RecyclerView recyclerViewFolder;
    private ArrayList<Topic> topics;
    private ArrayList<Folder> folders;
    private TextInputEditText searchBar;
    private TextView topicMessage;
    private TextView folderMessage;
    private List<String> topicTitles = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        topicViewModel = new ViewModelProvider(this).get(TopicViewModel.class);
        folderViewModel = new ViewModelProvider(this).get(FolderViewModel.class);

        authViewModel.getCurrentUser().observe(this, firebaseUser -> {
            if (firebaseUser == null) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                topicMessage = findViewById(R.id.messageEmptyTopic);
                folderMessage = findViewById(R.id.messageEmptyFolder);

                String userId = firebaseUser.getUid();
                authViewModel.isUserDataExists(userId).observe(this, userDataExists -> {
                    if (!userDataExists) {
                        openProfileModificationDialog();
                    } else {
                        recyclerViewTopic = findViewById(R.id.topicRecyclerView);
                        adapterTopic = new TopicAdapter(this, new ArrayList<>(), authViewModel.getCurrentUserId(), topicViewModel);
                        recyclerViewTopic.setLayoutManager(new LinearLayoutManager(this));
                        recyclerViewTopic.setAdapter(adapterTopic);

                        fetchUserTopics();

                        recyclerViewFolder = findViewById(R.id.folderRecyclerView);
                        adapterFolder = new FolderAdapter(this, new ArrayList<>(), authViewModel.getCurrentUserId(), folderViewModel);
                        recyclerViewFolder.setLayoutManager(new LinearLayoutManager(this));
                        recyclerViewFolder.setAdapter(adapterFolder);

                        fetchUserFolders();

                        add = findViewById(R.id.add);
                        registerForContextMenu(add);

                        appBar = findViewById(R.id.bottomAppBar);
                        appBar.setOnMenuItemClickListener(item -> {
                            if (item.getItemId() == R.id.logoutButton) {
                                authViewModel.logout();
                                startActivity(new Intent(this, LoginActivity.class));
                                finish();
                                return true;
                            } else if (item.getItemId() == R.id.profileButton) {
                                startActivity(new Intent(this, ProfileActivity.class));
                                return true;
                            } else if (item.getItemId() == R.id.leaderBoardButton) {
                                startActivity(new Intent(this, RankActivity.class));
                                return true;
                            } else if (item.getItemId() == R.id.settingButton) {
                                startActivity(new Intent(this, SettingActivity.class));
                                return true;
                            } else {
                                return false;
                            }
                        });

                        add.setOnClickListener(v -> openContextMenu(v));

                        topicViewModel.getAllTopics().observe(this, topics -> {
                            Set<String> uniqueTitles = new HashSet<>();

                            if (topics != null && !topics.isEmpty()) {
                                for (Topic topic : topics) {
                                    uniqueTitles.add(topic.getTitle().toLowerCase());
                                }

                                topicTitles.clear();
                                topicTitles.addAll(uniqueTitles);
                            }
                        });
                    }
                });
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        authViewModel.getCurrentUser().observe(this, firebaseUser -> {
            if (firebaseUser == null) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                String userId = firebaseUser.getUid();
                authViewModel.isUserDataExists(userId).observe(this, userDataExists -> {
                    if (!userDataExists) {
                        openProfileModificationDialog();
                    } else {
                        fetchUserTopics();
                        fetchUserFolders();
                    }
                });
            }
        });
    }

    private void fetchUserTopics() {
        topicViewModel.getUserTopics(authViewModel.getCurrentUserId()).observe(this, fetchedTopics -> {
            if (fetchedTopics != null && fetchedTopics.size() > 0) {
                topics = fetchedTopics;
                adapterTopic.setTopics(topics);

                topicMessage.setVisibility(View.GONE);
            } else {
                Log.e("ERROR", "Cannot fetch data");
                topics = new ArrayList<>();
                adapterTopic.setTopics(topics);

                topicMessage.setVisibility(View.VISIBLE);
                topicMessage.setText("Get started by creating topic");
            }
        });
    }

    private void fetchUserFolders() {
        folderViewModel.getFoldersByUserId(authViewModel.getCurrentUserId()).observe(this, fetchedFolders -> {
            if (fetchedFolders != null && fetchedFolders.size() > 0) {
                folders = fetchedFolders;
                adapterFolder.setFolders(folders);

                topicMessage.setVisibility(View.GONE);
            } else {
                Log.e("ERROR", "Cannot fetch data");
                folders = new ArrayList<>();
                adapterFolder.setFolders(folders);

                folderMessage.setVisibility(View.VISIBLE);
                folderMessage.setText("Create folder to manage your topics");
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.add) {
            menu.add(Menu.NONE, 1, Menu.NONE, "Create Folder");
            menu.add(Menu.NONE, 2, Menu.NONE, "Create Topic");
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                // CREATE FOLDER DIALOG
                createFolderDialog();
                return true;
            case 2:
                startActivity(new Intent(this, CreateTopicActivity.class));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void createFolderDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.CustomMaterialAlertDialog);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_create_folder, null);
        builder.setView(dialogView);

        TextInputEditText titleFolder = dialogView.findViewById(R.id.titleNewFolder);
        TextInputEditText descriptionFolder = dialogView.findViewById(R.id.descriptionNewFolder);

        builder.setTitle("Create Folder")
                .setPositiveButton("Create", (dialog, which) -> {
                    String userId = authViewModel.getCurrentUserId();
                    Folder folder = new Folder();
                    folder.setTitle(titleFolder.getText().toString());
                    folder.setDescription(descriptionFolder.getText().toString());
                    folder.setUserId(userId);

                    folderViewModel.addFolder(folder).observe(this, folderId -> {
                        if (folderId != null) {
                            Toast.makeText(this, "Successfully created folder", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, FolderDetailActivity.class);
                            intent.putExtra("userId", userId);
                            intent.putExtra("folderId", folderId);
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "Cannot create folder", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void openProfileModificationDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.CustomMaterialAlertDialog);
        builder.setTitle("Profile Modification Required")
                .setMessage("You need to modify your profile before proceeding.")
                .setPositiveButton("Modify Profile", (dialog, which) -> {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        MenuItem qrScanner = menu.findItem(R.id.qrScanner);

        qrScanner.setOnMenuItemClickListener(item -> {
            if (hasCameraPermission()) {
                startQRScanActivity();
            } else {
                requestCameraPermission();
            }
            return true;
        });

        SearchView searchView = (SearchView) searchItem.getActionView();
        setupSearchView(searchView);

        return true;
    }
    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.CAMERA},
                REQUEST_CAMERA_PERMISSION);
    }
    private void startQRScanActivity() {
        Intent intent = new Intent(this, QRScanActivity.class);
        startActivity(intent);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startQRScanActivity();
            } else {
                Toast.makeText(this, "Camera permission is required to scan QR code", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void setupSearchView(SearchView searchView) {
        searchView.setQueryHint("Search topics...");

        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView.setInputType(InputType.TYPE_CLASS_TEXT);
        searchView.setFocusable(true);
        searchView.requestFocus();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setSuggestionsAdapter(new SimpleCursorAdapter(
                    this,
                    R.layout.dropdown_item,
                    null,
                    new String[]{"suggestion"},
                    new int[]{android.R.id.text1},
                    CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
            ));

            searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                @Override
                public boolean onSuggestionSelect(int position) {
                    return false;
                }

                @Override
                public boolean onSuggestionClick(int position) {
                    CursorAdapter adapter = (CursorAdapter) searchView.getSuggestionsAdapter();
                    Cursor cursor = adapter.getCursor();
                    if (cursor != null) {
                        int columnIndex = cursor.getColumnIndex("suggestion");
                        if (columnIndex != -1 && cursor.moveToPosition(position)) {
                            String query = cursor.getString(columnIndex);
                            searchView.setQuery(query, true);
                            performSearch(query);
                        }
                    }
                    return true;
                }
            });

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (query != null && !query.isEmpty()) {
                        performSearch(query);
                        Log.d("SUBMIT", query);
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    MatrixCursor cursor = new MatrixCursor(new String[]{"_id", "suggestion"});
                    for (int i = 0; i < topicTitles.size(); i++) {
                        if (topicTitles.get(i).toLowerCase().contains(newText.toLowerCase())) {
                            cursor.addRow(new Object[]{i, topicTitles.get(i)});
                        }
                    }
                    searchView.getSuggestionsAdapter().changeCursor(cursor);
                    return true;
                }
            });
        }
    }

    private void performSearch(String query) {
        if (NetworkUtil.isNetworkAvailable(this)) {
            Intent intent = new Intent(MainActivity.this, SearchResultActivity.class);
            intent.putExtra("query", query);
            startActivity(intent);
        } else {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle("No Internet Connection")
                    .setMessage("Please check your internet connection and try again.")
                    .setPositiveButton("OK", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        }
    }


}