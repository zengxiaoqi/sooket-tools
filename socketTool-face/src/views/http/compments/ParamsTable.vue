<template>
    <div>
        <el-button type="success" size="mini" icon="el-icon-plus" circle @click="addOneRow"></el-button>
        <el-button type="danger" size="mini" icon="el-icon-minus" circle @click="delSelectRow"></el-button>
        <slot></slot>
        <el-table :data="paramList" border
                  @selection-change="handleSelectionChange">
            <el-table-column
                type="selection"
                width="30">
            </el-table-column>
            <el-table-column
                type="index"
                width="40">
            </el-table-column>
            <el-table-column property="key" label="KEY">
                <template slot-scope="scope">
                    <el-input v-model="scope.row.key" placeholder="key"></el-input>
                </template>
            </el-table-column>
            <el-table-column property="value" label="VALUE">
                <template slot-scope="scope">
                    <el-input v-model="scope.row.value" placeholder="value"></el-input>
                </template>
            </el-table-column>
            <slot></slot>
        </el-table>
    </div>
</template>

<script>

import listOpr from "@/utils/listOpration.js"

export default {
    name: "ParamsTable",
    //父组件通过props属性传递进来的数据
    props: {
        paramList: {
            type: Array,
            default: () => {
                return [];
            }
        }
    },
    data() {
        return {

        }
    },
    methods: {
        addOneRow: function() {
            let _this = this;
            let param = {key: "", value: ""};
            _this.paramList.push(param);
            _this.listChange();
        },

        delSelectRow: function(){
            let _this = this;
            listOpr.delFromList(_this.paramList, _this.multipleSelection);
            _this.listChange();
        },

        handleSelectionChange: function(val){
            let _this = this;
            _this.multipleSelection = val;
        },

        listChange: function () {
            this.$emit('listChange', this.paramList);
        }
    }
}
</script>

<style scoped>

</style>
