package zone.czh.woi.woim.base.obj.vo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
*@ClassName: Intent
*@Description: None
*@author woi
*/
@Data
@NoArgsConstructor
public class WOIMIntent {
    @Accessors(chain = true)
    String cmd;
    Map<String, Pair> extras;
    public Object getExtra(String key){
        try {
            if (extras ==null){
                return null;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            Pair pair = extras.get(key);
            String className = pair.getKey();
            String json = pair.getValue();
            return objectMapper.readValue(json, Class.forName(className));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public WOIMIntent putExtra(String key, Object value){
        try {
            if (value==null){
                return this;
            }
            if (extras ==null){
                extras = new HashMap<>();
            }
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(value);
            Pair pair = new Pair(value.getClass().getName(),json);
            extras.put(key,pair);
        }catch (Exception e){
            e.printStackTrace();
            return this;
        }
        return this;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Pair{
        String key;
        String value;
    }
}
