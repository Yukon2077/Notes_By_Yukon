package adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.yukon.notes.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import activities.WriteActivity;
import database.NotesSQLiteHelper;

public class EntryAdapter extends RecyclerView.Adapter <EntryAdapter.EntryViewHolder> implements View.OnCreateContextMenuListener {

    private final Context mContext;
    private Cursor mCursor;
    private int contextId, contextPosition;

    public EntryAdapter(Context context, Cursor cursor){
        this.mContext = context;
        this.mCursor=cursor;
    }

    @NonNull
    @Override
    public EntryAdapter.EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        MaterialCardView view = (MaterialCardView) layoutInflater.inflate(R.layout.recyclerview_entry_cardview, parent,false);
        return new EntryViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull EntryAdapter.EntryViewHolder holder, int position) {
        if(!mCursor.moveToPosition(position)){
            return;
        }
        String datetime,entry;
        Integer id;
        datetime = mCursor.getString(mCursor.getColumnIndex("LAST_MODIFIED_DATETIME"));
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
                mContext.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                contextId = id;
                contextPosition = holder.getAdapterPosition();
                return false;
            }
        });
        holder.itemView.setOnCreateContextMenuListener(this);

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.add(Menu.NONE, R.id.edit,
                Menu.NONE, "Edit");
        contextMenu.add(Menu.NONE, R.id.delete,
                Menu.NONE, "Delete");
    }

    public static class  EntryViewHolder extends RecyclerView.ViewHolder{

        TextView datetime, entry;
        MaterialCardView cardView;
        public EntryViewHolder(@NonNull View itemView) {

            super(itemView);
            datetime = itemView.findViewById(R.id.datetime);
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
    }

    public void edit() {
        Intent intent = new Intent(mContext, WriteActivity.class);
        intent.putExtra("ID",contextId);
        mContext.startActivity(intent);
    }

    public void delete(Context context, String table_name) {
        NotesSQLiteHelper notesSQLiteHelper = new NotesSQLiteHelper(context);
        SQLiteDatabase db = notesSQLiteHelper.getWritableDatabase();
        notesSQLiteHelper.deleteEntry(db, table_name, contextId);
        swapCursor(notesSQLiteHelper.getAllItems(db, table_name));
        notifyItemRemoved(contextPosition);
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
