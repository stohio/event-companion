package io.stoh.hackcompanion.data;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NetworkLoaderService extends IntentService {

    public NetworkLoaderService() {
        super("NetworkLoaderService");
    }

    /**
     *
     * @param context Application Context
     * @param myMLHToken Token used to get User Data
     */
    public static void startActionLoadUserData(Context context, ResultReceiver receiver, String myMLHToken) {
        Intent intent = new Intent(context, NetworkLoaderService.class);
        intent.setAction(Constants.Actions.LOAD_USER_DATA);
        intent.putExtra(Constants.Keys.MYMLH_TOKEN, myMLHToken);
        intent.putExtra(Constants.Keys.RECEIVER, receiver);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
            final String action = intent.getAction();
            if (action.equals(Constants.Actions.LOAD_USER_DATA)) {
                String myMLHToken = intent.getStringExtra(Constants.Keys.MYMLH_TOKEN);
                ResultReceiver receiver = intent.getParcelableExtra(Constants.Keys.RECEIVER);
                handleActionLoadUserData(myMLHToken, receiver);
        }
    }


    private void handleActionLoadUserData(final String myMLHToken, final ResultReceiver receiver) {
        final Bundle result = new Bundle();
        result.putString("Action", Constants.Actions.LOAD_USER_DATA);
        Volley.newRequestQueue(this).add(
                new StringRequest(Request.Method.GET,
                    "https://my.mlh.io/api/v2/user.json",
                    new Response.Listener<String>() {
                        public void onResponse(String response) {
                            Log.d("NetworkLoaderService", "LoadUserData > Success");
                            result.putString(Constants.Keys.MYMLH_USER_DATA, response);
                            receiver.send(0, result);
                        }
                    },
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            Log.d("NetworkLoaderService", "LoadUserData > Fail");
                            receiver.send(1, result);
                            error.printStackTrace();
                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse != null && networkResponse.statusCode == 401) {
                                //Token was not authorized
                                throw new IllegalArgumentException("Token is not valid");
                            }
                        }
                    }) {
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + myMLHToken);
                    return headers;
                }

            });
        Log.d("NetworkLoaderService", "LoadUserData > Start");

    }

}
