package com.jclark.xml.apps;

import java.io.*;
import com.jclark.xml.parse.*;
import com.jclark.xml.parse.io.*;
import com.jclark.xml.output.*;

/**
 * @version $Revision: 1.1 $ $Date: 2003/10/18 15:26:20 $
 */
public class Normalize extends ApplicationImpl {

  private final XMLWriter w;

  /**
   * Writes a normalized version of an XML document to the standard
   * output.
   * If an argument is specified, then that is treated as the filename
   * of the XML document, otherwise the XML document is read from the
   * standard input.
   */
  public static void main(String args[]) throws IOException {
    if (args.length > 1) {
      System.err.println("usage: jview com.jclark.xml.apps.Normalize [file]");
      System.exit(1);
    }
    Parser parser = new ParserImpl();
    parser.setApplication(new Normalize(new UTF8XMLWriter(new FileOutputStream(FileDescriptor.out))));
    try {
      parser.parseDocument(args.length == 0
			   ? EntityManagerImpl.openStandardInput()
			   : EntityManagerImpl.openFile(args[0]));
    }
    catch (NotWellFormedException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
  }

  public Normalize(XMLWriter w) {
    this.w = w;
  }

  public void startElement(StartElementEvent event) throws IOException {
    w.startElement(event.getName());
    int nAtts = event.getAttributeCount();
    for (int i = 0; i < nAtts; i++)
      w.attribute(event.getAttributeName(i),
		  event.getAttributeValue(i));
  }

  public void endElement(EndElementEvent event) throws IOException {
    w.endElement(event.getName());
  }

  public void endDocument() throws IOException {
    w.write('\n');
    w.flush();
  }

  public void processingInstruction(ProcessingInstructionEvent event) throws IOException {
    w.processingInstruction(event.getName(), event.getInstruction());
  }

  public void characterData(CharacterDataEvent event) throws IOException {
    event.writeChars(w);
  }
}
