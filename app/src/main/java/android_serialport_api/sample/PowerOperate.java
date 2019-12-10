package android_serialport_api.sample;
import com.android.charger.*;
public class PowerOperate
{
  private static final int RIFID_MODULE_PIN = 95;
  private static final int FINGERPRINT_MODULE_5V_PIN = 4;
  public static void enableRIFID_Module_5Volt()
  {
    boolean isHigh = true;
    System.out.println("kkohhho");
    mtSetGPIOValue(RIFID_MODULE_PIN, isHigh);
  }
  
  public static void disableRIFID_Module_5Volt()
  {
    boolean isHigh = false;
    mtSetGPIOValue(RIFID_MODULE_PIN, isHigh);
  }

  public static void enableFingerprintModule_5Volt()
  {
    boolean isHigh = true;
    mtSetGPIOValue(FINGERPRINT_MODULE_5V_PIN, isHigh);
  }
  
  public static void disableFingerprintModule_5Volt()
  {
    boolean isHigh = false;
    mtSetGPIOValue(FINGERPRINT_MODULE_5V_PIN, isHigh);
  }
 
  
  private static  void mtSetGPIOValue(int pin, boolean bHigh)
  {
    if (pin < 0) {
      return;
    }
   
    EmGpio.gpioInit();
    System.out.println("kkoogggg");
    EmGpio.setGpioMode(pin);
    if (bHigh)
    {
    	EmGpio.setGpioOutput(pin);
    	EmGpio.setGpioDataHigh(pin);
    }
    else
    {
    	EmGpio.setGpioOutput(pin);
    	EmGpio.setGpioDataLow(pin);
    }
    EmGpio.gpioUnInit();
  }
}
