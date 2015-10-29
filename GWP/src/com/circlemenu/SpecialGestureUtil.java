package com.circlemenu;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/**
 * @author：darren
 * @version：创建时间 2015年2月13日
 */
public class SpecialGestureUtil {

	private enum DeltaState {

		STATE_1(1),

		STATE_2(2),

		STATE_3(3),

		STATE_4(4);
		private int number;

		DeltaState(int number) {
			this.number = number;
		}

		int getValue() {
			return number;
		}
	}

	public enum GestureClockwiseState {
		CLOCKWISE, COUNTER_CLOCKWISE, UNKNOWN
	}

	private static final String TAG = "SpecialGestureUtil";

	private List<DeltaState> mStateList;

	public SpecialGestureUtil() {
		mStateList = new ArrayList<DeltaState>();
	}

	private DeltaState createState(boolean isDeltaXGreaterThanZero,
			boolean isDeltaYGreaterThanZero) {
		DeltaState returnState;
		if (isDeltaXGreaterThanZero) {
			if (isDeltaYGreaterThanZero) {
				returnState = DeltaState.STATE_2;
			} else {
				returnState = DeltaState.STATE_1;
			}
		} else {
			if (isDeltaYGreaterThanZero) {
				returnState = DeltaState.STATE_3;
			} else {
				returnState = DeltaState.STATE_4;
			}
		}
		return returnState;
	}

	public void createState(float newX, float newY, float lastX, float lastY) {
		if (newX == lastX || newY == lastY) {
			// Ignore the unclear gesture.
		} else {
			boolean deltaX = (newX - lastX) > 0 ? true : false;
			boolean deltaY = (newY - lastY) > 0 ? true : false;
			DeltaState curentState = createState(deltaX, deltaY);
			pushState(curentState);
		}
	}

	public GestureClockwiseState getGestureState() {
		int clockwiseFlag = 0;
		int counterClockwiseFlag = 0;
		int historySize = mStateList.size();

		int firstFlag = 0;
		for (int i = 0; i < historySize - 1; i++) {
			Log.d(TAG, mStateList.get(i).toString());
		}
		// compare the delta state from the history.
		for (int i = 0; i < historySize - 1; i++) {
			int d = mStateList.get(i + 1).getValue()
					- mStateList.get(i).getValue();
			if (d == 1 || d == -3) {
				clockwiseFlag++;
				if (firstFlag == 0) {
					firstFlag = 1;
				}
			}
			if (d == -1 || d == 3) {
				counterClockwiseFlag++;
				if (firstFlag == 0) {
					firstFlag = -1;
				}
			}
		}
		Log.d(TAG, "clockwiseFlag = " + clockwiseFlag
				+ " counterClockwiseFlag = " + counterClockwiseFlag
				+ " historySize =" + historySize);
		mStateList.clear();
		if (counterClockwiseFlag != 0 && counterClockwiseFlag > clockwiseFlag) {
			return GestureClockwiseState.COUNTER_CLOCKWISE;
		}
		if (clockwiseFlag != 0 && clockwiseFlag > counterClockwiseFlag) {
			return GestureClockwiseState.CLOCKWISE;
		}
		// For unclear gesture, the following enhanced judgment help to figure
		// out what the real gesture.

		if (counterClockwiseFlag != 0 && clockwiseFlag != 0
				&& counterClockwiseFlag == clockwiseFlag) {
			if (firstFlag == 1) {
				return GestureClockwiseState.COUNTER_CLOCKWISE;
			}
			if (firstFlag == -1) {
				return GestureClockwiseState.CLOCKWISE;
			}
		}
		// End of enhanced judgment
		return GestureClockwiseState.UNKNOWN;
	}

	private void pushState(DeltaState state) {
		mStateList.add(state);
	}
}