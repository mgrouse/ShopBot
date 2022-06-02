package com.github.mgrouse.shopbot.database;

import java.math.BigDecimal;
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


//Table Player
// ID, DISCORD_NAME, CURR_CHAR_DNDB_NUM, CASH, BILL
//                     |
//Table PC            \|/
// ID, PLAYER_ID, DNDB_NUM, NAME, AVATAR_URL

//Table Item
// ID, Name, Category, Buy_AMT, Sell_AMT    AMTs are  java.math.BigDecimal

//1 short sword /r
//2 potions/r
//Player p = (player instanceof Player ? (Player) player : null);


public class DataBaseTools
{

    public enum DBASE
    {
	PROD, TEST;
    }

    private static Logger m_logger = LoggerFactory.getLogger(DataBaseTools.class);

    private static DataBaseTools m_instance = new DataBaseTools();

    private static DBASE m_base = null;

    private static Connection m_connection = null;


    private DataBaseTools()
    {
    }

    public static DataBaseTools getInstance()
    {
	return m_instance;
    }

    public static void init(DBASE base)
    {
	String connURL = "";

	// if m_base is not equal to param
	if (m_base != base)
	{
	    m_base = base;

	    if (base == DBASE.PROD)
	    {
		connURL = Secret.PROD_URL;
	    }

	    if (base == DBASE.TEST)
	    {
		connURL = Secret.TEST_URL;
	    }

	    makeJDBCConnection(connURL);
	}

    }

    public static void close()
    {
	if (null != m_connection)
	{
	    closeJDBCConnection();
	    m_connection = null;
	}
    }


    private static void makeJDBCConnection(String url)
    {
	try
	{
	    // if there is already a connection, call close()
	    if (m_connection != null)
	    {
		close();
	    }

	    m_connection = DriverManager.getConnection(url);

	    if (m_connection != null)
	    {
		m_logger.info("Connection Successful! " + url);
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


    // Player ===================================

    public Player findOrCreatePlayer(String name)
    {
	Player p = readPlayer(name);

	if (null == p)
	{
	    p = new Player();

	    p.setDiscordName(name);
	    p.setCurrCharDNDB_Id("");
	    p.setCash(new BigDecimal("0.00"));
	    p.setBill(new BigDecimal("0.00"));

	    p = createPlayer(p);
	}

	return p;
    }


    public Player createPlayer(@Nonnull Player player)
    {
	Player retVal = null;

	String query = "insert into PLAYER(DISCORD_NAME, CURR_CHAR_DNDB_NUM, CASH, BILL) VALUES (?, ?, ?, ?)";

	PreparedStatement ps;
	try
	{
	    ps = m_connection.prepareStatement(query);
	    ps.setString(1, player.getDiscordName());
	    ps.setString(2, player.getCurrCharDNDB_Id());
	    ps.setString(3, player.getCash().toString());
	    ps.setString(4, player.getBill().toString());

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
	return retVal;
    }

    public Player readPlayer(@Nonnull String name)
    {
	Player player = null;

	if (!name.contentEquals(""))
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
		    player.setCash(new BigDecimal(rs.getString("CASH")));
		    player.setBill(new BigDecimal(rs.getString("BILL")));
		}

	    } // try
	    catch (SQLException e)
	    {
		e.printStackTrace();
	    }
	} // if !""

	return player;
    }

    public Boolean updatePlayer(@Nonnull Player player)
    {
	Boolean retVal = false;

	String query = "update PLAYER set CURR_CHAR_DNDB_NUM=?, DISCORD_NAME=?, CASH=?, BILL=? where ID=?";

	try
	{
	    PreparedStatement ps = m_connection.prepareStatement(query);

	    ps.setString(1, player.getCurrCharDNDB_Id());
	    ps.setString(2, player.getDiscordName());
	    ps.setString(3, player.getCash().toString());
	    ps.setString(4, player.getBill().toString());
	    ps.setInt(5, player.getID());

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

	return retVal;
    }

    public void destroyPlayer(@Nonnull Player player)
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


    // Player and Character ====================================

    public void associatePlayerAndPC(@Nonnull Player player, @Nonnull PlayerCharacter pc)
    {
	pc.setPlayerId(player.getID());
    }


    public PlayerCharacter getPC(@Nonnull String pName, @Nonnull String dndb_Num)
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


    public PlayerCharacter getPCByPlayerNameAndPCName(@Nonnull String playerName, @Nonnull String pcName)
    {
	PlayerCharacter retVal = null;

	List<PlayerCharacter> pcList = getAllCharactersByPlayerName(playerName);

	// get first word in argument pcName
	String pcNameWord = pcName.split(" ")[0];

	String pcDBWord = "";

	for (PlayerCharacter pc : pcList)
	{
	    // get first word in pc.name
	    pcDBWord = pc.getName().split(" ")[0];

	    // if they match assign the PC and continue
	    if (pcDBWord.contentEquals(pcNameWord))
	    {
		retVal = pc;
		continue;
	    }

	} // for

	return retVal;
    }

    // TODO test this
    public PlayerCharacter getActivePcByPlayerName(@Nonnull String name)
    {
	PlayerCharacter pc = null;

	if (null != name)
	{
	    Player player = readPlayer(name);

	    if (player != null)
	    {
		pc = readCharacter(player.getCurrCharDNDB_Id());
	    }
	}
	return pc;
    }


    public List<PlayerCharacter> getAllCharactersByPlayerName(@Nonnull String name)
    {

	List<PlayerCharacter> ls = new ArrayList<PlayerCharacter>();

	Player player = readPlayer(name);

	if (null != player)
	{
	    try
	    {
		String query = "select * from PC where PLAYER_ID = ?";
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

	} // if player

	return ls;
    }

    // Character CRUD ====================================


    public PlayerCharacter createCharacter(@Nonnull PlayerCharacter character)
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

	return character;
    }

    public PlayerCharacter readCharacter(@Nonnull String DNDB_Num)
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

    public Boolean updateCharacter(@Nonnull PlayerCharacter character)
    {
	Boolean retVal = false;

	// DNDB_NUM, CHAR_NAME, AVATAR_URL
	String query = "update PC set PLAYER_ID=?, DNDB_NUM=?, CHAR_NAME=?, AVATAR_URL=? where ID=?";

	try
	{
	    PreparedStatement ps = m_connection.prepareStatement(query);

	    ps.setInt(1, character.getPlayerID());
	    ps.setString(2, character.getDNDB_Num());
	    ps.setString(3, character.getName());
	    ps.setString(4, character.getAvatarURL());
	    ps.setInt(5, character.getID());

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

	return retVal;
    }

    public Boolean destroyCharacter(@Nonnull PlayerCharacter character)
    {
	Boolean retVal = false;

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


    // Item ================================


    public List<Item> getAllItems()
    {
	List<Item> retVal = new ArrayList<Item>();

	Item temp = null;

	String query = "select * from ITEM";

	try
	{
	    PreparedStatement ps = m_connection.prepareStatement(query);

	    ResultSet rs = ps.executeQuery();

	    while (rs.next())
	    {
		// ID, NAME, CATEGORY, BUY_AMT, SELL_AMT
		temp = new Item();

		temp.setID(rs.getInt("ID"));
		temp.setName(rs.getString("NAME"));
		temp.setCategory(rs.getString("CATEGORY"));
		temp.setBuyAmt(new BigDecimal(rs.getString("BUY_AMT")));
		temp.setSellAmt(new BigDecimal(rs.getString("SELL_AMT")));

		retVal.add(temp);
	    }
	    retVal.sort(null);
	}
	catch (SQLException e)
	{
	    e.printStackTrace();
	}
	return retVal;
    }


    public List<String> getCategories()
    {
	List<String> retVal = new ArrayList<String>();

	String temp = null;

	String query = "SELECT DISTINCT(CATEGORY)from ITEM";

	try
	{
	    PreparedStatement ps = m_connection.prepareStatement(query);

	    ResultSet rs = ps.executeQuery();

	    while (rs.next())
	    {
		// ID, NAME, CATEGORY, BUY_AMT, SELL_AMT
		temp = rs.getString("CATEGORY");

		retVal.add(temp);
	    }
	    retVal.sort(null);
	}
	catch (SQLException e)
	{
	    e.printStackTrace();
	}
	return retVal;
    }


    public List<Item> getItemsByCategory(String category)
    {
	List<Item> retVal = new ArrayList<Item>();

	Item temp = null;

	String query = "select * from ITEM where CATEGORY = '" + category + "'";

	try
	{
	    PreparedStatement ps = m_connection.prepareStatement(query);

	    ResultSet rs = ps.executeQuery();

	    while (rs.next())
	    {
		// ID, NAME, CATEGORY, BUY_AMT, SELL_AMT
		temp = new Item();

		temp.setID(rs.getInt("ID"));
		temp.setName(rs.getString("NAME"));
		temp.setCategory(rs.getString("CATEGORY"));
		temp.setBuyAmt(new BigDecimal(rs.getString("BUY_AMT")));
		temp.setSellAmt(new BigDecimal(rs.getString("SELL_AMT")));

		retVal.add(temp);
	    }
	    retVal.sort(null);
	}
	catch (SQLException e)
	{
	    e.printStackTrace();
	}
	return retVal;
    }


    public void deleteAllItems()
    {
	try
	{
	    String query = "delete from ITEM";
	    PreparedStatement ps = m_connection.prepareStatement(query);
	    ps.executeUpdate();

	    query = "alter table ITEM AUTO_INCREMENT = 1";
	    ps = m_connection.prepareStatement(query);
	    ps.executeUpdate();
	}
	catch (SQLException e)
	{
	    e.printStackTrace();
	}
    }

    public Item createItem(Item item)
    {
	Item retVal = null;

	// ID, NAME, CATEGORY, BUY_AMT, SELL_AMT
	String query = "insert into ITEM(NAME, CATEGORY, BUY_AMT, SELL_AMT) VALUES (?, ?, ?, ?)";

	try
	{
	    PreparedStatement ps = m_connection.prepareStatement(query);

	    ps.setString(1, item.getName());
	    ps.setString(2, item.getCategory());
	    ps.setString(3, item.getBuyAmt().toString());
	    ps.setString(4, item.getSellAmt().toString());

	    int n = ps.executeUpdate();

	    if (1 == n)
	    {
		retVal = readItem(item.getName());
	    }

	} // try
	catch (SQLException e)
	{
	    e.printStackTrace();
	}


	return retVal;
    }

    public Item readItem(String name)
    {
	String query = "select * from ITEM where NAME like '%" + name + "%' ";

	Item retVal = null;

	try
	{
	    PreparedStatement ps = m_connection.prepareStatement(query);

	    // ps.setString(1, name);

	    ResultSet rs = ps.executeQuery();

	    if (rs.next())
	    {
		// ID, NAME, CATEGORY, BUY_AMT, SELL_AMT
		retVal = new Item();

		retVal.setID(rs.getInt("ID"));
		retVal.setName(rs.getString("NAME"));
		retVal.setCategory(rs.getString("CATEGORY"));
		retVal.setBuyAmt(new BigDecimal(rs.getString("BUY_AMT")));
		retVal.setSellAmt(new BigDecimal(rs.getString("SELL_AMT")));
	    }
	}
	catch (SQLException e)
	{
	    e.printStackTrace();
	}

	return retVal;
    }

    public Boolean updateItem(Item item)
    {
	Boolean retVal = false;

	// ID, NAME, CATEGORY, BUY_AMT, SELL_AMT
	String query = "update ITEM set NAME=?, CATEGORY=?, BUY_AMT=?, SELL_AMT=? where ID=?";

	try
	{
	    PreparedStatement ps = m_connection.prepareStatement(query);

	    ps.setString(1, item.getName());
	    ps.setString(2, item.getCategory());
	    ps.setString(3, item.getBuyAmt().toString());
	    ps.setString(4, item.getSellAmt().toString());

	    ps.setInt(5, item.getID());

	    int rows = ps.executeUpdate();

	    if (rows == 1)
	    {
		retVal = true;
	    }
	}
	catch (SQLException e)
	{
	    e.printStackTrace();
	}

	return retVal;
    }

    public Boolean destroyItem(Item item)
    {
	Boolean retVal = false;

	String query = "delete from ITEM where ID =?";
	try
	{
	    PreparedStatement ps = m_connection.prepareStatement(query);

	    ps.setInt(1, item.getID());

	    int rows = ps.executeUpdate();

	    if (rows == 1)
	    {
		retVal = true;
	    }
	}
	catch (SQLException e)
	{
	    e.printStackTrace();
	}

	return retVal;
    }


}
