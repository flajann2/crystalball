package com.jclark.xml.parse;

/**
 * Information about a comment
 * @see com.jclark.xml.parse.base.Application#comment
 * @version $Revision: 1.1 $ $Date: 2003/10/18 15:26:20 $
 */
public interface CommentEvent extends LocatedEvent {
  /**
   * Returns the body of the comment occurring between
   * the <code>&lt;--</code> and <code>--&gt;</code>.
   */
  String getComment();
}
