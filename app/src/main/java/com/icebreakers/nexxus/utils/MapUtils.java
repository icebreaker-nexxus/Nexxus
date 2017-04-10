package com.icebreakers.nexxus.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

/**
 * Created by radhikak on 4/5/17.
 */

public class MapUtils {

    public static BitmapDescriptor createBubble(Context context, int style, String title) {

        IconGenerator iconGenerator = new IconGenerator(context);

        // Possible color options:
        // STYLE_WHITE, STYLE_RED, STYLE_BLUE, STYLE_GREEN, STYLE_PURPLE, STYLE_ORANGE
        iconGenerator.setStyle(style);

        Bitmap bitmap = iconGenerator.makeIcon(title);
        // Use BitmapDescriptorFactory to create the marker
        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);

        return icon;
    }

    public static Marker addMarker(GoogleMap map, LatLng mapLocation, String title, BitmapDescriptor icon, boolean draggable) {

        // Creates and adds marker to the map
        Marker marker = map.addMarker(new MarkerOptions()
                .position(mapLocation)
                .title(title)
                .icon(icon));

        marker.setDraggable(draggable);

        return marker;
    }
}
