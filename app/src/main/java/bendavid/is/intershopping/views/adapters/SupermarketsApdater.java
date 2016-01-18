package bendavid.is.intershopping.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import bendavid.is.intershopping.R;
import bendavid.is.intershopping.models.entities.ShoppingList;
import bendavid.is.intershopping.models.entities.Supermarket;
import bendavid.is.intershopping.utils.ItemTouchHelperAdapter;
import bendavid.is.intershopping.utils.ItemTouchHelperViewHolder;
import bendavid.is.intershopping.utils.SupermarketClickListener;

public class SupermarketsApdater extends RecyclerView.Adapter<SupermarketsApdater.ViewHolder>
        implements ItemTouchHelperAdapter {

    private Context context;
    private List<Supermarket> supermarkets;
    private SupermarketClickListener listener;

    public SupermarketsApdater(Context context, List<Supermarket> supermarkets, SupermarketClickListener listener) {
        this.context = context;
        this.supermarkets = supermarkets;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_supermarkets_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.bind(supermarkets.get(position));
    }

    @Override
    public int getItemCount() {
        return supermarkets == null ? 0 : supermarkets.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(supermarkets, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        if (ShoppingList.find(ShoppingList.class, "Supermarket = ?",
                String.valueOf(supermarkets.get(position).getId())).isEmpty()) {
            supermarkets.get(position).delete();
            supermarkets.remove(position);
            notifyItemRemoved(position);
        } else {
            notifyDataSetChanged();
            Toast.makeText(context, "The supermarket has shopping lists", Toast.LENGTH_SHORT).show();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, ItemTouchHelperViewHolder {

        private CardView card;
        public TextView letter;
        public TextView supermarketName;
        public TextView location;
        private Drawable background;

        private SupermarketClickListener listener;

        public ViewHolder(View itemView, SupermarketClickListener listener) {
            super(itemView);

            card = (CardView) itemView.findViewById(R.id.cv);
            letter = (TextView) itemView.findViewById(R.id.letter_sm);
            supermarketName = (TextView) itemView.findViewById(R.id.supermarket_name);
            location = (TextView) itemView.findViewById(R.id.location);

            background = card.getBackground();

            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        public void bind(Supermarket supermarket) {
            letter.setText(supermarket.toString().substring(0, 1).toUpperCase());
            supermarketName.setText(supermarket.toString());
            location.setText(supermarket.getAddress());
        }

        @Override
        public void onClick(View v) {
            listener.onClick(supermarkets.get(getAdapterPosition()));
        }

        @Override
        public void onItemSelected() {
            card.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            card.setBackground(background);
        }
    }
}
