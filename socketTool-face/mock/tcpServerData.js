import Mock from 'mockjs'



const data = Mock.mock({

  'data|30': [{
    id: '@id',
    'port|+1': 2001,
    'status|1': ['open', 'close'],
    operation: 'edit,delete',
    sendMsg: '@sentence(10, 20)',
    recvMsg: '@sentence(10, 20)',
  }]
})

var rspData = {
  "total": null,
  "root": null,
  "success": true,
  "message": null,
  "errorCode": null,
  "data": data.data,
}

var rcvMsg = {
    "total": null,
    "root": null,
    "success": true,
    "message": null,
    "errorCode": null,
    "data": '@sentence(20, 50)',
}
export default[
    {
        path: '/TcpServerData',
        data: rspData,
    },
    {
        path: '/getRcvMsg',
        data: rcvMsg,
    }
]

/*
{
  body: [
    {

      'id': "server-01",
      'port': '2001',
      'statue|1': ["启用", "断开"],
      operation: 'edit,delete',
      sendMsg: "",
      recvMsg: "",
    },
    {

      'id': "server-02",
      'port': '2002',
      'statue|1': ["启用", "断开"],
      operation: 'edit,delete',
      sendMsg: "",
      recvMsg: "",
    }
  ]
}*/
