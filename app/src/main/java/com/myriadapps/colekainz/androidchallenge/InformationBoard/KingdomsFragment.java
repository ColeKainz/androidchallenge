package com.myriadapps.colekainz.androidchallenge.InformationBoard;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.myriadapps.colekainz.androidchallenge.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class KingdomsFragment extends Fragment {

    public KingdomsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_kingdoms, container, false);
    }
}
