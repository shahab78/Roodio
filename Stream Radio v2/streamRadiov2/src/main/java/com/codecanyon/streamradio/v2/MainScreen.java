package com.codecanyon.streamradio.v2;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.flashlight.R;

/**
 * Created by Márk on 2014.02.23..
 */
public class MainScreen extends Fragment {
    private static ImageView loading;
    private static TextView radioListLocation;
    private TextView radioListName;
    

    public static ImageView getLoadingImage() {
        return loading;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View android = inflater.inflate(R.layout.main_main, container, false);
        loading = (ImageView) android.findViewById(R.id.loading);
        radioListLocation = (TextView) android.findViewById(R.id.mainRadioName);
        radioListName = (TextView) android.findViewById(R.id.mainRadioLocation);
        Typeface fontRegular = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/font.otf");
        radioListLocation.setTypeface(fontRegular);
        radioListName.setTypeface(fontRegular);
        return android;
    }
}
