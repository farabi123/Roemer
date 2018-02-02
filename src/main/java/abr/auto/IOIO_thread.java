package abr.auto;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.Uart;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;
import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.Uart;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;

public class IOIO_thread extends BaseIOIOLooper
{
    //IR sensor
    AnalogInput  irCenter;
    float irCenterReading;
    float dataIRCenter;

    //Servos
    private PwmOutput pwm_pan_output, pwm_tilt_output;
    InputStream in;
    OutputStream out;
    static final int SERVO_DEFAULT_PWM = 1500, SERVO_MAX_PWM = 2000, SERVO_MIN_PWM = 1000;

    //Speed and Steering
    static final int DEFAULT_PWM = 1520, MAX_PWM = 1900, MIN_PWM = 1100;
    // < 1500  is Right
    // > 1500 is Left
    private PwmOutput  pwm_left_output, pwm_right_output;
    public int counter = 0;

    @Override
    public void setup() throws ConnectionLostException
    {
        try {
            //IR Sensor
            irCenter = ioio_.openAnalogInput(43);

            //Servos
            //tiff test comment
            pwm_pan_output = ioio_.openPwmOutput(5, 50); //motor channel 1: back right;
            pwm_tilt_output = ioio_.openPwmOutput(6, 50); //motor channel 1: back right;

            //Uart uart = ioio_.openUart(3, 4, 230400, Uart.Parity.NONE, Uart.StopBits.ONE);
            //in = uart.getInputStream();
            //out = uart.getOutputStream();

            pwm_pan_output.setPulseWidth(SERVO_DEFAULT_PWM);
            pwm_tilt_output.setPulseWidth(SERVO_DEFAULT_PWM);


            //Steer & speed
            pwm_left_output = ioio_.openPwmOutput(3, 50); // S1 to pin 3 controls motor 1 aka left
            pwm_right_output = ioio_.openPwmOutput(4, 50); //S2 to pin 4 controls motor 2 aka right

          //  pwm_left_output.setPulseWidth(DEFAULT_PWM);
          //  pwm_right_output.setPulseWidth(DEFAULT_PWM);
        }
        catch (ConnectionLostException e){throw e;}
    }

    @Override
    public void loop() throws ConnectionLostException
    {
        ioio_.beginBatch();
        try {
            //IR Sensor
            dataIRCenter = irCenter.read();
            System.out.println("object dataIRCenter" + dataIRCenter);

            if (dataIRCenter > 0.3){
                System.out.println("object detected!!!!!!!!!!!!!!");
            }
            //irCenterReading = irCenter.getVoltage();

            //Servo
            pan(DEFAULT_PWM);
            tilt(DEFAULT_PWM);

            //Motor Controller


                set_left(1200);
                set_right(1200);

                set_left(DEFAULT_PWM);
                set_right(DEFAULT_PWM);


            /*if(counter >= 10) {
                set_left(1750);
                set_right(1750);
            }
            counter++;*/

            // pwm_right_output.setPulseWidth(1250);
           // pwm_left_output.setPulseWidth(1250);

            Thread.sleep(10);
        } catch (InterruptedException e) {
            ioio_.disconnect();
        } finally {
            ioio_.endBatch();
        }
    }


    public float getIrCenterReading() {
        return dataIRCenter/*irCenterReading*/;
    }

    public synchronized void pan(int value)
    {
        try {
            if(value > SERVO_MAX_PWM)
                pwm_pan_output.setPulseWidth(SERVO_DEFAULT_PWM);
            else if(value < SERVO_MIN_PWM)
                pwm_pan_output.setPulseWidth(SERVO_DEFAULT_PWM);
            else
                pwm_pan_output.setPulseWidth(value);
        } catch (ConnectionLostException e) {
            ioio_.disconnect();
        }

    }

    public synchronized void tilt(int value)
    {
        try {
            if(value > SERVO_MAX_PWM)
                pwm_tilt_output.setPulseWidth(SERVO_DEFAULT_PWM);
            else if(value < SERVO_MIN_PWM)
                pwm_tilt_output.setPulseWidth(SERVO_DEFAULT_PWM);
            else
                pwm_tilt_output.setPulseWidth(value);
        } catch (ConnectionLostException e) {
            ioio_.disconnect();
        }
    }

    public synchronized void set_left(int value) {
        try {
            if(value > MAX_PWM)
                pwm_left_output.setPulseWidth(MAX_PWM);
            else if(value < MIN_PWM)
                pwm_left_output.setPulseWidth(MIN_PWM);
            else {
                if (pwm_left_output != null) {
                    pwm_left_output.setPulseWidth(value);
                }
            }
        } catch (ConnectionLostException e) {
                ioio_.disconnect();
            }
    }

    public synchronized void set_right(int value){
        try {
            if(value > MAX_PWM)
                pwm_right_output.setPulseWidth(MAX_PWM);
            else if(value < MIN_PWM)
                pwm_right_output.setPulseWidth(MIN_PWM);
            else {
                if (pwm_right_output != null) {
                    pwm_right_output.setPulseWidth(value);
                }
            }
        } catch (ConnectionLostException e) {
            ioio_.disconnect();
        }
    }






}