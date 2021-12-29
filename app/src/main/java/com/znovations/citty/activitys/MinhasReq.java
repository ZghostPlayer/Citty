package com.znovations.citty.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.znovations.citty.R;
import com.znovations.citty.adapter.RequisicoesAdapter;
import com.znovations.citty.config.ConfiguracaoFirebase;
import com.znovations.citty.helper.UsuarioFirebase;
import com.znovations.citty.model.ModeloPerfil;
import com.znovations.citty.model.RequisicoesCitty;
import com.znovations.citty.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class MinhasReq extends AppCompatActivity {

    private RecyclerView recyclerRequisicoes;
    private RequisicoesAdapter requisicoesAdapter;
    private List <RequisicoesCitty> requisicao = new ArrayList<>();
    private List <ModeloPerfil> pessoaQuestao = new ArrayList<>();
    private List <String> idCompa = new ArrayList<>();
    private Usuario usuarioAtual = UsuarioFirebase.refUsuarioAtual();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minhas_req);

        recyclerRequisicoes = findViewById(R.id.recyclerViewReq);

        //Configura a cor da janela
        View decorView = getWindow().getDecorView();
        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.red));


        carregarIds();
    }

    public void carregarIds(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference idsRef = firebaseRef.child("Requisicoes");
        Query refId = idsRef.child(usuarioAtual.getId()).child("Enviadas").orderByChild("id");
        refId.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for(DataSnapshot ds: snapshot.getChildren()) {
                    RequisicoesCitty reqSnapshots = ds.getValue(RequisicoesCitty.class);
                    requisicao.add(reqSnapshots);
                }
                carregarRequisicoes();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void carregarRequisicoes(){

        List snapshots = new ArrayList();

        for(RequisicoesCitty requisicoes: requisicao) {
            if (requisicoes.getIdPessoa() != null) {
                DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
                Query requisicoesEnviadas = firebaseRef.child("Requisicoes").child(usuarioAtual.getId())
                        .child("Enviadas").child(requisicoes.getIdPessoa());

                requisicoesEnviadas.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        RequisicoesCitty reqSnap = snapshot.getValue(RequisicoesCitty.class);
                            if (reqSnap == null) {

                            }
                            else if (reqSnap.getStatus().equals("Remover")) {
                                ///
                                if(pessoaQuestao.size()>0) {
                                    pessoaQuestao.remove(Integer.parseInt(reqSnap.getPosition()));
                                    requisicoesAdapter.notifyItemRemoved(Integer.parseInt(reqSnap.getPosition()));
                                    requisicoesAdapter.notifyItemRangeChanged(Integer.parseInt(reqSnap.getPosition()), pessoaQuestao.size());
                                }
                                    reqSnap.apagarRequisicoesEnviadas( UsuarioFirebase.refUsuarioAtual().getId(), reqSnap.getIdPessoa());
                            } else{

                                idCompa.add(reqSnap.getIdPessoa());
                                snapshots.add(snapshot);
                                if (snapshots.size() == requisicao.size()) {
                                    recuperarPessoasModelos();
                                }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }



    public void recuperarPessoasModelos(){

        List snapshots2 = new ArrayList();
        for(String idComp: idCompa) {
            DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
            Query pessoas = firebaseRef.child("Perfil").child(idComp);
            pessoas.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    ModeloPerfil pessoas = snapshot.getValue(ModeloPerfil.class);
                    pessoaQuestao.add(pessoas);
                    snapshots2.add(snapshot);
                    if(snapshots2.size() == idCompa.size()){
                        carregarMinhasRequisicoes();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void apagarDados(String id){

    }


    public void carregarMinhasRequisicoes(){
        //Listar tarefas

        //Configura um adapter

        requisicoesAdapter = new RequisicoesAdapter(pessoaQuestao,this);

        //Configura um Recycler View
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerRequisicoes.setLayoutManager(layoutManager);
        recyclerRequisicoes.setHasFixedSize(true);
        recyclerRequisicoes.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
        recyclerRequisicoes.setAdapter(requisicoesAdapter);
    }
}