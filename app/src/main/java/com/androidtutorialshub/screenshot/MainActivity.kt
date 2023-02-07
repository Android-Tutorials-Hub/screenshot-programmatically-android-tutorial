package com.androidtutorialshub.screenshot

import android.Manifest
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.androidtutorialshub.helper.FileUtil
import com.androidtutorialshub.helper.ScreenshotUtil
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val activity: AppCompatActivity = this@MainActivity
    private var parentView: LinearLayout? = null
    private var buttonScreenshotActivity: Button? = null
    private var buttonScreenshotView: Button? = null
    private var buttonSaveScreenshot: Button? = null
    private var buttonReset: Button? = null
    private var imageViewShowScreenshot: ImageView? = null
    private var bitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // initializing the views
        initViews()

        // initializing the listeners
        initListeners()
    }

    /**
     * method to initialize the views
     */
    private fun initViews() {
        parentView = findViewById(R.id.parentView)
        buttonScreenshotActivity = findViewById(R.id.buttonScreenshotActivity)
        buttonScreenshotView = findViewById(R.id.buttonScreenshotView)
        buttonSaveScreenshot = findViewById(R.id.buttonSaveScreenshot)
        buttonReset = findViewById(R.id.buttonReset)
        imageViewShowScreenshot = findViewById(R.id.imageViewShowScreenshot)
    }

    /**
     * method to initialize the listeners
     */
    private fun initListeners() {
        buttonScreenshotActivity!!.setOnClickListener(this)
        buttonScreenshotView!!.setOnClickListener(this)
        buttonSaveScreenshot!!.setOnClickListener(this)
        buttonReset!!.setOnClickListener(this)
    }

    /**
     * method for click listener
     *
     * @param view
     */
    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonScreenshotActivity -> {
                bitmap =
                    ScreenshotUtil.instance!!.takeScreenshotForScreen(activity) // Take ScreenshotUtil for activity
                imageViewShowScreenshot!!.setImageBitmap(bitmap)
            }
            R.id.buttonScreenshotView -> {
                bitmap =
                    ScreenshotUtil.instance!!.takeScreenshotForView(parentView!!) // Take ScreenshotUtil for any view
                imageViewShowScreenshot!!.setImageBitmap(bitmap)
            }
            R.id.buttonSaveScreenshot -> requestPermissionAndSave()
            R.id.buttonReset -> {
                bitmap = null
                imageViewShowScreenshot!!.setImageBitmap(bitmap)
            }
        }
    }

    /**
     * Requesting storage permission
     * Once the permission granted, screen shot captured
     * On permanent denial show toast
     */
    private fun requestPermissionAndSave() {
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    if (bitmap != null) {
                        val path =
                            Environment.getExternalStorageDirectory().toString() + "/test.png"
                        FileUtil.instance!!.storeBitmap(bitmap!!, path)
                        Toast.makeText(
                            activity,
                            getString(R.string.toast_message_screenshot_success) + " " + path,
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            activity,
                            getString(R.string.toast_message_screenshot),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    // check for permanent denial of permission
                    if (response.isPermanentlyDenied) {
                        Toast.makeText(
                            activity,
                            getString(R.string.settings_message),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }
}