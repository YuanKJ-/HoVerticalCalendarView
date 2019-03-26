package com.wehotel.calendar.util;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.wehotel.calendar.adapter.BaseTitleAdapter;
import com.wehotel.calendar.adapter.BaseValueAdapter;
import com.wehotel.calendar.bean.BaseTitleBean;
import com.wehotel.calendar.bean.BaseValueBean;
import com.wehotel.calendar.view.SuctionTopDecoration;


/**
 * Created by kejie.yuan
 * Date: 2019/3/20
 * Description: 联动列表工具类
 */
public class LinkageScrollUtil {

    public static void bindLinkageScroll(RecyclerView titleRv, final RecyclerView valueRv) {
        // 初始化联合滚动效果
        final SmoothPos smoothPos = new SmoothPos();

        final BaseTitleAdapter titleAdapter = (BaseTitleAdapter) titleRv.getAdapter();
        final BaseValueAdapter valueAdapter = (BaseValueAdapter) valueRv.getAdapter();

        // 向上滚动时先显示targetItem相隔一屏的位置，再缓动上去,向下同理
        titleAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                BaseTitleBean item = (BaseTitleBean) titleAdapter.getItem(position);
                if (item != null) {
                    // 点击时立刻取消上一次滑动并重置状态
                    stopScrollIfNeed(valueRv, smoothPos);
                    smoothPos.mScrolling = true;
                    smoothMoveToPosition(valueRv, item.bindValuePosition, smoothPos);
                    // 直接指定title列表选中item
                    titleAdapter.setSelectedYear(item.year);
                }
            }
        });
        // valueRv滚动状态时需要实时获取第一个显示的item相应更新titleRv的选中
        valueRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (RecyclerView.SCROLL_STATE_DRAGGING == newState
                        && smoothPos.mScrolling) {
                    // 缓动状态中突然触摸拖拽需要更新titleRv选中item
                    updateSelectPos(valueRv, valueAdapter, titleAdapter, false);
                }
                if (RecyclerView.SCROLL_STATE_IDLE == newState ||
                        RecyclerView.SCROLL_STATE_DRAGGING == newState) {
                    // 缓动过程中手指突然触摸拖拽或rv进入idle状态,需要立刻取消滑动并重置状态
                    stopScrollIfNeed(valueRv, smoothPos);
                    smoothPos.mScrolling = false;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                updateSelectPos(valueRv, valueAdapter, titleAdapter, smoothPos.mScrolling);
            }
        });
        // scrollToPositionWithOffset时会进入globalLayout回调,判断状态进入二次缓动
        valueRv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (smoothPos.mShouldScroll) {
                    smoothPos.mShouldScroll = false;
                    smoothMoveToPosition(valueRv, smoothPos.mToPosition, smoothPos);
                }
            }
        });
        //无侵入式粘性头部
        SuctionTopDecoration suctionTopDecoration = new SuctionTopDecoration(valueRv.getContext());
        valueRv.addItemDecoration(suctionTopDecoration);
    }

    /**
     * 滑动到指定位置
     */
    private static void smoothMoveToPosition(RecyclerView mRecyclerView, final int position, final SmoothPos smoothPos) {
        // 第一个可见位置
        int firstItem = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        // 最后一个可见位置
        int lastItem = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
        int offsetCount = lastItem - firstItem;
        if (position < firstItem && position + offsetCount < firstItem) {
            // 第一种可能:跳转位置在第一个可见位置之前且相隔超过一屏，使用scrollToPositionWithOffset
            int targetPos = position + offsetCount > firstItem ? firstItem : position + offsetCount;
            // 直接跳
            ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(targetPos, 0);
            smoothPos.mToPosition = position;
            smoothPos.mShouldScroll = true;
        } else if (position > lastItem && position - offsetCount > lastItem) {
            // 第二种可能:跳转位置在最后可见项之后且相隔超过一屏，则先调用scrollToPositionWithOffset指定到目标位置相隔一屏的位置
            // 再通过OnGlobalLayoutListener控制再次调用smoothMoveToPosition，执行smoothScrollToPosition方法
            int targetPos = position - offsetCount < lastItem ? lastItem : position - offsetCount;
            // 直接跳
            ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(targetPos, 0);
            smoothPos.mToPosition = position;
            smoothPos.mShouldScroll = true;
        } else {
            // 第三种可能:往上的跳转位置在第一个可见位置相隔不超过一屏，或往下的跳转位置和最后一个可见位置相隔不超过一屏
            // 使用smoothScrollToPosition进行缓动，缓动速率详见 LinearLayoutManagerWithScrollTop 类
            mRecyclerView.smoothScrollToPosition(position);
        }
    }

    /**
     * 取消valRv的滚动,并重置SmoothPos的状态
     *
     * @param valRv     valRv
     * @param smoothPos 状态
     */
    private static void stopScrollIfNeed(RecyclerView valRv, SmoothPos smoothPos) {
        if (smoothPos.mScrolling) {
            valRv.stopScroll();
            smoothPos.resetState();
        }
    }

    /**
     * 更新titleRv的选中状态
     *
     * @param valRv        valRecyclerView
     * @param valAdapter   valueAdapter
     * @param titleAdapter titleAdapter
     * @param scrolling    是否在缓动状态中,缓动中忽略更新选中
     */
    private static void updateSelectPos(RecyclerView valRv, BaseValueAdapter valAdapter,
                                        BaseTitleAdapter titleAdapter, boolean scrolling) {
        // 获取右侧列表的第一个可见Item的position
        int topPosition = ((LinearLayoutManager) valRv.getLayoutManager()).findFirstVisibleItemPosition();
        // 如果列表不是滚动状态,更新此项对应的是左边的大类的index
        BaseValueBean item = (BaseValueBean) valAdapter.getItem(topPosition);
        if (item != null && !scrolling) {
            int bindPos = item.bindTitlePosition;
            titleAdapter.updateSelectedPos(bindPos);
        }
    }

    public static class SmoothPos {
        //目标项是否需要二次滑动
        public boolean mShouldScroll;
        //记录目标项位置
        public int mToPosition;
        //点击title列表使右侧value列表滚动过程中,title列表不跟随变化
        public boolean mScrolling;

        public void resetState() {
            mShouldScroll = false;
            mScrolling = false;
            mToPosition = 0;
        }
    }

}
