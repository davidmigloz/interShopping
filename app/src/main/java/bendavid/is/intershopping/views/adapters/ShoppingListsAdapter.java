package bendavid.is.intershopping.views.adapters;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;

import bendavid.is.intershopping.R;
import bendavid.is.intershopping.models.entities.ShoppingList;
import bendavid.is.intershopping.utils.ItemTouchHelperAdapter;
import bendavid.is.intershopping.utils.ItemTouchHelperViewHolder;
import bendavid.is.intershopping.utils.ShoppingListClickListener;

public class ShoppingListsAdapter extends RecyclerView.Adapter<ShoppingListsAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {

    private List<ShoppingList> shoppingLists;
    private ShoppingListClickListener listener;

    public ShoppingListsAdapter(List<ShoppingList> shoppingLists,
                                ShoppingListClickListener listener) {
        this.listener = listener;
        this.shoppingLists = shoppingLists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_shopping_lists_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.bind(shoppingLists.get(position));
    }

    @Override
    public int getItemCount() {
        return shoppingLists == null ? 0 : shoppingLists.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(shoppingLists, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        ShoppingList s = shoppingLists.remove(position);
        s.delete();
        notifyItemRemoved(position);
    }

    public void updateData(List<ShoppingList> shoppingLists) {
        this.shoppingLists = shoppingLists;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, ItemTouchHelperViewHolder {

        public CardView card;
        public ImageView icon;
        public TextView itemName;
        public TextView supermarketName;
        public TextView boughtItems;
        public TextView totalPrice;
        private Drawable background;

        private ShoppingListClickListener listener;

        public ViewHolder(View itemView, ShoppingListClickListener listener) {
            super(itemView);

            card = (CardView) itemView.findViewById(R.id.cv);
            itemName = (TextView) itemView.findViewById(R.id.item_name);
            supermarketName = (TextView) itemView.findViewById(R.id.supermarket_name);
            boughtItems = (TextView) itemView.findViewById(R.id.bought_items);
            totalPrice = (TextView) itemView.findViewById(R.id.total_price);

            background = card.getBackground();

            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        public void bind(ShoppingList shoppingList) {
            itemName.setText(shoppingList.toString());
            supermarketName.setText(shoppingList.getSupermarked().toString());
            boughtItems.setText(shoppingList.getNumItemsBought() +
                    "/" + shoppingList.getNumItems());
            NumberFormat nf = new DecimalFormat("#,###.##€");
            totalPrice.setText(nf.format(shoppingList.getTotalPrice()));
        }

        @Override
        public void onClick(View v) {
            listener.onClick(shoppingLists.get(getAdapterPosition()));
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