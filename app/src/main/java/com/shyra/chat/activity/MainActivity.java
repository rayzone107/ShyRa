package com.shyra.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.clans.fab.FloatingActionButton;
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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String TIMELINE_EVENT_CHILD = "timelineEvent";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.fab)
    FloatingActionButton mFab;

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
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new TimelineAdapter(
                TimelineEvent.class,
                R.layout.rv_timeline_row_left,
                TimelineAdapter.TimelineHolder.class,
                mFirebaseDatabaseReference.child(TIMELINE_EVENT_CHILD)) {

            @Override
            protected void populateViewHolder(TimelineHolder viewHolder, TimelineEvent timelineEvent, int position) {
                viewHolder.bind(getApplicationContext(), timelineEvent, position == getItemCount() - 1);
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    mTimelineRV.scrollToPosition(positionStart);
                }
            }
        });

        mLinearLayoutManager = new LinearLayoutManager(this);
        mTimelineRV.setLayoutManager(mLinearLayoutManager);
        mTimelineRV.setAdapter(mFirebaseAdapter);
        mTimelineRV.addItemDecoration(new VerticalSpaceItemDecoration(Constants.DIMENSIONS.TIMELINE_RV_TOP_SPACING));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_profile:
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                break;
            case R.id.action_sign_out:
                if (mFirebaseUser != null) {
                    mFirebaseAuth.signOut();
                    LoginManager.getInstance().logOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                    return true;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
