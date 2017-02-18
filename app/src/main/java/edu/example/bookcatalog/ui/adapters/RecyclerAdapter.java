package edu.example.bookcatalog.ui.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import edu.example.bookcatalog.R;
import edu.example.bookcatalog.data.BookModel;
import edu.example.bookcatalog.ui.activities.DetailActivity;
import edu.example.bookcatalog.utils.ConstantManager;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.InfoHolder>{
    private Context mContext;
    private ArrayList<BookModel> booksList;

    public RecyclerAdapter(Context context, ArrayList<BookModel> booksList) {
        this.mContext = context;
        this.booksList = booksList;
    }

    @Override
    public InfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new InfoHolder(v);
    }

    @Override
    public void onBindViewHolder(InfoHolder holder, int position) {
        holder.title.setText(booksList.get(position).getTitle());
        holder.author.setText(booksList.get(position).getAuthor());
    }

    @Override
    public int getItemCount() {
        return booksList.size();
    }

    public class InfoHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private TextView author;

        public InfoHolder(View itemView) {
            super(itemView);

            title = (TextView)itemView.findViewById(R.id.tv_title);
            author = (TextView)itemView.findViewById(R.id.tv_author);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DetailActivity.class);
                    intent.putExtra(ConstantManager.EXTRA_SELECTED_BOOK, booksList.get(getAdapterPosition()));
                    mContext.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));
                }
            });
        }
    }
}
