package com.example.newscrawling.login

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.newscrawling.App
import com.example.newscrawling.R
import com.example.newscrawling.main.MainActivity
import com.example.newscrawling.news.NewsActivity
import com.example.newscrawling.register.RegisterActivity
import com.example.newscrawling.retrofit.RetrofitManager
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.btn_register
import kotlinx.android.synthetic.main.activity_login.et_email
import kotlinx.android.synthetic.main.activity_login.et_password
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import kotlin.random.Random


class LoginActivity : AppCompatActivity() {

    val TAG: String = "로그"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        // gif 배경
        Glide.with(App.instance.context())
            .load(R.drawable.bg_sky).override(login_layout.width, login_layout.height)
            .into(layout_bg)

        // 회원가입시 받아온 userID를 et_email에 넣는다.
        Log.d(TAG, "LoginActivity - onCreate() called / ${intent.getStringExtra("userID")}")
        if (intent.getStringExtra("userID") != null) {
            et_email.setText(intent.getStringExtra("userID"))
        }
        btn_login_google.setOnClickListener {
            toast("구글버튼 클릭")
        }

        btn_login_facebook.setOnClickListener {
            toast("페이스북 클릭")
        }

        btn_login_kakao.setOnClickListener {
            toast("카카오버튼 클릭")
        }

        btn_login.setOnClickListener {
            if (et_email.text.toString().isEmpty() ||
                et_password.text.toString().isEmpty()
            ) {
                toast("이메일 또는 패스워드를 입력하세요")
            } else if (
                !Patterns.EMAIL_ADDRESS.matcher(et_email.text.toString()).matches() &&
                // 관리자 ID("admin") 일때 이메일 형식이 아니여도 로그인 가능
                et_email.text.toString() != "admin"
            ) {
                toast("이메일 형식이 아닙니다.")
            } else {
                // 로그인 API 호출
                RetrofitManager.instanse.getLogin(
                    userID = et_email.text.toString(),
                    userPassword = et_password.text.toString(),
                    completion = {
                        Log.d(TAG, "LoginActivity - onCreate() called / ${it.body()}")
                        val parser = JsonParser.parseString(it.body().toString()).asJsonObject
                        val result = parser.get("success")
                        val ID: String? = parser.get("ID").toString()
                        val USER_ID: String? = parser.get("USER_ID").asString
                        val USER_PASSWORD: String? = parser.get("USER_PASSWORD").asString
                        var USER_NAME = parser.get("USER_NAME")
                        val USER_NICK = parser.get("USER_NICK")
                        val USER_IMAGE = parser.get("USER_IMAGE")
                        val USER_REGISTER = parser.get("USER_REGISTER")

                        if (result.asBoolean) {
                            // 로그인 성공
                            toast("환영합니다!")
                            startActivity<MainActivity>(
                                "ID" to ID.toString(),
                                "USER_ID" to USER_ID.toString(),
                                "USER_PASSWORD" to USER_PASSWORD.toString(),
                                "USER_NAME" to USER_NAME.toString(),
                                "USER_NICK" to USER_NICK.toString(),
                                "USER_IMAGE" to USER_IMAGE.toString(),
                                "USER_REGISTER" to USER_REGISTER.asString
                            )


                            finish()
                        } else {
                            // 로그인 실패
                            toast("아이디 또는 비밀번호를 확인하세요")
                        }
                    })
            }

        }

        btn_forgotPassword.setOnClickListener {
            toast("비밀번호 찾기 클릭")
        }

        btn_register.setOnClickListener {
            toast("회원가입 클릭")
            startActivity<RegisterActivity>()
        }


    }
}
