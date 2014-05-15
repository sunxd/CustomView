package com.sunxd.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

public class SlideLayout extends LinearLayout implements GestureDetector.OnGestureListener{

	/**
     * 认为是用户滑动的最小距离
     */
    private int mSlop;
	
    /**
	 * 手势
	 */
	private GestureDetector gestureDetector;
	
	/**
	 * 展开的距离
	 */
	private int distance;
	
	/**
	 * 正在屏开
	 */
	private boolean scrolling;
	
	/**
	 * 当前滑动的距离
	 */
	private float deltaY;
	
	/**
	 * 
	 */
	private float dy;
	
	/**
	 * 按下的位置
	 */
	private float downY;
	
	private ExpandListener expandListener;
	
	
	public SlideLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		ViewConfiguration vc = ViewConfiguration.get(context);
        mSlop = vc.getScaledTouchSlop();
        gestureDetector = new GestureDetector(getContext(), this);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return gestureDetector.onTouchEvent(ev);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(distance == 0) return false;
		if(event.getAction() == MotionEvent.ACTION_UP && scrolling) {
			deltaY = event.getY() - downY;
			final RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) getLayoutParams();
			if(deltaY < 0){//向上
				performAnimate(distance - Math.abs(lp.topMargin));
			}else {//向下
				performAnimate(lp.topMargin);
			}
 		}
		gestureDetector.onTouchEvent(event);
		return true;
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		dy = 0;
		downY = e.getY();
		scrolling = false;
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		scrolling = true;
		dy += distanceY;
		final RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) getLayoutParams();
		if(lp.topMargin >= -distance && dy >0) {
			lp.topMargin = Math.max((lp.topMargin - (int) dy), -distance);;
			setLayoutParams(lp);
		}else if(lp.topMargin <= 0 && dy < 0){
			lp.topMargin = Math.min(lp.topMargin - (int)dy, 0);
			setLayoutParams(lp);
		}
		if(expandListener != null) {
			float u = Math.abs(lp.topMargin);
			float d = SlideLayout.this.distance;
			expandListener.onExpandPercent(u/d);
		}
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return true;
	}
	
	/**
	 * 动画
	 */
	private void performAnimate(final int distance) {
		final RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) getLayoutParams();
		final int topMargin = lp.topMargin;
		ValueAnimator animator = ValueAnimator.ofInt(0, distance);
		animator.setDuration(120);
		animator.setInterpolator(new AccelerateInterpolator());
		animator.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator arg0) {
				int offerset = (Integer)arg0.getAnimatedValue();
				if(distance > 0) {
					lp.topMargin = topMargin - offerset;
				}else {
					lp.topMargin = topMargin - offerset;
				}
				if(expandListener != null) {
					float u = Math.abs(lp.topMargin);
					float d = SlideLayout.this.distance;
					expandListener.onExpandPercent(u/d);
				}
				setLayoutParams(lp);
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
				if(lp.topMargin == 0) {
					if(expandListener != null) {
						expandListener.onExpandChanged(false);
						expandListener.onExpandPercent(0F);
					}
				}else {
					if(expandListener != null) {
						expandListener.onExpandChanged(true);
						expandListener.onExpandPercent(1F);
					}
				}
				
			}
			
			@Override
			public void onAnimationCancel(Animator arg0) {
			}
		});
		animator.start();
	}
	
	
	/**
	 * 是否屏开<br/>
	 * 孙晓达<br/>
	 * sunxd14@gmail.com<br/>
	 * 2014-5-13 下午2:47:18<br/>
	 *
	 */
	public interface ExpandListener {
		/**
		 * 屏开
		 * @param expand
		 */
		void onExpandChanged(boolean expand);
		
		/**
		 * 屏开百分比
		 * @param percent
		 */
		void onExpandPercent(float percent);
	}


	public ExpandListener getExpandListener() {
		return expandListener;
	}

	public void setExpandListener(ExpandListener expandListener) {
		this.expandListener = expandListener;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}
}
