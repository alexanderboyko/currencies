package boyko.alex.waluty;

import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.R.attr.id;

/**
 * Created by Sasha on 31.01.2018.
 *
 * Service check USD course every
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CurrencyService extends JobService {

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://api.nbp.pl/api/exchangerates/rates/c/usd/today/";

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("rates");
                            double from = array.getJSONObject(0).getDouble("bid");
                            if(!(from > SharedPreferencesHelper.getBuyFrom() && from < SharedPreferencesHelper.getBuyTo())){
                                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(id, new NotificationCompat.Builder(getApplicationContext(), "some channel")
                                        .setContentTitle("Course isn't normal")
                                        .setContentText("Check it in app")
                                        .setSmallIcon(R.drawable.avd_hide_password_1)
                                        .build());
                                jobFinished(jobParameters, true);
                            }else {
                                jobFinished(jobParameters, true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            jobFinished(jobParameters, true);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        jobFinished(jobParameters, true);
                    }
                });

        queue.add(req);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }

}
