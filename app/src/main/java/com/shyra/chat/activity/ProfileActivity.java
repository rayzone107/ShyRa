package com.shyra.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.shyra.chat.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName();

    @BindView(R.id.user_profile_iv)
    CircleImageView mUserProfileIV;

    @BindView(R.id.user_name_tv)
    TextView mUserNameTV;

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
        }

        Glide.with(this).load(mFirebaseUser.getPhotoUrl())
                .placeholder(R.drawable.ic_person_placeholder_black)
                .into(mUserProfileIV);

        mUserNameTV.setText(mFirebaseUser.getDisplayName());
    }
}
