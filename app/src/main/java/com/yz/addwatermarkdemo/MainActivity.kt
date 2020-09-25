package com.yz.addwatermarkdemo

import android.annotation.SuppressLint
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.net.HttpURLConnection
import java.net.URL
import java.io.IOException
import java.net.MalformedURLException
import android.os.Handler
import android.os.Message
import android.util.Log
import com.blankj.utilcode.util.ImageUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.log


class MainActivity : AppCompatActivity() {

    private val srcs = arrayListOf<WatermarkData>()
    private var bitmap : Bitmap? = null
    private var logo:Bitmap? = null


    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler(){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what){
                1 -> runOnUiThread { original_img.setImageBitmap(addWatermark()) }//ImageUtils.addImageWatermark(bitmap,logo,srcs[0].logoInfo[0].startX,srcs[0].logoInfo[0].startY,0)
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initData()

        bitmap = returnBitMap(srcs[0].imgUrl)
        Glide.with(this)
            .asBitmap()
            .load(srcs[0].logoInfo[0].logoUrl)
            .into(object : SimpleTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    logo = resource
                    mHandler.sendEmptyMessage(1)
//                    original_img.setImageBitmap(ImageUtils.addImageWatermark(bitmap,logo,srcs[0].logoInfo[0].startX,srcs[0].logoInfo[0].startY,0))
                }
            })

    }


    private fun addWatermark():Bitmap{
        val newBitmap = Bitmap.createBitmap(srcs[0].imgWidth,srcs[0].imgHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        canvas.drawBitmap(bitmap!!,0f,0f,null)
        canvas.save()
//        canvas.setBitmap(logo)
        val paint = Paint()
        val colors = IntArray(2)
        colors[0] = Color.parseColor("#ffff00")
        colors[1] = Color.parseColor("#ffff00")
        paint.shader = LinearGradient(0f, 0f, 310f, 310f,colors,null, Shader.TileMode.CLAMP)
//        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
//        paint.style = Paint.Style.STROKE

//        val logoBitmap = Bitmap.createBitmap(srcs[0].logoInfo[0].logoWidth,srcs[0].logoInfo[0].logoHeight,Bitmap.Config.ARGB_8888)
//        canvas.drawBitmap(logoBitmap,srcs[0].logoInfo[0].startX.toFloat(),srcs[0].logoInfo[0].startY.toFloat(),null)

//        canvas.rotate(srcs[0].logoInfo[0].rotate.toFloat())srcRect
            val desRect = Rect(srcs[0].logoInfo[0].startX,srcs[0].logoInfo[0].startY,srcs[0].logoInfo[0].startX+(srcs[0].logoInfo[0].logoWidth),srcs[0].logoInfo[0].startY+(srcs[0].logoInfo[0].logoHeight))
            val srcRect = Rect(0,0,310,310)
            Log.e("TAG","logo width:${logo?.width}")
            Log.e("TAG","logo height:${logo?.height}")
//        canvas.drawRect(100f,187f,186f,187f+86f,paint)
            canvas.drawBitmap(logo!!,srcRect,desRect,paint)
//        canvas.drawBitmap(logo!!,srcs[0].logoInfo[0].startX.toFloat(),srcs[0].logoInfo[0].startY.toFloat(),paint)
            canvas.save()
            canvas.restore()
            return newBitmap
    }

    private fun initData() {
        srcs.add(WatermarkData(540,"http://qiniu.yuzhua.info/toolbox/20200925/6fd6bbff15993d27858935eb5e136f61.png",850,
            arrayListOf(
                LogoInfo("000000",86,"http://toolbox.yuzhua-test.com/client/img/logo.6cf8570.png",86,40,0,100,187),
                LogoInfo("000000",86,"http://toolbox.yuzhua-test.com/client/img/logo.6cf8570.png",86,40,0,324,187),
                LogoInfo("000000",86,"http://toolbox.yuzhua-test.com/client/img/logo.6cf8570.png",86,40,0,324,187),
                LogoInfo("000000",86,"http://toolbox.yuzhua-test.com/client/img/logo.6cf8570.png",86,40,0,324,187)
            )))
        srcs.add(WatermarkData(540,"http://qiniu.yuzhua.info/toolbox/20200925/6fd6bbff15993d27858935eb5e136f61.png",850,
            arrayListOf(
                LogoInfo("FFB87333",86,"http://toolbox.yuzhua-test.com/client/img/logo.6cf8570.png",86,40,0,324,187),
                LogoInfo("B87333",86,"http://toolbox.yuzhua-test.com/client/img/logo.6cf8570.png",86,40,0,324,187),
                LogoInfo("B87333",86,"http://toolbox.yuzhua-test.com/client/img/logo.6cf8570.png",86,40,0,324,187),
                LogoInfo("B87333",86,"http://toolbox.yuzhua-test.com/client/img/logo.6cf8570.png",86,40,0,324,187)
            )))
    }


    private fun returnBitMap(url: String): Bitmap? {
        Thread(Runnable {
            var imageurl: URL? = null

            try {
                imageurl = URL(url)
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }

            try {
                val conn = imageurl!!.openConnection() as HttpURLConnection
                conn.doInput = true
                conn.connect()
                val `is` = conn.inputStream
                bitmap = BitmapFactory.decodeStream(`is`)
                `is`.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }).start()

        return bitmap
    }
}
