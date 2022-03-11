package zone.czh.woi.woim.service.inter;

import zone.czh.woi.woim.base.obj.po.WOIMSession;
import zone.czh.woi.woim.base.obj.vo.SessionState;

/**
*@ClassName: RouterService
*@Description: None
*@author woi
*/
public interface RouterService {
    SessionState push(WOIMSession session, Object data);
    void closeSession(WOIMSession session);
}
