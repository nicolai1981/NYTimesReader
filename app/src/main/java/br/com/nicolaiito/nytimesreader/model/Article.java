package br.com.nicolaiito.nytimesreader.model;

import android.databinding.BaseObservable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Article extends BaseObservable {
    private static final String URL_ROOT = "http://www.nytimes.com/";
    private static final String UNKNOWN_IMAGE = "undefined";
    private static final SimpleDateFormat sDateFormatter = new SimpleDateFormat("MM/dd/yyyy - HH:mm");
    public String id;
    public String titleText;
    public Date publishDate;
    public String abstractText;
    public String imageURL;

    public String getPublishDate() {
        return sDateFormatter.format(publishDate).toUpperCase();
    }

    public static Article getArticle(PopularArticleResponse.ArticleData data) {
        Article article = new Article();
        article.id = String.valueOf(data.mId);
        article.titleText = data.mTitle;
        article.abstractText = data.mAbstract;
        article.publishDate = data.mDate;
        article.imageURL = UNKNOWN_IMAGE;
        for (PopularArticleResponse.MediaData media : data.mListMedia) {
            if ("image".equals(media.mType) && !media.mListMetaData.isEmpty()) {
                int width = 0;
                for (PopularArticleResponse.PhotoMetaData metaData : media.mListMetaData) {
                    if ((width == 0) || (metaData.mWidth > width && !metaData.mFormat.toLowerCase().contains("square"))) {
                        article.imageURL = metaData.mURL;
                        width = metaData.mWidth;
                    }
                }
                break;
            }
        }
        return article;
    }

    public static Article getArticle(SearchArticleResponse.DocData data) {
        Article article = new Article();
        article.id = data.mId;
        article.titleText = data.mHeadLine.mMain;
        article.abstractText = data.mAbstract == null ? data.mLeadParagraph : data.mAbstract;
        article.publishDate = data.mDate;
        article.imageURL = UNKNOWN_IMAGE;
        int width = 0;
        for (SearchArticleResponse.PhotoData media : data.mListPhoto) {
            if ("image".equals(media.mType)) {
                if ((width == 0) || (media.mWidth > width && !media.mSubtype.contains("thumbnail"))) {
                    article.imageURL = media.mURL;
                    width = media.mWidth;
                }
            }
        }
        article.imageURL = URL_ROOT + article.imageURL;
        return article;
    }
}
