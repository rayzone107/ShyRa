package com.shyra.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shyra.chat.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName();

    @BindView(R.id.profile_backdrop_iv)
    ImageView mUserProfileIV;

    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mUserNameTV;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        Glide.with(this).load(mFirebaseUser.getPhotoUrl())
                .placeholder(R.drawable.ic_person_placeholder_black)
                .into(mUserProfileIV);

        mUserNameTV.setTitle(mFirebaseUser.getDisplayName());
    }
}
