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
import com.google.android.things.pio.Pwm;

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
    private Handler mHandler1 = new Handler();
    private Gpio mButtonGpio;
    private static final double MIN_ACTIVE_PULSE_DURATION_MS = 0;
    private static final double MAX_ACTIVE_PULSE_DURATION_MS = 100;
    private static final double PULSE_PERIOD_MS = 20;
    private static final double PULSE_CHANGE_PER_STEP_MS = 25;
    private boolean mIsPulseIncreasing1 = true;
    private boolean mIsPulseIncreasing2 = true;
    private Pwm mPwm1;
    private Pwm mPwm2;
    private double mActivePulseDuration1;
    private double mActivePulseDuration2;
    private int chooser = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Starting BlinkActivity");
        try {
            mActivePulseDuration1 = MIN_ACTIVE_PULSE_DURATION_MS;
            mActivePulseDuration2 = MIN_ACTIVE_PULSE_DURATION_MS;

            mButtonGpio = PeripheralManager.getInstance().openGpio("BCM5");
            mButtonGpio.setDirection(Gpio.DIRECTION_IN);
            mButtonGpio.setEdgeTriggerType(Gpio.EDGE_FALLING);
            mButtonGpio.registerGpioCallback(mCallback);

            mPwm1 = PeripheralManager.getInstance().openPwm("PWM0");
            mPwm2 = PeripheralManager.getInstance().openPwm("PWM1");

            mPwm1.setPwmFrequencyHz(1000 / PULSE_PERIOD_MS);
            mPwm1.setPwmDutyCycle(mActivePulseDuration1);
            mPwm1.setEnabled(true);

            mPwm2.setPwmFrequencyHz(1000 / PULSE_PERIOD_MS);
            mPwm2.setPwmDutyCycle(mActivePulseDuration2);
            mPwm2.setEnabled(true);

            Log.i(TAG, "Start blinking LED GPIO pin");
        } catch (IOException e) {
            Log.e(TAG, "Error on PeripheralIO API", e);
        }
    }

    private GpioCallback mCallback = new GpioCallback() {
        @Override
        public boolean onGpioEdge(Gpio gpio) {
            if (chooser == 1){
                if (mIsPulseIncreasing1) {
                    mActivePulseDuration1 += PULSE_CHANGE_PER_STEP_MS;
                } else {
                    mActivePulseDuration1 -= PULSE_CHANGE_PER_STEP_MS;
                }

                if (mActivePulseDuration1 > MAX_ACTIVE_PULSE_DURATION_MS) {
                    mActivePulseDuration1 = MAX_ACTIVE_PULSE_DURATION_MS;
                    mIsPulseIncreasing1 = !mIsPulseIncreasing1;
                } else if (mActivePulseDuration1 < MIN_ACTIVE_PULSE_DURATION_MS) {
                    mActivePulseDuration1 = MIN_ACTIVE_PULSE_DURATION_MS;
                    mIsPulseIncreasing1 = !mIsPulseIncreasing1;
                }

                try {
                    mPwm1.setPwmDutyCycle(mActivePulseDuration1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                chooser = 2;
            }
            else{
                if (mIsPulseIncreasing2) {
                    mActivePulseDuration2 += PULSE_CHANGE_PER_STEP_MS;
                } else {
                    mActivePulseDuration2 -= PULSE_CHANGE_PER_STEP_MS;
                }

                if (mActivePulseDuration2 > MAX_ACTIVE_PULSE_DURATION_MS) {
                    mActivePulseDuration2 = MAX_ACTIVE_PULSE_DURATION_MS;
                    mIsPulseIncreasing2 = !mIsPulseIncreasing2;
                } else if (mActivePulseDuration2 < MIN_ACTIVE_PULSE_DURATION_MS) {
                    mActivePulseDuration2 = MIN_ACTIVE_PULSE_DURATION_MS;
                    mIsPulseIncreasing2 = !mIsPulseIncreasing2;
                }

                try {
                    mPwm2.setPwmDutyCycle(mActivePulseDuration2);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                chooser = 1;
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
        if (mPwm1 != null) {
            try {
                mPwm1.close();
                mPwm1 = null;
            } catch (IOException e) {
                Log.w(TAG, "Unable to close PWM", e);
            }
        }
        if (mPwm2 != null) {
            try {
                mPwm2.close();
                mPwm2 = null;
            } catch (IOException e) {
                Log.w(TAG, "Unable to close PWM", e);
            }
        }
    }
}

