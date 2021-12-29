package com.znovations.citty.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.znovations.citty.config.ConfiguracaoFirebase;
import com.znovations.citty.helper.UsuarioFirebase;

import java.util.HashMap;
import java.util.Map;

public class ModeloPerfil {

    private String id;
    private String nome;
    private String idade;
    private String sexo;
    private String status;
    private String k_palavra;
    private String descrição;
    private String foto;

    public ModeloPerfil() {
    }

    public void SalvarPerfil(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference user = firebaseRef.child("Perfil").child(getId());

        user.setValue(this);
    }

    public void atualizar(){

        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuarioRef = firebase.child("Perfil").child(identificadorUsuario);

        Map<String, Object > valoresUsuario = converterParaMap();

        usuarioRef.updateChildren(valoresUsuario);
    }

    @Exclude
    public Map<String, Object > converterParaMap(){
        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("id", getId());
        usuarioMap.put("nome", getNome());
        usuarioMap.put("sexo", getSexo());
        usuarioMap.put("status", getStatus());
        usuarioMap.put("k_palavra", getK_palavra());
        usuarioMap.put("descricao", getDescrição());
        usuarioMap.put("foto", getFoto());

        return usuarioMap;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getK_palavra() {
        return k_palavra;
    }

    public void setK_palavra(String k_palavra) {
        this.k_palavra = k_palavra;
    }

    public String getDescrição() {
        return descrição;
    }

    public void setDescrição(String descrição) {
        this.descrição = descrição;
    }

    public String getIdade() {
        return idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
