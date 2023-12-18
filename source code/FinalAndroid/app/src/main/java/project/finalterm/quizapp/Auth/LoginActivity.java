package project.finalterm.quizapp.Auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import project.finalterm.quizapp.Interface.AuthCallback;
import project.finalterm.quizapp.MainActivity;
import project.finalterm.quizapp.R;
import project.finalterm.quizapp.ViewModel.AuthViewModel;

public class LoginActivity extends AppCompatActivity {
    private AuthViewModel authViewModel;
    private TextInputEditText email, password;
    private Button login;
    private TextView register, forgotpassword;
    private CircularProgressIndicator progressIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        email = findViewById(R.id.emailLogin);
        password = findViewById(R.id.passwordLogin);

        register = findViewById(R.id.registerDirect);
        register.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        login = findViewById(R.id.loginButton);
        login.setOnClickListener(view -> {
            String reqEmail = email.getText().toString();
            String reqPassword = password.getText().toString();

            if (reqEmail.isEmpty() || reqPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(reqEmail, reqPassword);
            }

        });

        forgotpassword = findViewById(R.id.forgotPassword);
        forgotpassword.setOnClickListener(v -> {
            startActivity(new Intent(this, VerifyEmailActivity.class));
        });
    }
    private void loginUser(String email, String password) {
        progressIndicator = findViewById(R.id.loginLoading);
        progressIndicator.setVisibility(View.VISIBLE);
        authViewModel.loginUser(email, password, new AuthCallback() {
            @Override
            public void onSuccess() {
                // Login successful
                progressIndicator.setVisibility(View.GONE);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                // Show error message to the user
                progressIndicator.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}