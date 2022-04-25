package com.github.mgrouse.shopbot.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mgrouse.shopbot.Secret;
import com.github.mgrouse.shopbot.common.IItem;


//Table Player
// ID, DISCORD_NAME, CURR_CHAR_DNDB_NUM, IN_TRANSACTION
//                     /\
//Table Character      |
// ID, PLAYER_ID, DNDB_NUM, CHAR_NAME, AVATAR_URL,     ??? Currency  ???  PLATINUM, GOLD, ELECTRUM, SILVER, COPPER, TRANS_AMT

//Table Item
// ID, Name, Category, Buy_AMT, Sell_AMT    AMTs are  java.math.BigDecimal

//1 short sword /r
//2 potions/r
//Player p = (player instanceof Player ? (Player) player : null);


public class DataBaseTools
{


    // Production:
    // 104.128.237.201:3306
    // user u61487_krvLrhArJK
    // pass sGhGH=V16sLR=vPiN6aL2=s4
    // jdbc:mysql://u61487_krvLrhArJK:sGhGH=V16sLR=vPiN6aL2=s4@104.128.237.201:3306/s61487_shop_bot

    // TEST:
    // 104.128.237.201:3306
    // User: u61487_caS66SPPqU
    // Pass: h.r!b3bOtw^BD.S3Y7^epCye
    // jdbc:mysql://u61487_caS66SPPqU:h.r!b3bOtw^BD.S3Y7^epCye@104.128.237.201:3306/s61487_shop_bot_test

    public enum DBASE
    {
	PROD, TEST;
    }

    private static Logger m_logger = LoggerFactory.getLogger(DataBaseTools.class);
    private static DataBaseTools m_instance = new DataBaseTools();

    private static Connection m_connection = null;

    private DataBaseTools()
    {
    }

    public static DataBaseTools getInstance()
    {
	return m_instance;
    }

    public void init(DBASE base)
    {
	String connURL = "";
	if (base == DBASE.PROD)
	{
	    connURL = Secret.PROD_URL;
	}

	if (base == DBASE.TEST)
	{
	    connURL = Secret.TEST_URL;
	}


	if (null == m_connection)
	{
	    makeJDBCConnection(connURL);
	}
    }

    public void close()
    {
	if (null != m_connection)
	{
	    closeJDBCConnection();
	    m_connection = null;
	}
    }


    private static Boolean makeJDBCConnection(String url)
    {
	Boolean retVal = false;

	try
	{
	    m_connection = DriverManager.getConnection(url);

	    if (m_connection != null)
	    {
		m_logger.info("Connection Successful! " + url);
		retVal = true;
	    }
	    else
	    {
		m_logger.info("Failed to make connection! " + url);
	    }

	}
	catch (SQLException e)
	{
	    m_logger.info("MySQL Connection Failed!");
	    e.printStackTrace();
	}
	return retVal;
    }

    private static void closeJDBCConnection()
    {
	try
	{
	    m_connection.close();
	}
	catch (SQLException e)
	{
	    m_logger.info("MySQL Connection did not close properly!");
	    e.printStackTrace();
	}
    }


    // Player

    public Player findOrCreatePlayer(String name)
    {
	Player p = readPlayer(name);

	if (null == p)
	{
	    p = new Player();

	    p.setDiscordName(name);
	    p.setCurrCharDNDB_Id("");
	    p.setIsInTransaction(false);

	    p = createPlayer(p);
	}

	return p;
    }

    public Player createPlayer(Player player)
    {
	Player retVal = null;

	if (null != player)
	{
	    String query = "insert into PLAYER(DISCORD_NAME, CURR_CHAR_DNDB_NUM, IN_TRANSACTION) VALUES (?, ?, ?)";

	    PreparedStatement ps;
	    try
	    {
		ps = m_connection.prepareStatement(query);
		ps.setString(1, player.getDiscordName());
		ps.setString(2, player.getCurrCharDNDB_Id());
		ps.setBoolean(3, player.getIsInTransaction());

		int n = ps.executeUpdate();

		if (1 == n)
		{
		    // get player back out of database for the ID number
		    retVal = readPlayer(player.getDiscordName());
		}

	    } // try
	    catch (SQLException e)
	    {
		e.printStackTrace();
	    }
	}

	return retVal;
    }

    public Player readPlayer(String name)
    {
	Player player = null;

	if ((null != name) && (!name.contentEquals("")))
	{
	    String query = "select * from PLAYER where DISCORD_NAME= ?";

	    try
	    {
		PreparedStatement ps = m_connection.prepareStatement(query);

		ps.setString(1, name);

		ResultSet rs = ps.executeQuery();

		while (rs.next())
		{
		    player = new Player();

		    player.setID(rs.getInt("ID"));
		    player.setCurrCharDNDB_Id(rs.getString("CURR_CHAR_DNDB_NUM"));
		    player.setDiscordName(rs.getString("DISCORD_NAME"));
		    player.setIsInTransaction(rs.getBoolean("IN_TRANSACTION"));
		}

	    } // try
	    catch (SQLException e)
	    {
		e.printStackTrace();
	    }
	} // if !null && !""

	return player;
    }

    public Boolean updatePlayer(Player player)
    {
	Boolean retVal = false;

	if (null != player)
	{
	    String query = "update PLAYER set CURR_CHAR_DNDB_NUM=?, DISCORD_NAME=?, IN_TRANSACTION=? where ID=?";

	    try
	    {
		PreparedStatement ps = m_connection.prepareStatement(query);

		ps.setString(1, player.getCurrCharDNDB_Id());
		ps.setString(2, player.getDiscordName());
		ps.setBoolean(3, player.getIsInTransaction());
		ps.setInt(4, player.getID());

		int rows = ps.executeUpdate();

		if (rows > 0)
		{
		    retVal = true;
		}
	    }
	    catch (SQLException e)
	    {
		e.printStackTrace();
	    }

	} // if null
	return retVal;
    }

    public void destroyPlayer(Player player)
    {
	if (null != player)
	{
	    String query = "delete from PLAYER where ID =?";
	    try
	    {
		PreparedStatement ps = m_connection.prepareStatement(query);
		ps.setInt(1, player.getID());
		ps.executeUpdate();
	    }
	    catch (SQLException e)
	    {
		e.printStackTrace();
	    }
	}
    }

    // package level function
    public void deleteAllPlayers()
    {
	try
	{
	    String query = "delete from PLAYER";
	    PreparedStatement ps = m_connection.prepareStatement(query);
	    ps.executeUpdate();

	    query = "ALTER TABLE PLAYER AUTO_INCREMENT = 1";
	    ps = m_connection.prepareStatement(query);
	    ps.executeUpdate();
	}
	catch (SQLException e)
	{
	    e.printStackTrace();
	}
    }


    // Character
    public void associatePlayerAndPC(@Nonnull Player player, @Nonnull PlayerCharacter pc)
    {
	pc.setPlayerId(player.getID());
    }


    public PlayerCharacter getPC(String pName, String dndb_Num)
    {
	PlayerCharacter pc = null;

	Player player = readPlayer(pName);

	if (null != player)
	{

	    String query = "select * from PC where PLAYER_ID = ? and DNDB_NUM = ?";

	    try
	    {
		PreparedStatement ps = m_connection.prepareStatement(query);

		ps.setInt(1, player.getID());
		ps.setString(2, dndb_Num);

		ResultSet rs = ps.executeQuery();

		if (rs.next())
		{
		    pc = new PlayerCharacter();

		    // ID, PLAYER_ID, DNDB_NUM, CHAR_NAME, AVATAR_URL
		    pc = new PlayerCharacter();
		    pc.setID(rs.getInt("ID"));
		    pc.setPlayerId(rs.getInt("PLAYER_ID"));
		    pc.setDNDB_Num(rs.getString("DNDB_NUM"));
		    pc.setName(rs.getString("CHAR_NAME"));
		    pc.setAvatarURL(rs.getString("AVATAR_URL"));
		}
	    }
	    catch (SQLException e)
	    {
		e.printStackTrace();
	    }
	} // if player

	return pc;
    }

    public List<PlayerCharacter> getAllCharactersByPlayerName(String name)
    {
	Player player = readPlayer(name);

	String query = "select * from PC where PLAYER_ID = ?";

	List<PlayerCharacter> ls = new ArrayList<PlayerCharacter>();

	try
	{
	    PreparedStatement ps = m_connection.prepareStatement(query);

	    ps.setInt(1, player.getID());

	    ResultSet rs = ps.executeQuery();

	    PlayerCharacter c = null;

	    while (rs.next())
	    {
		// ID, PLAYER_ID, DNDB_NUM, CHAR_NAME, AVATAR_URL

		c = new PlayerCharacter();
		c.setID(rs.getInt("ID"));
		c.setPlayerId(rs.getInt("PLAYER_ID"));
		c.setDNDB_Num(rs.getString("DNDB_NUM"));
		c.setName(rs.getString("CHAR_NAME"));
		c.setAvatarURL(rs.getString("AVATAR_URL"));

		ls.add(c);
	    }
	}
	catch (SQLException e)
	{
	    e.printStackTrace();
	}

	return ls;
    }


    public PlayerCharacter createCharacter(PlayerCharacter character)
    {
	if (null != character)
	{

	    // ID, PLAYER_ID, DNDB_NUM, CHAR_NAME, AVATAR_URL
	    String query = "insert into PC(PLAYER_ID, DNDB_NUM, CHAR_NAME, AVATAR_URL) VALUES (?, ?, ?, ?)";

	    PreparedStatement ps;

	    try
	    {
		ps = m_connection.prepareStatement(query);

		ps.setInt(1, character.getPlayerID());
		ps.setString(2, character.getDNDB_Num());
		ps.setString(3, character.getName());
		ps.setString(4, character.getAvatarURL());

		int n = ps.executeUpdate();

		if (1 == n)
		{
		    character = readCharacter(character.getDNDB_Num());
		}

	    } // try
	    catch (SQLException e)
	    {
		e.printStackTrace();
	    }
	}

	return character;

    }

    public PlayerCharacter readCharacter(String DNDB_Num)
    {
	String query = "select * from PC where DNDB_NUM = ?";

	PlayerCharacter c = null;

	try
	{
	    PreparedStatement ps = m_connection.prepareStatement(query);

	    ps.setString(1, DNDB_Num);

	    ResultSet rs = ps.executeQuery();

	    if (rs.next())
	    {
		// ID, PLAYER_ID, DNDB_NUM, CHAR_NAME, AVATAR_URL
		c = new PlayerCharacter();

		c.setID(rs.getInt("ID"));
		c.setPlayerId(rs.getInt("PLAYER_ID"));
		c.setDNDB_Num(rs.getString("DNDB_NUM"));
		c.setName(rs.getString("CHAR_NAME"));
		c.setAvatarURL(rs.getString("AVATAR_URL"));
	    }
	}
	catch (SQLException e)
	{
	    e.printStackTrace();
	}

	return c;
    }

    public Boolean updateCharacter(PlayerCharacter character)
    {
	Boolean retVal = false;

	if (null != character)
	{
	    // DNDB_NUM, CHAR_NAME, AVATAR_URL
	    String query = "update PC set DNDB_NUM=?, CHAR_NAME=?, AVATAR_URL=? where ID=?";

	    try
	    {
		PreparedStatement ps = m_connection.prepareStatement(query);

		ps.setString(1, character.getDNDB_Num());
		ps.setString(2, character.getName());
		ps.setString(3, character.getAvatarURL());
		ps.setInt(4, character.getID());

		int rows = ps.executeUpdate();

		if (rows > 0)
		{
		    retVal = true;
		}
	    }
	    catch (SQLException e)
	    {
		e.printStackTrace();
	    }

	} // if ! null
	return retVal;
    }

    public Boolean destroyCharacter(PlayerCharacter character)
    {
	Boolean retVal = false;

	if (null != character)
	{
	    String query = "delete from PC where ID =?";
	    try
	    {
		PreparedStatement ps = m_connection.prepareStatement(query);

		ps.setInt(1, character.getID());

		int rows = ps.executeUpdate();
		if (rows > 0)
		{
		    retVal = true;
		}
	    }
	    catch (SQLException e)
	    {
		e.printStackTrace();
	    }
	}
	return retVal;
    }

    public void deleteAllCharacters()
    {
	try
	{
	    String query = "delete from PC";
	    PreparedStatement ps = m_connection.prepareStatement(query);
	    ps.executeUpdate();

	    query = "ALTER TABLE PC AUTO_INCREMENT = 1";
	    ps = m_connection.prepareStatement(query);
	    ps.executeUpdate();
	}
	catch (SQLException e)
	{
	    e.printStackTrace();
	}

    }


    // Item
    public List<IItem> getAllItems()
    {
	return null;
    }

    public List<String> getCategories()
    {
	return null;
    }

    public List<IItem> getItemsByCategory(String category)
    {
	return null;
    }

    public IItem createItem(IItem ittem)
    {
	return null;
    }

    public IItem readItem(String name)
    {
	return null;
    }

    public void updateItem(IItem ittem)
    {

    }

    public void destroyItem(IItem ittem)
    {

    }


}
