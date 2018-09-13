package iniyan.com.smartnews.Adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import iniyan.com.smartnews.Adapter.ViewHolder.ListSourceViewHolder
import iniyan.com.smartnews.Interface.ItemClickListener
import iniyan.com.smartnews.ListNews
import iniyan.com.smartnews.Model.websitebean
import iniyan.com.smartnews.NetworkConnectivity.NetworkReceiver.Companion.getConnectivityStatusString
import iniyan.com.smartnews.R
import iniyan.com.smartnews.common.Common.countryhashMap
import iniyan.com.smartnews.common.Common.hashmap_Country
import iniyan.com.smartnews.common.Common.hashmap_Language

class ListSourceAdapter(private val context: Context, private val webSite: websitebean) :
        RecyclerView.Adapter<ListSourceViewHolder>() {
    private val mColorGenerator = ColorGenerator.DEFAULT
    private var mDrawableBuilder: TextDrawable? = null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListSourceViewHolder {
   val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.source_news_layout, parent, false)
        hashmap_Country()
        hashmap_Language()
        return ListSourceViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return webSite.sources.size
    }

    override fun onBindViewHolder(holder: ListSourceViewHolder, position: Int) {
        holder.source_title.text = webSite.sources[position].name
        holder.source_title_category.text = "Category : ${webSite.sources[position].category}"
        holder.source_title_language.text = "Language : ${webSite.sources[position].language}"


        for( entry in countryhashMap.entries){


            println("Element at key $entry = ${countryhashMap[entry.key]} +${countryhashMap[entry.value]}")

            if(entry.key.equals(webSite.sources[position].country))
                holder.source_title_country.text = "Country : ${entry.value}"

                 //   holder.source_title_country.text = "Country : ${webSite.sources[position].country}"
            }


        for( entry in hashmap_Language().entries){


            println("Element at key $entry = ${countryhashMap[entry.key]} +${countryhashMap[entry.value]}")

            if(entry.key.equals(webSite.sources[position].language))
                holder.source_title_language.text = "Language : ${entry.value}"

            //      holder.source_title_language.text = "Language : ${webSite.sources[position].language}"

        }







        var letter = ""

        if (webSite.sources[position].name != null && !webSite.sources[position].name!!.isEmpty()) {
            letter = webSite.sources[position].name!!.substring(0, 1)
        }

        val color = mColorGenerator.getRandomColor()

        // Create a circular icon consisting of  a random background colour and first letter of title
        mDrawableBuilder = TextDrawable.builder()
                .buildRound(letter, color)
        holder.source_title_Image.setImageDrawable(mDrawableBuilder)







        holder.setItemClickListener(object : ItemClickListener {



            override fun OnClick(view: View, position: Int) {


             //   Toast.makeText(context, "Will be Implement in next tutorial", Toast.LENGTH_SHORT).show()
                val status = getConnectivityStatusString(context)

                if(!status.equals("Not connected to Internet")) {
                    val intent = Intent(context, ListNews::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("source", webSite.sources[position].id)
                    context.startActivity(intent)



                }else
                    Toast.makeText(context,"Not Connected Internet",Toast.LENGTH_SHORT).show()
            }


        })
    }



}