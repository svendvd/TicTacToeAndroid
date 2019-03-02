package gmp.thiago.apps.ai;

import java.util.ArrayList;

public class ComputerAI {

    public static int getNextMove(ArrayList<Integer> availableSpaces) {
        if (availableSpaces.size() == 0) return -1;

        try {
            Thread.sleep((long)(Math.random()*5000)+3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return (availableSpaces.get((int)(Math.random()*availableSpaces.size())));
    }

}
