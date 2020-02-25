package com.example.anote.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.realm.Realm;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.anote.R;
import com.example.anote.adapter.item.item.Memo;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NewMemoActivity extends AppCompatActivity {
    public static final int PICK_ALBUM = 1000;

    Button mBtnSaveMemo;
    Button mBtnLoadImage;

    EditText mMemoTitle;
    EditText mMemoContents;

    String requestCode;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_memo);

        mBtnSaveMemo = findViewById(R.id.btnSaveMemo);
        mBtnLoadImage = findViewById(R.id.btnLoadImage);
        mMemoTitle = findViewById(R.id.memoTitle);
        mMemoContents = findViewById(R.id.memoContents);

        Intent intent = getIntent();
        requestCode = intent.getStringExtra("requestCode");
        mMemoTitle.setText(intent.getStringExtra("title"));
        mMemoContents.setText(intent.getStringExtra("memoTextContents"));
        id = intent.getStringExtra("id");

        mBtnLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_ALBUM);
            }
        });

        mBtnSaveMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                String date;
                String title;
                String textContents;
                String ImageContents;

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                date = df.format(calendar.getTime());
                title = mMemoTitle.getText().toString();
                textContents = mMemoContents.getText().toString(); // +image


                Intent newMemo = new Intent();
                newMemo.putExtra("date", date);
                newMemo.putExtra("title", title);
                newMemo.putExtra("textContents", textContents);
                if(requestCode.equals("1")){
                    setResult(1, newMemo);
                }
                else if(requestCode.equals("2")){
                    newMemo.putExtra("id", id);
                    setResult(2, newMemo);
                }
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(pictureSelected(resultCode, data)){
            Uri imageUri = data.getData(); // 선택한 data(image) Uri

            // 각도 조정
            ExifInterface oldExif = null;
            String exifOrientation;
            int degree = 0;
            try {
                oldExif = new ExifInterface(getRealPathFromUri(getApplicationContext(),imageUri)); // realPathFromUri : 실제 사진 path
            } catch (IOException e) {
                e.printStackTrace();
            }
            exifOrientation = oldExif.getAttribute(ExifInterface.TAG_ORIENTATION);
            if (exifOrientation != null) degree = getOrientation(Integer.parseInt(exifOrientation));

            try {
                Bitmap resize = getBitmapFromUri(data.getData()); // bitmap
                resize = RotateBitmap(resize, degree); // 사진 줄이기
                imageUri = getImageUri(getApplicationContext(), resize); // 다시 Uri

                insertImageToCurrentSelection(new BitmapDrawable(this.getResources(), resize));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private boolean pictureSelected(int resultCode, @Nullable Intent data) {
        return resultCode == RESULT_OK && data != null;
    }

    public void insertImageToCurrentSelection(Drawable drawable) {
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        ImageSpan imageSpan = new ImageSpan(drawable);

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(mMemoContents.getText());

        String imgId = "img";

        int selStart = mMemoContents.getSelectionStart();

        // current selection is replaceв with imageId
        builder.replace(mMemoContents.getSelectionStart(), mMemoContents.getSelectionEnd(), imgId);

        // This adds a span to display image where the imageId is. If you do builder.toString() - the string will contain imageId where the imageSpan is.
        // you can use it later - if you want to find location of imageSpan in text;
        builder.setSpan(imageSpan, selStart, selStart + imgId.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mMemoContents.setText(builder);
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {

        ParcelFileDescriptor parcelFileDescriptor = getApplicationContext().getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

        BitmapFactory.Options opts = new BitmapFactory.Options();
        //세부 정보 말고 크기 정보만 갖고 온다
        opts.inJustDecodeBounds = true;

        int width = opts.outWidth;
        int height = opts.outHeight;

        float sampleRatio = getSampleRatio(width, height);
        opts.inJustDecodeBounds=false;
        opts.inSampleSize=(int)sampleRatio;

        Bitmap resizedBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, opts);
        parcelFileDescriptor.close();
        return resizedBitmap;
    }

    private float getSampleRatio(int width, int height) {
        //상한
        final int targetWidth = mMemoContents.getWidth();
        final int targetHeight = mMemoContents.getHeight();
        Log.i("ddd2",width +"vs"+targetWidth);
        Log.i("ddd2",height +"vs"+targetHeight);
        float ratio;

        if(width > height){
            if(width > targetWidth)
                ratio = (float)width / (float)targetWidth;
            else
                ratio = 1f;
        }
        else{
            if(height > targetHeight)
                ratio=(float)height/(float)targetHeight;
            else
                ratio = 1f;
        }

        return Math.round(ratio);
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(),
                inImage, "ResizeImage", null);
        return Uri.parse(path);
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public int getOrientation(int orientation){
        int degree;
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                degree = 0;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
            case ExifInterface.ORIENTATION_UNDEFINED:
                degree = 0;
                break;
            default:
                degree = 90;
        }
        return degree;
    }
}
