package com.tobeitech.seouledunews.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.tobeitech.api.utils.APIUtils;
import com.tobeitech.api.vo.pushsetting.PushSettingVo;

//import com.tobeitech.seouledunews.PreferencesKey;

/**
 * Created by Ted
 */
public class User {

    /**
     * Setting user data
     *
     * @param context       context
     * @param pushSettingVo PushSettingVo
     */
    public static void setData(Context context, PushSettingVo pushSettingVo) {
        SharedPreferences pref = context.getSharedPreferences(PreferencesKey.PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putString(PreferencesKey.USER, APIUtils.toJson(pushSettingVo));
        prefsEditor.apply();
    }

    /**
     * Get user data
     *
     * @param context context
     * @return UserVO
     */
    public static PushSettingVo getData(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PreferencesKey.PREFERENCES_KEY, Context.MODE_PRIVATE);
        String user = pref.getString(PreferencesKey.USER, null);
        if (user == null)
            return null;
        else
            return (PushSettingVo) APIUtils.toJsonString(user, PushSettingVo.class);
    }

    public static void clearData(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PreferencesKey.PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.clear();
        prefsEditor.apply();
        //prefsEditor.commit();
    }

    /**
     * Get user id
     *
     * @param context context
     * @return user id
     */


//
//    /**
//     * Get user type
//     *
//     * @param context context
//     * @return user type (seller : 1, child_seller : 2, customer : 3)
//     */
//    public static int getType(Context context) {
//        return getData(context).getType();
//    }
}
