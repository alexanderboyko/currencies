package boyko.alex.waluty;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * Created by Sasha on 31.01.2018.
 *
 * Activity for setting value range
 */

public class SettingsActivity extends AppCompatActivity {
    private EditText from, to;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        from = findViewById(R.id.from);
        to = findViewById(R.id.to);

        from.setText(String.valueOf(SharedPreferencesHelper.getBuyFrom()));
        to.setText(String.valueOf(SharedPreferencesHelper.getBuyTo()));

        from.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!from.getText().toString().isEmpty() && !to.getText().toString().isEmpty()) {
                    double fromValue = Double.valueOf(from.getText().toString());
                    double toValue = Double.valueOf(to.getText().toString());

                    //Za pierwszym razem to się nie zapisze, bo toValue będzie zero. A from przestawiasz w pierwszej kolejnośći.
                    if (fromValue > toValue) {
                        to.setError("Error");
                    } else {
                        to.setError(null);
                        SharedPreferencesHelper.setBuyFrom(fromValue);
                    }
                }
            }
        });

        //Ten TextWatcher jest dokładnie taki sam jak ten wyżej. Można by było wykorzystać jeden obiekt jako listener do dwóch EditTextów.
        to.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!from.getText().toString().isEmpty() && !to.getText().toString().isEmpty()) {
                    double fromValue = Double.valueOf(from.getText().toString());
                    double toValue = Double.valueOf(to.getText().toString());

                    if (fromValue > toValue) {
                        to.setError("Error");
                    } else {
                        to.setError(null);
                        SharedPreferencesHelper.setBuyTo(toValue);
                    }
                }
            }
        });
    }
}
