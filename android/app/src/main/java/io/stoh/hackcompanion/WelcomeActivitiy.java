package io.stoh.hackcompanion;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

public class WelcomeActivitiy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_activitiy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String url = getIntent().getDataString();
        String code = url.substring(url.indexOf("code/") + 5);
        Toast.makeText(getApplicationContext(), code, Toast.LENGTH_LONG).show();
        String appId = "664be6f8c0d9f8098c83f56454c3fa5abfe507514d8304b044163ff8b3cfb783";
        String secret = "50ae87bd735d40c6005817d1bd30f848ddc0016249d925d8960e64e9264d36d2";
        String token_url = "https://my.mlh.io/oauth/token?client_id=" + appId + "&client_secret = " + secret + "&code=" + code + "&redirect_uri=" + "http%3A%2F%2Fstoh.io%2Foauth%2Fcallback.html" + "&grant_type=authorization_code";

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

}
