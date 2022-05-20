package com.github.mgrouse.shopbot.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mgrouse.shopbot.database.DataBaseTools;
import com.github.mgrouse.shopbot.database.Player;
import com.github.mgrouse.shopbot.database.PlayerCharacter;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


public class BuyCommandHandler
{
    private static Logger m_logger = LoggerFactory.getLogger(ImportCommandHandler.class);

    private DataBaseTools m_dBase;

    private SlashCommandInteractionEvent m_event = null;

    private Player m_player = null;

    private String m_message = "";

    public BuyCommandHandler(DataBaseTools dBase)
    {
	m_dBase = dBase;
    }

    public void doBuy(SlashCommandInteractionEvent event)
    {
	m_event = event;

	// get User's Discord Name (Not the Koni nick Name)
	User user = m_event.getUser();

	String playerName = user.getName();

	// get the parameters from command line for now

	performBuy();

	displayResults();
    }

    // package level function for testing
    void performBuy(String userName, ShoppingList)
    {
	m_dBase = DataBaseTools.getInstance();

	//look up item(s) -- pass in List of names
	// get back list of Items.
	
	// add up price
	
	// check PC's gold amt
	
	// if enough, create success message
	
	// if not enough let them know
	
	//TODO make into a
    }

    private void displayResults()
    {
	// TODO Different Class? make this an Embed and
	// if the m_pc exists, display the avatar

	m_event.getHook().sendMessage(m_message).queue();
    }


}
