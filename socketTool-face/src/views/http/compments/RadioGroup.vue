<template>
    <div>
        <el-radio-group v-model="radio" @change="selectedRadio(radio)" v-for="(radioParam,index) in radios" :key="index" :size="size">
            <el-radio v-if="radioType == 'radio'" :label="radioParam.value">{{radioParam.name}}</el-radio>
            <el-radio-button v-else-if="radioType == 'button'" :label="radioParam.value">{{radioParam.name}}</el-radio-button>
        </el-radio-group>
    </div>
</template>

<script>
export default {
    name: "RadioGroup",
    props: {
        radios: {
            type: Array,
            default: () => {
                return [];
            }
        },
        selectRadio: {
            type: String,
            default: () => {
                return "";
            }
        },
        radioType: {
            type: String,
            default: () => {
                return "radio";
            }
        },
        size:{
            type: String,
            default: () => {
                return "mini";
            }
        }
    },
    data() {
        return {
            radio: null,
        };
    },

    methods: {
        selectedRadio: function (value) {
            //this.$emit('selectRadio', value);
            this.$emit('update:selectRadio', value);
        },
    },
}
</script>

<style scoped>

</style>
