package boyko.alex.waluty;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //Musiałem sobie policzyć na kalkulatorze ile to jest. Najlepiej dopisać w komentarzu ile to jest,
    //albo zapisać to w takiej formie: 12 * 60 * 60 * 1000 i od razu widać, że to jest 12h.
    private final long PERIOD = 3600000 * 12;

    private RecyclerView recyclerView;
    private CurrencyRecyclerAdapter adapter;

    private LinearLayout title;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                launchSettingsActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        initViews();
        initRecyclerView();

        setElementsVisibility(false);
        makeRequest();
        launchService();
    }

    private void initViews() {
        //Do bindowania widoków najlepiej użyć jakieś biblioteki, która wyelimuje konieczność pisania zdębnego kodu (tzw. boilerplate code).
        //np. Butterknife albo Android annotations
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        title = findViewById(R.id.title);
    }

    private void initRecyclerView() {
        adapter = new CurrencyRecyclerAdapter(new ArrayList<Currency>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void makeRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://api.nbp.pl/api/exchangerates/tables/C";

        //Najlepiej swojego requesta napisać, żeby zwracał od razu listę obiektów typu Currency.
        JsonArrayRequest req = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        adapter.setCurrencies(parseResponse(response));
                        adapter.notifyDataSetChanged();

                        setElementsVisibility(true);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                setElementsVisibility(false);
            }
        });
        //Dobrą praktyką jest cancelowanie requestów w onStop. W dokumentacji Valley jest nawet to napisane, dodać tag do requestu i potem go cancelować.
        //Jeśli byś wywoływał requesty z Fragmentu, to po zamknięciu apki fragment.getActivity() mogłoby zwrócić null i wtedy byś miał NPE.

        queue.add(req);
    }

    private ArrayList<Currency> parseResponse(JSONArray response) {
        ArrayList<Currency> currencies = new ArrayList<>();
        try {
            //Do parsowania takich rzeczy służą biblioteki, np. Gson. Activity nie powinno się zajmować parsowaniem niczego.
            JSONArray currenciesJSONArray = response.getJSONObject(0).getJSONArray("rates");
            for (int i = 0; i < currenciesJSONArray.length(); i++) {
                JSONObject jsonObject = currenciesJSONArray.getJSONObject(i);
                Currency currency = new Currency();
                currency.setName(jsonObject.getString("currency"));
                currency.setCode(jsonObject.getString("code"));
                currency.setBuy(jsonObject.getDouble("bid"));
                currency.setSell(jsonObject.getDouble("ask"));
                currencies.add(currency);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return currencies;
    }

    private void setElementsVisibility(boolean isSuccessful) {
        if (isSuccessful) {
            recyclerView.setVisibility(View.VISIBLE);
            title.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void launchService(){
        ComponentName componentName = new ComponentName(this, CurrencyService.class);
        JobInfo jobInfo;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jobInfo = new JobInfo.Builder(11111, componentName)
                    .setMinimumLatency(PERIOD)//to jest tylko delay, dla api >= 24 nie ustawiasz w ogóle okresu!
                    .build();
        } else {
            jobInfo = new JobInfo.Builder(11111, componentName)
                    .setPeriodic(PERIOD)
                    .build();
        }
        //Do job info można by było dodać setPersisted(true) to po restarcie urządzenia serwis dalej by się odpalał.
        //W twoim rozwiązaniu po restarcie trzeba odpalić apkę, żeby znowu ustawił się ten serwis.
        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobInfo);
    }

    private void launchSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

}
