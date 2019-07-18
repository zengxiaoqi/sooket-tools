<template>
    <el-row>
        <el-col :span="4">
            <el-tree :data="httpList" :props="defaultProps" default-expand-all @node-click="handleNodeClick"></el-tree>
        </el-col>
        <el-col :span="20">
            <el-col :span="2">
                <el-select v-model="httpMethod" filterable placeholder="请选择">
                    <el-option
                        v-for="item in options"
                        :key="item.value"
                        :label="item.label"
                        :value="item.value">
                    </el-option>
                </el-select>
            </el-col>
            <el-col :span="16">
                <el-input v-model="httpURL" placeholder="请输入URL"></el-input>
            </el-col>
            <el-col :span="2">
                <el-button type="primary">发送</el-button>
            </el-col>
        </el-col>
        <el-col :span="20">
            <el-tabs v-model="activeTabName" @tab-click="handleTabClick">
                <el-tab-pane label="Params" name="Params">
                    <params-table :param-list="ParamList" @listChange="listChange"></params-table>
                </el-tab-pane>
                <el-tab-pane label="Headers" name="Headers">
                    <params-table :paramList="headList" @listChange="listChange"></params-table>
                </el-tab-pane>
                <el-tab-pane label="Body" name="Body">
                    <params-table :paramList="bodyList" @listChange="listChange"></params-table>
                </el-tab-pane>
                <el-tab-pane label="Cookie" name="Cookie">
                    <params-table :paramList="cookieList" @listChange="listChange"></params-table>
                </el-tab-pane>
            </el-tabs>
        </el-col>
        <el-col :span="20" style="float: right">
            <el-divider style="el-divider: el-divider--horizontal" content-position="left">Response</el-divider>
            <el-button size="mini" style="el-button:el-button--default" @click="highLight=!highLight">高亮</el-button>
            <!--<el-button size="mini" style="el-button:el-button&#45;&#45;default" @click="highLight=!highLight">文本</el-button>-->
            <el-select v-show="!highLight" size="mini" v-model="txtType" placeholder="请选择">
                <el-option
                    v-for="item in contentTypeOpt"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value">
                </el-option>
            </el-select>
            <div v-if="highLight">{{outputValue}}</div>
            <div v-else>
                <pre v-highlightjs="outputValue">
                    <code :class="txtType">
                    </code>
                </pre>
            </div>
        </el-col>
    </el-row>
</template>

<script>
import listOpr from "../../utils/listOpration.js"
import ParamsTable from "./compments/ParamsTable"

export default {
    name: "http-client",
    components: {ParamsTable},
    comments: { ParamsTable },
    data() {
        return {
            httpList: [
                {
                    label: '一级 1',
                    children: [{
                        label: '二级 1-1',
                        children: null,
                        leaf: true,
                    }],
                    leaf: false,
                },
            ],
            defaultProps: {
                children: 'children',
                label: 'label',
                leaf: 'leaf',
            },
            options: [{
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
            contentTypeOpt: [{
                value: 'json',
                label: 'JSON'
            },{
                value: 'xml',
                label: 'XML'
            },{
                value: 'html',
                label: 'HTML'
            },

            ],
            httpMethod: null,
            httpURL: null,
            ParamList: [{
                key: "",
                value: "",
            }],
            activeTabName: "Params",
            multipleSelection: [],
            currentRow: null,
            headList: [],
            bodyList: [],
            cookieList: [],
            outputValue: "<key>value</key>",
            txtType: 'xml',
            highLight: false,
        };
    },

    methods: {
        handleNodeClick: function () {

        },
        handleTabClick: function () {

        },
        listChange: function(newList) {
            let _this = this;
            if(_this.activeTabName == "Params"){
                _this.ParamList = newList;
            }else if(_this.activeTabName == "Headers") {
                _this.headList = newList;
            }else if(_this.activeTabName == "Body") {
                _this.bodyList = newList;
            }else if(_this.activeTabName == "Cookie") {
                _this.cookieList = newList;
            }
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
</style>
<style scoped>

</style>
