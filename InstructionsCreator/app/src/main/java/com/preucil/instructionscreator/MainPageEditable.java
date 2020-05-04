package com.preucil.instructionscreator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class MainPageEditable extends Fragment {
    EditText title;
    String tit;
    TextView numofpages;
    int pages = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.main_page_editable, container, false);
        title = rootView.findViewById(R.id.editNadpis);
        numofpages = rootView.findViewById(R.id.numofpagesedit);
        if (tit != null) {
            title.setText(tit);
        }
        if (pages != -1) {
            numofpages.setText(pages + " pages");
        }

        return rootView;
    }

    //Upraveny nazev
    public String getTitle() {
        return title.getText().toString();
    }

    //Ziskani puvodniho nazvu z ReadActivity
    public void setTitle(String title1) {
        tit = title1;
    }

    //Ziskani poctu stran
    public void setPageCount(int num) {
        if (numofpages == null) {
            pages = num;
        } else {
            numofpages.setText(num + " pages");
        }
    }
}

