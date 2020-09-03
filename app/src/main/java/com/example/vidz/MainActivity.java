package com.example.vidz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.amplifyframework.core.Amplify;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.github.jksiezni.permissive.PermissionsGrantedListener;
import com.github.jksiezni.permissive.PermissionsRefusedListener;
import com.github.jksiezni.permissive.Permissive;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.marcinmoskala.videoplayview.VideoPlayView;
import com.picker.gallery.model.GalleryImage;
import com.picker.gallery.model.GalleryVideo;
import com.picker.gallery.model.interactor.GalleryPicker;
import com.picker.gallery.view.PickerActivity;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import gun0912.tedimagepicker.builder.TedImagePicker;
import gun0912.tedimagepicker.builder.listener.OnSelectedListener;
import swipeable.com.layoutmanager.OnItemSwiped;
import swipeable.com.layoutmanager.SwipeableLayoutManager;
import swipeable.com.layoutmanager.SwipeableTouchHelperCallback;
import swipeable.com.layoutmanager.touchelper.ItemTouchHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ListAdapter adapter;
FloatingActionButton actionButton;
Context ctx;
VideoPlayView videoPlayView;
    private final int VIDRESULT=999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       ctx=this;
       actionButton=findViewById(R.id.floatingActionButton);
        actionButton.setOnClickListener(this);
        adapter = new ListAdapter();
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        SwipeableTouchHelperCallback swipeableTouchHelperCallback =
                new SwipeableTouchHelperCallback(new OnItemSwiped() {
                    @Override public void onItemSwiped() {
                        adapter.removeTopItem();
                    }

                    @Override public void onItemSwipedLeft() {
                        Log.e("SWIPE", "LEFT");
                    }

                    @Override public void onItemSwipedRight() {
                        Log.e("SWIPE", "RIGHT");
                    }

                    @Override public void onItemSwipedUp() {
                        Log.e("SWIPE", "UP");
                    }

                    @Override public void onItemSwipedDown() {
                        Log.e("SWIPE", "DOWN");
                    }
                }) {
                    @Override
                    public int getAllowedSwipeDirectionsMovementFlags(RecyclerView.ViewHolder viewHolder) {
                        return ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT;
                    }
                };
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeableTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new SwipeableLayoutManager().setAngle(10)
                .setAnimationDuratuion(450)
                .setMaxShowCount(3)
                .setScaleGap(0.1f)
                .setTransYGap(0));
        recyclerView.setAdapter(adapter = new ListAdapter());


    }

    @Override
    public void onClick(View view) {
        new Permissive.Request(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
                .whenPermissionsGranted(new PermissionsGrantedListener() {
                    @Override
                    public void onPermissionsGranted(String[] permissions) throws SecurityException {
                        // given permissions are granted
                        Pix.start(MainActivity.this, Options.init().setRequestCode(VIDRESULT));

                    }
                })
                .whenPermissionsRefused(new PermissionsRefusedListener() {
                    @Override
                    public void onPermissionsRefused(String[] permissions) {
                        // given permissions are refused
                    }
                })
                .execute((Activity) ctx);
        //startActivityForResult(new Intent(getApplicationContext(), PickerActivity.class).putExtra("IMAGES_LIMIT", 4).putExtra("VIDEOS_LIMIT", 1),VIDRESULT);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
           case VIDRESULT:
               if (resultCode == Activity.RESULT_OK && data!=null) {
                   ArrayList<String> returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                  String path=returnValue.get(0);
                   Log.e("Result",returnValue.get(0).toString());
uploadFile(path);
               }
               break;


        }


    }
    private void uploadFile(String path) {

        File exampleFile = new File(path);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(exampleFile));
            writer.append("Example file contents");
            writer.close();
        } catch (Exception exception) {
            Log.e("MyAmplifyApp", "Upload failed", exception);
        }

        Amplify.Storage.uploadFile(
                "Video",
                exampleFile,
                result -> Log.i("MyAmplifyApp", "Successfully uploaded: " + result.getKey()),
                storageFailure -> Log.e("MyAmplifyApp", "Upload failed", storageFailure)
        );
    }
}
