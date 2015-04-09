package com.jclark.xml.parse;

/**
 * Interface for events which provide location information.
 * @version $Revision: 1.1 $ $Date: 2003/10/18 15:26:21 $
 */
public interface LocatedEvent {
  /**
   * Returns the location
   * of the first character of the markup of the event.
   * The return value is valid only so long as the event
   * itself is valid.
   */
  ParseLocation getLocation();
}

