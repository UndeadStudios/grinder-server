package com.grinder.util;

import com.grinder.net.packet.DataType;
import com.grinder.net.packet.GamePacketBuilder;

import java.util.Arrays;

/**
 * A class which contains text-related utility methods.
 *
 * @author Graham
 * @apiNote Stan
 */
public class TextUtil {
    /**
     * An array of characters ordered by frequency - the elements with lower indices (generally) appear more often in
     * chat messages.
     */
    public static final char[] FREQUENCY_ORDERED_CHARS = { ' ', 'e', 't', 'a', 'o', 'i', 'h', 'n', 's', 'r', 'd', 'l',
            'u', 'm', 'w', 'c', 'y', 'f', 'g', 'p', 'b', 'v', 'k', 'x', 'j', 'q', 'z', '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', ' ', '!', '?', '.', ',', ':', ';', '(', ')', '-', '&', '*', '\\', '\'', '@', '#', '+',
            '=', '\243', '$', '%', '"', '[', ']' };

    /**
     * Capitalizes the string correctly.
     *
     * @param string The input string.
     * @return The string with correct capitalization.
     */
    public static String capitalize(String string) {
        boolean capitalize = true;
        StringBuilder builder = new StringBuilder(string);
        int length = string.length();

        for (int index = 0; index < length; index++) {
            char character = builder.charAt(index);

            if (character == '.' || character == '!' || character == '?') {
                capitalize = true;
            } else if (capitalize && !Character.isWhitespace(character)) {
                builder.setCharAt(index, Character.toUpperCase(character));
                capitalize = false;
            } else {
                builder.setCharAt(index, Character.toLowerCase(character));
            }
        }

        return builder.toString();
    }

    /**
     * Compresses the input text ({@code in}) and places the result in the {@code out} array.
     *
     * @param in The input text.
     * @param out The output array.
     * @return The number of bytes written to the output array.
     */
    public static int compress(String in, byte[] out) {
        if (in.length() > 80) {
            in = in.substring(0, 80);
        }
        in = in.toLowerCase();

        int carry = -1;
        int outPos = 0;
        for (int inPos = 0; inPos < in.length(); inPos++) {
            char c = in.charAt(inPos);
            int tblPos = 0;
            for (int i = 0; i < FREQUENCY_ORDERED_CHARS.length; i++) {
                if (c == FREQUENCY_ORDERED_CHARS[i]) {
                    tblPos = i;
                    break;
                }
            }
            if (tblPos > 12) {
                tblPos += 195;
            }
            if (carry == -1) {
                if (tblPos < 13) {
                    carry = tblPos;
                } else {
                    out[outPos++] = (byte) tblPos;
                }
            } else if (tblPos < 13) {
                out[outPos++] = (byte) ((carry << 4) + tblPos);
                carry = -1;
            } else {
                out[outPos++] = (byte) ((carry << 4) + (tblPos >> 4));
                carry = tblPos & 0xF;
            }
        }
        if (carry != -1) {
            out[outPos++] = (byte) (carry << 4);
        }
        return outPos;
    }
    public static int compress(String in, GamePacketBuilder out) {
        if (in.length() > 80) {
            in = in.substring(0, 80);
        }
        in = in.toLowerCase();

        int carry = -1;
        int outPos = 0;
        for (int inPos = 0; inPos < in.length(); inPos++) {
            char c = in.charAt(inPos);
            int tblPos = 0;
            for (int i = 0; i < FREQUENCY_ORDERED_CHARS.length; i++) {
                if (c == FREQUENCY_ORDERED_CHARS[i]) {
                    tblPos = i;
                    break;
                }
            }
            if (tblPos > 12) {
                tblPos += 195;
            }
            if (carry == -1) {
                if (tblPos < 13) {
                    carry = tblPos;
                } else {
                    out.put(DataType.BYTE, tblPos);
                }
            } else if (tblPos < 13) {
                out.put(DataType.BYTE, (carry << 4) + tblPos);
                carry = -1;
            } else {
                out.put(DataType.BYTE, (carry << 4) + (tblPos >> 4));
                carry = tblPos & 0xF;
            }
        }
        if (carry != -1) {
            out.put(DataType.BYTE, carry << 4);
        }
        return outPos;
    }

    /**
     * Filters invalid characters from the specified string.
     *
     * @param str The input string.
     * @return The filtered string.
     */
    public static String filterInvalidCharacters(String str) {
        StringBuilder builder = new StringBuilder();
        for (char c : str.toLowerCase().toCharArray()) {
            for (char validChar : FREQUENCY_ORDERED_CHARS) {
                if (c == validChar) {
                    builder.append(c);
                    break;
                }
            }
        }
        return builder.toString();
    }

    /**
     * Uncompresses the compressed data ({@code in}) with the length ({@code len}) and returns the uncompressed
     * {@link String}.
     *
     * @param in The compressed input data.
     * @param len The length.
     * @return The uncompressed {@link String}.
     */
    public static String decompress(byte[] in, int len) {
        byte[] out = new byte[4096];
        int outPos = 0;
        int carry = -1;

        for (int i = 0; i < len * 2; i++) {
            int tblPos = in[i / 2] >> 4 - 4 * (i % 2) & 0xF;
            if (carry == -1) {
                if (tblPos < 13) {
                    out[outPos++] = (byte) FREQUENCY_ORDERED_CHARS[tblPos];
                } else {
                    carry = tblPos;
                }
            } else {
                out[outPos++] = (byte) FREQUENCY_ORDERED_CHARS[(carry << 4) + tblPos - 195];
                carry = -1;
            }
        }
        return new String(out, 0, outPos);
    }
    /**
     * This is an implementation of the levenstein algorithm.
     * In essence, this can be used to numerically represent the similarity in two given strings.
     *
     * @param a first input
     * @param b second input
     *
     * @return the numerical similarity of two given inputs.
     */
    public static int calculateLevensteinDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        for (int i = 0; i <= a.length(); i++) {
            for (int j = 0; j <= b.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                }
                else if (j == 0) {
                    dp[i][j] = i;
                }
                else {
                    dp[i][j] = min(dp[i - 1][j - 1]
                                    + costOfSubstitution(a.charAt(i - 1), b.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1);
                }
            }
        }

        return dp[a.length()][b.length()];
    }

    private static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    private static int min(int... numbers) {
        return Arrays.stream(numbers)
                .min().orElse(Integer.MAX_VALUE);
    }
    public static boolean isInteger(String s) {
        return isInteger(s,10);
    }

    public static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }
}
