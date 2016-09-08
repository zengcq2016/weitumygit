package com.libtop.weitu.activity.main.subsubject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.ContentFragment;
import com.libtop.weitu.activity.main.adapter.SubjectFileAdapter;
import com.libtop.weitu.activity.search.BookDetailFragment;
import com.libtop.weitu.activity.search.dto.BookDto;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.widget.listview.XListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemClick;
import okhttp3.Call;


/**
 * Created by Zeng on 2016/9/8
 */
public class SelectSubjectFragment extends ContentFragment {

    @Bind(R.id.back_btn)
    ImageView backBtn;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.commit)
    TextView commit;
    @Bind(R.id.title_container)
    LinearLayout titleContainer;
    @Bind(R.id.select_sub_list)
    ListView selectSubList;

    private List<BookDto> listBooks = new ArrayList<BookDto>();
    private SubjectFileAdapter mAdapter;
    private int mCurPage = 1;
    private boolean hasData = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new SubjectFileAdapter(mContext, listBooks);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_select_subject;
    }


    @Override
    public void onCreation(View root) {
        initView();
        loadRmdBooks();
    }


    private void initView() {
        title.setText("选择主题");
        selectSubList.setAdapter(mAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Nullable
    @OnClick({R.id.back_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }


    @Nullable
    @OnItemClick(value = R.id.select_sub_list)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //显示图书详情
        BookDto dto = listBooks.get(position - 1);

        ContantsUtil.UPDATE_HISTORY = false;
        Bundle bundle = new Bundle();
        bundle.putString("name", dto.title);
        bundle.putString("cover", dto.cover);
        bundle.putString("auth", dto.author);
        bundle.putString("isbn", dto.isbn);
        bundle.putString("publisher", dto.publisher);
        bundle.putString("school", Preference.instance(mContext).getString(Preference.SchoolCode));
        bundle.putBoolean("isFromMainPage", true);
        bundle.putString(ContentActivity.FRAG_CLS, BookDetailFragment.class.getName());
        bundle.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
        bundle.putBoolean(ContentActivity.FRAG_ISBACK, true);
        mContext.startActivity(bundle, ContentActivity.class);
    }


    private void loadRmdBooks() {
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", mCurPage);
        params.put("method", "book.listRecommend");
        params.put("lid", mPreference.getString(Preference.SchoolCode));
        HttpRequest.loadWithMapSec(params, new HttpRequest.CallBackSec() {
            @Override
            public void onError(Call call, Exception e, int id) {
                dismissLoading();
            }


            @Override
            public void onResponse(String json, int id) {
                dismissLoading();
                if (TextUtils.isEmpty(json)) {
                    showToast("没有相关数据");
                    return;
                }
                try {
                    listBooks.clear();
                    JSONArray array = new JSONArray(json);
                    for (int i = 0; i < array.length(); i++) {
                        BookDto bean = new BookDto();
                        bean.of(array.getJSONObject(i));
                        bean.title.replaceAll("　　", "").trim();
                        listBooks.add(bean);
                    }
                    mAdapter.setData(listBooks);
                    mAdapter.notifyDataSetChanged();
                    if (listBooks.size() == 0) {
                        showToast("没有相关数据");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("Json数据解析错误");
                }
            }
        });
    }
}