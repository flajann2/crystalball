package com.allaire.util;



/**
 * UTF-8 XML character encoding converter.
 *
 * <p>This class provides a table of character translations between plain
 * ASCII values and UTF8 characters and character references, combined
 * with XML escapes of characters such as <, >, and ?.</p>
 *
 * @author Simeon Simeonov (simeons@allaire.com)
 * @version 1.0
 * @see com.allaire.util.SpecialCharInfo
 */
public class UTF8Converter extends java.lang.Object
{
    /**
     * Table of character information.
     */
    private SpecialCharInfo m_charInfo[];
    
    
    /**
     * Constructs the converter and initializes the table of characters. 
     */
    public UTF8Converter()
    {
        m_charInfo = new SpecialCharInfo[256];
        
    	// Characters in the range 0 - 31

        String charImage = new String("<char code='00'/>");
    	for (int i = 0; i < 32; ++i)
    	{
    	    SpecialCharInfo info = new SpecialCharInfo();
    	    info.isSpecialChar = true;
    	    info.encoding = charImage.toCharArray();
    		info.encoding[12] = Character.forDigit(i / 16, 16);
    		info.encoding[13] = Character.forDigit(i % 16, 16);
    		m_charInfo[i] = info;
    	}

    	// Basic initialization of UTF8 characters

    	for (int i = 32; i < 128; ++i)
    	{
    	    SpecialCharInfo info = new SpecialCharInfo();
    	    info.isSpecialChar = false;
    	    info.encoding = new Character((char)i).toString().toCharArray();
    		m_charInfo[i] = info;
    	}

    	// Characters in the range 128-255

    	charImage = new String("&#x00;");
    	for (int i = 128; i < 256; ++i)
    	{
    	    SpecialCharInfo info = new SpecialCharInfo();
    	    info.isSpecialChar = true;
    	    info.encoding = charImage.toCharArray();
    		info.encoding[3] = Character.forDigit(i / 16, 16);
    		info.encoding[4] = Character.forDigit(i % 16, 16);;
    		m_charInfo[i] = info;
    	}

    	// Override for specially defined characters

   	    SpecialCharInfo info;
   	    
   	    info = new SpecialCharInfo();
    	info.isSpecialChar = false;
    	info.encoding = null;
    	m_charInfo['\t'] = info;
    	m_charInfo['\n'] = info;

   	    info = new SpecialCharInfo();
    	info.isSpecialChar = true;
    	info.encoding = new String("&lt;").toCharArray();
    	m_charInfo['<'] = info;
   	    
   	    info = new SpecialCharInfo();
    	info.isSpecialChar = true;
    	info.encoding = new String("&gt;").toCharArray();
    	m_charInfo['>'] = info;
   	    
   	    info = new SpecialCharInfo();
    	info.isSpecialChar = true;
    	info.encoding = new String("&amp;").toCharArray();
    	m_charInfo['&'] = info;
    }

    /** 
     * Retrieves the character information table.
     *
     * @return Character information table.
     */
    public SpecialCharInfo[] getCharInfo()
    {
        return m_charInfo;
    }
}
