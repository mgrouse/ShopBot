package com.github.mgrouse.shopbot.listener;

import com.github.mgrouse.shopbot.database.DataBaseTools;
import com.github.mgrouse.shopbot.database.Player;
import com.github.mgrouse.shopbot.database.PlayerCharacter;
import com.github.mgrouse.shopbot.net.NetTools;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;


public class CommandHandler extends ListenerAdapter
{

    public CommandHandler()
    {

    }


    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
    {
	switch (event.getName())
	{
	    case "import":
	    {
		handleImport(event);
	    }
	    default:
	    {
		// log error
	    }
	}
    }


    // TODO make test case for this
    private void handleImport(SlashCommandInteractionEvent event)
    {
	// get the argument. PC ID = DNDB_NUM
	String dndb_Num = event.getOption("PC ID").getAsString();

	// get User's Discord Name (Not the Koni nick Name)
	User user = event.getUser();

	String pName = user.getName();

	PlayerCharacter pc = performImport(pName, dndb_Num);

	outPutImportResults(pc, event.getChannel());
    }


    public PlayerCharacter performImport(String pName, String dndb_Num)
    {

	PlayerCharacter dBasePC, webPC;
	DataBaseTools dBase = DataBaseTools.getInstance();

	// get the PC from the net if any
	webPC = NetTools.getDndbPlayerCharacter(dndb_Num);

	if (null == webPC)
	{
	    // message user that character not found
	    // message = "PC with Id = " + dndb_Num + " was not found.";
	}
	else
	{
	    // Get PC with Player's name and PC's DNDB_NUM
	    dBasePC = dBase.getPC(pName, dndb_Num);

	    // If PC is in database,
	    if (null != dBasePC)
	    {
		// Update dBaseCharacter
		dBasePC.setAvatarURL(webPC.getAvatarURL());
		dBasePC.setName(webPC.getName());

		dBase.updateCharacter(dBasePC);

		// message user that character updated
		// message = webPC.getName() + " was upated.";
	    }
	    else
	    { // PC is not in DBase

		// get PC from Database (if they are not there insert them)
		Player player = dBase.findOrCreatePlayer(pName);

		// Link the PC to the Player
		dBase.associatePlayerAndPC(player, webPC);

		// Update Player
		dBase.updatePlayer(player);

		// put PC in Database
		dBase.createCharacter(webPC);

		// message user that character was imported
		// message = webPC.getName() + " was imported.";
	    }
	} // webPC found

	return webPC;
    }

    private void outPutImportResults(PlayerCharacter pc, MessageChannel channel)
    {
	if (null == pc)
	{
	    channel.sendMessage("No character found.").queue();
	}
	else
	{
	    channel.sendMessage("Character named: " + pc.getName() + " was imported or updated.").queue();
	}
    }

}
