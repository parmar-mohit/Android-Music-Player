package com.game.musicplayer.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.game.musicplayer.MainActivity;
import com.game.musicplayer.MusicPlayer;
import com.game.musicplayer.R;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;

public class MusicRecyclerViewAdapter extends RecyclerView.Adapter<MusicRecyclerViewAdapter.ViewHolder> {
    private ArrayList<File> localDataSet;
    private ViewGroup parent;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView songNameTextView;
        private TextView singerNameTextView;
        private ImageView coverImageView;

        public ViewHolder(View view) {
            super(view);

            //Getting Views
            songNameTextView = (TextView)view.findViewById(R.id.songNameTextView);
            singerNameTextView = (TextView)view.findViewById(R.id.singerNameTextView);
            coverImageView = (ImageView)view.findViewById(R.id.coverImageView);
        }
    }

    public MusicRecyclerViewAdapter(ArrayList<File> dataSet){
        localDataSet = dataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.parent = parent;
        View musicView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_music,parent,false);
        return new ViewHolder(musicView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.songNameTextView.setText(localDataSet.get(position).getName().replace(".mp3",""));

        //Getting Singer Name
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        Uri uri = Uri.fromFile(localDataSet.get(position));
        mediaMetadataRetriever.setDataSource(parent.getContext(),uri);

        String singerName = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        holder.singerNameTextView.setText(singerName);

        //Setting Image
        byte[] img = mediaMetadataRetriever.getEmbeddedPicture();

        if( img != null ){
            Bitmap coverImg = BitmapFactory.decodeByteArray(img,0,img.length);
            holder.coverImageView.setImageBitmap(coverImg);
        }else{
            holder.coverImageView.setImageResource(R.drawable.music_icon);
        }

        mediaMetadataRetriever.close();
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
