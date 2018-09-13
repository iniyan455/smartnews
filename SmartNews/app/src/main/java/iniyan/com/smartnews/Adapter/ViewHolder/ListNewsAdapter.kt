package iniyan.com.smartnews.Adapter.ViewHolder

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.squareup.picasso.Picasso
import iniyan.com.smartnews.Interface.ItemClickListener
import  iniyan.com.smartnews.Model.Article
import iniyan.com.smartnews.NetworkConnectivity.NetworkReceiver.Companion.getConnectivityStatusString
import iniyan.com.smartnews.NewsDetails
import iniyan.com.smartnews.R
import iniyan.com.smartnews.common.ISO8601Parser
import java.text.ParseException
import java.util.*

class ListNewsAdapter(val articleList: MutableList<Article>, private val context: Context) : RecyclerView.Adapter<ListNewsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListNewsViewHolder {


        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.news_layout, parent, false)
        return ListNewsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return articleList.size
    }

    override fun onBindViewHolder(holder: ListNewsViewHolder, position: Int) {

        Picasso.get().load(articleList[position].urlToImage).into(holder.article_image)

        if (articleList[position].title!!.length > 65) {
            holder.article_title.text = articleList[position].title!!.substring(0, 65) + "..."
        } else {
            holder.article_title.text = articleList[position].title!!
        }

        if (articleList[position].publishedAt != null) {

            var date: Date? = null

            try {


                date = ISO8601Parser.parse(articleList[position].publishedAt!!)

                if(date.time!=null) {
                    holder.article_time!!.setReferenceTime(date.time)
                }

            } catch (e: ParseException) {
                e.printStackTrace()
            }



        }


        //set Event Click

        holder.setItemClickListener(object : ItemClickListener {
            override fun OnClick(view: View, position: Int) {
                //implement soon
                val status = getConnectivityStatusString(context)

                if(!status.equals("Not connected to Internet")) {


                val details = Intent(context, NewsDetails::class.java)
                    details.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    details.putExtra("webURL", articleList[position].url)
                context.startActivity(details)
                }else
                    Toast.makeText(context,"Not Connected Internet",Toast.LENGTH_SHORT).show()

            }

        })


    }


}