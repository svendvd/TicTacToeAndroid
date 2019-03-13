package gmp.thiago.apps.ai;

import java.util.ArrayList;

import gmp.thiago.apps.ai.model.AreaState;

public class ComputerAI {

    public static int getNextMove(AreaState[] areaStates) {
        ArrayList<Integer> availableSpaces = new ArrayList<>();
        for (int i = 0; i < areaStates.length; i++) {
            if (areaStates[i] == AreaState.NONE){
               availableSpaces.add(i);
            }
        }

        if (availableSpaces.size() == 0) return -1;

        try {
            Thread.sleep((long)(Math.random()*5000)+3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return (availableSpaces.get((int)(Math.random()*availableSpaces.size())));
    }

}
