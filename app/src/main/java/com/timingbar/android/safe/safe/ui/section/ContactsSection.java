package com.timingbar.android.safe.safe.ui.section;

import android.content.Context;
import android.view.View;
import android.widget.Toast;
import com.timingbar.android.safe.R;
import com.timingbar.safe.library.view.recyclerview.base.ViewHolder;
import com.timingbar.safe.library.view.recyclerview.section.SectionParameters;
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

public class ContactsSection extends StatelessSection {
    String title;
    List<String> list;
    Context mContext;

    public ContactsSection(Context context, String title, List<String> list) {
        super (new SectionParameters.Builder (R.layout.section_ex1_item).headerResourceId (R.layout.section_ex1_header).build ());
        this.mContext = context;
        this.title = title;
        this.list = list;
    }

    @Override
    public int getContentItemsTotal() {
        return list == null ? -1 : list.size ();
    }

    @Override
    public ViewHolder getItemViewHolder(View view) {
        return super.getItemViewHolder (view);
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
    public ViewHolder getHeaderViewHolder(View view) {
        return super.getHeaderViewHolder (view);
    }

    @Override
    public void onBindHeaderViewHolder(ViewHolder holder) {
        super.onBindHeaderViewHolder (holder);
        holder.setText (R.id.tv_section_title, title);
    }
}
