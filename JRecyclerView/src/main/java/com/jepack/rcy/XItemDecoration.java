package com.jepack.rcy;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

/**
 * 控制item间隔
 * Created by zhanghaihai on 2017/6/28.
 */

public class XItemDecoration extends RecyclerView.ItemDecoration {
    private final int startPos;
    private int orientation;//方向
    private int verticalDecoration;
    private int horizonDecoration;
    private Paint paint = new Paint();
    private int lineWidth = -100;
    private int color = -100;
    private int backgroundColor = -100;
    private SparseArray<SpecialItem> specialItemSparseArray = new SparseArray<>();
    private SparseArray<SpecialItem> typeSpecialItemSparseArray = new SparseArray<>();
    private boolean ifAddLeftStartDecoration = false;
    private boolean ifAddTopStartDecoration = false;
    private SpecialItem endSpecialItem;

    public XItemDecoration(@LinearLayoutCompat.OrientationMode int orientation, int lineWidth, int verticalDecoration, int horizonDecoration, int color, int backgroundColor) {
        this(orientation, lineWidth, verticalDecoration, horizonDecoration, color, backgroundColor, 0);

    }

    public XItemDecoration(@LinearLayoutCompat.OrientationMode int orientation, int lineWidth, int verticalDecoration, int horizonDecoration, int color, int backgroundColor, int startPos) {
        this.orientation = orientation;
        this.verticalDecoration = verticalDecoration;
        this.horizonDecoration = horizonDecoration;
        this.lineWidth = lineWidth;
        this.color = color;
        this.backgroundColor = backgroundColor;
        paint.setColor(Color.parseColor("#eeeeee"));
        paint.setStyle(Paint.Style.FILL);
        this.startPos = startPos;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getLayoutManager().getPosition(view);
        if(position >= 0 && position < parent.getAdapter().getItemCount()) {
            int type = parent.getAdapter().getItemViewType(position);
            SpecialItem sItem = specialItemSparseArray.get(position);
            int left, top, right, bottom;
            if (sItem == null) {
                sItem = typeSpecialItemSparseArray.get(type);
                if (sItem == null) {
                    //垂直走向时第一个要绘制左边距，其他不绘制
                    left = getLeftDecoration(horizonDecoration, view, parent, position);
                    right = getRightDecoration(horizonDecoration, view, parent, position);
                    //水平走向时第一个要绘制左右距，其他不绘制
                    top = getTopDecoration(verticalDecoration, view, parent, position);
                    bottom = getBottomDecoration(verticalDecoration, view, parent, position);
                }else{
                    left = sItem.left;
                    top = sItem.top;
                    right = sItem.right;
                    bottom = sItem.bottom;
                }
            }else{
                left = sItem.left;
                top = sItem.top;
                right = sItem.right;
                bottom = sItem.bottom;
            }
            outRect.set(left, top, right, bottom);
        }
    }

    /**
     * 获取底部边距
     * @param verticalDecoration 不为0则所有Item设置底部边距
     * @param view
     * @param parent
     * @param position
     * @return
     */
    private int getBottomDecoration(int verticalDecoration, View view, RecyclerView parent, int position) {
        return verticalDecoration > 0? verticalDecoration: 0;
    }

    /**
     * 获取顶部边距
     * @param verticalDecoration
     * @param view
     * @param parent
     * @param position
     * @return
     */
    private int getTopDecoration(int verticalDecoration, View view, RecyclerView parent, int position) {
        if(orientation == LinearLayoutCompat.HORIZONTAL){
            if (isLeftOrTopChild(parent, position) && verticalDecoration > 0 && ifAddTopStartDecoration) {
                return verticalDecoration;
            } else {
                return 0;
            }
        }else{
            if(ifAddTopStartDecoration && isFirstLineChild(parent, position)) {
                return verticalDecoration;
            }else{
                return 0;
            }
        }
    }

    /**
     * 获取右侧边距
     * @param horizonDecoration 不为0则所有Item设置右侧边距
     * @param view
     * @param parent
     * @param position
     * @return
     */
    private int getRightDecoration(int horizonDecoration, View view, RecyclerView parent, int position){
        return horizonDecoration > 0? horizonDecoration: 0;
    }

    /**
     * 获取左侧边距
     * @param horizonDecoration
     * @param view
     * @param parent
     * @param position
     * @return
     */
    private int getLeftDecoration(int horizonDecoration, View view, RecyclerView parent, int position){
        if(orientation == LinearLayoutCompat.VERTICAL) {
            if (isLeftOrTopChild(parent, position) && horizonDecoration > 0 && ifAddLeftStartDecoration) {
                return horizonDecoration;
            } else {
                return 0;
            }
        }else{
            if(ifAddLeftStartDecoration && isFirstLineChild(parent, position)){
                return horizonDecoration;
            }
            return 0;
        }
    }

    /**
     * 判断是否是最左侧或者最右侧Item
     * @param parent
     * @param position
     * @return
     */
    private boolean isLeftOrTopChild(RecyclerView parent, int position){
        if(parent.getLayoutManager() instanceof GridLayoutManager){
            GridLayoutManager layoutManager = ((GridLayoutManager) parent.getLayoutManager());
            layoutManager.setMeasurementCacheEnabled(true);
            int index = layoutManager.getSpanSizeLookup().getSpanIndex(position, layoutManager.getSpanCount());
            return index == 0;
        }else{
            return true;
        }
    }

    /**
     * 判断是否是第一行child
     * @param parent
     * @param position
     * @return
     */
    private boolean isFirstLineChild(RecyclerView parent, int position){
        if(parent.getLayoutManager() instanceof GridLayoutManager){
            GridLayoutManager layoutManager = ((GridLayoutManager) parent.getLayoutManager());

            return layoutManager.getSpanSizeLookup().getSpanGroupIndex(position, layoutManager.getSpanCount()) == 0;
        }else{
            return 0 == position;
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        for(int i = 0; i < parent.getChildCount(); i ++){
            View view = parent.getChildAt(i);
            if(view != null) {
                int position = parent.getLayoutManager().getPosition(view);
                if (position >= startPos){
                    draw(c, view, parent, position);
                }
            }
        }
    }

    /**
     * 绘制上下左右边距背景
     * @param canvas
     * @param view
     * @param parent
     * @param position
     */
    private void draw(Canvas canvas, View view, RecyclerView parent, int position){
        int horizonDecoration = this.horizonDecoration;
        int verticalDecoration = this.verticalDecoration;
        paint.setColor(backgroundColor == -100? Color.TRANSPARENT: backgroundColor);
        //特殊的Item
        SpecialItem item;
        if(position == parent.getAdapter().getItemCount() - 1 && endSpecialItem != null){
            item = endSpecialItem;
        }else {
            //特殊的Item
            item = specialItemSparseArray.get(position);
        }

        int left, top, right, bottom;
        int backgroundLeft, backgroundTop, backgroundRight, backgroundBottom;
        if(item == null){
            item = typeSpecialItemSparseArray.get(parent.getAdapter().getItemViewType(position));
            if(item == null){
                left = getLeftDecoration(horizonDecoration, view, parent, position);
                right = getRightDecoration(horizonDecoration, view, parent, position);
                top = getTopDecoration(verticalDecoration, view, parent, position);
                bottom = getBottomDecoration(verticalDecoration, view, parent, position);
                backgroundLeft = backgroundColor;
                backgroundTop = backgroundColor;
                backgroundRight = backgroundColor;
                backgroundBottom = backgroundColor;
            }else{
                left = item.left;
                top = item.top;
                right = item.right;
                bottom = item.bottom;
                backgroundLeft = item.backgroundLeft;
                backgroundTop = item.backgroundTop;
                backgroundRight = item.backgroundRight;
                backgroundBottom = item.backgroundBottom;
            }
        }else{
            left = item.left;
            top = item.top;
            right = item.right;
            bottom = item.bottom;
            backgroundLeft = item.backgroundLeft;
            backgroundTop = item.backgroundTop;
            backgroundRight = item.backgroundRight;
            backgroundBottom = item.backgroundTop;
        }


        //绘制
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.FILL);

        paint.setColor(backgroundLeft);
        if(left > 0){
            canvas.drawRect(view.getLeft() - left, view.getTop() , view.getLeft(), view.getBottom() + bottom, paint);
        }

        paint.setColor(backgroundTop);
        if(top > 0){
            canvas.drawRect(view.getLeft() - left, view.getTop() - top , view.getRight() + right, view.getTop(), paint);
        }

        paint.setColor(backgroundRight);
        if(right > 0){
            canvas.drawRect(view.getRight(), view.getTop(), view.getRight() + right, view.getBottom() + bottom, paint);
        }

        paint.setColor(backgroundBottom);
        if(bottom > 0){
            canvas.drawRect(view.getLeft() - left , view.getBottom(), view.getRight() + right, view.getBottom() + bottom, paint);
        }

        //设置了分割线高度时，只有最后的Item底部不绘制分割线
        if(lineWidth > 0 && position < parent.getLayoutManager().getItemCount() - 1) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(color == -100? Color.TRANSPARENT: color);
            paint.setStrokeWidth(lineWidth);
            if (orientation == LinearLayoutCompat.HORIZONTAL) {
                float x = view.getRight() + horizonDecoration / 2f - lineWidth / 2f;
                canvas.drawLine(x, view.getTop(), x, view.getBottom(), paint);
            } else {
                float y = view.getBottom() + verticalDecoration / 2f - lineWidth / 2f;
                canvas.drawLine(view.getLeft(), y, view.getRight(), y, paint);
            }
        }
    }

    public static class SpecialItem{
        private int left;
        private int top;
        private int right;
        private int bottom;
        private int backgroundLeft;
        private int backgroundTop;
        private int backgroundRight;
        private int backgroundBottom;

        public SpecialItem(int left, int top, int right, int bottom, int background) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            this.backgroundLeft = background;
            this.backgroundTop = background;
            this.backgroundRight = background;
            this.backgroundBottom = background;
        }

        public SpecialItem(int left, int top, int right, int bottom, int backgroundLeft, int backgroundTop, int backgroundRight, int backgroundBottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            this.backgroundLeft = backgroundLeft;
            this.backgroundTop = backgroundTop;
            this.backgroundRight = backgroundRight;
            this.backgroundBottom = backgroundBottom;
        }
    }

    public XItemDecoration addSpecialItem(SpecialItem item, int position){
        specialItemSparseArray.put(position, item);
        return this;
    }

    public XItemDecoration addTypeSpecialItem(SpecialItem item, int type){
        typeSpecialItemSparseArray.put(type, item);
        return this;
    }

    public XItemDecoration setIfAddLeftStartDecoration(boolean ifAddLeftStartDecoration) {
        this.ifAddLeftStartDecoration = ifAddLeftStartDecoration;
        return this;
    }

    public XItemDecoration setIfAddTopStartDecoration(boolean ifAddTopStartDecoration) {
        this.ifAddTopStartDecoration = ifAddTopStartDecoration;
        return this;
    }

    public void setEndSpecialItem(SpecialItem endSpecialItem) {
        this.endSpecialItem = endSpecialItem;
    }
}
