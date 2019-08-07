// indexedDB.js，浏览器本地数据库操作
import store from '@/store/';

export default {
    // indexedDB兼容
    indexedDB: window.indexedDB || window.webkitindexedDB || window.msIndexedDB || mozIndexedDB,
    // 打开数据库
    // 新对象储存空间newStore参数：newStore.name、newStore.key
    // 新增对象存储空间要更改数据库版本
    openDB: function (dbname, version, db, newStore, callback) {
        var version = version || 1;
        var request = this.indexedDB.open(dbname, version);
        request.onerror = function (event) {
            console.log('IndexedDB数据库打开错误');
        };
        request.onsuccess = function (event) {
            console.log("新建数据库成功："+dbname+" 版本："+version)
            db = event.target.result;
            if (callback && (typeof callback === 'function')) {
                callback(db);
            }
        };
        // onupgradeneeded，调用创建新的储存空间 version 变化
        request.onupgradeneeded = function (event) {
            console.log("更新数据库...");
            var db = event.target.result;
            if (newStore) {
                if( newStore.constructor === Array && newStore.length > 1){
                    newStore.forEach(item => {
                        if (!db.objectStoreNames.contains(item.name)) {
                            var objectStore = db.createObjectStore(item.name, {
                                keyPath: item.key,  //主键（key）是默认建立索引的属性
                            });
                            objectStore.createIndex('key_index', 'id', { unique: true });
                            console.log("新建表成功："+item.name+" 索引："+item.key)
                        }
                    })
                }else {
                    if (!db.objectStoreNames.contains(newStore.name)) {
                        var objectStore = db.createObjectStore(newStore.name, {
                            keyPath: newStore.key,  //主键（key）是默认建立索引的属性
                        });
                        objectStore.createIndex('key_index', 'id', { unique: true });
                        console.log("新建表成功："+newStore.name+" 索引："+newStore.key)
                    }
                }
            }
        };
    },
    // 删除数据库
    deleteDB: function (dbname, callback) {
        var deleteQuest = this.indexedDB.deleteDatabase(dbname);
        deleteQuest.onerror = function () {
            console.log('删除数据库出错');
        };
        deleteQuest.onsuccess = function () {
            if (callback && (typeof callback === 'function')) {
                callback();
            }
        }
    },
    // 关闭数据库
    closeDB: function (dbname) {
        dbname.close();
        console.log('数据库已关闭');
    },
    // 更新旧值
    putData: function (db, storename, data, callback) {
        var store = db.transaction(storename, 'readwrite').objectStore(storename),
            request;
        request = store.put(data);
        request.onerror = function () {
            console.error('PUT添加数据报错');
            if (callback && (typeof callback === 'function')) {
                callback(false);
            }
        };
        request.onsuccess = function (result) {
            console.info('PUT添加数据成功');
            if (callback && (typeof callback === 'function')) {
                callback(true);
            }
        };
    },
    //新增
    addData: function (db, storename, data, callback) {
        var store = db.transaction(storename, 'readwrite').objectStore(storename),
            request;
        request = store.add(data);
        request.onerror = function () {
            console.error('ADD添加数据报错');
            if (callback && (typeof callback === 'function')) {
                callback(false);
            }
        };
        request.onsuccess = function (result) {
            console.info('ADD添加数据成功');
            if (callback && (typeof callback === 'function')) {
                callback(true);
            }
        };
    },
    // 插入或新增
    putDatas: function (db, storename, dataArr, callback) {
        let errDataList = [];
        dataArr.forEach(item => {
            console.log("当前记录为：");
            console.log(item);
            this.read(db, storename, item.id).then(x => {
                if (x) {
                    this.putData(db, storename, item, function (result) {
                        if(!result){
                            errDataList.push(item); //记录失败数据
                        }
                    });
                } else {
                    this.addData(db, storename, item, function (result) {
                        if(!result){
                            errDataList.push(item); //记录失败数据
                        }
                    });
                }
            })
        });
        callback(errDataList);
    },
    // 删除数据
    deleteData: function (db, storename, key, callback) {
        var store = db.transaction(storename, 'readwrite').objectStore(storename);
        store.delete(key);
        if (callback && (typeof callback === 'function')) {
            callback();
        }
    },
    // 清空数据
    clearData: function (db, storename, callback) {
        var store = db.transaction(storename, 'readwrite').objectStore(storename);
        store.clear();
        if (callback && (typeof callback === 'function')) {
            callback();
        }
    },
    read: function (db, storeName, id) {
        var transaction = db.transaction(storeName);
        var objectStore = transaction.objectStore(storeName);
        var indexs = objectStore.index('key_index');
        var request = indexs.openCursor(IDBKeyRange.only(id));
        return new Promise((resolve, reject) => {
            request.onsuccess = function (e) {
                var cursor = e.target.result;
                if (cursor) {
                    resolve(true);
                }
                else {
                    resolve(false);
                }
            }
        })
    },
    // 通过key获取数据
    getDataById: function (db, storeName, id) {
        var transaction = db.transaction(storeName);
        var objectStore = transaction.objectStore(storeName);
        var indexs = objectStore.index('key_index');
        var request = indexs.openCursor(IDBKeyRange.only(id));
        return new Promise((resolve, reject) => {
            request.onsuccess = function (e) {
                var cursor = e.target.result;
                if (cursor) {
                    resolve(cursor.value);
                }else {
                    resolve(null);
                }
            }
        })
    },
    //通过游标遍历数据
    getdatabycursor: function (db, storename) {
        var objectStore = db.transaction(storename).objectStore(storename);
        var dataList = [];
        var i = 0;
        return new Promise((resolve, reject) => {
            objectStore.openCursor().onsuccess = function (event) {
                var cursor = event.target.result;
                if (cursor) {
                    dataList.push(cursor.value)
                    cursor.continue();
                } else {
                    resolve(dataList);
                }
            };
        })
    },
    getdata: function (db, storename) {
        var objectStore = db.transaction(storename).objectStore(storename);
        var data = [];
        return new Promise((resolve, reject) => {
            objectStore.openCursor().onsuccess = function (event) {
                var cursor = event.target.result;
                if (cursor) {
                    data.push(cursor.value)
                    resolve(data)
                } else {
                    reject(false)
                }
            };
        })
    },
    saveDb: function (db) {
        store.dispatch("indexedDB/setdb", db);
    },

    /******************/
    /*//根据key修改数量
    updateDataByKey: function (db, storeName, value, QTY, addtime) {
        var transaction = db.transaction(storeName, 'readwrite');
        var store = transaction.objectStore(storeName);
        var request = store.get(value);
        return new Promise((resolve, reject) => {
            request.onsuccess = function (e) {
                var stocktable = e.target.result;
                if (stocktable) {
                    stocktable.qty = QTY;
                    stocktable.addtime = addtime;
                    resolve(store.put(stocktable));
                } else {
                    reject(false);
                }
            };
        })
    },

    updateDataBycode: function (db, storeName, value, QTY) {
        var transaction = db.transaction(storeName, 'readwrite');
        var store = transaction.objectStore(storeName);
        var request = store.get(value);
        return new Promise((resolve, reject) => {
            request.onsuccess = function (e) {
                var stocktable = e.target.result;
                if (stocktable) {
                    stocktable.qty = QTY;

                    resolve(store.put(stocktable));
                } else {
                    reject(false);
                }
            };
        })
    },
    //根据key修改数量
    updateDataByKeys: function (db, storeName, value, addtime, callback) {
        var transaction = db.transaction(storeName, 'readwrite');
        var store = transaction.objectStore(storeName);
        var request = store.get(value);

        return new Promise((resolve, reject) => {
            //console.log(addtime)
            request.onsuccess = function (e) {
                var stocktable = e.target.result;
                if (stocktable) {
                    stocktable.qty += 1;
                    stocktable.addtime = addtime;
                    resolve(store.put(stocktable));
                } else {
                    reject(false);
                }
            };
        })
    },

    // 通过barcode获取数据
    reads: function (db, storeName, values) {
        var transaction = db.transaction(storeName);
        var objectStore = transaction.objectStore(storeName);
        var indexs = objectStore.index('barcode_index');
        var data = [];
        var request = indexs.openCursor(IDBKeyRange.only(values));
        return new Promise((resolve, reject) => {
            request.onsuccess = function (e) {
                var cursor = e.target.result;
                if (cursor) {
                    data.push(cursor.value);
                    // resolve(data);
                    cursor.continue();
                } else {
                    resolve(data)
                }
            };
        })

    },
    //根据counter索引查询数据
    getdatabyCounter: function (db, storeName, values) {
        var transaction = db.transaction(storeName);
        var store = transaction.objectStore(storeName);
        var indexs = store.index('counter_index');
        var datas = [];
        var request = indexs.openCursor(IDBKeyRange.only(values))
        return new Promise((resolve, reject) => {
            request.onsuccess = function (e) {
                var cursor = e.target.result;
                if (cursor) {
                    datas.push(cursor.value);
                    cursor.continue();
                }
                else {
                    resolve(datas)
                }
            }
        })
    },
    //根据主键和索引查询
    getAll: function (db, storeName, counter, barcode) {
        var transaction = db.transaction(storeName);
        var objectStore = transaction.objectStore(storeName);
        var counterCode = [counter, barcode];
        var indexs = objectStore.index('counter_code');
        var request = indexs.openCursor(IDBKeyRange.only(counterCode));
        var data = [];
        return new Promise((resolve, reject) => {
            request.onsuccess = function (e) {
                var cursor = e.target.result;
                if (cursor) {
                    data.push(cursor.value);
                    //resolve(data);
                    cursor.continue();
                } else {
                    resolve(data)
                }

            }
        })
    },
    //根据key查询数量是否存在
    getqtyBykey: function (db, storeName, key) {
        var transaction = db.transaction(storeName);
        var objectStore = transaction.objectStore(storeName);
        var request = objectStore.get(key);
        request.onerror = function (event) {
            console.log('事务失败');
        };
        return new Promise((resolve, reject) => {
            request.onsuccess = function (event) {
                if (request.result) {
                    //console.log(request.result.qty)
                    resolve(request.result);
                } else {
                    resolve(false);
                }
            };
        })

    },

    //查询所有的柜台
    getAllCounter: function (db, storename) {
        var transaction = db.transaction(storename);
        var store = transaction.objectStore(storename);
        var indexs = store.index('counter_index');
        var data = [];
        return new Promise((resolve, reject) => {
            indexs.openCursor().onsuccess = function (e) {
                var cursor = e.target.result;
                if (cursor) {
                    //    console.log(cursor.value.counter);
                    data.push(cursor.value.counter);
                    resolve(data);
                    cursor.continue();
                }
            }
        })
    },

    getqtybyqtyindex: function (db, storename) {
        var transaction = db.transaction(storename);
        var store = transaction.objectStore(storename);
        var indexs = store.index('qty_index');
        var sum = 0;
        return new Promise((resolve, reject) => {
            indexs.openCursor().onsuccess = function (e) {
                var cursor = e.target.result;
                if (cursor) {
                    sum += cursor.value.qty
                    cursor.continue();
                }
                else {
                    resolve(sum)
                }
            }
        })
    }*/
}
