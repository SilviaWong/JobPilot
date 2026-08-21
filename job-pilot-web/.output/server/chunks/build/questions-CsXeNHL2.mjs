import { _ as _plugin_vue_export_helper_default, $ as $fetch$1, a as isNumber, u as useNamespace, d as debugWarn } from '../virtual/entry.mjs';
import { w as withInstall, b as buildProps, i as iconPropType, d as definePropType, e as arrow_right_default, q as arrow_left_default, E as ElIcon, r as d_arrow_left_default, s as more_filled_default, t as d_arrow_right_default } from './icon-BTfkZ5_-.mjs';
import { m as mutable } from './typescript-DnijNOm4.mjs';
import { E as ElInput, C as CHANGE_EVENT } from './css-BTXcRLa_.mjs';
import { a as ElCard, E as ElButton, b as ElMessage, j as componentSizes, f as useSizeProp, i as useLocale, k as useGlobalSize, h as useDeprecated } from './css-n73NFgGH.mjs';
import { E as ElSelect, a as ElOption } from './css-CQsUtvuV.mjs';
import { E as ElTable, a as ElTableColumn, b as ElPopconfirm, c as ElForm, d as ElFormItem } from './css-Dvk9M-E-.mjs';
import { E as ElTag, a as ElDrawer } from './css-B_mwQh58.mjs';
import { v as vLoading } from './css-BxzHr8sS.mjs';
import { ref, mergeProps, withCtx, createVNode, openBlock, createBlock, toDisplayString, createCommentVNode, createTextVNode, Fragment, renderList, withDirectives, withKeys, defineComponent, getCurrentInstance, computed, watch, provide, h, useSSRContext, createElementBlock, unref, normalizeClass, resolveDynamicComponent, createElementVNode, inject } from 'vue';
import { isEqual } from 'lodash-unified';
import { ssrRenderAttrs, ssrRenderComponent, ssrGetDirectiveProps, ssrRenderStyle, ssrInterpolate, ssrRenderList } from 'vue/server-renderer';
import { isArray } from '@vue/shared';
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
import './arrays-DLJ1qaKZ.mjs';
import '@ctrl/tinycolor';
import './css-20Rh4iVR.mjs';
import '@popperjs/core';
import './vnode-BA-uK14B.mjs';
import './click-outside-mj8goBwo.mjs';
import './css-DCGp0dw5.mjs';
import 'async-validator';
import 'normalize-wheel-es';

//#region node_modules/element-plus/es/components/pagination/src/constants.mjs
var elPaginationKey = Symbol("elPaginationKey");
//#endregion
//#region node_modules/element-plus/es/components/pagination/src/components/prev.mjs
var paginationPrevProps = buildProps({
	disabled: Boolean,
	currentPage: {
		type: Number,
		default: 1
	},
	prevText: { type: String },
	prevIcon: { type: iconPropType }
});
var paginationPrevEmits = { click: (evt) => evt instanceof MouseEvent };
//#endregion
//#region node_modules/element-plus/es/components/pagination/src/components/prev.vue_vue_type_script_setup_true_lang.mjs
var _hoisted_1$4 = [
	"disabled",
	"aria-label",
	"aria-disabled"
];
var _hoisted_2$2 = { key: 0 };
//#endregion
//#region node_modules/element-plus/es/components/pagination/src/components/prev2.mjs
var prev_default = /* @__PURE__ */ defineComponent({
	name: "ElPaginationPrev",
	__name: "prev",
	props: paginationPrevProps,
	emits: paginationPrevEmits,
	setup(__props) {
		const props = __props;
		const { t } = useLocale();
		const internalDisabled = computed(() => props.disabled || props.currentPage <= 1);
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("button", {
				type: "button",
				class: "btn-prev",
				disabled: internalDisabled.value,
				"aria-label": _ctx.prevText || unref(t)("el.pagination.prev"),
				"aria-disabled": internalDisabled.value,
				onClick: _cache[0] || (_cache[0] = ($event) => _ctx.$emit("click", $event))
			}, [_ctx.prevText ? (openBlock(), createElementBlock("span", _hoisted_2$2, toDisplayString(_ctx.prevText), 1)) : (openBlock(), createBlock(unref(ElIcon), { key: 1 }, {
				default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(_ctx.prevIcon)))]),
				_: 1
			}))], 8, _hoisted_1$4);
		};
	}
});
//#endregion
//#region node_modules/element-plus/es/components/pagination/src/components/next.mjs
var paginationNextProps = buildProps({
	disabled: Boolean,
	currentPage: {
		type: Number,
		default: 1
	},
	pageCount: {
		type: Number,
		default: 50
	},
	nextText: { type: String },
	nextIcon: { type: iconPropType }
});
//#endregion
//#region node_modules/element-plus/es/components/pagination/src/components/next.vue_vue_type_script_setup_true_lang.mjs
var _hoisted_1$3 = [
	"disabled",
	"aria-label",
	"aria-disabled"
];
var _hoisted_2$1 = { key: 0 };
//#endregion
//#region node_modules/element-plus/es/components/pagination/src/components/next2.mjs
var next_default = /* @__PURE__ */ defineComponent({
	name: "ElPaginationNext",
	__name: "next",
	props: paginationNextProps,
	emits: ["click"],
	setup(__props) {
		const props = __props;
		const { t } = useLocale();
		const internalDisabled = computed(() => props.disabled || props.currentPage === props.pageCount || props.pageCount === 0);
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("button", {
				type: "button",
				class: "btn-next",
				disabled: internalDisabled.value,
				"aria-label": _ctx.nextText || unref(t)("el.pagination.next"),
				"aria-disabled": internalDisabled.value,
				onClick: _cache[0] || (_cache[0] = ($event) => _ctx.$emit("click", $event))
			}, [_ctx.nextText ? (openBlock(), createElementBlock("span", _hoisted_2$1, toDisplayString(_ctx.nextText), 1)) : (openBlock(), createBlock(unref(ElIcon), { key: 1 }, {
				default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(_ctx.nextIcon)))]),
				_: 1
			}))], 8, _hoisted_1$3);
		};
	}
});
//#endregion
//#region node_modules/element-plus/es/components/pagination/src/usePagination.mjs
var usePagination = () => inject(elPaginationKey, {});
//#endregion
//#region node_modules/element-plus/es/components/pagination/src/components/sizes.mjs
var paginationSizesProps = buildProps({
	pageSize: {
		type: Number,
		required: true
	},
	pageSizes: {
		type: definePropType(Array),
		default: () => mutable([
			10,
			20,
			30,
			40,
			50,
			100
		])
	},
	popperClass: { type: String },
	popperStyle: { type: definePropType([String, Object]) },
	disabled: Boolean,
	teleported: Boolean,
	size: {
		type: String,
		values: componentSizes
	},
	appendSizeTo: String
});
//#endregion
//#region node_modules/element-plus/es/components/pagination/src/components/sizes2.mjs
var sizes_default = /* @__PURE__ */ defineComponent({
	name: "ElPaginationSizes",
	__name: "sizes",
	props: paginationSizesProps,
	emits: ["page-size-change"],
	setup(__props, { emit: __emit }) {
		const props = __props;
		const emit = __emit;
		const { t } = useLocale();
		const ns = useNamespace("pagination");
		const pagination = usePagination();
		const innerPageSize = ref(props.pageSize);
		watch(() => props.pageSizes, (newVal, oldVal) => {
			if (isEqual(newVal, oldVal)) return;
			if (isArray(newVal)) emit("page-size-change", newVal.includes(props.pageSize) ? props.pageSize : props.pageSizes[0]);
		});
		watch(() => props.pageSize, (newVal) => {
			innerPageSize.value = newVal;
		});
		const innerPageSizes = computed(() => props.pageSizes);
		function handleChange(val) {
			if (val !== innerPageSize.value) {
				innerPageSize.value = val;
				pagination.handleSizeChange?.(Number(val));
			}
		}
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("span", { class: normalizeClass(unref(ns).e("sizes")) }, [createVNode(unref(ElSelect), {
				"model-value": innerPageSize.value,
				disabled: _ctx.disabled,
				"popper-class": _ctx.popperClass,
				"popper-style": _ctx.popperStyle,
				size: _ctx.size,
				teleported: _ctx.teleported,
				"validate-event": false,
				"append-to": _ctx.appendSizeTo,
				onChange: handleChange
			}, {
				default: withCtx(() => [(openBlock(true), createElementBlock(Fragment, null, renderList(innerPageSizes.value, (item) => {
					return openBlock(), createBlock(unref(ElOption), {
						key: item,
						value: item,
						label: item + unref(t)("el.pagination.pagesize")
					}, null, 8, ["value", "label"]);
				}), 128))]),
				_: 1
			}, 8, [
				"model-value",
				"disabled",
				"popper-class",
				"popper-style",
				"size",
				"teleported",
				"append-to"
			])], 2);
		};
	}
});
//#endregion
//#region node_modules/element-plus/es/components/pagination/src/components/jumper.mjs
var paginationJumperProps = buildProps({ size: {
	type: String,
	values: componentSizes
} });
//#endregion
//#region node_modules/element-plus/es/components/pagination/src/components/jumper.vue_vue_type_script_setup_true_lang.mjs
var _hoisted_1$2 = ["disabled"];
//#endregion
//#region node_modules/element-plus/es/components/pagination/src/components/jumper2.mjs
var jumper_default = /* @__PURE__ */ defineComponent({
	name: "ElPaginationJumper",
	__name: "jumper",
	props: paginationJumperProps,
	setup(__props) {
		const { t } = useLocale();
		const ns = useNamespace("pagination");
		const { pageCount, disabled, currentPage, changeEvent } = usePagination();
		const userInput = ref();
		const innerValue = computed(() => userInput.value ?? currentPage?.value);
		function handleInput(val) {
			userInput.value = val ? +val : "";
		}
		function handleChange(val) {
			val = Math.trunc(+val);
			changeEvent?.(val);
			userInput.value = void 0;
		}
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("span", {
				class: normalizeClass(unref(ns).e("jump")),
				disabled: unref(disabled)
			}, [
				createElementVNode("span", { class: normalizeClass([unref(ns).e("goto")]) }, toDisplayString(unref(t)("el.pagination.goto")), 3),
				createVNode(unref(ElInput), {
					size: _ctx.size,
					class: normalizeClass([unref(ns).e("editor"), unref(ns).is("in-pagination")]),
					min: 1,
					max: unref(pageCount),
					disabled: unref(disabled),
					"model-value": innerValue.value,
					"validate-event": false,
					"aria-label": unref(t)("el.pagination.page"),
					type: "number",
					"onUpdate:modelValue": handleInput,
					onChange: handleChange
				}, null, 8, [
					"size",
					"class",
					"max",
					"disabled",
					"model-value",
					"aria-label"
				]),
				createElementVNode("span", { class: normalizeClass([unref(ns).e("classifier")]) }, toDisplayString(unref(t)("el.pagination.pageClassifier")), 3)
			], 10, _hoisted_1$2);
		};
	}
});
//#endregion
//#region node_modules/element-plus/es/components/pagination/src/components/total.mjs
var paginationTotalProps = buildProps({ total: {
	type: Number,
	default: 1e3
} });
//#endregion
//#region node_modules/element-plus/es/components/pagination/src/components/total.vue_vue_type_script_setup_true_lang.mjs
var _hoisted_1$1 = ["disabled"];
//#endregion
//#region node_modules/element-plus/es/components/pagination/src/components/total2.mjs
var total_default = /* @__PURE__ */ defineComponent({
	name: "ElPaginationTotal",
	__name: "total",
	props: paginationTotalProps,
	setup(__props) {
		const { t } = useLocale();
		const ns = useNamespace("pagination");
		const { disabled } = usePagination();
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("span", {
				class: normalizeClass(unref(ns).e("total")),
				disabled: unref(disabled)
			}, toDisplayString(unref(t)("el.pagination.total", { total: _ctx.total })), 11, _hoisted_1$1);
		};
	}
});
//#endregion
//#region node_modules/element-plus/es/components/pagination/src/components/pager.mjs
var paginationPagerProps = buildProps({
	currentPage: {
		type: Number,
		default: 1
	},
	pageCount: {
		type: Number,
		required: true
	},
	pagerCount: {
		type: Number,
		default: 7
	},
	disabled: Boolean
});
//#endregion
//#region node_modules/element-plus/es/components/pagination/src/components/pager.vue_vue_type_script_setup_true_lang.mjs
var _hoisted_1 = [
	"aria-current",
	"aria-label",
	"tabindex"
];
var _hoisted_2 = ["tabindex", "aria-label"];
var _hoisted_3 = [
	"aria-current",
	"aria-label",
	"tabindex"
];
var _hoisted_4 = ["tabindex", "aria-label"];
var _hoisted_5 = [
	"aria-current",
	"aria-label",
	"tabindex"
];
//#endregion
//#region node_modules/element-plus/es/components/pagination/src/components/pager2.mjs
var pager_default = /* @__PURE__ */ defineComponent({
	name: "ElPaginationPager",
	__name: "pager",
	props: paginationPagerProps,
	emits: [CHANGE_EVENT],
	setup(__props, { emit: __emit }) {
		const props = __props;
		const emit = __emit;
		const nsPager = useNamespace("pager");
		const nsIcon = useNamespace("icon");
		const { t } = useLocale();
		const showPrevMore = ref(false);
		const showNextMore = ref(false);
		const quickPrevHover = ref(false);
		const quickNextHover = ref(false);
		const quickPrevFocus = ref(false);
		const quickNextFocus = ref(false);
		const pagers = computed(() => {
			const pagerCount = props.pagerCount;
			const halfPagerCount = (pagerCount - 1) / 2;
			const currentPage = Number(props.currentPage);
			const pageCount = Number(props.pageCount);
			let showPrevMore = false;
			let showNextMore = false;
			if (pageCount > pagerCount) {
				if (currentPage > pagerCount - halfPagerCount) showPrevMore = true;
				if (currentPage < pageCount - halfPagerCount) showNextMore = true;
			}
			const array = [];
			if (showPrevMore && !showNextMore) {
				const startPage = pageCount - (pagerCount - 2);
				for (let i = startPage; i < pageCount; i++) array.push(i);
			} else if (!showPrevMore && showNextMore) for (let i = 2; i < pagerCount; i++) array.push(i);
			else if (showPrevMore && showNextMore) {
				const offset = Math.floor(pagerCount / 2) - 1;
				for (let i = currentPage - offset; i <= currentPage + offset; i++) array.push(i);
			} else for (let i = 2; i < pageCount; i++) array.push(i);
			return array;
		});
		const prevMoreKls = computed(() => [
			"more",
			"btn-quickprev",
			nsIcon.b(),
			nsPager.is("disabled", props.disabled)
		]);
		const nextMoreKls = computed(() => [
			"more",
			"btn-quicknext",
			nsIcon.b(),
			nsPager.is("disabled", props.disabled)
		]);
		const tabindex = computed(() => props.disabled ? -1 : 0);
		watch(() => [
			props.pageCount,
			props.pagerCount,
			props.currentPage
		], ([pageCount, pagerCount, currentPage]) => {
			const halfPagerCount = (pagerCount - 1) / 2;
			let showPrev = false;
			let showNext = false;
			if (pageCount > pagerCount) {
				showPrev = currentPage > pagerCount - halfPagerCount;
				showNext = currentPage < pageCount - halfPagerCount;
			}
			quickPrevHover.value &&= showPrev;
			quickNextHover.value &&= showNext;
			showPrevMore.value = showPrev;
			showNextMore.value = showNext;
		}, { immediate: true });
		function onMouseEnter(forward = false) {
			if (props.disabled) return;
			if (forward) quickPrevHover.value = true;
			else quickNextHover.value = true;
		}
		function onFocus(forward = false) {
			if (forward) quickPrevFocus.value = true;
			else quickNextFocus.value = true;
		}
		function onEnter(e) {
			const target = e.target;
			if (target.tagName.toLowerCase() === "li" && Array.from(target.classList).includes("number")) {
				const newPage = Number(target.textContent);
				if (newPage !== props.currentPage) emit(CHANGE_EVENT, newPage);
			} else if (target.tagName.toLowerCase() === "li" && Array.from(target.classList).includes("more")) onPagerClick(e);
		}
		function onPagerClick(event) {
			const target = event.target;
			if (target.tagName.toLowerCase() === "ul" || props.disabled) return;
			let newPage = Number(target.textContent);
			const pageCount = props.pageCount;
			const currentPage = props.currentPage;
			const pagerCountOffset = props.pagerCount - 2;
			if (target.className.includes("more")) {
				if (target.className.includes("quickprev")) newPage = currentPage - pagerCountOffset;
				else if (target.className.includes("quicknext")) newPage = currentPage + pagerCountOffset;
			}
			if (!Number.isNaN(+newPage)) {
				if (newPage < 1) newPage = 1;
				if (newPage > pageCount) newPage = pageCount;
			}
			if (newPage !== currentPage) emit(CHANGE_EVENT, newPage);
		}
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("ul", {
				class: normalizeClass(unref(nsPager).b()),
				onClick: onPagerClick,
				onKeyup: withKeys(onEnter, ["enter"])
			}, [
				_ctx.pageCount > 0 ? (openBlock(), createElementBlock("li", {
					key: 0,
					class: normalizeClass([[unref(nsPager).is("active", _ctx.currentPage === 1), unref(nsPager).is("disabled", _ctx.disabled)], "number"]),
					"aria-current": _ctx.currentPage === 1,
					"aria-label": unref(t)("el.pagination.currentPage", { pager: 1 }),
					tabindex: tabindex.value
				}, " 1 ", 10, _hoisted_1)) : createCommentVNode("v-if", true),
				showPrevMore.value ? (openBlock(), createElementBlock("li", {
					key: 1,
					class: normalizeClass(prevMoreKls.value),
					tabindex: tabindex.value,
					"aria-label": unref(t)("el.pagination.prevPages", { pager: _ctx.pagerCount - 2 }),
					onMouseenter: _cache[0] || (_cache[0] = ($event) => onMouseEnter(true)),
					onMouseleave: _cache[1] || (_cache[1] = ($event) => quickPrevHover.value = false),
					onFocus: _cache[2] || (_cache[2] = ($event) => onFocus(true)),
					onBlur: _cache[3] || (_cache[3] = ($event) => quickPrevFocus.value = false)
				}, [(quickPrevHover.value || quickPrevFocus.value) && !_ctx.disabled ? (openBlock(), createBlock(unref(d_arrow_left_default), { key: 0 })) : (openBlock(), createBlock(unref(more_filled_default), { key: 1 }))], 42, _hoisted_2)) : createCommentVNode("v-if", true),
				(openBlock(true), createElementBlock(Fragment, null, renderList(pagers.value, (pager) => {
					return openBlock(), createElementBlock("li", {
						key: pager,
						class: normalizeClass([[unref(nsPager).is("active", _ctx.currentPage === pager), unref(nsPager).is("disabled", _ctx.disabled)], "number"]),
						"aria-current": _ctx.currentPage === pager,
						"aria-label": unref(t)("el.pagination.currentPage", { pager }),
						tabindex: tabindex.value
					}, toDisplayString(pager), 11, _hoisted_3);
				}), 128)),
				showNextMore.value ? (openBlock(), createElementBlock("li", {
					key: 2,
					class: normalizeClass(nextMoreKls.value),
					tabindex: tabindex.value,
					"aria-label": unref(t)("el.pagination.nextPages", { pager: _ctx.pagerCount - 2 }),
					onMouseenter: _cache[4] || (_cache[4] = ($event) => onMouseEnter()),
					onMouseleave: _cache[5] || (_cache[5] = ($event) => quickNextHover.value = false),
					onFocus: _cache[6] || (_cache[6] = ($event) => onFocus()),
					onBlur: _cache[7] || (_cache[7] = ($event) => quickNextFocus.value = false)
				}, [(quickNextHover.value || quickNextFocus.value) && !_ctx.disabled ? (openBlock(), createBlock(unref(d_arrow_right_default), { key: 0 })) : (openBlock(), createBlock(unref(more_filled_default), { key: 1 }))], 42, _hoisted_4)) : createCommentVNode("v-if", true),
				_ctx.pageCount > 1 ? (openBlock(), createElementBlock("li", {
					key: 3,
					class: normalizeClass([[unref(nsPager).is("active", _ctx.currentPage === _ctx.pageCount), unref(nsPager).is("disabled", _ctx.disabled)], "number"]),
					"aria-current": _ctx.currentPage === _ctx.pageCount,
					"aria-label": unref(t)("el.pagination.currentPage", { pager: _ctx.pageCount }),
					tabindex: tabindex.value
				}, toDisplayString(_ctx.pageCount), 11, _hoisted_5)) : createCommentVNode("v-if", true)
			], 34);
		};
	}
});
//#endregion
//#region node_modules/element-plus/es/components/pagination/src/pagination.mjs
/**
* It it user's responsibility to guarantee that the value of props.total... is number
* (same as pageSize, defaultPageSize, currentPage, defaultCurrentPage, pageCount)
* Otherwise we can reasonable infer that the corresponding field is absent
*/
var isAbsent = (v) => typeof v !== "number";
var paginationProps = buildProps({
	/**
	* @description options of item count per page
	*/
	pageSize: Number,
	/**
	* @description default initial value of page size, not setting is the same as setting 10
	*/
	defaultPageSize: Number,
	/**
	* @description total item count
	*/
	total: Number,
	/**
	* @description total page count. Set either `total` or `page-count` and pages will be displayed; if you need `page-sizes`, `total` is required
	*/
	pageCount: Number,
	/**
	* @description number of pagers. Pagination collapses when the total page count exceeds this value
	*/
	pagerCount: {
		type: Number,
		validator: (value) => {
			return isNumber(value) && Math.trunc(value) === value && value > 4 && value < 22 && value % 2 === 1;
		},
		default: 7
	},
	/**
	* @description current page number
	*/
	currentPage: Number,
	/**
	* @description default initial value of current-page, not setting is the same as setting 1
	*/
	defaultCurrentPage: Number,
	/**
	* @description layout of Pagination, elements separated with a comma
	*/
	layout: {
		type: String,
		default: [
			"prev",
			"pager",
			"next",
			"jumper",
			"->",
			"total"
		].join(", ")
	},
	/**
	* @description item count of each page
	*/
	pageSizes: {
		type: definePropType(Array),
		default: () => mutable([
			10,
			20,
			30,
			40,
			50,
			100
		])
	},
	/**
	* @description custom class name for the page size Select's dropdown
	*/
	popperClass: {
		type: String,
		default: ""
	},
	/**
	* @description custom style for the page size Select's dropdown
	*/
	popperStyle: { type: definePropType([String, Object]) },
	/**
	* @description text for the prev button
	*/
	prevText: {
		type: String,
		default: ""
	},
	/**
	* @description icon for the prev button, higher priority of `prev-text`
	*/
	prevIcon: {
		type: iconPropType,
		default: () => arrow_left_default
	},
	/**
	* @description text for the next button
	*/
	nextText: {
		type: String,
		default: ""
	},
	/**
	* @description icon for the next button, higher priority of `next-text`
	*/
	nextIcon: {
		type: iconPropType,
		default: () => arrow_right_default
	},
	/**
	* @description whether Pagination size is teleported to body
	*/
	teleported: {
		type: Boolean,
		default: true
	},
	/**
	* @description whether to use small pagination
	*/
	small: Boolean,
	/**
	* @description set page size
	*/
	size: useSizeProp,
	/**
	* @description whether the buttons have a background color
	*/
	background: Boolean,
	/**
	* @description whether Pagination is disabled
	*/
	disabled: Boolean,
	/**
	* @description whether to hide when there's only one page
	*/
	hideOnSinglePage: Boolean,
	/**
	* @description which element the size dropdown appends to.
	*/
	appendSizeTo: String
});
var paginationEmits = {
	"update:current-page": (val) => isNumber(val),
	"update:page-size": (val) => isNumber(val),
	"size-change": (val) => isNumber(val),
	change: (currentPage, pageSize) => isNumber(currentPage) && isNumber(pageSize),
	"current-change": (val) => isNumber(val),
	"prev-click": (val) => isNumber(val),
	"next-click": (val) => isNumber(val)
};
var componentName = "ElPagination";
var pagination_default = defineComponent({
	name: componentName,
	props: paginationProps,
	emits: paginationEmits,
	setup(props, { emit, slots }) {
		const { t } = useLocale();
		const ns = useNamespace("pagination");
		const vnodeProps = getCurrentInstance().vnode.props || {};
		const _globalSize = useGlobalSize();
		const _size = computed(() => props.small ? "small" : props.size ?? _globalSize.value);
		useDeprecated({
			from: "small",
			replacement: "size",
			version: "3.0.0",
			scope: "el-pagination",
			ref: "https://element-plus.org/zh-CN/component/pagination.html"
		}, computed(() => !!props.small));
		const hasCurrentPageListener = "onUpdate:currentPage" in vnodeProps || "onUpdate:current-page" in vnodeProps || "onCurrentChange" in vnodeProps;
		const hasPageSizeListener = "onUpdate:pageSize" in vnodeProps || "onUpdate:page-size" in vnodeProps || "onSizeChange" in vnodeProps;
		const assertValidUsage = computed(() => {
			if (isAbsent(props.total) && isAbsent(props.pageCount)) return false;
			if (!isAbsent(props.currentPage) && !hasCurrentPageListener) return false;
			if (props.layout.includes("sizes")) {
				if (!isAbsent(props.pageCount)) {
					if (!hasPageSizeListener) return false;
				} else if (!isAbsent(props.total)) {
					if (!isAbsent(props.pageSize)) {
						if (!hasPageSizeListener) return false;
					}
				}
			}
			return true;
		});
		const innerPageSize = ref(isAbsent(props.defaultPageSize) ? 10 : props.defaultPageSize);
		const innerCurrentPage = ref(isAbsent(props.defaultCurrentPage) ? 1 : props.defaultCurrentPage);
		const pageSizeBridge = computed({
			get() {
				return isAbsent(props.pageSize) ? innerPageSize.value : props.pageSize;
			},
			set(v) {
				if (isAbsent(props.pageSize)) innerPageSize.value = v;
				if (hasPageSizeListener) {
					emit("update:page-size", v);
					emit("size-change", v);
				}
			}
		});
		const pageCountBridge = computed(() => {
			let pageCount = 0;
			if (!isAbsent(props.pageCount)) pageCount = props.pageCount;
			else if (!isAbsent(props.total)) pageCount = Math.max(1, Math.ceil(props.total / pageSizeBridge.value));
			return pageCount;
		});
		const currentPageBridge = computed({
			get() {
				return isAbsent(props.currentPage) ? innerCurrentPage.value : props.currentPage;
			},
			set(v) {
				let newCurrentPage = v;
				if (v < 1) newCurrentPage = 1;
				else if (v > pageCountBridge.value) newCurrentPage = pageCountBridge.value;
				if (isAbsent(props.currentPage)) innerCurrentPage.value = newCurrentPage;
				if (hasCurrentPageListener) {
					emit("update:current-page", newCurrentPage);
					emit("current-change", newCurrentPage);
				}
			}
		});
		watch(pageCountBridge, (val) => {
			if (currentPageBridge.value > val) currentPageBridge.value = val;
		});
		watch([currentPageBridge, pageSizeBridge], (value) => {
			emit(CHANGE_EVENT, ...value);
		}, { flush: "post" });
		function handleCurrentChange(val) {
			currentPageBridge.value = val;
		}
		function handleSizeChange(val) {
			pageSizeBridge.value = val;
			const newPageCount = pageCountBridge.value;
			if (currentPageBridge.value > newPageCount) currentPageBridge.value = newPageCount;
		}
		function prev() {
			if (props.disabled) return;
			currentPageBridge.value -= 1;
			emit("prev-click", currentPageBridge.value);
		}
		function next() {
			if (props.disabled) return;
			currentPageBridge.value += 1;
			emit("next-click", currentPageBridge.value);
		}
		function addClass(element, cls) {
			if (element) {
				if (!element.props) element.props = {};
				element.props.class = [element.props.class, cls].join(" ");
			}
		}
		provide(elPaginationKey, {
			pageCount: pageCountBridge,
			disabled: computed(() => props.disabled),
			currentPage: currentPageBridge,
			changeEvent: handleCurrentChange,
			handleSizeChange
		});
		return () => {
			if (!assertValidUsage.value) {
				debugWarn(componentName, t("el.pagination.deprecationWarning"));
				return null;
			}
			if (!props.layout) return null;
			if (props.hideOnSinglePage && pageCountBridge.value <= 1) return null;
			const rootChildren = [];
			const rightWrapperChildren = [];
			const rightWrapperRoot = h("div", { class: ns.e("rightwrapper") }, rightWrapperChildren);
			const TEMPLATE_MAP = {
				prev: h(prev_default, {
					disabled: props.disabled,
					currentPage: currentPageBridge.value,
					prevText: props.prevText,
					prevIcon: props.prevIcon,
					onClick: prev
				}),
				jumper: h(jumper_default, { size: _size.value }),
				pager: h(pager_default, {
					currentPage: currentPageBridge.value,
					pageCount: pageCountBridge.value,
					pagerCount: props.pagerCount,
					onChange: handleCurrentChange,
					disabled: props.disabled
				}),
				next: h(next_default, {
					disabled: props.disabled,
					currentPage: currentPageBridge.value,
					pageCount: pageCountBridge.value,
					nextText: props.nextText,
					nextIcon: props.nextIcon,
					onClick: next
				}),
				sizes: h(sizes_default, {
					pageSize: pageSizeBridge.value,
					pageSizes: props.pageSizes,
					popperClass: props.popperClass,
					popperStyle: props.popperStyle,
					disabled: props.disabled,
					teleported: props.teleported,
					size: _size.value,
					appendSizeTo: props.appendSizeTo
				}),
				slot: slots?.default?.() ?? null,
				total: h(total_default, { total: isAbsent(props.total) ? 0 : props.total })
			};
			const components = props.layout.split(",").map((item) => item.trim());
			let haveRightWrapper = false;
			components.forEach((c) => {
				if (c === "->") {
					haveRightWrapper = true;
					return;
				}
				if (!haveRightWrapper) rootChildren.push(TEMPLATE_MAP[c]);
				else rightWrapperChildren.push(TEMPLATE_MAP[c]);
			});
			addClass(rootChildren[0], ns.is("first"));
			addClass(rootChildren[rootChildren.length - 1], ns.is("last"));
			if (haveRightWrapper && rightWrapperChildren.length > 0) {
				addClass(rightWrapperChildren[0], ns.is("first"));
				addClass(rightWrapperChildren[rightWrapperChildren.length - 1], ns.is("last"));
				rootChildren.push(rightWrapperRoot);
			}
			return h("div", { class: [
				ns.b(),
				ns.is("background", props.background),
				ns.m(_size.value)
			] }, rootChildren);
		};
	}
});
//#endregion
//#region node_modules/element-plus/es/components/pagination/index.mjs
var ElPagination = withInstall(pagination_default);
//#endregion
//#region pages/questions/index.vue
var _sfc_main = {
	__name: "index",
	__ssrInlineRender: true,
	setup(__props) {
		const questions = ref([]);
		const loading = ref(false);
		const page = ref(1);
		const pageSize = ref(20);
		const total = ref(0);
		const searchQuery = ref("");
		const searchDomain = ref("");
		const dialogVisible = ref(false);
		const saving = ref(false);
		const isEdit = ref(false);
		const formRef = ref(null);
		const formData = ref({
			id: "",
			title: "",
			domain: "",
			categoryTags: "",
			corePoints: "",
			answerKey: ""
		});
		const rules = { title: [{
			required: true,
			message: "请输入标准问题",
			trigger: "blur"
		}] };
		const fetchQuestions = async () => {
			loading.value = true;
			try {
				const params = new URLSearchParams({
					page: page.value,
					pageSize: pageSize.value,
					keyword: searchQuery.value,
					domain: searchDomain.value
				});
				const res = await $fetch$1(`/api/questions?${params.toString()}`);
				if (res && res.success) {
					questions.value = res.data || [];
					total.value = res.total || 0;
				}
			} catch (error) {
				console.error("Failed to fetch questions:", error);
				ElMessage.error("加载题库失败");
			} finally {
				loading.value = false;
			}
		};
		const handleSearch = () => {
			page.value = 1;
			fetchQuestions();
		};
		const aiAnswer = async () => {
			if (!formData.value.title) {
				ElMessage.warning("请先输入标准问题");
				return;
			}
			const loadingMsg = ElMessage({
				message: "正在思考并结合简历生成答案，请稍候...",
				type: "info",
				duration: 0
			});
			try {
				const res = await $fetch$1("/api/questions/ai-answer", {
					method: "POST",
					body: {
						title: formData.value.title,
						corePoints: formData.value.corePoints
					}
				});
				if (res && res.success) {
					formData.value.aiAnswer = res.data;
					ElMessage.success("答案生成成功！");
				} else ElMessage.error(res?.error || "AI 回答生成失败");
			} catch (error) {
				console.error("AI generate failed:", error);
				ElMessage.error("AI 服务调用异常");
			} finally {
				loadingMsg.close();
			}
		};
		const openDialog = (row = null) => {
			if (row) {
				isEdit.value = true;
				formData.value = {
					id: row.id,
					title: row.title,
					domain: row.domain || "",
					categoryTags: row.categoryTags || "",
					corePoints: row.corePoints || "",
					answerKey: row.answerKey || "",
					aiAnswer: row.aiAnswer || ""
				};
			} else {
				isEdit.value = false;
				formData.value = {
					id: "",
					title: "",
					domain: "",
					categoryTags: "",
					corePoints: "",
					answerKey: "",
					aiAnswer: ""
				};
			}
			dialogVisible.value = true;
		};
		const saveQuestion = async () => {
			if (!formRef.value) return;
			await formRef.value.validate(async (valid) => {
				if (valid) {
					saving.value = true;
					try {
						const url = isEdit.value ? `/api/questions/${formData.value.id}` : "/api/questions";
						const method = isEdit.value ? "PUT" : "POST";
						const res = await $fetch$1(url, {
							method,
							body: {
								title: formData.value.title,
								domain: formData.value.domain,
								categoryTags: formData.value.categoryTags,
								corePoints: formData.value.corePoints,
								answerKey: formData.value.answerKey,
								aiAnswer: formData.value.aiAnswer
							}
						});
						if (res && res.success) {
							ElMessage.success(isEdit.value ? "更新成功" : "添加成功");
							dialogVisible.value = false;
							fetchQuestions();
						} else ElMessage.error(res?.error || "保存失败");
					} catch (error) {
						console.error("Save failed:", error);
						ElMessage.error("保存失败");
					} finally {
						saving.value = false;
					}
				}
			});
		};
		const deleteQuestion = async (id) => {
			try {
				const res = await $fetch$1(`/api/questions/${id}`, { method: "DELETE" });
				if (res && res.success) {
					ElMessage.success("删除成功");
					fetchQuestions();
				} else ElMessage.error(res?.error || "删除失败");
			} catch (error) {
				console.error("Delete failed:", error);
				ElMessage.error("删除失败");
			}
		};
		return (_ctx, _push, _parent, _attrs) => {
			const _component_el_card = ElCard;
			const _component_el_select = ElSelect;
			const _component_el_option = ElOption;
			const _component_el_input = ElInput;
			const _component_el_button = ElButton;
			const _component_el_table = ElTable;
			const _component_el_table_column = ElTableColumn;
			const _component_el_tag = ElTag;
			const _component_el_popconfirm = ElPopconfirm;
			const _component_el_pagination = ElPagination;
			const _component_el_drawer = ElDrawer;
			const _component_el_form = ElForm;
			const _component_el_form_item = ElFormItem;
			const _directive_loading = vLoading;
			_push(`<div${ssrRenderAttrs(mergeProps({ class: "questions-page" }, _attrs))} data-v-59baa5e4>`);
			_push(ssrRenderComponent(_component_el_card, {
				shadow: "never",
				class: "table-card"
			}, {
				header: withCtx((_, _push, _parent, _scopeId) => {
					if (_push) {
						_push(`<div class="card-header" data-v-59baa5e4${_scopeId}><h2 class="header-title" data-v-59baa5e4${_scopeId}><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" data-v-59baa5e4${_scopeId}><path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20" data-v-59baa5e4${_scopeId}></path><path d="M9.001 7.002H15" data-v-59baa5e4${_scopeId}></path><path d="M9.001 11.002H15" data-v-59baa5e4${_scopeId}></path></svg> 题库管理 </h2><div class="header-actions" data-v-59baa5e4${_scopeId}>`);
						_push(ssrRenderComponent(_component_el_select, {
							modelValue: searchDomain.value,
							"onUpdate:modelValue": ($event) => searchDomain.value = $event,
							placeholder: "按技术领域筛选...",
							clearable: "",
							style: { "width": "180px" },
							onChange: handleSearch
						}, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) {
									_push(ssrRenderComponent(_component_el_option, {
										label: "Java基础",
										value: "Java基础"
									}, null, _parent, _scopeId));
									_push(ssrRenderComponent(_component_el_option, {
										label: "Spring",
										value: "Spring"
									}, null, _parent, _scopeId));
									_push(ssrRenderComponent(_component_el_option, {
										label: "数据库",
										value: "数据库"
									}, null, _parent, _scopeId));
									_push(ssrRenderComponent(_component_el_option, {
										label: "前端",
										value: "前端"
									}, null, _parent, _scopeId));
									_push(ssrRenderComponent(_component_el_option, {
										label: "其他",
										value: "其他"
									}, null, _parent, _scopeId));
								} else return [
									createVNode(_component_el_option, {
										label: "Java基础",
										value: "Java基础"
									}),
									createVNode(_component_el_option, {
										label: "Spring",
										value: "Spring"
									}),
									createVNode(_component_el_option, {
										label: "数据库",
										value: "数据库"
									}),
									createVNode(_component_el_option, {
										label: "前端",
										value: "前端"
									}),
									createVNode(_component_el_option, {
										label: "其他",
										value: "其他"
									})
								];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(ssrRenderComponent(_component_el_input, {
							modelValue: searchQuery.value,
							"onUpdate:modelValue": ($event) => searchQuery.value = $event,
							placeholder: "搜索标准问题或变体题目...",
							style: { "width": "250px" },
							clearable: "",
							onKeyup: handleSearch,
							onClear: handleSearch
						}, null, _parent, _scopeId));
						_push(ssrRenderComponent(_component_el_button, {
							type: "primary",
							style: {
								"border": "none",
								"border-radius": "6px"
							},
							onClick: ($event) => openDialog()
						}, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) _push(` ➕ 新增题目 `);
								else return [createTextVNode(" ➕ 新增题目 ")];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(`</div></div>`);
					} else return [createVNode("div", { class: "card-header" }, [createVNode("h2", { class: "header-title" }, [(openBlock(), createBlock("svg", {
						xmlns: "http://www.w3.org/2000/svg",
						width: "24",
						height: "24",
						viewBox: "0 0 24 24",
						fill: "none",
						stroke: "currentColor",
						"stroke-width": "2",
						"stroke-linecap": "round",
						"stroke-linejoin": "round"
					}, [
						createVNode("path", { d: "M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20" }),
						createVNode("path", { d: "M9.001 7.002H15" }),
						createVNode("path", { d: "M9.001 11.002H15" })
					])), createTextVNode(" 题库管理 ")]), createVNode("div", { class: "header-actions" }, [
						createVNode(_component_el_select, {
							modelValue: searchDomain.value,
							"onUpdate:modelValue": ($event) => searchDomain.value = $event,
							placeholder: "按技术领域筛选...",
							clearable: "",
							style: { "width": "180px" },
							onChange: handleSearch
						}, {
							default: withCtx(() => [
								createVNode(_component_el_option, {
									label: "Java基础",
									value: "Java基础"
								}),
								createVNode(_component_el_option, {
									label: "Spring",
									value: "Spring"
								}),
								createVNode(_component_el_option, {
									label: "数据库",
									value: "数据库"
								}),
								createVNode(_component_el_option, {
									label: "前端",
									value: "前端"
								}),
								createVNode(_component_el_option, {
									label: "其他",
									value: "其他"
								})
							]),
							_: 1
						}, 8, ["modelValue", "onUpdate:modelValue"]),
						createVNode(_component_el_input, {
							modelValue: searchQuery.value,
							"onUpdate:modelValue": ($event) => searchQuery.value = $event,
							placeholder: "搜索标准问题或变体题目...",
							style: { "width": "250px" },
							clearable: "",
							onKeyup: withKeys(handleSearch, ["enter"]),
							onClear: handleSearch
						}, null, 8, ["modelValue", "onUpdate:modelValue"]),
						createVNode(_component_el_button, {
							type: "primary",
							style: {
								"border": "none",
								"border-radius": "6px"
							},
							onClick: ($event) => openDialog()
						}, {
							default: withCtx(() => [createTextVNode(" ➕ 新增题目 ")]),
							_: 1
						}, 8, ["onClick"])
					])])];
				}),
				default: withCtx((_, _push, _parent, _scopeId) => {
					if (_push) {
						_push(ssrRenderComponent(_component_el_table, mergeProps({
							data: questions.value,
							style: { "width": "100%" },
							"row-key": "id"
						}, ssrGetDirectiveProps(_ctx, _directive_loading, loading.value)), {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) {
									_push(ssrRenderComponent(_component_el_table_column, { type: "expand" }, {
										default: withCtx(({ row }, _push, _parent, _scopeId) => {
											if (_push) {
												_push(`<div class="expand-content" data-v-59baa5e4${_scopeId}><div class="core-points-section" style="${ssrRenderStyle({ "margin-bottom": "16px" })}" data-v-59baa5e4${_scopeId}><h4 data-v-59baa5e4${_scopeId}>核心考点：</h4><div class="answer-box" style="${ssrRenderStyle({ "background": "#fdfdfd" })}" data-v-59baa5e4${_scopeId}>`);
												if (row.corePoints) _push(`<p style="${ssrRenderStyle({
													"white-space": "pre-wrap",
													"margin": "0"
												})}" data-v-59baa5e4${_scopeId}>${ssrInterpolate(row.corePoints)}</p>`);
												else _push(`<p class="text-gray-400" style="${ssrRenderStyle({ "margin": "0" })}" data-v-59baa5e4${_scopeId}>暂无核心考点</p>`);
												_push(`</div></div><div class="answer-section" style="${ssrRenderStyle({ "margin-bottom": "16px" })}" data-v-59baa5e4${_scopeId}><h4 data-v-59baa5e4${_scopeId}>标准答案要点：</h4><div class="answer-box" data-v-59baa5e4${_scopeId}>`);
												if (row.answerKey) _push(`<p style="${ssrRenderStyle({
													"white-space": "pre-wrap",
													"margin": "0"
												})}" data-v-59baa5e4${_scopeId}>${ssrInterpolate(row.answerKey)}</p>`);
												else _push(`<p class="text-gray-400" style="${ssrRenderStyle({ "margin": "0" })}" data-v-59baa5e4${_scopeId}>暂无答案要点</p>`);
												_push(`</div></div>`);
												if (row.aiAnswer) _push(`<div class="ai-answer-section" style="${ssrRenderStyle({ "margin-bottom": "16px" })}" data-v-59baa5e4${_scopeId}><h4 data-v-59baa5e4${_scopeId}>🤖 AI 个性化解答 (结合简历)：</h4><div class="answer-box" style="${ssrRenderStyle({
													"background": "#f0f9eb",
													"border-left": "4px solid #67c23a"
												})}" data-v-59baa5e4${_scopeId}><p style="${ssrRenderStyle({
													"white-space": "pre-wrap",
													"margin": "0"
												})}" data-v-59baa5e4${_scopeId}>${ssrInterpolate(row.aiAnswer)}</p></div></div>`);
												else _push(`<!---->`);
												_push(`<div class="variants-section" data-v-59baa5e4${_scopeId}><h4 data-v-59baa5e4${_scopeId}>面试变体真题 (${ssrInterpolate(row.variants?.length || 0)})：</h4>`);
												if (row.variants?.length) _push(ssrRenderComponent(_component_el_table, {
													data: row.variants,
													size: "small",
													border: ""
												}, {
													default: withCtx((_, _push, _parent, _scopeId) => {
														if (_push) {
															_push(ssrRenderComponent(_component_el_table_column, {
																prop: "serialNo",
																label: "序号",
																width: "80",
																align: "center"
															}, null, _parent, _scopeId));
															_push(ssrRenderComponent(_component_el_table_column, {
																prop: "title",
																label: "面试题目"
															}, null, _parent, _scopeId));
														} else return [createVNode(_component_el_table_column, {
															prop: "serialNo",
															label: "序号",
															width: "80",
															align: "center"
														}), createVNode(_component_el_table_column, {
															prop: "title",
															label: "面试题目"
														})];
													}),
													_: 2
												}, _parent, _scopeId));
												else _push(`<div class="text-gray-400" style="${ssrRenderStyle({ "padding": "10px" })}" data-v-59baa5e4${_scopeId}>暂无收集到的真题变体</div>`);
												_push(`</div></div>`);
											} else return [createVNode("div", { class: "expand-content" }, [
												createVNode("div", {
													class: "core-points-section",
													style: { "margin-bottom": "16px" }
												}, [createVNode("h4", null, "核心考点："), createVNode("div", {
													class: "answer-box",
													style: { "background": "#fdfdfd" }
												}, [row.corePoints ? (openBlock(), createBlock("p", {
													key: 0,
													style: {
														"white-space": "pre-wrap",
														"margin": "0"
													}
												}, toDisplayString(row.corePoints), 1)) : (openBlock(), createBlock("p", {
													key: 1,
													class: "text-gray-400",
													style: { "margin": "0" }
												}, "暂无核心考点"))])]),
												createVNode("div", {
													class: "answer-section",
													style: { "margin-bottom": "16px" }
												}, [createVNode("h4", null, "标准答案要点："), createVNode("div", { class: "answer-box" }, [row.answerKey ? (openBlock(), createBlock("p", {
													key: 0,
													style: {
														"white-space": "pre-wrap",
														"margin": "0"
													}
												}, toDisplayString(row.answerKey), 1)) : (openBlock(), createBlock("p", {
													key: 1,
													class: "text-gray-400",
													style: { "margin": "0" }
												}, "暂无答案要点"))])]),
												row.aiAnswer ? (openBlock(), createBlock("div", {
													key: 0,
													class: "ai-answer-section",
													style: { "margin-bottom": "16px" }
												}, [createVNode("h4", null, "🤖 AI 个性化解答 (结合简历)："), createVNode("div", {
													class: "answer-box",
													style: {
														"background": "#f0f9eb",
														"border-left": "4px solid #67c23a"
													}
												}, [createVNode("p", { style: {
													"white-space": "pre-wrap",
													"margin": "0"
												} }, toDisplayString(row.aiAnswer), 1)])])) : createCommentVNode("", true),
												createVNode("div", { class: "variants-section" }, [createVNode("h4", null, "面试变体真题 (" + toDisplayString(row.variants?.length || 0) + ")：", 1), row.variants?.length ? (openBlock(), createBlock(_component_el_table, {
													key: 0,
													data: row.variants,
													size: "small",
													border: ""
												}, {
													default: withCtx(() => [createVNode(_component_el_table_column, {
														prop: "serialNo",
														label: "序号",
														width: "80",
														align: "center"
													}), createVNode(_component_el_table_column, {
														prop: "title",
														label: "面试题目"
													})]),
													_: 1
												}, 8, ["data"])) : (openBlock(), createBlock("div", {
													key: 1,
													class: "text-gray-400",
													style: { "padding": "10px" }
												}, "暂无收集到的真题变体"))])
											])];
										}),
										_: 1
									}, _parent, _scopeId));
									_push(ssrRenderComponent(_component_el_table_column, {
										prop: "title",
										label: "标准问题",
										"min-width": "250"
									}, {
										default: withCtx(({ row }, _push, _parent, _scopeId) => {
											if (_push) _push(`<div class="question-title" data-v-59baa5e4${_scopeId}>${ssrInterpolate(row.title)}</div>`);
											else return [createVNode("div", { class: "question-title" }, toDisplayString(row.title), 1)];
										}),
										_: 1
									}, _parent, _scopeId));
									_push(ssrRenderComponent(_component_el_table_column, {
										prop: "domain",
										label: "技术领域",
										width: "140"
									}, {
										default: withCtx(({ row }, _push, _parent, _scopeId) => {
											if (_push) _push(`<span style="${ssrRenderStyle({ "color": "#333" })}" data-v-59baa5e4${_scopeId}>${ssrInterpolate(row.domain || "-")}</span>`);
											else return [createVNode("span", { style: { "color": "#333" } }, toDisplayString(row.domain || "-"), 1)];
										}),
										_: 1
									}, _parent, _scopeId));
									_push(ssrRenderComponent(_component_el_table_column, {
										prop: "categoryTags",
										label: "分类标签",
										"min-width": "180"
									}, {
										default: withCtx(({ row }, _push, _parent, _scopeId) => {
											if (_push) if (row.categoryTags) {
												_push(`<div style="${ssrRenderStyle({
													"display": "flex",
													"flex-wrap": "wrap",
													"gap": "4px"
												})}" data-v-59baa5e4${_scopeId}><!--[-->`);
												ssrRenderList(row.categoryTags.split("/").filter((t) => t.trim() !== ""), (tag, idx) => {
													_push(ssrRenderComponent(_component_el_tag, {
														key: idx,
														size: "small",
														type: "info",
														"disable-transitions": ""
													}, {
														default: withCtx((_, _push, _parent, _scopeId) => {
															if (_push) _push(`${ssrInterpolate(tag.trim())}`);
															else return [createTextVNode(toDisplayString(tag.trim()), 1)];
														}),
														_: 2
													}, _parent, _scopeId));
												});
												_push(`<!--]--></div>`);
											} else _push(`<span class="text-gray-400" data-v-59baa5e4${_scopeId}>-</span>`);
											else return [row.categoryTags ? (openBlock(), createBlock("div", {
												key: 0,
												style: {
													"display": "flex",
													"flex-wrap": "wrap",
													"gap": "4px"
												}
											}, [(openBlock(true), createBlock(Fragment, null, renderList(row.categoryTags.split("/").filter((t) => t.trim() !== ""), (tag, idx) => {
												return openBlock(), createBlock(_component_el_tag, {
													key: idx,
													size: "small",
													type: "info",
													"disable-transitions": ""
												}, {
													default: withCtx(() => [createTextVNode(toDisplayString(tag.trim()), 1)]),
													_: 2
												}, 1024);
											}), 128))])) : (openBlock(), createBlock("span", {
												key: 1,
												class: "text-gray-400"
											}, "-"))];
										}),
										_: 1
									}, _parent, _scopeId));
									_push(ssrRenderComponent(_component_el_table_column, {
										label: "考察频次",
										width: "100",
										align: "center"
									}, {
										default: withCtx(({ row }, _push, _parent, _scopeId) => {
											if (_push) _push(ssrRenderComponent(_component_el_tag, {
												type: (row.variants?.length || 0) >= 5 ? "danger" : (row.variants?.length || 0) >= 2 ? "warning" : "info",
												size: "small"
											}, {
												default: withCtx((_, _push, _parent, _scopeId) => {
													if (_push) _push(`${ssrInterpolate(row.variants?.length || 0)} 次 `);
													else return [createTextVNode(toDisplayString(row.variants?.length || 0) + " 次 ", 1)];
												}),
												_: 2
											}, _parent, _scopeId));
											else return [createVNode(_component_el_tag, {
												type: (row.variants?.length || 0) >= 5 ? "danger" : (row.variants?.length || 0) >= 2 ? "warning" : "info",
												size: "small"
											}, {
												default: withCtx(() => [createTextVNode(toDisplayString(row.variants?.length || 0) + " 次 ", 1)]),
												_: 2
											}, 1032, ["type"])];
										}),
										_: 1
									}, _parent, _scopeId));
									_push(ssrRenderComponent(_component_el_table_column, {
										label: "操作",
										width: "150",
										fixed: "right"
									}, {
										default: withCtx(({ row }, _push, _parent, _scopeId) => {
											if (_push) {
												_push(ssrRenderComponent(_component_el_button, {
													link: "",
													type: "primary",
													size: "small",
													onClick: ($event) => openDialog(row)
												}, {
													default: withCtx((_, _push, _parent, _scopeId) => {
														if (_push) _push(`编辑`);
														else return [createTextVNode("编辑")];
													}),
													_: 2
												}, _parent, _scopeId));
												_push(ssrRenderComponent(_component_el_popconfirm, {
													title: "确定要删除这道题及所有变体吗？",
													onConfirm: ($event) => deleteQuestion(row.id)
												}, {
													reference: withCtx((_, _push, _parent, _scopeId) => {
														if (_push) _push(ssrRenderComponent(_component_el_button, {
															link: "",
															type: "danger",
															size: "small"
														}, {
															default: withCtx((_, _push, _parent, _scopeId) => {
																if (_push) _push(`删除`);
																else return [createTextVNode("删除")];
															}),
															_: 2
														}, _parent, _scopeId));
														else return [createVNode(_component_el_button, {
															link: "",
															type: "danger",
															size: "small"
														}, {
															default: withCtx(() => [createTextVNode("删除")]),
															_: 1
														})];
													}),
													_: 2
												}, _parent, _scopeId));
											} else return [createVNode(_component_el_button, {
												link: "",
												type: "primary",
												size: "small",
												onClick: ($event) => openDialog(row)
											}, {
												default: withCtx(() => [createTextVNode("编辑")]),
												_: 1
											}, 8, ["onClick"]), createVNode(_component_el_popconfirm, {
												title: "确定要删除这道题及所有变体吗？",
												onConfirm: ($event) => deleteQuestion(row.id)
											}, {
												reference: withCtx(() => [createVNode(_component_el_button, {
													link: "",
													type: "danger",
													size: "small"
												}, {
													default: withCtx(() => [createTextVNode("删除")]),
													_: 1
												})]),
												_: 1
											}, 8, ["onConfirm"])];
										}),
										_: 1
									}, _parent, _scopeId));
								} else return [
									createVNode(_component_el_table_column, { type: "expand" }, {
										default: withCtx(({ row }) => [createVNode("div", { class: "expand-content" }, [
											createVNode("div", {
												class: "core-points-section",
												style: { "margin-bottom": "16px" }
											}, [createVNode("h4", null, "核心考点："), createVNode("div", {
												class: "answer-box",
												style: { "background": "#fdfdfd" }
											}, [row.corePoints ? (openBlock(), createBlock("p", {
												key: 0,
												style: {
													"white-space": "pre-wrap",
													"margin": "0"
												}
											}, toDisplayString(row.corePoints), 1)) : (openBlock(), createBlock("p", {
												key: 1,
												class: "text-gray-400",
												style: { "margin": "0" }
											}, "暂无核心考点"))])]),
											createVNode("div", {
												class: "answer-section",
												style: { "margin-bottom": "16px" }
											}, [createVNode("h4", null, "标准答案要点："), createVNode("div", { class: "answer-box" }, [row.answerKey ? (openBlock(), createBlock("p", {
												key: 0,
												style: {
													"white-space": "pre-wrap",
													"margin": "0"
												}
											}, toDisplayString(row.answerKey), 1)) : (openBlock(), createBlock("p", {
												key: 1,
												class: "text-gray-400",
												style: { "margin": "0" }
											}, "暂无答案要点"))])]),
											row.aiAnswer ? (openBlock(), createBlock("div", {
												key: 0,
												class: "ai-answer-section",
												style: { "margin-bottom": "16px" }
											}, [createVNode("h4", null, "🤖 AI 个性化解答 (结合简历)："), createVNode("div", {
												class: "answer-box",
												style: {
													"background": "#f0f9eb",
													"border-left": "4px solid #67c23a"
												}
											}, [createVNode("p", { style: {
												"white-space": "pre-wrap",
												"margin": "0"
											} }, toDisplayString(row.aiAnswer), 1)])])) : createCommentVNode("", true),
											createVNode("div", { class: "variants-section" }, [createVNode("h4", null, "面试变体真题 (" + toDisplayString(row.variants?.length || 0) + ")：", 1), row.variants?.length ? (openBlock(), createBlock(_component_el_table, {
												key: 0,
												data: row.variants,
												size: "small",
												border: ""
											}, {
												default: withCtx(() => [createVNode(_component_el_table_column, {
													prop: "serialNo",
													label: "序号",
													width: "80",
													align: "center"
												}), createVNode(_component_el_table_column, {
													prop: "title",
													label: "面试题目"
												})]),
												_: 1
											}, 8, ["data"])) : (openBlock(), createBlock("div", {
												key: 1,
												class: "text-gray-400",
												style: { "padding": "10px" }
											}, "暂无收集到的真题变体"))])
										])]),
										_: 1
									}),
									createVNode(_component_el_table_column, {
										prop: "title",
										label: "标准问题",
										"min-width": "250"
									}, {
										default: withCtx(({ row }) => [createVNode("div", { class: "question-title" }, toDisplayString(row.title), 1)]),
										_: 1
									}),
									createVNode(_component_el_table_column, {
										prop: "domain",
										label: "技术领域",
										width: "140"
									}, {
										default: withCtx(({ row }) => [createVNode("span", { style: { "color": "#333" } }, toDisplayString(row.domain || "-"), 1)]),
										_: 1
									}),
									createVNode(_component_el_table_column, {
										prop: "categoryTags",
										label: "分类标签",
										"min-width": "180"
									}, {
										default: withCtx(({ row }) => [row.categoryTags ? (openBlock(), createBlock("div", {
											key: 0,
											style: {
												"display": "flex",
												"flex-wrap": "wrap",
												"gap": "4px"
											}
										}, [(openBlock(true), createBlock(Fragment, null, renderList(row.categoryTags.split("/").filter((t) => t.trim() !== ""), (tag, idx) => {
											return openBlock(), createBlock(_component_el_tag, {
												key: idx,
												size: "small",
												type: "info",
												"disable-transitions": ""
											}, {
												default: withCtx(() => [createTextVNode(toDisplayString(tag.trim()), 1)]),
												_: 2
											}, 1024);
										}), 128))])) : (openBlock(), createBlock("span", {
											key: 1,
											class: "text-gray-400"
										}, "-"))]),
										_: 1
									}),
									createVNode(_component_el_table_column, {
										label: "考察频次",
										width: "100",
										align: "center"
									}, {
										default: withCtx(({ row }) => [createVNode(_component_el_tag, {
											type: (row.variants?.length || 0) >= 5 ? "danger" : (row.variants?.length || 0) >= 2 ? "warning" : "info",
											size: "small"
										}, {
											default: withCtx(() => [createTextVNode(toDisplayString(row.variants?.length || 0) + " 次 ", 1)]),
											_: 2
										}, 1032, ["type"])]),
										_: 1
									}),
									createVNode(_component_el_table_column, {
										label: "操作",
										width: "150",
										fixed: "right"
									}, {
										default: withCtx(({ row }) => [createVNode(_component_el_button, {
											link: "",
											type: "primary",
											size: "small",
											onClick: ($event) => openDialog(row)
										}, {
											default: withCtx(() => [createTextVNode("编辑")]),
											_: 1
										}, 8, ["onClick"]), createVNode(_component_el_popconfirm, {
											title: "确定要删除这道题及所有变体吗？",
											onConfirm: ($event) => deleteQuestion(row.id)
										}, {
											reference: withCtx(() => [createVNode(_component_el_button, {
												link: "",
												type: "danger",
												size: "small"
											}, {
												default: withCtx(() => [createTextVNode("删除")]),
												_: 1
											})]),
											_: 1
										}, 8, ["onConfirm"])]),
										_: 1
									})
								];
							}),
							_: 1
						}, _parent, _scopeId));
						if (total.value > 0) {
							_push(`<div class="pagination-container" data-v-59baa5e4${_scopeId}>`);
							_push(ssrRenderComponent(_component_el_pagination, {
								"current-page": page.value,
								"onUpdate:currentPage": ($event) => page.value = $event,
								"page-size": pageSize.value,
								"onUpdate:pageSize": ($event) => pageSize.value = $event,
								"page-sizes": [
									10,
									20,
									50
								],
								total: total.value,
								layout: "total, sizes, prev, pager, next, jumper",
								onSizeChange: fetchQuestions,
								onCurrentChange: fetchQuestions
							}, null, _parent, _scopeId));
							_push(`</div>`);
						} else _push(`<div class="empty-state" data-v-59baa5e4${_scopeId}><p data-v-59baa5e4${_scopeId}>题库为空，请先执行导入脚本或手动添加题目。</p></div>`);
					} else return [withDirectives((openBlock(), createBlock(_component_el_table, {
						data: questions.value,
						style: { "width": "100%" },
						"row-key": "id"
					}, {
						default: withCtx(() => [
							createVNode(_component_el_table_column, { type: "expand" }, {
								default: withCtx(({ row }) => [createVNode("div", { class: "expand-content" }, [
									createVNode("div", {
										class: "core-points-section",
										style: { "margin-bottom": "16px" }
									}, [createVNode("h4", null, "核心考点："), createVNode("div", {
										class: "answer-box",
										style: { "background": "#fdfdfd" }
									}, [row.corePoints ? (openBlock(), createBlock("p", {
										key: 0,
										style: {
											"white-space": "pre-wrap",
											"margin": "0"
										}
									}, toDisplayString(row.corePoints), 1)) : (openBlock(), createBlock("p", {
										key: 1,
										class: "text-gray-400",
										style: { "margin": "0" }
									}, "暂无核心考点"))])]),
									createVNode("div", {
										class: "answer-section",
										style: { "margin-bottom": "16px" }
									}, [createVNode("h4", null, "标准答案要点："), createVNode("div", { class: "answer-box" }, [row.answerKey ? (openBlock(), createBlock("p", {
										key: 0,
										style: {
											"white-space": "pre-wrap",
											"margin": "0"
										}
									}, toDisplayString(row.answerKey), 1)) : (openBlock(), createBlock("p", {
										key: 1,
										class: "text-gray-400",
										style: { "margin": "0" }
									}, "暂无答案要点"))])]),
									row.aiAnswer ? (openBlock(), createBlock("div", {
										key: 0,
										class: "ai-answer-section",
										style: { "margin-bottom": "16px" }
									}, [createVNode("h4", null, "🤖 AI 个性化解答 (结合简历)："), createVNode("div", {
										class: "answer-box",
										style: {
											"background": "#f0f9eb",
											"border-left": "4px solid #67c23a"
										}
									}, [createVNode("p", { style: {
										"white-space": "pre-wrap",
										"margin": "0"
									} }, toDisplayString(row.aiAnswer), 1)])])) : createCommentVNode("", true),
									createVNode("div", { class: "variants-section" }, [createVNode("h4", null, "面试变体真题 (" + toDisplayString(row.variants?.length || 0) + ")：", 1), row.variants?.length ? (openBlock(), createBlock(_component_el_table, {
										key: 0,
										data: row.variants,
										size: "small",
										border: ""
									}, {
										default: withCtx(() => [createVNode(_component_el_table_column, {
											prop: "serialNo",
											label: "序号",
											width: "80",
											align: "center"
										}), createVNode(_component_el_table_column, {
											prop: "title",
											label: "面试题目"
										})]),
										_: 1
									}, 8, ["data"])) : (openBlock(), createBlock("div", {
										key: 1,
										class: "text-gray-400",
										style: { "padding": "10px" }
									}, "暂无收集到的真题变体"))])
								])]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								prop: "title",
								label: "标准问题",
								"min-width": "250"
							}, {
								default: withCtx(({ row }) => [createVNode("div", { class: "question-title" }, toDisplayString(row.title), 1)]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								prop: "domain",
								label: "技术领域",
								width: "140"
							}, {
								default: withCtx(({ row }) => [createVNode("span", { style: { "color": "#333" } }, toDisplayString(row.domain || "-"), 1)]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								prop: "categoryTags",
								label: "分类标签",
								"min-width": "180"
							}, {
								default: withCtx(({ row }) => [row.categoryTags ? (openBlock(), createBlock("div", {
									key: 0,
									style: {
										"display": "flex",
										"flex-wrap": "wrap",
										"gap": "4px"
									}
								}, [(openBlock(true), createBlock(Fragment, null, renderList(row.categoryTags.split("/").filter((t) => t.trim() !== ""), (tag, idx) => {
									return openBlock(), createBlock(_component_el_tag, {
										key: idx,
										size: "small",
										type: "info",
										"disable-transitions": ""
									}, {
										default: withCtx(() => [createTextVNode(toDisplayString(tag.trim()), 1)]),
										_: 2
									}, 1024);
								}), 128))])) : (openBlock(), createBlock("span", {
									key: 1,
									class: "text-gray-400"
								}, "-"))]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "考察频次",
								width: "100",
								align: "center"
							}, {
								default: withCtx(({ row }) => [createVNode(_component_el_tag, {
									type: (row.variants?.length || 0) >= 5 ? "danger" : (row.variants?.length || 0) >= 2 ? "warning" : "info",
									size: "small"
								}, {
									default: withCtx(() => [createTextVNode(toDisplayString(row.variants?.length || 0) + " 次 ", 1)]),
									_: 2
								}, 1032, ["type"])]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "操作",
								width: "150",
								fixed: "right"
							}, {
								default: withCtx(({ row }) => [createVNode(_component_el_button, {
									link: "",
									type: "primary",
									size: "small",
									onClick: ($event) => openDialog(row)
								}, {
									default: withCtx(() => [createTextVNode("编辑")]),
									_: 1
								}, 8, ["onClick"]), createVNode(_component_el_popconfirm, {
									title: "确定要删除这道题及所有变体吗？",
									onConfirm: ($event) => deleteQuestion(row.id)
								}, {
									reference: withCtx(() => [createVNode(_component_el_button, {
										link: "",
										type: "danger",
										size: "small"
									}, {
										default: withCtx(() => [createTextVNode("删除")]),
										_: 1
									})]),
									_: 1
								}, 8, ["onConfirm"])]),
								_: 1
							})
						]),
						_: 1
					}, 8, ["data"])), [[_directive_loading, loading.value]]), total.value > 0 ? (openBlock(), createBlock("div", {
						key: 0,
						class: "pagination-container"
					}, [createVNode(_component_el_pagination, {
						"current-page": page.value,
						"onUpdate:currentPage": ($event) => page.value = $event,
						"page-size": pageSize.value,
						"onUpdate:pageSize": ($event) => pageSize.value = $event,
						"page-sizes": [
							10,
							20,
							50
						],
						total: total.value,
						layout: "total, sizes, prev, pager, next, jumper",
						onSizeChange: fetchQuestions,
						onCurrentChange: fetchQuestions
					}, null, 8, [
						"current-page",
						"onUpdate:currentPage",
						"page-size",
						"onUpdate:pageSize",
						"total"
					])])) : (openBlock(), createBlock("div", {
						key: 1,
						class: "empty-state"
					}, [createVNode("p", null, "题库为空，请先执行导入脚本或手动添加题目。")]))];
				}),
				_: 1
			}, _parent));
			_push(ssrRenderComponent(_component_el_drawer, {
				modelValue: dialogVisible.value,
				"onUpdate:modelValue": ($event) => dialogVisible.value = $event,
				title: isEdit.value ? "编辑标准问题" : "新增标准问题",
				size: "800px",
				"destroy-on-close": "",
				"custom-class": "qb-drawer"
			}, {
				footer: withCtx((_, _push, _parent, _scopeId) => {
					if (_push) {
						_push(`<span class="dialog-footer" style="${ssrRenderStyle({
							"display": "flex",
							"justify-content": "flex-end",
							"gap": "12px"
						})}" data-v-59baa5e4${_scopeId}>`);
						_push(ssrRenderComponent(_component_el_button, { onClick: ($event) => dialogVisible.value = false }, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) _push(`取消`);
								else return [createTextVNode("取消")];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(ssrRenderComponent(_component_el_button, {
							type: "primary",
							onClick: saveQuestion,
							loading: saving.value
						}, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) _push(`保存`);
								else return [createTextVNode("保存")];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(`</span>`);
					} else return [createVNode("span", {
						class: "dialog-footer",
						style: {
							"display": "flex",
							"justify-content": "flex-end",
							"gap": "12px"
						}
					}, [createVNode(_component_el_button, { onClick: ($event) => dialogVisible.value = false }, {
						default: withCtx(() => [createTextVNode("取消")]),
						_: 1
					}, 8, ["onClick"]), createVNode(_component_el_button, {
						type: "primary",
						onClick: saveQuestion,
						loading: saving.value
					}, {
						default: withCtx(() => [createTextVNode("保存")]),
						_: 1
					}, 8, ["loading"])])];
				}),
				default: withCtx((_, _push, _parent, _scopeId) => {
					if (_push) _push(ssrRenderComponent(_component_el_form, {
						model: formData.value,
						rules,
						ref_key: "formRef",
						ref: formRef,
						"label-position": "top"
					}, {
						default: withCtx((_, _push, _parent, _scopeId) => {
							if (_push) {
								_push(ssrRenderComponent(_component_el_form_item, {
									label: "标准问题",
									prop: "title"
								}, {
									default: withCtx((_, _push, _parent, _scopeId) => {
										if (_push) _push(ssrRenderComponent(_component_el_input, {
											modelValue: formData.value.title,
											"onUpdate:modelValue": ($event) => formData.value.title = $event,
											type: "textarea",
											rows: 2,
											placeholder: "请输入标准问题...",
											style: { "font-size": "16px" }
										}, null, _parent, _scopeId));
										else return [createVNode(_component_el_input, {
											modelValue: formData.value.title,
											"onUpdate:modelValue": ($event) => formData.value.title = $event,
											type: "textarea",
											rows: 2,
											placeholder: "请输入标准问题...",
											style: { "font-size": "16px" }
										}, null, 8, ["modelValue", "onUpdate:modelValue"])];
									}),
									_: 1
								}, _parent, _scopeId));
								_push(`<div style="${ssrRenderStyle({
									"display": "flex",
									"gap": "12px"
								})}" data-v-59baa5e4${_scopeId}>`);
								_push(ssrRenderComponent(_component_el_form_item, {
									label: "技术领域",
									prop: "domain",
									style: { "flex": "1" }
								}, {
									default: withCtx((_, _push, _parent, _scopeId) => {
										if (_push) _push(ssrRenderComponent(_component_el_input, {
											modelValue: formData.value.domain,
											"onUpdate:modelValue": ($event) => formData.value.domain = $event,
											placeholder: "例如: Java基础"
										}, null, _parent, _scopeId));
										else return [createVNode(_component_el_input, {
											modelValue: formData.value.domain,
											"onUpdate:modelValue": ($event) => formData.value.domain = $event,
											placeholder: "例如: Java基础"
										}, null, 8, ["modelValue", "onUpdate:modelValue"])];
									}),
									_: 1
								}, _parent, _scopeId));
								_push(ssrRenderComponent(_component_el_form_item, {
									label: "分类标签",
									prop: "categoryTags",
									style: { "flex": "1" }
								}, {
									default: withCtx((_, _push, _parent, _scopeId) => {
										if (_push) _push(ssrRenderComponent(_component_el_input, {
											modelValue: formData.value.categoryTags,
											"onUpdate:modelValue": ($event) => formData.value.categoryTags = $event,
											placeholder: "例如: Java/面向对象"
										}, null, _parent, _scopeId));
										else return [createVNode(_component_el_input, {
											modelValue: formData.value.categoryTags,
											"onUpdate:modelValue": ($event) => formData.value.categoryTags = $event,
											placeholder: "例如: Java/面向对象"
										}, null, 8, ["modelValue", "onUpdate:modelValue"])];
									}),
									_: 1
								}, _parent, _scopeId));
								_push(`</div>`);
								_push(ssrRenderComponent(_component_el_form_item, {
									label: "核心考点",
									prop: "corePoints"
								}, {
									default: withCtx((_, _push, _parent, _scopeId) => {
										if (_push) _push(ssrRenderComponent(_component_el_input, {
											modelValue: formData.value.corePoints,
											"onUpdate:modelValue": ($event) => formData.value.corePoints = $event,
											placeholder: "例如: 接口 vs 抽象类、单继承多实现"
										}, null, _parent, _scopeId));
										else return [createVNode(_component_el_input, {
											modelValue: formData.value.corePoints,
											"onUpdate:modelValue": ($event) => formData.value.corePoints = $event,
											placeholder: "例如: 接口 vs 抽象类、单继承多实现"
										}, null, 8, ["modelValue", "onUpdate:modelValue"])];
									}),
									_: 1
								}, _parent, _scopeId));
								_push(ssrRenderComponent(_component_el_form_item, { label: "标准答案要点" }, {
									default: withCtx((_, _push, _parent, _scopeId) => {
										if (_push) _push(ssrRenderComponent(_component_el_input, {
											modelValue: formData.value.answerKey,
											"onUpdate:modelValue": ($event) => formData.value.answerKey = $event,
											type: "textarea",
											rows: 4,
											placeholder: "在此输入标准答案的答题要点..."
										}, null, _parent, _scopeId));
										else return [createVNode(_component_el_input, {
											modelValue: formData.value.answerKey,
											"onUpdate:modelValue": ($event) => formData.value.answerKey = $event,
											type: "textarea",
											rows: 4,
											placeholder: "在此输入标准答案的答题要点..."
										}, null, 8, ["modelValue", "onUpdate:modelValue"])];
									}),
									_: 1
								}, _parent, _scopeId));
								_push(ssrRenderComponent(_component_el_form_item, null, {
									label: withCtx((_, _push, _parent, _scopeId) => {
										if (_push) {
											_push(`<div style="${ssrRenderStyle({
												"display": "flex",
												"justify-content": "space-between",
												"align-items": "center",
												"width": "100%"
											})}" data-v-59baa5e4${_scopeId}><span data-v-59baa5e4${_scopeId}>🤖 AI 个性化解答 (结合简历)</span>`);
											_push(ssrRenderComponent(_component_el_button, {
												size: "small",
												color: "#f5a623",
												style: {
													"color": "white",
													"border": "none"
												},
												onClick: aiAnswer
											}, {
												default: withCtx((_, _push, _parent, _scopeId) => {
													if (_push) _push(` 生成解答 `);
													else return [createTextVNode(" 生成解答 ")];
												}),
												_: 1
											}, _parent, _scopeId));
											_push(`</div>`);
										} else return [createVNode("div", { style: {
											"display": "flex",
											"justify-content": "space-between",
											"align-items": "center",
											"width": "100%"
										} }, [createVNode("span", null, "🤖 AI 个性化解答 (结合简历)"), createVNode(_component_el_button, {
											size: "small",
											color: "#f5a623",
											style: {
												"color": "white",
												"border": "none"
											},
											onClick: aiAnswer
										}, {
											default: withCtx(() => [createTextVNode(" 生成解答 ")]),
											_: 1
										})])];
									}),
									default: withCtx((_, _push, _parent, _scopeId) => {
										if (_push) _push(ssrRenderComponent(_component_el_input, {
											modelValue: formData.value.aiAnswer,
											"onUpdate:modelValue": ($event) => formData.value.aiAnswer = $event,
											type: "textarea",
											rows: 6,
											placeholder: "点击右上角按钮生成 AI 个性化解答..."
										}, null, _parent, _scopeId));
										else return [createVNode(_component_el_input, {
											modelValue: formData.value.aiAnswer,
											"onUpdate:modelValue": ($event) => formData.value.aiAnswer = $event,
											type: "textarea",
											rows: 6,
											placeholder: "点击右上角按钮生成 AI 个性化解答..."
										}, null, 8, ["modelValue", "onUpdate:modelValue"])];
									}),
									_: 1
								}, _parent, _scopeId));
							} else return [
								createVNode(_component_el_form_item, {
									label: "标准问题",
									prop: "title"
								}, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: formData.value.title,
										"onUpdate:modelValue": ($event) => formData.value.title = $event,
										type: "textarea",
										rows: 2,
										placeholder: "请输入标准问题...",
										style: { "font-size": "16px" }
									}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
									_: 1
								}),
								createVNode("div", { style: {
									"display": "flex",
									"gap": "12px"
								} }, [createVNode(_component_el_form_item, {
									label: "技术领域",
									prop: "domain",
									style: { "flex": "1" }
								}, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: formData.value.domain,
										"onUpdate:modelValue": ($event) => formData.value.domain = $event,
										placeholder: "例如: Java基础"
									}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
									_: 1
								}), createVNode(_component_el_form_item, {
									label: "分类标签",
									prop: "categoryTags",
									style: { "flex": "1" }
								}, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: formData.value.categoryTags,
										"onUpdate:modelValue": ($event) => formData.value.categoryTags = $event,
										placeholder: "例如: Java/面向对象"
									}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
									_: 1
								})]),
								createVNode(_component_el_form_item, {
									label: "核心考点",
									prop: "corePoints"
								}, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: formData.value.corePoints,
										"onUpdate:modelValue": ($event) => formData.value.corePoints = $event,
										placeholder: "例如: 接口 vs 抽象类、单继承多实现"
									}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, { label: "标准答案要点" }, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: formData.value.answerKey,
										"onUpdate:modelValue": ($event) => formData.value.answerKey = $event,
										type: "textarea",
										rows: 4,
										placeholder: "在此输入标准答案的答题要点..."
									}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, null, {
									label: withCtx(() => [createVNode("div", { style: {
										"display": "flex",
										"justify-content": "space-between",
										"align-items": "center",
										"width": "100%"
									} }, [createVNode("span", null, "🤖 AI 个性化解答 (结合简历)"), createVNode(_component_el_button, {
										size: "small",
										color: "#f5a623",
										style: {
											"color": "white",
											"border": "none"
										},
										onClick: aiAnswer
									}, {
										default: withCtx(() => [createTextVNode(" 生成解答 ")]),
										_: 1
									})])]),
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: formData.value.aiAnswer,
										"onUpdate:modelValue": ($event) => formData.value.aiAnswer = $event,
										type: "textarea",
										rows: 6,
										placeholder: "点击右上角按钮生成 AI 个性化解答..."
									}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
									_: 1
								})
							];
						}),
						_: 1
					}, _parent, _scopeId));
					else return [createVNode(_component_el_form, {
						model: formData.value,
						rules,
						ref_key: "formRef",
						ref: formRef,
						"label-position": "top"
					}, {
						default: withCtx(() => [
							createVNode(_component_el_form_item, {
								label: "标准问题",
								prop: "title"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: formData.value.title,
									"onUpdate:modelValue": ($event) => formData.value.title = $event,
									type: "textarea",
									rows: 2,
									placeholder: "请输入标准问题...",
									style: { "font-size": "16px" }
								}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
								_: 1
							}),
							createVNode("div", { style: {
								"display": "flex",
								"gap": "12px"
							} }, [createVNode(_component_el_form_item, {
								label: "技术领域",
								prop: "domain",
								style: { "flex": "1" }
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: formData.value.domain,
									"onUpdate:modelValue": ($event) => formData.value.domain = $event,
									placeholder: "例如: Java基础"
								}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
								_: 1
							}), createVNode(_component_el_form_item, {
								label: "分类标签",
								prop: "categoryTags",
								style: { "flex": "1" }
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: formData.value.categoryTags,
									"onUpdate:modelValue": ($event) => formData.value.categoryTags = $event,
									placeholder: "例如: Java/面向对象"
								}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
								_: 1
							})]),
							createVNode(_component_el_form_item, {
								label: "核心考点",
								prop: "corePoints"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: formData.value.corePoints,
									"onUpdate:modelValue": ($event) => formData.value.corePoints = $event,
									placeholder: "例如: 接口 vs 抽象类、单继承多实现"
								}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, { label: "标准答案要点" }, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: formData.value.answerKey,
									"onUpdate:modelValue": ($event) => formData.value.answerKey = $event,
									type: "textarea",
									rows: 4,
									placeholder: "在此输入标准答案的答题要点..."
								}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, null, {
								label: withCtx(() => [createVNode("div", { style: {
									"display": "flex",
									"justify-content": "space-between",
									"align-items": "center",
									"width": "100%"
								} }, [createVNode("span", null, "🤖 AI 个性化解答 (结合简历)"), createVNode(_component_el_button, {
									size: "small",
									color: "#f5a623",
									style: {
										"color": "white",
										"border": "none"
									},
									onClick: aiAnswer
								}, {
									default: withCtx(() => [createTextVNode(" 生成解答 ")]),
									_: 1
								})])]),
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: formData.value.aiAnswer,
									"onUpdate:modelValue": ($event) => formData.value.aiAnswer = $event,
									type: "textarea",
									rows: 6,
									placeholder: "点击右上角按钮生成 AI 个性化解答..."
								}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
								_: 1
							})
						]),
						_: 1
					}, 8, ["model"])];
				}),
				_: 1
			}, _parent));
			_push(`</div>`);
		};
	}
};
var _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
	const ssrContext = useSSRContext();
	(ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/questions/index.vue");
	return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
var questions_default = /*#__PURE__*/ _plugin_vue_export_helper_default(_sfc_main, [["__scopeId", "data-v-59baa5e4"]]);

export { questions_default as default };
//# sourceMappingURL=questions-CsXeNHL2.mjs.map
