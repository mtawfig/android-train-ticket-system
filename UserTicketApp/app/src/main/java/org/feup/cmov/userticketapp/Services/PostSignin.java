package org.feup.cmov.userticketapp.Services;


import android.content.ContentValues;
import android.os.AsyncTask;

import org.feup.cmov.userticketapp.Models.UserToken;

public class PostSignin extends AsyncTask<ContentValues, Void, UserToken> {

    public interface OnPostSigninTaskCompleted {
        void onTaskCompleted(UserToken userToken);
    }

    private OnPostSigninTaskCompleted listener;

    public PostSignin(OnPostSigninTaskCompleted listener){
        this.listener = listener;
    }

    @Override
    protected final UserToken doInBackground(ContentValues... contentValues) {

        String response = ApiService.getHttpPostResponse("/login", contentValues[0]);
        if (response == null) {
            return null;
        }
        return ApiService.gson.fromJson(response, UserToken.class);
    }

    @Override
    protected void onPostExecute(UserToken userToken){
        listener.onTaskCompleted(userToken);
    }
}