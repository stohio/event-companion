package io.stoh.hackcompanion.activity.navigation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.stoh.hackcompanion.data.MyMLHUser;
import io.stoh.hackcompanion.R;

/**
 * Created by csinko on 1/9/17.
 */

public class UserFragment extends Fragment {
    private MyMLHUser myMLHUser = MyMLHUser.getInstance();
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_user,container,false);
        TextView firstNameTV = (TextView) v.findViewById(R.id.user_first_name);
        TextView lastNameTV = (TextView) v.findViewById(R.id.user_last_name);
        firstNameTV.setText(myMLHUser.getUser().getData().getFirstName());
        lastNameTV.setText(myMLHUser.getUser().getData().getLastName());
        return v;
    }
}
