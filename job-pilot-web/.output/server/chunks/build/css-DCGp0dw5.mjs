import { u as useNamespace, a as isNumber, d as debugWarn, t as throwError, i as isBoolean, g as isPropAbsent, c as isUndefined$1 } from '../virtual/entry.mjs';
import { w as withInstall, b as buildProps, d as definePropType, a as addUnit, c as withNoopInstall } from './icon-BTfkZ5_-.mjs';
import { u as useAriaProps } from './arrays-DLJ1qaKZ.mjs';
import { f as formContextKey } from './typescript-DnijNOm4.mjs';
import { U as UPDATE_MODEL_EVENT, C as CHANGE_EVENT } from './css-BTXcRLa_.mjs';
import { f as useSizeProp, e as useFormDisabled, u as useFormItem, d as useFormItemInputId, h as useDeprecated, c as useFormSize } from './css-n73NFgGH.mjs';
import { defineComponent, ref, computed, watch, nextTick, provide, reactive, openBlock, createElementBlock, normalizeClass, unref, createElementVNode, normalizeStyle, createBlock, resolveDynamicComponent, withCtx, renderSlot, createCommentVNode, inject, Fragment, createVNode, toRefs, renderList, mergeProps, useSlots, withDirectives, isRef, withModifiers, vModelCheckbox, createTextVNode, toDisplayString, toRef, Transition, vShow, getCurrentInstance, toRaw } from 'vue';
import { useResizeObserver, useEventListener, isClient } from '@vueuse/core';
import { pick, isEqual, omit } from 'lodash-unified';
import { isObject, NOOP, isArray, isString } from '@vue/shared';

//#region node_modules/element-plus/es/components/scrollbar/src/scrollbar.mjs
/**
* @deprecated Removed after 3.0.0, Use `ScrollbarProps` instead.
*/
var scrollbarProps = buildProps({
	/**
	* @description trigger distance(px)
	*/
	distance: {
		type: Number,
		default: 0
	},
	/**
	* @description height of scrollbar
	*/
	height: {
		type: [String, Number],
		default: ""
	},
	/**
	* @description max height of scrollbar
	*/
	maxHeight: {
		type: [String, Number],
		default: ""
	},
	/**
	* @description whether to use the native scrollbar
	*/
	native: Boolean,
	/**
	* @description style of wrap
	*/
	wrapStyle: {
		type: definePropType([
			String,
			Object,
			Array,
			Boolean
		]),
		default: ""
	},
	/**
	* @description class of wrap
	*/
	wrapClass: {
		type: [String, Array],
		default: ""
	},
	/**
	* @description class of view
	*/
	viewClass: {
		type: [String, Array],
		default: ""
	},
	/**
	* @description style of view
	*/
	viewStyle: {
		type: definePropType([
			String,
			Object,
			Array,
			Boolean
		]),
		default: ""
	},
	/**
	* @description do not respond to container size changes, if the container size does not change, it is better to set it to optimize performance
	*/
	noresize: Boolean,
	/**
	* @description element tag of the view
	*/
	tag: {
		type: String,
		default: "div"
	},
	/**
	* @description always show
	*/
	always: Boolean,
	/**
	* @description minimum size of scrollbar
	*/
	minSize: {
		type: Number,
		default: 20
	},
	/**
	* @description Wrap tabindex
	*/
	tabindex: {
		type: [String, Number],
		default: void 0
	},
	/**
	* @description id of view
	*/
	id: String,
	/**
	* @description role of view
	*/
	role: String,
	...useAriaProps(["ariaLabel", "ariaOrientation"])
});
var scrollbarEmits = {
	"end-reached": (direction) => [
		"left",
		"right",
		"top",
		"bottom"
	].includes(direction),
	scroll: ({ scrollTop, scrollLeft }) => [scrollTop, scrollLeft].every(isNumber)
};
//#endregion
//#region node_modules/element-plus/es/components/scrollbar/src/util.mjs
var BAR_MAP = {
	vertical: {
		offset: "offsetHeight",
		scroll: "scrollTop",
		scrollSize: "scrollHeight",
		size: "height",
		key: "vertical",
		axis: "Y",
		client: "clientY",
		direction: "top"
	},
	horizontal: {
		offset: "offsetWidth",
		scroll: "scrollLeft",
		scrollSize: "scrollWidth",
		size: "width",
		key: "horizontal",
		axis: "X",
		client: "clientX",
		direction: "left"
	}
};
var renderThumbStyle = ({ move, size, bar }) => ({
	[bar.size]: size,
	transform: `translate${bar.axis}(${move}%)`
});
//#endregion
//#region node_modules/element-plus/es/components/scrollbar/src/thumb.mjs
/**
* @deprecated Removed after 3.0.0, Use `ThumbProps` instead.
*/
var thumbProps = buildProps({
	vertical: Boolean,
	size: String,
	move: Number,
	ratio: {
		type: Number,
		required: true
	},
	always: Boolean
});
//#endregion
//#region node_modules/element-plus/es/components/scrollbar/src/constants.mjs
var scrollbarContextKey = Symbol("scrollbarContextKey");
//#endregion
//#region node_modules/element-plus/es/utils/numbers.mjs
/**
* Due to browser rendering and calculation precision loss issues,
* boundary checks cannot be based solely on value equality;
* a certain range of fluctuation is permissible.
*/
function isGreaterThan(a, b, epsilon = .03) {
	return a - b > epsilon;
}
//#endregion
//#region node_modules/element-plus/es/components/scrollbar/src/bar.mjs
/**
* @deprecated Removed after 3.0.0, Use `BarProps` instead.
*/
var barProps = buildProps({
	always: {
		type: Boolean,
		default: true
	},
	minSize: {
		type: Number,
		required: true
	}
});
//#endregion
//#region node_modules/element-plus/es/components/scrollbar/src/thumb.vue_vue_type_script_setup_true_lang.mjs
var COMPONENT_NAME$1 = "Thumb";
//#endregion
//#region node_modules/element-plus/es/components/scrollbar/src/thumb2.mjs
var thumb_default = /* @__PURE__ */ defineComponent({
	__name: "thumb",
	props: thumbProps,
	setup(__props) {
		const props = __props;
		const scrollbar = inject(scrollbarContextKey);
		const ns = useNamespace("scrollbar");
		if (!scrollbar) throwError(COMPONENT_NAME$1, "can not inject scrollbar context");
		const instance = ref();
		const thumb = ref();
		const thumbState = ref({});
		const visible = ref(false);
		let cursorDown = false;
		let cursorLeave = false;
		let baseScrollHeight = 0;
		let baseScrollWidth = 0;
		let originalOnSelectStart = isClient ? (void 0).onselectstart : null;
		const bar = computed(() => BAR_MAP[props.vertical ? "vertical" : "horizontal"]);
		const thumbStyle = computed(() => renderThumbStyle({
			size: props.size,
			move: props.move,
			bar: bar.value
		}));
		const offsetRatio = computed(() => instance.value[bar.value.offset] ** 2 / scrollbar.wrapElement[bar.value.scrollSize] / props.ratio / thumb.value[bar.value.offset]);
		const clickThumbHandler = (e) => {
			e.stopPropagation();
			if (e.ctrlKey || [1, 2].includes(e.button)) return;
			(void 0).getSelection()?.removeAllRanges();
			startDrag(e);
			const el = e.currentTarget;
			if (!el) return;
			thumbState.value[bar.value.axis] = el[bar.value.offset] - (e[bar.value.client] - el.getBoundingClientRect()[bar.value.direction]);
		};
		const clickTrackHandler = (e) => {
			if (!thumb.value || !instance.value || !scrollbar.wrapElement) return;
			const thumbPositionPercentage = (Math.abs(e.target.getBoundingClientRect()[bar.value.direction] - e[bar.value.client]) - thumb.value[bar.value.offset] / 2) * 100 * offsetRatio.value / instance.value[bar.value.offset];
			scrollbar.wrapElement[bar.value.scroll] = thumbPositionPercentage * scrollbar.wrapElement[bar.value.scrollSize] / 100;
		};
		const startDrag = (e) => {
			e.stopImmediatePropagation();
			cursorDown = true;
			baseScrollHeight = scrollbar.wrapElement.scrollHeight;
			baseScrollWidth = scrollbar.wrapElement.scrollWidth;
			(void 0).addEventListener("mousemove", mouseMoveDocumentHandler);
			(void 0).addEventListener("mouseup", mouseUpDocumentHandler);
			originalOnSelectStart = (void 0).onselectstart;
			(void 0).onselectstart = () => false;
		};
		const mouseMoveDocumentHandler = (e) => {
			if (!instance.value || !thumb.value) return;
			if (cursorDown === false) return;
			const prevPage = thumbState.value[bar.value.axis];
			if (!prevPage) return;
			const thumbPositionPercentage = ((instance.value.getBoundingClientRect()[bar.value.direction] - e[bar.value.client]) * -1 - (thumb.value[bar.value.offset] - prevPage)) * 100 * offsetRatio.value / instance.value[bar.value.offset];
			if (bar.value.scroll === "scrollLeft") scrollbar.wrapElement[bar.value.scroll] = thumbPositionPercentage * baseScrollWidth / 100;
			else scrollbar.wrapElement[bar.value.scroll] = thumbPositionPercentage * baseScrollHeight / 100;
		};
		const mouseUpDocumentHandler = () => {
			cursorDown = false;
			thumbState.value[bar.value.axis] = 0;
			(void 0).removeEventListener("mousemove", mouseMoveDocumentHandler);
			(void 0).removeEventListener("mouseup", mouseUpDocumentHandler);
			restoreOnselectstart();
			if (cursorLeave) visible.value = false;
		};
		const mouseMoveScrollbarHandler = () => {
			cursorLeave = false;
			visible.value = !!props.size;
		};
		const mouseLeaveScrollbarHandler = () => {
			cursorLeave = true;
			visible.value = cursorDown;
		};
		const restoreOnselectstart = () => {
			if ((void 0).onselectstart !== originalOnSelectStart) (void 0).onselectstart = originalOnSelectStart;
		};
		useEventListener(toRef(scrollbar, "scrollbarElement"), "mousemove", mouseMoveScrollbarHandler);
		useEventListener(toRef(scrollbar, "scrollbarElement"), "mouseleave", mouseLeaveScrollbarHandler);
		return (_ctx, _cache) => {
			return openBlock(), createBlock(Transition, {
				name: unref(ns).b("fade"),
				persisted: ""
			}, {
				default: withCtx(() => [withDirectives(createElementVNode("div", {
					ref_key: "instance",
					ref: instance,
					class: normalizeClass([unref(ns).e("bar"), unref(ns).is(bar.value.key)]),
					onMousedown: clickTrackHandler,
					onClick: _cache[0] || (_cache[0] = withModifiers(() => {}, ["stop"]))
				}, [createElementVNode("div", {
					ref_key: "thumb",
					ref: thumb,
					class: normalizeClass(unref(ns).e("thumb")),
					style: normalizeStyle(thumbStyle.value),
					onMousedown: clickThumbHandler
				}, null, 38)], 34), [[vShow, __props.always || visible.value]])]),
				_: 1
			}, 8, ["name"]);
		};
	}
});
//#endregion
//#region node_modules/element-plus/es/components/scrollbar/src/bar2.mjs
var bar_default = /* @__PURE__ */ defineComponent({
	__name: "bar",
	props: barProps,
	setup(__props, { expose: __expose }) {
		const props = __props;
		const scrollbar = inject(scrollbarContextKey);
		const moveX = ref(0);
		const moveY = ref(0);
		const sizeWidth = ref("");
		const sizeHeight = ref("");
		const ratioY = ref(1);
		const ratioX = ref(1);
		const handleScroll = (wrap) => {
			if (wrap) {
				const offsetHeight = wrap.offsetHeight - 4;
				const offsetWidth = wrap.offsetWidth - 4;
				moveY.value = wrap.scrollTop * 100 / offsetHeight * ratioY.value;
				moveX.value = wrap.scrollLeft * 100 / offsetWidth * ratioX.value;
			}
		};
		const update = () => {
			const wrap = scrollbar?.wrapElement;
			if (!wrap) return;
			const offsetHeight = wrap.offsetHeight - 4;
			const offsetWidth = wrap.offsetWidth - 4;
			const originalHeight = offsetHeight ** 2 / wrap.scrollHeight;
			const originalWidth = offsetWidth ** 2 / wrap.scrollWidth;
			const height = Math.max(originalHeight, props.minSize);
			const width = Math.max(originalWidth, props.minSize);
			ratioY.value = originalHeight / (offsetHeight - originalHeight) / (height / (offsetHeight - height));
			ratioX.value = originalWidth / (offsetWidth - originalWidth) / (width / (offsetWidth - width));
			sizeHeight.value = height + 4 < offsetHeight ? `${height}px` : "";
			sizeWidth.value = width + 4 < offsetWidth ? `${width}px` : "";
		};
		__expose({
			handleScroll,
			update
		});
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock(Fragment, null, [createVNode(thumb_default, {
				move: moveX.value,
				ratio: ratioX.value,
				size: sizeWidth.value,
				always: __props.always
			}, null, 8, [
				"move",
				"ratio",
				"size",
				"always"
			]), createVNode(thumb_default, {
				move: moveY.value,
				ratio: ratioY.value,
				size: sizeHeight.value,
				vertical: "",
				always: __props.always
			}, null, 8, [
				"move",
				"ratio",
				"size",
				"always"
			])], 64);
		};
	}
});
//#endregion
//#region node_modules/element-plus/es/components/scrollbar/src/scrollbar.vue_vue_type_script_setup_true_lang.mjs
var _hoisted_1$2 = ["tabindex"];
var COMPONENT_NAME = "ElScrollbar";
//#endregion
//#region node_modules/element-plus/es/components/scrollbar/index.mjs
var ElScrollbar = withInstall(/* @__PURE__ */ defineComponent({
	name: COMPONENT_NAME,
	__name: "scrollbar",
	props: scrollbarProps,
	emits: scrollbarEmits,
	setup(__props, { expose: __expose, emit: __emit }) {
		const props = __props;
		const emit = __emit;
		const ns = useNamespace("scrollbar");
		let stopResizeObserver = void 0;
		let stopWrapResizeObserver = void 0;
		let stopResizeListener = void 0;
		let wrapScrollTop = 0;
		let wrapScrollLeft = 0;
		let direction = "";
		const distanceScrollState = {
			bottom: false,
			top: false,
			right: false,
			left: false
		};
		const scrollbarRef = ref();
		const wrapRef = ref();
		const resizeRef = ref();
		const barRef = ref();
		const wrapStyle = computed(() => {
			const style = {};
			const height = addUnit(props.height);
			const maxHeight = addUnit(props.maxHeight);
			if (height) style.height = height;
			if (maxHeight) style.maxHeight = maxHeight;
			return [props.wrapStyle, style];
		});
		const wrapKls = computed(() => {
			return [
				props.wrapClass,
				ns.e("wrap"),
				{ [ns.em("wrap", "hidden-default")]: !props.native }
			];
		});
		const resizeKls = computed(() => {
			return [ns.e("view"), props.viewClass];
		});
		const shouldSkipDirection = (direction) => {
			return distanceScrollState[direction] ?? false;
		};
		const DIRECTION_PAIRS = {
			top: "bottom",
			bottom: "top",
			left: "right",
			right: "left"
		};
		const updateTriggerStatus = (arrivedStates) => {
			const oppositeDirection = DIRECTION_PAIRS[direction];
			if (!oppositeDirection) return;
			const arrived = arrivedStates[direction];
			const oppositeArrived = arrivedStates[oppositeDirection];
			if (arrived && !distanceScrollState[direction]) distanceScrollState[direction] = true;
			if (!oppositeArrived && distanceScrollState[oppositeDirection]) distanceScrollState[oppositeDirection] = false;
		};
		const handleScroll = () => {
			if (wrapRef.value) {
				barRef.value?.handleScroll(wrapRef.value);
				const prevTop = wrapScrollTop;
				const prevLeft = wrapScrollLeft;
				wrapScrollTop = wrapRef.value.scrollTop;
				wrapScrollLeft = wrapRef.value.scrollLeft;
				const arrivedStates = {
					bottom: !isGreaterThan(wrapRef.value.scrollHeight - props.distance, wrapRef.value.clientHeight + wrapScrollTop),
					top: wrapScrollTop <= props.distance && prevTop !== 0,
					right: !isGreaterThan(wrapRef.value.scrollWidth - props.distance, wrapRef.value.clientWidth + wrapScrollLeft) && prevLeft !== wrapScrollLeft,
					left: wrapScrollLeft <= props.distance && prevLeft !== 0
				};
				emit("scroll", {
					scrollTop: wrapScrollTop,
					scrollLeft: wrapScrollLeft
				});
				if (prevTop !== wrapScrollTop) direction = wrapScrollTop > prevTop ? "bottom" : "top";
				if (prevLeft !== wrapScrollLeft) direction = wrapScrollLeft > prevLeft ? "right" : "left";
				if (props.distance > 0) {
					if (shouldSkipDirection(direction)) return;
					updateTriggerStatus(arrivedStates);
				}
				if (arrivedStates[direction]) emit("end-reached", direction);
			}
		};
		function scrollTo(arg1, arg2) {
			if (isObject(arg1)) wrapRef.value.scrollTo(arg1);
			else if (isNumber(arg1) && isNumber(arg2)) wrapRef.value.scrollTo(arg1, arg2);
		}
		const setScrollTop = (value) => {
			if (!isNumber(value)) {
				debugWarn(COMPONENT_NAME, "value must be a number");
				return;
			}
			wrapRef.value.scrollTop = value;
		};
		const setScrollLeft = (value) => {
			if (!isNumber(value)) {
				debugWarn(COMPONENT_NAME, "value must be a number");
				return;
			}
			wrapRef.value.scrollLeft = value;
		};
		const update = () => {
			barRef.value?.update();
			distanceScrollState[direction] = false;
			if (wrapRef.value) barRef.value?.handleScroll(wrapRef.value);
		};
		watch(() => props.noresize, (noresize) => {
			if (noresize) {
				stopResizeObserver?.();
				stopWrapResizeObserver?.();
				stopResizeListener?.();
			} else {
				({stop: stopResizeObserver} = useResizeObserver(resizeRef, update));
				({stop: stopWrapResizeObserver} = useResizeObserver(wrapRef, update));
				stopResizeListener = useEventListener("resize", update);
			}
		}, { immediate: true });
		watch(() => [props.maxHeight, props.height], () => {
			if (!props.native) nextTick(() => {
				update();
			});
		});
		provide(scrollbarContextKey, reactive({
			scrollbarElement: scrollbarRef,
			wrapElement: wrapRef
		}));
		__expose({
			/** @description scrollbar wrap ref */
			wrapRef,
			/** @description update scrollbar state manually */
			update,
			/** @description scrolls to a particular set of coordinates */
			scrollTo,
			/** @description set distance to scroll top */
			setScrollTop,
			/** @description set distance to scroll left */
			setScrollLeft,
			/** @description handle scroll event */
			handleScroll
		});
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("div", {
				ref_key: "scrollbarRef",
				ref: scrollbarRef,
				class: normalizeClass(unref(ns).b())
			}, [createElementVNode("div", {
				ref_key: "wrapRef",
				ref: wrapRef,
				class: normalizeClass(wrapKls.value),
				style: normalizeStyle(wrapStyle.value),
				tabindex: __props.tabindex,
				onScroll: handleScroll
			}, [(openBlock(), createBlock(resolveDynamicComponent(__props.tag), {
				id: __props.id,
				ref_key: "resizeRef",
				ref: resizeRef,
				class: normalizeClass(resizeKls.value),
				style: normalizeStyle(__props.viewStyle),
				role: __props.role,
				"aria-label": __props.ariaLabel,
				"aria-orientation": __props.ariaOrientation
			}, {
				default: withCtx(() => [renderSlot(_ctx.$slots, "default")]),
				_: 3
			}, 8, [
				"id",
				"class",
				"style",
				"role",
				"aria-label",
				"aria-orientation"
			]))], 46, _hoisted_1$2), !__props.native ? (openBlock(), createBlock(bar_default, {
				key: 0,
				ref_key: "barRef",
				ref: barRef,
				always: __props.always,
				"min-size": __props.minSize
			}, null, 8, ["always", "min-size"])) : createCommentVNode("v-if", true)], 2);
		};
	}
}));
//#endregion
//#region node_modules/element-plus/es/components/checkbox/src/checkbox.mjs
/**
* @deprecated Removed after 3.0.0, Use `CheckboxProps` instead.
*/
var checkboxProps = {
	/**
	* @description binding value
	*/
	modelValue: {
		type: [
			Number,
			String,
			Boolean
		],
		default: void 0
	},
	/**
	* @description label of the Checkbox when used inside a `checkbox-group`
	*/
	label: {
		type: [
			String,
			Boolean,
			Number,
			Object
		],
		default: void 0
	},
	/**
	* @description value of the Checkbox when used inside a `checkbox-group`
	*/
	value: {
		type: [
			String,
			Boolean,
			Number,
			Object
		],
		default: void 0
	},
	/**
	* @description Set indeterminate state, only responsible for style control
	*/
	indeterminate: Boolean,
	/**
	* @description whether the Checkbox is disabled
	*/
	disabled: {
		type: Boolean,
		default: void 0
	},
	/**
	* @description if the Checkbox is checked
	*/
	checked: Boolean,
	/**
	* @description native 'name' attribute
	*/
	name: {
		type: String,
		default: void 0
	},
	/**
	* @description value of the Checkbox if it's checked
	*/
	trueValue: {
		type: [String, Number],
		default: void 0
	},
	/**
	* @description value of the Checkbox if it's not checked
	*/
	falseValue: {
		type: [String, Number],
		default: void 0
	},
	/**
	* @deprecated use `trueValue` instead
	* @description value of the Checkbox if it's checked
	*/
	trueLabel: {
		type: [String, Number],
		default: void 0
	},
	/**
	* @deprecated use `falseValue` instead
	* @description value of the Checkbox if it's not checked
	*/
	falseLabel: {
		type: [String, Number],
		default: void 0
	},
	/**
	* @description input id
	*/
	id: {
		type: String,
		default: void 0
	},
	/**
	* @description whether to add a border around Checkbox
	*/
	border: Boolean,
	/**
	* @description size of the Checkbox
	*/
	size: useSizeProp,
	/**
	* @description input tabindex
	*/
	tabindex: [String, Number],
	/**
	* @description whether to trigger form validation
	*/
	validateEvent: {
		type: Boolean,
		default: true
	},
	ariaLabel: String,
	...useAriaProps(["ariaControls"])
};
var checkboxEmits = {
	[UPDATE_MODEL_EVENT]: (val) => isString(val) || isNumber(val) || isBoolean(val),
	change: (val) => isString(val) || isNumber(val) || isBoolean(val)
};
//#endregion
//#region node_modules/element-plus/es/components/checkbox/src/constants.mjs
var checkboxGroupContextKey = Symbol("checkboxGroupContextKey");
//#endregion
//#region node_modules/element-plus/es/components/checkbox/src/checkbox-group.mjs
/**
* @deprecated Removed after 3.0.0, Use `CheckboxGroupProps` instead.
*/
var checkboxGroupProps = buildProps({
	/**
	* @description binding value
	*/
	modelValue: {
		type: definePropType(Array),
		default: () => []
	},
	/**
	* @description whether the nesting checkboxes are disabled
	*/
	disabled: {
		type: Boolean,
		default: void 0
	},
	/**
	* @description minimum number of checkbox checked
	*/
	min: Number,
	/**
	* @description maximum number of checkbox checked
	*/
	max: Number,
	/**
	* @description size of checkbox
	*/
	size: useSizeProp,
	/**
	* @description border and background color when button is active
	*/
	fill: String,
	/**
	* @description font color when button is active
	*/
	textColor: String,
	/**
	* @description element tag of the checkbox group
	*/
	tag: {
		type: String,
		default: "div"
	},
	/**
	* @description whether to trigger form validation
	*/
	validateEvent: {
		type: Boolean,
		default: true
	},
	options: { type: definePropType(Array) },
	props: {
		type: definePropType(Object),
		default: () => checkboxDefaultProps
	},
	type: {
		type: String,
		values: ["checkbox", "button"],
		default: "checkbox"
	},
	...useAriaProps(["ariaLabel"])
});
var checkboxGroupEmits = {
	[UPDATE_MODEL_EVENT]: (val) => isArray(val),
	change: (val) => isArray(val)
};
var checkboxDefaultProps = {
	label: "label",
	value: "value",
	disabled: "disabled"
};
//#endregion
//#region node_modules/element-plus/es/components/checkbox/src/composables/use-checkbox-disabled.mjs
var useCheckboxDisabled = ({ model, isChecked }) => {
	const checkboxGroup = inject(checkboxGroupContextKey, void 0);
	const formContext = inject(formContextKey, void 0);
	const isLimitDisabled = computed(() => {
		const max = checkboxGroup?.max?.value;
		const min = checkboxGroup?.min?.value;
		return !isUndefined$1(max) && model.value.length >= max && !isChecked.value || !isUndefined$1(min) && model.value.length <= min && isChecked.value;
	});
	return {
		isDisabled: useFormDisabled(computed(() => {
			if (checkboxGroup === void 0) return formContext?.disabled ?? isLimitDisabled.value;
			else return checkboxGroup.disabled?.value || isLimitDisabled.value;
		})),
		isLimitDisabled
	};
};
//#endregion
//#region node_modules/element-plus/es/components/checkbox/src/composables/use-checkbox-event.mjs
var useCheckboxEvent = (props, { model, isLimitExceeded, hasOwnLabel, isDisabled, isLabeledByFormItem }) => {
	const checkboxGroup = inject(checkboxGroupContextKey, void 0);
	const { formItem } = useFormItem();
	const { emit } = getCurrentInstance();
	function getLabeledValue(value) {
		return [
			true,
			props.trueValue,
			props.trueLabel
		].includes(value) ? props.trueValue ?? props.trueLabel ?? true : props.falseValue ?? props.falseLabel ?? false;
	}
	function emitChangeEvent(checked, e) {
		emit(CHANGE_EVENT, getLabeledValue(checked), e);
	}
	function handleChange(e) {
		if (isLimitExceeded.value) return;
		const target = e.target;
		emit(CHANGE_EVENT, getLabeledValue(target.checked), e);
	}
	async function onClickRoot(e) {
		if (isLimitExceeded.value) return;
		if (!hasOwnLabel.value && !isDisabled.value && isLabeledByFormItem.value) {
			if (!e.composedPath().some((item) => item.tagName === "LABEL")) {
				model.value = getLabeledValue([
					false,
					props.falseValue,
					props.falseLabel
				].includes(model.value));
				await nextTick();
				emitChangeEvent(model.value, e);
			}
		}
	}
	const validateEvent = computed(() => checkboxGroup?.validateEvent || props.validateEvent);
	watch(() => props.modelValue, () => {
		if (validateEvent.value) formItem?.validate("change").catch(NOOP);
	});
	return {
		handleChange,
		onClickRoot
	};
};
//#endregion
//#region node_modules/element-plus/es/components/checkbox/src/composables/use-checkbox-model.mjs
var useCheckboxModel = (props) => {
	const selfModel = ref(false);
	const { emit, vnode } = getCurrentInstance();
	const checkboxGroup = inject(checkboxGroupContextKey, void 0);
	const isGroup = computed(() => isUndefined$1(checkboxGroup) === false);
	const isLimitExceeded = ref(false);
	const isControlled = computed(() => {
		const rawProps = vnode.props ?? {};
		return "modelValue" in rawProps || "model-value" in rawProps;
	});
	const model = computed({
		get() {
			return isGroup.value ? checkboxGroup?.modelValue?.value : !isControlled.value ? selfModel.value : props.modelValue;
		},
		set(val) {
			if (isGroup.value && isArray(val)) {
				isLimitExceeded.value = checkboxGroup?.max?.value !== void 0 && val.length > checkboxGroup?.max.value && val.length > model.value.length;
				isLimitExceeded.value === false && checkboxGroup?.changeEvent?.(val);
			} else {
				emit(UPDATE_MODEL_EVENT, val);
				selfModel.value = val;
			}
		}
	});
	return {
		model,
		isGroup,
		isLimitExceeded
	};
};
//#endregion
//#region node_modules/element-plus/es/components/checkbox/src/composables/use-checkbox-status.mjs
var useCheckboxStatus = (props, slots, { model }) => {
	const checkboxGroup = inject(checkboxGroupContextKey, void 0);
	const isFocused = ref(false);
	const actualValue = computed(() => {
		if (!isPropAbsent(props.value)) return props.value;
		return props.label;
	});
	const isChecked = computed(() => {
		const value = model.value;
		if (isBoolean(value)) return value;
		else if (isArray(value)) if (isObject(actualValue.value)) return value.map(toRaw).some((o) => isEqual(o, actualValue.value));
		else return value.map(toRaw).includes(actualValue.value);
		else if (value !== null && value !== void 0) return value === props.trueValue || value === props.trueLabel;
		else return !!value;
	});
	return {
		checkboxButtonSize: useFormSize(computed(() => checkboxGroup?.size?.value), { prop: true }),
		isChecked,
		isFocused,
		checkboxSize: useFormSize(computed(() => checkboxGroup?.size?.value)),
		hasOwnLabel: computed(() => {
			return !!slots.default || !isPropAbsent(actualValue.value);
		}),
		actualValue
	};
};
//#endregion
//#region node_modules/element-plus/es/components/checkbox/src/composables/use-checkbox.mjs
var useCheckbox = (props, slots) => {
	const { formItem: elFormItem } = useFormItem();
	const { model, isGroup, isLimitExceeded } = useCheckboxModel(props);
	const { isFocused, isChecked, checkboxButtonSize, checkboxSize, hasOwnLabel, actualValue } = useCheckboxStatus(props, slots, { model });
	const { isDisabled } = useCheckboxDisabled({
		model,
		isChecked
	});
	const { inputId, isLabeledByFormItem } = useFormItemInputId(props, {
		formItemContext: elFormItem,
		disableIdGeneration: hasOwnLabel,
		disableIdManagement: isGroup
	});
	const { handleChange, onClickRoot } = useCheckboxEvent(props, {
		model,
		isLimitExceeded,
		hasOwnLabel,
		isDisabled,
		isLabeledByFormItem
	});
	const setStoreValue = () => {
		function addToStore() {
			if (isArray(model.value) && !model.value.includes(actualValue.value)) model.value.push(actualValue.value);
			else model.value = props.trueValue ?? props.trueLabel ?? true;
		}
		props.checked && addToStore();
	};
	setStoreValue();
	useDeprecated({
		from: "label act as value",
		replacement: "value",
		version: "3.0.0",
		scope: "el-checkbox",
		ref: "https://element-plus.org/en-US/component/checkbox.html"
	}, computed(() => isGroup.value && isPropAbsent(props.value)));
	useDeprecated({
		from: "true-label",
		replacement: "true-value",
		version: "3.0.0",
		scope: "el-checkbox",
		ref: "https://element-plus.org/en-US/component/checkbox.html"
	}, computed(() => !!props.trueLabel));
	useDeprecated({
		from: "false-label",
		replacement: "false-value",
		version: "3.0.0",
		scope: "el-checkbox",
		ref: "https://element-plus.org/en-US/component/checkbox.html"
	}, computed(() => !!props.falseLabel));
	return {
		inputId,
		isLabeledByFormItem,
		isChecked,
		isDisabled,
		isFocused,
		checkboxButtonSize,
		checkboxSize,
		hasOwnLabel,
		model,
		actualValue,
		handleChange,
		onClickRoot
	};
};
//#endregion
//#region node_modules/element-plus/es/components/checkbox/src/checkbox.vue_vue_type_script_setup_true_lang.mjs
var _hoisted_1$1 = [
	"id",
	"indeterminate",
	"name",
	"tabindex",
	"disabled"
];
//#endregion
//#region node_modules/element-plus/es/components/checkbox/src/checkbox2.mjs
var checkbox_default = /* @__PURE__ */ defineComponent({
	name: "ElCheckbox",
	__name: "checkbox",
	props: checkboxProps,
	emits: checkboxEmits,
	setup(__props) {
		const props = __props;
		const { inputId, isLabeledByFormItem, isChecked, isDisabled, isFocused, checkboxSize, hasOwnLabel, model, actualValue, handleChange, onClickRoot } = useCheckbox(props, useSlots());
		const inputBindings = computed(() => {
			if (props.trueValue || props.falseValue || props.trueLabel || props.falseLabel) return {
				"true-value": props.trueValue ?? props.trueLabel ?? true,
				"false-value": props.falseValue ?? props.falseLabel ?? false
			};
			return { value: actualValue.value };
		});
		const ns = useNamespace("checkbox");
		const compKls = computed(() => {
			return [
				ns.b(),
				ns.m(checkboxSize.value),
				ns.is("disabled", isDisabled.value),
				ns.is("bordered", props.border),
				ns.is("checked", isChecked.value)
			];
		});
		const spanKls = computed(() => {
			return [
				ns.e("input"),
				ns.is("disabled", isDisabled.value),
				ns.is("checked", isChecked.value),
				ns.is("indeterminate", props.indeterminate),
				ns.is("focus", isFocused.value)
			];
		});
		return (_ctx, _cache) => {
			return openBlock(), createBlock(resolveDynamicComponent(!unref(hasOwnLabel) && unref(isLabeledByFormItem) ? "span" : "label"), {
				for: !unref(hasOwnLabel) && unref(isLabeledByFormItem) ? null : unref(inputId),
				class: normalizeClass(compKls.value),
				"aria-controls": __props.indeterminate ? __props.ariaControls : null,
				"aria-checked": __props.indeterminate ? "mixed" : void 0,
				"aria-label": __props.ariaLabel,
				onClick: unref(onClickRoot)
			}, {
				default: withCtx(() => [createElementVNode("span", { class: normalizeClass(spanKls.value) }, [withDirectives(createElementVNode("input", mergeProps({
					id: unref(inputId),
					"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => isRef(model) ? model.value = $event : null),
					class: unref(ns).e("original"),
					type: "checkbox",
					indeterminate: __props.indeterminate,
					name: __props.name,
					tabindex: __props.tabindex,
					disabled: unref(isDisabled)
				}, inputBindings.value, {
					onChange: _cache[1] || (_cache[1] = (...args) => unref(handleChange) && unref(handleChange)(...args)),
					onFocus: _cache[2] || (_cache[2] = ($event) => isFocused.value = true),
					onBlur: _cache[3] || (_cache[3] = ($event) => isFocused.value = false),
					onClick: _cache[4] || (_cache[4] = withModifiers(() => {}, ["stop"]))
				}), null, 16, _hoisted_1$1), [[vModelCheckbox, unref(model)]]), createElementVNode("span", { class: normalizeClass(unref(ns).e("inner")) }, null, 2)], 2), unref(hasOwnLabel) ? (openBlock(), createElementBlock("span", {
					key: 0,
					class: normalizeClass(unref(ns).e("label"))
				}, [renderSlot(_ctx.$slots, "default"), !_ctx.$slots.default ? (openBlock(), createElementBlock(Fragment, { key: 0 }, [createTextVNode(toDisplayString(__props.label), 1)], 64)) : createCommentVNode("v-if", true)], 2)) : createCommentVNode("v-if", true)]),
				_: 3
			}, 8, [
				"for",
				"class",
				"aria-controls",
				"aria-checked",
				"aria-label",
				"onClick"
			]);
		};
	}
});
//#endregion
//#region node_modules/element-plus/es/components/checkbox/src/checkbox-button.vue_vue_type_script_setup_true_lang.mjs
var _hoisted_1 = [
	"name",
	"tabindex",
	"disabled"
];
//#endregion
//#region node_modules/element-plus/es/components/checkbox/src/checkbox-button.mjs
var checkbox_button_default = /* @__PURE__ */ defineComponent({
	name: "ElCheckboxButton",
	__name: "checkbox-button",
	props: checkboxProps,
	emits: checkboxEmits,
	setup(__props) {
		const props = __props;
		const { isFocused, isChecked, isDisabled, checkboxButtonSize, model, actualValue, handleChange } = useCheckbox(props, useSlots());
		const inputBindings = computed(() => {
			if (props.trueValue || props.falseValue || props.trueLabel || props.falseLabel) return {
				"true-value": props.trueValue ?? props.trueLabel ?? true,
				"false-value": props.falseValue ?? props.falseLabel ?? false
			};
			return { value: actualValue.value };
		});
		const checkboxGroup = inject(checkboxGroupContextKey, void 0);
		const ns = useNamespace("checkbox");
		const activeStyle = computed(() => {
			const fillValue = checkboxGroup?.fill?.value ?? "";
			return {
				backgroundColor: fillValue,
				borderColor: fillValue,
				color: checkboxGroup?.textColor?.value ?? "",
				boxShadow: fillValue ? `-1px 0 0 0 ${fillValue}` : void 0
			};
		});
		const labelKls = computed(() => {
			return [
				ns.b("button"),
				ns.bm("button", checkboxButtonSize.value),
				ns.is("disabled", isDisabled.value),
				ns.is("checked", isChecked.value),
				ns.is("focus", isFocused.value)
			];
		});
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("label", { class: normalizeClass(labelKls.value) }, [withDirectives(createElementVNode("input", mergeProps({
				"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => isRef(model) ? model.value = $event : null),
				class: unref(ns).be("button", "original"),
				type: "checkbox",
				name: __props.name,
				tabindex: __props.tabindex,
				disabled: unref(isDisabled)
			}, inputBindings.value, {
				onChange: _cache[1] || (_cache[1] = (...args) => unref(handleChange) && unref(handleChange)(...args)),
				onFocus: _cache[2] || (_cache[2] = ($event) => isFocused.value = true),
				onBlur: _cache[3] || (_cache[3] = ($event) => isFocused.value = false),
				onClick: _cache[4] || (_cache[4] = withModifiers(() => {}, ["stop"]))
			}), null, 16, _hoisted_1), [[vModelCheckbox, unref(model)]]), _ctx.$slots.default || __props.label ? (openBlock(), createElementBlock("span", {
				key: 0,
				class: normalizeClass(unref(ns).be("button", "inner")),
				style: normalizeStyle(unref(isChecked) ? activeStyle.value : void 0)
			}, [renderSlot(_ctx.$slots, "default", {}, () => [createTextVNode(toDisplayString(__props.label), 1)])], 6)) : createCommentVNode("v-if", true)], 2);
		};
	}
});
//#endregion
//#region node_modules/element-plus/es/components/checkbox/src/checkbox-group2.mjs
var checkbox_group_default = /* @__PURE__ */ defineComponent({
	name: "ElCheckboxGroup",
	__name: "checkbox-group",
	props: checkboxGroupProps,
	emits: checkboxGroupEmits,
	setup(__props, { emit: __emit }) {
		const props = __props;
		const emit = __emit;
		const ns = useNamespace("checkbox");
		const checkboxDisabled = useFormDisabled();
		const { formItem } = useFormItem();
		const { inputId: groupId, isLabeledByFormItem } = useFormItemInputId(props, { formItemContext: formItem });
		const changeEvent = async (value) => {
			emit(UPDATE_MODEL_EVENT, value);
			await nextTick();
			emit(CHANGE_EVENT, value);
		};
		const modelValue = computed({
			get() {
				return props.modelValue;
			},
			set(val) {
				changeEvent(val);
			}
		});
		const aliasProps = computed(() => ({
			...checkboxDefaultProps,
			...props.props
		}));
		const getOptionProps = (option) => {
			const { label, value, disabled } = aliasProps.value;
			const base = {
				label: option[label],
				value: option[value],
				disabled: option[disabled]
			};
			return {
				...omit(option, [
					label,
					value,
					disabled
				]),
				...base
			};
		};
		const optionComponent = computed(() => props.type === "button" ? checkbox_button_default : checkbox_default);
		provide(checkboxGroupContextKey, {
			...pick(toRefs(props), [
				"size",
				"min",
				"max",
				"validateEvent",
				"fill",
				"textColor"
			]),
			disabled: checkboxDisabled,
			modelValue,
			changeEvent
		});
		watch(() => props.modelValue, (newVal, oldValue) => {
			if (props.validateEvent && !isEqual(newVal, oldValue)) formItem?.validate("change").catch(NOOP);
		});
		return (_ctx, _cache) => {
			return openBlock(), createBlock(resolveDynamicComponent(__props.tag), {
				id: unref(groupId),
				class: normalizeClass(unref(ns).b("group")),
				role: "group",
				"aria-label": !unref(isLabeledByFormItem) ? __props.ariaLabel || "checkbox-group" : void 0,
				"aria-labelledby": unref(isLabeledByFormItem) ? unref(formItem)?.labelId : void 0
			}, {
				default: withCtx(() => [renderSlot(_ctx.$slots, "default", {}, () => [(openBlock(true), createElementBlock(Fragment, null, renderList(__props.options, (item, index) => {
					return openBlock(), createBlock(resolveDynamicComponent(optionComponent.value), mergeProps({ key: index }, { ref_for: true }, getOptionProps(item)), null, 16);
				}), 128))])]),
				_: 3
			}, 8, [
				"id",
				"class",
				"aria-label",
				"aria-labelledby"
			]);
		};
	}
});
//#endregion
//#region node_modules/element-plus/es/components/checkbox/index.mjs
var ElCheckbox = withInstall(checkbox_default, {
	CheckboxButton: checkbox_button_default,
	CheckboxGroup: checkbox_group_default
});
withNoopInstall(checkbox_button_default);
var ElCheckboxGroup = withNoopInstall(checkbox_group_default);

export { ElCheckbox as E, ElScrollbar as a, ElCheckboxGroup as b, isGreaterThan as i, scrollbarEmits as s };
//# sourceMappingURL=css-DCGp0dw5.mjs.map
