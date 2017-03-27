package br.com.nicolaiito.nytimesreader.model;

import com.google.gson.annotations.SerializedName;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class PopularArticleResponse {
    @SerializedName("status")
    public String mStatus;

    @SerializedName("num_results")
    public int mTotalResults;

    @SerializedName("results")
    public List<ArticleData> mListResults;

    public static class ArticleDataComparator implements Comparator<ArticleData> {
        public int compare(ArticleData left, ArticleData right) {
            return left.mViews == right.mViews ? 0 : left.mViews < right.mViews ? 1 : -1;
        }
    }

    public static class ArticleData {
        @SerializedName("id")
        public long mId;

        @SerializedName("views")
        public int mViews;

        @SerializedName("url")
        public String mURL;

        @SerializedName("title")
        public String mTitle;

        @SerializedName("abstract")
        public String mAbstract;

        @SerializedName("published_date")
        public Date mDate;

        @SerializedName("media")
        public List<MediaData> mListMedia;
    }

    public static class MediaData {
        @SerializedName("type")
        public String mType;

        @SerializedName("media-metadata")
        public List<PhotoMetaData> mListMetaData;
    }

    public static class PhotoMetaData {
        @SerializedName("url")
        public String mURL;

        @SerializedName("format")
        public String mFormat;

        @SerializedName("height")
        public int mHeight;

        @SerializedName("width")
        public int mWidth;
    }
}
