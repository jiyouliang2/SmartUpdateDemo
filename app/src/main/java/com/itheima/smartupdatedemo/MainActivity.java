package com.itheima.smartupdatedemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.itheima.updatelib.PatchUtil;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mDialog = new ProgressDialog(this);
    }

    public void update(View v) {
        try {
            PackageManager pm = getPackageManager();
            ApplicationInfo appInfo = pm.getApplicationInfo(getPackageName(), 0);

            final String oldPath = appInfo.sourceDir;
            final File newApkFile = new File(Environment.getExternalStorageDirectory(), "mynew.apk");
            final File patchFile = new File(Environment.getExternalStorageDirectory(), "mynew.patch");
            mDialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int result = PatchUtil.patch(oldPath, newApkFile.getAbsolutePath(), patchFile.getAbsolutePath());
                    if (result == 0) {

                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               mDialog.dismiss();
                               install(newApkFile.getAbsolutePath());

                           }
                       });
                    }

                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void install(String apkPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + apkPath),
                "application/vnd.android.package-archive");

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
