package com.blkcaphax.smartbin;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class WaitDialog {
    Activity activity;
    AlertDialog alertDialog;
    WaitDialog(Activity activity){
        this.activity = activity;
    }

    void showLoadindDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.layout_wait,null));
        builder.setCancelable(false);
        this.alertDialog = builder.create();
        this.alertDialog.show();
    }

    void hideLoadingDialog(){
        if (alertDialog != null && alertDialog.isShowing())
            this.alertDialog.dismiss();
    }
}
