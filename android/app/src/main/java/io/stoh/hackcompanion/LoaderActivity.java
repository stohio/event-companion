package io.stoh.hackcompanion;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by csinko on 1/7/17.
 */

public class LoaderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);

        //Try to get My MLH Token from Shared Preferences
        SharedPreferences settings = getSharedPreferences("HackCompanion", 0);
        String myMLHToken = settings.getString("myMLHToken", null);

        //If there isn't a token
        if (myMLHToken == null) {
            //Inform user that the app uses My MLH, and give them option to redirect or quit
            AlertDialog ActiveCallDialog = new AlertDialog.Builder(this)
                    .setMessage(R.string.my_mlh_msg)
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
                            finish();
                        }
                    })
                    .show();
        }
        //Otherwise, load the application
        else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("myMLHToken", myMLHToken);
            startActivity(intent);

        }



    }
}
