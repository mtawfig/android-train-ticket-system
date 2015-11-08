package org.feup.cmov.inspectorticketapp.Controllers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.feup.cmov.inspectorticketapp.Models.ErrorResponse;
import org.feup.cmov.inspectorticketapp.Models.Ticket;
import org.feup.cmov.inspectorticketapp.Models.TicketDbHelper;
import org.feup.cmov.inspectorticketapp.R;
import org.feup.cmov.inspectorticketapp.Services.GetStatistics;
import org.feup.cmov.inspectorticketapp.Services.GetTickets;

import java.util.List;

public class StatisticsActivity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView = (WebView)findViewById(R.id.web_view);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.loadUrl("file:///android_asset/stats.html");

        final Context context = this;

        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                setData(webView, null);
            }
        });

        new GetStatistics(context, new GetStatistics.OnGetStatisticsTaskCompleted() {
            @Override
            public void onTaskCompleted(final String json) {
                setData(null, json);
            }

            @Override
            public void onTaskError(ErrorResponse error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute();
    }

    String mData;
    WebView mView;
    private void setData(WebView view, String data) {
        if (view != null) {
            mView = view;
        }

        if (data != null) {
            mData = data;
        }

        if (mData != null && mView != null) {
            mView.loadUrl("javascript:loadData(" + mData + ");");
        }
    }
}
