package com.cp2y.cube.network.api.Impl;

import android.text.TextUtils;
import android.util.Log;

import com.cp2y.cube.helper.ContextHelper;
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
import com.cp2y.cube.network.api.ApiHelper;
import com.cp2y.cube.network.api.LotteryApi;
import com.cp2y.cube.network.provider.LotteryProvider;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.GzipUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by js on 2016/11/29.
 */
public class LotteryApiImpl extends ApiHelper implements LotteryApi {
    @Override
    public Observable<LotteryListModel> lotteryList(String requestType, String version) {
        return getApiService(LotteryProvider.class).lotteryList(getBasicTransBody().put("requestType", requestType).put("version", version));
    }

    @Override
    public Observable<LotteryDrawModel> lotteryDrawList(String lotteryIds,int userID) {
        HttpParam param=getBasicTransBody();
        if(!TextUtils.isEmpty(lotteryIds)){
            param.put("lotteryIds",lotteryIds);
        }
        param.put("userID",userID);
        return getApiService(LotteryProvider.class).lotteryDrawList(param).compose(applySchedulers());
    }

    @Override
    public Observable<LotteryHistoryModel> lotteryHistoryList(int id, int page, int fetchSize,String dat) {
        HttpParam param=getBasicTransBody();
        param.put("lotteryId",id).put("page",page).put("fetchSize",fetchSize);
        if(!TextUtils.isEmpty(dat))param.put("dat",dat);
        return getApiService(LotteryProvider.class).lotteryHistoryList(param).compose(applySchedulers());
    }

    @Override
    public Observable<LotteryOpenHistoryModel> lotteryHistoryOpen(int show, int lotteryID) {
        return getApiService(LotteryProvider.class).lotteryHistoryOpen(getBasicTransBody().put("show",show).put("lotteryID",lotteryID)).compose(applySchedulers());
    }


    @Override
    public Observable<Object> uploadLotteryFile(File file, String lotteryId, String userId, String issue, boolean isGzip) {
        if (isGzip) {//启用GZIP压缩文件
            try {
                File orgFile = file;
                file = new File(file.getParent(), file.getName().concat(".gz"));
                InputStream is = new FileInputStream(orgFile);
                OutputStream os = new FileOutputStream(file);
                GzipUtil.compress(is, os);
                is.close();
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Map<String, RequestBody> data = new HashMap<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        data.put("file\"; filename=\""+ file.getName(), requestBody);
        return getApiService(LotteryProvider.class).uploadLotteryFile(lotteryId, userId, issue, data);
    }

    @Override
    public Observable<Integer> uploadLotteryFilePin(File file, String lotteryId, int userId, String issue,int playType, int noteNumber,boolean isGzip) {
        File orgFile = file;
        Log.e("yangfan1", "uploadLotteryFilePin: "+orgFile.length() );
        if (isGzip) {//启用GZIP压缩文件
            try {
                file = new File(file.getParent(), file.getName().concat(".gz"));
                InputStream is = new FileInputStream(orgFile);
                OutputStream os = new FileOutputStream(file);
                GzipUtil.compress(is, os);
                is.close();
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.e("yangfan2", "uploadLotteryFilePin: "+file.length() );
        Map<String, RequestBody> data = new HashMap<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        data.put("file\"; filename=\""+ file.getName(), requestBody);
        final String filePath = file.getAbsolutePath();
        return getApiService(LotteryProvider.class).uploadLotteryFilePin(data,lotteryId, userId, issue,playType,noteNumber)
                .map(lotteryUploadModel -> {
                    int id = lotteryUploadModel.getMessage();
                    if (id > 0) orgFile.renameTo(new File(orgFile.getParent(), String.valueOf(id)));//重命名当前文件
                    new File(filePath).delete();  //删除压缩文件
                    return id;
                });
    }

    @Override
    public Observable<LotterySummaryModel> lotterySummaryList(int lotteryId, String issueOrder) {
        return getApiService(LotteryProvider.class).lotterySummaryList(getBasicTransBody().put("lotteryId",lotteryId).put("issueOrder",issueOrder)).compose(applySchedulers());
    }


    @Override
    public Observable<String> downloadFile(String downUrl,final boolean isGzip) {
        return getDownloadService(LotteryProvider.class).downloadFile(downUrl).subscribeOn(Schedulers.io())//子线程执行
                .flatMap((responseBody) -> {
                    try {
                        File tmpFile = new File(ContextHelper.getApplication().getCacheDir(), downUrl.hashCode()+".tmp");
                        if (tmpFile.exists()) tmpFile.delete();
                        FileOutputStream os = new FileOutputStream(tmpFile);
                        InputStream is = responseBody.byteStream();
                        if (isGzip) {
                            GzipUtil.decompress(is, os);
                        } else {
                            int count;
                            byte data[] = new byte[1024];
                            while ((count = is.read(data, 0, 1024)) != -1) {
                                os.write(data, 0, count);
                            }
                        }
                        is.close();
                        os.flush();
                        os.close();
                        return Observable.just(tmpFile.getAbsolutePath());//返回文件路径
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return Observable.just("error");
                }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<IssueNumberDataModel> getIssueNumber(int lotteryID, String targets, int show, int week, int mode) {
        HttpParam param = getBasicTransBody().put("lotteryID",lotteryID).put("targets",targets);
        if (show != 0) param.put("show", show);
        if (week != 0) param.put("week", week);
        if (mode != 0) param.put("mode", mode);
        return getApiService(LotteryProvider.class).getIssueNumber(param)
                .map((issueNumberModel) -> {
                    String dataStr = issueNumberModel.getData();
                    IssueNumberDataModel data = null;
                    if (dataStr != null) {
                        data = new Gson().fromJson(dataStr, new TypeToken<IssueNumberDataModel>(){}.getType());
                    }
                    return data;
                });
    }

    @Override
    public Observable<IssueAnalyseDataModel> getIssueAnalyse(int lotteryID, String target, int show, int week) {
        HttpParam param = getBasicTransBody().put("lotteryID",lotteryID).put("target",target);
        if (show != 0) param.put("show", show);
        if (week != 0) param.put("week", week);
        return getApiService(LotteryProvider.class).getIssueAnalyse(param)
                .map(issueAnalyseModel -> {
                    String dataStr = issueAnalyseModel.getData();
                    IssueAnalyseDataModel data = null;
                    if (dataStr != null) {
                        data = new Gson().fromJson(dataStr, new TypeToken<IssueAnalyseDataModel>(){}.getType());
                    }
                    return data;
                });
    }

    @Override
    public Observable<NewNumberModel> getNumberLibrary(int userId, int lotteryId, int issue, int page, int size) {
        if (size == 0) size = 10;
        if (lotteryId == 0) lotteryId = 10002;
        HttpParam param = getBasicTransBody();
        param.put("userId", userId).put("lotteryId", lotteryId).put("page", page).put("size", size);
        if (issue != 0) {
            param.put("issue",issue);
        }
        return getApiService(LotteryProvider.class).getNumberLibrary(param);
    }

    @Override
    public Observable<IssueModel> getNewIssue(String lotteryId) {
        return getApiService(LotteryProvider.class).getNewIssue(getBasicTransBody().put("lotteryId", lotteryId));
    }

    @Override
    public Observable<IssueModel> getNewDrawIssue(String lotteryId) {
        return getApiService(LotteryProvider.class).getNewDrawIssue(getBasicTransBody().put("lotteryId", lotteryId));
    }

    @Override
    public Observable<String> downloadLibrary(int id) {
        return getApiService(LotteryProvider.class).downloadLibrary(id).subscribeOn(Schedulers.io())//子线程执行
                .flatMap((responseBody) -> {
                    try {
                        File tmpFile = new File(ContextHelper.getApplication().getCacheDir(), String.valueOf(id)+".tmp");
                        if (tmpFile.exists()) tmpFile.delete();
                        FileOutputStream os = new FileOutputStream(tmpFile);
                        InputStream is = responseBody.byteStream();
                        GzipUtil.decompress(is, os);
                        is.close();
                        os.flush();
                        os.close();
                        return Observable.just(tmpFile.getAbsolutePath());//返回文件路径
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return Observable.just("error");
                }).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<LotteryMissModel> getCurrentLotteryMiss(String lotteryId) {
        return getApiService(LotteryProvider.class).getCurrentLotteryMiss(lotteryId);
    }

    @Override
    public Observable<DeleteLibraryModel> deleteNumberLibrary(int id, int userId) {
        return getApiService(LotteryProvider.class).deleteNumberLibrary(getBasicTransBody().put("id", id).put("userId",userId));
    }

    @Override
    public Observable<VersionControlModel> versionControl(int type) {
        return getApiService(LotteryProvider.class).versionControl(getBasicTransBody().put("type",type).put("requestType", CommonUtil.getAppChannel().substring(CommonUtil.getAppChannel().indexOf("1")))).compose(applySchedulers());
    }

    @Override
    public Observable<BaiduMapPoiModel> getBaiduMapPoi(double latitude, double longitude) {
        return getApiService(LotteryProvider.class).getBaiduMapPoi(getBasicTransBody().put("location",latitude+","+longitude).put("output","json").put("pois",1).put("ak","LaO6Mbcgm4wijbNyaCG2s6k8jGQ5kYMs").put("mcode","D0:A5:A3:5A:02:8F:FC:80:8E:03:C5:45:5E:B2:9F:BE:71:76:AE:F2;com.cp2y.cube")).compose(applySchedulers());
    }

    @Override
    public Observable<NewHomeNumberModel> getHomeNumberLibrary(int userId,int page, int size) {
        if (size == 0) size = 20;
        HttpParam param = getBasicTransBody();
        param.put("userId", userId).put("page", page).put("size", size);
        return getApiService(LotteryProvider.class).getHomeNumberLibrary(param);
    }

    @Override
    public Observable<LotteryUploadModel> getFeedBack(String content, String mobile, String userAgent, int userId, File file) {
        Map<String, RequestBody> data = new HashMap<>();
        if(file!=null){
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            data.put("file\"; filename=\""+ file.getName(), requestBody);
        }
        data.put("content", RequestBody.create(MediaType.parse("multipart/form-data"), content));
        data.put("mobile", RequestBody.create(MediaType.parse("multipart/form-data"), mobile));
        data.put("userAgent", RequestBody.create(MediaType.parse("multipart/form-data"), userAgent));
        data.put("userId", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(userId)));
        return getApiService(LotteryProvider.class).getFeedBack(data);
    }

    @Override
    public Observable<NumLibraryExitModel> getNumberExist(int userId,int status) {
        return getApiService(LotteryProvider.class).getNumberExist(getBasicTransBody().put("userId",userId).put("status",status)).compose(applySchedulers());
    }

    @Override
    public Observable<IgnoreTrendNumModel> getMissTrend(String lotteryID, int type, int issues) {
        return getApiService(LotteryProvider.class).getMissTrend(getBasicTransBody().put("lotteryID",lotteryID).put("type",type).put("issues",issues)).compose(applySchedulers());
    }

    @Override
    public Observable<IgnoreTrendNumModel> getConditionMissTrend(String lotteryID, int type, int issues, int targetID) {
        return getApiService(LotteryProvider.class).getConditionMissTrend(getBasicTransBody().put("lotteryID",lotteryID).put("type",type).put("issues",issues).put("targetID",targetID)).compose(applySchedulers());
    }

    @Override
    public Observable<BaiduCheckLotteryModel> checkLottery(String fromdevice, String clientip, String detecttype, String languagetype, String imagetype, String image) {
        return getApiService(LotteryProvider.class).checkLottery(getBasicTransBody().put("fromdevice",fromdevice).put("clientip",clientip).put("detecttype",detecttype).put("languagetype",languagetype).put("imagetype",imagetype).put("image",image)).compose(applySchedulers());
    }

    @Override
    public Observable<ScanCashNumModel> scanCashNumber(String lotteryId, String issue, int multiple, boolean additional, String numbers) {
        HttpParam param=getBasicTransBody();
        param.put("lotteryId",lotteryId).put("issue",issue).put("numbers",numbers);
        if(multiple!=1){
            param.put("multiple",multiple);
        }
        if(additional==true){
            param.put("additional",1);
        }
        return getApiService(LotteryProvider.class).scanCashNumber(param).compose(applySchedulers());
    }

    @Override
    public Observable<LotteryUploadModel> scanCashPicture(int status, int userId, File file, int type, String lotteryId, String brand) {
        Map<String, RequestBody> data = new HashMap<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        data.put("file\"; filename=\""+ file.getName(), requestBody);
        data.put("status", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(status)));
        data.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(type)));
        data.put("userId", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(userId)));
        data.put("lotteryId", RequestBody.create(MediaType.parse("multipart/form-data"), lotteryId));
        data.put("brand", RequestBody.create(MediaType.parse("multipart/form-data"), brand));
        return getApiService(LotteryProvider.class).scanCashPicture(data);
    }

    @Override
    public Observable<NewsModel> getNewsList(String cid, int userId, int pageNo, int size,boolean queryRecommend,boolean isPl5) {
        HttpParam param=getBasicTransBody();
        param.put("cid",cid).put("userId",userId).put("pageNo",pageNo).put("size",size).put("isPl5",isPl5);
        if(queryRecommend){
            param.put("queryRecommend","1");
        }
        return getApiService(LotteryProvider.class).getNewsList(param);
    }

    @Override
    public Observable<NewsIndustryModel> getNewsIndustryList(String cid, int userId, int pageNo, int size) {
        return getApiService(LotteryProvider.class).getNewsIndustryList(getBasicTransBody().put("cid",cid).put("userId",userId).put("pageNo",pageNo).put("size",size));
    }

    @Override
    public Observable<NewsDetailModel> getNewsDetailList(String id) {
        return getApiService(LotteryProvider.class).getNewsDetailList(getBasicTransBody().put("id",id));
    }

    @Override
    public Observable<D3LotteryMissModel> getD3CurrentLotteryMiss(String lotteryId) {
        return getApiService(LotteryProvider.class).getD3CurrentLotteryMiss(lotteryId);
    }

    @Override
    public Observable<D3NumberMiss> getNumberMissBy3D(String lotteryId, int issues) {
        return getApiService(LotteryProvider.class).getNumberMissBy3D(lotteryId,issues);
    }

    @Override
    public Observable<UpLoadScanModel> upScanCount(int userID, String lotteryID, String remark) {
        return getApiService(LotteryProvider.class).upScanCount(getBasicTransBody().put("userID",userID).put("lotteryID",lotteryID).put("remark",remark));
    }

    @Override
    public Observable<CustomLotteryModel> CustomLottery(String provinceId) {
        return getApiService(LotteryProvider.class).CustomLottery(getBasicTransBody().put("provinceId",provinceId));
    }

    @Override
    public Observable<CustomLotteryProvinceModel> CustomLotteryProvince() {
        return getApiService(LotteryProvider.class).CustomLotteryProvince(getBasicTransBody());
    }

    @Override
    public Observable<LotteryProvinceModel> getLotteryProvince(int provinceId,int lotteryType) {
        HttpParam param=getBasicTransBody();
        if(provinceId!=0){
            param.put("provinceId",provinceId);
        }
        if(lotteryType!=0){
            param.put("lotteryType",lotteryType);
        }
        return getApiService(LotteryProvider.class).getLotteryProvince(param);
    }

    @Override
    public Observable<BetModel> getByBetting(String provinceId, int wOrs) {
        return getApiService(LotteryProvider.class).getByBetting(getBasicTransBody().put("provinceId",provinceId).put("wOrs",wOrs));
    }

    @Override
    public Observable<RulesModel> getRuleLottery(int lotteryID) {
        return getApiService(LotteryProvider.class).getRuleLottery(getBasicTransBody().put("lotteryID",lotteryID));
    }

    @Override
    public Observable<CustomModel> getFilterHome(int userId,int versionID) {
        return getApiService(LotteryProvider.class).getFilterHome(getBasicTransBody().put("userId",userId).put("versionID",versionID));
    }

    @Override
    public Observable<TrendModel> getTrendHome(int userID, String lotteryIDs,int versionID) {
        HttpParam param=getBasicTransBody();
        param.put("userID",userID).put("versionID",versionID);
        if(!TextUtils.isEmpty(lotteryIDs))param.put("lotteryIDs",lotteryIDs);
        return getApiService(LotteryProvider.class).getTrendHome(param);
    }

    @Override
    public Observable<MainHomeModel> getHomePage(String lotteryIds, int userID) {
        HttpParam param=getBasicTransBody();
        if(!TextUtils.isEmpty(lotteryIds)){
            param.put("lotteryIds",lotteryIds);
        }
        param.put("userID",userID);
        return getApiService(LotteryProvider.class).getHomePage(param).compose(applySchedulers());
    }

    @Override
    public Observable<PushSingleTitleModel> getPushSingleTitle(int userID) {
        return getApiService(LotteryProvider.class).getPushSingleTitle(getBasicTransBody().put("userID",userID));
    }

    @Override
    public Observable<PushSingleListModel> getPushSingleList(int userID,int lotteryID,int page,int size) {
        return getApiService(LotteryProvider.class).getPushSingleList(getBasicTransBody().put("userID",userID).put("lotteryID",lotteryID).put("page",page).put("size",size));
    }

    @Override
    public Observable<AttentionFansListModel> getAttentionFansList(int userID, int subscribeUserId,int type, int pageNum, int pageSize) {
        return getApiService(LotteryProvider.class).getAttentionFansList(getBasicTransBody().put("userId",userID).put("subscribeUserId",subscribeUserId).put("type",type).put("pageNum",pageNum).put("pageSize",pageSize));
    }

    @Override
    public Observable<SelectSingleTitleModel> getSelectSingleTitle(int userID) {
        return getApiService(LotteryProvider.class).getSelectSingleTitle(getBasicTransBody().put("userID",userID));
    }

    @Override
    public Observable<SelectSingleNumModel> getSelectSingleNumber(int userID, int lotteryID) {
        return getApiService(LotteryProvider.class).getSelectSingleNumber(getBasicTransBody().put("userID",userID).put("lotteryID",lotteryID));
    }

    @Override
    public Observable<FlagModel> pushSingle(int userID,String issue,int lotteryID,String numberLibraryID,String shows,String title,int forwardingID,int type) {
        HttpParam param=getBasicTransBody();
            if(!TextUtils.isEmpty(title))param.put("title",title);
            param.put("issue",issue).put("lotteryID",lotteryID).put("type",type).put("userID",userID);
            if(type==0){
                param.put("numberLibraryID",numberLibraryID ).put("shows",shows);//推单
            }else{
                param.put("forwardingID",forwardingID );//推单
            }
        return getApiService(LotteryProvider.class).pushSingle(param);
    }

    @Override
    public Observable<ActiveModel> pushSingleActive(int page, int size) {
        return getApiService(LotteryProvider.class).pushSingleActive(getBasicTransBody().put("page",page).put("size",size));
    }

    @Override
    public Observable<ActiveModel> pushSingleAttention(int userId, int page, int size) {
        return getApiService(LotteryProvider.class).pushSingleAttention(getBasicTransBody().put("userId",userId).put("page",page).put("size",size));
    }

    @Override
    public Observable<PushSingleWarmModel> getPushSingleWarm(int userID) {
        return getApiService(LotteryProvider.class).getPushSingleWarm(getBasicTransBody().put("userID",userID));
    }

    @Override
    public Observable<FlagModel> setPushSingleWarm(int userID) {
        return getApiService(LotteryProvider.class).setPushSingleWarm(getBasicTransBody().put("userID",userID));
    }

    @Override
    public Observable<PushSingleSummaryModel> getPushSingleSummary(int userID, int pushSingleID) {
        return getApiService(LotteryProvider.class).getPushSingleSummary(getBasicTransBody().put("userID",userID).put("pushSingleID",pushSingleID));
    }

    @Override
    public Observable<CommentModel> getCommentList(int pushSingleID, int page, int size) {
        return getApiService(LotteryProvider.class).getCommentList(getBasicTransBody().put("pushSingleID",pushSingleID).put("page",page).put("size",size));
    }

    @Override
    public Observable<CommentModel> putComment(int pushSingleID, int criticsID, int byCriticsID,String content,int type,int commentsID) {
        HttpParam param=getBasicTransBody();
        param.put("pushSingleID",pushSingleID);
        param.put("criticsID",criticsID);
        param.put("content",content);
        param.put("type",type);
        if(type!=0){//相互评论
            param.put("byCriticsID",byCriticsID);
            param.put("commentsID",commentsID);
        }
        return getApiService(LotteryProvider.class).putComment(param);
    }

    @Override
    public Observable<PersonalModel> personalDetail(int userId, int otherUserId) {
        return getApiService(LotteryProvider.class).personalDetail(getBasicTransBody().put("userId",userId).put("otherUserId",otherUserId));
    }

    @Override
    public Observable<AttentionFansListModel> getPersonalAttentionFansList(int userId, int subscribeUserId, int type, int pageNum, int pageSize, int otherUserId) {
        HttpParam param=getBasicTransBody();
        param.put("type",type).put("otherUserId",otherUserId).put("pageNum",pageNum).put("pageSize",pageSize);
        if(type==0){
            param.put("userId",userId);
        }else{
            param.put("subscribeUserId",subscribeUserId);
        }
        return getApiService(LotteryProvider.class).getPersonalAttentionFansList(param);
    }

    @Override
    public Observable<FlagModel> doSubscribeUser(int userId, int subscribeUserId) {
        return getApiService(LotteryProvider.class).doSubscribeUser(getBasicTransBody().put("userId",userId).put("subscribeUserId",subscribeUserId));
    }

    @Override
    public Observable<FlagModel> cancleSubscribeUser(int userId, int subscribeUserId, int id) {
        return getApiService(LotteryProvider.class).cancleSubscribeUser(getBasicTransBody().put("userId",userId).put("subscribeUserId",subscribeUserId).put("id",id));
    }

    @Override
    public Observable<RankModel> rankList(int userId, int type) {
        return getApiService(LotteryProvider.class).rankList(getBasicTransBody().put("userId",userId).put("type",type));
    }

    @Override
    public Observable<PushListModel> pushLotteryList(int userId) {
        return getApiService(LotteryProvider.class).pushLotteryList(getBasicTransBody().put("userId",userId));
    }

    @Override
    public Observable<FlagModel> updateCustomPush(int userId,int platform) {
        return getApiService(LotteryProvider.class).updateCustomPush(getBasicTransBody().put("userId",userId).put("platform",platform));
    }

    @Override
    public Observable<FlagModel> setCustomPush(int userId, int lotteryId, int type) {
        return getApiService(LotteryProvider.class).setCustomPush(getBasicTransBody().put("userId",userId).put("lotteryId",lotteryId).put("type",type));
    }

    @Override
    public Observable<CalcLotteryModel> getCalcLottery(int userId, String lotteryIds) {
        return getApiService(LotteryProvider.class).getCalcLottery(getBasicTransBody().put("userId",userId).put("lotteryIds",lotteryIds));
    }

    @Override
    public Observable<CalcLotteryIssueModel> getCalcLotteryIssue(int lotteryId) {
        return getApiService(LotteryProvider.class).getCalcLotteryIssue(getBasicTransBody().put("lotteryId",lotteryId));
    }

    @Override
    public Observable<ScanCashNumModel> calcLottery(File file, int lotteryId, String issue, int multiple, boolean additional, boolean isGzip) {
        if (isGzip) {//启用GZIP压缩文件
            try {
                File orgFile = file;
                file = new File(file.getParent(), file.getName().concat(".gz"));
                InputStream is = new FileInputStream(orgFile);
                OutputStream os = new FileOutputStream(file);
                GzipUtil.compress(is, os);
                is.close();
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Map<String, RequestBody> data = new HashMap<>();
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        data.put("file\"; filename=\""+ file.getName(), requestBody);
        return getApiService(LotteryProvider.class).calcLottery(data,lotteryId, issue, multiple, additional);
    }
}
