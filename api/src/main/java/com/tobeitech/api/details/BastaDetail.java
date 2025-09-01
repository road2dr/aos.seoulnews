package com.tobeitech.api.details;

import android.content.Context;
import android.support.annotation.NonNull;

import com.tobeitech.api.interfaces.APIResponseListener;
import com.tobeitech.api.requestclient.APIRequestManager;
import com.tobeitech.api.requestclient.APIRequestVO;
import com.tobeitech.api.requestclient.RequestClient;
import com.tobeitech.api.services.BastaServices;
import com.tobeitech.api.utils.APIUtils;
import com.tobeitech.api.utils.Dlog;
import com.tobeitech.api.vo.MemberVO;
import com.tobeitech.api.vo.alarmreply.AlarmReplyVo;
import com.tobeitech.api.vo.bastaprofile.BastaVo;
import com.tobeitech.api.vo.join.JoinVo;
import com.tobeitech.api.vo.login.LoginVo;
import com.tobeitech.api.vo.pushsetting.PushSettingVo;
import com.tobeitech.api.vo.userinfo.UserInfoVo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * API Collection
 * Created by Ted
 */
public class BastaDetail {

    private final Context mContext;
    private final APIRequestManager mAPIRequestManager;
    private APIResponseListener mAPIResponseListener = null;

    private APIRequestVO<Object> mRequestItem;
    private final String mUniqueID;

    public BastaDetail(Context context) {
        mContext = context;
        mAPIRequestManager = APIRequestManager.getInstance();
        mUniqueID = APIUtils.getRandomCode();
    }

    public BastaDetail requestGetAlarmReply(int uid) {
        mRequestItem = new APIRequestVO<>();
        BastaServices service = (BastaServices) new RequestClient(mContext).getClient(BastaServices.class);
        mRequestItem.setCall(service.requestGetAlarmReply(uid));
        mRequestItem.setCallback(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful()) {
                    AlarmReplyVo vo = (AlarmReplyVo) APIUtils.toJsonObject(response.body(), AlarmReplyVo.class);
                    mAPIRequestManager.successResponse(mUniqueID, vo, mAPIResponseListener);
                } else {
                    APIUtils.errorResponse(mContext, mUniqueID, response, mAPIResponseListener);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                APIUtils.errorFailure(mContext, mUniqueID, t, mAPIResponseListener);
            }
        });

        return this;
    }

    public BastaDetail requestGetBastaProfile(int uid, String orderType) {
        mRequestItem = new APIRequestVO<>();
        BastaServices service = (BastaServices) new RequestClient(mContext).getClient(BastaServices.class);
        mRequestItem.setCall(service.requestGetBastaProfile(uid, orderType));
        mRequestItem.setCallback(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful()) {
                    BastaVo vo = (BastaVo) APIUtils.toJsonObject(response.body(), BastaVo.class);
                    mAPIRequestManager.successResponse(mUniqueID, vo, mAPIResponseListener);
                } else {
                    APIUtils.errorResponse(mContext, mUniqueID, response, mAPIResponseListener);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                APIUtils.errorFailure(mContext, mUniqueID, t, mAPIResponseListener);
            }
        });

        return this;
    }

    public BastaDetail requestGetUserInfo(int uid) {
        mRequestItem = new APIRequestVO<>();
        BastaServices service = (BastaServices) new RequestClient(mContext).getClient(BastaServices.class);
        mRequestItem.setCall(service.requestGetUserInfo(uid));
        mRequestItem.setCallback(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful()) {
                    UserInfoVo vo = (UserInfoVo) APIUtils.toJsonObject(response.body(), UserInfoVo.class);
                    mAPIRequestManager.successResponse(mUniqueID, vo, mAPIResponseListener);
                } else {
                    APIUtils.errorResponse(mContext, mUniqueID, response, mAPIResponseListener);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                APIUtils.errorFailure(mContext, mUniqueID, t, mAPIResponseListener);
            }
        });

        return this;
    }

    public BastaDetail requestGetChangeNick(int uid, String nick) {
        mRequestItem = new APIRequestVO<>();
        BastaServices service = (BastaServices) new RequestClient(mContext).getClient(BastaServices.class);
        mRequestItem.setCall(service.requestGetChangeNick(uid, nick));
        mRequestItem.setCallback(new Callback<Object>() {

            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful()) {
                    UserInfoVo vo = (UserInfoVo) APIUtils.toJsonObject(response.body(), UserInfoVo.class);
                    mAPIRequestManager.successResponse(mUniqueID, vo, mAPIResponseListener);
                } else {
                    APIUtils.errorResponse(mContext, mUniqueID, response, mAPIResponseListener);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                APIUtils.errorFailure(mContext, mUniqueID, t, mAPIResponseListener);
            }
        });

        return this;
    }

    public BastaDetail requestGetLogin(String id, String pwd) {
        mRequestItem = new APIRequestVO<>();
        BastaServices service = (BastaServices) new RequestClient(mContext).getClient(BastaServices.class);
        mRequestItem.setCall(service.requestGetLogin(id, pwd));
        mRequestItem.setCallback(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful()) {
                    LoginVo vo = (LoginVo) APIUtils.toJsonObject(response.body(), LoginVo.class);
                    mAPIRequestManager.successResponse(mUniqueID, vo, mAPIResponseListener);
                } else {
                    APIUtils.errorResponse(mContext, mUniqueID, response, mAPIResponseListener);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                APIUtils.errorFailure(mContext, mUniqueID, t, mAPIResponseListener);
            }
        });

        return this;
    }

    public BastaDetail requestGetJoin(String id, String pwd, String tel) {
        mRequestItem = new APIRequestVO<>();
        BastaServices service = (BastaServices) new RequestClient(mContext).getClient(BastaServices.class);
        mRequestItem.setCall(service.requestGetJoin(id, pwd, tel));
        mRequestItem.setCallback(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful()) {
                    JoinVo vo = (JoinVo) APIUtils.toJsonObject(response.body(), JoinVo.class);
                    mAPIRequestManager.successResponse(mUniqueID, vo, mAPIResponseListener);
                } else {
                    APIUtils.errorResponse(mContext, mUniqueID, response, mAPIResponseListener);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                APIUtils.errorFailure(mContext, mUniqueID, t, mAPIResponseListener);
            }
        });

        return this;
    }

    /**
     * Get member
     * id : 1116818331724950, sns : facebook
     */
    public BastaDetail requestGetLoginSns(String id, String sns) {
        mRequestItem = new APIRequestVO<>();
        BastaServices service = (BastaServices) new RequestClient(mContext).getClient(BastaServices.class);
        mRequestItem.setCall(service.requestGetLoginSns(id, sns));
        mRequestItem.setCallback(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful()) {
                    Dlog.d("" + response.body());
                    LoginVo vo = (LoginVo) APIUtils.toJsonObject(response.body(), LoginVo.class);
                    mAPIRequestManager.successResponse(mUniqueID, vo, mAPIResponseListener);
                } else {
                    APIUtils.errorResponse(mContext, mUniqueID, response, mAPIResponseListener);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                APIUtils.errorFailure(mContext, mUniqueID, t, mAPIResponseListener);
            }
        });

        return this;
    }

    /**
     * Get member
     * id : 1116818331724950, sns : facebook
     */
    public BastaDetail requestGetMatchSns(String id, String sns, String email) {
        mRequestItem = new APIRequestVO<>();
        BastaServices service = (BastaServices) new RequestClient(mContext).getClient(BastaServices.class);
        mRequestItem.setCall(service.requestGetMatchSns(id, sns, email));
        mRequestItem.setCallback(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful()) {
                    Dlog.d("" + response.body());
                    LoginVo vo = (LoginVo) APIUtils.toJsonObject(response.body(), LoginVo.class);
                    mAPIRequestManager.successResponse(mUniqueID, vo, mAPIResponseListener);
                } else {
                    APIUtils.errorResponse(mContext, mUniqueID, response, mAPIResponseListener);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                APIUtils.errorFailure(mContext, mUniqueID, t, mAPIResponseListener);
            }
        });

        return this;
    }

    /**
     * push setting send (POST)
     *
     * @param vo
     * @return
     */

    public BastaDetail requestSetPushInfo(PushSettingVo vo) {
        mRequestItem = new APIRequestVO<>();
        BastaServices service = (BastaServices) new RequestClient(mContext).getClient(BastaServices.class);
        mRequestItem.setCall(service.requestSetPushInfo(vo));
        mRequestItem.setCallback(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful()) {
                    PushSettingVo vo = (PushSettingVo) APIUtils.toJsonObject(response.body(), PushSettingVo.class);
                    mAPIRequestManager.successResponse(mUniqueID, vo, mAPIResponseListener);
                } else {
                    APIUtils.errorResponse(mContext, mUniqueID, response, mAPIResponseListener);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                APIUtils.errorFailure(mContext, mUniqueID, t, mAPIResponseListener);
            }
        });

        return this;
    }


    /**
     * Put member
     */
    public BastaDetail requestPutMember(int id, MemberVO vo) {
        mRequestItem = new APIRequestVO<>();
        BastaServices service = (BastaServices) new RequestClient(mContext).getClient(BastaServices.class);
        mRequestItem.setCall(service.requestPutMember(id, vo));
        mRequestItem.setCallback(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful()) {
                    MemberVO vo = (MemberVO) APIUtils.toJsonObject(response.body(), MemberVO.class);
                    mAPIRequestManager.successResponse(mUniqueID, vo, mAPIResponseListener);
                } else {
                    APIUtils.errorResponse(mContext, mUniqueID, response, mAPIResponseListener);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                APIUtils.errorFailure(mContext, mUniqueID, t, mAPIResponseListener);
            }
        });

        return this;
    }

    /**
     * Delete member
     */
    public BastaDetail requestDeleteMember(int id) {
        mRequestItem = new APIRequestVO<>();
        BastaServices service = (BastaServices) new RequestClient(mContext).getClient(BastaServices.class);
        mRequestItem.setCall(service.requestDeleteMember(id));
        mRequestItem.setCallback(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (response.isSuccessful()) {
                    MemberVO vo = (MemberVO) APIUtils.toJsonObject(response.body(), MemberVO.class);
                    mAPIRequestManager.successResponse(mUniqueID, vo, mAPIResponseListener);
                } else {
                    APIUtils.errorResponse(mContext, mUniqueID, response, mAPIResponseListener);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                APIUtils.errorFailure(mContext, mUniqueID, t, mAPIResponseListener);
            }
        });

        return this;
    }


    /**
     * Set api response listener
     *
     * @param listener APIResponseListener
     */
    public BastaDetail setListener(APIResponseListener listener) {
        mAPIResponseListener = listener;
        return this;
    }

    /**
     * API Request build
     */
    public BastaDetail build() {
        mAPIRequestManager.addRequestCall(mUniqueID, mRequestItem);
        return this;
    }
}
