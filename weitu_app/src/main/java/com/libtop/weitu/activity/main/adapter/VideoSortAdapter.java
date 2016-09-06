package com.libtop.weitu.activity.main.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.main.videoUpload.VideoSortFragment;
import com.libtop.weitu.base.BaseAdapter;

import java.util.List;


/**
 * Created by LianTu on 2016/4/25.
 */
public class VideoSortAdapter extends BaseAdapter<VideoSortFragment.VideoSortBean>
{


    public VideoSortAdapter(Context context, List<VideoSortFragment.VideoSortBean> data)
    {
        super(context, data, R.layout.item_grid_string);
    }


    @Override
    protected void newView(View convertView)
    {

    }


    @Override
    protected void holderView(View convertView, VideoSortFragment.VideoSortBean videoSortBean, int position)
    {
        TextView text = (TextView) convertView.findViewById(R.id.text);
        text.setText(videoSortBean.sortName);
    }

}
