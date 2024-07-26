import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

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
}




