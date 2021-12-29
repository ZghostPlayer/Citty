package com.znovations.citty.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.znovations.citty.R;
import com.znovations.citty.config.ConfiguracaoFirebase;
import com.znovations.citty.helper.UsuarioFirebase;
import com.znovations.citty.model.ModeloPerfil;
import com.znovations.citty.model.RequisicoesCitty;
import com.znovations.citty.model.Usuario;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequisicoesAdapter extends RecyclerView.Adapter<RequisicoesAdapter.MyViewHolder> {

    private List <ModeloPerfil> listaPerfil;
    private Context context;
    private Usuario usuarioAtual = UsuarioFirebase.refUsuarioAtual();

    public RequisicoesAdapter(List<ModeloPerfil> listaPerfil, Context context) {
        this.listaPerfil = listaPerfil;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.minhas_requi_adapter, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ModeloPerfil modeloPerfil = listaPerfil.get(position);
        holder.requisicoes.setText(modeloPerfil.getNome());
        holder.status.setText(modeloPerfil.getStatus());
        holder.detalhes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.linearRequisicoes.getVisibility() == View.GONE) {
                    holder.linearRequisicoes.setVisibility(View.VISIBLE);
                    holder.textNome.setText("Nome: " + modeloPerfil.getNome());
                    holder.textIdade.setText("Idade: " + modeloPerfil.getIdade());
                    holder.textSexo.setText("Sexo: " + modeloPerfil.getSexo());
                    holder.textProc.setText("Procurando: " + modeloPerfil.getK_palavra());
                    holder.textDesc.setText(modeloPerfil.getDescrição());

                    if (!modeloPerfil.getFoto().equals(null)) {
                        Glide.with(context)
                                .load(modeloPerfil.getFoto())
                                .into(holder.imagemDetalhes);
                    } else {
                        holder.imagemPerfil.setImageResource(R.drawable.padrao);
                    }
                }else{
                    holder.linearRequisicoes.setVisibility(View.GONE);
                }
            }
        });

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuarioEnv = firebaseRef.child("Requisicoes").child(usuarioAtual.getId())
                .child("Enviadas").child(modeloPerfil.getId());
        usuarioEnv.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                RequisicoesCitty req = snapshot.getValue(RequisicoesCitty.class);
                if(req == null){

                }else if(req.getStatus().equals("Aceito")) {
                    Log.d("Green", "IsGreen");
                    holder.status.setBackgroundColor(Color.GREEN);
                    holder.textAceito.setVisibility(View.VISIBLE);
                }else if(req.getStatus().equals("Recusado")){
                    holder.status.setBackgroundColor(Color.RED);
                    holder.status.setTextColor(Color.WHITE);
                    holder.imagemCancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RequisicoesCitty reqCitty = new RequisicoesCitty();
                            req.setStatus("Remover");
                            req.setPosition(String.valueOf(holder.getAdapterPosition()));
                            req.atualizarRequisicaoEnviada(modeloPerfil.getId(), usuarioAtual.getId());
                            Toast.makeText(context,"Você recusou a solicitação", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else if(req.getStatus().equals("Aguardando")){
                    holder.status.setBackgroundColor(Color.YELLOW);
                    holder.imagemCancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RequisicoesCitty reqCitty = new RequisicoesCitty();
                            req.setStatus("Remover");
                            req.setPosition(String.valueOf(holder.getAdapterPosition()));
                            req.atualizarRequisicaoEnviada(modeloPerfil.getId(), usuarioAtual.getId());
                            req.apagarRequisicoesRecebidas(usuarioAtual.getId(), modeloPerfil.getId());
                            Toast.makeText(context,"Você recusou a solicitação", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(!modeloPerfil.getFoto().equals(null)){
            Glide.with(context)
                    .load(modeloPerfil.getFoto())
                    .into(holder.imagemPerfil);
        }else{
            holder.imagemPerfil.setImageResource(R.drawable.padrao);
        }
    }

    @Override
    public int getItemCount() {
        return this.listaPerfil.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView requisicoes, status, detalhes, textNome, textIdade, textSexo, textProc, textDesc,
        textAceito;
        CircleImageView imagemPerfil, imagemDetalhes;
        ImageButton imagemCancelar;
        ConstraintLayout constraintRequisicoes;
        LinearLayout linearRequisicoes;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            requisicoes = itemView.findViewById(R.id.textNomeReq);
            status = itemView.findViewById(R.id.textStatusReq);
            imagemPerfil = itemView.findViewById(R.id.circlePerfilAdapter);
            imagemCancelar = itemView.findViewById(R.id.imageButtonClose);
            constraintRequisicoes = itemView.findViewById(R.id.ConstraintReq);
            detalhes = itemView.findViewById(R.id.textViewDet);
            textNome = itemView.findViewById(R.id.textNomeRequi);
            textIdade = itemView.findViewById(R.id.textIdadeRequi);
            textSexo = itemView.findViewById(R.id.textSexoRequi);
            textProc = itemView.findViewById(R.id.textProcurandoRequi);
            textDesc = itemView.findViewById(R.id.textDescricaoRequi);
            linearRequisicoes = itemView.findViewById(R.id.linearLayoutRequisicoes);
            imagemDetalhes = itemView.findViewById(R.id.circlePerfilReq);
            textAceito = itemView.findViewById(R.id.textAceito);

        }
    }

}
