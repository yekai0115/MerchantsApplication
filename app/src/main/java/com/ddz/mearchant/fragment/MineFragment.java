package com.ddz.mearchant.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ddz.mearchant.BaseFragment;
import com.ddz.mearchant.R;
import com.ddz.mearchant.activity.AboutWebViewActivity;
import com.ddz.mearchant.activity.CreatBillActivity;
import com.ddz.mearchant.activity.LoginActivity;
import com.ddz.mearchant.activity.ModifyPassWordActivity;
import com.ddz.mearchant.activity.MyCollectionActivity;
import com.ddz.mearchant.activity.MyMessageActivity;
import com.ddz.mearchant.activity.MyShopActivity;
import com.ddz.mearchant.activity.ShopSettingActivity;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.dialog.DialogConfirm;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.models.ShopBase;
import com.ddz.mearchant.utils.ImageUrl;
import com.ddz.mearchant.view.HandyTextView;

import retrofit2.Call;
import retrofit2.Response;

import static com.ddz.mearchant.R.id.my_shop_linear;


public class MineFragment extends BaseFragment implements View.OnClickListener{
	private  View view;
	private LinearLayout myShopLinear,goodSet,mineFragmentLinear,modifyPasswordLinear,contactHelpLinear;
	private ImageView shopLogo,myMessage;
	private ShopBase data;
	private int shopInte = 1;
	private HandyTextView shopName,shopCatName,shopTime;
	private LinearLayout myCollectionLinear,creatBittLinear,aboutDiandianLinear;
	public MineFragment(){
		super();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.mine_fragment, container, false);
		initViews();
		initDialog();
		initEvents();
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
	}
	@Override
	protected void init() {

	}

	@Override
	protected void initEvents() {
		getShopInfo();
	}

	@Override
	protected void initViews() {
		myShopLinear = (LinearLayout) view.findViewById(R.id.my_shop_linear);
		contactHelpLinear = (LinearLayout) view.findViewById(R.id.contact_help_linear);
		shopLogo = (ImageView) view.findViewById(R.id.shop_logo);
		shopName = (HandyTextView) view.findViewById(R.id.shop_name);
		shopCatName = (HandyTextView) view.findViewById(R.id.shop_cat_name);
		shopTime = (HandyTextView) view.findViewById(R.id.shop_time);
		goodSet = (LinearLayout) view.findViewById(R.id.good_set);
		creatBittLinear = (LinearLayout) view.findViewById(R.id.creat_bitt_linear);
		mineFragmentLinear = (LinearLayout) view.findViewById(R.id.mine_fragment_linear);
		modifyPasswordLinear= (LinearLayout) view.findViewById(R.id.modify_password_linear);
		myCollectionLinear= (LinearLayout) view.findViewById(R.id.my_collection_linear);
		myMessage = (ImageView) view.findViewById(R.id.my_message);
		aboutDiandianLinear = (LinearLayout) view.findViewById(R.id.about_diandian_linear);
		goodSet.setOnClickListener(this);
		myShopLinear.setOnClickListener(this);
		mineFragmentLinear.setOnClickListener(this);
		modifyPasswordLinear.setOnClickListener(this);
		myCollectionLinear.setOnClickListener(this);
		creatBittLinear.setOnClickListener(this);
		myMessage.setOnClickListener(this);
		contactHelpLinear.setOnClickListener(this);
		aboutDiandianLinear.setOnClickListener(this);
	}
	/**
	 * 获取账户余额
	 */
	private void getShopInfo() {
		dialog.show();
		APIService userBiz = RetrofitWrapper.getInstance().create(
				APIService.class);
		Call<BaseResponse<ShopBase>> call = userBiz.getShopInfo(baseApplication.mUser.token);//"18813904075:123456789"
		call.enqueue(new HttpCallBack<BaseResponse<ShopBase>>() {

			@Override
			public void onResponse(Call<BaseResponse<ShopBase>> arg0,
								   Response<BaseResponse<ShopBase>> response) {
				if (dialog.isShowing()){dialog.dismiss();}
				super.onResponse(arg0,response);
				BaseResponse<ShopBase> baseResponse = response.body();
				if (null != baseResponse && baseResponse.getStatus().equals(Constants.T_OK)) {
					String status = baseResponse.getStatus();
					data = baseResponse.getData();
					try {
						ImageUrl.setbitmap(Constants.ALI_MEARCHENT_LOAD+data.getLogo(),shopLogo);
//						Glide.with(getActivity()).load(Constants.ALI_MEARCHENT_LOAD+data.getLogo()).fitCenter()
//								.placeholder(getResources().getDrawable(R.drawable.default_mearent)).error(getResources().getDrawable(R.drawable.default_mearent)).into(shopLogo);
					}catch (Exception e){
						e.printStackTrace();
					}
//						ImageUrl.setbitmap(Constants.ALI_MEARCHENT_LOAD+data.getLogo(),shopLogo);
						shopName.setText(data.getName());
						shopCatName.setText(data.getCat_name());
						shopTime.setText(data.getService_time());
					if (data.getRecord_order().equals("1")){
						creatBittLinear.setVisibility(View.VISIBLE);
					}else{
						creatBittLinear.setVisibility(View.GONE);
					}
					if (data.getQr_pay().equals("1")){
						myCollectionLinear.setVisibility(View.VISIBLE);
					}else{
						myCollectionLinear.setVisibility(View.GONE);
					}
						baseApplication.mCache.put(Constants.AUTH_SHOP_USER,data);


				}

			}

			@Override
			public void onFailure(Call<BaseResponse<ShopBase>> arg0,
								  Throwable arg1) {
				if (dialog.isShowing()){dialog.dismiss();}
				super.onFailure(arg0,arg1);
			}
		});

	}
	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case my_shop_linear:
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				intent.setClass(getActivity(),MyShopActivity.class);
				ShopBase shopBase =  (ShopBase)baseApplication.mCache.getAsObject(Constants.AUTH_SHOP_USER);
				bundle.putSerializable("ShopBase",shopBase);
				intent.putExtra("ShopBase",bundle);
				startActivity(intent);
				break;
			case R.id.good_set:
				startActivity(ShopSettingActivity.class);
				break;
			case R.id.mine_fragment_linear:
				showExitDialog();
				break;
			case R.id.modify_password_linear:
				startActivity(ModifyPassWordActivity.class);
				break;
			case R.id.my_collection_linear:
				startActivity(MyCollectionActivity.class);
				break;
			case R.id.creat_bitt_linear:
				startActivity(CreatBillActivity.class);
				break;
			case R.id.my_message:
				startActivity(MyMessageActivity.class);
				break;
			case R.id.contact_help_linear:
				showDialDialog();
				break;
			case R.id.about_diandian_linear:
				startActivity(AboutWebViewActivity.class);
				break;

		}
	}
	private void showExitDialog() {
		DialogConfirm alert = new DialogConfirm();
		alert.setListener(new DialogConfirm.OnOkCancelClickedListener() {
			@Override
			public void onClick(boolean isOkClicked) {
				if(isOkClicked)
					startActivity(LoginActivity.class);
			}
		});
		alert.showDialog(getActivity(), getResources().getString(R.string.dialog_personal_exit),"退出","取消");
	}
	private void showDialDialog() {
		DialogConfirm alert = new DialogConfirm();
		alert.setListener(new DialogConfirm.OnOkCancelClickedListener() {
			@Override
			public void onClick(boolean isOkClicked) {
				if(isOkClicked)
					startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", Constants.COM_PHONE_NUM, null)));
			}
		});
		alert.showDialog(getActivity(), getResources().getString(R.string.dialog_personal_phone),"呼叫","取消");
	}
	@Override
	public void onResume() {
		super.onResume();
		if(null!=baseApplication.mCache.getAsObject(Constants.AUTH_SHOP_USER))
			baseApplication.mShopBase = (ShopBase)baseApplication.mCache.getAsObject(Constants.AUTH_SHOP_USER);
		if(baseApplication.mShopBase != null && baseApplication.mShopBase.getLogo() != null) {
			try {
				shopName.setText(baseApplication.mShopBase.getName());
				shopCatName.setText(baseApplication.mShopBase.getCat_name());
				shopTime.setText(baseApplication.mShopBase.getService_time());
//				Glide.with(getActivity()).load(Constants.ALI_MEARCHENT_LOAD+baseApplication.mShopBase.getLogo()).fitCenter()
//						.placeholder(getResources().getDrawable(R.drawable.default_mearent)).error(getResources().getDrawable(R.drawable.default_mearent)).into(shopLogo);
				ImageUrl.setbitmap(Constants.ALI_MEARCHENT_LOAD+baseApplication.mShopBase.getLogo(),shopLogo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();

	}
	
	
}
