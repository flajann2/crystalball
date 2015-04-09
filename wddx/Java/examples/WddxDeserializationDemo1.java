import java.io.StringReader;
import java.io.StringWriter;
import org.xml.sax.InputSource;
import com.allaire.wddx.WddxDeserializer;
import com.allaire.wddx.WddxSerializer;


public class WddxDeserializationDemo1
{
    /**
     * Main entry point.
     */
    public static void main (String args[]) throws Exception
    {
        // This is an example WDDX packet that is quite complex
        String wddxPacket =
            "<wddxPacket version='1.0'><header/>" +
            "<data>" +
            "<array length='11'>" +
              "<string>This is array element #1</string>" +
              "<string>This is array element #2</string>" +
              "<string>This is array element #3</string>" +
              "<string>Some special characters, e.g., " +
                "control characters " +
                "<char code='07'/><char code='0b'/>," + 
                "pre-defined entities &lt;&amp;&gt;, " +
                "and high-ASCII values &#x80;&#xff;." +
              "</string>" +
              "<struct>" +
                "<var name='B'><string>b</string></var>" +
                "<var name='A'><string>a</string></var>" +
              "</struct>" +
              "<string>true</string>" +
              "<number>-12.456</number>" +
              "<dateTime>1996-9-1T2:3:4-5:0</dateTime>" +
              "<recordset rowCount='15' " +
                "fieldNames='MESSAGE_ID,THREAD_ID,USERNAME,POSTED'>" +
                "<field name='MESSAGE_ID'>" +
                  "<number>346.0</number>" +
                  "<number>347.0</number><number>348.0</number>" +
                  "<number>349.0</number><number>350.0</number>" +
                  "<number>352.0</number><number>353.0</number>" +
                  "<number>354.0</number><number>355.0</number>" +
                  "<number>356.0</number><number>357.0</number>" +
                  "<number>358.0</number><number>359.0</number>" +
                  "<number>360.0</number><number>361.0</number>" +
                "</field>" +
                "<field name='THREAD_ID'>" +
                  "<number>162.0</number>" +
                  "<number>162.0</number><number>162.0</number>" +
                  "<number>162.0</number><number>163.0</number>" +
                  "<number>163.0</number><number>163.0</number>" +
                  "<number>164.0</number><number>164.0</number>" +
                  "<number>164.0</number><number>164.0</number>" +
                  "<number>165.0</number><number>165.0</number>" +
                  "<number>166.0</number><number>166.0</number>" +
                "</field>" +
                "<field name='USERNAME'>" +
                  "<string>Frank Johnson</string>" +
                  "<string>Linda Jason</string>" +
                  "<string>Frank Johnson</string>" +
                  "<string>Alan Russell</string>" +
                  "<string>Nick Driscoll</string>" +
                  "<string>Peter Lyden</string>" +
                  "<string>Alvin Langston</string>" +
                  "<string>Jack Anderson</string>" +
                  "<string>Patricia Westerman</string>" +
                  "<string>Dana Adams</string>" +
                  "<string>Patricia Westerman</string>" +
                  "<string>Barbara Glass</string>" +
                  "<string>Andrew Harper</string>" +
                  "<string>Jeff Richards</string>" +
                  "<string>Peter Nelson</string>" +
                "</field>" +
                "<field name='POSTED'>" +
                  "<dateTime>1996-9-27T20:9:4-5:0</dateTime>" +
                  "<dateTime>1996-9-27T20:14:43-5:0</dateTime>" +
                  "<dateTime>1996-9-27T20:19:2-5:0</dateTime>" +
                  "<dateTime>1996-9-27T20:37:27-5:0</dateTime>" +
                  "<dateTime>1996-9-22T21:20:8-5:0</dateTime>" +
                  "<dateTime>1996-9-24T21:22:55-5:0</dateTime>" +
                  "<dateTime>1996-9-25T21:31:21-5:0</dateTime>" +
                  "<dateTime>1996-9-25T21:45:30-5:0</dateTime>" +
                  "<dateTime>1996-7-26T21:51:3-5:0</dateTime>" +
                  "<dateTime>1996-9-27T21:53:57-5:0</dateTime>" +
                  "<dateTime>1996-9-27T21:55:51-5:0</dateTime>" +
                  "<dateTime>1996-9-29T21:58:23-5:0</dateTime>" +
                  "<dateTime>1996-9-30T22:5:51-5:0</dateTime>" +
                  "<dateTime>1996-9-1T22:20:39-5:0</dateTime>" +
                  "<dateTime>1996-9-1T22:24:30-5:0</dateTime>" +
                "</field>" +
              "</recordset>" +
              "<array length='3'>" +
                "<string>1</string>" +
                "<string>2</string>" +
                "<string>3</string>" +
              "</array>" +
              "<array length='3'>" +
                "<array length='1'>" +
                   "<string>1</string>" +
                "</array>" +
                "<array length='2'>" +
                  "<string></string>" +
                  "<string>2</string>" +
                "</array>" +
                "<array length='3'>" +
                  "<string></string>" +
                  "<string></string>" +
                  "<string>3</string>" +
                "</array>" +
              "</array>" +
            "</array>" +
            "</data>" +
            "</wddxPacket>";      
        System.out.println(wddxPacket);

        // Deserialize the packet
        Object result1 = testDeserialization(wddxPacket);
        
        // Output the packet contents
        if (result1 == null)
        {
            System.out.println("Null result1");
        }
        else
        {
            System.out.println(result1.toString());
        }
                
        // Serialize the data
        String output1 = testSerialization(result1);
        
        Object result2 = testDeserialization(output1);
        // Output the result. It should be exactly the same as the packet above
        System.out.println(output1);
        
        /******
           objects are not equal, presumably because equals() needs finer grained
           behavior, e.g. recursing appropriately?
           
        if (result2.equals(result1)) 
        {
            System.out.println("They are equal objects!!! Yea!!!!!!!");
        }
        else
        {
            System.out.println("They are not equal objects!!! Waaaaahh!!!!!!!");
        }
        *****************/
        
        String output2 = testSerialization(result2);
        
        if (output1.equals(output2)) 
        {
            System.out.println("Yea!!!!!!! Serialization results are equal strings!!!");
        }
        else
        {
            System.out.println("Waaaaahh!!!!!!! Serialization results are not equal strings!!!");
        }        
        if (wddxPacket.equals(output2)) 
        {
            System.out.println("Yea!!!!!!! Original and serialization are equal strings!!!");
        }
        else
        {
            System.out.println("Waaaaahh!!!!!!! Original and serialization are not equal strings!!!");
        }        
    }
    
    
    /**
     * Deserialize an arbitrary packet.
     */
    public static Object testDeserialization(String wddxPacket) throws Exception
    {
        // Create an input source (org.xml.sax.InputSource) bound to the packet
        InputSource source = new InputSource(new StringReader(wddxPacket));

        // Create a WDDX deserialiser (com.allaire.wddx.WddxDeserializer)
        // This particular instance will use James Clark's XP parser
        // XP is available from http://www.jclark.com/xml/xp/index.html
        WddxDeserializer deserializer = new WddxDeserializer("com.jclark.xml.sax.Driver");

        // Deserialize the WDDX packet
        Object result = deserializer.deserialize(source);
        
        return result;
    }

    /**
     * Serialize an arbitrary packet.
     */
    public static String testSerialization(Object data) throws Exception
    {
        // Create a WDDX serializer
        WddxSerializer ws = new WddxSerializer();

        // Create a writer to store the generated WDDX
        StringWriter sw = new StringWriter();
        
        // Serialize the data
        ws.serialize(data, sw);
        
        // Return the WDDX packet
        return sw.toString();
    }
}
