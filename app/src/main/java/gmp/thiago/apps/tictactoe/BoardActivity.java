package gmp.thiago.apps.tictactoe;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import gmp.thiago.apps.ai.model.AreaState;
import gmp.thiago.apps.tictactoe.model.Result;
import gmp.thiago.apps.tictactoe.utils.AIPlayer;
import gmp.thiago.apps.tictactoe.utils.GameState;
import gmp.thiago.apps.tictactoe.view.AreaView;
import gmp.thiago.apps.tictactoe.view.TimerView;

public class BoardActivity extends AppCompatActivity implements View.OnClickListener, GameState.Callback, AIPlayer.Callback {
    private static final int ROW_COUNT = 3;
    private static final int COLUMN_COUNT = 3;
    private static final int COUNT_TO_WIN = 3;

    public static String WINNER_KEY = "WINNER_KEY";
    public static String TOTAL_TIME_KEY = "TOTAL_TIME_KEY";

    private String playerName;

    private ThinkingDialog dialog;

    private GameState gameState;

    private AIPlayer computerAI;

    AreaView[][] areaViews = new AreaView[3][3];

    //View Bindings
    @BindView(R.id.currentPlayerTv)
    TextView currentPlayerTextView;
    @BindView(R.id.cronoView)
    TimerView timerView;

    // TODO: consider to create this fields by code.
    private static int[][] areaViewIds = new int[][]{
      new int[]{R.id.area_0_0, R.id.area_0_1, R.id.area_0_2},
      new int[]{R.id.area_1_0, R.id.area_1_1, R.id.area_1_2},
      new int[]{R.id.area_2_0, R.id.area_2_1, R.id.area_2_2},
    };

    private void bindAreaViews() {
        for (int y = 0; y < ROW_COUNT; y++) {
            for (int x = 0; x < COLUMN_COUNT; x++) {
                areaViews[x][y] = findViewById(areaViewIds[x][y]);
                areaViews[x][y].setOnClickListener(this);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        playerName = prefs.getString(getString(R.string.player_name_pref_key), null);
        boolean useDarkTheme = prefs.getBoolean(getString(R.string.dark_theme_pref_key), false);

        setTheme(useDarkTheme ? R.style.DarkAppTheme : R.style.AppTheme);
        setContentView(R.layout.activity_board);

        gameState = new GameState(ROW_COUNT, COLUMN_COUNT, COUNT_TO_WIN, this);

        dialog = new ThinkingDialog(this, useDarkTheme ? R.style.DarkDialog : R.style.LightDialog);

        ButterKnife.bind(this);
        bindAreaViews();

        computerAI = new AIPlayer(this,this);

        if (savedInstanceState != null) {
            gameState.onRestoreInstanceState(savedInstanceState);
            updateUiFromGameState();
        } else {
            gameState.randomSelectPlayer();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        gameState.onSaveInstanceState(outState);
    }

    public void updateUiFromGameState() {
        for (int y = 0; y < gameState.getRowCount(); y++) {
            for (int x = 0; x < gameState.getColumnCount(); x++) {
                areaViews[x][y].setState(gameState.getStateAt(x, y));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        computerAI.register();
        if (gameState.getCurrentPlayer() == GameState.Player.USER) {
            timerView.start();
        } else {
            dialog.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        computerAI.unregister();
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onClick(View area) {
        if (gameState.getCurrentPlayer() == GameState.Player.USER) {
            // TODO: consider to create this fields by code, and get this info from the view itself.
            for (int y = 0; y < ROW_COUNT; y++) {
                for (int x = 0; x < COLUMN_COUNT; x++) {
                    if (areaViewIds[x][y] == area.getId()) {
                        if (gameState.choose(x, y)) {
                            timerView.stop();
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onGameFinished(Result result) {
        timerView.stop();

        Intent resultIntent = new Intent(this, ResultsActivity.class);
        resultIntent.putExtra(WINNER_KEY, result);
        resultIntent.putExtra(TOTAL_TIME_KEY, timerView.getTotalTimeInMilliseconds());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(
              resultIntent,
              ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            );
        } else {
            startActivity(resultIntent);
        }
        finish();
    }

    @Override
    @UiThread
    public void onStateChanged(int x, int y, AreaState state) {
        areaViews[x][y].animateState(state);
    }

    @Override
    public void onPlayerChanged(GameState.Player currentPlayer) {
        if (currentPlayer == GameState.Player.COMPUTER) {
            currentPlayerTextView.setText(R.string.computer_name);
            computerAI.makeMove(gameState.getAreaStates());
        } else {
            currentPlayerTextView.setText(playerName);
            timerView.start();
        }
    }

    @Override
    @UiThread
    public void onAIThinkingStart() {
        currentPlayerTextView.setText(R.string.computer_name);
        dialog.show();
    }


    @Override
    @UiThread
    public void onAIThinkingResult(int aiChoose) {
        if (gameState.getCurrentPlayer() == GameState.Player.COMPUTER) {
            dialog.dismiss();
            int x = aiChoose % ROW_COUNT;
            int y = aiChoose / COLUMN_COUNT;
            gameState.choose(x, y);
        }
    }

    @Override
    @UiThread
    public void onAIThinkingError() {
        // TODO: Better error handling.
        Toast.makeText(this.getApplicationContext(), R.string.computer_thinking_error, Toast.LENGTH_LONG).show();
        finish();
    }


}
