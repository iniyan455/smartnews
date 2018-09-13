package iniyan.com.smartnews.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class sourcebean(
                      var id: String?,
                      var name: String?,
                      var description: String?,
                      var category: String?,
                      var language: String?,
                      var country: String?,
                      var url: String?
                    )


