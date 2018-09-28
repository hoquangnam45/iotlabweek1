package com.iot.hoquangnam.blink;

import android.app.Activity;
import android.os.Bundle;
import android.os.*;
import com.google.android.things.pio.PeripheralManager;
import android.util.Log;
import android.util.*;
import com.google.android.things.pio.Gpio;
import java.io.IOException;

/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 *
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int INTERVAL_BETWEEN_BLINKS_MS1 = 500;
    private static final int INTERVAL_BETWEEN_BLINKS_MS2 = 2000;
    private static final int INTERVAL_BETWEEN_BLINKS_MS3 = 3000;
    private Handler mHandler1 = new Handler();
    private Handler mHandler2 = new Handler();
    private Handler mHandler3 = new Handler();
    private Gpio mLedGpio1;
    private Gpio mLedGpio2;
    private Gpio mLedGpio3;
    private boolean mLedState1 = false;
    private boolean mLedState2 = false;
    private boolean mLedState3 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Starting BlinkActivity");
        try {
            String pinName = BoardDefaults.getGPIOForLED();
            mLedGpio1 = PeripheralManager.getInstance().openGpio("BCM17");
            mLedGpio1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLedGpio2 = PeripheralManager.getInstance().openGpio("BCM27");
            mLedGpio2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLedGpio3 = PeripheralManager.getInstance().openGpio("BCM22");
            mLedGpio3.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            Log.i(TAG, "Start blinking LED GPIO pin");
            mHandler1.post(mBlinkRunnable1);
            mHandler2.post(mBlinkRunnable2);
            mHandler3.post(mBlinkRunnable3);
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        }
    }

    private Runnable mBlinkRunnable1 = new Runnable() {
        @Override
        public void run() {
            if (mLedGpio1 == null) {// Exit Runnable if the GPIO is already closed
                return;
            }
            try {
                // Toggle the GPIO state
                mLedState1 = !mLedState1;
                mLedGpio1.setValue(mLedState1);
                Log.d(TAG, "State set to " + mLedState1);
                // Reschedule the same runnable in {#INTERVAL_BETWEEN_BLINKS_MS} milliseconds
                mHandler1.postDelayed(mBlinkRunnable1, INTERVAL_BETWEEN_BLINKS_MS1);
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
    };

    private Runnable mBlinkRunnable2 = new Runnable() {
        @Override
        public void run() {
            if (mLedGpio2 == null) {// Exit Runnable if the GPIO is already closed
                return;
            }
            try {
                // Toggle the GPIO state
                mLedState2 = !mLedState2;
                mLedGpio2.setValue(mLedState2);
                Log.d(TAG, "State set to " + mLedState2);
                // Reschedule the same runnable in {#INTERVAL_BETWEEN_BLINKS_MS} milliseconds
                mHandler2.postDelayed(mBlinkRunnable2, INTERVAL_BETWEEN_BLINKS_MS2);
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
    };
    private Runnable mBlinkRunnable3 = new Runnable() {
        @Override
        public void run() {
            if (mLedGpio3 == null) {// Exit Runnable if the GPIO is already closed
                return;
            }
            try {
                // Toggle the GPIO state
                mLedState3 = !mLedState3;
                mLedGpio3.setValue(mLedState3);
                Log.d(TAG, "State set to " + mLedState3);
                // Reschedule the same runnable in {#INTERVAL_BETWEEN_BLINKS_MS} milliseconds
                mHandler3.postDelayed(mBlinkRunnable3, INTERVAL_BETWEEN_BLINKS_MS3);
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove pending blink Runnable from the handler.
        mHandler1.removeCallbacks(mBlinkRunnable1);
        mHandler2.removeCallbacks(mBlinkRunnable2);
        mHandler3.removeCallbacks(mBlinkRunnable3);
        // Close the Gpio pin.
        Log.i(TAG, "Closing LED GPIO pin");
        try {
            mLedGpio1.close();
            mLedGpio2.close();
            mLedGpio3.close();
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        } finally {
            mLedGpio1 = null;
            mLedGpio2 = null;
            mLedGpio3 = null;
        }
    }
}

