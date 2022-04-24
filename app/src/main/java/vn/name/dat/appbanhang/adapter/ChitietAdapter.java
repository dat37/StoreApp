package vn.name.dat.appbanhang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.name.dat.appbanhang.R;
import vn.name.dat.appbanhang.model.Item;

public class ChitietAdapter extends RecyclerView.Adapter<ChitietAdapter.MyViewHolder> {
    Context context;
    List<Item> itemList;

    public ChitietAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chitiet, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.txtTen.setText(item.getTensp() + "");
        holder.txtsoluong.setText("Số lượng: " + item.getSoluong() + "");
        Glide.with(context).load(item.getHinhanh()).into(holder.imagechitiet);

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imagechitiet;
        TextView txtTen, txtsoluong;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imagechitiet = itemView.findViewById(R.id.item_imgchitiet);
            txtTen = itemView.findViewById(R.id.item_tensp_chitiet);
            txtsoluong = itemView.findViewById(R.id.item_soluongchitiet);
        }
    }
}
