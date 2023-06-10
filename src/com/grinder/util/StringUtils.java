package com.grinder.util;

import java.security.SecureRandom;
import java.util.Random;

/**
 * @author hadyn
 */
public class StringUtils {
  public static final char[] ALPHA_NUMERIC_CHARS =
          "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
  public static final char[] HEX_CHARS = "ABCDEF01234567890".toCharArray();
  private static final Random random = new SecureRandom();
  public static final char[] NUMERIC_CHARS = "0123456789".toCharArray();
  private static final char[] BASE_37_CHARACTERS = { '_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

  public static long encodeBase37(String string) {
    long encoded = 0L;
    for (int index = 0; index < string.length() && index < 12; index++) {
      char c = string.charAt(index);
      encoded *= 37L;
      if (c >= 'A' && c <= 'Z')
        encoded += (1 + c) - 65;
      else if (c >= 'a' && c <= 'z')
        encoded += (1 + c) - 97;
      else if (c >= '0' && c <= '9')
        encoded += (27 + c) - 48;
    }

    for (; encoded % 37L == 0L && encoded != 0L; encoded /= 37L)
      ;
    return encoded;
  }

  public static String decodeBase37(long encoded) {
    try {
      if (encoded <= 0L || encoded >= 0x5b5b57f8a98a5dd1L)
        return "invalid_name";
      if (encoded % 37L == 0L)
        return "invalid_name";
      int length = 0;
      char chars[] = new char[12];
      while (encoded != 0L) {
        long l1 = encoded;
        encoded /= 37L;
        chars[11 - length++] = BASE_37_CHARACTERS[(int) (l1 - encoded * 37L)];
      }
      return new String(chars, 12 - length, length);
    } catch (RuntimeException runtimeexception) {
      runtimeexception.printStackTrace();
    }
    throw new RuntimeException();
  }
  private StringUtils() {}

  public static String generateUsername() {
    return generateString(6, 12, ALPHA_NUMERIC_CHARS);
  }

  public static String generatePassword() {
    return generateString(6, 12, ALPHA_NUMERIC_CHARS);
  }

  public static String generateString(int length, char[] chars) {
    StringBuilder string = new StringBuilder();
    for (int i = 0; i < length; i++) {
      string.append(chars[random.nextInt(chars.length)]);
    }
    return string.toString();
  }

  public static String generateString(int minimum, int maximum, char[] chars) {
    StringBuilder string = new StringBuilder();
    int length = random.nextInt(maximum - minimum) + minimum;
    for (int i = 0; i < length; i++) {
      string.append(chars[random.nextInt(chars.length)]);
    }
    return string.toString();
  }

  /**
   * Generates a MAC address. A MAC address is an 64 bit identifier for the local network hardware.
   *
   * @return the generated MAC address.
   */
  public static String generateMac() {
    StringBuilder builder = new StringBuilder();
    boolean first = true;
    for (int i = 0; i < 6; i++) {
      if (!first) {
        builder.append('-');
      }

      for (int j = 0; j < 2; j++) {
        builder.append(HEX_CHARS[random.nextInt(HEX_CHARS.length)]);
      }
      first = false;
    }
    return builder.toString();
  }

  public static String getMotherboardSN() {
    return "".trim();
  }

  public static String getHDSerialNumber(String drive) {
    return "".trim();
  }

  public static String generateUUID() {
    return generateString(32, HEX_CHARS);
  }
}
