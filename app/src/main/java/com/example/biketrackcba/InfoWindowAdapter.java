package com.example.biketrackcba;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
Context context;
public  InfoWindowAdapter(Context context){
    this.context=context;
}
    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_info_window,null);
        TextView title = view.findViewById(R.id.userTitle);
        TextView snippet1 = view.findViewById(R.id.usersnippet1);
        title.setText(marker.getTitle());
        snippet1.setText(marker.getSnippet());
        return view;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        return null;
    }
}
