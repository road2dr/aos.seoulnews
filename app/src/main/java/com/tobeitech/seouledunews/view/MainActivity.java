package com.tobeitech.seouledunews.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.tobeitech.api.config.APIConfig;
import com.tobeitech.api.details.BastaDetail;
import com.tobeitech.api.interfaces.APIResponseListener;
import com.tobeitech.api.utils.Dlog;
import com.tobeitech.api.utils.DlogBase;
import com.tobeitech.api.vo.ErrorVO;
import com.tobeitech.api.vo.pushsetting.PushSettingVo;
import com.tobeitech.seouledunews.R;
import com.tobeitech.seouledunews.gcm.RegistrationIntentService;
import com.tobeitech.seouledunews.util.ParcelableUrlInfo;
import com.tobeitech.seouledunews.util.User;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;


// TODO 뉴스 목록화면에 아이콘 나오게
public class MainActivity extends AppCompatActivity {
    private WebView webViewMain;
    private WebSettings webSettings;
    private PushSettingVo pushSettingVo;
    private String toUrl;

    private static final int MSG_INIT_VARIABLE = 10001;
    private static final int MSG_CLOSE_SPLASH = 10002;
    private static final int ELAPSED_CLOSE_SPLASH = 1500;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static Context mContext;
    private boolean bSplashShow = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //putContentView(R.layout.activity_main, mClickListener);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mContext = this;
        DlogBase.DEBUG = DlogBase.isDebuggable(this);

        initThread();


    }

    private void initThread() {
        Thread worker = new Thread(new Runnable() {
            public void run() {
                mHandler.sendMessage(Message.obtain(mHandler, MSG_INIT_VARIABLE));
            }
        });
        worker.start();
    }

    public final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INIT_VARIABLE:
                    initVariable();
                    initUserAgent();
                    break;
                case MSG_CLOSE_SPLASH:
                    ImageView imageView = (ImageView) findViewById(R.id.ivSplash);
                    imageView.setVisibility(View.GONE);
                default:
                    break;
            }
        }
    };

    private void initVariable() {
        if (!hasPermissions(PERMISSIONS)) { //퍼미션 허가를 했었는지 여부를 확인
            requestNecessaryPermissions(PERMISSIONS);//퍼미션 허가안되어 있다면 사용자에게 요청
        } else {
            //이미 사용자에게 퍼미션 허가를 받음.
        }

        loadUserInfo();
        getGcmInstanceIdToken();

        parseIntentUri(getIntent());

        webViewMain = (WebView) findViewById(R.id.webView_Main);

        webSettings = webViewMain.getSettings();
        webSettings.setJavaScriptEnabled(true);             //javascript
        webSettings.setBuiltInZoomControls(false);           //zoom
        webSettings.setDisplayZoomControls(false);          //돋보기

        // todo junseo
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {    // junseo
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptThirdPartyCookies(webViewMain, true);
        }

        webViewMain.setWebViewClient(webViewClient);
        webViewMain.setWebChromeClient(new WebChromeClient() {  //dialog, favicon, title, progress 사용

        });

        webViewMain.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeRight() {
                String webUrl = webViewMain.getOriginalUrl();

                if (webUrl.indexOf("/scrap_view") > 0 || webUrl.indexOf("/notice_view") > 0)
                    webViewMain.loadUrl("javascript:previousItem()");
            }

            public void onSwipeLeft() {
                String webUrl = webViewMain.getOriginalUrl();

                if (webUrl.indexOf("/scrap_view") > 0 || webUrl.indexOf("/notice_view") > 0)
                    webViewMain.loadUrl("javascript:nextItem()");
            }

        });

        loadingLayer(true);

        webViewMain.getSettings().setUserAgentString("Android");

        if (toUrl != null) {
            webViewMain.loadUrl(toUrl);
        } else {
            webViewMain.loadUrl(APIConfig.APP_HOME_URL);
        }
    }


    private void initUserAgent() {
        WebSettings settings = webViewMain.getSettings();
        webViewMain.getSettings().setUseWideViewPort(true);
        webViewMain.getSettings().setLoadWithOverviewMode(true);

        Context context = webViewMain.getContext();
        PackageManager packageManager = context.getPackageManager();
        String appName;
        String appVersion;
        String userAgent = settings.getUserAgentString();

        try {
            // App 이름 추출
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            appName = packageManager.getApplicationLabel(applicationInfo).toString();

            // App 버전 추출
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            appVersion = String.format("%s", "" + packageInfo.versionName);


            // User-Agent에 App 이름과 버전을 붙여서 보내줌
            // junseo 한글 useragent 제거. 페이스북, 인스타 실행 안됨...
            userAgent = String.format("%s %s", userAgent, appVersion);


            // 변경된 User-Agent 반영
            settings.setUserAgentString(userAgent);

        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    private void getGcmInstanceIdToken() {
        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    private void parseIntentUri(Intent intent) {
        if (intent == null)
            return;

        toUrl = null;

        toUrl = getIntent().getStringExtra("SeoulEduNewsToUrl");
        if (toUrl != null)
            return;

        // 공유 인텐트 파싱.
        Uri uri = intent.getData();
        if (uri != null) {
            String id = uri.getQueryParameter("id");
            String date = uri.getQueryParameter("date");

            if (id != null && date != null) {
                toUrl = APIConfig.APP_HOME_URL + "scrap_view?id=" + id;
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);

        parseIntentUri(intent);

        // onCreate 거치는지 체크.
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Dlog.d("This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public final WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Dlog.d("onPageStarted");
            loadingLayer(true);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Dlog.d("onPageFinished");

            loadingLayer(false);

            if (bSplashShow) {
                bSplashShow = false;

                setTheme(R.style.AppTheme);
                //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

                // Splash hide
                mHandler.sendMessageDelayed(Message.obtain(mHandler, MSG_CLOSE_SPLASH), ELAPSED_CLOSE_SPLASH);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (parseShouldOverrideUrlLoading(view, url)) {
                return true;
            } else {
                loadingLayer(true);
                return super.shouldOverrideUrlLoading(view, url);
            }
        }
    };

    private void loadingLayer(boolean bLayer) {
        Dlog.d("loadingLayer:" + bLayer);

        if (bLayer) {
            ImageView vCapture = (ImageView) findViewById(R.id.vCapture);
            webViewMain.destroyDrawingCache();
            webViewMain.buildDrawingCache();
            Bitmap bitmap3 = webViewMain.getDrawingCache();
            if (bitmap3 != null) {
                vCapture.setImageBitmap(bitmap3);
            } else {
                Dlog.d("bitmap is null");
            }
            vCapture.setVisibility(View.VISIBLE);

            View viewDimmed = findViewById(R.id.vDimmed);
            viewDimmed.setVisibility(View.VISIBLE);

            ProgressBar progressBarLoading = (ProgressBar) findViewById(R.id.progressBarLoading);
            progressBarLoading.setVisibility(View.VISIBLE);
        } else {
            ImageView vCapture = (ImageView) findViewById(R.id.vCapture);
            vCapture.setVisibility(View.GONE);

            View viewDimmed = findViewById(R.id.vDimmed);
            viewDimmed.setVisibility(View.GONE);

            ProgressBar progressBarLoading = (ProgressBar) findViewById(R.id.progressBarLoading);
            progressBarLoading.setVisibility(View.GONE);
        }
    }

    private boolean parseShouldOverrideUrlLoading(WebView view, String url) {
        //currentUrl = url;
        Dlog.d("parseShouldOverrideUrlLoading url:" + url);
        if (url != null) {
            if (url.startsWith("seouledu://")) {
                Dlog.d("seouledu:" + url);
                if (url.startsWith("seouledu://main")) {
                    sendDisplayerInfo();
                } else if (url.startsWith("seouledu://loadsetting")) {
                    sendPushSettings();
                } else if (url.startsWith("seouledu://notisetting")) {
                    saveUserInfo(url);
                } else if (url.startsWith("seouledu://download")) {
                    fileDownload(url);
                } else if (url.startsWith("seouledu://exit")) {
                    showExitAlert();
                } else if (url.startsWith("seouledu://loadingStart")) {
                    loadingStartNoti();
                } else if (url.startsWith("seouledu://loadingEnd")) {
                    loadingEndNoti();
                } else if (url.startsWith("seouledu://openPopupUrl")) {
                    openPopupUrl(url);
                }else if (url.startsWith("seouledu://noticePopup")) {
                    noticePopupUrl(url,"알림");
                }else if (url.startsWith("seouledu://pressPopup")) {
                    noticePopupUrl(url,"보도자료");
                } else if (url.startsWith("seouledu://newsLinkPopup")) {
                    linkPopupUrl(url);
                } else if (url.startsWith("seouledu://newsScrapPopup")) {
                    scrapPopupUrl(url);
                } else if (url.startsWith("seouledu://goSearch")) {
                    Dlog.d("goSearch url:" + url);
                    Uri uri = Uri.parse(url);
                    String corpIsOn = uri.getQueryParameter("corpIsOn");
                    String titleIsOn = uri.getQueryParameter("titleIsOn");
                    String reporterIsOn = uri.getQueryParameter("reporterIsOn");
                    String startDate = uri.getQueryParameter("startDate");
                    String endDate = uri.getQueryParameter("endDate");
                    String strUrl = uri.getQueryParameter("url");
                    strUrl = strUrl + "&corpIsOn=" +corpIsOn + "&titleIsOn=" + titleIsOn + "&reporterIsOn=" + reporterIsOn + "&startDate=" + startDate + "&endDate=" + endDate;
                    goUrl(strUrl);
                } else if (url.startsWith("seouledu://viewPagerImage")) {
                    openImagePopup(url);
                } else if (url.startsWith("seouledu://loadFontSize")) {
                    webViewMain.loadUrl("javascript:setFont(" + pushSettingVo.getFontSize() + ")");
                } else if (url.startsWith("seouledu://share")) {
                    sendShare(url);
                }

                return true;
            }
        }
        return false;
    }

    public void goUrl(String url) {
        Dlog.d("goUrl:" + url);
        webViewMain.loadUrl(url);
    }


    private void openImagePopup(String url) {
        Uri uri = Uri.parse(url);
        String abc = uri.getQueryParameter("url");
        Dlog.d("openImagePopup:" + abc);

        ParcelableUrlInfo item = new ParcelableUrlInfo(1, abc);
        Intent intent = new Intent(getApplicationContext(), ImageViewActivity.class);
        intent.putExtra("send_Parcelable", item);
        startActivityForResult(intent, 1);
    }

    private void openPopupUrl(String url) {
        Uri uri = Uri.parse(url);
        String abc = uri.getQueryParameter("url");
        Dlog.d("openPopupUrl:" + abc);

        ParcelableUrlInfo item = new ParcelableUrlInfo(2, abc);
        Intent intent = new Intent(getApplicationContext(), ExternWebActivity.class);
        intent.putExtra("send_Parcelable", item);
        intent.putExtra("popupTitle", "서울특별시교육청");
        startActivityForResult(intent, 1);
    }

    private void noticePopupUrl(String url, String title) {
        Uri uri = Uri.parse(url);
        String abc = uri.getQueryParameter("url");
        Dlog.d("noticePopupUrl:" + abc);

        ParcelableUrlInfo item = new ParcelableUrlInfo(2, abc);
        Intent intent = new Intent(getApplicationContext(), ExternWebActivity.class);
        intent.putExtra("send_Parcelable", item);
        intent.putExtra("popupTitle", title);
        startActivityForResult(intent, 1);
    }

    private void linkPopupUrl(String url) {
        Uri uri = Uri.parse(url);
        String abc = uri.getQueryParameter("url");
        Dlog.d("linkPopupUrl:" + abc);

        ParcelableUrlInfo item = new ParcelableUrlInfo(2, abc);
        Intent intent = new Intent(getApplicationContext(), ExternWebActivity.class);
        intent.putExtra("send_Parcelable", item);
        intent.putExtra("popupTitle", "뉴스 링크");
        startActivityForResult(intent, 1);
    }

    private void scrapPopupUrl(String url) {
        Uri uri = Uri.parse(url);
        String abc = uri.getQueryParameter("url");
        Dlog.d("scrapPopupUrl:" + abc);

        ParcelableUrlInfo item = new ParcelableUrlInfo(2, abc);
        Intent intent = new Intent(getApplicationContext(), ExternWebActivity.class);
        intent.putExtra("send_Parcelable", item);
        intent.putExtra("popupTitle", "뉴스 스크랩");
        startActivityForResult(intent, 1);
    }

    private void loadingEndNoti() {
        Dlog.d("loadingEndNoti");
        loadingLayer(false);
    }

    private void loadingStartNoti() {
        Dlog.d("loadingStartNoti");
        loadingLayer(true);
    }

    private void loadUserInfo() {
        //User.clearData(this);
        pushSettingVo = User.getData(this);
        if (pushSettingVo == null) {
            pushSettingVo = new PushSettingVo();

            pushSettingVo.setPlatform("android");
            pushSettingVo.setToken("");
            pushSettingVo.setAlarmOnOff(true);
            pushSettingVo.setNoNotiStartTime("-1");
            pushSettingVo.setNoNotiEndTime("-1");
            pushSettingVo.setFontSize("1");
            User.setData(this, pushSettingVo);

        }

        if (!TextUtils.isEmpty(pushSettingVo.getToken())) {
            sendTokenInfo();
        }
    }

    private void saveUserInfo(String url) {
        //seouledu://notisetting?alarmOnOff=true&noNotiStartTime=1610&noNotiEndStartTime=1820
        Dlog.d("notisetting:" + url);
        Uri uri = Uri.parse(url);

        pushSettingVo.setPlatform("android");
        //pushSettingVo.setToken(uri.getQueryParameter("token"));
        pushSettingVo.setAlarmOnOff(uri.getBooleanQueryParameter("alarmOnOff", true));
        pushSettingVo.setNoNotiStartTime(uri.getQueryParameter("noNotiStartTime"));
        pushSettingVo.setNoNotiEndTime(uri.getQueryParameter("noNotiEndTime"));
        pushSettingVo.setFontSize(uri.getQueryParameter("fontSize"));
        User.setData(this, pushSettingVo);
    }

    public void saveTokenOfUserInfo(String token) {
        //if (token.compareTo(pushSettingVo.getToken()) != 0) {
        pushSettingVo.setToken(token);
        User.setData(this, pushSettingVo);
        //}
    }

    private void sendPushSettings() {
        Dlog.d("token:" + pushSettingVo.getToken() + "," + pushSettingVo.isAlarmOnOff() + "," + pushSettingVo.getNoNotiStartTime() + "," + pushSettingVo.getNoNotiEndTime() + "," + pushSettingVo.getFontSize());
        webViewMain.loadUrl("javascript:setSetting('android','" + pushSettingVo.getToken() + "'," + pushSettingVo.isAlarmOnOff() + ",'" + pushSettingVo.getNoNotiStartTime() + "','" + pushSettingVo.getNoNotiEndTime() + "'," + pushSettingVo.getFontSize() + ")");
    }

    private void sendDisplayerInfo() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int heightPixels = metrics.heightPixels;
        int widthPixels = metrics.widthPixels;
        int densityDpi = metrics.densityDpi;
        float density = metrics.density;
        float scaledDensity = metrics.scaledDensity;
        float xdpi = metrics.xdpi;
        float ydpi = metrics.ydpi;

        webViewMain.loadUrl("javascript:setMain('android'," + widthPixels + "," + heightPixels + "," + densityDpi + "," + scaledDensity + "," + xdpi + "," + ydpi + ")");
    }

    public void sendTokenInfo() {
        Dlog.d("sendTokenInfo()");
        new BastaDetail(getApplicationContext())
                .requestSetPushInfo(pushSettingVo)
                .setListener(new APIResponseListener() {
                    @Override
                    public void getData(Object obj) {
                        Dlog.d("sendTokenInfo success");
                    }

                    @Override
                    public void getError(ErrorVO errorVO) {
                        Dlog.d("sendTokenInfo fail");
                    }
                }).build();
    }

    boolean isBackPressed = false;

    @Override
    public void onBackPressed() {
        Dlog.d("onBackPressed");

        WebBackForwardList list = webViewMain.copyBackForwardList();
        Dlog.d("index:" + list.getCurrentIndex() + ", canGoBack:" + webViewMain.canGoBack() + ". url : " + webViewMain.getUrl());

        if (webViewMain.getUrl().equalsIgnoreCase(APIConfig.APP_HOME_URL)) {
            showExitAlert();
        } else if (list.getCurrentIndex() == 0 && !webViewMain.canGoBack()) {
            webViewMain.loadUrl(APIConfig.APP_HOME_URL);
        } else if (webViewMain.getUrl().indexOf("/scrap_view") > 0) {
            webViewMain.loadUrl("javascript:goToList()");
        } else if (webViewMain.getUrl().indexOf("/notice_list") > 0 || webViewMain.getUrl().indexOf("/scrap_list") > 0 || webViewMain.getUrl().indexOf("/favorite_list") > 0) {
            webViewMain.loadUrl(APIConfig.APP_HOME_URL);
        } else {
            webViewMain.goBack();   //webViewMain.loadUrl("javascript:history.back()");
        }
    }

    private void showExitAlert() {
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setTitle("종료");
        alt_bld.setMessage(R.string.warn_exit_alert_msg).setCancelable(
                false).setPositiveButton(R.string.warn_exit_alert_yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'Yes' Button
                        finish();
                    }
                }).setNegativeButton(R.string.warn_exit_alert_no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alt_bld.create();
        alert.setTitle(R.string.warn_exit_alert_title);
        //alert.setIcon(R.drawable.icon);       // Icon for AlertDialog
        alert.show();
    }

    /**
     * file download
     */

    private ProgressDialog progressBar;

    static final int PERMISSION_REQUEST_CODE = 1;
    final String[] PERMISSIONS = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private File outputFile; //파일명까지 포함한 경로
    private File path;//디렉토리경로

    private boolean hasPermissions(String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return true;
        }

        int res;
        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions) {
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)) {
                //퍼미션 허가 안된 경우
                return false;
            }
        }
        //퍼미션이 허가된 경우
        return true;
    }

    private void requestNecessaryPermissions(String[] permissions) {
        //마시멜로( API 23 )이상에서 런타임 퍼미션(Runtime Permission) 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }

    protected void fileDownload(String url) {
        Uri uri = Uri.parse(url);
        final String fileURL = uri.getQueryParameter("url");
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        outputFile = new File(path, uri.getQueryParameter("filename")); //파일명까지 포함함 경로의 File 객체 생성
/*
        //url에서 name 구하기
        String fileName = fileURL.substring(fileURL.lastIndexOf('/')+1, fileURL.length());
        String fileNameWithoutExtn = fileName.substring(0, fileName.lastIndexOf('.'));
        String fileNameExtn = fileName.substring(fileName.lastIndexOf('.')+1, fileName.length());

        String fileNameTime = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date(System.currentTimeMillis()));
*/

        progressBar = new ProgressDialog(MainActivity.this);
        progressBar.setMessage("다운로드중");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setIndeterminate(true);
        progressBar.setCancelable(true);

        if (outputFile.exists()) { //이미 다운로드 되어 있는 경우
            AlertDialog.Builder alt_bld = new AlertDialog.Builder(MainActivity.this);
            alt_bld.setTitle("파일 다운로드");
            alt_bld.setMessage("이미 SD 카드에 존재합니다. 다시 다운로드 받을까요?").setCancelable(
                    false).setPositiveButton(R.string.warn_exit_alert_yes,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Action for 'Yes' Button
                            outputFile.delete(); //파일 삭제

                            final DownloadFilesTask downloadTask = new DownloadFilesTask(MainActivity.this);
                            downloadTask.execute(fileURL);

                            progressBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    downloadTask.cancel(true);
                                }
                            });
                        }
                    }).setNegativeButton(R.string.warn_exit_alert_no,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Action for 'NO' Button
                            //Toast.makeText(getApplicationContext(),"기존 파일을 플레이합니다.",Toast.LENGTH_LONG).show();
                            openDownloadFile(outputFile.getPath());
                            //dialog.cancel();
                        }
                    });
            AlertDialog alert = alt_bld.create();
            alert.setTitle(R.string.warn_exit_alert_title);
            //alert.setIcon(R.drawable.icon);       // Icon for AlertDialog
            alert.show();
        } else { //새로 다운로드 받는 경우
            final DownloadFilesTask downloadTask = new DownloadFilesTask(MainActivity.this);
            downloadTask.execute(fileURL);

            progressBar.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    downloadTask.cancel(true);
                }
            });
        }
    }

    private class DownloadFilesTask extends AsyncTask<String, String, Long> {

        private final Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadFilesTask(Context context) {
            this.context = context;
        }


        //파일 다운로드를 시작하기 전에 프로그레스바를 화면에 보여줍니다.
        @Override
        protected void onPreExecute() { //2
            super.onPreExecute();

            //사용자가 다운로드 중 파워 버튼을 누르더라도 CPU가 잠들지 않도록 해서
            //다시 파워버튼 누르면 그동안 다운로드가 진행되고 있게 됩니다.
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (pm == null) {
                return;
            }
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            mWakeLock.acquire();

            progressBar.show();
        }

        //파일 다운로드를 진행합니다.
        @Override
        protected Long doInBackground(String... string_url) { //3
            int count;
            long FileSize = -1;
            InputStream input = null;
            OutputStream output = null;
            URLConnection connection;

            try {
                URL url = new URL(string_url[0]);
                connection = url.openConnection();
                connection.connect();

                //파일 크기를 가져옴
                FileSize = connection.getContentLength();

                //URL 주소로부터 파일다운로드하기 위한 input stream
                input = new BufferedInputStream(url.openStream(), 8192);

                //path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                //outputFile = new File(path, "Alight.avi"); //파일명까지 포함함 경로의 File 객체 생성

                // SD카드에 저장하기 위한 Output stream
                output = new FileOutputStream(outputFile);

                byte data[] = new byte[1024];
                long downloadedSize = 0;
                while ((count = input.read(data)) != -1) {
                    //사용자가 BACK 버튼 누르면 취소가능
                    if (isCancelled()) {
                        input.close();
                        return (long) -1;
                    }

                    downloadedSize += count;

                    if (FileSize > 0) {
                        float per = ((float) downloadedSize / FileSize) * 100;
                        String str = "Downloaded " + downloadedSize + "B / " + FileSize + "B (" + (int) per + "%)";
                        publishProgress("" + (int) ((downloadedSize * 100) / FileSize), str);
                    }

                    //파일에 데이터를 기록합니다.
                    output.write(data, 0, count);
                }
                // Flush output
                output.flush();

                // Close streams
                output.close();
                input.close();
            } catch (Exception e) {
                Dlog.e(e.getMessage());
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }
                mWakeLock.release();
            }
            return FileSize;
        }

        private String convertFileSize(Long size) {
            double fSize = (double) size;
            if (fSize < 1024)
                return size.toString();
            if (fSize < 1024 * 1024)
                return String.format(Locale.getDefault(), "%.1f", Math.round((fSize / 1024) * 10d) / 10d) + "KB";
            if (fSize < 1024 * 1024 * 1024)
                return String.format(Locale.getDefault(), "%.1f", Math.round((fSize / 1024 / 1024) * 10d) / 10d) + "MB";
            if (fSize < 1024 * 1024 * 1024 * 1024)
                return String.format(Locale.getDefault(), "%.1f", Math.round((fSize / 1024 / 1024 / 1024) * 10d) / 10d) + "GB";

            return String.format(Locale.getDefault(), "%.1f", Math.round((fSize / 1024 / 1024 / 1024 / 1024) * 10d) / 10d) + "TB";
        }

        //다운로드 중 프로그레스바 업데이트
        @Override
        protected void onProgressUpdate(String... progress) { //4
            super.onProgressUpdate(progress);

            // if we get here, length is known, now set indeterminate to false
            progressBar.setIndeterminate(false);
            progressBar.setMax(100);
            progressBar.setProgress(Integer.parseInt(progress[0]));
            progressBar.setMessage(progress[1]);
        }

        //파일 다운로드 완료 후
        @Override
        protected void onPostExecute(Long size) { //5
            super.onPostExecute(size);

            progressBar.dismiss();
            if (size > 0) {
                //Toast.makeText(getApplicationContext(), "다운로드 완료되었습니다. 파일 크기=" + size.toString(), Toast.LENGTH_LONG).show();
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(Uri.fromFile(outputFile));
                sendBroadcast(mediaScanIntent);

                openDownloadFile(outputFile.getPath());
            } else {
                //Toast.makeText(getApplicationContext(), "다운로드 에러", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (permsRequestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean readAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        if (!readAccepted || !writeAccepted) {
                            showDialogforPermission("앱을 실행하려면 퍼미션을 허가하셔야 합니다.");
                            return;
                        }
                    }
                }
                break;
        }
    }

    private void showDialogforPermission(String msg) {
        final AlertDialog.Builder myDialog = new AlertDialog.Builder(MainActivity.this);
        myDialog.setTitle("알림");
        myDialog.setMessage(msg);
        myDialog.setCancelable(false);
        myDialog.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(PERMISSIONS, PERMISSION_REQUEST_CODE);
                }

            }
        });
        myDialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        myDialog.show();
    }

    private void openDownloadFile(String path) {
        Toast.makeText(mContext, "다운로드가 완료 되었습니다.", Toast.LENGTH_SHORT).show();

        Uri fileUri = Uri.fromFile(new File(path));

        Intent fileOpenIntent = new Intent(Intent.ACTION_VIEW);
        fileOpenIntent.addCategory(Intent.CATEGORY_DEFAULT);

        String fileNameExtn = path.substring(path.lastIndexOf('.') + 1, path.length());
        if (fileNameExtn.equalsIgnoreCase("mp4")
                || fileNameExtn.equalsIgnoreCase("3gp")) {
            fileOpenIntent.setDataAndType(fileUri, "video/*");
        } else if (fileNameExtn.equalsIgnoreCase("mp3")
                || fileNameExtn.equalsIgnoreCase("wav")
                || fileNameExtn.equalsIgnoreCase("ogg")) {
            fileOpenIntent.setDataAndType(fileUri, "audio/*");
        } else if (fileNameExtn.equalsIgnoreCase("jpg")
                || fileNameExtn.equalsIgnoreCase("jpeg")
                || fileNameExtn.equalsIgnoreCase("gif")
                || fileNameExtn.equalsIgnoreCase("png")
                || fileNameExtn.equalsIgnoreCase("bmp")) {
            fileOpenIntent.setDataAndType(fileUri, "image/*");
        } else if (fileNameExtn.equalsIgnoreCase("txt")) {
            fileOpenIntent.setDataAndType(fileUri, "text/*");
        } else if (fileNameExtn.equalsIgnoreCase("doc")
                || fileNameExtn.equalsIgnoreCase("docx")) {
            fileOpenIntent.setDataAndType(fileUri, "application/msword");
        } else if (fileNameExtn.equalsIgnoreCase("xls")
                || fileNameExtn.equalsIgnoreCase("xlsx")) {
            fileOpenIntent.setDataAndType(fileUri, "application/vnd.ms-excel");
        } else if (fileNameExtn.equalsIgnoreCase("ppt")
                || fileNameExtn.equalsIgnoreCase("pptx")) {
            fileOpenIntent.setDataAndType(fileUri, "application/vnd.ms-powerpoint");
        } else if (fileNameExtn.equalsIgnoreCase("pdf")) {
            fileOpenIntent.setDataAndType(fileUri, "application/pdf");
        } else if (fileNameExtn.equalsIgnoreCase("hwp")) {
            fileOpenIntent.setDataAndType(fileUri, "application/haansofthwp");
        } else {
            return;
        }

        if (fileOpenIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(fileOpenIntent, null));
        } else {
            Toast.makeText(mContext, "해당 파일을 볼 수 있는 앱이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
        }

    }

    private void sendShare(String url) {
        Uri uri = Uri.parse(url);
        String id = uri.getQueryParameter("id");
        String date = uri.getQueryParameter("date");
        String news_title = uri.getQueryParameter("news_title");
        String media_name = uri.getQueryParameter("media_name");
        String shortUrl = uri.getQueryParameter("shortUrl");

        String title = "서울교육뉴스\n" + news_title + "\n\n" + "출처: " + media_name + "\n\n";
        Log.d("junseo", "PPP: "+shortUrl);
        Dlog.d("shortUrl ===>  "+shortUrl);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, shortUrl);

        intent.addCategory(Intent.CATEGORY_DEFAULT);
        startActivity(Intent.createChooser(intent, "공유"));
    }

}
