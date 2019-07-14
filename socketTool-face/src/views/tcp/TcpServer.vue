<template>
<div>
    <!-- 工具条 -->
    <el-button type="primary" @click="addTcpServer" >新增</el-button>
    <el-button type="primary" @click="delTcpServer" >删除</el-button>
    <el-button type="primary" :disabled="isOpen" @click="startServer" >启动监听</el-button>
    <el-button type="primary" :disabled="!isOpen" @click="closeServer" >停止监听</el-button>
    <el-button type="primary" @click="sendData" >发送数据</el-button>
    <el-button type="primary" @click="freshLog" >{{freshButton}}</el-button>
    <el-row>
        <el-col :span="4">
            <el-tree :data="serverList" :props="defaultProps" default-expand-all @node-click="handleNodeClick"></el-tree>
        </el-col>
        <el-col :span="20">
            <!-- 表格体 -->
          <el-table
            :data="tableData.data"
            element-loading-text="Loading"
            border
            fit
            highlight-current-row
            @current-change="rowClick"
          >
            <el-table-column sortable label="本地IP">
              <template slot-scope="scope">
                {{ scope.row.ip }}
              </template>
            </el-table-column>
            <el-table-column label="本地端口" sortable align="center">
              <template slot-scope="scope">
                <span>{{ scope.row.port }}</span>
              </template>
            </el-table-column>
              <el-table-column label="对方IP" sortable align="center">
                  <template slot-scope="scope">
                      <span>{{ scope.row.remoteIp }}</span>
                  </template>
              </el-table-column>
              <el-table-column label="对方端口" sortable align="center">
                  <template slot-scope="scope">
                      <span>{{ scope.row.remotePort }}</span>
                  </template>
              </el-table-column>
            <el-table-column class-name="status-col" sortable label="连接状态"  align="center">
              <template slot-scope="scope">
                <el-tag :type="scope.row.status | statusFilter">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>

            <div class="top-container">接收消息：
                <el-input type="textarea" :rows="10" v-model="recvMsg" :readonly="readonly"></el-input>
            </div>
            <div class="bottom-container">发送消息：
                <el-checkbox v-model="checkedHex">HEX</el-checkbox>
                <el-input type="textarea" :rows="10" v-model="sendMsg" ></el-input>
            </div>
        </el-col>
    </el-row>

    <!--编辑弹框-->
    <el-col :span="24">
        <el-dialog :title="title" :visible.sync="editFormVisible" :close-on-click-modal="true" center>
            <!--el-form 的ref值必须为editForm -->
            <el-form :model="editFormModel" label-width="80px" :rules="editFormRules" ref="editFormModel">
                <el-col :span="12">
                    <el-form-item label="监听IP：" prop="ip" label-width="100px">
                        <el-input v-model="localIP" :disabled="true"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="端口：" prop="port" label-width="100px">
                        <el-input clearable v-model="editFormModel.port"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="字符编码：" prop="port" label-width="100px">
                        <el-select v-model="editFormModel.encode" filterable placeholder="请选择">
                            <el-option
                                v-for="item in options"
                                :key="item.value"
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
</div>
</template>


<script>
import { mapGetters } from "vuex";
import { getServerInfo,createServer,sendData,getRcvMsg,closeServer,getIP,getSocketInfo,startServer,getNodeTree,delServer } from '@/api/tcp'
import splitPane from 'vue-splitpane'

export default {
    name: "TcpServer",
    components: { splitPane },
    filters: {
      statusFilter(status) {
        const statusMap = {
          open: 'success',
          close: 'danger',
        }
        return statusMap[status]
      }
    },
    data() {
        return {
            isOpen: true,
            //treeData: [],
            defaultProps: {
                parentId: 'parentId',
                children: 'children',
                label: 'id',
                isLeaf: 'leaf'
            },
          loading: true,  //v-loading在接口为请求到数据之前，显示加载中，直到请求到数据后消失
            searchForm: {
                type: '',
                text: ''
            },
            tableData: {
                data: []
            },
            param: {
              id: ""
            },
            //显示或隐藏编辑页面
            editFormVisible: false,
            //提交按钮是否禁用
            disabledBool: false,
            title: "新建服务端",
            titleTip: "TCP SERVER",
            defaultValues: {
                ip: this.localIP,
                port: ""
            },
            //form表单绑定的对象
            editFormModel: {
                id: "",
                ip: "",
                port: "",
                encode: "utf-8",
                statue: "",
                recvMsg: "",
                sendMsg: "",
            },

            //弹框form表单校验规则
            editFormRules: {
                /*id: [{ required: true, message: "该输入项为必填项!" }],*/
                encode: [{required: true, message: "该选择项为必填项!" }],
                port: [{ required: true, message: "该输入项为必填项!" }],
            },
            readonly: true,
            hasRowSelect: true,
            childNode: null,
            serverNode: null,
            checkedHex: false,
            sendMsg: "",
            recvMsg: "",
            clearTimeSet: null,
            refreshLog: false,
            freshButton: "刷新日志",
            localIP: document.location.hostname,
            options: [{
                value: 'GBK',
                label: 'GBK'
            }, {
                value: 'UTF-8',
                label: 'UTF-8'
            }, {
                value: 'ISO_8859_1',
                label: 'ISO_8859_1'
            }],
        }
    },
    mounted() {
        //this.getTableData();
        this.getIP();
        //设置定时任务-定时获取服务端接收的报文
        if(this.refreshLog){
            this.setTime();
        }
        this.getNodeTree();
    },
    beforeDestroy() {    //页面关闭时清除定时器
        clearInterval(this.clearTimeSet);
    },
    computed: {
        ...mapGetters(['serverInfo','serverList']),

    },
    watch: {
        refreshLog(newValue,oldVuale){
            if(newValue){
                this.setTime();
            }else if(null != this.clearTimeSet){
                clearInterval(this.clearTimeSet);
            }
        }
    },
    methods: {
        getIP() {
            getIP().then(response => {
                this.localIP = response.data;
                this.editFormModel.ip = response.data;
            })
        },
        getNodeTree(){
            let _this = this;
            getNodeTree().then(response => {
                console.log(response.data);
                _this.$store.dispatch("connect/setServerList", response.data)
            })
        },
        handleNodeClick(data) {
            let _this =this;
            console.log(data);
            if(data.leaf) {
                console.log("点击叶子节点，显示数据");
                _this.childNode = data;
                let params = {
                    parentId: data.parentId,
                    id: data.id,
                };
                _this.getTableData(params);
            }else {
                _this.serverNode = data;
                let param = {id: data.id};
                getServerInfo(param).then(response => {
                    console.log(response.data);
                    _this.isOpen = response.data.status;

                })
            }
        },
        // 获取table数据
        getTableData(params) {
            let _this = this;
            //let param = {id: id};
            getSocketInfo(params).then(response => {
                _this.loading = false
                _this.tableData.data = response.data;
                _this.recvMsg = response.data[0].message;
            /*response.data.forEach((value)=> {
              this.$store.dispatch('app/setServerInfo', value)
            })*/
          })
        },

        addTcpServer(index, row) {
            let _this = this;
            //调用公用编辑界面方法
            _this.handleEdit(index, row, true);
        },
        //显示编辑界面
        handleEdit: function(index, row, flag) {
            let _this = this;
            _this.isAdd = flag;

            if (_this.$refs.editFormModel != undefined) {
                _this.$refs.editFormModel.resetFields();
            }
            //_this.editFormModel = _this.defaultValues; //弹框默认值
            if (row) {
                _this.readonly = true;
                _this.$nextTick(() => {
                    _this.editFormModel = Object.assign({}, row);
                });
            } else {
                _this.readonly = false;
            }
            _this.editFormVisible = true;

        },
        submit() {
            let _this = this;
            let model = _this.$refs.editFormModel.model;

          //_this.$store.dispatch('app/setServerInfo', model)

            _this.$refs.editFormModel.validate(valid => {
                if (valid) {
                    let url = "";
                    let params = {};
                    if (_this.isAdd) {
                        createServer(model).then(response => {
                          _this.$message.success("创建服务端成功");
                            _this.editFormVisible = false;
                            _this.isOpen = true;
                            //this.tableData.data = response.data;
                            //_this.treeData = response.data;
                            _this.$store.dispatch('connect/setServerList', response.data);
                        });
                    } else {

                    }
                }
            });
        },

        rowClick(row) {
            let _this = this;
            //根据选中行的ID找到发送消息框和接收消息框的对应关系
            //console.log("---------"+row.id);
            //_this.childNode = row;
            //_this.hasRowSelect = true;
        },

        sendData() {
            let _this = this;
            if(_this.childNode == null){
                _this.$message({
                    type: 'info',
                    message: '请选择一行数据'
                });
                return;
            }

            let data = {
                id: _this.childNode["id"],
                parentId: _this.childNode["parentId"],
                sendMsg : _this.sendMsg,
                hexStr: _this.checkedHex,
            }
            sendData(data).then(response => {
              _this.$message.success("发送数据成功")
            });
        },

        setTime() //设置定时器
        {
            this.clearTimeSet=setInterval(() => {
                this.getRecvMsg();
            }, 1000);
        },
        getRecvMsg() {
            let _this = this;
            //根据当前选择行去后台请求数据
            if(_this.childNode != null) {
                //_this.param.id = _this.childNode["id"];
                let param = {
                    parentId: _this.childNode["parentId"],
                    id: _this.childNode["id"]
                }
                getRcvMsg(param).then(response => {
                        //_this.$message.success("接收数据成功");
                        console.log("接收数据: "+response.data);
                        _this.recvMsg = response.data;
                    }
                );
            }
        },

        startServer(){
            let _this = this;
            if(_this.serverNode != null) {
                startServer(_this.serverNode).then(response => {
                        _this.$message.success("启动监听成功");
                        //console.log("接收数据: "+response.data);
                        _this.tableData.data = response.data;
                    }
                ).catch(
                    response => {
                        console.log('catch data::', response)
                    });
            }else {
                _this.$message.warning("请选择一行数据")
            }
        },

        closeServer(){
            let _this = this;
            if(_this.serverNode != null) {
                _this.param.id = _this.serverNode["id"];
                closeServer(_this.param).then(response => {
                        _this.$message.success("服务关闭成功");
                        //console.log("接收数据: "+response.data);
                        //_this.$store.dispatch('connect/setServerList', response.data);
                        _this.isOpen = false;
                    }
                ).catch(
                    response => {
                        console.log('catch data::', response)
                    });
            }else {
                _this.$message.warning("请选择一行数据")
            }
        },
        delTcpServer(){
            let _this = this;
            if(_this.serverNode != null) {
                _this.param.id = _this.serverNode["id"];
                delServer(_this.param).then(response => {
                        _this.$message.success("服务删除成功");
                        _this.isOpen = false;
                        //_this.$store.dispatch('connect/setServerList', response.data);
                        _this.getNodeTree();    //实时刷新数据
                    }
                ).catch(
                    response => {
                        console.log('catch data::', response)
                    });
            }else {
                _this.$message.warning("请选择一行数据")
            }

        },
        freshLog(){
            let _this = this;
            _this.refreshLog = !_this.refreshLog;
            if(_this.refreshLog){
                _this.freshButton = "停止刷新";
            }else{
                _this.freshButton = "刷新日志";
            }
        },
    },
}
</script>

<style scoped>
    #three_dot_view {
        width: 20px;
        height: 400px;
    }

    .gray_line {
        background: #e5e5e5;
        width: 20px;
        height: 400px;
        float: left;
        margin: 10px 5px;
    }

    .top-container {
        background-color: #FCE38A;
        width: 100%;
        height: 100%;
    }

    .bottom-container {
        width: 100%;
        background-color: #95E1D3;
        height: 100%;
    }
</style>
