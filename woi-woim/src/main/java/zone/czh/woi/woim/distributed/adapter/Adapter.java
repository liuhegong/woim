package zone.czh.woi.woim.distributed.adapter;

import lombok.Getter;
import zone.czh.woi.woim.distributed.agent.Agent;
import zone.czh.woi.woim.vo.RequestInfo;

import java.util.HashMap;
import java.util.Map;
/**
*@ClassName: Adapter
*@Description: None
*@author woi
*/
@Getter
public abstract class Adapter {

    public Map<Agent.Service,String> path;


    public Adapter(){
        path = new HashMap<>();
    }

    public abstract RequestInfo getRequestInfo(Agent.Service service, Map<Agent.Key,Object> params) throws Exception;

    public abstract<T> T parse(Agent.Service service, Object response,Class<T> targetCls) throws Exception;

}
