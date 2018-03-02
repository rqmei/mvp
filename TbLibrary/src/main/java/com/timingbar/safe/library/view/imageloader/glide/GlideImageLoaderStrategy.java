package com.timingbar.safe.library.view.imageloader.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.*;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.timingbar.safe.library.view.imageloader.IBaseImageLoaderStrategy;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


/**
 * GlideImageLoaderStrategy 策略模式接口的相关具体实现（IBaseImageLoaderStrategy的子类）
 * -----------------------------------------------------------------------------------------------------------------------------------
 * 这里是图片加载配置信息的基类,可以定义一些所有图片加载框架都可以用的通用参数
 *
 * @author rqmei on 2018/1/25
 */
public class GlideImageLoaderStrategy implements IBaseImageLoaderStrategy<GlideImageConfig> {
    /**
     * 加载图片
     *
     * @param ctx    上下文
     * @param config 需加载图片的配置信息
     */
    @Override
    public void loadImage(Context ctx, GlideImageConfig config) {
        if (ctx == null)
            throw new IllegalStateException ("Context is required");
        if (config == null)
            throw new IllegalStateException ("GlideImageConfig is required");
        if (TextUtils.isEmpty (config.getUrl ()))
            throw new IllegalStateException ("Url is required");
        if (config.getImageView () == null && config.getBgView () == null)
            throw new IllegalStateException ("Imageview and vgView is required");
        Transformation<Bitmap> transformation = null;
        if (config.getTransformation () != null) {
            transformation = config.getTransformation ();
        } else if (config.getTransformationType () != 0) {
            switch (config.getTransformationType ()) {
                //图片形状
                case 1://圆形
                    transformation = new CropCircleTransformation (ctx);
                    break;
                case 2://矩形圆角
                    transformation = new RoundedCornersTransformation (ctx, 15, 0, RoundedCornersTransformation.CornerType.ALL);
                    break;
                case 3://高斯模糊
                    transformation = new BlurTransformation (ctx);
                    break;
            }
        }

        RequestManager manager = Glide.with (ctx);//如果context是activity则自动使用Activity的生命周期
        //设置ImageView的资源图片
        if (config.getImageView () != null) {
            DrawableRequestBuilder<String> requestBuilder = manager.load (config.getUrl ())
                    .crossFade ()//灰度处理 
                    .centerCrop ()//裁剪处理
                    .fitCenter ();//居中处理

            setImageConfig (config, requestBuilder);
            //glide用它来改变图形的形状
            if (transformation != null) {
                requestBuilder.bitmapTransform (transformation);
            } else if (config.getTransformationType () == 4) {
                //高斯模糊+圆形
                requestBuilder.bitmapTransform (new BlurTransformation (ctx), new CropCircleTransformation (ctx));
            } else if (config.getTransformationType () == 5) {
                //高斯模糊+矩形圆角
                requestBuilder.bitmapTransform (new BlurTransformation (ctx), new RoundedCornersTransformation (ctx, 15, 0, RoundedCornersTransformation.CornerType.ALL));
            }
            requestBuilder.into (config.getImageView ());
        }
        //设置View的背景图片
        if (config.getBgView () != null) {
            BitmapRequestBuilder<String, Bitmap> bitmapTypeRequest = manager.load (config.getUrl ())
                    .asBitmap ()//显示gif静态图片
                    .crossFade ().centerCrop ();
            //            GifRequestBuilder<String> gifRequestBuilder = manager.load (config.getUrl ()).asGif ().crossFade ().centerCrop ();
            //            gifRequestBuilder.into (new SimpleTarget<GifDrawable> () {
            //                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            //                @Override
            //                public void onResourceReady(GifDrawable resource, GlideAnimation<? super GifDrawable> glideAnimation) {
            //                    config.getBgView ().setBackground (resource);
            //                }
            //            });
            setImageConfig (config, bitmapTypeRequest);
            //glide用它来改变图形的形状
            if (transformation != null) {
                bitmapTypeRequest.transform (transformation);
            } else if (config.getTransformationType () == 4) {
                //高斯模糊+圆形
                bitmapTypeRequest.transform (new BlurTransformation (ctx), new CropCircleTransformation (ctx));
            } else if (config.getTransformationType () == 5) {
                //高斯模糊+矩形圆角
                bitmapTypeRequest.transform (new BlurTransformation (ctx), new RoundedCornersTransformation (ctx, 15, 0, RoundedCornersTransformation.CornerType.ALL));
            }
            //加载图片
            bitmapTypeRequest.into (new SimpleTarget<Bitmap> () {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onResourceReady(Bitmap bgView, GlideAnimation<? super Bitmap> glideAnimation) {
                    BitmapDrawable bd = new BitmapDrawable (bgView);
                    config.getBgView ().setBackground (bd);
                }
            });
        }

    }

    /**
     * 设置加载图片的通用设置
     *
     * @param config
     * @param genericRequestBuilder
     */
    private void setImageConfig(GlideImageConfig config, GenericRequestBuilder genericRequestBuilder) {
        switch (config.getCacheStrategy ()) {//缓存策略
            case 0:
                genericRequestBuilder.diskCacheStrategy (DiskCacheStrategy.ALL);
                break;
            case 1:
                genericRequestBuilder.diskCacheStrategy (DiskCacheStrategy.NONE);
                break;
            case 2:
                genericRequestBuilder.diskCacheStrategy (DiskCacheStrategy.SOURCE);
                break;
            case 3:
                genericRequestBuilder.diskCacheStrategy (DiskCacheStrategy.RESULT);
                break;
        }
        if (config.getPlaceholder () != 0)//设置占位符
            genericRequestBuilder.placeholder (config.getPlaceholder ());

        if (config.getErrorPic () != 0)//设置错误的图片
            genericRequestBuilder.error (config.getErrorPic ());
    }

    /**
     * 根据相关配置信息清空加载图片时产生的缓存信息
     *
     * @param ctx    上下文
     * @param config 需清空图片的配置信息
     */
    @Override
    public void clear(final Context ctx, GlideImageConfig config) {
        if (ctx == null)
            throw new IllegalStateException ("Context is required");
        if (config == null)
            throw new IllegalStateException ("GlideImageConfig is required");

        if (config.getImageViews () != null && config.getImageViews ().length > 0) {//取消在执行的任务并且释放资源
            for (ImageView imageView : config.getImageViews ()) {
                Glide.clear (imageView);
            }
        }
        if (config.getBgViews () != null && config.getBgViews ().length > 0) {
            for (View bgView : config.getBgViews ()) {
                Glide.clear (bgView);
            }
        }
        if (config.getTargets () != null && config.getTargets ().length > 0) {//取消在执行的任务并且释放资源
            for (Target target : config.getTargets ())
                Glide.clear (target);
        }


        if (config.isClearDiskCache ()) {//清除本地缓存
            Observable.just (0)
                    .observeOn (Schedulers.io ())
                    .subscribe (integer -> Glide.get (ctx).clearDiskCache ());
        }

        if (config.isClearMemory ()) {//清除内存缓存
            Glide.get (ctx).clearMemory ();
        }

    }
}
