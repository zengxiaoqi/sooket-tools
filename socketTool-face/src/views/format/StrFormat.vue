<template>
    <div>
        <el-form :inline="true">
            <el-form-item>
                <el-select v-model="type" placeholder="选择格式化类型">
                    <el-option label="XML" value="xml"></el-option>
                    <el-option label="JSON" value="json"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="formatStr">格式化</el-button>
            </el-form-item>
        </el-form>
        <el-row>
            <el-col :span="10">
                输入字符串：
                <el-input type="textarea" :rows="20" v-model="inputStr" ></el-input>
            </el-col>
            <el-col :span="10">
                输出字符串：
                <!--<el-input type="textarea" :rows="20" v-model="outputStr" ></el-input>-->
                <div>
                    <!-- 使用高亮指令 -->
                    <pre style="pre:hljs" v-highlightjs="outputStr">
                        <code :class="type"><!-- 声明什么类型的代码 -->
                        </code>
                    </pre>
                </div>
            </el-col>
        </el-row>
    </div>
</template>

<script>
  import { formatStr } from '@/api/format'
  export default {
    name: "StrFormat",
    data() {
        return {
            type: "",
            inputStr: "",
            outputStr: "",
        }
    },
    mounted(){

    },
    methods: {
        formatStr(){
            if(this.type == ""){
                this.$message.info("请选择格式化类型")
                return;
            }
            if(this.inputStr == ""){
                this.$message.info("请输入要解析的字符串")
                return;
            }
          let data = {
              "inputStr": this.inputStr,
              "type": this.type
          }
          formatStr(data).then(res => {
              this.outputStr = res.data.outputStr;
              this.$message.success("解析成功")
            });
        }
    }
}
</script>
<style>
    .hljs {
        display: block;
        overflow-x: auto;
        padding: 0.0em;
        background: #23241f;
    }

    pre {
        display: block;
        font-family: monospace;
        white-space: pre;
        margin: 0px 0px;
    }
    pre {
        hljs: his;
    }

</style>
<style scoped>

</style>
