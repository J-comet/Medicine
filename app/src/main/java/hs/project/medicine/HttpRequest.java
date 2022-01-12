package hs.project.medicine;


import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import hs.project.medicine.datas.NaverGeocodingResult;
import hs.project.medicine.util.LogUtil;

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

    public static NaverGeocodingResult searchNaverGeocode(final String location) {

        NaverGeocodingResult geocodingResult = new NaverGeocodingResult();

        String clientId = MediApplication.ApplicationContext().getResources().getString(R.string.naver_client_id);
        String clientSecret = MediApplication.ApplicationContext().getResources().getString(R.string.naver_client_secret);

        try {
            String strSearch = URLEncoder.encode(location, "UTF-8");
            String apiURL = Config.URL_GET_NAVER_GEOCODE + "?query=" + strSearch; // json 결과

            LogUtil.e("apiURL=" + apiURL);

            //String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="+ text; // xml 결과
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);

            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
                response.append("\n");
            }
            br.close();

            String naverHtml = response.toString();

            Bundle bun = new Bundle();
            bun.putString("NAVER_HTML", naverHtml);
//                    Message msg = handler.obtainMessage();
//                    msg.setData(bun);
//                    handler.sendMessage(msg);

            //testText.setText(response.toString());
            System.out.println(response.toString());

            JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
            geocodingResult.setStatus(jsonObject.get("status").getAsString());
            geocodingResult.setErrorMessage(jsonObject.get("errorMessage").getAsString());

            if (!jsonObject.get("status").getAsString().equalsIgnoreCase("OK")) {
                return geocodingResult;
            }

            JsonObject metaObject = jsonObject.get("meta").getAsJsonObject();
            if (metaObject != null) {
                int totalCount = metaObject.get("totalCount").getAsInt();
                if (totalCount < 1) {
                    // OK 이더라도 totalCount 가 0 이면 못가져온 것이고, 이 경우 주소 검색이 안 된 경우
                    geocodingResult.setStatus("INVALID_ADDRESS");
                    return geocodingResult;
                }
                int count = metaObject.get("count").getAsInt();
                int page = metaObject.get("page").getAsInt();
            }

            // 주소가 혹시 여러개가 오더라도 어떤 것을 선택하거나 하게 할 수 없다. 그냥 첫번째 것을 사용
            JsonArray addressArray = jsonObject.get("addresses").getAsJsonArray();
            JsonElement addressEle = addressArray.get(0);
            JsonObject addressObject = addressEle.getAsJsonObject();
            geocodingResult.setJibunAddress(addressObject.get("jibunAddress").getAsString());
            geocodingResult.setRoadAddress(addressObject.get("roadAddress").getAsString());

            String strX = addressObject.get("x").getAsString();
            String strY = addressObject.get("y").getAsString();

            geocodingResult.setX(Double.parseDouble(strX));
            geocodingResult.setY(Double.parseDouble(strY));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return geocodingResult;
    }

}

