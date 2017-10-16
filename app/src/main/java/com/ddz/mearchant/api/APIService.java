package com.ddz.mearchant.api;


import com.ddz.mearchant.bean.BannerBean;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.models.BalanceBase;
import com.ddz.mearchant.models.BillRecord;
import com.ddz.mearchant.models.ChargeQRCod;
import com.ddz.mearchant.models.Convertibility;
import com.ddz.mearchant.models.Custom;
import com.ddz.mearchant.models.EarnBase;
import com.ddz.mearchant.models.EditProduct;
import com.ddz.mearchant.models.ForRecordBase;
import com.ddz.mearchant.models.GivingRecordsBase;
import com.ddz.mearchant.models.GreatDetailBase;
import com.ddz.mearchant.models.MessageBase;
import com.ddz.mearchant.models.OrderBase;
import com.ddz.mearchant.models.OrderDetailBase;
import com.ddz.mearchant.models.OrderDownBase;
import com.ddz.mearchant.models.OrderDownDetail;
import com.ddz.mearchant.models.OrderRecord;
import com.ddz.mearchant.models.Postage;
import com.ddz.mearchant.models.Product;
import com.ddz.mearchant.models.ProductType;
import com.ddz.mearchant.models.Proivince;
import com.ddz.mearchant.models.ShopBase;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;


public interface APIService {

	/**
	 * 登陆
	 */
	@GET("login/validateSupplierCredentials")
	Call<BaseResponse<String>> loginRepo(@Header("X-AUTH-TOKEN") String token);

	@POST("newShop/getbanner")
	Call<BaseResponse<List<BannerBean>>> getbanner();

	@POST("newShop/myOrder")
	@FormUrlEncoded
	Call<BaseResponse<OrderBase>> getOrderList(@Field("orderType") Integer orderType);

	/**
	 * 全部线上订单
	 */
	@POST("supplier/orderAll")
	@FormUrlEncoded
	Call<BaseResponse<List<OrderBase>>> getOrderAllList(@Header("X-AUTH-TOKEN") String token, @Field("page") Integer page,@Field("psize") Integer psize);

	/**
	 * 待发货
	 */
	@POST("supplier/orderDelivery")
	@FormUrlEncoded
	Call<BaseResponse<List<OrderBase>>> getOrderDeliveryList(@Header("X-AUTH-TOKEN") String token, @Field("page") Integer page);

	/**
	 * 待收货
	 */
	@POST("supplier/orderSign")
	@FormUrlEncoded
	Call<BaseResponse<List<OrderBase>>> getOrderSignList(@Header("X-AUTH-TOKEN") String token, @Field("page") Integer page);

	/**
	 * 已完成
	 */
	@POST("supplier/orderDeal")
	@FormUrlEncoded
	Call<BaseResponse<List<OrderBase>>> orderSign(@Header("X-AUTH-TOKEN") String token, @Field("page") Integer page);

	/**
	 * 待付款
	 */
	@POST("supplier/orderNopay")
	@FormUrlEncoded
	Call<BaseResponse<List<OrderBase>>> orderNopay(@Header("X-AUTH-TOKEN") String token, @Field("page") Integer page);
	/**
	 * 累计订单记录
	 */
	@POST("supplier/orderRecord")
	Call<BaseResponse<OrderRecord>> getOrderRecord(@Header("X-AUTH-TOKEN") String token);

	/**
	 * 累计订单记录
	 */
	@POST("supplier/orderLineRecord")
	Call<BaseResponse<OrderRecord>> getOrderDownRecord(@Header("X-AUTH-TOKEN") String token);

	/**
	 * 线下订单
	 */
	@POST("supplier/orderLine")
	@FormUrlEncoded
	Call<BaseResponse<List<OrderDownBase>>> getOrderDownList(@Header("X-AUTH-TOKEN") String token, @Field("page") Integer page);

	/**
	 * 线上订单查找
	 */
	@POST("supplier/orderAllFind")
	@FormUrlEncoded
	Call<BaseResponse<List<OrderBase>>> getOrderListByTime(@Header("X-AUTH-TOKEN") String token, @Field("page") Integer page, @Field("begin") Integer begin, @Field("end") Integer end);

	/**
	 * 线下订单查找
	 */
	@POST("supplier/orderLineFind")
	@FormUrlEncoded
	Call<BaseResponse<List<OrderDownBase>>> getOrderDownListByTime(@Header("X-AUTH-TOKEN") String token, @Field("page") Integer page, @Field("begin") Integer begin, @Field("end") Integer end);

	/**
	 * 获取商铺信息
	 */
	@POST("supplier/getShopInfo")
	Call<BaseResponse<ShopBase>> getShopInfo(@Header("X-AUTH-TOKEN") String token);

	/**
	 * 修改商铺信息
	 */
	@POST("supplier/updateShopInfo")
	@FormUrlEncoded
	Call<BaseResponse<String>> updateShopInfoName(@Header("X-AUTH-TOKEN") String token,
												  @Field("name") String name,
												  @Field("service_time") String service_time,
												  @Field("logo") String logo,
												  @Field("ablum") String ablum,
												  @Field("shop_headpic") String shop_headpic,
												  @Field("cat_id") String cat_id,
												  @Field("shop_licence") String shop_licence);

	/**
	 * 获取模板信息
	 */
	@POST("supplier/getShippingTpl")
	Call<BaseResponse<List<Postage>>> getShippingTpl(@Header("X-AUTH-TOKEN") String token);

	/**
	 * 添加模板信息
	 */
	@POST("supplier/addShippingTpl")
	@FormUrlEncoded
	Call<BaseResponse<String>> addShippingTpl(@Header("X-AUTH-TOKEN") String token, @Field("tpl_name") String tpl_name, @Field("shipping") String shipping);

	/**
	 * 删除模板信息
	 */
	@POST("supplier/delShippingTpl")
	@FormUrlEncoded
	Call<BaseResponse<String>> deleteShippingTpl(@Header("X-AUTH-TOKEN") String token, @Field("shipping_tpl_id") String shipping_tpl_id);

	/**
	 * 获取省信息
	 */
	@POST("supplier/getRegionSon")
	Call<BaseResponse<List<Proivince>>> getProivinceList(@Header("X-AUTH-TOKEN") String token);

	/**
	 * 获取省信息
	 */
	@POST("supplier/getRegionSon")
	@FormUrlEncoded
	Call<BaseResponse<List<Proivince>>> getCityList(@Header("X-AUTH-TOKEN") String token,@Field("region_id") int region_id);

	/**
	 * 获取自定义分类信息
	 */
	@POST("supplier/getGoodsCategory")
	Call<BaseResponse<List<Custom>>> getGoodsCategory(@Header("X-AUTH-TOKEN") String token);

	/**
	 * 添加自定义分类信息
	 */
	@POST("supplier/addGoodsCategory")
	@FormUrlEncoded
	Call<BaseResponse<String>> addGoodsCategory(@Header("X-AUTH-TOKEN") String token, @Field("user_cate_name") String user_cate_name);

	/**
	 * 添加自定义分类信息
	 */
	@POST("supplier/delGoodsCategory")
	@FormUrlEncoded
	Call<BaseResponse<String>> delGoodsCategory(@Header("X-AUTH-TOKEN") String token, @Field("goods_user_cat_id") String goods_user_cat_id);

	/**
	 * 获取订单详情
	 */
	@POST("supplier/orderDetail")
	@FormUrlEncoded
	Call<BaseResponse<OrderDetailBase>> orderDetail(@Header("X-AUTH-TOKEN") String token, @Field("trade_id") int trade_id);

	/**
	 * 获取商品列表
	 */
	@POST("supplier/getGoodsList")
	@FormUrlEncoded
	Call<BaseResponse<List<Product>>> getGoodsLis(@Header("X-AUTH-TOKEN") String token, @Field("page") int page);

	/**
	 * 获取商品一级分类
	 */
	@POST("supplier/goodsCategory")
	Call<BaseResponse<List<ProductType>>> goodsCategory(@Header("X-AUTH-TOKEN") String token);

	/**
	 * 获取商品列表
	 */
	@POST("supplier/findGoodsSale")
	@FormUrlEncoded
	Call<BaseResponse<List<Product>>> findGoodsSale(@Header("X-AUTH-TOKEN") String token, @Field("mode") int mode);

	/**
	 * 获取商品列表
	 */
	@POST("supplier/findGoodsUserCate")
	@FormUrlEncoded
	Call<BaseResponse<List<Product>>> findGoodsUserCate(@Header("X-AUTH-TOKEN") String token, @Field("goods_user_cat_id") int goods_user_cat_id);

	/**
	 * 获取商品列表
	 */
	@POST("supplier/findGoodsKeyowrd")
	@FormUrlEncoded
	Call<BaseResponse<List<Product>>> findGoodsKeyowrd(@Header("X-AUTH-TOKEN") String token, @Field("keyword") String keyword);

	/**
	 * 添加商品
	 */
	@POST("supplier/addGoodsPost")
	@FormUrlEncoded
	Call<BaseResponse<String>> addGoodsPost(@Header("X-AUTH-TOKEN") String token,
											@Field("cat_id") int cat_id,
											@Field("goods_name") String goods_name,
											@Field("profit") int profit,
											@Field("detail") String detail,
											@Field("detail_type") int detail_type,
											@Field("goods_img") String goods_img,
											@Field("goods_attr") String goods_attr,
											@Field("shipping_tpl_id") int shipping_tpl_id,
											@Field("is_on_sale") int is_on_sale,
											@Field("goods_user_cat_id") int goods_user_cat_id);

	/**
	 * 添加商品
	 */
	@POST("supplier/saveGoodsEdit")
	@FormUrlEncoded
	Call<BaseResponse<String>> editGoodsPost(@Header("X-AUTH-TOKEN") String token,
										    @Field("goods_id") int goods_id,
											@Field("cat_id") int cat_id,
											@Field("goods_name") String goods_name,
											@Field("profit") int profit,
											@Field("detail") String detail,
											@Field("detail_type") int detail_type,
											@Field("goods_img") String goods_img,
											@Field("goods_attr") String goods_attr,
											@Field("shipping_tpl_id") int shipping_tpl_id,
											@Field("is_on_sale") int is_on_sale,
											@Field("goods_user_cat_id") int goods_user_cat_id);

	/**
	 * 获取商品列表
	 */
	@POST("supplier/delGoodsBat")
	@FormUrlEncoded
	Call<BaseResponse<String>> delGoodsBat(@Header("X-AUTH-TOKEN") String token, @Field("goods_id") String goods_id);

	/**
	 * 下架商品
	 */
	@POST("supplier/downGoodsBat")
	@FormUrlEncoded
	Call<BaseResponse<String>> downGoodsBat(@Header("X-AUTH-TOKEN") String token, @Field("goods_id") String goods_id);

	/**
	 * 上架商品
	 */
	@POST("supplier/upGoodsBat")
	@FormUrlEncoded
	Call<BaseResponse<String>> upGoodsBat(@Header("X-AUTH-TOKEN") String token, @Field("goods_id") String goods_id);

	/**
	 * 获取验证码
	 */
	@POST("forgetSecret/getCode")
	@FormUrlEncoded
	Call<BaseResponse<String>> getCode(@Header("X-AUTH-TOKEN") String token, @Field("mobile") String mobile);

	/**
	 * 验证支付密码
	 */
	@POST("supplier/verifyPayPassword")
	@FormUrlEncoded
	Call<BaseResponse<String>> verifyPayPassword(@Header("X-AUTH-TOKEN") String token, @Field("verifySecret") String verifySecret);

	/**
	 * 获取支付验证码
	 */
	@POST("supplier/getVerifyCode")
	@FormUrlEncoded
	Call<BaseResponse<String>> getVerifyCode(@Header("X-AUTH-TOKEN") String token, @Field("type") int type);

	/**
	 * 修改密码
	 */
	@POST("supplier/getBinkBankToken")
	@FormUrlEncoded
	Call<BaseResponse<Object>> getBinkBankToken(@Header("X-AUTH-TOKEN") String token, @Field("code") String code);
	/**
	 * 修改密码
	 */
	@POST("forgetSecret/getResetPwdToken")
	@FormUrlEncoded
	Call<BaseResponse<String>> getResetPwdToken(@Header("X-AUTH-TOKEN") String token, @Field("mobile") String mobile, @Field("code") String code);



	/**
	 * 修改密码
	 */
	@POST("api/getResetPayPwdToken")
	@FormUrlEncoded
	Call<BaseResponse<String>> getResetPayPwdToken(@Header("X-AUTH-TOKEN") String token, @Field("mobile") String mobile, @Field("code") String code);
	/**
	 * 修改密码
	 */
	@POST("forgetSecret/resetPwdPost")
	@FormUrlEncoded
	Call<BaseResponse<String>> resetPwdPost(@Header("X-AUTH-TOKEN") String token, @Field("mobile") String mobile, @Field("reset_token") String reset_token, @Field("password") String password);

	/**
	 * 修改密码
	 */
	@POST("api/resetPayPwdPost")
	@FormUrlEncoded
	Call<BaseResponse<String>> resetPayPwdPos(@Header("X-AUTH-TOKEN") String token, @Field("mobile") String mobile, @Field("reset_token") String reset_token, @Field("password") String password);

	/**
	 * 修改密码
	 */
	@POST("supplier/getProfitIndex")
	Call<BaseResponse<EarnBase>> getEarnIngInfo(@Header("X-AUTH-TOKEN") String token);

	/**
	 * 获取兑换记录
	 */
	@POST("api/shopExchangeLog")
	@FormUrlEncoded
	Call<BaseResponse<List<ForRecordBase>>> getEarnIngRecord(@Header("X-AUTH-TOKEN") String token,@Field("page") String page);

	/**
	 * 修改密码
	 */
	@POST("supplier/getPraiseRecord")
	Call<BaseResponse<GreatDetailBase>> getshopPraiseDetail(@Header("X-AUTH-TOKEN") String token);

	/**
	 * 修改密码
	 */
	@POST("supplier/getCanCash")
	Call<BaseResponse<Convertibility>> getCanCash(@Header("X-AUTH-TOKEN") String token);

	/**
	 * 修改密码
	 */
	@POST("supplier/getGoodsDetail")
	@FormUrlEncoded
	Call<BaseResponse<EditProduct>> getGoodsDetail(@Header("X-AUTH-TOKEN") String token, @Field("goods_id") int goods_id);

	/**
	 * 关闭订单
	 */
	@POST("supplier/orderGoodsClose")
	@FormUrlEncoded
	Call<BaseResponse<String>> orderClose(@Header("X-AUTH-TOKEN") String token, @Field("trade_id") int trade_id);

	/**
	 * 关闭订单
	 */
	@POST("supplier/deliveryQuery")
	@FormUrlEncoded
	Call<BaseResponse<Object>> deliveryQuery(@Header("X-AUTH-TOKEN") String token, @Field("delivery_id") String delivery_id);

	/**
	 * 修改密码
	 */
	@POST("supplier/deliveryRecord")
	@FormUrlEncoded
	Call<BaseResponse<String>> deliveryRecord(@Header("X-AUTH-TOKEN") String token, @Field("trade_id") String trade_id, @Field("delivery_no") String delivery_no);

	/**
	 * 修改密码
	 */
	@POST("supplier/setPayPawword")
	@FormUrlEncoded
	Call<BaseResponse<String>> setPayPawword(@Header("X-AUTH-TOKEN") String token, @Field("trade_id") int trade_id);


	/**
	 * 绑定银行卡
	 */
	@POST("supplier/bindBankSubmit")
	@FormUrlEncoded
	Call<BaseResponse<String>> bindBankSubmit(@Header("X-AUTH-TOKEN") String token, @Field("bank_token") String bank_token,
											  @Field("name") String name,
											  @Field("bank_card") String bank_card,
											  @Field("bank_pic") String bank_pic,
											  @Field("bankname") String bankname,
											  @Field("bank_code") String bank_code,
											  @Field("deposit_bank") String deposit_bank);
	/**
	 * 绑定银行卡
	 */
	@POST("supplier/canCashExchange")
	@FormUrlEncoded
	Call<BaseResponse<String>> canCashExchange(@Header("X-AUTH-TOKEN") String token,
											  @Field("bankId") String bankId,
											  @Field("money") String money,
											  @Field("paypwd") String paypwd);

	/**
	 * 获取录单信息
	 */
	@POST("supplier/getRecordOrderInfo")
	Call<BaseResponse<BillRecord>> getRecordOrderInfo(@Header("X-AUTH-TOKEN") String token);

	/**
	 * 提交录单信息
	 */
	@POST("supplier/recordOrderSubmit")
	@FormUrlEncoded
	Call<BaseResponse<Object>> recordOrderSubmit(@Header("X-AUTH-TOKEN") String token,
												  @Field("mobile") String mobile,
												  @Field("money") String money,
												  @Field("profit") String profit);

	/**
	 * 录单支付
	 */
	@POST("supplier/getPayParam")
	@FormUrlEncoded
	Call<BaseResponse<String>> getPayParam(@Header("X-AUTH-TOKEN") String token,
												 @Field("pay_id") String pay_id,
												 @Field("pay_type") String pay_type);
	/**
	 * 收款二维码
	 */
	@POST("supplier/chargeQRCode")
	@FormUrlEncoded
	Call<BaseResponse<ChargeQRCod>> chargeQRCode(@Header("X-AUTH-TOKEN") String token,
												 @Field("profit") String profit,
												 @Field("username") String username);

	/**
	 * 获取消息列表
	 */
	@POST("supplier/myMessage")
	@FormUrlEncoded
	Call<BaseResponse<MessageBase>> myMessage(@Header("X-AUTH-TOKEN") String token,
											  @Field("msg_type") String msg_type,
											  @Field("page") String page);

	/**
	 * 获取消息列表
	 */
	@POST("supplier/profitAvailable")
	Call<BaseResponse<String>> profitAvailable(@Header("X-AUTH-TOKEN") String token);

	/**
	 * 获取赠送类型
	 */
	@POST("supplier/getTransfer")
	Call<BaseResponse<BalanceBase>> getTransfer(@Header("X-AUTH-TOKEN") String token);

	/**
	 * z赠送攒分提交
	 */
	@POST("supplier/transferSubmit")
	@FormUrlEncoded
	Call<BaseResponse<String>> transferSubmit(@Header("X-AUTH-TOKEN") String token,
											  @Field("type") String type,
											  @Field("money") String money,
											  @Field("mobile") String mobile,
											  @Field("pay_pwd") String pay_pwd);

	/**
	 * 获取赠送记录
	 */
	@POST("supplier/transferRecord")
	@FormUrlEncoded
	Call<BaseResponse<GivingRecordsBase>> transferRecord(@Header("X-AUTH-TOKEN") String token,
														 @Field("type") String type,
														 @Field("page") String page);

	/**
	 * 获取线下订单详情
	 */
	@POST("supplier/orderLineDetail")
	@FormUrlEncoded
	Call<BaseResponse<OrderDownDetail>> orderLineDetail(@Header("X-AUTH-TOKEN") String token,
														@Field("pay_id") int pay_id);

}
