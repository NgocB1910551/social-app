package com.example.socialapp.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
        "Content-Type:application/json",
        "Authorization:key=AAAAyao66lk:APA91bGumhiZKahUBoinFiDV3NK22Hm-eUeCLbAvRO6jcihLs_WQ0OJVDOHZKzKwM7lL8uXpwZwsfiW7ALGyH1zjgglu0qa4g7rvtGMUzMvsyS5lVi7CjnkdlRQWvWjpKiJvIHSE3jla"
    })
    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}
