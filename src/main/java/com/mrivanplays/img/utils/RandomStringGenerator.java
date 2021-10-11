package com.mrivanplays.img.utils;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

public class RandomStringGenerator {

  public static String generateRandomString() {
    byte[] rnd = new byte[11];
    ThreadLocalRandom.current().nextBytes(rnd);
    return new String(rnd, StandardCharsets.UTF_8);
  }
}
