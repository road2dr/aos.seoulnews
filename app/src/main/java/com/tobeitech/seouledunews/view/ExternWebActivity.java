package com.tobeitech.seouledunews.view;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tobeitech.api.config.APIConfig;
import com.tobeitech.api.details.BastaDetail;
import com.tobeitech.api.interfaces.APIResponseListener;
import com.tobeitech.api.utils.APIUtils;
import com.tobeitech.api.utils.APIUtils.OnResultListener;
import com.tobeitech.api.utils.Dlog;
import com.tobeitech.api.vo.ErrorVO;
import com.tobeitech.api.vo.pushsetting.PushSettingVo;
import com.tobeitech.seouledunews.R;
import com.tobeitech.seouledunews.util.ParcelableUrlInfo;
import com.tobeitech.seouledunews.util.User;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Locale;

/**
 * Created by LocalUser0 on 2017-06-17.
 */

public class ExternWebActivity extends AppCompatActivity {
    private WebView webViewExternWeb;
    private WebSettings webSettings;
    private boolean bReloadedPage;
    private String externWebUrl;
    private PushSettingVo pushSettingVo;
    private ImageButton imageButtonClose;
    private ImageButton imageButtonScreenShot;
    public static Context mContext;

    Activity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_externweb);

        inteVariable();
        initControls();
        initUserAgent();

        loadUrl(externWebUrl);

        mActivity = this;
        mContext = this;
    }

    private void inteVariable() {

        if (!hasPermissions(PERMISSIONS)) { //퍼미션 허가를 했었는지 여부를 확인
            requestNecessaryPermissions(PERMISSIONS);//퍼미션 허가안되어 있다면 사용자에게 요청
        } else {
            //이미 사용자에게 퍼미션 허가를 받음.
        }

        loadUserInfo();

        bReloadedPage = false;

        ParcelableUrlInfo item = getIntent().getParcelableExtra("send_Parcelable");
        String popupTitle = getIntent().getStringExtra("popupTitle");

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(popupTitle);

        Dlog.d("type:" + item.getType());
        Dlog.d("url:" + item.getUrl());

        externWebUrl = item.getUrl();
    }

    private void initControls() {
        imageButtonClose = (ImageButton) findViewById(R.id.imageButton_closeWeb);
        imageButtonClose.setOnClickListener(buttonClickListener);

        imageButtonScreenShot = (ImageButton) findViewById(R.id.imageButton_screenShot);
        imageButtonScreenShot.setOnClickListener(buttonClickListener);

        webViewExternWeb = (WebView) findViewById(R.id.webView_ExternWeb);

        webSettings = webViewExternWeb.getSettings();
        webSettings.setJavaScriptEnabled(true);             //javascript
        webSettings.setBuiltInZoomControls(false);           //zoom
        webSettings.setDisplayZoomControls(false);          //돋보기
        //webSettings.setSupportZoom(true);

        // todo junseo
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {    // junseo
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptThirdPartyCookies(webViewExternWeb, true);
        }


        webViewExternWeb.setInitialScale(100);
        webViewExternWeb.setWebViewClient(webViewClient);
        //webViewExternWeb.setWebChromeClient(new WebChromeClient() {  //dialog, favicon, title, progress 사용
        //});
    }

    private void initUserAgent() {
        WebSettings settings = webViewExternWeb.getSettings();
        webViewExternWeb.getSettings().setUseWideViewPort(true);
        webViewExternWeb.getSettings().setLoadWithOverviewMode(true);

        // junseo
        settings.setSupportZoom(true);

        settings.setJavaScriptEnabled(true);

        Context context = webViewExternWeb.getContext();
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
            // junseo appName 제거. 한글 agent 가 페이스북 인스타그램 오류 발생 시킴.
            userAgent = String.format("%s %s", userAgent, appVersion);


            // 변경된 User-Agent 반영
            settings.setUserAgentString(userAgent);

        } catch (Exception e) {
            // e.printStackTrace();
        }
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

    private void loadUrl(String url) {
        webViewExternWeb.loadUrl(url);
    }

    final WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            Dlog.d("onPageFinished");
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Dlog.d("onPageStarted");
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("junseo3", "shouldOverrideUrlLoading: "+url);
            if (url != null && url.startsWith("intent:")) {
                try {
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    intent.addCategory("android.intent.category.BROWSABLE");    // junseo
                    intent.setComponent(null);
                    intent.setSelector(null);
                    Intent existPackage = getPackageManager().getLaunchIntentForPackage(intent.getPackage());
                    if (existPackage != null) {
                        startActivity(intent);
                    } else {
                        Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                        intent.addCategory("android.intent.category.BROWSABLE");
                        intent.setComponent(null);
                        intent.setSelector(null);
                        marketIntent.setData(Uri.parse("market://details?id=" + intent.getPackage()));
                        startActivity(marketIntent);
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (url != null && url.startsWith("market://")) {
                try {
                    Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    intent.addCategory("android.intent.category.BROWSABLE");
                    intent.setComponent(null);
                    intent.setSelector(null);
                    if (intent != null) {
                        startActivity(intent);
                    }
                    return true;
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            } else {
                if (parseShouldOverrideUrlLoading(view, url)) {
                    return true;
                } else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }

//            view.loadUrl(url);
            return false;
        }
    };

    private void sendPushSettings() {
        Dlog.d("token:" + pushSettingVo.getToken() + "," + pushSettingVo.isAlarmOnOff() + "," + pushSettingVo.getNoNotiStartTime() + "," + pushSettingVo.getNoNotiEndTime() + "," + pushSettingVo.getFontSize());
        webViewExternWeb.loadUrl("javascript:setSetting('android','" + pushSettingVo.getToken() + "'," + pushSettingVo.isAlarmOnOff() + ",'" + pushSettingVo.getNoNotiStartTime() + "','" + pushSettingVo.getNoNotiEndTime() + "'," + pushSettingVo.getFontSize() + ")");
    }

    public boolean parseShouldOverrideUrlLoading(WebView view, String url) {
        //currentUrl = url;
        Dlog.d("parseShouldOverrideUrlLoading url:" + url);
        if (url != null) {
            if (url.startsWith("seouledu://")) {
                if (url.startsWith("seouledu://loadFontSize")) {
                    webViewExternWeb.loadUrl("javascript:setFont(" + pushSettingVo.getFontSize() + ")");
                }
                else if (url.startsWith("seouledu://download")) {
                    fileDownload(url);
                }
                else if (url.startsWith("seouledu://loadsetting"))
                {
                    sendPushSettings();
                }
                else if (url.startsWith("seouledu://newsScrapPopupClose"))
                {
                    finish();
                } else if (url.startsWith("seouledu://newsScrapPopupHome")) {
                    ((MainActivity) MainActivity.mContext).goUrl(APIConfig.APP_HOME_URL);
                    finish();
                }else if (url.startsWith("seouledu://goSearch")) {
                    Dlog.d("goSearch url:" + url);
                    Uri uri = Uri.parse(url);
                    String corpIsOn = uri.getQueryParameter("corpIsOn");
                    String titleIsOn = uri.getQueryParameter("titleIsOn");
                    String reporterIsOn = uri.getQueryParameter("reporterIsOn");
                    String startDate = uri.getQueryParameter("startDate");
                    String endDate = uri.getQueryParameter("endDate");
                    String strUrl = uri.getQueryParameter("url");
                    strUrl = strUrl + "&corpIsOn=" +corpIsOn + "&titleIsOn=" + titleIsOn + "&reporterIsOn=" + reporterIsOn + "&startDate=" + startDate + "&endDate=" + endDate;
                    ((ExternWebActivity)ExternWebActivity.mContext).goUrl(strUrl);
                    finish();
                }else if (url.startsWith("seouledu://viewPagerImage")) {
                    openImagePopup(url);
                }else if (url.startsWith("seouledu://share")) {
                    sendShare(url);
                }

                return true;
            }else if (url.startsWith("http://enews.sen.go.kr/McmsAdmin/Common/inc/Download2.jsp")) {
                Dlog.d("sen.go.kr fileDownload url:" + url);
                Uri uri = Uri.parse(url);
                final String fileURL = uri.getQueryParameter("url");
                final String fileURL2 = uri.getQueryParameter("file");
//                try {
//                    fileURL2 = URLDecoder.decode( fileURL2 , "UTF8" );
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
                final String fileURLResult = "http://enews.sen.go.kr/McmsAdmin/Common/inc/Download2.jsp?url=http://enews.sen.go.kr/" + fileURL + "/" + fileURL2;
                Dlog.d("sen.go.kr fileDownload fileURLResult:" + fileURLResult);
                fileDownload(fileURL);
            }
        }
        return false;
    }

    public void goUrl(String url) {
        Dlog.d("goUrl:" + url);
        webViewExternWeb.loadUrl(url);
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

        progressBar = new ProgressDialog(ExternWebActivity.this);
        progressBar.setMessage("다운로드중");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setIndeterminate(true);
        progressBar.setCancelable(true);

        if (outputFile.exists()) { //이미 다운로드 되어 있는 경우
            AlertDialog.Builder alt_bld = new AlertDialog.Builder(ExternWebActivity.this);
            alt_bld.setTitle("파일 다운로드");
            alt_bld.setMessage("이미 SD 카드에 존재합니다. 다시 다운로드 받을까요?").setCancelable(
                    false).setPositiveButton(R.string.warn_exit_alert_yes,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Action for 'Yes' Button
                            outputFile.delete(); //파일 삭제

                            final ExternWebActivity.DownloadFilesTask downloadTask = new ExternWebActivity.DownloadFilesTask(ExternWebActivity.this);
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
            final ExternWebActivity.DownloadFilesTask downloadTask = new ExternWebActivity.DownloadFilesTask(ExternWebActivity.this);
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
        final AlertDialog.Builder myDialog = new AlertDialog.Builder(ExternWebActivity.this);
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

    // original code
    private void sendShare(String url) {
        Uri uri = Uri.parse(url);
        String id = uri.getQueryParameter("id");
        String date = uri.getQueryParameter("date");
        String news_title = uri.getQueryParameter("news_title");
        String media_name = uri.getQueryParameter("media_name");
        String shortUrl = uri.getQueryParameter("shortUrl");

        String title = "서울교육뉴스\n" + news_title + "\n\n" + "출처: " + media_name + "\n\n";

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, shortUrl);

        intent.addCategory(Intent.CATEGORY_DEFAULT);
        startActivity(Intent.createChooser(intent, "공유"));
    }

    // junseo code
//    private void sendShare(String url) {
//        Uri uri = Uri.parse(url);
//        String id = uri.getQueryParameter("id");
//        String date = uri.getQueryParameter("date");
//        String news_title = uri.getQueryParameter("news_title");
//        String media_name = uri.getQueryParameter("media_name");
//        final String shortUrl = uri.getQueryParameter("shortUrl");
//
//        final String title = "서울교육뉴스\n" + news_title + "\n\n" + "출처: " + media_name + "\n\n";
//
////        String metaImage = APIUtils.getMetadataImage(shortUrl);
////        Log.d("junseo", "gg: "+metaImage);
//
//        APIUtils.requestMetadataImage(ExternWebActivity.this, shortUrl, new OnResultListener<Uri>() {
//            @Override
//            public void onResult(Uri imgUri) {
//                Log.d("junseo", "gg: "+imgUri);
//
////                Uri uri = Uri.parse(metaImage);
//
//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.setType("text/plain");
////                intent.setType("image/*");
//                intent.putExtra(Intent.EXTRA_SUBJECT, title);
//                intent.putExtra(Intent.EXTRA_TEXT, shortUrl);
//                intent.putExtra(Intent.EXTRA_STREAM, imgUri);
//
////                intent.addCategory(Intent.CATEGORY_DEFAULT);
//                startActivity(Intent.createChooser(intent, "공유"));
//            }
//        });
//    }

    public Bitmap makeImage(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }



    private void screenshot(Bitmap bm) {

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},00);

        try {


            File path = null;

            String ext = Environment.getExternalStorageState();
            if (ext.equals(Environment.MEDIA_MOUNTED)) {
                path = new File(Environment.getExternalStorageDirectory()+File.separator+"DCIM/Camera");
            }else
            {
                path = new File(Environment.getDataDirectory()+File.separator+"DCIM/Camera");
            }

            if(! path.isDirectory()) {

                path.mkdirs();

            }



            Dlog.d("!!!!!! path: "+ path.toString());

             Date now = new Date();
            String strDate = android.text.format.DateFormat.format("yyyyMMddhhmmss", now).toString();
            String filename = "seoulEdu_"+strDate+".jpg";
            String fullPath = path + File.separator + filename;

            FileOutputStream out = new FileOutputStream(fullPath);

            bm.compress(Bitmap.CompressFormat.JPEG, 100, out);




            if (ext.equals(Environment.MEDIA_MOUNTED)) {

                // 외장메모리가 마운트 되어 있을 때

                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+ fullPath)));

            } else {

                // 외장메모리가 마운트 되어 있지 않을 때


                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.parse("file://"+ fullPath)));

            }

            Toast.makeText(ExternWebActivity.this, "화면이 캡쳐되었습니다 " + fullPath, Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {

            Dlog.d("FileNotFoundException: "+ e.toString());

        }

    }



    final View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.imageButton_closeWeb) {
                finish();
                //externWebUrl = "http://image.fnnews.com/resource/paper/image/2017/06/09/f201706091501_l.jpg";
                //loadUrl(externWebUrl);
            }else if (v.getId() == R.id.imageButton_screenShot) {
                //                screenshot(getBitmapFromView(mActivity.getWindow().getDecorView()));
//
                screenshot(makeImage(webViewExternWeb));

            }
        }
    };

}
