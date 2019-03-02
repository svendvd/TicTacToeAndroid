package gmp.thiago.apps.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultsActivity extends AppCompatActivity {
    private SharedPreferences prefs;
    private boolean darkTheme;
    private String userName;

    // View Bindings
    @BindView(R.id.result_tv)
    TextView resultTv;
    @BindView(R.id.totalTime_tv)
    TextView totalTimeTv;
    @BindView(R.id.playBtn)
    Button playAgainBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        darkTheme = prefs.getBoolean(getString(R.string.dark_theme_pref_key), false);
        setTheme(darkTheme ? R.style.DarkAppTheme : R.style.AppTheme);
        userName = prefs.getString(getString(R.string.player_name_pref_key), null);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        ButterKnife.bind(this);

        Intent incomingIntent = getIntent();
        GameUtils.Result result = (GameUtils.Result)incomingIntent.getSerializableExtra(getString(R.string.winner_key));
        long totalTime = incomingIntent.getLongExtra(getString(R.string.total_time_key), 0);

        switch (result) {
            case USER_WON:
                resultTv.setText(String.format(getString(R.string.result_user), userName==null ? "" : userName));
                totalTimeTv.setText(String.format("%02d:%02d.%03d",totalTime/60000,(totalTime/1000)%60,
                        totalTime%1000));
                break;
            case COMPUTER_WON:
                resultTv.setText(getString(R.string.result_computer));
                break;
            default:
                resultTv.setText(getString(R.string.result_tie));
        }

        playAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playIntent = new Intent(ResultsActivity.this, BoardActivity.class);
                startActivity(playIntent);
                finish();
            }
        });
    }
}
