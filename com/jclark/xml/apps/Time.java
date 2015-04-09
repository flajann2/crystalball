package com.jclark.xml.apps;

import java.io.IOException;

import com.jclark.xml.parse.io.Parser;
import com.jclark.xml.parse.io.ParserImpl;
import com.jclark.xml.parse.EntityManagerImpl;
import com.jclark.xml.parse.OpenEntity;
import com.jclark.xml.parse.NotWellFormedException;

/**
 * @version $Revision: 1.1 $ $Date: 2003/10/18 15:26:20 $
 */
public class Time {

  /**
   * Each of the specified argument is treated as the name
   * of a file containing an XML document to be parsed.
   * If no arguments are specified, the standard input is read.
   * The total time in seconds for the parse is reported
   * on the standard output.
   * For each document that is not well-formed, a message
   * is written in the standard error.
   */
  public static void main(String args[]) throws IOException {
    long startTime = System.currentTimeMillis();
    boolean hadErr = false;
    if (args.length == 0)
      hadErr = !parseEntity(EntityManagerImpl.openStandardInput());
    else {
      for (int i = 0; i < args.length; i++)
	if (!parseEntity(EntityManagerImpl.openFile(args[i])))
	  hadErr = true;
    }
    System.out.println((System.currentTimeMillis() - startTime)/1000.0);
    System.exit(hadErr ? 1 : 0);
  }

  static boolean parseEntity(OpenEntity entity) throws IOException {
    Parser parser = new ParserImpl();
    try {
      parser.parseDocument(entity);
      return true;
    }
    catch (NotWellFormedException e) {
      System.err.println(e.getMessage());
      return false;
    }
  }
}
