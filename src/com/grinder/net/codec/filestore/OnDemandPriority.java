package com.grinder.net.codec.filestore;

/**
 * An enumeration containing the different request priorities.
 */
public enum OnDemandPriority {

    /**
     * High priority - used when a player is in-game and data is required immediately.
     */
    HIGH(0),

    /**
     * Medium priority - used while loading extra resources when the client is not logged in.
     */
    MEDIUM(1),

    /**
     * Low priority - used when a file is not required urgently (such as when serving the rest of the cache whilst
     * the player is in-game).
     */
    LOW(2);

    /**
     * The integer value.
     */
    private final int value;

    /**
     * Creates the Priority.
     *
     * @param value The integer value.
     */
    private OnDemandPriority(int value) {
        this.value = value;
    }

    /**
     * Converts the integer value to a Priority.
     *
     * @param value The integer value.
     * @return The priority.
     * @throws IllegalArgumentException If the value is outside of the range 1-3 inclusive.
     */
    public static OnDemandPriority valueOf(int value) {
        switch (value) {
            case 0:
                return HIGH;
            case 1:
                return MEDIUM;
            case 2:
                return LOW;
            default:
                throw new IllegalArgumentException("Priority out of range - received " + value + ".");
        }
    }

    /**
     * Compares this Priority with the specified other Priority.
     * <p>
     * Used as an ordinal-independent variant of {@link #compareTo}.
     *
     * @param other The other Priority.
     * @return 1 if this Priority is greater than {@code other}, 0 if they are equal, otherwise -1.
     */
    public int compareWith(OnDemandPriority other) {
        return Integer.compare(value, other.value);
    }

    /**
     * Converts the priority to an integer.
     *
     * @return The integer value.
     */
    public int toInteger() {
        return value;
    }

}