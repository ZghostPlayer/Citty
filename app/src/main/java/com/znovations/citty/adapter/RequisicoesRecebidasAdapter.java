package com.znovations.citty.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.znovations.citty.R;
import com.znovations.citty.helper.UsuarioFirebase;
import com.znovations.citty.model.ModeloPerfil;
import com.znovations.citty.model.RequisicoesCitty;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequisicoesRecebidasAdapter extends RecyclerView.Adapter<RequisicoesRecebidasAdapter.MyViewHolder> {

    private List<ModeloPerfil> listaPerfil;
    private Context context;

    public RequisicoesRecebidasAdapter(List<ModeloPerfil> listaPerfil, Context context) {
        this.listaPerfil = listaPerfil;
        this.context = context;
    }

    @NonNull
    @Override
    public RequisicoesRecebidasAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.minhas_requi_recebidas_adapter, parent, false);
        return new RequisicoesRecebidasAdapter.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull RequisicoesRecebidasAdapter.MyViewHolder holder, int position) {

        ModeloPerfil modeloPerfil = listaPerfil.get(position);
        holder.requisicoes2.setText(modeloPerfil.getNome());
        holder.status2.setText(modeloPerfil.getStatus());

        holder.imagemConfirmar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequisicoesCitty req = new RequisicoesCitty();
                req.setStatus("Aceito");
                req.atualizarRequisicaoRecebida(UsuarioFirebase.refUsuarioAtual().getId(), modeloPerfil.getId());
            }
        });

        holder.imagemCancelar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequisicoesCitty req = new RequisicoesCitty();
                req.setStatus("Recusado");
                Log.d("Id pessoa", modeloPerfil.getId());
                Log.d("Meu id",UsuarioFirebase.refUsuarioAtual().getId() );
                req.setPosition(String.valueOf(holder.getAdapterPosition()));
                req.atualizarRequisicaoRecebida( UsuarioFirebase.refUsuarioAtual().getId(), modeloPerfil.getId());
                Toast.makeText(context,"Você recusou a solicitação", Toast.LENGTH_SHORT).show();

            }
        });

        if(!modeloPerfil.getFoto().equals(null)){
            Glide.with(context)
                    .load(modeloPerfil.getFoto())
                    .into(holder.imagemPerfil2);
        }else{
            holder.imagemPerfil2.setImageResource(R.drawable.padrao);
        }
    }

    @Override
    public int getItemCount() {
        Log.d("perfil",String.valueOf(this.listaPerfil.size()));
        return this.listaPerfil.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView requisicoes2, status2;
        CircleImageView imagemPerfil2;
        ImageButton imagemConfirmar2,imagemCancelar2;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            requisicoes2 = itemView.findViewById(R.id.textNomeReq2);
            status2 = itemView.findViewById(R.id.textStatusReq2);
            imagemPerfil2 = itemView.findViewById(R.id.circlePerfilAdapter2);
            imagemConfirmar2 = itemView.findViewById(R.id.imageButtonCheck2);
            imagemCancelar2 = itemView.findViewById(R.id.imageButtonClose2);
        }
    }

}
