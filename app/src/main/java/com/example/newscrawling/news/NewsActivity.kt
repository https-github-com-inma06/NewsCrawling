package com.example.newscrawling

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.newscrawling.adapter.NewsListAdapter
import com.example.newscrawling.model.News
import kotlinx.android.synthetic.main.activity_news.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jsoup.Jsoup



//TODO:
// 1. 페이지네이션 할때 하단 공간에 프로그래스바 만들기
// 2. 메인액티비티에 바텀네비게이션 만들기


class NewsActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    val TAG: String = "로그"

    //TODO: 외부 클래스로 수정해야함.
    val NEWS_BASE_URL: String = "https://www.christiantoday.co.kr"
    var NEWS_PAGE: Int = 1
    // 페이지당 한번에 가져오는 뉴스 아이템 갯수 (+ 1)
    val NEWS_LIST_COUNT: Int = 14

    //크롤링한 뉴스 데이터를 담을 그릇
    private var newsList = ArrayList<News>()
    private lateinit var newsListAdapter: NewsListAdapter


    // 스와이프 리플래시를 당겼을때
    override fun onRefresh() {
        Log.d(TAG, "NewsActivity - onRefresh() called / 당겼음")
        //newsList 배열에 있는 데이터를 초기화 해준다
        newsList.clear()
        newsListAdapter.clearList()

        //TODO: notifyDataSetChanged를 할경우 아이템이 중복되서 보임
        // notifyDataSetChanged를 하지 않을경우 아이템이 사라졌다가 다시 보여지지 않음
//        newsListAdapter.notifyDataSetChanged()
        // 페이지를 1페이지로 초기화한다.
        NEWS_PAGE = 1
        // dataCrawling() 메소드를 호출하고 완료되면 컴플레션 블럭으로 알려준다.
        dataCrawling(completion = {
            Log.d(TAG, "MainActivity - onCreate() /크롤링 완료 컴플레션 블럭이 호출 되었다. 배열의 크기 : ${it.size}")
            //메인 Ui 쓰레드에서 실행합니다.
            runOnUiThread {
                newsListAdapter.submitList(it)
                newsListAdapter.notifyDataSetChanged()
            }
        })
        Toast.makeText(App.instance.context(), "새로고침 되었습니다.", Toast.LENGTH_SHORT).show()
        // 데이터 리플래시가 끝났을때 종료해준다.
        swipe_refresh.isRefreshing = false
    }

    // 액티비티가 처음으로 실행될때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        Log.d(TAG, "NewsActivity - onCreate() called / 처음 액티비티 뷰가 메모리에 올라갔을 때")

        // 스와이프 리플래쉬를 사용한다.
        swipe_refresh.setOnRefreshListener(this)
        // 어답터를 메모리에 올린다.
        newsListAdapter = NewsListAdapter()
        // 리사이클러뷰를 설정한다.
        news_recyclerView.apply {
            layoutManager =
                LinearLayoutManager(this@NewsActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = newsListAdapter

            //아이템이 클릭되었을 때
            newsListAdapter.itemClick = object : NewsListAdapter.ItemClick {
                override fun onClick(view: View, position: Int) {

                    // Anko 라이브러리를 사용해서 간단하게
                    // 인텐트로 NewsDetailAcActivity에넘겨준다.
                    startActivity<NewsDetailActivity>(
                        "title" to newsListAdapter.mNewsList[position].title,
                        "content" to newsListAdapter.mNewsList[position].content,
                        "time" to newsListAdapter.mNewsList[position].time,
                        "thumbNail" to newsListAdapter.mNewsList[position].thumbNail,
                        "link" to newsListAdapter.mNewsList[position].link
                    )
                }
            }

            // 뷰가 열릴때 처음 데이터를 크롤링하는 함수
            // 함수가 끝나면 컴플레션 블럭으로 알려준다.
            dataCrawling(completion = {
                Log.d(TAG, "MainActivity - onCreate() /크롤링 완료 컴플레션 블럭이 호출 되었다. 배열의 크기 : ${it.size}")

                //메인 Ui 쓰레드에서 실행합니다.
                runOnUiThread {
                    newsListAdapter.submitList(it)
                    newsListAdapter.notifyDataSetChanged()
                }

                //현재 화면에 출력된 리스트중 마지막 item의 position을 리턴
            })
            this.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    // 마지막에 위치한 아이템의 포지션값
                    var lastVisibleItemPosition: Int =
                        (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()

                    // 플로팅 액션 버튼이 클릭되었을 때
                    // 리사이클러뷰의 스크롤을 최상단으로 올린다.
                    main_fab.setOnClickListener {
                        news_recyclerView.smoothScrollToPosition(0)
//                        news_recyclerView.scrollToPosition(0)
//                        main_fab.visibility = View.INVISIBLE
                    }

                    if (lastVisibleItemPosition > 0) {
                        // 스크롤이 내려갔다면
                        main_fab.visibility = View.VISIBLE
//                        isDownScroll = true
                    } else if(lastVisibleItemPosition == 0) {
                        // 스크롤이 최상단이라면
                        main_fab.visibility = View.INVISIBLE
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    // 마지막에 위치한 아이템의 포지션값
                    var lastVisibleItemPosition: Int =
                        (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()

                    // 현재 아이템의 총 갯수
                    var itemTotalCount = recyclerView.adapter!!.itemCount

                    // 맨 밑의 아이템 포지션값 + 1은 아이템의 총 갯수와 같다면?
                    // 즉 리사이클러뷰 바닥에 도달한 상태라면
                    if (lastVisibleItemPosition + 1 == itemTotalCount) {
                        Log.d(
                            TAG,
                            "NewsActivity - onScrollStateChanged() called / end of last 배열 크기 : " +
                                    "${recyclerView.adapter!!.itemCount}"
                        )
                        // 뉴스페이지를 증가시킨다. ( 다음페이지로
                        NEWS_PAGE += 1
                        Toast.makeText(context, "${NEWS_PAGE}페이지 입니다", Toast.LENGTH_SHORT).show()
                        // 리스트 마지막(바닥) 도착!!!!! 다음 페이지 데이터 로드!!
                        dataCrawling(completion = {
                            // 메인 Ui 쓰레드에서 실행합니다.
                            runOnUiThread {
                                newsListAdapter.submitList(it)
                                newsListAdapter.notifyDataSetChanged()
                            }
                        })
                        Log.d(TAG, "NewsActivity - onScrolled() called / 끝")
                    }
                }
            })
        }
    }

    // 한페이지당 15개 뉴스 목록을 반복문으로 가져온다. ( for(i in 0..14) )

    /*
    * 데이터를 Jsoup 으로 크롤링해서 컴플레션 블럭으로 처리는 메소드
    * */
    fun dataCrawling(completion: (ArrayList<News>) -> Unit) {
        doAsync {
            // doc => 크롤링한 사이트의 전체 html
            val doc = Jsoup.connect("$NEWS_BASE_URL/archives/page$NEWS_PAGE.htm").get()
            Log.d(TAG, "NewsActivity - dataCrawling() called / \n ${doc}")
            for (i in 0..NEWS_LIST_COUNT) {
                val newsItem = News(
                    //doc 에서 파싱한 뉴스데이터의 time 값
                    time = doc.select("div.section-5 article").select("article.hb time")[i].text(),
                    //doc 에서 파싱한 뉴스데이터의 title 값
                    title = doc.select("div.section-5 article").select("article.hb")
                        .select("div.info")
                        .select("h4").select("a")[i].text(),
                    //doc 에서 파싱한 뉴스데이터의 content 값
                    content = doc.select("div.section-5 article").select("article.hb")
                        .select("div.info")
                        .select("summary")[i].text(),
                    //doc 에서 파싱한 뉴스데이터의 thumbNail 값
                    thumbNail = doc.select("div.section-5 article").select("article.hb figure")
                        .select("a").select("img")[i].attr("src").toString(),
                    //doc 에서 파싱한 뉴스데이터의 link 값
                    link = NEWS_BASE_URL + doc.select("div.section-5 article")
                        .select("article.hb figure").select("a")[i].attr("href").toString()
                )

                //반복문이 돌때 마다 데이터를 하나씩 배열에 담는다.
                newsList.add(newsItem)
            }
            //데이터가 모두 크롤링되면 ( 15개 ) 컴플레션 블럭으로 알려준다.
            completion(newsList)
        }
    }
}