package edu.example.bookcatalog.ui.activities;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;

import edu.example.bookcatalog.R;

public class BaseActivity extends AppCompatActivity{
    protected ProgressDialog mProgressDialog;

    public void showProgress(){
        if(mProgressDialog == null){
            mProgressDialog = new ProgressDialog(this, R.style.custom_dialog);

            //Запрещаем отменить нажав на девайсе BACK
            mProgressDialog.setCancelable(false);

            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.show();
            mProgressDialog.setContentView(R.layout.progress_splash);
        } else {
            mProgressDialog.show();
            mProgressDialog.setContentView(R.layout.progress_splash);
        }
    }

    public void hideProgress(){
        if(mProgressDialog != null){
            if(mProgressDialog.isShowing()){
                mProgressDialog.hide();
            }
        }
    }
}
