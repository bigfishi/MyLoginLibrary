package com.fisher.myloginlibrary.arrbuild1.login;


import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

public class LoginLibrary {

    static Activity mActivity = null;

    private static final String TAG = "LoginLibrary";
    /**
     * 仅初始化facebook
     * @param target
     */
    public static void init(Activity target, Context context, String gpWebServerId){
        mActivity = target;
        Log.d(TAG, "init");
        Log.d(TAG, "mActivity=" + mActivity);
        LoginLibrary_google.init(mActivity, context, gpWebServerId);
        LoginLibrary_facebook.init(mActivity);
    }

    public static void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d(TAG, "onActivityResult");
        Log.d(TAG, "requestCode=" + requestCode);
        Log.d(TAG, "resultCode=" + resultCode);
        Log.d(TAG, "data=" + data);
        LoginLibrary_google.onActivityResult(requestCode, resultCode, data);
        LoginLibrary_facebook.onActivityResult(requestCode, resultCode, data);
    }

    public static void login(){
        int loginType = getLoginType();
        Log.d(TAG, "login loginType=" + loginType);
        if(mActivity == null)
            return ;
        if (loginType == 0) {
            LoginLibrary_facebook.login();
        } else if (loginType == 1) {
            LoginLibrary_google.login();
        }
    }

    public static void logout() {
        int loginType = getLoginType();
        Log.d(TAG, "logout loginType=" + loginType);
        if(mActivity == null)
            return ;
        if (loginType == 0) {
            LoginLibrary_facebook.logout();
        } else if (loginType == 1) {
            LoginLibrary_google.logout();
        }
    }

    public static void deleteAccount() {
        int loginType = getLoginType();
        Log.d(TAG, "deleteAccount loginType=" + loginType);
        if(mActivity == null)
            return ;
        if (loginType == 0) {
            LoginLibrary_facebook.deleteAccount();
        } else if (loginType == 1) {
            LoginLibrary_google.deleteAccount();
        }
        // 删除账号
//        GamemamaClientLibrary.initData("0");
        // 将授权信息清空，发送数据
//        GamemamaClientLibrary.deleteServerData();
        // 成功回调里删除本地数据，并关闭程序
//        onDeleteAccountSuccess();
//        uploadAllGameData
    }

    public static String getLoginProvider() {
        int loginType = getLoginType();
        String provider = "";
        switch (loginType) {
            case 0:
                provider = "fb";
                break;
            case 1:
                provider = "gp";
                break;
            default:
                break;
        }
        return provider;
    }

    public static native int getLoginType();
    public static native String getLoginUid();
    //    public static native void onInitDataSuccess();
//    public static native void onInitDataFail();
    public static native void onSavePlayerInfoSuccess(String id);
    public static native void onSavePlayerInfoFail();

    public static native void onLoginSuccess(String uid);
    public static native void onLoginCancel();
    public static native void onLoginFailure();

    public static native void onLogoutSuccess();

    public static native void onLogoutFailure();

    public static native void onDeleteAccountSuccess();

    public static native void onDeleteAccountFailure();

    public static native String generateGameDataJSONString();

    public static boolean getNetType(){
        if(mActivity == null)
            return  false;
        ConnectivityManager connectivityManager = (ConnectivityManager)mActivity.getSystemService(mActivity.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return true;
                }
                else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return true;
                }
                else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    return true;
                }
            }
        }
        else {
            try {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                    return true;
                }
            }
            catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }
        }
        return false;
    }
}