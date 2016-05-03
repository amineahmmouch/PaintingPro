package commacreations.apps.paintingpro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

public class Splashscreen extends Activity {

    protected int _splashTime = 4000;
    private Thread _splashTread;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splahscreen);

        _splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized(this){
                        wait(_splashTime);
                    }

                } catch(InterruptedException e) {}
                finally {
                    finish();
                    Intent i = new Intent();
                    i.setClass(Splashscreen.this, Home.class);
                    startActivity(i);
                }
            }
        };
        _splashTread.start();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            synchronized(_splashTread){
                _splashTread.notifyAll();
            }
        }
        return true;
    }

    protected void onPause() {
        super.onPause();
        finish();
    }
}
