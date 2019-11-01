package com.wray2.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wray2.Class.Rubbish;
import com.wray2.CustomComponent.FlowLayout;
import com.wray2.FragmentsActivity;
import com.wray2.R;
import com.wray2.RecyclerViewAdapter.RecyclerViewItemTouchListener;
import com.wray2.RecyclerViewAdapter.SearchGarbageRelativeListAdapter;
import com.wray2.SearchResultActivity;
import com.wray2.Thread.JsonDataObjects.ErrorData;
import com.wray2.Thread.JsonDataObjects.SearchbackData;
import com.wray2.Thread.RelativeSearchThreadRunnable;
import com.wray2.Util.ThemeUtils;
import com.wray2.Util.mValsUtils;

import java.util.LinkedList;

public class SearchFragment extends Fragment
{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private FragmentsActivity activity;

    private EditText searchEditText;
    private FlowLayout mFlowLayout;
    private ImageView search;
    private LayoutInflater mInflater;
    private LinkedList<String> mVals = new LinkedList<>();
    private ImageView delete;
    private RecyclerView relativeList;

    //RecyclerView的数据源
    private LinkedList<Rubbish> searchRubData = new LinkedList<>();
    public static String feedbackId = "";

    private SearchGarbageRelativeListAdapter searchGarbageRelativeListAdapter;

    //服务器返回的数据对象
    private SearchbackData searchfeedbackData;

    public SearchFragment()
    {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(String param1, String param2)
    {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), ThemeUtils.getThemeId(activity));
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.fragment_search_garbage, container, false);
        //布局创建
        searchEditText = (EditText)view.findViewById(R.id.editxt_search_garbage);
        mFlowLayout = (FlowLayout)view.findViewById(R.id.flowlayout_history);
        search = (ImageView)view.findViewById(R.id.img_card_search);
        mInflater = LayoutInflater.from(view.getContext());
        delete = (ImageView)view.findViewById(R.id.img_search_deletehistory);
        relativeList = (RecyclerView)view.findViewById(R.id.search_relativeList);
        initData();

        delete.setOnClickListener(v ->
        {
            mValsUtils.deletemValsList(activity);
            initData();
        });

        searchEditText.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (keyCode == android.view.KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)
                {
                    String string = searchEditText.getText().toString();
                    if (!string.isEmpty() && searchRubData.size() != 0)
                    {
                        Intent intent = new Intent(activity, SearchResultActivity.class);
                        intent.putExtra("rubbishInfo", searchRubData.get(0));
                        startActivity(intent);
                        mValsUtils.upDatemValsList(activity, searchRubData.get(0).getRubbishName());
                    }
                    else if (string.isEmpty())
                        searchEditText.setHint("查询名称不可以为空哦~");
                    else if (searchRubData.size() == 0)
                    {
                        searchEditText.setText("");
                        searchEditText.setHint("没有找到相关结果QAQ~");
                    }
                    return true;
                }
                return false;
            }
        });

        searchEditText.setOnClickListener(v ->
        {
            //searchEditText.setHint("");
            relativeList.setVisibility(View.GONE);
        });

        searchEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.length() != 0)
                {
                    search.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (s.length() == 0)
                {
                    searchEditText.setHint("搜索垃圾");
                    clearList();
                    relativeList.setVisibility(View.GONE);
                }
                else showListView();
            }
        });

        searchGarbageRelativeListAdapter = new SearchGarbageRelativeListAdapter(activity, searchRubData);
        relativeList.setLayoutManager(new LinearLayoutManager(activity));
        relativeList.setAdapter(searchGarbageRelativeListAdapter);
        RecyclerViewItemTouchListener recyclerViewItemTouchListener = new RecyclerViewItemTouchListener(activity, new RecyclerViewItemTouchListener.OnRecyclerItemClickListener.Builder()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                Intent intent = new Intent(activity, SearchResultActivity.class);
                intent.putExtra("rubbishInfo", searchRubData.get(position));
                startActivity(intent);
                mValsUtils.upDatemValsList(activity, searchRubData.get(position).getRubbishName());
            }
        });
        relativeList.addOnItemTouchListener(recyclerViewItemTouchListener);

        return view;
    }

    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        activity = (FragmentsActivity)context;
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener)context;
        }
        else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume()
    {
        initData();
        super.onResume();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause()
    {
        activity.hideIME();
        searchEditText.clearFocus();
        searchEditText.setText("");
        clearList();
        relativeList.setVisibility(View.GONE);
        search.setVisibility(View.VISIBLE);
        super.onPause();
    }

    public interface OnFragmentInteractionListener
    {
        void onFragmentInteraction(Uri uri);
    }

    public void initData()
    {
        mFlowLayout.removeAllViews();
        searchEditText.setText("");
        mVals = mValsUtils.readmValsList(activity);
        @SuppressLint("HandlerLeak")
        Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if (msg.what == 1)
                {
                    Bundle bundle = msg.getData();
                    searchfeedbackData = bundle.getParcelable("search_data");
                    searchRubData.clear();
                    searchRubData = searchfeedbackData.getResultList();
                    Intent intent = new Intent(getActivity(), SearchResultActivity.class);
                    intent.putExtra("rubbishInfo", searchRubData.get(0));
                    startActivity(intent);
                }
                else if (msg.what == -1)
                {
                    Bundle bundle = msg.getData();
                    ErrorData errorData = bundle.getParcelable("search_error_data");
                }
            }
        };
        for (int i = 0; i < mVals.size(); i++)
        {
            TextView tv = (TextView)mInflater.inflate(R.layout.searched_lable_tv, mFlowLayout, false);
            tv.setText(mVals.get(i));
            //点击事件
            tv.setOnClickListener(v ->
            {
                String str = tv.getText().toString();
                RelativeSearchThreadRunnable relativeSearchThreadRunnable = new RelativeSearchThreadRunnable(handler, str);
                Thread thread = new Thread(relativeSearchThreadRunnable);
                thread.start();
            });
            mFlowLayout.addView(tv);//添加到父View
        }
    }

    public boolean isRename(String str, String[] mVals)
    {
        for (int i = 0; i < mVals.length; i++)
        {
            if (str.equals(mVals[i]))
            {
                return true;
            }
        }
        return false;
    }

    private void showListView()
    {
        relativeList.setVisibility(View.VISIBLE);
        String str = searchEditText.getText().toString();
        @SuppressLint("HandlerLeak")
        Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if (msg.what == 1)
                {
                    Bundle bundle = msg.getData();
                    searchfeedbackData = bundle.getParcelable("search_data");
                    searchRubData.clear();
                    searchRubData = searchfeedbackData.getResultList();
                    searchGarbageRelativeListAdapter.setList(searchRubData);
                    relativeList.getAdapter().notifyDataSetChanged();
                }
                else if (msg.what == -1)
                {
                    Bundle bundle = msg.getData();
                    ErrorData errorData = bundle.getParcelable("search_error_data");
                }
            }
        };
        RelativeSearchThreadRunnable relativeSearchThreadRunnable = new RelativeSearchThreadRunnable(handler, str);
        Thread thread = new Thread(relativeSearchThreadRunnable);
        thread.start();
    }

    private void clearList()
    {
        searchRubData.clear();
        searchGarbageRelativeListAdapter.setList(searchRubData);
        relativeList.getAdapter().notifyDataSetChanged();
    }
}
