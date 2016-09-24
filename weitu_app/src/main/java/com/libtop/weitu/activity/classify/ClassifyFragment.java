package com.libtop.weitu.activity.classify;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.classify.adapter.ClassifyAdapter;
import com.libtop.weitu.activity.classify.bean.ClassifyBean;
import com.libtop.weitu.activity.search.SearchActivity;
import com.libtop.weitu.base.BaseFragment;
import com.libtop.weitu.http.MapUtil;
import com.libtop.weitu.http.WeituNetwork;
import com.libtop.weitu.utils.ACache;
import com.libtop.weitu.widget.NetworkLoadingLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by LianTu on 2016/7/19.
 */
public class ClassifyFragment extends BaseFragment implements NetworkLoadingLayout.OnRetryClickListner
{

    @Bind(R.id.networkloadinglayout)
    NetworkLoadingLayout networkLoadingLayout;
    @Bind(R.id.list)
    ListView listView;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.back_btn)
    ImageView backBtn;

    private ACache mCache;
    private boolean isFirstIn = true;

    private ClassifyAdapter mAdapter;
    private List<ClassifyBean> mData = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mAdapter = new ClassifyAdapter(mContext, mData);
        mCache = ACache.get(mContext);
    }


    @Override
    protected int getLayoutId()
    {
        return R.layout.fragment_classify2;
    }


    @Override
    public void onCreation(View root)
    {
        initView();
        getData();
    }

    @Override
    public void onBackPressed()
    {
        mContext.finish();
    }

    private void getData()
    {
        final List<ClassifyBean> classifyBeensList = (List<ClassifyBean>) mCache.getAsObject("classifyBeens");
        if(classifyBeensList!=null && !classifyBeensList.isEmpty()){
            handleResult(classifyBeensList);
            networkLoadingLayout.dismiss();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("method", "categories.root");
        String[] arrays = MapUtil.map2Parameter(map);
        subscription = WeituNetwork.getWeituApi().getClassify(arrays[0], arrays[1], arrays[2]).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<ClassifyBean>>()
        {
            @Override
            public void onCompleted()
            {

            }


            @Override
            public void onError(Throwable e)
            {
                if(classifyBeensList!=null){

                }else{
                    networkLoadingLayout.showLoadFailAndRetryPrompt();
                }
            }


            @Override
            public void onNext(List<ClassifyBean> classifyBeens)
            {
                networkLoadingLayout.dismiss();
                mCache.put("classifyBeens", (Serializable) classifyBeens);
                if (classifyBeens.size() == 0 && classifyBeensList==null) {
                    networkLoadingLayout.showEmptyPrompt();
                }
                handleResult(classifyBeens);
            }
        });
    }


    private void handleResult(List<ClassifyBean> classifyBeens)
    {
        mData.clear();
        if (classifyBeens == null && classifyBeens.isEmpty())
        {
            return;
        }
        mData = classifyBeens;
        mAdapter.setData(mData);
        mAdapter.notifyDataSetChanged();
    }


    private void initView()
    {
        if (isFirstIn)
        {
            isFirstIn = false;
            networkLoadingLayout.showLoading();
            getData();
        }
        title.setText("分类");
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                ClassifyBean classifyBean = mData.get(position);
                Intent intent = new Intent(mContext, ClassifyDetailActivity.class);
                intent.putExtra("code", classifyBean.code);
                intent.putExtra("name", classifyBean.name);
                startActivity(intent);
            }
        });
        networkLoadingLayout.setOnRetryClickListner(this);
    }


    @Nullable
    @OnClick({R.id.back_btn})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.back_btn:
                mContext.finish();
                break;
        }
    }


    private void searchClick()
    {
        mContext.startActivity(null, SearchActivity.class);
    }

    @Override
    public void onRetryClick(View v) {
        getData();
    }
}
