package io.stoh.hackcompanion;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Observable;
import java.util.Observer;

import io.stoh.hackcompanion.io.stoh.hackcompanion.data.MyMLHUser;

/**
 * Created by csinko on 1/7/17.
 */

public class LoaderActivity extends AppCompatActivity implements Observer {
    private String myMLHToken;
    private boolean myMLHUserDataLoaded = false;
    private boolean myMLHNetworkDataLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);

        //Try to get My MLH Token from Shared Preferences
        SharedPreferences settings = getSharedPreferences("HackCompanion", 0);
        myMLHToken = settings.getString("myMLHToken", null);

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


        //load user data and start the application
        else {
            //get MyMLHUser Instance
            MyMLHUser myMLHUser = MyMLHUser.getInstance();

            //Initialize MyMLhUser and give it context.  If local data was loaded,
            //then make note of it
            if (myMLHUser.init(getApplicationContext())) {
                myMLHUserDataLoaded = true;
            }

            //Try to update over the internet
            StringRequest request = myMLHUser.updateUser(myMLHToken);
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            //Add as Observer immediately before running request
            //so Loader is only notified after request is finished
            myMLHUser.addObserver(this);
            requestQueue.add(request);


        }



    }

    @Override
    public void update(Observable o, Object arg) {
        if ((boolean) arg) {
            //update was successful
            myMLHUserDataLoaded = true;
            myMLHNetworkDataLoaded = true;
        }
        startApplication();
    }

    public void startApplication() {
        //Check if MyMLH User Data was loaded
        if (myMLHUserDataLoaded) {
            //Start the Application
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("myMLHToken", myMLHToken);

            if (!myMLHNetworkDataLoaded) {
                //If the data is local data, make note in the intent
                intent.putExtra("myMLHUserDataLoadedFromLocal", true);
            }
            startActivity(intent);
            finishAffinity();
        }

        else {
            //Inform user that My MLH Data was unable to be loaded and quit
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
