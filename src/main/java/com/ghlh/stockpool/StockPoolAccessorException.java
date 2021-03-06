package com.ghlh.stockpool;

import com.ghlh.autotrade.DemoStockException;

public class StockPoolAccessorException extends DemoStockException {

	private static final long serialVersionUID = 1202194525031558648L;

	public StockPoolAccessorException(String message, Exception rootException) {
		super(message, rootException);
	}

	public StockPoolAccessorException(String message) {
		super(message);
	}

	public StockPoolAccessorException(Exception rootException) {
		super(rootException);
	}

}
