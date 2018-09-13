package iniyan.com.smartnews.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Source{


    var id:String?=null
    var name:String?=null
    var description:String?=null
    var url:String?=null
    @SerializedName("category")
    @Expose
    var category:String?=null
        get() = field
        set(value) { field = value }
    var language:String?=null
    var country:String?=null

    constructor()



}