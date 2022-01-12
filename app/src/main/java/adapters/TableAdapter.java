package adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.yukon.notes.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import activities.EntryActivity;
import database.NotesSQLiteHelper;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {

    private Cursor cursor;

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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EntryActivity.class);
                intent.putExtra("TB_NAME",table_name);
                v.getContext().startActivity(intent);
            }
        });
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(view.getContext(), view);
                try {
                    Field[] fields = popup.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if ("mPopup".equals(field.getName())) {
                            field.setAccessible(true);
                            Object menuPopupHelper = field.get(popup);
                            Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                            Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                            setForceIcons.invoke(menuPopupHelper, true);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                                // Needs confirmation
                                notesSQLiteHelper.deleteTable(db,table_name);
                                cursor = notesSQLiteHelper.getAllTables(db);
                                updateData(cursor);
                                notifyItemRemoved(holder.getAdapterPosition());
                                return true;

                            case R.id.rename:
                                Context context = holder.itemView.getContext();
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Rename document");
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
                                        // Needs more Exception handling
                                        if(table_name.toLowerCase().equals(new_name.toLowerCase())){
                                            Toast.makeText(context,"New name can't be same as old name",Toast.LENGTH_SHORT).show();
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_tables_cardview,parent,false);
        return new TableViewHolder(view);
    }

    public static class TableViewHolder extends RecyclerView.ViewHolder {

        public TextView tb_name;
        public ImageButton imageButton;
        public TableViewHolder(View view){
            super(view);
            tb_name = view.findViewById(R.id.table_name);
            imageButton = view.findViewById(R.id.imageButton);
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public void updateData(Cursor cursor){
        this.cursor = cursor;
        notifyDataSetChanged();
    }

}
