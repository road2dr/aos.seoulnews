package com.tobeitech.api.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore.Images;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.tobeitech.api.config.ErrorType;
import com.tobeitech.api.interfaces.APIResponseListener;
import com.tobeitech.api.requestclient.APIRequestManager;
import com.tobeitech.api.requestclient.RequestClient;
import com.tobeitech.api.vo.ErrorVO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * API Utils
 * Created by Ted
 */
public class APIUtils {

    /**
     * API request error
     * response received but request not successful (like 400,401,403,404 etc)
     *
     * @param context  context
     * @param uniqueID API unique ID
     * @param obj      response object
     * @param listener APIResponseListener
     */
    public static void errorResponse(Context context, String uniqueID, Response<Object> obj, APIResponseListener listener) {
        try {
            ErrorVO vo = (ErrorVO) toJsonString(obj.errorBody().string(), ErrorVO.class);
            if (vo != null) {
                if (RequestClient.isLog)
                    Dlog.e("Error code : " + vo.getError().getCode() + ", message : " + vo.getError().getMessage() + " type : " + vo.getError().getType());
                switch (vo.getError().getType()) {
                    // 토큰 만료 오류 시
                    case ErrorType.TOKEN_ACCESS_EXPIRED:
                    case ErrorType.TOKEN_NOT_EXIST:
                        APIRequestManager.getInstance().cancelAllRequest(false);
                        APIRequestManager.getInstance().responseTokenError();
                        // AuthDetail.requestRefreshToken(context);
                        break;

                    default:
                        APIRequestManager.getInstance().removeRequestCall(uniqueID);
                        if (listener != null)
                            listener.getError(vo);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * API request failure
     *
     * @param context  context
     * @param t        error object
     * @param listener api response listener
     */
    public static void errorFailure(Context context, String uniqueID, Throwable t, APIResponseListener listener) {
        if (RequestClient.isLog) Dlog.e("[errorFailure]" + t.getMessage());
        APIRequestManager.getInstance().removeRequestCall(uniqueID);
    }

    /**
     * Get random code
     *
     * @return random code
     */
    public static String getRandomCode() {
        Random rnd = new Random();
        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < 20; i++) {
            if (rnd.nextBoolean()) {
                buf.append((char) (rnd.nextInt(26) + 97));
            } else {
                buf.append((rnd.nextInt(10)));
            }
        }

        return String.valueOf(buf);
    }

    /**
     * Get device country
     *
     * @param context context
     * @return country code
     */
    public static String getDeviceCountryCode(Context context) {
        return String.valueOf(context.getResources().getConfiguration().locale.getCountry());
    }

    /**
     * Get device language
     *
     * @param context context
     * @return language code
     */
    public static String getDeviceLanguage(Context context) {
        return String.valueOf(context.getResources().getConfiguration().locale.toString());
    }

    /**
     * Get device timezone offset
     *
     * @return timezone offest
     */
    public static String getCurrentTimezoneOffset() {
        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        int mGMTOffset = -mTimeZone.getRawOffset();

        return String.valueOf(TimeUnit.MINUTES.convert(mGMTOffset, TimeUnit.MILLISECONDS));
    }

    /**
     * Get app version code
     *
     * @param context     context
     * @param packageName app package name
     * @return version code
     */
    public static int getVersionCode(Context context, String packageName) {
        int v = 0;
        try {
            v = context.getPackageManager().getPackageInfo(packageName, 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    /**
     * Make a vo to json string
     *
     * @param json  json string
     * @param clazz convert class
     * @return vo object
     */
    public static Object toJsonString(String json, Class clazz) {
        Gson gson = new Gson();
        TypeAdapter adapter = gson.getAdapter(clazz);
        try {
            return adapter.fromJson(json);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Make a vo to json object
     *
     * @param object json object
     * @param clazz  convert class
     * @return vo object
     */
    public static Object toJsonObject(Object object, Class clazz) {
        Gson gson = new Gson();
        String json = gson.toJson(object);
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(json).getAsJsonObject();
        return gson.fromJson(jsonObject, clazz);
    }

    /**
     * Make to json
     *
     * @param obj vo object
     * @return Json String
     */
    public static String toJson(Object obj) {
        return new Gson().toJson(obj);
    }



    public static boolean isEmpty(Object obj) {
        if (obj == null) { return true; }
        if ((obj instanceof String) && (((String)obj).trim().length() == 0)) { return true; }
        if (obj instanceof Map) { return ((Map<?, ?>)obj).isEmpty(); }
        if (obj instanceof List) { return ((List<?>)obj).isEmpty(); }
        if (obj instanceof Object[]) { return (((Object[])obj).length == 0); }

        return false;
    }


    // junseo
    public static void requestMetadataImage(Context context, final String url, final OnResultListener<Uri> listener) {
        final ContentResolver resolver = context.getContentResolver();

        Single<Uri> single = Single.create(new SingleOnSubscribe<Uri>() {
            @Override
            public void subscribe(@NonNull SingleEmitter<Uri> emitter) throws Exception {
                Document doc = Jsoup.connect(url).get();
                String image = doc.select("meta[property=og:image]").get(0).attr("content");

//                File file =  new File(Environment.getExternalStoragePublicDirectory(
//                        Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
//                file.getParentFile().mkdirs();
//                FileOutputStream out = new FileOutputStream(file);
//                bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
//                out.close();
//                bmpUri = Uri.fromFile(file);

                String fileName = url.substring( url.lastIndexOf('/')+1, url.length() );

                URL url = new URL(image);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);

                String path = Images.Media.insertImage(resolver, bitmap, fileName, null);
                Uri bitmapUri = Uri.parse(path);

                emitter.onSuccess(bitmapUri);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

        Disposable disposable = single.subscribe(new Consumer<Uri>() {
            @Override
            public void accept(Uri result) throws Exception {
                if (listener != null)
                    listener.onResult(result);
            }
        });
    }



    public static String getMetadataImage(String url) {
        try {
            String image = "";

            Document doc = Jsoup.connect(url).get();
//            metadata.title = doc.select("meta[property=og:title]").first().attr("content");
//            metadata.description = doc.select("meta[property=og:description]").get(0).attr("content");
            image = doc.select("meta[property=og:image]").get(0).attr("content");
            return image;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public interface OnResultListener<T> {
        public void onResult(T result);
    }
}
