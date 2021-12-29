package com.znovations.citty.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.znovations.citty.R;
import com.znovations.citty.config.ConfiguracaoFirebase;
import com.znovations.citty.helper.UsuarioFirebase;
import com.znovations.citty.model.ModeloPerfil;
import com.znovations.citty.model.Usuario;

public class Perfil_outter extends AppCompatActivity {

    EditText palavra, descricao;
    ModeloPerfil modeloPerfil = new ModeloPerfil();
    Usuario usuarioAtual = UsuarioFirebase.refUsuarioAtual();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_outter);

        palavra = findViewById(R.id.editPalavraChave);
        descricao = findViewById(R.id.textDescricaoMap);

        //Configura a full screen
        View decorView = getWindow().getDecorView();
        Window window = getWindow();

        //Configura a decorView
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.red));
    }

    public void SalvarInfs(View view) {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pegarDados = firebaseRef.child("Perfil").child(usuarioAtual.getId());
        pegarDados.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModeloPerfil perfilSnap = snapshot.getValue(ModeloPerfil.class);
                modeloPerfil.setNome(perfilSnap.getNome());
                modeloPerfil.setIdade(perfilSnap.getIdade());
                modeloPerfil.setSexo(perfilSnap.getSexo());
                modeloPerfil.setStatus(perfilSnap.getStatus());
                modeloPerfil.setFoto(perfilSnap.getFoto());
                ConferirInf();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void verificarUsuario() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuarioVerificar = firebaseRef.child("Perfil").child(usuarioAtual.getId());
        usuarioVerificar.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModeloPerfil modeloPerfil = snapshot.getValue(ModeloPerfil.class);
                if (modeloPerfil.getK_palavra() != null) {
                    palavra.setText(modeloPerfil.getK_palavra());
                }
                if (modeloPerfil.getDescrição() != null) {
                    descricao.setText(modeloPerfil.getDescrição());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void ConferirInf() {
        String palavraChave = palavra.getText().toString();
        String descricaoPerfil = descricao.getText().toString();

        if (!palavraChave.isEmpty()) {
            if (!descricaoPerfil.isEmpty()) {
                modeloPerfil.setId(usuarioAtual.getId());
                modeloPerfil.setK_palavra(palavra.getText().toString());
                modeloPerfil.setDescrição(descricao.getText().toString());
                modeloPerfil.SalvarPerfil();

                Intent intent = new Intent(Perfil_outter.this, MapsActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(Perfil_outter.this,
                        "Escreva a descrição do seu pefil",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Perfil_outter.this,
                    "Escreva a palavra chave",
                    Toast.LENGTH_SHORT).show();

        }
    }

}