package com.czhappy.autoinstall.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Environment;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.czhappy.autoinstall.utils.SpUtils;

import java.io.File;
import java.util.List;

/**
 * Description:
 * User: chenzheng
 * Date: 2016/12/8 0008
 * Time: 14:35
 */
public class AutomaticInstallationService extends AccessibilityService {
    // 大多数的手机包名一样，联想部分机型的手机不一样
    private String[] packageNames = { "com.android.packageinstaller", "com.lenovo.security", "com.lenovo.safecenter" };

    /**
     * 此方法是accessibility service的配置信息 写在java类中是为了向下兼容
     */
    @Override
    protected void onServiceConnected() {

        super.onServiceConnected();

        AccessibilityServiceInfo mAccessibilityServiceInfo = new AccessibilityServiceInfo();
        // 响应事件的类型，这里是全部的响应事件（长按，单击，滑动等）
        mAccessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        // 反馈给用户的类型，这里是语音提示
        mAccessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;
        // 过滤的包名
        mAccessibilityServiceInfo.packageNames = packageNames;

        // 开启服务后，在偏好设置文件中将"IS_AUTO_INSTALL"的值设为true
        SpUtils.put(this, "IS_AUTO_INSTALL", true);

        setServiceInfo(mAccessibilityServiceInfo);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // 服务断开后，在偏好设置文件中将"isAllowAutoInstallation"的值设为false
        SpUtils.put(this, "IS_AUTO_INSTALL", false);

        return super.onUnbind(intent);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        installApplication(event);

    }

    @Override
    public void onInterrupt() {

    }

    /**
     * 查找关键字并执行点击按钮的操作
     *
     * @param event
     */
    @SuppressLint("NewApi")
    private void installApplication(AccessibilityEvent event) {
        boolean isAutoInstall = (boolean) SpUtils.get(this,"IS_AUTO_INSTALL", false);
        // 若是true，是我们自己下载的app，可以执行自动安装，否则为普通安装
        if (isAutoInstall == true) {

            if (event.getSource() != null && isContainInPackages(event.getPackageName().toString())) {

                // 得到“下一步”节点
                findNodesByText(event, "下一步");
                // 得到“安装”节点
                findNodesByText(event, "安装");

                // 得到“完成”节点
                findNodesByText(event, "完成");
                // 得到“打开”节点
                findNodesByText(event, "打开");

            }
        }
    }

    /**
     * 根据文字寻找节点
     *
     * @param event
     * @param text
     *            文字
     */

    @SuppressLint("NewApi")
    private void findNodesByText(AccessibilityEvent event, String text) {
        List<AccessibilityNodeInfo> nodes = event.getSource().findAccessibilityNodeInfosByText(text);

        if (nodes != null && !nodes.isEmpty()) {
            for (AccessibilityNodeInfo info : nodes) {
                if (info.isClickable()) {// 只有根据节点信息是下一步，安装，完成，打开，且是可以点击的时候，才执行后面的点击操作
                    if (text.equals("完成") || text.equals("打开")) {
                        // 如果安装完成，点击任意一个按钮把允许自动装的值改为false
                        SpUtils.put(this,"IS_AUTO_INSTALL", false);
                        File file = new File(Environment.getExternalStorageDirectory() + "/tianqibao/tianqibao.apk");
                        if (file.exists()) {

                            file.delete();
                        }

                    } else {

                        info.performAction(AccessibilityNodeInfo.ACTION_CLICK);

                    }

                }

            }
        }

    }

    /**
     * 判断包名
     *
     * @param str
     *            当前界面包名
     * @return
     */
    private boolean isContainInPackages(String str) {
        boolean flag = false;
        for (int i = 0; i < packageNames.length; i++) {
            if ((packageNames[i]).equals(str)) {
                flag = true;
                return flag;
            }
        }
        return flag;
    }
}
