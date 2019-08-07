<template>
    <div class="right-menu"
         :style="'left:' + rightMenuLeft + 'px; top:' + rightMenuTop + 'px;'"
         v-if="rightMenuVisible"
         v-clickoutside="closeRightMenu">

        <ul v-for="menuItem in menuItems">
            <li @click="onClick(menuItem)">{{menuItem.name}}</li>
        </ul>

    </div>
</template>

<script>

import { mapState } from 'vuex';
import Clickoutside from 'element-ui/src/utils/clickoutside'

export default {
    name: "RightMenu",
    watch: {
        rightMenuVisible (newVal, oldVal) {
            if (newVal) {

            }
        }
    },
    directives: {
        Clickoutside
    },
    computed: {
        ...mapState({
            rightMenuVisible: state => state.rightMenu.rightMenuVisible,
            rightMenuLeft: state => state.rightMenu.rightMenuLeft,
            rightMenuTop: state => state.rightMenu.rightMenuTop,
        }),
    },
    props: {
        menuItems: {
            type: Array,
            default: () => {
                return [];
            }
        },
    },
    data () {
        return {
            /*menuItems: [{
                name: '复制',
                func: this.copy(),
            },{
                name: '粘贴',
                func: this.paste(),
            },{
                name: '修改',
                func: this.modify(),
            },{
                name: '删除',
                func: this.delete(),
            }],*/
        };
    },

    methods: {
        /**
         *  点击右键菜单
         */
        onClick: function(menuItem){
            console.log(menuItem);
            this.$emit('onClick', menuItem);
            this.$store.commit('rightMenu/SET_VISIBLE', false);
        },

        /**
         * 关闭右键菜单
         */
        closeRightMenu () {
            this.$store.commit('rightMenu/SET_VISIBLE', false);
        }
    }
}
</script>

<style lang="less" scoped>
    .right-menu{
        position: fixed;
        z-index: 2800;
        background: #fff;
        border: 1px solid #aaa;
        border-radius: 4px;
        -webkit-tap-highlight-color: transparent;
        box-shadow: 2px 2px 8px 0 #555;

        ul{
            margin: 0;
            list-style-type: none;
            padding: 3px 14px;
            border-radius: 4px;
            font-size: 14px;
            color: #333;
            line-height: 1;
            li{
                margin: 4px 0;
                &:hover{
                    background: #e5e5e5;
                    cursor: pointer;
                }
            }
        }
    }
</style>
