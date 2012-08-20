package cmusv.mr.carbon;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ImageAnimation{
	public final int[] test= {R.drawable.mrc_angry,R.drawable.mrc_happy, R.drawable.mrc_sad};
	public AnimationDrawable animation;
	private Context context;
	private ImageView imageView;
	public ImageAnimation(Context context, ImageView imageView){
		this.context = context;
		this.imageView = imageView; 
	}
	public void startAnimation(int[] drawablesAddrArray){
        animation = new AnimationDrawable();
        for(int drawableAddr: drawablesAddrArray){
        	animation.addFrame(context.getResources().getDrawable(drawableAddr), 1000);
        }
        
        animation.setOneShot(false);
        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT ,LinearLayout.LayoutParams.WRAP_CONTENT );  
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(286,370);

        imageView.setLayoutParams(params);
        imageView.setImageDrawable(animation);
        imageView.post(new Starter());
    }
    class Starter implements Runnable {
        public void run() {
             animation.start();
         }
     }
}
