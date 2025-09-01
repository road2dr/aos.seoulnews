package com.tobeitech.api.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.tobeitech.api.config.APIConfig;
import com.tobeitech.api.config.APIPreferencesKey;

/**
 * Access Token and Refresh Token Manager Class
 * Created by ted
 */
public class TokenManager {

    private static TokenManager mInstance;

    public static TokenManager getInstance() {
        if (mInstance == null) {
            mInstance = new TokenManager();
        }
        return mInstance;
    }

    /**
     * Set access token
     */
    public void setAccessToken(Context context, String token) {
        SharedPreferences pref = context.getSharedPreferences(APIConfig.PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(APIPreferencesKey.ACCESS_TOKEN, token);
        edit.apply();
    }

    /**
     * Get access token
     *
     * @return Access Toekn
     */
    public String getAccessToken(Context context) {
        SharedPreferences pref = context.getSharedPreferences(APIConfig.PREFERENCES_NAME, Context.MODE_PRIVATE);
        return pref.getString(APIPreferencesKey.ACCESS_TOKEN, null);
    }

    /**
     * Set refresh token
     */
    public void setRefreshToken(Context context, String token) {
        SharedPreferences pref = context.getSharedPreferences(APIConfig.PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(APIPreferencesKey.REFRESH_TOKEN, token);
        edit.apply();
    }

    /**
     * Get refresh token
     *
     * @return refresh token
     */
    public String getRefreshToken(Context context) {
        SharedPreferences pref = context.getSharedPreferences(APIConfig.PREFERENCES_NAME, Context.MODE_PRIVATE);
        return pref.getString(APIPreferencesKey.REFRESH_TOKEN, null);
    }
}
