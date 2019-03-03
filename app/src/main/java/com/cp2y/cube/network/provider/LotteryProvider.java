package com.cp2y.cube.network.provider;

import com.cp2y.cube.model.ActiveModel;
import com.cp2y.cube.model.AttentionFansListModel;
import com.cp2y.cube.model.BaiduCheckLotteryModel;
import com.cp2y.cube.model.BaiduMapPoiModel;
import com.cp2y.cube.model.BetModel;
import com.cp2y.cube.model.CalcLotteryIssueModel;
import com.cp2y.cube.model.CalcLotteryModel;
import com.cp2y.cube.model.CommentModel;
import com.cp2y.cube.model.CustomLotteryModel;
import com.cp2y.cube.model.CustomLotteryProvinceModel;
import com.cp2y.cube.model.CustomModel;
import com.cp2y.cube.model.D3LotteryMissModel;
import com.cp2y.cube.model.D3NumberMiss;
import com.cp2y.cube.model.DeleteLibraryModel;
import com.cp2y.cube.model.FlagModel;
import com.cp2y.cube.model.IgnoreTrendNumModel;
import com.cp2y.cube.model.IssueAnalyseModel;
import com.cp2y.cube.model.IssueModel;
import com.cp2y.cube.model.IssueNumberModel;
import com.cp2y.cube.model.LotteryDrawModel;
import com.cp2y.cube.model.LotteryHistoryModel;
import com.cp2y.cube.model.LotteryListModel;
import com.cp2y.cube.model.LotteryMissModel;
import com.cp2y.cube.model.LotteryOpenHistoryModel;
import com.cp2y.cube.model.LotteryProvinceModel;
import com.cp2y.cube.model.LotterySummaryModel;
import com.cp2y.cube.model.LotteryUploadModel;
import com.cp2y.cube.model.MainHomeModel;
import com.cp2y.cube.model.NewHomeNumberModel;
import com.cp2y.cube.model.NewNumberModel;
import com.cp2y.cube.model.NewsDetailModel;
import com.cp2y.cube.model.NewsIndustryModel;
import com.cp2y.cube.model.NewsModel;
import com.cp2y.cube.model.NumLibraryExitModel;
import com.cp2y.cube.model.PersonalModel;
import com.cp2y.cube.model.PushListModel;
import com.cp2y.cube.model.PushSingleListModel;
import com.cp2y.cube.model.PushSingleSummaryModel;
import com.cp2y.cube.model.PushSingleTitleModel;
import com.cp2y.cube.model.PushSingleWarmModel;
import com.cp2y.cube.model.RankModel;
import com.cp2y.cube.model.RulesModel;
import com.cp2y.cube.model.ScanCashNumModel;
import com.cp2y.cube.model.SelectSingleNumModel;
import com.cp2y.cube.model.SelectSingleTitleModel;
import com.cp2y.cube.model.TrendModel;
import com.cp2y.cube.model.UpLoadScanModel;
import com.cp2y.cube.model.VersionControlModel;
import com.cp2y.cube.network.NetConst;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by js on 2016/11/29.
 */
public interface LotteryProvider {

    @GET("/lottery/lottery_list")
    Observable<LotteryListModel> lotteryList(@QueryMap Map<String, Object> map);
    @GET("/draw/getLotteryDrawListCustom")
    Observable<LotteryDrawModel> lotteryDrawList(@QueryMap Map<String, Object> map);
    @GET("/draw/getHisDrawCustom")
    Observable<LotteryHistoryModel> lotteryHistoryList(@QueryMap Map<String, Object> map);
    @GET("/draw/getHisDrawNumCustom")
    Observable<LotteryOpenHistoryModel> lotteryHistoryOpen(@QueryMap Map<String, Object> map);
    @Multipart
    @POST("/saveNumberLibrary.do")
    Observable<Object> uploadLotteryFile(@Query("lotteryId") String lotteryId, @Query("userId") String userId, @Query("issue") String issue, @PartMap Map<String, RequestBody> params);
    @Multipart
    @POST("/numberLibrary/saveNumberLibrary")
    Observable<LotteryUploadModel> uploadLotteryFilePin(@PartMap Map<String, RequestBody> params, @Query("lotteryId") String lotteryId, @Query("userId") int userId, @Query("issue") String issue,@Query("playType")int playType,@Query("noteNumber")int noteNumber);
    @GET("/draw/drawLotteryDetailCustom")
    Observable<LotterySummaryModel> lotterySummaryList(@QueryMap Map<String, Object> map);
    @GET
    Observable<ResponseBody> downloadFile(@Url String downUrl);
    @GET(NetConst.DOWN_NUMBER_URL)
    Observable<ResponseBody> downloadLibrary(@Query("id") int id);
    @GET("/analyse/issueNumber")
    Observable<IssueNumberModel> getIssueNumber(@QueryMap Map<String, Object> map);
    @GET("/analyse/issueAnalyse")
    Observable<IssueAnalyseModel> getIssueAnalyse(@QueryMap Map<String, Object> map);
    @FormUrlEncoded
    @POST("/numberLibrary/queryNumberLibraryMerge")
    Observable<NewNumberModel> getNumberLibrary(@FieldMap Map<String, Object> map);
    @GET("/draw/getNewIssue")
    Observable<IssueModel> getNewIssue(@QueryMap Map<String, Object> map);
    @GET("/draw/getNewDrawIssueCustom")
    Observable<IssueModel> getNewDrawIssue(@QueryMap Map<String, Object> map);
    @GET("/miss/currentLotteryMiss")
    Observable<LotteryMissModel> getCurrentLotteryMiss(@Query("lotteryId") String lotteryId);
    @GET("/numberLibrary/deleteNumberLibrary")
    Observable<DeleteLibraryModel> deleteNumberLibrary(@QueryMap Map<String, Object> map);
    @GET("/user/client/isNewClientVersion")
    Observable<VersionControlModel> versionControl(@QueryMap Map<String, Object> map);
    @GET("http://api.map.baidu.com/geocoder/v2/")
    Observable<BaiduMapPoiModel> getBaiduMapPoi(@QueryMap Map<String, Object> map);
    @GET("/numberLibrary/queryHomeNumberLibraryMerge")
    Observable<NewHomeNumberModel> getHomeNumberLibrary(@QueryMap Map<String, Object> map);
    @Multipart
    @POST("/leave/saveLeaveWord")
    Observable<LotteryUploadModel> getFeedBack(@PartMap Map<String, RequestBody> params);
    @GET("/numberLibrary/getNumberExist")
    Observable<NumLibraryExitModel> getNumberExist(@QueryMap Map<String, Object> map);
    @GET("/miss/missNumber")
    Observable<IgnoreTrendNumModel> getMissTrend(@QueryMap Map<String, Object> map);
    @GET("/miss/missTarget")
    Observable<IgnoreTrendNumModel> getConditionMissTrend(@QueryMap Map<String, Object> map);
    /*百度计奖接口*/
    @POST("http://apis.baidu.com/idl_baidu/baiduocrpay/idlocrpaid")
    @Headers("apikey: 8e0bd5a3f458bdd7daf50d7cf2045d11")
    @FormUrlEncoded
    Observable<BaiduCheckLotteryModel> checkLottery(@FieldMap Map<String, Object> map);
    @GET("/numberLibrary/cashNumber")
    Observable<ScanCashNumModel> scanCashNumber(@QueryMap Map<String, Object> map);
    @Multipart
    @POST("lotteryPicture/savePicture")
    Observable<LotteryUploadModel> scanCashPicture(@PartMap Map<String, RequestBody> params);
    @GET("/news/getNewsList")
    Observable<NewsModel> getNewsList(@QueryMap Map<String,Object> map);
    @GET("/news/getNewsListByHYDT")
    Observable<NewsIndustryModel> getNewsIndustryList(@QueryMap Map<String,Object> map);
    @GET("/news/getNewsDetails")
    Observable<NewsDetailModel> getNewsDetailList(@QueryMap Map<String,Object> map);
    @GET("/miss/currentLotteryMiss")
    Observable<D3LotteryMissModel> getD3CurrentLotteryMiss(@Query("lotteryId") String lotteryId);
    @GET("/miss/getNumberMissBy3D")
    Observable<D3NumberMiss> getNumberMissBy3D(@Query("lotteryID") String lotteryId, @Query("issues") int issues);
    @GET("/lotteryPicture/isSuccess")
    Observable<UpLoadScanModel> upScanCount(@QueryMap Map<String,Object> map);
    @GET("/area/getHome")
    Observable<CustomLotteryModel> CustomLottery(@QueryMap Map<String,Object> map);
    @GET("/area/getProvince")
    Observable<CustomLotteryProvinceModel> CustomLotteryProvince(@QueryMap Map<String,Object> map);
    @GET("/area/getAreaLottery")
    Observable<LotteryProvinceModel> getLotteryProvince(@QueryMap Map<String,Object> map);
    @GET("/area/getByBetting")
    Observable<BetModel> getByBetting(@QueryMap Map<String,Object> map);
    @GET("/draw/getRuleLottery")
    Observable<RulesModel> getRuleLottery(@QueryMap Map<String,Object> map);
    @GET("/custom/getFilterHome")
    Observable<CustomModel> getFilterHome(@QueryMap Map<String,Object> map);
    @GET("/custom/getTrendHome")
    Observable<TrendModel> getTrendHome(@QueryMap Map<String,Object> map);
    @GET("/homePage/index")
    Observable<MainHomeModel> getHomePage(@QueryMap Map<String,Object> map);
    @GET("/pushSingle/myPushSingleTitle")
    Observable<PushSingleTitleModel> getPushSingleTitle(@QueryMap Map<String,Object> map);
    @GET("/pushSingle/myPushSingleList")
    Observable<PushSingleListModel> getPushSingleList(@QueryMap Map<String,Object> map);
    @GET("/analyseUser/lookSubscribeUserList")
    Observable<AttentionFansListModel> getAttentionFansList(@QueryMap Map<String,Object> map);
    @GET("/numberLibrary/findPushSingleLottery")
    Observable<SelectSingleTitleModel> getSelectSingleTitle(@QueryMap Map<String,Object> map);
    @GET("/numberLibrary/findPushSingleNumber")
    Observable<SelectSingleNumModel> getSelectSingleNumber(@QueryMap Map<String,Object> map);
    @GET("/pushSingle/savePushSingle")
    Observable<FlagModel> pushSingle(@QueryMap Map<String,Object> map);
    @GET("/pushSingle/pushSingleIndex")
    Observable<ActiveModel> pushSingleActive(@QueryMap Map<String,Object> map);
    @GET("/pushSingle/pushSingleAttention")
    Observable<ActiveModel> pushSingleAttention(@QueryMap Map<String,Object> map);
    @GET("/pushSingle/getPushSingleWarm")
    Observable<PushSingleWarmModel> getPushSingleWarm(@QueryMap Map<String,Object> map);
    @GET("/pushSingle/setPushSingleWarm")
    Observable<FlagModel> setPushSingleWarm(@QueryMap Map<String,Object> map);
    @GET("/pushSingle/PushSingleDetails")
    Observable<PushSingleSummaryModel> getPushSingleSummary(@QueryMap Map<String,Object> map);
    @GET("/comments/commentsList")
    Observable<CommentModel> getCommentList(@QueryMap Map<String,Object> map);
    @GET("/comments/save")
    Observable<CommentModel> putComment(@QueryMap Map<String,Object> map);
    @GET("/homePage/otherIndex")
    Observable<PersonalModel> personalDetail(@QueryMap Map<String,Object> map);
    @GET("/analyseUser/lookSubscribeUserListByOtherUserId")
    Observable<AttentionFansListModel> getPersonalAttentionFansList(@QueryMap Map<String,Object> map);
    @GET("/analyseUser/doSubscribeUser")
    Observable<FlagModel> doSubscribeUser(@QueryMap Map<String,Object> map);
    @GET("/analyseUser/cancleSubscribeUser")
    Observable<FlagModel> cancleSubscribeUser(@QueryMap Map<String,Object> map);
    @GET("/analyseUser/rankList")
    Observable<RankModel> rankList(@QueryMap Map<String,Object> map);
    @GET("/sendInfo/queryCustomByUserId")
    Observable<PushListModel> pushLotteryList(@QueryMap Map<String,Object> map);
    @GET("/sendInfo/saveCustomByUserId")
    Observable<FlagModel> updateCustomPush(@QueryMap Map<String,Object> map);
    @GET("/sendInfo/changeCustomByUserIdAndLotteryId")
    Observable<FlagModel> setCustomPush(@QueryMap Map<String,Object> map);
    @GET("/numberLibrary/redeemLottery")
    Observable<CalcLotteryModel> getCalcLottery(@QueryMap Map<String,Object> map);
    @GET("/numberLibrary/hisDrawIssue")
    Observable<CalcLotteryIssueModel> getCalcLotteryIssue(@QueryMap Map<String,Object> map);
    @Multipart
    @POST("/numberLibrary/cashNumberByHand")
    Observable<ScanCashNumModel> calcLottery(@PartMap Map<String, RequestBody> params, @Query("lotteryId") int lotteryId, @Query("issue") String issue, @Query("multiple") int multiple,@Query("additional")boolean additional);
}
