package gmp.thiago.apps.tictactoe;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences prefs;
    private boolean darkTheme;

    private String playerName;

    // View Bindings
    @BindView(R.id.greetings)
    TextView greetingsTv;
    @BindView(R.id.playerName)
    EditText playerNameEt;
    @BindView(R.id.start_game_btn)
    Button startGameBtn;
    @BindView(R.id.change_name_btn)
    Button changeNameBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        darkTheme = prefs.getBoolean(getString(R.string.dark_theme_pref_key), false);
        setTheme(darkTheme ? R.style.DarkAppTheme : R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        playerName = prefs.getString(getString(R.string.player_name_pref_key), null);

        if (null != playerName) {
            greetingsTv.setVisibility(View.VISIBLE);
            greetingsTv.setText(String.format(getString(R.string.greetings), playerName));
            playerNameEt.setVisibility(View.INVISIBLE);
            changeNameBtn.setVisibility(View.VISIBLE);
        } else {
            greetingsTv.setVisibility(View.INVISIBLE);
            playerNameEt.setVisibility(View.VISIBLE);
            changeNameBtn.setVisibility(View.INVISIBLE);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        startGameBtn.setOnClickListener(this);
        changeNameBtn.setOnClickListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings_menu, menu);

        menu.findItem(darkTheme ? R.id.dark_theme : R.id.light_theme).setChecked(true);
        return true;
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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        startActivity(refreshIntent,
                                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                    } else {
                        startActivity(refreshIntent);
                    }
                    finish();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        SharedPreferences.Editor editor = prefs.edit();
        InputMethodManager inputMethodService = (InputMethodManager)   getSystemService(INPUT_METHOD_SERVICE);

        switch (v.getId()) {
            case R.id.change_name_btn:
                playerName = null;
                editor.remove(getString(R.string.player_name_pref_key));
                editor.commit();
                greetingsTv.setVisibility(View.INVISIBLE);
                playerNameEt.setVisibility(View.VISIBLE);
                changeNameBtn.setVisibility(View.INVISIBLE);
                if (inputMethodService != null) {
                    inputMethodService.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
                break;
            case R.id.start_game_btn:
                if (null == playerName || playerName.isEmpty()) {
                    playerName = playerNameEt.getText().toString().trim();
                }
                if (!playerName.isEmpty()) {
                    editor.putString(getString(R.string.player_name_pref_key), playerName);
                    editor.commit();

                    if (inputMethodService != null) {
                        inputMethodService.hideSoftInputFromWindow(playerNameEt.getWindowToken(), 0);
                    }

                    Intent gameIntent = new Intent(this, BoardActivity.class);
                    startActivity(gameIntent);
                    finish();
                } else {
                    Toast.makeText(this,
                            getString(R.string.no_name_error), Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }
}
