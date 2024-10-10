package Drones;

import CommonUtils.BetterHashTable;
import CommonUtils.Interfaces.BetterQueue;
import CommonUtils.MinHeap;
import Drones.Interfaces.BetterCleanSwordManagerInterface;
import Items.Sword;
import Items.Types.SwordType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Manages everything regarding the cleaning of swords in our game.  Will be integrated with
 * the other drone classes
 */
public class BetterCleanSwordManager implements BetterCleanSwordManagerInterface {

    /**
     * Request class to hold information about time and sword returned
     */
    private class Request implements Comparable<Request> {
        int time;
        int totalHealth;
        int DPS;
        int attackSpeed;
        String style;

        public Request(int time, int totalHealth, int DPS, int attackSpeed, String style) {
            this.time = time;
            this.totalHealth = totalHealth;
            this.DPS = DPS;
            this.attackSpeed = attackSpeed;
            this.style = style;
        }

        public int compareTo(Request request) {
            return this.time - request.time;
        }
    }

    /**
     * Gets the cleaning times per the specifications.
     *
     * @param filename file to read input from
     * @return the list of times requests were filled, times it took to fill them,
     * and sword returned, as per the specifications
     */
    @Override
    public ArrayList<DetailedCleanSwordTime<SwordType>> getCleaningTimes(String filename) {

        ArrayList<DetailedCleanSwordTime<SwordType>> finishedSwords = new ArrayList<>();

        try {
            BufferedReader bf = new BufferedReader(new FileReader(filename));

            /** input:
             * Line 1: Z number of swords in game (N) _ Z number of requests (M)
             *
             * Lines [2, N + 1]: C, T, TH, HL, L, DPS, AS, "N", "DSC", "COM", "STY" ... for number of swords in game
             *
             * Lines [N + 2, M + N + 1]: Z_j, TH, DPS, AS, "STY", ... for number of requests
             */

            String[] line = null;
            int N = 0;
            int M = 0;

            try {
                line = bf.readLine().split(" ");
                N = Integer.valueOf(line[0]);
                M = Integer.valueOf(line[1]);
            } catch (Exception e) {
                System.err.println("Error on line 1" + e.getMessage());
                System.exit(1);
            }

            MinHeap<Sword> swordHeap = new MinHeap<>();
            BetterHashTable<Integer, Sword> swordHash = new BetterHashTable();

            int requestOrder = 0;

            /* read N lines for all the swords and add them to an appropriate data structure */

            for (int i = 0; i < N; i++) {
                String[] swordLine = null;
                Sword sword = null;
                try {
                    swordLine = bf.readLine().split(", ");

                    sword = new Sword(Integer.valueOf(swordLine[0]), Integer.valueOf(swordLine[1]),
                            Integer.valueOf(swordLine[2]), Integer.valueOf(swordLine[3]),
                            Integer.valueOf(swordLine[4]), Integer.valueOf(swordLine[5]),
                            Integer.valueOf(swordLine[6]), swordLine[7].substring(1, swordLine[7].length() - 1), swordLine[8].substring(1, swordLine[8].length() - 1),
                            swordLine[9].substring(1, swordLine[9].length() - 1), swordLine[10].substring(1, swordLine[10].length() - 1));
                } catch (Exception e) {
                    System.err.println("Error on line " + (i + 2) + "\n" + e.getMessage());
                    System.exit(1);
                }

                /* if the sword is in our possession, add to the min heap      */
                /* Otherwise, add it to the hash table to store cleaning time */
                /* adding request order to make min heap stable               */

                if (sword.getCleanliness() != -1) {
                    sword.setTimeOfClean(sword.getCleanliness());
                    sword.setRequestOrder(requestOrder);
                    requestOrder++;
                    swordHeap.add(sword);
                }
                swordHash.insert(sword.HashCode(), sword);
            }

            BetterQueue<Request> requestQueue = new BetterQueue<>();

            /* read M lines for all the requests and add them to a queue */

            for (int i = 0; i < M; i++) {
                String[] requestLine = null;
                Request request = null;

                try {
                    requestLine = bf.readLine().split(", ");
                    request = new Request(Integer.valueOf(requestLine[0]), Integer.valueOf(requestLine[1]),
                            Integer.valueOf(requestLine[2]), Integer.valueOf(requestLine[3]),
                            requestLine[4].substring(1, requestLine[4].length() - 1));
                } catch (Exception e) {
                    System.err.println("Error on line " + (i + N + 2) + "\n" + e.getMessage());
                    System.exit(1);
                }
                requestQueue.add(request);
            }

            int t = 0;

            BetterQueue<Sword> finishedSwordQueue = new BetterQueue<>();
            BetterQueue<Request> finsihedRequestQueue = new BetterQueue<>();

            while (true) {
                while (!requestQueue.isEmpty() && requestQueue.peek().time == t) {
                    Request current = requestQueue.remove();
                    Sword sword = new Sword(-1, -1, current.totalHealth,
                            -1, -1, current.DPS, current.attackSpeed,
                            "", "", "", current.style);

                    sword = swordHash.get(sword.HashCode());

                    boolean addToHeap = false;
                    if (sword.getTimeOfClean() == -1) {
                        addToHeap = true;
                        sword.setCleanliness(sword.getTimeToClean());
                        sword.setTimeOfClean(t + sword.getTimeToClean());
                    }

                    sword.setRequestOrder(requestOrder);
                    requestOrder++;

                    if (addToHeap) {
                        swordHeap.add(sword);
                    }

                    finsihedRequestQueue.add(current);
                }

                while (swordHeap.size() != 0 && swordHeap.peekMin().getTimeOfClean() <= t) {
                    finishedSwordQueue.add(swordHeap.removeMin());
                }

                while (!finsihedRequestQueue.isEmpty() && !finishedSwordQueue.isEmpty()) {
                    Request request = finsihedRequestQueue.remove();
                    Sword sword = finishedSwordQueue.remove();

                    finishedSwords.add(new DetailedCleanSwordTime<>(t, t - request.time, sword));
                }

                /* advance to the next necessary t */

                int nextSword = Integer.MAX_VALUE;
                int nextRequest = Integer.MAX_VALUE;
                if (swordHeap.size() != 0) {
                    nextSword = swordHeap.peekMin().getTimeOfClean();
                }
                if (!requestQueue.isEmpty()) {
                    nextRequest = requestQueue.peek().time;
                }
                if (nextSword < nextRequest) {
                    t = nextSword;
                } else {
                    t = nextRequest;
                }

                if (requestQueue.isEmpty() && finsihedRequestQueue.isEmpty()) {
                    break;
                }

            }
        } catch (IOException e) {
            System.err.println("Couldn't find test file: \"" + filename + "\":: " + e.getMessage());
            System.exit(1);
        }
        return finishedSwords;
    }
}
