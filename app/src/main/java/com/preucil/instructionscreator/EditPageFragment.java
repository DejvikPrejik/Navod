package com.preucil.instructionscreator;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditPageFragment extends Fragment {
    private ImageView image;
    private EditText text;
    private String text1;
    private Uri uri;
    String photoName;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.edit_page_fragment, container, false);
        image = rootView.findViewById(R.id.textImage);
        text = rootView.findViewById(R.id.editText);
        if (text1 != null) {
            text.setText(text1);
        }
        //Vyber fotky kliknutim(napred kontrola povoleni, pak vyber uri fotky)
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            3);
                    // Permission is not granted
                } else {
                    Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    i.setType("image/*");

                    startActivityForResult(i, 1);
                }
            }
        });

        //Dlouhe podrzeni(kontrola pristupu k fotaku a porizeni fotky
        image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            3);

                } else if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA},
                            3);
                } else {
                    //Starani se, aby se fotilo na jinem vlaknu nez bezi aplikace
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Vytvoreni slozky
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {

                        Log.i("ERROR", "IOException");
                    }
                    // Ulozeni fotky
                    if (photoFile != null) {
                        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(i, 0);
                    }
                }


                return true;
            }
        });


        if (uri != null) {
            Glide.with(getContext()).load(uri).diskCacheStrategy(DiskCacheStrategy.ALL).into(image);

        } else {
            Glide.with(getContext()).load(R.drawable.add_photo).diskCacheStrategy(DiskCacheStrategy.ALL).into(image);
        }

        return rootView;
    }


    private File createImageFile() throws IOException {
        // Tvorba souboru fotografie
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );


        photoName = "file:" + image.getAbsolutePath();
        return image;
    }

    //Vlozeni textu
    public void setText(String data) {
        text1 = (data);
    }

    //Ziskani upraveneho textu
    public String getText() {
        return text.getText().toString();
    }

    //Ziskani fotky
    public String getImage() {
        if (uri != null)
            return uri.toString();
        else
            return "";
    }

    //Cesta k fotce
    public void setImage(String path) {
        if (path == "") {

            return;
        }
        uri = Uri.parse(path);

    }

    //Vlozeni fotky
    private void setImage(Uri ur) {
        uri = ur;
        Glide.with(getContext()).load(uri).into(image);


    }

    //Vysledek zobrazeneho obrazku dle vyberu porizeni
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                setImage(data.getData());

            } else if (requestCode == 0) {
                try {

                    setImage(Uri.parse(photoName));


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }
    }
}
