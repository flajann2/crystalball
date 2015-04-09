package com.jclark.xml.apps;

import java.io.*;
import java.util.Enumeration;
import com.jclark.xml.parse.*;
import com.jclark.xml.parse.io.*;
import com.jclark.xml.output.*;

/**
 * @version $Revision: 1.1 $ $Date: 2003/10/18 15:26:20 $
 */
public class Doctype extends ApplicationImpl {

  private static final String attTypeNames[] = new String[] {
    "CDATA", "ID", "IDREF", "IDREFS", "ENTITY", "ENTITIES", "NMTOKEN",
      "NMTOKENS", null, "NOTATION" };
  
  private final PrintWriter w;
  private int openEntityCount = 0;
  private boolean inDoctype = false;

  /**
   * Writes an equivalent version of the doctype declaration
   * on the standard output.
   * If an argument is specified, then that is treated as the filename
   * of the XML document, otherwise the XML document is read from the
   * standard input.
   */
  public static void main(String args[]) throws IOException {
    if (args.length > 1) {
      System.err.println("usage: jview com.jclark.xml.apps.Doctype [file]");
      System.exit(1);
    }
    Parser parser = new ParserImpl();
    parser.setApplication(new Doctype(new PrintWriter(new FileOutputStream(FileDescriptor.out))));
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

  public Doctype(PrintWriter w) {
    this.w = w;
  }

  public void startDocumentTypeDeclaration(StartDocumentTypeDeclarationEvent event) {
    DTD dtd = event.getDTD();
    w.print("<!DOCTYPE " + dtd.getDocumentTypeName() + " ");
    Entity entity = dtd.getEntity(DTD.PARAMETER_ENTITY,
				  DTD.EXTERNAL_SUBSET_NAME);
    if (entity != null) {
      writeExternalId(entity);
      w.println(" [");
    }
    else
      w.println("[");
    inDoctype = true;
  }

  public void endDocumentTypeDeclaration(EndDocumentTypeDeclarationEvent event) {
    inDoctype = false;
    w.println("]>");
    w.flush();
  }

  public void startEntityReference(StartEntityReferenceEvent event) {
    if (inDoctype) {
      if (openEntityCount++ == 0 && !event.getName().equals("#DOCTYPE"))
	w.println("%" + event.getName() + ";");
    }
  }

  public void endEntityReference(EndEntityReferenceEvent event) {
    if (inDoctype)
      --openEntityCount;
  }

  public void processingInstruction(ProcessingInstructionEvent event) {
    if (openEntityCount == 0 && inDoctype)
      w.println("<?" + event.getName() + " " + event.getInstruction() + "?>");
  }

  public void comment(CommentEvent event) {
    if (openEntityCount == 0 && inDoctype)
      w.println("<!--" + event.getComment() + "-->");
  }

  public void markupDeclaration(MarkupDeclarationEvent event) {
    if (openEntityCount != 0)
      return;
    String name = event.getName();
    DTD dtd = event.getDTD();
    switch (event.getType()) {
    case MarkupDeclarationEvent.ELEMENT:
      writeElementDecl(dtd, name);
      break;
    case MarkupDeclarationEvent.GENERAL_ENTITY:
      writeEntityDecl(dtd, DTD.GENERAL_ENTITY, name);
      break;
    case MarkupDeclarationEvent.PARAMETER_ENTITY:
      writeEntityDecl(dtd, DTD.PARAMETER_ENTITY, name);
      break;
    case MarkupDeclarationEvent.NOTATION:
      writeEntityDecl(dtd, DTD.NOTATION, name);
      break;
    case MarkupDeclarationEvent.ATTRIBUTE:
      writeAttributeDef(dtd, name, event.getAttributeName());
      break;
    }
  }

  public void writeEntityDecl(DTD dtd, byte type, String name) {
    switch (type) {
    case DTD.GENERAL_ENTITY:
      w.print("<!ENTITY ");
      break;
    case DTD.PARAMETER_ENTITY:
      w.print("<!ENTITY % ");
      break;
    case DTD.NOTATION:
      w.print("<!NOTATION ");
      break;
    }
    w.print(name + " ");
    Entity entity = dtd.getEntity(type, name);
    String text = entity.getReplacementText();
    if (text == null) {
      writeExternalId(entity);
      if (type == DTD.GENERAL_ENTITY) {
	name = entity.getNotationName();
	if (name != null)
	  w.print(" NDATA " + name);
      }
    }
    else {
      w.print('"');
      for (int i = 0; i < text.length(); i++) {
	char c = text.charAt(i);
	switch (c) {
	case '&':
	case '"':
	case '%':
	case 13:
	  w.print("&#" + (int)c + ";");
	  break;
	default:
	  w.print(c);
	  break;
	}
      }
      w.print('"');
    }
    w.println(">");
  }      

  public void writeElementDecl(DTD dtd, String name) {
    w.println("<!ELEMENT " + name + " " + dtd.getElementType(name).getContentSpec() + ">");
  }

  public void writeAttributeDef(DTD dtd, String name, String attName) {
    AttributeDefinition def
      = dtd.getElementType(name).getAttributeDefinition(attName);
    w.print("<!ATTLIST " + name + " " + attName);
    String type
      = attTypeNames[def.getType()];
    if (type != null)
      w.print(" " + type);
    Enumeration allowEnum = def.allowedValues();
    if (allowEnum != null) {
      w.print(" (" + (String)allowEnum.nextElement());
      while (allowEnum.hasMoreElements())
	w.print((String)allowEnum.nextElement());
      w.print(")");
    }
    String value = def.getDefaultUnnormalizedValue();
    boolean required = def.isRequired();
    if (value == null)
      w.print(required ? " #REQUIRED" : " #IMPLIED");
    else {
      if (required)
	w.print(" #FIXED");
      char lit = value.indexOf('"') < 0 ? '"' : '\'';
      w.print(" " + lit + value + lit);
    }
    w.println(">");
  }

  void writeExternalId(Entity entity) {
    String pub = entity.getPublicId();
    String sys = entity.getSystemId();
    if (pub != null)
      w.print("PUBLIC \"" + pub + "\"");
    if (sys != null) {
      if (pub == null)
	w.print("SYSTEM");
      w.print(' ');
      char lit = sys.indexOf('\"') < 0 ? '"' : '\'';
      w.print(lit);
      w.print(sys);
      w.print(lit);
    }
  }
}
