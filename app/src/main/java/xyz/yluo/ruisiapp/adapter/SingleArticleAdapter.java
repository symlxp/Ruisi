package xyz.yluo.ruisiapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.yluo.ruisiapp.MySetting;
import xyz.yluo.ruisiapp.R;
import xyz.yluo.ruisiapp.activity.UserDetailActivity;
import xyz.yluo.ruisiapp.data.SingleArticleData;
import xyz.yluo.ruisiapp.listener.RecyclerViewClickListener;
import xyz.yluo.ruisiapp.utils.CircleImageView;
import xyz.yluo.ruisiapp.utils.MyHtmlTextView;
import xyz.yluo.ruisiapp.utils.MyWebView;

/**
 * Created by free2 on 16-3-7.
 * 单篇文章adapter
 */

public class SingleArticleAdapter extends RecyclerView.Adapter<SingleArticleAdapter.BaseViewHolder>{

    private static final int TYPE_COMENT = 1;
    private static final int TYPE_LOAD_MORE_N = 2;
    private static final int TYPE_CONTENT=0;
    //数据
    private List<SingleArticleData> datalist;
    private static RecyclerViewClickListener itemListener;
    //上下文
    private Activity activity;


    public SingleArticleAdapter(Activity activity, RecyclerViewClickListener itemListener, List<SingleArticleData> datalist) {
        this.datalist = datalist;
        this.activity =activity;
        SingleArticleAdapter.itemListener = itemListener;
    }


    @Override
    public int getItemViewType(int position) {
        //判断listItem类型
        if(position==0){
            return TYPE_CONTENT;
        }else if (position!= getItemCount() - 1) {
            return TYPE_COMENT;
        } else {
            return TYPE_LOAD_MORE_N;
        }
        //TODO 普通文章类型再分类
        //int type   =  DataSet.get(position).getType();
    }
    //设置view
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case TYPE_CONTENT:
                return new ArticleContentViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_article_content_item, viewGroup, false));
            case TYPE_LOAD_MORE_N:
                return new LoadMoreViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_article_load_more, viewGroup, false));
            default: // TYPE_COMMENT
                return new CommentViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_article_comment_item, viewGroup, false));
        }
    }

    //设置data
    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setData(position);
    }
    //为了让 list item可以变化，我们可以重写这个方法
    //他的返回值是onCreateViewHolder 的参数viewType
    //这儿可以 知道position 和 data

    @Override
    public int getItemCount() {
        if(datalist.size()==0){
            return 0;
        }
        return datalist.size()+1;
    }

    public abstract class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public BaseViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }
        abstract void setData(int position);

        @Override
        public void onClick(View v)
        {
            //getItemCount()
            itemListener.recyclerViewListClicked(v, this.getLayoutPosition());
        }

    }

    //文章内容 楼主ViewHolder
    public class ArticleContentViewHolder extends BaseViewHolder{
        @Bind(R.id.article_title)
        protected TextView article_title;
        @Bind(R.id.article_user_image)
        protected CircleImageView article_user_image;
        @Bind(R.id.article_type)
        protected ImageView article_type;
        @Bind(R.id.article_username)
        protected TextView article_username;
        @Bind(R.id.article_replaycount)
        protected TextView article_replaycount;
        @Bind(R.id.article_post_time)
        protected TextView article_post_time;
        @Bind(R.id.content_webView)
        protected MyWebView webView;

        public ArticleContentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        @OnClick(R.id.btn_star)
        protected void btn_star_click(View v){
            itemListener.recyclerViewListClicked(v, this.getLayoutPosition());
        }

        @OnClick(R.id.btn_reply)
        protected void btn_reply_click(View v){
            itemListener.recyclerViewListClicked(v, this.getLayoutPosition());
        }

        @OnClick(R.id.btn_share)
        protected void btn_share_click(){
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, datalist.get(getAdapterPosition()).getCotent());
            shareIntent.setType("text/plain");
            //设置分享列表的标题，并且每次都显示分享列表
            activity.startActivity(Intent.createChooser(shareIntent, "分享到文章到:"));
        }
        @OnClick(R.id.article_user_image)
        protected void authorClick(){
            UserDetailActivity.openWithTransitionAnimation(activity, datalist.get(0).getUsername(), article_user_image,datalist.get(0).getUserImgUrl());
        }
        @Override
        void setData(int position){
            SingleArticleData single = datalist.get(position);
            article_title.setText(single.getTitle());
            //normal zhidin gold:100
            if(single.getType().equalsIgnoreCase("zhidin")){
                article_type.setVisibility(View.VISIBLE);
            }else{
                article_type.setVisibility(View.INVISIBLE);
            }
            article_username.setText(single.getUsername());
            article_replaycount.setText(single.getReplyCount());
            Picasso.with(activity).load(single.getUserImgUrl()).resize(44,44).centerCrop().placeholder(R.drawable.image_placeholder).into(article_user_image);
            article_post_time.setText(single.getPostTime());
            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.loadDataWithBaseURL(MySetting.BBS_BASE_URL,single.getCotent(),"text/html","UTF-8",null);
        }

    }

    //评论列表ViewHolder 如果想创建别的样式还可以创建别的houlder继承自RecyclerView.ViewHolder
    public  class CommentViewHolder extends BaseViewHolder{
        //protected ImageView good;

        @Bind(R.id.article_user_image)
        protected ImageView replay_image;
        @Bind(R.id.btn_reply_2)
        protected ImageView btn_reply_2;
        @Bind(R.id.replay_author)
        protected TextView replay_author;
        @Bind(R.id.replay_index)
        protected TextView replay_index;
        @Bind(R.id.replay_time)
        protected TextView replay_time;
        @Bind(R.id.html_text)
        protected MyHtmlTextView htmlTextView;
        @Bind(R.id.bt_lable_lz)
        protected TextView bt_lable_lz;


        public CommentViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);
        }

        //设置listItem的数据
        @Override
        void setData(final int position) {
            SingleArticleData single = datalist.get(position);
            replay_author.setText(single.getUsername());
            //判断是不是楼主
            if(datalist.get(position).getUsername().equals(datalist.get(0).getUsername())){
                bt_lable_lz.setVisibility(View.VISIBLE);
            }else {
                bt_lable_lz.setVisibility(View.GONE);
            }
            if(!single.getReplyUrl().contains("action=reply")){
                //链接不合法
                btn_reply_2.setVisibility(View.GONE);
            }else {
                btn_reply_2.setVisibility(View.VISIBLE);
            }
            Picasso.with(activity).load(single.getUserImgUrl()).resize(36,36).centerCrop().placeholder(R.drawable.image_placeholder).into(replay_image);
            //.error(R.drawable.user_placeholder_error)
            replay_time.setText(single.getPostTime());
            replay_index.setText(single.getIndex());
            htmlTextView.mySetText(activity, single.getCotent());
        }

        @OnClick(R.id.article_user_image)
            protected void onBtnAvatarClick() {
                UserDetailActivity.openWithTransitionAnimation(activity, datalist.get(getAdapterPosition()).getUsername(), replay_image,datalist.get(getAdapterPosition()).getUserImgUrl());
            }

        @OnClick(R.id.btn_reply_2)
        protected void btn_reply2_click(View v){
            itemListener.recyclerViewListClicked(v, this.getLayoutPosition());
        }
    }

    //加载更多ViewHolder
    public class LoadMoreViewHolder extends BaseViewHolder{

        @Bind(R.id.aticle_load_more_text)
        protected TextView aticle_load_more_text;

        public LoadMoreViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this,itemView);
        }

        @Override
        void setData(int position) {
            //TODO
            //load more 现在没有数据填充
            if(position==1){
                aticle_load_more_text.setText("还没有人回复快来抢沙发吧！！");
            }else if(position%10==0){
                aticle_load_more_text.setText("加载更多");
            }
            else{
                aticle_load_more_text.setText("暂无更多");
            }
        }

    }
}