package com.example.vidz;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.recyclerview.widget.RecyclerView;

import com.marcinmoskala.videoplayview.VideoPlayView;

public class ListItem extends RecyclerView.ViewHolder {
    TextView textView;
    VideoPlayView videoView;
    public ListItem(View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.text);
        videoView=itemView.findViewById(R.id.picassoVideoView);
    }

    public void bind(int i) {

        textView.setText(String.valueOf(i));
    }
}
