package gmp.thiago.apps.tictactoe;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import gmp.thiago.apps.tictactoe.model.Result;

public class ResultsActivity extends AppCompatActivity {

    // View Bindings
    @BindView(R.id.result_tv)
    TextView resultTv;
    @BindView(R.id.totalTime_tv)
    TextView totalTimeTv;
    @BindView(R.id.playBtn)
    Button playAgainBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean useDarkTheme = prefs.getBoolean(getString(R.string.dark_theme_pref_key), false);
        String userName = prefs.getString(getString(R.string.player_name_pref_key), null);

        setTheme(useDarkTheme ? R.style.DarkAppTheme : R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        ButterKnife.bind(this);

        Intent incomingIntent = getIntent();
        Result result = (Result) incomingIntent.getSerializableExtra(BoardActivity.WINNER_KEY);
        long totalTime = incomingIntent.getLongExtra(BoardActivity.TOTAL_TIME_KEY, 0);

        switch (result) {
            case X_WON:
                resultTv.setText(String.format(getString(R.string.result_user), userName));
                totalTimeTv.setText(String.format("%02d:%02d.%03d", totalTime / 60000, (totalTime / 1000) % 60,
                  totalTime % 1000));
                break;
            case O_WON:
                resultTv.setText(getString(R.string.result_computer));
                break;
            default:
                resultTv.setText(getString(R.string.result_tie));
        }

        playAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playIntent = new Intent(ResultsActivity.this, BoardActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(playIntent,
                      ActivityOptions.makeSceneTransitionAnimation(ResultsActivity.this).toBundle());
                } else {
                    startActivity(playIntent);
                }
                finish();
            }
        });
    }
}
