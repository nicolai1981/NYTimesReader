package br.com.nicolaiito.nytimesreader.viewmodel;

import android.databinding.ObservableArrayList;

import org.greenrobot.eventbus.EventBus;

import java.util.LinkedHashMap;
import java.util.List;

import br.com.nicolaiito.nytimesreader.model.Article;
import br.com.nicolaiito.nytimesreader.model.ArticleModel;

public class ArticleViewModel {
    private static ArticleViewModel sInstance;
    private final ArticleModel mArticleModel;
    private LinkedHashMap<String, Article> mArticleStore;
    private ObservableArrayList<Article> mArticleList;
    private String mKeyWords = "";
    private int mLastSearchPage = 0;
    private int mSearchPage = 0;

    private ArticleViewModel() {
        mArticleModel = new ArticleModel();
        mArticleList = new ObservableArrayList<>();
        mArticleStore = new LinkedHashMap<String, Article>();
    }

    public static final ArticleViewModel getInstance() {
        if (sInstance == null) {
            sInstance = new ArticleViewModel();
        }
        return sInstance;
    }

    public ObservableArrayList<Article> getArticleList() {
        return mArticleList;
    }

    public void getPopularArticles() {
        mArticleStore.clear();
        mArticleModel.reqPopularArticles();
    }

    public void updateArticleList(List<Article> list, int page) {
        // Avoid duplicated items on app list
        for (Article item : list) {
            mArticleStore.put(item.id, item);
        }
        mArticleList.clear();
        mArticleList.addAll(mArticleStore.values());
        mLastSearchPage = page;
        if (page > 0) {
            EventBus.getDefault().post(new ListUpdatedEvent());
        }
    }

    public static final class ListUpdatedEvent{} ;

    public void queryArticles(String keyWords) {
        if (!mKeyWords.equals(keyWords)) {
            mSearchPage = 0;
            mLastSearchPage = 0;
            mArticleStore.clear();

            if ((keyWords == null) || keyWords.isEmpty()) {
                mKeyWords = "";
                getPopularArticles();
            } else {
                mKeyWords = keyWords;
                mArticleModel.reqQueryArticles(mKeyWords, 0);
            }
        }
    }

    public void getNextPage() {
        if (!mKeyWords.isEmpty() && (mLastSearchPage == mSearchPage)) {
            mSearchPage++;
            mArticleModel.reqQueryArticles(mKeyWords, mSearchPage);
        }
    }

    public String getLastKeyWords() {
        return mKeyWords;
    }
}
