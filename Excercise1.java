package com.iot.hoquangnam.blink;

import android.app.Activity;
import android.os.Bundle;
import android.os.*;
import com.google.android.things.pio.PeripheralManager;
import android.util.Log;
import android.util.*;
import com.google.android.things.pio.Gpio;
import java.io.IOException;
import com.google.android.things.pio.GpioCallback;

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
    private static int INTERVAL_BETWEEN_BLINKS_MS1 = 2000;
    private Handler mHandler1 = new Handler();
    private Gpio mLedGpio1;
    private Gpio mLedGpio2;
    private Gpio mLedGpio3;
    private Gpio mButtonGpio;
    private boolean mLedState1 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Starting BlinkActivity");
        try {
            mButtonGpio = PeripheralManager.getInstance().openGpio("BCM5");
            mButtonGpio.setDirection(Gpio.DIRECTION_IN);
            mButtonGpio.setEdgeTriggerType(Gpio.EDGE_BOTH);
            mButtonGpio.setActiveType(Gpio.ACTIVE_LOW);
            mButtonGpio.registerGpioCallback(mCallback);

            mLedGpio1 = PeripheralManager.getInstance().openGpio("BCM17");
            mLedGpio1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLedGpio2 = PeripheralManager.getInstance().openGpio("BCM27");
            mLedGpio2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLedGpio3 = PeripheralManager.getInstance().openGpio("BCM22");
            mLedGpio3.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            Log.i(TAG, "Start blinking LED GPIO pin");
            mHandler1.post(mBlinkRunnable1);
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        }
    }

    private Runnable mBlinkRunnable1 = new Runnable() {
        @Override
        public void run() {
            if (mLedGpio1 == null | mLedGpio2 == null | mLedGpio3 == null) {// Exit Runnable if the GPIO is already closed
                return;
            }
            try {
                // Toggle the GPIO state
                mLedState1 = !mLedState1;
                mLedGpio1.setValue(mLedState1);
                mLedGpio2.setValue(mLedState1);
                mLedGpio3.setValue(mLedState1);
                Log.d(TAG, "State set to " + mLedState1);
                // Reschedule the same runnable in {#INTERVAL_BETWEEN_BLINKS_MS} milliseconds
                mHandler1.postDelayed(mBlinkRunnable1, INTERVAL_BETWEEN_BLINKS_MS1);
            } catch (IOException e) {
                Log.e(TAG, "Error on PeripheralIO API", e);
            }
        }
    };

    private GpioCallback mCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            switch (INTERVAL_BETWEEN_BLINKS_MS1) {
                case 2000:
                    INTERVAL_BETWEEN_BLINKS_MS1 = 1000;
                    break;
                case 1000:
                    INTERVAL_BETWEEN_BLINKS_MS1 = 500;
                    break;
                case 500:
                    INTERVAL_BETWEEN_BLINKS_MS1 = 100;
                    break;
                case 100:
                    INTERVAL_BETWEEN_BLINKS_MS1 = 2000;
                    break;
                default:
                    break;
            }
            // Return true to keep callback active.
            return true;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mButtonGpio != null) {
            mButtonGpio.unregisterGpioCallback(mCallback);
            try {
                mButtonGpio.close();
            } catch (IOException e) {
                Log.w(TAG, "Error closing GPIO", e);
            } finally {
                mButtonGpio = null;
            }
        }
        // Remove pending blink Runnable from the handler.
        mHandler1.removeCallbacks(mBlinkRunnable1);
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

