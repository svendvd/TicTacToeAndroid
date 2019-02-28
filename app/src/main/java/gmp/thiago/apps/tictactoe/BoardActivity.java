package gmp.thiago.apps.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class BoardActivity extends AppCompatActivity implements View.OnClickListener {
    private SharedPreferences prefs;
    private boolean darkTheme;

    private boolean userPlaying;

    private long userTotalTime;

    // Bindings
    @BindView(R.id.area_0_0)
    View area_0_0;
    @BindView(R.id.area_0_1)
    View area_0_1;
    @BindView(R.id.area_0_2)
    View area_0_2;
    @BindView(R.id.area_1_0)
    View area_1_0;
    @BindView(R.id.area_1_1)
    View area_1_1;
    @BindView(R.id.area_1_2)
    View area_1_2;
    @BindView(R.id.area_2_0)
    View area_2_0;
    @BindView(R.id.area_2_1)
    View area_2_1;
    @BindView(R.id.area_2_2)
    View area_2_2;
    @BindView(R.id.currentPlayerTv)
    TextView currentPlayerTv;
    @BindView(R.id.cronoTv)
    TextView cronoTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        darkTheme = prefs.getBoolean(getString(R.string.dark_theme_pref_key), false);
        setTheme(darkTheme ? R.style.DarkAppTheme : R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        ButterKnife.bind(this);

        area_0_0.setOnClickListener(this);
        area_0_1.setOnClickListener(this);
        area_0_2.setOnClickListener(this);
        area_1_0.setOnClickListener(this);
        area_1_1.setOnClickListener(this);
        area_1_2.setOnClickListener(this);
        area_2_0.setOnClickListener(this);
        area_2_1.setOnClickListener(this);
        area_2_2.setOnClickListener(this);

        userPlaying = true;

        handler.post(timerRunnable);
    }

    /*
     * By deliberate decision, Player will play X, while computer will play O
     */
    @Override
    public void onClick(View area) {
        if (userPlaying) {
            Drawable symbol = getResources().getDrawable(R.drawable.x_symbol);
            switch (area.getId()) {
                case R.id.area_0_0:
                    area_0_0.setBackground(symbol);
                    break;
                case R.id.area_0_1:
                    area_0_1.setBackground(symbol);
                    break;
                case R.id.area_0_2:
                    area_0_2.setBackground(symbol);
                    break;
                case R.id.area_1_0:
                    area_1_0.setBackground(symbol);
                    break;
                case R.id.area_1_1:
                    area_1_1.setBackground(symbol);
                    break;
                case R.id.area_1_2:
                    area_1_2.setBackground(symbol);
                    break;
                case R.id.area_2_0:
                    area_2_0.setBackground(symbol);
                    break;
                case R.id.area_2_1:
                    area_2_1.setBackground(symbol);
                    break;
                case R.id.area_2_2:
                    area_2_2.setBackground(symbol);
                    break;
                default:
                    break;
            }

            handler.removeCallbacks(timerRunnable);
           // userPlaying = false;
            // TODO: Disparar evento Computador
            // TODO: checar resultado
        }
    }

    final Handler handler = new Handler();
    private Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {

            cronoTv.setText(String.format("%02d:%02d:%02d",userTotalTime/3600,(userTotalTime%3600)/60,userTotalTime%60));

            userTotalTime++;

            handler.postDelayed(this, 1000);
        }
    };
}
