package project.finalterm.quizapp.Auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import project.finalterm.quizapp.Interface.AuthCallback;
import project.finalterm.quizapp.R;
import project.finalterm.quizapp.ViewModel.AuthViewModel;

public class VerifyEmailActivity extends AppCompatActivity {
    private TextInputEditText email;
    private Button sendOTP;
    private AuthViewModel authViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        email = findViewById(R.id.emailVerify);
        sendOTP = findViewById(R.id.sendOTP);
        sendOTP.setOnClickListener(v -> {
            onSendOTP(email.getText().toString());
        });
    }
    private void onSendOTP(String email) {
        if (!TextUtils.isEmpty(email)) {
            authViewModel.sendPasswordResetEmail(email, new AuthCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(VerifyEmailActivity.this, "Check your mail to change password", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(VerifyEmailActivity.this, "Failed to send reset email: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Enter your email", Toast.LENGTH_SHORT).show();
        }
    }
}