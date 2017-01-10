package io.stoh.hackcompanion.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import io.stoh.hackcompanion.R;

public class TokenActivity extends AppCompatActivity {
    public static final String DATA_MYMLH_TOKEN = "io.stoh.hackcompanion.data.key.MYMLH_TOKEN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_activitiy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String url = getIntent().getDataString();
        Log.d("Address", url);
        final String myMLHToken = url.substring(url.indexOf("token/") + 6);
        Log.d("Token", myMLHToken);

        SharedPreferences settings = getSharedPreferences("HackCompanion", 0);
        settings.edit().putString(DATA_MYMLH_TOKEN, myMLHToken).apply();

        Toast.makeText(getApplicationContext(), "My MLH Authenticated!", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, LoaderActivity.class);
        startActivity(intent);


    }

}
