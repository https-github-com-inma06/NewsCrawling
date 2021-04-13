package com.example.newscrawling.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.HandlerCompat.postDelayed
import com.bumptech.glide.Glide
import com.example.newscrawling.App
import com.example.newscrawling.R
import com.example.newscrawling.login.LoginActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.startActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Glide.with(App.instance.context())
            .load(R.drawable.bg_sky).into(splash_bg)
/*

        val hd = Handler()
        hd.postDelayed({
            startActivity<LoginActivity>()
        },3000)
*/


        // Anko 라이브러리로 코드 간결하게
        //startActivity<LoginActivity>()

        // 핸들러를 이용해서 3초뒤 로그인액티비티로
        Handler().postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        },3000)
    }


    // 스플래시 액티비티가 onPause() 상태가되면 종료된다
    override fun onPause() {
        super.onPause()
        finish()
    }

    // super를 지움으로 백버튼 동작을 막는다
    override fun onBackPressed() {
    }
}
