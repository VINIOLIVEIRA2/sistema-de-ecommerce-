package com.vini.payments.infrastructure;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class FailureSimulator {
  public boolean shouldFail() {
    // 20% de chance de falhar
    return ThreadLocalRandom.current().nextInt(100) < 20;
  }
}
