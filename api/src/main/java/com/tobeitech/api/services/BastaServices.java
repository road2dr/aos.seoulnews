package com.tobeitech.api.services;

import com.tobeitech.api.config.APIAddress;
import com.tobeitech.api.config.APIParamValue;
import com.tobeitech.api.vo.MemberVO;
import com.tobeitech.api.vo.pushsetting.PushSettingVo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Ted
 */
public interface BastaServices {

    @GET("/api/basta" + "/rank")
    Call<Object> requestGetRank(@Query("rankType") String rankType,
                                @Query("termsType") String termsType,
                                @Query("orderType") String orderType);

    @GET("/api/basta" + "/getFollow")
    Call<Object> requestGetFollowMine(@Query("uid") int uid,
                                      @Query("type") String type);

    @GET("/api/basta" + "/getFollow")
    Call<Object> requestGetScrap(@Query("uid") int uid,
                                 @Query("type") String type);

    @GET("/api/basta" + "/notice")
    Call<Object> requestGetNotice();

    @GET("/api/basta" + "/qna")
    Call<Object> requestGetQna(@Query("uid") int uid);

    @GET("/api/basta" + "/insertQna")
    Call<Object> requestGetInsertQna(@Query("uid") int uid,
                                     @Query("pid") int pid,
                                     @Query("text") String text);

    @GET("/api/basta" + "/alarmReply")
    Call<Object> requestGetAlarmReply(@Query("uid") int uid);

    @GET("/api/basta" + "/users/profile")
    Call<Object> requestGetBastaProfile(@Query("uid") int uid,
                                        @Query("orderType") String orderType);

    @GET("/api/basta" + "/checkFollow")
    Call<Object> requestGetCheckFollow(@Query("uid") int uid,
                                       @Query("followId") int followId,
                                       @Query("type") String type);

    @GET("/api/basta" + "/sendWing")
    Call<Object> requestGetSendWing(@Query("uid") int uid,
                                    @Query("touid") int touid,
                                    @Query("wing") int wing);

    @GET("/api/basta" + "/followBasta")
    Call<Object> requestGetBastaFollow(@Query("uid") int uid,
                                       @Query("followId") int followId);


    @GET("/api/basta" + "/chargeWing")
    Call<Object> requestGetChargeWing(@Query("uid") int uid,
                                      @Query("wing") int wing,
                                      @Query("hilight") int hilight);

    @GET("/api/basta" + "/likeContent")
    Call<Object> requestGetLikeContent(@Query("uid") int uid,
                                       @Query("likeId") int likeId);

    @GET("/api/basta" + "/clipContent")
    Call<Object> requestGetClipContent(@Query("uid") int uid,
                                       @Query("clipId") int clipId);

    @GET("/api/basta" + "/content")
    Call<Object> requestGetContentDetail(@Query("uid") int uid,
                                         @Query("cid") int cid);

    @GET("/api/basta" + "/reply")
    Call<Object> requestGetShowReply(@Query("cid") int cid);

    @GET("/api/basta" + "/deleteReply")
    Call<Object> requestGetDeleteReply(@Query("rid") int rid);

    @GET("/api/basta" + "/insertReply")
    Call<Object> requestGetInsertReply(@Query("cid") int cid,
                                       @Query("uid") int uid,
                                       @Query("pid") int pid,
                                       @Query("text") String text);

    @GET("/api/basta" + "/search")
    Call<Object> requestGetSearch(@Query("type") String type,
                                  @Query("keyword") String keyword);

    @GET("/api/basta" + "/search")
    Call<Object> requestGetSearchContent(@Query("type") String type,
                                         @Query("keyword") String keyword);

    @GET("/api/basta" + "/search")
    Call<Object> requestGetIncreaseQry(@Query("uid") int uid,
                                       @Query("cid") int cid);

    @GET("/api/basta" + "/contentCorp")
    Call<Object> requestGetCorpContent(@Query("cid") int cid);

    @GET("/api/basta" + "/users/info")
    Call<Object> requestGetUserInfo(@Query("uid") int uid);

    @GET("/api/basta" + "/users/nick")
    Call<Object> requestGetChangeNick(@Query("uid") int uid,
                                      @Query("nick") String nick);

    @GET("/api/ssmi" + "/users/login")
    Call<Object> requestGetLogin(@Query("id") String id,
                                 @Query("pwd") String pwd);

    @GET("/api/ssmi" + "/users/join")
    Call<Object> requestGetJoin(@Query("id") String id,
                                @Query("pwd") String pwd,
                                @Query("tel") String tel);

    @GET("/api/ssmi" + "/users/loginSns")
    Call<Object> requestGetLoginSns(@Query("id") String id,
                                    @Query("sns") String pwd);

    @GET("/api/ssmi" + "/users/matchSns")
    Call<Object> requestGetMatchSns(@Query("id") String id,
                                    @Query("sns") String pwd,
                                    @Query("email") String tel);

    @GET("/api/ssmi" + "/board/list")
    Call<Object> requestHkbList(@Query("uid") String uid);

    @GET("/api/ssmi" + "/board/insert")
    Call<Object> requestHkbListItemInsert(@Query("uid") int uid,
                                          @Query("inout") int inout_type,
                                          @Query("category") String category,
                                          @Query("price") int price,
                                          @Query("title") String title,
                                          @Query("memo") String memo,
                                          @Query("date") String date);

    @GET("/api/ssmi" + "/board/update")
    Call<Object> requestHkbListItemUpdate(@Query("idx") int idx,
                                          @Query("uid") int uid,
                                          @Query("inout") int inout_type,
                                          @Query("category") String category,
                                          @Query("price") int price,
                                          @Query("title") String title,
                                          @Query("memo") String memo,
                                          @Query("date") String date);

    @GET("/api/ssmi" + "/board/delete")
    Call<Object> requestHkbListItemDelete(@Query("idx") int idx);

    @GET("/api/ssmi" + "/board/item")
    Call<Object> requestHkbListItemEach(@Query("idx") int idx);

    @POST("/restful/api" + "/eduAdmin/push")
    Call<Object> requestSetPushInfo(@Body PushSettingVo vo);

    @PUT(APIAddress.Member + "/{" + APIParamValue.ID + "}")
    Call<Object> requestPutMember(@Path(APIParamValue.ID) int id,
                                  @Body MemberVO vo);

    @DELETE(APIAddress.Member + "/{" + APIParamValue.ID + "}")
    Call<Object> requestDeleteMember(@Path(APIParamValue.ID) int id);
}

