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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.wray2.Class.Rubbish;
import com.wray2.CustomComponent.FlowLayout;
import com.wray2.FragmentsActivity;
import com.wray2.R;
import com.wray2.RecyclerViewAdapter.RecyclerViewItemTouchListener;
import com.wray2.RecyclerViewAdapter.SearchGarbageRelativeListAdapter;
import com.wray2.SearchResultActivity;
import com.wray2.Thread.JsonDataObjects.ErrorData;
import com.wray2.Thread.JsonDataObjects.FeedbackData;
import com.wray2.Thread.RelativeSearchThreadRunnable;
import com.wray2.Util.mValsUtils;

import java.util.ArrayList;
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

    //服务器返回的数据对象
    private FeedbackData searchfeedbackData = new FeedbackData("", "", new LinkedList<Rubbish>());
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
        View view = inflater.inflate(R.layout.fragment_search_garbage, container, false);
        //布局创建
        searchEditText = (EditText)view.findViewById(R.id.editxt_search_garbage);
        mFlowLayout = (FlowLayout) view.findViewById(R.id.flowlayout_history);
        search =(ImageView)view.findViewById(R.id.img_card_search);
        mInflater = LayoutInflater.from(view.getContext());
        delete = (ImageView)view.findViewById(R.id.img_search_deletehistory);
        relativeList = (RecyclerView) view.findViewById(R.id.search_relativeList);
        initData();
        search.setOnClickListener(v ->
        {
            if (!searchEditText.getText().toString().isEmpty()) { //不可以getText != null,返回类型是Editable
                String str = searchEditText.getText().toString();
                Intent intent1 = new Intent(activity, SearchResultActivity.class);
                str = searchEditText.getText().toString();
                intent1.putExtra("str", str);
                startActivity(intent1);
                mValsUtils.upDatemValsList(activity, str);
            } else searchEditText.setHint("查询名称不可以为空哦~");
        });
        delete.setOnClickListener(v->{
           mValsUtils.deletemValsList(activity);
            initData();
        });
        searchEditText.setOnClickListener(v -> {
            searchEditText.setHint("");
            relativeList.setVisibility(View.GONE);
        });
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()==0){
                    search.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0){
                    searchEditText.setHint("Search Garbage");
                    relativeList.setVisibility(View.GONE);
                }
                else showListView();
            }
        });
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
    public void onResume(){
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
        super.onPause();
    }

    public interface OnFragmentInteractionListener
    {
        void onFragmentInteraction(Uri uri);
    }

    public void initData(){
        mFlowLayout.removeAllViews();
        searchEditText.setText("");
        mVals = mValsUtils.readmValsList(activity);
        for (int i = 0; i<mVals.size();i++){
            TextView tv = (TextView) mInflater.inflate(R.layout.searched_lable_tv,mFlowLayout,false);
            tv.setText(mVals.get(i));
            //点击事件
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str = tv.getText().toString();
                    Intent intent = new Intent(getActivity(), SearchResultActivity.class);
                    intent.putExtra("str",str);
                    startActivity(intent);
                }
            });
            mFlowLayout.addView(tv);//添加到父View
        }
    }

    public boolean isRename(String str,String[] mVals){
        for (int i = 0; i < mVals.length;i++){
            if (str.equals(mVals[i])){
                return true;
            }
        }
        return false;
    }

    private void showListView(){
        relativeList.setVisibility(View.VISIBLE);
        String str = searchEditText.getText().toString();
        @SuppressLint("HandlerLeak")
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1){
                    Bundle bundle = msg.getData();
                    searchfeedbackData = bundle.getParcelable("search_feedback_data");
                    searchRubData.clear();
                    searchRubData = searchfeedbackData.getResultList();
                    SearchGarbageRelativeListAdapter searchGarbageRelativeListAdapter = new SearchGarbageRelativeListAdapter(activity,searchRubData);
                    relativeList.setAdapter(searchGarbageRelativeListAdapter);
                    RecyclerViewItemTouchListener recyclerViewItemTouchListener = new RecyclerViewItemTouchListener(activity,new RecyclerViewItemTouchListener.OnRecyclerItemClickListener.Builder(){
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent intent = new Intent(activity,SearchResultActivity.class);
                            String rubbish_name =searchRubData.get(position).getRubbishName();
                            int rubbish_sort =searchRubData.get(position).getRubbishSortNum();
                            intent.putExtra("rubbish_name",rubbish_name);
                            intent.putExtra("rubbish_sort",rubbish_sort);
                            startActivity(intent);
                        }
                    });
                    relativeList.addOnItemTouchListener(recyclerViewItemTouchListener);
                }else if (msg.what == -1){
                    Bundle bundle = msg.getData();
                    ErrorData errorData = bundle.getParcelable("search_error_data");
                }
            }
        };
        RelativeSearchThreadRunnable relativeSearchThreadRunnable = new RelativeSearchThreadRunnable(handler,str);
        Thread thread = new Thread(relativeSearchThreadRunnable);
        thread.start();

    }
}
