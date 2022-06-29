package com.androsoft.imagecompressor;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import id.zelory.compressor.Compressor;

public class MainActivity extends AppCompatActivity {


    ImageView andro_original_img, andro_compressed_img;
    TextView andro_text_original_size, andro_text_compressed_size;

    TextView andro_text_quality;

    AppCompatButton andro_pick_img_Button;
    AppCompatButton andro_img_compress_Button;

    EditText andro_text_height, andro_text_width;
    SeekBar andro_seekBar_quality;

    File andro_original_img_file, andro_compressed_img_file;

    private static String mFilepath;
    File mPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/photoCompressor");
//    File mPath = new File(System.getenv("EXTERNAL_STORAGE")+ "/photoCompressor");

    public static final int RESULT_IMAGE_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        andro_original_img = findViewById(R.id.andro_original_img);
        andro_compressed_img = findViewById(R.id.andro_compressed_img);

        andro_text_height = findViewById(R.id.andro_text_height);
        andro_text_width = findViewById(R.id.andro_text_width);

        andro_pick_img_Button = findViewById(R.id.andro_pick_img_Button);
        andro_img_compress_Button = findViewById(R.id.andro_img_compress_Button);
        andro_seekBar_quality = findViewById(R.id.andro_seekBar_quality);

        andro_text_original_size = findViewById(R.id.andro_text_original_size);
        andro_text_compressed_size = findViewById(R.id.andro_text_compressed_size);

        andro_text_quality = findViewById(R.id.andro_text_quality);





        andro_seekBar_quality.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                andro_text_quality.setText("Quality: " + progress);
                andro_seekBar_quality.setMax(100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        andro_pick_img_Button.setOnClickListener(v -> {
            openPhotoGallery();

        });


        andro_img_compress_Button.setOnClickListener(v -> {
            int imgHeight = Integer.parseInt(andro_text_height.getText().toString());
            int imgWidth = Integer.parseInt(andro_text_width.getText().toString());
            int imgQuality = andro_seekBar_quality.getProgress();

            //VISIBLE.
            andro_compressed_img.setVisibility(View.VISIBLE);
            andro_text_compressed_size.setVisibility(View.VISIBLE);

            //GONE.
            andro_original_img.setVisibility(View.GONE);
            andro_text_original_size.setVisibility(View.GONE);


//            andro_compressed_img_file = new Compressor.Builder(MainActivity.this)
//                    .setMaxWidth(imgWidth)
//                    .setMaxHeight(imgHeight)
//                    .setQuality(imgQuality)
//                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
//                    .setDestinationDirectoryPath(mFilepath)
//                    .build().compressToFile(andro_original_img_file);


            andro_compressed_img_file = new Compressor.Builder(this)
                    .setMaxWidth(imgWidth)
                    .setMaxHeight(imgHeight)
                    .setQuality(imgQuality)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES).getAbsolutePath()).build()
                    .compressToFile(andro_original_img_file);



            File finalImgFile = new File(mFilepath, andro_original_img_file.getName());
            Bitmap finalImgBitmap = BitmapFactory.decodeFile(finalImgFile.getAbsolutePath());
            andro_compressed_img.setImageBitmap(finalImgBitmap);
            andro_text_compressed_size.setText("Compressed Size: " + Formatter.formatShortFileSize(MainActivity.this, finalImgFile.length()));
            Toast.makeText(MainActivity.this, "Compressed Successful", Toast.LENGTH_SHORT).show();

        });


        alowPermission();


        //create a new folder.
        mFilepath = mPath.getAbsolutePath();
        if (!mPath.exists())
        {
            mPath.mkdirs();
            mPath.mkdir();
        }
    }

    private void openPhotoGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_IMAGE_CODE);

//            Intent intent = new Intent();
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            intent.setType("image/*");
//            startActivityForResult(intent, 8);
        };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode == RESULT_OK)
        {
            andro_img_compress_Button.setVisibility(View.VISIBLE);
            andro_original_img.setVisibility(View.VISIBLE);
            andro_text_original_size.setVisibility(View.VISIBLE);
            final Uri imgUri = data.getData();
            try {
                final InputStream imgInputStream = getContentResolver().openInputStream(imgUri);

                //Bitmap.
                final Bitmap selectedImgBitmap = BitmapFactory.decodeStream(imgInputStream);
                andro_original_img.setImageBitmap(selectedImgBitmap);

                andro_original_img_file = new File(imgUri.getPath().replace("raw/", ""));
                andro_text_original_size.setText("Original Size: " + Formatter.formatShortFileSize(MainActivity.this, andro_original_img_file.length()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Wrong", Toast.LENGTH_SHORT).show();
            }

        } else
        {
            Toast.makeText(MainActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void alowPermission() {

        Dexter.withContext(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE).
                withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                        
                    }
                }).check();

    }

}