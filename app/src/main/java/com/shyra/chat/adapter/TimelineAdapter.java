package com.shyra.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.shyra.chat.R;
import com.shyra.chat.model.TimelineEvent;
import com.shyra.chat.widgets.TimelineSeparatorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Adapter for Timeline on TimelineActivity
 * Created by Rachit Goyal for ShyRa on 10/2/16.
 */

public class TimelineAdapter extends FirebaseRecyclerAdapter<TimelineEvent, TimelineAdapter.TimelineHolder> {

    private static final String TAG = TimelineAdapter.class.getSimpleName();
    private static final int VIEW_TYPE_ODD = 1;
    private static final int VIEW_TYPE_EVEN = 2;

    private OnItemClickListener mOnItemClickListener;

    public TimelineAdapter(Class<TimelineEvent> modelClass, int modelLayout, Class<TimelineHolder> viewHolderClass,
                           DatabaseReference ref, OnItemClickListener listener) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        mOnItemClickListener = listener;
    }

    @Override
    public TimelineHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_ODD:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_timeline_row_left, parent, false);
                return new TimelineHolder(view);
            case VIEW_TYPE_EVEN:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_timeline_row_right, parent, false);
                return new TimelineHolder(view);
        }
        return null;
    }

    @Override
    protected void populateViewHolder(TimelineHolder viewHolder, TimelineEvent timelineEvent, int position) {
        viewHolder.bind(getApplicationContext(), timelineEvent, position == getItemCount() - 1, mOnItemClickListener);
    }

    @Override
    public int getItemViewType(int position) {
        return (position % 2) == 0 ? VIEW_TYPE_EVEN : VIEW_TYPE_ODD;
    }

    public class TimelineHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.timeline_event_container)
        LinearLayout mTimelineEventContainer;

        @BindView(R.id.timeline_title_tv)
        TextView mTimelineTitleTV;

        @BindView(R.id.timeline_description_tv)
        TextView mTimelineDescriptionTV;

        @BindView(R.id.timeline_image_iv)
        CircleImageView mTimelineImageIV;

        @BindView(R.id.timeline_date_tv)
        TextView mTimelineDateTV;

        @BindView(R.id.timeline_separator)
        TimelineSeparatorView mTimelineSeparator;

        TimelineHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Context context, final TimelineEvent timelineEvent, boolean isLast, final OnItemClickListener listener) {
            mTimelineTitleTV.setText(timelineEvent.getTitle());
            mTimelineTitleTV.setTextColor(timelineEvent.getColor());
            mTimelineDescriptionTV.setText(timelineEvent.getDescription());
            Glide.with(context).load(timelineEvent.getImageUrl())
                    .placeholder(R.drawable.timeline_placeholder)
                    .into(mTimelineImageIV);

            mTimelineDateTV.setText(timelineEvent.getDate());
            mTimelineDateTV.setTextColor(timelineEvent.getColor());

            mTimelineSeparator.setTopCircleFillColor(timelineEvent.getColor());
            /*mTimelineImageIV.setBorderColor(timelineEvent.getColor());*/

            if (isLast) {
                mTimelineSeparator.setVisibility(View.INVISIBLE);
            }

            mTimelineEventContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(timelineEvent, mTimelineImageIV);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(TimelineEvent timelineEvent, View mTimelineImageView);
    }
}
