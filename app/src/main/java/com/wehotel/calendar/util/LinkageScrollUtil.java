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
import com.wehotel.calendar.bean.WeekBeanV3;
import com.wehotel.calendar.bean.WeekDateBeanV3;
import com.wehotel.calendar.view.SuctionTopDecoration;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

/**
 * Created by kejie.yuan
 * Date: 2019/3/20
 * Description: 联动列表工具类
 */
public class LinkageScrollUtil {

    public static void bindLinkageScroll(RecyclerView titleRv, final RecyclerView valueRv) {
        //初始化联合滚动效果
        final SmoothPos smoothPos = new SmoothPos();
        // 向上滚动时先滚到targetItem的下面几个item，再缓动上去,向下同理
        final BaseTitleAdapter titleAdapter = (BaseTitleAdapter) titleRv.getAdapter();
        titleAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                BaseTitleBean item = (BaseTitleBean) titleAdapter.getItem(position);
                if (item != null) {
                    smoothPos.mScrolling = true;
                    smoothMoveToPosition(valueRv, item.bindValuePosition, smoothPos);

                    titleAdapter.setSelectedYear(item.year);
                    titleAdapter.notifyDataSetChanged();
                }
            }
        });
        final BaseValueAdapter valueAdapter = (BaseValueAdapter) valueRv.getAdapter();
        valueRv.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (RecyclerView.SCROLL_STATE_IDLE == newState) {
                    smoothPos.mScrolling = false;
                    if (smoothPos.mShouldScroll) {
                        smoothPos.mShouldScroll = false;
                        smoothMoveToPosition(valueRv, smoothPos.mToPosition, smoothPos);
                    }
                } else if (RecyclerView.SCROLL_STATE_DRAGGING == newState){
                    if(smoothPos.mScrolling) {
                        valueRv.stopScroll();
                    }
                    smoothPos.mScrolling = false;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //获取右侧列表的第一个可见Item的position
                int topPosition = ((LinearLayoutManager) valueRv.getLayoutManager()).findFirstVisibleItemPosition();
                // 如果此项对应的是左边的大类的index
                BaseValueBean item = (BaseValueBean) valueAdapter.getItem(topPosition);
                if (item != null && !smoothPos.mScrolling) {
                    int bindPos = item.bindTitlePosition;
                    titleAdapter.updateSelectedPos(bindPos);
                }
            }
        });
        valueRv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (smoothPos.mShouldScroll) {
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
        int firstItem = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        // 最后一个可见位置
        int lastItem = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
        int offsetCount = lastItem - firstItem;
        if (position < firstItem && position + offsetCount < firstItem) {
            // 第一种可能:跳转位置在第一个可见位置之前，使用smoothScrollToPosition
            int targetPos = position + offsetCount > firstItem ? firstItem : position + offsetCount;
            // 直接跳
            ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(targetPos, 0);
            smoothPos.mToPosition = position;
            smoothPos.mShouldScroll = true;
        } else if (position > lastItem && position - offsetCount > lastItem){
            // 第二种可能:跳转位置在最后可见项之后，则先调用smoothScrollToPosition将要跳转的位置滚动到可见位置
            // 再通过onScrollStateChanged控制再次调用smoothMoveToPosition，执行上一个判断中的方法
            int targetPos = position - offsetCount < lastItem ? lastItem : position - offsetCount;
            // 直接跳
            ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(targetPos, 0);
            smoothPos.mToPosition = position;
            smoothPos.mShouldScroll = true;
        } else {
            // 第三种可能:跳转位置在第一个可见位置之后，最后一个可见项之前
            mRecyclerView.smoothScrollToPosition(position);
        }
    }

    public static class SmoothPos {
        //目标项是否在最后一个可见项之后,是否需要二次滑动
        public boolean mShouldScroll;
        //记录目标项位置
        public int mToPosition;
        //点击title列表使右侧value列表滚动过程中,title列表不跟随变化
        public boolean mScrolling;
    }

}
