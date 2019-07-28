<template>
    <el-row>
        <el-col :span="4">
            <el-tree :data="httpList" :props="defaultProps" default-expand-all @node-click="handleNodeClick"></el-tree>
        </el-col>
        <el-col :span="20">
            <el-col :span="20">
                <el-input v-model="httpURL" placeholder="请输入URL">
                    <el-select  v-model="httpMethod"  placeholder="请选择" slot="prepend">
                        <el-option
                            v-for="item in options"
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
            <el-tabs v-model="activeTabName" @tab-click="handleTabClick">
                <el-tab-pane label="Params" name="Params">
                    <!--<params-table :param-list.sync="ParamList" @listChange="listChange"></params-table>-->
                    <!--.sync父子组件双向数据绑定-->
                    <params-table :param-list.sync="ParamList"></params-table>
                </el-tab-pane>
                <el-tab-pane label="Headers" name="Headers">
                    <!--<params-table :param-list.sync="headList" @listChange="listChange"></params-table>-->
                    <params-table :param-list.sync="headList"></params-table>
                </el-tab-pane>
                <el-tab-pane label="Body" name="Body">
                    <el-col :span="20">
                        <!--<radio-group :radios="bodyRadios" @selectRadio="selectRadio"></radio-group>-->
                        <radio-group :radios="bodyRadios" :selectRadio.sync="bodyRadio"></radio-group>
                    </el-col>
                    <el-col :span="4">
                        <el-select v-if="bodyRadio=='row'" size="mini" v-model="contentType" placeholder="请选择">
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
                    <params-table v-if="bodyRadio == 'form-data'" :param-list.sync="bodyList" ></params-table>
                    <!--<el-input v-model="bodyText" v-else-if="bodyRadio == 'row'" type="textarea" rows="10"></el-input>-->
                    <vue-ace-editor :lang="contentType.type" @editorChange="getBodyText" v-else-if="bodyRadio == 'row'" ></vue-ace-editor>
                    <file-upload v-else="bodyRadio == 'binary'"
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
                    <params-table :paramList.sync="cookieList" ></params-table>
                </el-tab-pane>
            </el-tabs>
        </el-col>
        <el-col :span="20" style="float: right">
            <el-divider style="el-divider: el-divider--horizontal" content-position="left">Response</el-divider>
            <el-button size="mini" style="el-button:el-button--default" @click="highLight=!highLight">高亮</el-button>
            <!--<el-button size="mini" style="el-button:el-button&#45;&#45;default" @click="highLight=!highLight">文本</el-button>-->
            <el-select v-show="!highLight" size="mini" v-model="txtType" placeholder="请选择">
                <el-option
                    v-for="item in highLightOpt"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value">
                </el-option>
            </el-select>
            <div v-if="highLight">{{outputValue}}</div>
            <div v-else>
                <!--<pre v-highlightjs="outputValue">
                    <code :class="txtType">
                    </code>
                </pre>-->
                <el-button @click="setContent">高亮显示结果</el-button>
                <vue-ace-editor ref="aceEditor" :lang="txtType" @editorChange="contentChange" ></vue-ace-editor>
            </div>

        </el-col>
    </el-row>
</template>

<script>

import ParamsTable from "./compments/ParamsTable"
import RadioGroup from "./compments/RadioGroup"
import FileUpload from "./compments/FileUpload"
import {axiosGet, axiosPost} from "@/api/axiosApi"
import ObjectUtil from "@/utils/ObjectUtil.js"
import VueAceEditor from "@/components/VueAceEditor"

export default {
    name: "http-client",
    components: {ParamsTable, RadioGroup, FileUpload,VueAceEditor},
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
            },

            ],
            contentType: "TXT(text/plain)",
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
            httpMethod: "GET",
            httpURL: "",
            ParamList: [{
                key: "",
                value: "",
                desc: "",
            }],
            activeTabName: "Params",
            multipleSelection: [],
            currentRow: null,
            headList: [],
            bodyList: [],
            bodyText: null,
            httpBody: {
                headers: "",
                params: "",
                bodyContext: "",
                cookies: "",
                httpMethod: "",
                httpURL: "",
                contentType: "application/x-www-form-urlencoded",
            },
            cookieList: [],
            outputValue: "",
            txtType: 'abc',
            highLight: false,
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
            bodyRadio: "form-data",
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
            rspData: "",
        };
    },

    methods: {
        sendHttpRequest: function() {
            let _this = this;
            if(_this.bodyRadio == "binary"){
                _this.fileUploadParam.data.destUrl = _this.httpURL;
                _this.fileUploadParam.data.params = _this.ParamList;
                //_this.fileUploadParam.headers = _this.headList;
                _this.$refs.uploadFile.submitUpload();
            }else {
                if(_this.headList.length > 0){
                    _this.httpBody.headers = ObjectUtil.array2Map(_this.headList);
                }
                if(_this.ParamList.length > 0){
                    _this.httpBody.params = ObjectUtil.array2Map(_this.ParamList);
                }
                if(_this.bodyRadio == "form-data" && _this.bodyList.length>0){
                    _this.httpBody.bodyContext = ObjectUtil.array2Map(_this.bodyList);
                    _this.httpBody.contentType = "multipart/form-data";
                }else if(_this.bodyRadio == "row" && null != _this.bodyText){
                    _this.httpBody.bodyContext = _this.bodyText;
                    _this.httpBody.contentType = _this.contentType;
                }else if(_this.bodyRadio == "x-www-form-urlencoded" && null != _this.bodyText){
                    _this.httpBody.bodyContext = ObjectUtil.array2Map(_this.bodyList);
                    _this.httpBody.contentType = "x-www-form-urlencoded";
                }
                _this.httpBody.httpURL = _this.httpURL;
                _this.httpBody.httpMethod = _this.httpMethod;

                console.log(_this.httpBody);
                axiosPost("/httpRequest",
                    {'Content-Type': 'application/json;charset=UTF-8'},
                    null,
                    _this.httpBody
                ).then(res => {
                    console.log(res)
                });
            }

        },
        handleNodeClick: function () {

        },
        handleTabClick: function () {

        },
        /*listChange: function(newList) {
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
        },*/
        /*selectRadio: function (radio) {
            this.bodyRadio = radio;
        },*/
        contentChange: function (val) {
            this.rspData = val;
        },
        getBodyText: function(val) {
            this.bodyText = val;
        },
        setContent: function () {
            let val = "<java>test</java>"
            this.$refs.aceEditor.setContent(val);
        }
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
