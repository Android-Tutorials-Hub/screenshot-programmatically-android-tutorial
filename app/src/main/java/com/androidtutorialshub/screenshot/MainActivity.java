package com.androidtutorialshub.screenshot;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidtutorialshub.helper.FileUtil;
import com.androidtutorialshub.helper.ScreenshotUtil;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private AppCompatActivity activity = MainActivity.this;

    private LinearLayout parentView;
    private Button buttonScreenshotActivity;
    private Button buttonScreenshotView;
    private Button buttonSaveScreenshot;
    private Button buttonReset;

    private ImageView imageViewShowScreenshot;

    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initializing the views
        initViews();

        // initializing the listeners
        initListeners();


    }

    /**
     * method to initialize the views
     */
    private void initViews() {

        parentView = findViewById(R.id.parentView);

        buttonScreenshotActivity = findViewById(R.id.buttonScreenshotActivity);
        buttonScreenshotView = findViewById(R.id.buttonScreenshotView);
        buttonSaveScreenshot = findViewById(R.id.buttonSaveScreenshot);
        buttonReset = findViewById(R.id.buttonReset);

        imageViewShowScreenshot = findViewById(R.id.imageViewShowScreenshot);

    }

    /**
     * method to initialize the listeners
     */
    private void initListeners() {

        buttonScreenshotActivity.setOnClickListener(this);
        buttonScreenshotView.setOnClickListener(this);
        buttonSaveScreenshot.setOnClickListener(this);
        buttonReset.setOnClickListener(this);


    }

    /**
     * method for click listener
     *
     * @param view
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.buttonScreenshotActivity:
                bitmap = ScreenshotUtil.getInstance().takeScreenshotForScreen(activity); // Take ScreenshotUtil for activity
                imageViewShowScreenshot.setImageBitmap(bitmap);
                break;

            case R.id.buttonScreenshotView:
                bitmap = ScreenshotUtil.getInstance().takeScreenshotForView(parentView); // Take ScreenshotUtil for any view
                imageViewShowScreenshot.setImageBitmap(bitmap);
                break;

            case R.id.buttonSaveScreenshot:
                requestPermissionAndSave();
                break;

            case R.id.buttonReset:
                bitmap = null;
                imageViewShowScreenshot.setImageBitmap(bitmap);
                break;

        }
    }


    /**
     * Requesting storage permission
     * Once the permission granted, screen shot captured
     * On permanent denial show toast
     */
    private void requestPermissionAndSave() {

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        if (bitmap != null) {
                            String path = Environment.getExternalStorageDirectory().toString() + "/test.png";
                            FileUtil.getInstance().storeBitmap(bitmap, path);
                            Toast.makeText(activity, getString(R.string.toast_message_screenshot_success) + " " + path, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(activity, getString(R.string.toast_message_screenshot), Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            Toast.makeText(activity, getString(R.string.settings_message), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
}
