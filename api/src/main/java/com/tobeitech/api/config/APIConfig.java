package com.tobeitech.api.config;

import com.tobeitech.api.BuildConfig;

/**
 * API communication Config
 * Created by Ted
 */
public class APIConfig {

    public static final String PREFERENCES_NAME = "PREFERENCES_NAME";

    /**
     * api home
     */
    public static final String BASE_URL;
    /**
     * front
     */
    public static final String APP_HOME_URL;

    static {
        if (BuildConfig.DEBUG && false) {
            //BASE_URL = "http://106.247.231.42:15762";
            //APP_HOME_URL = "http://106.247.231.42:6673/";
            BASE_URL = "http://192.168.111.20:15762";
            APP_HOME_URL = "http://192.168.111.20:6673/";
            //BASE_URL = "http://seoul-news.com";
            //APP_HOME_URL = BASE_URL + "/app/";
        } else {
            // junseo - org
//            BASE_URL = "http://192.168.111.20:15762";
//            APP_HOME_URL = "http://192.168.111.20:6673/";
//            //BASE_URL = "http://seoul-news.com";
//            //APP_HOME_URL = BASE_URL + "/app/";

            // junseo - change
//            BASE_URL = "http://192.168.111.20:15762";
//            APP_HOME_URL = "http://192.168.111.20:6673/";
            BASE_URL = "http://seoul-news.com";
            APP_HOME_URL = BASE_URL +"/app/main_new";
//            APP_HOME_URL = BASE_URL + "/app/";    // junseo
        }

    }
}
