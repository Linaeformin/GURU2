
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY  // BODY 레벨로 설정하여 요청/응답의 본문 내용까지 로그로 출력
    }

    private const val BASE_URL = "https://port-0-guru2-backend-g0424l70py8py.gksl2.cloudtype.app/" // 실제 API 기본 URL로 대체

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder().addInterceptor(logging).build()) // 로깅 인터셉터 추가
        .build()

    val Service: ApiService= retrofit.create(ApiService::class.java)
}



