package com.cp2y.cube.network.api;

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
import com.cp2y.cube.model.IssueAnalyseDataModel;
import com.cp2y.cube.model.IssueModel;
import com.cp2y.cube.model.IssueNumberDataModel;
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

import java.io.File;

import rx.Observable;

/**
 * Created by js on 2016/11/29.
 */
public interface LotteryApi {

    /**彩票大厅**/
    Observable<LotteryListModel> lotteryList(String requestType, String version);
    /**开奖公告**/
    Observable<LotteryDrawModel> lotteryDrawList(String lotteryIds,int userID);
    /**开奖历史**/
    Observable<LotteryHistoryModel> lotteryHistoryList(int lotteryId, int page, int fetchSize,String dat);
    /**历史开奖期*/
    Observable<LotteryOpenHistoryModel> lotteryHistoryOpen(int show, int lotteryID);
    /**文件上传**/
    Observable<Object> uploadLotteryFile(File file, String lotteryId, String userId, String issue, boolean isGzip);
    Observable<Integer> uploadLotteryFilePin(File file, String lotteryId, int userId, String issue,int playType,int noteNumber, boolean isGzip);
    /**开奖详情**/
    Observable<LotterySummaryModel> lotterySummaryList(int lotteryId, String issueOrder);
    /**下载文件**/
    Observable<String> downloadFile(String downUrl, boolean isGzip);
    /**获得彩票走势基本数据**/
    Observable<IssueNumberDataModel> getIssueNumber(int lotteryID, String targets, int show, int week, int mode);
    /**获得彩票走势扩展诗句**/
    Observable<IssueAnalyseDataModel> getIssueAnalyse(int lotteryID, String target, int show, int week);
    /**查询号码库**/
    Observable<NewNumberModel> getNumberLibrary(int userId, int lotteryId, int issue, int page, int size);
    /**当前最新奖期**/
    Observable<IssueModel> getNewIssue(String lotteryId);
    /**当前奖期**/
    Observable<IssueModel> getNewDrawIssue(String lotteryId);
    /**下载号码库**/
    Observable<String> downloadLibrary(int id);
    /**获取当前遗漏**/
    Observable<LotteryMissModel> getCurrentLotteryMiss(String lotteryId);
    /**删除号码库**/
    Observable<DeleteLibraryModel> deleteNumberLibrary(int id, int userId);
    /**版本控制**/
    Observable<VersionControlModel> versionControl(int Type);
    /**版本控制**/
    Observable<BaiduMapPoiModel> getBaiduMapPoi(double latitude, double longitude);
    /**主页号码库**/
    Observable<NewHomeNumberModel> getHomeNumberLibrary(int userId,int page, int size);
    /**客服上传**/
    Observable<LotteryUploadModel> getFeedBack(String content, String mobile, String userAgent, int userId, File file);
    /**号码库存在彩种**/
    Observable<NumLibraryExitModel> getNumberExist(int userId,int status);
    /**号码遗漏**/
    Observable<IgnoreTrendNumModel> getMissTrend(String lotteryID, int type,int issues);
    /**号码指标遗漏**/
    Observable<IgnoreTrendNumModel> getConditionMissTrend(String lotteryID, int type,int issues,int targetID);
    /**百度接口**/
    Observable<BaiduCheckLotteryModel> checkLottery(String fromdevice, String clientip, String detecttype, String languagetype, String imagetype, String image);
    /**扫一扫计奖接口**/
    Observable<ScanCashNumModel> scanCashNumber(String lotteryId, String issue, int multiple, boolean additional, String numbers);
    /**扫一扫计奖接口**/
    Observable<LotteryUploadModel> scanCashPicture(int status, int userId, File pic, int  type,String lotteryId,String brand);
    /**资讯接口**/
    Observable<NewsModel> getNewsList(String cid, int userId, int pageNo, int size,boolean queryRecommend,boolean isPl5);
    /**行业动态接口**/
    Observable<NewsIndustryModel> getNewsIndustryList(String cid, int userId, int pageNo, int size);
    /**新闻详情接口**/
    Observable<NewsDetailModel> getNewsDetailList(String id);
    /**3D遗漏接口**/
    Observable<D3LotteryMissModel> getD3CurrentLotteryMiss(String lotteryId);
    /**3D号码遗漏接口**/
    Observable<D3NumberMiss> getNumberMissBy3D(String lotteryId, int issues);
    /**上传扫一扫结果**/
    Observable<UpLoadScanModel> upScanCount(int userID, String lotteryID, String remark);
    /**首次定制省份彩种**/
    Observable<CustomLotteryModel> CustomLottery(String provinceId);
    /**定制获得省份列表**/
    Observable<CustomLotteryProvinceModel> CustomLotteryProvince();
    /**按省份获取彩种**/
    Observable<LotteryProvinceModel> getLotteryProvince(int provinceId,int lotteryType);
    /**获取投注站彩种**/
    Observable<BetModel> getByBetting(String provinceId, int wOrs);
    /**彩种玩法**/
    Observable<RulesModel> getRuleLottery(int lotteryID);
    /**过滤首页接口**/
    Observable<CustomModel> getFilterHome(int userId,int versionID);
    /**走势首页接口**/
    Observable<TrendModel> getTrendHome(int userID, String lotteryIDs,int versionID);
    /**个人中心**/
    Observable<MainHomeModel> getHomePage(String lotteryIds, int userID);
    /**我的推单头部**/
    Observable<PushSingleTitleModel> getPushSingleTitle(int userID);
    /**我的推单内容**/
    Observable<PushSingleListModel> getPushSingleList(int userID,int lotteryID,int page,int size);
    /**我的关注粉丝**/
    Observable<AttentionFansListModel> getAttentionFansList(int userID,int subscribeUserId, int type, int pageNum, int pageSize);
    /**选单头部**/
    Observable<SelectSingleTitleModel> getSelectSingleTitle(int userID);
    /**选单数据**/
    Observable<SelectSingleNumModel> getSelectSingleNumber(int userID, int lotteryID);
    /**发布推单**/
    Observable<FlagModel> pushSingle(int userID,String issue,int lotteryID,String numberLibraryID,String shows,String title,int forwardingID,int type);
    /**动态首页**/
    Observable<ActiveModel> pushSingleActive(int page, int size);
    /**关注首页**/
    Observable<ActiveModel> pushSingleAttention(int userId,int page, int size);
    /**动态首页是否展示心水号**/
    Observable<PushSingleWarmModel> getPushSingleWarm(int userID);
    /**动态首页设置心水号标记**/
    Observable<FlagModel> setPushSingleWarm(int userID);
    /**推单详情**/
    Observable<PushSingleSummaryModel> getPushSingleSummary(int userID, int pushSingleID);
    /**推单详情评论列表**/
    Observable<CommentModel> getCommentList(int pushSingleID, int page, int size);
    /**发表评论**/
    Observable<CommentModel> putComment(int pushSingleID, int criticsID, int byCriticsID,String content,int type,int commentsID);
    /**他人个人主页**/
    Observable<PersonalModel> personalDetail(int userId, int otherUserId);
    /**他人关注粉丝**/
    Observable<AttentionFansListModel> getPersonalAttentionFansList(int userId , int subscribeUserId , int type, int pageNum, int pageSize ,int otherUserId  );
    /**关注**/
    Observable<FlagModel> doSubscribeUser(int userId,int subscribeUserId);
    /**取消关注**/
    Observable<FlagModel> cancleSubscribeUser(int userId,int subscribeUserId,int id);
    /**榜单**/
    Observable<RankModel> rankList(int userId, int type);
    /**推送彩种列表**/
    Observable<PushListModel> pushLotteryList(int userId);
    /**更新推送彩种**/
    Observable<FlagModel> updateCustomPush(int userId,int platform);
    /**设置彩种推送列表**/
    Observable<FlagModel> setCustomPush(int userId,int lotteryId,int type);
    /**计奖器彩种列表**/
    Observable<CalcLotteryModel> getCalcLottery(int userId, String lotteryIds);
    /**计奖器彩种奖期**/
    Observable<CalcLotteryIssueModel> getCalcLotteryIssue(int lotteryId);
    /**计奖器彩种兑奖**/
    Observable<ScanCashNumModel> calcLottery(File file,int lotteryId,String issue,int multiple,boolean additional,boolean isGzip);
}
