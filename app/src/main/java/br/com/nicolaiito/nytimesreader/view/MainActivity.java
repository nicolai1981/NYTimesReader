package br.com.nicolaiito.nytimesreader.view;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
    private Dialog mDialog;

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
        if (ArticleViewModel.getInstance().getLastKeyWords().isEmpty()) {
            ArticleViewModel.getInstance().getPopularArticles();
        }
    }

    @Override
    protected void onPause() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();


        String lastKeyWords = ArticleViewModel.getInstance().getLastKeyWords();
        if (!lastKeyWords.isEmpty()) {
            MenuItem searchViewItem = menu.findItem(R.id.action_search);
            searchViewItem.expandActionView();
            mSearchView.setQuery(lastKeyWords, false);
            mSearchView.clearFocus();
        }

        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                ArticleViewModel.getInstance().queryArticles(newText);
                return true;
            }

            public boolean onQueryTextSubmit(String query) {
                ArticleViewModel.getInstance().queryArticles(query);
                mSearchView.clearFocus();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            createDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ArticleViewModel.ListUpdatedEvent event) {
        // TODO add a better way to avoid the scroll when the list is updated
        if (mLayoutManager.getItemCount() > mCurrentLastPosition) {
            mLayoutManager.scrollToPosition(mCurrentLastPosition);
        }
    }

    private void createDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_about, null);
        TextView txtMsg = (TextView) dialogView.findViewById(R.id.license_content);
        txtMsg.setText(Html.fromHtml(getResources().getString(R.string.license_text)));
        txtMsg.setMovementMethod(LinkMovementMethod.getInstance());

        dialogView.findViewById(R.id.api_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://developer.nytimes.com"));
                startActivity(intent);
            }
        });

        dialogBuilder.setTitle(R.string.dialog_title_license);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDialog.dismiss();
                mDialog = null;
            }
        });
        mDialog = dialogBuilder.create();
        mDialog.show();
    }
}
