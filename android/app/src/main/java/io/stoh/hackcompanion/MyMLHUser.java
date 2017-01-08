package io.stoh.hackcompanion;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.util.HashMap;
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
    //TODO fix lastname, firstname, etc to match JSON underscore versions
    public static class MyMLHUserObject {
        private String status;
        private Data data;

        public String getStatus() {
            return status;
        }
        public Data getData() {
            return data;
        }

        static class Data {
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
        static class School {
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

    //Token to authenticate
    private String myMLHToken;

    //Context used to load SharedPreferences
    private Context context;
    private SharedPreferences settings;


    /**
     * Initialization Function for MyMLHUser.  Gives Class context in order to be able to
     * load local User Data if it is stored
     * @param context Application Context
     */
    public void init(Context context) {
        this.context = context;
        settings = context.getSharedPreferences("HackCompanion", 0);
        String userJson = settings.getString("myMLHUser", null);
        if (userJson != null) {
            Log.d("MyMLHUser", "Loading Stored JSON Data");
            MyMLHUserObject userObject = new Gson().fromJson(userJson, MyMLHUserObject.class);
            setData(userObject);
        }

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
     * IllegalArgumentException is thrown
     * @return Volley Request to be added to RequestQueue
     */
    public StringRequest updateUser() {

        if (myMLHToken == null)
            throw new IllegalArgumentException("MyMLH Token is Not Defined");

        Log.d("MyMLHUser", "updateUser > Token Used: " + myMLHToken);
        StringRequest request = new StringRequest(Request.Method.GET,
                "https://my.mlh.io/api/v2/user.json",
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        Log.d("MyMLHUser", " updateUser > Response Received. Updating Data");
                        MyMLHUserObject userObject = new Gson().fromJson(response, MyMLHUserObject.class);
                        setData(userObject);

                    }
                },
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.d("MyMLHUser", " updateUser > Response Error");
                        error.printStackTrace();
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
    private void setData(MyMLHUserObject object) {
        Log.d("MyMLHUser", "setData");
        userObject = object;

        if(context != null && settings != null) {
            Log.d("MyMLHUser", "Saving User Data Locally");
            String userJson = new Gson().toJson(object);
            settings.edit().putString("myMLHUser", userJson).apply();
        }
        else {
            Log.e("MyMLHUser", "Unable to Save User Data Locally");
        }

        setChanged();
        notifyObservers();
    }

    /**
     * Get MyMLH User Data.  Further getters can be used to retrieve specific
     * information about the user
     * @return MyMLH User Data Object
     */
    public MyMLHUserObject getData() {
        return userObject;
    }




}
