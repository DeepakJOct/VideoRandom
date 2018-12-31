package com.example.lenovo.videorandom;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VideoRandomPlay extends AppCompatActivity {

    VideoView videoView;
    TextView textView;
    MediaController mediaController;
    List<String> fileData;
    Uri videoUri;
    ArrayAdapter<String> arrayAdapter;
    private int currentPosition = 0;
    MediaPlayer player;
    Button stopPlayback, closePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_random_play);
        videoView = (VideoView) findViewById(R.id.video_view);
        textView = (TextView) findViewById(R.id.textview_now_playing);
        stopPlayback = (Button) findViewById(R.id.btn_stop_playing);
        closePlayer = (Button) findViewById(R.id.btn_close);
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        getJSON("https://yellowprogrammer.000webhostapp.com/android_video/getAllVideos.php");

        stopPlayback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.stopPlayback();
            }
        });
        closePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

/*
        videoUri = Uri.parse(arrayAdapter.getItem(k));
        Log.d("Post Execute]====== URi", "the uri in for loop is " + videoUri);
        videoView.setVideoURI(videoUri);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.requestFocus();
                videoView.start();
            }
        });

        */

        //fileData =
    }

    private void getJSON(final String urlWebService) {
        class GetJSON extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                final Random rand = new Random();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                Log.d("getJSON", "onPostExecute: The string is " + s);
                try {
                    videoUrls(s);
//                    Log.d("Array adapter from onPostExecute: ======= ", "The array addapter is " + arrayAdapter.getItem(0));
//                    Log.d("Array adapter from onPostExecute: ======= ", "The array addapter is " + arrayAdapter.getItem(1));
//                    Log.d("Array adapter from onPostExecute: ======= ", "The array addapter is " + arrayAdapter.getItem(2));
                    //Log.d("Array list in Post Execute: -------- ", "The array list is " + arrayList);

                    fileData = new ArrayList<String>();
                    for(int k = 0; k < arrayAdapter.getCount(); k++) {
                        fileData.add(arrayAdapter.getItem(k));
                    }

                    // Directly play the random videos without default loading video
                    final int numberOfElements = fileData.size();
                    Uri randUri = null;
                    for(int i = 0; i < numberOfElements; i++) {
                        int randomIndex = rand.nextInt(fileData.size());
                        Log.d("randomIndex", "The value is " + randomIndex);
                        String randomElement = fileData.get(randomIndex);
                        Log.d("randomElement", "The element value is " + randomIndex);
                        randUri = Uri.parse(randomElement);
                        Log.d("randUri", "The randUri value is " + randomIndex);
                    }

                            /*
                            if(!(currentPosition < fileData.size())) {
                                return;
                            }
                            Uri nextUri = Uri.parse(fileData.get(currentPosition++));
                            */

                    videoView.setVideoURI(randUri);
                    videoView.start();
                    textView.setText("Now Playing: " + randUri.getLastPathSegment());

                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            Uri nextUri = null;
                            for(int i = 0; i < numberOfElements; i++) {
                                int randomIndex = rand.nextInt(fileData.size());
                                Log.d("randomIndex", "The value is " + randomIndex);
                                String randomElement = fileData.get(randomIndex);
                                Log.d("randomElement", "The element value is " + randomIndex);
                                nextUri = Uri.parse(randomElement);
                                Log.d("nextUri", "The nextUri value is " + randomIndex);
                            }

                            videoView.setVideoURI(nextUri);
                            videoView.start();
                            textView.setText("Now Playing: " + nextUri.getLastPathSegment());
                        }
                    });



                    /*
                    videoUri = Uri.parse(fileData.get(0));
                    videoView.setVideoURI(videoUri);
                    //videoView.requestFocus();
                    videoView.start();


                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {

                            int numberOfElements = fileData.size();
                            Uri randUri = null;
                            for(int i = 0; i < numberOfElements; i++) {
                                int randomIndex = rand.nextInt(fileData.size());
                                Log.d("randomIndex", "The value is " + randomIndex);
                                String randomElement = fileData.get(randomIndex);
                                Log.d("randomElement", "The element value is " + randomIndex);
                                randUri = Uri.parse(randomElement);
                                Log.d("randUri", "The randUri value is " + randomIndex);
                            }



                            videoView.setVideoURI(randUri);
                            videoView.start();
                            textView.setText("Now Playing: " + randUri.getLastPathSegment());

                             /*
                            if(!(currentPosition < fileData.size())) {
                                return;
                            }
                            Uri nextUri = Uri.parse(fileData.get(currentPosition++));
                            */

                            /*
                            if(currentPosition == fileData.size()) {
                                currentPosition = 0;
                            }

                        }
                    });

                    */


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    URL url = new URL(urlWebService);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    Log.d("doInBackground", "The result for json read buffer is :-" + sb.toString().trim());
                    return sb.toString().trim();
                } catch (IOException e) {
                    return null;
                }
            }
        }
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    public void videoUrls(String json) throws JSONException {

        JSONArray jsonArray = new JSONArray(json);
        String[] videoUrls = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            videoUrls[i] = obj.getString("url");
        }
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, videoUrls);
        //listView.setAdapter(arrayAdapter);

    }
}
