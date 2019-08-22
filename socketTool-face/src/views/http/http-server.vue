<template>
    <el-row>
        <el-col :span="4">
            <el-button type="primary" size="mini" icon="el-icon-plus" @click="addHttpServer">新增</el-button>

            <el-scrollbar style="height:100%">
                <edit-tree :treeData="httpServerList"
                           ref="editTree"
                           :expand="true"
                           @nodeClick="handleNodeClick"
                >
                </edit-tree>
            </el-scrollbar>
        </el-col>
        <el-col :span="20">
            <!-- 表格体 -->
            <el-table
                :data="tableData"
                border
                fit
                highlight-current-row
            >
                <el-table-column  label="端口" prop="port" align="center" width="100"></el-table-column>
                <el-table-column label="路径" prop="path" align="center"></el-table-column>
                <el-table-column label="编码" prop="encode" align="center" width="100"></el-table-column>
                <el-table-column label="自动应答" align="center" width="100">
                    <template slot-scope="scope">
                        <span >{{ scope.row.autoResp }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="应答报文"  align="center" >
                    <template slot-scope="scope">
                        <el-input type="textarea" readonly v-if="scope.row.autoResp" >{{ scope.row.respStr }}</el-input>
                        <el-input type="textarea" v-model="httpMessage.respMsg" v-else></el-input>
                    </template>
                </el-table-column>
                <el-table-column label="操作"  width="100">
                    <template slot-scope="scope">
                        <el-button type="primary" @click="sendRespon" v-show="!scope.row.autoResp">发送</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <el-col :span="12">
                <el-divider content-position="left">接收信息</el-divider>
                <!--<el-button type="primary" >接收</el-button>-->
                <el-tabs v-model="recvTabName">
                    <el-tab-pane label="Body" name="Body">
                        <el-input type="textarea" :rows="10" v-model="httpMessage.recvMsg"></el-input>
                    </el-tab-pane>
                    <el-tab-pane label="Headers" name="Headers">
                        <el-table :data="httpMessage.recvHeaderList" style="width: 100%">
                            <el-table-column prop="key" label="KEY"></el-table-column>
                            <el-table-column prop="value" label="VALUE"></el-table-column>
                        </el-table>
                    </el-tab-pane>
                    <el-tab-pane label="Params" name="Params">
                        <el-table :data="httpMessage.recvParamList" style="width: 100%">
                            <el-table-column prop="key" label="KEY"></el-table-column>
                            <el-table-column prop="value" label="VALUE"></el-table-column>
                        </el-table>
                    </el-tab-pane>
                </el-tabs>
            </el-col>
            <!--<el-col :span="12">
                &lt;!&ndash;<el-divider content-position="left">发送信息</el-divider>&ndash;&gt;
                <el-button type="primary" >发送</el-button>
                <el-tabs v-model="sendTabName">
                    <el-tab-pane label="Body" name="Body">
                        <el-input type="textarea" :rows="10" v-model="httpMessage.sendMsg"></el-input>
                    </el-tab-pane>
                    <el-tab-pane label="Headers" name="Headers">
                        <el-table :data="httpMessage.sendHeaders" style="width: 100%">
                            <el-table-column prop="key" label="KEY"></el-table-column>
                            <el-table-column prop="value" label="VALUE"></el-table-column>
                        </el-table>
                    </el-tab-pane>
                </el-tabs>
            </el-col>-->


        </el-col>

        <!--编辑弹框-->
        <el-col :span="24">
            <el-dialog :title="title" :visible.sync="editFormVisible" :close-on-click-modal="true" center>
                <!--el-form 的ref值必须为editForm -->
                <el-form :model="editFormModel" label-width="80px" :rules="editFormRules" ref="editFormModel">
                    <el-col :span="12">
                        <el-form-item label="名称：" prop="name" label-width="100px">
                            <el-input clearable v-model="editFormModel.name"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="端口：" prop="port" label-width="100px">
                            <el-input clearable v-model="editFormModel.port"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="路径：" prop="path" label-width="100px">
                            <el-input v-model="editFormModel.path"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="是否自动应答：" prop="autoResp" label-width="100px">
                            <el-select v-model="editFormModel.autoResp" filterable placeholder="请选择">
                                <el-option
                                    v-for="item in autoRespOps"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="应答报文：" prop="respStr" label-width="100px">
                            <el-input type="textarea" v-model="editFormModel.respStr" placeholder="请输入内容"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="字符编码：" prop="encode" label-width="100px">
                            <el-select v-model="editFormModel.encode" filterable placeholder="请选择">
                                <el-option
                                    v-for="item in encodeOps"
                                    :key="item.label"
                                    :label="item.label"
                                    :value="item.value">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-form>
                <span slot="footer" class="ec-dialog-footer">
                  <el-button size="small" v-bind:disabled="disabledBool" type="primary" @click="submit">提交</el-button>
                  <el-button size="small" @click="editFormVisible=false">取消</el-button>
                </span>
            </el-dialog>
        </el-col>
    </el-row>
</template>

<script>
    import EditTree from "@/components/EditTree"
    import { createServer, getHttpServerTree,sendResp,getHttpMessage,delHtppNode} from '@/api/http'
    import { guuid } from '@/utils/index.js'
    import { mapGetters } from "vuex";
    import IndexedDB from "@/utils/indexedDB.js"
    import ObjectUtil from "@/utils/ObjectUtil.js"
export default {
    name: "http-server",
    components: {EditTree},
    computed: {
        ...mapGetters(['httpServerList','dbName','db','version']),

    },
    mounted() {
        this.getHttpServerTree();
    },
    created() {
        //创建数据库和表
        IndexedDB.openDB(this.dbName, this.version, this.db,this.dbInfo,this.saveDb);
    },
    watch:{
        //vue computed watch计算属性无法监听到数组内部变化
        /*tableData: function (val) {
            this.tableData = val;
        }*/
    },
    data() {
        return {
            dbInfo: [{
                name: "http_server_info",
                key: "id"
            }],
            //显示或隐藏编辑页面
            editFormVisible: false,
            //提交按钮是否禁用
            disabledBool: false,
            title: "新建HTTP服务端",
            //form表单绑定的对象
            editFormModel: {
                id: "",
                name: "",
                port: "",
                path: "",
                autoResp: false,
                respStr: "",
                isHttps: false,
                encode: "utf-8",
            },
            //弹框form表单校验规则
            editFormRules: {
                name: [{ required: true, message: "该输入项为必填项!" }],
                path: [{required: true, message: "该选择项为必填项!" }],
                port: [{ required: true, message: "该输入项为必填项!" }],
                autoResp: [{ required: true, message: "该输入项为必填项!" }],
            },
            autoRespOps: [{
                value: true,
                label: '是'
            }, {
                value: false,
                label: '否'
            }],
            encodeOps: [{
                value: 'GBK',
                label: 'GBK'
            }, {
                value: 'UTF-8',
                label: 'UTF-8'
            }, {
                value: 'ISO_8859_1',
                label: 'ISO_8859_1'
            }],
            tableData: [{
                id: "",
                name: "",
                port: "",
                path: "",
                autoResp: false,
                respStr: "",
                isHttps: false,
                encode: "utf-8",
            }],
            currNodeData: null,
            recvTabName: "Body",
            sendTabName: "Body",
            httpMessage: {
                parentId: null,
                id: null,
                recvHeaders: null,
                recvHeaderList: null,
                recvParamList: null,
                recvParams: null,
                recvMsg: "",
                respHeaders: null,
                respMsg: "",
                respFlag: null,
            },
        };
    },

    methods: {
        getHttpServerTree: function(){
            let _this = this;
            getHttpServerTree().then(response => {
                console.log(response.data);
                _this.$store.dispatch("connect/setHttpServerList", response.data)
            })
        },
        handleNodeClick: function (data,flag) {
            console.log("选中的节点为："+data.name);
            let _this = this;
            _this.currNodeData = data;
            if(flag === "click"){
                if(!data.leaf){
                    //根据id获取数据中的数据
                    IndexedDB.getDataById(_this.db, _this.dbInfo[0].name, data.id).then(res =>{
                        if(res != null){
                            console.log("查找到id的数据："+ data.id);
                            console.log(res);
                            //_this.tableData[0] = res; //直接赋值表格监听不到数组变化页面不会重新渲染，需要调用$set
                            this.$set(this.tableData, 0, res);
                        }else {
                            console.log("找不到id对应的数据："+ data.id);
                            //_this.tableData[0] = {};
                            this.$set(this.tableData, 0, {});
                        }
                    });
                }else{
                    //获取服务器中请求信息
                    getHttpMessage(data).then(response => {
                        console.log("getHttpMessage ok");
                        console.log(response.data);
                        _this.httpMessage = response.data;
                        _this.httpMessage.recvHeaderList = ObjectUtil.map2Array(response.data.recvHeaders);
                        _this.httpMessage.recvParamList = ObjectUtil.map2Array(response.data.recvParams);
                    })
                }

            }else if(flag === "delete"){
                if(!data.leaf){
                    console.log("删除数据库中数据，ID："+data.id);
                    IndexedDB.deleteData(_this.db, _this.dbInfo[0].name, data.id, function(){
                        console.error("删除数据库数据失败.");
                    });
                }
                delHtppNode(data).then(response => {
                    console.log("删除节点信息成功,id:"+data.id);
                    //_this.$store.dispatch("connect/setHttpServerList", response.data)
                })
            }else if(flag === "update"){
                console.log("更新数据库中数据，ID："+data.id);
                _this.tableData[0].name = data.name;
                IndexedDB.putData(_this.db, _this.dbInfo[0].name, _this.tableData[0], function (result) {
                    if(!result){
                        console.error("入库失败数据...");
                    }
                })
            }
        },
        addHttpServer: function (index, row) {
            let _this = this;
            if (row) {
                _this.$nextTick(() => {
                    _this.editFormModel = Object.assign({}, row);
                });
            }
            _this.editFormVisible = true;
        },
        submit() {
            let _this = this;
            _this.editFormModel.id = guuid();
            //_this.tableData[0] = _this.editFormModel;
            this.$set(_this.tableData, 0, _this.editFormModel);
            let model = _this.$refs.editFormModel.model;
            _this.$refs.editFormModel.validate(valid => {
                if (valid) {
                    createServer(model).then(response => {
                        _this.$message.success("创建服务端成功");
                        console.log(response);
                        _this.editFormVisible = false;
                        _this.$store.dispatch('connect/setHttpServerList', response.data);
                    });
                }
            });
            IndexedDB.putData(_this.db,_this.dbInfo[0].name,_this.editFormModel, function (result) {
                if(!result){
                    console.error("入库失败数据...");
                }
            });
        },
        saveDb: function (db) {
            let _this = this;
            _this.$store.dispatch("indexedDB/setdb", db);
        },
        sendRespon: function () {
            let _this = this;
            if(_this.currNodeData == null || !_this.currNodeData.leaf){
                _this.$message.warning("请选中一个请求节点!");
            }else if(_this.currNodeData.leaf){
                _this.httpMessage.parentId = _this.currNodeData.parentId;
                _this.httpMessage.id = _this.currNodeData.id;

                sendResp(_this.httpMessage).then(response => {
                    _this.$message.success("发送应答成功");
                    console.log(response);
                    //_this.$store.dispatch('connect/setHttpServerList', response.data);
                });
            }
        }
    },
}
</script>

<style scoped>
    .ec-dialog-footer {
        margin: 10px 200px;
        display: inline-block;
    }
</style>
