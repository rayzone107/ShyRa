package com.shyra.chat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Adapter for Timeline on MainActivity
 * Created by Rachit Goyal for ShyRa on 10/2/16.
 */

public class TimelineAdapter extends FirebaseRecyclerAdapter<TimelineEvent, TimelineAdapter.TimelineHolder> {

    private static final String TAG = TimelineAdapter.class.getSimpleName();
    private static final int VIEW_TYPE_ODD = 1;
    private static final int VIEW_TYPE_EVEN = 2;

    public TimelineAdapter(Class<TimelineEvent> modelClass, int modelLayout, Class<TimelineHolder> viewHolderClass, DatabaseReference ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }

    @Override
    public TimelineHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_ODD:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_timeline_row_left2, parent, false);
                return new TimelineHolder(view);
            case VIEW_TYPE_EVEN:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_timeline_row_right2, parent, false);
                return new TimelineHolder(view);
        }
        return null;
    }

    @Override
    protected void populateViewHolder(TimelineHolder viewHolder, TimelineEvent model, int position) {

    }

    @Override
    public int getItemViewType(int position) {
        return (position % 2) == 0 ? VIEW_TYPE_EVEN : VIEW_TYPE_ODD;
    }

    public class TimelineHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.timeline_title_tv)
        TextView mTimelineTitleTV;

        @BindView(R.id.timeline_description_tv)
        TextView mTimelineDescriptionTV;

        @BindView(R.id.timeline_image_iv)
        CircleImageView mTimelineImageIV;

        @BindView(R.id.timeline_date_tv)
        TextView mTimelineDateTV;

        @BindView(R.id.timeline_separator_fl)
        TimelineSeparatorView mTimelineSeparatorFL;

        TimelineHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Context context, TimelineEvent timelineEvent, boolean isLast) {
            mTimelineTitleTV.setText(timelineEvent.getTitle());
            mTimelineTitleTV.setTextColor(timelineEvent.getColor());
            mTimelineDescriptionTV.setText(timelineEvent.getDescription());
            Glide.with(context).load(timelineEvent.getImageUrl())
                    .placeholder(R.drawable.timeline_placeholder)
                    .into(mTimelineImageIV);

            mTimelineDateTV.setText(timelineEvent.getDate());
            mTimelineDateTV.setTextColor(timelineEvent.getColor());

            mTimelineSeparatorFL.setTopCircleFillColor(timelineEvent.getColor());
            /*mTimelineImageIV.setBorderColor(timelineEvent.getColor());*/

            if (isLast) {
                mTimelineSeparatorFL.setVisibility(View.INVISIBLE);
            }

            /*viewHolder.mTimelineSeparator1.setBackgroundColor(timelineEvent.getColor());
            viewHolder.mTimelineSeparator2.setBackgroundColor(timelineEvent.getColor());*/
        }
    }
}
