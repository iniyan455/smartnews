package iniyan.com.smartnews

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_news_details.*


class NewsDetails : AppCompatActivity() {

val TAG="NewsDetails"
    lateinit var dialog: AlertDialog
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_details)

        dialog = SpotsDialog(this)
      //  dialog.setCancelable(false)

        dialog.show()




        //webview
        webView!!.settings.javaScriptEnabled=true
        webView!!.webChromeClient = WebChromeClient()
       webView!!.settings.javaScriptCanOpenWindowsAutomatically=true
//        webView!!.loadUrl("http://www.etownpanchayat.com/PublicServices/WebView/LandingPage.aspx?RType=DCB&qTaxType=Property&qDistrict=Krishnagiri&qPanchayat=Mathigiri&qFinYear=2018-2019");
//        val intent = Intent(Intent.ACTION_VIEW)
//        intent.data = Uri.parse("http://www.etownpanchayat.com/PublicServices/WebView/LandingPage.aspx?RType=DCB&qTaxType=Property&qDistrict=Krishnagiri&qPanchayat=Mathigiri&qFinYear=2018-2019");
//        startActivity(intent)

//        webView!!.settings.allowFileAccessFromFileURLs=true
//        webView!!.settings.allowContentAccess=true


       webView!!.webViewClient = object : WebViewClient(){


            override  fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
               // super.onPageFinished(view, url)
                dialog.dismiss()
            }
        }


        //https@ //docs.google.com/viewer?url=   view pdf

        if (intent != null) {
         //   if (!intent.getStringExtra("webURL").isEmpty()){
                val url=intent.getStringExtra("webURL")


            if(!url.isEmpty()){
                Log.e(TAG,"url"+url)
             //   webView!!.loadUrl("http://www.etownpanchayat.com/PublicServices/WebView/LandingPage.aspx?RType=DCB&qTaxType=Property&qDistrict=Krishnagiri&qPanchayat=Mathigiri&qFinYear=2018-2019")
                     webView!!.loadUrl(url)
               // webView!!.loadUrl("http@//www.etownpanchayat.com/PublicServices/WebView/Report.aspx")
//                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.etownpanchayat.com/PublicServices/WebView/LandingPage.aspx?RType=DCB&qTaxType=Property&qDistrict=Krishnagiri&qPanchayat=Mathigiri&qFinYear=2018-2019"))
//                startActivity(browserIntent)
//
//                val intent = Intent(Intent.ACTION_VIEW)
//
//                intent.setDataAndType(Uri.parse("http@//www.etownpanchayat.com/PublicServices/WebView/Report.aspx"), "text/html")
//                startActivity(intent)
                }

          //  }

           // Log.e(TAG,""+intent.getStringExtra("webURL"))
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && this.webView!!.canGoBack()) {
            this.webView!!.goBack()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

}
