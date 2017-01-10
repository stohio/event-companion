package io.stoh.hackcompanion.io.stoh.hackcompanion.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

/**
 * Created by csinko on 1/7/17.
 */

public  class MyMLHUser extends Observable {

    //Uses Singleton Scheme in order to be Observable and ensure only a single instance exists
    private static final MyMLHUser INSTANCE = new MyMLHUser();

    private MyMLHUser() {}

    //Get instance in another class by running MyMLHUser.getInstance
    public static MyMLHUser getInstance() {
        return INSTANCE;
    }


    //Object Model for JSON -> GSON Object.  Models GET user response Object
    public static class MyMLHUserObject {
        private String status;
        private Data data;

        public String getStatus() {
            return status;
        }
        public Data getData() {
            return data;
        }

        public static class Data {
            private int id;
            private String email, createdAt, updatedAt, firstName, lastName, major,
                    shirtSize, dietaryRestrictions, specialNeeds, dateOfBirth, gender, phoneNumber,
                    levelOfStudy;
            private String[] scopes;
            private School school;

            public int getId() {
                return id;
            }
            public String getEmail() {
                return email;
            }
            public String getCreatedAt() {
                return createdAt;
            }
            public String getUpdatedAt() {
                return updatedAt;
            }
            public String getFirstName() {
                return firstName;
            }
            public String getLastName() {
                return lastName;
            }
            public String getMajor() {
                return major;
            }
            public String getShirtSize() {
                return shirtSize;
            }
            public String getDietaryRestrictions() {
                return dietaryRestrictions;
            }
            public String getSpecialNeeds() {
                return specialNeeds;
            }
            public String getDateOfBirth() {
                return dateOfBirth;
            }
            public String getGender() {
                return gender;
            }
            public String getPhoneNumber() {
                return phoneNumber;
            }
            public String getLevelOfStudy() {
                return levelOfStudy;
            }
            public String[] getScopes() {
                return scopes;
            }
            public School getSchool() {
                return school;
            }
        }
        public static class School {
            private int id;
            private String name;

            public int getId() {
                return id;
            }
            public String getName() {
                return name;
            }
        }

    }

    //User data is stored as MyMLHUserObject, which is Static
    private MyMLHUserObject userObject;
    private List<Hackathon> hackathons;
    private Hackathon currentHackathon;

    //Token to authenticate
    private String myMLHToken;

    //Context used to load SharedPreferences
    private Context context;
    private SharedPreferences settings;

    //GSON with settings for converting Underscores to CamelCase
    private Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();


    /**
     * TODO update JavaDoc to cover Loading Hackathon Data
     * Initialization Function for MyMLHUser.  Gives Class context in order to be able to
     * load local User Data if it is stored.  Should be executed in first use of Class after
     * adding it as an observer.
     * @param context Application Context
     * @return True if data was loaded locally, False if data was not loaded  locally.
     * If returns false, then updateUser needs to be run
     */
    public boolean init(Context context) {
        boolean successful;
        this.context = context;
        settings = context.getSharedPreferences("HackCompanion", 0);
        String userJson = settings.getString("myMLHUser", null);
        if (userJson != null) {
            Log.d("MyMLHUser", "Loading Stored JSON MyMLH User Data");
            MyMLHUserObject userObject = new Gson().fromJson(userJson, MyMLHUserObject.class);
            setUser(userObject);
            successful = true;
        }
        else
            successful = false;

        String hackathonJson = settings.getString("hackathons", null);
        if (hackathonJson != null) {
            Log.d("MyMLHUser", "Loading Stored JSON Hackathon Data");
            Type listType = new TypeToken<List<Hackathon>>(){}.getType();
            hackathons = gson.fromJson(hackathonJson, listType);

        }




        return successful;
    }

    /**
     * Sets new token and updates User information using new token.
     * @param newToken the new Authentication Token
     * @return Volley Request to be added to RequestQueue
     */
    public StringRequest updateUser(String newToken) {
        myMLHToken = newToken;
        return updateUser();
    }

    /**
     * updates User Information using current Token.  If Token is not defined,
     * token will attempt to be loaded from SharedPreferences, otherwise
     * IllegalArgumentException is thrown
     * @return Volley Request to be added to a RequestQueue
     */
    public StringRequest updateUser() {

        if (myMLHToken == null) {
            settings.getString("myMLHToken", null);
            if (myMLHToken == null)
                throw new IllegalArgumentException("MyMLH Token is Not Defined");
        }

        Log.d("MyMLHUser", "updateUser > Token Used: " + myMLHToken);
        StringRequest request = new StringRequest(Request.Method.GET,
                "https://my.mlh.io/api/v2/user.json",
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        Log.d("MyMLHUser", " updateUser > Response Received. Updating Data");
                        MyMLHUserObject userObject = gson.fromJson(response, MyMLHUserObject.class);
                        setUser(userObject);

                    }
                },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.d("MyMLHUser", " updateUser > Response Error");
                        setChanged();
                        notifyObservers(false);
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

        };

        return request;

    }

    /**
     * Sets MyMLH User Data and Notifies Observers.  ALl data is overwritten with new
     * Object's data and is saved locally if possible
     * @param object the new Data to set
     */
    private void setUser(MyMLHUserObject object) {
        Log.d("MyMLHUser", "setData");
        userObject = object;

        if(context != null && settings != null) {
            Log.d("MyMLHUser", "Saving User Data Locally");
            String userJson = gson.toJson(object);
            settings.edit().putString("myMLHUser", userJson).apply();
        }
        else {
            Log.e("MyMLHUser", "Unable to Save User Data Locally");
        }

        setChanged();
        notifyObservers(true);
    }

    /**
     * Get MyMLH User Data.  Further getters can be used to retrieve specific
     * information about the user
     * @return MyMLH User Data Object
     */
    public MyMLHUserObject getUser() {
        return userObject;
    }




}
