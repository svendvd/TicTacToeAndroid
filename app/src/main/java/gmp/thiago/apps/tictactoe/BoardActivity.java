package gmp.thiago.apps.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import gmp.thiago.apps.ai.ComputerAI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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

        playerName = prefs.getString(getString(R.string.player_name_pref_key), null);

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

        availableSpaces = new ArrayList<>();
        for (int i = 0; i< 9; i++) {
            availableSpaces.add(i);
        }
        positions = new char[9];
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

    private void playComputer() {
        currentPlayerTv.setText(R.string.computer_name);
        new ComputerPlay().execute(availableSpaces);
    }

    private void processComputerPlay(int computer) {
        availableSpaces.remove(availableSpaces.indexOf(computer));
        positions[computer] = 'O';
        switch (computer) {
            case 0:
                drawSymbol(area_0_0);
                break;
            case 1:
                drawSymbol(area_0_1);
                break;
            case 2:
                drawSymbol(area_0_2);
                break;
            case 3:
                drawSymbol(area_1_0);
                break;
            case 4:
                drawSymbol(area_1_1);
                break;
            case 5:
                drawSymbol(area_1_2);
                break;
            case 6:
                drawSymbol(area_2_0);
                break;
            case 7:
                drawSymbol(area_2_1);
                break;
            case 8:
                drawSymbol(area_2_2);
                break;
            default:
                break;
        }
        checkResult();

        if (!gameFinished) {
            userPlaying = true;
            currentPlayerTv.setText(playerName);
            startTime = System.nanoTime();
            handler.post(timerRunnable);
        }
    }

    /*
     * By deliberate decision, Player will play X, while computer will play O
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
        drawSymbol(area);
        checkResult();
        if (!gameFinished) {
            userPlaying = false;
            playComputer();
        }
    }

    private void drawSymbol(View area) {
        Drawable symbol;
        if (userPlaying) {
            symbol = getResources().getDrawable(R.drawable.x_symbol);
            handler.removeCallbacks(timerRunnable);
        } else {
            symbol = getResources().getDrawable(R.drawable.o_symbol);
        }
        area.setBackground(symbol);
    }

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

            startActivity(resultIntent);
            finish();
        }
    }

    final Handler handler = new Handler();
    private Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            cronoTv.setText(String.format("%02d:%02d.%03d",userTotalTime/60000,(userTotalTime/1000)%60,
                                                            userTotalTime%1000));

            userTotalTime += (System.nanoTime() - startTime)/1000000;

            startTime = System.nanoTime();
            handler.postDelayed(this, 10);
        }
    };

    private class ComputerPlay extends AsyncTask<ArrayList<Integer>, Void, Integer> {

        @Override
        protected Integer doInBackground(ArrayList<Integer>... arrayLists) {
            return ComputerAI.getNextMove(arrayLists[0]);
        }

        @Override
        protected void onPostExecute(Integer result) {
            processComputerPlay(result);
        }
    }
}
