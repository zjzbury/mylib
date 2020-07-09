package red.bury.mylibrary;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {
    public final static List<Cookie> cookieStore = new ArrayList<>();
    private final static OkHttpClient client = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
//                    if (list!=null&&list.size()>0){
//                        Cookie cookie = list.get(0);
//                        if (!cookie.name().equals("JSESSIONID")){
//                            SharedPreferences login = MyApplication.getInstance().getSharedPreferences("login", 0);
//                            Map<String, String> map = new HashMap<>();
//                            map.put("username",  login.getString("username",""));
//                            map.put("password",     login.getString("passwd", ""));
//                            HttpUtil.requestPost(UrlConfig.LOGIN, map, new Callback() {
//                                @Override
//                                public void success(String json) throws Exception {
//
//                                }
//
//                                @Override
//                                public void fail(String error) {
//
//                                }
//                            });
//                        }
//                    }
//
//                    cookieStore.put(httpUrl.host(), list);
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                    return cookieStore;
                }
            })
              .build();

    public static void requestPost(String url, Map<String, String> reqData, final Callback callback) {
        FormBody.Builder formBody = new FormBody.Builder();
        for (Map.Entry entry : reqData.entrySet()) {
            formBody.add(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
        }
        Request request = new Request.Builder()
                .url( url)
                .post(formBody.build())
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                AsyncTask asyncTask = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        callback.fail(e.getMessage());
                    }
                };
                asyncTask.execute();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String data = response.body().string();
                AsyncTask asyncTask = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        try {
                            callback.success(data);
                        } catch (Exception e) {
                        }
                    }
                };
                asyncTask.execute();
            }
        });
    }

    /**
     * 发送语言消息的网络请求
     * @param url
     * @param path
     * @param callback
     */
    public static void postFile(String url, String path, final Callback callback){
        File file = new File(path);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("uploadfile", file.getName(),
                        RequestBody.create(MediaType.parse("multipart/form-data"), file))
                .build();
        Request request = new Request.Builder()
                .header("Authorization", "ClientID" + UUID.randomUUID())
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.fail(e.toString());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                try {
                    callback.success(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public static void requestGet(String url, Map<String, String> reqData, final Callback callback) {
        String copyurl = url;
        if (reqData.size() > 0) {
            ArrayList<String> keys = new ArrayList<>(reqData.keySet());
            for (int i = 0; i < reqData.size(); i++) {
                if (i == 0) copyurl += "?";
                if (i == reqData.size() - 1) {
                    if (reqData.get(keys.get(i)) != null) {
                        copyurl += keys.get(i) + "=" + reqData.get(keys.get(i));
                    }
                } else {
                    if (reqData.get(keys.get(i)) != null) {
                        copyurl += keys.get(i) + "=" + reqData.get(keys.get(i)) + "&";
                    }
                }
            }
        }
        Request request = new Request.Builder()
                .url(copyurl)
                .get()
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull final IOException e) {
                AsyncTask asyncTask = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        callback.fail(e.getMessage());
                    }
                };
                asyncTask.execute();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String data = response.body().string();
                AsyncTask asyncTask = new AsyncTask() {
                    @Override
                    protected Object doInBackground(Object[] objects) {
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        try {
                            callback.success(data);
                        } catch (Exception e) {
                        }
                    }
                };
                asyncTask.execute();
            }
        });


    }


    public static void updateImage(String imagePath, String url, final Callback callback) {
        File file = new File(imagePath);
        RequestBody image = RequestBody.create(MediaType.parse("image/png"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", imagePath, image)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.fail(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String string = response.body().string();
                try {
                    callback.success(string);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }



    public static void get(String address, okhttp3.Callback callback)
    {
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            }
        });
    }
}
