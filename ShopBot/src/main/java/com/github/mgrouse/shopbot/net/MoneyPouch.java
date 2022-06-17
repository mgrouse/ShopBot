package com.github.mgrouse.shopbot.net;

import java.math.BigDecimal;

import com.google.gson.JsonObject;


class MoneyPouch
{
    private BigDecimal m_value = new BigDecimal("0.00");

    MoneyPouch(JsonObject money)
    {
	// track total as gold
	BigDecimal total = new BigDecimal("0.00");

	BigDecimal coin;

	if (null != money)
	{

	    // copper
	    coin = money.get("cp").getAsBigDecimal();

	    coin = coin.divide(new BigDecimal("100.00"));

	    total = total.add(coin);

	    // silver
	    coin = money.get("sp").getAsBigDecimal();

	    coin = coin.divide(new BigDecimal("10.00"));

	    total = total.add(coin);


	    // Electrum
	    coin = money.get("ep").getAsBigDecimal();

	    coin = coin.divide(new BigDecimal("2.00"));

	    total = total.add(coin);

	    // gold
	    coin = money.get("gp").getAsBigDecimal();

	    total = total.add(coin);

	    // Platinum
	    coin = money.get("pp").getAsBigDecimal();

	    coin = coin.multiply(new BigDecimal("10.00"));

	    total = total.add(coin);
	}

	m_value = total;

    }

    public BigDecimal asBigDecimal()
    {
	return m_value;
    }

    public BigDecimal subtract(MoneyPouch pouch)
    {
	return null;
    }
}
