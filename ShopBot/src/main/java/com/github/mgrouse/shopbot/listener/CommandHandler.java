package com.github.mgrouse.shopbot.listener;

import com.github.mgrouse.shopbot.database.DataBaseTools;
import com.github.mgrouse.shopbot.database.Player;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;


public class CommandHandler
{

    private DataBaseTools m_dBase;

    private SlashCommandInteractionEvent m_event = null;

    private Player m_player = null;

    private String m_message = "";

    public CommandHandler(DataBaseTools dBase)
    {
	m_dBase = dBase;
    }

    public void go(SlashCommandInteractionEvent event)
    {
	parse();
	perform();
	display();
    }

    private void parse()
    {

    }

    void perform()
    {

    }

    private void display()
    {

    }
}
