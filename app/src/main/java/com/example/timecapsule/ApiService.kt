import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    //회원가입 POST 요청 정의
    @POST("api/signup")
    fun signUp(@Body signUpRequest: SignUpRequest): Call<ResponseBody>

    //회원가입 데이터
    data class SignUpRequest(
        @SerializedName("username") val username: String,
        @SerializedName("password") val password: String
    )
}



