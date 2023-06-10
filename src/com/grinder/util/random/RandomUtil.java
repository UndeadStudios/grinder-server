package com.grinder.util.random;

import java.util.concurrent.ThreadLocalRandom;

/**
 * TODO: add documentation
 *
 * @author Stan van der Bend (https://www.rune-server.ee/members/StanDev/)
 * @version 1.0
 * @since 2019-06-05
 */
public class RandomUtil {

    public static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public static int getRandomInclusive(int length) {
        return RANDOM.nextInt(length + 1);
    }

}
