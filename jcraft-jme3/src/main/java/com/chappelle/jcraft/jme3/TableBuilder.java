package com.chappelle.jcraft.jme3;

import java.text.DecimalFormat;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

/**
 * Utility class that allows you to format text to standard out as a table
 */
public class TableBuilder
{
	private static final String LEFT_PADDING = "| ";
	private static final String COLUMN_SEPARATOR = " | ";
	private List<Object[]> rows = new ArrayList<Object[]>();
	private String[] columnHeaders;
	private int totalWidth;
	private int[] colWidths;
	
	public TableBuilder(String...columnHeaders)
	{
		this.columnHeaders = columnHeaders;
	}
	
	public void addRow(Object...cols)
	{
		rows.add(cols);
	}
	
	@Override
	public String toString()
	{
		computeColumnWidths();

		StringBuilder buf = new StringBuilder();
		for(int i = 0; i < totalWidth; i++)
		{
			if(i == 0)
			{
				buf.append(" _");
			}
			else
			{
				buf.append("_");
			}
		}
		buf.append('\n');

		for(int i = 0; i < columnHeaders.length; i++)
		{
			if(i == 0)
			{
				buf.append(LEFT_PADDING);
			}
			String col = columnHeaders[i];
			buf.append(StringUtils.rightPad(StringUtils.defaultString(col), colWidths[i]));
			buf.append(COLUMN_SEPARATOR);
		}
		buf.append('\n');
		
		for(int i = 0; i < totalWidth; i++)
		{
			if(i == 0)
			{
				buf.append("|");
			}
			buf.append("=");
		}
		buf.append("|");
		buf.append('\n');

		for(int i = 0; i < rows.size(); i++)
		{
			buf.append(LEFT_PADDING);
			Object[] row = rows.get(i);
			for(int colNum = 0; colNum < row.length; colNum++)
			{
				buf.append(StringUtils.rightPad(StringUtils.defaultString(columnToString(row[colNum])), colWidths[colNum]));
				buf.append(COLUMN_SEPARATOR);
			}
			buf.append('\n');
		}
		return buf.toString();
	}
	
	private void computeColumnWidths()
	{
		int cols = -1;
		
		for(Object[] row : rows)
		{
			cols = Math.max(cols,  row.length);
		}
		
		int[] widths = new int[cols];
		totalWidth = -1;
		
		for(Object[] row : rows)
		{
			int rowWidth = 0;
			for(int colNum = 0; colNum < row.length; colNum++)
			{
				widths[colNum] = Math.max(widths[colNum], StringUtils.length(columnToString(row[colNum])));
				widths[colNum] = Math.max(widths[colNum], columnHeaders[colNum].length());
				rowWidth += widths[colNum];
			}
			totalWidth = Math.max(totalWidth, rowWidth);
		}
		totalWidth += COLUMN_SEPARATOR.length()*(cols-1) + LEFT_PADDING.length();
		
		colWidths = widths;
	}
	
	private String columnToString(Object obj)
	{
		if(obj instanceof Double)
		{
			return new DecimalFormat("#0.0000").format((Double)obj);
		}
		return Objects.toString(obj);
	}
}