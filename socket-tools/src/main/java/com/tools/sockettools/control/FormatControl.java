package com.tools.sockettools.control;

import com.tools.sockettools.jsonformat.JsonFormatter;
import com.tools.sockettools.xmlformat.XmlFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class FormatControl {

    @RequestMapping(value="/formatStr",method = RequestMethod.POST)
    @ResponseBody
    public ReturnResult formatStr(@RequestBody Map<String,Object> config){
        String inputStr = (String) config.get("inputStr");
        String type = (String) config.get("type");
        ReturnResult returnResult = new ReturnResult();
        String rspStr = null;
        if(type.equals("xml")){
            rspStr = XmlFormatter.format(inputStr);
        }else if(type.equals("json")){
            rspStr = JsonFormatter.jsonFormat(inputStr);
        }
        returnResult.setSuccess(true);
        Map<String, Object> data = new HashMap<>();
        data.put("outputStr", rspStr);
        returnResult.setData(data);

        return returnResult;
    }
}
