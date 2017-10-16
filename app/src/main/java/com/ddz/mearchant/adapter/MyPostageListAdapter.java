package com.ddz.mearchant.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ddz.mearchant.BaseApplication;
import com.ddz.mearchant.R;
import com.ddz.mearchant.api.APIService;
import com.ddz.mearchant.api.RetrofitWrapper;
import com.ddz.mearchant.bean.BaseResponse;
import com.ddz.mearchant.config.Constants;
import com.ddz.mearchant.dialog.LoadingDialog;
import com.ddz.mearchant.http.HttpCallBack;
import com.ddz.mearchant.models.Postage;
import com.ddz.mearchant.view.HandyTextView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;


public class MyPostageListAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private ArrayList<Postage> postages = new ArrayList<Postage>();
    private ProgressDialog progressDialog;
    private int total = 0;
    int deletePostion = -1;
    private LoadingDialog dialog;
    private BaseApplication baseApplication = BaseApplication.getInstance();
    public MyPostageListAdapter(Context context) {
        mContext = context;
    }

    public void setList(ArrayList<Postage> postages) {
        this.postages = postages;
    }



    
    public int getGroupCount() {
        return postages.size();
    }

    
    public int getChildrenCount(int groupPosition) {
        return postages.get(groupPosition).getArea_list().size();
    }

    
    public Object getGroup(int groupPosition) {
        return postages.get(groupPosition);
    }

    
    public Object getChild(int groupPosition, int childPosition) {
        return postages.get(groupPosition).getArea_list().get(childPosition);
    }

    
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    
    public boolean hasStableIds() {
        return false;
    }
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder holder = null;
        if (convertView == null) {
            holder = new GroupViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_postage_parent_list, parent, false);
            holder.postageName = (HandyTextView) convertView.findViewById(R.id.postage_name);
            holder.postageDelete = (LinearLayout) convertView.findViewById(R.id.postage_delete_linear);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }
        holder.postageName.setText(postages.get(groupPosition).getShipping_name());
        holder.postageName.setTag(groupPosition);
        holder.postageDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePostige(groupPosition);
            }
        });
        return convertView;
    }
    private void initDialog() {
        dialog = new LoadingDialog(mContext, R.style.dialog, "请稍候...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }
    private void deletePostige(final int deletePostion){
        initDialog();
        APIService userBiz = RetrofitWrapper.getInstance().create(
                APIService.class);
        Call<BaseResponse<String>> call = userBiz.deleteShippingTpl(baseApplication.mUser.token,postages.get(deletePostion).getShipping_tpl_id());//"18813904075:123456789"
        call.enqueue(new HttpCallBack<BaseResponse<String>>() {

            @Override
            public void onResponse(Call<BaseResponse<String>> arg0,
                                   Response<BaseResponse<String>> response) {
                if (dialog.isShowing()){dialog.dismiss();}
                super.onResponse(arg0,response);
                BaseResponse<String> baseResponse = response.body();
                if (null != baseResponse && baseResponse.getStatus().equals(Constants.T_OK)) {
                    String status = baseResponse.getStatus();
                    String data = baseResponse.getData();
//                    for (int i = 0; i<postages.get(deletePostion).getArea_list().size();i++){
//                        postages.get(deletePostion).getArea_list().remove(i);
//                    }
                    if (postages.size()>0){
                        postages.remove(postages.get(deletePostion));
                        notifyDataSetChanged();
                    }
                    if (postages.size()==0){
                        onDeleterListener.getProductSize(postages.size());
                    }
                    Toast.makeText(mContext,data,Toast.LENGTH_SHORT).show();
                }else{
                    String status = baseResponse.getStatus();
                    String data = baseResponse.getData();
                    Toast.makeText(mContext,data,Toast.LENGTH_SHORT).show();
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
    /**
     * child view
     */
    
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder holder = null;
        if (convertView == null) {
            holder = new ChildViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_postage_list, parent, false);
			holder.postagePrice = (HandyTextView) convertView.findViewById(R.id.postage_price);
			holder.postageAddress = (HandyTextView) convertView.findViewById(R.id.postage_address);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        holder.postagePrice.setText(postages.get(groupPosition).getArea_list().get(childPosition).getPrice());
        holder.postageAddress.setText(postages.get(groupPosition).getArea_list().get(childPosition).getRegion_str());
        return convertView;
    }


    class GroupViewHolder {
        HandyTextView postageName;
        LinearLayout postageDelete;
    }

    class ChildViewHolder {
		HandyTextView postagePrice;
		HandyTextView postageAddress;
    }

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}
    private OnDeleterListener onDeleterListener;
	public void setOnDeleterListener(OnDeleterListener onDeleterListener) {
        this.onDeleterListener = onDeleterListener;
    }
    public static interface OnDeleterListener{
        void getProductSize(int size);
    }
}
