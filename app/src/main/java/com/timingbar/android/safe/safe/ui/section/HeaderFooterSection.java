package com.timingbar.android.safe.safe.ui.section;

import android.content.Context;
import android.view.View;
import android.widget.Toast;
import com.timingbar.android.safe.R;
import com.timingbar.safe.library.view.recyclerview.base.ViewHolder;
import com.timingbar.safe.library.view.recyclerview.section.SectionParameters;
import com.timingbar.safe.library.view.recyclerview.section.StatelessSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * HeaderFootSection
 * -----------------------------------------------------------------------------------------------------------------------------------
 *
 * @author rqmei on 2018/3/9
 */

public class HeaderFooterSection extends StatelessSection {
    String title;
    List<String> list;
    int imgPlaceholderResId;
    Context mContext;

    public HeaderFooterSection(Context context, int type) {
        super (new SectionParameters.Builder (R.layout.section_ex1_item).headerResourceId (R.layout.section_ex1_header).footerResourceId (R.layout.section_footer).build ());
        this.mContext = context;
        switch (type) {
            case 0:
                this.title = "World";
                this.list = getNews (R.array.news_world);
                this.imgPlaceholderResId = R.mipmap.ic_news_time;
                break;
            case 1:
                this.title = "Business";
                this.list = getNews (R.array.news_biz);
                this.imgPlaceholderResId = R.mipmap.ic_launcher;
                break;
            case 2:
                this.title = "Technology";
                this.list = getNews (R.array.news_tech);
                this.imgPlaceholderResId = R.mipmap.ic_news_time;
                break;
        }
    }

    @Override
    public int getContentItemsTotal() {
        return list == null ? -1 : list.size ();
    }


    @Override
    public void onBindItemViewHolder(ViewHolder holder, int position) {
        super.onBindItemViewHolder (holder, position);
        holder.setImageResource (R.id.imgItem, imgPlaceholderResId);
        holder.setText (R.id.tvItem, list.get (position));
    }

    @Override
    public void onBindHeaderViewHolder(ViewHolder holder) {
        super.onBindHeaderViewHolder (holder);
        holder.setText (R.id.tv_section_title, title);
    }

    @Override
    public void onBindFooterViewHolder(ViewHolder holder) {
        super.onBindFooterViewHolder (holder);
        holder.itemView.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Toast.makeText (mContext, "see more....", Toast.LENGTH_SHORT).show ();
            }
        });
    }

    private List<String> getNews(int arrayResource) {
        return new ArrayList<> (Arrays.asList (mContext.getResources ().getStringArray (arrayResource)));
    }

}
