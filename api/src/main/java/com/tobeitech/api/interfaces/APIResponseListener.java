package com.tobeitech.api.interfaces;


import com.tobeitech.api.vo.ErrorVO;

/**
 * API response listener
 * Created by Ted
 */
public interface APIResponseListener {

    void getData(Object obj);

    void getError(ErrorVO errorVO);

}
