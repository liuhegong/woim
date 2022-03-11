package zone.czh.woi.woim.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import zone.czh.woi.base.web.usual.Response;
import zone.czh.woi.woim.base.obj.po.WOIMSession;
import zone.czh.woi.woim.base.obj.vo.PushInfo;
import zone.czh.woi.woim.base.obj.vo.SessionState;
import zone.czh.woi.woim.constant.WOIMUrl;
import zone.czh.woi.woim.controller.inter.PushController;
import zone.czh.woi.woim.service.inter.PushService;

/**
*@ClassName: DefaultPushController
*@Description: None
*@author woi
*/
@RequestMapping(WOIMUrl.Push.MODULE)
@ResponseBody
public class DefaultPushController implements PushController {
    @Autowired
    PushService pushService;

    /**
     * 本地推送
     * @param info
     * @return
     */
    @PostMapping(WOIMUrl.Push.PATH_PUSH)
    @Override
    public Response<SessionState> push(@RequestBody PushInfo info){
        try {
            WOIMSession session = info.getSession();
            return new Response<>(pushService.push(session.getCid(), info.parseData()));
        }catch (Exception e){
            return new Response<>(new SessionState().setState(SessionState.OFFLINE));
        }

    }
}
