<template>
<div>
    <!-- 工具条 -->
    <el-button type="primary" @click="addTcpServer" >新增</el-button>
    <el-button type="primary" @click="startServer" >启动监听</el-button>
    <el-button type="primary" @click="closeServer" >停止监听</el-button>
    <el-button type="primary" @click="sendData" >发送数据</el-button>
    <el-row>
        <el-col :span="4">
            <el-tree :data="data" :props="defaultProps" @node-click="handleNodeClick"></el-tree>
        </el-col>
        <el-col :span="10">
            <!-- 表格体 -->
          <el-table
            v-loading="loading"
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
            <!--<el-table v-loading="loading" :data="tableData.data" border :default-sort="{prop: 'index', order: 'descending'}"
                      highlight-current-row
                      @current-change="rowClick">
                <el-table-column type="index" label="序号" width="64" align="center"></el-table-column>
                &lt;!&ndash;<el-table-column v-for="(item,index) in tableData.head" :prop="item.key" :label="item.name" sortable :key="index"></el-table-column>&ndash;&gt;
                <el-table-column prop="id" label="ID" align="center"></el-table-column>
                <el-table-column prop="port" label="端口" align="center" sortable></el-table-column>
                <el-table-column prop="statue" label="状态" align="center" sortable></el-table-column>
                &lt;!&ndash;<el-table-column label="操作" align="center" >
                    <template slot-scope="scope" >
                        <el-button  size="mini" @click="handleEdit(scope.$index, scope.row,false)" >启动监听</el-button>
                        <el-button  size="mini" @click="handleEdit(scope.$index, scope.row,false)" >停止监听</el-button>
                    </template>
                </el-table-column>&ndash;&gt;
            </el-table>-->
        </el-col>
        <el-col :span="10">
            <div v-if="hasRowSelect">
                接收消息:
                <el-input id="recv-input" type="textarea" :rows="10" v-model="recvMsg" :readonly="readonly"></el-input>
              发送消息：
                <el-input type="textarea" :rows="10" clearable v-model="sendMsg"></el-input>
                <el-checkbox v-model="checkedHex">HEX</el-checkbox>
            </div>
        </el-col>
    </el-row>

    <!--编辑弹框-->
    <el-col :span="24">
        <el-dialog :title="title" :visible.sync="editFormVisible" :close-on-click-modal="true" top>
            <!--el-form 的ref值必须为editForm -->
            <el-form :model="editFormModel" label-width="80px" :rules="editFormRules" ref="editFormModel">
                <el-col :span="12">
                    <el-form-item label="主键名称：" prop="id" label-width="100px">
                        <el-input clearable v-model="editFormModel.id" :readonly="readonly"></el-input>
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
import { getServerInfo,createServer,sendData,getRcvMsg,closeServer } from '@/api/tcp'

export default {
    name: "TcpServer",
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
            data: [{
                label: '一级 1',
                children: [{
                    label: '二级 1-1',
                    children: [{
                        label: '三级 1-1-1'
                    }]
                }]
            }, {
                label: '一级 2',
                children: [{
                    label: '二级 2-1',
                    children: [{
                        label: '三级 2-1-1'
                    }]
                }, {
                    label: '二级 2-2',
                    children: [{
                        label: '三级 2-2-1'
                    }]
                }]
            }, {
                label: '一级 3',
                children: [{
                    label: '二级 3-1',
                    children: [{
                        label: '三级 3-1-1'
                    }]
                }, {
                    label: '二级 3-2',
                    children: [{
                        label: '三级 3-2-1'
                    }]
                }]
            }],
            defaultProps: {
                children: 'children',
                label: 'label'
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
                id: "",
                port: ""
            },
            //form表单绑定的对象
            editFormModel: {
                id: "",
                port: "",
                statue: "",
                recvMsg: "",
                sendMsg: "",
            },
            tmpServerInfo: {
                id: "",
                port: "",
                statue: "",
                recvMsg: "",
                sendMsg: "",
            },
            //弹框form表单校验规则
            editFormRules: {
                id: [{ required: true, message: "该输入项为必填项!" }],
                port: [{ required: true, message: "该输入项为必填项!" }],
            },
            readonly: true,
            hasRowSelect: false,
            currentRow: null,
            checkedHex: false,
            //serverInfo: new Map(), //Map 保存连接信息 Key=id value=tmpServerInfo
            sendMsg: "",
            recvMsg: "",
            clearTimeSet: null,
        }
    },
    mounted() {
        this.getTableData();
        //设置定时任务-定时获取服务端接收的报文
        this.setTime();
    },
    beforeDestroy() {    //页面关闭时清除定时器
        clearInterval(this.clearTimeSet);
    },
    computed: {
        ...mapGetters(['serverInfo'])
    },
    methods: {
        handleNodeClick(data) {
            console.log(data);
        },
        // 获取table数据
        getTableData() {
          getServerInfo().then(response => {
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
            _this.editFormModel = _this.defaultValues; //弹框默认值
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

            _this.setModel2Object(model);
            //_this.$store.commit("setServerInfo", _this.tmpServerInfo)
          _this.$store.dispatch('app/setServerInfo', _this.tmpServerInfo)
            //_this.serverInfo.set(model.id, _this.tmpServerInfo);  //保存服务信息

            _this.$refs.editFormModel.validate(valid => {
                if (valid) {
                    let url = "";
                    let params = {};
                    if (_this.isAdd) {
                        createServer(model).then(response => {
                          _this.$message.success("创建服务端成功");
                            _this.editFormVisible = false;
                            this.tableData.data = response.data;
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
        setModel2Object(model) {
            let _this = this;
            _this.tmpServerInfo.id = model.id;
            _this.tmpServerInfo.port = model.port;
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
</style>
