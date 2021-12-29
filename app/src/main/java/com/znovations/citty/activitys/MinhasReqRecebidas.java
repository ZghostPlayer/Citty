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
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.znovations.citty.R;
import com.znovations.citty.adapter.RequisicoesAceitasAdapter;
import com.znovations.citty.adapter.RequisicoesRecebidasAdapter;
import com.znovations.citty.config.ConfiguracaoFirebase;
import com.znovations.citty.helper.UsuarioFirebase;
import com.znovations.citty.model.ModeloPerfil;
import com.znovations.citty.model.RequisicoesCitty;
import com.znovations.citty.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class MinhasReqRecebidas extends AppCompatActivity {

    private RecyclerView recyclerRequisicoes, recyclerAceito;
    private RequisicoesRecebidasAdapter requisicoesAdapter;
    private RequisicoesAceitasAdapter requisicoesAceitasAdapter;
    private List<RequisicoesCitty> requisicao = new ArrayList<>();
    private List <ModeloPerfil> pessoaQuestao = new ArrayList<>();
    private List <ModeloPerfil> pessoaQuestaoAceito = new ArrayList<>();
    private List <String> idCompa = new ArrayList<>();
    private Usuario usuarioAtual = UsuarioFirebase.refUsuarioAtual();
    private TextView nenhuma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minhas_req_recebidas);

        recyclerRequisicoes = findViewById(R.id.recyclerViewReqRecebidas);
        recyclerAceito = findViewById(R.id.recyclerViewAceitas);
        nenhuma = findViewById(R.id.textViewNenhuma);

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
        Query refId = idsRef.child(usuarioAtual.getId()).child("Recebidas").orderByChild("id");
        refId.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for(DataSnapshot ds: snapshot.getChildren()) {
                    RequisicoesCitty reqSnapshots = ds.getValue(RequisicoesCitty.class);
                    Log.d("IdPessoas",reqSnapshots.getMeuId());
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

                DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
                Query requisicoesEnviadas = firebaseRef.child("Requisicoes").child(usuarioAtual.getId())
                        .child("Recebidas").child(requisicoes.getMeuId());

                requisicoesEnviadas.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        RequisicoesCitty reqSnap = snapshot.getValue(RequisicoesCitty.class);
                        if(reqSnap == null){
                            nenhuma.setVisibility(View.VISIBLE);
                        }else if(reqSnap.getStatus().equals("Aceito")){
                            if(pessoaQuestao.size()>0) {
                                pessoaQuestao.remove(Integer.parseInt(reqSnap.getPosition()));
                                requisicoesAdapter.notifyItemRemoved(Integer.parseInt(reqSnap.getPosition()));
                                requisicoesAdapter.notifyItemRangeChanged(Integer.parseInt(reqSnap.getPosition()), pessoaQuestao.size());
                            }
                            idCompa.add(reqSnap.getMeuId());
                            snapshots.add(snapshot);
                            if (snapshots.size() == requisicao.size()) {
                                recuperarPessoasModelosAceitos();
                            }
                        }else if(reqSnap.getStatus().equals("Recusado")){
                            if(pessoaQuestao.size()>0) {
                                pessoaQuestao.remove(Integer.parseInt(reqSnap.getPosition()));
                                requisicoesAdapter.notifyItemRemoved(Integer.parseInt(reqSnap.getPosition()));
                                requisicoesAdapter.notifyItemRangeChanged(Integer.parseInt(reqSnap.getPosition()), pessoaQuestao.size());
                            }
                            reqSnap.apagarRequisicoesRecebidas(UsuarioFirebase.refUsuarioAtual().getId(), reqSnap.getMeuId());
                        }else {
                            idCompa.add(reqSnap.getMeuId());
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

    public void recuperarPessoasModelosAceitos(){

        List snapshots2 = new ArrayList();
        for(String idComp: idCompa) {
            DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
            Query pessoas = firebaseRef.child("Perfil").child(idComp);
            pessoas.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    ModeloPerfil pessoas = snapshot.getValue(ModeloPerfil.class);
                    pessoaQuestaoAceito.add(pessoas);
                    snapshots2.add(snapshot);
                    if(snapshots2.size() == idCompa.size()){
                        carregarMinhasRequisicoesAceitas();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


    public void carregarMinhasRequisicoes(){
        if(!pessoaQuestao.equals(null)) {
            //Listar tarefas


            //Configura um adapter

            requisicoesAdapter = new RequisicoesRecebidasAdapter(pessoaQuestao, this);

            //Configura um Recycler View
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerRequisicoes.setLayoutManager(layoutManager);
            recyclerRequisicoes.setHasFixedSize(true);
            recyclerRequisicoes.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
            recyclerRequisicoes.setAdapter(requisicoesAdapter);
        }
    }

    public void carregarMinhasRequisicoesAceitas(){

        requisicoesAceitasAdapter = new RequisicoesAceitasAdapter(pessoaQuestaoAceito, this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerAceito.setLayoutManager(layoutManager);
        recyclerAceito.setHasFixedSize(true);
        recyclerRequisicoes.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
        recyclerAceito.setAdapter(requisicoesAceitasAdapter);

    }
}