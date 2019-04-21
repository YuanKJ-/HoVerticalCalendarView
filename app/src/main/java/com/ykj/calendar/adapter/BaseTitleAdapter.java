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

    public abstract void updateSelectedPos(int pos);
}
