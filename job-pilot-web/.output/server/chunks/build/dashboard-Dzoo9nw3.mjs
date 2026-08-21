import { _ as _plugin_vue_export_helper_default, u as useNamespace, a as isNumber, c as isUndefined$1 } from '../virtual/entry.mjs';
import { w as withInstall, b as buildProps, k as keysOf, T as TypeComponentsMap, E as ElIcon, f as TypeComponents, d as definePropType, c as withNoopInstall, p as picture_filled_default } from './icon-BTfkZ5_-.mjs';
import { f as flattedChildren, i as isComment } from './vnode-BA-uK14B.mjs';
import { C as ClientOnly } from './client-only-DkcSMmmB.mjs';
import { ref, computed, mergeProps, defineComponent, useSlots, openBlock, createBlock, Transition, unref, withCtx, withDirectives, createElementVNode, normalizeClass, renderSlot, resolveDynamicComponent, createCommentVNode, createElementBlock, createTextVNode, toDisplayString, Fragment, createVNode, vShow, toRef, renderList, normalizeProps, useSSRContext, watch } from 'vue';
import { ssrRenderAttrs, ssrInterpolate, ssrRenderComponent } from 'vue/server-renderer';
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { BarChart, PieChart } from 'echarts/charts';
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components';
import { isObject } from '@vue/shared';
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
import 'lodash-unified';
import 'unhead/utils';

//#region node_modules/element-plus/es/hooks/use-throttle-render/index.mjs
var useThrottleRender = (loading, throttle = 0) => {
	if (throttle === 0) return loading;
	const throttled = ref(isObject(throttle) && Boolean(throttle.initVal));
	let timeoutHandle = null;
	const dispatchThrottling = (timer) => {
		if (isUndefined$1(timer)) {
			throttled.value = loading.value;
			return;
		}
		if (timeoutHandle) clearTimeout(timeoutHandle);
		timeoutHandle = setTimeout(() => {
			throttled.value = loading.value;
		}, timer);
	};
	const dispatcher = (type) => {
		if (type === "leading") if (isNumber(throttle)) dispatchThrottling(throttle);
		else dispatchThrottling(throttle.leading);
		else if (isObject(throttle)) dispatchThrottling(throttle.trailing);
		else throttled.value = false;
	};
	watch(() => loading.value, (val) => {
		dispatcher(val ? "leading" : "trailing");
	});
	return throttled;
};
/**
* @deprecated Removed after 3.0.0, Use `AlertProps` instead.
*/
var alertProps = buildProps({
	/**
	* @description alert title.
	*/
	title: {
		type: String,
		default: ""
	},
	description: {
		type: String,
		default: ""
	},
	/**
	* @description alert type.
	*/
	type: {
		type: String,
		values: keysOf(TypeComponentsMap),
		default: "info"
	},
	/**
	* @description whether alert can be dismissed.
	*/
	closable: {
		type: Boolean,
		default: true
	},
	/**
	* @description text for replacing x button
	*/
	closeText: {
		type: String,
		default: ""
	},
	/**
	* @description whether show icon
	*/
	showIcon: Boolean,
	/**
	* @description should content be placed in center.
	*/
	center: Boolean,
	effect: {
		type: String,
		values: ["light", "dark"],
		default: "light"
	}
});
//#endregion
//#region node_modules/element-plus/es/components/alert/index.mjs
var ElAlert = withInstall(/* @__PURE__ */ defineComponent({
	name: "ElAlert",
	__name: "alert",
	props: alertProps,
	emits: { close: (evt) => evt instanceof MouseEvent },
	setup(__props, { emit: __emit }) {
		const { Close } = TypeComponents;
		const props = __props;
		const emit = __emit;
		const slots = useSlots();
		const ns = useNamespace("alert");
		const visible = ref(true);
		const iconComponent = computed(() => TypeComponentsMap[props.type]);
		const hasDesc = computed(() => {
			if (props.description) return true;
			const slotContent = slots.default?.();
			if (!slotContent) return false;
			return flattedChildren(slotContent).some((child) => !isComment(child));
		});
		const close = (evt) => {
			visible.value = false;
			emit("close", evt);
		};
		return (_ctx, _cache) => {
			return openBlock(), createBlock(Transition, {
				name: unref(ns).b("fade"),
				persisted: ""
			}, {
				default: withCtx(() => [withDirectives(createElementVNode("div", {
					class: normalizeClass([
						unref(ns).b(),
						unref(ns).m(__props.type),
						unref(ns).is("center", __props.center),
						unref(ns).is(__props.effect)
					]),
					role: "alert"
				}, [__props.showIcon && (_ctx.$slots.icon || iconComponent.value) ? (openBlock(), createBlock(unref(ElIcon), {
					key: 0,
					class: normalizeClass([unref(ns).e("icon"), unref(ns).is("big", hasDesc.value)])
				}, {
					default: withCtx(() => [renderSlot(_ctx.$slots, "icon", {}, () => [(openBlock(), createBlock(resolveDynamicComponent(iconComponent.value)))])]),
					_: 3
				}, 8, ["class"])) : createCommentVNode("v-if", true), createElementVNode("div", { class: normalizeClass(unref(ns).e("content")) }, [
					__props.title || _ctx.$slots.title ? (openBlock(), createElementBlock("span", {
						key: 0,
						class: normalizeClass([unref(ns).e("title"), { "with-description": hasDesc.value }])
					}, [renderSlot(_ctx.$slots, "title", {}, () => [createTextVNode(toDisplayString(__props.title), 1)])], 2)) : createCommentVNode("v-if", true),
					hasDesc.value ? (openBlock(), createElementBlock("p", {
						key: 1,
						class: normalizeClass(unref(ns).e("description"))
					}, [renderSlot(_ctx.$slots, "default", {}, () => [createTextVNode(toDisplayString(__props.description), 1)])], 2)) : createCommentVNode("v-if", true),
					__props.closable ? (openBlock(), createElementBlock(Fragment, { key: 2 }, [__props.closeText ? (openBlock(), createElementBlock("div", {
						key: 0,
						class: normalizeClass([unref(ns).e("close-btn"), unref(ns).is("customed")]),
						onClick: close
					}, toDisplayString(__props.closeText), 3)) : (openBlock(), createBlock(unref(ElIcon), {
						key: 1,
						class: normalizeClass(unref(ns).e("close-btn")),
						onClick: close
					}, {
						default: withCtx(() => [createVNode(unref(Close))]),
						_: 1
					}, 8, ["class"]))], 64)) : createCommentVNode("v-if", true)
				], 2)], 2), [[vShow, visible.value]])]),
				_: 3
			}, 8, ["name"]);
		};
	}
}));
//#endregion
//#region node_modules/element-plus/es/components/skeleton/src/skeleton.mjs
/**
* @deprecated Removed after 3.0.0, Use `SkeletonProps` instead.
*/
var skeletonProps = buildProps({
	/**
	* @description whether showing the animation
	*/
	animated: Boolean,
	/**
	* @description how many fake items to render to the DOM
	*/
	count: {
		type: Number,
		default: 1
	},
	/**
	* @description numbers of the row, only useful when no template slot were given
	*/
	rows: {
		type: Number,
		default: 3
	},
	/**
	* @description whether showing the real DOM
	*/
	loading: {
		type: Boolean,
		default: true
	},
	/**
	* @description rendering delay in milliseconds
	*/
	throttle: { type: definePropType([Number, Object]) }
});
//#endregion
//#region node_modules/element-plus/es/components/skeleton/src/skeleton-item.mjs
/**
* @deprecated Removed after 3.0.0, Use `SkeletonItemProps` instead.
*/
var skeletonItemProps = buildProps({ 
/**
* @description the current rendering skeleton type
*/
variant: {
	type: String,
	values: [
		"circle",
		"rect",
		"h1",
		"h3",
		"text",
		"caption",
		"p",
		"image",
		"button"
	],
	default: "text"
} });
//#endregion
//#region node_modules/element-plus/es/components/skeleton/src/skeleton-item2.mjs
var skeleton_item_default = /* @__PURE__ */ defineComponent({
	name: "ElSkeletonItem",
	__name: "skeleton-item",
	props: skeletonItemProps,
	setup(__props) {
		const ns = useNamespace("skeleton");
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("div", { class: normalizeClass([unref(ns).e("item"), unref(ns).e(__props.variant)]) }, [__props.variant === "image" ? (openBlock(), createBlock(unref(picture_filled_default), { key: 0 })) : createCommentVNode("v-if", true)], 2);
		};
	}
});
//#endregion
//#region node_modules/element-plus/es/components/skeleton/index.mjs
var ElSkeleton = withInstall(/* @__PURE__ */ defineComponent({
	name: "ElSkeleton",
	__name: "skeleton",
	props: skeletonProps,
	setup(__props, { expose: __expose }) {
		const props = __props;
		const ns = useNamespace("skeleton");
		const uiLoading = useThrottleRender(toRef(props, "loading"), props.throttle);
		__expose({ 
		/** @description loading state */
uiLoading });
		return (_ctx, _cache) => {
			return unref(uiLoading) ? (openBlock(), createElementBlock("div", mergeProps({
				key: 0,
				class: [unref(ns).b(), unref(ns).is("animated", __props.animated)]
			}, _ctx.$attrs), [(openBlock(true), createElementBlock(Fragment, null, renderList(__props.count, (i) => {
				return openBlock(), createElementBlock(Fragment, { key: i }, [unref(uiLoading) ? renderSlot(_ctx.$slots, "template", { key: i }, () => [createVNode(skeleton_item_default, {
					class: normalizeClass(unref(ns).is("first")),
					variant: "p"
				}, null, 8, ["class"]), (openBlock(true), createElementBlock(Fragment, null, renderList(__props.rows, (item) => {
					return openBlock(), createBlock(skeleton_item_default, {
						key: item,
						class: normalizeClass([unref(ns).e("paragraph"), unref(ns).is("last", item === __props.rows && __props.rows > 1)]),
						variant: "p"
					}, null, 8, ["class"]);
				}), 128))]) : createCommentVNode("v-if", true)], 64);
			}), 128))], 16)) : renderSlot(_ctx.$slots, "default", normalizeProps(mergeProps({ key: 1 }, _ctx.$attrs)));
		};
	}
}), { SkeletonItem: skeleton_item_default });
withNoopInstall(skeleton_item_default);
//#endregion
//#region pages/dashboard/index.vue
var _sfc_main = {
	__name: "index",
	__ssrInlineRender: true,
	setup(__props) {
		use([
			CanvasRenderer,
			BarChart,
			PieChart,
			TitleComponent,
			TooltipComponent,
			LegendComponent,
			GridComponent
		]);
		const loading = ref(true);
		const error = ref(null);
		const statsData = ref(null);
		const totalJobs = ref(0);
		computed(() => {
			if (!statsData.value) return {};
			const data = statsData.value.salary;
			return {
				tooltip: {
					trigger: "axis",
					axisPointer: { type: "shadow" }
				},
				grid: {
					left: "3%",
					right: "4%",
					bottom: "3%",
					containLabel: true
				},
				xAxis: {
					type: "category",
					data: Object.keys(data),
					axisTick: { alignWithLabel: true }
				},
				yAxis: { type: "value" },
				series: [{
					name: "职位数量",
					type: "bar",
					barWidth: "60%",
					data: Object.values(data),
					itemStyle: { color: "#3498db" }
				}]
			};
		});
		computed(() => {
			if (!statsData.value) return {};
			const data = statsData.value.experience;
			return {
				tooltip: { trigger: "item" },
				legend: {
					top: "5%",
					left: "center"
				},
				series: [{
					name: "经验要求",
					type: "pie",
					radius: ["40%", "70%"],
					avoidLabelOverlap: false,
					itemStyle: {
						borderRadius: 10,
						borderColor: "#fff",
						borderWidth: 2
					},
					label: {
						show: false,
						position: "center"
					},
					emphasis: { label: {
						show: true,
						fontSize: 18,
						fontWeight: "bold"
					} },
					labelLine: { show: false },
					data: Object.keys(data).map((key) => ({
						name: key,
						value: data[key]
					}))
				}],
				color: [
					"#e74c3c",
					"#f1c40f",
					"#2ecc71",
					"#9b59b6",
					"#95a5a6"
				]
			};
		});
		computed(() => {
			if (!statsData.value) return {};
			const data = statsData.value.skills;
			return {
				tooltip: { show: true },
				series: [{
					type: "wordCloud",
					shape: "circle",
					keepAspect: false,
					left: "center",
					top: "center",
					width: "100%",
					height: "100%",
					right: null,
					bottom: null,
					sizeRange: [14, 50],
					rotationRange: [-45, 90],
					rotationStep: 45,
					gridSize: 8,
					drawOutOfBound: false,
					layoutAnimation: true,
					textStyle: {
						fontFamily: "\"Helvetica Neue\", Helvetica, Arial, sans-serif",
						fontWeight: "bold",
						color: function() {
							const colors = [
								"#3498db",
								"#e74c3c",
								"#2ecc71",
								"#e67e22",
								"#9b59b6",
								"#34495e",
								"#1abc9c",
								"#e84393",
								"#6c5ce7",
								"#00cec9"
							];
							return colors[Math.floor(Math.random() * colors.length)];
						}
					},
					emphasis: {
						focus: "self",
						textStyle: {
							textShadowBlur: 10,
							textShadowColor: "#333"
						}
					},
					data: Object.entries(data).sort((a, b) => b[1] - a[1]).slice(0, 80).map(([name, value]) => ({
						name,
						value
					}))
				}]
			};
		});
		return (_ctx, _push, _parent, _attrs) => {
			const _component_el_skeleton = ElSkeleton;
			const _component_el_alert = ElAlert;
			const _component_ClientOnly = ClientOnly;
			_push(`<div${ssrRenderAttrs(mergeProps({ class: "dashboard-page" }, _attrs))} data-v-f104cec3><div class="page-header" data-v-f104cec3><h2 data-v-f104cec3>数据看板</h2><p class="subtitle" data-v-f104cec3>共分析 ${ssrInterpolate(totalJobs.value)} 个职位</p></div>`);
			if (loading.value) {
				_push(`<div class="loading-state" data-v-f104cec3>`);
				_push(ssrRenderComponent(_component_el_skeleton, {
					rows: 5,
					animated: ""
				}, null, _parent));
				_push(`</div>`);
			} else if (error.value) {
				_push(`<div class="error-state" data-v-f104cec3>`);
				_push(ssrRenderComponent(_component_el_alert, {
					title: error.value,
					type: "error",
					"show-icon": ""
				}, null, _parent));
				_push(`</div>`);
			} else {
				_push(`<div class="dashboard-container" data-v-f104cec3><div class="charts-row" data-v-f104cec3><div class="chart-card" data-v-f104cec3><h3 class="chart-title" data-v-f104cec3>平均薪资分布 (区间)</h3>`);
				_push(ssrRenderComponent(_component_ClientOnly, null, {}, _parent));
				_push(`</div><div class="chart-card" data-v-f104cec3><h3 class="chart-title" data-v-f104cec3>经验要求占比</h3>`);
				_push(ssrRenderComponent(_component_ClientOnly, null, {}, _parent));
				_push(`</div></div><div class="charts-row full-width" data-v-f104cec3><div class="chart-card" data-v-f104cec3><h3 class="chart-title" data-v-f104cec3>热门技能词云</h3>`);
				_push(ssrRenderComponent(_component_ClientOnly, null, {}, _parent));
				_push(`</div></div></div>`);
			}
			_push(`</div>`);
		};
	}
};
var _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
	const ssrContext = useSSRContext();
	(ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/dashboard/index.vue");
	return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
var dashboard_default = /*#__PURE__*/ _plugin_vue_export_helper_default(_sfc_main, [["__scopeId", "data-v-f104cec3"]]);

export { dashboard_default as default };
//# sourceMappingURL=dashboard-Dzoo9nw3.mjs.map
