package com.d42gmail.cavar.beforeandafter.custom_view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.d42gmail.cavar.beforeandafter.R;
import com.d42gmail.cavar.beforeandafter.utils.UiUtils;

public class BeforeAndAfterView extends RelativeLayout implements LoadingListener {

    private Integer beforeSrc = Constants.INIT_INT;
    private Integer afterSrc = Constants.INIT_INT;
    private Integer progress = Constants.DEFAULT_PROGRESS;
    private Integer progressDrawable = R.drawable.seek_bar_thumb;
    private Integer cornerMaskDrawable = R.drawable.round_edge_mask;
    private Float progressPaddingStart;
    private Float progressPaddingEnd;
    private Integer placeHolder = Constants.DEFAULT_INT;
    private String beforeUrl;
    private String afterUrl;
    private Boolean roundCorners;

    private SeekBar ptSeekBar;
    private ImageView ivPlaceHolder;
    private ImageView ptBackgroundImageLeft;
    private ImageView ptBackgroundImageRight;
    private View vMask;

    public BeforeAndAfterView(Context context) {
        this(context, null);
    }

    public BeforeAndAfterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BeforeAndAfterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.before_and_after_layout, this);

        ptSeekBar = findViewById(R.id.ptSeekBar);
        ivPlaceHolder = findViewById(R.id.ivPlaceHolder);
        ptBackgroundImageLeft = findViewById(R.id.ptBackgroundImageLeft);
        ptBackgroundImageRight = findViewById(R.id.ptBackgroundImageRight);
        vMask = findViewById(R.id.vMask);

        getAttrsValues(context, attrs);
    }

    private void getAttrsValues(Context context, AttributeSet attrs) {
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BeforeAndAfterView, 0, 0);

        try {
            beforeSrc = attributes.getResourceId(R.styleable.BeforeAndAfterView_rightImageSrc, Constants.INIT_INT);
            afterSrc = attributes.getResourceId(R.styleable.BeforeAndAfterView_leftImageSrc, Constants.INIT_INT);
            beforeUrl = attributes.getString(R.styleable.BeforeAndAfterView_rightImageUrl);
            afterUrl = attributes.getString(R.styleable.BeforeAndAfterView_leftImageUrl);
            roundCorners = attributes.getBoolean(R.styleable.BeforeAndAfterView_roundCorners, false);
            cornerMaskDrawable = attributes.getResourceId(R.styleable.BeforeAndAfterView_cornerMask, R.drawable.round_edge_mask);
            progress = attributes.getInt(R.styleable.BeforeAndAfterView_progress, 50);
            progressDrawable = attributes.getResourceId(R.styleable.BeforeAndAfterView_progressDrawable, R.drawable.seek_bar_thumb);
            progressPaddingStart = attributes.getDimension(R.styleable.BeforeAndAfterView_progressPaddingStart, UiUtils.convertDpToPix(Constants.DEFAULT_PROGRESS_PADDING, context));
            progressPaddingEnd = attributes.getDimension(R.styleable.BeforeAndAfterView_progressPaddingEnd, UiUtils.convertDpToPix(Constants.DEFAULT_PROGRESS_PADDING, context));
            placeHolder = attributes.getResourceId(R.styleable.BeforeAndAfterView_placeHolderSrc, Constants.DEFAULT_INT);
        } finally {
            attributes.recycle();
        }

        applyStyle();
        loadingArbitrar();
    }

    private void applyStyle() {
        setRoundCorners(roundCorners);
        setProgress(progress);
        setProgressThumb(ContextCompat.getDrawable(getContext(), progressDrawable));
        setProgressPadding(progressPaddingStart.intValue(), 0, progressPaddingEnd.intValue(), 0);
        setMask(ContextCompat.getDrawable(getContext(), cornerMaskDrawable));
        if (placeHolder != 0) {
            setPlaceHolder(ContextCompat.getDrawable(getContext(), placeHolder));
        }
    }

    private void loadingArbitrar() {
        new Handler().post(() -> {
            if (beforeSrc != 0 && afterSrc != 0) {
                loadImagesBySrc(afterSrc, beforeSrc);
            } else if (beforeUrl != null && afterUrl != null) {
                loadImagesByUrl(afterUrl, beforeUrl);
            }
        });
    }

    public void setProgress(int progress) {
        ptSeekBar.setProgress(validateProgress(progress));
    }

    public void setProgressThumb(Drawable drawable) {
        ptSeekBar.setThumb(drawable);
    }

    public void setRoundCorners(@NonNull Boolean roundCorners) {
        if (roundCorners) {
            vMask.setVisibility(VISIBLE);
        }
    }

    public void setProgressPadding(int start, int top, int end, int bottom) {
        ptSeekBar.setPadding(start, top, end, bottom);
    }

    public void setMask(Drawable drawable) {
        vMask.setBackground(drawable);
    }

    public void setPlaceHolder(Drawable drawable) {
        ivPlaceHolder.setImageDrawable(drawable);
    }

    public void loadImagesByUrl(String imageLeftUrl, String imageRightUrl) {
        new ClipDrawableAsync<>(ptBackgroundImageLeft, ptBackgroundImageRight, ptSeekBar, validateProgress(progress), this).execute(imageLeftUrl, imageRightUrl);
    }

    public void loadImagesBySrc(int imageLeftSrc, int imageRightSrc) {
        new ClipDrawableAsync<>(ptBackgroundImageLeft, ptBackgroundImageRight, ptSeekBar, validateProgress(progress), this).execute(imageLeftSrc, imageRightSrc);
    }

    @Override
    public void loadingStatus(boolean loadedSuccess) {
        new Handler().post(() -> {
            if (loadedSuccess) {
                recalculateNewImageHeight(ptBackgroundImageRight);
                recalculateNewImageHeight(ptBackgroundImageLeft);
            }
        });
    }

    private void recalculateNewImageHeight(View view) {
        if (view != null) {
            view.getLayoutParams().height = calculateNewImageHeight(view.getLayoutParams().width);
            ptSeekBar.setVisibility(View.VISIBLE);
            ivPlaceHolder.setVisibility(View.GONE);
        }
    }

    private int calculateNewImageHeight(int width) {
        return (int) (width / 0.6875);
    }

    private int validateProgress(int progress) {
        if (progress < 0) return 0;
        if (progress > 100) return 10000;
        return progress * 100;
    }
}

