package project.finalterm.quizapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.util.List;

import project.finalterm.quizapp.R;

public class QRScanActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private static final int REQUEST_CODE_SELECT_IMAGE = 52100033;
    private static final int REQUEST_CAMERA_PERMISSION = 52100967;
    private boolean activityStarted = false;
    private Camera camera;
    private SurfaceView surfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);

        surfaceView = findViewById(R.id.surfaceView);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(this);

        Button selectQRImageButton = findViewById(R.id.selectQRImageButton);

        selectQRImageButton.setOnClickListener(v -> selectImageFromGallery());
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera = Camera.open();
            camera.setPreviewDisplay(holder);
            camera.startPreview();
            camera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    Camera.Size previewSize = camera.getParameters().getPreviewSize();
                    PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(
                            data, previewSize.width, previewSize.height, 0, 0,
                            previewSize.width, previewSize.height, false);

                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                    Reader reader = new MultiFormatReader();
                    try {
                        Result result = reader.decode(bitmap);
                        if (result != null) {
                            String scannedData = result.getText();
                            handleScannedData(scannedData);
                        }
                    } catch (NotFoundException | ChecksumException | FormatException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);

            Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            int rotation = display.getRotation();
            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0:
                    degrees = 0;
                    break;
                case Surface.ROTATION_90:
                    degrees = 90;
                    break;
                case Surface.ROTATION_180:
                    degrees = 180;
                    break;
                case Surface.ROTATION_270:
                    degrees = 270;
                    break;
            }

            int result;
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;  // compensate for the mirror effect
            } else {
                result = (info.orientation - degrees + 360) % 360;
            }

            camera.setDisplayOrientation(result);
            parameters.setRotation(result);
            camera.setParameters(parameters);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    private void startQRScanner() {
        if (hasCameraPermission()) {
            initiateQRScan();
        } else {
            requestCameraPermission();
        }
    }
    private void initiateQRScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setOrientationLocked(false);
        integrator.setCameraId(0);
        integrator.setPrompt("Scan QR code");
        integrator.initiateScan();
    }
    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }
    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                REQUEST_CAMERA_PERMISSION);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startQRScanner();
            } else {
                Toast.makeText(this, "Camera permission is required to scan QR code", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null && result.getContents() != null) {
                String scannedData = result.getContents();
                handleScannedData(scannedData);
            } else {
                Log.e("ERROR", "NULL");
                showToast("An error occurred 2");
            }
        } else if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri selectedImageUri = data.getData();
                handleImageForQRScanning(selectedImageUri);
            } else {
                Log.e("ERROR", "NULL");
                showToast("An error occurred 3");
            }
        } else {
            showToast("An error occurred 1");
        }
    }
    private void handleImageForQRScanning(Uri selectedImageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);

            if (bitmap != null) {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int[] pixels = new int[width * height];
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

                RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
                BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

                Reader reader = new MultiFormatReader();
                Result result = reader.decode(binaryBitmap);

                String scannedData = result.getText();
                handleScannedData(scannedData);
            } else {
                showToast("Failed to decode QR code from the selected image");
            }
        } catch (IOException | NotFoundException | ChecksumException | FormatException e) {
            e.printStackTrace();
            showToast("Failed to decode QR code from the selected image");
        }
    }

    private void handleScannedData(String scannedData) {
        if (!activityStarted) {
            if (scannedData != null && scannedData.contains("@")) {
                String[] parts = scannedData.split("@");
                if (parts.length == 2) {
                    String userId = parts[0];
                    String topicId = parts[1];

                    Intent intent = new Intent(this, TopicDetailActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("topicId", topicId);
                    startActivity(intent);
                    finish();

                    activityStarted = true;
                } else {
                    showToast("Invalid QR code format");
                }
            } else {
                showToast("Invalid QR code format");
            }
        }
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
