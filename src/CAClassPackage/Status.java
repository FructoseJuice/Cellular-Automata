package CAClassPackage;

/**
 * ENUM representing 2 possible states for a cell, Alive, or Dead
 */
public enum Status {
    ALIVE {
        @Override
        public int getBit() {
            return 1;
        }
    },

    DEAD {
        @Override
        public int getBit() {
            return 0;
        }
    };

    public int getBit() {
        return -1;
    }
}



