package com.jclark.xml.sax;

import org.xml.sax.SAXException;

/**
 * A wrapper around an EntityResolver generated exception.
 *
 * @see Driver
 * @version $Revision: 1.1 $ $Date: 2003/10/18 15:26:23 $
 */
class WrapperException extends java.io.IOException {
  SAXException wrapped;
  WrapperException(SAXException e) {
    wrapped = e;
  }
  SAXException getWrapped() {
    return wrapped;
  }
}

