package com.example.bluesky.hmediaplayer.view;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluesky.hmediaplayer.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {
    private String TAG = MainActivity.class.getSimpleName();
    private int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 0x0010;
    private int CAMERA_OK = 100;
    private TextView tvShowUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btnSingleVideo).setOnClickListener(i -> startActivity(new Intent(MainActivity.this, SigleVideoActivity.class)));
        findViewById(R.id.btnMoreVideo).setOnClickListener(i -> startActivity(new Intent(MainActivity.this, MoreVideoPlayerActivity.class)));
        findViewById(R.id.btnRecord).setOnClickListener(i -> recordVideo());
        tvShowUrl = findViewById(R.id.tvShowUrl);
    }

    private void recordVideo() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{android.Manifest.permission.CAMERA}, CAMERA_OK);
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        Uri fileUri = null;
        fileUri = Uri.fromFile(createRecordDir()); // create a file to save the video
        // start the Video Capture Intent
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);//画质0.5
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 70000);     //限制持续时长
        startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
    }

    /**
     * 创建视频文件保存路径
     */
    private File createRecordDir() {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Toast.makeText(this, "请查看您的SD卡是否存在！", Toast.LENGTH_SHORT).show();
            return null;
        }

        File sampleDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Record");
        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }
        String recordName = "VID_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".mp4";
        File mVecordFile = new File(sampleDir, recordName);
        return mVecordFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Video captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Video saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
                //Display the video
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the video capture
            } else {
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_OK) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //这里已经获取到了摄像头的权限，想干嘛干嘛了可以

            } else {
                //这里是拒绝给APP摄像头权限，给个提示什么的说明一下都可以。
                Toast.makeText(MainActivity.this, "请手动打开相机权限", Toast.LENGTH_SHORT).show();
            }

        }

    }

}