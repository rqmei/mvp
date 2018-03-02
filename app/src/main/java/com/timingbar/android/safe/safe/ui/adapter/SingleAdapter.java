package com.timingbar.android.safe.safe.ui.adapter;

import android.content.Context;
import android.widget.ImageView;
import com.timingbar.android.safe.R;
import com.timingbar.android.safe.safe.modle.entity.User;
import com.timingbar.safe.library.view.imageloader.ImageLoader;
import com.timingbar.safe.library.view.imageloader.glide.GlideImageConfig;
import com.timingbar.safe.library.view.recyclerview.adapter.CommonAdapter;
import com.timingbar.safe.library.view.recyclerview.base.ViewHolder;
import timber.log.Timber;


/**
 * SingleAdapter
 * -----------------------------------------------------------------------------------------------------------------------------------
 * 单个布局的adapter使用用例
 *
 * @author rqmei on 2018/3/1
 */

public class SingleAdapter extends CommonAdapter<User> {
    Context mContext;
    ImageLoader imageLoader;

    public SingleAdapter(Context context, ImageLoader imageLoader) {
        super (context, R.layout.item_single);
        this.mContext = context;
        this.imageLoader = imageLoader;
    }

    @Override
    protected void convert(ViewHolder holder, User user, int position) {
        Timber.i ("SingleAdapter convert--->" + user.getUserName ());
        holder.setText (R.id.tv_user_name, user.getUserName ());
        //两种加载图片的方式都可以
        // holder.setImageUrl (imageLoader, R.id.image, "http://img1.imgtn.bdimg.com/it/u=3440016527,164210732&fm=27&gp=0.jpg");
        ImageView imageView = holder.getView (R.id.image);
        imageLoader.loadImage (mContext, GlideImageConfig.builder ().imageView (imageView).transformationType (1).url ("http://img1.imgtn.bdimg.com/it/u=3440016527,164210732&fm=27&gp=0.jpg").errorPic (R.mipmap.ic_launcher).isClearDiskCache (true).isClearMemory (true).build ());
    }

}
