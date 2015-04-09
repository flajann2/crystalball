package com.jclark.xml.parse;

/**
 * Information about the prolog.
 * @see com.jclark.xml.parse.base.Application#endProlog
 * @version $Revision: 1.1 $ $Date: 2003/10/18 15:26:21 $
 */
public interface EndPrologEvent {
  /**
   * Returns the DTD.
   * This will not be null even if there was no DOCTYPE declaration.
   */
  DTD getDTD();
}
