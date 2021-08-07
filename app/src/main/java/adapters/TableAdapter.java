package adapters;

import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.yukon.notes.R;
import java.util.List;

import activities.EntryActivity;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {

    private List<String> tableList;
    private Integer tableposition;

    public TableAdapter(List<String> tableList){
        this.tableList = tableList;
    }

    @Override
    public void onBindViewHolder(TableAdapter.TableViewHolder holder, int position) {
        MaterialCardView cardView = holder.cardView;
        TextView tb_name = cardView.findViewById(R.id.table_name);
        tb_name.setText(tableList.get(position));
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(cardView.getContext(), EntryActivity.class);
                intent.putExtra("TB_NAME",tableList.get(position));
                cardView.getContext().startActivity(intent);
            }
        });
        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setTablePosition(position);
                return false;
            }
        });
    }

    @Override
    public TableAdapter.TableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MaterialCardView cardView = (MaterialCardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_tables_cardview,parent,false);
        return new TableViewHolder(cardView);
    }

    public static class TableViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public MaterialCardView cardView;
        public TableViewHolder(MaterialCardView cardView){
            super(cardView);
            this.cardView = cardView;
            cardView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            menu.setHeaderIcon(R.drawable.ic_menu);
            menu.add("Delete");
            menu.add("Rename");
        }
    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }

    public Integer getTablePosition() {
        return tableposition;
    }

    public void setTablePosition(Integer position) {
        this.tableposition = position;
    }
}
