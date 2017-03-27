package br.com.nicolaiito.nytimesreader.viewmodel;

import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import br.com.nicolaiito.nytimesreader.R;
import br.com.nicolaiito.nytimesreader.model.Article;
import br.com.nicolaiito.nytimesreader.view.ArticleAdapter;

public class ViewBinder {
    @BindingAdapter("bind:items")
    public static void bindList(RecyclerView view, ObservableArrayList<Article> list) {
        view.setAdapter(new ArticleAdapter(list));
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {
        Picasso.with(view.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.nytimeslogo)
                .into(view);
    }
}
