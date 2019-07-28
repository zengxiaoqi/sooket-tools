<template>
    <div>
        <el-radio-group v-model="radio" @change="selectedRadio(radio)" v-for="radioParam in radios">
            <el-radio :label="radioParam.value">{{radioParam.name}}</el-radio>
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
