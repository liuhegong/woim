package zone.czh.woi.woim.base.obj.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import zone.czh.woi.woim.base.obj.po.GroupMember;

import java.util.List;
/**
*@ClassName: ChatGroupMod
*@Description: None
*@author woi
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatGroupMod {
    Long id;
    String name;
    String avatar;
    String description;
}
