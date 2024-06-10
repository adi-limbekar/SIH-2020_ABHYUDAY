package com.sih2020.abhyuday.Ambulance;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.sih2020.abhyuday.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class RegisterActivity extends AppCompatActivity {

    public static final int PROFILE_PICK_IMAGE = 97;
    public static final int PROFILE_TAKE_IMAGE = 98;
    public static final int LICENCE_PICK_IMAGE = 99;
    public static final int LICENCE_TAKE_IMAGE = 100;
    public static final int CAMERA_PERMISSION = 101;
    EditText etFullName, etEmail, etPassword, etPhoneNo, etAddress, etAmbulancePlateNo;
    TextView driverImg, licenceImg;
    MaterialButton btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        driverImg = findViewById(R.id.tv_driver_img);
        licenceImg = findViewById(R.id.tv_licence_img);
        etFullName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etPhoneNo = findViewById(R.id.et_phone_no);
        etAddress = findViewById(R.id.et_address);
        etAmbulancePlateNo = findViewById(R.id.et_ambulance_plate_no);
        btnRegister = findViewById(R.id.btn_register);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
        }

        driverImg.setOnClickListener(v -> {
            selectImage(PROFILE_PICK_IMAGE, PROFILE_TAKE_IMAGE);
        });

        licenceImg.setOnClickListener(v -> {
            selectImage(LICENCE_PICK_IMAGE, LICENCE_TAKE_IMAGE);
        });

        btnRegister.setOnClickListener(v -> {

        });
    }

    private void selectImage(int pickRequestCode, int takeRequestCode) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, takeRequestCode);
            } else if (options[item].equals("Choose from Gallery")) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, pickRequestCode);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case PROFILE_PICK_IMAGE: {
                    String picturePath = getPath(data);
                    Bitmap profileImage = (BitmapFactory.decodeFile(picturePath));
                    driverImg.setText(picturePath);
                    break;
                }
                case LICENCE_PICK_IMAGE: {
                    String picturePath = getPath(data);
                    Bitmap licenceImage = (BitmapFactory.decodeFile(picturePath));
                    licenceImg.setText(picturePath);
                    break;
                }
                case PROFILE_TAKE_IMAGE: {
                    Bitmap capturedImage = (Bitmap) data.getExtras().get("data");
                    String picturePath = saveImage(capturedImage);
                    driverImg.setText(picturePath);
                    break;
                }
                case LICENCE_TAKE_IMAGE: {
                    Bitmap capturedImage = (Bitmap) data.getExtras().get("data");
                    String picturePath = saveImage(capturedImage);
                    licenceImg.setText(picturePath);
                }
            }
        }
    }

    private String saveImage(Bitmap bitmap) {
        File directory = new File(this.getApplicationContext().getFilesDir(), File.pathSeparator + "Images");
        if (!directory.exists())
            directory.mkdirs();
        File file = new File(directory, System.currentTimeMillis() + ".jpeg");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    private String getPath(Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }
}