package abr.auto;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;

import android.util.Log;


public class IOIO_thread extends BaseIOIOLooper
{
    static final int DEFAULT_PWM = 1500, MAX_PWM = 2000, MIN_PWM = 1000;
    AnalogInput  irCenter;
    private PwmOutput pwm_speed_output, pwm_steering_output;

    int pwm_speed, pwm_steering;
    float irCenterReading;
    float dataIRCenter;

    @Override
    public void setup() throws ConnectionLostException
    {
        try
        {
            irCenter = ioio_.openAnalogInput(43);
            pwm_speed_output = ioio_.openPwmOutput(3, 50); //motor channel 4: front left
            pwm_steering_output = ioio_.openPwmOutput(4, 50); //motor channel 3: back left

            pwm_speed_output.setPulseWidth(1500);
            pwm_steering_output.setPulseWidth(1500);


        }
        catch (ConnectionLostException e){throw e;}
    }

    @Override
    public void loop() throws ConnectionLostException
    {
        System.out.println("object HELLOOOOOO");
        ioio_.beginBatch();

            try {
                pwm_speed_output.setPulseWidth(1800);
                pwm_steering_output.setPulseWidth(1800);
                //set_speed(1800);
                dataIRCenter = irCenter.read();
                System.out.println("object dataIRCenter" +dataIRCenter);

                   if (dataIRCenter > 0.3){
                       System.out.println("object detected!!!!!!!!!!!!!!");
                   }
                    //irCenterReading = irCenter.getVoltage();

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

    public synchronized void set_speed(int value)
    {
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