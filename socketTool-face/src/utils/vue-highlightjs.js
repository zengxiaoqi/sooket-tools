'use strict';
// highlight.js  代码高亮指令
/**使用说明
 * <!-- If your source-code lives in a variable called 'sourcecode' -->
 *  <！ -如果您的源代码位于名为“sourcecode”的变量中- >
 <pre v-highlightjs="sourcecode"><code class="javascript"></code></pre>
 <!-- If you want to highlight hardcoded source-code -->
 <pre v-highlightjs><code class="javascript">const s = new Date().toString()</code></pre>
 * 支持的高亮格式参考 https://highlightjs.readthedocs.io/en/latest/css-classes-reference.html?highlight=xml
 */
import Vue from 'vue';

var hljs = require('highlight.js');
// import hljs from 'highlight.js';

import 'highlight.js/styles/monokai-sublime.css';
//import 'highlight.js/styles/googlecode.css'

let vueHighlightJS  = {};
vueHighlightJS .install = function (Vue, options) {
    Vue.directive('highlightjs', {
        deep: true,
        bind: function bind(el, binding) {
            // on first bind, highlight all targets
            var targets = el.querySelectorAll('code');
            var target;
            var i;

            for (i = 0; i < targets.length; i += 1) {
                target = targets[i];

                if (typeof binding.value === 'string') {
                    // if a value is directly assigned to the directive, use this
                    // instead of the element content.
                    target.textContent = binding.value;
                }

                hljs.highlightBlock(target);
            }
        },
        componentUpdated: function componentUpdated(el, binding) {
            // after an update, re-fill the content and then highlight
            var targets = el.querySelectorAll('code');
            var target;
            var i;

            for (i = 0; i < targets.length; i += 1) {
                target = targets[i];
                if (typeof binding.value === 'string') {
                    target.textContent = binding.value;
                }
                hljs.highlightBlock(target);
            }
        }
    });
};

export default vueHighlightJS;
