package iniyan.com.smartnews

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.squareup.picasso.Picasso
import dmax.dialog.SpotsDialog
import iniyan.com.smartnews.Adapter.ViewHolder.ListNewsAdapter
import iniyan.com.smartnews.Interface.NewsService
import iniyan.com.smartnews.Model.Article
import iniyan.com.smartnews.Model.News
import iniyan.com.smartnews.common.Common
import kotlinx.android.synthetic.main.activity_list_news.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListNews : AppCompatActivity() {


    var source = ""
    lateinit var dialog: AlertDialog
    var webHotUrl: String? = ""
    lateinit var mService: NewsService


    lateinit var adapter: ListNewsAdapter


    lateinit var layoutManager: LinearLayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_news)

        //Init view
        mService = Common.newsService
        dialog = SpotsDialog(this)
        dialog.setCancelable(false)



        swipe_to_refresh.setColorSchemeResources( android.R.color.holo_blue_bright,
                android.R.color.holo_orange_dark,
                android.R.color.holo_green_dark,R.color.colorPrimary
        )

        diagonalLayout.setOnClickListener {

            val details = Intent(applicationContext, NewsDetails::class.java)
            details.putExtra("webURL", webHotUrl)
            startActivity(details)
        }


        list_news.setHasFixedSize(true)
        list_news.layoutManager = LinearLayoutManager(this)

        if (intent != null) {


            source = intent.getStringExtra("source")
            if (!source.isEmpty())
                loadNews(source, false)
        }
        swipe_to_refresh.setOnRefreshListener { loadNews(source, true) }

    }

    private fun loadNews(source: String?, isRefreshed: Boolean) {


        if (isRefreshed) {

            dialog.show()
            mService.getNewsFromSource(Common.getNewsApI(source!!)).enqueue(object : Callback<News> {
                override fun onFailure(call: Call<News>?, t: Throwable?) {


                    swipe_to_refresh.isRefreshing = false
                    dialog.dismiss()
                }

                override fun onResponse(call: Call<News>?, response: Response<News>?) {
                    swipe_to_refresh.isRefreshing = false
                    dialog.dismiss()

                    //get first articlehot news
                    Picasso.get().load(response!!.body()!!.articles!![0].urlToImage).into(top_image)
                    top_title.text = response.body()!!.articles!![0].title

                    top_author.text = response.body()!!.articles!![0].author

                    webHotUrl = response.body()!!.articles!![0].url

                    val removeFirstItem = response.body()!!.articles

                    //Because we get First item to hot news so we need to remove it
                    removeFirstItem!!.removeAt(0)


                    runLayoutAnimation(removeFirstItem)

                }
            })
        } else {


            swipe_to_refresh.isRefreshing = true
            mService.getNewsFromSource(Common.getNewsApI(source!!)).enqueue(object : Callback<News> {
                override fun onFailure(call: Call<News>?, t: Throwable?) {


                    swipe_to_refresh.isRefreshing = false
                    Toast.makeText(applicationContext,"Sorry , Something went Wrong In News App ",Toast.LENGTH_SHORT).show()

                }

                override fun onResponse(call: Call<News>?, response: Response<News>?) {

                    swipe_to_refresh.isRefreshing = false
                    //get first articlehot news

                    if(response!!.body()!!.articles!![0].urlToImage!=null && !response.body()!!.articles!![0].urlToImage!!.isEmpty() )
                    Picasso.get().load(response.body()!!.articles!![0].urlToImage).into(top_image)






                    if(response.body()!!.articles!![0].title!=null && !response.body()!!.articles!![0].title!!.isEmpty() )
                    top_title.text = response.body()!!.articles!![0].title

                    if(response.body()!!.articles!![0].author!=null && !response.body()!!.articles!![0].author!!.isEmpty() )
                        top_author.text = response.body()!!.articles!![0].author
                    if(response.body()!!.articles!![0].url!=null && !response.body()!!.articles!![0].url!!.isEmpty() ) {
                        webHotUrl = response.body()!!.articles!![0].url

                        val removeFirstItem = response.body()!!.articles

                        //Because we get First item to hot news so we need to remove it
                        removeFirstItem!!.removeAt(0)

                        runLayoutAnimation(removeFirstItem)

                    }else
                        Toast.makeText(applicationContext,"Sorry No Current News Updated",Toast.LENGTH_SHORT).show()
                }

            })

        }
    }

    private fun runLayoutAnimation(list_Article:MutableList<Article> ) {
        val context = list_news.getContext()
        val controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_left)

        adapter = ListNewsAdapter(list_Article, baseContext)
        adapter.notifyDataSetChanged()
        list_news.adapter = adapter


        list_news.setLayoutAnimation(controller)
        list_news.getAdapter().notifyDataSetChanged()
        list_news.scheduleLayoutAnimation()
    }
}
