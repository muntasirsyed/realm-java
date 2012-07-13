/* This file was automatically generated by TightDB. */
package com.tightdb.generated;

import com.tightdb.*;
import com.tightdb.lib.*;

/**
 * This class represents a TightDB query and was automatically generated.
 */
public class StockQuery extends AbstractQuery<StockQuery, Stock, StockView> {

    public final StringQueryColumn<Stock, StockView, StockQuery> Date;
    public final LongQueryColumn<Stock, StockView, StockQuery> Open;
    public final LongQueryColumn<Stock, StockView, StockQuery> High;
    public final LongQueryColumn<Stock, StockView, StockQuery> Low;
    public final LongQueryColumn<Stock, StockView, StockQuery> Close;
    public final LongQueryColumn<Stock, StockView, StockQuery> Volume;
    public final LongQueryColumn<Stock, StockView, StockQuery> Adj_Close;
	public StockQuery(TableBase table, TableQuery query) {
		super(StockTable.TYPES, table, query);
        Date = new StringQueryColumn<Stock, StockView, StockQuery>(StockTable.TYPES, table, query, 0, "Date");
        Open = new LongQueryColumn<Stock, StockView, StockQuery>(StockTable.TYPES, table, query, 1, "Open");
        High = new LongQueryColumn<Stock, StockView, StockQuery>(StockTable.TYPES, table, query, 2, "High");
        Low = new LongQueryColumn<Stock, StockView, StockQuery>(StockTable.TYPES, table, query, 3, "Low");
        Close = new LongQueryColumn<Stock, StockView, StockQuery>(StockTable.TYPES, table, query, 4, "Close");
        Volume = new LongQueryColumn<Stock, StockView, StockQuery>(StockTable.TYPES, table, query, 5, "Volume");
        Adj_Close = new LongQueryColumn<Stock, StockView, StockQuery>(StockTable.TYPES, table, query, 6, "Adj_Close");
	}

}