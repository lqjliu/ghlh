package com.ghlh.strategy.once;

import com.ghlh.autotrade.StockTradeIntradyMonitor;
import com.ghlh.autotrade.StockTradeIntradyUtil;
import com.ghlh.stockquotes.StockQuotesBean;
import com.ghlh.strategy.MonitoringStrategy;

public class OnceIntradayStrategy implements MonitoringStrategy {
	public void processSell(StockTradeIntradyMonitor monitor,
			StockQuotesBean sqb) {
		StockTradeIntradyUtil.processSell(monitor, sqb);
	}

	public void processBuy(StockTradeIntradyMonitor monitor, StockQuotesBean sqb) {
		StockTradeIntradyUtil.processBuy(monitor, sqb);
	}

}
