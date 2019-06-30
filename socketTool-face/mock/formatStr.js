import Mock from 'mockjs'

var data = {
    "total": null,
    "root": null,
    "success": true,
    "message": null,
    "errorCode": null,
    "data": {
        "outputStr": "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<ResInfo>\n    <barcode/>\n    <asset>\n        <assetcardno>B</assetcardno>\n        <comments/>\n        <buydate/>\n        <category/>\n    </asset>\n    <entity>\n        <entityid>901061314</entityid>\n        <entitycode>LTJHXYYCX01/XA-HWMACBTS529</entitycode>\n        <entityname>蓝田局华胥电信营业厅CDMA基站/BTS529</entityname>\n        <entityspec>BTS</entityspec>\n        <vendorname>HuaWei</vendorname>\n        <model>HUAWEI BTS3900</model>\n        <installaddress/>\n    </entity>\n    <version/>\n    <sectornum>3</sectornum>\n    <rackname>HW401C</rackname>\n    <containers>\n        <container>\n            <shelfhight>0.086</shelfhight>\n            <cardinfos>\n                <cardinfo>\n                    <cardname>290.蓝田局华胥电信营业厅CDMA基站/BTS529/架1列1/框3槽(0)HECM</cardname>\n                </cardinfo>\n                <cardinfo>\n                    <cardname>290.蓝田局华胥电信营业厅CDMA基站/BTS529/架1列1/框3槽(10)FAN</cardname>\n                </cardinfo>\n                <cardinfo>\n                    <cardname>290.蓝田局华胥电信营业厅CDMA基站/BTS529/架1列1/框3槽(9)UPEU</cardname>\n                </cardinfo>\n                <cardinfo>\n                    <cardname>290.蓝田局华胥电信营业厅CDMA基站/BTS529/架1列1/框3槽(7)CMPT</cardname>\n                </cardinfo>\n                <cardinfo>\n                    <cardname>290.蓝田局华胥电信营业厅CDMA基站/BTS529/架1列1/框3槽(2)HCPM</cardname>\n                </cardinfo>\n            </cardinfos>\n        </container>\n        <container>\n            <shelfhight>0.308</shelfhight>\n            <cardinfos>\n                <cardinfo>\n                    <cardname>290.蓝田局华胥电信营业厅CDMA基站/BTS529/架1列1/框1槽(2)CRFU</cardname>\n                </cardinfo>\n                <cardinfo>\n                    <cardname>290.蓝田局华胥电信营业厅CDMA基站/BTS529/架1列1/框1槽(4)CRFU</cardname>\n                </cardinfo>\n                <cardinfo>\n                    <cardname>290.蓝田局华胥电信营业厅CDMA基站/BTS529/架1列1/框1槽(0)CRFU</cardname>\n                </cardinfo>\n            </cardinfos>\n        </container>\n        <container>\n            <shelfhight>0.086</shelfhight>\n        </container>\n        <container>\n            <shelfhight>0.044</shelfhight>\n        </container>\n    </containers>\n    <ReturnResult>0</ReturnResult>\n    <ReturnInfo>成功</ReturnInfo>\n</ResInfo>\n"
    }
}

export default [{
    path: '/formatStr',
    data: data
}]
