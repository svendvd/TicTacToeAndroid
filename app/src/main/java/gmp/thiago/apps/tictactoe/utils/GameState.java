package gmp.thiago.apps.tictactoe.model;

import android.os.Bundle;

import gmp.thiago.apps.ai.model.AreaState;

public class GameState {

    private static final String POSITIONS_KEY = "positions_key";

    private enum SearchDirection {
        RIGHT,
        RIGHT_BOTTOM,
        BOTTOM
    }

    private AreaState[] areaStates;
    private int rowCount;
    private int columnCount;
    private int countToWin;

    private Callback callback;

    public GameState(int rowCount, int columnCount, int countToWin, Callback callback) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.countToWin = countToWin;
        this.callback = callback;
        this.areaStates = new AreaState[rowCount * columnCount];
        for (int i = 0; i < areaStates.length; i++) {
            this.areaStates[i] = AreaState.NONE;
        }
    }

    private boolean hasSameSateInDirection(int x, int y, AreaState compareSate, SearchDirection searchDirection) {
        switch (searchDirection) {
            case RIGHT:
                if (x + countToWin <= rowCount) {
                    for (int offset = 0; offset < countToWin; offset++) {
                        if (getStateAt(x + offset, y) != compareSate) return false;
                    }
                    return true;
                } else return false;
            case BOTTOM:
                if (y + countToWin <= columnCount) {
                    for (int offset = 0; offset < countToWin; offset++) {
                        if (getStateAt(x, y + offset) != compareSate) return false;
                    }
                    return true;
                } else return false;
            case RIGHT_BOTTOM:
                if (x + countToWin <= rowCount && y + countToWin <= columnCount) {
                    for (int offset = 0; offset < countToWin; offset++) {
                        if (getStateAt(x + offset, y + offset) != compareSate) return false;
                    }
                    return true;
                } else return false;

            default: throw new RuntimeException("Direction "+searchDirection+" needs to be implemented");
        }
    }


    private boolean hasWon(int x, int y) {
        AreaState compareSate = getStateAt(x, y);
        return hasSameSateInDirection(x, y, compareSate, SearchDirection.RIGHT)
          || hasSameSateInDirection(x, y, compareSate, SearchDirection.RIGHT_BOTTOM)
          || hasSameSateInDirection(x, y, compareSate, SearchDirection.BOTTOM);
    }

    private void checkGameState() {
        Result result = Result.TIE;
        for (int y = 0; y < rowCount; y++) {
            for (int x = 0; x < columnCount; x++) {
                AreaState areaState = getStateAt(x, y);
                if (areaState == AreaState.NONE) {
                    result = Result.NONE;
                } else if (hasWon(x, y)) {
                    if (areaState == AreaState.O) {
                        result = Result.COMPUTER_WON;
                    } else {
                        result = Result.USER_WON;
                    }
                    break;
                }
            }
        }

        if (result != Result.NONE) {
            callback.onGameFinished(result);
        }
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void load(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            areaStates = (AreaState[]) savedInstanceState.getSerializable(POSITIONS_KEY);
        }
    }

    public void save(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(POSITIONS_KEY, areaStates);
    }

    public AreaState getStateAt(int index) {
        return areaStates[index];
    }

    public AreaState getStateAt(int x, int y) {
        return areaStates[ x + y * columnCount];
    }

    public void setStateAt(int x, int y, AreaState state) {
        areaStates[x + y * columnCount] = state;
        checkGameState();
    }

    public interface Callback {
        void onGameFinished(Result result);
        void onStateChanged(int x, int y, AreaState state);
    }
}
