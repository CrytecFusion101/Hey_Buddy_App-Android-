package com.heybuddy.ui.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.FragmentActivity;

import com.heybuddy.Controller;
import com.heybuddy.Model.UserData;
import com.heybuddy.R;
import com.heybuddy.activity.AppNavigationActivity;
import com.heybuddy.base.BaseFragment;
import com.heybuddy.constant.AppConstant;
import com.heybuddy.constant.DbConstant;
import com.heybuddy.constant.SharedPrefConstant;
import com.heybuddy.enumeration.BackStack;
import com.heybuddy.enumeration.FragmentState;
import com.heybuddy.ui.Presenter.LoginPresenter;
import com.heybuddy.ui.view.LoginView;
import com.heybuddy.utility.AppValidation;
import com.heybuddy.utility.DebugLog;
import com.heybuddy.utility.PreferanceHelper;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginFragment extends BaseFragment<LoginView, LoginPresenter> implements LoginView, GoogleApiClient.OnConnectionFailedListener,
        FacebookCallback<LoginResult>, View.OnClickListener, GraphRequest.GraphJSONObjectCallback {

    private View view;

    @BindView(R.id.edtEmail)
    EditText edtEmail;
    @BindView(R.id.edtPassword)
    EditText edtPassword;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.imgEmail)
    ImageView imgEmail;
    @BindView(R.id.imgFacebook)
    ImageView imgFacebook;
    @BindView(R.id.txtSignUp)
    TextView txtSignUp;
    @BindView(R.id.imgLogo)
    AppCompatImageView imgLogo;
    @BindView(R.id.linSocialSignUp)
    LinearLayout linSocialSignUp;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabaseReference;

    private Context context;

    private static final int RC_SIGN_IN = 9001;

    //Facebook Declaration
    private CallbackManager facebookCallbackManager;
    private LoginButton fb_login;
    private ProgressDialog progressDialog;
    private String loginType;
    private GoogleSignInClient mGoogleSignInClient = null;
    private GoogleApiClient mGoogleApiClient = null;
    private FirebaseFirestore mDatabase;


    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    @Override
    public LoginPresenter initView() {
        return new LoginPresenter(this);
    }


    public static LoginFragment newInstance(String loginType) {
        LoginFragment fragment = new LoginFragment();
        fragment.setLoginType(loginType);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.login_fragment, container, false);
        ButterKnife.bind(this, view);

        initUI();
        appNavigationActivity = (AppNavigationActivity) context;
        context = getActivity();

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();



       /* FirebaseFirestore.getInstance().collection(DbConstant.USERS).get();
        // asynchronously retrieve all users
        ApiFuture<QuerySnapshot> query = mDatabase.collection("users").get();
        mDatabase.collection("users").get();
// ...
// query.get() blocks on response
        QuerySnapshot querySnapshot = query.get();
        List<DocumentSnapshot> documents = querySnapshot.getDocuments();
        for (DocumentSnapshot document : documents) {
            System.out.println("User: " + document.getUid());
            System.out.println("First: " + document.getString("first"));
            if (document.contains("middle")) {
                System.out.println("Middle: " + document.getString("middle"));
            }
            System.out.println("Last: " + document.getString("last"));
            System.out.println("Born: " + document.getLong("born"));
        }
*/

        fb_login = view.findViewById(R.id.login_button);

        mapping();
        if (loginType.equalsIgnoreCase(AppConstant.BEABUDDY)) {
            txtSignUp.setVisibility(View.INVISIBLE);
            linSocialSignUp.setVisibility(View.INVISIBLE);
        }

        txtSignUp.setOnClickListener(view12 -> authActivity.openRegistrationFragment(FragmentState.REPLACE, BackStack.YES));
        return view;
    }

    private void initUI() {
//        edtEmail.setText("vibhiksha@gmail.com");
//        edtPassword.setText("123456");
    }

    private void mapping() {
        btnLogin = view.findViewById(R.id.btnLogin);
        edtPassword = view.findViewById(R.id.edtPassword);
        edtEmail = view.findViewById(R.id.edtEmail);
        txtSignUp = view.findViewById(R.id.txtSignUp);
        imgFacebook = view.findViewById(R.id.imgFacebook);
        imgEmail = view.findViewById(R.id.imgEmail);

        btnLogin.setOnClickListener(this);
        imgFacebook.setOnClickListener(this);
        imgEmail.setOnClickListener(this);
        imgFacebook.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                if (isValid()) {
                    loginUserAccount();
                }
                break;
            case R.id.imgFacebook:
                initFacebookData();
                break;
            case R.id.imgEmail:
                initGoogleLogin();
                break;

        }
    }

    private boolean isValid() {
        if (AppValidation.isEmptyFieldValidate(edtEmail.getText().toString().trim())) {
            authActivity.openAlertDialog(getString(R.string.please_enter_email));
            return false;
        }
        if (!AppValidation.isEmailValidate(edtEmail.getText().toString().trim())) {
            authActivity.openAlertDialog(getString(R.string.please_enter_valid_email));
            return false;
        }
        if (AppValidation.isEmptyFieldValidate(edtPassword.getText().toString().trim())) {
            authActivity.openAlertDialog(getString(R.string.please_enter_password));
            return false;
        }
        return true;
    }


    private void loginUserAccount() {
        authActivity.showProgressDialog();
        firebaseAuth.signInWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserData userData = new UserData();
                        userData.setUid(FirebaseAuth.getInstance().getUid());
                        userData.setEmail_id(edtEmail.getText().toString());
                        userData.setOnline(AppConstant.ONLINE_TRUE);
                        userData.setDevice_token(FirebaseInstanceId.getInstance().getToken());
                        userData.setUserType(AppConstant.NEEDABUDY);
                        userData.setLoginType(AppConstant.LOGIN_NORMAL);

                        onLoginSuccess(userData);
                    } else {
                        onLoginFailed(task.getException().getMessage());
                    }
                });
    }

    private void loginWithFacebook(AccessToken token) {
        authActivity.showProgressDialog();
        DebugLog.d("handleFacebookAccessToken" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(baseActivity, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        if (user != null) {
                            UserData userData = new UserData();
                            userData.setUid(user.getUid());

                            userData.setUsername(user.getDisplayName());
                            userData.setEmail_id(user.getEmail() != null ? user.getEmail() : "");
                            userData.setOnline(AppConstant.ONLINE_TRUE);
                            userData.setDevice_token(FirebaseInstanceId.getInstance().getToken());
                            userData.setUserType(AppConstant.NEEDABUDY);
                            userData.setProfile(user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "");
                            userData.setLoginType(AppConstant.LOGIN_FACEBOOK);

                            onLoginSuccess(userData);
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        DebugLog.d("signInWithCredential:failure" + task.getException());
                        onLoginFailed(task.getException().getMessage());
                    }
                });
    }

    private void loginWithGoogle(GoogleSignInAccount acct) {
        authActivity.showProgressDialog();
        DebugLog.d("firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(baseActivity, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        if (user != null) {
                            UserData userData = new UserData();
                            userData.setUid(user.getUid());

                            userData.setUsername(user.getDisplayName());
                            userData.setEmail_id(user.getEmail());
                            userData.setOnline(AppConstant.ONLINE_TRUE);
                            userData.setDevice_token(FirebaseInstanceId.getInstance().getToken());
                            userData.setUserType(AppConstant.NEEDABUDY);
                            userData.setProfile(user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "");
                            userData.setLoginType(AppConstant.LOGIN_GOOGLE);

                            onLoginSuccess(userData);
                        }

                    } else {
                        // If sign in fails, display a message to the user.
                        DebugLog.d("signInWithCredential:failure" + task.getException());
                        onLoginFailed(task.getException().getMessage());
                    }
                });
    }


    private void onLoginFailed(String errorMessage) {
        authActivity.hideProgressDialog();
        authActivity.openAlertDialog(errorMessage);
    }


    private void onLoginSuccess(UserData data) {
        String uid = FirebaseAuth.getInstance().getUid();

        if (data.getLoginType().equalsIgnoreCase(AppConstant.LOGIN_NORMAL)) {
            DocumentReference reference = mDatabase.collection(DbConstant.USERS).document(uid);

            reference.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        authActivity.hideProgressDialog();
                        try {
                            Log.d("", "login user : " + documentSnapshot);
                            UserData userData = documentSnapshot.toObject(UserData.class);
                            if (userData != null) {
                                if ((userData.getUserType().equals(AppConstant.NEEDABUDY) && loginType.equalsIgnoreCase(AppConstant.NEEDABUDY))
                                        || (userData.getUserType().equals(AppConstant.BEABUDDY) && loginType.equalsIgnoreCase(AppConstant.BEABUDDY))) {

                                    Map<String, Object> map = new HashMap<>();
                                    map.put(DbConstant.DEVICE_TOKEN, data.getDevice_token());
                                    map.put(DbConstant.ONLINE, data.getOnline());
                                    reference.update(map)
                                            .addOnSuccessListener(aVoid -> {
                                                initLoginSuccess(userData);
                                            })
                                            .addOnFailureListener(e -> {
                                                authActivity.openAlertDialog("Failed to update device toke. Please login again.");
                                            });
                                } else {
                                    authActivity.openAlertDialog("invalid login credential or user type");
                                }
                            } else {
                                authActivity.openAlertDialog("No user details found in database.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            authActivity.openAlertDialog("Parser error");
                        }

                    })
                    .addOnFailureListener(e -> {
                        authActivity.hideProgressDialog();
                        authActivity.openAlertDialog(e.getMessage());
                    });

        } else {
            // social login
            // add user details if not in user table
            uploadUserDetails(data);
        }

    }


    private void uploadUserDetails(UserData data) {
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put(DbConstant.DEVICE_TOKEN, data.getDevice_token());
        userMap.put(DbConstant.UID, data.getUid());
        userMap.put(DbConstant.EMAIL_ID, data.getEmail_id());
        userMap.put(DbConstant.ONLINE, data.getOnline());
        userMap.put(DbConstant.USERNAME, data.getUsername());
        userMap.put(DbConstant.USER_TYPE, data.getUserType());
        userMap.put(DbConstant.PROFILE, data.getProfile());
        userMap.put(DbConstant.LOGIN_TYPE, data.getLoginType());

        DocumentReference reference = mDatabase.collection(DbConstant.USERS).document(data.getUid());
        reference.set(userMap)
                .addOnSuccessListener(documentReference -> {
                    reference.get()
                            .addOnSuccessListener(documentSnapshot -> {
                                authActivity.hideProgressDialog();
                                UserData userData = documentSnapshot.toObject(UserData.class);
                                initLoginSuccess(userData);
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

    public void initLoginSuccess(UserData userData) {
        Controller.getInstance().setUpOfflineListener(userData.getUid());
        PreferanceHelper.getInstance().putBoolean(SharedPrefConstant.IS_LOGIN, true);
        PreferanceHelper.getInstance().setUserDetails(userData);
        authActivity.openHomeActivity();
    }


    private void initFacebookData() {
        facebookCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        fb_login.registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loginWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();


       /* Auth.GoogleSignInApi.signOut(mGoogleSignInClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        //do what you want
                    }
                });*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // facebook logout
        LoginManager.getInstance().logOut();

        // Google logout
        if (mGoogleSignInClient != null)
            mGoogleSignInClient.signOut();

    }

    private void initGoogleLogin() {
        FragmentActivity activity = getActivity();
        if (activity == null) return;

        progressDialog = ProgressDialog.show(context, null, context.getResources().getString(R.string.please_wait), true, false);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
        if (account != null) {
            handleSignInResult(account);
            return;
        }


        /*GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
            mGoogleApiClient = new GoogleApiClient.Builder(baseActivity)
                    .enableAutoManage(baseActivity *//* FragmentActivity *//*, this *//* OnConnectionFailedListener *//*)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                    .build();
        }

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
*/
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions);
        System.out.println("Brij data get signed in");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("onConnectionFailed", "Error Code: " + connectionResult.getErrorCode() + " Error Message: " + connectionResult.getErrorMessage());
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (FacebookSdk.isFacebookRequestCode(requestCode)) {

            if (facebookCallbackManager != null)
                facebookCallbackManager.onActivityResult(requestCode, resultCode, data);

        } else if (requestCode == RC_SIGN_IN) {
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = task.getResult(ApiException.class);
                handleSignInResult(account);

                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                }
                if (mGoogleApiClient != null) {
                    mGoogleApiClient.stopAutoManage(baseActivity);
                    mGoogleApiClient.disconnect();
                }

            } catch (ApiException e) {
                e.printStackTrace();
                progressDialog.dismiss();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void handleSignInResult(GoogleSignInAccount account) {
        progressDialog.dismiss();
        loginWithGoogle(account);
    }


    @Override
    public void onSuccess(LoginResult loginResult) {
        if (AccessToken.getCurrentAccessToken() != null) {
            progressDialog = ProgressDialog.show(context, null, context.getResources().getString(R.string.please_wait), true, false);
            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), this);
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,first_name,last_name,link,email,picture.type(large)");
            request.setParameters(parameters);
            request.executeAsync();
        } else {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    @Override
    public void onCancel() {
        Log.e("onCancel", "onCancel");
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }

    @Override
    public void onError(FacebookException error) {
        Log.e("FacebookException", error.toString());
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onCompleted(JSONObject object, GraphResponse response) {
        try {
            JSONObject json = response.getJSONObject();
            Log.e("Response", json.toString());
            if (json.has("email")) {
                String profilePicUrl = "";
                if (json.has("picture")) {
                    profilePicUrl = json.getJSONObject("picture").getJSONObject("data").getString("url");
                }
//                login(progressDialog, TYPE_FACEBOOK, json.getString("email"), null, json.getString("first_name") + " " + json.getString("last_name"), profilePicUrl, "");
            } else {
//                DialogUtil.showOkDialogBox(context, R.string.unable_to_fetch_your_email_address_from_facebook_please_check_it_might_be_marked_as_private, null);
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }



    /*@Override
     void onStop() {
        super.onStop();
        mAuth.signOut();
        LoginManager.getInstance().logOut();
    }
*/
    /*
   #  change get api to post 4 apis
   #  disable past date in add order, add add project work screen when it's not pendding order
   #  lat long not pass issue in add order and project work screen
   #  getting more time for fatching dashboard data
   #  user can not add last 3 days order
   #  when select rsm in summry and report getting se data issue
   #  when user not selcet rsm then only show se data in summry and report screen

    rsm login summary ya report tema     se selecte
    */
}