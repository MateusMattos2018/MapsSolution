package com.example.solutionmaps.ui.notificacoes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;

import com.example.solutionmaps.R;
import com.example.solutionmaps.classes.Notificacao;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class NotificacoesFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;

    Notificacao notificacao;
    List<Notificacao> notificacoes = new ArrayList<>();
    LatLngBounds.Builder builder;
    CameraUpdate cameraUpdate;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_notificacoes, container, false);
        notificacao = new Notificacao();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("notificacao");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot data: snapshot.getChildren()){
                        notificacao = data.getValue(Notificacao.class);
                        notificacoes.add(notificacao);
                    }
                    SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                    mapFragment.getMapAsync(NotificacoesFragment.this);
                }
                else{
                    Toast toast = Toast.makeText(
                            root.getContext(),
                            "Erro na localização do lugar, tente novamente!",
                            Toast.LENGTH_LONG
                    );
                    toast.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast toast = Toast.makeText(
                        root.getContext(),
                        "Houve um problema no banco de dados!",
                        Toast.LENGTH_LONG
                );
                toast.show();
            }

        });

        return root;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        List<Marker> markers = new ArrayList<Marker>();
        int padding = 50;

        for (Notificacao notificacao : notificacoes) {

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(notificacao.getLatitude(), notificacao.getLongitude()))
                    .title(notificacao.getObservacao()));
            markers.add(marker);
        }

        builder = new LatLngBounds.Builder();

        for (Marker m : markers) {
            builder.include(m.getPosition());
        }

        LatLngBounds bounds = builder.build();
        cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.animateCamera(cameraUpdate);
            }
        });
    }

}