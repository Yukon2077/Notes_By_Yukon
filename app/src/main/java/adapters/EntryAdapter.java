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
import activities.WriteActivity;

public class EntryAdapter extends RecyclerView.Adapter <EntryAdapter.EntryViewHolder> {

    private final Context mContext;
    private Cursor mCursor;

    public EntryAdapter(Context context, Cursor cursor){
        this.mContext = context;
        this.mCursor=cursor;
    }

    @NonNull
    @Override
    public EntryAdapter.EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.recyclerview_entry_cardview,parent,false);
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

        time = changeTimeFormat(time);
        date = changeDateFormat(date);

        holder.time.setText(time);
        holder.date.setText(date);
        holder.entry.setText(entry);
        holder.itemView.setTag(id);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, WriteActivity.class);
                intent.putExtra("ID",id);
                intent.putExtra("ENTRY",entry);
                mContext.startActivity(intent);
            }
        });
    }

    public static class  EntryViewHolder extends RecyclerView.ViewHolder{

        TextView date, time, entry;
        public EntryViewHolder(@NonNull View itemView) {

            super(itemView);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            entry = itemView.findViewById(R.id.entry);
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
    }

    public String changeTimeFormat(String time){
        int x = Integer.parseInt(time.substring(0, time.indexOf(":")));
        String y = time.substring(time.indexOf(":"));
        if (x > 12 ){
            x = x-12;
            time = x + y + " PM";
        } else if(x>0){
            time = x + y + " AM";
        } else{
            x=12;
            time = x + y + " AM";
        }
        return time;
    }

    public String changeDateFormat(String date){
        String y, m, d;
        y = date.substring(0,date.indexOf("-"));
        m = date.substring(date.indexOf("-")+1,date.lastIndexOf("-"));
        d = date.substring(date.lastIndexOf("-")+1);

        date = m + "/" + d;

        return date;
    }

    public void removeEntry(Integer position){
        notifyItemRemoved(position);
    }


}
