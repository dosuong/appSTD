package com.example.qldrl.FCM;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Notification {
    private static final String postULR = "https://fcm.googleapis.com/v1/projects/miniapptest-705a9/messages:send";
    private String userFcmToken;
    private String title, body;
    private Context context;

    public Notification(String userFcmToken, String title, String body, Context context) {
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.context = context;
    }

    public void sendNotification() {
        RequestQueue responseQueue = Volley.newRequestQueue(context);
        JSONObject object = new JSONObject();
        try {
            JSONObject notification = new JSONObject();
            notification.put("title", title);
            notification.put("body", body);

            JSONObject messaging = new JSONObject();
            messaging.put("token", userFcmToken);
            messaging.put("notification", notification);

            object.put("message", messaging);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    postULR,
                    object,
                    response -> {
                        // Xử lý phản hồi thành công từ server
                        // Ví dụ: Hiển thị thông báo thành công, ghi log, v.v.
                        // Log.d("FCM", "Notification sent successfully");
                    },
                    volleyError -> {
                        // Xử lý lỗi khi gửi thông báo
                        // Ví dụ: Hiển thị thông báo lỗi cho người dùng, ghi log lỗi, v.v.
                        // Log.e("FCM", "Error sending notification: " + volleyError.getMessage());
                    }
            ) {
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header = new HashMap<>();
                    AccessToken accessToken = new AccessToken();
                    String accesskey = accessToken.getAccessToken();
                    header.put("Content-Type", "application/json; charset=utf-8");
                    header.put("Authorization", "Bearer " + accesskey);
                    return header;
                }
            };

            responseQueue.add(request);

        } catch (JSONException e) {
            // Xử lý lỗi JSON

        }
    }
}