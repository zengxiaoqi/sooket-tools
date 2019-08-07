export default {
    indexOf: function (list, data) {
        //1,获得数组的长度
        var length = list.length;
        //2,设置返回数据的下标
        var index = -1;
        //3,遍历数据
        for (var i = 0; i < length; i++) {
            if (data == list[i]) {
                index = i;
                return index;
            }
        }
        return index;
    },

    delOneFromList: function (list, data) {
        var index = this.indexOf(list, data);
        if (index > -1) {
            //splice(start,deleteCount) 从start位置开始删除deleteCount项
            list.splice(index, 1);
        }
    },

    delFromList: function (orgList, delList) {
        let newList = orgList;
        for(let index in delList){
            this.delOneFromList(newList, delList[index]);
        }
    },

    insertList: function (list, data, index) {
        //将一个或多个新元素插入到数组的指定位置，插入位置的元素自动后移，返回”“
        list.splice(index, 0, data);
    },

    /* 将数组list中data的值替换成newData */
    modifyList: function (list, data, newData) {
        //1,获得数组的长度
        var length = list.length;
        //2,设置返回数据的下标
        var index = -1;
        //3,遍历数据
        for (var i = 0; i < length; i++) {
            if (data == list[i]) {
                list[i] = newData;
                return list;
            }
        }
        return list;
    },
}
