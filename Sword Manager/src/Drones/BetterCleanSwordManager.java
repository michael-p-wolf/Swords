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
 *   the other drone classes.  UPDATED VERSION -- handles sword management with the new rules.
 *   Old version-CleanSwordManager-is deprecated.
 *
 * <bold>251 students: you may use any of the data structures you have previously created, but may not use
 *   any Java library for stacks, queues, min heaps/priority queues, or hash tables (or any similar classes).</bold>
 */
public class BetterCleanSwordManager implements BetterCleanSwordManagerInterface {

    

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

        public int compareTo (Request request) {
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

            /* input:
            number of swords in game _ number of requests

            C, T, TH, HL, L, DPS, AS, "N", "DSC", "COM", "STY"

            ... for number of swords in game

            Z_j, TH, DPS, AS, "STY"

            ... for number of requests


            ID depends on TH, DPS, AS, STY **PUT IN HASH FUNCTION

            plan: put all the swords in the game w/ all information into a hashtable, w/ key based on TH, DPS, AS, "STY"
            create a min heap w/ simpleSword object (only has 4 attributes).
            When a request is filled, get the sword object from the hash table... but do we even need all the other info?


             */

            String[] line = bf.readLine().split(" ");
            int N = Integer.valueOf(line[0]);
            int M = Integer.valueOf(line[1]);

            MinHeap<Sword> swordHeap = new MinHeap<>();
            BetterHashTable<Integer, Sword> swordHash = new BetterHashTable();

            int requestOrder = 0;

            for (int i = 0; i < N; i++) {
                String[] swordLine = bf.readLine().split(", ");


                Sword sword = new Sword(Integer.valueOf(swordLine[0]), Integer.valueOf(swordLine[1]),
                                        Integer.valueOf(swordLine[2]), Integer.valueOf(swordLine[3]),
                                        Integer.valueOf(swordLine[4]), Integer.valueOf(swordLine[5]),
                                        Integer.valueOf(swordLine[6]), swordLine[7].substring(1, swordLine[7].length() -1), swordLine[8].substring(1, swordLine[8].length() -1),
                                        swordLine[9].substring(1, swordLine[9].length() -1), swordLine[10].substring(1, swordLine[10].length() -1));

/*
                SimpleSword simpleSword = new SimpleSword(Integer.valueOf(swordLine[0]), Integer.valueOf(swordLine[1]),
                                                          Integer.valueOf(swordLine[2]), Integer.valueOf(swordLine[5]),
                                                          Integer.valueOf(swordLine[6]), swordLine[10]);
                                                          */

                if (sword.getCleanliness() != -1) {
                    sword.setTimeOfClean(sword.getCleanliness());
                    sword.setRequestOrder(requestOrder);
                    requestOrder++;
                    swordHeap.add(sword);
                }
                swordHash.insert(sword.HashCode(), sword);
            }

            /* a hash table would be good for swords if we want to find and return them */
            /* maybe a hash table of swords and a min heap of the swords w/ less info */

            //MinHeap<Request> requestHeap = new MinHeap<>();

            BetterQueue<Request> requestQueue = new BetterQueue<>();

            for (int i = 0; i < M; i++) {
                String[] requestLine = bf.readLine().split(", ");
                Request request = new Request(Integer.valueOf(requestLine[0]), Integer.valueOf(requestLine[1]),
                                              Integer.valueOf(requestLine[2]), Integer.valueOf(requestLine[3]),
                                              requestLine[4].substring(1, requestLine[4].length() -1));
                requestQueue.add(request);
            }

            /* why would i use a min heap for request if they're already sorted */

            //finishedSwords.add(new DetailedCleanSwordTime<>(0, 0, Sword asdf));

            /* add swords to a min heap based on timeOfClean*/
            /* add requests to a min heap based on time of request */

            int t = 0;

            BetterQueue<Sword> finishedSwordQueue = new BetterQueue<>();
            BetterQueue<Request> finsihedRequestQueue = new BetterQueue<>();


            while (true) {
                //System.out.println("t: " + t);
                while (!requestQueue.isEmpty() && requestQueue.peek().time == t) {
                    Request current = requestQueue.remove();

                    //System.out.println("current attack speed: " + current.attackSpeed);
                    //SimpleSword sword = new SimpleSword(-1, -1, current.totalHealth, current.DPS, current.attackSpeed, current.style);
                    Sword sword = new Sword(-1,-1, current.totalHealth,
                                  -1,-1,current.DPS, current.attackSpeed,
                                      "","","", current.style);




                    /*how can we efficiently check that the sword in already in the heap */

                    sword = swordHash.get(sword.HashCode()); //need to keep track of time to clean probably need hash table
                    
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

                while (swordHeap.size() != 0 && swordHeap.peekMin().getTimeOfClean() <= t) { /* consider <= */
                    finishedSwordQueue.add(swordHeap.removeMin());
                }

                while (!finsihedRequestQueue.isEmpty() && !finishedSwordQueue.isEmpty()) {
                    Request request = finsihedRequestQueue.remove();
                    Sword sword = finishedSwordQueue.remove();

                    finishedSwords.add(new DetailedCleanSwordTime<>(t, t - request.time, sword));
                }

                //advance to next t

                int nextSword = Integer.MAX_VALUE;
                int nextRequest = Integer.MAX_VALUE;
                if (swordHeap.size() != 0) {
                    nextSword = swordHeap.peekMin().getTimeOfClean();
                }
                if (!requestQueue.isEmpty()) {
                    nextRequest = requestQueue.peek().time;
                }
                //System.out.println("next sword: " + nextSword + " next request: " + nextRequest);
                if (nextSword < nextRequest) {
                    t = nextSword;
                } else {
                    t = nextRequest;
                }

                //if request queue is empty then break

                if (requestQueue.isEmpty() && finsihedRequestQueue.isEmpty()) {
                    break;
                }

            }




            //MinHeap<Sword>;

            /* could make a new request object */



            /* put all swords in a has table right away ? */

            //todo
        } catch (IOException e) {
            //This should never happen... uh oh o.o
            System.err.println("ATTENTION TAs: Couldn't find test file: \"" + filename + "\":: " + e.getMessage());
            System.exit(1);
        }
        return finishedSwords;
    }
}
