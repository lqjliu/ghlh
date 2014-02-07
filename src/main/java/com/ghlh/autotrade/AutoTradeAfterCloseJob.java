package com.ghlh.autotrade;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ghlh.data.db.GhlhDAO;
import com.ghlh.data.db.StockdailyinfoVO;
import com.ghlh.data.db.StocktradeDAO;
import com.ghlh.data.db.StocktradeVO;
import com.ghlh.stockquotes.StockQuotesBean;
import com.ghlh.strategy.TradeConstants;
import com.ghlh.strategy.TradeUtil;
import com.ghlh.util.EastMoneyUtil;
import com.ghlh.util.StockMarketUtil;

public class AutoTradeAfterCloseJob implements Job {
	private static Logger logger = Logger
			.getLogger(AutoTradeAfterCloseJob.class);

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		String message = "开始15:05盘后处理";
		EventRecorder.recordEvent(this.getClass(), message);
		if (StockMarketUtil.isMarketRest()) {
			return;
		}
		processPredoneOrders();
		collectStockDailyInfo();
		message = "结束15:05盘后处理";
		EventRecorder.recordEvent(this.getClass(), message);
	}

	private void collectStockDailyInfo() {
		List<StockQuotesBean> list = EastMoneyUtil.collectData(Constants.SZ_STOCK_COUNT);
		Date now = new Date();
		for (int i = 0; i < list.size(); i++) {
			StockQuotesBean sqb = (StockQuotesBean) list.get(i);
			System.out.println(i);
			GhlhDAO.createStockDailyIinfo(sqb, now);
		}
	}

	public static void main(String[] args) {
		new AutoTradeAfterCloseJob().collectStockDailyInfo();
	}

	private void processPredoneOrders() {
		List unfinishedTrades = StocktradeDAO.getUnfinishedTradeRecords();
		for (int i = 0; i < unfinishedTrades.size(); i++) {
			StocktradeVO stVO = (StocktradeVO) unfinishedTrades.get(i);
			String eventMessage = null;
			if (stVO.getStatus() == TradeConstants.STATUS_POSSIBLE_SELL
					|| stVO.getStatus() == TradeConstants.STATUS_T_0_BUY) {
				eventMessage = TradeUtil.getAfterCloseEventMessage(stVO);
				StocktradeDAO.updateStocktradeStatus(stVO.getId(),
						TradeConstants.STATUS_HOLDING);
			}
			if (stVO.getStatus() == TradeConstants.STATUS_PENDING_BUY) {
				eventMessage = TradeUtil.getAfterCloseEventMessage(stVO);
				StocktradeDAO.removeStocktrade(stVO.getId());
			}
			if (eventMessage != null) {
				EventRecorder.recordEvent(this.getClass(), eventMessage);
			}
		}
	}
}
