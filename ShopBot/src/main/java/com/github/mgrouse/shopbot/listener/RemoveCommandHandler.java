package com.github.mgrouse.shopbot.listener;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mgrouse.shopbot.database.DataBaseTools;
import com.github.mgrouse.shopbot.database.Player;
import com.github.mgrouse.shopbot.database.PlayerCharacter;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


public class RemoveCommandHandler
{
    private static Logger m_logger = LoggerFactory.getLogger(ImportCommandHandler.class);

    DataBaseTools m_dBase;

    private SlashCommandInteractionEvent m_event = null;

    private String m_message = "";

    private Player m_player = null;

    private PlayerCharacter m_pc = null;


    enum RemoveError
    {
	NONE, NO_PLAYER, NO_PC_DB;
    }

    public RemoveCommandHandler(DataBaseTools dBase)
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
	// get the argument. 'name' = name of the PC to remove
	String pcName = m_event.getOption("name").getAsString();

	// get User's Discord Name (Not the Koni nick Name)
	String playerName = m_event.getUser().getName();

	performRemove(playerName, pcName);
    }

    RemoveError performRemove(String playerName, String pcName)
    {
	RemoveError err = validate(playerName, pcName);

	if (RemoveError.NONE != err)
	{
	    return err;
	}

	// if the Player.CurrPC == the PC to remove
	if (m_player.getCurrCharDNDB_Id().contentEquals(m_pc.getDNDB_Num()))
	{
	    // clear the CurrPC field
	    m_player.setCurrCharDNDB_Id("");

	    // clear the transaction as it was for this PC
	    m_player.setBill(new BigDecimal("0.00"));
	    m_player.setCash(new BigDecimal("0.00"));
	}

	// update player in DB
	m_dBase.updatePlayer(m_player);

	// get rid of the PC
	m_dBase.destroyCharacter(m_pc);

	return RemoveError.NONE;
    }

    private RemoveError validate(String playerName, String pcName)
    {
	// look to see if there is a Player
	m_player = m_dBase.readPlayer(playerName);

	if (null == m_player)
	{
	    m_message = "You do not have any PC's in the ShopBot system.";
	    return RemoveError.NO_PLAYER;
	}

	// look to see if there is a PC
	m_pc = m_dBase.getPCByPlayerNameAndPCName(playerName, pcName);

	if (null == m_pc)
	{
	    m_message = pcName + " is not in the ShopBot system.";
	    return RemoveError.NO_PC_DB;
	}

	return RemoveError.NONE;
    }

    private void display()
    {
	// TODO Different Class? make this an Embed and
	// if the m_pc exists, display the avatar

	m_event.getHook().sendMessage(m_message).queue();
    }

}
