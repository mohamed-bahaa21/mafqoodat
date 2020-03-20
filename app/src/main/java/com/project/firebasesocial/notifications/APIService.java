package com.project.firebasesocial.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAIDG2phE:APA91bGuznNgnOYYGzhnGVSYNyh3vTIEL5g0a7ZB9ctBhUFCx2CWorv5VtODG1D0Vn1Ag_9KV8Vkfmo7MzRONTcym2IiuoYVYbE82bkqc_dkatG_v52OlO3LE1Er2je2ZADrmvPNkrLR"
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);

}
