package com.github.mgrouse.shopbot.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mgrouse.shopbot.database.DataBaseTools;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


public class ItemCommandHandler extends CommandHandler
{
    private static Logger m_logger = LoggerFactory.getLogger(ItemCommandHandler.class);

    private SlashCommandInteractionEvent m_event = null;


    public ItemCommandHandler(DataBaseTools dBase)
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
	// get the argument. 'id' = DNDB_NUM
	String dndb_Num = m_event.getOption("id").getAsString();

	// get User's Discord Name (Not the Koni nick Name)
	String pName = m_event.getUser().getName();

	performImport(pName, dndb_Num);
    }

    // package function for testing
    AppError performImport(String pName, String dndb_Num)
    {
	return null;

    }

    private void display()
    {
	// TODO Different Class? make this an Embed and
	// if the m_pc exists, display the avatar

	m_event.getHook().sendMessage(m_message).queue();
    }
}
