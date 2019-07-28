<template>
    <div>
        <el-button type="primary" @click="addTcpClient" >新增</el-button>
        <el-button type="primary" @click="delTcpClient" >删除</el-button>
        <!--<el-button type="primary" :disabled="isOpen" @click="connectServer" >连接</el-button>
        <el-button type="primary" :disabled="!isOpen" @click="closeServer" >断开</el-button>-->
        <el-button type="primary" @click="sendData" >发送数据</el-button>
        <el-button type="primary" @click="freshLog" >{{freshButton}}</el-button>
        <el-row>
            <el-col :span="10">
                <!-- 表格体 -->
                <el-table
                    :data="clientList"
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
                    <el-table-column align="center" label="操作" width="120">
                        <template slot-scope="{row}">
                            <el-button
                                v-if="row.status"
                                type="danger"
                                size="small"
                                icon="el-icon-circle-check-outline"
                                @click="statusOperation(row)"
                            >
                                close
                            </el-button>
                            <el-button
                                v-else
                                type="success"
                                size="small"
                                icon="el-icon-edit"
                                @click="statusOperation(row)"
                            >
                                open
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </el-col>
            <el-col :span="14">
                <div class="top-container">接收消息：
                    <el-input type="textarea" :rows="10" v-model="recvMsg" :readonly="true"></el-input>
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
                            <el-input clearable v-model="editFormModel.ip" ></el-input>
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
import { createClient, getClientList,stopClient,openClient,delClient,getRcvMsg,sendMsg} from '@/api/tcp'
export default {
    name: "TcpClient",

    filters: {
        statusFilter(status) {
            const statusMap = {
                true: 'success',
                false: 'danger',
            }
            return statusMap[status]
        }
    },

    data() {
        return {
            clientList: [],
            //显示或隐藏编辑页面
            editFormVisible: false,
            //提交按钮是否禁用
            disabledBool: false,
            title: "新建客户端",
            titleTip: "TCP CLIENT",
            //form表单绑定的对象
            editFormModel: {
                id: "",
                ip: "127.0.0.1",
                port: "8888",
                encode: "UTF-8",
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
            defaultValues: {
                ip: "127.0.0.1",
                port: "8888",
                //encode: "UTF-8",
            },
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
            checkedHex: false,
            sendMsg: "",
            recvMsg: "",
            currentRow: null,
            clearTimeSet: null,
            refreshLog: false,
            freshButton: "刷新日志",
        }
    },
    mounted() {
        this.getClientList();
        //设置定时任务-定时获取服务端接收的报文
        if(this.refreshLog){
            this.setTime();
        }
    },
    beforeDestroy() {    //页面关闭时清除定时器
        clearInterval(this.clearTimeSet);
    },
    computed: {
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
        addTcpClient(index, row){
            let _this = this;

            if (_this.$refs.editFormModel != undefined) {
                _this.$refs.editFormModel.resetFields();
            }
            //_this.editFormModel = _this.defaultValues; //弹框默认值
            if (row) {
                _this.$nextTick(() => {
                    _this.editFormModel = Object.assign({}, row);
                });
            }
            _this.editFormVisible = true;
        },
        submit(){
            let _this = this;
            let model = _this.$refs.editFormModel.model;
            _this.$refs.editFormModel.validate(valid => {
                if (valid) {
                    createClient(model).then(response => {
                        _this.$message.success("创建客户端成功");
                        console.log(response.data);
                        _this.editFormVisible = false;
                        _this.isOpen = true;
                        _this.clientList = response.data;
                    });
                }
            });
            _this.editFormVisible = false;
        },

        getClientList(){
            let _this = this;
            getClientList().then(response => {
                    //console.log(response.data);
                    _this.clientList = response.data;
                }
            );
        },

        statusOperation(row) {
            let _this = this;
            if(row.status){
                //关闭连接
                stopClient(row).then(response => {
                        //console.log(response.data);
                        _this.clientList = response.data;
                    }
                );
            }else{
                //打开连接
                openClient(row).then(response => {
                        //console.log(response.data);
                        _this.clientList = response.data;
                    }
                );
            }
        },

        delTcpClient(){
            let _this = this;
            delClient(_this.currentRow).then(response => {
                    //console.log(response.data);
                    _this.$message.success("删除成功");
                    _this.clientList = response.data;
                }
            );

        },
        sendData() {
            let _this = this;
            let rowData = _this.currentRow;
            if(_this.currentRow == null){
                _this.$message({
                    type: 'info',
                    message: '请选择一行数据'
                });
                return;
            }
            rowData.hexStr = _this.checkedHex;
            rowData.sendMsg = _this.sendMsg;
            sendMsg(rowData).then(response => {
                    _this.$message.success("发送数据成功");
                    //_this.recvMsg = response.data;
                }
            );
        },
        rowClick(currentRow) {
            let _this = this;
            _this.currentRow = currentRow;
            _this.getRecvMsg();
        },

        getRecvMsg() {
            let _this = this;
            //根据当前选择行去后台请求数据
            if(_this.currentRow != null) {
                getRcvMsg(_this.currentRow).then(response => {
                        //_this.$message.success("接收数据成功");
                        console.log("接收数据: "+response.data);
                        _this.recvMsg = response.data;
                    }
                );
            }
        },

        setTime() //设置定时器
        {
            this.clearTimeSet=setInterval(() => {
                this.getRecvMsg();
            }, 1000);
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
</style>
