package com.jclark.xml.parse;

/**
 * Information about a markup declaration.
 * @see com.jclark.xml.parse.base.Application#markupDeclaration
 * @version $Revision: 1.1 $ $Date: 2003/10/18 15:26:22 $
 */
public interface MarkupDeclarationEvent {
  static int ATTRIBUTE = 0;
  static int ELEMENT = 1;
  static int GENERAL_ENTITY = 2;
  static int PARAMETER_ENTITY = 3;
  static int NOTATION = 4;
  int getType();
  String getName();
  String getAttributeName();
  DTD getDTD();
}
