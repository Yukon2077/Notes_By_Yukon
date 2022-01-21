package adapters;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.yukon.notes.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import activities.WriteActivity;
import database.NotesSQLiteHelper;
import models.Entry;

public class EntryAdapter extends RecyclerView.Adapter <EntryAdapter.EntryViewHolder> {

    public List<Entry> entryList;
    private int contextItemId, contextItemPosition;


    public EntryAdapter(List<Entry> entryList){
        this.entryList = entryList;
    }

    @NonNull
    @Override
    public EntryAdapter.EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_entry_cardview, parent,false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryAdapter.EntryViewHolder holder, int position) {

        String datetime,entry;
        Integer id;
        datetime = entryList.get(position).getLast_modified_datetime();
        entry = entryList.get(position).getEntry();
        id = entryList.get(position).getId();

        datetime = changeDateTimeFormat(datetime);

        holder.datetime.setText(datetime);
        holder.entry.setText(entry);
        holder.itemView.setTag(id);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                contextItemId = id;
                contextItemPosition = holder.getAdapterPosition();
                return false;
            }
        });
    }

    public void updateEntryList(List<Entry> adapterData) {
        this.entryList = adapterData;
        notifyDataSetChanged();
    }

    public void deleteEntry(Context context, String table_name) {
        NotesSQLiteHelper notesSQLiteHelper = new NotesSQLiteHelper(context);
        SQLiteDatabase db = notesSQLiteHelper.getWritableDatabase();
        notesSQLiteHelper.deleteEntry(db, table_name, contextItemId);
        entryList.remove(contextItemPosition);
        notifyItemRemoved(contextItemPosition);
    }

    public void editEntry(Context context) {
        Intent intent = new Intent(context, WriteActivity.class);
        intent.putExtra("ID", contextItemId);
        context.startActivity(intent);
    }

    public static class  EntryViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        TextView datetime, entry;
        MaterialCardView cardView;
        public EntryViewHolder(@NonNull View itemView) {

            super(itemView);
            datetime = itemView.findViewById(R.id.datetime);
            entry = itemView.findViewById(R.id.entry);
            cardView = itemView.findViewById(R.id.cardview);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.add(Menu.NONE, R.id.edit,
                    Menu.NONE, "Edit");
            contextMenu.add(Menu.NONE, R.id.delete,
                    Menu.NONE, "Delete");
        }
    }

    @Override
    public int getItemCount() {
        return entryList.size();
    }


    public String changeDateTimeFormat(String dateString){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date date = simpleDateFormat.parse(dateString);
            SimpleDateFormat displayDateFormat = new SimpleDateFormat("yy/MM/dd hh:mm a", Locale.getDefault());
            return displayDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Parse Exception";
        }
    }

}
