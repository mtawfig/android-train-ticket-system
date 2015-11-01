package org.feup.cmov.inspectorticketapp.Controllers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import org.feup.cmov.inspectorticketapp.Models.ErrorResponse;
import org.feup.cmov.inspectorticketapp.Models.SharedPreferencesFactory;
import org.feup.cmov.inspectorticketapp.Models.Ticket;
import org.feup.cmov.inspectorticketapp.Models.TicketDbHelper;
import org.feup.cmov.inspectorticketapp.R;
import org.feup.cmov.inspectorticketapp.Services.ApiService;
import org.feup.cmov.inspectorticketapp.Services.GetTickets;
import org.feup.cmov.inspectorticketapp.Services.PutTickets;

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
        uploadTicketsToServer(new PutTickets.OnPutTicketsTaskCompleted() {
            @Override
            public void onTaskCompleted(String message) {
                new GetTickets(context, new GetTickets.OnGetTicketsTaskCompleted() {
                    @Override
                    public void onTaskCompleted(final List<Ticket> tickets) {
                        SQLiteDatabase db = mDbHelper.getWritableDatabase();

                        TicketDbHelper.deleteAllTickets(db);

                        for (Ticket ticket : tickets) {
                            TicketDbHelper.insertTicket(db, ticket);
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

            @Override
            public void onTaskError(ErrorResponse error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    public void onValidateTicketQRClickHandler(View view) {
        Intent intent = new Intent(this, TicketQRScannerActivity.class);
        startActivity(intent);
    }

    public void onUploadTicketDataClickHandler(View view) {
        uploadTicketsToServer(new PutTickets.OnPutTicketsTaskCompleted() {
            @Override
            public void onTaskCompleted(String message) {
                Toast.makeText(context, message, Toast.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onTaskError(ErrorResponse error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    public void uploadTicketsToServer(PutTickets.OnPutTicketsTaskCompleted handler) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        Ticket[] tickets = TicketDbHelper.getAllTickets(db);

        new PutTickets(this, handler).execute(tickets);
    }
}
