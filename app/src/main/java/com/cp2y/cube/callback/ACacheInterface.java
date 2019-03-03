package com.cp2y.cube.callback;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by yangfan on 2017/6/28.
 */
public interface ACacheInterface {
    /**资讯推荐和彩种**/
    public interface getNewLists{
        @GET("/news/getNewsList")
        Observable<ResponseBody> getNewList(@Query("cid") String cid,@Query("userId") int userId,@Query("pageNo")int pageNo,@Query("size")int size,@Query("queryRecommend")String queryRecommend);
        @GET("/news/getNewsList")
        Observable<ResponseBody> getNewList(@Query("cid") String cid,@Query("userId") int userId,@Query("pageNo")int pageNo,@Query("size")int size,@Query("isPl5")boolean isPl5);
    }
    /**开奖公告**/
    public interface lotteryDrawList{
        @GET("/draw/getLotteryDrawListCustom")
        Observable<ResponseBody> lotteryDrawList(@QueryMap Map<String, Object> map);
    }
    /**资讯行业动态**/
    public interface getNewsIndustryList{
        @GET("/news/getNewsListByHYDT")
        Observable<ResponseBody> getNewsIndustryList(@Query("cid") String cid, @Query("pin") String pin, @Query("userId") int userId,@Query("pageNo")int pageNo,@Query("size")int size);
    }
}
