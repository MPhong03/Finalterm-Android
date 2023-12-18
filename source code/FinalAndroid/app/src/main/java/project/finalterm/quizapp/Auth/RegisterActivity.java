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

import javax.security.auth.login.LoginException;

import project.finalterm.quizapp.Interface.AuthCallback;
import project.finalterm.quizapp.R;
import project.finalterm.quizapp.ViewModel.AuthViewModel;

public class RegisterActivity extends AppCompatActivity {
    private AuthViewModel authViewModel;
    private TextInputEditText email, password, confirm;
    private Button register;
    private TextView login;
    private CircularProgressIndicator progressIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        email = findViewById(R.id.emailRegister);
        password = findViewById(R.id.passwordRegister);
        confirm = findViewById(R.id.confirmRegister);

        login = findViewById(R.id.loginDirect);
        login.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        register = findViewById(R.id.registerButton);
        register.setOnClickListener(view -> {
            String reqEmail = email.getText().toString();
            String reqPassword = password.getText().toString();
            String reqConfirm = confirm.getText().toString();

            if (reqEmail.isEmpty() || reqPassword.isEmpty() || reqConfirm.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            } else if (!reqPassword.equals(reqConfirm)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                registerUser(reqEmail, reqPassword);
            }
        });
    }
    private void registerUser(String email, String password) {
        progressIndicator = findViewById(R.id.registerLoading);
        progressIndicator.setVisibility(View.VISIBLE);
        authViewModel.registerUser(email, password, new AuthCallback() {
            @Override
            public void onSuccess() {
                // Registration successful
                progressIndicator.setVisibility(View.GONE);
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                // Show error message to the user
                progressIndicator.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, "Register failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}