package com.example.allowrepeatcallers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.example.quietexceptional.R;

public class DialogUtils {

    public static void showAlertDialog(Context context, String title, String message,
                                       DialogInterface.OnClickListener positiveListener,
                                       DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton(R.string.continue_menu, positiveListener);
        builder.setNegativeButton(R.string.cancel_menu, negativeListener);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
