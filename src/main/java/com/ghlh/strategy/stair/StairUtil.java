package com.ghlh.strategy.stair;

import com.ghlh.autotrade.EventRecorder;
import com.ghlh.strategy.TradeConstants;
import com.ghlh.strategy.TradeUtil;
import com.ghlh.util.MathUtil;

public class StairUtil {

	public static void dealBuy(String stockId, AdditionalInfoBean aib,
			double basePrice, double currentPrice, int spaceNumber) {
		dealBuy1(stockId, aib, basePrice, currentPrice, spaceNumber, false,0);
	}

	private static void dealBuy1(String stockId, AdditionalInfoBean aib,
			double basePrice, double currentPrice, int spaceNumber,
			boolean isIntradyFirstBuy, int priceType) {
		double possibleMinPrice = currentPrice * TradeConstants.MAX_DF;

		double sellPrice = MathUtil.formatDoubleWith2QuanShe(basePrice
				* (1 + aib.getStairZDF()));

		for (int i = 0; i < spaceNumber; i++) {
			boolean canBuy = StairUtil.buyAsCondition(stockId, aib,
					possibleMinPrice, basePrice, sellPrice, isIntradyFirstBuy && i == 0, priceType);
			if (!canBuy) {
				break;
			}
			sellPrice = basePrice;
			basePrice = MathUtil.formatDoubleWith2QuanJin(basePrice
					* (1 - aib.getStairZDF()));

		}
	}

	public static void dealBuyIntradyFirstBuy(String stockId,
			AdditionalInfoBean aib, double basePrice, double currentPrice,
			int spaceNumber, int priceType) {
		dealBuy1(stockId, aib, basePrice, currentPrice, spaceNumber, true, priceType);
	}

	private static boolean buyAsCondition(String stockId,
			AdditionalInfoBean aib, double possibleMinPrice, double basePrice,
			double sellPrice, boolean isIntradyFirstBuy, int priceType) {
		if (isIntradyFirstBuy) {
			String message = TradeUtil.getIntradyPriceBuyMessage(stockId,
					TradeUtil.getTradeNumber(aib.getStairMoney(), basePrice),
					basePrice,priceType);
			EventRecorder.recordEvent(StairUtil.class, message);
			TradeUtil.dealBuyStockSuccessfully(stockId, aib.getStairMoney(),
					basePrice, sellPrice, StairConstants.STAIR_STRATEGY_NAME);
			return true;

		} else if (possibleMinPrice < basePrice) {
			String message = TradeUtil.getPendingBuyMessage(stockId,
					TradeUtil.getTradeNumber(aib.getStairMoney(), basePrice),
					basePrice);
			EventRecorder.recordEvent(StairUtil.class, message);
			TradeUtil.dealBuyStock(stockId, aib.getStairMoney(), basePrice,
					sellPrice, StairConstants.STAIR_STRATEGY_NAME);
			return true;

		} else {
			return false;
		}
	}
}
