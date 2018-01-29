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
    static final int DEFAULT_PWM = 1500, MAX_PWM = 2000, MIN_PWM = 1000;
    int counter;


    @Override
    public void setup() throws ConnectionLostException
    {
        try {
            //IR Sensor
            irCenter = ioio_.openAnalogInput(43);


            //Servos
            pwm_pan_output = ioio_.openPwmOutput(5, 50); //motor channel 1: back right;
            pwm_tilt_output = ioio_.openPwmOutput(6, 50); //motor channel 1: back right;

            Uart uart = ioio_.openUart(3, 4, 230400, Uart.Parity.NONE, Uart.StopBits.ONE);
            in = uart.getInputStream();
            out = uart.getOutputStream();
            counter++;
            pwm_pan_output.setPulseWidth(DEFAULT_PWM);
            pwm_tilt_output.setPulseWidth(DEFAULT_PWM);
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
            pan(DEFAULT_PWM);
            tilt(DEFAULT_PWM);
            System.out.println("Counter: ");

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
            if(value > MAX_PWM)
                pwm_pan_output.setPulseWidth(DEFAULT_PWM);
            else if(value < MIN_PWM)
                pwm_pan_output.setPulseWidth(DEFAULT_PWM);
            else
                pwm_pan_output.setPulseWidth(value);
        } catch (ConnectionLostException e) {
            ioio_.disconnect();
        }

    }

    public synchronized void tilt(int value)
    {
        try {
            if(value > MAX_PWM)
                pwm_tilt_output.setPulseWidth(DEFAULT_PWM);
            else if(value < MIN_PWM)
                pwm_tilt_output.setPulseWidth(DEFAULT_PWM);
            else
                pwm_tilt_output.setPulseWidth(value);
        } catch (ConnectionLostException e) {
            ioio_.disconnect();
        }
    }

}