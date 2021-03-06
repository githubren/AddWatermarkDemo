package com.yz.addwatermarkdemo

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.Bitmap
import android.graphics.ColorMatrixColorFilter
import android.graphics.ColorMatrix
import android.util.Log
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.blankj.utilcode.util.ToastUtils


class MainActivity : AppCompatActivity() {

    private val srcs = arrayListOf<WatermarkData>()
    private var bitmap : Bitmap? = null
    private var logo:Bitmap? = null


    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler(){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what){
//                1 -> runOnUiThread { original_img.setImageBitmap(addWatermark()) }//ImageUtils.addImageWatermark(bitmap,logo,srcs[0].logoInfo[0].startX,srcs[0].logoInfo[0].startY,0)
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initData()

        bitmap = returnBitMap(srcs[0].imgUrl)
        srcs[0].logoInfo.forEachIndexed { index, logoInfo ->
            Glide.with(this)
                .asBitmap()
                .load(srcs[0].logoInfo[index].logoUrl)
                .into(object : SimpleTarget<Bitmap>(){
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        logo = resource
                        runOnUiThread {  original_img.setImageBitmap(addWatermark(resource)) }
                    }
                })
        }


        btn.setOnClickListener { view ->
            val t1 = ValueAnimator.ofFloat(10f,-10f)
            t1.duration = 150
            t1.interpolator = AccelerateInterpolator()
            t1.addUpdateListener {
                view.translationZ = it.animatedValue as Float
            }
            val t2 = ValueAnimator.ofFloat(-10f,10f)
            t2.duration = 150
            t2.interpolator = DecelerateInterpolator()
            t2.addUpdateListener {
                view.translationZ = it.animatedValue as Float
            }
            val animatorSet = AnimatorSet()
            animatorSet.playSequentially(t1,t2)
            animatorSet.start()
        }
    }


    private fun addWatermark(logo:Bitmap): Bitmap {
        val newBitmap = Bitmap.createBitmap(srcs[0].imgWidth, srcs[0].imgHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        canvas.drawBitmap(bitmap!!,0f,0f,null)
        canvas.save()
        val paint = Paint()
        val rs = srcs[0].logoInfo[0].color.substring(0,2)
        Log.e("TAG","r:$rs")
        val ri = rs.toInt(16)
        Log.e("TAG","ri:$ri")
        val r = ri.toFloat().div(255f)
        Log.e("TAG","r:$r")
        val g = srcs[0].logoInfo[0].color.substring(2,4).toInt(16).toFloat().div(255f)
        val b = srcs[0].logoInfo[0].color.substring(4,6).toInt(16).toFloat().div(255f)
        val a = srcs[0].logoInfo[0].opacity.toFloat()/100
        Log.e("TAG","a:$a")
        //颜色矩阵
        val rtog = ColorMatrix(
            floatArrayOf(
                r, 0f, 0f, 0f, 0f,//RED
                0f, g, 0f, 0f, 0f,//GREEN
                0f, 0f, b, 0f, 0f,//BLUE
                0f, 0f, 0f, a, 0f//ALPHA
            ))
        val colorArray = rtog.array
        colorArray.forEach {
            Log.e("TAG","value:${it}")
        }
        paint.colorFilter = ColorMatrixColorFilter(rtog)

        val scaleX = (srcs[0].logoInfo[0].logoWidth.toFloat().div(logo.width.toFloat()))
        val scaleY = (srcs[0].logoInfo[0].logoHeight.toFloat().div(logo.height.toFloat()))
        val matrix = Matrix()
        matrix.postRotate(srcs[0].logoInfo[0].rotate.toFloat())
        matrix.postScale(scaleX, scaleY)

        matrix.postTranslate(
            srcs[0].logoInfo[0].startX.toFloat(),
            srcs[0].logoInfo[0].startY.toFloat()
        )
        canvas.drawBitmap(logo, matrix, paint)
        canvas.save()
        canvas.restore()
        return newBitmap
    }

    private fun initData() {
        srcs.add(WatermarkData(540,"http://qiniu.yuzhua.info/toolbox/20200925/6fd6bbff15993d27858935eb5e136f61.png",850,
            arrayListOf(
                LogoInfo("666666",86,"http://toolbox.yuzhua-test.com/client/img/logo.6cf8570.png",86,10,45,100,187),
                LogoInfo("000000",86,"http://toolbox.yuzhua-test.com/client/img/logo.6cf8570.png",86,40,0,324,187)/*,
                LogoInfo("000000",86,"http://toolbox.yuzhua-test.com/client/img/logo.6cf8570.png",86,40,0,324,187),
                LogoInfo("000000",86,"http://toolbox.yuzhua-test.com/client/img/logo.6cf8570.png",86,40,0,324,187)*/
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
