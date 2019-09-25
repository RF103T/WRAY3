package com.wray2.RecyclerViewAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wray2.R;

public class HelpListViewHolder extends RecyclerView.ViewHolder //implements View.OnClickListener
{
    ImageView rubbishImage;
    TextView rubbishName;
    TextView rubbishClaasifyName;

    //构造函数中添加自定义的接口参数
    public HelpListViewHolder(@NonNull View itemView)//HelpListAdapter.OnItemClickListener listener
    {
        super(itemView);
        rubbishImage = itemView.findViewById(R.id.help_picture);
        rubbishName = itemView.findViewById(R.id.help_rubbish_name);
        rubbishClaasifyName = itemView.findViewById(R.id.help_rubbish_classify_name);

    }


}

