package com.tools.sockettools.xmlformat;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

public class XmlFormatter {
    public static String format(String unformattedXml) {
        try {
            final Document document = parseXmlFile(unformattedXml);
            OutputFormat format = new OutputFormat(document);
            format.setLineWidth(65);
            format.setIndenting(true);
            format.setIndent(4);    //缩进
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(document);
            return out.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Document parseXmlFile(String in) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception{
        //String s = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><PARAM><DBID>35</DBID><SEQUENCE>atgtca</SEQUENCE><MAXNS>10</MAXNS><MINIDENTITIES>90</MINIDENTITIES><MAXEVALUE>10</MAXEVALUE><USERNAME>admin</USERNAME><PASSWORD>111111</PASSWORD><TYPE>P</TYPE><RETURN_TYPE>2</RETURN_TYPE></PARAM>";//未格式化前的xml
        String s="<?xml version=\"1.0\" encoding=\"utf-8\"?><ResInfo>  <barcode/>  <asset>    <assetcardno>B</assetcardno>    <comments/>    <buydate/>    <category/>  </asset>  <entity>    <entityid>901061314</entityid>    <entitycode>LTJHXYYCX01/XA-HWMACBTS529</entitycode>    <entityname>蓝田局华胥电信营业厅CDMA基站/BTS529</entityname>    <entityspec>BTS</entityspec>    <vendorname>HuaWei</vendorname>    <model>HUAWEI BTS3900</model>    <installaddress/>  </entity>  <version/>  <sectornum>3</sectornum>  <rackname>HW401C</rackname>  <containers>    <container>      <shelfhight>0.086</shelfhight>      <cardinfos>        <cardinfo>          <cardname>290.蓝田局华胥电信营业厅CDMA基站/BTS529/架1列1/框3槽(0)HECM</cardname>        </cardinfo>        <cardinfo>          <cardname>290.蓝田局华胥电信营业厅CDMA基站/BTS529/架1列1/框3槽(10)FAN</cardname>        </cardinfo>        <cardinfo>          <cardname>290.蓝田局华胥电信营业厅CDMA基站/BTS529/架1列1/框3槽(9)UPEU</cardname>        </cardinfo>        <cardinfo>          <cardname>290.蓝田局华胥电信营业厅CDMA基站/BTS529/架1列1/框3槽(7)CMPT</cardname>        </cardinfo>        <cardinfo>          <cardname>290.蓝田局华胥电信营业厅CDMA基站/BTS529/架1列1/框3槽(2)HCPM</cardname>        </cardinfo>      </cardinfos>    </container>    <container>      <shelfhight>0.308</shelfhight>      <cardinfos>        <cardinfo>          <cardname>290.蓝田局华胥电信营业厅CDMA基站/BTS529/架1列1/框1槽(2)CRFU</cardname>        </cardinfo>        <cardinfo>          <cardname>290.蓝田局华胥电信营业厅CDMA基站/BTS529/架1列1/框1槽(4)CRFU</cardname>        </cardinfo>        <cardinfo>          <cardname>290.蓝田局华胥电信营业厅CDMA基站/BTS529/架1列1/框1槽(0)CRFU</cardname>        </cardinfo>      </cardinfos>    </container>    <container>      <shelfhight>0.086</shelfhight>    </container>    <container>      <shelfhight>0.044</shelfhight>    </container>  </containers>  <ReturnResult>0</ReturnResult>  <ReturnInfo>成功</ReturnInfo></ResInfo>";
        System.out.println(XmlFormatter.format(s));

    }
}
