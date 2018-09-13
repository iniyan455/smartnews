package iniyan.com.smartnews.common

import iniyan.com.smartnews.Interface.NewsService
import iniyan.com.smartnews.Remote.RetrofitClient

object Common {
    val BASE_URL = "https://newsapi.org/"
    val API_KEY = "00320d4b86af4cb9bf0e432fd6f063e3"
    val countryhashMap:HashMap<String,String> = HashMap<String,String>() //define empty hashmap
    val languagehashMap:HashMap<String,String> = HashMap<String,String>() //define empty hashmap


    val newsService: NewsService get() = RetrofitClient.getClient(BASE_URL).create(NewsService::class.java)


    fun getNewsApI(source: String): String {
        val apiUrl = StringBuilder("https://newsapi.org/v2/top-headlines?sources=")
                .append(source)
                .append("&apiKey=")
                .append(API_KEY)
                .toString()
        return apiUrl
    }



    fun  hashmap_Country():HashMap<String,String>{
        countryhashMap.put("us","United States")
        countryhashMap.put("en","English")
        countryhashMap.put("no","Norway")
        countryhashMap.put("au","Australia")
        countryhashMap.put("it","Italy")
        countryhashMap.put("pk","Pakistan")
        countryhashMap.put("gb","United Kingdom")
        countryhashMap.put("de","Germany")
        countryhashMap.put("br","Brazil")
        countryhashMap.put("ca","Canada")
        countryhashMap.put("es","Spain")
        countryhashMap.put("fr","France")
        countryhashMap.put("sa","Saudi Arabia")
        countryhashMap.put("fr","France")
        countryhashMap.put("in","India")
        countryhashMap.put("ru","Russian Federation")
        countryhashMap.put("se","Sweden")
        countryhashMap.put("ar","Argentina")
        countryhashMap.put("nl","Netherlands")
        countryhashMap.put("ie","Ireland")
        countryhashMap.put("is","Iceland")
        countryhashMap.put("zh","Chinese")
        countryhashMap.put("za","South Africa")

        return countryhashMap

    }

    fun  hashmap_Language():HashMap<String,String>{
        languagehashMap.put("he","Hebrew")
        languagehashMap.put("zh","Zhuang,Chuang")
        languagehashMap.put("de","German")
        languagehashMap.put("en","English")
        languagehashMap.put("se","Sami Sweden")
        languagehashMap.put("ar","Northern Sami")
        languagehashMap.put("nl","Dutch")
        languagehashMap.put("ru","Russian")
        languagehashMap.put("no","Norwegian")
        languagehashMap.put("es","Spain")
        languagehashMap.put("fr","France")
        languagehashMap.put("sa","Spanish")
        languagehashMap.put("fr","France")
        languagehashMap.put("ud","French")
        languagehashMap.put("pt","Portuguese")
        languagehashMap.put("it","Italian")
        languagehashMap.put("ud","Urdu")
        return languagehashMap

    }





}