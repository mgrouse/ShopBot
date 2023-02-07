package com.github.mgrouse.shopbot.net;

import java.util.ArrayList;
import java.util.List;

import com.github.mgrouse.shopbot.database.Lot;
import com.github.mgrouse.shopbot.database.Lot.TransactionType;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;


public class Inventory
{
    private List<Lot> m_lots = new ArrayList<Lot>();

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
		m_lots.add(new Lot(quantity, itemName, TransactionType.BUY));
	    }
	}

	return retVal;
    }

    public Boolean isEmpty()
    {
	return m_lots.isEmpty();
    }

    // looks through the inventory to see if there are at least
    // lot.size of lot.name objects.
    // sets lot.numOwned = inv.size
    public Boolean hasLot(Lot lot)
    {
	Boolean retVal = false;
	// go thru each item to see if it is the same thing
	for (Lot inv : m_lots)
	{
	    // if the same item
	    if (inv.getName().contentEquals(lot.getName()))
	    {
		// Inventory.size > lot.size?
		if (inv.getSize() >= lot.getSize())
		{
		    // set the numOwned to the amount in inv
		    lot.setNumOwned(inv.getSize());

		    // yep they have at least as many
		    retVal = true;
		    break;
		}
	    }
	}

	return retVal;
    }

    // looks at the inventory to see if objects called lot.name
    // the invLot.size <= lot.NumOwned - lot.size
    // indicating the items to be sold have been removed
    public Boolean hasRemovedLot(Lot lot)
    {
	Boolean retVal = true;

	// go thru each item to see if it is the same Item
	for (Lot inv : m_lots)
	{
	    // if the same item
	    if (inv.getName().contentEquals(lot.getName()))
	    {
		// ?Inventory.size ("CurrentOwned") > lot.NumOwned - lot.size?
		if (inv.getSize() > lot.getNumOwned() - lot.getSize())
		{
		    // yep they still got items they said they would sell
		    retVal = false;
		    break;
		}
	    }
	}

	// if we made it all the way through the loop,
	// they must have gotten rid of at least as many as they promised
	// as they have none.

	return retVal;
    }


}
