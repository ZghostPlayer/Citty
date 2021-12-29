package com.znovations.citty.activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.znovations.citty.R;
import com.znovations.citty.config.ConfiguracaoFirebase;
import com.znovations.citty.helper.Permissao;
import com.znovations.citty.helper.UsuarioFirebase;
import com.znovations.citty.model.ModeloPerfil;
import com.znovations.citty.model.Usuario;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private EditText editNome, editIdade, editSexo;
    private RadioButton radioButton, radioButton2;
    private ModeloPerfil modeloPerfil;
    private Usuario usuarioAtual;
    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private ImageButton imageButtonCamera, imageButtonGaleria;
    private static final int SELECAO_GALERIA= 200;
    private static final int SELECAO_CAMERA= 100;
    private CircleImageView perfilImagem;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        FirebaseUser usuario = UsuarioFirebase.getUsuarioAtual();
        Uri url = usuario.getPhotoUrl();

        //findView dos botões
        radioGroup = findViewById(R.id.radioGroup);
        radioButton = findViewById(R.id.radioButton);
        radioButton2 = findViewById(R.id.radioButton2);
        editNome = findViewById(R.id.editTextUserNome);
        editIdade = findViewById(R.id.editTextUserIdade);
        editSexo = findViewById(R.id.editTextUserSexo);
        imageButtonCamera = findViewById(R.id.imageButtonCamera);
        imageButtonGaleria = findViewById(R.id.imageButtonGaleria);
        perfilImagem = findViewById(R.id.circlePerfilReq);

        if(url != null){
            Glide.with(PerfilActivity.this)
                    .load(url)
                    .into(perfilImagem);
        }else{
            perfilImagem.setImageResource(R.drawable.padrao);
        }

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

        //Configs usuario
        usuarioAtual = UsuarioFirebase.refUsuarioAtual();

        //Inicializar componentes
        configRadio();
        modeloPerfil = new ModeloPerfil();
        Permissao.validarPermissoes(permissoesNecessarias,this,1);
        verificarUsuario();

    }


    //Verificar o que o usuario já possui
    public void verificarUsuario(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuarioVerificar = firebaseRef.child("Perfil").child(usuarioAtual.getId());
        usuarioVerificar.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModeloPerfil modeloPerfil = snapshot.getValue(ModeloPerfil.class);
                if(modeloPerfil.getNome() != null){
                    editNome.setText(modeloPerfil.getNome());
                }
                if(modeloPerfil.getIdade() != null){
                    editIdade.setText(modeloPerfil.getIdade());
                }
                if(modeloPerfil.getSexo() != null){
                    editSexo.setText(modeloPerfil.getSexo());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configRadio() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButton) {
                    modeloPerfil.setStatus("Disponível");
                } else if (checkedId == R.id.radioButton2) {
                    modeloPerfil.setStatus("Indisponível");
                } else {
                    Toast.makeText(PerfilActivity.this,
                            "Selecione um status",
                            Toast.LENGTH_SHORT).show();
                }


            }
        });
    }



    public void abrirCamera(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, SELECAO_CAMERA);
        }
    }

    public void abrirGaleria(View view){
        Intent pickGaleryImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(pickGaleryImage.resolveActivity((getPackageManager())) != null){
            startActivityForResult(pickGaleryImage, SELECAO_GALERIA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Bitmap imagem = null;

            try{
                switch(requestCode){
                    case SELECAO_CAMERA:
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(),
                                localImagemSelecionada);
                        break;
                }

                if(imagem != null){

                    perfilImagem.setImageBitmap(imagem);

                    //Recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //Salvar imagem no firebase
                    final StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("perfil")
                            .child(usuarioAtual.getId())
                            .child("perfil jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(PerfilActivity.this
                                    ,"Sucesso ao salvar a imagem "
                                    ,Toast.LENGTH_SHORT).show();

                            imagemRef.getDownloadUrl().addOnCompleteListener
                                    (new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                    atualizaFotoUsuario(url);
                                }
                            });
                        }
                    });


                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void atualizaFotoUsuario(Uri url){
        UsuarioFirebase.atualizaFotoUsuario(url);
    }

    public void conferirCampos(View view) {
        String nome = editNome.getText().toString();
        String idade = editIdade.getText().toString();
        String sexo = editSexo.getText().toString();
        String status = modeloPerfil.getStatus();
        FirebaseUser usuario = UsuarioFirebase.getUsuarioAtual();

        if (!nome.isEmpty()){
            if (!idade.isEmpty()) {
                if (!sexo.isEmpty()) {
                    if(!status.isEmpty()) {
                        modeloPerfil.setNome(editNome.getText().toString());
                        modeloPerfil.setIdade(editIdade.getText().toString());
                        modeloPerfil.setSexo(editSexo.getText().toString());
                        modeloPerfil.setId(usuarioAtual.getId());
                        modeloPerfil.setFoto(usuario.getPhotoUrl().toString());
                        modeloPerfil.SalvarPerfil();
                        confirmarPerfil();
                    }else{
                        Toast.makeText(PerfilActivity.this,
                                "Escolha seu status",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PerfilActivity.this,
                            "Escolha seu sexo",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(PerfilActivity.this,
                        "Digite sua idade",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(PerfilActivity.this,
                    "Digite seu nome",
                    Toast.LENGTH_SHORT).show();
        }

    }


    public void confirmarPerfil() {
        Intent intent = new Intent(this, Perfil_outter.class);
        startActivity(intent);
        finish();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int permissaoResultado : grantResults){

            if(permissaoResultado == PackageManager.PERMISSION_DENIED){

                alertaValidarPermissao();

            }
        }
    }



    private void alertaValidarPermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissoes Negadas");
        builder.setMessage("Para utilizar o aplicativo é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confimar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}