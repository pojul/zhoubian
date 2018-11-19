package com.yjyc.zhoubian.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.utils.ArrayUtil;
import com.yjyc.zhoubian.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;


public class FlowTagView extends View {

    //常亮默认值，这些参数若不调用方法传递，则直接使用默认值
    public static final int ROUND_RADIUS = 30;
    public static final int TEXT_COLOR_DEFAULT = Color.GRAY;
    public static final int TEXT_COLOR_SELECTED = Color.WHITE;
    public static final int TEXT_SIZE = 30;
    public static final int BACKGROUND_COLOR_DEFAULT = Color.GRAY;
    public static final int BACKGROUND_COLOR_SELECTED = Color.parseColor("#ed8718");
    public static final int HORIZONTAL_PADDING = 30;
    public static final int VERTICAL_PADDING = 30;
    public static final int TEXT_HORIZONTAL_PADDING = 30;
    public static final int ITEM_HEIGHT = 60;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int textColorDefault = TEXT_COLOR_DEFAULT;
    private int textColorSelected = TEXT_COLOR_SELECTED;
    private int textSize = TEXT_SIZE;
    private int backgroundColorDefault = BACKGROUND_COLOR_DEFAULT;
    private int backgroundColorSelected = BACKGROUND_COLOR_SELECTED;
    //Tag之间的横向和纵向的间隔
    private int horizontalPadding = HORIZONTAL_PADDING;
    private int verticalPadding = VERTICAL_PADDING;
    //每个Tag内部的横向间隔
    private int textHorizontalPadding = TEXT_HORIZONTAL_PADDING;
    //每个Tag的高度
    private int itemHeight = ITEM_HEIGHT;

    private int radio = ROUND_RADIUS;

    //tag的绘制起点，动态计算得值
    private int startX;
    private int startY;
    //Tag显示的文本
    private List<String> datas = new ArrayList<>();
    private List<Tag> tags = new ArrayList<>();

    //点击事件的滑动距离阈值
    private int mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    //ACTION_DOWN时的坐标值
    private float mTouchX;
    private float mTouchY;
    //ACTION_DOWN时选中的tag的索引
    private int mTouchPosition;

    private OnTagSelectedListener listener;
    private int selectMode = 1; //1: 单选中; 2: 多选中

    public FlowTagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public FlowTagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public FlowTagView(Context context) {
        super(context);
        init(null);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FlowTagView);
            textSize = a.getDimensionPixelSize(R.styleable.FlowTagView_text_size, TEXT_SIZE);
            textHorizontalPadding = a.getDimensionPixelSize(R.styleable.FlowTagView_tag_horiz_padding, TEXT_HORIZONTAL_PADDING);
            itemHeight = a.getDimensionPixelSize(R.styleable.FlowTagView_tag_height, ITEM_HEIGHT);
            radio = a.getDimensionPixelSize(R.styleable.FlowTagView_tag_radio, ROUND_RADIUS);
            horizontalPadding = DensityUtil.dp2px(getContext(), 10);
            verticalPadding = DensityUtil.dp2px(getContext(), 8);
            a.recycle();
        }
    }

    public FlowTagView textColor(int defaultColor, int selectedColor) {
        this.textColorDefault = defaultColor;
        this.textColorSelected = selectedColor;
        return this;
    }

    public FlowTagView textSize(int textSize) {
        this.textSize = textSize;
        return this;
    }

    public FlowTagView backgroundColor(int defaultColor, int selectedColor) {
        this.backgroundColorDefault = defaultColor;
        this.backgroundColorSelected = selectedColor;
        return this;
    }

    public FlowTagView padding(int horizontalPadding, int verticalPadding, int textHorizontalPadding) {
        this.horizontalPadding = horizontalPadding;
        this.verticalPadding = verticalPadding;
        this.textHorizontalPadding = textHorizontalPadding;
        return this;
    }

    public FlowTagView itemHeight(int height) {
        this.itemHeight = height;
        return this;
    }

    public FlowTagView datas(List<String> datas) {
        this.datas = datas;
        return this;
    }

    public FlowTagView listener(OnTagSelectedListener listener) {
        this.listener = listener;
        return this;
    }

    public void commit() {
        if (datas == null) {
            Log.e("FlowTagView", "maybe not invok the method named datas(String[])");
            throw new IllegalStateException("maybe not invok the method named datas(String[])");
        }
        paint.setTextSize(textSize);
        if (datas.size() != tags.size()) {
            //重新实例化
            ViewGroup.LayoutParams params = getLayoutParams();
            setLayoutParams(params);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        //算出绘制起点
        startX = getPaddingLeft();
        startY = getPaddingTop();
        //tags.clear();
        ArrayList<Tag> tempTags = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            //判断是否越过边界
            if (startX + getRealWidth(paint, textSize, datas.get(i), textHorizontalPadding) + horizontalPadding > width - getPaddingRight()) {
                //在下一行开始绘制
                startX = getPaddingLeft();
                startY += itemHeight + verticalPadding;
            }
            Tag tag = new Tag(datas.get(i), textSize, textColorDefault, textColorSelected,
                    backgroundColorDefault, backgroundColorSelected, paint, itemHeight, textHorizontalPadding, startX, startY);
            if(i <= (tags.size() - 1) && tags.get(i) != null){
                tag.isSelected = tags.get(i).isSelected;
            }
            tempTags.add(tag);
            //动态更新值
            startX += getRealWidth(paint, textSize, datas.get(i), textHorizontalPadding) + horizontalPadding;
        }
        tags = tempTags;
        //算出整个控件需要的高度
        int height = startY + itemHeight + getPaddingBottom();
        setMeasuredDimension(width, height);
    }

    /**
     * 根据参数算出某个Tag所需要占用的宽度值，包括内补白
     */
    public static int getRealWidth(Paint paint, int textSize, String text, int textHorizontalPadding) {
        paint.setTextSize(textSize);
        int textWidth = (int) paint.measureText(text);
        return textWidth + 2 * textHorizontalPadding;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制代理
        for (int i = 0; i < tags.size(); i++) {
            tags.get(i).draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isEnabled()){
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchX = event.getX();
                mTouchY = event.getY();
                mTouchPosition = getTagPosition(mTouchX, mTouchY);
                return true;

            case MotionEvent.ACTION_UP:
                float mUpX = event.getX();
                float mUpY = event.getY();
                //滑动距离小于点击阈值并且点击时的索引值不是非法值，并且up时的索引值和down时的索引值相等时，才触发选中操作
                if (Math.abs(mUpX - mTouchX) < mTouchSlop && Math.abs(mUpY - mTouchY) < mTouchSlop
                        && mTouchPosition != -1 && getTagPosition(mUpX, mUpY) == mTouchPosition) {
                    //触发点击选中
                    setSelect(mTouchPosition);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 根本坐标值，返回对应的tag的索引，若不存在则返回-1
     */
    private int getTagPosition(float x, float y) {
        for (int i = 0; i < tags.size(); i++) {
            if (tags.get(i).rect.contains(x, y)) {
                return i;
            }
        }
        return -1;
    }

    public void setSelect(int position) {
        if (position < 0 || position >= tags.size()) {
            Log.e("FlowTagView", "the position is illetal");
            throw new IllegalArgumentException("the position is illetal");
        }
        if(selectMode == 1){
            Tag tag = tags.get(position);
            if (!tag.isSelected) {
                for (int i = 0; i < tags.size(); i++) {
                    //关闭其他选择
                    if (i != position) {
                        tags.get(i).isSelected = false;
                    } else {
                        tags.get(i).isSelected = true;
                    }
                }
            } else {
                tag.isSelected = false;
            }
        } else {
            tags.get(position).isSelected = !tags.get(position).isSelected;
        }

        //触发监听器
        if (listener != null) {
            listener.onTagSelected(this, position);
        }
        //必须要刷新UI
        invalidate();
    }

    public String getTags(){
        StringBuffer tagStrs = new StringBuffer();
        for (int i = 0; i < tags.size(); i++) {
            if (tags.get(i).isSelected) {
                if(tagStrs.length() > 0){
                    tagStrs.append(",");
                }
                tagStrs.append(datas.get(i));
            }
        }
        return tagStrs.toString();
    }

    public List<String> getSelectTags(){
        List<String> selectTags = new ArrayList<>();
        if(tags == null){
            return selectTags;
        }
        for (int i = 0; i < tags.size(); i++) {
            if (tags.get(i).isSelected) {
                selectTags.add(datas.get(i));
            }
        }
        return selectTags;
    }

    public void setSelectedTags(List<Integer> selectedTags){
        if(tags == null){
            return;
        }
        for(int i = 0; i < selectedTags.size(); i++){
            if(i < tags.size()){
                int position = selectedTags.get(i);
                tags.get(position).isSelected = true;
            }
        }
        invalidate();
    }

    public void setSelectedTagStrs(List<String> selectedTags){
        if(tags == null){
            return;
        }
        for(int i = 0; i < tags.size(); i++){
            Tag tag = tags.get(i);
            if(ArrayUtil.containsStringVal(selectedTags, tag.text)){
                tag.isSelected = true;
            }else{
                tag.isSelected = false;
            }
        }
        invalidate();
    }

    public class Tag {
        //文本属性
        public String text;
        public int textColorDefault;
        public int textColorSelected;
        public int backgroundColorDefault;
        public int backgroundColorSelected;
        public boolean isSelected;
        public Paint paint;
        //文本的绘制起点
        public int drawX;
        public int drawY;
        //整个Tag占用的坐标范围
        public RectF rect = new RectF();

        public Tag(String text, int textSize, int textColorDefault, int textColorSelected, int backgroundColorDefault, int backgroundColorSelected,
                   Paint paint, int height, int horizontalPadding, int startX, int startY) {
            this.text = text;
            this.textColorDefault = textColorDefault;
            this.textColorSelected = textColorSelected;
            this.backgroundColorDefault = backgroundColorDefault;
            this.backgroundColorSelected = backgroundColorSelected;
            this.paint = paint;
            //求出整个Tag的宽度
            paint.setTextSize(textSize);
            int textWidth = (int) paint.measureText(text);
            int width = textWidth + 2 * horizontalPadding;
            //计算坐标范围，startX，staryY是指左上角的起点
            rect.left = startX;
            rect.top = startY;
            rect.right = startX + width;
            rect.bottom = startY + height;
            //计算居中绘制时的绘制起点
            drawX = startX + horizontalPadding;
            Paint.FontMetrics metrics = paint.getFontMetrics();
            drawY = (int) (startY + height / 2 + (metrics.bottom - metrics.top) / 2 - metrics.bottom);
        }

        public void draw(Canvas canvas) {
            if (isSelected) {
                //绘制背景
                paint.setColor(backgroundColorSelected);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawRoundRect(rect, radio, radio, paint);
                //绘制文本
                paint.setColor(textColorSelected);
                canvas.drawText(text, drawX, drawY, paint);
            } else {
                //绘制背景
                paint.setColor(backgroundColorDefault);
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawRoundRect(rect, radio, radio, paint);
                //绘制文本
                paint.setColor(textColorDefault);
                canvas.drawText(text, drawX, drawY, paint);
            }
        }

    }

    public interface OnTagSelectedListener {
        void onTagSelected(FlowTagView view, int position);
    }

}
