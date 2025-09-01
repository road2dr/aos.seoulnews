package com.tobeitech.api.requestclient;

import android.content.Context;
import android.support.annotation.NonNull;

import com.tobeitech.api.manager.NativeManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ted
 */
public class RequestClient {

    public static final boolean isLog = false;

    private final Context mContext;
    private final NativeManager mNativeManager;

    public RequestClient(Context context) {
        mContext = context;
        mNativeManager = NativeManager.getInstance();
    }

    /**
     * Retrofit clients that use a common
     *
     * @param clazz service interface
     * @return Retrofit Client
     */
    public Object getClient(Class clazz) {
        OkHttpClient.Builder okClientBuilder = new OkHttpClient.Builder();
        if (isLog) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okClientBuilder.interceptors().add(interceptor);
        }

        okClientBuilder.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                return chain.proceed(com.tobeitech.api.requestclient.RequestHeaders.getInstance(mContext).getCommonHeader(chain));
            }
        });

        Retrofit client = new Retrofit.Builder()
                .baseUrl(mNativeManager.getBaseUrl(mContext))
                .client(okClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return client.create(clazz);
    }

    /**
     * Retrofit clients that do not use the token
     *
     * @param clazz service interface
     * @return Retrofit Client
     */
    public Object getNotTokenClient(Class clazz) {
        OkHttpClient.Builder okClientBuilder = new OkHttpClient.Builder();
        if (isLog) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okClientBuilder.interceptors().add(interceptor);
        }

        okClientBuilder.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                return chain.proceed(com.tobeitech.api.requestclient.RequestHeaders.getInstance(mContext).getNotTokenHeader(chain));
            }
        });

        Retrofit client = new Retrofit.Builder()
                .baseUrl(mNativeManager.getBaseUrl(mContext))
                .client(okClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return client.create(clazz);
    }

    /**
     * Retrofit clients that use the File Upload
     *
     * @param clazz service interface
     * @return Retrofit Client
     */
    public Object getFileUploadClient(Class clazz) {
        OkHttpClient.Builder okClientBuilder = new OkHttpClient.Builder();
        if (isLog) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okClientBuilder.interceptors().add(interceptor);
        }

        okClientBuilder.interceptors().add(new Interceptor() {

            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                return chain.proceed(com.tobeitech.api.requestclient.RequestHeaders.getInstance(mContext).getFileUploadHeader(chain));
            }
        });

        Retrofit client = new Retrofit.Builder()
                .baseUrl(mNativeManager.getBaseUrl(mContext))
                .client(okClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return client.create(clazz);
    }

}
