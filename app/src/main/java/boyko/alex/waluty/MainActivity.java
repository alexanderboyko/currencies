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

        queue.add(req);
    }

    private ArrayList<Currency> parseResponse(JSONArray response) {
        ArrayList<Currency> currencies = new ArrayList<>();
        try {
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
                    .setMinimumLatency(PERIOD)
                    .build();
        } else {
            jobInfo = new JobInfo.Builder(11111, componentName)
                    .setPeriodic(PERIOD)
                    .build();
        }
        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobInfo);
    }

    private void launchSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

}
