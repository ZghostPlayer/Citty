package com.znovations.citty.model;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.znovations.citty.config.ConfiguracaoFirebase;

public class LocalizacaoUsuario {

    private String id;
    private String latitude;
    private String longitude;

    public LocalizacaoUsuario(){

    }

    public void localUsuario() {

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        firebaseRef.child("Localizacao").child(getId()).setValue(this);

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
