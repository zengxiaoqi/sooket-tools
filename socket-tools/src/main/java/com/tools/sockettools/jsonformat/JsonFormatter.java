package com.tools.sockettools.jsonformat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JsonFormatter {
    /**
     * 格式化
     *
     * @param jsonStr
     * @return
     * @author
     * @Date
     * @Modified
     */
    public static String formatJson(String jsonStr) {
        if (null == jsonStr || "".equals(jsonStr))
            return "";
        StringBuilder sb = new StringBuilder();
        char last = '\0';
        char current = '\0';
        int indent = 0;
        boolean isInQuotationMarks = false;
        for (int i = 0; i < jsonStr.length(); i++) {
            last = current;
            current = jsonStr.charAt(i);
            switch (current) {
                case '"':
                    if (last != '\\'){
                        isInQuotationMarks = !isInQuotationMarks;
                    }
                    sb.append(current);
                    break;
                case '{':
                case '[':
                    sb.append(current);
                    if (!isInQuotationMarks) {
                        sb.append('\n');
                        indent++;
                        addIndentBlank(sb, indent);
                    }
                    break;
                case '}':
                case ']':
                    if (!isInQuotationMarks) {
                        sb.append('\n');
                        indent--;
                        addIndentBlank(sb, indent);
                    }
                    sb.append(current);
                    break;
                case ',':
                    sb.append(current);
                    if (last != '\\' && !isInQuotationMarks) {
                        sb.append('\n');
                        addIndentBlank(sb, indent);
                    }
                    break;
                default:
                    sb.append(current);
            }
        }

        return sb.toString();
    }

    /**
     * 添加space
     *
     * @param sb
     * @param indent
     * @author lizhgb
     * @Date 2015-10-14 上午10:38:04
     */
    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append('\t');
        }
    }

    public static String jsonFormat(String reqjson) {

        JSONObject jsonObject = JSONObject.parseObject(reqjson);
        String jsonString = JSON.toJSONString(jsonObject, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
        return jsonString;
    }

    public static void main(String[] args) throws Exception{
        String s="{\"head\":{\"BisUid\":\"atm1101201804261959331032\",\"TransCode\":\"E120\",\"MsgType\":\"0200\",\"ExSerial\":\"001032\",\"Acctoper\":\"A0002\",\"TradeType\":\"0000\",\"BranchNo\":\"0915\",\"Channel\":\"atm\",\"TermNo\":\"0002\",\"TermIp\":\"172.29.4.213\",\"TransDate\":\"20180426\",\"TransTime\":\"195938\",\"TermNoInn\":\"0002\",\"TopBranchNo\":\"0900\",\"BatchNo\":\"7\",\"Token\":\"\",\"EncodeType\":\"1\",\"Auther\":\"\"},\"body\":{\"keyName\":\"ATM.10000001.SM2\",\"keyGroup\":\"01\",\"algorithmID\":\"SM2\",\"keyType\":\"1\",\"pkExponent\":65537,\"keyLen\":256,\"updateKeyFlag\":0,\"vkStoreLocation\":0,\"hsmGroupID\":\"\",\"vkIndex\":\"00\",\"oldVersionKeyIsUsed\":1,\"mode\":1,\"inputFlag\":0,\"outputFlag\":0,\"exportFlag\":0,\"effectiveDays\":36500,\"enabled\":1,\"activeDate\":0,\"keyApplyPlatform\":\"\",\"keyDistributePlatform\":\"\"}}";
        System.out.println(JsonFormatter.jsonFormat(s));
        //System.out.println(JsonFormatter.formatJson(s));
    }
}
