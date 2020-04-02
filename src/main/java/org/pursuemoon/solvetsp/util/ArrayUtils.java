package org.pursuemoon.solvetsp.util;

/**
 * A utility class for manipulating arrays.
 */
public final class ArrayUtils {

    public static int indexOfUnique(int[] gene, int uniqueElement) {
        int ret = -1;
        for (int f = 0; f < gene.length; ++f) {
            if (gene[f] == uniqueElement) {
                ret = f;
                break;
            }
        }
        if (ret == -1)
            throw new RuntimeException(String.format("The gene array doesn't contain %d.", uniqueElement));
        return ret;
    }
}
