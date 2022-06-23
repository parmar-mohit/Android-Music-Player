package com.game.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.game.musicplayer.adapter.MusicRecyclerViewAdapter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<File> songsList;

    public static final String EXTRA_SONGS_LIST = "com.game.musicplayer.EXTRA_SONGS_LIST";
    public static final String EXTRA_POSITION = "com.game.musicplayer.EXTRA_POSITION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity","Before Setting Content View");
        setContentView(R.layout.activity_main);
        Log.d("MainActivity","After Setting Content View");

        //Getting Views
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Log.d("MainActivity","Checking for Permisson");
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Log.d("MainActivity","Permission Granted");
                        Log.d("MainActivity","Starting to Fetch Songs");
                        Log.d("MainActivity","Location : "+Environment.getExternalStorageDirectory());
                        songsList = fetchSongs(Environment.getExternalStorageDirectory());
                        Log.d("MainActivity","Songs Retrieved");

                        //Setting Adapter for Recycler View
                        MusicRecyclerViewAdapter musicRecyclerViewAdapter = new MusicRecyclerViewAdapter(songsList);
                        recyclerView.setAdapter(musicRecyclerViewAdapter);
                        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(MainActivity.this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(MainActivity.this,MusicPlayer.class);
                                intent.putExtra(EXTRA_SONGS_LIST,songsList);
                                intent.putExtra(EXTRA_POSITION,position);
                                startActivity(intent);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }
                        }));
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }

    private ArrayList<File> fetchSongs(File location){
        ArrayList<File> songsList = new ArrayList<File>();
        File[] files = location.listFiles();

        if( files != null ){
            for( File file : files){
                Log.d("File Name : ",file.getName());
                if( file.isDirectory() && !file.isHidden() ){
                    songsList.addAll(fetchSongs(file));
                }else if(file.getName().endsWith(".mp3")){
                    songsList.add(file);
                }
            }
        }else{
            Log.d("MainActivity","Got Null");
        }

        return songsList;
    }
}