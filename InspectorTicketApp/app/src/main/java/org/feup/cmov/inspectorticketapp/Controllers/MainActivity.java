package org.feup.cmov.inspectorticketapp.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.feup.cmov.inspectorticketapp.Models.ErrorResponse;
import org.feup.cmov.inspectorticketapp.Models.SharedPreferencesFactory;
import org.feup.cmov.inspectorticketapp.Models.Station;
import org.feup.cmov.inspectorticketapp.Models.Ticket;
import org.feup.cmov.inspectorticketapp.Models.TicketContract;
import org.feup.cmov.inspectorticketapp.Models.TicketDbHelper;
import org.feup.cmov.inspectorticketapp.Models.TicketEntry;
import org.feup.cmov.inspectorticketapp.Models.User;
import org.feup.cmov.inspectorticketapp.R;
import org.feup.cmov.inspectorticketapp.Services.ApiService;
import org.feup.cmov.inspectorticketapp.Services.GetTickets;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private TicketDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDbHelper = new TicketDbHelper(this);

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

    private Context context = this;

    public void onDownloadTicketDataClickHandler(View view) {
        new GetTickets(this, new GetTickets.OnGetTicketsTaskCompleted() {
            @Override
            public void onTaskCompleted(List<Ticket> tickets) {

                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                db.execSQL("delete from " + TicketEntry.TABLE_NAME);

                for(Ticket ticket : tickets) {
                    ContentValues values = ticket.toDatabaseValues();
                    db.insert(TicketEntry.TABLE_NAME, "null", values);
                }

                Toast.makeText(context, "Tickets have been stored in local storage", Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onTaskError(ErrorResponse error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        }).execute();
    }

    public void onValidateTicketQRClickHandler(View view) {
        Intent intent = new Intent(this, TicketQRScannerActivity.class);
        startActivity(intent);
    }
}
