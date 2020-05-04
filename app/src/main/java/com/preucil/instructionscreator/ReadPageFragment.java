package com.preucil.instructionscreator;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class ReadPageFragment extends Fragment {
    private String text;
    private TextView textView;
    private Uri uri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.read_page_fragment, container, false);
        textView = rootView.findViewById(R.id.readtext);
        ImageView image = rootView.findViewById(R.id.readimage);
        if (text != null)
            textView.setText(text);


        //Vlozeni obrazku
        if (uri != null) {
            try {

                Glide.with(getContext()).load(uri).apply(new RequestOptions()
                        .placeholder(R.drawable.add_photo)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)).into(image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rootView;
    }
    //Vlozeni ulozeneho textu
    public void setText(String data) {
        text = data;
    }

    //Nastaveni obrazku
    public void setImage(String path) {
        if (path == "") {

            return;
        }
        uri = Uri.parse(path);


    }

}
