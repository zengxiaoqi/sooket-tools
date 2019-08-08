<template>
    <div class="block">
        <el-tree
            :data="data"
            :props="defaultProps"
            :show-checkbox="checkbox"
            :node-key="nodeKey"
            :default-expand-all="expand"
            :expand-on-click-node="false"
            highlight-current
            check-on-click-node
            @node-click="nodeClick"
            :render-content="renderContent">
        </el-tree>
    </div>
</template>

<script>
export default {
    name: "RenderTree",
    props: {
        data: {
            type: Array,
            default: () => {
                return [];
            }
        },
        defaultProps: {
            children: 'child',
            label: 'name'
        },
        checkbox: false,
        nodeKey: "1",
        expand: false,
    },
    methods: {
        append(data) {
            const newChild = { id: id++, name: 'test', child: [] };
            if (!data.child) {
                this.$set(data, 'child', []);
            }
            data.child.push(newChild);
        },

        remove(node, data) {
            const parent = node.parent;
            const children = parent.data.child || parent.data;
            const index = children.findIndex(d => d.id === data.id);
            children.splice(index, 1);
            //删除数据库信息
            this.$emit("nodeClick", data, "delete");
        },

        renderContent(h, { node, data, store }) {
            return ( <span class="custom-tree-node">
                <span>{data.name}</span>
            <span style="right">
            <el-button size="mini" type="text" icon="el-icon-delete" on-click={ () => this.remove(node, data) }></el-button>
            </span>
            </span>);
            /*return ( <span>
                        <span>{node.label}</span>
                        <span>
                            <el-button size="mini" type="text" icon="el-icon-delete" on-click={ () => this.remove(node, data) }></el-button>
                        </span>
                    </span>);*/

        },

        nodeClick(data,node,val){
            let _this =this;
            console.log(data);
            _this.$emit("nodeClick", data, "click");
        },
    }
}
</script>

<style>
    .custom-tree-node {
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: space-between;
        font-size: 14px;
        padding-right: 10px;
    }
</style>
