package com.example.winnie.webviewdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Set;

public class WebViewActivity extends AppCompatActivity {

    WebView webView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webView = (WebView) findViewById(R.id.webview);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new MyOnClickListener());

        WebSettings webSettings = webView.getSettings();
        // 设置与Js交互的权限
        webSettings.setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 通过addJavascriptInterface()将Java对象映射到JS对象
        //参数1：Javascript对象名
        //参数2：Java对象名
        webView.addJavascriptInterface(new AndroidToJs(this), "AndroidCall");


        // 先载入JS代码
        // 格式规定为:file:///android_asset/文件名.html
        webView.loadUrl("file:///android_asset/javascript.html");


        // 由于设置了弹窗检验调用结果,所以需要支持js对话框
        // webview只是载体，内容的渲染需要使用webviewChromClient类去实现
        // 通过设置WebChromeClient对象处理JavaScript的对话框
        //设置响应js 的Alert()函数
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.setWebViewClient(new MyWebViewClient());
    }

    private class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            webView.post(new Runnable() {
                @Override
                public void run() {
                    // 注意调用的JS方法名要对应上
                    // 调用javascript的callJS()方法

                    //webView.loadUrl("javascript:callJS()");

                    webView.evaluateJavascript("javascript:callAndroid()", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {

                        }
                    });
                }
            });
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // 步骤2：根据协议的参数，判断是否是所需要的url
            // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）
            //假定传入进来的 url = "js://webview?arg1=111&arg2=222"（同时也是约定好的需要拦截的）

            Uri uri = Uri.parse(url);
            // 如果url的协议 = 预先约定的 js 协议
            // 就解析往下解析参数
            if (uri.getScheme().equals("js")) {

                // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
                // 所以拦截url,下面JS开始调用Android需要的方法
                if (uri.getAuthority().equals("webview")) {

                    //  步骤3：
                    // 执行JS所需要调用的逻辑
                    showToast("js调用了Android的方法");
                    // 可以在协议上带有参数并传递到Android上
                    HashMap<String, String> params = new HashMap<>();
                    Set<String> collection = uri.getQueryParameterNames();

                }
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            AlertDialog.Builder b = new AlertDialog.Builder(WebViewActivity.this, R.style.AppTheme_AlertDialog);
            b.setTitle("Alert");
            b.setMessage(message);
            b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.confirm();
                }
            });
            b.setCancelable(false);
            b.create().show();
            return true;
        }
    }

    private void showToast(String msg){
        Toast.makeText(WebViewActivity.this.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
