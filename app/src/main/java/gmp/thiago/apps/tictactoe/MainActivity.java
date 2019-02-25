package gmp.thiago.apps.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private Boolean darkTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        darkTheme = prefs.getBoolean(getString(R.string.dark_theme_pref_key), false);
        setTheme(darkTheme ? R.style.DarkAppTheme : R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        if (null != menuInflater) {
            menuInflater.inflate(R.menu.settings_menu, menu);

            if (darkTheme) {
                menu.getItem(0).setChecked(true);
            } else {
                menu.getItem(1).setChecked(true);
            }
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!item.isChecked()) {
            SharedPreferences.Editor editor = prefs.edit();
            switch (item.getItemId()) {
                case R.id.dark_theme:
                case R.id.light_theme:
                    editor.putBoolean(getString(R.string.dark_theme_pref_key), item.getItemId() == R.id.dark_theme);
                    editor.commit();
                    item.setChecked(true);

                    Intent refreshIntent = new Intent(this, MainActivity.class);
                    startActivity(refreshIntent);
                    finish();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }


        }
        return super.onOptionsItemSelected(item);
    }
}
