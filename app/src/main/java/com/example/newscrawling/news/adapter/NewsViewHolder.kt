package com.example.newscrawling.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newscrawling.model.News
import kotlinx.android.synthetic.main.layout_news_item.view.*

class NewsViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView){
    // 뉴스제목
    // 뉴스내용
    // 뉴스 작성일
    // 뉴스 썸네일
    // 뉴스 링크
    val itemTitle = itemView.tv_main_title
    val itemContent = itemView.tv_main_content
    val itemTime = itemView.tv_main_time
    val itemThumbNail = itemView.iv_main_thumbNail
    val itemLink = itemView.tv_main_link

    // 데이터와 뷰를 묶는다. 즉 포스트 모델 클래스랑 뷰의 요소를 일치 시킴
    fun bind(newsItem: News) {
        itemTitle.text = newsItem.title
        itemContent.text = newsItem.content
        itemTime.text = newsItem.time
        Glide.with(itemView.context).load(newsItem.thumbNail).into(itemThumbNail)
        itemLink.text = newsItem.link
    }

}