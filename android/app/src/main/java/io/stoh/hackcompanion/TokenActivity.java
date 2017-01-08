package io.stoh.hackcompanion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class TokenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_activitiy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String url = getIntent().getDataString();
        final String myMLHToken = url.substring(url.indexOf("token/") + 6);
        Log.d("Token", myMLHToken);

        SharedPreferences settings = getSharedPreferences("HackCompanion", 0);
        settings.edit().putString("myMLHToken", myMLHToken).apply();

        Toast.makeText(getApplicationContext(), "My MLH Authenticated!", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, LoaderActivity.class);
        intent.putExtra("myMLHToken", myMLHToken);
        startActivity(intent);


    }

}
