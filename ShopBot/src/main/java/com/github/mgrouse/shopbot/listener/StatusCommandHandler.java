package com.github.mgrouse.shopbot.listener;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mgrouse.shopbot.database.DataBaseTools;
import com.github.mgrouse.shopbot.database.Lot;
import com.github.mgrouse.shopbot.database.PlayerCharacter;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


public class StatusCommandHandler extends CommandHandler
{
    private static Logger m_logger = LoggerFactory.getLogger(ImportCommandHandler.class);

    private SlashCommandInteractionEvent m_event = null;

    // protected DataBaseTools m_dBase;

    // protected String m_message = "";

    // protected Player m_player = null;

    // protected PlayerCharacter m_pc = null;

    List<PlayerCharacter> allPCs = null;


    public StatusCommandHandler(DataBaseTools dBase)
    {
	m_dBase = dBase;
    }

    public void go(SlashCommandInteractionEvent event)
    {
	m_event = event;
	parse();
	display();
    }

    public void parse()
    {
	// get User's Discord Name (Not the Koni nick Name)
	String pName = m_event.getUser().getName();

	performStatus(pName);
    }

    AppError performStatus(String pName)
    {
	StringBuilder string = new StringBuilder();

	// check to see if there is a player
	AppError err = validatePlayer(pName);

	if (AppError.NONE != err)
	{
	    // Here there is no point in continuing as
	    // there is no player to use to retrieve anything with
	    m_message = err.message();
	    return err;
	}

	// check to see if there is an Active PC listed
	err = validateActivePCIfAny();

	if (AppError.NONE != err)
	{
	    // Here we start documenting what is going on
	    string.append(err.message());
	    return err;
	}

	// if so, check for All PCs.
	allPCs = m_dBase.getAllCharactersByPlayerName(pName);

	string.append("You have " + allPCs.size() + " PCs in the system.\n");
	for (PlayerCharacter pc : allPCs)
	{
	    // start string builder representation of this PC
	    string.append(pc.getName() + ":");

	    if ((null != m_pc) && (pc.getName().contentEquals(m_pc.getName())))
	    {
		string.append(" (Active)");
	    }
	    // check to see if pc is on D&DB
	    err = validatePCOnDNDB(pc.getDNDB_Num());

	    if (err == AppError.NONE)
	    {
		string.append(" Found on D&DB(" + pc.getDNDB_Num() + ").\n");
	    }
	    else
	    {
		string.append(" Not found on D&DB(" + pc.getDNDB_Num() + ").\n");
	    }

	}

	// if there is a buy transaction in progress
	if (m_player.hasTransaction())
	{
	    string.append("You are currently in the middle of a BUY transaction:\n");
	    string.append("You have a 'Cash Record' of:" + m_player.getCash() + " gp.\n");
	    string.append("You have a 'Bill' of:" + m_player.getBill() + " gp.\n");
	}

	// if there is a sell transaction in progress
	m_lots = m_dBase.getSellLotsByPlayer(pName);

	if (m_lots.size() > 0)
	{
	    string.append("You are currently in the middle of a SELL transaction.\n");
	}

	for (Lot lot : m_lots)
	{
	    string.append("Item: " + lot.getName());
	    string.append(", Number Owned: " + lot.getNumOwned());

	    string.append(", Number to Sell: " + lot.getSize() + ".\n");
	}

	m_message = string.toString();
	return AppError.NONE;
    }

    String getMessage()
    {
	return m_message;
    }

    private void display()
    {
	// TODO Different Class? make this an Embed and
	// if the m_pc exists, display the avatar

	m_event.getHook().sendMessage(m_message).queue();
    }
}
