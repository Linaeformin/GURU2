import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    //회원가입 POST 요청 정의
    @POST("api/signup")
    fun signUp(@Body userRequest: UserRequest): Call<ResponseBody>

    //회원가입 데이터
    data class UserRequest(
        @SerializedName("username") val username: String,
        @SerializedName("password") val password: String
    )

    //로그인 POST 요청 정의
    @POST("api/login")
    fun login(@Body userRequest: UserRequest): Call<ResponseBody>

    //글쓰기 POST 요청 정의
    @Multipart
    @POST("api/timecapsules/")
    fun writeTimeCapsule(
        @Header("Authorization") authorization: String?,
        @Part imageFile: MultipartBody.Part?,
        @Part("timecapsuleReqDto") timecapsuleReqDto: RequestBody
    ): Call<TimeCapsuleResponse>

    // 글쓰기 데이터
    data class TimeCapsuleResponse(
        val title: String,
        val content: String,
        val category: String,
        val fileName: String,
        val viewableAt: String,
        val latitude: Double,
        val longitude: Double
    )
}




