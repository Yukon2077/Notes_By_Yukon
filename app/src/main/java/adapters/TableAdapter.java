package adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.yukon.notes.R;

import java.util.Collections;
import java.util.List;

import activities.EntryActivity;
import database.NotesSQLiteHelper;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {

    private Cursor cursor;
    private Integer tableposition;

    public TableAdapter(Cursor cursor){
        this.cursor = cursor;
    }

    @Override
    public void onBindViewHolder(TableAdapter.TableViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if(!cursor.moveToPosition(position)){
            return;
        }
        String table_name = cursor.getString(cursor.getColumnIndex("TABLE_NAME"));
        holder.tb_name.setText(table_name);
        holder.itemView.setTag(table_name);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.cardView.getContext(), EntryActivity.class);
                intent.putExtra("TB_NAME",table_name);
                holder.cardView.getContext().startActivity(intent);
            }
        });
        holder.materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTablePosition(position);
                PopupMenu popup = new PopupMenu(view.getContext(), view);
                popup.inflate(R.menu.popup_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        NotesSQLiteHelper notesSQLiteHelper;
                        SQLiteDatabase db;
                        notesSQLiteHelper = new NotesSQLiteHelper(holder.itemView.getContext());
                        db = notesSQLiteHelper.getWritableDatabase();
                        switch (item.getItemId()) {

                            case R.id.delete:
                                notesSQLiteHelper.deleteTable(db,table_name);
                                cursor = notesSQLiteHelper.getAllTables(db);
                                updateData(cursor);
                                notifyItemRemoved(holder.getAdapterPosition());
                                return true;

                            case R.id.rename:
                                Context context = holder.itemView.getContext();
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Rename File");
                                builder.setMessage("Enter a new name");
                                EditText input = new EditText(context);
                                FrameLayout container = new FrameLayout(context);
                                FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                params.leftMargin = context.getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                                params.rightMargin = context.getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                                input.setLayoutParams(params);
                                container.addView(input);
                                builder.setView(container);
                                builder.setPositiveButton("Rename", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String new_name = String.valueOf(input.getText());
                                        if(table_name.toLowerCase().equals(new_name.toLowerCase())){
                                            Toast.makeText(context,"New name can't be same as old name",Toast.LENGTH_SHORT).show();
                                            return;
                                        } else if (new_name.equals("TempContentValue")){
                                            Toast.makeText(context,"New name can't be that, the app uses it",Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        notesSQLiteHelper.renameTable(db, table_name, new_name);
                                        cursor = notesSQLiteHelper.getAllTables(db);
                                        updateData(cursor);
                                        notifyItemChanged(holder.getAdapterPosition());

                                    }
                                });
                                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                builder.setCancelable(true);
                                builder.show();
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public TableAdapter.TableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MaterialCardView cardView = (MaterialCardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_tables_cardview,parent,false);
        return new TableViewHolder(cardView);
    }

    public static class TableViewHolder extends RecyclerView.ViewHolder {
        public MaterialCardView cardView;
        public MaterialButton materialButton;
        public TextView tb_name;
        public TableViewHolder(MaterialCardView cardView){
            super(cardView);
            this.cardView = cardView;
            tb_name = cardView.findViewById(R.id.table_name);
            materialButton = cardView.findViewById(R.id.materialButton);
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public Integer getTablePosition() {
        return tableposition;
    }

    public void setTablePosition(Integer position) {
        this.tableposition = position;
    }

    public void updateData(Cursor cursor){
        this.cursor = cursor;
    }

}
