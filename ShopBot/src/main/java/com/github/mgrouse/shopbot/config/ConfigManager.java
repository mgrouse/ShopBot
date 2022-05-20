package com.github.mgrouse.shopbot.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;


public class ConfigManager
{
    private final static String m_fileName = "ShopBotConfig.yml";

    private static Properties m_props = null;

    private static void loadValues()
    {
	if (null == m_props)
	{
	    try
	    {
		InputStream iStream = new FileInputStream(m_fileName);

		m_props.load(iStream);

		iStream.close();
	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }
	}

    }

    public static String getValue(String key)
    {
	String retVal = "";
	if (null != key)
	{
	    if (null == m_props)
	    {
		loadValues();
	    }
	    retVal = m_props.getProperty(key);
	}
	return retVal;
    }

    public static void setValue(String key, String value)
    {
	if (null == m_props)
	{
	    loadValues();
	}

	if ((null != key) && (null != value) && !key.isBlank() && !value.isBlank())
	{
	    m_props.setProperty(key, value);
	}
    }

    public static void saveValues()
    {
	if (null != m_props)
	{
	    try
	    {
		OutputStream oStream = new FileOutputStream(m_fileName);

		m_props.store(oStream, null);

		oStream.close();
	    }
	    catch (Exception e)
	    {
		e.printStackTrace();
	    }
	}
    }
}
