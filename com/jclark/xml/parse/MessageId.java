package com.jclark.xml.parse;

/**
 * The keys of the messages that can be generated by <code>EntityParser</code>.
 * @see EntityParser
 * @version $Revision: 1.1 $ $Date: 2003/10/18 15:26:22 $
 */

interface MessageId {
  /* Using the same name of the identifier as the string saves space
     in the class file. */
  static final String MESSAGE_FORMAT = "MESSAGE_FORMAT";
  static final String PUBID_CHAR = "PUBID_CHAR";
  static final String ELEMENT_AFTER_DOCUMENT_ELEMENT = "ELEMENT_AFTER_DOCUMENT_ELEMENT";
  static final String BAD_INITIAL_BYTES = "BAD_INITIAL_BYTES";
  static final String BAD_DECL_ENCODING = "BAD_DECL_ENCODING";
  static final String INVALID_XML_DECLARATION = "INVALID_XML_DECLARATION";
  static final String IGNORE_SECT_CHAR = "IGNORE_SECT_CHAR";
  static final String INVALID_END_TAG = "INVALID_END_TAG";
  static final String EPILOG_JUNK = "EPILOG_JUNK";
  static final String MISMATCHED_END_TAG = "MISMATCHED_END_TAG";
  static final String MISPLACED_XML_DECL = "MISPLACED_XML_DECL";
  static final String MISSING_END_TAG = "MISSING_END_TAG";
  static final String NO_DOCUMENT_ELEMENT = "NO_DOCUMENT_ELEMENT";
  static final String NOT_WELL_FORMED = "NOT_WELL_FORMED";
  static final String PE_GROUP_NESTING = "PE_GROUP_NESTING";
  static final String PE_DECL_NESTING = "PE_DECL_NESTING";
  static final String INTERNAL_PEREF_ENTVAL = "INTERNAL_PEREF_ENTVAL";
  static final String RECURSION = "RECURSION";
  static final String EXTERN_REF_ATTVAL = "EXTERN_REF_ATTVAL";
  static final String UNDEF_REF = "UNDEF_REF";
  static final String UNDEF_PEREF = "UNDEF_PEREF";
  static final String UNPARSED_REF = "UNPARSED_REF";
  static final String SYNTAX_ERROR = "SYNTAX_ERROR";
  static final String UNCLOSED_CDATA_SECTION = "UNCLOSED_CDATA_SECTION";
  static final String UNCLOSED_CONDITIONAL_SECTION = "UNCLOSED_CONDITIONAL_SECTION";
  static final String UNCLOSED_TOKEN = "UNCLOSED_TOKEN";
  static final String UNSUPPORTED_ENCODING = "UNSUPPORTED_ENCODING";
  static final String DUPLICATE_ATTRIBUTE = "DUPLICATE_ATTRIBUTE";
  static final String XML_TARGET = "XML_TARGET";
  static final String ILLEGAL_CHAR = "ILLEGAL_CHAR";
}
