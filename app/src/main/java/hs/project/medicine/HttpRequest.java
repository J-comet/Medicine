package hs.project.medicine;


import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    // Request 메소드 정의
    public enum HttpType {
        GET, POST
    }

    public static String getRequest(String url, HttpType method, Map<String, Object> parameter) {
        try {
            String param = null;
            if (parameter != null) {
                StringBuffer sb = new StringBuffer();
                for (String key : parameter.keySet()) {
                    if (sb.length() > 0) {
                        sb.append("&");
                    }
                    sb.append(key);
                    sb.append("=");
                    sb.append(parameter.get(key));
                }
                param = sb.toString();
            } else {
                param = "";
            }

            // Http method가 GET 방식의 경우 파라미터를 url 주소 뒤에 붙인다.
            if (HttpType.GET.equals(method)) {
                if (url.contains("?")) {
                    url += "&" + param;
                } else {
                    url += "?" + param;
                }
            }
            URL uri = new URL(url); // url 통해 HttpURLConnection 클래스를 생성

            Log.e("hs", "request_url=" + url);

            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) uri.openConnection();  // 해더의 메소드를 정의한다.
            connection.setRequestMethod(method.toString()); // 해더의 ContentType를 정의한다.
            connection.setRequestProperty("ContentType", "application/x-www-form-urlencoded");

            // HttpType POST 방식의 경우, 해더 아래에
            if (HttpType.POST.equals(method)) {

                // 커넥션의 header 밑의 Stream 을 사용. (GET의 경우는 필요 없음)
                connection.setDoOutput(true);
                try (DataOutputStream output = new DataOutputStream(connection.getOutputStream())) {
                    output.writeBytes(param);
                    output.flush();
                }
            }

            int code = connection.getResponseCode();
            System.out.println(code);
            // 스트림으로 반환 결과값을 받는다.
            try (BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                StringBuffer buffer = new StringBuffer();
                while ((line = input.readLine()) != null) {
                    buffer.append(line);
                }
                return buffer.toString();
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}

