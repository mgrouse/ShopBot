package com.github.mgrouse.shopbot.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;


public class ConfigFile
{
    private final static String m_fileName = "ShopBotConfig.yml";

    public static void createConfigFile()
    {
	ClassLoader loader = ConfigFile.class.getClassLoader();

	try
	{
	    // look for file on file system
	    File file = new File(m_fileName);

	    // if it's not there
	    if (!file.exists())
	    {
		// get RESOURCE config file as a stream
		InputStream stream = loader.getResourceAsStream(m_fileName);

		// get as String
		String result = IOUtils.toString(stream, StandardCharsets.UTF_8);

		// make a new base config file
		file.createNewFile();

		// write the resource to the file
		BufferedWriter writer = new BufferedWriter(new FileWriter(m_fileName));
		writer.write(result);
		writer.close();
	    }
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	}
    }
}
