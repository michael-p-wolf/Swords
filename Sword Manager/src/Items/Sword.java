package Items;

import Drones.BetterCleanSwordManager;
import Items.Types.SwordType;

import java.util.Objects;

/**
 * Implements our SwordType class
 */
public class Sword extends SwordType implements Comparable<Sword> {

    int cleanliness;
    int timeOfClean;
    int requestOrder;

    /**
     * Constructs the Sword object by referencing the parent class.  Fields are as named.
     */
    public Sword(int cleanliness, int timeToClean, int totalHealth, int healthLeft, int length, int DPS, int attackSpeed,
                 String name, String description, String comments, String style) {
        super(totalHealth, healthLeft, timeToClean, length, DPS, attackSpeed, name, description, comments, style);
        this.cleanliness = cleanliness;
        this.timeOfClean = -1;
    }


    /**
     * CompareTo function for use in the min heap. Compares based off timeOfClean
     * compares this.sword to @param sword
     * If necessary, compares request order to mantain stability in the min heap
     */

    public int compareTo(Sword sword) {
        int compare = this.timeOfClean - sword.timeOfClean;
        if (compare == 0) {
            return this.requestOrder - sword.requestOrder;
        }
        return compare;

    }

    public void setRequestOrder(int requestOrder) {
        this.requestOrder = requestOrder;
    }

    public int getCleanliness() {
        return this.cleanliness;
    }

    public int getTimeOfClean() {
        return this.timeOfClean;
    }

    public void setCleanliness(int cleanliness) {
        this.cleanliness = cleanliness;
    }

    public void setTimeOfClean(int timeOfClean) {
        this.timeOfClean = timeOfClean;
    }


    /**
     * Returns the hash code of this object, for internal use only
     */
    public int HashCode() {
        /* unique hash code needs to be based on th, dps, as, sty */

        int total = 0;

        /* summing the value for each char in the string */

        for (int i = 0; i < this.style.length(); i++) {
            total += this.style.charAt(i);
        }

        total = total * 100;

        total += this.attackSpeed * 10 + this.DPS * 1 + this.totalHealth * 1000;

        return total;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
