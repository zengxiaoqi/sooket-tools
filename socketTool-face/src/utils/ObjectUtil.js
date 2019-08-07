export default {
    //Map转为数组
     map2Array: function(map) {
         let arry = [];
         let newObj = {key: null, value: null};
         for(var key in map){
             newObj.key = key;
             newObj.value = map[key];
             arry.push(newObj);
         }
        return arry;
    },
    //数组 转为 Map
     array2Map: function(arr) {
         var map = {};
         for(var i = 0; i < arr.length; i++) {    //遍历list数据
            var one = arr[i];    //ai代表list中的某一项
             map[one.key] = one.value;
        }
        return map;
        //return new Map(arr.map((value, key) => [key, value]));
    },
    //Map 转为对象
     map2Object: function(map) {
        let obj = {};
        for (let [k, v] of map) {
            obj[k] = v;
        }
        return obj;
    },
    //数组转对象
    array2Object: function(arr){
        return this.map2Object(this.array2Map(arr));
    },
    //对象转为 Map
    object2Map: function(obj) {
        let map = new Map();
        for (let key in obj) {
            map.set(key, obj[key]);
        }
        return map;
    },

    //Map 转为 JSON
    map2Json: function(map) {
        return JSON.stringify(this.map2Object(map));
    },

    //JSON 转为 Map
    Json2Map: function(json) {
        return this.object2Map(JSON.parse(json));
    },
}
