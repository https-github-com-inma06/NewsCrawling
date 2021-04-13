package com.example.newscrawling.main

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.newscrawling.R
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.json.JSONObject
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket


class MainActivity : AppCompatActivity() {

    val TAG: String = "로그"

    // 채팅관련
    private var SERVER_PORT: Int = 1234
//    private var SERVER_IP = "localhost"
    private var SERVER_IP = "192.168.0.26"
//    private var SERVER_IP: InetAddress = InetAddress.getByName("3.34.61.169")

    private var socket: Socket = Socket()
    private var dos: DataOutputStream = DataOutputStream(null)
    private var dis: DataInputStream = DataInputStream(null)


    // 뒤로가기 버튼 두번 눌러 종료
    private var backKeyPressedTime: Long = 0L
    private var toast: Toast? = null
    private var isDoubleBackKeyPressed = false

    private var userPrimaryId: String? = String()
    private var userId: String? = String()
    private var userPass: String? = String()
    private var userName: String? = String()
    private var userNick: String? = String()
    private var userImage: String? = String()
    private var userRegister: String? = String()
    private val jsonObject: JSONObject = JSONObject()
    private val userData: JSONObject = JSONObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userPrimaryId = intent.getStringExtra("ID")
        userId = intent.getStringExtra("USER_ID")
        userPass = intent.getStringExtra("USER_PASSWORD")
        userName = intent.getStringExtra("USER_NAME")
        userNick = intent.getStringExtra("USER_NICK")
        userImage = intent.getStringExtra("USER_IMAGE")
        userRegister = intent.getStringExtra("USER_REGISTER")

        tv_id.text = userPrimaryId
        tv_userId.text = userId
        tv_userPassword.text = userPass
        tv_userName.text = userName
        tv_userNick.text = userNick
        tv_userImage.text = userImage
        tv_userRegister.text = userRegister


        btn_connect.setOnClickListener {
            // establish the connection
            doAsync {
                socket = Socket(SERVER_IP, SERVER_PORT)
                dos = DataOutputStream(socket.getOutputStream())
                dis = DataInputStream(socket.getInputStream())

                connectServer( completion = {
                    Log.d(TAG, "MainActivity - connectServer() completion")
                    readMsg(completion = {
                        //TODO:JSON 으로 받은것 파싱해야함.
                        tv_readMsg.text = it
                        Log.d(TAG, "MainActivity - onCreate() called / $it")
                        Log.d(TAG, "MainActivity - btn_connect called /socket : $socket")
                    })
                })


            }

        }

        btn_disConnect.setOnClickListener {
            doAsync {
                try {
                    dos.writeUTF("logout")
                    dos.flush()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                /*socket.close()
                dos.close()
                dis.close()*/
                Log.d(TAG, "MainActivity - btn_disConnect called / socket : $socket")
            }

        }


        btn_sendBtn.setOnClickListener {
            if (et_sendMsg.text.isEmpty()) {
                toast("메시지를 입력하세요")
            }
            sendMsg(completion = {
                et_sendMsg.text.clear()
            })
        }

    }

    private fun connectServer(completion: () -> Unit) {
        doAsync {
            try {


                // Json 형식으로 보내기
                jsonObject.put("USER_PRIMARY_KEY", userPrimaryId)
                jsonObject.put("USER_ID", userId)
                jsonObject.put("USER_NAME", userName)
                jsonObject.put("USER_NICK", userNick)
                jsonObject.put("USER_IMAGE", userImage)
                jsonObject.put("ROOM_NUM", "10")
                jsonObject.put("USER_MSG", et_sendMsg.text.toString())
                jsonObject.put("MSG_FORMAT","text")
                jsonObject.put("SEND_IMAGE", "유저가보낸 이미지")
                jsonObject.put("SEND_VIDEO", "유저가보낸 비디오")

                Log.d(TAG, "MainActivity - / $jsonObject")
                dos.writeUTF(jsonObject.toString())
                dos.flush()

            } catch (e: IOException) {
                e.printStackTrace()
            }
            completion()
        }
    }

    private fun sendMsg(completion: () -> Unit) {
        doAsync {
            try {

                // Json 형식으로 보내기
                jsonObject.put("USER_PRIMARY_KEY", userPrimaryId)
                jsonObject.put("USER_ID", userId)
                jsonObject.put("USER_NAME", userName)
                jsonObject.put("USER_NICK", userNick)
                jsonObject.put("USER_IMAGE", userImage)
                jsonObject.put("ROOM_NUM", "10")
                jsonObject.put("USER_MSG", et_sendMsg.text.toString())
                jsonObject.put("MSG_FORMAT","text")
                jsonObject.put("SEND_IMAGE", "유저가보낸 이미지")
                jsonObject.put("SEND_VIDEO", "유저가보낸 비디오")

                Log.d(TAG, "MainActivity - / $jsonObject")
                dos.writeUTF(jsonObject.toString())
                dos.flush()

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        completion()
    }

    private fun readMsg(completion: (msg: String) -> Unit) {
        doAsync {

            while (true) {
                try {
                    // read the message sent to this client
                    val msg = dis.readUTF()

                    Log.d(TAG, "MainActivity - readMsg() called / msg : $msg")
                    completion(msg)
                } catch (e: Exception) {
                }

            }


        }
    }


    override fun onBackPressed() {
//        super.onBackPressed()

        // 뒤로가기 버튼을 두번 누르면 앱을 종료합니다.
        if (isDoubleBackKeyPressed) {
            ActivityCompat.finishAffinity(this);
            // TODO : 모든 앱을 종료한다. apk 16버전 아래는 밑의 코드를 사용
//            exitProcess(0)
        }
        this.isDoubleBackKeyPressed = true
        Toast.makeText(this, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
        Handler().postDelayed(
            {
                isDoubleBackKeyPressed = false
            },
            2000
        )
    }
}



