package com.libtop.weitu.activity.search.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.search.dto.SearchResult;
import com.libtop.weitu.base.impl.ImgAdapter;
import com.libtop.weitu.utils.ContantsUtil;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by Administrator on 2015/12/23 0023.
 */
public class ResultListAdapter extends ImgAdapter
{
    public ResultListAdapter(Context context, List<?> data)
    {
        super(context, data, R.layout.item_list_result2);
    }


    @Override
    protected void newView(View convertView)
    {
        ViewHolder holder = new ViewHolder();
        holder.iconCover = (ImageView) convertView.findViewById(R.id.icon);
        holder.titleText = (TextView) convertView.findViewById(R.id.title);
        holder.uploaderText = (TextView) convertView.findViewById(R.id.uploader);
        convertView.setTag(holder);
    }


    @Override
    protected void holderView(View convertView, Object itemObject, int position)
    {
        ViewHolder holder = (ViewHolder) convertView.getTag();
        SearchResult data = (SearchResult) itemObject;
        holder.titleText.setText(data.title);
        holder.uploaderText.setText("上传者:" + data.uploadUsername);
        Picasso.with(mContext).load(ContantsUtil.getCoverUrl(data.id)).into(holder.iconCover);
    }


    protected class ViewHolder
    {
        ImageView iconCover;
        TextView titleText, uploaderText;
    }
}
