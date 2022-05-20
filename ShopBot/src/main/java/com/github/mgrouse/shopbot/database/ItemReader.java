package com.github.mgrouse.shopbot.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class ItemReader
{
    private String m_fileName = "Items.csv";;

    DataBaseTools m_dBase;

    public ItemReader(DataBaseTools dBase)
    {
	m_dBase = dBase;
    }

    public void readItems(String fileName)
    {
	if (null != fileName)
	{
	    m_fileName = fileName;
	}

	// look for the file
	File file = new File(m_fileName);

	// if it's there
	if (file.exists())
	{
	    // Delete All Items
	    m_dBase.deleteAllItems();

	    // Open the file
	    try
	    {
		BufferedReader reader = new BufferedReader(new FileReader(file));

		// perform the read and insert
		performReadItems(reader);
	    }
	    catch (FileNotFoundException e)
	    {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	} // if file
    }


    // package level function for testing
    void performReadItems(BufferedReader reader)
    {
	String line;
	Item item;

	// read a line
	try
	{
	    line = reader.readLine();

	    // While not at end
	    while ((null != line) && (!line.isBlank()))
	    {
		// parse
		String values[] = line.split(",");

		// create Item
		item = new Item();
		item.setName(values[0]);
		item.setCategory(values[1]);
		item.setBuyAmt(values[2]);
		item.setSellAmt(values[3]);

		// Store in DB
		m_dBase.createItem(item);

		// read a line
		line = reader.readLine();
	    }

	    // Close file
	    reader.close();
	}
	catch (IOException e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
}
