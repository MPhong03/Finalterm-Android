package project.finalterm.quizapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

import project.finalterm.quizapp.Adapter.RankAdapter;
import project.finalterm.quizapp.Auth.LoginActivity;
import project.finalterm.quizapp.Data.UserRank;
import project.finalterm.quizapp.R;
import project.finalterm.quizapp.ViewModel.AuthViewModel;
import project.finalterm.quizapp.ViewModel.RankViewModel;

public class RankActivity extends AppCompatActivity {
    private RankViewModel rankViewModel;
    private AuthViewModel authViewModel;
    private RecyclerView recyclerView;
    private RankAdapter adapter;
    private ArrayList<UserRank> userRanks = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        rankViewModel = new ViewModelProvider(this).get(RankViewModel.class);

        authViewModel.getCurrentUser().observe(this, firebaseUser -> {
            if (firebaseUser == null) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        recyclerView = findViewById(R.id.rankRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        rankViewModel.getRankedUsers().observe(this, userRanks -> {
            if (userRanks != null) {
                adapter = new RankAdapter(this, userRanks);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });

    }

}