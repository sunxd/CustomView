// Copyright 2012 Square, Inc.
package com.squareup.timessquare;

import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.nineoldandroids.view.ViewHelper;


public class CalendarView extends LinearLayout implements GestureDetector.OnGestureListener{
  private TextView title;
  private ImageView preMonth;
  private ImageView nextMonth;
  CalendarGridView grid;
  private CellClickListener cellClickListener = new CellClickedListener(); 
  private OnDateSelectedListener dateListener;
  private OnMonthChangedListener monthChangedListener;
  private OnInvalidDateSelectedListener invalidDateListener =
      new DefaultOnInvalidDateSelectedListener();
  final List<MonthCellDescriptor> selectedCellDess = new ArrayList<MonthCellDescriptor>();
  final List<Calendar> selectedCals = new ArrayList<Calendar>();
  final Map<String, List<Calendar>> markCalsMap = new HashMap<String, List<Calendar>>();
  final Map<String, List<Calendar>> markTextCalsMap = new HashMap<String, List<Calendar>>();
  
  MonthDescriptor currentMonthDes;
  private List<List<MonthCellDescriptor>> currentMonthCells;
  private Locale locale;
  private Calendar today;
  private Calendar monthCounter;
  private DateFormat monthNameFormat;
  private DateFormat weekdayNameFormat;
  private Context context;
  
  private LinearLayout calContaienr;
  
  /**
	* ����
	*/
  private GestureDetector gestureDetector;
  
  public static CalendarView create(ViewGroup parent, LayoutInflater inflater,
      DateFormat weekdayNameFormat, CellClickListener listener, Calendar today) {
    final CalendarView view = (CalendarView) inflater.inflate(R.layout.month, parent, false);

    final int originalDayOfWeek = today.get(Calendar.DAY_OF_WEEK);

    int firstDayOfWeek = today.getFirstDayOfWeek();
    final CalendarRowView headerRow = (CalendarRowView) view.grid.getChildAt(0);
    for (int offset = 0; offset < 7; offset++) {
      today.set(Calendar.DAY_OF_WEEK, firstDayOfWeek + offset);
      final TextView textView = (TextView) headerRow.getChildAt(offset);
      textView.setText(weekdayNameFormat.format(today.getTime()));
    }
    today.set(Calendar.DAY_OF_WEEK, originalDayOfWeek);
    view.cellClickListener = listener;
    return view;
  }
  
    /*public static MonthView create(Context context, ViewGroup parent) {
	    final MonthView view = (MonthView) LayoutInflater.from(context)
	    		.inflate(R.layout.month, parent, false);
	    
	    return view; 
    }*/
    public CalendarView(Context context) {
        super(context);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.month_inner, this, true);
        onFinishInflate();
    }
    
    public CalendarView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    this.context = context;
	    LayoutInflater.from(context).inflate(R.layout.month_inner, this, true);
    }

	private void initData(Context context) {
		gestureDetector = new GestureDetector(getContext(), this);
		
		locale = Locale.getDefault();
	    today = Calendar.getInstance(locale);
	    monthCounter = Calendar.getInstance(locale);
	
	    monthNameFormat = new SimpleDateFormat(context.getString(R.string.month_name_format), locale);
	    weekdayNameFormat = new SimpleDateFormat(context.getString(R.string.day_name_format), locale);
	    
	    final int originalDayOfWeek = today.get(Calendar.DAY_OF_WEEK);

	    int firstDayOfWeek = today.getFirstDayOfWeek();
	    final CalendarRowView headerRow = (CalendarRowView) grid.getChildAt(0);
	    for (int offset = 0; offset < 7; offset++) {
	      today.set(Calendar.DAY_OF_WEEK, firstDayOfWeek + offset);
	      final TextView textView = (TextView) headerRow.getChildAt(offset);
	      textView.setText(weekdayNameFormat.format(today.getTime()));
	    }
	    today.set(Calendar.DAY_OF_WEEK, originalDayOfWeek);
	    
	    initAndUpdateCalendar();
	}


    private void initAndUpdateCalendar() {
	    Date date = monthCounter.getTime();
	    MonthDescriptor month =
	        new MonthDescriptor(monthCounter.get(MONTH), monthCounter.get(YEAR), date,
	            monthNameFormat.format(date));
	    
	    List<List<MonthCellDescriptor>> cells = getMonthCells(month, monthCounter);
	    currentMonthDes = month;
	    currentMonthCells = cells;
	    updateCellViews(month, cells);
    }

    public void updateDataSetChanged(){
    	updateCellViews(currentMonthDes, currentMonthCells);
    }
  @Override 
  protected void onFinishInflate() {
    super.onFinishInflate();
    title = (TextView) findViewById(R.id.title);
    preMonth = (ImageView) findViewById(R.id.btn_pre_month);
    nextMonth = (ImageView) findViewById(R.id.btn_next_month);
    grid = (CalendarGridView) findViewById(R.id.calendar_grid);
    calContaienr = (LinearLayout) findViewById(R.id.cal_container);
    calContaienr.post(new Runnable() {
		
		@Override
		public void run() {
			calHeight = calContaienr.getHeight();
		}
	});
    
    preMonth.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			previousMonth();
		}
	});
    nextMonth.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			nextMonth();
		}
	});
    initData(context);
  }
  
  public void nextMonth() {
	  monthCounter.add(MONTH, 1);
	  initAndUpdateCalendar();
	  invalidate();
	  if (monthChangedListener != null) {
		  monthChangedListener.onChangedToNextMonth(monthCounter.getTime());
	  }
  }
  
  public void previousMonth() {
	  monthCounter.add(MONTH, -1);
	  initAndUpdateCalendar();
	  invalidate();
	  if (monthChangedListener != null) {
		  monthChangedListener.onChangedToPreMonth(monthCounter.getTime());
	  }
  }

  /**
   * Mark dates of a month
   * @param dates
   */
  public void markDatesOfMonth(int year, int month, 
		  boolean markIcon, boolean markText, Integer...dates) {
	  List<Integer> dateList = Arrays.asList(dates);
	  List<Calendar> calendars = new ArrayList<Calendar>(dateList.size()+5);
	  Calendar cal = Calendar.getInstance(locale);
	  for (Integer day : dateList) {
		  cal = Calendar.getInstance(locale);
		  cal.set(year, month, day, 0, 0, 0);
		  
		  calendars.add(cal);
	  }
	  String monthLabel = monthNameFormat.format(cal.getTime());
	  if (markIcon) {
		  markCalsMap.put(monthLabel, calendars);
	  }
	  if (markText) {
		  markTextCalsMap.put(monthLabel, calendars);
	  }
	  if (currentMonthDes.getLabel().equals(monthLabel)) {
		  initAndUpdateCalendar();
	  }
  }
  
  public void markDatesOfMonth(int year, int month, 
		  boolean markIcon, boolean markText, List<Calendar> calendars) {
	  Calendar cal = Calendar.getInstance(locale);
	  cal.set(year, month, 1);
	  
	  String monthLabel = monthNameFormat.format(cal.getTime());
	  if (markIcon) {
		  markCalsMap.put(monthLabel, calendars);
	  }
	  if (markText) {
		  markTextCalsMap.put(monthLabel, calendars);
	  }
	  if (currentMonthDes.getLabel().equals(monthLabel)) {
		  initAndUpdateCalendar();
	  }
  }
  
  public List<List<MonthCellDescriptor>> getMonthCells(MonthDescriptor month, Calendar startCal) {
	    Calendar cal = Calendar.getInstance(locale);
	    cal.setTime(startCal.getTime());
	    List<List<MonthCellDescriptor>> cells = new ArrayList<List<MonthCellDescriptor>>();
	    cal.set(DAY_OF_MONTH, 1);
	    int firstDayOfWeek = cal.get(DAY_OF_WEEK);
	    int offset = cal.getFirstDayOfWeek() - firstDayOfWeek;
	    if (offset > 0) {
	      offset -= 7;
	    }
	    cal.add(Calendar.DATE, offset);

	    /*Calendar minSelectedCal = minDate(selectedCals);
	    Calendar maxSelectedCal = maxDate(selectedCals);*/

	    while ((cal.get(MONTH) < month.getMonth() + 1 || cal.get(YEAR) < month.getYear()) //
	        && cal.get(YEAR) <= month.getYear()) {
	      Logr.d("Building week row starting at %s", cal.getTime());
	      List<MonthCellDescriptor> weekCells = new ArrayList<MonthCellDescriptor>();
	      cells.add(weekCells);
	      for (int c = 0; c < 7; c++) {
	        Date date = cal.getTime();
	        boolean isCurrentMonth = cal.get(MONTH) == month.getMonth();
	        boolean isSelected = isCurrentMonth && containsDate(selectedCals, cal);
	        boolean isSelectable =
	            isCurrentMonth /*&& betweenDates(cal, minCal, maxCal) && isDateSelectable(date)*/;
	        boolean isToday = sameDate(cal, today);
	        boolean isMark = markCalsContainsDate(cal);
	        boolean isMarkText = markTextCalsContainsDate(cal);
	        
	        int value = cal.get(DAY_OF_MONTH);

	        MonthCellDescriptor.RangeState rangeState = MonthCellDescriptor.RangeState.NONE;
	        /*if (selectedCals != null && selectedCals.size() > 1) {
	          if (sameDate(minSelectedCal, cal)) {
	            rangeState = MonthCellDescriptor.RangeState.FIRST;
	          } else if (sameDate(maxDate(selectedCals), cal)) {
	            rangeState = MonthCellDescriptor.RangeState.LAST;
	          } else if (betweenDates(cal, minSelectedCal, maxSelectedCal)) {
	            rangeState = MonthCellDescriptor.RangeState.MIDDLE;
	          }
	        }*/
	        MonthCellDescriptor monthCellDes = new MonthCellDescriptor(date, isCurrentMonth, isSelectable, isSelected, 
            		isToday, isMark, isMarkText, value, rangeState);
	        weekCells.add(monthCellDes);
	        if (isSelected && !containCell(selectedCellDess, monthCellDes)) {
	        	selectedCellDess.add(monthCellDes);
			}
	        cal.add(DATE, 1);
	      }
	    }
	    return cells;
  }
  
    private boolean markTextCalsContainsDate(Calendar cal) {
    	List<Calendar> selectedCals = markTextCalsMap.get(monthNameFormat.format(cal.getTime()));
    	return (selectedCals!= null)?containsDate(selectedCals, cal):false;
    }

	private boolean markCalsContainsDate(Calendar cal) {
		List<Calendar> selectedCals = markCalsMap.get(monthNameFormat.format(cal.getTime()));
    	return (selectedCals!= null)?containsDate(selectedCals, cal):false;
	}

	private boolean isPreMonthDate(Date date) {
    	boolean isPre = false;
	    Calendar dateCal = Calendar.getInstance(locale);
		dateCal.setTime(date);
		if (dateCal.get(YEAR) < monthCounter.get(YEAR)) {
			isPre = true;
		}else if (dateCal.get(YEAR)==monthCounter.get(YEAR) 
				&& dateCal.get(MONTH) < monthCounter.get(MONTH)){
			isPre = true;
		}
	    return isPre;
    }
    
    private boolean isCurrentMonthDate(Date date) {
    	boolean isCurrent = false;
	    Calendar dateCal = Calendar.getInstance(locale);
		dateCal.setTime(date);
		if (dateCal.get(YEAR)==monthCounter.get(YEAR) 
				&& dateCal.get(MONTH) == monthCounter.get(MONTH)){
			isCurrent = true;
		}
	    return isCurrent;
    }
    private boolean isNextMonthDate(Date date) {
    	boolean isNext = false;
		isNext = !isPreMonthDate(date) && !isCurrentMonthDate(date);
	    return isNext;
    }
  public void updateCellViews(MonthDescriptor month, List<List<MonthCellDescriptor>> cells) {
    Logr.d("Initializing MonthView (%d) for %s", System.identityHashCode(this), month);
    long start = System.currentTimeMillis();
    title.setText(month.getLabel());

    final int numRows = cells.size();
    grid.setNumRows(numRows);
    for (int i = 0; i < 6; i++) {
      CalendarRowView weekRow = (CalendarRowView) grid.getChildAt(i + 1);
      weekRow.setCellClickListener(cellClickListener);
      if (i < numRows) {
        weekRow.setVisibility(VISIBLE);
        List<MonthCellDescriptor> week = cells.get(i);
        for (int c = 0; c < week.size(); c++) {
          MonthCellDescriptor cell = week.get(c);
          CalendarCellView cellView = (CalendarCellView) weekRow.getChildAt(c);

          cellView.setText(Integer.toString(cell.getValue()));
          //cellView.setEnabled(cell.isCurrentMonth());

          cellView.setSelectable(cell.isSelectable());
          cellView.setSelected(cell.isSelected());
          cellView.setCurrentMonth(cell.isCurrentMonth());
          cellView.setMark(cell.isMark());
          cellView.setMarkText(cell.isMarkText());
          cellView.setToday(cell.isToday());
          cellView.setRangeState(cell.getRangeState());
          cellView.setTag(cell);
        }
      } else {
        weekRow.setVisibility(GONE);
      }
    }
    invalidate();
    Logr.d("MonthView.init took %d ms", System.currentTimeMillis() - start);
  }

  
    private class CellClickedListener implements CellClickListener {
	    @Override 
	    public void handleClick(View v, MonthCellDescriptor cell) {
	      Date clickedDate = cell.getDate();
	      if (!cell.isCurrentMonth() || !cell.isSelectable()) {
	          if (invalidDateListener != null) {
	              invalidDateListener.onInvalidDateSelected(clickedDate);
		          if (!cell.isCurrentMonth() && isPreMonthDate(clickedDate)) {
		        	  invalidDateListener.onPreMonthDateSelected(clickedDate);
				  }else if (!cell.isCurrentMonth() && isNextMonthDate(clickedDate)) {
					  invalidDateListener.onNextMonthDateSelected(clickedDate);
				  }
	          }
	      } else {
	        boolean wasSelected = doSingleSelectDate(cell);
	        if (dateListener != null) {
	        	if (wasSelected) {
	                dateListener.onDateSelected(clickedDate);
	              } else {
	                dateListener.onDateUnselected(clickedDate);
	              }
	        }
	      }
	    }
    }
    public boolean selectDate(Date date) {
        if (date == null) {
          throw new IllegalArgumentException("Selected date must be non-null.  " + date);
        }
        if (date.getTime() == 0) {
          throw new IllegalArgumentException("Selected date must be non-zero.  " + date);
        }
        MonthCellDescriptor monthCellDescriptor = getMonthCellDesByDate(date);
        if (monthCellDescriptor == null) {
        	Calendar newlySelCal = Calendar.getInstance(locale);
            newlySelCal.setTime(date);
        	clearOldSelections();
			selectedCals.add(newlySelCal);
			
			monthCounter.setTime(date);
		    initAndUpdateCalendar();
		    
		    if (monthChangedListener != null) {
		    	if (monthCounter.get(MONTH) < today.get(MONTH)) {
		    		monthChangedListener.onChangedToPreMonth(monthCounter.getTime());
				}else {
					monthChangedListener.onChangedToNextMonth(monthCounter.getTime());
				}
		    }
          return false;
        }
        boolean wasSelected = doSingleSelectDate(monthCellDescriptor);
        return wasSelected;
    }
    
    /** Return cell for a given Date. */
    private MonthCellDescriptor getMonthCellDesByDate(Date date) {
      Calendar searchCal = Calendar.getInstance(locale);
      searchCal.setTime(date);
      Calendar actCal = Calendar.getInstance(locale);

      for (List<MonthCellDescriptor> weekCells : currentMonthCells) {
          for (MonthCellDescriptor actCell : weekCells) {
            actCal.setTime(actCell.getDate());
            if (sameDate(actCal, searchCal) && actCell.isCurrentMonth()) {
              return actCell;
            }
          }
      }
      return null;
    }
    private boolean doSingleSelectDate(final MonthCellDescriptor cell) {
    	boolean wasSelected = false;
    	Calendar newlySelCal = Calendar.getInstance(locale);
        newlySelCal.setTime(cell.getDate());
        
    	if (selectedCals.size() > 0 && containsDate(selectedCals, newlySelCal)) {
        	clearOldSelections();
        	cell.setSelected(false);
        	wasSelected = false;
		}else {
			clearOldSelections();
			
			selectedCals.add(newlySelCal);
			selectedCellDess.add(cell);
            cell.setSelected(true);
            wasSelected = true;
		}
    	updateDataSetChanged();
    	return wasSelected;
    }
    private static boolean containsDate(List<Calendar> selectedCals, Calendar cal) {
        for (Calendar selectedCal : selectedCals) {
          if (sameDate(cal, selectedCal)) {
            return true;
          }
        }
        return false;
    }
    
    private static boolean sameDate(Calendar cal, Calendar selectedDate) {
        return cal.get(MONTH) == selectedDate.get(MONTH)
            && cal.get(YEAR) == selectedDate.get(YEAR)
            && cal.get(DAY_OF_MONTH) == selectedDate.get(DAY_OF_MONTH);
      }
    
    private boolean containCell(List<MonthCellDescriptor> selectedCells, final MonthCellDescriptor cell) {
        for (MonthCellDescriptor selectedCell : selectedCells) {
          if (selectedCell.equals(cell)) {
        	  return true;
          }
        }
		return false;
    }
    private void clearOldSelections() {
        for (MonthCellDescriptor selectedCell : selectedCellDess) {
          // De-select the currently-selected cell.
          selectedCell.setSelected(false);
        }
        selectedCellDess.clear();
        selectedCals.clear();
    }
  
    public interface CellClickListener {
    	void handleClick(View v, MonthCellDescriptor cell);
    }

	/*protected void setCellClickListener(CellClickListener cellClickListener) {
		this.cellClickListener = cellClickListener;
		for (int i = 0; i < 6; i++) {
		    CalendarRowView weekRow = (CalendarRowView) grid.getChildAt(i + 1);
		    weekRow.setCellClickListener(cellClickListener);
		}
	}*/
	
	public void setOnDateSelectedListener(OnDateSelectedListener listener) {
	    dateListener = listener;
	  }

	public void setOnMonthChangedListener(OnMonthChangedListener monthChangedListener) {
		this.monthChangedListener = monthChangedListener;
	}

	/**
	   * Set a listener to react to user selection of a disabled date.
	   *
	   * @param listener the listener to set, or null for no reaction
	   */
	  public void setOnInvalidDateSelectedListener(OnInvalidDateSelectedListener listener) {
	    invalidDateListener = listener;
	  }
	
	/**
	   * Interface to be notified when a new date is selected or unselected. This will only be called when the user
	   * initiates the date selection.  If you call {@link #selectDate(java.util.Date)} this listener will not be
	   * notified.
	   *
	   * @see #setOnDateSelectedListener(com.squareup.timessquare.CalendarView.OnDateSelectedListener)
	   */
	  public interface OnDateSelectedListener {
	    void onDateSelected(Date date);  
	    void onDateUnselected(Date date);
	  }

	  /**
	   * Interface to be notified when an invalid date is selected by the user. This will only be
	   * called when the user initiates the date selection. If you call {@link #selectDate(java.util.Date)} this
	   * listener will not be notified.
	   *
	   * @see #setOnInvalidDateSelectedListener(com.squareup.timessquare.CalendarView.OnInvalidDateSelectedListener)
	   */
	  public interface OnInvalidDateSelectedListener {
	    void onInvalidDateSelected(Date date);
	    void onPreMonthDateSelected(Date date);
	    void onNextMonthDateSelected(Date date);
	  }

	  public interface OnMonthChangedListener {
		    void onChangedToPreMonth(Date dateOfMonth);  
		    void onChangedToNextMonth(Date dateOfMonth);
	  }
	  
  private class DefaultOnInvalidDateSelectedListener implements OnInvalidDateSelectedListener {
    @Override 
    public void onInvalidDateSelected(Date date) {
    	
    }

	@Override
	public void onPreMonthDateSelected(Date date) {
		previousMonth();
	}

	@Override
	public void onNextMonthDateSelected(Date date) {
		nextMonth();
	}
  }

  
   /***********************
    * ����չ��������Ч��<br/>
	* ������<br/>
	* sunxd14@gmail.com<br/>
	* 2014-5-13 ����2:30:15<br/>
    * ********************/
  
  	
	private boolean expandable;
  	private float downX, downY, deltaY; 
  	private float scrollY;
  	private int top, rowHeight, calHeight;
  	private RelativeLayout.LayoutParams lp;
  	private boolean scrolling;
  	
  	private CalendarRowView selectRowView;
  	
  	
  	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return gestureDetector.onTouchEvent(ev);
	}
  	
  	@Override
  	public boolean onTouchEvent(MotionEvent event) {
  		if(event.getAction() == MotionEvent.ACTION_UP && scrolling) {
  			deltaY = event.getY() - downY;
  			if(deltaY < 0){//����
  				performAnimate(1);
  			}else {//����
  				performAnimate(-1);
  			}
  		}
  		gestureDetector.onTouchEvent(event);
  		return true;
  	}
  	
  	
	@Override
	public boolean onDown(MotionEvent me) {
		if(!expandable) return false;
		downX = me.getX();
		downY = me.getY();
		Rect rect = getRectOnScreen(calContaienr);
		//TODO �޷�ȷ��selectRowView�ױߵ���ʵλ�� ������selectRowView�·�������Ҳ��չ���������Ǹ�Bug
		if(downY > (rect.bottom + rowHeight)) {
			scrolling = false;
			return false;
		}
		scrolling = true;
		scrollY = Math.abs(ViewHelper.getTranslationY(calContaienr));
		lp = (RelativeLayout.LayoutParams) calContaienr.getLayoutParams();
		lp.height = calContaienr.getHeight();
		calContaienr.setLayoutParams(lp);
		selectRowView = getSelectRow();
		top = selectRowView.getTop();
		rowHeight = selectRowView.getHeight();
		return false;
	}
	
	/**
	 * ����ʱ��ʾ���У�ѡ�����ȣ�������Σ���û��Ĭ�ϵ�һ��
	 * @return
	 */
	private CalendarRowView getSelectRow() {
		CalendarRowView todayRow = null, selectRow = null;
		int count = grid.getChildCount();
		for(int i=1; i<count; i++) {
			CalendarRowView weekRow = (CalendarRowView) grid.getChildAt(i);
			for (int c = 0; c < weekRow.getChildCount(); c++) {
				CalendarCellView cellView = (CalendarCellView) weekRow.getChildAt(c);
				MonthCellDescriptor cell = (MonthCellDescriptor) cellView.getTag();
				if(cell != null) {
					if(cell.isToday()) {
						todayRow = weekRow;
					}
					if(cell.isSelected()) {
						selectRow = weekRow;
					}
				}
			}
		}
		return selectRow!=null?selectRow:(todayRow!=null?todayRow:(CalendarRowView)grid.getChildAt(1));
	}
	
	/**
     * ��ȡ�ؼ�����Ļ����
     */
    public Rect getRectOnScreen(View view){
        Rect rect = new Rect();
        int[] location = new int[2];
        View parent = view;
        if(view.getParent() instanceof View){
            parent = (View)view.getParent();
        }
        parent.getLocationOnScreen(location);
        view.getHitRect(rect);
        rect.offset(location[0], location[1]);
        return rect;
    }
	
	@Override
	public boolean onFling(MotionEvent me0, MotionEvent me1, float velocityX,
			float velocityY) {
		return true;
	}
	
	@Override
	public void onLongPress(MotionEvent me) {
	}
	
	@Override
	public boolean onScroll(MotionEvent me0, MotionEvent me1, float disX,
			float disY) {
		if(!scrolling) return false;
		scrollY += disY;
		if(disY > 0) {
			ViewHelper.setTranslationY(calContaienr, Math.max(Math.min(-scrollY, 0), -top));
			if(ViewHelper.getTranslationY(calContaienr) == -top) {
				RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) calContaienr.getLayoutParams();
				p.height = (int)Math.max(rowHeight + top, p.height - (int)disY);
				calContaienr.setLayoutParams(p);
			}
		}else {
			ViewHelper.setTranslationY(calContaienr, Math.min(Math.max(-scrollY, -top), 0));
			if(ViewHelper.getTranslationY(calContaienr) == 0) {
				RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) calContaienr.getLayoutParams();
				p.height = (int)Math.min(calHeight, p.height + Math.abs(disY));
				calContaienr.setLayoutParams(p);
			}
		}
		return true;
	}
	
	@Override
	public void onShowPress(MotionEvent me) {
	}
	
	@Override
	public boolean onSingleTapUp(MotionEvent me) {
		return false;
	}
	
	/**
	 * ����
	 * @param d>0: ���� <br/> d<0: ����
	 */
	private void performAnimate(int d) {
		if(d > 0) {//����
			final int deltaH = calContaienr.getLayoutParams().height;
			if(deltaH == calHeight) {
				int tanslateY = (int)Math.abs(ViewHelper.getTranslationY(calContaienr));
				ValueAnimator animator = ValueAnimator.ofInt(tanslateY, top);
				animator.setDuration(80);
				animator.setInterpolator(new AccelerateInterpolator());
				animator.addUpdateListener(new AnimatorUpdateListener() {

					@Override
					public void onAnimationUpdate(ValueAnimator arg0) {
						int offerset = (Integer)arg0.getAnimatedValue();
						ViewHelper.setTranslationY(calContaienr, -offerset);
					}
					
				});
				animator.addListener(new AnimatorListener() {
					
					@Override
					public void onAnimationStart(Animator arg0) {
					}
					
					@Override
					public void onAnimationRepeat(Animator arg0) {
					}
					
					@Override
					public void onAnimationEnd(Animator arg0) {
						performLayoutHeightAnimate(calContaienr.getLayoutParams().height, rowHeight + top);
					}
					
					@Override
					public void onAnimationCancel(Animator arg0) {
					}
				});
				animator.start();
			}else {
				performLayoutHeightAnimate(calContaienr.getLayoutParams().height, rowHeight + top);
			}
		}else {//����
			int translateY = (int)ViewHelper.getTranslationY(calContaienr);
			if(translateY < 0) {
				ValueAnimator animator = ValueAnimator.ofInt(translateY, 0);
				animator.setDuration(80);
				animator.setInterpolator(new AccelerateInterpolator());
				animator.addUpdateListener(new AnimatorUpdateListener() {

					@Override
					public void onAnimationUpdate(ValueAnimator arg0) {
						int offerset = (Integer)arg0.getAnimatedValue();
						ViewHelper.setTranslationY(calContaienr, offerset);
					}
				});
				animator.addListener(new AnimatorListener() {
					
					@Override
					public void onAnimationStart(Animator arg0) {
					}
					
					@Override
					public void onAnimationRepeat(Animator arg0) {
					}
					
					@Override
					public void onAnimationEnd(Animator arg0) {
						performLayoutHeightAnimate(calContaienr.getLayoutParams().height, calHeight);
					}
					
					@Override
					public void onAnimationCancel(Animator arg0) {
					}
				});
				animator.start();
			}else {
				performLayoutHeightAnimate(calContaienr.getLayoutParams().height, calHeight);
			}
		}
	}
	
	/**
	 * �߶ȱ�ö���
	 * @param from
	 * @param to
	 */
	public void performLayoutHeightAnimate(int from, int to) {
		final RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) calContaienr.getLayoutParams();
		ValueAnimator animator = ValueAnimator.ofInt(from, to);
		animator.setDuration(80);
		animator.setInterpolator(new AccelerateInterpolator());
		animator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				int offerset = (Integer)arg0.getAnimatedValue();
				p.height = offerset;
				calContaienr.setLayoutParams(p);
			}
		});
		animator.start();
	}

	public boolean isExpandable() {
		return expandable;
	}

	public void setExpandable(boolean expandable) {
		this.expandable = expandable;
	}
	
	/**
	 * �ֶ�����
	 * @param expand
	 */
	public void manualExpand(boolean expand) {
		if(!expand) {
			selectRowView = getSelectRow();
			top = selectRowView.getTop();
			rowHeight = selectRowView.getHeight();
			ViewHelper.setTranslationY(calContaienr, -top);
			RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) calContaienr.getLayoutParams();
			p.height = top + rowHeight;
			calContaienr.setLayoutParams(p);
		}
	}
}
