package zone.czh.woi.woim.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import zone.czh.woi.base.util.CommonUtil;
import zone.czh.woi.spring.base.util.WoiNetUtil;
import zone.czh.woi.woim.base.constant.SystemIntent;
import zone.czh.woi.woim.base.obj.po.WOIMSession;
import zone.czh.woi.woim.base.obj.vo.WOIMIntent;
import zone.czh.woi.woim.distributed.agent.Agent;
import zone.czh.woi.woim.listener.WOIMEventListener;
import zone.czh.woi.woim.mapper.WOIMSessionMapper;
import zone.czh.woi.woim.server.WOIMServer;
import zone.czh.woi.woim.service.inter.PushService;
import zone.czh.woi.woim.service.inter.RouterService;
import zone.czh.woi.woim.service.inter.SessionService;
import zone.czh.woi.woim.session.storage.LocalSessionStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
*@ClassName: SessionServiceImpl
*@Description: None
*@author woi
*/

public class SessionServiceImpl implements SessionService {
    @Autowired
    private WOIMServer woimServer;
    @Autowired
    private WOIMSessionMapper woimSessionMapper;
    @Autowired
    private PushService pushService;
    @Autowired
    private WOIMEventListener woimEventListener;
    @Autowired
    private Agent agent;
    @Autowired
    private RouterService routerService;
    @Autowired
    private LocalSessionStorage localSessionStorage;//相当于缓存

    /**
     * 关键方法！
     * 关闭本地channel
     * 应该确保是本地的channel
     * @param cid
     */
    @Override
    public void closeChannel(String cid) {
        woimServer.closeChannel(cid);
    }



    @Override
    public List<WOIMSession> getSessions(String uid){
        return woimSessionMapper.select(new WOIMSession().setUid(uid));
    }

    @Override
    public WOIMSession getSession(String uid, String cid) {
//        Map<String, WOIMSession> userSessionMap = tempSessionStorage.get(uid);
//        WOIMSession session = null;
//
//        if (userSessionMap!=null){
//            //说明本地存在此用户的会话信息，但不确定是不是对应的channel
//            session = userSessionMap.get(cid);
//        }
        WOIMSession session = localSessionStorage.getSession(uid, cid);
        if (session==null){
            //本地不存在，则查数据库
            session = woimSessionMapper.selectOne(new WOIMSession().setUid(uid).setCid(cid));
        }
        return session;
    }


    /**
     * 确保是本地会话
     * @param session
     */
    @Transactional
    @Override
    public void createSession(WOIMSession session) {
        if (session!=null){
//            String uid = session.getUid();
//            String cid = session.getCid();
//            Map<String, WOIMSession> userSessionMap = tempSessionStorage.get(uid);
//            if (userSessionMap==null){
//                userSessionMap = new ConcurrentHashMap<>();
//            }
//            userSessionMap.put(cid,session);
//            tempSessionStorage.put(session.getUid(),userSessionMap);
            localSessionStorage.storeSession(session);//本地缓存一份
            woimSessionMapper.insertSelective(session);//数据库插入一份
        }
    }

    @Transactional
    @Override
    public void closeLocalSession(WOIMSession session) {
        if (session!=null){
            removeWOIMSession(session);
            closeChannel(session.getCid());
            woimEventListener.onSessionClosed(session);
        }
    }



    @Override
    public void closeLocalSession(String uid, String cid) {
//        WOIMSession session = getSession(uid, cid);
        //以防搞混，就不用getsession了
        WOIMSession session = localSessionStorage.getSession(uid, cid);
        closeLocalSession(session);
    }




    @Override
    public void removeWOIMSession(WOIMSession session) {
        String uid = session.getUid();
        String cid = session.getCid();
        CommonUtil.checkNull(uid,cid);
        //先删除数据库的数据
        woimSessionMapper.delete(new WOIMSession().setUid(uid).setCid(cid));
        //todo 从redis中删除session信息
//        Map<String, WOIMSession> userSessionMap = tempSessionStorage.get(uid);
//        userSessionMap.remove(cid);
        localSessionStorage.removeSession(session);
    }

    @Override
    public void kickOff(String uid, Integer channelType) {
        kickOff(uid,channelType,null);
    }

    @Override
    public void kickOff(String uid,Integer channelType, String msg) {
        List<WOIMSession> sessions = getSessions(uid);
        if (sessions!=null){
            for (WOIMSession session:sessions){
                if (channelType.equals(session.getChannelType())){
                    kickOff(session,msg);
                }
            }
        }
    }

    @Override
    public void kickOff(Long sessionId, String msg) {
        WOIMSession session = woimSessionMapper.selectByPrimaryKey(sessionId);
        kickOff(session,msg);
    }

    @Override
    public void kickOff(WOIMSession session, String msg) {
        if (session!=null){
            WOIMIntent intent = new WOIMIntent().setCmd(SystemIntent.CMD_KICK_OFF);
            if (msg==null){
                msg = "下线提醒";
            }
            intent.putExtra(SystemIntent.KEY_KICK_OFF_MSG,msg);
            pushService.pushDistributed(session,intent);
//            closeLocalSession(session);
            //todo 下线功能会话可能不是存储在本地的，交由router层处理
            routerService.closeSession(session);
        }
    }
}
