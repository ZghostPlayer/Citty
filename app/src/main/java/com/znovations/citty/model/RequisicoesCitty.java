package com.znovations.citty.model;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;
import com.znovations.citty.config.ConfiguracaoFirebase;
import com.znovations.citty.helper.UsuarioFirebase;

import java.util.HashMap;
import java.util.Map;

public class RequisicoesCitty {

    private String meuId;
    private String idPessoa;
    private String status;
    private String Position;

    public RequisicoesCitty() {
    }

    public void salvarRequisicoes(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference user = firebaseRef.child("Requisicoes").child( getMeuId())
                .child("Enviadas").child(getIdPessoa());

        user.setValue(this);
    }

    public void salvarRequisicoesRecebidas(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuario = firebaseRef.child("Requisicoes").child(getIdPessoa())
                .child("Recebidas").child(getMeuId());

        usuario.setValue(this);
    }

    public void apagarRequisicoesRecebidas(String meuId, String idPessoa){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuarioExc = firebaseRef.child("Requisicoes").child(meuId)
                .child("Recebidas").child(idPessoa);

        usuarioExc.removeValue();
    }

    public void apagarRequisicoesEnviadas(String meuId, String idPessoa){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuarioExc = firebaseRef.child("Requisicoes").child(meuId)
                .child("Enviadas").child(idPessoa);

        usuarioExc.removeValue();
    }

    public void atualizarRequisicaoRecebida(String meuId, String idPessoa){


        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuarioAtt = firebaseRef.child("Requisicoes").child(idPessoa)
                .child("Enviadas").child(meuId);
        DatabaseReference usuarioAtua = firebaseRef.child("Requisicoes").child(meuId)
                .child("Recebidas").child(idPessoa);

        Map<String, Object > valoresUsuario = converterParaMap();

        usuarioAtt.updateChildren(valoresUsuario);
        usuarioAtua.updateChildren(valoresUsuario);
    }

    public void atualizarRequisicaoEnviada(String meuId, String idPessoa){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuarioAtt = firebaseRef.child("Requisicoes").child(idPessoa)
                .child("Enviadas").child(meuId);

        Map<String, Object > valoresUsuario = converterParaMap();

        usuarioAtt.updateChildren(valoresUsuario);
    }

    public Map<String, Object > converterParaMap(){
        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("status", getStatus());
        usuarioMap.put("position", getPosition());

        return usuarioMap;
    }

    public String getPosition() {
        return Position;
    }

    public void setPosition(String position) {
        Position = position;
    }

    public String getMeuId() {
        return meuId;
    }

    public void setMeuId(String meuId) {
        this.meuId = meuId;
    }

    public String getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(String idPessoa) {
        this.idPessoa = idPessoa;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
