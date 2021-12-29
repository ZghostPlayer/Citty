package com.znovations.citty.activitys;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.znovations.citty.R;
import com.znovations.citty.config.ConfiguracaoFirebase;
import com.znovations.citty.databinding.ActivityMapaBinding;
import com.znovations.citty.helper.UsuarioFirebase;
import com.znovations.citty.model.ModeloPerfil;
import com.znovations.citty.model.RequisicoesCitty;
import com.znovations.citty.model.Usuario;
import com.znovations.citty.model.LocalizacaoUsuario;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapaBinding binding;
    private FirebaseAuth autenticacao;
    private Toolbar toolbar;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng localUsuario;
    private List<LocalizacaoUsuario> listaPessoas = new ArrayList<>();
    private Marker marcadorUsuario;
    private Marker marcadorPessoa;
    private TextView textView, textNome, textIdade, textSexo, textProc, textDesc;
    private Usuario usuarioAtual = UsuarioFirebase.refUsuarioAtual();
    private LinearLayout linearLayout, linearLayoutDesc;
    private CircleImageView carregarFotoPerfil;
    private Marker marcadorReferencia;
    private Button botaoRequisicao;
    private List <RequisicoesCitty> requisicao = new ArrayList<>();
    private List <ModeloPerfil> pessoaQuestao = new ArrayList<>();
    private List <String> idCompa = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        toolbar = findViewById(R.id.toolbar);
        textView = findViewById(R.id.textViewStatus);
        textNome = findViewById(R.id.textNomeMapa);
        textIdade = findViewById(R.id.textIdadeMapa);
        textSexo = findViewById(R.id.textSexoMapa);
        textProc = findViewById(R.id.textProcurandoMap);
        textDesc = findViewById(R.id.textDescricaoMap);
        linearLayout = findViewById(R.id.linearLayoutMapa);
        carregarFotoPerfil = findViewById(R.id.circlePerfilMapa);
        botaoRequisicao = findViewById(R.id.buttonCitty);
        linearLayoutDesc = findViewById(R.id.linearLayoutDesc);

        //Configuracoes inciais
        autenticacao = FirebaseAuth.getInstance();
        configurarToolbar();
        mostraStatus();

        //Configura a cor da janela
        View decorView = getWindow().getDecorView();
        Window window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.red));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        botaoRequisicao.setVisibility(View.GONE);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Recuperar localização do usuário
        recuperarLocalizacaoUsuario();
        recuperarPessoas();
    }

    private void passaLocalizacaoUsuario(){
        LocalizacaoUsuario localizacao = new LocalizacaoUsuario() ;
        localizacao.setId(usuarioAtual.getId());
        localizacao.setLatitude(String.valueOf(localUsuario.latitude));
        localizacao.setLongitude(String.valueOf(localUsuario.longitude));
        localizacao.localUsuario();
    }

    public void recuperarPessoas(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference pessoas = firebaseRef.child("Localizacao");
        Query localPessoas = pessoas.orderByChild("id");
        localPessoas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getChildrenCount()>0){
                    long children = snapshot.getChildrenCount();
                    String childer = String.valueOf(children);
                }
              for(DataSnapshot ds: snapshot.getChildren()){
                  LocalizacaoUsuario pessoas = ds.getValue(LocalizacaoUsuario.class);
                  if(!pessoas.getId().equals(usuarioAtual.getId()))
                  listaPessoas.add(pessoas);
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void exibirPessoas(){

        if(marcadorPessoa != null)
            marcadorUsuario.remove();

        for(LocalizacaoUsuario pessoas: listaPessoas){
                double latitude = Double.parseDouble(pessoas.getLatitude());
                double longitude = Double.parseDouble(pessoas.getLongitude());
                LatLng posicaoPessoa = new LatLng(latitude, longitude);
                marcadorPessoa = mMap.addMarker(new MarkerOptions()
                        .position(posicaoPessoa)
                        .title("Pessoa: ")
                        .snippet(pessoas.getId())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.usuario))
                );

        }
    }

    public void reqCitty(View view){
        RequisicoesCitty requisicoesCitty = new RequisicoesCitty();
        requisicoesCitty.setMeuId(usuarioAtual.getId());
        Marker marcadorRef = getMarker();
        requisicoesCitty.setIdPessoa(marcadorRef.getSnippet());
        requisicoesCitty.setStatus("Aguardando");
        requisicoesCitty.salvarRequisicoes();
        requisicoesCitty.salvarRequisicoesRecebidas();
    }

    public void clicarMapa(){
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                mMap.addMarker(
                        new MarkerOptions()
                        .position(latLng)
                        .title("Local de experiencia")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.cranio))

                );
            }
        });
    }

    private void recuperarLocalizacaoUsuario() {

        if(marcadorUsuario != null)
            marcadorUsuario.remove();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                localUsuario = new LatLng(latitude, longitude);

                passaLocalizacaoUsuario();
                exibirPessoas();
                //clicarMapa();
                cliqueInformações();

                marcadorUsuario = mMap.addMarker(
                        new MarkerOptions()
                                .position(localUsuario)
                                .title("Meu marcador")
                                .snippet(usuarioAtual.getId())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.usuario))
                );

                mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(localUsuario, 10)
                );

            }

        };

        //Solicitar atualizações de localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    10,
                    locationListener
            );
        }

    }

    @Override
    public void onBackPressed(){
        if(linearLayout.getVisibility() == View.VISIBLE){
            linearLayout.setVisibility(View.GONE);
            linearLayoutDesc.setVisibility(View.VISIBLE);
            botaoRequisicao.setVisibility(View.GONE);
        }else{
            super.onBackPressed();
        }
    }

    public void setMarker(Marker marcador){
        this.marcadorReferencia = marcador;
    }

    public Marker getMarker(){
        return marcadorReferencia;
    }

    public void cliqueInformações(){

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                linearLayout.setVisibility(View.VISIBLE);
                botaoRequisicao.setVisibility(View.VISIBLE);
                linearLayoutDesc.setVisibility(View.GONE);

                setMarker(marker);

                DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
                String compara = marker.getSnippet();
                //Acessa a referencia a usuario
                DatabaseReference userAtt = firebaseRef.child("usuarios");
                Query userAtua = userAtt.child(compara);
                //acessa a referencia ao perfil
                DatabaseReference userEst = firebaseRef.child("Perfil");
                Query userEstra = userEst.child(compara);
                userAtua.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Usuario userAtt = snapshot.getValue(Usuario.class);
                        textNome.setText("Nome: " + userAtt.getNome());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                userEstra.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ModeloPerfil stat = snapshot.getValue(ModeloPerfil.class);
                        textIdade.setText("Idade: " + stat.getIdade());
                        textSexo.setText("Sexo: " + stat.getSexo());
                        textProc.setText("Procurando: " + stat.getK_palavra());
                        textDesc.setText(stat.getDescrição());

                        //Carregar a foto
                        if(marker.getSnippet() == usuarioAtual.getId()){
                            FirebaseUser usuario = UsuarioFirebase.getUsuarioAtual();
                            Uri url = usuario.getPhotoUrl();

                            if(url != null){
                                Glide.with(MapsActivity.this)
                                        .load(url)
                                        .into(carregarFotoPerfil);
                            }else{
                                carregarFotoPerfil.setImageResource(R.drawable.padrao);
                            }
                        }else{

                            if(!stat.getFoto().equals(null)){
                                Glide.with(MapsActivity.this)
                                        .load(stat.getFoto())
                                        .into(carregarFotoPerfil);
                            }else{
                                carregarFotoPerfil.setImageResource(R.drawable.padrao);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                return false;
            }
        });

    }

    public void fechaDetalhes(){
        linearLayout.setVisibility(View.GONE);
        botaoRequisicao.setVisibility(View.GONE);
    }

    public void mostraStatus(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference status = firebaseRef.child("Perfil").child(usuarioAtual.getId());
        status.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ModeloPerfil perfilSnap = snapshot.getValue(ModeloPerfil.class);
                    textView.setText(perfilSnap.getStatus());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void configurarToolbar(){
        toolbar.setTitle("Encontrar");
        toolbar.inflateMenu(R.menu.menu_main);
        Menu menu = toolbar.getMenu();
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){


                    case R.id.menuPerfil:
                        break;

                    case R.id.menuRequisicoes:
                        Intent intentReq = new Intent(MapsActivity.this,
                                MinhasReq.class);
                        startActivity(intentReq);
                        break;

                    case R.id.menuRequisicoesRecebidas:
                        Intent intentReqRec = new Intent(MapsActivity.this,
                                MinhasReqRecebidas.class);
                        startActivity(intentReqRec);
                        break;

                    case R.id.menuInformacoes:
                        break;

                    case R.id.menuSair:
                        deslogarUsuario();
                        break;
                }
                return false;
            }
        });
    }

    public void deslogarUsuario(){
        autenticacao.signOut();
        finish();
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//
//        switch (item.getItemId()){
//
//            case R.id.menuSair:
//                autenticacao.signOut();
//                finish();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}