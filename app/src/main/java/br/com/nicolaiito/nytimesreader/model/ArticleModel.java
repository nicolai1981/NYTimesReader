package br.com.nicolaiito.nytimesreader.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.nicolaiito.nytimesreader.viewmodel.ArticleViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class ArticleModel {
    private static final String TAG = "ArticleModel";

    public enum REQ_ERROR {
        NO_INTERNET,
    }

    private static final String BASE_URL = "https://api.nytimes.com/svc/";
    // TODO encrypt the key value
    private static final String PARAM = "57c9e7fe88234b458f7f91ce00bd9062";
    private static final String SORT_PARAM = "newest";
    private static final String FIELD_PARAM = "_id,headline,pub_date,abstract,lead_paragraph,multimedia,document_type";

    private final Retrofit mPopularRetrofit;
    private final Retrofit mSearchRetrofit;
    private PopularArticleCB mPopularCB;
    private Call<PopularArticleResponse> mPopularCall;
    private QueryArticleCB mQueryCB;
    private Call<SearchArticleResponse> mQueryCall;

    public ArticleModel() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .setLenient()
                .create();

        mPopularRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        gson = new GsonBuilder()
               .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
               .setLenient()
               .create();

        mSearchRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public interface NYTApi {
        @GET("mostpopular/v2/mostviewed/all-sections/7.json")
        Call<PopularArticleResponse> popular(@Query("api-key") String apiKey);

        @GET("search/v2/articlesearch.json")
        Call<SearchArticleResponse> search(@Query("api-key") String apiKey,
                                           @Query("sort") String sort,
                                           @Query("fl") String fields,
                                           @Query("q") String keyWords,
                                           @Query("page") int page);
    }

    public void reqPopularArticles() {
        cancelRequests();
        mPopularCB = new PopularArticleCB();
        mPopularCall = mPopularRetrofit.create(NYTApi.class).popular(PARAM);
        mPopularCall.enqueue(mPopularCB);
    }

    private class PopularArticleCB implements Callback<PopularArticleResponse> {
        private boolean mCancelled = false;

        @Override
        public void onResponse(Call<PopularArticleResponse> call, Response<PopularArticleResponse> response) {
            List<Article> list = new ArrayList<Article>();
            if (response.isSuccessful()) {
                Collections.sort(response.body().mListResults, new PopularArticleResponse.ArticleDataComparator());
                int totalItems = 10;
                if (response.body().mTotalResults < 10) {
                    totalItems = response.body().mTotalResults;
                }
                for (int i=0; i<totalItems; i++) {
                    list.add(Article.getArticle(response.body().mListResults.get(i)));
                }
            }
            if (!mCancelled) {
                ArticleViewModel.getInstance().updateArticleList(list, 0);
            }
        }

        @Override
        public void onFailure(Call<PopularArticleResponse> call, Throwable t) {
            Log.e(TAG, "onFailure: <" + t + ">");
            // TODO add case when failed because the free acount limits
            if (!mCancelled) {
                if (t instanceof UnknownHostException) {
                    ArticleViewModel.getInstance().requestFailed(REQ_ERROR.NO_INTERNET);
                }
            }
        }

        public void cancel() {
            mCancelled = true;
        }
    }

    public void reqQueryArticles(String keyWords, int page) {
        cancelRequests();
        mQueryCB = new QueryArticleCB(page);
        mQueryCall = mSearchRetrofit.create(NYTApi.class).search(PARAM, SORT_PARAM, FIELD_PARAM, keyWords, page);
        mQueryCall.enqueue(mQueryCB);
    }

    private class QueryArticleCB implements Callback<SearchArticleResponse> {
        private boolean mCancelled = false;
        private final int mPage;

        public QueryArticleCB(int page) {
            mPage = page;
        }

        @Override
        public void onResponse(Call<SearchArticleResponse> call, Response<SearchArticleResponse> response) {
            List<Article> list = new ArrayList<Article>();
            if (response.isSuccessful()) {
                for (SearchArticleResponse.DocData item : response.body().mResponse.mListDocs) {
                    list.add(Article.getArticle(item));
                }
            }
            if (!mCancelled) {
                ArticleViewModel.getInstance().updateArticleList(list, mPage);
            }
        }

        @Override
        public void onFailure(Call<SearchArticleResponse> call, Throwable t) {
            Log.e(TAG, "onFailure: <" + t + ">");
            // TODO add case when failed because the free acount limits
            if (!mCancelled) {
                if (t instanceof UnknownHostException) {
                    ArticleViewModel.getInstance().requestFailed(REQ_ERROR.NO_INTERNET);
                }
            }
        }

        public void cancel() {
            mCancelled = true;
        }
    }

    public void cancelRequests() {
        if (mPopularCall != null) {
            if (!mPopularCall.isExecuted() || !mPopularCall.isCanceled()) {
                mPopularCall.cancel();
            }
            mPopularCall = null;
        }
        if (mPopularCB != null) {
            mPopularCB.cancel();
            mPopularCB = null;
        }

        if (mQueryCall!= null) {
            if (!mQueryCall.isExecuted() || !mQueryCall.isCanceled()) {
                mQueryCall.cancel();
            }
            mQueryCall = null;
        }
        if (mQueryCB != null) {
            mQueryCB.cancel();
            mQueryCB = null;
        }
    }
}
