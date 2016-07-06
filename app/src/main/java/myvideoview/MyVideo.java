package myvideoview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by 鼎鈞 on 2016/6/22.
 */
public class MyVideo extends VideoView {

    public MyVideo(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public MyVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public MyVideo(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    //重點在此，override這個 function 才可以正常滿版!
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
}