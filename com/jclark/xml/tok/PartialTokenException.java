package com.jclark.xml.tok;

/**
 * Thrown to indicate that the byte subarray being tokenized does not start
 * with a legal XML token but might be one if the subarray were extended.
 * @version $Revision: 1.1 $ $Date: 2003/10/18 15:26:23 $
 */
public class PartialTokenException extends TokenException {
}
