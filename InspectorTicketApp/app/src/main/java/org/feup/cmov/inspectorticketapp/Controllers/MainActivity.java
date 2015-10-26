package org.feup.cmov.inspectorticketapp.Controllers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.feup.cmov.inspectorticketapp.Models.SharedPreferencesFactory;
import org.feup.cmov.inspectorticketapp.R;
import org.feup.cmov.inspectorticketapp.Services.ApiService;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences(
                getString(R.string.shared_preferences_identifier),
                Context.MODE_PRIVATE);

        if (!ApiService.isUserSignedIn(this)) {
            goToLogin();
        }
    }

    public void onLogOutClickHandler(View view) {
        String userName = SharedPreferencesFactory.getStringValueFromPreferences(
                getString(R.string.shared_preferences_user_name_key),
                sharedPreferences);

        Toast toast = Toast.makeText(getApplicationContext(), "See you soon, " +
                userName + ".", Toast.LENGTH_SHORT);
        toast.show();

        SharedPreferencesFactory.clearPreferences(sharedPreferences);
        SharedPreferencesFactory.setBooleanValueToPreferences(
                getString(R.string.shared_preferences_user_sign_in),
                false, sharedPreferences);

        goToLogin();
    }

    public void goToLogin() {
        Intent intent = new Intent(this, SignInActivity.class);
        finish();
        startActivity(intent);
    }
}
