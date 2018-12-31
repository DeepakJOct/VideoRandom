package com.example.lenovo.videorandom;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class VideoUpload extends AppCompatActivity {

    Button btnChoose, btnUpload;
    TextView textViewChoose, textViewUpload;

    private static final int SELECT_VIDEO = 3;
    private String selectedPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload);
        btnChoose = (Button) findViewById(R.id.button_choose_video);
        btnUpload = (Button) findViewById(R.id.button_upload_video);
        textViewChoose = (TextView) findViewById(R.id.selected_video);
        textViewUpload = (TextView) findViewById(R.id.uploaded_video);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseVideo();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadVideo();
            }
        });
        if(!(selectedPath != null)) {
            disableButton();
        }

    }

    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select a Video "), SELECT_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == SELECT_VIDEO) {
                System.out.println("SELECT_VIDEO");
                Uri selectedVideoUri = data.getData();
                selectedPath = getPath(selectedVideoUri);
                textViewChoose.setText(selectedPath);
            }
        }
        if(selectedPath != null) {
            enableButton();
        } else {
            disableButton();
        }

    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();
        return path;
    }

    private void uploadVideo() {
        class UploadVideo extends AsyncTask<Void, Void, String> {

            ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(VideoUpload.this, "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                uploading.dismiss();
                alertCancelable("Success", "File was uploaded at :" + s);
                textViewUpload.setText(Html.fromHtml("<b>Uploaded at <a href='" + s + "'>" + s + "</a></b>"));
                textViewUpload.setMovementMethod(LinkMovementMethod.getInstance());
                selectedPath = null;
                disableButton();
                textViewChoose.setEnabled(false);
            }

            @Override
            protected String doInBackground(Void... params) {
                upload u = new upload();
                String msg = u.uploadVideo(selectedPath);
                return msg;
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        textViewChoose.setEnabled(true);
    }

    private void disableButton() {
        btnUpload.setEnabled(false);
        btnUpload.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        btnUpload.setText("Video not selected");
    }
    private void enableButton() {
        btnUpload.setEnabled(true);
        btnUpload.getBackground().setColorFilter(null);
        btnUpload.setText("Upload");
    }

    private void alertCancelable(String title, String message) {
        final AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(VideoUpload.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        builder.setCancelable(true);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
