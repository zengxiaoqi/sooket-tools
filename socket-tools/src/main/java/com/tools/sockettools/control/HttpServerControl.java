package com.tools.sockettools.control;

import com.tools.sockettools.common.StaticStore;
import com.tools.sockettools.entity.HttpSendRespEntity;
import com.tools.sockettools.entity.HttpServerEntity;
import com.tools.sockettools.entity.NodeTree;
import com.tools.sockettools.http.server.HttpListener;
import com.tools.sockettools.http.server.HttpMessage;
import com.tools.sockettools.http.server.HttpServerInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/http")
public class HttpServerControl {

    @RequestMapping(value="/createServer",method = RequestMethod.POST)
    @ResponseBody
    public ReturnResult createServer(@RequestBody HttpServerEntity httpServerEntity) {
        ReturnResult returnResult = new ReturnResult();

        try {
            HttpListener httpListener = new HttpListener(httpServerEntity);
            StaticStore.httpServerMap.put(httpServerEntity.getId(), httpListener);

            NodeTree nodeTree = new NodeTree();
            nodeTree.setId(httpServerEntity.getId());
            nodeTree.setName(httpServerEntity.getName());
            nodeTree.setLeaf(false);
            StaticStore.httpServerTree.add(nodeTree);

            returnResult.setSuccess(true);

            returnResult.setData(StaticStore.httpServerTree);
        } catch (Exception e) {
            e.printStackTrace();
            returnResult.setSuccess(false);
            returnResult.setMessage("新建服务失败："+e.getMessage());
        }

        return returnResult;
    }

    @RequestMapping(value="/sendResp",method = RequestMethod.POST)
    @ResponseBody
    public ReturnResult sendResp(@RequestBody HttpSendRespEntity httpSendRespEntity) {
        ReturnResult returnResult = new ReturnResult();

        try {
            HttpListener httpListener = StaticStore.httpServerMap.get(httpSendRespEntity.getParentId());
            List<HttpServerInfo> httpServerInfoList = httpListener.getHttpServerInfoList();
            for (int i=0; i<httpServerInfoList.size(); i++){
                if(httpServerInfoList.get(i).getId().equals(httpSendRespEntity.getId())){
                    httpListener.send(httpServerInfoList.get(i), httpSendRespEntity.getSendMsg().getBytes());
                }
            }
            HttpMessage httpMessage = StaticStore.getHttpMessage(httpSendRespEntity.getParentId(),httpSendRespEntity.getId());
            if(httpMessage != null){
                httpMessage.setRespFlag(true);
                httpMessage.setRespMsg(httpSendRespEntity.getSendMsg());

                returnResult.setSuccess(true);
                returnResult.setData(httpMessage);
            }else{
                returnResult.setSuccess(false);
                returnResult.setMessage("应答发送成功，没有找到节点对应的信息...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnResult.setSuccess(false);
            returnResult.setMessage("发送消息失败："+e.getMessage());
        }

        return returnResult;
    }

    @RequestMapping(value="/getHttpServerTree",method = RequestMethod.GET)
    @ResponseBody
    public ReturnResult getHttpServerTree() {
        ReturnResult returnResult = new ReturnResult();
        returnResult.setSuccess(true);
        returnResult.setData(StaticStore.httpServerTree);
        return returnResult;
    }

    @RequestMapping(value="/getHttpMessage",method = RequestMethod.POST)
    @ResponseBody
    public ReturnResult getHttpMessage(@RequestBody NodeTree nodeTree) {
        ReturnResult returnResult = new ReturnResult();
        try {
            HttpMessage httpMessage = StaticStore.getHttpMessage(nodeTree.getParentId(),nodeTree.getId());
            if(httpMessage != null){
                returnResult.setSuccess(true);
                returnResult.setData(httpMessage);
            }else{
                returnResult.setSuccess(false);
                returnResult.setMessage("没有找到节点对应的信息...");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnResult.setSuccess(false);
            returnResult.setMessage("获取节点信息出错："+e.getMessage());
        }

        return returnResult;
    }

    @RequestMapping(value="/delHtppNode",method = RequestMethod.POST)
    @ResponseBody
    public ReturnResult delHtppNode(@RequestBody NodeTree nodeTree) {
        ReturnResult returnResult = new ReturnResult();
        try {
            if(nodeTree.getLeaf()){
                log.info("删除子节点信息,parentId[{}], id[{}]", nodeTree.getParentId(),nodeTree.getId());
                StaticStore.deleteChild(StaticStore.httpServerTree,nodeTree.getParentId(),nodeTree.getId(),StaticStore.WS_TYPE_HttpSERVERLIST);
                StaticStore.delHttpMessage(nodeTree.getParentId(),nodeTree.getId());
            }else{
                log.info("删除根节点信息, id[{}]", nodeTree.getId());
                HttpListener httpListener = StaticStore.httpServerMap.get(nodeTree.getId());
                httpListener.close();

                NodeTree pareNode = StaticStore.getParentNodeById(StaticStore.httpServerTree,nodeTree.getId());
                StaticStore.httpServerTree.remove(pareNode);

                StaticStore.httpServerMap.remove(nodeTree.getId());
            }
            returnResult.setSuccess(true);
            returnResult.setData(StaticStore.httpServerTree);
        }catch (Exception e){
            e.printStackTrace();
            returnResult.setSuccess(false);
            returnResult.setMessage("删除节点信息出错："+e.getMessage());
        }

        return returnResult;
    }


}
