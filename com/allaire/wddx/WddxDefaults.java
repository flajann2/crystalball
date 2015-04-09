package com.allaire.wddx;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;

/**
 * Provides WDDX access to the default WDDX settings.
 *
 * @author Spike Washburn (spike@allaire.com)
 * @version 1.0
 */

class WddxDefaults{
    ///////////////////////////////////////////////////////////////////////
    //
    // Implementation data
    //
    ///////////////////////////////////////////////////////////////////////
    static private Properties m_properties = new Properties();


    ///////////////////////////////////////////////////////////////////////
    //
    // Construction/Finalization
    //
    ///////////////////////////////////////////////////////////////////////
    static{
        //load the default wddx properties from the SDK property file for this JVM version
        String version = System.getProperty("java.version");
        int index = version.indexOf('.');
        int majorVersion = new Integer(version.substring(0,index)).intValue();
        int index2 = version.indexOf('.', index+1);
        int minorVersion = new Integer(version.substring(index+1, index2)).intValue();
        String propsLoc = "/com/allaire/wddx/wddx_java" + majorVersion + "_" + minorVersion + ".properties";
        try{
            //use the classloader to obtain a relative reference to the properties file.
            InputStream in = WddxDefaults.class.getResourceAsStream(propsLoc);
            while(in == null && majorVersion >= 1){
                //decrement through the JDK versions until a default set of properties are found.
                if(minorVersion == 0){
                    majorVersion--;
                    minorVersion = 9;
                }
                minorVersion--;
                propsLoc = "/com/allaire/wddx/wddx_java" + majorVersion + "_" + minorVersion + ".properties";
                in = WddxDefaults.class.getResourceAsStream(propsLoc);
            }
            if(in == null){
                throw new IOException("Missing default serializer properties");
            }
            m_properties.load(in);
        }
        catch(IOException e){
            System.err.println("Could not read default serializer properties: " + propsLoc);
            e.printStackTrace();
        }
    }

    //private constructor to prevent instances of this static class from being created.
    private WddxDefaults(){
        //no instances
    }


    ///////////////////////////////////////////////////////////////////////
    //
    // Operations
    //
    ///////////////////////////////////////////////////////////////////////

    /**
     * Get default Wddx properties configuration.
     * The Wddx properties are loaded based on the version of the current JVM.
     *  
     * @return the default Wddx properties
     */
    public static Properties getProperties(){
        return m_properties;
    }

    /**
     * Get the value of the specified property.
     *
     * @return the value of the specified property or null if it does not exist
     */
    public static String getProperty(String name){
        return m_properties.getProperty(name);
    }
}
