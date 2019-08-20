package com.tools.sockettools.control;

import com.tools.sockettools.common.StaticStore;
import com.tools.sockettools.entity.HttpSendRespEntity;
import com.tools.sockettools.entity.HttpServerEntity;
import com.tools.sockettools.entity.NodeTree;
import com.tools.sockettools.http.server.HttpListener;
import com.tools.sockettools.http.server.HttpMessage;
import com.tools.sockettools.http.server.HttpServerInfo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            HttpListener httpListener = StaticStore.httpServerMap.get(httpSendRespEntity.getId());;
            List<HttpServerInfo> httpServerInfoList = httpListener.getHttpServerInfoList();
            for (int i=0; i<httpServerInfoList.size(); i++){
                if(httpServerInfoList.get(i).getId().equals(httpSendRespEntity.getId())){
                    httpListener.send(httpServerInfoList.get(i), httpSendRespEntity.getRespStr().getBytes());
                }
            }
            for(HttpMessage httpMessage : StaticStore.httpMessageList){
                if(httpMessage.getId().equals(httpSendRespEntity.getId()) &&
                        httpMessage.getParentId().equals(httpSendRespEntity.getParentId())){
                    httpMessage.setRespMsg(httpSendRespEntity.getRespStr());
                }
            }
            returnResult.setSuccess(true);

            returnResult.setData(StaticStore.httpServerMap);
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
}
