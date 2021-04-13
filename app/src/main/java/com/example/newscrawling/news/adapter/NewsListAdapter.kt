package com.example.newscrawling.news.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.newscrawling.R
import com.example.newscrawling.news.model.News

class NewsListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mNewsList: ArrayList<News> = ArrayList()

    // 아이템 클릭 리스너
    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null

    // 뷰홀더가 메모리에 올라갔을때
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NewsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_news_item, parent, false)
        )

    }

    // 보여줄 목록의 갯수
    override fun getItemCount(): Int {
        return mNewsList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // 아이템 클릭 호출
        if(itemClick != null){
            holder?.itemView?.setOnClickListener { v ->
                itemClick?.onClick(v, position)
            }
        }

        //switch
        when (holder) {
            is NewsViewHolder -> {
                if (mNewsList.isNotEmpty()) {
                    // 데이터와 뷰를 묶어준다
                    holder.bind(mNewsList[position])
                }

            }
        }
    }

    // 어답터에 데이터를 넣는 메소드
    fun submitList(newsList: ArrayList<News>) {
        this.mNewsList = newsList
    }

//    fun addList(newsList: ArrayList<News>) {
//        this.mNewsList.addAll(newsList)
//    }

    // 어답터에 있는 데이터를 지워준다.
    fun clearList() {
        this.mNewsList.clear()
    }
}