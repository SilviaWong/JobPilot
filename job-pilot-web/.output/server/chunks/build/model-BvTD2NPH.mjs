import { _ as _plugin_vue_export_helper_default } from '../virtual/entry.mjs';
import { ref, computed, mergeProps, useSSRContext } from 'vue';
import { ssrRenderAttrs, ssrRenderStyle, ssrRenderList, ssrRenderClass, ssrInterpolate, ssrRenderAttr, ssrIncludeBooleanAttr } from 'vue/server-renderer';
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

//#region pages/ai/model.vue
var _sfc_main = {
	__name: "model",
	__ssrInlineRender: true,
	setup(__props) {
		const loading = ref(false);
		const testing = ref(false);
		const activeProfileId = ref("1");
		const profiles = ref([]);
		const activeProfile = computed(() => {
			return profiles.value.find((p) => p.id === activeProfileId.value);
		});
		return (_ctx, _push, _parent, _attrs) => {
			_push(`<div${ssrRenderAttrs(mergeProps({
				class: "mac-container",
				style: {
					"height": "calc(100vh - 100px)",
					"padding": "20px",
					"box-sizing": "border-box"
				}
			}, _attrs))} data-v-b9a5de79><div style="${ssrRenderStyle({
				"display": "flex",
				"height": "100%",
				"gap": "0",
				"background": "#fff",
				"border-radius": "12px",
				"box-shadow": "0 4px 24px rgba(0,0,0,0.04)",
				"overflow": "hidden"
			})}" data-v-b9a5de79><div style="${ssrRenderStyle({
				"width": "250px",
				"background": "#fcfcfc",
				"border-right": "1px solid #f0f0f0",
				"display": "flex",
				"flex-direction": "column"
			})}" data-v-b9a5de79><div style="${ssrRenderStyle({
				"padding": "16px",
				"border-bottom": "1px solid #f0f0f0",
				"display": "flex",
				"justify-content": "space-between",
				"align-items": "center"
			})}" data-v-b9a5de79><h3 style="${ssrRenderStyle({
				"margin": "0",
				"font-size": "16px",
				"color": "#333",
				"font-weight": "600"
			})}" data-v-b9a5de79>模型平台</h3><button style="${ssrRenderStyle({
				"background": "transparent",
				"border": "none",
				"font-size": "20px",
				"color": "#007bff",
				"cursor": "pointer",
				"padding": "0 4px",
				"display": "flex",
				"align-items": "center",
				"justify-content": "center"
			})}" title="新增平台配置" data-v-b9a5de79><svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" data-v-b9a5de79><line x1="12" y1="5" x2="12" y2="19" data-v-b9a5de79></line><line x1="5" y1="12" x2="19" y2="12" data-v-b9a5de79></line></svg></button></div><div style="${ssrRenderStyle({
				"flex": "1",
				"overflow-y": "auto",
				"padding": "8px"
			})}" data-v-b9a5de79><!--[-->`);
			ssrRenderList(profiles.value, (profile) => {
				_push(`<div class="${ssrRenderClass([{ "is-active": activeProfileId.value === profile.id }, "profile-item"])}" data-v-b9a5de79>${ssrInterpolate(profile.name || "未命名配置")}</div>`);
			});
			_push(`<!--]--></div></div><div style="${ssrRenderStyle({
				"flex": "1",
				"padding": "32px",
				"overflow-y": "auto",
				"display": "flex",
				"flex-direction": "column"
			})}" data-v-b9a5de79>`);
			if (activeProfile.value) {
				_push(`<!--[--><div style="${ssrRenderStyle({
					"display": "flex",
					"align-items": "center",
					"justify-content": "space-between",
					"margin-bottom": "24px"
				})}" data-v-b9a5de79><div data-v-b9a5de79><h2 style="${ssrRenderStyle({
					"margin": "0 0 8px 0",
					"font-size": "24px",
					"color": "#333"
				})}" data-v-b9a5de79>配置详情</h2><p style="${ssrRenderStyle({
					"margin": "0",
					"color": "#666",
					"font-size": "14px"
				})}" data-v-b9a5de79>配置兼容 OpenAI 格式的 API 接口，支持多平台切换使用。</p></div>`);
				if (profiles.value.length > 1) _push(`<button style="${ssrRenderStyle({
					"background": "transparent",
					"border": "1px solid #dc3545",
					"color": "#dc3545",
					"border-radius": "6px",
					"padding": "6px 16px",
					"cursor": "pointer",
					"font-size": "13px"
				})}" data-v-b9a5de79>删除此配置</button>`);
				else _push(`<!---->`);
				_push(`</div><div class="config-form" style="${ssrRenderStyle({ "max-width": "600px" })}" data-v-b9a5de79><div class="input-group" data-v-b9a5de79><label class="input-label" data-v-b9a5de79>配置名称</label><input type="text" class="api-input"${ssrRenderAttr("value", activeProfile.value.name)} placeholder="例如: DeepSeek, 阿里通义千问, 本地模型" data-v-b9a5de79></div><div class="input-group" data-v-b9a5de79><label class="input-label" data-v-b9a5de79>API Base URL</label><input type="text" class="api-input"${ssrRenderAttr("value", activeProfile.value.url)} placeholder="https://api.deepseek.com/v1" data-v-b9a5de79></div><div class="input-group" data-v-b9a5de79><label class="input-label" data-v-b9a5de79>模型名称</label><input type="text" class="api-input"${ssrRenderAttr("value", activeProfile.value.model)} placeholder="deepseek-chat" data-v-b9a5de79></div><div class="input-group" data-v-b9a5de79><label class="input-label" data-v-b9a5de79>API Key</label><input type="password" class="api-input"${ssrRenderAttr("value", activeProfile.value.key)} placeholder="sk-..." data-v-b9a5de79></div></div><div style="${ssrRenderStyle({
					"margin-top": "32px",
					"display": "flex",
					"gap": "12px",
					"max-width": "600px"
				})}" data-v-b9a5de79><button${ssrIncludeBooleanAttr(testing.value) ? " disabled" : ""} class="btn-test" style="${ssrRenderStyle({
					"flex": "1",
					"padding": "12px",
					"border": "1px solid #ddd",
					"border-radius": "8px",
					"font-weight": "500",
					"cursor": "pointer",
					"background": "#fff",
					"color": "#333",
					"transition": "all 0.2s"
				})}" data-v-b9a5de79>${ssrInterpolate(testing.value ? "测试中..." : "测试连接")}</button><button${ssrIncludeBooleanAttr(loading.value) ? " disabled" : ""} class="btn-primary" data-v-b9a5de79>${ssrInterpolate(loading.value ? "保存中..." : "保存配置")}</button></div><!--]-->`);
			} else _push(`<!---->`);
			_push(`</div></div></div>`);
		};
	}
};
var _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
	const ssrContext = useSSRContext();
	(ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/ai/model.vue");
	return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
var model_default = /*#__PURE__*/ _plugin_vue_export_helper_default(_sfc_main, [["__scopeId", "data-v-b9a5de79"]]);

export { model_default as default };
//# sourceMappingURL=model-BvTD2NPH.mjs.map
