package com.d42gmail.cavar.beforeandafter.custom_view;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.os.AsyncTask;
import android.os.Looper;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ClipDrawableAsync<T> extends AsyncTask<T, Void, ArrayList<ClipDrawable>> {
    private final WeakReference<ImageView> imageRefLeft;
    private final WeakReference<ImageView> imageRefRight;
    private final WeakReference<SeekBar> seekBarRef;
    private final int progress;
    private final LoadingListener loadedFinishedListener;

    public ClipDrawableAsync(ImageView imageLeftView, ImageView imageRightView, SeekBar seekBar, int progress, LoadingListener loadedFinishedListener) {
        this.imageRefLeft = new WeakReference<>(imageLeftView);
        this.imageRefRight = new WeakReference<>(imageRightView);
        this.seekBarRef = new WeakReference<>(seekBar);
        this.progress = progress;
        this.loadedFinishedListener = loadedFinishedListener;
    }

    @Override
    protected ArrayList<ClipDrawable> doInBackground(T... args) {
        ArrayList<ClipDrawable> array = new ArrayList<>();
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
        try {
            ImageView leftImageView = imageRefLeft.get();
            ImageView rightImageView = imageRefRight.get();
            if (leftImageView == null || rightImageView == null) return array;

            // Load Left Bitmap
            FutureTarget<Bitmap> futureLeft = Glide.with(leftImageView.getContext())
                    .asBitmap()
                    .load(args[0])
                    .centerCrop()
                    .submit(getWidth(), getHeight());
            Bitmap rawLeftBitmap = futureLeft.get();
            Bitmap scaledLeftBitmap = getScaledBitmap(rawLeftBitmap);
            if (scaledLeftBitmap != null) {
                rawLeftBitmap = scaledLeftBitmap;
            }
            BitmapDrawable leftDrawable = new BitmapDrawable(leftImageView.getContext().getResources(), rawLeftBitmap);

            // Load Right Bitmap
            FutureTarget<Bitmap> futureRight = Glide.with(rightImageView.getContext())
                    .asBitmap()
                    .load(args[1])
                    .centerCrop()
                    .submit(getWidth(), getHeight());
            Bitmap rightBitmap = futureRight.get();
            BitmapDrawable rightDrawable = new BitmapDrawable(rightImageView.getContext().getResources(), rightBitmap);

            array.add(new ClipDrawable(leftDrawable, Gravity.START, ClipDrawable.HORIZONTAL));
            array.add(new ClipDrawable(rightDrawable, Gravity.START, ClipDrawable.HORIZONTAL));
        } catch (Exception e) {
            e.printStackTrace();
            array.clear();
        }
        return array;
    }

    @Override
    protected void onPostExecute(ArrayList<ClipDrawable> array) {
        if (array.size() == 2 && imageRefLeft.get() != null && imageRefRight.get() != null) {
            ImageView leftImageView = imageRefLeft.get();
            ImageView rightImageView = imageRefRight.get();
            if (rightImageView != null && array.get(1) != null) {
                rightImageView.setImageDrawable(array.get(1));
                array.get(1).setLevel(10000);
            }
            if (leftImageView != null && array.get(0) != null) {
                initSeekBar(array.get(0));
                leftImageView.setImageDrawable(array.get(0));
                array.get(0).setLevel(progress);
            }
            if (loadedFinishedListener != null) {
                loadedFinishedListener.loadingStatus(true);
            }
        } else {
            if (loadedFinishedListener != null) {
                loadedFinishedListener.loadingStatus(false);
            }
        }
    }

    private int getWidth() {
        ImageView imageView = imageRefLeft.get();
        if (imageView == null) return 0;
        int width;
        do {
            width = imageView.getWidth();
        } while (width == 0);
        return width;
    }

    private int getHeight() {
        ImageView imageView = imageRefLeft.get();
        if (imageView == null) return 0;
        int height;
        do {
            height = imageView.getHeight();
        } while (height == 0);
        return height;
    }

    private Bitmap getScaledBitmap(Bitmap bitmap) {
        try {
            ImageView imageView = imageRefLeft.get();
            if (imageView == null) return bitmap;
            int imageWidth = imageView.getWidth();
            int imageHeight = imageView.getHeight();
            if (imageWidth > 0 && imageHeight > 0) {
                return Bitmap.createScaledBitmap(bitmap, imageWidth, imageHeight, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initSeekBar(final ClipDrawable clipDrawable) {
        SeekBar seekBar = seekBarRef.get();
        if (seekBar != null) {
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    clipDrawable.setLevel(i);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }
    }

}
