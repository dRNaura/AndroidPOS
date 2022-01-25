package com.pos.salon.client;


import android.content.Context;
import android.content.SharedPreferences;
import com.pos.salon.utilConstant.AppConstant;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    public static Retrofit getClient(Context context) {
        Retrofit retrofit = null;
        if (context != null) {

            if (AppConstant.isNetworkAvailable(context)) {

                OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
                OkHttpClient client = httpClient.build();
                retrofit = new Retrofit.Builder()
                        .baseUrl(AppConstant.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .build();

                return retrofit;
            } else {

            }
        }

        return retrofit;
    }


    public static Retrofit getClientToken(Context context) {

        final SharedPreferences sharedPreferences=context.getSharedPreferences("login",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        Retrofit retrofit = null;
        if (context != null) {

            if (AppConstant.isNetworkAvailable(context)) {

                OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
                httpClient.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Interceptor.Chain chain) throws IOException {

                        Request original = chain.request();

                        Request request = original.newBuilder()
                                .header("Content-Type", "application/json")
                                .header("Accept", "application/json")
                              //  .header("Authorization", "Bearer "+AppConstant.TEMP_AUTH)
                                .header("Authorization", "Bearer "+sharedPreferences.getString("token",""))
                                .method(original.method(), original.body())
                                .build();

                        return chain.proceed(request);
                    }
                });
//
                OkHttpClient client = httpClient.build();
                retrofit = new Retrofit.Builder()
                        .baseUrl(AppConstant.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .build();




                return retrofit;
            } else {

            }
        }

        return retrofit;
    }

}
