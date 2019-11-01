package com.wray2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.wray2.Class.GlobalValue;
import com.wray2.Fragment.CameraFragment;
import com.wray2.Manager.LayoutDropManager;
import com.wray2.Class.Rubbish;
import com.wray2.RecyclerViewAdapter.HelpListAdapter;
import com.wray2.RecyclerViewAdapter.RecyclerViewItemTouchListener;
import com.wray2.Thread.JsonDataObjects.ErrorData;
import com.wray2.Thread.JsonDataObjects.FeedbackData;
import com.wray2.Thread.FeedbackThreadRunnable;
import com.wray2.Util.ScreenUtils;
import com.wray2.Thread.ResultThreadRunnable;
import com.wray2.Util.ThemeUtils;

import java.util.ArrayList;
import java.util.LinkedList;

import me.samlss.broccoli.Broccoli;

public class ResultActivity extends AppCompatActivity
{
    private ConstraintLayout resultConstraintLayout;//第一张卡片
    private ConstraintLayout feedbackConstraintLayout;//第二张卡片
    private ConstraintLayout feedbackResultConstraintLayout;//第三张卡片

    private ConstraintLayout resultContentConstraintLayout;//第一张卡片内容
    private ConstraintLayout feedbackContentConstraintLayout;//第二张卡片内容
    private ConstraintLayout feedbackResultContentConstraintLayout;//第二张卡片内容

    private LayoutDropManager resultDropManager;
    private LayoutDropManager feedbackDropManager;
    private LayoutDropManager feedbackResultDropManager;

    //需要做加载动画的控件
    private TextView resultCardTitle;
    private CardView resultCard1;
    private CardView resultCard2;
    private CardView resultCard3;

    private ConstraintLayout waitView;
    private ImageView uploadImageView;
    private ImageView leftback;
    private TextView resultHelpText;
    private ImageView resultSortImageView;
    private Button butHelpSendName;
    private RecyclerView helpList;
    private TextView resultNotcorrect;
    private TextView resultRubbishname;
    private EditText helpEdittext;
    private TextView resultRubbishIntroduce;
    private TextView resultRubbishguidance;
    private TextView resultDroppguidance;

    //传入的图片
    private Bitmap uploadImage;
    //指示图片是否为相机拍摄
    private boolean isCameraPhoto;

    //RecyclerView的数据源
    private LinkedList<Rubbish> RubData = new LinkedList<>();
    public static String feedbackId = "";

    //服务器返回的数据对象
    private FeedbackData feedbackData = new FeedbackData("", "", new LinkedList<Rubbish>());

    //指示卡片是否已经初始化完成，完成后不需要初始化动画了
    private boolean isCardInit = false;

    private Broccoli broccoli = new Broccoli();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setTheme(ThemeUtils.getThemeId(this));
        setContentView(R.layout.activity_result);

        //获取数据
        Intent intent = getIntent();
        isCameraPhoto = intent.getBooleanExtra("isCameraPhoto", false);

        //初始化卡片
        resultConstraintLayout = (ConstraintLayout)findViewById(R.id.result_ConstraintLayout_Card1);
        resultContentConstraintLayout = (ConstraintLayout)findViewById(R.id.result_card_content);
        resultDropManager = new LayoutDropManager(resultConstraintLayout);
        feedbackConstraintLayout = (ConstraintLayout)findViewById(R.id.result_ConstraintLayout_Card2);
        feedbackContentConstraintLayout = (ConstraintLayout)findViewById(R.id.feedback_card_content);
        feedbackDropManager = new LayoutDropManager(feedbackConstraintLayout);
        feedbackResultConstraintLayout = (ConstraintLayout)findViewById(R.id.result_ConstraintLayout_Card3);
        feedbackResultContentConstraintLayout = (ConstraintLayout)findViewById(R.id.feedback_result_card_content);
        feedbackResultDropManager = new LayoutDropManager(feedbackResultConstraintLayout);

        //卡片被度量后的初始化
        ViewTreeObserver globalObserver = feedbackResultConstraintLayout.getViewTreeObserver();
        globalObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                if (!isCardInit)
                {
                    //三个卡片都被度量以后进行动画初始化
                    resultConstraintLayout.setTranslationY(resultDropManager.getMaxHeight());
                    feedbackConstraintLayout.setVisibility(View.INVISIBLE);
                    feedbackResultConstraintLayout.setVisibility(View.INVISIBLE);
                }
            }
        });

        //初始化其余控件
        resultCardTitle = (TextView)findViewById(R.id.result_title_Card1);
        resultCard1 = (CardView)findViewById(R.id.result_card1);
        resultCard2 = (CardView)findViewById(R.id.result_card2);
        resultCard3 = (CardView)findViewById(R.id.result_card3);

        waitView = (ConstraintLayout)findViewById(R.id.wait_view);
        uploadImageView = (ImageView)findViewById(R.id.upload_image);
        leftback = (ImageView)findViewById(R.id.toolbar_back2);
        resultHelpText = (TextView)findViewById(R.id.result_helptext);
        resultSortImageView = (ImageView)findViewById(R.id.result_picture);
        helpList = (RecyclerView)findViewById(R.id.help_RecycleView_Card2);
        butHelpSendName = (Button)findViewById(R.id.help_send_button);
        resultNotcorrect = (TextView)findViewById(R.id.result_notcorrect);
        resultRubbishname = (TextView)findViewById(R.id.result_rubbishname);
        helpEdittext = (EditText)findViewById(R.id.help_edittext);
        resultRubbishIntroduce = (TextView)findViewById(R.id.result_rubbishIntroduce);
        resultRubbishguidance = (TextView)findViewById(R.id.result_rubbishguidance);
        resultDroppguidance = (TextView)findViewById(R.id.result_droppguidance);

        //加载动画
//        broccoli.removeAllPlaceholders();
//        broccoli.addPlaceholder(new PlaceholderParameter.Builder()
//                .setView(resultCardTitle)
//                .setDrawable(new BroccoliGradientDrawable(Color.parseColor("#DDDDDD"),
//                        Color.parseColor("#CCCCCC"), 0, 1000, new LinearInterpolator()))
//                .build());
//        broccoli.addPlaceholder(new PlaceholderParameter.Builder()
//                .setView(resultCard1)
//                .setDrawable(new BroccoliGradientDrawable(Color.parseColor("#DDDDDD"),
//                        Color.parseColor("#CCCCCC"), 0, 1000, new LinearInterpolator()))
//                .build());
//        broccoli.addPlaceholder(new PlaceholderParameter.Builder()
//                .setView(resultCard2)
//                .setDrawable(new BroccoliGradientDrawable(Color.parseColor("#DDDDDD"),
//                        Color.parseColor("#CCCCCC"), 0, 1000, new LinearInterpolator()))
//                .build());
//        broccoli.addPlaceholder(new PlaceholderParameter.Builder()
//                .setView(resultCard3)
//                .setDrawable(new BroccoliGradientDrawable(Color.parseColor("#DDDDDD"),
//                        Color.parseColor("#CCCCCC"), 0, 1000, new LinearInterpolator()))
//                .build());
//        broccoli.show();

        //服务器返回数据
        @SuppressLint("HandlerLeak")
        Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if (msg.what == 1)
                {
                    Bundle bundle = msg.getData();
                    feedbackData = bundle.getParcelable("feedback_data");
                    RubData.clear();
                    updateUI();
                    //视图更新完成才开始动画
                    AnimatorSet animatorSet = new AnimatorSet();
                    ObjectAnimator waitViewGone = ObjectAnimator.ofFloat(waitView, "alpha", 1, 0);
                    waitViewGone.setInterpolator(new LinearInterpolator());
                    waitViewGone.setDuration(300);
                    ObjectAnimator resultConstraintLayoutShow = ObjectAnimator.ofFloat(resultConstraintLayout, "translationY", resultDropManager.getMaxHeight(), 0);
                    resultConstraintLayoutShow.setInterpolator(new AccelerateDecelerateInterpolator());
                    resultConstraintLayoutShow.setDuration(500);
                    animatorSet.playTogether(waitViewGone, resultConstraintLayoutShow);
                    animatorSet.addListener(new AnimatorListenerAdapter()
                    {
                        @Override
                        public void onAnimationEnd(Animator animation)
                        {
                            super.onAnimationEnd(animation);
                            waitView.setVisibility(View.GONE);
                        }
                    });
                    animatorSet.start();
                }
                else if (msg.what == -1)
                {
                    Bundle bundle = msg.getData();
                    ErrorData errorData = bundle.getParcelable("error_data");
                    //todo:服务器报错处理
                }
            }
        };

        uploadImage = dealImage(GlobalValue.savedBitmap);
        uploadImageView.setImageBitmap(uploadImage);

        ResultThreadRunnable resultThreadRunnable = new ResultThreadRunnable(uploadImage, handler);
        Thread thread = new Thread(resultThreadRunnable);
        thread.start();

        //跳转部分
        leftback.setOnClickListener(v ->
        {
            Intent intent1 = new Intent();
            ResultActivity.this.setResult(CameraFragment.RESULT_BACK_FLAG, intent1);
            ResultActivity.this.finish();
        });
        resultHelpText.setOnClickListener(v ->
        {
            //card1内容消失-下压-隐藏
            //card2显示（内容隐藏）-等待card1消失
            feedbackConstraintLayout.setVisibility(View.VISIBLE);
            feedbackContentConstraintLayout.animate().alpha(0).setDuration(100);
            AnimatorSet card1ToCard2 = new AnimatorSet();
            //card1内容消失
            ObjectAnimator contentDisappear = ObjectAnimator.ofFloat(resultContentConstraintLayout, "alpha", 1, 0);
            contentDisappear.setDuration(100);
            ObjectAnimator sizeDown = ObjectAnimator.ofFloat(resultConstraintLayout, "translationY", 0, resultDropManager.getMaxHeight() - feedbackDropManager.getMaxHeight());
            card1ToCard2.setDuration(300);
            card1ToCard2.play(contentDisappear).after(50).with(sizeDown);
            card1ToCard2.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    resultConstraintLayout.setVisibility(View.INVISIBLE);
                    feedbackContentConstraintLayout.animate().alpha(1).setStartDelay(200);
                }
            });
            card1ToCard2.start();
        });
        butHelpSendName.setOnClickListener(v ->
        {
            TextInputLayout inputLayout = (TextInputLayout)findViewById(R.id.help_input_layout);
            String userTagName = helpEdittext.getText().toString();
            if (!TextUtils.isEmpty(userTagName))
            {
                inputLayout.setErrorEnabled(false);
                InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(helpEdittext.getWindowToken(), 0);
                feedbackEvent(1, helpEdittext.getText().toString());
            }
            else
            {
                inputLayout.setError("请输入标签");
                inputLayout.setErrorEnabled(true);
            }
        });


    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onPause()
    {
        overridePendingTransition(0, 0);
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //全屏,隐藏系统状态栏和虚拟按键
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        broccoli.removeAllPlaceholders();
        feedbackData.destroy();
        for (Rubbish rubbish : RubData)
            rubbish.getRubbishPicture().recycle();
        RubData.clear();
        helpList.setAdapter(null);
    }

    //初步处理相机返回的图片
    private Bitmap dealImage(Bitmap image)
    {
        Bitmap tempBitmap = image;
        float screenRate = (float)ScreenUtils.screenWidth / ScreenUtils.screenHeight;
        if (isCameraPhoto)
        {
            //相机拍摄的照片需要旋转90度
            Matrix rotateMatrix = new Matrix();
            rotateMatrix.preRotate(90);
            tempBitmap = Bitmap.createBitmap(tempBitmap, 0, 0, tempBitmap.getWidth(), tempBitmap.getHeight(), rotateMatrix, false);
        }
        return tempBitmap;
    }

    //使用服务器数据更新UI
    private void updateUI()
    {
        //更新UI
        String rubbishName = feedbackData.getResultList().get(0).getRubbishName();
        resultCardTitle.setText("它可能是" + rubbishName);
        resultNotcorrect.setText("这不是" + rubbishName + '？');
        resultRubbishname.setText(feedbackData.getResultList().get(0).getRubbishSortName());
        int drawablePicId = R.drawable.pic_recyclables;
        switch (feedbackData.getResultList().get(0).getRubbishSortNum())
        {
            case 0:
                resultRubbishname.setText(this.getResources().getString(R.string.recycleName));
                resultRubbishIntroduce.setText(this.getResources().getText(R.string.recyclables_info));
                resultRubbishguidance.setText(this.getResources().getText(R.string.recyclables_guide));
                resultDroppguidance.setText(this.getResources().getText(R.string.recyclables_throw_demand));
                break;
            case 1:
                drawablePicId = R.drawable.pic_dry_waste;
                resultRubbishname.setText(this.getResources().getString(R.string.dryRubbishname));
                resultRubbishIntroduce.setText(this.getResources().getText(R.string.dryWaste_info));
                resultRubbishguidance.setText(this.getResources().getText(R.string.dryWaste_guide));
                resultDroppguidance.setText(this.getResources().getText(R.string.dryWaste_throw_demand));
                break;
            case 2:
                drawablePicId = R.drawable.pic_wet_waste;
                resultRubbishname.setText(this.getResources().getString(R.string.wetRubbishname));
                resultRubbishIntroduce.setText(this.getResources().getText(R.string.wetWaste_info));
                resultRubbishguidance.setText(this.getResources().getText(R.string.wetWaste_guide));
                resultDroppguidance.setText(this.getResources().getText(R.string.wetWaste_throw_demand));
                break;
            case 3:
                drawablePicId = R.drawable.pic_harmful_waste;
                resultRubbishname.setText(this.getResources().getString(R.string.harmfulRubbishname));
                resultRubbishIntroduce.setText(this.getResources().getText(R.string.harmful_waste_info));
                resultRubbishguidance.setText(this.getResources().getText(R.string.harmful_waste_guide));
                resultDroppguidance.setText(this.getResources().getText(R.string.harmful_waste_throw_demand));
                break;
        }
        //裁剪图片
        Glide.with(this).load(drawablePicId).apply(RequestOptions.bitmapTransform(new RoundedCorners(20))).into(resultSortImageView);
        //更新RecyclerView数据
        RubData.addAll(feedbackData.getResultList().subList(1, feedbackData.getResultList().size()));
        //RecylerView返回数据之后创建Adapter
        HelpListAdapter helpListAdapter = new HelpListAdapter(ResultActivity.this, RubData);
        helpList.setAdapter(helpListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        helpList.setLayoutManager(linearLayoutManager);
        RecyclerViewItemTouchListener helpListItemTouchListener = new RecyclerViewItemTouchListener(this, new RecyclerViewItemTouchListener.OnRecyclerItemClickListener.Builder()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                feedbackEvent(0, RubData.get(position).getRubbishClassifyName());
            }
        });
        helpList.addOnItemTouchListener(helpListItemTouchListener);
        isCardInit = true;
    }

    //反馈动作处理
    private boolean feedbackEvent(int resultSort, String value)
    {
        if (TextUtils.isEmpty(feedbackData.getId()))
            return false;

        FeedbackThreadRunnable feedbackThreadRunnable = new FeedbackThreadRunnable(feedbackData.getId(), resultSort, value);
        Thread feedbackThread = new Thread(feedbackThreadRunnable);
        feedbackThread.start();

        //todo:添加等待动画
        //card2内容消失-下压-隐藏
        //card3显示（内容隐藏）-等待card2消失
        feedbackResultConstraintLayout.setVisibility(View.VISIBLE);
        feedbackResultContentConstraintLayout.animate().alpha(0).setDuration(100);
        AnimatorSet card2ToCard3 = new AnimatorSet();
        //card2内容消失
        ObjectAnimator contentDisappear = ObjectAnimator.ofFloat(feedbackContentConstraintLayout, "alpha", 1, 0);
        contentDisappear.setDuration(100);
        ObjectAnimator sizeDown = ObjectAnimator.ofFloat(feedbackConstraintLayout, "translationY", 0, feedbackDropManager.getMaxHeight() - feedbackResultDropManager.getMaxHeight());
        card2ToCard3.setDuration(300);
        card2ToCard3.play(contentDisappear).after(50).with(sizeDown);
        card2ToCard3.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                feedbackConstraintLayout.setVisibility(View.INVISIBLE);
                feedbackResultContentConstraintLayout.animate().alpha(1).setStartDelay(200);
            }
        });
        card2ToCard3.start();
        return true;
    }
}
