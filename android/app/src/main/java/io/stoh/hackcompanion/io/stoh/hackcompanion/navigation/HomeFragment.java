package io.stoh.hackcompanion.io.stoh.hackcompanion.navigation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.stoh.hackcompanion.R;

/**
 * Created by csinko on 1/9/17.
 */

public class HomeFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_main,container,false);
        return v;
    }
}
