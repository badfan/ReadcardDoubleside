package android_serialport_api.sample;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class dianyuan extends Activity {
    private Button powerOn = null;
    private Button powerDown = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dy);
        powerOn = (Button) findViewById(R.id.poweron);
        powerOn.setOnClickListener(new kai());
        powerDown = (Button) findViewById(R.id.powerdown);
        powerDown.setOnClickListener(new guan());

    }

    class kai implements OnClickListener {

        @Override
        public void onClick(View p1) {
            // TODO: Implement this method
            System.out.println("kkoo");
            PowerOperate.enableRIFID_Module_5Volt();

        }

    }

    class guan implements OnClickListener {

        @Override
        public void onClick(View p1) {
            // TODO: Implement this method


        }

    }
}
