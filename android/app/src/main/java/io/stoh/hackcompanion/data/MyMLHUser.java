package io.stoh.hackcompanion.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import io.stoh.hackcompanion.data.NetworkResultReceiver.Receiver;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;


/**
 * Created by csinko on 1/7/17.
 */

public class MyMLHUser extends Observable implements Receiver {

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
    private Gson gson;
    NetworkResultReceiver networkResultReceiver;

    /**
     * TODO update JavaDoc to cover Loading Hackathon Data
     * Initialization Function for MyMLHUser.  Gives Class context in order to be able to
     * load local User Data if it is stored.  Should be executed in first use of Class after
     * adding it as an observer.
     * @param context Application Context
     * @return True if data was loaded locally, False if data was not loaded  locally.
     * If returns false, then updateUser needs to be run
     */
    public boolean init(Context context, String myMLHToken) {
        Log.d("MyMLHUser", "Init");
        this.context = context;
        this.myMLHToken = myMLHToken;

        //Initialization
        networkResultReceiver = new NetworkResultReceiver(new Handler());
        networkResultReceiver.setReceiver(this);

        gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        settings = context.getSharedPreferences("HackCompanion", 0);

        //Try to Load Local Hackathon Data
        String hackathonJson = settings.getString(Constants.Keys.DATA_HACKATHONS, null);
        if (hackathonJson != null) {
            Type listType = new TypeToken<List<Hackathon>>(){}.getType();
            hackathons = gson.fromJson(hackathonJson, listType);
            currentHackathon = hackathons.get(0);

        }

        //Try to Load Local MyMLH User Data
        String myMLHUserJson = settings.getString(Constants.Keys.DATA_MYMLH_USER, null);
        if (myMLHUserJson != null) {
            //Data Loaded Successfully
            setUser(myMLHUserJson);
            return true;
        }
        //Data not Loaded Successfully
        return false;
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        Log.d("MyMLHUser", "ReceiveResult");
        switch (resultData.getString("Action", "")) {
            case Constants.Actions.LOAD_USER_DATA:
                if (resultCode == 0) {
                    //Success
                    setUser(resultData.getString(Constants.Keys.MYMLH_USER_DATA));
                }
                else {
                    //Fail
                    Bundle update = new Bundle();
                    update.putBoolean(Constants.Updates.USER, false);
                    setChanged();
                    notifyObservers(update);
                }
                break;
            default:
                break;
        }

    }


    /**
     * updates User Information using current Token.  If Token is not defined,
     * IllegalArgumentException is thrown
     */
    public void updateUser() {
        NetworkLoaderService.startActionLoadUserData(context, networkResultReceiver, myMLHToken);

    }

    public void updateHackathons() {

        //Static Content to "load" for now.  Later will send Request to NetworkLoaderService
        Hackathon hAkron = new Hackathon(1, Constants.Modes.USER, "HAkron", "2016-04-01", "2016-04-02", "Akron, OH");
        Hackathon mHacks = new Hackathon(2, Constants.Modes.USER, "MHacks", "2016-05-05", "2016-05-06", "Detroit, MI");
        Hackathon uncommonHacks = new Hackathon(3, Constants.Modes.USER, "Uncommon Hacks", "2016-01-14", "2016-01-15", "Chicago, IL");
        List<Hackathon> hackathons = Arrays.asList(hAkron, mHacks, uncommonHacks);
        String hackathonJson = gson.toJson(hackathons);

        //Save Content
        setHackathons(hackathonJson);
    }

    public void setHackathons(String hackathonJson) {
        //Make Sure JSON isn't empty
        if (hackathonJson == null || hackathonJson.equals(""))
            throw new IllegalArgumentException("Hackathons JSON is empty");

        //Save String
        settings.edit().putString(Constants.Keys.DATA_HACKATHONS, hackathonJson).apply();
        //Set hackathons
        Type listType = new TypeToken<List<Hackathon>>(){}.getType();
        hackathons = gson.fromJson(hackathonJson, listType);


        //Set current Hackathon
        currentHackathon = this.hackathons.get(0);

        //Notify Observers
        Bundle update = new Bundle();
        update.putBoolean(Constants.Updates.HACKATHON, true);
        setChanged();
        notifyObservers(update);
    }


    private void setUser(String myMLHUserJson) {
        //Make Sure Json isn't empty
        if (myMLHUserJson == null || myMLHUserJson.equals(""))
            throw new IllegalArgumentException("User JSON is empty");

        //Save String
        settings.edit().putString(Constants.Keys.DATA_MYMLH_USER, myMLHUserJson).apply();

        //Set Instance Object to String Converted to Object
        userObject = gson.fromJson(myMLHUserJson, MyMLHUserObject.class);

        //Notify Observers of update
        Bundle update = new Bundle();
        update.putBoolean(Constants.Updates.USER, true);
        setChanged();
        notifyObservers(update);
    }

    /**
     * Get MyMLH User Data.  Further getters can be used to retrieve specific
     * information about the user
     * @return MyMLH User Data Object
     */
    public MyMLHUserObject getUser() {
        return userObject;
    }

    public Constants.Modes getMode() {
        if (currentHackathon != null)
            return currentHackathon.getType();
        return Constants.Modes.EMPTY;
    }

    public List<Hackathon> getHackathons() {
        return hackathons;
    }

    public Hackathon getCurrentHackathon() {
        return currentHackathon;
    }




}
