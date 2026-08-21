import { _ as _plugin_vue_export_helper_default } from '../virtual/entry.mjs';
import { ref, mergeProps, useSSRContext } from 'vue';
import { ssrRenderAttrs, ssrRenderStyle, ssrInterpolate, ssrIncludeBooleanAttr } from 'vue/server-renderer';
import 'nostics';
import 'nostics/formatters/ansi';
import '../nitro/nitro.mjs';
import 'node:http';
import 'node:https';
import 'node:events';
import 'node:buffer';
import 'node:fs';
import 'node:path';
import 'node:crypto';
import 'node:url';
import '../routes/renderer.mjs';
import 'unhead/server';
import 'unhead/legacy';
import 'unhead/plugins';
import 'vue-bundle-renderer/runtime';
import 'devalue';
import 'vue-router';
import '@vue/shared';
import '@vueuse/core';
import 'lodash-unified';
import 'unhead/utils';

//#region pages/ai/resume.vue
var _sfc_main = {
	__name: "resume",
	__ssrInlineRender: true,
	setup(__props) {
		const loading = ref(false);
		const resumeText = ref("");
		return (_ctx, _push, _parent, _attrs) => {
			_push(`<div${ssrRenderAttrs(mergeProps({
				class: "mac-container",
				style: {
					"height": "calc(100vh - 100px)",
					"padding": "20px",
					"box-sizing": "border-box"
				}
			}, _attrs))} data-v-d861371a><div style="${ssrRenderStyle({
				"background": "#fff",
				"border-radius": "12px",
				"box-shadow": "0 4px 24px rgba(0,0,0,0.04)",
				"padding": "32px",
				"display": "flex",
				"flex-direction": "column",
				"height": "100%",
				"box-sizing": "border-box"
			})}" data-v-d861371a><div style="${ssrRenderStyle({
				"display": "flex",
				"align-items": "center",
				"gap": "16px",
				"margin-bottom": "24px"
			})}" data-v-d861371a><div style="${ssrRenderStyle({
				"width": "48px",
				"height": "48px",
				"border-radius": "12px",
				"background": "rgba(52, 199, 89, 0.1)",
				"color": "#34C759",
				"display": "flex",
				"align-items": "center",
				"justify-content": "center"
			})}" data-v-d861371a><svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" data-v-d861371a><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" data-v-d861371a></path><polyline points="14 2 14 8 20 8" data-v-d861371a></polyline><line x1="16" y1="13" x2="8" y2="13" data-v-d861371a></line><line x1="16" y1="17" x2="8" y2="17" data-v-d861371a></line></svg></div><div data-v-d861371a><h2 style="${ssrRenderStyle({
				"margin": "0 0 4px 0",
				"font-size": "24px",
				"color": "#333"
			})}" data-v-d861371a>简历管理</h2><p style="${ssrRenderStyle({
				"margin": "0",
				"color": "#666",
				"font-size": "14px"
			})}" data-v-d861371a>你的简历内容将用于 AI 匹配度打分和生成个性化打招呼语。请确保信息准确完整。</p></div></div><div style="${ssrRenderStyle({
				"flex": "1",
				"min-height": "0",
				"margin-bottom": "16px"
			})}" data-v-d861371a><textarea class="resume-editor" placeholder="在此处粘贴或编辑你的简历内容...

建议包含：教育经历、工作经历、项目经验、技能特长等。" data-v-d861371a>${ssrInterpolate(resumeText.value)}</textarea></div><p style="${ssrRenderStyle({
				"margin": "0 0 24px 0",
				"font-size": "13px",
				"color": "#86868B"
			})}" data-v-d861371a> 使用建议：请确保简历信息真实、详细，包含与目标岗位相关的技能和经验，这将有助于 AI 更准确地进行评分。 </p><div style="${ssrRenderStyle({
				"display": "flex",
				"justify-content": "flex-end"
			})}" data-v-d861371a><button class="btn-save btn-primary"${ssrIncludeBooleanAttr(loading.value) ? " disabled" : ""} data-v-d861371a>${ssrInterpolate(loading.value ? "保存中..." : "保存简历")}</button></div></div></div>`);
		};
	}
};
var _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
	const ssrContext = useSSRContext();
	(ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/ai/resume.vue");
	return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
var resume_default = /*#__PURE__*/ _plugin_vue_export_helper_default(_sfc_main, [["__scopeId", "data-v-d861371a"]]);

export { resume_default as default };
//# sourceMappingURL=resume-Byc7P9ce.mjs.map
