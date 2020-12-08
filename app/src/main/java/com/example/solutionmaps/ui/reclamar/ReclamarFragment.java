package com.example.solutionmaps.ui.reclamar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.solutionmaps.R;
import com.example.solutionmaps.classes.Notificacao;
import com.example.solutionmaps.classes.Reclamacao;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

public class ReclamarFragment extends Fragment {

    private DatabaseReference mDatabase;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_reclamar, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Button button = root.findViewById(R.id.button_enviar);
        final TextInputLayout nome = root.findViewById(R.id.text_nome);
        final TextInputLayout telefone = root.findViewById(R.id.text_telefone);
        final TextInputLayout endereco = root.findViewById(R.id.text_endereco);
        final TextInputLayout observacao = root.findViewById(R.id.text_detalhamento);
        final MaterialTextView idSolicitacao = root.findViewById(R.id.text_id_solicitacao);

        idSolicitacao.setText(gerarRandom());
        idSolicitacao.setClickable(true);
        idSolicitacao.setFocusable(false);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(endereco.getEditText().getText().toString() != "")
                {
                    final String URL = "https://www.cepaberto.com/api/v3/cep?cep="+ endereco.getEditText().getText().toString();
                    //buscar informações do cep
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(URL)
                            .header("Authorization","Token token=a43bece717e0bfa1bb43d10166662258")
                            .build();

                    try{

                        Response response = client.newCall(request).execute();

                        if (response.code() != 200) {
                            Toast toast = Toast.makeText(
                                    root.getContext(),
                                    "Falha ao consultar o cep, tente novamente!",
                                    Toast.LENGTH_LONG
                            );
                            toast.show();
                        } else {

                            String jsonData = response.body().string();
                            JSONObject bodyJson = new JSONObject(jsonData);

                            Reclamacao reclamacao = new Reclamacao(
                                    nome.getEditText().getText().toString(),
                                    telefone.getEditText().getText().toString(),
                                    endereco.getEditText().getText().toString(),
                                    observacao.getEditText().getText().toString(),
                                    "Novo Registro",
                                    idSolicitacao.getText().toString()
                            );

                            Notificacao notificacao = new Notificacao(
                                    idSolicitacao.getText().toString(),
                                    Float.parseFloat(bodyJson.getString("latitude")),
                                    Float.parseFloat(bodyJson.getString("longitude")),
                                    "Novo Registro",
                                    "Obra não inciada"
                            );

                            mDatabase.child("reclamacao")
                                    .child(reclamacao.getIdSolicitacao())
                                    .setValue(reclamacao)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast toast = Toast.makeText(root.getContext(),"Registrado com sucesso",Toast.LENGTH_LONG);
                                            toast.show();
                                            nome.getEditText().setText("");
                                            telefone.getEditText().setText("");
                                            endereco.getEditText().setText("");
                                            observacao.getEditText().setText("");
                                            idSolicitacao.setText(gerarRandom());
                                        }
                                    });

                            mDatabase.child("notificacao")
                                    .child(reclamacao.getIdSolicitacao())
                                    .setValue(notificacao);
                        }
                    } catch (IOException | JSONException e) {
                        Toast toast = Toast.makeText(
                                root.getContext(),
                                e.getMessage(),
                                Toast.LENGTH_LONG
                        );
                        toast.show();
                    }
                }
                else{
                    Toast toast = Toast.makeText(
                            root.getContext(),
                            "Preencha todos os campos para enviar",
                            Toast.LENGTH_LONG
                    );
                    toast.show();
                }
            }
        });
        return root;
    }

    private String gerarRandom() {
        String zeros = "000000";
        Random random = new Random();
        String s = Integer.toString(random.nextInt(0X1000000), 16);
        s = zeros.substring(s.length()) + s;
        return s;
    }
}