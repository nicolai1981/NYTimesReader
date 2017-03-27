package br.com.nicolaiito.nytimesreader.view;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.nicolaiito.nytimesreader.R;
import br.com.nicolaiito.nytimesreader.databinding.ItemArticleBinding;
import br.com.nicolaiito.nytimesreader.model.Article;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ItemArticleBinding mBinder;

        public ViewHolder(View view) {
            super(view);
            mBinder = DataBindingUtil.bind(view);
        }
    }

    private ObservableArrayList<Article> mArticleList;

    public ArticleAdapter(ObservableArrayList<Article> list) {
        mArticleList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Article article = mArticleList.get(position);
        holder.mBinder.setArticle(article);
        holder.mBinder.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mArticleList.size();
    }
}
