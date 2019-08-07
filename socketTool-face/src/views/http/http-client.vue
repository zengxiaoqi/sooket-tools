<template>
    <el-row>
        <el-col :span="4">
            <el-button type="primary" size="mini" icon="el-icon-plus" @click="addOne">新增</el-button>
            <!--<el-button type="primary" size="mini" icon="el-icon-minus" @click="delOne">删除</el-button>-->
            <!--<el-tree :data="httpList" :props="defaultProps" default-expand-all @node-click="handleNodeClick"></el-tree>-->
            <render-tree :data="httpList" @nodeClick="handleNodeClick" @contextmenu.prevent.native="openRightMenu($event)"></render-tree>
        </el-col>
        <el-col :span="20">
            <el-col :span="20">
                <el-input v-model="httpClient.request.httpURL" placeholder="请输入URL">
                    <el-select  v-model="httpClient.request.httpMethod"  placeholder="请选择" slot="prepend">
                        <el-option
                            v-for="item in methodOptions"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                        </el-option>
                    </el-select>
                </el-input>
            </el-col>
            <el-col :span="2">
                <el-button type="primary" @click="sendHttpRequest">发送</el-button>
            </el-col>
        </el-col>
        <el-col :span="20">
            <el-tabs v-model="httpClient.activeTabName" @tab-click="handleTabClick">
                <el-tab-pane label="Params" name="Params">
                    <!--<params-table :param-list.sync="ParamList" @listChange="listChange"></params-table>-->
                    <!--.sync父子组件双向数据绑定-->
                    <params-table :param-list.sync="httpClient.request.ParamList"></params-table>
                </el-tab-pane>
                <el-tab-pane label="Headers" name="Headers">
                    <!--<params-table :param-list.sync="headList" @listChange="listChange"></params-table>-->
                    <params-table :param-list.sync="httpClient.request.headList"></params-table>
                </el-tab-pane>
                <el-tab-pane label="Body" name="Body">
                    <el-col :span="20">
                        <!--<radio-group :radios="bodyRadios" @selectRadio="selectRadio"></radio-group>-->
                        <radio-group :radios="bodyRadios" :selectRadio.sync="httpClient.bodyRadio"></radio-group>
                    </el-col>
                    <el-col :span="4">
                        <el-select v-if="httpClient.bodyRadio=='row'" size="mini" v-model="httpClient.contentType" placeholder="请选择">
                            <el-option
                                v-for="item in contentTypeOpt"
                                :key="item.value"
                                :label="item.label"
                                :value="item">
                            </el-option>
                        </el-select>
                    </el-col>
                    <!--<el-divider></el-divider>-->
                    <!--<params-table v-if="bodyRadio == 'form-data'" :param-list.sync="bodyList" @listChange="listChange"></params-table>-->
                    <params-table v-show="httpClient.bodyRadio == 'form-data'" :param-list.sync="httpClient.request.formList" ></params-table>
                    <params-table v-show="httpClient.bodyRadio == 'x-www-form-urlencoded'" :param-list.sync="httpClient.request.bodyList" ></params-table>
                    <!--<el-input v-model="bodyText" v-else-if="bodyRadio == 'row'" type="textarea" rows="10"></el-input>-->
                    <vue-ace-editor ref="reqEdit" :lang="httpClient.contentType.type" :contentVal.sync="httpClient.request.bodyText" v-show="httpClient.bodyRadio == 'row'" ></vue-ace-editor>
                    <file-upload v-show="httpClient.bodyRadio == 'binary'"
                                 ref="uploadFile"
                                 :headers="fileUploadParam.headers"
                                 :data="fileUploadParam.data"
                                 :limit="fileUploadParam.limit"
                                 :tips="fileUploadParam.tips"
                    >
                    </file-upload>
                </el-tab-pane>
                <el-tab-pane label="Cookie" name="Cookie">
                    <!--<params-table :paramList.sync="cookieList" @listChange="listChange"></params-table>-->
                    <params-table :paramList.sync="httpClient.request.cookieList" ></params-table>
                </el-tab-pane>
            </el-tabs>
        </el-col>
        <el-col :span="20" style="float: right">
            <el-divider style="el-divider: el-divider--horizontal" content-position="left">Response</el-divider>
            <el-tabs v-model="respTabName" @tab-click="handleTabClick">
                <el-tab-pane label="Body" name="Body">
                    <el-col :span="4">
                        <radio-group :radios="respRadios" :selectRadio.sync="respRadio" :radioType="radioType"></radio-group>
                    </el-col>
                    <el-col :span="2">
                        <el-select size="mini" v-model="httpClient.txtType" v-if="respRadio=='Pretty'">
                            <el-option
                                v-for="item in highLightOpt"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value">
                            </el-option>
                        </el-select>
                    </el-col>

                    <!-- 显示应答报文体 -->
                    <vue-ace-editor ref="respEdit" :lang="httpClient.txtType" v-show="respRadio=='Pretty'"></vue-ace-editor>
                    <el-input type="textarea" :rows="10" v-model="httpClient.response.respDataTxt" v-show="respRadio=='Raw'"></el-input>
                </el-tab-pane>
                <el-tab-pane label="Headers" name="Headers">
                    <el-table :data="httpClient.response.respHeaders" style="width: 100%">
                        <el-table-column prop="key" label="KEY"></el-table-column>
                        <el-table-column prop="value" label="VALUE"></el-table-column>
                    </el-table>
                </el-tab-pane>
            </el-tabs>
            <el-col :span="4">
                <label>Status: {{this.httpClient.response.status}}  {{this.httpClient.response.statusText}}</label>
            </el-col>
            <el-col :span="4">
                <label>Time: {{this.httpClient.response.times}}ms</label>
            </el-col>

        </el-col>

        <el-dialog
            title="新建HTTP请求"
            :visible.sync="dialogVisible"
            width="30%"
            close-on-click-modal>
            <div>Request name</div>
            <el-input v-model="httpLabel"></el-input>
            <el-button @click="dialogVisible = false">取 消</el-button>
            <el-button type="primary" @click="addTree">确 定</el-button>
        </el-dialog>
        <right-menu :menuItems="menuItems" @onClick="rightMenuClick"></right-menu>
    </el-row>
</template>

<script>

import ParamsTable from "./compments/ParamsTable"
import RadioGroup from "./compments/RadioGroup"
import FileUpload from "./compments/FileUpload"
import {axiosGet, axiosPost} from "@/api/axiosApi"
import ObjectUtil from "@/utils/ObjectUtil.js"
import VueAceEditor from "@/components/VueAceEditor"
import IndexedDB from "@/utils/indexedDB.js"
import { guuid } from '@/utils/index.js'
import { mapGetters,mapState } from "vuex";
import RenderTree from "./compments/RenderTree";
import {formateXml} from "../../utils";
import RightMenu from "./compments/RightMenu"

export default {
    name: "http-client",
    components: {RenderTree, ParamsTable, RadioGroup, FileUpload,VueAceEditor, RightMenu},
    comments: { ParamsTable },

    mounted() {
        /*IndexedDB.openDB(this.$store.state.indexedDB.dbName,
                         this.$store.state.indexedDB.version,
                        this.$store.state.indexedDB.db,
                        this.dbInfo,this.saveDb);*/
        //this.getTree();
        //阻止浏览器默认事件
        document.addEventListener('keydown',this.handleEvent)
    },
    created() {
        //创建数据库和表
        IndexedDB.openDB(this.dbName, this.version, this.db,this.dbInfo,this.saveDb);
    },
    watch: {
        httpClient: function (val) {
            //更新数据到 indexedDB
        },
    },
    beforeDestroy() {
        //页面关闭时保存页面数据到indexedDB
        this.saveAllToDb();
        //需要销毁事件 防止全局生效
        document.removeEventListener('keydown', this.handleEvent);
    },
    computed: {
        //...mapGetters(['dbName','db','version']),
        ...mapState({
            dbName : state => state.indexedDB.dbName,
            db : state => state.indexedDB.db,
            version : state => state.indexedDB.version,
            httpList : state => state.indexedDB.httpList,
        }),

    },
    data() {
        return {

            defaultProps: {
                children: 'children',
                label: 'label',
                leaf: 'leaf',
            },
            methodOptions: [{
                value: 'GET',
                label: 'GET'
            },{
                value: 'POST',
                label: 'POST'
            },{
                value: 'PUT',
                label: 'PUT'
            },{
                value: 'PATCH',
                label: 'PATCH'
            },{
                value: 'DELETE',
                label: 'DELETE'
            },
            ],
            highLightOpt: [{
                value: 'abc',
                label: 'TEXT'
            },{
                value: 'json',
                label: 'JSON'
            },{
                value: 'xml',
                label: 'XML'
            },{
                value: 'html',
                label: 'HTML'
            },],
            contentTypeOpt: [{
                value: 'text/plain',
                label: 'TXT(text/plain)',
                type: "abc"
            },{
                value: 'application/json',
                label: 'JSON(application/json)',
                type: "json"
            },{
                value: 'application/xml',
                label: 'XML(application/xml)',
                type: "xml"
            },{
                value: 'text/xml',
                label: 'XML(text/xml)',
                type: "xml"
            },{
                value: 'text/html',
                label: 'HTML(text/html)',
                type: "html"
            },{
                value: 'application/javascript',
                label: 'JavaScript(application/javascript)',
                type: "javascript"
            },
            ],

            httpBody: {
                headers: {},
                params: {},
                bodyContext: "",
                cookies: {},
                httpMethod: "",
                httpURL: "",
                contentType: "application/x-www-form-urlencoded",
            },
            bodyRadios: [{
                name: "form-data",
                value: "form-data",
            },{
                name: "x-www-form-urlencoded",
                value: "x-www-form-urlencoded",
            },{
                name: "row",
                value: "row",
            },{
                name: "binary",
                value: "binary",
            }],
            respRadios: [{
                name: "Pretty",
                value: "Pretty",
            },{
                name: "Raw",
                value: "Raw",
            },],
            respRadio: "Pretty",
            radioType: "button",
            respTabName: "Body",
            respHeadList: [],
            fileUploadParam: {
                headers: null,
                data: {
                    destUrl: "",
                    params: null,
                },
                limit: 3,
                multiple: true,
                tips: "一次最多选择3个文件",
            },
            dbInfo: [{
                name: "http_info",
                key: "id"
            },{
                name: "clientTree_info",
                key: "id"
            }],
            dialogVisible: false,
            highLight: false,
            httpLabel: "",
            dbHttpInfo: {
                id: "",
                name: "",
                createdAt: "",
                iterations: {},
            },
            httpClient: {
                contentType: {
                    value: 'text/plain',
                    label: 'TXT(text/plain)',
                    type: "abc"
                },
                activeTabName: "Params",
                txtType: 'abc',
                bodyRadio: "form-data",
                request:{
                    httpMethod: "GET",
                    httpURL: "",
                    ParamList: [],
                    headList: [],
                    bodyList: [],
                    bodyText: null,
                    cookieList: [],
                    beginTime: 0,
                },
                response: {
                    respHeaders: null,
                    respData: null,
                    respDataTxt: null,
                    status: null,
                    statusText: null,
                    endTime: 0,
                    times: -1,
                    respLength: 0,
                },
            },

            menuItems: [{
                name: '复制',
                func: "copy",
            },{
                name: '修改',
                func: "modify",
            },{
                name: '删除',
                func: "delete",
            }],
        };
    },

    methods: {
        initHttpClient(){
            this.httpClient = {
                contentType: {
                    value: 'text/plain',
                    label: 'TXT(text/plain)',
                    type: "abc"
                },
                activeTabName: "Params",
                txtType: 'abc',
                bodyRadio: "form-data",
                request:{
                    httpMethod: "GET",
                    httpURL: "",
                    ParamList: [],
                    headList: [],
                    formList: [],
                    bodyList: [],
                    bodyText: "",
                    cookieList: [],
                    beginTime: (new Date()).getTime(),
                },
                response: {
                    respHeaders: null,
                    respData: "",
                    status: null,
                    statusText: null,
                    endTime: 0,
                    times: -1,
                    respLength: 0,
                },
            }
            return this.httpClient;
        },
        addOne: function(){
            this.dialogVisible = true;
        },
        addTree: function(){
            let _this = this;
            if(_this.httpLabel != null && _this.httpLabel != ""){
                _this.dialogVisible = false;
                let data = {id: guuid(), label: _this.httpLabel};
                //this.httpList.push(data);
                _this.$store.dispatch("indexedDB/addhttplist", data);

                _this.dbHttpInfo.id = data.id;
                _this.dbHttpInfo.name = data.label;
                _this.dbHttpInfo.createdAt = new Date();
                _this.dbHttpInfo.iterations = _this.initHttpClient();
                //let db = this.$store.state.indexedDB.db;
                IndexedDB.putData(_this.db,_this.dbInfo[0].name,_this.dbHttpInfo, function (result) {
                    if(!result){
                        console.error("入库失败数据...")
                        console.error(errData);
                    }
                });
                //let httpList = this.$store.state.indexedDB.httpList;
                IndexedDB.putDatas(_this.db,_this.dbInfo[1].name, _this.httpList, function (errData) {
                    if(errData.length > 0){
                        console.error("入库失败数据...")
                        console.error(errData);
                    }
                });
            }else {
                this.$message.error("输入数据不能为空");
            }

        },
        sendHttpRequest: function() {
            let _this = this;
            if(_this.httpClient.bodyRadio == "form-data" && _this.httpClient.request.formList.length>0){
                _this.httpBody.bodyContext = ObjectUtil.array2Map(_this.httpClient.request.formList);
                _this.httpBody.contentType = "multipart/form-data";
            }else if(_this.httpClient.bodyRadio == "row" && null != _this.httpClient.request.bodyText){
                _this.httpBody.bodyContext = _this.httpClient.request.bodyText;
                _this.httpBody.contentType = _this.httpClient.contentType.value;
            }else if(_this.httpClient.bodyRadio == "x-www-form-urlencoded" && _this.httpClient.request.bodyList.length>0){
                _this.httpBody.bodyContext = ObjectUtil.array2Map(_this.httpClient.request.bodyList);
                _this.httpBody.contentType = "x-www-form-urlencoded";
            }else {
                _this.httpBody.contentType = _this.httpClient.contentType.value;
            }
            if(_this.httpClient.request.headList.length > 0){
                _this.httpBody.headers = ObjectUtil.array2Map(_this.httpClient.request.headList);
            }
            if(_this.httpClient.request.ParamList.length > 0){
                _this.httpBody.params = ObjectUtil.array2Map(_this.httpClient.request.ParamList);
            }
            _this.httpBody.headers['Content-Type'] = _this.httpBody.contentType;
            _this.httpBody.headers['DEST_URL'] = _this.httpClient.request.httpURL;
            _this.httpBody.headers['Access-Control-Allow-Origin'] = "*";
            console.log(_this.httpBody);
            _this.httpClient.request.beginTime = (new Date()).getTime();
            if(_this.httpClient.request.httpMethod == "GET"){
                axiosGet("/http/httpRequest",
                    _this.httpBody.headers,
                    _this.httpBody.params
                ).then(res => {
                    console.log(res)
                });
            }else if(_this.httpClient.request.httpMethod == "POST"){
                axiosPost("/http/httpRequest",
                    _this.httpBody.headers,
                    _this.httpBody.params,
                    _this.httpBody.bodyContext
                ).then(res => {
                    console.log(res);
                    _this.responseControl(res);
                    //baody显示应答信息
                    //_this.initContent("response", res.data);
                });
            }

        },
        handleNodeClick: function (data,flag) {
            console.log("选中的节点为："+data.label);
            let _this = this;
            if(flag == "click"){
                //根据id获取数据中的数据
                IndexedDB.getDataById(_this.db, _this.dbInfo[0].name, data.id).then(res =>{
                    if(res != null){
                        console.log(res.iterations);
                        _this.httpClient = res.iterations;
                        _this.dbHttpInfo = res;
                        //设置请求/应答报文内容
                        _this.initContent("request",_this.httpClient.request.bodyText);
                        _this.initContent("response",_this.httpClient.response.respData);
                    }
                });
            }else if(flag == "delete"){
                console.log("删除数据库中数据，ID："+data.id);
                IndexedDB.deleteData(_this.db, _this.dbInfo[0].name, data.id, null);
                IndexedDB.deleteData(_this.db, _this.dbInfo[1].name, data.id, null);
            }

        },
        handleTabClick: function () {

        },

        /*contentChange: function (val) {
            this.rspData = val;
        },*/

        initContent: function (flag,val) {
            let _this =this;
            if(flag == "request"){
                //setTimeout 解决子组件还未渲染完成的问题，Uncaught (in promise) TypeError: Cannot read property 'setContent' of undefined
                setTimeout(() => {
                    //设置请求报文内容
                    _this.$refs.reqEdit.setContent(val);
                }, 100);
                /*_this.$nextTick(() => {
                    _this.$refs.reqEdit.setContent(val);
                });*/
            }else {
                setTimeout(() => {
                    //设置请求报文内容
                    _this.$refs.respEdit.setContent(val);
                }, 100);
            }
        },
        saveDb: function (db) {
            let _this = this;
            _this.$store.dispatch("indexedDB/setdb", db);
            //_this.$store.dispatch("indexedDB/setversion", _this.version);
            _this.getTree();
        },
        getTree: function () {
            let _this = this;
            console.log("获取树节点...");
            console.log(_this.db);
            //let db = this.$store.state.indexedDB.db;
            if(_this.db != null){
                IndexedDB.getdatabycursor(_this.db, _this.dbInfo[1].name).then(res =>{
                        console.log("查询结果：");
                        console.log(res);
                        _this.$store.dispatch("indexedDB/sethttplist", res);
                    }
                )
            }else {
                _this.$message.error("数据库没有创建");
            }

        },
        handleEvent(event){

            if (event.keyCode === 37) {
                console.log('拦截到37');
                //this.switchBno(false);//自己的方法 37=←
                event.preventDefault();
                event.returnValue = false;
                return false;
            }else if(event.keyCode === 39){
                console.log('拦截到39');
                //this.switchBno(true);//39=→
                event.preventDefault();
                event.returnValue = false;
                return false;
            }else if(event.keyCode === 83 && event.ctrlKey){
                console.log('拦截到ctrl+s');//ctrl+s
                this.saveAllToDb();
                event.preventDefault();
                event.returnValue = false;
                return false;
            }else if(event.keyCode === 81 && event.ctrlKey){
                console.log('拦截到Q+ctrl');
                //this.addWatermark();//ctrl+q
                event.preventDefault();
                event.returnValue = false;
                return false;
            }

        },
        saveAllToDb: function () {
            let _this = this;
            _this.dbHttpInfo.iterations = _this.httpClient;
            IndexedDB.putData(_this.db,_this.dbInfo[0].name,_this.dbHttpInfo, function (result) {
                if(!result){
                    _this.$message.error("数据保存失败");
                    console.error("入库失败数据...");
                    console.error(_this.dbHttpInfo);
                }else {
                    _this.$message.success("数据保存成功");
                }
            });
        },

        responseControl: function (response) {
            let _this = this;
            var responText = "";

            //_this.httpClient.response = response;
            let header = response.headers;

            _this.httpClient.response.respHeaders = ObjectUtil.map2Array(header);

            console.log("应答报文头：");
            console.log(header);
            console.log(_this.httpClient.response.respHeaders);

            let contentType = header["content-type"];
            if(contentType.indexOf("json") > 0){
                //格式化输出
                responText = JSON.stringify(response.data, null, "\t");
                _this.httpClient.response.respDataTxt = JSON.stringify(response.data);
                _this.httpClient.txtType = "json";
            }else if(contentType.indexOf("xml") > 0){
                responText = formateXml(response.data);
                _this.httpClient.txtType = "xml";
                _this.httpClient.response.respDataTxt = response.data;
            }else {
                responText = response.data;
                _this.httpClient.txtType = "abc";
                _this.httpClient.response.respDataTxt = response.data;
            }
            _this.initContent("response", responText);

            /* 设置应答信息 */
            _this.httpClient.response.respData = responText;
            _this.httpClient.response.status = response.status;
            _this.httpClient.response.statusText = response.statusText;
            _this.httpClient.response.endTime = (new Date()).getTime();
            _this.httpClient.response.times = _this.httpClient.response.endTime - _this.httpClient.request.beginTime;
        },

        /**
         * 打开右键菜单
         */
        openRightMenu (e) {
            console.log("右键点击：");
            console.log(e);
            this.$store.commit('rightMenu/SET_VISIBLE', true);
            this.$store.commit('rightMenu/SET_LEFT', e.clientX);
            this.$store.commit('rightMenu/SET_TOP', e.clientY);
        },
        rightMenuClick(item){
            let _this = this;
            if(_this.dbHttpInfo.id != null && _this.dbHttpInfo.id != ''){
                if(item.func == 'copy'){
                    _this.copy();
                }else if(item.func == 'modify'){

                }else if(item.func == 'delete'){

                }
            }else {
                _this.$message.warning("请先选择一条数据");
            }
        },
        copy(){
            let _this = this;
            /* 保存原数据 */
            _this.saveAllToDb();
            //let copyDb = _this.dbHttpInfo;    //对象浅拷贝，dbHttpInfo变化会引起copyDb变
            let copyDb = Object.assign({}, _this.dbHttpInfo);   //对象深拷贝
            _this.httpLabel = _this.dbHttpInfo.name + " Copy";
            _this.addTree();
            _this.dbHttpInfo.iterations = copyDb.iterations;
            _this.httpClient = copyDb.iterations;
            _this.saveAllToDb();
        },
        modify(){

        },
        delete(){

        },

    },
}
</script>

<style>
    .el-divider {
        background-color: #DCDFE6;
        position: relative;
    }

    .el-divider--horizontal {
        display: block;
        height: 4px;
        width: 100%;
        margin: 24px 0;
    }

    el-divider {
        el-divider: el-divider--horizontal;
    }

    .el-button+.el-button {
        margin-left: 0px;
    }
    .el-button {
        margin-left: 0px;
    }

    el-button {
        el-button: el-button;
    }

    pre {
        display: block;
        font-family: monospace;
        white-space: pre;
        margin: 0px 0px;
    }
    .hljs {
        display: block;
        overflow-x: auto;
        padding: 0.0em;
        background: rgba(24, 24, 24, 0.95);
    }

    .el-select .el-input {
        width: 120px;
    }
</style>
<style scoped>

</style>
