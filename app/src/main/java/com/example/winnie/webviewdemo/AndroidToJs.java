package com.example.winnie.webviewdemo;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * Created by winnie on 2018/3/30.
 */

public class AndroidToJs {

    private Context context;

    public AndroidToJs(Context context) {
        this.context = context;
    }

    // 定义JS需要调用的方法
    // 被JS调用的方法必须加入@JavascriptInterface注解
    @JavascriptInterface
    public void hello(String msg) {
        showToast(msg);
    }

    private void showToast(String msg) {
        //如果页面主题中使用了fitsSystemWindows 属性，那么Toast只有使用context.getApplicationContext()上下文，否则Toast样式会有问题
        Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
