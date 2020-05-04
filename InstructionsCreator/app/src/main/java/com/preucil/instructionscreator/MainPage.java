package com.preucil.instructionscreator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class MainPage extends Fragment {
    String title;
    TextView numofpages;
    int pages = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.main_page, container, false);
        TextView txt = rootView.findViewById(R.id.readNadpis);
        numofpages = rootView.findViewById(R.id.numofpages);
        if (title != null) {
            txt.setText(title);
        }
        if (pages != -1) {
            numofpages.setText(pages + " pages");
        }
        return rootView;
    }

    //Ziskani nazvu z ReadActivity
    public void setTitle(String tit) {
        title = tit;
    }

    //Ziskani poctu stranek z ReadActivity
    public void setPageCount(int num) {
        if (numofpages == null) {
            pages = num;
        } else {
            numofpages.setText(num + " pages");
        }
    }
}

