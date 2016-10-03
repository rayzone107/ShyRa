package com.shyra.chat.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shyra.chat.R;
import com.shyra.chat.helper.Constants;
import com.shyra.chat.model.TimelineEvent;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventDetailActivity extends AppCompatActivity {

    private static final String TAG = EventDetailActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;

    @BindView(R.id.event_detail_backdrop_iv)
    ImageView mEventDetailBackdropIV;

    @BindView(R.id.event_detail_title_tv)
    TextView mEventDetailTitleTV;

    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;

    TimelineEvent mTimelineEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        ButterKnife.bind(this);
        mTimelineEvent = getIntent().getParcelableExtra(Constants.EXTRA.TIMELINE_EVENT);

//        mToolbarLayout.setTitle(mTimelineEvent.getTitle());

        Glide.with(this).load(mTimelineEvent.getImageUrl())
                .placeholder(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))).into(mEventDetailBackdropIV);

        mEventDetailTitleTV.setText(mTimelineEvent.getTitle());
        mEventDetailTitleTV.setTextColor(mTimelineEvent.getColor());

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ActivityCompat.finishAfterTransition(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
