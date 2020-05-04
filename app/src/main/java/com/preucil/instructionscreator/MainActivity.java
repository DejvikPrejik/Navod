package com.preucil.instructionscreator;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    InstructionsAdapter adapter;
    ArrayList<String> navody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //inicializace zobrazeni listu a
        ListView list = findViewById(R.id.list);
        navody = new ArrayList<String>();

        //Ziskani poovoleni k cteni pameti telefonu
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    3);

        }
        //Nacteni nazvu souboru z pameti a ulozeni do ArrayList
        File path = getFilesDir();

        File[] files = path.listFiles();
        Log.d("Files", "Size: " + files.length);
        for (int i = 0; i < files.length; i++) {
            Log.e("Files", "FileName:" + files[i].getName());
            navody.add(files[i].getName().replace(".txt", ""));

        }
        //Ziskani tlacitka na vytvoreni noveho navodu a
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(MainActivity.this, ReadActivity.class);

                intent.putExtra("EDITABLE", true);

                startActivityForResult(intent, 5);
            }
        });
        //Prdeleni seznamu do adapteru a jeho vytvoreni
        adapter = new InstructionsAdapter(this, navody);


        list.setAdapter(adapter);

    }

    //Vysledek pri odkazu noveho navodu a zarazeni ho do aktualniho seznamu
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 5) {
                if (!navody.contains(data.getStringExtra("Name")))
                    navody.add(data.getStringExtra("Name"));
                adapter.notifyDataSetChanged();

            }
        }
    }

    //Adapter pro prideleni nazvu z listu ke kolonce v seznamu, spolecne s pridelenim tlacitek ke kazdemu navodu
    public class InstructionsAdapter extends ArrayAdapter<String> {
        public InstructionsAdapter(Context context, ArrayList<String> names) {
            super(context, 0, names);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final String name = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.instruct, parent, false);
            }

            TextView textname = convertView.findViewById(R.id.textView);
            textname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ReadActivity.class);

                    intent.putExtra("EDITABLE", false);
                    intent.putExtra("TITLE", name);
                    startActivity(intent);
                }
            });

            convertView.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ReadActivity.class);
                    intent.putExtra("EDITABLE", true);
                    intent.putExtra("TITLE", name);
                    startActivityForResult(intent, 5);
                }
            });
            convertView.findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    deleteData(name);
                    Intent i = new Intent(getContext(), MainActivity.class);
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(i);
                    overridePendingTransition(0, 0);


                }
            });
            textname.setText(name);
            return convertView;

        }
    }

    //Metoda na odstranění návodu
    public void deleteData(String name) {

        File path = getFilesDir();
        File file = new File(path, name + ".txt");
        file.delete();
    }
}





