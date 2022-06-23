package com.game.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MusicPlayer extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private ArrayList<File> songsList;
    private int position;

    private ImageView coverImage;
    private TextView songName,currentTimeTextView,endTimeTextView;
    private ImageButton playPauseButton;
    private MediaPlayer mediaPlayer;
    private SeekBar seekbar;
    private UpdateSeekbarThread updateSeekbarThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        ((ImageButton)findViewById(R.id.nextButton)).setImageResource(android.R.drawable.ic_media_next);
        ((ImageButton)findViewById(R.id.previousButton)).setImageResource(android.R.drawable.ic_media_previous);

        //Getting Values From Intent
        Intent intent = getIntent();
        songsList = (ArrayList) intent.getSerializableExtra(MainActivity.EXTRA_SONGS_LIST);
        position = intent.getIntExtra(MainActivity.EXTRA_POSITION,0);

        //Getting Views
        coverImage = findViewById(R.id.coverImage);
        songName = findViewById(R.id.songName);
        currentTimeTextView = findViewById(R.id.currentTimeTextView);
        seekbar = findViewById(R.id.seekBar);
        endTimeTextView = findViewById(R.id.endTimeTextView);
        playPauseButton = findViewById(R.id.playPauseButton);

        seekbar.setOnSeekBarChangeListener(this);
        setSong();
    }

    private void setSong(){
        File song = songsList.get(position);
        playPauseButton.setImageResource(android.R.drawable.ic_media_pause);

        songName.setText(song.getName().replace(".mp3",""));
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        Uri uri = Uri.fromFile(song);
        mediaMetadataRetriever.setDataSource(this,uri);
        //Setting Image
        byte[] img = mediaMetadataRetriever.getEmbeddedPicture();

        if( img != null ){
            Bitmap coverImg = BitmapFactory.decodeByteArray(img,0,img.length);
            coverImage.setImageBitmap(coverImg);
        }else{
            coverImage.setImageResource(R.drawable.music_icon);
        }
        mediaMetadataRetriever.close();
        mediaPlayer = MediaPlayer.create(this,uri);
        seekbar.setMax(mediaPlayer.getDuration());
        endTimeTextView.setText(convertTime(mediaPlayer.getDuration()));
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                seekbar.setProgress(i);
                currentTimeTextView.setText(convertTime(i));
            }
        });
        mediaPlayer.start();
        updateSeekbarThread = new UpdateSeekbarThread(mediaPlayer,seekbar);
        updateSeekbarThread.start();
    }

    public void playPauseButtonOnClick(View view){
        if( mediaPlayer.isPlaying() ){
            mediaPlayer.pause();
            playPauseButton.setImageResource(android.R.drawable.ic_media_play);
        }else{
            mediaPlayer.start();
            playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
        }
    }

    public void previousButtonOnClick(View view){
        if( position != 0) {
            position--;
            mediaPlayer.stop();
            mediaPlayer.release();
            setSong();
        }else{
            Toast.makeText(this,"No Song Found",Toast.LENGTH_SHORT).show();
        }
    }

    public void nextButtonOnClick(View view){
        if( position != songsList.size()-1 ){
        position++;
        mediaPlayer.stop();
        mediaPlayer.release();
        setSong();
        }else{
            Toast.makeText(this,"No Song Found",Toast.LENGTH_SHORT).show();
        }
    }

    public static String convertTime(int time){
        String converted = "";
        int seconds = time/1000;
        int minutes = seconds/60;
        if( minutes < 10 ){
            converted += "0";
        }
        converted += minutes+":";
        seconds = seconds%60;
        if( seconds < 10 ){
            converted += "0";
        }
        converted += seconds;
        return converted;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if( b ) {
            currentTimeTextView.setText(convertTime(i));
            mediaPlayer.seekTo(i);
        }else{
            currentTimeTextView.setText(convertTime(i));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}