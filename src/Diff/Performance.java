package Diff;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.text.DecimalFormat;

/**
 *
 * @author pehlivanz
 */
public class Performance {
     /**
  * Start the stopwatch.
  *
  * @throws IllegalStateException if the stopwatch is already running.
  */

  public void start(){
    if ( fIsRunning ) {
      throw new IllegalStateException("Must stop before calling start again.");
    }
    //reset both start and stop
    fStart = System.nanoTime();
    fStop = 0;
    fIsRunning = true;
    fHasBeenUsedOnce = true;
  }

  /**
  * Stop the stopwatch.
  *
  * @throws IllegalStateException if the stopwatch is not already running.
  */
  public void stop() {
    if ( !fIsRunning ) {
      throw new IllegalStateException("Cannot stop if not currently running.");
    }
    fStop = System.nanoTime();
    fIsRunning = false;
  }

  /**
  * Express the "reading" on the stopwatch.
  *
  * @throws IllegalStateException if the Stopwatch has never been used,
  * or if the stopwatch is still running.
  */
  public String toString() {
      DecimalFormat dc = new DecimalFormat();
      dc.setMinimumFractionDigits(3);
      dc.setMaximumFractionDigits(3);

    StringBuilder result = new StringBuilder();
    String res2 = dc.format(((double)(fStop - fStart))/1000000);
     result.append(res2);
    result.append(" ms");
    return result.toString();
  }

  /**
  * Express the "reading" on the stopwatch as a numeric type.
  *
  * @throws IllegalStateException if the Stopwatch has never been used,
  * or if the stopwatch is still running.
  */
  public long toValue() {
    validateIsReadable();
    return fStop - fStart;
  }

  // PRIVATE ////
  private long fStart;
  private long fStop;

  private boolean fIsRunning;
  private boolean fHasBeenUsedOnce;

  /**
  * Throws IllegalStateException if the watch has never been started,
  * or if the watch is still running.
  */
  private void validateIsReadable() {
    if ( fIsRunning ) {
      String message = "Cannot read a stopwatch which is still running.";
      throw new IllegalStateException(message);
    }
    if ( !fHasBeenUsedOnce ) {
      String message = "Cannot read a stopwatch which has never been started.";
      throw new IllegalStateException(message);
    }
  }


}
