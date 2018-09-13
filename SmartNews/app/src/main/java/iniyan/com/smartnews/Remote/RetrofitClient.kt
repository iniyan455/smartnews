package iniyan.com.smartnews.Remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.security.cert.CertificateException

object RetrofitClient {


    private var retrofit: Retrofit? = null
    fun getClient(baseUrl: String?): Retrofit {
        if (retrofit == null) {

            retrofit = Retrofit.Builder().baseUrl(baseUrl!!).addConverterFactory(GsonConverterFactory.create())
                    .client(unsafeOkHttpClient.build()).build()
        }

        return retrofit!!

    }
    val unsafeOkHttpClient: OkHttpClient.Builder
        get() {

            try {
                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                    }

                    override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                        return arrayOf()
                    }
                })
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, trustAllCerts, java.security.SecureRandom())
                val sslSocketFactory = sslContext.socketFactory

                val builder = OkHttpClient.Builder()
                builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                builder.hostnameVerifier { hostname, session -> true }
                return builder
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

        }

}