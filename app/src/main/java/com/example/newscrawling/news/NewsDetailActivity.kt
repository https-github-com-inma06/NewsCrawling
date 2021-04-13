package com.example.newscrawling.news

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.newscrawling.R
import kotlinx.android.synthetic.main.activity_news_detail.*

class NewsDetailActivity : AppCompatActivity() {

    val TAG: String = "로그"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "NewsDetailActivity - onCreate() called")
        setContentView(R.layout.activity_news_detail)


        // 이전 액티비티에서 인텐트로 정보를 받아와서 저장한다.
        val time = intent.getStringExtra("time")
        val title = intent.getStringExtra("title")
        val thumbNail = intent.getStringExtra("thumbNail")
        val content = intent.getStringExtra("content")
        val link = intent.getStringExtra("link")

        news_time_detail.text = time
        news_title_detail.text = title
        Glide.with(this).load(thumbNail).into(news_thumbNail_detail)
        news_content_detail.text = content
//        news_link_detail.text = link

        // 공유 버튼
        fab_share.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, link)
            val chooser = Intent.createChooser(intent,"친구에게 공유하기")
            startActivity(chooser);
        }




        // 뉴스를 클릭하면 해당 기사로 웹브라우저가 실행된다.
        news_detail_layout.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(intent)
        }
    }
}
