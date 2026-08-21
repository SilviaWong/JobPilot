import { _ as _plugin_vue_export_helper_default, $ as $fetch$1, u as useNamespace, f as useId } from '../virtual/entry.mjs';
import { w as withInstall, E as ElIcon, g as warning_filled_default, h as circle_check_default, j as circle_close_default, m as check_default, n as close_default, b as buildProps, a as addUnit, d as definePropType } from './icon-BTfkZ5_-.mjs';
import { E as ElButton, b as ElMessage, i as useLocale } from './css-n73NFgGH.mjs';
import { a as ElDrawer, E as ElTag } from './css-B_mwQh58.mjs';
import { ref, mergeProps, withCtx, unref, createVNode, createTextVNode, toDisplayString, openBlock, createBlock, createCommentVNode, Fragment, renderList, defineComponent, computed, createElementBlock, normalizeClass, createElementVNode, normalizeStyle, renderSlot, resolveDynamicComponent, useSSRContext } from 'vue';
import { ssrRenderComponent, ssrInterpolate, ssrRenderList, ssrRenderStyle } from 'vue/server-renderer';
import { Building, MapPin, Map, Briefcase, GraduationCap, Globe, BarChart, Gift, FileText, Tags, ExternalLink, Ban, Lightbulb, Target, Sparkles, Copy, RefreshCw, Rocket } from 'lucide-vue-next';
import { isFunction, isString } from '@vue/shared';

//#region node_modules/element-plus/es/components/empty/src/empty.mjs
/**
* @deprecated Removed after 3.0.0, Use `EmptyProps` instead.
*/
var emptyProps = buildProps({
	/**
	* @description image URL of empty
	*/
	image: {
		type: String,
		default: ""
	},
	/**
	* @description image size (width) of empty
	*/
	imageSize: Number,
	/**
	* @description description of empty
	*/
	description: {
		type: String,
		default: ""
	}
});
//#endregion
//#region node_modules/element-plus/es/components/empty/src/img-empty.vue_vue_type_script_setup_true_lang.mjs
var _hoisted_1$2 = {
	viewBox: "0 0 79 86",
	version: "1.1",
	xmlns: "http://www.w3.org/2000/svg",
	"xmlns:xlink": "http://www.w3.org/1999/xlink"
};
var _hoisted_2$2 = ["id"];
var _hoisted_3$1 = ["stop-color"];
var _hoisted_4$1 = ["stop-color"];
var _hoisted_5$1 = ["id"];
var _hoisted_6 = ["stop-color"];
var _hoisted_7 = ["stop-color"];
var _hoisted_8 = ["id"];
var _hoisted_9 = {
	stroke: "none",
	"stroke-width": "1",
	fill: "none",
	"fill-rule": "evenodd"
};
var _hoisted_10 = { transform: "translate(-1268.000000, -535.000000)" };
var _hoisted_11 = { transform: "translate(1268.000000, 535.000000)" };
var _hoisted_12 = ["fill"];
var _hoisted_13 = ["fill"];
var _hoisted_14 = { transform: "translate(34.500000, 31.500000) scale(-1, 1) rotate(-25.000000) translate(-34.500000, -31.500000) translate(7.000000, 10.000000)" };
var _hoisted_15 = ["fill"];
var _hoisted_16 = ["fill"];
var _hoisted_17 = ["fill"];
var _hoisted_18 = ["fill"];
var _hoisted_19 = ["fill"];
var _hoisted_20 = { transform: "translate(53.000000, 45.000000)" };
var _hoisted_21 = ["fill", "xlink:href"];
var _hoisted_22 = ["fill", "mask"];
var _hoisted_23 = ["fill"];
//#endregion
//#region node_modules/element-plus/es/components/empty/src/img-empty.mjs
var img_empty_default = /* @__PURE__ */ defineComponent({
	name: "ImgEmpty",
	__name: "img-empty",
	setup(__props) {
		const ns = useNamespace("empty");
		const id = useId();
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("svg", _hoisted_1$2, [createElementVNode("defs", null, [
				createElementVNode("linearGradient", {
					id: `linearGradient-1-${unref(id)}`,
					x1: "38.8503086%",
					y1: "0%",
					x2: "61.1496914%",
					y2: "100%"
				}, [createElementVNode("stop", {
					"stop-color": `var(${unref(ns).cssVarBlockName("fill-color-1")})`,
					offset: "0%"
				}, null, 8, _hoisted_3$1), createElementVNode("stop", {
					"stop-color": `var(${unref(ns).cssVarBlockName("fill-color-4")})`,
					offset: "100%"
				}, null, 8, _hoisted_4$1)], 8, _hoisted_2$2),
				createElementVNode("linearGradient", {
					id: `linearGradient-2-${unref(id)}`,
					x1: "0%",
					y1: "9.5%",
					x2: "100%",
					y2: "90.5%"
				}, [createElementVNode("stop", {
					"stop-color": `var(${unref(ns).cssVarBlockName("fill-color-1")})`,
					offset: "0%"
				}, null, 8, _hoisted_6), createElementVNode("stop", {
					"stop-color": `var(${unref(ns).cssVarBlockName("fill-color-6")})`,
					offset: "100%"
				}, null, 8, _hoisted_7)], 8, _hoisted_5$1),
				createElementVNode("rect", {
					id: `path-3-${unref(id)}`,
					x: "0",
					y: "0",
					width: "17",
					height: "36"
				}, null, 8, _hoisted_8)
			]), createElementVNode("g", _hoisted_9, [createElementVNode("g", _hoisted_10, [createElementVNode("g", _hoisted_11, [
				createElementVNode("path", {
					d: "M39.5,86 C61.3152476,86 79,83.9106622 79,81.3333333 C79,78.7560045 57.3152476,78 35.5,78 C13.6847524,78 0,78.7560045 0,81.3333333 C0,83.9106622 17.6847524,86 39.5,86 Z",
					fill: `var(${unref(ns).cssVarBlockName("fill-color-3")})`
				}, null, 8, _hoisted_12),
				createElementVNode("polygon", {
					fill: `var(${unref(ns).cssVarBlockName("fill-color-7")})`,
					transform: "translate(27.500000, 51.500000) scale(1, -1) translate(-27.500000, -51.500000) ",
					points: "13 58 53 58 42 45 2 45"
				}, null, 8, _hoisted_13),
				createElementVNode("g", _hoisted_14, [
					createElementVNode("polygon", {
						fill: `var(${unref(ns).cssVarBlockName("fill-color-7")})`,
						transform: "translate(11.500000, 5.000000) scale(1, -1) translate(-11.500000, -5.000000) ",
						points: "2.84078316e-14 3 18 3 23 7 5 7"
					}, null, 8, _hoisted_15),
					createElementVNode("polygon", {
						fill: `var(${unref(ns).cssVarBlockName("fill-color-5")})`,
						points: "-3.69149156e-15 7 38 7 38 43 -3.69149156e-15 43"
					}, null, 8, _hoisted_16),
					createElementVNode("rect", {
						fill: `url(#linearGradient-1-${unref(id)})`,
						transform: "translate(46.500000, 25.000000) scale(-1, 1) translate(-46.500000, -25.000000) ",
						x: "38",
						y: "7",
						width: "17",
						height: "36"
					}, null, 8, _hoisted_17),
					createElementVNode("polygon", {
						fill: `var(${unref(ns).cssVarBlockName("fill-color-2")})`,
						transform: "translate(39.500000, 3.500000) scale(-1, 1) translate(-39.500000, -3.500000) ",
						points: "24 7 41 7 55 -3.63806207e-12 38 -3.63806207e-12"
					}, null, 8, _hoisted_18)
				]),
				createElementVNode("rect", {
					fill: `url(#linearGradient-2-${unref(id)})`,
					x: "13",
					y: "45",
					width: "40",
					height: "36"
				}, null, 8, _hoisted_19),
				createElementVNode("g", _hoisted_20, [createElementVNode("use", {
					fill: `var(${unref(ns).cssVarBlockName("fill-color-8")})`,
					transform: "translate(8.500000, 18.000000) scale(-1, 1) translate(-8.500000, -18.000000) ",
					"xlink:href": `#path-3-${unref(id)}`
				}, null, 8, _hoisted_21), createElementVNode("polygon", {
					fill: `var(${unref(ns).cssVarBlockName("fill-color-9")})`,
					mask: `url(#mask-4-${unref(id)})`,
					transform: "translate(12.000000, 9.000000) scale(-1, 1) translate(-12.000000, -9.000000) ",
					points: "7 0 24 0 20 18 7 16.5"
				}, null, 8, _hoisted_22)]),
				createElementVNode("polygon", {
					fill: `var(${unref(ns).cssVarBlockName("fill-color-2")})`,
					transform: "translate(66.000000, 51.500000) scale(-1, 1) translate(-66.000000, -51.500000) ",
					points: "62 45 79 45 70 58 53 58"
				}, null, 8, _hoisted_23)
			])])])]);
		};
	}
});
//#endregion
//#region node_modules/element-plus/es/components/empty/src/empty.vue_vue_type_script_setup_true_lang.mjs
var _hoisted_1$1 = ["src"];
var _hoisted_2$1 = { key: 1 };
//#endregion
//#region node_modules/element-plus/es/components/empty/index.mjs
var ElEmpty = withInstall(/* @__PURE__ */ defineComponent({
	name: "ElEmpty",
	__name: "empty",
	props: emptyProps,
	setup(__props) {
		const props = __props;
		const { t } = useLocale();
		const ns = useNamespace("empty");
		const emptyDescription = computed(() => props.description || t("el.table.emptyText"));
		const imageStyle = computed(() => ({ width: addUnit(props.imageSize) }));
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("div", { class: normalizeClass(unref(ns).b()) }, [
				createElementVNode("div", {
					class: normalizeClass(unref(ns).e("image")),
					style: normalizeStyle(imageStyle.value)
				}, [__props.image ? (openBlock(), createElementBlock("img", {
					key: 0,
					src: __props.image,
					ondragstart: "return false"
				}, null, 8, _hoisted_1$1)) : renderSlot(_ctx.$slots, "image", { key: 1 }, () => [createVNode(img_empty_default)])], 6),
				createElementVNode("div", { class: normalizeClass(unref(ns).e("description")) }, [_ctx.$slots.description ? renderSlot(_ctx.$slots, "description", { key: 0 }) : (openBlock(), createElementBlock("p", _hoisted_2$1, toDisplayString(emptyDescription.value), 1))], 2),
				_ctx.$slots.default ? (openBlock(), createElementBlock("div", {
					key: 0,
					class: normalizeClass(unref(ns).e("bottom"))
				}, [renderSlot(_ctx.$slots, "default")], 2)) : createCommentVNode("v-if", true)
			], 2);
		};
	}
}));
//#endregion
//#region node_modules/element-plus/es/components/progress/src/progress.mjs
/**
* @deprecated Removed after 3.0.0, Use `ProgressProps` instead.
*/
var progressProps = buildProps({
	/**
	* @description type of progress bar
	*/
	type: {
		type: String,
		default: "line",
		values: [
			"line",
			"circle",
			"dashboard"
		]
	},
	/**
	* @description percentage, required
	*/
	percentage: {
		type: Number,
		default: 0,
		validator: (val) => val >= 0 && val <= 100
	},
	/**
	* @description the current status of progress bar
	*/
	status: {
		type: String,
		default: "",
		values: [
			"",
			"success",
			"exception",
			"warning"
		]
	},
	/**
	* @description set indeterminate progress
	*/
	indeterminate: Boolean,
	/**
	* @description control the animation duration of indeterminate progress or striped flow progress
	*/
	duration: {
		type: Number,
		default: 3
	},
	/**
	* @description the width of progress bar
	*/
	strokeWidth: {
		type: Number,
		default: 6
	},
	/**
	* @description butt/circle/dashboard type shape at the end path
	*/
	strokeLinecap: {
		type: definePropType(String),
		default: "round"
	},
	/**
	* @description whether to place the percentage inside progress bar, only works when `type` is 'line'
	*/
	textInside: Boolean,
	/**
	* @description the canvas width of circle progress bar
	*/
	width: {
		type: Number,
		default: 126
	},
	/**
	* @description whether to show percentage
	*/
	showText: {
		type: Boolean,
		default: true
	},
	/**
	* @description background color of progress bar. Overrides `status` prop
	*/
	color: {
		type: definePropType([
			String,
			Array,
			Function
		]),
		default: ""
	},
	/**
	* @description stripe over the progress bar's color
	*/
	striped: Boolean,
	/**
	* @description get the stripes to flow
	*/
	stripedFlow: Boolean,
	/**
	* @description custom text format
	*/
	format: {
		type: definePropType(Function),
		default: (percentage) => `${percentage}%`
	}
});
//#endregion
//#region node_modules/element-plus/es/components/progress/src/progress.vue_vue_type_script_setup_true_lang.mjs
var _hoisted_1 = ["aria-valuenow"];
var _hoisted_2 = { viewBox: "0 0 100 100" };
var _hoisted_3 = [
	"d",
	"stroke",
	"stroke-linecap",
	"stroke-width"
];
var _hoisted_4 = [
	"d",
	"stroke",
	"opacity",
	"stroke-linecap",
	"stroke-width"
];
var _hoisted_5 = { key: 0 };
//#endregion
//#region node_modules/element-plus/es/components/progress/index.mjs
var ElProgress = withInstall(/* @__PURE__ */ defineComponent({
	name: "ElProgress",
	__name: "progress",
	props: progressProps,
	setup(__props) {
		const STATUS_COLOR_MAP = {
			success: "#13ce66",
			exception: "#ff4949",
			warning: "#e6a23c",
			default: "#20a0ff"
		};
		const props = __props;
		const ns = useNamespace("progress");
		const barStyle = computed(() => {
			const barStyle = {
				width: `${props.percentage}%`,
				animationDuration: `${props.duration}s`
			};
			const color = getCurrentColor(props.percentage);
			if (color.includes("gradient")) barStyle.background = color;
			else barStyle.backgroundColor = color;
			return barStyle;
		});
		const relativeStrokeWidth = computed(() => (props.strokeWidth / props.width * 100).toFixed(1));
		const radius = computed(() => {
			if (["circle", "dashboard"].includes(props.type)) return Number.parseInt(`${50 - Number.parseFloat(relativeStrokeWidth.value) / 2}`, 10);
			return 0;
		});
		const trackPath = computed(() => {
			const r = radius.value;
			const isDashboard = props.type === "dashboard";
			return `
          M 50 50
          m 0 ${isDashboard ? "" : "-"}${r}
          a ${r} ${r} 0 1 1 0 ${isDashboard ? "-" : ""}${r * 2}
          a ${r} ${r} 0 1 1 0 ${isDashboard ? "" : "-"}${r * 2}
          `;
		});
		const perimeter = computed(() => 2 * Math.PI * radius.value);
		const rate = computed(() => props.type === "dashboard" ? .75 : 1);
		const strokeDashoffset = computed(() => {
			return `${ -1 * perimeter.value * (1 - rate.value) / 2}px`;
		});
		const trailPathStyle = computed(() => ({
			strokeDasharray: `${perimeter.value * rate.value}px, ${perimeter.value}px`,
			strokeDashoffset: strokeDashoffset.value
		}));
		const circlePathStyle = computed(() => ({
			strokeDasharray: `${perimeter.value * rate.value * (props.percentage / 100)}px, ${perimeter.value}px`,
			strokeDashoffset: strokeDashoffset.value,
			transition: "stroke-dasharray 0.6s ease 0s, stroke 0.6s ease, opacity ease 0.6s"
		}));
		const stroke = computed(() => {
			let ret;
			if (props.color) ret = getCurrentColor(props.percentage);
			else ret = STATUS_COLOR_MAP[props.status] || STATUS_COLOR_MAP.default;
			return ret;
		});
		const statusIcon = computed(() => {
			if (props.status === "warning") return warning_filled_default;
			if (props.type === "line") return props.status === "success" ? circle_check_default : circle_close_default;
			else return props.status === "success" ? check_default : close_default;
		});
		const progressTextSize = computed(() => {
			return props.type === "line" ? 12 + props.strokeWidth * .4 : props.width * .111111 + 2;
		});
		const content = computed(() => props.format(props.percentage));
		function getColors(color) {
			const span = 100 / color.length;
			return color.map((seriesColor, index) => {
				if (isString(seriesColor)) return {
					color: seriesColor,
					percentage: (index + 1) * span
				};
				return seriesColor;
			}).sort((a, b) => a.percentage - b.percentage);
		}
		const getCurrentColor = (percentage) => {
			const { color } = props;
			if (isFunction(color)) return color(percentage);
			else if (isString(color)) return color;
			else {
				const colors = getColors(color);
				for (const color of colors) if (color.percentage > percentage) return color.color;
				return colors[colors.length - 1]?.color;
			}
		};
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("div", {
				class: normalizeClass([
					unref(ns).b(),
					unref(ns).m(__props.type),
					unref(ns).is(__props.status),
					{
						[unref(ns).m("without-text")]: !__props.showText,
						[unref(ns).m("text-inside")]: __props.textInside
					}
				]),
				role: "progressbar",
				"aria-valuenow": __props.percentage,
				"aria-valuemin": "0",
				"aria-valuemax": "100"
			}, [__props.type === "line" ? (openBlock(), createElementBlock("div", {
				key: 0,
				class: normalizeClass(unref(ns).b("bar"))
			}, [createElementVNode("div", {
				class: normalizeClass(unref(ns).be("bar", "outer")),
				style: normalizeStyle({ height: `${__props.strokeWidth}px` })
			}, [createElementVNode("div", {
				class: normalizeClass([
					unref(ns).be("bar", "inner"),
					{ [unref(ns).bem("bar", "inner", "indeterminate")]: __props.indeterminate },
					{ [unref(ns).bem("bar", "inner", "striped")]: __props.striped },
					{ [unref(ns).bem("bar", "inner", "striped-flow")]: __props.stripedFlow }
				]),
				style: normalizeStyle(barStyle.value)
			}, [(__props.showText || _ctx.$slots.default) && __props.textInside ? (openBlock(), createElementBlock("div", {
				key: 0,
				class: normalizeClass(unref(ns).be("bar", "innerText"))
			}, [renderSlot(_ctx.$slots, "default", { percentage: __props.percentage }, () => [createElementVNode("span", null, toDisplayString(content.value), 1)])], 2)) : createCommentVNode("v-if", true)], 6)], 6)], 2)) : (openBlock(), createElementBlock("div", {
				key: 1,
				class: normalizeClass(unref(ns).b("circle")),
				style: normalizeStyle({
					height: `${__props.width}px`,
					width: `${__props.width}px`
				})
			}, [(openBlock(), createElementBlock("svg", _hoisted_2, [createElementVNode("path", {
				class: normalizeClass(unref(ns).be("circle", "track")),
				d: trackPath.value,
				stroke: `var(${unref(ns).cssVarName("fill-color-light")}, #e5e9f2)`,
				"stroke-linecap": __props.strokeLinecap,
				"stroke-width": relativeStrokeWidth.value,
				fill: "none",
				style: normalizeStyle(trailPathStyle.value)
			}, null, 14, _hoisted_3), createElementVNode("path", {
				class: normalizeClass(unref(ns).be("circle", "path")),
				d: trackPath.value,
				stroke: stroke.value,
				fill: "none",
				opacity: __props.percentage ? 1 : 0,
				"stroke-linecap": __props.strokeLinecap,
				"stroke-width": relativeStrokeWidth.value,
				style: normalizeStyle(circlePathStyle.value)
			}, null, 14, _hoisted_4)]))], 6)), (__props.showText || _ctx.$slots.default) && !__props.textInside ? (openBlock(), createElementBlock("div", {
				key: 2,
				class: normalizeClass(unref(ns).e("text")),
				style: normalizeStyle({ fontSize: `${progressTextSize.value}px` })
			}, [renderSlot(_ctx.$slots, "default", { percentage: __props.percentage }, () => [!__props.status ? (openBlock(), createElementBlock("span", _hoisted_5, toDisplayString(content.value), 1)) : (openBlock(), createBlock(unref(ElIcon), { key: 1 }, {
				default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(statusIcon.value)))]),
				_: 1
			}))])], 6)) : createCommentVNode("v-if", true)], 10, _hoisted_1);
		};
	}
}));
//#endregion
//#region components/JobDetailDrawer.vue
var _sfc_main = {
	__name: "JobDetailDrawer",
	__ssrInlineRender: true,
	emits: ["status-changed"],
	setup(__props, { expose: __expose, emit: __emit }) {
		const emit = __emit;
		const visible = ref(false);
		const job = ref(null);
		const marking = ref(false);
		const predicting = ref(false);
		const generatingGreeting = ref(false);
		const generatePredictions = async () => {
			if (!job.value || !job.value.jobId) return;
			predicting.value = true;
			try {
				const res = await $fetch$1("/api/jobs/predict-questions", {
					method: "POST",
					body: { jobId: job.value.jobId }
				});
				if (res && res.success && res.data) {
					ElMessage.success("面试预测生成成功！");
					job.value.aiResult = res.data;
				} else ElMessage.error(res?.error || "生成失败，请检查配置");
			} catch (error) {
				console.error("Prediction failed:", error);
				ElMessage.error("服务调用异常");
			} finally {
				predicting.value = false;
			}
		};
		const generateGreeting = async () => {
			if (!job.value || !job.value.jobId) return;
			generatingGreeting.value = true;
			try {
				const res = await $fetch$1("/api/jobs/generate-greeting", {
					method: "POST",
					body: { jobId: job.value.jobId }
				});
				if (res && res.success && res.data) {
					ElMessage.success("打招呼语生成成功！");
					job.value.aiResult = res.data;
				} else ElMessage.error(res?.error || "生成失败，请检查配置");
			} catch (error) {
				console.error("Greeting generation failed:", error);
				ElMessage.error("服务调用异常");
			} finally {
				generatingGreeting.value = false;
			}
		};
		const copyIntro = async () => {
			if (!job.value?.aiResult?.intro) return;
			try {
				await (void 0).clipboard.writeText(job.value.aiResult.intro);
				ElMessage.success("已复制到剪贴板");
			} catch (err) {
				console.error("Copy failed:", err);
				ElMessage.error("复制失败");
			}
		};
		const availableSkills = ref([]);
		ref("");
		const activeSkillResult = ref("");
		ref("");
		const fetchSkills = async () => {
			if (availableSkills.value.length > 0) return;
			try {
				const res = await $fetch$1("/api/skills/list");
				if (res && res.success) availableSkills.value = res.data.filter((s) => s.isActive);
			} catch (err) {
				console.error("Failed to fetch AI skills:", err);
			}
		};
		const open = (jobData) => {
			job.value = jobData;
			visible.value = true;
			activeSkillResult.value = "";
			fetchSkills();
		};
		const markAsExpired = async (jobData) => {
			if (!jobData || !jobData.jobId) return;
			marking.value = true;
			try {
				const res = await $fetch$1(`/api/jobs/${jobData.id}/status`, {
					method: "PUT",
					body: { status: "expired" }
				});
				if (res.success) {
					ElMessage.success("已标记为失效职位");
					visible.value = false;
					emit("status-changed", jobData, "expired");
				} else ElMessage.error(res.message || res.error || "标记失败");
			} catch (err) {
				ElMessage.error("标记失败");
			} finally {
				marking.value = false;
			}
		};
		const getScoreColor = (score, isTag = false) => {
			if (!score) return isTag ? "info" : "#909399";
			if (score >= 80) return isTag ? "success" : "#67C23A";
			if (score >= 60) return isTag ? "warning" : "#E6A23C";
			return isTag ? "danger" : "#F56C6C";
		};
		const formatDesc = (desc) => {
			if (!desc) return "暂无描述";
			return String(desc).replace(/<br\s*\/?>/gi, "\n").replace(/<\/?p[^>]*>/gi, "\n").replace(/<\/div>\s*<div[^>]*>/gi, "\n").replace(/<\/?div[^>]*>/gi, "\n").replace(/\\n/g, "\n").replace(/\\r/g, "").replace(/\r\n/g, "\n").replace(/\n{3,}/g, "\n\n").trim();
		};
		const formatAiAnalysis = (text) => {
			if (!text) return "暂无分析结果";
			let formatted = String(text);
			formatted = formatted.replace(/^### (.*?)$/gm, "\n\n<h3>$1</h3>\n\n");
			formatted = formatted.replace(/^## (.*?)$/gm, "\n\n<h2>$1</h2>\n\n");
			formatted = formatted.replace(/^# (.*?)$/gm, "\n\n<h1>$1</h1>\n\n");
			formatted = formatted.replace(/\*\*(.*?)\*\*/g, "<strong>$1</strong>");
			formatted = formatted.replace(/^\s*[-*]\s+(.*?)$/gm, "<li>$1</li>");
			formatted = formatted.replace(/(<li>.*?<\/li>(?:\s*<li>.*?<\/li>)*)/g, (match, p1) => `\n\n<ul>${p1.replace(/\n/g, "")}</ul>\n\n`);
			formatted = formatted.replace(/^\s*(\d+)[.、)]\s*(.*?)$/gm, "<li class=\"num\" value=\"$1\">$2</li>");
			formatted = formatted.replace(/(<li class="num"[^>]*>.*?<\/li>(?:\s*<li class="num"[^>]*>.*?<\/li>)*)/g, (match, p1) => `\n\n<ol>${p1.replace(/\n/g, "")}</ol>\n\n`);
			formatted = formatted.split(/\n\s*\n/).map((block) => {
				const trimmed = block.trim();
				if (!trimmed) return "";
				if (trimmed.startsWith("<h") || trimmed.startsWith("<ul") || trimmed.startsWith("<ol")) return block;
				return `<p>${block.replace(/\n/g, "<br>")}</p>`;
			}).join("");
			return formatted;
		};
		const getJobUrl = (jobData) => {
			if (!jobData) return "";
			return jobData.normalizedData?.jobUrl || "";
		};
		__expose({ open });
		return (_ctx, _push, _parent, _attrs) => {
			const _component_el_drawer = ElDrawer;
			const _component_el_icon = ElIcon;
			const _component_el_tag = ElTag;
			const _component_el_button = ElButton;
			const _component_el_progress = ElProgress;
			const _component_el_empty = ElEmpty;
			_push(ssrRenderComponent(_component_el_drawer, mergeProps({
				modelValue: visible.value,
				"onUpdate:modelValue": ($event) => visible.value = $event,
				title: job.value?.title || "职位详情",
				size: "75%",
				"destroy-on-close": "",
				class: "detail-drawer"
			}, _attrs), {
				default: withCtx((_, _push, _parent, _scopeId) => {
					if (_push) if (job.value) {
						_push(`<div class="drawer-split-layout" data-v-50c9e742${_scopeId}><div class="drawer-left" data-v-50c9e742${_scopeId}><div class="drawer-left-content" data-v-50c9e742${_scopeId}><div class="detail-header" data-v-50c9e742${_scopeId}><h1 class="detail-title" data-v-50c9e742${_scopeId}>${ssrInterpolate(job.value.title)}</h1><span class="detail-salary" data-v-50c9e742${_scopeId}>${ssrInterpolate(job.value.salary)}</span></div><div class="detail-sub-header" data-v-50c9e742${_scopeId}><span class="company-text" data-v-50c9e742${_scopeId}>`);
						_push(ssrRenderComponent(_component_el_icon, null, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) _push(ssrRenderComponent(unref(Building), null, null, _parent, _scopeId));
								else return [createVNode(unref(Building))];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(` ${ssrInterpolate(job.value.normalizedData?.clientCompanyName || job.value.normalizedData?.brandName || "-")}</span><span class="divider" data-v-50c9e742${_scopeId}>|</span><span class="location-text" data-v-50c9e742${_scopeId}>`);
						_push(ssrRenderComponent(_component_el_icon, null, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) _push(ssrRenderComponent(unref(MapPin), null, null, _parent, _scopeId));
								else return [createVNode(unref(MapPin))];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(` ${ssrInterpolate([
							job.value.normalizedData?.city,
							job.value.normalizedData?.area,
							job.value.normalizedData?.businessDistrict
						].filter(Boolean).join("·"))}</span></div>`);
						if (job.value.normalizedData?.address) {
							_push(`<div class="detail-address" data-v-50c9e742${_scopeId}><span data-v-50c9e742${_scopeId}>`);
							_push(ssrRenderComponent(_component_el_icon, null, {
								default: withCtx((_, _push, _parent, _scopeId) => {
									if (_push) _push(ssrRenderComponent(unref(Map), null, null, _parent, _scopeId));
									else return [createVNode(unref(Map))];
								}),
								_: 1
							}, _parent, _scopeId));
							_push(` ${ssrInterpolate(job.value.normalizedData.address)}</span></div>`);
						} else _push(`<!---->`);
						_push(`<div class="detail-badges" data-v-50c9e742${_scopeId}>`);
						_push(ssrRenderComponent(_component_el_tag, {
							effect: "light",
							type: "info",
							size: "large",
							class: "badge-tag"
						}, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) {
									_push(ssrRenderComponent(_component_el_icon, null, {
										default: withCtx((_, _push, _parent, _scopeId) => {
											if (_push) _push(ssrRenderComponent(unref(Briefcase), null, null, _parent, _scopeId));
											else return [createVNode(unref(Briefcase))];
										}),
										_: 1
									}, _parent, _scopeId));
									_push(` ${ssrInterpolate(job.value.normalizedData?.experience || "经验不限")}`);
								} else return [createVNode(_component_el_icon, null, {
									default: withCtx(() => [createVNode(unref(Briefcase))]),
									_: 1
								}), createTextVNode(" " + toDisplayString(job.value.normalizedData?.experience || "经验不限"), 1)];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(ssrRenderComponent(_component_el_tag, {
							effect: "light",
							type: "info",
							size: "large",
							class: "badge-tag"
						}, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) {
									_push(ssrRenderComponent(_component_el_icon, null, {
										default: withCtx((_, _push, _parent, _scopeId) => {
											if (_push) _push(ssrRenderComponent(unref(GraduationCap), null, null, _parent, _scopeId));
											else return [createVNode(unref(GraduationCap))];
										}),
										_: 1
									}, _parent, _scopeId));
									_push(` ${ssrInterpolate(job.value.normalizedData?.degree || "学历不限")}`);
								} else return [createVNode(_component_el_icon, null, {
									default: withCtx(() => [createVNode(unref(GraduationCap))]),
									_: 1
								}), createTextVNode(" " + toDisplayString(job.value.normalizedData?.degree || "学历不限"), 1)];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(ssrRenderComponent(_component_el_tag, {
							effect: "light",
							type: "info",
							size: "large",
							class: "badge-tag"
						}, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) {
									_push(ssrRenderComponent(_component_el_icon, null, {
										default: withCtx((_, _push, _parent, _scopeId) => {
											if (_push) _push(ssrRenderComponent(unref(Globe), null, null, _parent, _scopeId));
											else return [createVNode(unref(Globe))];
										}),
										_: 1
									}, _parent, _scopeId));
									_push(` ${ssrInterpolate(job.value.platform)}`);
								} else return [createVNode(_component_el_icon, null, {
									default: withCtx(() => [createVNode(unref(Globe))]),
									_: 1
								}), createTextVNode(" " + toDisplayString(job.value.platform), 1)];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(`</div><div class="section-title" data-v-50c9e742${_scopeId}>`);
						_push(ssrRenderComponent(_component_el_icon, null, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) _push(ssrRenderComponent(unref(BarChart), null, null, _parent, _scopeId));
								else return [createVNode(unref(BarChart))];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(` 岗位概览</div><div class="stats-grid" data-v-50c9e742${_scopeId}><div class="stat-box" data-v-50c9e742${_scopeId}><span class="stat-label" data-v-50c9e742${_scopeId}>所属行业</span><span class="stat-value" data-v-50c9e742${_scopeId}>${ssrInterpolate(job.value.normalizedData?.companyIndustry || "-")}</span></div><div class="stat-box" data-v-50c9e742${_scopeId}><span class="stat-label" data-v-50c9e742${_scopeId}>公司阶段</span><span class="stat-value" data-v-50c9e742${_scopeId}>${ssrInterpolate(job.value.normalizedData?.companyStage || "-")}</span></div><div class="stat-box" data-v-50c9e742${_scopeId}><span class="stat-label" data-v-50c9e742${_scopeId}>人员规模</span><span class="stat-value" data-v-50c9e742${_scopeId}>${ssrInterpolate(job.value.normalizedData?.companyScale || "-")}</span></div><div class="stat-box" data-v-50c9e742${_scopeId}><span class="stat-label" data-v-50c9e742${_scopeId}>招聘人员</span><span class="stat-value" data-v-50c9e742${_scopeId}>${ssrInterpolate(job.value.normalizedData?.hrName || "-")}</span></div><div class="stat-box" data-v-50c9e742${_scopeId}><span class="stat-label" data-v-50c9e742${_scopeId}>招聘职位</span><span class="stat-value" data-v-50c9e742${_scopeId}>${ssrInterpolate(job.value.normalizedData?.hrPosition || "-")}</span></div><div class="stat-box" data-v-50c9e742${_scopeId}><span class="stat-label" data-v-50c9e742${_scopeId}>所属公司</span><span class="stat-value" data-v-50c9e742${_scopeId}>${ssrInterpolate(job.value.normalizedData?.hrCompanyName || "-")}</span></div><div class="stat-box" data-v-50c9e742${_scopeId}><span class="stat-label" data-v-50c9e742${_scopeId}>发布日期</span><span class="stat-value" data-v-50c9e742${_scopeId}>${ssrInterpolate(job.value.normalizedData?.publishDate || "-")}</span></div><div class="stat-box" data-v-50c9e742${_scopeId}><span class="stat-label" data-v-50c9e742${_scopeId}>更新日期</span><span class="stat-value" data-v-50c9e742${_scopeId}>${ssrInterpolate(job.value.normalizedData?.updateDate || "-")}</span></div><div class="stat-box" data-v-50c9e742${_scopeId}><span class="stat-label" data-v-50c9e742${_scopeId}>采集日期</span><span class="stat-value" data-v-50c9e742${_scopeId}>${ssrInterpolate(job.value.normalizedData?.spiderDate || "-")}</span></div></div>`);
						if (job.value.normalizedData?.welfareList?.length) {
							_push(`<!--[--><div class="section-title" data-v-50c9e742${_scopeId}>`);
							_push(ssrRenderComponent(_component_el_icon, null, {
								default: withCtx((_, _push, _parent, _scopeId) => {
									if (_push) _push(ssrRenderComponent(unref(Gift), null, null, _parent, _scopeId));
									else return [createVNode(unref(Gift))];
								}),
								_: 1
							}, _parent, _scopeId));
							_push(` 福利待遇</div><div class="tags-container" data-v-50c9e742${_scopeId}><!--[-->`);
							ssrRenderList(job.value.normalizedData.welfareList, (tag, idx) => {
								_push(ssrRenderComponent(_component_el_tag, {
									key: idx,
									size: "default",
									type: "success",
									effect: "light",
									class: "welfare-tag"
								}, {
									default: withCtx((_, _push, _parent, _scopeId) => {
										if (_push) _push(`${ssrInterpolate(tag)}`);
										else return [createTextVNode(toDisplayString(tag), 1)];
									}),
									_: 2
								}, _parent, _scopeId));
							});
							_push(`<!--]--></div><!--]-->`);
						} else _push(`<!---->`);
						_push(`<div class="section-title" data-v-50c9e742${_scopeId}>`);
						_push(ssrRenderComponent(_component_el_icon, null, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) _push(ssrRenderComponent(unref(FileText), null, null, _parent, _scopeId));
								else return [createVNode(unref(FileText))];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(` 岗位职责与要求</div><div class="job-desc" style="${ssrRenderStyle({ "white-space": "pre-wrap" })}" data-v-50c9e742${_scopeId}>${ssrInterpolate(formatDesc(job.value.normalizedData?.jobDesc))}</div>`);
						if (job.value.normalizedData?.skills?.length) {
							_push(`<!--[--><div class="section-title" data-v-50c9e742${_scopeId}>`);
							_push(ssrRenderComponent(_component_el_icon, null, {
								default: withCtx((_, _push, _parent, _scopeId) => {
									if (_push) _push(ssrRenderComponent(unref(Tags), null, null, _parent, _scopeId));
									else return [createVNode(unref(Tags))];
								}),
								_: 1
							}, _parent, _scopeId));
							_push(` 技能标签</div><div class="tags-container" data-v-50c9e742${_scopeId}><!--[-->`);
							ssrRenderList(job.value.normalizedData.skills, (tag, idx) => {
								_push(ssrRenderComponent(_component_el_tag, {
									key: idx,
									size: "default",
									type: "primary",
									effect: "light",
									class: "welfare-tag"
								}, {
									default: withCtx((_, _push, _parent, _scopeId) => {
										if (_push) _push(`${ssrInterpolate(tag)}`);
										else return [createTextVNode(toDisplayString(tag), 1)];
									}),
									_: 2
								}, _parent, _scopeId));
							});
							_push(`<!--]--></div><!--]-->`);
						} else _push(`<!---->`);
						_push(`</div><div class="drawer-left-footer" data-v-50c9e742${_scopeId}>`);
						if (getJobUrl(job.value)) _push(ssrRenderComponent(_component_el_button, {
							type: "primary",
							tag: "a",
							href: getJobUrl(job.value),
							target: "_blank",
							rel: "noopener"
						}, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) {
									_push(ssrRenderComponent(_component_el_icon, null, {
										default: withCtx((_, _push, _parent, _scopeId) => {
											if (_push) _push(ssrRenderComponent(unref(ExternalLink), null, null, _parent, _scopeId));
											else return [createVNode(unref(ExternalLink))];
										}),
										_: 1
									}, _parent, _scopeId));
									_push(` 查看原网页`);
								} else return [createVNode(_component_el_icon, null, {
									default: withCtx(() => [createVNode(unref(ExternalLink))]),
									_: 1
								}), createTextVNode("\xA0查看原网页")];
							}),
							_: 1
						}, _parent, _scopeId));
						else _push(`<!---->`);
						_push(ssrRenderComponent(_component_el_button, {
							type: "danger",
							plain: "",
							onClick: ($event) => markAsExpired(job.value),
							style: { "margin-left": "12px" },
							loading: marking.value
						}, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) {
									_push(ssrRenderComponent(_component_el_icon, null, {
										default: withCtx((_, _push, _parent, _scopeId) => {
											if (_push) _push(ssrRenderComponent(unref(Ban), null, null, _parent, _scopeId));
											else return [createVNode(unref(Ban))];
										}),
										_: 1
									}, _parent, _scopeId));
									_push(` 标记失效`);
								} else return [createVNode(_component_el_icon, null, {
									default: withCtx(() => [createVNode(unref(Ban))]),
									_: 1
								}), createTextVNode("\xA0标记失效")];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(`</div></div><div class="drawer-right" data-v-50c9e742${_scopeId}><div class="ai-panel" data-v-50c9e742${_scopeId}>`);
						if (job.value.aiResult) {
							_push(`<!--[--><div class="ai-score-card-drawer" data-v-50c9e742${_scopeId}>`);
							_push(ssrRenderComponent(_component_el_progress, {
								type: "dashboard",
								percentage: job.value.aiResult.score || 0,
								color: getScoreColor(job.value.aiResult.score),
								width: 100
							}, {
								default: withCtx(({ percentage }, _push, _parent, _scopeId) => {
									if (_push) _push(`<span class="ai-score-big" data-v-50c9e742${_scopeId}>${ssrInterpolate(percentage)}分</span>`);
									else return [createVNode("span", { class: "ai-score-big" }, toDisplayString(percentage) + "分", 1)];
								}),
								_: 1
							}, _parent, _scopeId));
							_push(`<div class="ai-score-text" data-v-50c9e742${_scopeId}><h3 data-v-50c9e742${_scopeId}>AI 匹配度评分 `);
							_push(ssrRenderComponent(_component_el_tag, {
								type: getScoreColor(job.value.aiResult.score, true),
								size: "small",
								effect: "dark",
								style: { "margin-left": "5px" }
							}, {
								default: withCtx((_, _push, _parent, _scopeId) => {
									if (_push) _push(`${ssrInterpolate(job.value.aiResult.matchLevel || "-")}`);
									else return [createTextVNode(toDisplayString(job.value.aiResult.matchLevel || "-"), 1)];
								}),
								_: 1
							}, _parent, _scopeId));
							_push(`</h3><span class="ai-time" data-v-50c9e742${_scopeId}>最新评分</span></div></div><div class="ai-section" style="${ssrRenderStyle({ "margin-bottom": "20px" })}" data-v-50c9e742${_scopeId}><h4 data-v-50c9e742${_scopeId}>`);
							_push(ssrRenderComponent(_component_el_icon, null, {
								default: withCtx((_, _push, _parent, _scopeId) => {
									if (_push) _push(ssrRenderComponent(unref(Lightbulb), null, null, _parent, _scopeId));
									else return [createVNode(unref(Lightbulb))];
								}),
								_: 1
							}, _parent, _scopeId));
							_push(` 详细分析</h4><div class="ai-analysis-content" data-v-50c9e742${_scopeId}>${formatAiAnalysis(job.value.aiResult.resultText) ?? ""}</div></div><!--]-->`);
						} else {
							_push(`<div class="ai-empty" style="${ssrRenderStyle({
								"margin-bottom": "20px",
								"background": "white",
								"border-radius": "12px",
								"border": "1px solid #e2e8f0",
								"padding": "20px"
							})}" data-v-50c9e742${_scopeId}>`);
							_push(ssrRenderComponent(_component_el_empty, {
								description: "暂无综合匹配度分析，可点击下方按钮独立生成预测",
								"image-size": 80
							}, null, _parent, _scopeId));
							_push(`</div>`);
						}
						_push(`<div class="ai-section generator-section" style="${ssrRenderStyle({ "margin-bottom": "20px" })}" data-v-50c9e742${_scopeId}><h4 data-v-50c9e742${_scopeId}>`);
						_push(ssrRenderComponent(_component_el_icon, null, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) _push(ssrRenderComponent(unref(Target), null, null, _parent, _scopeId));
								else return [createVNode(unref(Target))];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(` 🎯 面试题目预测</h4>`);
						if (job.value.aiResult && job.value.aiResult.predictedQuestions) _push(`<div class="ai-analysis-content" data-v-50c9e742${_scopeId}>${formatAiAnalysis(job.value.aiResult.predictedQuestions) ?? ""}</div>`);
						else {
							_push(`<!--[--><p class="generator-desc" data-v-50c9e742${_scopeId}>结合岗位 JD 和您的简历，预测可能被问到的高频面试题。</p>`);
							_push(ssrRenderComponent(_component_el_button, {
								type: "warning",
								plain: "",
								class: "generate-btn",
								loading: predicting.value,
								onClick: generatePredictions
							}, {
								default: withCtx((_, _push, _parent, _scopeId) => {
									if (_push) {
										_push(ssrRenderComponent(_component_el_icon, null, {
											default: withCtx((_, _push, _parent, _scopeId) => {
												if (_push) _push(ssrRenderComponent(unref(Sparkles), null, null, _parent, _scopeId));
												else return [createVNode(unref(Sparkles))];
											}),
											_: 1
										}, _parent, _scopeId));
										_push(` 一键生成面试预测`);
									} else return [createVNode(_component_el_icon, null, {
										default: withCtx(() => [createVNode(unref(Sparkles))]),
										_: 1
									}), createTextVNode("\xA0一键生成面试预测")];
								}),
								_: 1
							}, _parent, _scopeId));
							_push(`<!--]-->`);
						}
						_push(`</div><div class="ai-section generator-section" style="${ssrRenderStyle({ "margin-bottom": "20px" })}" data-v-50c9e742${_scopeId}><h4 data-v-50c9e742${_scopeId}>`);
						_push(ssrRenderComponent(_component_el_icon, null, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) _push(ssrRenderComponent(unref(Sparkles), null, null, _parent, _scopeId));
								else return [createVNode(unref(Sparkles))];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(` AI 自我介绍生成</h4>`);
						if (job.value.aiResult && job.value.aiResult.intro) {
							_push(`<div data-v-50c9e742${_scopeId}><div class="ai-analysis-content" data-v-50c9e742${_scopeId}>${formatAiAnalysis(job.value.aiResult.intro) ?? ""}</div><div style="${ssrRenderStyle({
								"margin-top": "15px",
								"display": "flex",
								"gap": "10px"
							})}" data-v-50c9e742${_scopeId}>`);
							_push(ssrRenderComponent(_component_el_button, {
								size: "small",
								onClick: copyIntro
							}, {
								default: withCtx((_, _push, _parent, _scopeId) => {
									if (_push) {
										_push(ssrRenderComponent(_component_el_icon, null, {
											default: withCtx((_, _push, _parent, _scopeId) => {
												if (_push) _push(ssrRenderComponent(unref(Copy), null, null, _parent, _scopeId));
												else return [createVNode(unref(Copy))];
											}),
											_: 1
										}, _parent, _scopeId));
										_push(` 复制话术`);
									} else return [createVNode(_component_el_icon, null, {
										default: withCtx(() => [createVNode(unref(Copy))]),
										_: 1
									}), createTextVNode("\xA0复制话术")];
								}),
								_: 1
							}, _parent, _scopeId));
							_push(ssrRenderComponent(_component_el_button, {
								size: "small",
								type: "primary",
								plain: "",
								loading: generatingGreeting.value,
								onClick: generateGreeting
							}, {
								default: withCtx((_, _push, _parent, _scopeId) => {
									if (_push) {
										_push(ssrRenderComponent(_component_el_icon, null, {
											default: withCtx((_, _push, _parent, _scopeId) => {
												if (_push) _push(ssrRenderComponent(unref(RefreshCw), null, null, _parent, _scopeId));
												else return [createVNode(unref(RefreshCw))];
											}),
											_: 1
										}, _parent, _scopeId));
										_push(` 重新生成`);
									} else return [createVNode(_component_el_icon, null, {
										default: withCtx(() => [createVNode(unref(RefreshCw))]),
										_: 1
									}), createTextVNode("\xA0重新生成")];
								}),
								_: 1
							}, _parent, _scopeId));
							_push(`</div></div>`);
						} else {
							_push(`<!--[--><p class="generator-desc" data-v-50c9e742${_scopeId}>让 AI 为您生成一份专业且有针对性的打招呼语，提高回复率。</p>`);
							_push(ssrRenderComponent(_component_el_button, {
								type: "primary",
								plain: "",
								class: "generate-btn",
								loading: generatingGreeting.value,
								onClick: generateGreeting
							}, {
								default: withCtx((_, _push, _parent, _scopeId) => {
									if (_push) {
										_push(ssrRenderComponent(_component_el_icon, null, {
											default: withCtx((_, _push, _parent, _scopeId) => {
												if (_push) _push(ssrRenderComponent(unref(Rocket), null, null, _parent, _scopeId));
												else return [createVNode(unref(Rocket))];
											}),
											_: 1
										}, _parent, _scopeId));
										_push(` 一键生成自我介绍`);
									} else return [createVNode(_component_el_icon, null, {
										default: withCtx(() => [createVNode(unref(Rocket))]),
										_: 1
									}), createTextVNode("\xA0一键生成自我介绍")];
								}),
								_: 1
							}, _parent, _scopeId));
							_push(`<!--]-->`);
						}
						_push(`</div></div></div></div>`);
					} else _push(`<!---->`);
					else return [job.value ? (openBlock(), createBlock("div", {
						key: 0,
						class: "drawer-split-layout"
					}, [createVNode("div", { class: "drawer-left" }, [createVNode("div", { class: "drawer-left-content" }, [
						createVNode("div", { class: "detail-header" }, [createVNode("h1", { class: "detail-title" }, toDisplayString(job.value.title), 1), createVNode("span", { class: "detail-salary" }, toDisplayString(job.value.salary), 1)]),
						createVNode("div", { class: "detail-sub-header" }, [
							createVNode("span", { class: "company-text" }, [createVNode(_component_el_icon, null, {
								default: withCtx(() => [createVNode(unref(Building))]),
								_: 1
							}), createTextVNode(" " + toDisplayString(job.value.normalizedData?.clientCompanyName || job.value.normalizedData?.brandName || "-"), 1)]),
							createVNode("span", { class: "divider" }, "|"),
							createVNode("span", { class: "location-text" }, [createVNode(_component_el_icon, null, {
								default: withCtx(() => [createVNode(unref(MapPin))]),
								_: 1
							}), createTextVNode(" " + toDisplayString([
								job.value.normalizedData?.city,
								job.value.normalizedData?.area,
								job.value.normalizedData?.businessDistrict
							].filter(Boolean).join("·")), 1)])
						]),
						job.value.normalizedData?.address ? (openBlock(), createBlock("div", {
							key: 0,
							class: "detail-address"
						}, [createVNode("span", null, [createVNode(_component_el_icon, null, {
							default: withCtx(() => [createVNode(unref(Map))]),
							_: 1
						}), createTextVNode(" " + toDisplayString(job.value.normalizedData.address), 1)])])) : createCommentVNode("", true),
						createVNode("div", { class: "detail-badges" }, [
							createVNode(_component_el_tag, {
								effect: "light",
								type: "info",
								size: "large",
								class: "badge-tag"
							}, {
								default: withCtx(() => [createVNode(_component_el_icon, null, {
									default: withCtx(() => [createVNode(unref(Briefcase))]),
									_: 1
								}), createTextVNode(" " + toDisplayString(job.value.normalizedData?.experience || "经验不限"), 1)]),
								_: 1
							}),
							createVNode(_component_el_tag, {
								effect: "light",
								type: "info",
								size: "large",
								class: "badge-tag"
							}, {
								default: withCtx(() => [createVNode(_component_el_icon, null, {
									default: withCtx(() => [createVNode(unref(GraduationCap))]),
									_: 1
								}), createTextVNode(" " + toDisplayString(job.value.normalizedData?.degree || "学历不限"), 1)]),
								_: 1
							}),
							createVNode(_component_el_tag, {
								effect: "light",
								type: "info",
								size: "large",
								class: "badge-tag"
							}, {
								default: withCtx(() => [createVNode(_component_el_icon, null, {
									default: withCtx(() => [createVNode(unref(Globe))]),
									_: 1
								}), createTextVNode(" " + toDisplayString(job.value.platform), 1)]),
								_: 1
							})
						]),
						createVNode("div", { class: "section-title" }, [createVNode(_component_el_icon, null, {
							default: withCtx(() => [createVNode(unref(BarChart))]),
							_: 1
						}), createTextVNode(" 岗位概览")]),
						createVNode("div", { class: "stats-grid" }, [
							createVNode("div", { class: "stat-box" }, [createVNode("span", { class: "stat-label" }, "所属行业"), createVNode("span", { class: "stat-value" }, toDisplayString(job.value.normalizedData?.companyIndustry || "-"), 1)]),
							createVNode("div", { class: "stat-box" }, [createVNode("span", { class: "stat-label" }, "公司阶段"), createVNode("span", { class: "stat-value" }, toDisplayString(job.value.normalizedData?.companyStage || "-"), 1)]),
							createVNode("div", { class: "stat-box" }, [createVNode("span", { class: "stat-label" }, "人员规模"), createVNode("span", { class: "stat-value" }, toDisplayString(job.value.normalizedData?.companyScale || "-"), 1)]),
							createVNode("div", { class: "stat-box" }, [createVNode("span", { class: "stat-label" }, "招聘人员"), createVNode("span", { class: "stat-value" }, toDisplayString(job.value.normalizedData?.hrName || "-"), 1)]),
							createVNode("div", { class: "stat-box" }, [createVNode("span", { class: "stat-label" }, "招聘职位"), createVNode("span", { class: "stat-value" }, toDisplayString(job.value.normalizedData?.hrPosition || "-"), 1)]),
							createVNode("div", { class: "stat-box" }, [createVNode("span", { class: "stat-label" }, "所属公司"), createVNode("span", { class: "stat-value" }, toDisplayString(job.value.normalizedData?.hrCompanyName || "-"), 1)]),
							createVNode("div", { class: "stat-box" }, [createVNode("span", { class: "stat-label" }, "发布日期"), createVNode("span", { class: "stat-value" }, toDisplayString(job.value.normalizedData?.publishDate || "-"), 1)]),
							createVNode("div", { class: "stat-box" }, [createVNode("span", { class: "stat-label" }, "更新日期"), createVNode("span", { class: "stat-value" }, toDisplayString(job.value.normalizedData?.updateDate || "-"), 1)]),
							createVNode("div", { class: "stat-box" }, [createVNode("span", { class: "stat-label" }, "采集日期"), createVNode("span", { class: "stat-value" }, toDisplayString(job.value.normalizedData?.spiderDate || "-"), 1)])
						]),
						job.value.normalizedData?.welfareList?.length ? (openBlock(), createBlock(Fragment, { key: 1 }, [createVNode("div", { class: "section-title" }, [createVNode(_component_el_icon, null, {
							default: withCtx(() => [createVNode(unref(Gift))]),
							_: 1
						}), createTextVNode(" 福利待遇")]), createVNode("div", { class: "tags-container" }, [(openBlock(true), createBlock(Fragment, null, renderList(job.value.normalizedData.welfareList, (tag, idx) => {
							return openBlock(), createBlock(_component_el_tag, {
								key: idx,
								size: "default",
								type: "success",
								effect: "light",
								class: "welfare-tag"
							}, {
								default: withCtx(() => [createTextVNode(toDisplayString(tag), 1)]),
								_: 2
							}, 1024);
						}), 128))])], 64)) : createCommentVNode("", true),
						createVNode("div", { class: "section-title" }, [createVNode(_component_el_icon, null, {
							default: withCtx(() => [createVNode(unref(FileText))]),
							_: 1
						}), createTextVNode(" 岗位职责与要求")]),
						createVNode("div", {
							class: "job-desc",
							style: { "white-space": "pre-wrap" }
						}, toDisplayString(formatDesc(job.value.normalizedData?.jobDesc)), 1),
						job.value.normalizedData?.skills?.length ? (openBlock(), createBlock(Fragment, { key: 2 }, [createVNode("div", { class: "section-title" }, [createVNode(_component_el_icon, null, {
							default: withCtx(() => [createVNode(unref(Tags))]),
							_: 1
						}), createTextVNode(" 技能标签")]), createVNode("div", { class: "tags-container" }, [(openBlock(true), createBlock(Fragment, null, renderList(job.value.normalizedData.skills, (tag, idx) => {
							return openBlock(), createBlock(_component_el_tag, {
								key: idx,
								size: "default",
								type: "primary",
								effect: "light",
								class: "welfare-tag"
							}, {
								default: withCtx(() => [createTextVNode(toDisplayString(tag), 1)]),
								_: 2
							}, 1024);
						}), 128))])], 64)) : createCommentVNode("", true)
					]), createVNode("div", { class: "drawer-left-footer" }, [getJobUrl(job.value) ? (openBlock(), createBlock(_component_el_button, {
						key: 0,
						type: "primary",
						tag: "a",
						href: getJobUrl(job.value),
						target: "_blank",
						rel: "noopener"
					}, {
						default: withCtx(() => [createVNode(_component_el_icon, null, {
							default: withCtx(() => [createVNode(unref(ExternalLink))]),
							_: 1
						}), createTextVNode("\xA0查看原网页")]),
						_: 1
					}, 8, ["href"])) : createCommentVNode("", true), createVNode(_component_el_button, {
						type: "danger",
						plain: "",
						onClick: ($event) => markAsExpired(job.value),
						style: { "margin-left": "12px" },
						loading: marking.value
					}, {
						default: withCtx(() => [createVNode(_component_el_icon, null, {
							default: withCtx(() => [createVNode(unref(Ban))]),
							_: 1
						}), createTextVNode("\xA0标记失效")]),
						_: 1
					}, 8, ["onClick", "loading"])])]), createVNode("div", { class: "drawer-right" }, [createVNode("div", { class: "ai-panel" }, [
						job.value.aiResult ? (openBlock(), createBlock(Fragment, { key: 0 }, [createVNode("div", { class: "ai-score-card-drawer" }, [createVNode(_component_el_progress, {
							type: "dashboard",
							percentage: job.value.aiResult.score || 0,
							color: getScoreColor(job.value.aiResult.score),
							width: 100
						}, {
							default: withCtx(({ percentage }) => [createVNode("span", { class: "ai-score-big" }, toDisplayString(percentage) + "分", 1)]),
							_: 1
						}, 8, ["percentage", "color"]), createVNode("div", { class: "ai-score-text" }, [createVNode("h3", null, [createTextVNode("AI 匹配度评分 "), createVNode(_component_el_tag, {
							type: getScoreColor(job.value.aiResult.score, true),
							size: "small",
							effect: "dark",
							style: { "margin-left": "5px" }
						}, {
							default: withCtx(() => [createTextVNode(toDisplayString(job.value.aiResult.matchLevel || "-"), 1)]),
							_: 1
						}, 8, ["type"])]), createVNode("span", { class: "ai-time" }, "最新评分")])]), createVNode("div", {
							class: "ai-section",
							style: { "margin-bottom": "20px" }
						}, [createVNode("h4", null, [createVNode(_component_el_icon, null, {
							default: withCtx(() => [createVNode(unref(Lightbulb))]),
							_: 1
						}), createTextVNode(" 详细分析")]), createVNode("div", {
							class: "ai-analysis-content",
							innerHTML: formatAiAnalysis(job.value.aiResult.resultText)
						}, null, 8, ["innerHTML"])])], 64)) : (openBlock(), createBlock("div", {
							key: 1,
							class: "ai-empty",
							style: {
								"margin-bottom": "20px",
								"background": "white",
								"border-radius": "12px",
								"border": "1px solid #e2e8f0",
								"padding": "20px"
							}
						}, [createVNode(_component_el_empty, {
							description: "暂无综合匹配度分析，可点击下方按钮独立生成预测",
							"image-size": 80
						})])),
						createVNode("div", {
							class: "ai-section generator-section",
							style: { "margin-bottom": "20px" }
						}, [createVNode("h4", null, [createVNode(_component_el_icon, null, {
							default: withCtx(() => [createVNode(unref(Target))]),
							_: 1
						}), createTextVNode(" 🎯 面试题目预测")]), job.value.aiResult && job.value.aiResult.predictedQuestions ? (openBlock(), createBlock("div", {
							key: 0,
							class: "ai-analysis-content",
							innerHTML: formatAiAnalysis(job.value.aiResult.predictedQuestions)
						}, null, 8, ["innerHTML"])) : (openBlock(), createBlock(Fragment, { key: 1 }, [createVNode("p", { class: "generator-desc" }, "结合岗位 JD 和您的简历，预测可能被问到的高频面试题。"), createVNode(_component_el_button, {
							type: "warning",
							plain: "",
							class: "generate-btn",
							loading: predicting.value,
							onClick: generatePredictions
						}, {
							default: withCtx(() => [createVNode(_component_el_icon, null, {
								default: withCtx(() => [createVNode(unref(Sparkles))]),
								_: 1
							}), createTextVNode("\xA0一键生成面试预测")]),
							_: 1
						}, 8, ["loading"])], 64))]),
						createVNode("div", {
							class: "ai-section generator-section",
							style: { "margin-bottom": "20px" }
						}, [createVNode("h4", null, [createVNode(_component_el_icon, null, {
							default: withCtx(() => [createVNode(unref(Sparkles))]),
							_: 1
						}), createTextVNode(" AI 自我介绍生成")]), job.value.aiResult && job.value.aiResult.intro ? (openBlock(), createBlock("div", { key: 0 }, [createVNode("div", {
							class: "ai-analysis-content",
							innerHTML: formatAiAnalysis(job.value.aiResult.intro)
						}, null, 8, ["innerHTML"]), createVNode("div", { style: {
							"margin-top": "15px",
							"display": "flex",
							"gap": "10px"
						} }, [createVNode(_component_el_button, {
							size: "small",
							onClick: copyIntro
						}, {
							default: withCtx(() => [createVNode(_component_el_icon, null, {
								default: withCtx(() => [createVNode(unref(Copy))]),
								_: 1
							}), createTextVNode("\xA0复制话术")]),
							_: 1
						}), createVNode(_component_el_button, {
							size: "small",
							type: "primary",
							plain: "",
							loading: generatingGreeting.value,
							onClick: generateGreeting
						}, {
							default: withCtx(() => [createVNode(_component_el_icon, null, {
								default: withCtx(() => [createVNode(unref(RefreshCw))]),
								_: 1
							}), createTextVNode("\xA0重新生成")]),
							_: 1
						}, 8, ["loading"])])])) : (openBlock(), createBlock(Fragment, { key: 1 }, [createVNode("p", { class: "generator-desc" }, "让 AI 为您生成一份专业且有针对性的打招呼语，提高回复率。"), createVNode(_component_el_button, {
							type: "primary",
							plain: "",
							class: "generate-btn",
							loading: generatingGreeting.value,
							onClick: generateGreeting
						}, {
							default: withCtx(() => [createVNode(_component_el_icon, null, {
								default: withCtx(() => [createVNode(unref(Rocket))]),
								_: 1
							}), createTextVNode("\xA0一键生成自我介绍")]),
							_: 1
						}, 8, ["loading"])], 64))])
					])])])) : createCommentVNode("", true)];
				}),
				_: 1
			}, _parent));
		};
	}
};
var _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
	const ssrContext = useSSRContext();
	(ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("components/JobDetailDrawer.vue");
	return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
var JobDetailDrawer_default = /*#__PURE__*/ _plugin_vue_export_helper_default(_sfc_main, [["__scopeId", "data-v-50c9e742"]]);

export { ElEmpty as E, JobDetailDrawer_default as J };
//# sourceMappingURL=JobDetailDrawer-BOXyODc5.mjs.map
