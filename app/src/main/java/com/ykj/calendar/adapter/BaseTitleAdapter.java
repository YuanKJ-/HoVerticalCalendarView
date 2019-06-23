package com.ykj.calendar.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ykj.calendar.bean.BaseBindPositionBean;

import java.util.List;

/**
 * Created by kejie.yuan
 * Date: 2019/3/20
 * Description: 联动列表左侧title列表adapter基类
 */
public abstract class BaseTitleAdapter<T extends BaseBindPositionBean, K extends BaseViewHolder> extends BaseQuickAdapter<T, K> {

    public BaseTitleAdapter(int layoutResId, @Nullable List<T> data) {
        super(layoutResId, data);
    }

    public BaseTitleAdapter(@Nullable List<T> data) {
        super(data);
    }

    public BaseTitleAdapter(int layoutResId) {
        super(layoutResId);
    }

    /**
     * 子类实现该方法做高亮逻辑处理
     * 点击title列表某一个item时会进入该回调
     * 当value列表滑动时会进入该回调方法
     * 需要注意recyclerView onScroll方法滑动时会一直触发,所以在方法内部需要判断pos是否与当前高亮pos相同,相同则不做处理,优化性能
     * @param pos 被选中高亮的item位置
     */
    public abstract void updateSelectedPos(int pos);
}
