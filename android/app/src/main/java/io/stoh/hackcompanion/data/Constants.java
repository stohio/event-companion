package io.stoh.hackcompanion.data;

/**
 * Created by csinko on 1/10/17.
 */

public final class Constants {

    public enum Modes {
        USER,
        MENTOR,
        ORGANIZER,
        DEVELOPER,
        EMPTY;
    }

    public static final class Actions {

        public static final String LOAD_USER_DATA = "io.stoh.hackcompanion.data.action.LOAD_USER_DATA";

    }

    public static final class Keys {

        //Data
        public static final String MYMLH_TOKEN = "io.stoh.hackcompanion.data.key.MYMLH_TOKEN";
        public static final String MYMLH_USER_DATA = "io.stoh.hackcompanion.data.key.MY_MLH_USER_DATA";
        public static final String RECEIVER = "io.stoh.hackcompanion.data.key.RECEIVER";
        public static final String DATA_HACKATHONS = "io.stoh.hackcompanion.data.key.HACKATHONS";
        public static final String DATA_MYMLH_USER = "io.stoh.hackcompanion.data.key.MYMLH_USER";
    }

    public static final class Updates {

        public static final String HACKATHON = "io.stoh.hackcompanion.data.update.HACKATHON";
        public static final String USER = "io.stoh.hackcompanion.data.update.USER";
    }
}
