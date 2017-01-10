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

    //Actions
    public static final String ACTION_LOAD_USER_DATA = "io.stoh.hackcompanion.data.action.LOAD_USER_DATA";

    //Data
    public static final String DATA_MYMLH_TOKEN = "io.stoh.hackcompanion.data.key.DATA_MYMLH_TOKEN";
    public static final String DATA_MYMLH_USER_DATA = "io.stoh.hackcompanion.data.key.MY_MLH_USER_DATA";
    public static final String DATA_RECEIVER = "io.stoh.hackcompanion.data.key.DATA_RECEIVER";

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
        intent.setAction(ACTION_LOAD_USER_DATA);
        intent.putExtra(DATA_MYMLH_TOKEN, myMLHToken);
        intent.putExtra(DATA_RECEIVER, receiver);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
            final String action = intent.getAction();
            if (ACTION_LOAD_USER_DATA.equals(action)) {
                String myMLHToken = intent.getStringExtra(DATA_MYMLH_TOKEN);
                ResultReceiver receiver = intent.getParcelableExtra(DATA_RECEIVER);
                handleActionLoadUserData(myMLHToken, receiver);
        }
    }


    private void handleActionLoadUserData(final String myMLHToken, final ResultReceiver receiver) {
        final Bundle result = new Bundle();
        result.putString("Action", ACTION_LOAD_USER_DATA);
        Volley.newRequestQueue(this).add(
                new StringRequest(Request.Method.GET,
                    "https://my.mlh.io/api/v2/user.json",
                    new Response.Listener<String>() {
                        public void onResponse(String response) {
                            Log.d("NetworkLoaderService", "LoadUserData > Success");
                            result.putString(DATA_MYMLH_USER_DATA, response);
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
