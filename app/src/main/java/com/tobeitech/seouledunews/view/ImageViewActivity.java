package com.tobeitech.seouledunews.view;

import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.tobeitech.api.utils.Dlog;
import com.tobeitech.seouledunews.R;
import com.tobeitech.seouledunews.util.ParcelableUrlInfo;

/**
 * Created by LocalUser0 on 2017-06-08.
 */

public class ImageViewActivity extends AppCompatActivity {
    private WebView webViewImage;
    private WebSettings webSettings;
    private boolean bReloadedPage;
    private String imageUrl;
    private ImageButton imageButtonClose;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview);
        //putContentView(R.layout.activity_imageview);

        inteVariable();
        initControls();
        loadUrl("about:blank");
    }

    private void inteVariable() {
        bReloadedPage = false;

        ParcelableUrlInfo item  = getIntent().getParcelableExtra("send_Parcelable");

        Dlog.d("type:" + item.getType());
        Dlog.d("url:" + item.getUrl());

        imageUrl = item.getUrl();
        //imageUrl = "http://cfile223.uf.daum.net/image/113730244C1030B675AA6E";
    }

    private void initControls() {
        imageButtonClose = (ImageButton) findViewById(R.id.imageButton_close);
        imageButtonClose.setOnClickListener(buttonClickListener);

        webViewImage = (WebView) findViewById(R.id.webView_Image);

        webSettings = webViewImage.getSettings();
        webSettings.setJavaScriptEnabled(true);             //javascript
        webSettings.setBuiltInZoomControls(true);           //zoom
        webSettings.setDisplayZoomControls(false);          //돋보기
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {    // junseo
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptThirdPartyCookies(webViewImage, true);
        }
        //webSettings.setSupportZoom(true);

        webViewImage.setInitialScale(100);
        webViewImage.setWebViewClient(webViewClient);
        webViewImage.setWebChromeClient(new WebChromeClient() {  //dialog, favicon, title, progress 사용
        });
    }

    private void loadUrl(String url) {
        if (!bReloadedPage) {
            webViewImage.loadUrl(url);
            return;
        }
        webViewImage.loadDataWithBaseURL(null, creHtmlBody(url), "text/html", "utf-8", null);
        //webViewImage.loadUrl(url);
    }

    private String creHtmlBody(String imagUrl) {
        Dlog.d("imagUrl >>>>>> " + imagUrl);
        StringBuilder sb = new StringBuilder("<HTML>");
        sb.append("<HEAD>");
        sb.append("</HEAD>");
        sb.append("<BODY style='margin:0px 0px 0px 0px; padding:0; text-align:center; vertical-align:middle;'>");    //중앙정렬
        //sb.append("<img width='100%' height='100%' src=\"" + imagUrl+"\">");
        sb.append("<img width='100%' src=\"" + imagUrl + "\">");
        sb.append("</BODY>");
        sb.append("</HTML>");
        return sb.toString();
    }

    final WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            Dlog.d("onPageFinished");
            super.onPageFinished(view, url);
            if (!bReloadedPage) {
                Dlog.d("onPageFinished2");
                bReloadedPage = true;
                loadUrl(imageUrl);
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Dlog.d("onPageStarted");
        }
    };

    final View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.imageButton_close) {
                finish();
                //imageUrl = "http://image.fnnews.com/resource/paper/image/2017/06/09/f201706091501_l.jpg";
                //loadUrl(imageUrl);
            }
        }
    };
}
