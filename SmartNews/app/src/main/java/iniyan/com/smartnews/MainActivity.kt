package iniyan.com.smartnews

import `in`.galaxyofandroid.spinerdialog.OnSpinerItemClick
import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import com.irozon.sneaker.Sneaker
import dmax.dialog.SpotsDialog
import iniyan.com.smartnews.Adapter.ListSourceAdapter
import iniyan.com.smartnews.Interface.NewsService
import iniyan.com.smartnews.Model.sourcebean
import iniyan.com.smartnews.Model.websitebean
import iniyan.com.smartnews.common.Common
import iniyan.com.smartnews.common.Common.countryhashMap
import iniyan.com.smartnews.common.Common.hashmap_Country
import iniyan.com.smartnews.common.Common.hashmap_Language
import iniyan.com.smartnews.common.Common.languagehashMap
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.filter.*
import kotlinx.android.synthetic.main.filter.view.*
import retrofit2.Call
import retrofit2.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var toolbar: Toolbar? = null
    private val TAG = "MainActivity"
    lateinit var layoutManager: LinearLayoutManager

    var rootlayout:LinearLayout?=null
    private val validate_config = "validate"

    private val url_config = "url"

    private var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null
    var websitebeanList = ArrayList<websitebean>()
    var key = "";


    lateinit var bottom_dialog: BottomSheetDialog
    var sourcebeanList = ArrayList<sourcebean>()


    val language_arraylist = ArrayList<String>()
    val category_arraylist = ArrayList<String>()
    val country_arraylist = ArrayList<String>()
    val channel_arraylist = ArrayList<String>()

    internal lateinit var language: SpinnerDialog
    internal lateinit var categorySpinner: SpinnerDialog
    internal lateinit var countrySpinner: SpinnerDialog
    internal lateinit var channelNameSpinner: SpinnerDialog


    lateinit var mService: NewsService
    lateinit var adapter: ListSourceAdapter
    lateinit var dialog: AlertDialog
    internal var e: Exception? = null
//    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            val status = getConnectivityStatusString(this@MainActivity)
//            //   setSnackbarMessage(status!!, false)
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)


        if (getSupportActionBar() != null) {
//            getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar()!!.setDisplayShowHomeEnabled(true);
            getSupportActionBar()!!.setTitle(R.string.app_name);
        }

        //Init code
        Paper.init(this)

        //mService
        mService = Common.newsService

        swipe_to_refresh.setColorSchemeResources(R.color.colorPrimary, android.R.color.holo_blue_bright,
                android.R.color.holo_orange_dark,
                android.R.color.holo_green_dark
        )

        recycler_view_source_news.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        recycler_view_source_news.layoutManager = layoutManager

        /***  remote config **/
        val defaults = mapOf(
                "font_size" to 18,
                "font_color" to "#ff0000"
        )


        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()


        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()
        mFirebaseRemoteConfig!!.setConfigSettings(configSettings)

        fetchRemoteConfig()


        dialog = SpotsDialog(this@MainActivity)






        hashmap_Country();
        hashmap_Language();

        loadWebsiteSource(false)
        //Init Swipeview
        swipe_to_refresh.setOnRefreshListener {

            loadWebsiteSource(true)
        }


    }

    private fun runLayoutAnimation(websitebeanpojo: websitebean) {
        val context = recycler_view_source_news.getContext()
        val controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_left)

        adapter = ListSourceAdapter(baseContext, websitebeanpojo)
        adapter.notifyDataSetChanged()
        recycler_view_source_news.adapter = adapter


        recycler_view_source_news.setLayoutAnimation(controller)
        recycler_view_source_news.getAdapter().notifyDataSetChanged()
        recycler_view_source_news.scheduleLayoutAnimation()
    }

    private fun fetchRemoteConfig() {


        var cacheExpiration: Long = 1 // 1 hour in seconds.

        if (mFirebaseRemoteConfig!!.info.configSettings.isDeveloperModeEnabled) {
            cacheExpiration = 0
        }


        mFirebaseRemoteConfig!!.fetch(cacheExpiration)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
//                        Toast.makeText(this@MainActivity, "Fetch Succeeded",
//                                Toast.LENGTH_SHORT).show()


                        mFirebaseRemoteConfig!!.activateFetched()


                    } else {
//                        Toast.makeText(this@MainActivity, "Fetch Failed",
//                                Toast.LENGTH_SHORT).show()
                    }

                    val validateConfig = mFirebaseRemoteConfig!!.getString(validate_config)
                    val urlConfig = mFirebaseRemoteConfig!!.getString(url_config)
                    val validateversioncode = mFirebaseRemoteConfig!!.getString("versioncode")



                    try {
                        val pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
                        val version = pInfo.versionName;
                        val versioncode = pInfo.versionCode;

                        Log.e(TAG, validateConfig + "   --- " + versioncode + version + "" + validateversioncode)
                        if (validateConfig.toInt() > versioncode) {
                            if (version.equals(validateversioncode))
                            else
                                openAlertToUpdate(urlConfig)
                        }
                    } catch (e: PackageManager.NameNotFoundException) {
                        e.printStackTrace();
                    }



                    Log.e(TAG, validateConfig + "   --- " + urlConfig)
//                    Toast.makeText(this@MainActivity, validateConfig,
//                            Toast.LENGTH_LONG).show()
                }

    }


    fun openAlertToUpdate(url: String) {
        val dialog = AlertDialog.Builder(this)
        dialog.setCancelable(false)
                .setTitle("New Version Available")
                .setMessage("Please, update World Trending News to new version to continue App.")
                .setPositiveButton("Update",
                        object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface, which: Int) {
                                redirectStore(url)

                            }
                        }).setNegativeButton("No, thanks",
                        object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface, which: Int) {
                                finish()
                            }
                        }).create()
        dialog.show()


    }

    private fun redirectStore(updateUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish()
    }

    private fun loadWebsiteSource(isRefresh: Boolean) {
        if (!isRefresh) {
            val cache = Paper.book().read<String>("cache")
            if (cache != null && !cache.isBlank() && cache != "null") {


                //Read cache
                val webSite = Gson().fromJson<websitebean>(cache, websitebean::class.java)
                runLayoutAnimation(webSite)


                //try with catch to used for filter to add specific field in api

                try {
                    val response_website = Gson().fromJson<websitebean>(cache, websitebean::class.java)


                    //source list
                    val response_source = response_website!!.sources
                    for (i in response_source.indices) {
                        sourcebeanList.add(sourcebean(response_source.get(i).id, response_source.get(i).name,
                                response_source.get(i).description, response_source.get(i).category
                                , response_source.get(i).language, response_source.get(i).country,
                                response_source.get(i).url))


                        websitebeanList.add(websitebean("0", sourcebeanList))


                    }
                    //Category
                    System.out.println("category :" + response_source.get(0).category)


                    for (i in response_source.indices) {
                        val element = response_source.get(i).category
                        if (!category_arraylist.contains(element))
                            category_arraylist.add(element!!)

                    }
                    println("category" + category_arraylist.size)


                    //language
                    System.out.println("language :" + response_source.get(0).language)



                    for (i in response_source.indices) {
                        val element_country = response_source.get(i).country



                        for (entry in countryhashMap.entries) {


                            println("Element at key $entry = ${countryhashMap[entry.key]} +${countryhashMap[entry.value]}")

                            if (entry.key.equals(element_country))
                                if (!country_arraylist.contains(entry.value!!)) {


                                    country_arraylist.add(entry.value!!)
                                }
                        }


                    }

                    for (i in response_source.indices) {
                        val element_language = response_source.get(i).language



                        for (entry in languagehashMap.entries) {


                            println("Element at key $entry = ${languagehashMap[entry.key]} +${languagehashMap[entry.value]}")

                            if (entry.key.equals(element_language))
                                if (!language_arraylist.contains(entry.value!!)) {


                                    language_arraylist.add(entry.value!!)
                                }
                        }


                    }

//                    for (i in response_source.indices) {
//                        val element_language = response_source.get(i).language
//                        if (!language_arraylist.contains(element_language))
//                            language_arraylist.add(element_language!!)
//
//                    }
                    println("language" + language_arraylist.size)


                    //country
                    System.out.println("country :" + response_source.get(0).country)


                    for (i in response_source.indices) {
                        val element_country = response_source.get(i).country



                        for (entry in countryhashMap.entries) {


                            println("Element at key $entry = ${countryhashMap[entry.key]} +${countryhashMap[entry.value]}")

                            if (entry.key.equals(element_country))
                                if (!country_arraylist.contains(entry.value!!)) {


                                    country_arraylist.add(entry.value!!)
                                }
                        }


                    }

                    println("country" + country_arraylist.size)


                    //channel
                    System.out.println("channel name:" + response_source.get(0).name)


                    for (i in response_source.indices) {
                        val element_channel = response_source.get(i).name
                        if (!channel_arraylist.contains(element_channel))
                            channel_arraylist.add(element_channel!!)

                    }
                    println("name" + channel_arraylist.size)


                } catch (e: IOException) {
                    e.printStackTrace(); }


            } else {


                //Load Website and write cache
                dialog.show()
                //mservice new data
                mService.sources.enqueue(object : retrofit2.Callback<websitebean> {
                    override fun onFailure(call: Call<websitebean>?, t: Throwable?) {
                        Toast.makeText(baseContext, "Error", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }

                    override fun onResponse(call: Call<websitebean>?, response: Response<websitebean>?) {

//                        adapter = ListSourceAdapter(baseContext, response!!.body()!!)
//                        adapter.notifyDataSetChanged()
//                        recycler_view_source_news.adapter = adapter

                        runLayoutAnimation(response!!.body()!!)


                        //save to cache
                        //try with catch to used for filter to add specific field in api

                        try {
                            val response_website = response.body()


                            //source list
                            val response_source = response_website!!.sources

                            for (i in response_source.indices) {


                                sourcebeanList.add(sourcebean(response_source.get(i).id, response_source.get(i).name,
                                        response_source.get(i).description, response_source.get(i).category
                                        , response_source.get(i).language, response_source.get(i).country,
                                        response_source.get(i).url))

                            }

                            //Category
                            System.out.println("category :" + response_source.get(0).category)


                            for (i in response_source.indices) {
                                val element = response_source.get(i).category
                                if (!category_arraylist.contains(element))
                                    category_arraylist.add(element!!)

                            }
                            println("category" + category_arraylist.size)


                            //language
                            System.out.println("language :" + response_source.get(0).language)
                            for (i in response_source.indices) {
                                val element_language = response_source.get(i).language



                                for (entry in languagehashMap.entries) {


                                    println("Element at key $entry = ${languagehashMap[entry.key]} +${languagehashMap[entry.value]}")

                                    if (entry.key.equals(element_language))
                                        if (!language_arraylist.contains(entry.value!!)) {


                                            language_arraylist.add(entry.value!!)
                                        }
                                }


                            }

//
//                            for (i in response_source.indices) {
//                                val element_language = response_source.get(i).language
//                                if (!language_arraylist.contains(element_language))
//                                    language_arraylist.add(element_language!!)
//
//                            }
                            println("language" + language_arraylist.size)


                            //country
                            System.out.println("country :" + response_source.get(0).country)

//
//                            for (i in response_source.indices) {
//                                val element_country = response_source.get(i).country
//                                if (!country_arraylist.contains(element_country))
//                                    country_arraylist.add(element_country!!)
//
//                            }


                            for (i in response_source.indices) {
                                val element_country = response_source.get(i).country



                                for (entry in countryhashMap.entries) {


                                    println("Element at key $entry = ${countryhashMap[entry.key]} +${countryhashMap[entry.value]}")

                                    if (entry.key.equals(element_country))
                                        if (!country_arraylist.contains(entry.value!!)) {


                                            country_arraylist.add(entry.value!!)
                                        }
                                }


                            }


                            println("country" + country_arraylist.size)


                            //channel
                            System.out.println("channel name:" + response_source.get(0).name)


                            for (i in response_source.indices) {
                                val element_channel = response_source.get(i).name
                                if (!channel_arraylist.contains(element_channel))
                                    channel_arraylist.add(element_channel!!)

                            }
                            println("name" + channel_arraylist.size)


                        } catch (e: IOException) {
                            e.printStackTrace(); }



                        Paper.book().write("cache", Gson().toJson(response.body()!!))
                        dialog.dismiss()
                    }

                })
            }


        } else {

            swipe_to_refresh.isRefreshing = true


            mService.sources.enqueue(object : retrofit2.Callback<websitebean> {
                override fun onFailure(call: Call<websitebean>?, t: Throwable?) {
                    Toast.makeText(baseContext, "Error" + t, Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "" + t)
                }

                override fun onResponse(call: Call<websitebean>?, response: Response<websitebean>?) {

                    //     val response_source = response!!.body()!!.sources

                    runLayoutAnimation(response!!.body()!!)


                    //save to cache


                    Log.e(TAG, "else" + response.body()!!)


                    //try with catch to used for filter to add specific field in api

                    try {
                        val response_website = response.body()


                        //source list
                        val response_source = response_website!!.sources

                        for (i in response_source.indices) {
                            sourcebeanList.add(sourcebean(response_source.get(i).id, response_source.get(i).name,
                                    response_source.get(i).description, response_source.get(i).category
                                    , response_source.get(i).language, response_source.get(i).country,
                                    response_source.get(i).url))

                        }


                        //Category
                        System.out.println("category :" + response_source.get(0).category)


                        for (i in response_source.indices) {
                            val element = response_source.get(i).category
                            if (!category_arraylist.contains(element))
                                category_arraylist.add(element!!)

                        }
                        println("category" + category_arraylist.size)


                        //language
                        System.out.println("language :" + response_source.get(0).language)
                        for (i in response_source.indices) {
                            val element_language = response_source.get(i).language



                            for (entry in languagehashMap.entries) {


                                println("Element at key $entry = ${languagehashMap[entry.key]} +${languagehashMap[entry.value]}")

                                if (entry.key.equals(element_language))
                                    if (!language_arraylist.contains(entry.value!!)) {


                                        language_arraylist.add(entry.value!!)
                                    }
                            }


                        }

//
//                        for (i in response_source.indices) {
//                            val element_language = response_source.get(i).language
//                            if (!language_arraylist.contains(element_language))
//                                language_arraylist.add(element_language!!)
//
//                        }
                        println("language" + language_arraylist.size)


                        //country
                        System.out.println("country :" + response_source.get(0).country)

//
//                        for (i in response_source.indices) {
//                            val element_country = response_source.get(i).country
//                            if (!country_arraylist.contains(element_country))
//                                country_arraylist.add(element_country!!)
//
//                        }


                        for (i in response_source.indices) {
                            val element_country = response_source.get(i).country



                            for (entry in countryhashMap.entries) {


                                println("Element at key $entry = ${countryhashMap[entry.key]} +${countryhashMap[entry.value]}")

                                if (entry.key.equals(element_country))
                                    if (!country_arraylist.contains(entry.value!!)) {


                                        country_arraylist.add(entry.value!!)
                                    }
                            }


                        }

                        println("country" + country_arraylist.size)


                        //channel
                        System.out.println("channel name:" + response_source.get(0).name)


                        for (i in response_source.indices) {
                            val element_channel = response_source.get(i).name
                            if (!channel_arraylist.contains(element_channel))
                                channel_arraylist.add(element_channel!!)

                        }
                        println("name" + channel_arraylist.size)


                    } catch (e: IOException) {
                        e.printStackTrace(); }





                    Paper.book().write("cache", Gson().toJson(response.body()!!))

                    swipe_to_refresh.isRefreshing = false
                }

            })


        }

    }


//    override fun onResume() {
//        super.onResume()
//        registerInternetCheckReceiver()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        unregisterReceiver(broadcastReceiver)
//    }
//
//    private fun registerInternetCheckReceiver() {
//        val internetFilter = IntentFilter()
//        internetFilter.addAction("android.net.wifi.STATE_CHANGE")
//        internetFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
//        registerReceiver(broadcastReceiver, internetFilter)
//    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filter -> {
                openDialog();


                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }


    private fun openDialog() {
        val view = layoutInflater.inflate(R.layout.filter, null)
        bottom_dialog = BottomSheetDialog(this)
        bottom_dialog.setContentView(view)

         rootlayout = view.findViewById(R.id.rootlayout) as LinearLayout


        val txt_Category = view.findViewById(R.id.category) as TextView
        val txt_Language = view.findViewById(R.id.language) as TextView
        val txt_Country = view.findViewById(R.id.country) as TextView
        val txt_Channel = view.findViewById(R.id.channel) as TextView
        val btnFilter = view.findViewById(R.id.filter_submit) as Button

        val img_Category = view.findViewById(R.id.category_img) as ImageView
        val img_Language = view.findViewById(R.id.language_img) as ImageView
        val img_Country = view.findViewById(R.id.country_img) as ImageView
        val img_Channel = view.findViewById(R.id.channel_img) as ImageView


        img_Category.setOnClickListener {
            categorySpinner.showSpinerDialog()


        }
        img_Language.setOnClickListener {
            language.showSpinerDialog()

        }

        img_Country.setOnClickListener {

            countrySpinner.showSpinerDialog()

        }
        img_Channel.setOnClickListener {
            channelNameSpinner.showSpinerDialog()


        }


        //OnClick For TextViews
        txt_Category.setOnClickListener {
            categorySpinner.showSpinerDialog()

            //dialog.dismiss()
        }
        txt_Language.setOnClickListener {
            language.showSpinerDialog()
            // dialog.dismiss()
        }

        txt_Channel.setOnClickListener {
            channelNameSpinner.showSpinerDialog()

        }
        txt_Country.setOnClickListener {
            countrySpinner.showSpinerDialog()


        }


        //onClick Filter Button
        btnFilter.setOnClickListener {




            if (txt_Channel.text.toString().equals("Channel") &&
                     txt_Country.text.toString().equals("Country")
                    && txt_Category.text.toString().equals("Category") &&
                    txt_Language.text.toString().equals("Language")) {

           //   bottom_dialog.dismiss()
Toast.makeText(applicationContext,"Please Click AnyOne Atleast",Toast.LENGTH_SHORT).show();
                //Snackbar.make(swipe_to_refresh!!,"Please Click AnyOne Atleast",Snackbar.LENGTH_SHORT).show();
                return@setOnClickListener;
            }

















            if (!txt_Channel.text.toString().equals("Channel") &&
                    !TextUtils.isEmpty(txt_Channel.text.toString())
                    && !txt_Country.text.toString().equals("Country")
                    && !TextUtils.isEmpty(txt_Country.text.toString())
                    && !txt_Category.text.toString().equals("Category") &&
                    !TextUtils.isEmpty(txt_Category.text.toString()) &&
                    !txt_Language.text.toString().equals("Language") &&
                    !TextUtils.isEmpty(txt_Language.text.toString())) {

                var languagekey = "";

                var countrykey = "";
                for (entry in languagehashMap.entries) {

                    Log.e(TAG, languagekey + "" + entry.key + "" + entry.value);


                    if (entry.value.equals(txt_Language.text.toString())) {
                        languagekey = entry.key;

                        break;


                    }
                }
                for (entry in countryhashMap.entries) {

                    Log.e(TAG, countrykey + "" + entry.key + "" + entry.value);


                    if (entry.value.equals(txt_Country.text.toString())) {
                        countrykey = entry.key;

                        break;


                    }
                }
                if (languagekey.isEmpty() && countrykey.isEmpty()) {
                    sneakersMessage_Warning("No data Found Try Something !!")
                    return@setOnClickListener
                }

                val list = sourcebeanList.filter {
                    it.name.equals(txt_Channel.text.toString()) &&
                            it.language.equals(languagekey)
                            && it.category.equals(txt_Category.text.toString()) &&

                            it.country.equals(countrykey)
                }.map { it }
                Log.e(TAG, "1" + list);
                val list_converted_source = ArrayList<sourcebean>()
                list_converted_source.addAll(list);

                Log.e(TAG, "list size" + list_converted_source.size)
                runLayoutAnimation(websitebean("0", list_converted_source))


                bottom_dialog.dismiss()


                if (list_converted_source.size == 0)
                    sneakersMessage_Warning("No data Found Try Something !!")

            } else if (!txt_Channel.text.toString().equals("Channel") &&

                    //language missed
                    !TextUtils.isEmpty(txt_Channel.text.toString())
                    && !txt_Country.text.toString().equals("Country")
                    && !TextUtils.isEmpty(txt_Country.text.toString())
                    && !txt_Category.text.toString().equals("Category") &&
                    !TextUtils.isEmpty(txt_Category.text.toString())) {


                var countrykey = "";

                for (entry in countryhashMap.entries) {

                    Log.e(TAG, countrykey + "" + entry.key + "" + entry.value);


                    if (entry.value.equals(txt_Country.text.toString())) {
                        countrykey = entry.key;

                        break;


                    }
                }
                if (countrykey.isEmpty() ) {
                    sneakersMessage_Warning("No data Found Try Something !!")
                    return@setOnClickListener
                }


                val list = sourcebeanList.filter { it.name.equals(txt_Channel.text.toString()) && it.country.equals(countrykey) && it.category.equals(txt_Category.text.toString()) }.map { it }
                Log.e(TAG, "2" + list);
                val list_converted_source = ArrayList<sourcebean>()
                list_converted_source.addAll(list);

                Log.e(TAG, "list size" + list_converted_source.size)

                runLayoutAnimation(websitebean("0", list_converted_source))

                bottom_dialog.dismiss()
                if (list_converted_source.size == 0)
                    sneakersMessage_Warning("No data Found Try Something !!")
            } else if (!txt_Channel.text.toString().equals("Channel") &&


                    !TextUtils.isEmpty(txt_Channel.text.toString())
                    && !txt_Country.text.toString().equals("Country")
                    && !TextUtils.isEmpty(txt_Country.text.toString())
                    &&
                    !txt_Language.text.toString().equals("Language") &&
                    !TextUtils.isEmpty(txt_Language.text.toString())) {


                var countrykey = "";
                var languagekey = "";
                for (entry in countryhashMap.entries) {

                    Log.e(TAG, countrykey + "" + entry.key + "" + entry.value);


                    if (entry.value.equals(txt_Country.text.toString())) {
                        countrykey = entry.key;

                        break;


                    }
                }



                for (entry in languagehashMap.entries) {

                    Log.e(TAG, languagekey + "" + entry.key + "" + entry.value);


                    if (entry.value.equals(txt_Language.text.toString())) {
                        languagekey = entry.key;

                        break;


                    }
                }
                if (countrykey.isEmpty() && languagekey.isEmpty()) {
                    sneakersMessage_Warning("No data Found Try Something !!")
                    return@setOnClickListener
                }


                //category   missed
                val list = sourcebeanList.filter { it.name.equals(txt_Channel.text.toString()) && it.language.equals(languagekey) && it.country.equals(countrykey) }.map { it }

                val list_converted_source = ArrayList<sourcebean>()
                list_converted_source.addAll(list);


                runLayoutAnimation(websitebean("0", list_converted_source))

                bottom_dialog.dismiss()
                if (list_converted_source.size == 0)
                    sneakersMessage_Warning("No data Found Try Something !!")
            }



            if (!txt_Channel.text.toString().equals("Channel") &&
                    !TextUtils.isEmpty(txt_Channel.text.toString())
                    && !txt_Category.text.toString().equals("Category") &&
                    !TextUtils.isEmpty(txt_Category.text.toString()) &&
                    !txt_Language.text.toString().equals("Language") &&
                    !TextUtils.isEmpty(txt_Language.text.toString())) {


                var languagekey = "";

                for (entry in languagehashMap.entries) {

                    Log.e(TAG, languagekey + "" + entry.key + "" + entry.value);


                    if (entry.value.equals(txt_Language.text.toString())) {
                        languagekey = entry.key;

                        break;


                    }
                }


                if (languagekey.isEmpty()) {
                    sneakersMessage_Warning("No data Found Try Something !!")
                    return@setOnClickListener
                }
//country   missed


                val list = sourcebeanList.filter { it.name.equals(txt_Channel.text.toString()) && it.language.equals(languagekey) && it.category.equals(txt_Category.text.toString()) }.map { it }
                Log.e(TAG, "3" + list);
                val list_converted_source = ArrayList<sourcebean>()
                list_converted_source.addAll(list);

                Log.e(TAG, "list size" + list_converted_source.size)

                runLayoutAnimation(websitebean("0", list_converted_source))

                bottom_dialog.dismiss()
                if (list_converted_source.size == 0)
                    sneakersMessage_Warning("No data Found Try Something !!")
            } else if (!txt_Country.text.toString().equals("Country")
                    && !TextUtils.isEmpty(txt_Country.text.toString())
                    && !txt_Category.text.toString().equals("Category") &&
                    !TextUtils.isEmpty(txt_Category.text.toString()) &&
                    !txt_Language.text.toString().equals("Language") &&
                    !TextUtils.isEmpty(txt_Language.text.toString())) {
//channel    missed


                var languagekey = "";

                for (entry in languagehashMap.entries) {

                    Log.e(TAG, languagekey + "" + entry.key + "" + entry.value);


                    if (entry.value.equals(txt_Language.text.toString())) {
                        languagekey = entry.key;

                        break;


                    }
                }


                val list = sourcebeanList.filter { it.language.equals(languagekey) && it.category.equals(txt_Category.text.toString()) && it.country.equals(txt_Country.text.toString()) }.map { it }
                Log.e(TAG, "4" + list);
                val list_converted_source = ArrayList<sourcebean>()
                list_converted_source.addAll(list);

                Log.e(TAG, "list size" + list_converted_source.size)

                runLayoutAnimation(websitebean("0", list_converted_source))

                bottom_dialog.dismiss()
                if (list_converted_source.size == 0)
                    sneakersMessage_Warning("No data Found Try Something !!")
            } else if (!txt_Language.text.toString().equals("Language") && !TextUtils.isEmpty(txt_Language.text.toString())) {


                for (entry in languagehashMap.entries) {

                    Log.e(TAG, key + "" + entry.key + "" + entry.value);


                    if (entry.value.equals(txt_Language.text.toString())) {
                        key = entry.key;

                        break;


                    }
                }




                Log.e(TAG, txt_Language.text.toString() + "====" + key + languagehashMap.size)



                if (key.isEmpty()) {
                    sneakersMessage_Warning("No data Found Try Something !!")
                    return@setOnClickListener
                }
                val list = sourcebeanList.filter { it.language.equals(key) }.map { it }

                val list_converted_source = ArrayList<sourcebean>()
                list_converted_source.addAll(list);


                Log.e(TAG, "4 1" + list + sourcebeanList.get(0).language + "   " + txt_Language.text.toString() + key);
                Log.e(TAG, "list size" + list_converted_source.size)

                runLayoutAnimation(websitebean("0", list_converted_source))

                bottom_dialog.dismiss()
                if (list_converted_source.size == 0)
                    sneakersMessage_Warning("No data Found Try Something !!")



            } else if (!txt_Category.text.toString().equals("Category") && !TextUtils.isEmpty(txt_Category.text.toString())) {


                val list = sourcebeanList.filter { it.category.equals(txt_Category.text.toString()) }.map { it }
                Log.e(TAG, "5" + list);
                val list_converted_source = ArrayList<sourcebean>()
                list_converted_source.addAll(list);

                Log.e(TAG, "list size" + list_converted_source.size)

                runLayoutAnimation(websitebean("0", list_converted_source))

                bottom_dialog.dismiss()
                if (list_converted_source.size == 0)
                    sneakersMessage_Warning("No data Found Try Something !!")
            } else if (!txt_Country.text.toString().equals("Country") && !TextUtils.isEmpty(txt_Country.text.toString())) {

                for (entry in countryhashMap.entries) {

                    Log.e(TAG, key + "" + entry.key + "" + entry.value);


                    if (entry.value.equals(txt_Country.text.toString())) {
                        key = entry.key;

                        break;


                    }
                }




                Log.e(TAG, txt_Country.text.toString() + "====" + key + countryhashMap.size)


                if (key.isEmpty()) {
                    sneakersMessage_Warning("No data Found Try Something !!")
                    return@setOnClickListener
                }


                val list = sourcebeanList.filter { it.country.equals(key) }.map { it }

                val list_converted_source = ArrayList<sourcebean>()
                list_converted_source.addAll(list);
                Log.e(TAG, "6" + list);
                Log.e(TAG, "list size" + list_converted_source.size)

                runLayoutAnimation(websitebean("0", list_converted_source))

                bottom_dialog.dismiss()
                if (list_converted_source.size == 0)
                    sneakersMessage_Warning("No data Found Try Something !!")
            } else if (!txt_Channel.text.toString().equals("Channel") && !TextUtils.isEmpty(txt_Channel.text.toString())) {


                val list = sourcebeanList.filter { it.name.equals(txt_Channel.text.toString()) }.map { it }
                Log.e(TAG, "7" + list);
                val list_converted_source = ArrayList<sourcebean>()
                list_converted_source.addAll(list);

                Log.e(TAG, "list size" + list_converted_source.size)

                runLayoutAnimation(websitebean("0", list_converted_source))

                bottom_dialog.dismiss()
                if (list_converted_source.size == 0)
                    sneakersMessage_Warning("No data Found Try Something !!")


            }
            else
                Snackbar.make(rootlayout!!,"Please Click AnyOne Atleast",Snackbar.LENGTH_SHORT).show();


        }
        bottom_dialog.show()





        language = SpinnerDialog(this@MainActivity, language_arraylist as java.util.ArrayList<String>?, "Select or Search Language", R.style.DialogAnimations_SmileWindow, "Close")



        language.bindOnSpinerListener(object : OnSpinerItemClick {
            override fun onClick(item: String, position: Int) {
                txt_Language.setText(item)


            }
        })



        categorySpinner = SpinnerDialog(this@MainActivity, category_arraylist as java.util.ArrayList<String>?, "Select or Search Category", R.style.DialogAnimations_SmileWindow, "Close")



        categorySpinner.bindOnSpinerListener(object : OnSpinerItemClick {
            override fun onClick(item: String, position: Int) {
                txt_Category.setText(item)


            }
        })


        countrySpinner = SpinnerDialog(this@MainActivity, country_arraylist as java.util.ArrayList<String>?, "Select or Search Country", R.style.DialogAnimations_SmileWindow, "Close")



        countrySpinner.bindOnSpinerListener(object : OnSpinerItemClick {
            override fun onClick(item: String, position: Int) {
                txt_Country.setText(item)


            }
        })


        channelNameSpinner = SpinnerDialog(this@MainActivity, channel_arraylist as java.util.ArrayList<String>?, "Select or Search Channel", R.style.DialogAnimations_SmileWindow, "Close")



        channelNameSpinner.bindOnSpinerListener(object : OnSpinerItemClick {
            override fun onClick(item: String, position: Int) {
                txt_Channel.setText(item)


            }
        })


    }


    fun sneakersMessage_Warning(msg: String) {
        Sneaker.with(this)
                .setTitle("No Data Found!!")
                .setMessage(msg)
                .setDuration(4000) // Time duration to show
                .autoHide(true) // Auto hide Sneaker view
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .sneak(R.color.colorAccent);


    }


}
