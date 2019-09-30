package com.wray2.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.wray2.CustomComponent.FlowLayout;
import com.wray2.FragmentsActivity;
import com.wray2.R;
import com.wray2.SearchResultActivity;
import com.wray2.Util.mValsUtils;

import java.util.LinkedList;

import static android.content.Context.INPUT_METHOD_SERVICE;

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
        initData();
        search.setOnClickListener(v ->
        {
            if (!searchEditText.getText().toString().isEmpty()) { //不可以getText != null,返回类型是Editable
                String str = searchEditText.getText().toString();
                Intent intent1 = new Intent(getActivity(), SearchResultActivity.class);
                str = searchEditText.getText().toString();
                intent1.putExtra("str", str);
                startActivity(intent1);
                mValsUtils.upDatemValsList(activity, str);
                initData();
            } else searchEditText.setHint("查询名称不可以为空哦~");
        });
        delete.setOnClickListener(v->{
           mValsUtils.deletemValsList(activity);
            initData();
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
        super.onResume();
        initData();
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
        hideIME();
        super.onPause();
    }

    public void hideIME()
    {
        InputMethodManager manager = (InputMethodManager)activity.getSystemService(INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
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
}
