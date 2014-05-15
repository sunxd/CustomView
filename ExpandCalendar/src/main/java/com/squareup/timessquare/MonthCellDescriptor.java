// Copyright 2012 Square, Inc.

package com.squareup.timessquare;

import java.util.Date;

/** Describes the state of a particular date cell in a {@link CalendarView}. */
public class MonthCellDescriptor {
  public enum RangeState {
    NONE, FIRST, MIDDLE, LAST
  }

  private final Date date;
  private final int value;
  private final boolean isCurrentMonth;
  private boolean isSelected;
  private final boolean isToday;
  private final boolean isSelectable;
  private boolean isMark;
  private boolean isMarkText;
  private RangeState rangeState;

  public MonthCellDescriptor(Date date, boolean currentMonth, boolean selectable, boolean selected,
      boolean today, int value, RangeState rangeState) {
    this.date = date;
    isCurrentMonth = currentMonth;
    isSelectable = selectable;
    isSelected = selected;
    isToday = today;
    this.isMark = false;
    this.isMarkText = false;
    this.value = value;
    this.rangeState = rangeState;
  }
  
  public MonthCellDescriptor(Date date, boolean currentMonth, boolean selectable, boolean selected,
	      boolean today, boolean isMark, boolean isMarkText, int value, RangeState rangeState) {
	    this.date = date;
	    isCurrentMonth = currentMonth;
	    isSelectable = selectable;
	    isSelected = selected;
	    isToday = today;
	    this.isMark = isMark;
	    this.isMarkText = isMarkText;
	    this.value = value;
	    this.rangeState = rangeState;
	  }

  public Date getDate() {
    return date;
  }

  public boolean isCurrentMonth() {
    return isCurrentMonth;
  }

  public boolean isSelectable() {
    return isSelectable;
  }

  public boolean isSelected() {
    return isSelected;
  }

  public void setSelected(boolean selected) {
    isSelected = selected;
  }

  public boolean isToday() {
    return isToday;
  }

  public RangeState getRangeState() {
    return rangeState;
  }

  public void setRangeState(RangeState rangeState) {
    this.rangeState = rangeState;
  }

  public int getValue() {
    return value;
  }

  
  public boolean isMark() {
	return isMark;
  }

	public void setMark(boolean isMark) {
		this.isMark = isMark;
	}

   public boolean isMarkText() {
		return isMarkText;
	}

	public void setMarkText(boolean isMarkText) {
		this.isMarkText = isMarkText;
	}

@Override public String toString() {
    return "MonthCellDescriptor{"
        + "date="
        + date
        + ", value="
        + value
        + ", isCurrentMonth="
        + isCurrentMonth
        + ", isSelected="
        + isSelected
        + ", isToday="
        + isToday
        + ", isSelectable="
        + isSelectable
        + ", isMark="
        + isMark
        + ", isMarkText="
        + isMarkText
        + ", rangeState="
        + rangeState
        + '}';
  }
}
