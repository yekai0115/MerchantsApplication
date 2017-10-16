package com.ddz.mearchant.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ddz.mearchant.BaseFragment;
import com.ddz.mearchant.R;
import com.ddz.mearchant.activity.AddProductActivity;
import com.ddz.mearchant.activity.EditProductActivity;
import com.ddz.mearchant.activity.SearchProductActivity;
import com.ddz.mearchant.adapter.TitlePopAdapter;
import com.ddz.mearchant.adapter.TitlePopRightAdapter;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.dialog.DialogConfirm;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.interfaces.SweepOnTouchListener;
import com.ddz.mearchant.models.Custom;
import com.ddz.mearchant.models.Product;
import com.ddz.mearchant.view.HandyTextView;
import com.ddz.mearchant.view.PullToRefreshLayout;
import com.ddz.mearchant.view.SlideListView2;
import com.ddz.mearchant.view.SwipeListMenu;
import com.ddz.mearchant.view.SwipeListMenuCreator;
import com.ddz.mearchant.view.SwipeListMenuItem;
import com.ddz.mearchant.view.TitlePopRightWindow;
import com.ddz.mearchant.view.TitlePopWindow;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static com.ddz.mearchant.R.id.manager_all_text;


public class CommodityManageFragment extends BaseFragment implements View.OnClickListener,PullToRefreshLayout.OnRefreshListener{
	private View view;
	private SlideListView2 scrolHorinListView;
	private ArrayList<Product> products = new ArrayList<>();
	private ArrayList<Product> selectProducts = new ArrayList<>();
	private HandyTextView choiceNum,downeChonicProduct,upChonicProduct;
	private ProductAdapter productAdapter;
	private ImageView mangerEdit,addProductButton,mangerAll;
	private HandyTextView managerAllText,deleteChonicProduct;
	private LinearLayout showChoiseLinear,searchLinear;
	private RelativeLayout relayoutView,managerMore;
	private ArrayList<Custom> customs = new ArrayList<>();
	private SweepOnTouchListener sweepOnTouchListener;
	private ArrayList<Integer> selectPositon = new ArrayList<>();
	// 定义标题栏弹窗按钮
	private TitlePopWindow window;
	// 定义标题栏弹窗按钮
	private TitlePopRightWindow windowRight;
	private LinearLayout noListShow;
	private PullToRefreshLayout layout;
	private int refreshCount = 1;
	private boolean pull = false;
	private boolean isLastRecord = false;
	private CheckBox selectAllProduct;
	private boolean isSelectAll = false;
	private boolean isInitCheck = false;
	public CommodityManageFragment(){
		super();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.commodity_manage_fragment, container, false);
		initViews();
		initDialog();
		initEvents();
		return view;
	}
	@Override
	protected void initEvents() {
		products.clear();
		customs.clear();
		selectProducts.clear();
		getProductList(1);
		getCustomList();
	}
	private void getProductList(int page){
		dialog.show();
		APIService userBiz = RetrofitWrapper.getInstance().create(
				APIService.class);
		Call<BaseResponse<List<Product>>> call = userBiz.getGoodsLis(baseApplication.mUser.token,page);//"18813904075:123456789"
		call.enqueue(new HttpCallBack<BaseResponse<List<Product>>>() {

			@Override
			public void onResponse(Call<BaseResponse<List<Product>>> arg0,
								   Response<BaseResponse<List<Product>>> response) {
				if (dialog.isShowing()){dialog.dismiss();}
				BaseResponse<List<Product>> baseResponse = response.body();
				if (null != baseResponse && baseResponse.getStatus().equals(Constants.T_OK)) {
					String status = baseResponse.getStatus();
					List<Product> data = baseResponse.getData();
					if (data.size()>0){
						scrolHorinListView.setPullUp(false);
						products.addAll(data);
						layout.setVisibility(View.VISIBLE);
						noListShow.setVisibility(View.GONE);
						isInitCheck = true;
						productAdapter.notifyDataSetChanged();
						if (products.size() < 10){
							isLastRecord = true;
							scrolHorinListView.setPullUp(true);
						}
					}else{
						if (pull){
							isLastRecord = true;
							scrolHorinListView.setPullUp(true);
						}else{
							layout.setVisibility(View.GONE);
							noListShow.setVisibility(View.VISIBLE);
						}
					}
				}

			}

			@Override
			public void onFailure(Call<BaseResponse<List<Product>>> arg0,
								  Throwable arg1) {
				if (dialog.isShowing()){dialog.dismiss();}
			}
		});
	}
	@Override
	protected void init() {

	}
	private boolean isEditing =false;
	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.manger_edit:
					isSelectAll = false;
					isInitCheck = true;
					selectProducts.clear();
					selectAllProduct.setChecked(false);
					choiceNum.setText(String.valueOf(selectProducts.size()));
					if(!isEditing){
						showChoiseLinear.setVisibility(View.VISIBLE);
						isEditing = true;
						productAdapter.notifyDataSetChanged();
					}else {
						showChoiseLinear.setVisibility(View.GONE);
						isEditing = false;
						productAdapter.notifyDataSetChanged();
					}
				break;
			case R.id.manager_all_text:
				showCustomDialog();
				break;
			case R.id.manager_more:
				showInventoryDialog();
				break;
			case R.id.add_product_button:
				startActivity(AddProductActivity.class);
				break;
			case R.id.search_linear:
				startActivity(SearchProductActivity.class);
				break;
			case R.id.delete_chonic_product:
				if (selectProducts.size()>0) {
					final String productdelId = getSelectId();
					DialogConfirm alert = new DialogConfirm();
					alert.setListener(new DialogConfirm.OnOkCancelClickedListener() {
						@Override
						public void onClick(boolean isOkClicked) {
							if (isOkClicked){
								delGoodsBat(productdelId,-1);
							}else{
								Toast.makeText(getActivity(),"取消",Toast.LENGTH_SHORT).show();
							}

						}
					});
					alert.showDialog(getActivity(),"确定删除所选商品吗","确定","取消");
				}else{
					Toast.makeText(getActivity(),"您还没有选择商品",Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.down_chonic_product:
				if (selectProducts.size()>0) {
					final String productdownId = getSelectId();
					DialogConfirm alert = new DialogConfirm();
					alert.setListener(new DialogConfirm.OnOkCancelClickedListener() {
						@Override
						public void onClick(boolean isOkClicked) {
							if (isOkClicked){
								downGoodsBat(productdownId,-1);
							}else{
								Toast.makeText(getActivity(),"取消",Toast.LENGTH_SHORT).show();
							}

						}
					});
					alert.showDialog(getActivity(),"确定下架所选商品吗","确定","取消");
				}else{
					Toast.makeText(getActivity(),"您还没有选择商品",Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.up_chonic_product:
				if (selectProducts.size()>0) {
					final String productdownId = getSelectId();
					DialogConfirm alert = new DialogConfirm();
					alert.setListener(new DialogConfirm.OnOkCancelClickedListener() {
						@Override
						public void onClick(boolean isOkClicked) {
							if (isOkClicked){
								upGoodsBat(productdownId,-1);
							}else{
								Toast.makeText(getActivity(),"取消",Toast.LENGTH_SHORT).show();
							}

						}
					});
					alert.showDialog(getActivity(),"确定上架所选商品吗","确定","取消");
				}else{
					Toast.makeText(getActivity(),"您还没有选择商品",Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.select_all_product:
					if (!isSelectAll){
						selectAllProduct.setChecked(true);
						isSelectAll = true;
						selectProducts.clear();
						selectProducts.addAll(products);
						productAdapter.notifyDataSetChanged();
					}else{
						isSelectAll = false;
						selectAllProduct.setChecked(false);
						selectProducts.clear();
						productAdapter.notifyDataSetChanged();
					}
					choiceNum.setText(String.valueOf(selectProducts.size()));
				break;
		}
	}
	private String getSelectId() {
		String productId = "";
		for (int i = 0; i < selectProducts.size(); i++) {
			productId = productId + selectProducts.get(i).getGoods_id() + ",";
		}
		final String proId = productId.substring(0, productId.length() - 1);
		return proId;
	}
	private void  showInventoryDialog(){
		ArrayList<Custom> cs = new ArrayList<>();
		Custom custom = new Custom();
		custom.setUser_cate_name("全部");
		Custom custom1 = new Custom();
		custom1.setUser_cate_name("在售中");
		Custom custom2 = new Custom();
		custom2.setUser_cate_name("在库中");
		Custom custom3 = new Custom();
		custom3.setUser_cate_name("库存不足");
		cs.add(custom);
		cs.add(custom1);
		cs.add(custom2);
		cs.add(custom3);
		windowRight = new TitlePopRightWindow(getActivity(), ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT, cs);
		if (Build.VERSION.SDK_INT == 24)
		{
			// 适配 android 7.0
			int[] location = new int[2];
			relayoutView.getLocationOnScreen(location);
			int x = location[0];
			int y = location[1];
			windowRight.showAtLocation(relayoutView, Gravity.NO_GRAVITY, -100, y + relayoutView.getHeight());
		}
		else
		{
			windowRight.showAsDropDown(relayoutView, 0, 0);
		}
		windowRight.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
			}
		});
		// 实例化标题栏按钮并设置监听
		windowRight.setPopItemClickListener(new TitlePopRightAdapter.PopItemClickListener() {

			@Override
			public void click(int position, View view) {
				products.clear();
				if (position == 0){
					getProductList(1);
				}else{
					queryProductByStore(position);
					//分类搜索
				}

			}
		});
	}
	private void initpullView(){
		refreshCount = 1;
		isLastRecord = false;
		scrolHorinListView.setPullUp(false);
		scrolHorinListView.setPullDown(false);
		pull = false;
	}
	private void  showCustomDialog(){
		mangerAll.setBackground(getResources().getDrawable(R.drawable.manage_option1));
		if (customs.size()==0){
			return;
		}
		window = new TitlePopWindow(getActivity(), ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT, customs);
		if (Build.VERSION.SDK_INT == 24)
		{
			// 适配 android 7.0
			int[] location = new int[2];
			relayoutView.getLocationOnScreen(location);
			int x = location[0];
			int y = location[1];
			window.showAtLocation(relayoutView, Gravity.NO_GRAVITY, x + relayoutView.getWidth(), y + relayoutView.getHeight());
		}
		else
		{
			window.showAsDropDown(relayoutView, 0, 0);
		}
		window.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				mangerAll.setBackground(getResources().getDrawable(R.drawable.manage_option));
			}
		});
		// 实例化标题栏按钮并设置监听
		window.setPopItemClickListener(new TitlePopAdapter.PopItemClickListener() {

			@Override
			public void click(int position, View view) {
				managerAllText.setText(customs.get(position).getUser_cate_name());
				mangerAll.setBackground(getResources().getDrawable(R.drawable.manage_option));
				//分类搜索
				products.clear();
				if (position==0){
					getProductList(1);
				}else{
					queryProductByCustomId(customs.get(position).getGoods_user_cat_id());
				}
			}
		});
	}
	SwipeListMenuCreator creator = new SwipeListMenuCreator() {
		@Override
		public void create(SwipeListMenu menu) {
			switch (menu.getViewType()) {
				case 0:
					createMenu1(menu);
					break;
				case 1:
					createMenu2(menu);
					break;
			}
		}
	};
	private void createMenu1(SwipeListMenu menu){
		// create "open" item
		SwipeListMenuItem openItem = new SwipeListMenuItem(mContext);
		// set item background
		openItem.setBackground(new ColorDrawable(Color.rgb(255, 208, 78)));
		// set item width
		openItem.setWidth(140);
		openItem.setId(1);
		// set item title
		openItem.setTitle("下架");
		// // set item title fontsize
		openItem.setTitleSize(15);
		// // set item title font color
		openItem.setTitleColor(getResources().getColor(R.color.bg_color));

		// openItem.setIcon(R.drawable.collect);
		// add to menu
		// create "open" item
		SwipeListMenuItem openItem1 = new SwipeListMenuItem(mContext);
		// set item background
		openItem1.setBackground(new ColorDrawable(Color.rgb(255, 91, 41)));
		// set item width
		openItem1.setWidth(140);
		openItem1.setId(2);
		// set item title
		openItem1.setTitle("删除");
		// // set item title fontsize
		openItem1.setTitleSize(15);
		// // set item title font color
		openItem1.setTitleColor(Color.WHITE);
		// openItem.setIcon(R.drawable.collect);
		// add to menu
		menu.addMenuItem(openItem);
		menu.addMenuItem(openItem1);
	}
	private void createMenu2(SwipeListMenu menu){
		// create "open" item
		SwipeListMenuItem openItem = new SwipeListMenuItem(mContext);
		// set item background
		openItem.setBackground(new ColorDrawable(Color.rgb(255, 208, 78)));
		// set item width
		openItem.setWidth(140);
		openItem.setId(1);
		// set item title
		openItem.setTitle("上架");
		// // set item title fontsize
		openItem.setTitleSize(15);
		// // set item title font color
		openItem.setTitleColor(getResources().getColor(R.color.bg_color));

		// openItem.setIcon(R.drawable.collect);
		// add to menu
		// create "open" item
		SwipeListMenuItem openItem1 = new SwipeListMenuItem(mContext);
		// set item background
		openItem1.setBackground(new ColorDrawable(Color.rgb(255, 91, 41)));
		// set item width
		openItem1.setWidth(140);
		openItem1.setId(2);
		// set item title
		openItem1.setTitle("删除");
		// // set item title fontsize
		openItem1.setTitleSize(15);
		// // set item title font color
		openItem1.setTitleColor(Color.WHITE);
		// openItem.setIcon(R.drawable.collect);
		// add to menu
		menu.addMenuItem(openItem);
		menu.addMenuItem(openItem1);
	}
	private  void queryProductByStore(int type){dialog.show();
		APIService userBiz = RetrofitWrapper.getInstance().create(
				APIService.class);
		Call<BaseResponse<List<Product>>> call = userBiz.findGoodsSale(baseApplication.mUser.token,type);//"18813904075:123456789"
		call.enqueue(new HttpCallBack<BaseResponse<List<Product>>>() {

			@Override
			public void onResponse(Call<BaseResponse<List<Product>>> arg0,
								   Response<BaseResponse<List<Product>>> response) {
				if (dialog.isShowing()){dialog.dismiss();}
				super.onResponse(arg0,response);
				BaseResponse<List<Product>> baseResponse = response.body();
				if (null != baseResponse && baseResponse.getStatus().equals(Constants.T_OK)) {
					String status = baseResponse.getStatus();
					List<Product> data = baseResponse.getData();
					if (data.size()>0){
						isLastRecord = true;
						scrolHorinListView.setPullUp(true);
						scrolHorinListView.setPullDown(true);
						products.addAll(data);
						noListShow.setVisibility(View.GONE);
						layout.setVisibility(View.VISIBLE);
						isInitCheck = true;
						productAdapter.notifyDataSetChanged();
					}else {
						noListShow.setVisibility(View.VISIBLE);
						layout.setVisibility(View.GONE);
					}
				}

			}

			@Override
			public void onFailure(Call<BaseResponse<List<Product>>> arg0,
								  Throwable arg1) {
				if (dialog.isShowing()){dialog.dismiss();}
				super.onFailure(arg0,arg1);
			}
		});
	}
	private  void queryProductByCustomId(int customid){
		dialog.show();
		APIService userBiz = RetrofitWrapper.getInstance().create(
				APIService.class);
		Call<BaseResponse<List<Product>>> call = userBiz.findGoodsUserCate(baseApplication.mUser.token,customid);
		call.enqueue(new HttpCallBack<BaseResponse<List<Product>>>() {

			@Override
			public void onResponse(Call<BaseResponse<List<Product>>> arg0,
								   Response<BaseResponse<List<Product>>> response) {
				if (dialog.isShowing()){dialog.dismiss();}
				super.onResponse(arg0,response);
				BaseResponse<List<Product>> baseResponse = response.body();
				if (null != baseResponse && baseResponse.getStatus().equals(Constants.T_OK)) {
					String status = baseResponse.getStatus();
					List<Product> data = baseResponse.getData();
					if (data.size()>0){
						isLastRecord = true;
						scrolHorinListView.setPullUp(true);
						scrolHorinListView.setPullDown(true);
						products.addAll(data);
						noListShow.setVisibility(View.GONE);
						layout.setVisibility(View.VISIBLE);
						isInitCheck = true;
						productAdapter.notifyDataSetChanged();
					}else {
						noListShow.setVisibility(View.VISIBLE);
						layout.setVisibility(View.GONE);
					}
				}

			}

			@Override
			public void onFailure(Call<BaseResponse<List<Product>>> arg0,
								  Throwable arg1) {
				if (dialog.isShowing()){dialog.dismiss();}
				super.onFailure(arg0,arg1);
			}
		});
	}
	@Override
	protected void initViews() {
		layout = (PullToRefreshLayout)view.findViewById(R.id.product_refresh_view);
		managerAllText = (HandyTextView)view.findViewById(manager_all_text);
		mangerAll = (ImageView)view.findViewById(R.id.manager_all);
		mangerEdit = (ImageView)view.findViewById(R.id.manger_edit);
		scrolHorinListView = (SlideListView2)view.findViewById(R.id.manager_list);
		relayoutView = (RelativeLayout)view.findViewById(R.id.relayout_view);
		showChoiseLinear = (LinearLayout)view.findViewById(R.id.show_choise_linear);
		addProductButton = (ImageView)view.findViewById(R.id.add_product_button);
		choiceNum = (HandyTextView)view.findViewById(R.id.choice_product_num);
		deleteChonicProduct= (HandyTextView)view.findViewById(R.id.delete_chonic_product);
		downeChonicProduct= (HandyTextView)view.findViewById(R.id.down_chonic_product);
		upChonicProduct = (HandyTextView)view.findViewById(R.id.up_chonic_product);
		noListShow = (LinearLayout)view.findViewById(R.id.no_list_show);
		managerMore = (RelativeLayout)view.findViewById(R.id.manager_more);
		searchLinear = (LinearLayout)view.findViewById(R.id.search_linear);
		selectAllProduct  = (CheckBox)view.findViewById(R.id.select_all_product);
		productAdapter = new ProductAdapter();
		scrolHorinListView.setAdapter(productAdapter);
		mangerEdit.setOnClickListener(this);
		managerAllText.setOnClickListener(this);
		addProductButton.setOnClickListener(this);
		deleteChonicProduct.setOnClickListener(this);
		downeChonicProduct.setOnClickListener(this);
		upChonicProduct.setOnClickListener(this);
		managerMore.setOnClickListener(this);
		searchLinear.setOnClickListener(this);
		layout.setOnRefreshListener(this);
		selectAllProduct.setOnClickListener(this);
//		scrolHorinListView.setMenuCreator(creator);
		scrolHorinListView.setPullDown(true);
		scrolHorinListView.initSlideMode(SlideListView2.MOD_RIGHT);
		scrolHorinListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent();
				intent.putExtra("productId",products.get(position).getGoods_id().toString());
				intent.setClass(getActivity(), EditProductActivity.class);
				startActivity(intent);
			}
		});
		/*scrolHorinListView.setOnMenuItemClickListener(new SwipeListView.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(final int position, final SwipeListMenu menu,final int index) {
				final Product product = (Product) productAdapter.getItem(position);
				if (menu.getMenuItem(index).getId() == 2){
					DialogConfirm alert = new DialogConfirm();
						alert.setListener(new DialogConfirm.OnOkCancelClickedListener() {
							@Override
							public void onClick(boolean isOkClicked) {
								if (isOkClicked){
									delGoodsBat(product.getGoods_id(),position);
								}else{
									Toast.makeText(getActivity(),"取消",Toast.LENGTH_SHORT).show();
								}

							}
						});
						alert.showDialog(getActivity(),"确定删除所选商品吗","确定","取消");
				}else {
					DialogConfirm alert = new DialogConfirm();
						alert.setListener(new DialogConfirm.OnOkCancelClickedListener() {
							@Override
							public void onClick(boolean isOkClicked) {
								if (isOkClicked){
									if (product.getIs_on_sale().equals("0")){
										product.setIs_on_sale("1");
										menu.getMenuItem(0).setTitle("上架");
										downGoodsBat(product.getGoods_id(),position);
									}else{
										product.setIs_on_sale("0");
										menu.getMenuItem(0).setTitle("下架");
										upGoodsBat(product.getGoods_id(),position);
									}
//									Toast.makeText(getActivity(),"下架",Toast.LENGTH_SHORT).show();
								}else{
									Toast.makeText(getActivity(),"取消",Toast.LENGTH_SHORT).show();
								}

							}
						});
					if(product.getIs_on_sale().equals("1")){
						alert.showDialog(getActivity(),"确定上架所选商品吗","确定","取消");
					}else{
						alert.showDialog(getActivity(),"确定下架所选商品吗","确定","取消");
					}

				}
				return false;
			}
		});*/
	}

	@Override
	public void onResume() {
		super.onResume();
		isEditing = false;
	}
	
	@Override
	public void onPause() {
		super.onPause();

	}

	class ProductAdapter extends BaseAdapter {
		private LayoutInflater mInflater;

		@Override
		public int getCount() {
			return products.size();
		}

		@Override
		public Object getItem(int position) {
			return products.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public int getViewTypeCount() {
			// menu type count
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			// current menu type
			if (products.get(position).getIs_on_sale() == null){
				return 1;
			}
			if (products.get(position).getIs_on_sale().equals("1")){
				return 1;
			}else{
				return 0;
			}
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null){
				convertView = View.inflate(getActivity(),R.layout.adapter_product_list,null);
				holder = new ViewHolder();

				holder.productDetailLinear= (LinearLayout) convertView.findViewById(R.id.product_detail_linear);
				holder.productCheckbox= (CheckBox) convertView.findViewById(R.id.show_product_checkbox);
				holder.productImage= (ImageView) convertView.findViewById(R.id.product_image);
				holder.productStatus = (HandyTextView) convertView.findViewById(R.id.product_status);
				holder.productName = (HandyTextView) convertView.findViewById(R.id.product_name);
				holder.productPrice = (HandyTextView) convertView.findViewById(R.id.product_price);
				holder.productType = (HandyTextView) convertView.findViewById(R.id.product_type);
				holder.productNum = (HandyTextView) convertView.findViewById(R.id.product_num);
				holder.textLine = (HandyTextView) convertView.findViewById(R.id.text_line);
				holder.other2 = (RelativeLayout) convertView.findViewById(R.id.other2);
				holder.delete2 = (RelativeLayout) convertView.findViewById(R.id.delete2);

				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			if (isInitCheck){
				holder.productCheckbox.setChecked(false);
			}
			if (isSelectAll){
				holder.productCheckbox.setChecked(true);
			}else{
				holder.productCheckbox.setChecked(false);
			}
			if (isEditing){
				products.get(position).setChecked(false);
				holder.productCheckbox.setVisibility(View.VISIBLE);
			}else {
				holder.productCheckbox.setVisibility(View.GONE);
			}
			if (products.get(position).getIs_on_sale().equals("0")){
				holder.textLine.setText("下架");
			}else{
				holder.textLine.setText("上架");
			}
			holder.other2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					DialogConfirm alert = new DialogConfirm();
					alert.setListener(new DialogConfirm.OnOkCancelClickedListener() {
						@Override
						public void onClick(boolean isOkClicked) {
							if (isOkClicked){
								scrolHorinListView.slideBack();
								if (products.get(position).getIs_on_sale().equals("0")){
									downGoodsBat(products.get(position).getGoods_id(),position);
								}else{
									upGoodsBat(products.get(position).getGoods_id(),position);
								}
							}else{
								Toast.makeText(getActivity(),"取消",Toast.LENGTH_SHORT).show();
							}

						}
					});
					if(products.get(position).getIs_on_sale().equals("1")){
						alert.showDialog(getActivity(),"确定上架所选商品吗","确定","取消");
					}else{
						alert.showDialog(getActivity(),"确定下架所选商品吗","确定","取消");
					}
				}
			});
			holder.delete2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					DialogConfirm alert = new DialogConfirm();
					alert.setListener(new DialogConfirm.OnOkCancelClickedListener() {
						@Override
						public void onClick(boolean isOkClicked) {
							if (isOkClicked){
								scrolHorinListView.slideBack();
								delGoodsBat(products.get(position).getGoods_id(),position);
							}else{
								Toast.makeText(getActivity(),"取消",Toast.LENGTH_SHORT).show();
							}

						}
					});
					alert.showDialog(getActivity(),"确定删除所选商品吗","确定","取消");
				}
			});
			holder.productCheckbox.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (products.get(position).isChecked()){
						products.get(position).setChecked(false);
						for (int i = 0; i < selectProducts.size(); i++) {
							if (selectProducts.get(i).getGoods_id() == products.get(position).getGoods_id()) {
								selectProducts.remove(i);
							}
						}
					}else{
						products.get(position).setChecked(true);
						selectProducts.add(products.get(position));
					}
					if (selectProducts.size() == products.size()){
						selectAllProduct.setChecked(true);
						isSelectAll = true;
					}else{
						selectAllProduct.setChecked(false);
						isSelectAll = false;
					}
					choiceNum.setText(String.valueOf(selectProducts.size()));
				}
			});
			if (selectProducts.size() > 0) {
				for (int i = 0; i < selectProducts.size(); i++) {
					if (selectProducts.get(i).getGoods_id() == products.get(position).getGoods_id()) {
						holder.productCheckbox.setChecked(true);
						products.get(position).setChecked(true);
					}
				}
			}
				Glide.with(getActivity()).load(products.get(position).getGoods_pid_uri()+products.get(position).getGoods_thumb()).fitCenter()
						.placeholder(R.drawable.no_image).error(R.drawable.no_image).into(holder.productImage);
//				ImageUrl.setbitmap(products.get(position).getGoods_pid_uri()+products.get(position).getGoods_thumb(),holder.productImage);
			final Integer status = products.get(position).getGoods_status();
				holder.productName.setText(products.get(position).getGoods_name());
			String price = products.get(position).getGoods_price();
			SpannableString styledText = new SpannableString(products.get(position).getGoods_price());
			styledText.setSpan(new AbsoluteSizeSpan(40), 0, price.indexOf("."), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			styledText.setSpan(new AbsoluteSizeSpan(30), price.indexOf("."), styledText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			holder.productPrice.setText(styledText, TextView.BufferType.SPANNABLE);
//				holder.productPrice.setText(products.get(position).getGoods_price());
				final Integer profit = Integer.valueOf(products.get(position).getProfit());
				switch (profit){
				case 1:
					holder.productType.setText("20%");
					break;
				case 2:
					holder.productType.setText("10%");
					break;
				case 5:
					holder.productType.setText("5%");
					break;
				case 10:
					holder.productType.setText("0%");
					break;
			}
			switch (status){
				case 0:
					holder.productStatus.setText("在售中");
					break;
				case 1:
					holder.productStatus.setText("库存不足");
					break;
				case 2:
					holder.productStatus.setText("在库中");
					break;
			}
				holder.productNum.setText(products.get(position).getGoods_number()+"件");
//				holder.productDetailLinear.setOnClickListener(new View.OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						Intent intent = new Intent();
//						intent.putExtra("productId",products.get(position).getGoods_id().toString());
//						intent.setClass(getActivity(), EditProductActivity.class);
//						startActivity(intent);
//					}
//				});
			return convertView;
		}
	}

	public  class ViewHolder{
		private ImageView productImage;
		private HandyTextView productStatus;
		private HandyTextView productName;
		private HandyTextView productPrice;
		private HandyTextView productType;
		private HandyTextView productNum;
		private CheckBox productCheckbox;
		private HandyTextView textLine;
		private RelativeLayout other2,delete2;
		private LinearLayout productDetailLinear;
	}

	private void getCustomList(){
		APIService userBiz = RetrofitWrapper.getInstance().create(
				APIService.class);
		Call<BaseResponse<List<Custom>>> call = userBiz.getGoodsCategory(baseApplication.mUser.token);//"18813904075:123456789"
		call.enqueue(new HttpCallBack<BaseResponse<List<Custom>>>() {

			@Override
			public void onResponse(Call<BaseResponse<List<Custom>>> arg0,
								   Response<BaseResponse<List<Custom>>> response) {
				BaseResponse<List<Custom>> baseResponse = response.body();
				if (null != baseResponse && baseResponse.getStatus().equals(Constants.T_OK)) {
					String status = baseResponse.getStatus();
					if (status.equals(Constants.T_OK)){
						List<Custom> data = baseResponse.getData();
						Custom custom = new Custom();
						custom.setUser_cate_name("全部");
						custom.setChecked(false);
						custom.setGoods_user_cat_id(0);
						customs.add(custom);
						customs.addAll(data);
					}
				}

			}

			@Override
			public void onFailure(Call<BaseResponse<List<Custom>>> arg0,
								  Throwable arg1) {
			}
		});
	}

	private void delGoodsBat(final String productId, final int position) {
		dialog.show();
		APIService userBiz = RetrofitWrapper.getInstance().create(
				APIService.class);
		Call<BaseResponse<String>> call = userBiz.delGoodsBat(baseApplication.mUser.token, productId);//"18813904075:123456789"
		call.enqueue(new HttpCallBack<BaseResponse<String>>() {

			@Override
			public void onResponse(Call<BaseResponse<String>> arg0,
								   Response<BaseResponse<String>> response) {
				BaseResponse<String> baseResponse = response.body();
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				super.onResponse(arg0, response);
				if (null != baseResponse && baseResponse.getStatus().equals(Constants.T_OK)) {
					String status = baseResponse.getStatus();
					String data = baseResponse.getData();
//					if (status.equals(Constants.T_OK)){
					if (selectProducts.size() > 0) {
						for (int i = 0; i < selectProducts.size(); i++) {
							products.remove(selectProducts.get(i));
						}
						choiceNum.setText("0");
					}
					if (position >= 0) {
						for (int i = 0; i < products.size(); i++) {
							if (products.get(i).getGoods_id() == products.get(position).getGoods_id()) {
								products.remove(i);
							}
						}
					}
//						sweepOnTouchListener.setmLastDownView();
					isEditing = false;
					Toast.makeText(getActivity(),"删除商品成功",Toast.LENGTH_SHORT).show();
					showChoiseLinear.setVisibility(View.GONE);
					if (products.size() == 0){
						noListShow.setVisibility(View.VISIBLE);
						scrolHorinListView.setVisibility(View.GONE);
					}else {
						noListShow.setVisibility(View.GONE);
						scrolHorinListView.setVisibility(View.VISIBLE);
						isInitCheck = true;
						productAdapter.notifyDataSetChanged();

					}
//					}
				}

			}

			@Override
			public void onFailure(Call<BaseResponse<String>> arg0,
								  Throwable arg1) {
				if (dialog.isShowing()){dialog.dismiss();}
				super.onFailure(arg0,arg1);
			}
		});
	}
	//下架
	private void downGoodsBat(final String productId, final int position) {
		dialog.show();
		APIService userBiz = RetrofitWrapper.getInstance().create(
				APIService.class);
		Call<BaseResponse<String>> call = userBiz.downGoodsBat(baseApplication.mUser.token,productId);//"18813904075:123456789"
		call.enqueue(new HttpCallBack<BaseResponse<String>>() {

			@Override
			public void onResponse(Call<BaseResponse<String>> arg0,
								   Response<BaseResponse<String>> response) {
				BaseResponse<String> baseResponse = response.body();
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				super.onResponse(arg0, response);
				if (null != baseResponse && baseResponse.getStatus().equals(Constants.T_OK)) {
					String status = baseResponse.getStatus();
					String data = baseResponse.getData();
					if (status.equals(Constants.T_OK)) {
						if (selectProducts.size() > 0) {
							for (int i = 0;i<selectProducts.size();i++){
								for (int j = 0;j<products.size();j++){
									if (selectProducts.get(i).getGoods_id().equals(products.get(j).getGoods_id())){
										products.get(j).setIs_on_sale("1");
										products.get(j).setGoods_status(2);
										break;
									}
								}
							}
							selectProducts.clear();
							choiceNum.setText("0");
						}else{
							if (position>=0){
								products.get(position).setIs_on_sale("1");
								products.get(position).setGoods_status(2);
							}
						}
						showChoiseLinear.setVisibility(View.GONE);
						isEditing = false;
						isInitCheck = true;
						productAdapter.notifyDataSetChanged();
						Toast.makeText(getActivity(),"下架商品成功",Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(getActivity(),baseResponse.getData().toString(),Toast.LENGTH_SHORT).show();
					}

				}
			}
				@Override
				public void onFailure(Call<BaseResponse<String>> arg0,
						Throwable arg1) {
					if (dialog.isShowing()){dialog.dismiss();}
					super.onFailure(arg0,arg1);
				}
			});
		}

	//上架
	private void upGoodsBat(final String productId, final int position) {
		dialog.show();
		APIService userBiz = RetrofitWrapper.getInstance().create(
				APIService.class);
		Call<BaseResponse<String>> call = userBiz.upGoodsBat(baseApplication.mUser.token,productId);//"18813904075:123456789"
		call.enqueue(new HttpCallBack<BaseResponse<String>>() {

			@Override
			public void onResponse(Call<BaseResponse<String>> arg0,
								   Response<BaseResponse<String>> response) {
				BaseResponse<String> baseResponse = response.body();
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				super.onResponse(arg0, response);
				if (null != baseResponse && baseResponse.getStatus().equals(Constants.T_OK)) {
					String status = baseResponse.getStatus();
					String data = baseResponse.getData();
					if (status.equals(Constants.T_OK)) {
						if (selectProducts.size() > 0) {
							for (int i = 0;i<selectProducts.size();i++){
								for (int j = 0;j<products.size();j++){
									if (selectProducts.get(i).getGoods_id().equals(products.get(j).getGoods_id())){
										products.get(j).setIs_on_sale("0");
										products.get(j).setGoods_status(0);
										break;
									}
								}
							}
							selectProducts.clear();
							choiceNum.setText("0");
						}else{
							if (position>=0){
								products.get(position).setIs_on_sale("0");
								products.get(position).setGoods_status(0);
							}
						}
						showChoiseLinear.setVisibility(View.GONE);
						isEditing = false;
						isInitCheck = true;
						productAdapter.notifyDataSetChanged();
						Toast.makeText(getActivity(),"上架商品成功",Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(getActivity(),baseResponse.getData().toString(),Toast.LENGTH_SHORT).show();
					}

				}
			}
			@Override
			public void onFailure(Call<BaseResponse<String>> arg0,
								  Throwable arg1) {
				if (dialog.isShowing()){dialog.dismiss();}
				super.onFailure(arg0,arg1);
			}
		});
	}

	@Override
	public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
		// 下拉刷新操作
		products.clear();
		initpullView();
		productAdapter.notifyDataSetChanged();
		getProductList(1);
		pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
	}
	@Override
	public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
		// 加载操作
		if (!isLastRecord) {
			refreshCount++;
			pull = true;
			getProductList(refreshCount);
		}
		pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
	}
}