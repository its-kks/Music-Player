package com.example.musicplayerkks;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private List<String> songsList;
    private OnItemClickListener listener;


    public interface OnItemClickListener {
        void onItemClick(String songPath);
    }

    public SongAdapter(List<String> songsList, OnItemClickListener listener) {
        this.songsList = songsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        final String songPath = songsList.get(position);
        holder.bind(songPath);
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    class SongViewHolder extends RecyclerView.ViewHolder {
        private TextView songNameTextView;

        SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songNameTextView = itemView.findViewById(R.id.songNameTextView);
        }

        void bind(final String songPath) {
            // Set the song name or any other information you want to display
            // You can also set click listeners here
            String []  temp  = songPath.split("/");
            String name = temp[temp.length-1];
            songNameTextView.setText(name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(songPath);
                    }

                }
            });
        }
    }
}