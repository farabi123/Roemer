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
    int counter; int countervalue = 10;

    //Speed and Steering
    static final int DEFAULT_PWM = 1500, MAX_PWM = 2000, MIN_PWM = 1000;
    // < 1500  is Right
    // > 1500 is Left
    private PwmOutput pwm_speed_output, pwm_steering_output;


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
            //dlkjs
            pwm_pan_output.setPulseWidth(SERVO_DEFAULT_PWM);
            pwm_tilt_output.setPulseWidth(SERVO_DEFAULT_PWM);


            //Steer & speed
            pwm_speed_output = ioio_.openPwmOutput(3, 50); //motor channel 4: front left
            pwm_steering_output = ioio_.openPwmOutput(4, 50); //motor channel 3: back left

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

            counter += countervalue;
            pan(DEFAULT_PWM+counter);
            tilt(DEFAULT_PWM+counter);

            //Steer & speed
            if(countervalue > 0) set_speed(1800);
            if(countervalue < 0) set_steering(1800);
            //pwm_steering_output.setPulseWidth(1000);


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
            if(value > SERVO_MAX_PWM){
                pwm_pan_output.setPulseWidth(SERVO_DEFAULT_PWM);
            countervalue = -countervalue;}
            else if(value < SERVO_MIN_PWM){
                pwm_pan_output.setPulseWidth(SERVO_DEFAULT_PWM);
            countervalue = -countervalue;}

            else
                pwm_pan_output.setPulseWidth(value);
        } catch (ConnectionLostException e) {
            ioio_.disconnect();
        }

    }

    public synchronized void tilt(int value)
    {
        try {
            if(value > SERVO_MAX_PWM){
                pwm_tilt_output.setPulseWidth(SERVO_DEFAULT_PWM);
            countervalue = -countervalue;}

            else if(value < SERVO_MIN_PWM){
                pwm_tilt_output.setPulseWidth(SERVO_DEFAULT_PWM);
            countervalue = -countervalue;}

            else
                pwm_tilt_output.setPulseWidth(value);
        } catch (ConnectionLostException e) {
            ioio_.disconnect();
        }
    }

    public synchronized void set_speed(int value) {
        try {
            if(value > MAX_PWM)
                pwm_speed_output.setPulseWidth(MAX_PWM);
            else if(value < MIN_PWM)
                pwm_speed_output.setPulseWidth(MIN_PWM);
            else {
                if (pwm_speed_output != null) {
                    pwm_speed_output.setPulseWidth(value);
                }
            }
        } catch (ConnectionLostException e) {
                ioio_.disconnect();
            }
    }

    public synchronized void set_steering(int value){
        try {
            if(value > MAX_PWM)
                pwm_steering_output.setPulseWidth(MAX_PWM);
            else if(value < MIN_PWM)
                pwm_steering_output.setPulseWidth(MIN_PWM);
            else {
                if (pwm_steering_output != null) {
                    pwm_steering_output.setPulseWidth(value);
                }
            }
        } catch (ConnectionLostException e) {
            ioio_.disconnect();
        }
    }






}