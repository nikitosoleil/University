package org.ahoma.interruptiontests;
/*
 * Created by ahoma on 14/04/2019.
 * Copyright (C) 2019 Andrii Khoma. All rights reserved.
 */

import org.ahoma.ComputationManager;
import org.ahoma.TestManagerThread;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.SAME_THREAD)
class InterruptionTestZeroValue {
  private ComputationManager computationManager;

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;

  @BeforeEach
  void setup() {
    computationManager = new ComputationManager(0, (integer, integer2) -> integer * integer2);
    System.setOut(new PrintStream(outContent));
  }

  @AfterEach
  void restoreStreams() {
    System.setOut(originalOut);
  }

  @Test
  void fZeroGHangs() {
    TestManagerThread testThread =
        new TestManagerThread(
            false,
            5,
            10001,
            2,
            computationManager,
            integer -> 0,
            integer -> {
              try {
                while (!Thread.interrupted()) {
                  Thread.sleep(400);
                }
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              return integer * integer;
            });

    assertEquals(0, computationManager.getNowCalculated());
    assertFalse(computationManager.isComputed());

    testThread.start();

    try {
      while (testThread.notClientsSpawned()) Thread.sleep(20);
      Thread.sleep(100);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    assertEquals(1, computationManager.getNowCalculated());
    assertTrue(computationManager.isComputed());
    assertEquals(computationManager.getZeroValue(), computationManager.getResult());

    try {
      Thread.sleep(400);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    assertEquals(1, computationManager.getNowCalculated());
    assertTrue(computationManager.isComputed());
    assertEquals(computationManager.getZeroValue(), computationManager.getResult());

    testThread.stopCalculation();

    assertEquals("Computation result: 0\n", outContent.toString());
  }

  @Test
  void gZeroFHangs() {
    TestManagerThread testThread =
        new TestManagerThread(
            false,
            5,
            10002,
            2,
            computationManager,
            integer -> {
              try {
                while (!Thread.interrupted()) {
                  Thread.sleep(400);
                }
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              return integer * integer;
            },
            integer -> 0);

    assertEquals(0, computationManager.getNowCalculated());
    assertFalse(computationManager.isComputed());

    testThread.start();

    try {
      while (testThread.notClientsSpawned()) Thread.sleep(20);
      Thread.sleep(100);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    assertEquals(1, computationManager.getNowCalculated());
    assertTrue(computationManager.isComputed());
    assertEquals(computationManager.getZeroValue(), computationManager.getResult());

    try {
      Thread.sleep(400);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    assertEquals(1, computationManager.getNowCalculated());
    assertTrue(computationManager.isComputed());
    assertEquals(computationManager.getZeroValue(), computationManager.getResult());

    testThread.stopCalculation();

    assertEquals("Computation result: 0\n", outContent.toString());
  }
}
