package com.libtop.weituR.activity.main.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.search.dto.BookDto;
import com.libtop.weituR.base.BaseAdapter;
import com.libtop.weituR.utils.CheckUtil;
import com.libtop.weituR.utils.ContantsUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by LianTu on 2016/6/22.
 */
public class RmdBooksAdapter extends BaseAdapter<BookDto> {

    public RmdBooksAdapter(Context context, List<BookDto> data) {
        super(context, data, R.layout.item_list_rmd_books);
    }

    @Override
    protected void newView(View convertView) {
        Holder holder = new Holder();
        holder.bookCover=(ImageView)convertView.findViewById(R.id.rmd_book_cover_img);
        holder.titleText = (TextView) convertView.findViewById(R.id.rmd_book_title);
        holder.authorText = (TextView) convertView.findViewById(R.id.rmd_book_author);
        holder.publisherText = (TextView) convertView.findViewById(R.id.rmd_book_publisher);
        holder.introduceText = (TextView) convertView.findViewById(R.id.rmd_book_introduction);
        convertView.setTag(holder);
    }

    @Override
    protected void holderView(View convertView, BookDto book, int position) {
        Holder holder = (Holder) convertView.getTag();
        Picasso.with(mContext).load(ContantsUtil.IMG_BASE + book.cover).placeholder(R.drawable.default_image).into(holder.bookCover);
        holder.titleText.setText(book.title);
        holder.authorText.setText(book.author);
        holder.publisherText.setText(book.publisher);
        if(!CheckUtil.isNull(book.introduction))
        holder.introduceText.setText(book.introduction);
    }

    private class Holder{
        ImageView bookCover;
        TextView titleText,authorText,publisherText,introduceText;
    }

}
