package adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.yukon.notes.R;

import activities.CreateActivity;

public class EntryAdapter extends RecyclerView.Adapter <EntryAdapter.EntryViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public EntryAdapter(Context context, Cursor cursor){
        this.mContext = context;
        this.mCursor=cursor;
    }

    @NonNull
    @Override
    public EntryAdapter.EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.recyclerview_cardview,parent,false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryAdapter.EntryViewHolder holder, int position) {
        if(!mCursor.moveToPosition(position)){
            return;
        }
        String date,time,entry;
        Integer id;
        date = mCursor.getString(mCursor.getColumnIndex("DATE"));
        time = mCursor.getString(mCursor.getColumnIndex("TIME"));
        entry = mCursor.getString(mCursor.getColumnIndex("ENTRY"));
        id = mCursor.getInt(mCursor.getColumnIndex("_id"));

        int x = Integer.parseInt(time.substring(0,time.indexOf(":")));
        String y = time.substring(time.indexOf(":"),time.length());
        if (x >= 12 ){
            x = x-12;
            time = x + y + " PM";
        } else{
            x = x+1;
            time = x + y + " AM";
        }
        holder.time.setText(time);
        holder.date.setText(date);
        holder.entry.setText(entry);
        holder.itemView.setTag(id);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CreateActivity.class);
                intent.putExtra("ID",id);
                mContext.startActivity(intent);
            }
        });


    }

    public static class  EntryViewHolder extends RecyclerView.ViewHolder{

        TextView date,time,entry;
        CardView cardView;
        public EntryViewHolder(@NonNull View itemView) {

            super(itemView);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            entry = itemView.findViewById(R.id.entry);
            cardView = itemView.findViewById(R.id.cardview);
        }
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor){
        if(mCursor!=null){
            mCursor.close();
        }
        mCursor=newCursor;
        if(newCursor!=null){
            notifyDataSetChanged();
        }


    }
}
