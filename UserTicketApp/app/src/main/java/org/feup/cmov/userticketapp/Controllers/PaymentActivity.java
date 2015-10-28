package org.feup.cmov.userticketapp.Controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import org.feup.cmov.userticketapp.Helpers.CreditCardNumberChangeListener;
import org.feup.cmov.userticketapp.Models.SharedDataSingleton;
import org.feup.cmov.userticketapp.R;

import java.util.Calendar;

public class PaymentActivity extends AppCompatActivity {

    private EditText mCreditCardNumberView;
    private EditText mCreditCardMonthView;
    private EditText mCreditCardYearView;
    private EditText mCreditCardCodeView;

    private SharedDataSingleton sharedData = SharedDataSingleton.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCreditCardNumberView = (EditText) findViewById(R.id.credit_card_number);
        mCreditCardNumberView.addTextChangedListener(new CreditCardNumberChangeListener());

        mCreditCardMonthView = (EditText) findViewById(R.id.credit_card_month);
        mCreditCardYearView = (EditText) findViewById(R.id.credit_card_year);
        mCreditCardCodeView = (EditText) findViewById(R.id.credit_card_code);

        mCreditCardNumberView.setText(sharedData.getCreditCardNumber());
        mCreditCardMonthView.setText(sharedData.getCreditCardMonth());
        mCreditCardYearView.setText(sharedData.getCreditCardYear());
        mCreditCardCodeView.setText(sharedData.getCreditCardCode());
    }

    public void onProceedToCheckoutClickHandler(View view) {
        // Reset errors.
        mCreditCardNumberView.setError(null);

        String cardNumber = mCreditCardNumberView.getText().toString();
        String cardMonth = mCreditCardMonthView.getText().toString();
        String cardYear = mCreditCardYearView.getText().toString();
        String cardCode = mCreditCardCodeView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(cardNumber)) {
            mCreditCardNumberView.setError(getString(R.string.error_field_required));
            focusView = mCreditCardNumberView;
            cancel = true;
        } else if (!isCreditCardNumberValid(cardNumber)) {
            mCreditCardNumberView.setError(getString(R.string.error_invalid_credit_card_number));
            focusView = mCreditCardNumberView;
            cancel = true;
        }

        if (TextUtils.isEmpty(cardMonth)) {
            mCreditCardMonthView.setError(getString(R.string.error_field_required));
            focusView = mCreditCardMonthView;
            cancel = true;
        } else if (!isCreditCardMonthValid(cardMonth)) {
            mCreditCardMonthView.setError(getString(R.string.error_invalid_credit_card_month));
            focusView = mCreditCardMonthView;
            cancel = true;
        }

        if (TextUtils.isEmpty(cardYear)) {
            mCreditCardYearView.setError(getString(R.string.error_field_required));
            focusView = mCreditCardYearView;
            cancel = true;
        } else if (!isCreditCardYearValid(cardYear)) {
            mCreditCardYearView.setError(getString(R.string.error_invalid_credit_card_year));
            focusView = mCreditCardYearView;
            cancel = true;
        }

        if (TextUtils.isEmpty(cardCode)) {
            mCreditCardCodeView.setError(getString(R.string.error_field_required));
            focusView = mCreditCardCodeView;
            cancel = true;
        } else if (!isCreditCardCodeValid(cardCode)) {
            mCreditCardCodeView.setError(getString(R.string.error_invalid_credit_card_code));
            focusView = mCreditCardCodeView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            sharedData.setCreditCardNumber(cardNumber);
            sharedData.setCreditCardMonth(cardMonth);
            sharedData.setCreditCardYear(cardYear);
            sharedData.setCreditCardCode(cardCode);

            Intent intent = new Intent(getBaseContext(), CheckoutActivity.class);
            startActivity(intent);
        }
    }

    private boolean isCreditCardNumberValid(String creditCardNumber) {
        return true;
    }

    private boolean isCreditCardMonthValid(String cardMonth) {
        Integer month = Integer.parseInt(cardMonth, 10);
        return month >= 1 && month <= 12;
    }

    private boolean isCreditCardYearValid(String cardYear) {
        Integer year = Integer.parseInt(cardYear, 10) + 2000;
        Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
        return year >= currentYear;
    }

    private boolean isCreditCardCodeValid(String cardCode) {
        Integer code = Integer.parseInt(cardCode, 10);
        return code >= 111 && code <= 999;
    }
}

