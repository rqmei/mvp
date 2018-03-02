package com.timingbar.safe.library.view.imageloader.glide;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import jp.wasabeef.glide.transformations.gpu.VignetteFilterTransformation;

/**
 * ShowImageUtils
 * -----------------------------------------------------------------------------------------------------------------------------------
 * (1)ImageView设置图片（错误图片）
 * （2）ImageView设置图片---BitMap(不设置默认图)
 * （3） ImageView 设置圆角图片
 * （4）ImageView 设置矩形圆角
 * （5）ImageView 设置高斯模糊
 * （6）ImageView 设置高斯模糊+圆角
 * （7）ImageView 设置高斯模糊+矩形圆角
 * （8）View设置背景图片
 * （9）View设置背景图片(高斯模糊)
 * （10）View设置背景图片(圆形)
 * （11）View设置背景图片(矩形圆角)
 *
 * @author rqmei on 2018/1/23
 */

public class ShowImageUtils {
    /**
     * (1)
     * 显示图片 Imageview
     *
     * @param context  上下文
     * @param errorimg 错误的资源图片
     * @param url      图片链接
     * @param imgeview 组件
     */
    public static void showImageView(Context context, int errorimg, String url, ImageView imgeview) {
        Glide.with(context).load(url)// 加载图片
                .error(errorimg)// 设置错误图片
                .crossFade()// 设置淡入淡出效果，默认300ms，可以传参
                .placeholder(errorimg)// 设置占位图
                .diskCacheStrategy(DiskCacheStrategy.RESULT)// 缓存修改过的图片
                .into(imgeview);
        //.dontAnimate() //不显示动画效果

    }

    /**
     * （2）
     * 显示图片 获取到Bitmap---不设置错误图片，加载失败时隐藏imageView控件
     *
     * @param context   上下文
     * @param imageView 组件
     * @param url       图片链接
     */

    public static void showImageViewGone(Context context, final ImageView imageView, String url) {
        Glide.with(context).load(url).asBitmap().diskCacheStrategy(DiskCacheStrategy.RESULT)// 缓存修改过的图片
                .into(new SimpleTarget<Bitmap>() {

                    @SuppressLint("NewApi")
                    @Override
                    public void onResourceReady(Bitmap loadedImage, GlideAnimation<? super Bitmap> arg1) {
                        //加载图片成功
                        imageView.setVisibility(View.VISIBLE);
                        BitmapDrawable bd = new BitmapDrawable(loadedImage);
                        imageView.setImageDrawable(bd);
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        //加载失败，隐藏imageView控件
                        imageView.setVisibility(View.GONE);
                    }

                });

    }


    /**
     * （3）
     * 显示图片 圆角显示  ImageView
     *
     * @param context  上下文
     * @param errorimg 错误的资源图片
     * @param url      图片链接
     * @param imgeview 组件
     */
    public static void showImageViewToCircle(Context context, int errorimg, String url, ImageView imgeview) {
        Glide.with(context).load(url)
                // 加载图片
                .error(errorimg)
                // 设置错误图片
                .crossFade()
                // 设置淡入淡出效果，默认300ms，可以传参
                .placeholder(errorimg)
                // 设置占位图
                .bitmapTransform(new CropCircleTransformation(context))//圆角
                .diskCacheStrategy(DiskCacheStrategy.RESULT)// 缓存修改过的图片
                .into(imgeview);
    }

    /**
     * （4）
     * 显示图片 矩形圆角 ImageView
     *
     * @param context    上下文
     * @param errorimg   错误的资源图片
     * @param url        图片链接
     * @param imgeview   组件
     * @param radius     圆角大小
     * @param CornerType 显示圆角的位置（比如：all全部；BOTTOM下方；BOTTOM_LEFT坐下）
     */
    public static void showImageViewToRoundedCorners(Context context, int errorimg, String url, ImageView imgeview, int radius, RoundedCornersTransformation.CornerType CornerType) {
        Glide.with(context).load(url).error(errorimg)// 设置错误图片
                .bitmapTransform(new RoundedCornersTransformation(context, radius, 0, CornerType))// 设置矩形圆角
                .diskCacheStrategy(DiskCacheStrategy.RESULT)// 缓存修改过的图片
                .into(imgeview);
    }

    /**
     * （5）
     * 显示图片 高斯模糊 ImageView
     *
     * @param context    上下文
     * @param errorimg   错误的资源图片
     * @param url        图片链接
     * @param imgeview   组件
     * @param radius     圆角大小
     * @param CornerType 显示圆角的位置（比如：all全部；BOTTOM下方；BOTTOM_LEFT坐下）
     */
    public static void showImageViewToBlur(Context context, int errorimg, String url, ImageView imgeview, int radius, RoundedCornersTransformation.CornerType CornerType) {
        Glide.with(context).load(url).error(errorimg)// 设置错误图片
                .bitmapTransform(new BlurTransformation(context))// 设置矩形圆角
                .diskCacheStrategy(DiskCacheStrategy.RESULT)// 缓存修改过的图片
                .into(imgeview);
    }

    /**
     * （6）
     * 显示图片 多种样式（高斯模糊+圆角） ImageView
     *
     * @param context  上下文
     * @param errorimg 错误的资源图片
     * @param url      图片链接
     * @param imgeview 组件
     */
    public static void showImageViewToCircleAndBlur(Application context, int errorimg, String url, ImageView imgeview) {
        Glide.with(context).load(url).error(errorimg)// 设置错误图片
                .bitmapTransform(new BlurTransformation(context), new CropCircleTransformation(context))// 设置高斯模糊，圆角
                .diskCacheStrategy(DiskCacheStrategy.RESULT)// 缓存修改过的图片
                .into(imgeview);
    }

    /**
     * （7）
     * 显示图片 多种样式（高斯模糊+矩形圆角）ImageView
     *
     * @param context    上下文
     * @param errorimg   错误的资源图片
     * @param url        图片链接
     * @param imgeview   组件
     * @param radius     圆角大小
     * @param CornerType 显示圆角的位置（比如：all全部；BOTTOM下方；BOTTOM_LEFT坐下）
     */
    public static void showImageViewToRoundedCornersAndBlur(Context context, int errorimg, String url, ImageView imgeview, int radius, RoundedCornersTransformation.CornerType CornerType) {
        Glide.with(context).load(url).error(errorimg)// 设置错误图片
                .bitmapTransform(new BlurTransformation(context), new RoundedCornersTransformation(context, radius, 0, CornerType))// 设置矩形圆角
                .diskCacheStrategy(DiskCacheStrategy.RESULT)// 缓存修改过的图片
                .into(imgeview);
    }

    /**
     * （8）
     * 设置控件的背景图片 bgLayout
     * <p>
     * 获取到Bitmap
     *
     * @param context  上下文
     * @param errorimg 加载错误的背景资源图片
     * @param url      图片链接
     * @param bgLayout 展示图片的组件
     */

    public static void showImageViewToBg(Context context, int errorimg, String url, final View bgLayout) {
        Glide.with(context).load(url).asBitmap().error(errorimg)// 设置错误图片

                .diskCacheStrategy(DiskCacheStrategy.RESULT)// 缓存修改过的图片
                .placeholder(errorimg)// 设置占位图
                .into(new SimpleTarget<Bitmap>() {

                    @SuppressLint("NewApi")
                    @Override
                    public void onResourceReady(Bitmap loadedImage, GlideAnimation<? super Bitmap> arg1) {
                        //加载图片成功
                        BitmapDrawable bd = new BitmapDrawable(loadedImage);

                        bgLayout.setBackground(bd);

                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        // 加载图片失败
                        super.onLoadFailed(e, errorDrawable);

                        bgLayout.setBackgroundDrawable(errorDrawable);
                    }

                });

    }


    /**
     * （9）
     * 设置控件的背景图片 bgLayout
     * 获取到Bitmap 高斯模糊
     *
     * @param context  上下文
     * @param errorimg 加载错误的背景资源图片
     * @param url      图片链接
     * @param bgLayout 展示图片的组件
     */

    public static void showImageViewToBlurBg(Context context, int errorimg, String url, final View bgLayout) {
        Glide.with(context).load(url).asBitmap().error(errorimg)
                // 设置错误图片
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                // 缓存修改过的图片
                .placeholder(errorimg).transform(new BlurTransformation(context))// 高斯模糊处理
                // 设置占位图
                .into(new SimpleTarget<Bitmap>() {

                    @SuppressLint("NewApi")
                    @Override
                    public void onResourceReady(Bitmap loadedImage, GlideAnimation<? super Bitmap> arg1) {
                        BitmapDrawable bd = new BitmapDrawable(loadedImage);
                        bgLayout.setBackground(bd);

                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        // 加载失败展示默认的错图图片
                        super.onLoadFailed(e, errorDrawable);
                        bgLayout.setBackgroundDrawable(errorDrawable);
                    }

                });

    }

    /**
     * （10）
     * 设置控件的背景图片（圆形） bgLayout
     * <p>
     * 获取到Bitmap
     *
     * @param context  上下文
     * @param errorimg 加载错误的背景资源图片
     * @param url      图片链接
     * @param bgLayout 展示图片的组件
     */

    public static void showImageViewToCircleBg(Context context, int errorimg, String url, final View bgLayout) {
        Glide.with(context).load(url).asBitmap().error(errorimg)// 设置错误图片

                .diskCacheStrategy(DiskCacheStrategy.RESULT)// 缓存修改过的图片
                .placeholder(errorimg).transform(new CropCircleTransformation(context))// 设置占位图
                .into(new SimpleTarget<Bitmap>() {

                    @SuppressLint("NewApi")
                    @Override
                    public void onResourceReady(Bitmap loadedImage, GlideAnimation<? super Bitmap> arg1) {
                        //加载图片成功
                        BitmapDrawable bd = new BitmapDrawable(loadedImage);

                        bgLayout.setBackground(bd);

                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        // 加载图片失败
                        super.onLoadFailed(e, errorDrawable);

                        bgLayout.setBackgroundDrawable(errorDrawable);
                    }

                });

    }

    /**
     * （11）
     * 设置控件的背景图片（矩形圆形） bgLayout
     * <p>
     * 获取到Bitmap
     *
     * @param context  上下文
     * @param errorimg 加载错误的背景资源图片
     * @param url      图片链接
     * @param bgLayout 展示图片的组件
     * @param radius   圆形角度
     */

    public static void showImageViewToRoundedCircleBg(Context context, int errorimg, String url, final View bgLayout, int radius) {
        Glide.with(context).load(url).asBitmap().error(errorimg)// 设置错误图片

                .diskCacheStrategy(DiskCacheStrategy.RESULT)// 缓存修改过的图片
                .placeholder(errorimg).transform(new RoundedCornersTransformation(context, radius, 0, RoundedCornersTransformation.CornerType.ALL))// 设置占位图
                .into(new SimpleTarget<Bitmap>() {

                    @SuppressLint("NewApi")
                    @Override
                    public void onResourceReady(Bitmap loadedImage, GlideAnimation<? super Bitmap> arg1) {
                        //加载图片成功
                        BitmapDrawable bd = new BitmapDrawable(loadedImage);
                        bgLayout.setBackground(bd);

                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        // 加载图片失败
                        super.onLoadFailed(e, errorDrawable);
                        bgLayout.setBackgroundDrawable(errorDrawable);
                    }

                });

    }

    /**
     * （12）有点问题
     * 遮罩图层 ImageView
     *
     * @param context  上下文
     * @param errorimg 错误的资源图片
     * @param url      图片链接
     * @param imgeview 组件
     */
    public static void showImageViewToMask(Context context, int errorimg, String url, ImageView imgeview) {
        Glide.with(context).load(url).error(errorimg)// 设置错误图片
                .bitmapTransform(new VignetteFilterTransformation(context, new PointF(0.5f, 0.5f), new float[]{0.0f, 0.0f, 0.0f}, 0f, 0.75f)).diskCacheStrategy(DiskCacheStrategy.RESULT)// 缓存修改过的图片
                .into(imgeview);


    }
}
