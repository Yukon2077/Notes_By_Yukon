package adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.yukon.notes.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import activities.EntryActivity;
import database.NotesSQLiteHelper;
import models.Table;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {

    private List<Table> tableList;

    public TableAdapter(List<Table> tableList){
        this.tableList = tableList;
    }

    @Override
    public TableAdapter.TableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_tables_cardview,parent,false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TableAdapter.TableViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.tb_name.setText(tableList.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EntryActivity.class);
                intent.putExtra("TB_NAME",tableList.get(position).getName());
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
                                notesSQLiteHelper.deleteTable(db,tableList.get(position).getName());
                                tableList.remove(position);
                                notifyItemRemoved(holder.getAdapterPosition());
                                return true;

                            case R.id.rename:
                                Context context = holder.itemView.getContext();
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Rename document");
                                builder.setMessage("Enter new name");
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
                                        if(tableList.get(position).getName().equalsIgnoreCase(new_name)){
                                            Toast.makeText(context,"New name can't be same as old name",Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        notesSQLiteHelper.renameTable(db, tableList.get(position).getName(), new_name);
                                        tableList.get(position).setName(new_name);
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

    @SuppressLint("NotifyDataSetChanged")
    public void updateTableList(List<Table> adapterData) {
        this.tableList = adapterData;
        notifyDataSetChanged();
    }

    public void addNewTable(String table_name) {
        this.tableList.add(0, new Table(table_name));
        notifyItemInserted(0);
    }

    public static class TableViewHolder extends RecyclerView.ViewHolder {

        public TextView tb_name;
        public ImageButton imageButton;
        public TableViewHolder(View view) {
            super(view);
            tb_name = view.findViewById(R.id.table_name);
            imageButton = view.findViewById(R.id.imageButton);
        }
    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }
}
