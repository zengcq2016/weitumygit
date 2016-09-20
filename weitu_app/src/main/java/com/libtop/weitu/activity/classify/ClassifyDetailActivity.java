package com.libtop.weitu.activity.classify;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ListPopupWindow;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.classify.adapter.ClassifyCheckAdapter;
import com.libtop.weitu.activity.classify.adapter.ClassifyDetailAdapter;
import com.libtop.weitu.activity.classify.bean.ClassifyBean;
import com.libtop.weitu.activity.classify.bean.ClassifyDetailBean;
import com.libtop.weitu.activity.classify.bean.ClassifyResultBean;
import com.libtop.weitu.activity.search.SearchActivity;
import com.libtop.weitu.activity.search.adapter.MainPageAdapter;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.http.MapUtil;
import com.libtop.weitu.http.WeituNetwork;
import com.libtop.weitu.widget.NoSlideViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnPageChange;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by LianTu on 2016/7/20.
 */
public class ClassifyDetailActivity extends BaseActivity
{

    @Bind(R.id.title)
    TextView mTitleText;
    @Bind(R.id.tv_sort)
    TextView mSubTitleText;
    @Bind(R.id.titlebar)
    View titleBar;
    @Bind(R.id.img_arrow)
    ImageView imgArrow;
    @Bind(R.id.radioGroup)
    RadioGroup radioGroup;
    @Bind(R.id.viewpager)
    NoSlideViewPager mViewPager;

    private List<Fragment> mFrags;
    private MainPageAdapter mPageAdapter;
    private int pageIndex = 0;

    private final int TYPE_SUBJECT = 0;
    private final int TYPE_RESOURCE = 1;

    private int mCurPage = 1;
    private ClassifyDetailAdapter mAdapter;
    private List<ClassifyResultBean> mData;
    private ListPopupWindow mListPop, mListFilterPop;
    private boolean hasData = false;
    private boolean isFirstIn = true;
    private boolean isRefreshed = false;

    private long code, subCode;
    private String filterString = "view";

    private List<ClassifyBean> lists = new ArrayList<ClassifyBean>();
    private ClassifyCheckAdapter filterCheckAdapter, classifyCheckAdapter;
    private ClassifyDetailFragment classifyDetailFragment;

    public static final String VIDEO = "video-album", AUDIO = "audio-album", DOC = "document", PHOTO = "image-album", BOOK = "book";


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_classify_detail);
        code = mContext.getIntent().getExtras().getLong("code");
        String name = getIntent().getExtras().getString("name");
        mTitleText.setText(name);
        mData = new ArrayList<ClassifyResultBean>();
        mAdapter = new ClassifyDetailAdapter(mContext, mData);
        classifyDetailFragment=new ClassifyDetailFragment();
        initView();
        initPopView();
//        initListView();
        getData();
    }

    private void initView(){
        mFrags = new ArrayList<Fragment>();
        ClassifyDetailFragment f1 = new ClassifyDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", "subject");
        bundle.putString("api", "/category/subject/list");
        f1.setArguments(bundle);

        ClassifyDetailFragment f2 = new ClassifyDetailFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putString("type", "resource");
        bundle2.putString("api", "/category/resource/list");
        f2.setArguments(bundle2);

        mFrags.add(f1);
        mFrags.add(f2);

        mPageAdapter = new MainPageAdapter(getSupportFragmentManager(), mFrags);

        mViewPager.setPagingEnabled(true);
        mViewPager.setAdapter(mPageAdapter);
        mViewPager.setCurrentItem(0);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                onCheckedChangedAction(group, checkedId);
            }
        });
    }

    @Nullable
    @OnPageChange(value = R.id.viewpager)
    public void onPageSelected(int position)
    {
        switch (position)
        {
            case TYPE_SUBJECT:
                radioGroup.check(R.id.subject);
                break;
            case TYPE_RESOURCE:
                radioGroup.check(R.id.resource);
                break;
        }
    }


    public void onCheckedChangedAction(RadioGroup group, int checkedId) {
        pageIndex = -1;
        switch (checkedId) {
            case R.id.subject: {
                pageIndex = TYPE_SUBJECT;
            }
            break;
            case R.id.resource: {
                pageIndex = TYPE_RESOURCE;
            }
            break;
        }
        mViewPager.setCurrentItem(pageIndex);
    }


    private void initPopView()
    {
        classifyCheckAdapter = new ClassifyCheckAdapter(mContext, lists, false);
        mListPop = new ListPopupWindow(this);
        mListPop.setAdapter(classifyCheckAdapter);
        mListPop.setWidth(ListPopupWindow.MATCH_PARENT);
        mListPop.setHeight(ListPopupWindow.WRAP_CONTENT);
        mListPop.setAnchorView(titleBar);//设置ListPopupWindow的锚点，即关联PopupWindow的显示位置和这个锚点
        mListPop.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mListPop.setModal(true);//设置是否是模式
        mListPop.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                mSubTitleText.setText(lists.get(position).name);
                subCode = lists.get(position).code;
                classifyCheckAdapter.setCheck(position);
                mCurPage = 1;
                getData();
                mListPop.dismiss();
            }
        });
        mListPop.setOnDismissListener(new PopupWindow.OnDismissListener()
        {
            @Override
            public void onDismiss()
            {
                imgArrow.setImageResource(R.drawable.arrow_down);
            }
        });


        String[] filters = new String[]{"综合", "浏览数最多", "评论数最多", "收藏数最多", "最新上传"};
        List<ClassifyBean> filterList = new ArrayList<>();
        for (int i = 0; i < filters.length; i++)
        {
            ClassifyBean classifyBean = new ClassifyBean();
            classifyBean.name = filters[i];
            filterList.add(classifyBean);
        }
        filterCheckAdapter = new ClassifyCheckAdapter(mContext, filterList, true);
        mListFilterPop = new ListPopupWindow(this);
        mListFilterPop.setAdapter(filterCheckAdapter);
        mListFilterPop.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        mListFilterPop.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        mListFilterPop.setAnchorView(titleBar);//设置ListPopupWindow的锚点，即关联PopupWindow的显示位置和这个锚点
        mListFilterPop.setBackgroundDrawable(new ColorDrawable(0x99000000));
        mListFilterPop.setModal(true);//设置是否是模式
        mListFilterPop.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                setFilter(position);
                filterCheckAdapter.setCheck(position);
                mCurPage = 1;
                getData();
                mListFilterPop.dismiss();
            }
        });
    }


    private void setFilter(int position)
    {
        switch (position)
        {
            //综合
            case 0:
                filterString = "view";
                break;
            //浏览数最多
            case 1:
                filterString = "view";
                break;
            //评论数最多
            case 2:
                filterString = "comment";
                break;
            //收藏数最多 TODO bug?
            case 3:
                filterString = "comment";
                break;
            //最新上传
            case 4:
                filterString = "timeline";
                break;
        }
    }


    private void getData()
    {
        //        http://weitu.bookus.cn/search/categories.json?text={"label1":100000,"label2":0,"sort":"favorite","page":1,"method":"search.categories"}
        unsubscribe();
        Map<String, Object> map = new HashMap<>();
        map.put("label1", code);
        map.put("label2", subCode);
        map.put("sort", filterString);
        map.put("page", mCurPage);
        map.put("method", "search.categories");
        String[] arrays = MapUtil.map2Parameter(map);
        subscription = WeituNetwork.getWeituApi().getClassifyDetail(arrays[0], arrays[1], arrays[2]).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ClassifyDetailBean>()
        {
            @Override
            public void onCompleted()
            {

            }


            @Override
            public void onError(Throwable e)
            {
                if (mData.isEmpty() && !isRefreshed)
                {
//                    networkLoadingLayout.showLoadFailAndRetryPrompt();
//                    mListView.setVisibility(View.GONE);
                }
            }


            @Override
            public void onNext(ClassifyDetailBean classifyDetailBean)
            {
//                mListView.stopRefresh();
                lists.clear();
                int size = classifyDetailBean.categories.subCategories.size();
                ClassifyBean classifyBean = new ClassifyBean();
                classifyBean.name = "全部";
                classifyBean.code = 0;
                classifyBean.countString = classifyDetailBean.categories.subCategories.get(0).name + "等" + size + "类";
                lists.add(classifyBean);
                for (ClassifyBean classifyBean1 : classifyDetailBean.categories.subCategories)
                {
                    classifyBean1.countString = "共" + classifyBean1.count + "种资源";
                }
                lists.addAll(classifyDetailBean.categories.subCategories);
                classifyCheckAdapter.setData(lists);
                classifyCheckAdapter.notifyDataSetChanged();
                if (mCurPage == 1)
                {
                    mData.clear();
                }
                mData.addAll(classifyDetailBean.result);
                if (classifyDetailBean.result.size() < 10)
                {
                    hasData = false;
//                    mListView.setPullLoadEnable(false);
                }
                else
                {
                    hasData = true;
//                    mListView.setPullLoadEnable(true);
                }
                mCurPage++;
                if (mData.isEmpty())
                {
//                    networkLoadingLayout.showEmptyPrompt();
//                    mListView.setVisibility(View.GONE);
//                    classifyDetailFragment.getInstance().showEmptyPage();
                }
                else
                {
//                    networkLoadingLayout.dismiss();
//                    mListView.setVisibility(View.VISIBLE);
//                    classifyDetailFragment.getInstance().dismissEmptyPage();
                    mAdapter.setNewData(mData);
                }
            }
        });
    }


    @Nullable
    @OnClick({R.id.back_btn, R.id.commit, R.id.img_search, R.id.search_filter, R.id.title})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.back_btn:
                mContext.finish();
                break;
            case R.id.img_search:
                searchClick();
                break;
            case R.id.search_filter:
                searchFilterClick();
                break;
            case R.id.title:
                titleClick(v);
                break;
        }
    }


    //标题点击更改分类
    private void titleClick(View v)
    {
        imgArrow.setImageResource(R.drawable.arrow_up);
        mListPop.show();
    }


    //更改筛选
    private void searchFilterClick()
    {
        mListFilterPop.show();
    }


    //点击搜索
    private void searchClick()
    {
        mContext.startActivity(null, SearchActivity.class);
    }



    @Override
    public void onBackPressed()
    {
        mContext.finish();
    }

}
