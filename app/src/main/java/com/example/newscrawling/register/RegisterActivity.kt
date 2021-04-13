package com.example.newscrawling.register

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.newscrawling.App
import com.example.newscrawling.R
import com.example.newscrawling.login.LoginActivity
import com.example.newscrawling.retrofit.RetrofitManager
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


//TODO:
// 1. 이메일 본인확인

class RegisterActivity : AppCompatActivity() {
    val TAG: String = "로그"
    private var isVerify : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        Glide.with(App.instance.context())
            .load(R.drawable.bg_sky).into(register_bg)


        // 회원가입 버튼시 작동동
        btn_register.setOnClickListener {
            if (et_email.text.toString().isEmpty() ||
                et_password.text.toString().isEmpty() ||
                et_confirm_password.text.toString().isEmpty()
            ) {
                toast("정보를 올바르게 입력하세요.")
            } else if (!Patterns.EMAIL_ADDRESS.matcher(et_email.text.toString()).matches()) {
                toast("이메일 형식이 아닙니다.")
            } else if (et_password.text.toString() != et_confirm_password.text.toString()) {
                toast("비밀번호를 확인하세요.")
            } else if (et_confirm_password.text.toString().length < 4) {
                toast("비밀번호는 최소 4자이상 입력하세요")
            } else if (!checkbox_terms.isChecked) {
                toast("필수 약관에 동의해주세요.")
            } else {
                if(isVerify){
                    RetrofitManager.instanse.getRegister(
                        userID = et_email.text.toString(),
                        userPassword = et_confirm_password.text.toString(),
                        completion = {
                        Log.d(TAG, "RegisterActivity - onCreate() called / ${it.body()}")
                        val parser  = JsonParser.parseString(it.body().toString()).asJsonObject
                        val result = parser.get("success")
                        if(result.asBoolean) {
                            toast("${et_email.text.toString()}\n회원가입 성공")
                            isVerify = false
                            startActivity<LoginActivity>("userID" to et_email.text.toString())
                            finish()
                        } else {
                            toast("에러발생 : 관리자에게 문의하세요.")
                        }
                    })
                } else {
                    toast("이메일을 인증 하세요")
                }
            }
        }

        btn_verify.setOnClickListener {
            if(Patterns.EMAIL_ADDRESS.matcher(et_email.text.toString()).matches()){
                RetrofitManager.instanse.getVerify(userID = et_email.text.toString(), completion = {
                    Log.d(TAG, "RegisterActivity - onCreate() called / ${it.body()}")
                    val parser  = JsonParser.parseString(it.body().toString()).asJsonObject
                    val result = parser.get("success")
                    if(result.asBoolean) {
                        isVerify = true
                        toast("아이디 사용가능")
                    } else {
                        isVerify = false
                        toast("아이디를 이미 사용중입니다.")
                    }
                })
            } else {
                toast("이메일 형식이 아닙니다.")
            }
        }

        btn_terms.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://3.34.61.169/terms.html"))
            startActivity(intent)
        }

        checkbox_terms.setOnClickListener {
            if (checkbox_terms.isChecked) {
                toast("약관을 모두 읽은것으로 간주합니다.")
            }
        }
    }
}

