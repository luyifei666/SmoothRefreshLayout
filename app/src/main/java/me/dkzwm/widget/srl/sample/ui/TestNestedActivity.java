package me.dkzwm.widget.srl.sample.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import me.dkzwm.widget.srl.MaterialSmoothRefreshLayout;
import me.dkzwm.widget.srl.SmoothRefreshLayout;
import me.dkzwm.widget.srl.extra.IRefreshView;
import me.dkzwm.widget.srl.sample.R;
import me.dkzwm.widget.srl.sample.adapter.RecyclerViewAdapter;
import me.dkzwm.widget.srl.sample.utils.DataUtil;
import me.dkzwm.widget.srl.utils.PixelUtl;
import me.dkzwm.widget.srl.utils.ScrollCompat;

/**
 * Created by dkzwm on 2017/6/1.
 *
 * @author dkzwm
 */
public class TestNestedActivity extends AppCompatActivity {
    int mMinOffset;
    private MaterialSmoothRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private AppBarLayout mAppBarLayout;
    private RecyclerViewAdapter mAdapter;
    private Handler mHandler = new Handler();
    private int mCount = 0;
    private int mOffset = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_nested);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back_white_72x72);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView_test_nested_activity);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new RecyclerViewAdapter(getLayoutInflater());
        mRecyclerView.setAdapter(mAdapter);
        mRefreshLayout = (MaterialSmoothRefreshLayout) findViewById(R.id
                .smoothRefreshLayout_test_nested_activity);
        mRefreshLayout.materialStyle();
        mRefreshLayout.setOnRefreshListener(new SmoothRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefreshBegin(final boolean isRefresh) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isRefresh) {
                            mCount = 0;
                            List<String> list = DataUtil.createList(mCount, 20);
                            mCount += 20;
                            mAdapter.updateData(list);
                        } else {
                            List<String> list = DataUtil.createList(mCount, 20);
                            mCount += 20;
                            mAdapter.appendData(list);
                        }
                        mRefreshLayout.refreshComplete();
                    }
                }, 2000);
            }

            @Override
            public void onRefreshComplete(boolean isSuccessful) {
            }
        });
        mRefreshLayout.getDefaultHeader().setPadding(0, PixelUtl.dp2px(this, 80),
                0, PixelUtl.dp2px(this, 10));
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout_test_nested_activity);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                mOffset = verticalOffset;
                mMinOffset = Math.min(mMinOffset, mOffset);
            }
        });
        mRefreshLayout.setOnChildScrollUpCallback(new SmoothRefreshLayout.OnChildScrollUpCallback() {
            @Override
            public boolean canChildScrollUp(SmoothRefreshLayout parent, @Nullable View child, @Nullable IRefreshView header) {
                return parent.isInStartPosition() && (mOffset != 0 || ScrollCompat.canChildScrollUp(mRecyclerView));
            }
        });
        mRefreshLayout.setOnChildScrollDownCallback(new SmoothRefreshLayout.OnChildScrollDownCallback() {
            @Override
            public boolean canChildScrollDown(SmoothRefreshLayout parent, @Nullable View child, @Nullable IRefreshView footer) {
                return mMinOffset != mOffset || ScrollCompat.canChildScrollDown(mRecyclerView);
            }
        });
        mRefreshLayout.setLoadMoreScrollTargetView(mRecyclerView);
        mRefreshLayout.setOnLoadMoreScrollCallback(new SmoothRefreshLayout.OnLoadMoreScrollCallback() {
            @Override
            public boolean onScroll(View content, float deltaY) {
                return ScrollCompat.scrollCompat(mRecyclerView, deltaY);
            }
        });
        mRefreshLayout.autoRefresh(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TestNestedActivity.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
