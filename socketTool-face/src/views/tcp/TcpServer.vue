<template>
<div>
    <!-- 工具条 -->
    <el-button type="primary" @click="addTcpServer" >新增</el-button>
    <el-button type="primary" :disabled="isOpen" @click="startServer" >启动监听</el-button>
    <el-button type="primary" :disabled="!isOpen" @click="closeServer" >停止监听</el-button>
    <el-button type="primary" @click="sendData" >发送数据</el-button>
    <el-row>
        <el-col :span="4">
            <el-tree :data="serverList" :props="defaultProps" default-expand-all @node-click="handleNodeClick"></el-tree>
        </el-col>
        <!--<el-col :span="10">
            &lt;!&ndash; 表格体 &ndash;&gt;
          <el-table
            :data="tableData.data"
            element-loading-text="Loading"
            border
            fit
            highlight-current-row
            @current-change="rowClick"
          >
            <el-table-column align="center" label="序号" >
              <template slot-scope="scope">
                {{ scope.$index }}
              </template>
            </el-table-column>
            <el-table-column label="ID">
              <template slot-scope="scope">
                {{ scope.row.id }}
              </template>
            </el-table-column>
            <el-table-column label="PORT" sortable align="center">
              <template slot-scope="scope">
                <span>{{ scope.row.port }}</span>
              </template>
            </el-table-column>
            <el-table-column class-name="status-col" label="Status"  align="center">
              <template slot-scope="scope">
                <el-tag :type="scope.row.status | statusFilter">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-col>-->
        <el-col :span="20">
            <!--<el-form ref="serverForm" :model="serverForm" label-width="80px">
                <el-form-item label="连接">
                    <el-button v-model="serverForm.state"></el-button>
                </el-form-item>
                <el-form-item label="断开">
                    <el-button v-model="serverForm.state"></el-button>
                </el-form-item>
            </el-form>-->

            <div class="top-container">接收消息：
                <el-input type="textarea" :rows="10" v-model="recvMsg" :readonly="readonly"></el-input>
            </div>
            <div class="bottom-container">发送消息：
                <el-input type="textarea" :rows="10" v-model="sendMsg" ></el-input>
                <el-checkbox v-model="checkedHex">HEX</el-checkbox>
            </div>
            <split-pane v-if="hasRowSelect" split="horizontal">
                <template slot="paneL">

                </template>
                <template slot="paneR">

                </template>
            </split-pane>


        </el-col>
    </el-row>

    <!--编辑弹框-->
    <el-col :span="24">
        <el-dialog :title="title" :visible.sync="editFormVisible" :close-on-click-modal="true" top>
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
import { getServerInfo,createServer,sendData,getRcvMsg,closeServer,getIP } from '@/api/tcp'
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
                statue: "",
                recvMsg: "",
                sendMsg: "",
            },

            //弹框form表单校验规则
            editFormRules: {
                /*id: [{ required: true, message: "该输入项为必填项!" }],*/
                port: [{ required: true, message: "该输入项为必填项!" }],
            },
            readonly: true,
            hasRowSelect: true,
            currentRow: null,
            checkedHex: false,
            sendMsg: "",
            recvMsg: "",
            clearTimeSet: null,
            localIP: document.location.hostname,
        }
    },
    mounted() {
        //this.getTableData();
        this.getIP();
        //设置定时任务-定时获取服务端接收的报文
        this.setTime();
        //this.treeData = this.$store.state.websocket.serverList;
    },
    beforeDestroy() {    //页面关闭时清除定时器
        clearInterval(this.clearTimeSet);
    },
    computed: {
        ...mapGetters(['serverInfo','serverList']),

    },
    watch: {

    },
    methods: {
        getIP() {
            getIP().then(response => {
                this.localIP = response.data;
                this.editFormModel.ip = response.data;
            })
        },
        handleNodeClick(data) {
            console.log(data);
            if(data.leaf) {
                console.log("点击叶子节点，显示数据");
                //this.getTableData(data.id);
            }else {
                let param = {id: data.id};
                getServerInfo(param).then(response => {
                    console.log(response.data);
                    this.isOpen = response.data.status;
                })
            }
        },
        // 获取table数据
        getTableData(id) {
            let param = {id: id};
          getServerInfo(param).then(response => {
            this.loading = false
            this.tableData.data = response.data
            response.data.forEach((value)=> {
              this.$store.dispatch('app/setServerInfo', value)
            })
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

          _this.$store.dispatch('app/setServerInfo', model)

            _this.$refs.editFormModel.validate(valid => {
                if (valid) {
                    let url = "";
                    let params = {};
                    if (_this.isAdd) {
                        createServer(model).then(response => {
                          _this.$message.success("创建服务端成功");
                            _this.editFormVisible = false;
                            _this.isOpen = false;
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
            _this.currentRow = row;
            _this.hasRowSelect = true;
        },

        sendData() {
            let _this = this;
            if(_this.currentRow == null){
                _this.$message({
                    type: 'info',
                    message: '请选择一行数据'
                });
                return;
            }
            var Id = _this.currentRow["id"];
            var tmpDate = _this.serverInfo.get(Id);
            tmpDate.sendMsg = _this.sendMsg;
            sendData(tmpDate).then(response => {
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
            if(_this.currentRow != null) {
                //_this.param.id = _this.currentRow["id"];
                let param = {"id": _this.currentRow["id"]}
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
            if(_this.currentRow != null) {
                createServer(_this.currentRow).then(response => {
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
            if(_this.currentRow != null) {
                _this.param.id = _this.currentRow["id"];
                closeServer(_this.param).then(response => {
                        _this.$message.success("服务关闭成功");
                        //console.log("接收数据: "+response.data);
                        _this.tableData.data = response.data;
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
