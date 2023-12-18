package project.finalterm.quizapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.documentfile.provider.DocumentFile;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import project.finalterm.quizapp.Adapter.FlashCardAdapter;
import project.finalterm.quizapp.Data.Word;
import project.finalterm.quizapp.R;
import project.finalterm.quizapp.Utils.ExcelUtil;
import project.finalterm.quizapp.ViewModel.AuthViewModel;
import project.finalterm.quizapp.ViewModel.TopicViewModel;

public class TopicDetailActivity extends AppCompatActivity {
    private final int REQUEST_CODE_PICK_DIRECTORY = 101;
    private ViewPager2 viewPager;
    private FlashCardAdapter adapter;
    private TopicViewModel topicViewModel;
    private AuthViewModel authViewModel;
    private TextView topicTitle;
    private TextView topicSubtitle;
    private CardView learnButton, testButton, downloadButton, shareButton;
    private String userId;
    private String topicId;
    private ArrayList<Word> words;
    private CircularProgressIndicator progressIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_detail);

        userId = getIntent().getStringExtra("userId");
        topicId = getIntent().getStringExtra("topicId");

        Log.d("EXTRA_ID", userId + " - " + topicId);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        topicViewModel = new ViewModelProvider(this).get(TopicViewModel.class);

        topicTitle = findViewById(R.id.titleTopicDetail);
        topicSubtitle = findViewById(R.id.subtitleTopicDetail);

        topicViewModel.getUserTopicByTopicIdAndUserId(userId, topicId).observe(this, topic -> {
            if (topic != null) {
                topicTitle.setText(topic.getTitle());
                topicSubtitle.setText(topic.getSubtitle());
                words = topic.getWords();

                viewPager = findViewById(R.id.flashCardViewPager);
                adapter = new FlashCardAdapter(this, topic.getWordsAsMap());
                viewPager.setAdapter(adapter);

                Log.d("RETRIEVED_TOPIC", topic.getTitle());
            } else {
                Log.e("ERROR", "Failed to fetch topic");
            }
        });

        learnButton = findViewById(R.id.flashCardLearning);
        learnButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, FlashCardActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("topicId", topicId);
            startActivity(intent);
        });

        learnButton.setOnLongClickListener(v -> {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);

            return true;
        });

        testButton = findViewById(R.id.flashCardTest);
        testButton.setOnClickListener(v -> {
            openTestContextMenu(testButton);
        });

        downloadButton = findViewById(R.id.flashCardDownload);
        downloadButton.setOnClickListener(v -> {
            progressIndicator = findViewById(R.id.downloadTopicLoading);
            downloadExcelOfWords(words);
        });

        shareButton = findViewById(R.id.flashCardShare);
        shareButton.setOnClickListener(v -> {
            String data = userId + "@" + topicId;
            generateQRCode(data);
        });
    }
    private void openTestContextMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.test_context_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.openQuizTest) {
                Intent intent = new Intent(this, QuizActivity.class);
                intent.putExtra("topicId", topicId);
                intent.putExtra("TYPE", "QUIZ");
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.openMatchTest) {
                Intent intent = new Intent(this, QuizActivity.class);
                intent.putExtra("topicId", topicId);
                intent.putExtra("TYPE", "MATCH");
                startActivity(intent);
                return true;
            } else {
                return false;
            }
        });

        popup.show();
    }
    private void downloadExcelOfWords(ArrayList<Word> words) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_CODE_PICK_DIRECTORY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_DIRECTORY && resultCode == Activity.RESULT_OK) {
            Uri treeUri = data.getData();
            saveExcelToChosenDirectory(treeUri, words);
        }
    }

    private void saveExcelToChosenDirectory(Uri treeUri, ArrayList<Word> words) {
        DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);

        if (pickedDir != null && pickedDir.isDirectory()) {
            DocumentFile file = pickedDir.createFile("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "Words.xlsx");
            if (file != null) {
                try {
                    OutputStream outputStream = getContentResolver().openOutputStream(file.getUri());
                    if (outputStream != null) {

                        ExcelUtil.createExcelFile(words, outputStream);
                        Toast.makeText(this, "Successfully downloaded", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to save Excel file", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void generateQRCode(String data) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 500, 500);

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            showQRCodeDialog(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showQRCodeDialog(Bitmap bitmap) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.CustomMaterialAlertDialog);
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.dialog_qr_code, null);

        ImageView qrCodeImageView = dialogLayout.findViewById(R.id.qrCodeImageView);
        qrCodeImageView.setImageBitmap(bitmap);

        builder.setView(dialogLayout)
                .setTitle("QR Code")
                .setPositiveButton("Download", (dialog, which) -> {
                    saveQRCodeImage(bitmap);
                })
                .setNegativeButton("Close", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void saveQRCodeImage(Bitmap bitmap) {
        String fileName = topicTitle.getText().toString().toLowerCase().replaceAll("\\s+", "_") + ".png";

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");

        Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream outputStream = getContentResolver().openOutputStream(imageUri);
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
                Toast.makeText(this, "QR Code saved to Photos", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save QR Code", Toast.LENGTH_SHORT).show();
        }
    }


}