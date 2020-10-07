package com.heybuddy.ui.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.heybuddy.Controller;
import com.heybuddy.Model.UserData;
import com.heybuddy.R;
import com.heybuddy.base.BaseFragment;
import com.heybuddy.constant.AppConstant;
import com.heybuddy.constant.DbConstant;
import com.heybuddy.constant.SharedPrefConstant;
import com.heybuddy.enumeration.BackStack;
import com.heybuddy.enumeration.FragmentState;
import com.heybuddy.imagepicker.ImagePicker;
import com.heybuddy.ui.Presenter.RegistrationPresenter;
import com.heybuddy.ui.view.RegistrationView;
import com.heybuddy.utility.AppValidation;
import com.heybuddy.utility.DebugLog;
import com.heybuddy.utility.PreferanceHelper;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class RegistrationFragment extends BaseFragment<RegistrationView, RegistrationPresenter> implements RegistrationView {

    Unbinder unbinder;

    @BindView(R.id.imgAddPhoto)
    RoundedImageView imgAddPhoto;
    @BindView(R.id.txtAddPhoto)
    TextView txtAddPhoto;
    @BindView(R.id.edtUsername)
    EditText edtUsername;
    @BindView(R.id.edtEmail)
    EditText edtEmail;
    @BindView(R.id.edtPassword)
    EditText edtPassword;
    @BindView(R.id.btnSignUp)
    Button btnSignUp;

    private FirebaseAuth firebaseAuth;
    private static final int PICK_IMAGE = 1;
    private static final int STORAGE_PERMISSION_CODE = 1;
    private File fileProfileImage;
    private FirebaseStorage firebaseStorage;
    // Access a Cloud Firestore
    private FirebaseFirestore mDatabase;
    private byte[] profileData;

    public static RegistrationFragment newInstance() {
        return new RegistrationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup_fragment, container, false);
        super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        // Get a non-default Storage bucket
        firebaseStorage = FirebaseStorage.getInstance();
        mDatabase = FirebaseFirestore.getInstance();

        edtUsername = view.findViewById(R.id.edtUsername);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPassword = view.findViewById(R.id.edtPassword);
        btnSignUp = view.findViewById(R.id.btnSignUp);
        return view;
    }

    private void registerUser() {

        authActivity.showProgressDialog();
        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                .addOnCompleteListener(baseActivity, task -> {
                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();

                    //checking if success
                    if (task.isSuccessful() && current_user != null) {
                        if (profileData != null) {
                            // if user select profile image then upload profile then upload user details
                            uploadUserImage(current_user.getUid());
                        } else {
                            // if user select not profile image then only upload user details
                            uploadUserDetails(current_user.getUid(), "");
                        }

                    } else {
                        authActivity.hideProgressDialog();
                        authActivity.openAlertDialog(task.getException().getMessage());
                    }

                });
    }

    private void uploadUserImage(String uid) {
        String path = FirebaseAuth.getInstance().getUid() + ".png";
        StorageReference storageReference = firebaseStorage.getReference().child(DbConstant.PHOTO);
        storageReference.child(path).putBytes(profileData)
                .addOnSuccessListener(taskSnapshot -> {
                    Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();
                    downloadUri.addOnCompleteListener(task -> {
                        if (task.getResult() != null) {
                            uploadUserDetails(uid, task.getResult().toString());
                        } else {
                            uploadUserDetails(uid, "");
                        }
                    });
                    downloadUri.addOnFailureListener(e -> uploadUserDetails(uid, ""));
                })
                .addOnFailureListener(e -> {
                    uploadUserDetails(uid, "");
                });
    }

    private void uploadUserDetails(String uid, String imgUrl) {
        String token_id = FirebaseInstanceId.getInstance().getToken();
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put(DbConstant.DEVICE_TOKEN, token_id);
        userMap.put(DbConstant.UID, uid);
        userMap.put(DbConstant.EMAIL_ID, edtEmail.getText().toString());
        userMap.put(DbConstant.ONLINE, "true");
        userMap.put(DbConstant.USERNAME, edtUsername.getText().toString());
        userMap.put(DbConstant.USER_TYPE, AppConstant.NEEDABUDY);
        userMap.put(DbConstant.PROFILE, imgUrl);
        userMap.put(DbConstant.TYPEING_STATUS, "abcd");
        userMap.put(DbConstant.LOGIN_TYPE, AppConstant.LOGIN_NORMAL);

        DocumentReference reference = mDatabase.collection(DbConstant.USERS).document(uid);
        reference.set(userMap)
                .addOnSuccessListener(documentReference -> {
                    reference.get()
                            .addOnSuccessListener(documentSnapshot -> {
                                authActivity.hideProgressDialog();
                                UserData userData = documentSnapshot.toObject(UserData.class);
                                if (userData != null) {
                                    Controller.getInstance().setUpOfflineListener(userData.getUid());
                                    PreferanceHelper.getInstance().putBoolean(SharedPrefConstant.IS_LOGIN, true);
                                    PreferanceHelper.getInstance().setUserDetails(userData);
                                    authActivity.openGetStartFragment(FragmentState.REPLACE, BackStack.YES);
                                }
                            })
                            .addOnFailureListener(e -> {
                                authActivity.hideProgressDialog();
                                authActivity.openAlertDialog(e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    authActivity.hideProgressDialog();
                    authActivity.openAlertDialog(e.getMessage());
                });
    }


    @Override
    public RegistrationPresenter initView() {
        return new RegistrationPresenter(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    private boolean isValid() {
        if (AppValidation.isEmptyFieldValidate(edtUsername.getText().toString().trim())) {
            authActivity.openAlertDialog(getString(R.string.please_enter_username));
            return false;
        } else if (AppValidation.isEmptyFieldValidate(edtEmail.getText().toString().trim())) {
            authActivity.openAlertDialog(getString(R.string.please_enter_email));
            return false;
        } else if (!AppValidation.isEmailValidate(edtEmail.getText().toString().trim())) {
            authActivity.openAlertDialog(getString(R.string.please_enter_valid_email));
            return false;
        } else if (AppValidation.isEmptyFieldValidate(edtPassword.getText().toString().trim())) {
            authActivity.openAlertDialog(getString(R.string.please_enter_password));
            return false;
        } else if (!AppValidation.isPasswordValidate(edtPassword.getText().toString().trim())) {
            authActivity.openAlertDialog(getString(R.string.password_must_be_six_charater));
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            } else {
                showSnackbar(getView(), getString(R.string.to_upload_profile_pic_you_have_to_allow_storage_permission));
            }
        }
    }

    private boolean isReadStorageAllowed() {
        //If permission is granted returning true
        if (ContextCompat.checkSelfPermission(baseActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(baseActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(baseActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, STORAGE_PERMISSION_CODE);
            return false;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            DebugLog.e("Not null");
        } else {
            DebugLog.e("null");
        }
        if (requestCode == PICK_IMAGE) {
            Bitmap bitmap = ImagePicker.getImageFromResult(getActivity(), resultCode, data);
            if (bitmap != null) {
                if (isNetworkAvailable(getView())) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    profileData = stream.toByteArray();
                    Glide.with(baseActivity)
                            .asBitmap()
                            .load(profileData)
                            .apply(RequestOptions.circleCropTransform())
                            .into(imgAddPhoto);

                    txtAddPhoto.setVisibility(View.GONE);
                }

            }
        }
    }

    private void pickImage() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(getActivity());
        startActivityForResult(chooseImageIntent, PICK_IMAGE);

    }

    @OnClick({R.id.imgAddPhoto, R.id.txtAddPhoto, R.id.btnSignUp})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imgAddPhoto:
                if (isReadStorageAllowed())
                    pickImage();
                break;
            case R.id.txtAddPhoto:
                if (isReadStorageAllowed())
                    pickImage();
                break;
            case R.id.btnSignUp:

                HashMap<String, Object> userMap = new HashMap<>();
                userMap.put("device_token", "");
                userMap.put("uid", "");
                userMap.put("email_id", edtEmail.getText().toString());
                userMap.put("status", "online");
                userMap.put("username", edtUsername.getText().toString());
                userMap.put("userType", AppConstant.NEEDABUDY);
                userMap.put("profile", "");

                mDatabase.collection("collo11").document("doc").collection("collo22").add(userMap);

                if (isValid()) {
                    registerUser();
                }
                break;
        }
    }
}
