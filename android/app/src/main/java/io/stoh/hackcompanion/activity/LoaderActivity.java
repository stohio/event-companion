package io.stoh.hackcompanion.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import java.util.Observable;
import java.util.Observer;

import io.stoh.hackcompanion.R;
import io.stoh.hackcompanion.data.Constants;
import io.stoh.hackcompanion.data.MyMLHUser;

/**
 * Created by csinko on 1/7/17.
 */

public class LoaderActivity extends AppCompatActivity implements Observer {
    private String myMLHToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);

        //Try to get MyMLH Token from Shared Preferences
        SharedPreferences settings = getSharedPreferences("HackCompanion", 0);
        myMLHToken = settings.getString(Constants.Keys.MYMLH_TOKEN, null);

        //If there isn't a token
        if (myMLHToken == null) {
            //Inform user that the app uses My MLH, and give them option to redirect or quit
            new AlertDialog.Builder(this)
                    .setMessage(R.string.my_mlh_no_token_msg)
                    .setTitle("Authenticate My MLH")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //Redirect to Browser
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://my.mlh.io/oauth/authorize?client_id="
                                            + getResources().getString(R.string.my_mlh_app_id)
                                            + "&redirect_uri="
                                            + Uri.encode(getResources().getString(R.string.my_mlh_callback_url))
                                            + "&response_type=token"));
                            startActivity(browserIntent);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //Quit Application
                            finishAffinity();
                        }
                    })
                    .setOnKeyListener(new Dialog.OnKeyListener() {
                        public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                //Quit Application
                                finishAffinity();
                            }
                            return false;
                        }
                    })
                    .show();
        }


        //If there is a token, load user data and start the application
        else {

            //get MyMLHUser Instance
            MyMLHUser myMLHUser = MyMLHUser.getInstance();

            //Initialize MyMLHUser and give it context.  If local data was loaded,
            //then make note of it
            if (myMLHUser.init(getApplicationContext(), myMLHToken)) {
                startApplication();
            }
            else {
                //Add MyMLHUser as Observer so it will be notified when the update occurs
                myMLHUser.addObserver(this);
                myMLHUser.updateUser();
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        Bundle update = (Bundle) arg;
        if (update.containsKey(Constants.Updates.USER)) {
            if(update.getBoolean(Constants.Updates.USER)) {
                startApplication();
            }
            else {
                new AlertDialog.Builder(this)
                        .setMessage(R.string.my_mlh_no_data_msg)
                        .setTitle("Authenticate My MLH")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Quit Application
                                finishAffinity();

                            }
                        })
                        .setOnKeyListener(new Dialog.OnKeyListener() {
                            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                    //Quit Application
                                    finishAffinity();
                                }
                                return false;
                            }
                        })
                        .show();
            }
        }
    }

    public void startApplication() {
            //Start the Application
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(Constants.Keys.MYMLH_TOKEN, myMLHToken);
            startActivity(intent);
            finishAffinity();
    }
}
