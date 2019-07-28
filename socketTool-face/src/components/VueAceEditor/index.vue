<template>
    <div>
        <editor v-model="content" @init="editorInit" :lang="lang" :theme="theme" :width="width" :height="height" @onChange="editorChange"></editor>
    </div>
</template>

<script>
//var Vue = require('vue/dist/vue.common.js');

export default {
    name: "VueAceEditor",
    props: {
        contentVal: {
            type: String,
            default: ''
        },
        lang: {
            type: String,
            default: 'html'
        },
        theme: {
            type: String,
            default: 'chrome'
        },
        width: {
            type: Number,
            default: 1000
        },
        height: {
            type: Number,
            default: 100
        }
    },
    components: {
        editor: require('vue2-ace-editor'),
    },
    data(){
        return {
            content:"",
        }
    },
    watch: {
        content: function (val) {
            this.$emit("contentChange", val);
            this.$emit("update:contentVal", val);
        },
    },
    methods: {
        editorInit: function () {
            require('brace/ext/language_tools') //language extension prerequsite...
            require('brace/mode/html')
            require('brace/mode/javascript')    //language
            require('brace/mode/java')
            require('brace/mode/json')
            require('brace/mode/xml')
            require('brace/mode/css')
            require('brace/mode/abc')
            require('brace/mode/less')
            require('brace/theme/chrome')
            require('brace/snippets/javascript') //snippet
        },
        editorChange: function (val) {
            this.$emit("contentChange", val);
            this.$emit("update:contentVal", val);
        },
        setContent: function (val) {
            this.content = val;
        }
    },
}
</script>

<style scoped>

</style>
