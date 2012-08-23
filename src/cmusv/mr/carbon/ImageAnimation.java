package cmusv.mr.carbon;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class ImageAnimation{
	public final int[] bg= {R.drawable.background_1, R.drawable.background_2, R.drawable.background_3, R.drawable.background_4, R.drawable.background_5, R.drawable.background_6, R.drawable.background_7, R.drawable.background_8,R.drawable.background_9,R.drawable.background_10};
	public AnimationDrawable animation = null;
	private Context context;
	private ImageView imageView;
	public ImageAnimation(Context context, ImageView imageView){
		this.context = context;
		this.imageView = imageView; 
	}
	public void setAnimation(int[] drawablesAddrArray, int timeInterval){
		animation = new AnimationDrawable();
        for(int drawableAddr: drawablesAddrArray){
        	animation.addFrame(context.getResources().getDrawable(drawableAddr), timeInterval);
        }
        
        animation.setOneShot(false);
        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT );  
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams( LayoutParams.FILL_PARENT , 200);
        
        imageView.setScaleType(ScaleType.FIT_XY);
        imageView.setLayoutParams(params);
        imageView.setImageDrawable(animation);
	}
	public void startAnimation(){
        if(animation!=null)
        	imageView.post(new Starter());
    }
	public void stopAnimation(){
		if(animation!=null & animation.isRunning())
			animation.stop();
	}
    class Starter implements Runnable {
        public void run() {
             animation.start();
         }
     }
}
