package com.preucil.instructionscreator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

public class ReadActivity extends FragmentActivity {
    public int NUM_PAGES = 2;
    private ViewPager mPager;
    boolean editable = false;
    ArrayList pages;
    MainPageEditable mainPageEditable;
    String instructionsTitle;
    ArrayList<String> texts = new ArrayList<String>();
    ArrayList<String> images = new ArrayList<String>();
    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */


    private PagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read_activity);

        //Ziskani informaci z MainActivity a jejich nasledne nacteni

        Intent intent = getIntent();
        editable = intent.getBooleanExtra("EDITABLE", false);
        instructionsTitle = intent.getStringExtra("TITLE");
        File path = getFilesDir();
        File file = new File(path, instructionsTitle + ".txt");

        String inputtext;
        //Nacteni a zjisteni existence slozky
        if (file.exists()) {
            int length = (int) file.length();
            byte[] bytes = new byte[length];

            try {
                FileInputStream in = new FileInputStream(file);
                in.read(bytes);
                in.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputtext = new String(bytes);
                Log.e("JSON Data", inputtext);
                JSONObject instructs = new JSONObject(inputtext);
                instructionsTitle = instructs.getString("title");
                NUM_PAGES = instructs.getInt("num_pages");
                JSONArray pagedata = instructs.getJSONArray("pages");
                if (editable) {
                    mainPageEditable = new MainPageEditable();
                    pages = new ArrayList<EditPageFragment>();
                    for (int i = 0; i < pagedata.length(); i++) {
                        JSONObject page = pagedata.getJSONObject(i);
                        EditPageFragment ep = new EditPageFragment();
                        ep.setText(page.getString("text"));
                        ep.setImage(page.getString("img"));
                        pages.add(ep);
                    }
                } else {
                    pages = new ArrayList<ReadPageFragment>();
                    for (int i = 0; i < pagedata.length(); i++) {
                        JSONObject page = pagedata.getJSONObject(i);
                        ReadPageFragment rp = new ReadPageFragment();
                        rp.setText(page.getString("text"));
                        rp.setImage(page.getString("img"));
                        pages.add(rp);
                    }
                }
            } catch (JSONException e) {

            }


        } else {
            if (editable) {
                mainPageEditable = new MainPageEditable();
                pages = new ArrayList<EditPageFragment>();
                pages.add(new EditPageFragment());
                pages.add(new EditPageFragment());
            }

        }

        //Vytvoreni Pageru, ktery se stara o prohlizeni
        mPager = findViewById(R.id.readpager);
        mPager.setOffscreenPageLimit(NUM_PAGES + 1);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            //vytvoreni dalsich stran pri uprave
            @Override
            public void onPageSelected(int position) {
                if (position == NUM_PAGES - 1 && NUM_PAGES != 100 && editable) {


                    pages.add(new EditPageFragment());
                    NUM_PAGES++;
                    mPager.setOffscreenPageLimit(NUM_PAGES + 2);
                    pagerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //Nahrani do adapteru a zobrazeni
        mPager.setAdapter(pagerAdapter);
    }

    //Zjisteni pozice a ulozeni z uvodni stranky ci posunuti na predchozi stranu
    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        6);
            } else {

                File path = getFilesDir();

                Intent i = new Intent();
                if (editable) {
                    instructionsTitle = mainPageEditable.getTitle();

                    JSONObject instructions = new JSONObject();
                    JSONArray pagedata = new JSONArray();

                    for (int x = 0; x < pages.size(); x++) {
                        JSONObject page = new JSONObject();
                        EditPageFragment pg = (EditPageFragment) pages.get(x);
                        try {
                            if (!(pg.getText() == "" && pg.getImage() == "")) {
                                page.put("id", x + 1);
                                page.put("text", ((EditPageFragment) pages.get(x)).getText());
                                page.put("img", ((EditPageFragment) pages.get(x)).getImage());
                                pagedata.put(page);
                            }
                        } catch (JSONException e) {

                        }

                    }
                    try {
                        instructions.put("title", instructionsTitle);
                        instructions.put("num_pages", pagedata.length());
                        instructions.put("pages", pagedata);
                    } catch (JSONException e) {

                    }


                    File file = new File(path, instructionsTitle + ".txt");
                    try {
                        Writer out = new BufferedWriter(new FileWriter(file));
                        out.write(instructions.toString());
                        out.close();
                    } catch (IOException e) {

                    }
                }

                i.putExtra("Name", instructionsTitle);

                setResult(RESULT_OK, i);
                super.onBackPressed();
            }
        } else {

            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();

    }

    //Adapter ktery se stara o slozeni navodu
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (editable) {
                if (position == 0) {
                    mainPageEditable.setTitle(instructionsTitle);
                    mainPageEditable.setPageCount(NUM_PAGES);
                    return mainPageEditable;
                }
                return (EditPageFragment) pages.get(position - 1);
            } else {
                if (position == 0) {
                    MainPage mp = new MainPage();
                    mp.setPageCount(NUM_PAGES);
                    mp.setTitle(instructionsTitle);
                    return mp;
                }
                return (ReadPageFragment) pages.get(position - 1);
            }
        }
        //Pocet stran
        @Override
        public int getCount() {

            return NUM_PAGES + 1;
        }


    }
}