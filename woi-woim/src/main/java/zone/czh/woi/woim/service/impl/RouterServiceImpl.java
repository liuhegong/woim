package zone.czh.woi.woim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import zone.czh.woi.spring.base.util.WoiNetUtil;
import zone.czh.woi.woim.base.obj.po.WOIMSession;
import zone.czh.woi.woim.base.obj.vo.SessionState;
import zone.czh.woi.woim.distributed.agent.Agent;
import zone.czh.woi.woim.service.inter.RouterService;

import java.util.HashMap;
import java.util.Map;

/**
*@ClassName: RouterService
*@Description: None
*@author woi
*/
public class RouterServiceImpl implements RouterService {

    @Autowired
    Agent agent;
    @Override
    public SessionState push(WOIMSession session, Object data) {
        try {
            Map<Agent.Key,Object> params = new HashMap<>();
            params.put(Agent.Key.PUSH_KEY_SESSION,session);
            params.put(Agent.Key.PUSH_KEY_DATA,data);
            SessionState state = agent.call(session.getHostIp(), Agent.Service.PUSH, params, SessionState.class);
            return state;
        }catch (Exception e){
            System.out.println("err here");
            e.printStackTrace();
            return new SessionState(SessionState.OFFLINE);
        }
    }

    @Override
    public void closeSession(WOIMSession session) {
        if (session!=null){
            try {
                Map<Agent.Key,Object> params = new HashMap<>();
                params.put(Agent.Key.CLOSE_SESSION_KEY_WOIMSESSION,session);
                agent.call(session.getHostIp(),Agent.Service.CLOSE_SESSION,params,void.class);
            }catch (Exception e){
                e.printStackTrace();
                return;
            }
        }
    }
}
