package com.example.ocr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button btnLoad;
    private Bitmap imageBitmap;
    private TextView txtText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        btnLoad = findViewById(R.id.load);
        txtText = findViewById(R.id.txtDec);
    }

    public void ImageClick(View view) {
        this.dispatchTakePictureIntent();
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            getImageText();
        }
    }

    private void getImageText() {
        final FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        Task<FirebaseVisionText> firebaseVisionTextTask = firebaseVisionTextRecognizer.processImage(firebaseVisionImage);
        firebaseVisionTextTask.addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                setText(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onFail(e);
            }
        });
    }

    private void onFail(Exception e) {
        System.out.println("fail " + e);
    }

    private void setText(FirebaseVisionText text) {
        List<FirebaseVisionText.TextBlock> textBlocks = text.getTextBlocks();
        if (textBlocks.size() == 0) {
            Toast.makeText(this, "Not Text Fount ", Toast.LENGTH_SHORT).show();
        }
        for (FirebaseVisionText.TextBlock textBlock : text.getTextBlocks()) {
            String textValue = textBlock.getText();
            txtText.setText(textValue);
        }
    }

    public void LoadButton(View view) {

    }
}
