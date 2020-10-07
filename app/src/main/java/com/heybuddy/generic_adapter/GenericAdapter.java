package com.heybuddy.generic_adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public abstract class GenericAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected final Class<VH> mViewHolderClass;
    public List<T> modelList;
    protected int layout;

    public GenericAdapter(@LayoutRes int layout, Class<VH> mViewHolderClass, List<T> modelList) {
        this.modelList = modelList;
        this.layout = layout;
        this.mViewHolderClass = mViewHolderClass;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        try {
            Constructor<VH> constructor = mViewHolderClass.getConstructor(View.class);
            return constructor.newInstance(view);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        // call load more method if list reach to bottom
        if ((position >= getItemCount() - 1)) {
            loadMore();
        }
        T model = modelList.get(position);
        setViewHolderData(holder, model, position);
    }

    public abstract void loadMore();

    public abstract void setViewHolderData(VH viewHolderData, T data, int position);

    @Override
    public int getItemCount() {
        return modelList.size();
    }

}