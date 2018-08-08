package com.example.ledwisdom1.home;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.ledwisdom1.R;
import com.example.ledwisdom1.databinding.ItemLamp2Binding;
import com.example.ledwisdom1.device.entity.Lamp;
import com.example.ledwisdom1.common.BindingAdapters;

import java.util.List;
import java.util.Objects;


public class LampAdapter2 extends RecyclerView.Adapter<LampAdapter2.ViewHolder> {

    private List<Lamp> lampList;
    private final OnHandleLampListener handleLampListener;
    //    灯具的类型
    private int type;

    public LampAdapter2(OnHandleLampListener handleLampListener) {
        this.handleLampListener = handleLampListener;
    }

    public LampAdapter2(OnHandleLampListener handleLampListener, int type) {
        this.handleLampListener = handleLampListener;
        this.type = type;
    }

    public List<Lamp> getLampList() {
        return lampList;
    }


    int version = 0;

    @SuppressLint("StaticFieldLeak")
    public void replaceLamps(List<Lamp> data) {
        version++;
        if (null == lampList) {
            if (data == null) {
                return;
            }
            lampList = data;
            notifyDataSetChanged();
        } else if (null == data) {
            int oldSize = lampList.size();
            lampList = null;
            notifyItemRangeRemoved(0, oldSize);
        } else {
            final int startVersion = version;
            final List<Lamp> old = lampList;
            new AsyncTask<Void, Void, DiffUtil.DiffResult>() {
                @Override
                protected DiffUtil.DiffResult doInBackground(Void... voids) {
                    return DiffUtil.calculateDiff(new DiffUtil.Callback() {
                        @Override
                        public int getOldListSize() {
                            return old.size();
                        }

                        @Override
                        public int getNewListSize() {
                            return data.size();
                        }

                        @Override
                        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                            Lamp oldItem = old.get(oldItemPosition);
                            Lamp newItem = data.get(newItemPosition);
                            return Objects.equals(oldItem, newItem);
                        }

                        @Override
                        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                            Lamp oldItem = old.get(oldItemPosition);
                            Lamp newItem = data.get(newItemPosition);
                            return Objects.equals(oldItem.getId(), newItem.getId()) &&
                                    Objects.equals(oldItem.getBrightness(), newItem.getBrightness()) &&
                                    Objects.equals(oldItem.getColor(), newItem.getColor());
                        }
                    });

                }

                @Override
                protected void onPostExecute(DiffUtil.DiffResult diffResult) {
                    if (startVersion != version) {
                        return;
                    }
                    lampList = data;
                    diffResult.dispatchUpdatesTo(LampAdapter2.this);
                }
            }.execute();
        }
    }


    public void removeLamp(Lamp lamp) {
        lampList.remove(lamp);
        notifyDataSetChanged();
    }

    /**
     * 因为meshAddress是唯一的
     * 在灯的状态发生改变的时候 通过meshAddress 来定位Lamp修改状态
     *
     * @param meshAddress
     */


    public Lamp getLamp(int meshAddress) {
        for (int i = 0; i < getItemCount(); i++) {
            Lamp lamp = lampList.get(i);
            if (lamp.getDevice_id() == meshAddress) {
                return lamp;
            }
        }

        return null;
    }

    public void meshOff() {
        for (Lamp lamp : lampList) {
            lamp.lampStatus.set(BindingAdapters.LIGHT_CUT);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLamp2Binding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_lamp2, parent, false);
        binding.setHandler(handleLampListener);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Lamp lamp = lampList.get(position);
        lamp.setDescription(String.format("%s\n%s", lamp.getName(), lamp.getMac()));
        holder.mBinding.setLamp(lamp);
        holder.mBinding.executePendingBindings();

    }

    @Override
    public int getItemCount() {
        return lampList == null ? 0 : lampList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemLamp2Binding mBinding;

        public ViewHolder(ItemLamp2Binding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}

