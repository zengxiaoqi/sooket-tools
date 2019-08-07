/**
 * Created by PanJiaChen on 16/11/18.
 */

/**
 * Parse the time to string
 * @param {(Object|string|number)} time
 * @param {string} cFormat
 * @returns {string}
 */
export function parseTime(time, cFormat) {
  if (arguments.length === 0) {
    return null
  }
  const format = cFormat || '{y}-{m}-{d} {h}:{i}:{s}'
  let date
  if (typeof time === 'object') {
    date = time
  } else {
    if ((typeof time === 'string') && (/^[0-9]+$/.test(time))) {
      time = parseInt(time)
    }
    if ((typeof time === 'number') && (time.toString().length === 10)) {
      time = time * 1000
    }
    date = new Date(time)
  }
  const formatObj = {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds(),
    a: date.getDay()
  }
  const time_str = format.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
    let value = formatObj[key]
    // Note: getDay() returns 0 on Sunday
    if (key === 'a') { return ['日', '一', '二', '三', '四', '五', '六'][value ] }
    if (result.length > 0 && value < 10) {
      value = '0' + value
    }
    return value || 0
  })
  return time_str
}

/**
 * @param {number} time
 * @param {string} option
 * @returns {string}
 */
export function formatTime(time, option) {
  if (('' + time).length === 10) {
    time = parseInt(time) * 1000
  } else {
    time = +time
  }
  const d = new Date(time)
  const now = Date.now()

  const diff = (now - d) / 1000

  if (diff < 30) {
    return '刚刚'
  } else if (diff < 3600) {
    // less 1 hour
    return Math.ceil(diff / 60) + '分钟前'
  } else if (diff < 3600 * 24) {
    return Math.ceil(diff / 3600) + '小时前'
  } else if (diff < 3600 * 24 * 2) {
    return '1天前'
  }
  if (option) {
    return parseTime(time, option)
  } else {
    return (
      d.getMonth() +
      1 +
      '月' +
      d.getDate() +
      '日' +
      d.getHours() +
      '时' +
      d.getMinutes() +
      '分'
    )
  }
}

/**
 * @param {string} url
 * @returns {Object}
 */
export function param2Obj(url) {
  const search = url.split('?')[1]
  if (!search) {
    return {}
  }
  return JSON.parse(
    '{"' +
      decodeURIComponent(search)
        .replace(/"/g, '\\"')
        .replace(/&/g, '","')
        .replace(/=/g, '":"')
        .replace(/\+/g, ' ') +
      '"}'
  )
}

/**
 * 生成 UUID
 * @returns {string}
 * @constructor
 */
function S4() {
    return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
}
export function guuid() {
    return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4());
}


/**
 * 格式化xml代码
 */
export function formateXml(xmlStr){
    text = xmlStr;
    //使用replace去空格
    text = '\n' + text.replace(/(<\w+)(\s.*?>)/g,function($0, name, props){
        return name + ' ' + props.replace(/\s+(\w+=)/g," $1");
    }).replace(/>\s*?</g,">\n<");
    //处理注释
    text = text.replace(/\n/g,'\r').replace(/<!--(.+?)-->/g,function($0, text){
        var ret = '<!--' + escape(text) + '-->';
        return ret;
    }).replace(/\r/g,'\n');
    //调整格式  以压栈方式递归调整缩进
    var rgx = /\n(<(([^\?]).+?)(?:\s|\s*?>|\s*?(\/)>)(?:.*?(?:(?:(\/)>)|(?:<(\/)\2>)))?)/mg;
    var nodeStack = [];
    var output = text.replace(rgx,function($0,all,name,isBegin,isCloseFull1,isCloseFull2 ,isFull1,isFull2){
        var isClosed = (isCloseFull1 == '/') || (isCloseFull2 == '/' ) || (isFull1 == '/') || (isFull2 == '/');
        var prefix = '';
        if(isBegin == '!'){//!开头
            prefix = setPrefix(nodeStack.length);
        }else {
            if(isBegin != '/'){///开头
                prefix = setPrefix(nodeStack.length);
                if(!isClosed){//非关闭标签
                    nodeStack.push(name);
                }
            }else{
                nodeStack.pop();//弹栈
                prefix = setPrefix(nodeStack.length);
            }
        }
        var ret =  '\n' + prefix + all;
        return ret;
    });
    var prefixSpace = -1;
    var outputText = output.substring(1);
    //还原注释内容
    outputText = outputText.replace(/\n/g,'\r').replace(/(\s*)<!--(.+?)-->/g,function($0, prefix,  text){
        if(prefix.charAt(0) == '\r')
            prefix = prefix.substring(1);
        text = unescape(text).replace(/\r/g,'\n');
        var ret = '\n' + prefix + '<!--' + text.replace(/^\s*/mg, prefix ) + '-->';
        return ret;
    });
    outputText= outputText.replace(/\s+$/g,'').replace(/\r/g,'\r\n');
    return outputText;
}

//计算头函数 用来缩进
function setPrefix(prefixIndex) {
    var result = '';
    var span = '    ';//缩进长度
    var output = [];
    for(var i = 0 ; i < prefixIndex; ++i){
        output.push(span);
    }
    result = output.join('');
    return result;
}
