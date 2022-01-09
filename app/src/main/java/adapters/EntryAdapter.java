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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
        String datetime,entry;
        Integer id;
        datetime = mCursor.getString(mCursor.getColumnIndex("CREATED_DATETIME"));
        entry = mCursor.getString(mCursor.getColumnIndex("ENTRY"));
        id = mCursor.getInt(mCursor.getColumnIndex("_id"));

        datetime = changeDateTimeFormat(datetime);

        holder.datetime.setText(datetime);
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

        TextView datetime, entry;
        public EntryViewHolder(@NonNull View itemView) {

            super(itemView);
            datetime = itemView.findViewById(R.id.datetime);
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

    public String changeDateTimeFormat(String dateString){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateString);
            SimpleDateFormat displayDateFormat = new SimpleDateFormat("yy/MM/dd hh:mm a", Locale.getDefault());
            return displayDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Parse Exception";
        }
    }

    public void removeEntry(Integer position){
        notifyItemRemoved(position);
    }


}
