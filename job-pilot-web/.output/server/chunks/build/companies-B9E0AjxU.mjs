import { _ as _plugin_vue_export_helper_default, $ as $fetch$1, u as useNamespace, b as useIdInjection, a as isNumber, i as isBoolean, t as throwError, d as debugWarn } from '../virtual/entry.mjs';
import { E as ElIcon, w as withInstall, c as withNoopInstall, b as buildProps, d as definePropType, e as arrow_right_default, i as iconPropType } from './icon-BTfkZ5_-.mjs';
import './arrays-DLJ1qaKZ.mjs';
import { m as mutable } from './typescript-DnijNOm4.mjs';
import { E as ElCollapseTransition } from './collapse-transition-zZusOYp2.mjs';
import { E as ElInput, C as CHANGE_EVENT, U as UPDATE_MODEL_EVENT } from './css-BTXcRLa_.mjs';
import { a as ElCard, E as ElButton, b as ElMessage } from './css-n73NFgGH.mjs';
import { E as ElTag } from './css-B_mwQh58.mjs';
import { E as ElEmpty, J as JobDetailDrawer_default } from './JobDetailDrawer-BOXyODc5.mjs';
import { v as vLoading } from './css-BxzHr8sS.mjs';
import { ref, watch, mergeProps, withCtx, createTextVNode, toDisplayString, openBlock, createBlock, createCommentVNode, createVNode, Fragment, renderList, unref, withDirectives, useSSRContext, defineComponent, createElementBlock, normalizeClass, createElementVNode, withKeys, withModifiers, renderSlot, resolveDynamicComponent, vShow, inject, computed, provide } from 'vue';
import { ssrRenderAttrs, ssrRenderComponent, ssrGetDirectiveProps, ssrRenderStyle, ssrInterpolate, ssrRenderList, ssrRenderClass, ssrRenderAttr } from 'vue/server-renderer';
import { Loader2 } from 'lucide-vue-next';
import { isString, isArray, isPromise } from '@vue/shared';
import { castArray } from 'lodash-unified';
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
import '@vueuse/core';
import 'unhead/utils';
import '@ctrl/tinycolor';

//#region node_modules/element-plus/es/components/collapse/src/collapse.mjs
var emitChangeFn = (value) => isNumber(value) || isString(value) || isArray(value);
/**
* @deprecated Removed after 3.0.0, Use `CollapseProps` instead.
*/
var collapseProps = buildProps({
	/**
	* @description whether to activate accordion mode
	*/
	accordion: Boolean,
	/**
	* @description currently active panel, the type is `string` in accordion mode, otherwise it is `array`
	*/
	modelValue: {
		type: definePropType([
			Array,
			String,
			Number
		]),
		default: () => mutable([])
	},
	/**
	* @description set expand icon position
	*/
	expandIconPosition: {
		type: definePropType([String]),
		default: "right"
	},
	/**
	* @description before-collapse hook before the collapse state changes. If `false` is returned or a `Promise` is returned and then is rejected, will stop collapsing
	*/
	beforeCollapse: { type: definePropType(Function) }
});
var collapseEmits = {
	[UPDATE_MODEL_EVENT]: emitChangeFn,
	[CHANGE_EVENT]: emitChangeFn
};
//#endregion
//#region node_modules/element-plus/es/components/collapse/src/constants.mjs
var collapseContextKey = Symbol("collapseContextKey");
//#endregion
//#region node_modules/element-plus/es/components/collapse/src/collapse-item.mjs
/**
* @deprecated Removed after 3.0.0, Use `CollapseItemProps` instead.
*/
var collapseItemProps = buildProps({
	/**
	* @description title of the panel
	*/
	title: {
		type: String,
		default: ""
	},
	/**
	* @description unique identification of the panel
	*/
	name: {
		type: definePropType([String, Number]),
		default: void 0
	},
	/**
	* @description icon of the collapse item
	*/
	icon: {
		type: iconPropType,
		default: arrow_right_default
	},
	/**
	* @description disable the collapse item
	*/
	disabled: Boolean
});
//#endregion
//#region node_modules/element-plus/es/components/collapse/src/use-collapse.mjs
var SCOPE = "ElCollapse";
var useCollapse = (props, emit) => {
	const activeNames = ref(castArray(props.modelValue));
	const setActiveNames = (_activeNames) => {
		activeNames.value = _activeNames;
		const value = props.accordion ? activeNames.value[0] : activeNames.value;
		emit(UPDATE_MODEL_EVENT, value);
		emit(CHANGE_EVENT, value);
	};
	const handleChange = (name) => {
		if (props.accordion) setActiveNames([activeNames.value[0] === name ? "" : name]);
		else {
			const _activeNames = [...activeNames.value];
			const index = _activeNames.indexOf(name);
			if (index > -1) _activeNames.splice(index, 1);
			else _activeNames.push(name);
			setActiveNames(_activeNames);
		}
	};
	const handleItemClick = async (name) => {
		const { beforeCollapse } = props;
		if (!beforeCollapse) {
			handleChange(name);
			return;
		}
		const shouldChange = beforeCollapse(name);
		if (![isPromise(shouldChange), isBoolean(shouldChange)].includes(true)) throwError(SCOPE, "beforeCollapse must return type `Promise<boolean>` or `boolean`");
		if (isPromise(shouldChange)) shouldChange.then((result) => {
			if (result !== false) handleChange(name);
		}).catch((e) => {
			debugWarn(SCOPE, `some error occurred: ${e}`);
		});
		else if (shouldChange) handleChange(name);
	};
	watch(() => props.modelValue, () => activeNames.value = castArray(props.modelValue), { deep: true });
	provide(collapseContextKey, {
		activeNames,
		handleItemClick
	});
	return {
		activeNames,
		setActiveNames
	};
};
var useCollapseDOM = (props) => {
	const ns = useNamespace("collapse");
	return { rootKls: computed(() => [ns.b(), ns.b(`icon-position-${props.expandIconPosition}`)]) };
};
//#endregion
//#region node_modules/element-plus/es/components/collapse/src/collapse2.mjs
var collapse_default = /* @__PURE__ */ defineComponent({
	name: "ElCollapse",
	__name: "collapse",
	props: collapseProps,
	emits: collapseEmits,
	setup(__props, { expose: __expose, emit: __emit }) {
		const props = __props;
		const { activeNames, setActiveNames } = useCollapse(props, __emit);
		const { rootKls } = useCollapseDOM(props);
		__expose({
			/** @description active names */
			activeNames,
			/** @description set active names */
			setActiveNames
		});
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("div", { class: normalizeClass(unref(rootKls)) }, [renderSlot(_ctx.$slots, "default")], 2);
		};
	}
});
//#endregion
//#region node_modules/element-plus/es/components/collapse/src/use-collapse-item.mjs
var useCollapseItem = (props) => {
	const collapse = inject(collapseContextKey);
	const { namespace } = useNamespace("collapse");
	const focusing = ref(false);
	const isClick = ref(false);
	const idInjection = useIdInjection();
	const id = computed(() => idInjection.current++);
	const name = computed(() => {
		return props.name ?? `${namespace.value}-id-${idInjection.prefix}-${unref(id)}`;
	});
	const isActive = computed(() => collapse?.activeNames.value.includes(unref(name)));
	const handleFocus = () => {
		setTimeout(() => {
			if (!isClick.value) focusing.value = true;
			else isClick.value = false;
		}, 50);
	};
	const handleHeaderClick = (e) => {
		if (props.disabled) return;
		if (e.target?.closest("input, textarea, select")) return;
		collapse?.handleItemClick(unref(name));
		focusing.value = false;
		isClick.value = true;
	};
	const handleEnterClick = (e) => {
		if (e.target?.closest("input, textarea, select")) return;
		e.preventDefault();
		collapse?.handleItemClick(unref(name));
	};
	return {
		focusing,
		id,
		isActive,
		handleFocus,
		handleHeaderClick,
		handleEnterClick
	};
};
var useCollapseItemDOM = (props, { focusing, isActive, id }) => {
	const ns = useNamespace("collapse");
	const rootKls = computed(() => [
		ns.b("item"),
		ns.is("active", unref(isActive)),
		ns.is("disabled", props.disabled)
	]);
	const headKls = computed(() => [
		ns.be("item", "header"),
		ns.is("active", unref(isActive)),
		{ focusing: unref(focusing) && !props.disabled }
	]);
	const arrowKls = computed(() => [ns.be("item", "arrow"), ns.is("active", unref(isActive))]);
	return {
		itemTitleKls: computed(() => [ns.be("item", "title")]),
		arrowKls,
		headKls,
		rootKls,
		itemWrapperKls: computed(() => ns.be("item", "wrap")),
		itemContentKls: computed(() => ns.be("item", "content")),
		scopedContentId: computed(() => ns.b(`content-${unref(id)}`)),
		scopedHeadId: computed(() => ns.b(`head-${unref(id)}`))
	};
};
//#endregion
//#region node_modules/element-plus/es/components/collapse/src/collapse-item.vue_vue_type_script_setup_true_lang.mjs
var _hoisted_1 = [
	"id",
	"aria-expanded",
	"aria-controls",
	"aria-describedby",
	"tabindex",
	"aria-disabled"
];
var _hoisted_2 = [
	"id",
	"aria-hidden",
	"aria-labelledby"
];
//#endregion
//#region node_modules/element-plus/es/components/collapse/src/collapse-item2.mjs
var collapse_item_default = /* @__PURE__ */ defineComponent({
	name: "ElCollapseItem",
	__name: "collapse-item",
	props: collapseItemProps,
	setup(__props, { expose: __expose }) {
		const props = __props;
		const { focusing, id, isActive, handleFocus, handleHeaderClick, handleEnterClick } = useCollapseItem(props);
		const { arrowKls, headKls, rootKls, itemTitleKls, itemWrapperKls, itemContentKls, scopedContentId, scopedHeadId } = useCollapseItemDOM(props, {
			focusing,
			isActive,
			id
		});
		__expose({ 
		/** @description current collapse-item whether active */
isActive });
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("div", { class: normalizeClass(unref(rootKls)) }, [createElementVNode("div", {
				id: unref(scopedHeadId),
				class: normalizeClass(unref(headKls)),
				"aria-expanded": unref(isActive),
				"aria-controls": unref(scopedContentId),
				"aria-describedby": unref(scopedContentId),
				tabindex: __props.disabled ? void 0 : 0,
				"aria-disabled": __props.disabled,
				role: "button",
				onClick: _cache[0] || (_cache[0] = (...args) => unref(handleHeaderClick) && unref(handleHeaderClick)(...args)),
				onKeydown: _cache[1] || (_cache[1] = withKeys(withModifiers((...args) => unref(handleEnterClick) && unref(handleEnterClick)(...args), ["stop"]), ["space", "enter"])),
				onFocus: _cache[2] || (_cache[2] = (...args) => unref(handleFocus) && unref(handleFocus)(...args)),
				onBlur: _cache[3] || (_cache[3] = ($event) => focusing.value = false)
			}, [createElementVNode("span", { class: normalizeClass(unref(itemTitleKls)) }, [renderSlot(_ctx.$slots, "title", { isActive: unref(isActive) }, () => [createTextVNode(toDisplayString(__props.title), 1)])], 2), renderSlot(_ctx.$slots, "icon", { isActive: unref(isActive) }, () => [createVNode(unref(ElIcon), { class: normalizeClass(unref(arrowKls)) }, {
				default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(__props.icon)))]),
				_: 1
			}, 8, ["class"])])], 42, _hoisted_1), createVNode(unref(ElCollapseTransition), null, {
				default: withCtx(() => [withDirectives(createElementVNode("div", {
					id: unref(scopedContentId),
					role: "region",
					class: normalizeClass(unref(itemWrapperKls)),
					"aria-hidden": !unref(isActive),
					"aria-labelledby": unref(scopedHeadId)
				}, [createElementVNode("div", { class: normalizeClass(unref(itemContentKls)) }, [renderSlot(_ctx.$slots, "default")], 2)], 10, _hoisted_2), [[vShow, unref(isActive)]])]),
				_: 3
			})], 2);
		};
	}
});
//#endregion
//#region node_modules/element-plus/es/components/collapse/index.mjs
var ElCollapse = withInstall(collapse_default, { CollapseItem: collapse_item_default });
var ElCollapseItem = withNoopInstall(collapse_item_default);
//#endregion
//#region pages/companies/index.vue
var _sfc_main = {
	__name: "index",
	__ssrInlineRender: true,
	setup(__props) {
		const loading = ref(false);
		const companies = ref([]);
		const totalCompanies = ref(0);
		const searchQuery = ref("");
		const page = ref(1);
		const pageSize = ref(20);
		const hasMore = ref(true);
		const jobDetailDrawerRef = ref(null);
		const loadMoreTrigger = ref(null);
		let observer = null;
		const getPlatformType = (plat) => {
			if (!plat) return "info";
			if (plat.includes("Boss")) return "success";
			if (plat.includes("51job")) return "warning";
			if (plat.includes("猎聘")) return "danger";
			if (plat.includes("智联")) return "primary";
			return "info";
		};
		const formatDate = (dateStr) => {
			if (!dateStr) return "未知";
			try {
				const d = new Date(dateStr);
				if (isNaN(d)) return dateStr;
				return `${d.getMonth() + 1}-${d.getDate()}`;
			} catch (e) {
				return dateStr;
			}
		};
		const viewDetails = (job) => {
			if (jobDetailDrawerRef.value) jobDetailDrawerRef.value.open(job);
		};
		const handleJobStatusChange = (jobData, status) => {
			if (status === "expired") companies.value.forEach((company) => {
				if (company.jobs) {
					const job = company.jobs.find((j) => j.jobId === jobData.jobId);
					if (job) job.status = "expired";
				}
			});
		};
		const fetchCompanies = async (isLoadMore = false) => {
			if (loading.value) return;
			if (!isLoadMore) {
				page.value = 1;
				hasMore.value = true;
			}
			loading.value = true;
			try {
				const res = await $fetch$1("/api/companies", { params: {
					page: page.value,
					pageSize: pageSize.value,
					query: searchQuery.value
				} });
				if (res.success) {
					if (isLoadMore) companies.value.push(...res.data);
					else companies.value = res.data;
					totalCompanies.value = res.total || 0;
					if (res.data.length < pageSize.value) hasMore.value = false;
				}
			} catch (error) {
				ElMessage.error("获取公司数据失败");
			} finally {
				loading.value = false;
			}
		};
		const loadMore = () => {
			if (!loading.value && hasMore.value) {
				page.value++;
				fetchCompanies(true);
			}
		};
		watch(loadMoreTrigger, (el) => {
			if (el) {
				if (observer) observer.disconnect();
				observer = new IntersectionObserver((entries) => {
					if (entries[0].isIntersecting && hasMore.value && !loading.value) loadMore();
				}, { rootMargin: "250px" });
				observer.observe(el);
			}
		});
		let searchTimeout = null;
		watch(searchQuery, () => {
			if (searchTimeout) clearTimeout(searchTimeout);
			searchTimeout = setTimeout(() => {
				fetchCompanies(false);
			}, 500);
		});
		return (_ctx, _push, _parent, _attrs) => {
			const _component_el_card = ElCard;
			const _component_el_input = ElInput;
			const _component_el_button = ElButton;
			const _component_el_collapse = ElCollapse;
			const _component_el_collapse_item = ElCollapseItem;
			const _component_el_tag = ElTag;
			const _component_el_empty = ElEmpty;
			const _component_el_icon = ElIcon;
			const _component_JobDetailDrawer = JobDetailDrawer_default;
			const _directive_loading = vLoading;
			_push(`<div${ssrRenderAttrs(mergeProps({ class: "companies-page" }, _attrs))} data-v-7ffb705b>`);
			_push(ssrRenderComponent(_component_el_card, { shadow: "never" }, {
				header: withCtx((_, _push, _parent, _scopeId) => {
					if (_push) {
						_push(`<div class="card-header" data-v-7ffb705b${_scopeId}><span style="${ssrRenderStyle({
							"font-size": "18px",
							"font-weight": "600"
						})}" data-v-7ffb705b${_scopeId}>🏢 企业全景</span>`);
						_push(ssrRenderComponent(_component_el_input, {
							modelValue: searchQuery.value,
							"onUpdate:modelValue": ($event) => searchQuery.value = $event,
							placeholder: "搜索公司名称...",
							style: { "width": "300px" },
							clearable: ""
						}, {
							prefix: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) _push(`<span data-v-7ffb705b${_scopeId}>🔍</span>`);
								else return [createVNode("span", null, "🔍")];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(ssrRenderComponent(_component_el_button, {
							type: "primary",
							size: "small",
							onClick: fetchCompanies,
							loading: loading.value
						}, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) _push(` 刷新数据 `);
								else return [createTextVNode(" 刷新数据 ")];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(`</div>`);
					} else return [createVNode("div", { class: "card-header" }, [
						createVNode("span", { style: {
							"font-size": "18px",
							"font-weight": "600"
						} }, "🏢 企业全景"),
						createVNode(_component_el_input, {
							modelValue: searchQuery.value,
							"onUpdate:modelValue": ($event) => searchQuery.value = $event,
							placeholder: "搜索公司名称...",
							style: { "width": "300px" },
							clearable: ""
						}, {
							prefix: withCtx(() => [createVNode("span", null, "🔍")]),
							_: 1
						}, 8, ["modelValue", "onUpdate:modelValue"]),
						createVNode(_component_el_button, {
							type: "primary",
							size: "small",
							onClick: fetchCompanies,
							loading: loading.value
						}, {
							default: withCtx(() => [createTextVNode(" 刷新数据 ")]),
							_: 1
						}, 8, ["loading"])
					])];
				}),
				default: withCtx((_, _push, _parent, _scopeId) => {
					if (_push) {
						_push(`<div${ssrRenderAttrs(ssrGetDirectiveProps(_ctx, _directive_loading, loading.value && page.value === 1))} data-v-7ffb705b${_scopeId}><div style="${ssrRenderStyle({
							"margin-bottom": "15px",
							"color": "#606266",
							"font-size": "14px"
						})}" data-v-7ffb705b${_scopeId}> 共收录 <strong data-v-7ffb705b${_scopeId}>${ssrInterpolate(totalCompanies.value)}</strong> 家公司 </div>`);
						if (companies.value.length > 0) _push(ssrRenderComponent(_component_el_collapse, null, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) {
									_push(`<!--[-->`);
									ssrRenderList(companies.value, (company) => {
										_push(ssrRenderComponent(_component_el_collapse_item, {
											key: company.companyName,
											name: company.companyName
										}, {
											title: withCtx((_, _push, _parent, _scopeId) => {
												if (_push) {
													_push(`<div class="company-title-wrapper" data-v-7ffb705b${_scopeId}><div class="company-title" data-v-7ffb705b${_scopeId}><span class="company-name" data-v-7ffb705b${_scopeId}>${ssrInterpolate(company.companyName)}</span>`);
													if (company.rawData?.industry) _push(ssrRenderComponent(_component_el_tag, {
														size: "small",
														type: "info"
													}, {
														default: withCtx((_, _push, _parent, _scopeId) => {
															if (_push) _push(`${ssrInterpolate(company.rawData.industry)}`);
															else return [createTextVNode(toDisplayString(company.rawData.industry), 1)];
														}),
														_: 2
													}, _parent, _scopeId));
													else _push(`<!---->`);
													if (company.rawData?.scale) _push(ssrRenderComponent(_component_el_tag, {
														size: "small",
														type: "success"
													}, {
														default: withCtx((_, _push, _parent, _scopeId) => {
															if (_push) _push(`${ssrInterpolate(company.rawData.scale)}`);
															else return [createTextVNode(toDisplayString(company.rawData.scale), 1)];
														}),
														_: 2
													}, _parent, _scopeId));
													else _push(`<!---->`);
													if (company.rawData?.stage) _push(ssrRenderComponent(_component_el_tag, {
														size: "small",
														type: "warning"
													}, {
														default: withCtx((_, _push, _parent, _scopeId) => {
															if (_push) _push(`${ssrInterpolate(company.rawData.stage)}`);
															else return [createTextVNode(toDisplayString(company.rawData.stage), 1)];
														}),
														_: 2
													}, _parent, _scopeId));
													else _push(`<!---->`);
													_push(ssrRenderComponent(_component_el_tag, {
														size: "small",
														type: "success",
														effect: "dark",
														style: { "margin-left": "10px" }
													}, {
														default: withCtx((_, _push, _parent, _scopeId) => {
															if (_push) _push(` 共 ${ssrInterpolate(company.jobs?.length || 0)} 个职位 `);
															else return [createTextVNode(" 共 " + toDisplayString(company.jobs?.length || 0) + " 个职位 ", 1)];
														}),
														_: 2
													}, _parent, _scopeId));
													_push(`</div><div class="platform-tags" data-v-7ffb705b${_scopeId}><!--[-->`);
													ssrRenderList(company.platformSources, (plat) => {
														_push(ssrRenderComponent(_component_el_tag, {
															key: plat,
															size: "small",
															type: getPlatformType(plat),
															effect: "dark"
														}, {
															default: withCtx((_, _push, _parent, _scopeId) => {
																if (_push) _push(`${ssrInterpolate(plat)}`);
																else return [createTextVNode(toDisplayString(plat), 1)];
															}),
															_: 2
														}, _parent, _scopeId));
													});
													_push(`<!--]--></div></div>`);
												} else return [createVNode("div", { class: "company-title-wrapper" }, [createVNode("div", { class: "company-title" }, [
													createVNode("span", { class: "company-name" }, toDisplayString(company.companyName), 1),
													company.rawData?.industry ? (openBlock(), createBlock(_component_el_tag, {
														key: 0,
														size: "small",
														type: "info"
													}, {
														default: withCtx(() => [createTextVNode(toDisplayString(company.rawData.industry), 1)]),
														_: 2
													}, 1024)) : createCommentVNode("", true),
													company.rawData?.scale ? (openBlock(), createBlock(_component_el_tag, {
														key: 1,
														size: "small",
														type: "success"
													}, {
														default: withCtx(() => [createTextVNode(toDisplayString(company.rawData.scale), 1)]),
														_: 2
													}, 1024)) : createCommentVNode("", true),
													company.rawData?.stage ? (openBlock(), createBlock(_component_el_tag, {
														key: 2,
														size: "small",
														type: "warning"
													}, {
														default: withCtx(() => [createTextVNode(toDisplayString(company.rawData.stage), 1)]),
														_: 2
													}, 1024)) : createCommentVNode("", true),
													createVNode(_component_el_tag, {
														size: "small",
														type: "success",
														effect: "dark",
														style: { "margin-left": "10px" }
													}, {
														default: withCtx(() => [createTextVNode(" 共 " + toDisplayString(company.jobs?.length || 0) + " 个职位 ", 1)]),
														_: 2
													}, 1024)
												]), createVNode("div", { class: "platform-tags" }, [(openBlock(true), createBlock(Fragment, null, renderList(company.platformSources, (plat) => {
													return openBlock(), createBlock(_component_el_tag, {
														key: plat,
														size: "small",
														type: getPlatformType(plat),
														effect: "dark"
													}, {
														default: withCtx(() => [createTextVNode(toDisplayString(plat), 1)]),
														_: 2
													}, 1032, ["type"]);
												}), 128))])])];
											}),
											default: withCtx((_, _push, _parent, _scopeId) => {
												if (_push) {
													_push(`<div class="company-content" data-v-7ffb705b${_scopeId}>`);
													if (company.rawData?.companyDesc || company.rawData?.welfare?.length) {
														_push(`<div class="company-info-section" data-v-7ffb705b${_scopeId}>`);
														if (company.rawData?.companyDesc) _push(`<div data-v-7ffb705b${_scopeId}><h4 style="${ssrRenderStyle({ "margin-top": "5px" })}" data-v-7ffb705b${_scopeId}>公司介绍</h4><p class="company-desc" data-v-7ffb705b${_scopeId}>${ssrInterpolate(company.rawData.companyDesc)}</p></div>`);
														else _push(`<!---->`);
														if (company.rawData?.welfare?.length) {
															_push(`<div class="company-welfare" data-v-7ffb705b${_scopeId}><h4 data-v-7ffb705b${_scopeId}>公司福利</h4><!--[-->`);
															ssrRenderList(company.rawData.welfare, (w, idx) => {
																_push(ssrRenderComponent(_component_el_tag, {
																	key: idx,
																	size: "small",
																	type: "success",
																	effect: "light",
																	style: {
																		"margin-right": "8px",
																		"margin-bottom": "8px"
																	}
																}, {
																	default: withCtx((_, _push, _parent, _scopeId) => {
																		if (_push) _push(`${ssrInterpolate(w)}`);
																		else return [createTextVNode(toDisplayString(w), 1)];
																	}),
																	_: 2
																}, _parent, _scopeId));
															});
															_push(`<!--]--></div>`);
														} else _push(`<!---->`);
														_push(`</div>`);
													} else _push(`<!---->`);
													if (company.jobs && company.jobs.length > 0) {
														_push(`<div class="job-card-grid" data-v-7ffb705b${_scopeId}><!--[-->`);
														ssrRenderList(company.jobs, (job) => {
															_push(`<div class="${ssrRenderClass([{
																"is-expired": job.status === "expired" || job.status === "closed",
																"is-hidden": job.isHidden
															}, "job-card"])}" data-v-7ffb705b${_scopeId}><div class="job-card-header" data-v-7ffb705b${_scopeId}><h3 class="job-title"${ssrRenderAttr("title", job.title)} data-v-7ffb705b${_scopeId}>${ssrInterpolate(job.title)}</h3><span class="job-salary" data-v-7ffb705b${_scopeId}>${ssrInterpolate(job.salary)}</span></div><div class="job-card-info" data-v-7ffb705b${_scopeId}>${ssrInterpolate([job.normalizedData?.city, job.normalizedData?.area].filter(Boolean).join("·"))} | ${ssrInterpolate(job.normalizedData?.experience || "不限")} | ${ssrInterpolate(job.normalizedData?.degree || "不限")}</div>`);
															if (job.tags && job.tags.length > 0 || job.status === "expired" || job.status === "closed" || job.isHidden || job.normalizedData?.isHeadhunter) {
																_push(`<div class="job-card-tags" data-v-7ffb705b${_scopeId}>`);
																if (job.status === "expired" || job.status === "closed") _push(ssrRenderComponent(_component_el_tag, {
																	size: "small",
																	type: "danger",
																	effect: "dark",
																	style: {
																		"border": "none",
																		"padding": "0 6px",
																		"height": "20px",
																		"line-height": "20px"
																	}
																}, {
																	default: withCtx((_, _push, _parent, _scopeId) => {
																		if (_push) _push(`失效`);
																		else return [createTextVNode("失效")];
																	}),
																	_: 2
																}, _parent, _scopeId));
																else _push(`<!---->`);
																if (job.isHidden) _push(ssrRenderComponent(_component_el_tag, {
																	size: "small",
																	type: "info",
																	effect: "dark",
																	style: {
																		"border": "none",
																		"padding": "0 6px",
																		"height": "20px",
																		"line-height": "20px"
																	}
																}, {
																	default: withCtx((_, _push, _parent, _scopeId) => {
																		if (_push) _push(`不合适`);
																		else return [createTextVNode("不合适")];
																	}),
																	_: 2
																}, _parent, _scopeId));
																else _push(`<!---->`);
																if (job.normalizedData?.isHeadhunter) _push(ssrRenderComponent(_component_el_tag, {
																	size: "small",
																	color: "#fce4ec",
																	style: {
																		"color": "#c2185b",
																		"border": "1px solid #f8bbd0",
																		"padding": "0 6px",
																		"height": "20px",
																		"line-height": "20px"
																	}
																}, {
																	default: withCtx((_, _push, _parent, _scopeId) => {
																		if (_push) {
																			_push(` 代招公司`);
																			if (job.normalizedData?.clientCompanyName) _push(`<span data-v-7ffb705b${_scopeId}>：${ssrInterpolate(job.normalizedData.clientCompanyName)}</span>`);
																			else _push(`<!---->`);
																		} else return [createTextVNode(" 代招公司"), job.normalizedData?.clientCompanyName ? (openBlock(), createBlock("span", { key: 0 }, "：" + toDisplayString(job.normalizedData.clientCompanyName), 1)) : createCommentVNode("", true)];
																	}),
																	_: 2
																}, _parent, _scopeId));
																else _push(`<!---->`);
																_push(`<!--[-->`);
																ssrRenderList(job.tags, (t, idx) => {
																	_push(`<span class="job-tag" data-v-7ffb705b${_scopeId}>${ssrInterpolate(t)}</span>`);
																});
																_push(`<!--]--></div>`);
															} else _push(`<!---->`);
															_push(`<div class="job-card-footer" data-v-7ffb705b${_scopeId}><div class="job-hr" data-v-7ffb705b${_scopeId}>`);
															if (job.normalizedData?.hrName) _push(`<span data-v-7ffb705b${_scopeId}>${ssrInterpolate(job.normalizedData.hrName)}</span>`);
															else _push(`<!---->`);
															if (job.normalizedData?.hrPosition) _push(`<span data-v-7ffb705b${_scopeId}>${ssrInterpolate(job.normalizedData.hrName ? " · " : "")}${ssrInterpolate(job.normalizedData.hrPosition)}</span>`);
															else _push(`<!---->`);
															_push(`</div><div class="job-meta" style="${ssrRenderStyle({
																"display": "flex",
																"align-items": "center",
																"gap": "8px"
															})}" data-v-7ffb705b${_scopeId}><span class="job-date" data-v-7ffb705b${_scopeId}>${ssrInterpolate(formatDate(job.normalizedData?.updateDate))}</span>`);
															_push(ssrRenderComponent(_component_el_tag, {
																size: "small",
																type: getPlatformType(job.platform)
															}, {
																default: withCtx((_, _push, _parent, _scopeId) => {
																	if (_push) _push(`${ssrInterpolate(job.platform)}`);
																	else return [createTextVNode(toDisplayString(job.platform), 1)];
																}),
																_: 2
															}, _parent, _scopeId));
															_push(`</div></div></div>`);
														});
														_push(`<!--]--></div>`);
													} else _push(`<!---->`);
													_push(`</div>`);
												} else return [createVNode("div", { class: "company-content" }, [company.rawData?.companyDesc || company.rawData?.welfare?.length ? (openBlock(), createBlock("div", {
													key: 0,
													class: "company-info-section"
												}, [company.rawData?.companyDesc ? (openBlock(), createBlock("div", { key: 0 }, [createVNode("h4", { style: { "margin-top": "5px" } }, "公司介绍"), createVNode("p", { class: "company-desc" }, toDisplayString(company.rawData.companyDesc), 1)])) : createCommentVNode("", true), company.rawData?.welfare?.length ? (openBlock(), createBlock("div", {
													key: 1,
													class: "company-welfare"
												}, [createVNode("h4", null, "公司福利"), (openBlock(true), createBlock(Fragment, null, renderList(company.rawData.welfare, (w, idx) => {
													return openBlock(), createBlock(_component_el_tag, {
														key: idx,
														size: "small",
														type: "success",
														effect: "light",
														style: {
															"margin-right": "8px",
															"margin-bottom": "8px"
														}
													}, {
														default: withCtx(() => [createTextVNode(toDisplayString(w), 1)]),
														_: 2
													}, 1024);
												}), 128))])) : createCommentVNode("", true)])) : createCommentVNode("", true), company.jobs && company.jobs.length > 0 ? (openBlock(), createBlock("div", {
													key: 1,
													class: "job-card-grid"
												}, [(openBlock(true), createBlock(Fragment, null, renderList(company.jobs, (job) => {
													return openBlock(), createBlock("div", {
														class: ["job-card", {
															"is-expired": job.status === "expired" || job.status === "closed",
															"is-hidden": job.isHidden
														}],
														key: job.jobId,
														onClick: ($event) => viewDetails(job)
													}, [
														createVNode("div", { class: "job-card-header" }, [createVNode("h3", {
															class: "job-title",
															title: job.title
														}, toDisplayString(job.title), 9, ["title"]), createVNode("span", { class: "job-salary" }, toDisplayString(job.salary), 1)]),
														createVNode("div", { class: "job-card-info" }, toDisplayString([job.normalizedData?.city, job.normalizedData?.area].filter(Boolean).join("·")) + " | " + toDisplayString(job.normalizedData?.experience || "不限") + " | " + toDisplayString(job.normalizedData?.degree || "不限"), 1),
														job.tags && job.tags.length > 0 || job.status === "expired" || job.status === "closed" || job.isHidden || job.normalizedData?.isHeadhunter ? (openBlock(), createBlock("div", {
															key: 0,
															class: "job-card-tags"
														}, [
															job.status === "expired" || job.status === "closed" ? (openBlock(), createBlock(_component_el_tag, {
																key: 0,
																size: "small",
																type: "danger",
																effect: "dark",
																style: {
																	"border": "none",
																	"padding": "0 6px",
																	"height": "20px",
																	"line-height": "20px"
																}
															}, {
																default: withCtx(() => [createTextVNode("失效")]),
																_: 1
															})) : createCommentVNode("", true),
															job.isHidden ? (openBlock(), createBlock(_component_el_tag, {
																key: 1,
																size: "small",
																type: "info",
																effect: "dark",
																style: {
																	"border": "none",
																	"padding": "0 6px",
																	"height": "20px",
																	"line-height": "20px"
																}
															}, {
																default: withCtx(() => [createTextVNode("不合适")]),
																_: 1
															})) : createCommentVNode("", true),
															job.normalizedData?.isHeadhunter ? (openBlock(), createBlock(_component_el_tag, {
																key: 2,
																size: "small",
																color: "#fce4ec",
																style: {
																	"color": "#c2185b",
																	"border": "1px solid #f8bbd0",
																	"padding": "0 6px",
																	"height": "20px",
																	"line-height": "20px"
																}
															}, {
																default: withCtx(() => [createTextVNode(" 代招公司"), job.normalizedData?.clientCompanyName ? (openBlock(), createBlock("span", { key: 0 }, "：" + toDisplayString(job.normalizedData.clientCompanyName), 1)) : createCommentVNode("", true)]),
																_: 2
															}, 1024)) : createCommentVNode("", true),
															(openBlock(true), createBlock(Fragment, null, renderList(job.tags, (t, idx) => {
																return openBlock(), createBlock("span", {
																	class: "job-tag",
																	key: idx
																}, toDisplayString(t), 1);
															}), 128))
														])) : createCommentVNode("", true),
														createVNode("div", { class: "job-card-footer" }, [createVNode("div", { class: "job-hr" }, [job.normalizedData?.hrName ? (openBlock(), createBlock("span", { key: 0 }, toDisplayString(job.normalizedData.hrName), 1)) : createCommentVNode("", true), job.normalizedData?.hrPosition ? (openBlock(), createBlock("span", { key: 1 }, toDisplayString(job.normalizedData.hrName ? " · " : "") + toDisplayString(job.normalizedData.hrPosition), 1)) : createCommentVNode("", true)]), createVNode("div", {
															class: "job-meta",
															style: {
																"display": "flex",
																"align-items": "center",
																"gap": "8px"
															}
														}, [createVNode("span", { class: "job-date" }, toDisplayString(formatDate(job.normalizedData?.updateDate)), 1), createVNode(_component_el_tag, {
															size: "small",
															type: getPlatformType(job.platform)
														}, {
															default: withCtx(() => [createTextVNode(toDisplayString(job.platform), 1)]),
															_: 2
														}, 1032, ["type"])])])
													], 10, ["onClick"]);
												}), 128))])) : createCommentVNode("", true)])];
											}),
											_: 2
										}, _parent, _scopeId));
									});
									_push(`<!--]-->`);
								} else return [(openBlock(true), createBlock(Fragment, null, renderList(companies.value, (company) => {
									return openBlock(), createBlock(_component_el_collapse_item, {
										key: company.companyName,
										name: company.companyName
									}, {
										title: withCtx(() => [createVNode("div", { class: "company-title-wrapper" }, [createVNode("div", { class: "company-title" }, [
											createVNode("span", { class: "company-name" }, toDisplayString(company.companyName), 1),
											company.rawData?.industry ? (openBlock(), createBlock(_component_el_tag, {
												key: 0,
												size: "small",
												type: "info"
											}, {
												default: withCtx(() => [createTextVNode(toDisplayString(company.rawData.industry), 1)]),
												_: 2
											}, 1024)) : createCommentVNode("", true),
											company.rawData?.scale ? (openBlock(), createBlock(_component_el_tag, {
												key: 1,
												size: "small",
												type: "success"
											}, {
												default: withCtx(() => [createTextVNode(toDisplayString(company.rawData.scale), 1)]),
												_: 2
											}, 1024)) : createCommentVNode("", true),
											company.rawData?.stage ? (openBlock(), createBlock(_component_el_tag, {
												key: 2,
												size: "small",
												type: "warning"
											}, {
												default: withCtx(() => [createTextVNode(toDisplayString(company.rawData.stage), 1)]),
												_: 2
											}, 1024)) : createCommentVNode("", true),
											createVNode(_component_el_tag, {
												size: "small",
												type: "success",
												effect: "dark",
												style: { "margin-left": "10px" }
											}, {
												default: withCtx(() => [createTextVNode(" 共 " + toDisplayString(company.jobs?.length || 0) + " 个职位 ", 1)]),
												_: 2
											}, 1024)
										]), createVNode("div", { class: "platform-tags" }, [(openBlock(true), createBlock(Fragment, null, renderList(company.platformSources, (plat) => {
											return openBlock(), createBlock(_component_el_tag, {
												key: plat,
												size: "small",
												type: getPlatformType(plat),
												effect: "dark"
											}, {
												default: withCtx(() => [createTextVNode(toDisplayString(plat), 1)]),
												_: 2
											}, 1032, ["type"]);
										}), 128))])])]),
										default: withCtx(() => [createVNode("div", { class: "company-content" }, [company.rawData?.companyDesc || company.rawData?.welfare?.length ? (openBlock(), createBlock("div", {
											key: 0,
											class: "company-info-section"
										}, [company.rawData?.companyDesc ? (openBlock(), createBlock("div", { key: 0 }, [createVNode("h4", { style: { "margin-top": "5px" } }, "公司介绍"), createVNode("p", { class: "company-desc" }, toDisplayString(company.rawData.companyDesc), 1)])) : createCommentVNode("", true), company.rawData?.welfare?.length ? (openBlock(), createBlock("div", {
											key: 1,
											class: "company-welfare"
										}, [createVNode("h4", null, "公司福利"), (openBlock(true), createBlock(Fragment, null, renderList(company.rawData.welfare, (w, idx) => {
											return openBlock(), createBlock(_component_el_tag, {
												key: idx,
												size: "small",
												type: "success",
												effect: "light",
												style: {
													"margin-right": "8px",
													"margin-bottom": "8px"
												}
											}, {
												default: withCtx(() => [createTextVNode(toDisplayString(w), 1)]),
												_: 2
											}, 1024);
										}), 128))])) : createCommentVNode("", true)])) : createCommentVNode("", true), company.jobs && company.jobs.length > 0 ? (openBlock(), createBlock("div", {
											key: 1,
											class: "job-card-grid"
										}, [(openBlock(true), createBlock(Fragment, null, renderList(company.jobs, (job) => {
											return openBlock(), createBlock("div", {
												class: ["job-card", {
													"is-expired": job.status === "expired" || job.status === "closed",
													"is-hidden": job.isHidden
												}],
												key: job.jobId,
												onClick: ($event) => viewDetails(job)
											}, [
												createVNode("div", { class: "job-card-header" }, [createVNode("h3", {
													class: "job-title",
													title: job.title
												}, toDisplayString(job.title), 9, ["title"]), createVNode("span", { class: "job-salary" }, toDisplayString(job.salary), 1)]),
												createVNode("div", { class: "job-card-info" }, toDisplayString([job.normalizedData?.city, job.normalizedData?.area].filter(Boolean).join("·")) + " | " + toDisplayString(job.normalizedData?.experience || "不限") + " | " + toDisplayString(job.normalizedData?.degree || "不限"), 1),
												job.tags && job.tags.length > 0 || job.status === "expired" || job.status === "closed" || job.isHidden || job.normalizedData?.isHeadhunter ? (openBlock(), createBlock("div", {
													key: 0,
													class: "job-card-tags"
												}, [
													job.status === "expired" || job.status === "closed" ? (openBlock(), createBlock(_component_el_tag, {
														key: 0,
														size: "small",
														type: "danger",
														effect: "dark",
														style: {
															"border": "none",
															"padding": "0 6px",
															"height": "20px",
															"line-height": "20px"
														}
													}, {
														default: withCtx(() => [createTextVNode("失效")]),
														_: 1
													})) : createCommentVNode("", true),
													job.isHidden ? (openBlock(), createBlock(_component_el_tag, {
														key: 1,
														size: "small",
														type: "info",
														effect: "dark",
														style: {
															"border": "none",
															"padding": "0 6px",
															"height": "20px",
															"line-height": "20px"
														}
													}, {
														default: withCtx(() => [createTextVNode("不合适")]),
														_: 1
													})) : createCommentVNode("", true),
													job.normalizedData?.isHeadhunter ? (openBlock(), createBlock(_component_el_tag, {
														key: 2,
														size: "small",
														color: "#fce4ec",
														style: {
															"color": "#c2185b",
															"border": "1px solid #f8bbd0",
															"padding": "0 6px",
															"height": "20px",
															"line-height": "20px"
														}
													}, {
														default: withCtx(() => [createTextVNode(" 代招公司"), job.normalizedData?.clientCompanyName ? (openBlock(), createBlock("span", { key: 0 }, "：" + toDisplayString(job.normalizedData.clientCompanyName), 1)) : createCommentVNode("", true)]),
														_: 2
													}, 1024)) : createCommentVNode("", true),
													(openBlock(true), createBlock(Fragment, null, renderList(job.tags, (t, idx) => {
														return openBlock(), createBlock("span", {
															class: "job-tag",
															key: idx
														}, toDisplayString(t), 1);
													}), 128))
												])) : createCommentVNode("", true),
												createVNode("div", { class: "job-card-footer" }, [createVNode("div", { class: "job-hr" }, [job.normalizedData?.hrName ? (openBlock(), createBlock("span", { key: 0 }, toDisplayString(job.normalizedData.hrName), 1)) : createCommentVNode("", true), job.normalizedData?.hrPosition ? (openBlock(), createBlock("span", { key: 1 }, toDisplayString(job.normalizedData.hrName ? " · " : "") + toDisplayString(job.normalizedData.hrPosition), 1)) : createCommentVNode("", true)]), createVNode("div", {
													class: "job-meta",
													style: {
														"display": "flex",
														"align-items": "center",
														"gap": "8px"
													}
												}, [createVNode("span", { class: "job-date" }, toDisplayString(formatDate(job.normalizedData?.updateDate)), 1), createVNode(_component_el_tag, {
													size: "small",
													type: getPlatformType(job.platform)
												}, {
													default: withCtx(() => [createTextVNode(toDisplayString(job.platform), 1)]),
													_: 2
												}, 1032, ["type"])])])
											], 10, ["onClick"]);
										}), 128))])) : createCommentVNode("", true)])]),
										_: 2
									}, 1032, ["name"]);
								}), 128))];
							}),
							_: 1
						}, _parent, _scopeId));
						else if (!loading.value) _push(ssrRenderComponent(_component_el_empty, { description: "没有找到匹配的企业数据" }, null, _parent, _scopeId));
						else _push(`<!---->`);
						_push(`<div class="observer-trigger" style="${ssrRenderStyle({
							"height": "10px",
							"width": "100%"
						})}" data-v-7ffb705b${_scopeId}></div>`);
						if (loading.value && page.value > 1) {
							_push(`<div class="loading-more" style="${ssrRenderStyle({
								"text-align": "center",
								"padding": "15px",
								"color": "#909399"
							})}" data-v-7ffb705b${_scopeId}>`);
							_push(ssrRenderComponent(_component_el_icon, { class: "is-loading" }, {
								default: withCtx((_, _push, _parent, _scopeId) => {
									if (_push) _push(ssrRenderComponent(unref(Loader2), null, null, _parent, _scopeId));
									else return [createVNode(unref(Loader2))];
								}),
								_: 1
							}, _parent, _scopeId));
							_push(` 正在加载更多数据... </div>`);
						} else _push(`<!---->`);
						if (!hasMore.value && companies.value.length > 0) _push(`<div class="no-more" style="${ssrRenderStyle({
							"text-align": "center",
							"padding": "15px",
							"color": "#909399",
							"font-size": "14px"
						})}" data-v-7ffb705b${_scopeId}> 已经到底啦 ~ </div>`);
						else _push(`<!---->`);
						_push(`</div>`);
					} else return [withDirectives((openBlock(), createBlock("div", null, [
						createVNode("div", { style: {
							"margin-bottom": "15px",
							"color": "#606266",
							"font-size": "14px"
						} }, [
							createTextVNode(" 共收录 "),
							createVNode("strong", null, toDisplayString(totalCompanies.value), 1),
							createTextVNode(" 家公司 ")
						]),
						companies.value.length > 0 ? (openBlock(), createBlock(_component_el_collapse, { key: 0 }, {
							default: withCtx(() => [(openBlock(true), createBlock(Fragment, null, renderList(companies.value, (company) => {
								return openBlock(), createBlock(_component_el_collapse_item, {
									key: company.companyName,
									name: company.companyName
								}, {
									title: withCtx(() => [createVNode("div", { class: "company-title-wrapper" }, [createVNode("div", { class: "company-title" }, [
										createVNode("span", { class: "company-name" }, toDisplayString(company.companyName), 1),
										company.rawData?.industry ? (openBlock(), createBlock(_component_el_tag, {
											key: 0,
											size: "small",
											type: "info"
										}, {
											default: withCtx(() => [createTextVNode(toDisplayString(company.rawData.industry), 1)]),
											_: 2
										}, 1024)) : createCommentVNode("", true),
										company.rawData?.scale ? (openBlock(), createBlock(_component_el_tag, {
											key: 1,
											size: "small",
											type: "success"
										}, {
											default: withCtx(() => [createTextVNode(toDisplayString(company.rawData.scale), 1)]),
											_: 2
										}, 1024)) : createCommentVNode("", true),
										company.rawData?.stage ? (openBlock(), createBlock(_component_el_tag, {
											key: 2,
											size: "small",
											type: "warning"
										}, {
											default: withCtx(() => [createTextVNode(toDisplayString(company.rawData.stage), 1)]),
											_: 2
										}, 1024)) : createCommentVNode("", true),
										createVNode(_component_el_tag, {
											size: "small",
											type: "success",
											effect: "dark",
											style: { "margin-left": "10px" }
										}, {
											default: withCtx(() => [createTextVNode(" 共 " + toDisplayString(company.jobs?.length || 0) + " 个职位 ", 1)]),
											_: 2
										}, 1024)
									]), createVNode("div", { class: "platform-tags" }, [(openBlock(true), createBlock(Fragment, null, renderList(company.platformSources, (plat) => {
										return openBlock(), createBlock(_component_el_tag, {
											key: plat,
											size: "small",
											type: getPlatformType(plat),
											effect: "dark"
										}, {
											default: withCtx(() => [createTextVNode(toDisplayString(plat), 1)]),
											_: 2
										}, 1032, ["type"]);
									}), 128))])])]),
									default: withCtx(() => [createVNode("div", { class: "company-content" }, [company.rawData?.companyDesc || company.rawData?.welfare?.length ? (openBlock(), createBlock("div", {
										key: 0,
										class: "company-info-section"
									}, [company.rawData?.companyDesc ? (openBlock(), createBlock("div", { key: 0 }, [createVNode("h4", { style: { "margin-top": "5px" } }, "公司介绍"), createVNode("p", { class: "company-desc" }, toDisplayString(company.rawData.companyDesc), 1)])) : createCommentVNode("", true), company.rawData?.welfare?.length ? (openBlock(), createBlock("div", {
										key: 1,
										class: "company-welfare"
									}, [createVNode("h4", null, "公司福利"), (openBlock(true), createBlock(Fragment, null, renderList(company.rawData.welfare, (w, idx) => {
										return openBlock(), createBlock(_component_el_tag, {
											key: idx,
											size: "small",
											type: "success",
											effect: "light",
											style: {
												"margin-right": "8px",
												"margin-bottom": "8px"
											}
										}, {
											default: withCtx(() => [createTextVNode(toDisplayString(w), 1)]),
											_: 2
										}, 1024);
									}), 128))])) : createCommentVNode("", true)])) : createCommentVNode("", true), company.jobs && company.jobs.length > 0 ? (openBlock(), createBlock("div", {
										key: 1,
										class: "job-card-grid"
									}, [(openBlock(true), createBlock(Fragment, null, renderList(company.jobs, (job) => {
										return openBlock(), createBlock("div", {
											class: ["job-card", {
												"is-expired": job.status === "expired" || job.status === "closed",
												"is-hidden": job.isHidden
											}],
											key: job.jobId,
											onClick: ($event) => viewDetails(job)
										}, [
											createVNode("div", { class: "job-card-header" }, [createVNode("h3", {
												class: "job-title",
												title: job.title
											}, toDisplayString(job.title), 9, ["title"]), createVNode("span", { class: "job-salary" }, toDisplayString(job.salary), 1)]),
											createVNode("div", { class: "job-card-info" }, toDisplayString([job.normalizedData?.city, job.normalizedData?.area].filter(Boolean).join("·")) + " | " + toDisplayString(job.normalizedData?.experience || "不限") + " | " + toDisplayString(job.normalizedData?.degree || "不限"), 1),
											job.tags && job.tags.length > 0 || job.status === "expired" || job.status === "closed" || job.isHidden || job.normalizedData?.isHeadhunter ? (openBlock(), createBlock("div", {
												key: 0,
												class: "job-card-tags"
											}, [
												job.status === "expired" || job.status === "closed" ? (openBlock(), createBlock(_component_el_tag, {
													key: 0,
													size: "small",
													type: "danger",
													effect: "dark",
													style: {
														"border": "none",
														"padding": "0 6px",
														"height": "20px",
														"line-height": "20px"
													}
												}, {
													default: withCtx(() => [createTextVNode("失效")]),
													_: 1
												})) : createCommentVNode("", true),
												job.isHidden ? (openBlock(), createBlock(_component_el_tag, {
													key: 1,
													size: "small",
													type: "info",
													effect: "dark",
													style: {
														"border": "none",
														"padding": "0 6px",
														"height": "20px",
														"line-height": "20px"
													}
												}, {
													default: withCtx(() => [createTextVNode("不合适")]),
													_: 1
												})) : createCommentVNode("", true),
												job.normalizedData?.isHeadhunter ? (openBlock(), createBlock(_component_el_tag, {
													key: 2,
													size: "small",
													color: "#fce4ec",
													style: {
														"color": "#c2185b",
														"border": "1px solid #f8bbd0",
														"padding": "0 6px",
														"height": "20px",
														"line-height": "20px"
													}
												}, {
													default: withCtx(() => [createTextVNode(" 代招公司"), job.normalizedData?.clientCompanyName ? (openBlock(), createBlock("span", { key: 0 }, "：" + toDisplayString(job.normalizedData.clientCompanyName), 1)) : createCommentVNode("", true)]),
													_: 2
												}, 1024)) : createCommentVNode("", true),
												(openBlock(true), createBlock(Fragment, null, renderList(job.tags, (t, idx) => {
													return openBlock(), createBlock("span", {
														class: "job-tag",
														key: idx
													}, toDisplayString(t), 1);
												}), 128))
											])) : createCommentVNode("", true),
											createVNode("div", { class: "job-card-footer" }, [createVNode("div", { class: "job-hr" }, [job.normalizedData?.hrName ? (openBlock(), createBlock("span", { key: 0 }, toDisplayString(job.normalizedData.hrName), 1)) : createCommentVNode("", true), job.normalizedData?.hrPosition ? (openBlock(), createBlock("span", { key: 1 }, toDisplayString(job.normalizedData.hrName ? " · " : "") + toDisplayString(job.normalizedData.hrPosition), 1)) : createCommentVNode("", true)]), createVNode("div", {
												class: "job-meta",
												style: {
													"display": "flex",
													"align-items": "center",
													"gap": "8px"
												}
											}, [createVNode("span", { class: "job-date" }, toDisplayString(formatDate(job.normalizedData?.updateDate)), 1), createVNode(_component_el_tag, {
												size: "small",
												type: getPlatformType(job.platform)
											}, {
												default: withCtx(() => [createTextVNode(toDisplayString(job.platform), 1)]),
												_: 2
											}, 1032, ["type"])])])
										], 10, ["onClick"]);
									}), 128))])) : createCommentVNode("", true)])]),
									_: 2
								}, 1032, ["name"]);
							}), 128))]),
							_: 1
						})) : !loading.value ? (openBlock(), createBlock(_component_el_empty, {
							key: 1,
							description: "没有找到匹配的企业数据"
						})) : createCommentVNode("", true),
						createVNode("div", {
							ref_key: "loadMoreTrigger",
							ref: loadMoreTrigger,
							class: "observer-trigger",
							style: {
								"height": "10px",
								"width": "100%"
							}
						}, null, 512),
						loading.value && page.value > 1 ? (openBlock(), createBlock("div", {
							key: 2,
							class: "loading-more",
							style: {
								"text-align": "center",
								"padding": "15px",
								"color": "#909399"
							}
						}, [createVNode(_component_el_icon, { class: "is-loading" }, {
							default: withCtx(() => [createVNode(unref(Loader2))]),
							_: 1
						}), createTextVNode(" 正在加载更多数据... ")])) : createCommentVNode("", true),
						!hasMore.value && companies.value.length > 0 ? (openBlock(), createBlock("div", {
							key: 3,
							class: "no-more",
							style: {
								"text-align": "center",
								"padding": "15px",
								"color": "#909399",
								"font-size": "14px"
							}
						}, " 已经到底啦 ~ ")) : createCommentVNode("", true)
					])), [[_directive_loading, loading.value && page.value === 1]])];
				}),
				_: 1
			}, _parent));
			_push(ssrRenderComponent(_component_JobDetailDrawer, {
				ref_key: "jobDetailDrawerRef",
				ref: jobDetailDrawerRef,
				onStatusChanged: handleJobStatusChange
			}, null, _parent));
			_push(`</div>`);
		};
	}
};
var _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
	const ssrContext = useSSRContext();
	(ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/companies/index.vue");
	return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
var companies_default = /*#__PURE__*/ _plugin_vue_export_helper_default(_sfc_main, [["__scopeId", "data-v-7ffb705b"]]);

export { companies_default as default };
//# sourceMappingURL=companies-B9E0AjxU.mjs.map
