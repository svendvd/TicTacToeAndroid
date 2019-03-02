package gmp.thiago.apps.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import gmp.thiago.apps.ai.ComputerAI;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class BoardActivity extends AppCompatActivity implements View.OnClickListener {
    private SharedPreferences prefs;
    private boolean darkTheme;
    private String playerName;

    private boolean userPlaying;
    private boolean gameFinished;
    private GameUtils.Result result;

    private long userTotalTime;

    private long startTime;

    private ArrayList<Integer> availableSpaces;
    private char[] positions;

    private static ThinkingDialog dialog;

    private Drawable xSymbol;
    private Drawable oSymbol;

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

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getString(R.string.action));
        registerReceiver(receiver, intentFilter);

        playerName = prefs.getString(getString(R.string.player_name_pref_key), null);
        dialog = new ThinkingDialog(this, darkTheme ? R.style.DarkDialog : R.style.LightDialog);

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

        xSymbol = getResources().getDrawable(R.drawable.x_symbol);
        oSymbol = getResources().getDrawable(R.drawable.o_symbol);

        if (null != savedInstanceState) {
            availableSpaces = savedInstanceState.getIntegerArrayList(getString(R.string.available_key));
            positions = savedInstanceState.getCharArray(getString(R.string.positions_key));

            userPlaying = savedInstanceState.getBoolean(getString(R.string.user_playing_key));
            userTotalTime = savedInstanceState.getLong(getString(R.string.total_time_key));
            gameFinished = savedInstanceState.getBoolean(getString(R.string.game_finished_key));

            area_0_0.setBackground(positions[0] == 'X' ? xSymbol : positions[0] == 'O' ? oSymbol : null);
            area_0_1.setBackground(positions[1] == 'X' ? xSymbol : positions[1] == 'O' ? oSymbol : null);
            area_0_2.setBackground(positions[2] == 'X' ? xSymbol : positions[2] == 'O' ? oSymbol : null);
            area_1_0.setBackground(positions[3] == 'X' ? xSymbol : positions[3] == 'O' ? oSymbol : null);
            area_1_1.setBackground(positions[4] == 'X' ? xSymbol : positions[4] == 'O' ? oSymbol : null);
            area_1_2.setBackground(positions[5] == 'X' ? xSymbol : positions[5] == 'O' ? oSymbol : null);
            area_2_0.setBackground(positions[6] == 'X' ? xSymbol : positions[6] == 'O' ? oSymbol : null);
            area_2_1.setBackground(positions[7] == 'X' ? xSymbol : positions[7] == 'O' ? oSymbol : null);
            area_2_2.setBackground(positions[8] == 'X' ? xSymbol : positions[8] == 'O' ? oSymbol : null);

            if (savedInstanceState.getBoolean(getString(R.string.dialog_showing_key))) {
                dialog.show();
            }

        } else {
            availableSpaces = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                availableSpaces.add(i);
            }
            positions = new char[9];
        }
        gameFinished = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Math.random() >= 0.5) {
            userPlaying = true;
            startTime = System.nanoTime();
            handler.post(timerRunnable);
            currentPlayerTv.setText(playerName);
        } else {
            // Computer Starts
            playComputer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        unregisterReceiver(receiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharArray(getString(R.string.positions_key), positions);
        outState.putIntegerArrayList(getString(R.string.available_key), availableSpaces);
        outState.putBoolean(getString(R.string.game_finished_key), gameFinished);
        outState.putBoolean(getString(R.string.user_playing_key), userPlaying);
        outState.putLong(getString(R.string.total_time_key), userTotalTime);
        outState.putBoolean(getString(R.string.dialog_showing_key), dialog.isShowing());
    }

    /*
     * By deliberate decision, Player will play X, while computer will play O
     *
     * onClick method handles just the game areas. Nothing else should hit here
     * Will only be handled whan user is playing. Clicks while computer is thinking won't
     * be handled.
     */
    @Override
    public void onClick(View area) {
        if (!userPlaying) return;
        if (area.getBackground() != null) return;
        switch (area.getId()) {
            case R.id.area_0_0:
                availableSpaces.remove(availableSpaces.indexOf(0));
                positions[0] = 'X';
                break;
            case R.id.area_0_1:
                availableSpaces.remove(availableSpaces.indexOf(1));
                positions[1] = 'X';
                break;
            case R.id.area_0_2:
                availableSpaces.remove(availableSpaces.indexOf(2));
                positions[2] = 'X';
                break;
            case R.id.area_1_0:
                availableSpaces.remove(availableSpaces.indexOf(3));
                positions[3] = 'X';
                break;
            case R.id.area_1_1:
                availableSpaces.remove(availableSpaces.indexOf(4));
                positions[4] = 'X';
                break;
            case R.id.area_1_2:
                availableSpaces.remove(availableSpaces.indexOf(5));
                positions[5] = 'X';
                break;
            case R.id.area_2_0:
                availableSpaces.remove(availableSpaces.indexOf(6));
                positions[6] = 'X';
                break;
            case R.id.area_2_1:
                availableSpaces.remove(availableSpaces.indexOf(7));
                positions[7] = 'X';
                break;
            case R.id.area_2_2:
                availableSpaces.remove(availableSpaces.indexOf(8));
                positions[8] = 'X';
                break;
        }
        drawSymbol(area, xSymbol);
        checkResult();
        if (!gameFinished) {
            userPlaying = false;
            playComputer();
        }
    }

    /**
     * playComputer calls the AsyncTask to get computer move.
     * Move was created as a separate library to be easily replaced.
     */
    private void playComputer() {
        currentPlayerTv.setText(R.string.computer_name);
        dialog.show();
        new ComputerPlay().execute(availableSpaces);
    }

    /**
     * Handles the response from the computer
     * @param computer Result from the AsyncTask
     */
    private void processComputerPlay(int computer) {
        dialog.dismiss();
        availableSpaces.remove(availableSpaces.indexOf(computer));
        positions[computer] = 'O';
        View chosenView = null;
        switch (computer) {
            case 0:
                chosenView = area_0_0;
                break;
            case 1:
                chosenView = area_0_1;
                break;
            case 2:
                chosenView = area_0_2;
                break;
            case 3:
                chosenView = area_1_0;
                break;
            case 4:
                chosenView = area_1_1;
                break;
            case 5:
               chosenView = area_1_2;
                break;
            case 6:
                chosenView = area_2_0;
                break;
            case 7:
                chosenView = area_2_1;
                break;
            case 8:
                chosenView = area_2_2;
                break;
            default:
                break;
        }
        drawSymbol(chosenView, oSymbol);

        checkResult();

        if (!gameFinished) {
            userPlaying = true;
            currentPlayerTv.setText(playerName);
            startTime = System.nanoTime();
            handler.post(timerRunnable);
        }
    }

    /**
     * Draws the symbol in the designated area, providing a fade-in animation
     * @param area
     * @param symbol
     */
    private void drawSymbol(View area, Drawable symbol) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(area, View.ALPHA, 0f, 1f);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (null != area) {
                    area.setBackground(symbol);
                }
            }
        });
        animator.setDuration(750);
        animator.start();
        if (userPlaying) {
            handler.removeCallbacks(timerRunnable);
        }
    }

    /**
     * Checks all the possibilities to check if there's a winner, a tie or if game continues.
     */
    private void checkResult() {
        // There's no need if less than 5 moves were made. No one can win with 2 moves
        if (availableSpaces.size()>4) return;

        // Check all possibilities
        // Horizontals
        if (positions[0] == positions[1] && positions[1] == positions[2]) {
            if (positions[0] == 'X') {
                result = GameUtils.Result.USER_WON;
                gameFinished = true;
            } else if (positions[0] == 'O') {
                result = GameUtils.Result.COMPUTER_WON;
                gameFinished = true;
            }
        }
        if (positions[3] == positions[4] && positions[4] == positions[5]) {
            if (positions[3] == 'X') {
                result = GameUtils.Result.USER_WON;
                gameFinished = true;
            } else if (positions[3] == 'O') {
                result = GameUtils.Result.COMPUTER_WON;
                gameFinished = true;
            }
        }
        if (positions[6] == positions[7] && positions[7] == positions[8]) {
            if (positions[6] == 'X') {
                result = GameUtils.Result.USER_WON;
                gameFinished = true;
            } else if (positions[6] == 'O') {
                result = GameUtils.Result.COMPUTER_WON;
                gameFinished = true;
            }
        }

        // Verticals
        if (positions[0] == positions[3] && positions[3] == positions[6]) {
            if (positions[0] == 'X') {
                result = GameUtils.Result.USER_WON;
                gameFinished = true;
            } else if (positions[0] == 'O') {
                result = GameUtils.Result.COMPUTER_WON;
                gameFinished = true;
            }
        }
        if (positions[1] == positions[4] && positions[4] == positions[7]) {
            if (positions[1] == 'X') {
                result = GameUtils.Result.USER_WON;
                gameFinished = true;
            } else if (positions[1] == 'O') {
                result = GameUtils.Result.COMPUTER_WON;
                gameFinished = true;
            }
        }
        if (positions[2] == positions[5] && positions[5] == positions[8]) {
            if (positions[2] == 'X') {
                result = GameUtils.Result.USER_WON;
                gameFinished = true;
            } else if (positions[2] == 'O') {
                result = GameUtils.Result.COMPUTER_WON;
                gameFinished = true;
            }
        }

        // Diagonals
        if (positions[0] == positions[4] && positions[4] == positions[8]) {
            if (positions[0] == 'X') {
                result = GameUtils.Result.USER_WON;
                gameFinished = true;
            } else if (positions[0] == 'O') {
                result = GameUtils.Result.COMPUTER_WON;
                gameFinished = true;
            }
        }
        if (positions[2] == positions[4] && positions[4] == positions[6]) {
            if (positions[2] == 'X') {
                result = GameUtils.Result.USER_WON;
                gameFinished = true;
            } else if (positions[0] == 'O') {
                result = GameUtils.Result.COMPUTER_WON;
                gameFinished = true;
            }
        }

        // In case no winner and no more spaces to play, call it a Tie
        if (!gameFinished && availableSpaces.size() == 0) {
            result = GameUtils.Result.TIE;
            gameFinished = true;
        }

        if (gameFinished) {
            handler.removeCallbacks(timerRunnable);

            Intent resultIntent = new Intent(this, ResultsActivity.class);
            resultIntent.putExtra(getString(R.string.winner_key), result);
            resultIntent.putExtra(getString(R.string.total_time_key), userTotalTime);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(resultIntent,
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            } else {
                startActivity(resultIntent);
            }
            finish();
        }
    }

    /**
     * Handler for the user's play time
     */
    final static Handler handler = new Handler();
    private Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            cronoTv.setText(String.format(getString(R.string.time_mask),
                            userTotalTime/60000,(userTotalTime/1000)%60, userTotalTime%1000));

            userTotalTime += (System.nanoTime() - startTime)/1000000;

            startTime = System.nanoTime();
            handler.postDelayed(this, 10);
        }
    };

    /**
     * Receiver for the computer selection.
     * Opted to use a BroadcastReceiver to avoid problems during rotation
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(getString(R.string.action))) {
                processComputerPlay(intent.getIntExtra(getString(R.string.computer_move_key), -1));
            }
        }
    };

    /**
     * AsyncTask that requests the selection for the Computer.
     * This library expects to receive the available spaces.
     */
    private class ComputerPlay extends AsyncTask<ArrayList<Integer>, Void, Integer> {

        @Override
        protected Integer doInBackground(ArrayList<Integer>... arrayLists) {
            return ComputerAI.getNextMove(arrayLists[0]);
        }

        @Override
        protected void onPostExecute(Integer result) {
            //processComputerPlay(result);
            Intent compIntent = new Intent();
            compIntent.setAction(getString(R.string.action));
            compIntent.putExtra(getString(R.string.computer_move_key), result);
            sendBroadcast(compIntent);
        }
    }
}
