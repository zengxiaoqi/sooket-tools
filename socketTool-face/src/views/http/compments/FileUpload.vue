<template>
    <div>
        <el-col :span="24">
            <el-upload
                class="upload-demo"
                ref="upload"
                :action="this.uploadUrl"
                :headers="headers"
                :data="data"
                :on-preview="handlePreview"
                :on-remove="handleRemove"
                :before-remove="beforeRemove"
                :on-success="uploadSuccess"
                :on-error="uploadError"
                multiple="multiple"
                :limit="limit"
                :on-exceed="handleExceed"
                :file-list="fileList"
                :auto-upload="false"
            >
                <el-button size="small" type="primary">选取文件</el-button>
                <slot></slot>
                <div slot="tip" class="el-upload__tip">{{tips}}</div>
            </el-upload>
        </el-col>
    </div>
</template>

<script>
export default {
    name: "fileUpload",
    props: {
        uploadUrl:"",
        headers: {
            type: [String, Object],
            default() {
                return null;
            },
            required: false,
        },
        data: {
            type: [String, Object],
            default() {
                return null;
            },
            required: false,
        },
        limit: {
            type: Number,
            default: () => {
                return 3;
            }
        },
        multiple: {
            type: Boolean,
            default() {
                return false;
            },
        },
        tips: {type: String},
    },
    data(){
        return {
            //uploadUrl: "http://localhost:9001/http/httpRequest",
            //headers: null,  //设置上传的请求头部
            //data: null,     //上传时附带的额外参数
            //limit: 3,
            fileList: [],
        };
    },
    methods: {
        handleRemove(file, fileList) {
            console.log(file, fileList);
        },
        handlePreview(file) {
            console.log(file);
        },
        beforeRemove(file, fileList) {
            return this.$confirm(`确定移除 ${ file.name }？`);
        },
        handleExceed(files, fileList) {
            this.$message.warning(`当前限制选择 ${this.limit} 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`);
        },
        submitUpload() {
            this.$refs.upload.submit();
        },
        uploadSuccess: function (response, file, fileList) {
            this.$message.success("文件上传成功");
        },
        uploadError: function(err, file, fileList){
            this.$message.error("文件上传失败");
        }
    }
}
</script>

<style scoped>

</style>
