<template>
    <el-row>
        <el-col :span="4">
            <el-tree :data="httpList" :props="defaultProps" default-expand-all @node-click="handleNodeClick"></el-tree>
        </el-col>
    </el-row>
</template>

<script>
export default {
    name: "http-client",
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
            }
        };
    },
}
</script>

<style scoped>

</style>
