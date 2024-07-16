package com.fisher.myloginlibrary.arrbuild1.login;

//import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;


import com.fisher.myloginlibrary.arrbuild1.BaseSdk.BaseSdk;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import androidx.annotation.NonNull;
import androidx.credentials.ClearCredentialStateRequest;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.PasswordCredential;
import androidx.credentials.PublicKeyCredential;
import androidx.credentials.exceptions.ClearCredentialException;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.GetCredentialCancellationException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginLibrary_google {

    static Activity mActivity = null;

    static Context mContext = null;


    private static CredentialManager credentialManager;
    private static boolean oneTapStatus = false;

    private static final String TAG = "LoginLibrary_google";

    private static GetCredentialRequest request = null;

//    /**
//     * google登录客户端对象
//     */
//    static GoogleSignInClient mGoogleSignInClient = null;
    /**
     * 初始化facebook和twitter
     * @param target
     */
    public static void init(Activity target, Context context, String gpWebClientId)
    {
        mActivity = target;
        mContext = context;
        Log.d(TAG, "init");

        credentialManager = CredentialManager.Companion.create(mContext);
        Log.d(TAG, "credentialManager=" + credentialManager);

        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                //将 setFilterByAuthorizedAccounts 参数设置为 true。如果没有可用的凭据
                // ，请再次调用该 API 并将 setFilterByAuthorizedAccounts 设置为 false
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(true)
//                .setServerClientId(mActivity.getString(R.string.new_default_web_client_id))
                .setServerClientId(gpWebClientId)
                .build();
        request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        Log.d(TAG, "googleIdOption=" + googleIdOption);
        Log.d(TAG, "request=" + request);

    }

    public static void onActivityResult(int requestCode, int resultCode, Intent data){
    }

    public static void login() {
        Log.d(TAG, "googleLogin");

        android.os.CancellationSignal cancellationSignal = new android.os.CancellationSignal();
        Log.d(TAG, "cancellationSignal=" + cancellationSignal);
        cancellationSignal.setOnCancelListener(() -> {
            if (oneTapStatus) oneTapStatus = false;
            Log.d(TAG, "Preparing credentials with Google was cancelled.");

            BaseSdk.runOnGLThread(new Runnable() {
                @Override
                public void run() {
                    LoginLibrary.onLoginCancel();
                }
            });
        });
        // 创建执行器，如果要在当前线程执行，可以设置为null
        Executor executor = Executors.newSingleThreadExecutor();
        // 异步获取凭证
        credentialManager.getCredentialAsync(
                mActivity,
                request,
                cancellationSignal,
                executor,
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        Log.i(TAG, "onResult: login success");
                        String uid = (String) result.getCredential().getData().get("com.google.android.libraries.identity.googleid.BUNDLE_KEY_ID");

                        BaseSdk.runOnGLThread(new Runnable() {
                            @Override
                            public void run()
                            {
                                Log.d(TAG, "handleSignIn uid=" + uid);
                                LoginLibrary.onLoginSuccess(uid);
                            }
                        });
                    }
                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        Log.d(TAG, "Unexpected type of credential" + e);
                        Logger logger = Logger.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
                        logger.log(Level.SEVERE, "Error getting (or preparing) credential: " + e);
                        if (e instanceof androidx.credentials.exceptions.GetCredentialCancellationException) {
                            BaseSdk.runOnGLThread(new Runnable() {
                                @Override
                                public void run() {
                                    LoginLibrary.onLoginCancel();
                                }
                            });
                        }
                        else {
                            BaseSdk.runOnGLThread(new Runnable() {
                                @Override
                                public void run() {
                                    LoginLibrary.onLoginFailure();
                                }
                            });
                        }
                    }
                }
        );
    }

    /**
     * 删除账号
     */
    public static void deleteAccount() {
        Log.d(TAG, "deleteAccount");
        // TODO
    }

    /**
     * 注销登录
     */
    public static void logout() {
        Log.d(TAG, "googleLoginOut");
        ClearCredentialStateRequest clearCredentialStateRequest = new ClearCredentialStateRequest();
        android.os.CancellationSignal cancellationSignal = new android.os.CancellationSignal();
        cancellationSignal.setOnCancelListener(() -> {
            if (oneTapStatus) oneTapStatus = false;
            Log.d("GoogleLog", "Preparing credentials with Google was cancelled.");
        });
        if (credentialManager != null) {
            credentialManager.clearCredentialStateAsync(
                    clearCredentialStateRequest,
                    cancellationSignal,
                    Executors.newSingleThreadExecutor(),
                    new CredentialManagerCallback<Void, ClearCredentialException>() {
                        @Override
                        public void onResult(Void unused)
                        {
                            Log.d("GoogleLog", "google注销登录成功");

                            BaseSdk.runOnGLThread(new Runnable() {
                                @Override
                                public void run() {
                                    LoginLibrary.onLogoutSuccess();
                                }
                            });
                        }

                        @Override
                        public void onError(@NonNull ClearCredentialException e) {
                            Log.d("GoogleLog","注销出错"+e);
                            BaseSdk.runOnGLThread(new Runnable() {
                                @Override
                                public void run() {
                                    LoginLibrary.onLoginFailure();
                                }
                            });
                        }
                    }
            );
        } else {
            Log.d("GoogleLog","logout失败，credentialManager为空");
            BaseSdk.runOnGLThread(new Runnable() {
                @Override
                public void run() {
                    LoginLibrary.onLoginFailure();
                }
            });
        }
    }
}