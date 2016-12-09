package com.czhappy.autoinstall.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.czhappy.autoinstall.R;
import com.czhappy.autoinstall.utils.OpenOtherApp;
import com.czhappy.autoinstall.utils.SpUtils;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private String TAG = "tag";
    /**
     * 下载文件的button
     */
    private Button mDownLoadFileBtn;
    /**
     * 获取安装包信息
     */
    private PackageInfo mPackageInfo;
    /**
     * 判断是否安装了天气宝，true表示安装
     */
    private boolean isIntalled = false;
    private NumberProgressBar number_progress_bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        initOnListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initBtnText();

    }

    /**
     * 初始化下载按钮显示的字体，如果安装了天气宝则显示打开，否则显示下载文件
     */
    private void initBtnText() {
        try {
            mPackageInfo = this.getPackageManager().getPackageInfo(
                    "com.cz.hello", 0);
        } catch (PackageManager.NameNotFoundException e) {
            mPackageInfo = null;
            e.printStackTrace();
        }
        if (mPackageInfo == null) {
            isIntalled = false;
            mDownLoadFileBtn.setText(getString(R.string.download_file));

        } else {
            isIntalled = true;
            mDownLoadFileBtn.setText("打开应用");
        }
    }

    /**
     * 通过ID得到控件
     *
     */
    private void findViews() {
        mDownLoadFileBtn = (Button) findViewById(R.id.btn_download);
        number_progress_bar = (NumberProgressBar) findViewById(R.id.number_progress_bar);
    }

    /**
     * 设置监听事件
     */
    private void initOnListener() {
        // 引导用户打开辅助功能

        // 下载按钮的监听事件
        mDownLoadFileBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isIntalled) {
                    OpenOtherApp.openOtherAPP(MainActivity.this,
                            "com.cz.hello");// 根据包名打开应用

                } else {

                    downLoadFile();


                }

            }
        });
    }

    /**
     * 下载文件
     */
    private void downLoadFile() {
        Toast.makeText(this, "文件开始下载", Toast.LENGTH_SHORT).show();
        String apkUrl = "http://121.42.53.175:8080/hello_project/resources/upload/TianQiBao201605231.apk";
        OkGo.get(apkUrl)//
                .tag(this)//
                .execute(new FileCallback(Environment.getExternalStorageDirectory() + "/tianqibao/", "tianqibao.apk") {  //文件下载时，可以指定下载的文件目录和文件名
                    @Override
                    public void onSuccess(File file, Call call, Response response) {
                        // file 即为文件数据，文件保存在指定目录
                        Toast.makeText(MainActivity.this, "下载结束", Toast.LENGTH_SHORT).show();
                        //下载完成将自动安装设置成true，为的是区分我们自己下载的app和其它出处的app
                        SpUtils.put(MainActivity.this, "IS_AUTO_INSTALL", true);

                        // 调用安装器去安装我们的apk 一键安装开始啦，如果用户把那个服务打开了的话。
                        Intent intentInstall = new Intent(android.content.Intent.ACTION_VIEW);
                        intentInstall.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                        intentInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        MainActivity.this.startActivity(intentInstall);
                    }

                    @Override
                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        //这里回调下载进度(该回调在主线程,可以直接更新ui)
                        int a = (int) (progress*100);
                        number_progress_bar.setProgress(a);
                    }
                });
    }



}
