package com.shyra.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shyra.chat.R;
import com.shyra.chat.adapter.TimelineAdapter;
import com.shyra.chat.adapter.misc.VerticalSpaceItemDecoration;
import com.shyra.chat.helper.Constants;
import com.shyra.chat.model.TimelineEvent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TimelineActivity extends AppCompatActivity {

    private static final String TAG = TimelineActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.fab_menu)
    FloatingActionMenu mFabMenu;

    @BindView(R.id.timeline_add_event_fab)
    FloatingActionButton mTimelineAddEventFab;

    @BindView(R.id.timeline_upload_media_fab)
    FloatingActionButton mTimlineUploadMediaFab;

    @BindView(R.id.timeline_rv)
    RecyclerView mTimelineRV;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<TimelineEvent, TimelineAdapter.TimelineHolder>
            mFirebaseAdapter;

    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mFirebaseAdapter = new TimelineAdapter(
                TimelineEvent.class,
                R.layout.rv_timeline_row_left,
                TimelineAdapter.TimelineHolder.class,
                mFirebaseDatabaseReference.child(TimelineEvent.TIMELINE_EVENT), new TimelineAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TimelineEvent timelineEvent, View transitionImage) {
                Intent intent = new Intent(TimelineActivity.this, EventDetailActivity.class);
                intent.putExtra(Constants.EXTRA.TIMELINE_EVENT, timelineEvent);
                Pair<View, String> p1 = Pair.create(transitionImage, getString(R.string.transition_event_image));
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(TimelineActivity.this, p1);
                startActivity(intent, options.toBundle());
            }
        });

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                mFirebaseAdapter.notifyDataSetChanged();
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    mTimelineRV.scrollToPosition(positionStart);
                }
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                mFirebaseAdapter.notifyDataSetChanged();
            }
        });

        mLinearLayoutManager = new LinearLayoutManager(this);
        mTimelineRV.setLayoutManager(mLinearLayoutManager);
        mTimelineRV.setAdapter(mFirebaseAdapter);
        mTimelineRV.addItemDecoration(new VerticalSpaceItemDecoration(Constants.DIMENSIONS.TIMELINE_RV_TOP_SPACING));
    }

    @OnClick(R.id.timeline_add_event_fab)
    public void onAddEventClick() {
        Intent intent = new Intent(this, AddEventActivity.class);
        startActivity(intent);
        mFabMenu.close(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_profile:
                startActivity(new Intent(TimelineActivity.this, ProfileActivity.class));
                break;
            case R.id.action_sign_out:
                if (mFirebaseUser != null) {
                    mFirebaseAuth.signOut();
                    LoginManager.getInstance().logOut();
                    startActivity(new Intent(TimelineActivity.this, LoginActivity.class));
                    finish();
                    return true;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mFabMenu.isOpened()) {
            mFabMenu.close(true);
        } else {
            super.onBackPressed();
        }
    }
}
