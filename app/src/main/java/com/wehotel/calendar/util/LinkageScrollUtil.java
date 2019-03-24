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

    private static final int DEFAULT_OFFSET = 1500;
    //目标项是否在最后一个可见项之后
    private static boolean mShouldScroll;
    //记录目标项位置
    private static int mToPosition;

    public static void bindLinkageScroll(RecyclerView titleRv, final RecyclerView valueRv) {
        //初始化联合滚动效果

        // TODO: 2019/3/24 滚动时先计算有多少个item(放弃

        // TODO: 2019/3/24 向上滚动时先滚到targetItem的下面几个item，再缓动上去,向下同理
        final BaseTitleAdapter titleAdapter = (BaseTitleAdapter) titleRv.getAdapter();
        titleAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                BaseTitleBean item = (BaseTitleBean) titleAdapter.getItem(position);
                if (item != null) {

//                    int targetPos = valueRv.getChildAdapterPosition(valueRv.getChildAt(0));
//
//                    int range = -(targetPos - item.bindValuePosition);
//                    int offset = UIUtil.dip2px(valueRv.getContext(), 40f) * range;


//                    valueRv.smoothScrollToPosition(item.bindValuePosition);
                    smoothMoveToPosition(valueRv, item.bindValuePosition);
//                    int targetPos = item.bindValuePosition + 8;
//                    ((LinearLayoutManager) valueRv.getLayoutManager()).scrollToPositionWithOffset(targetPos, 0);
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
                if (mShouldScroll && RecyclerView.SCROLL_STATE_IDLE == newState) {
                    mShouldScroll = false;
                    smoothMoveToPosition(valueRv, mToPosition);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //获取右侧列表的第一个可见Item的position
                int topPosition = ((LinearLayoutManager) valueRv.getLayoutManager()).findFirstVisibleItemPosition();
                // 如果此项对应的是左边的大类的index
                BaseValueBean item = (BaseValueBean) valueAdapter.getItem(topPosition);
                if (item != null) {
                    int bindPos = item.bindTitlePosition;
                    titleAdapter.updateSelectedPos(bindPos);
                }
            }
        });
        valueRv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mShouldScroll) {
                    smoothMoveToPosition(valueRv, mToPosition);
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
    private static void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        // 第一个可见位置
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        // 最后一个可见位置
        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
        int offsetCount = lastItem - firstItem;
        if (position < firstItem && position + offsetCount < firstItem) {
            // 第一种可能:跳转位置在第一个可见位置之前，使用smoothScrollToPosition
            int targetPos = position + offsetCount > firstItem ? firstItem : position + offsetCount;
//            mRecyclerView.smoothScrollToPosition(targetPos);
            // 直接跳
            ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(targetPos, 0);
            mToPosition = position;
            mShouldScroll = true;
        } else if (position > lastItem && position - offsetCount > lastItem){
            // 第三种可能:跳转位置在最后可见项之后，则先调用smoothScrollToPosition将要跳转的位置滚动到可见位置
            // 再通过onScrollStateChanged控制再次调用smoothMoveToPosition，执行上一个判断中的方法

            int targetPos = position - offsetCount < lastItem ? lastItem : position - offsetCount;
//            mRecyclerView.smoothScrollToPosition(targetPos);
            // 直接跳
            ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(targetPos, 0);
            mToPosition = position;
            mShouldScroll = true;
        } else {
            // 第二种可能:跳转位置在第一个可见位置之后，最后一个可见项之前
//            int movePosition = position - firstItem;
//            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
//                int top = mRecyclerView.getChildAt(movePosition).getTop();
//                // smoothScrollToPosition 不会有效果，此时调用smoothScrollBy来滑动到指定位置
//                mRecyclerView.smoothScrollBy(0, top);
//            }
            mRecyclerView.smoothScrollToPosition(position);
        }
    }

    private static void goToPos(RecyclerView mRecyclerView, final int position) {
// 第一个可见位置
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        // 最后一个可见位置
        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
        if (position < firstItem-10) {
            // 第一种可能:跳转位置在第一个可见位置之前，使用smoothScrollToPosition
            mToPosition = position;
            mShouldScroll = true;
            ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(position, -mRecyclerView.getHeight());
        } else if (position > firstItem + 10){
            // 第三种可能:跳转位置在最后可见项之后，则先调用smoothScrollToPosition将要跳转的位置滚动到可见位置
            // 再通过onScrollStateChanged控制再次调用smoothMoveToPosition，执行上一个判断中的方法
            mToPosition = position;
            mShouldScroll = true;
            ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(position, mRecyclerView.getHeight());
        } else {
            // 第二种可能:跳转位置在第一个可见位置之后，最后一个可见项之前
            smoothMoveToPosition(mRecyclerView, position);
        }
    }


}
