package zone.czh.woi.woim.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zone.czh.woi.base.web.constant.ErrorCode;
import zone.czh.woi.base.web.usual.Response;
import zone.czh.woi.woim.base.obj.po.WOIMSession;
import zone.czh.woi.woim.base.obj.vo.PushInfo;
import zone.czh.woi.woim.base.obj.vo.SessionState;
import zone.czh.woi.woim.constant.WOIMUrl;
import zone.czh.woi.woim.controller.inter.RouterController;
import zone.czh.woi.woim.service.inter.PushService;
import zone.czh.woi.woim.service.inter.RouterService;
import zone.czh.woi.woim.service.inter.SessionService;
/**
*@ClassName: DefaultDistributedController
*@Description: 根据IP调用不同主机执行本地业务
*@author woi
*/
@RequestMapping(WOIMUrl.Router.MODULE)
@ResponseBody
public class DefaultRouterController implements RouterController {
    @Autowired
    RouterService routerService;
    @RequestMapping(WOIMUrl.Router.PATH_PUSH)
    @Override
    public Response<SessionState> push(@RequestBody PushInfo info) {

        try {
            SessionState sessionState = routerService.push(info.getSession(), info.parseData());
            return new Response<>(sessionState);
        }catch (Exception e){
            return new Response<>(new SessionState(SessionState.OFFLINE));
        }
    }

    @PostMapping(WOIMUrl.Router.PATH_CLOSE_SESSION)
    @Override
    public Response closeSession(@RequestBody WOIMSession session){
        routerService.closeSession(session);
        return new Response<>();
    }
}
