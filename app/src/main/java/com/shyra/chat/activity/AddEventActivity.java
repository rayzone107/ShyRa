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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shyra.chat.R;
import com.shyra.chat.helper.Constants;
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

    @BindView(R.id.add_event_cancel_button)
    Button mAddEventCancelButton;

    @BindView(R.id.add_event_save_button)
    Button mAddEventSaveButton;

    @BindView(R.id.add_event_image_select_iv)
    CircleImageView mAddEventImageSelectIV;

    @BindView(R.id.add_event_title_et)
    EditText mAddEventTitleET;

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

    private Bitmap mImageBitmap;

    private TimelineEvent mTimelineEvent;

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
        }

        File folder = new File(mFolderPath);
        folder.mkdirs();
        mFile = new File(folder.getPath() + "/tempEventImage.jpg");
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
        Random rnd = new Random();
        final int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        final String[] imageUrl = {""};
        if (mImageBitmap != null) {
            mStorageReference = mFirebaseStorage.getReference().child(Constants.SERVER_STORAGE_HIERARCHY.EVENT)
                    .child(Constants.SERVER_STORAGE_HIERARCHY.EVENT_PATH.IMAGE).child(mAddEventTitleET.getText().toString());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = mStorageReference.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddEventActivity.this, "Unable to save. Please try again.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageUrl[0] = taskSnapshot.getDownloadUrl().toString();
                    mTimelineEvent = new TimelineEvent(mAddEventTitleET.getText().toString(),
                            mAddEventDescriptionET.getText().toString(), imageUrl[0], mAddEventDateET.getText().toString(),
                            color);
                    mFirebaseDatabaseReference.child(Constants.DATABASE_HEADERS.TIMELINE_EVENT).push().setValue(mTimelineEvent);
                    finish();
                }
            });
        } else {
            mTimelineEvent = new TimelineEvent(mAddEventTitleET.getText().toString(),
                    mAddEventDescriptionET.getText().toString(), imageUrl[0], mAddEventDateET.getText().toString(),
                    color);
        }
        mFirebaseDatabaseReference.child(Constants.DATABASE_HEADERS.TIMELINE_EVENT).push().setValue(mTimelineEvent);
        finish();
    }

    @OnClick(R.id.add_event_cancel_button)
    public void onAddEventCancelClick() {
        finish();
    }

    @OnClick(R.id.add_event_image_select_iv)
    public void onAddEventImageSelectClick() {
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

        startActivityForResult(chooserIntent, 10);
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
                mImageBitmap = !isCamera ? bp : Bitmap.createBitmap(bp, 0, 0, bp.getWidth(),
                        bp.getHeight(), rotateMatrix, false);
                mAddEventImageSelectIV.setImageBitmap(mImageBitmap);
                mAddEventImageSelectIV.setPadding(0, 0, 0, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
