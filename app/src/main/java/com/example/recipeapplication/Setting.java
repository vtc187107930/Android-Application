package com.example.recipeapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Setting extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 101;
    int countImageChoose = 0;
    EditText userName;
    ImageView userImage;
    Button finish;

    Uri uriProfileImage;

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        mAuth = FirebaseAuth.getInstance();

        userName = findViewById(R.id.register_username);
        userImage = findViewById(R.id.register_icon);
        finish = findViewById(R.id.finish);

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishSetting();
            }
        });
    }

    private void finishSetting() {
        if (countImageChoose == 0) {
            Toast.makeText(this, "Please Choose Image", Toast.LENGTH_SHORT).show();
            return;
        }

        String username = userName.getText().toString().trim();
        if (username.isEmpty()) {
            userName.setError("Username is required");
            userName.requestFocus();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            saveUserInfo(currentUser);
            uploadImageToFireBaseStorage(currentUser);
            Toast.makeText(this, "Account Create Successful", Toast.LENGTH_SHORT).show();

        } else {
            Log.d("Setting", "finishSetting: currentUser return null");
        }
    }

    private void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Profile Image"), CHOOSE_IMAGE);
    }

    public static Bitmap cropToSquare(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width) ? height - (height - width) : height;
        int cropW = (width - height) / 2;
        cropW = (cropW < 0) ? 0 : cropW;
        int cropH = (height - width) / 2;
        cropH = (cropH < 0) ? 0 : cropH;
        Bitmap cropImg = Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);

        return cropImg;
    }

    private Bitmap getRoundedCroppedBitmap(Bitmap bitmap) {
        int widthLight = bitmap.getWidth();
        int heightLight = bitmap.getHeight();

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        Paint paintColor = new Paint();
        paintColor.setFlags(Paint.ANTI_ALIAS_FLAG);

        RectF rectF = new RectF(new Rect(0, 0, widthLight, heightLight));

        canvas.drawRoundRect(rectF, widthLight / 2 ,heightLight / 2,paintColor);

        Paint paintImage = new Paint();
        paintImage.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(bitmap, 0, 0, paintImage);

        return output;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                bitmap = cropToSquare(bitmap);
                bitmap = getRoundedCroppedBitmap(bitmap);
                userImage.setImageBitmap(bitmap);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                countImageChoose++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFireBaseStorage(FirebaseUser user) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profileImage/" + user.getUid() + ".png");
        byte[] data = baos.toByteArray();
        storageReference.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                finish();
            }
        });
    }

    private void saveUserInfo(FirebaseUser user) {
        String displayName = userName.getText().toString();
        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(displayName).build();
        user.updateProfile(userProfileChangeRequest);
    }
}
