package com.znovations.citty.helper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.znovations.citty.activitys.CadastroActivity;
import com.znovations.citty.activitys.MapsActivity;
import com.znovations.citty.activitys.PerfilActivity;
import com.znovations.citty.config.ConfiguracaoFirebase;
import com.znovations.citty.model.ModeloPerfil;
import com.znovations.citty.model.Usuario;

public class UsuarioFirebase {

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }

    public static Usuario refUsuarioAtual(){
        FirebaseUser firebaseUser = getUsuarioAtual();

        Usuario usuario = new Usuario();
        usuario.setId(firebaseUser.getUid());
        usuario.setEmail(firebaseUser.getEmail());
        usuario.setNome(firebaseUser.getDisplayName());

        return usuario;
    }

    public static boolean atualizarNomeUsuario(String nome){

    try{
        FirebaseUser user = getUsuarioAtual();
        UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().
                setDisplayName(nome)
                .build();
        user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(!task.isSuccessful()){
                    Log.d("Perfil","Erro ao atualizar o nome");
                }
            }
        });

        return true;

    }catch(Exception e){
        e.printStackTrace();
        return false;
    }



    }

    public static void redirecionaUsuarioLogado(Activity activity){
        FirebaseUser user = getUsuarioAtual();
        if(user != null) {
            DatabaseReference usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                    .child("usuarios")
                    .child(getIdentificadorUsuario());
            usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Usuario usuario = snapshot.getValue(Usuario.class);

                    Intent intent = new Intent(activity, PerfilActivity.class);
                    activity.startActivity(intent);
                    activity.finish();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    public static void redirecionaUsuarioLogadoDireto(Activity activity){
        FirebaseUser user = getUsuarioAtual();
        if(user != null) {
            DatabaseReference usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                    .child("usuarios")
                    .child(getIdentificadorUsuario());
            usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Usuario usuario = snapshot.getValue(Usuario.class);

                    Intent intent = new Intent(activity, MapsActivity.class);
                    activity.startActivity(intent);
                    activity.finish();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }



    public static boolean atualizaFotoUsuario(Uri url){
        try{
            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(url)
                    .build();

            user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Log.d("Perfil","Erro ao atualizar foto de perfil");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Perfil","Sucesso ao atualizar foto de perfil");
                }
            });
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static ModeloPerfil salvaFotoPerfil(){

        FirebaseUser firebaseUser = getUsuarioAtual();
        ModeloPerfil usuario = new ModeloPerfil();

        if (firebaseUser.getPhotoUrl() == null){
            usuario.setFoto("");
        }else{
            usuario.setFoto(firebaseUser.getPhotoUrl().toString());
        }
        return usuario;
    }

    public static String getIdentificadorUsuario(){
        return getUsuarioAtual().getUid();
    }
}
