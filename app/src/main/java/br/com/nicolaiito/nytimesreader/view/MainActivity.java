package br.com.nicolaiito.nytimesreader.view;

import android.app.SearchManager;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import br.com.nicolaiito.nytimesreader.R;
import br.com.nicolaiito.nytimesreader.databinding.ActivityMainBinding;
import br.com.nicolaiito.nytimesreader.viewmodel.ArticleViewModel;

public class MainActivity extends AppCompatActivity {
    private SearchView mSearchView;
    private LinearLayoutManager mLayoutManager;
    private int mCurrentLastPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setArticleVM(ArticleViewModel.getInstance());

        mLayoutManager = new LinearLayoutManager(this);
        RecyclerView recycler = (RecyclerView) binding.getRoot().findViewById(R.id.article_list);
        recycler.setLayoutManager(mLayoutManager);
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //check for scroll down
                if (dy > 0) {
                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    int firstVisiblePosition = mLayoutManager.findFirstVisibleItemPosition();
                    if ((visibleItemCount + firstVisiblePosition) >= totalItemCount) {
                        mCurrentLastPosition = totalItemCount -1;
                        ArticleViewModel.getInstance().getNextPage();
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArticleViewModel.getInstance().getPopularArticles();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if (mSearchView != null) {
            mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            mSearchView.setIconifiedByDefault(false);
        }

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                ArticleViewModel.getInstance().queryArticles(newText);
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                ArticleViewModel.getInstance().queryArticles(query);
                mSearchView.clearFocus();
                return true;
            }
        };
        mSearchView.setOnQueryTextListener(queryTextListener);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            mSearchView.setQuery(ArticleViewModel.getInstance().getLastKeyWords(), false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ArticleViewModel.ListUpdatedEvent event) {
        if (mLayoutManager.getItemCount() > mCurrentLastPosition) {
            mLayoutManager.scrollToPosition(mCurrentLastPosition);
        }
    }
}
