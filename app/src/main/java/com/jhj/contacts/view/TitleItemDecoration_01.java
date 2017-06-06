package com.jhj.contacts.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import com.jhj.contacts.activity.ThirdListActivity;
import com.jhj.contacts.bean.UserBean;

import java.util.List;


/**
 * 有分类title的ItemDecoration
 */
public class TitleItemDecoration_01 extends RecyclerView.ItemDecoration {
    /**
     * 当没有首字母时，分隔线的颜色
     */
    private static int TYPE_COLOR_LINE = Color.parseColor("#DFDFDF");
    /**
     * 当有首字母时，分割线的背景色
     */
    private static int TYPE_COLOR_BACKGROUND = Color.parseColor("#f1f1f1");

    /**
     * 当有首字母时，首字母文字的颜色（设置画笔）
     */
    private static int TYPE_COLOR_TEXT = Color.parseColor("#888888");
    /**
     * 当有首字母时，首字母距左边的距离(dp)
     */
    private static int DISTANCE_LEFT = 8;
    /**
     * 当有首字母时，分隔线的高度
     */
    private int mTitleHeight;//title的高
    /**
     * 数据源(有bean类的不同将有所调整)
     */
    private List<UserBean> mDatas;
    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 用于存放测量文字Rect
     */
    private Rect mBounds;
    /**
     * context
     */
    private Context context;


    public TitleItemDecoration_01(Context context, List<UserBean> datas) {
        super();
        this.context = context;
        this.mDatas = datas;
        mPaint = new Paint();
        mBounds = new Rect();
        mTitleHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, context.getResources().getDisplayMetrics());
        int mTitleFontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, context.getResources().getDisplayMetrics());
        mPaint.setTextSize(mTitleFontSize);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            if (child != null) {
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                int position = params.getViewLayoutPosition();
                if (position > -1) {
                    if (position == 0) {//等于0肯定要有title的
                        drawTitleArea(c, left, right, child, params, position);
                    } else {//其他的通过判断
                        if (null != getAlpha(position) && !getAlpha(position).equals(getAlpha(position - 1))) {
                            //不为空 且跟前一个tag不一样了，说明是新的分类，也要title
                            drawTitleArea(c, left, right, child, params, position);
                        } else {
                            drawLine(c, left, right, child, params, position);
                        }
                    }
                }
            }
        }

    }

    /**
     * 画直线
     */
    private void drawLine(Canvas c, int left, int right, View child, RecyclerView.LayoutParams params, int position) {
        mPaint.setColor(TYPE_COLOR_LINE);
        c.drawLine(left, child.getTop() - params.topMargin, right, child.getTop() - params.topMargin, mPaint);
    }

    /**
     * 绘制Title区域背景和文字的方法
     */
    private void drawTitleArea(Canvas c, int left, int right, View child, RecyclerView.LayoutParams params, int position) {//最先调用，绘制在最下层
        mPaint.setColor(TYPE_COLOR_BACKGROUND);
        c.drawRect(left, child.getTop() - params.topMargin - mTitleHeight, right, child.getTop() - params.topMargin, mPaint);
        mPaint.setColor(TYPE_COLOR_TEXT);
        mPaint.getTextBounds(getAlpha(position), 0, getAlpha(position).length(), mBounds);
        float scale = context.getResources().getDisplayMetrics().density;
        int x = (int) (DISTANCE_LEFT * scale + 0.5f);
        int y = child.getTop() - params.topMargin - (mTitleHeight / 2 - mBounds.height() / 2);
        c.drawText(getAlpha(position), x, y, mPaint);
    }

    /**
     * 获取position位置的首字母
     *
     * @param position 位置
     * @return 首字母
     */
    private String getAlpha(int position) {
        if (mDatas != null && mDatas.size() > 0 && mDatas.get(position) != null) {
            return mDatas.get(position).alpha;
        } else {
            return "";
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {//最后调用 绘制在最上层
        super.onDrawOver(c, parent, state);
        int pos = ((LinearLayoutManager) (parent.getLayoutManager())).findFirstVisibleItemPosition();
        if (pos != -1) {
            mPaint.setColor(TYPE_COLOR_BACKGROUND);
            c.drawRect(parent.getPaddingLeft(), parent.getPaddingTop(), parent.getRight() - parent.getPaddingRight(), parent.getPaddingTop() + mTitleHeight, mPaint);
            mPaint.setColor(TYPE_COLOR_TEXT);
            mPaint.getTextBounds(getAlpha(pos), 0, getAlpha(pos).length(), mBounds);
            float scale = context.getResources().getDisplayMetrics().density;
            int x = (int) (DISTANCE_LEFT * scale + 0.5f);
            int y = parent.getPaddingTop() + mTitleHeight - (mTitleHeight / 2 - mBounds.height() / 2);
            c.drawText(getAlpha(pos), x, y, mPaint);
        }

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        if (position > -1) {
            if (position == 0) {//等于0肯定要有title的
                outRect.set(0, mTitleHeight, 0, 0);
            } else {//其他的通过判断
                if (null != getAlpha(position) && !getAlpha(position).equals(getAlpha(position - 1))) {
                    outRect.set(0, mTitleHeight, 0, 0);//不为空 且跟前一个tag不一样了，说明是新的分类，也要title
                } else {
                    outRect.set(0, 0, 0, 0);
                }
            }
        }
    }
}
