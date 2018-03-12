package com.timingbar.android.safe.safe.ui.section;

import android.content.Context;
import android.view.View;
import android.widget.Toast;
import com.timingbar.android.safe.R;
import com.timingbar.safe.library.view.recyclerview.base.ViewHolder;
import com.timingbar.safe.library.view.recyclerview.section.SectionParameters;
import com.timingbar.safe.library.view.recyclerview.section.SectionedRecyclerViewAdapter;
import com.timingbar.safe.library.view.recyclerview.section.StatelessSection;
import timber.log.Timber;

import java.util.List;

/**
 * ContactsSection
 * -----------------------------------------------------------------------------------------------------------------------------------
 * section itemView中添加头文件
 *
 * @author rqmei on 2018/3/8
 */

public class HeaderSection extends StatelessSection {
    String title;
    List<String> list;
    Context mContext;
    boolean expanded = true;
    SectionedRecyclerViewAdapter sectionAdapter;

    public HeaderSection(Context context, String title, List<String> list) {
        super (new SectionParameters.Builder (R.layout.section_ex1_item).headerResourceId (R.layout.section_ex1_header).build ());
        this.mContext = context;
        this.title = title;
        this.list = list;
    }

    public HeaderSection(SectionedRecyclerViewAdapter sectionAdapter, Context context, String title, List<String> list) {
        super (new SectionParameters.Builder (R.layout.section_ex1_item).headerResourceId (R.layout.section_ex1_header).build ());
        this.mContext = context;
        this.title = title;
        this.list = list;
        this.sectionAdapter = sectionAdapter;
    }

    @Override
    public int getContentItemsTotal() {
        int size = 0;
        if (expanded) {
            size = list == null ? -1 : list.size ();
        }
        return size;
    }

    @Override
    public void onBindItemViewHolder(ViewHolder holder, int position) {
        String name = list.get (position);
        holder.setText (R.id.tvItem, name);
        holder.setImageResource (R.id.imgItem, R.mipmap.ic_news_time);

        holder.itemView.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Toast.makeText (mContext, String.format ("Clicked on position #%s of Section %s", position, title), Toast.LENGTH_SHORT).show ();
            }
        });
    }


    @Override
    public void onBindHeaderViewHolder(ViewHolder holder) {
        super.onBindHeaderViewHolder (holder);
        holder.setText (R.id.tv_section_title, title);
        if (sectionAdapter != null && expanded) {
            holder.setImageResource (R.id.iv_section_header_arrow, R.mipmap.ic_keyboard_arrow_down_black_18dp);
        }
        holder.itemView.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if (sectionAdapter != null) {
                    expanded = !expanded;
                    if (expanded) {
                        holder.setImageResource (R.id.iv_section_header_arrow, R.mipmap.ic_keyboard_arrow_down_black_18dp);
                    } else {
                        holder.setImageResource (R.id.iv_section_header_arrow, R.mipmap.ic_keyboard_arrow_right_black_18dp);
                    }
                    sectionAdapter.notifyDataSetChanged ();
                }
            }
        });

    }
}
