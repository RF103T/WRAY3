package com.wray2.RecyclerViewAdapter;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.wray2.Class.Rubbish;
import com.wray2.R;

import java.util.LinkedList;
import java.util.List;

public class HelpListAdapter extends RecyclerView.Adapter<HelpListViewHolder>
{
    private LayoutInflater inflater;
    private Context context;
    private List<Rubbish> datas;

    public HelpListAdapter(Context context, LinkedList<Rubbish> datas)
    {
        this.datas = datas;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public HelpListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = inflater.inflate(R.layout.activity_result_recylerview_item, viewGroup, false);
        return new HelpListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HelpListViewHolder helpListViewHolder, final int i)
    {
        Glide.with(context).load(datas.get(i).getRubbishPicture()).apply(RequestOptions.bitmapTransform(new RoundedCorners(10))).into(helpListViewHolder.rubbishImage);
        helpListViewHolder.rubbishImage.setImageBitmap(datas.get(i).getRubbishPicture());
        helpListViewHolder.rubbishName.setText(datas.get(i).getRubbishName());
        helpListViewHolder.rubbishClaasifyName.setText(datas.get(i).getRubbishSortName());
        switch (datas.get(i).getRubbishSortNum())
        {
            case 0:
            {
                //蓝
                helpListViewHolder.rubbishClaasifyName.setTextColor(Color.rgb(84, 96, 170));
                break;
            }
            case 1:
            {
                //灰
                helpListViewHolder.rubbishClaasifyName.setTextColor(Color.rgb(136, 136, 136));
                break;
            }
            case 2:
            {
                //棕
                helpListViewHolder.rubbishClaasifyName.setTextColor(Color.rgb(101, 63, 50));
                break;
            }
            case 3:
            {
                //红
                helpListViewHolder.rubbishClaasifyName.setTextColor(Color.rgb(221, 6, 22));
                break;
            }
        }
    }

    @Override
    public int getItemCount(){ return datas.size();}

    //绑定的新Adapter会调用onAttachedToRecyclerView，旧Adapter会调用onDetachedFromRecyclerView
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView)
    {
        super.onDetachedFromRecyclerView(recyclerView);
    }

}

