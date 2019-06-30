import Mock from 'mockjs'

import tcpServerData from './tcpServerData'
import createServer from './createServer'
import formatStr from './formatStr'

let data = [].concat(tcpServerData,createServer,formatStr)

data.forEach(function(res){
    Mock.mock(res.path, res.data)
})

export default Mock
