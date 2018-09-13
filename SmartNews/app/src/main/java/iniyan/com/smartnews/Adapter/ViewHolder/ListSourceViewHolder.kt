package iniyan.com.smartnews.Adapter.ViewHolder

import android.support.v7.widget.RecyclerView
import android.view.View
import iniyan.com.smartnews.Interface.ItemClickListener
import kotlinx.android.synthetic.main.source_news_layout.view.*

public class ListSourceViewHolder(itemViewHolder: View) : RecyclerView.ViewHolder(itemViewHolder),
        View.OnClickListener {

    private lateinit var itemClickListener: ItemClickListener

    var source_title = itemView.source_news_name
    var source_title_language = itemView.source_news_language
    var source_title_category = itemView.source_news_category
    var source_title_country = itemView.source_news_country
    var source_title_Image= itemView.news_image




    init {
        itemView.setOnClickListener(this)
    }

    fun setItemClickListener(itemClickListener: ItemClickListener) {

        this.itemClickListener = itemClickListener
    }

    override fun onClick(v: View?) {

        itemClickListener.OnClick(v!!, adapterPosition)

    }

}