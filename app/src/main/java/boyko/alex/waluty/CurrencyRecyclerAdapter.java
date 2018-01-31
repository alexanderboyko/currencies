package boyko.alex.waluty;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Sasha on 31.01.2018.
 * <p>
 * Currencies list adapter
 */

class CurrencyRecyclerAdapter extends RecyclerView.Adapter<CurrencyRecyclerAdapter.CurrencyHolder> {
    private ArrayList<Currency> currencies;

    CurrencyRecyclerAdapter(ArrayList<Currency> currencies){
        this.currencies = currencies;
    }

    void setCurrencies(ArrayList<Currency> currencies){
        this.currencies = currencies;
    }

    @Override
    public CurrencyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CurrencyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_currency, parent, false));
    }

    @Override
    public void onBindViewHolder(CurrencyHolder holder, int position) {
        Currency currency = currencies.get(position);

        holder.currencyCode.setText(currency.getCode());
        holder.currencyName.setText(currency.getName());
        holder.buy.setText(String.valueOf(currency.getBuy()));
        holder.sell.setText(String.valueOf(currency.getSell()));
    }

    @Override
    public int getItemCount() {
        return currencies.size();
    }

    class CurrencyHolder extends RecyclerView.ViewHolder {
        TextView currencyCode, currencyName, buy, sell;

        CurrencyHolder(View itemView) {
            super(itemView);
            currencyCode = itemView.findViewById(R.id.item_currency_code);
            currencyName = itemView.findViewById(R.id.item_currency_name);
            buy = itemView.findViewById(R.id.item_buy);
            sell = itemView.findViewById(R.id.item_sell);
        }
    }
}
