package com.mrivanplays.img.utils;

import java.util.concurrent.ThreadLocalRandom;

public class RandomStringGenerator {

  public static String generateRandomString() {
    int leftLimit = 97; // letter 'a'
    int rightLimit = 122; // letter 'z'
    int targetStringLength = 10;

    return ThreadLocalRandom.current().ints(leftLimit, rightLimit + 1)
        .limit(targetStringLength)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }
}
