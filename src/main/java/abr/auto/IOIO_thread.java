package abr.auto;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;

import android.util.Log;


public class IOIO_thread extends BaseIOIOLooper
{
    AnalogInput  irCenter;

    float irCenterReading;
    float dataIRCenter;

    @Override
    public void setup() throws ConnectionLostException
    {
        try
        {
            irCenter = ioio_.openAnalogInput(43);
        }
        catch (ConnectionLostException e){throw e;}
    }

    @Override
    public void loop() throws ConnectionLostException
    {
        System.out.println("object HELLOOOOOO");
        ioio_.beginBatch();

            try {
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


}