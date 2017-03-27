package br.com.nicolaiito.nytimesreader.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class SearchArticleResponse {
    @SerializedName("status")
    public String mStatus;

    @SerializedName("response")
    public SearchResultData mResponse;

    public static class SearchResultData {
        @SerializedName("docs")
        public List<DocData> mListDocs;
    }

    public static class DocData {
        @SerializedName("_id")
        public String mId;

        @SerializedName("headline")
        public HeadLineData mHeadLine;

        @SerializedName("abstract")
        public String mAbstract;

        @SerializedName("lead_paragraph")
        public String mLeadParagraph;

        @SerializedName("pub_date")
        public Date mDate;

        @SerializedName("document_type")
        public String mDocumentType;

        @SerializedName("multimedia")
        public List<PhotoData> mListPhoto;
    }

    public static class HeadLineData {
        @SerializedName("main")
        public String mMain;
    }

    public static class PhotoData {
        @SerializedName("url")
        public String mURL;

        @SerializedName("type")
        public String mType;

        @SerializedName("subtype")
        public String mSubtype;

        @SerializedName("height")
        public int mHeight;

        @SerializedName("width")
        public int mWidth;
    }
}
