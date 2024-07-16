package com.fisher.myloginlibrary.arrbuild1.login;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;

import android.app.Activity;
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


import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import androidx.annotation.NonNull;

import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsLogger;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.LoginBehavior;
import com.fisher.myloginlibrary.arrbuild1.BaseSdk.BaseSdk;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginLibrary_facebook {
    static Activity mActivity = null;
    private static CallbackManager mCallbackManager = CallbackManager.Factory.create();

    private static final String TAG = "LoginLibrary_facebook";

    /**
     * 初始化facebook和twitter
     * @param target
     */
    public static void init(Activity target)
    {
        mActivity = target;
        Log.d(TAG, "init");

        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);
    }

    public static void onActivityResult(int requestCode, int resultCode, Intent data){
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 账户登录
     */
    protected static void login() {
        Log.i(TAG,"Facebook Login!");
        LoginManager.getInstance().setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
        LoginManager.getInstance().logInWithReadPermissions(mActivity, Arrays.asList("public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //登录成功回调
                Log.i(TAG, "onSuccess: Login Success!");
                String openId = loginResult.getAccessToken().getUserId();
                Log.i(TAG, "openId=" + openId);

                BaseSdk.runOnGLThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        Log.d(TAG, "handleSignIn openId=" + openId);
                        LoginLibrary.onLoginSuccess(openId);
                    }
                });
            }

            @Override
            public void onCancel() {
                //登录取消回调
                Log.i(TAG, "onCancel: Login Cancel!");

                BaseSdk.runOnGLThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        LoginLibrary.onLoginCancel();
                    }
                });
            }

            @Override
            public void onError(@NonNull FacebookException e) {
                //登录错误回调
                Log.d(TAG, "Facebook Login onError: "+ e.getMessage());
                BaseSdk.runOnGLThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        LoginLibrary.onLoginFailure();
                    }
                });
            }
        });
    }


    /**
     * 判断登录状态
     * @return true 已登录，false 未登录
     */
    protected static boolean checkLogined() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken!= null && !accessToken.isExpired()){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 获取用户ID
     * @return 用户ID
     */
    protected static String getUid() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken!= null &&!accessToken.isExpired()){
            return accessToken.getUserId();
        }else{
            return "";
        }
    }

    /**
     * 删除账号
     */
    public static void deleteAccount() {
        // TODO
        Log.d(TAG, "deleteAccount");
    }

    /**
     * 注销登录
     */
    public static void logout() {
        Log.d(TAG, "googleLoginOut");
        if(!checkLogined()){
            return;
        }
        LoginManager.getInstance().logOut();

        Log.d(TAG, "facebook注销登录成功");
        BaseSdk.runOnGLThread(new Runnable() {
            @Override
            public void run() {
                LoginLibrary.onLogoutSuccess();
            }
        });
    }
}