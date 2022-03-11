package zone.czh.woi.woim.controller.inter;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import zone.czh.woi.base.web.usual.Response;
import zone.czh.woi.woim.base.obj.po.WOIMSession;
import zone.czh.woi.woim.base.obj.vo.PushInfo;
import zone.czh.woi.woim.base.obj.vo.SessionState;
import zone.czh.woi.woim.constant.WOIMUrl;

/**
*@ClassName: PushController
*@Description: None
*@author woi
*/
public interface PushController {
    public Response<SessionState> push(PushInfo info);
}
