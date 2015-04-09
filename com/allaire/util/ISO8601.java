package com.allaire.util;


import java.lang.*;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;


/**
 * Signals an integer parsing exception.
 *
 * @author Simeon Simeonov (simeons@allaire.com)
 * @version 1.0
 * @see com.allaire.util.IntegerParser
 */
class IntegerParserException extends Exception
{    
};


/**
 * Parses unsigned integers from a string.
 *
 * @author Simeon Simeonov (simeons@allaire.com)
 * @version 1.0
 */
class IntegerParser
{
    ///////////////////////////////////////////////////////////////////////
    //
    // Implementation data
    //
    ///////////////////////////////////////////////////////////////////////


    private CharacterIterator m_it;
    private int m_radix;
    

    ///////////////////////////////////////////////////////////////////////
    //
    // Construction/Finalization
    //
    ///////////////////////////////////////////////////////////////////////


    private IntegerParser()
    {
        // do nothing
    }


    /**
     * Constructs an IntegerParser to parse numbers in a given radix and
     * binds it to a CharacterIterator.
     *
     * @param it Iterator to input stream.
     * @param radix Radix to convert numbers from.
     */
    public IntegerParser(CharacterIterator it, int radix)
    {
        m_it = it;
        m_radix = radix;
    }
    

    ///////////////////////////////////////////////////////////////////////
    //
    // Operations
    //
    ///////////////////////////////////////////////////////////////////////


    /**
     * Parse an integer at current iterator position.
     *
     * @return Parsed integer.
     * @param minLength Minimum character length of the parsed integer.
     * @param maxLength Maximum character length of the parsed integer.
     * @exception com.allaire.util.IntegerParserException
     *      Signals that an integer could not be parsed given the 
     *      parsing requirements.
     * @see #parseInt(int minLength, int maxLength, char chTerminator)
     * @see #parseInt(int minLength, int maxLength, boolean bCheckTerminator, char chTerminator)
     */
    public int parseInt(int minLength, int maxLength) throws IntegerParserException
    {
        return parseInt(minLength, maxLength, false, '\0');
    }
    
    
    /**
     * Parse an integer at current iterator position.
     *
     * @return Parsed integer.
     * @param minLength Minimum character length of the parsed integer.
     * @param maxLength Maximum character length of the parsed integer.
     * @param chTerminator Character at which parsing must terminate.
     * @exception com.allaire.util.IntegerParserException
     *      Signals that an integer could not be parsed given the 
     *      parsing requirements.
     * @see #parseInt(int minLength, int maxLength)
     * @see #parseInt(int minLength, int maxLength, boolean bCheckTerminator, char chTerminator)
     */
    public int parseInt(int minLength, int maxLength, char chTerminator) throws IntegerParserException
    {
        return parseInt(minLength, maxLength, true, chTerminator);
    }
    
    
    /**
     * Parse an integer at current iterator position.
     *
     * @return Parsed integer.
     * @param minLength Minimum character length of the parsed integer.
     * @param maxLength Maximum character length of the parsed integer.
     * @param bCheckTerminator Should a check for a terminating character be made?
     * @param chTerminator Character at which parsing must terminate.
     * @exception com.allaire.util.IntegerParserException
     *      Signals that an integer could not be parsed given the 
     *      parsing requirements.
     * @see #parseInt(int minLength, int maxLength)
     * @see #parseInt(int minLength, int maxLength, char chTerminator)
     */
    public int parseInt(int minLength, int maxLength, boolean bCheckTerminator, char chTerminator) throws IntegerParserException
    {
        int nValue = 0;
        int nLen = 0;
        char c = m_it.current();
        
        while (c != CharacterIterator.DONE)
        {
            // Get the current digit
            int nDigit = Character.digit(c, m_radix);
            if (nDigit == -1)
            {
                break;
            }
            
            // Update value
            nValue = m_radix * nValue + nDigit;
            
            // Advance
            m_it.next();
            c = m_it.current();
            ++nLen;
        }

        if (nLen < minLength || maxLength < nLen || (bCheckTerminator && c != chTerminator))
        {
            // String too short, bad character was seen, or
            // string is too long, or
            // the terminating character is not valid
            throw new IntegerParserException();
        }
        else
        {
            return nValue;
        }
    }
};


/**
 * Provides ISO8601 date-time processing utilities 
 *
 * @author Simeon Simeonov (simeons@allaire.com)
 * @version 1.0
 */
public class ISO8601
{
    ///////////////////////////////////////////////////////////////////////
    //
    // Operations
    //
    ///////////////////////////////////////////////////////////////////////


    /**
     * Parses a ISO8601 date in full format
     *
     * <p>Some sample ISO8601 date strings:<ul>
     *      <li>1969-07-20T22:56:15-04:00</li>
     *      <li>1969-7-20T22:56:15-4:0</li></ul></p>
     *
     * @return Parsed date or null on failure
     * @param dateString ISO8601 date string
     */
    public static Date parseDate(String dateString)
    {        
        // Start with some believable defaults

        try
        {
            StringCharacterIterator it = new StringCharacterIterator(dateString);
            IntegerParser ip = new IntegerParser(it, 10);

            int year = ip.parseInt(4, 4, '-');
            it.next();

            int month = ip.parseInt(1, 2, '-');
            it.next();

            int day = ip.parseInt(1, 2, 'T');
            it.next();

            int hours = ip.parseInt(1, 2, ':');
            it.next();

            int minutes = ip.parseInt(1, 2, ':');
            it.next();

            int seconds = ip.parseInt(1, 2);

            boolean bHasOffset = false;
            int nRawTimezoneOffset = 0;
            
            char charOffset = it.current();
            if (charOffset != CharacterIterator.DONE)
            {
                if (charOffset != '+' && charOffset != '-')
                {
                    // Error: bad timezone character
                    return null;
                }

                bHasOffset = true;
                it.next();
                
                nRawTimezoneOffset = 60 * ip.parseInt(1, 2, ':');
                it.next();

                nRawTimezoneOffset = 60 * 1000 * (nRawTimezoneOffset + ip.parseInt(1, 2));
                
                if (charOffset == '-')
                {
                    nRawTimezoneOffset *= -1;
                }

                if (it.current() != CharacterIterator.DONE)
                {
                    // Error: garbage at end
                    return null;
                }
            }

            // Get current timezone offset
            // __SIM: need DST adjustment!
            int nCurrentTimezoneOffset = TimeZone.getDefault().getRawOffset();
            
            // Calculate net timezone offset
            int nNetTimezoneOffset = nCurrentTimezoneOffset - nRawTimezoneOffset;
            
            GregorianCalendar gc = new GregorianCalendar(year, month - 1, day, hours, minutes, seconds);
            gc.add(Calendar.MILLISECOND, nNetTimezoneOffset);

            return gc.getTime();
        }
        catch(Exception e)
        {
            return null;
        }
    }
    
    /**
     * Converts a date to ISO8601 date string in full format
     *
     * <p>Some sample ISO8601 date strings:<ul>
     *      <li>1969-07-20T22:56:15-04:00</li>
     *      <li>1969-7-20T22:56:15-4:0</li></ul></p>
     *
     * @return The ISO8601 string representation of the date time value
     * @param date Any date-time value
     * @param useTimezoneInfo Determines whether timezone information should be added
     */
    public static String stringValueOf(Date date, boolean useTimezoneInfo)
    {
        // __SIM: I use deprecated methods of Date
        
        StringBuffer sb = new StringBuffer();
        
        sb.append(date.getYear() + 1900);
        sb.append('-');
        sb.append(date.getMonth() + 1);
        sb.append('-');
        sb.append(date.getDate());
        sb.append('T');
        sb.append(date.getHours());
        sb.append(':');
        sb.append(date.getMinutes());
        sb.append(':');
        sb.append(date.getSeconds());
        
        if (useTimezoneInfo)
        {
            // Get current timezone offset
            // __SIM: need DST adjustment!
            int nCurrentTimezoneOffset = TimeZone.getDefault().getRawOffset();
            
            int nOffsetInMins = nCurrentTimezoneOffset / (60000);
            if (nOffsetInMins < 0)
            {
                sb.append('-');
                nOffsetInMins *= -1;
            }
            else
            {
                sb.append('+');
            }
            
            sb.append(nOffsetInMins / 60);
            sb.append(':');
            sb.append(nOffsetInMins % 60);
        }
        
        return sb.toString();      
    }
}
