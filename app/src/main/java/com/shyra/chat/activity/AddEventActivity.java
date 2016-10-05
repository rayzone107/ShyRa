package com.shyra.chat.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.shyra.chat.R;
import com.shyra.chat.helper.Constants;
import com.shyra.chat.helper.Helper;
import com.shyra.chat.model.TimelineEvent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class AddEventActivity extends AppCompatActivity {

    private static final String TAG = AddEventActivity.class.getSimpleName();

    private static final int REQUEST_CODE_IMAGE_SELECT = 10;
    private static final int REQUEST_CODE_BACKDROP_SELECT = 11;

    @BindView(R.id.add_event_cancel_button)
    Button mAddEventCancelButton;

    @BindView(R.id.add_event_save_button)
    Button mAddEventSaveButton;

    @BindView(R.id.add_event_backdrop_select_iv)
    ImageView mAddEventBackdropSelectIV;

    @BindView(R.id.add_event_backdrop_select_marker_iv)
    ImageView mAddEventBackdropSelectMarkerIV;

    @BindView(R.id.add_event_image_select_iv)
    CircleImageView mAddEventImageSelectIV;

    @BindView(R.id.add_event_title_et)
    EditText mAddEventTitleET;

    @BindView(R.id.add_event_color_picker_iv)
    ImageView mAddEventColorPickerIV;

    @BindView(R.id.add_event_description_et)
    EditText mAddEventDescriptionET;

    @BindView(R.id.add_event_date_et)
    EditText mAddEventDateET;

    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
    DatabaseReference mFirebaseDatabaseReference;
    FirebaseStorage mFirebaseStorage;
    StorageReference mStorageReference;

    Uri mImageUri;
    private String mFolderPath = Constants.LOCAL_STORAGE_PATHS.EVENT_IMAGE_PATH;
    private File mFile;

    private Bitmap mImageBitmap, mBackdropBitmap;

    private ColorPicker mColorPicker;
    boolean mIsColorSet = false;
    int mSelectedColor;

    private String mTimelineEventKey = "0";

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        ButterKnife.bind(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseStorage = FirebaseStorage.getInstance();


        if (mFirebaseUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        File folder = new File(mFolderPath);
        folder.mkdirs();
        mFile = new File(folder.getPath() + "/tempImage.jpg");
        mColorPicker = new ColorPicker(this);
    }

    @OnClick(R.id.add_event_save_button)
    public void onAddEventSaveClick() {
        if (mAddEventTitleET.getText().toString().isEmpty() || mAddEventDateET.getText().toString().isEmpty()
                || mAddEventDescriptionET.getText().toString().isEmpty()) {
            Toast.makeText(this, "Fields cannot be left blank", Toast.LENGTH_SHORT).show();
            mAddEventTitleET.setError("Cannot be blank");
            mAddEventDescriptionET.setError("Cannot be blank");
            mAddEventDateET.setError("Cannot be blank");
            return;
        }
        if (!mIsColorSet) {
            Random rnd = new Random();
            mSelectedColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        }

        mFirebaseDatabaseReference.child(TimelineEvent.TIMELINE_EVENT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TimelineEvent timelineEvent;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    mTimelineEventKey = String.valueOf(postSnapshot.getValue(TimelineEvent.class).getId() + 1);
                }
                mFirebaseDatabaseReference.child(TimelineEvent.TIMELINE_EVENT).child(mTimelineEventKey).push();
                timelineEvent = new TimelineEvent(Integer.valueOf(mTimelineEventKey), mAddEventTitleET.getText().toString(),
                        mAddEventDescriptionET.getText().toString(), "", "", mAddEventDateET.getText().toString(),
                        mSelectedColor);

                mFirebaseDatabaseReference.child(TimelineEvent.TIMELINE_EVENT)
                        .child(mTimelineEventKey).setValue(timelineEvent);
                uploadImage(mImageBitmap, false);
                uploadImage(mBackdropBitmap, true);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        /*.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mTimelineEventKey = dataSnapshot.getChildrenCount() > 0 ? String.valueOf(Integer.valueOf(dataSnapshot.getKey()) + 1) : "0";
                TimelineEvent timelineEvent = new TimelineEvent(mAddEventTitleET.getText().toString(),
                        mAddEventDescriptionET.getText().toString(), "", "", mAddEventDateET.getText().toString(),
                        mSelectedColor);

                mFirebaseDatabaseReference.child(TimelineEvent.TIMELINE_EVENT)
                        .child(String.valueOf(mTimelineEventKey))
                        .setValue(timelineEvent);
                mFirebaseDatabaseReference.child(TimelineEvent.TIMELINE_EVENT)
                        .child(mTimelineEventKey).setValue(timelineEvent);
                uploadImage(mImageBitmap, false);
                uploadImage(mBackdropBitmap, true);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }

    @OnClick(R.id.add_event_cancel_button)
    public void onAddEventCancelClick() {
        finish();
    }

    @OnClick(R.id.add_event_backdrop_select_marker_iv)
    public void onAddEventBackdropSelectMarkerClick() {
        mAddEventBackdropSelectIV.performClick();
    }

    @OnClick(R.id.add_event_color_picker_iv)
    public void onAddEventColorPickerClick() {
        mColorPicker.show();

        Button okColor = (Button) mColorPicker.findViewById(R.id.okColorButton);
        okColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectedColor = mColorPicker.getColor();
                mColorPicker.dismiss();
                mAddEventTitleET.setTextColor(mSelectedColor);
                mAddEventDateET.setTextColor(mSelectedColor);
            }
        });
    }

    @OnClick({R.id.add_event_image_select_iv, R.id.add_event_backdrop_select_iv})
    public void onAddEventImageSelectClick(View v) {
        mImageUri = Uri.fromFile(mFile);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            cameraIntents.add(intent);
        }

        // gallery.
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Chooser of Gallery options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, v.getId() == R.id.add_event_image_select_iv
                ? REQUEST_CODE_IMAGE_SELECT : REQUEST_CODE_BACKDROP_SELECT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            final boolean isCamera;
            if (data == null) {
                isCamera = true;
            } else {
                final String action = data.getAction();
                isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
            }
            if (!isCamera) {
                mImageUri = data.getData();
            }
            try {
                verifyStoragePermissions();
                Bitmap bp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
                Matrix rotateMatrix = new Matrix();
                rotateMatrix.postRotate(90);
                if (requestCode == REQUEST_CODE_IMAGE_SELECT) {
                    mImageBitmap = !isCamera ? bp : Bitmap.createBitmap(bp, 0, 0, bp.getWidth(),
                            bp.getHeight(), rotateMatrix, false);
                    mAddEventImageSelectIV.setImageBitmap(mImageBitmap);
                    mAddEventImageSelectIV.setPadding(0, 0, 0, 0);
                    mAddEventImageSelectIV.setBorderWidth((int) Helper.dpToPixels(getApplicationContext(), 2));
                    mAddEventImageSelectIV.setBorderColor(Color.BLACK);
                } else {
                    mBackdropBitmap = !isCamera ? bp : Bitmap.createBitmap(bp, 0, 0, bp.getWidth(),
                            bp.getHeight(), rotateMatrix, false);
                    mAddEventBackdropSelectIV.setImageBitmap(mBackdropBitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadImage(Bitmap bitmap, final boolean isBackdrop) {
        if (bitmap != null) {
            mStorageReference = mFirebaseStorage.getReference().child(Constants.SERVER_STORAGE_HIERARCHY.EVENT_PATH.EVENT)
                    .child(isBackdrop ? Constants.SERVER_STORAGE_HIERARCHY.EVENT_PATH.IMAGE
                            : Constants.SERVER_STORAGE_HIERARCHY.EVENT_PATH.BACKDROP)
                    .child(mAddEventTitleET.getText().toString());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = mStorageReference.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure: Unable to save " + (isBackdrop ? "image" : "backdrop"));
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String imageUrl = taskSnapshot.getDownloadUrl().toString();
                    mFirebaseDatabaseReference.child(TimelineEvent.TIMELINE_EVENT)
                            .child(mTimelineEventKey)
                            .child(isBackdrop ? TimelineEvent.IMAGE_URL :
                                    TimelineEvent.BACKDROP_URL)
                            .setValue(imageUrl);
                }
            });
        }
    }

    public void verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
