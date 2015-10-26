package org.feup.cmov.userticketapp.Services;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import org.feup.cmov.userticketapp.Models.ErrorResponse;
import org.feup.cmov.userticketapp.Models.HttpResponse;
import org.feup.cmov.userticketapp.Models.UserToken;

public class UserLogin extends AsyncTask<Void, Void, HttpResponse> {

    public interface OnUserLoginTaskCompleted {
        void onTaskCompleted(UserToken userToken);
        void onTaskError(ErrorResponse error);
        void onTaskCanceled();
    }

    private OnUserLoginTaskCompleted listener;
    private Context mContext;
    private final String mEmail;
    private final String mPassword;

    public UserLogin(Context context,
                         OnUserLoginTaskCompleted listener,
                         String email,
                         String password) {
        mEmail = email;
        mPassword = password;
        mContext = context;
        this.listener = listener;
    }

    @Override
    protected HttpResponse doInBackground(Void... params) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("email", mEmail);
        contentValues.put("password", mPassword);

        return ApiService.getHttpPostResponse(mContext, "/login", contentValues);
    }

    @Override
    protected void onPostExecute(HttpResponse response){
        if (response.isError()) {
            ErrorResponse error = ApiService.gson.fromJson(response.getContent(), ErrorResponse.class);
            listener.onTaskError(error);
        } else {
            UserToken userToken = ApiService.gson.fromJson(response.getContent(), UserToken.class);
            listener.onTaskCompleted(userToken);
        }
    }

    @Override
    protected void onCancelled() {
        listener.onTaskCanceled();
    }
}