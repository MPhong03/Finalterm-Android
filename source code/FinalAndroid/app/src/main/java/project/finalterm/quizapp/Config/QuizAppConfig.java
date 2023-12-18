package project.finalterm.quizapp.Config;

import android.app.Application;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.processphoenix.ProcessPhoenix;

public class QuizAppConfig extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle("WELCOME")
                    .setMessage("You need to restart the application first.")
                    .setPositiveButton("OK", (dialog, which) -> {
                        ProcessPhoenix.triggerRebirth(getApplicationContext());
                    })
                    .setCancelable(false)
                    .show();
        }
    }
}
