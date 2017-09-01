package com.itheima.demo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;

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
        mDialog = new ProgressDialog(mContext);
    }

    /**
     * com.ss.android.article.news
     *
     * @param v
     */
    public void update(View v) {
        try {
            PackageManager pm = getPackageManager();
            ApplicationInfo appInfo = pm.getApplicationInfo(getPackageName(), 0);
            final String oldPath = appInfo.sourceDir;//旧版本路径
            final File newApkFile = new File(Environment.getExternalStorageDirectory(), "toutiao_new.apk");//新版本保存路径
            final File patchFile = new File(Environment.getExternalStorageDirectory(), "toutiao.patch");//更新包路径

            if (!patchFile.exists()) {
                showToast("请将差分包toutiao.patch保存到sdcard");
                return;
            }

            mDialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int result = PatchUtil.patch(oldPath, newApkFile.getAbsolutePath(), patchFile.getAbsolutePath());
                    if (result == 0) {
                        //合并成功，安装apk
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDialog.dismiss();
                                install(newApkFile.getAbsolutePath());
                            }
                        });
                    } else {
                        showToast("合并失败");
                    }
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void install(String apkPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + apkPath),
                "application/vnd.android.package-archive");

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showToast(String msg) {
        Toast.makeText(mContext, "" + msg, Toast.LENGTH_SHORT).show();
    }
}
