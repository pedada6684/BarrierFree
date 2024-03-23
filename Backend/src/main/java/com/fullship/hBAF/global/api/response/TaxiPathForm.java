package com.fullship.hBAF.global.api.response;

import com.fullship.hBAF.global.response.ErrorCode;
import com.fullship.hBAF.global.response.exception.CustomException;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;

@Data
@Builder
public class TaxiPathForm {

  List<String[]> geoCode;


  public static TaxiPathForm jsonToO(ResponseEntity<String> result) {
    List<String[]> geoCode = new ArrayList<>();
    try {
      JSONParser parser = new JSONParser();
      JSONObject object = (JSONObject) parser.parse(result.getBody());
      System.out.println(object);
      JSONArray features = (JSONArray) object.get("features");
      for (Object o : features) {
        JSONObject feature = (JSONObject) o;
        JSONObject geometry = (JSONObject) feature.get("geometry");
        JSONArray coordinates = (JSONArray) geometry.get("coordinates");
        if (coordinates.get(0).getClass() != Double.class) {
          for (Object value : coordinates) {
            JSONArray coordinate = (JSONArray) value;
            geoCode.add(new String[]{coordinate.get(0).toString(), coordinate.get(1).toString()});
          }
        } else {
          geoCode.add(new String[]{coordinates.get(0).toString(), coordinates.get(1).toString()});
        }
      }
      System.out.print("{");
      for (String[] strings : geoCode) {
        System.out.print("{\"" + strings[0] + "\", \"" + strings[1] + "\"}, ");
      }
      System.out.println("}");
      return TaxiPathForm.builder().geoCode(geoCode).build();
    } catch (ParseException e) {
      throw new CustomException(ErrorCode.JSON_PARSE_IMPOSSIBLE);
    }
  }
}
