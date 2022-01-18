package adapters;

import android.content.Intent;
import android.view.LayoutInflater;
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
import models.Entry;

public class EntryAdapter extends RecyclerView.Adapter <EntryAdapter.EntryViewHolder> {

    public List<Entry> entryList;


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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), WriteActivity.class);
                intent.putExtra("ID",id);
                v.getContext().startActivity(intent);
            }
        });
    }

    public void updateEntryList(List<Entry> adapterData) {
        this.entryList = adapterData;
        notifyDataSetChanged();
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
