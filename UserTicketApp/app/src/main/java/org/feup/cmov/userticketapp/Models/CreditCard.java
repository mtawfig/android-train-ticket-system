package org.feup.cmov.userticketapp.Models;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;

import org.feup.cmov.userticketapp.R;

import lombok.Getter;
import lombok.Setter;

public class CreditCard {
    @Getter @Setter private Long    number;
    @Getter @Setter private Integer month;
    @Getter @Setter private Integer year ;
    @Getter @Setter private Integer code ;

    public CreditCard(Long number, Integer month, Integer year, Integer code) {
        this.number = number;
        this.month = month;
        this.year = year;
        this.code = code;
    }

    public ContentValues toDatabaseValues(Context context) {
        ContentValues values = new ContentValues();
        values.put(CreditCardEntry.COLUMN_NAME_NUMBER, number);
        values.put(CreditCardEntry.COLUMN_NAME_MONTH, month);
        values.put(CreditCardEntry.COLUMN_NAME_YEAR, year);
        values.put(CreditCardEntry.COLUMN_NAME_CODE, code);

        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.shared_preferences_identifier),
                Context.MODE_PRIVATE);
        Integer userId =
                Integer.valueOf(sharedPreferences.getString(
                                context.getString(R.string.shared_preferences_user_id_key), "0")
                );

        values.put(CreditCardEntry.COLUMN_NAME_USER_ID, userId);

        return values;
    }

}
