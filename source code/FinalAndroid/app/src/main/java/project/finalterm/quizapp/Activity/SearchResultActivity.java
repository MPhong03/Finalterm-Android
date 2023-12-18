package project.finalterm.quizapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import project.finalterm.quizapp.Adapter.FolderAdapter;
import project.finalterm.quizapp.Adapter.TopicPublicAdapter;
import project.finalterm.quizapp.Adapter.UserPublicAdapter;
import project.finalterm.quizapp.Auth.LoginActivity;
import project.finalterm.quizapp.Data.Topic;
import project.finalterm.quizapp.Data.User;
import project.finalterm.quizapp.Dto.TopicPublic;
import project.finalterm.quizapp.Dto.UserPublic;
import project.finalterm.quizapp.R;
import project.finalterm.quizapp.ViewModel.AuthViewModel;
import project.finalterm.quizapp.ViewModel.TopicViewModel;

public class SearchResultActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 52100987;
    private AuthViewModel authViewModel;
    private TopicViewModel topicViewModel;
    private String currentQuery;
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private TopicPublicAdapter topicAdapter;
    private UserPublicAdapter userAdapter;
    private List<String> topicTitles = new ArrayList<>();
    private TextView message;
    private int currentTabPosition = 0;
    private CircularProgressIndicator progressIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Search");
        }

        currentQuery = getIntent().getStringExtra("query");
        Log.d("QUERY", currentQuery);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        topicViewModel = new ViewModelProvider(this).get(TopicViewModel.class);

        authViewModel.getCurrentUser().observe(this, firebaseUser -> {
            if (firebaseUser == null) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        progressIndicator = findViewById(R.id.searchLoading);
        recyclerView = findViewById(R.id.queryDataRecyclerView);
        message = findViewById(R.id.emptyDataSearchMessage);
        tabLayout = findViewById(R.id.tabLayout);

        TabLayout.Tab topicsTab = tabLayout.newTab().setText("Topics");
        TabLayout.Tab usersTab = tabLayout.newTab().setText("Users");
        tabLayout.addTab(topicsTab);
        tabLayout.addTab(usersTab);

        performSearch(currentQuery);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                currentTabPosition = position;
                Log.d("TAB", "Position: " + position);
                if (position == 0) {
                    // Fetch and display topics
                    Log.d("TAB", "Topics tab í selected");
                    performSearch(currentQuery);
                } else if (position == 1) {
                    // Fetch and display users
                    Log.d("TAB", "Users tab í selected");
                    performUserSearch(currentQuery);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

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

    private void performSearch(String query) {
        progressIndicator.setVisibility(View.VISIBLE);
        LiveData<ArrayList<TopicPublic>> topicsLiveData = topicViewModel.filterTopicsByKeyword(query);
        topicsLiveData.observe(this, topics -> {
            if (currentTabPosition == 0) {
                progressIndicator.setVisibility(View.GONE);
                if (topics != null && !topics.isEmpty()) {
                    displayResultsMessage();
                } else {
                    displayNoResultsMessage();
                }
                topicAdapter = new TopicPublicAdapter(this, topics);
                recyclerView.setAdapter(topicAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                topicAdapter.notifyDataSetChanged();
            }
        });
    }

    private void performUserSearch(String query) {
        progressIndicator.setVisibility(View.VISIBLE);
        LiveData<ArrayList<UserPublic>> usersLiveData = authViewModel.getUsersByKeyword(query);
        usersLiveData.observe(this, users -> {
            if (currentTabPosition == 1) {
                progressIndicator.setVisibility(View.GONE);
                if (users != null && !users.isEmpty()) {
                    displayResultsMessage();
                } else {
                    displayNoResultsMessage();
                }
                userAdapter = new UserPublicAdapter(this, users);
                recyclerView.setAdapter(userAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                userAdapter.notifyDataSetChanged();
            }
        });
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

    private void displayNoResultsMessage() {
        message.setVisibility(View.VISIBLE);
    }

    private void displayResultsMessage() {
        message.setVisibility(View.GONE);
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
                            currentQuery = query;
                            searchView.setQuery(query, true);
                            //
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
                        currentQuery = query;
                        if (currentTabPosition == 0) {
                            performSearch(query);
                        } else if (currentTabPosition == 1) {
                            performUserSearch(query);
                        }
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
}