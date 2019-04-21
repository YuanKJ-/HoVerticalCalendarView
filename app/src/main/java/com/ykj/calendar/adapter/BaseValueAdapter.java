package com.ykj.calendar.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ykj.calendar.bean.BaseBindPositionBean;

import java.util.List;

/**
 * Created by kejie.yuan
 * Date: 2019/3/20
 * Description: 联动列表右侧value列表adapter基类
 */
public abstract class BaseValueAdapter<T extends BaseBindPositionBean, K extends BaseViewHolder> extends BaseQuickAdapter<T, K> {

    public BaseValueAdapter(int layoutResId, @Nullable List<T> data) {
        super(layoutResId, data);
    }

    public BaseValueAdapter(@Nullable List<T> data) {
        super(data);
    }

    public BaseValueAdapter(int layoutResId) {
        super(layoutResId);
    }
}
