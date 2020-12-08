package com.example.solutionmaps.ui.andamento;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.solutionmaps.R;
import com.example.solutionmaps.classes.Reclamacao;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AndamentoFragment extends Fragment {

    int[] iconStatus = new int[]{
            R.drawable.ic_baseline_obra_nova,
            R.drawable.ic_baseline_obra_finalizada,
            R.drawable.ic_baseline_obra_atencao,
            R.drawable.ic_baseline_obra_parada
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_andamento, container, false);
        final List<HashMap<String,String>> aList = new ArrayList<>();
        final List<Reclamacao> reclamacoes = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("reclamacao");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot data: snapshot.getChildren()){
                        Reclamacao reclamacao = data.getValue(Reclamacao.class);
                        reclamacoes.add(reclamacao);
                    }

                    for (Reclamacao rec: reclamacoes) {
                        HashMap<String, String> hm = new HashMap<>();
                        hm.put("endereco", "cep: " + rec.getEndereco());
                        hm.put("observacao", "detalhes: " + rec.getObservacao());

                        switch (rec.getSituacao()){
                            case "Novo Registro":
                                hm.put("status", Integer.toString(iconStatus[0]));
                                break;
                            case "Finalizado":
                                hm.put("status", Integer.toString(iconStatus[1]));
                                break;
                            case "Em Andamento":
                                hm.put("status", Integer.toString(iconStatus[2]));
                                break;
                            case "Parada":
                                hm.put("status", Integer.toString(iconStatus[3]));
                                break;
                        }

                        aList.add(hm);
                    }

                    String[] from = { "status","endereco","observacao" };
                    int[] to = { R.id.status, R.id.endereco, R.id.observacao };
                    SimpleAdapter adapter = new SimpleAdapter(
                            root.getContext(),
                            aList,
                            R.layout.listview_andamento,
                            from,
                            to
                    );
                    ListView listView = root.findViewById(R.id.listview);
                    listView.setAdapter(adapter);

                }
                else{
                    Toast toast = Toast.makeText(
                            root.getContext(),
                            "Não há dados para exibir!",
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
}