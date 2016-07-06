package pager;

/**
 * Created by 鼎鈞 on 2016/6/25.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.dualdisplaydemo.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import myvideoview.PlayList;
import myvideoview.SourceData;

public class MyPagerAdapter extends PagerAdapter {

    private List<View> mList;

    public int getListSize(){
        return mList.size();
    }

    public MyPagerAdapter(Context context, PlayList myPlayList) {
        mList = new ArrayList<View>();
        List<SourceData> listData = myPlayList.getList();

        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        for (SourceData data : listData) {
            String path = data.getPath();
            File file = new File(path);
            if (!file.exists())
                continue;

            ImageView mImageView = new ImageView(context);
            if (data.isImage()) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                mImageView.setImageBitmap(bitmap);
                mImageView.setLayoutParams(mParams);
                mList.add(mImageView);
            } else if (data.isVedio()) {
                mImageView.setImageResource(R.drawable.no1);
                mList.add(mImageView);
            }
            //mImageView.setBackgroundResource(list.get(i));
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // TODO Auto-generated method stub
        return arg0 == arg1;
    }

    //销毁上一页
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub
        position = position % mList.size();
        container.removeView(mList.get(position));
    }

    //添加下一页
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // TODO Auto-generated method stub
        position = position % mList.size();
        container.addView(mList.get(position));
        return mList.get(position);
    }

}
