package com.jclark.xml.parse;

/**
 * Thrown to indicate that a method in <code>Application</code> has thrown
 * an Exception.
 * An <code>ApplicationException</code> is not thrown when a method
 * in <code>Application</code> throws an exception derived
 * from <code>RuntimeException</code>.
 * @version $Revision: 1.1 $ $Date: 2003/10/18 15:26:20 $
 */

public class ApplicationException extends Exception {
  private final Exception exception;
  public ApplicationException(Exception e) {
    exception = e;
  }
  /**
   * Returns the exception thrown by the <code>Application</code> method.
   */
  public Exception getException() {
    return exception;
  }
}
