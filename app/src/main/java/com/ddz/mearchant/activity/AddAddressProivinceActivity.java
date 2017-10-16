package com.ddz.mearchant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ddz.mearchant.BaseActivity;
import com.ddz.mearchant.R;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.db.DBManager;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.models.Proivince;
import com.ddz.mearchant.models.ShopBase;
import com.ddz.mearchant.utils.PinyinUtils;
import com.ddz.mearchant.view.HandyTextView;
import com.ddz.mearchant.view.SideLetterBar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;


public class AddAddressProivinceActivity extends BaseActivity implements View.OnClickListener{
    private ListView mListView;
    private SideLetterBar mLetterBar;
    private ShopBase shopBase;
    private HandyTextView htvCenter,htvRight;
    private LinearLayout htvLeft;
    private CityListAdapter mCityAdapter;
    private List<Proivince> mAllCities = new ArrayList<Proivince>();
    private DBManager dbManager;
    private TextView overlay;
    private static final int VIEW_TYPE_COUNT = 3;
    private Context mContext;
    private LayoutInflater inflater;
    private List<Proivince> mCities;
    private List<Proivince> mSelectCities = new ArrayList<Proivince>();
    private HashMap<String, Integer> letterIndexes;
    private String[] sections;
    private String locatedCity;
    private Map<Integer,Integer> selected = new HashMap<Integer,Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address_name);
        if(getIntent().hasExtra("city")) {
            mSelectCities = (ArrayList<Proivince>) getIntent().getSerializableExtra("city");
        }
        mContext=this;
        initViews();
        initDialog();
        initEvents();
    }

    @Override
    protected void initViews() {
        htvRight = (HandyTextView)findViewById(R.id.title_htv_rigit);
        htvCenter = (HandyTextView)findViewById(R.id.title_htv_center);
        htvLeft = (LinearLayout)findViewById(R.id.title_htv_left);
        htvRight.setVisibility(View.VISIBLE);
        htvCenter.setText("选择地址");
        htvRight.setOnClickListener(this);
        htvLeft.setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.listview_all_city);

        overlay = (TextView) findViewById(R.id.tv_letter_overlay);
        mLetterBar = (SideLetterBar) findViewById(R.id.side_letter_bar);
//        dbManager = new DBManager(this);
//        dbManager.copyDBFile();
//        mAllCities = dbManager.getAllCities();
//        mCityAdapter = new CityListAdapter(this, mAllCities);
//        mListView.setAdapter(mCityAdapter);
//        mCityAdapter.updateLocateState();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_htv_rigit:
                if (mSelectCities.size()>0){
                    Intent data = new Intent();
                    Bundle bundle = new Bundle();
                    data.putExtra("cities", (Serializable) mSelectCities);
                    setResult(RESULT_OK, data);
                    defaultFinish();
                }else {
                    showShortToast("您还没有选择城市");
                }

                break;
            case R.id.title_htv_left:
                defaultFinish();
                break;
        }
    }
    private  void  getProivinceList(){
        dialog.show();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<List<Proivince>>> call = userBiz.getProivinceList(baseApplication.mUser.token);//"18813904075:123456789"
        call.enqueue(new HttpCallBack<BaseResponse<List<Proivince>>>() {

            @Override
            public void onResponse(Call<BaseResponse<List<Proivince>>> arg0,
                                   Response<BaseResponse<List<Proivince>>> response) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onResponse(arg0,response);
                BaseResponse<List<Proivince>> baseResponse = response.body();
                if (null != baseResponse && baseResponse.getStatus().equals(Constants.T_OK)) {
                    String status = baseResponse.getStatus();
                    List<Proivince> data = baseResponse.getData();
                    mAllCities.addAll(data);
                    mCityAdapter = new CityListAdapter();
                    mListView.setAdapter(mCityAdapter);
                    mCityAdapter.notifyDataSetChanged();
                    mLetterBar.setOverlay(overlay);
                    mLetterBar.setOnLetterChangedListener(new SideLetterBar.OnLetterChangedListener() {
                        @Override
                        public void onLetterChanged(String letter) {
                            int position = mCityAdapter.getLetterPosition(letter);
                            mListView.setSelection(position);
                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<BaseResponse<List<Proivince>>> arg0,
                                  Throwable arg1) {
            }
        });
    }
    @Override
    protected void initEvents() {
        getProivinceList();

    }








    class CityListAdapter extends BaseAdapter {


        public CityListAdapter() {
            if (mAllCities == null) {
                mAllCities = new ArrayList<>();
            }
            int size = mAllCities.size();
            letterIndexes = new HashMap<>();
            sections = new String[size];
            for (int index = 0; index < size; index++) {
                //当前城市拼音首字母
                String currentLetter = PinyinUtils.getFirstLetter(mAllCities.get(index).getSpell());
                //上个首字母，如果不存在设为""
                String previousLetter = index >= 1 ? PinyinUtils.getFirstLetter(mAllCities.get(index - 1).getSpell()) : "";
                if (!TextUtils.equals(currentLetter, previousLetter)) {
                    letterIndexes.put(currentLetter, index);
                    sections[index] = currentLetter;
                }
            }
        }

        /**
         * 更新定位状态
         *
         * @param
         */
        public void updateLocateState() {
            notifyDataSetChanged();
        }

        /**
         * 获取字母索引的位置
         *
         * @param letter
         * @return
         */
        public int getLetterPosition(String letter) {
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
            return mAllCities == null ? 0 : mAllCities.size();
        }

        @Override
        public Proivince getItem(int position) {
            return mAllCities == null ? null : mAllCities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            final CityViewHolder holder;
//            if (view == null){
            view = View.inflate(AddAddressProivinceActivity.this,R.layout.cp_item_city_listview,null);
            holder = new CityViewHolder();
            holder.checkbox = (CheckBox) view.findViewById(R.id.checkbox);
            holder.letter = (TextView) view.findViewById(R.id.tv_item_city_listview_letter);
            holder.name = (TextView) view.findViewById(R.id.tv_item_city_listview_name);
//            holder.checkbox.setTag(position);
            view.setTag(holder);
//            }else{
//                holder = (CityViewHolder) view.getTag();
//            }
//            holder.checkbox.setTag(position);
//            if (selected.containsKey(position))
//                holder.checkbox.setChecked(true);
//            else
//                holder.checkbox.setChecked(false);
            final String city = mAllCities.get(position).getRegion_name();
            final int cityId = mAllCities.get(position).getRegion_id();
            holder.name.setText(city);
            String currentLetter = PinyinUtils.getFirstLetter(mAllCities.get(position).getSpell());
            String previousLetter = position >= 1 ? PinyinUtils.getFirstLetter(mAllCities.get(position - 1).getSpell()) : "";
            if (!TextUtils.equals(currentLetter, previousLetter)) {
                holder.letter.setVisibility(View.VISIBLE);
                holder.letter.setText(currentLetter);
            } else {
                holder.letter.setVisibility(View.GONE);
            }
            holder.checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mAllCities.get(position).isChecked()){
                        mAllCities.get(position).setChecked(false);
                        for (int i = 0; i < mSelectCities.size(); i++) {
                            if (mSelectCities.get(i).getRegion_id() == mAllCities.get(position).getRegion_id()) {
                                mSelectCities.remove(i);
                            }
                        }
                    }else{
                        if(mSelectCities.size() < 5 ) {
                            mAllCities.get(position).setChecked(true);
                            mSelectCities.add(mAllCities.get(position));
                        }else{
                            holder.checkbox.setChecked(false);
                            showShortToast("最多选择五个城市");
                        }
                    }
                }
            });
            if (mSelectCities.size() > 0) {
                for (int i = 0; i < mSelectCities.size(); i++) {
                    if (mSelectCities.get(i).getRegion_id() == mAllCities.get(position).getRegion_id()) {
                        holder.checkbox.setChecked(true);
                        mAllCities.get(position).setChecked(true);
                    }
                }
            }
            return view;
        }

        class CityViewHolder {
            TextView letter;
            TextView name;
            CheckBox checkbox;
        }

    }
}
