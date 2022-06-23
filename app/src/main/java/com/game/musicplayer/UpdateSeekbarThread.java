package com.game.musicplayer;

import android.media.MediaPlayer;
import android.widget.SeekBar;
import android.widget.TextView;

public class UpdateSeekbarThread extends Thread{
    private MediaPlayer mediaPlayer;
    private SeekBar seekbar;

    public UpdateSeekbarThread(MediaPlayer mediaPlayer, SeekBar seekbar) {
        this.mediaPlayer = mediaPlayer;
        this.seekbar = seekbar;
    }

    @Override
    public void run() {
        int currentTime = 0;
        try{
            while( currentTime < mediaPlayer.getDuration() ){
                currentTime = mediaPlayer.getCurrentPosition();
                seekbar.setProgress(currentTime);
                sleep(800);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
