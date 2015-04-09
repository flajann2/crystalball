package com.allaire.util;


/**
 * Information about a given character as part of an encoding scheme.
 *
 * @author Simeon Simeonov (simeons@allaire.com)
 * @version 1.0
 * @see com.allaire.util.UTF8Converter
 */
public class SpecialCharInfo
{
    /**
     * Specifies whether this character has a special encoding.
     */
	public boolean isSpecialChar;
	
	/**
	 * Specifies the encoding of a character. Can be null, if isSpecialChar is false.
	 */
	public char[] encoding;
	
	/**
	 * Constructs an instance that is not a special character.
	 */
	SpecialCharInfo()
	{
	    isSpecialChar = false;
	    encoding = null;
	}

	/**
	 * Constructs an instance that is a special character.
	 *
	 * @param b Whether the character has a special encoding.
	 * @param enc The encoding of the character. Can be null, if b is false.
	 */
	SpecialCharInfo(boolean b, char[] enc)
	{
	    isSpecialChar = b;
	    encoding = enc;
	}
}


