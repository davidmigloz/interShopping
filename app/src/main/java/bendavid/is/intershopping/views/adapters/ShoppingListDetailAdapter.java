package bendavid.is.intershopping.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import bendavid.is.intershopping.R;
import bendavid.is.intershopping.models.entities.ListItem;
import bendavid.is.intershopping.utils.ItemTouchHelperAdapter;
import bendavid.is.intershopping.utils.ItemTouchHelperViewHolder;
import bendavid.is.intershopping.utils.OnChangeShoppingListDetailsListener;
import bendavid.is.intershopping.utils.ShoppingListDetailClickListener;

public class ShoppingListDetailAdapter extends RecyclerView.Adapter<ShoppingListDetailAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {

    private Context context;
    private List<ListItem> itemslist;
    private ShoppingListDetailClickListener clickListener;
    private OnChangeShoppingListDetailsListener changeListener;

    public ShoppingListDetailAdapter(Context context, List<ListItem> itemslist,
                                     ShoppingListDetailClickListener clickListener,
                                     OnChangeShoppingListDetailsListener changeListener) {
        this.context = context;
        this.itemslist = itemslist;
        this.clickListener = clickListener;
        this.changeListener = changeListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_shopping_list_detail, parent, false);
        return new ViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.bind(itemslist.get(position));
    }

    @Override
    public int getItemCount() {
        return itemslist == null ? 0 : itemslist.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(itemslist, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        ListItem li = itemslist.remove(position);
        li.delete();
        notifyItemRemoved(position);
        changeListener.onChangeShoppingListDetails();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, ItemTouchHelperViewHolder {

        public ImageView checkIcon;
        public TextView itemName;
        public ImageView cartIcon;
        public TextView itemPrice;
        private Drawable background;

        private ShoppingListDetailClickListener listener;

        public ViewHolder(View itemView, ShoppingListDetailClickListener listener) {
            super(itemView);

            checkIcon = (ImageView) itemView.findViewById(R.id.item_icon);
            itemName = (TextView) itemView.findViewById(R.id.item_name);
            cartIcon = (ImageView) itemView.findViewById(R.id.add_cart);
            itemPrice = (TextView) itemView.findViewById(R.id.item_price);

            background = itemView.getBackground();

            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        public void bind(ListItem listItem) {
            // Icon
            Drawable icon;
            if (listItem.isPurchased()) {
                // If it's already bought
                icon = ContextCompat.getDrawable(context, R.drawable.ic_check_box_1);
                itemName.setPaintFlags(itemName.getPaintFlags()
                        | Paint.STRIKE_THRU_TEXT_FLAG); // Cross text
                cartIcon.setVisibility(View.GONE);
                itemPrice.setText(context.getString(R.string.price_eur, listItem.getTotalPrice()));
                itemPrice.setVisibility(View.VISIBLE); // Show price
            } else {
                // If it's not bought
                icon = ContextCompat.getDrawable(context, R.drawable.ic_check_box_0);
            }
            checkIcon.setImageDrawable(icon);
            // Item name
            itemName.setText(context.getString(R.string.name_translation,
                    listItem.getName(), listItem.getTranslation()));
        }

        @Override
        public void onClick(View v) {
            ListItem listItem = itemslist.get(getAdapterPosition());
            if (listItem.isPurchased()) {
                // Reset it
                Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_check_box_0);
                checkIcon.setImageDrawable(icon);
                itemName.setPaintFlags(itemName.getPaintFlags()
                        & (~Paint.STRIKE_THRU_TEXT_FLAG));
                itemPrice.setVisibility(View.GONE); // Hide price
                cartIcon.setVisibility(View.VISIBLE); // Show cart
            } else {
                // Buy it
                Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_check_box_1);
                checkIcon.setImageDrawable(icon);
                itemName.setPaintFlags(itemName.getPaintFlags() |
                        Paint.STRIKE_THRU_TEXT_FLAG);
                cartIcon.setVisibility(View.GONE);
                itemPrice.setText(context.getString(R.string.price_eur, 0.0));
                itemPrice.setVisibility(View.VISIBLE);
            }
            listener.onClick(listItem);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackground(background);
        }
    }
}