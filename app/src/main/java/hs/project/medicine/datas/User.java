package hs.project.medicine.datas;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class User implements Serializable {
    String name;
//    String gender;
    String age;
    String relation;
//    boolean isCurrent;  // 현재 사용중인지 확인하기 위한 값

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getGender() {
//        return gender;
//    }
//
//    public void setGender(String gender) {
//        this.gender = gender;
//    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    /* 유저 구분을 위한 알람 키 */
    public String alarmKey() {
        String result = name + "/" + relation;
        return  result;
    }

    public String toJSON() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", getName());
//            jsonObject.put("gender", getGender());
            jsonObject.put("age", getAge());
            jsonObject.put("relation", getRelation());

            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}

