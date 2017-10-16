package com.ddz.mearchant.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ddz.mearchant.R;
import com.ddz.mearchant.models.Proivince;
import com.ddz.mearchant.utils.PinyinUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author zaaach on 2016/1/26.
 */
public class CityListAdapter extends BaseAdapter {
    private static final int VIEW_TYPE_COUNT = 3;

    private Context mContext;
    private LayoutInflater inflater;
    private List<Proivince> mCities;
    private List<Proivince> mSelectCities;
    private HashMap<String, Integer> letterIndexes;
    private String[] sections;
    private OnCityClickListener onCityClickListener;
    private OnCityCheckListener onCityCheckListener;
    private String locatedCity;
    private Map<Integer,Integer> selected = new HashMap<Integer,Integer>();

    public CityListAdapter(Context mContext, List<Proivince> mCities,List<Proivince> mSelectCities) {
        this.mContext = mContext;
        this.mCities = mCities;
        this.mSelectCities  = mSelectCities;
        this.inflater = LayoutInflater.from(mContext);
        if (mCities == null){
            mCities = new ArrayList<>();
        }
        int size = mCities.size();
        letterIndexes = new HashMap<>();
        sections = new String[size];
        for (int index = 0; index < size; index++){
            //当前城市拼音首字母
            String currentLetter = PinyinUtils.getFirstLetter(mCities.get(index).getSpell());
            //上个首字母，如果不存在设为""
            String previousLetter = index >= 1 ? PinyinUtils.getFirstLetter(mCities.get(index - 1).getSpell()) : "";
            if (!TextUtils.equals(currentLetter, previousLetter)){
                letterIndexes.put(currentLetter, index);
                sections[index] = currentLetter;
            }
        }
    }
    /**
     * 更新定位状态
     * @param
     */
    public void updateLocateState(){
        notifyDataSetChanged();
    }

    /**
     * 获取字母索引的位置
     * @param letter
     * @return
     */
    public int getLetterPosition(String letter){
        Integer integer = letterIndexes.get(letter);
        return integer == null ? -1 : integer;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return position < VIEW_TYPE_COUNT - 1 ? position : VIEW_TYPE_COUNT - 1;
    }

    @Override
    public int getCount() {
        return mCities == null ? 0: mCities.size();
    }

    @Override
    public Proivince getItem(int position) {
        return mCities == null ? null : mCities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        CityViewHolder holder;
                if (view == null){
                    view = inflater.inflate(R.layout.cp_item_city_listview, parent, false);
                    holder = new CityViewHolder();
                    holder.checkbox = (CheckBox) view.findViewById(R.id.checkbox);
                    holder.letter = (TextView) view.findViewById(R.id.tv_item_city_listview_letter);
                    holder.name = (TextView) view.findViewById(R.id.tv_item_city_listview_name);
                    holder.checkbox.setTag(position);
                    view.setTag(holder);
                }else{
                    holder = (CityViewHolder) view.getTag();
                }
                    holder.checkbox.setTag(position);
                    if(selected.containsKey(position))
                        holder.checkbox.setChecked(true);
                    else
                        holder.checkbox.setChecked(false);
                    final String city = mCities.get(position).getRegion_name();
                    final int cityId = mCities.get(position).getRegion_id();
                    holder.name.setText(city);
                    String currentLetter = PinyinUtils.getFirstLetter(mCities.get(position).getSpell());
                    String previousLetter = position >= 1 ? PinyinUtils.getFirstLetter(mCities.get(position - 1).getSpell()) : "";
                    if (!TextUtils.equals(currentLetter, previousLetter)){
                        holder.letter.setVisibility(View.VISIBLE);
                        holder.letter.setText(currentLetter);
                    }else{
                        holder.letter.setVisibility(View.GONE);
                    }
                    holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked){
                                if(!selected.containsKey(buttonView.getTag()))
                                    selected.put((Integer) buttonView.getTag(),position);
                            }
                            else{
                                selected.remove((Integer) buttonView.getTag());
                            }
//                            if (onCityCheckListener != null){
//                                    onCityCheckListener.onCityCheck(isChecked,mCities.get(position));
//                            }

                        }
                    });
                    if(mSelectCities.size()>0){
                        for (int i = 0;i<mSelectCities.size();i++){
                            if (mSelectCities.get(i).getRegion_id() == cityId){
                                holder.checkbox.setChecked(true);
                            }
                        }
                    }
                    holder.name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onCityClickListener != null){
                                onCityClickListener.onCityClick(city);
                            }
                        }
                    });
        return view;
    }

    public static class CityViewHolder{
        TextView letter;
        TextView name;
        CheckBox checkbox;
    }
    public void setOnCityCheckListener(OnCityCheckListener listener){
        this.onCityCheckListener = listener;
    }

    public interface OnCityCheckListener{
        void onCityCheck(Boolean isChecked,Proivince city);
    }
    public void setOnCityClickListener(OnCityClickListener listener){
        this.onCityClickListener = listener;
    }

    public interface OnCityClickListener{
        void onCityClick(String name);
    }
}
