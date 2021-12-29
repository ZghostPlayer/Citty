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

public class RequisicoesAceitasAdapter extends RecyclerView.Adapter<RequisicoesAceitasAdapter.MyViewHolder> {

    private List<ModeloPerfil> listaPerfil;
    private Context context;

    public RequisicoesAceitasAdapter(List<ModeloPerfil> listaPerfil, Context context) {
        this.listaPerfil = listaPerfil;
        this.context = context;
    }

    @NonNull
    @Override
    public RequisicoesAceitasAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.minhas_requi_recebidas_adapter, parent, false);
        return new RequisicoesAceitasAdapter.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull RequisicoesAceitasAdapter.MyViewHolder holder, int position) {

        ModeloPerfil modeloPerfil = listaPerfil.get(position);
        holder.requisicoes3.setText(modeloPerfil.getNome());
        holder.status3.setText(modeloPerfil.getStatus());

        if(!modeloPerfil.getFoto().equals(null)){
            Glide.with(context)
                    .load(modeloPerfil.getFoto())
                    .into(holder.imagemPerfil3);
        }else{
            holder.imagemPerfil3.setImageResource(R.drawable.padrao);
        }

    }


    @Override
    public int getItemCount() {
        Log.d("perfil",String.valueOf(this.listaPerfil.size()));
        return this.listaPerfil.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView requisicoes3, status3;
        CircleImageView imagemPerfil3;
        ImageButton imagemCancelar3;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            requisicoes3 = itemView.findViewById(R.id.textNomeReqAceito);
            status3 = itemView.findViewById(R.id.textStatusReqAceito);
            imagemPerfil3 = itemView.findViewById(R.id.circlePerfilAdapterAceito);
            imagemCancelar3 = itemView.findViewById(R.id.imageButtonCloseAceito);
        }
    }

}
