import com.example.timecapsule.HomeTimeCapsuleDetail
import com.example.timecapsule.UnviewableCapsule
import com.example.timecapsule.ViewableCapsule
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    //회원가입 POST 요청
    @POST("api/signup")
    fun signUp(@Body userRequest: UserRequest): Call<ResponseBody>

    //로그인 POST 요청
    @POST("api/login")
    fun login(@Body userRequest: UserRequest): Call<ResponseBody>

    //글쓰기 POST 요청
    @Multipart
    @POST("api/timecapsules/")
    fun writeTimeCapsule(
        @Header("Authorization") authorization: String?,
        @Part imageFile: MultipartBody.Part?,
        @Part("timecapsuleReqDto") timecapsuleReqDto: RequestBody
    ): Call<ResponseBody>

    //열람 가능한 타임캡슐 카테고리별로 GET 요청
    @GET("api/timecapsules/view")
    fun getViewableTimeCapsules(
        @Header("Authorization") authorization: String?,
        @Query("category") category: String
    ): Call<List<ViewableCapsule>>

    //열람 가능한 타임캡슐 id별로 GET 요청
    @GET("api/timecapsules/viewable/{id}")
    fun getViewableDetail(
        @Header("Authorization") authorization: String?,
        @Path("id") id: Int
    ): Call<ViewableCapsule>

    //열람 불가능한 타임캡슐 카테고리별로 GET 요청
    @GET("api/timecapsules/unviewable")
    fun getUnviewableTimeCapsules(
        @Header("Authorization") authorization: String?,
        @Query("category") category: String
    ): Call<List<UnviewableCapsule>>

    //타임캡슐 id별로 GET 요청
    @GET("api/timecapsules/main/{id}")
    fun getHomeTimeCapsule(
        @Header("Authorization") authorization: String?,
        @Path("id") id: Int
    ): Call<HomeTimeCapsuleDetail>

    //타임캡슐 id별로 DELETE 요청
    @DELETE("api/timecapsules/{id}")
    fun deleteTimeCapsule(
        @Header("Authorization") authorization: String?,
        @Path("id") id: Int,
    ): Call<ResponseBody>


    //회원가입 데이터
    data class UserRequest(
        @SerializedName("username") val username: String,
        @SerializedName("password") val password: String
    )
}




