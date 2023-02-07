package com.github.mgrouse.shopbot.listener;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mgrouse.shopbot.database.DataBaseTools;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


public class RemoveCommandHandler extends CommandHandler
{
    private static Logger m_logger = LoggerFactory.getLogger(ImportCommandHandler.class);

    private SlashCommandInteractionEvent m_event = null;

    // protected DataBaseTools m_dBase;

    // protected String m_message = "";

    // protected Player m_player = null;

    // protected PlayerCharacter m_pc = null;


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

    AppError performRemove(String playerName, String pcName)
    {
	AppError err = validatePlayerAndNamedPC(playerName, pcName);

	if (AppError.NONE != err)
	{
	    m_message = err.message();
	    return err;
	}

	// if the Player.CurrPC == the PC to remove
	if (m_player.getActiveDNDB_Id().contentEquals(m_pc.getDNDB_Num()))
	{
	    // clear the CurrPC field
	    m_player.setActiveDNDB_Id("");

	    // clear any transaction as it was for this PC
	    m_player.setBill(new BigDecimal("0.00"));
	    m_player.setCash(new BigDecimal("0.00"));

	    // update player in DB
	    m_dBase.updatePlayer(m_player);
	}

	m_message = "The PC " + m_pc.getName() + " has been removed from the ShopBot system";

	// get rid of the PC
	m_dBase.destroyCharacter(m_pc);

	return AppError.NONE;
    }


    private void display()
    {
	// TODO Different Class? make this an Embed and
	// if the m_pc exists, display the avatar

	m_event.getHook().sendMessage(m_message).queue();
    }

}
