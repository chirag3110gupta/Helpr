package com.example.helpr;

import com.google.android.gms.maps.model.Marker;

public class User {
    private String role;
    private Marker marker;
    User(String r, Marker m) {
        role = r;
        marker = m;
    }
    public void setRole(String r) {
        role = r;
    }
    public void setMarker(Marker m) {
        marker = m;
    }
    public String getRole() {
        return role;
    }
    public Marker getMarker() {
        return marker;
    }

}
