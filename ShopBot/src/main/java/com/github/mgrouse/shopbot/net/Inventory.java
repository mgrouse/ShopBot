package com.github.mgrouse.shopbot.net;

import java.util.ArrayList;
import java.util.List;

import com.github.mgrouse.shopbot.database.Lot;
import com.github.mgrouse.shopbot.database.Lot.TransactionType;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;


public class Inventory
{
    List<Lot> m_lots = new ArrayList<Lot>();

    Inventory()
    {

    }

    public Boolean load(JsonArray inventory)
    {
	Boolean retVal = false;

	Boolean found;

	// loop thru PC's items
	for (JsonElement item : inventory)
	{
	    found = false;
	    Integer quantity = item.getAsJsonObject().get("quantity").getAsInt();
	    String itemName = item.getAsJsonObject().get("definition").getAsJsonObject().get("name").getAsString();

	    // loop thru our lots
	    for (Lot lot : m_lots)
	    {
		// if the same item
		if (lot.getName().contentEquals(itemName))
		{
		    found = true;

		    // add amt to the current lot
		    lot.setSize(lot.getSize() + quantity);


		    break;
		}
	    }

	    // if we looped thru all our m_lots and did not find it
	    if (found == false)
	    {
		// add it
		m_lots.add(new Lot(quantity, itemName, TransactionType.PURCHASE));
	    }
	}

	return retVal;
    }

    public Boolean isEmpty()
    {
	return m_lots.isEmpty();
    }

    public Boolean hasLot(Lot lot)
    {
	Boolean retVal = false;

	// go thru each item to see if it is the same thing and enough
	for (Lot l : m_lots)
	{
	    // if the same item
	    if (l.getName().contentEquals(lot.getName()))
	    {
		// and >= amount
		if (l.getSize() >= lot.getSize())
		{
		    // set the Num Owned
		    lot.setNumOwned(l.getSize());
		    retVal = true;
		    break;
		}
	    }
	}

	return retVal;
    }
}
