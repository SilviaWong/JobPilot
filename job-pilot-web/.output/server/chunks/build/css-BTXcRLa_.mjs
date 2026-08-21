import { j as isFirefox, u as useNamespace, d as debugWarn, a as isNumber, i as isBoolean, k as useZIndex, f as useId, t as throwError } from '../virtual/entry.mjs';
import { w as withInstall, b as buildProps, i as iconPropType, j as circle_close_default, d as definePropType, V as ValidateComponentsMap, H as view_default, I as hide_default, E as ElIcon, F as isFocusable, a as addUnit, B as hasClass, y as addClass, D as getStyle } from './icon-BTfkZ5_-.mjs';
import { u as useAriaProps } from './arrays-DLJ1qaKZ.mjs';
import { m as mutable } from './typescript-DnijNOm4.mjs';
import { f as useSizeProp, u as useFormItem, d as useFormItemInputId, c as useFormSize, e as useFormDisabled, m as useGlobalConfig } from './css-n73NFgGH.mjs';
import { markRaw, defineComponent, useAttrs, useSlots, computed, shallowRef, ref, watch, nextTick, toRef, openBlock, createElementBlock, normalizeStyle, normalizeClass, unref, createCommentVNode, Fragment, renderSlot, createElementVNode, createBlock, withCtx, resolveDynamicComponent, mergeProps, withModifiers, toDisplayString, createVNode, h, getCurrentInstance, isRef, onScopeDispose } from 'vue';
import { useResizeObserver, isClient, useEventListener, useTimeoutFn } from '@vueuse/core';
import { isNil, fromPairs } from 'lodash-unified';
import { NOOP, isObject, isString, isFunction, isArray } from '@vue/shared';

//#region node_modules/element-plus/es/constants/event.mjs
var UPDATE_MODEL_EVENT = "update:modelValue";
var CHANGE_EVENT = "change";
var INPUT_EVENT = "input";
//#endregion
//#region node_modules/element-plus/es/hooks/use-attrs/index.mjs
var DEFAULT_EXCLUDE_KEYS = ["class", "style"];
var LISTENER_PREFIX = /^on[A-Z]/;
var useAttrs$1 = (params = {}) => {
	const { excludeListeners = false, excludeKeys } = params;
	const allExcludeKeys = computed(() => {
		return (excludeKeys?.value || []).concat(DEFAULT_EXCLUDE_KEYS);
	});
	const instance = getCurrentInstance();
	if (!instance) {
		debugWarn("use-attrs", "getCurrentInstance() returned null. useAttrs() must be called at the top of a setup function");
		return computed(() => ({}));
	}
	return computed(() => fromPairs(Object.entries(instance.proxy?.$attrs).filter(([key]) => !allExcludeKeys.value.includes(key) && !(excludeListeners && LISTENER_PREFIX.test(key)))));
};
//#endregion
//#region node_modules/element-plus/es/utils/raf.mjs
var rAF = (fn) => isClient ? (void 0).requestAnimationFrame(fn) : setTimeout(fn, 16);
var cAF = (handle) => isClient ? (void 0).cancelAnimationFrame(handle) : clearTimeout(handle);
//#endregion
//#region node_modules/element-plus/es/utils/dom/scroll.mjs
var scrollBarWidth;
var getScrollBarWidth = (namespace) => {
	if (!isClient) return 0;
	if (scrollBarWidth !== void 0) return scrollBarWidth;
	const outer = (void 0).createElement("div");
	outer.className = `${namespace}-scrollbar__wrap`;
	outer.style.visibility = "hidden";
	outer.style.width = "100px";
	outer.style.position = "absolute";
	outer.style.top = "-9999px";
	(void 0).body.appendChild(outer);
	const widthNoScroll = outer.offsetWidth;
	outer.style.overflow = "scroll";
	const inner = (void 0).createElement("div");
	inner.style.width = "100%";
	outer.appendChild(inner);
	const widthWithScroll = inner.offsetWidth;
	outer.parentNode?.removeChild(outer);
	scrollBarWidth = widthNoScroll - widthWithScroll;
	return scrollBarWidth;
};
/**
* Scroll with in the container element, positioning the **selected** element at the top
* of the container
*/
function scrollIntoView(container, selected) {
	if (!isClient) return;
	if (!selected) {
		container.scrollTop = 0;
		return;
	}
	const offsetParents = [];
	let pointer = selected.offsetParent;
	while (pointer !== null && container !== pointer && container.contains(pointer)) {
		offsetParents.push(pointer);
		pointer = pointer.offsetParent;
	}
	const top = selected.offsetTop + offsetParents.reduce((prev, curr) => prev + curr.offsetTop, 0);
	const bottom = top + selected.offsetHeight;
	const viewRectTop = container.scrollTop;
	const viewRectBottom = viewRectTop + container.clientHeight;
	if (top < viewRectTop) container.scrollTop = top;
	else if (bottom > viewRectBottom) container.scrollTop = bottom - container.clientHeight;
}
//#endregion
//#region node_modules/element-plus/es/hooks/use-lockscreen/index.mjs
/**
* Hook that monitoring the ref value to lock or unlock the screen.
* When the trigger became true, it assumes modal is now opened and vice versa.
* @param trigger {Ref<boolean>}
*/
var useLockscreen = (trigger, options = {}) => {
	if (!isRef(trigger)) throwError("[useLockscreen]", "You need to pass a ref param to this function");
	const ns = options.ns || useNamespace("popup");
	const hiddenCls = computed(() => ns.bm("parent", "hidden"));
	let scrollBarWidth = 0;
	let withoutHiddenClass = false;
	let cleaned = false;
	const cleanup = () => {
		if (cleaned) return;
		cleaned = true;
		setTimeout(() => {}, 200);
	};
	watch(trigger, (val) => {
		if (!val) {
			cleanup();
			return;
		}
		cleaned = false;
		withoutHiddenClass = !hasClass((void 0).body, hiddenCls.value);
		if (withoutHiddenClass) {
			(void 0).body.style.width;
			addClass((void 0).body, hiddenCls.value);
		}
		scrollBarWidth = getScrollBarWidth(ns.namespace.value);
		const bodyHasOverflow = (void 0).documentElement.clientHeight < (void 0).body.scrollHeight;
		const bodyOverflowY = getStyle((void 0).body, "overflowY");
		if (scrollBarWidth > 0 && (bodyHasOverflow || bodyOverflowY === "scroll") && withoutHiddenClass) (void 0).body.style.width = `calc(100% - ${scrollBarWidth}px)`;
	});
	onScopeDispose(() => cleanup());
};
//#endregion
//#region node_modules/element-plus/es/hooks/use-same-target/index.mjs
var useSameTarget = (handleClick) => {
	if (!handleClick) return {
		onClick: NOOP,
		onMousedown: NOOP,
		onMouseup: NOOP
	};
	let mousedownTarget = false;
	let mouseupTarget = false;
	const onClick = (e) => {
		if (mousedownTarget && mouseupTarget) handleClick(e);
		mousedownTarget = mouseupTarget = false;
	};
	const onMousedown = (e) => {
		mousedownTarget = e.target === e.currentTarget;
	};
	const onMouseup = (e) => {
		mouseupTarget = e.target === e.currentTarget;
	};
	return {
		onClick,
		onMousedown,
		onMouseup
	};
};
//#endregion
//#region node_modules/element-plus/es/hooks/use-cursor/index.mjs
function useCursor(input) {
	let selectionInfo;
	function recordCursor() {
		if (input.value == void 0) return;
		const { selectionStart, selectionEnd, value } = input.value;
		if (selectionStart == null || selectionEnd == null) return;
		selectionInfo = {
			selectionStart,
			selectionEnd,
			value,
			beforeTxt: value.slice(0, Math.max(0, selectionStart)),
			afterTxt: value.slice(Math.max(0, selectionEnd))
		};
	}
	function setCursor() {
		if (input.value == void 0 || selectionInfo == void 0) return;
		const { value } = input.value;
		const { beforeTxt, afterTxt, selectionStart } = selectionInfo;
		if (beforeTxt == void 0 || afterTxt == void 0 || selectionStart == void 0) return;
		let startPos = value.length;
		if (value.endsWith(afterTxt)) startPos = value.length - afterTxt.length;
		else if (value.startsWith(beforeTxt)) startPos = beforeTxt.length;
		else {
			const beforeLastChar = beforeTxt[selectionStart - 1];
			const newIndex = value.indexOf(beforeLastChar, selectionStart - 1);
			if (newIndex !== -1) startPos = newIndex + 1;
		}
		input.value.setSelectionRange(startPos, startPos);
	}
	return [recordCursor, setCursor];
}
//#endregion
//#region node_modules/element-plus/es/hooks/use-focus-controller/index.mjs
function useFocusController(target, { disabled, beforeFocus, afterFocus, beforeBlur, afterBlur } = {}) {
	const { emit } = getCurrentInstance();
	const wrapperRef = shallowRef();
	const isFocused = ref(false);
	const handleFocus = (event) => {
		const cancelFocus = isFunction(beforeFocus) ? beforeFocus(event) : false;
		if (unref(disabled) || isFocused.value || cancelFocus) return;
		isFocused.value = true;
		emit("focus", event);
		afterFocus?.();
	};
	const handleBlur = (event) => {
		const cancelBlur = isFunction(beforeBlur) ? beforeBlur(event) : false;
		if (unref(disabled) || event.relatedTarget && wrapperRef.value?.contains(event.relatedTarget) || cancelBlur) return;
		isFocused.value = false;
		emit("blur", event);
		afterBlur?.();
	};
	const handleClick = (event) => {
		if (unref(disabled) || isFocusable(event.target) || wrapperRef.value?.contains((void 0).activeElement) && wrapperRef.value !== (void 0).activeElement) return;
		target.value?.focus();
	};
	watch([wrapperRef, () => unref(disabled)], ([el, disabled]) => {
		if (!el) return;
		if (disabled) el.removeAttribute("tabindex");
		else el.setAttribute("tabindex", "-1");
	});
	useEventListener(wrapperRef, "focus", handleFocus, true);
	useEventListener(wrapperRef, "blur", handleBlur, true);
	useEventListener(wrapperRef, "click", handleClick, true);
	return {
		isFocused,
		/** Avoid using wrapperRef and handleFocus/handleBlur together */
		wrapperRef,
		handleFocus,
		handleBlur
	};
}
//#endregion
//#region node_modules/element-plus/es/hooks/use-composition/index.mjs
function useComposition({ afterComposition, emit }) {
	const isComposing = ref(false);
	const handleCompositionStart = (event) => {
		emit?.("compositionstart", event);
		isComposing.value = true;
	};
	const handleCompositionUpdate = (event) => {
		emit?.("compositionupdate", event);
		isComposing.value = true;
	};
	const handleCompositionEnd = (event) => {
		emit?.("compositionend", event);
		if (isComposing.value) {
			isComposing.value = false;
			nextTick(() => afterComposition(event));
		}
	};
	const handleComposition = (event) => {
		event.type === "compositionend" ? handleCompositionEnd(event) : handleCompositionUpdate(event);
	};
	return {
		isComposing,
		handleComposition,
		handleCompositionStart,
		handleCompositionUpdate,
		handleCompositionEnd
	};
}
//#endregion
//#region node_modules/element-plus/es/components/input/src/input.mjs
/**
* @deprecated Removed after 3.0.0, Use `InputProps` instead.
*/
var inputProps = buildProps({
	/**
	* @description native input id
	*/
	id: {
		type: String,
		default: void 0
	},
	/**
	* @description input box size
	*/
	size: useSizeProp,
	/**
	* @description whether to disable
	*/
	disabled: {
		type: Boolean,
		default: void 0
	},
	/**
	* @description binding value
	*/
	modelValue: {
		type: definePropType([
			String,
			Number,
			Object
		]),
		default: ""
	},
	/**
	* @description v-model modifiers, reference [Vue modifiers](https://vuejs.org/guide/essentials/forms.html#modifiers)
	*/
	modelModifiers: {
		type: definePropType(Object),
		default: () => ({})
	},
	/**
	* @description same as `maxlength` in native input
	*/
	maxlength: { type: [String, Number] },
	/**
	* @description same as `minlength` in native input
	*/
	minlength: { type: [String, Number] },
	/**
	* @description type of input, see more in [MDN](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input#Form_%3Cinput%3E_types)
	*/
	type: {
		type: definePropType(String),
		default: "text"
	},
	/**
	* @description control the resizability
	*/
	resize: {
		type: String,
		values: [
			"none",
			"both",
			"horizontal",
			"vertical"
		]
	},
	/**
	* @description whether textarea has an adaptive height
	*/
	autosize: {
		type: definePropType([Boolean, Object]),
		default: false
	},
	/**
	* @description native input autocomplete
	*/
	autocomplete: {
		type: definePropType(String),
		default: "off"
	},
	/**
	* @description format content
	*/
	formatter: { type: Function },
	/**
	* @description parse content
	*/
	parser: { type: Function },
	/**
	* @description placeholder
	*/
	placeholder: { type: String },
	/**
	* @description native input form
	*/
	form: { type: String },
	/**
	* @description native input readonly
	*/
	readonly: Boolean,
	/**
	* @description whether to show clear button
	*/
	clearable: Boolean,
	/**
	* @description custom clear icon component
	*/
	clearIcon: {
		type: iconPropType,
		default: circle_close_default
	},
	/**
	* @description toggleable password input
	*/
	showPassword: Boolean,
	/**
	* @description word count
	*/
	showWordLimit: Boolean,
	/**
	* @description word count position, valid when `show-word-limit` is true
	*/
	wordLimitPosition: {
		type: String,
		values: ["inside", "outside"],
		default: "inside"
	},
	/**
	* @description suffix icon
	*/
	suffixIcon: { type: iconPropType },
	/**
	* @description prefix icon
	*/
	prefixIcon: { type: iconPropType },
	/**
	* @description container role, internal properties provided for use by the picker component
	*/
	containerRole: {
		type: String,
		default: void 0
	},
	/**
	* @description input tabindex
	*/
	tabindex: {
		type: [String, Number],
		default: 0
	},
	/**
	* @description whether to trigger form validation
	*/
	validateEvent: {
		type: Boolean,
		default: true
	},
	/**
	* @description input or textarea element style
	*/
	inputStyle: {
		type: definePropType([
			Object,
			Array,
			String,
			Boolean
		]),
		default: () => mutable({})
	},
	/**
	* @description Count graphemes of input value. If it's set, native maxlength and minlength won't be used.
	*/
	countGraphemes: { type: definePropType(Function) },
	/**
	* @description native input autofocus
	*/
	autofocus: Boolean,
	rows: {
		type: Number,
		default: 2
	},
	...useAriaProps(["ariaLabel"]),
	/**
	* @description native input mode for virtual keyboards
	*/
	inputmode: {
		type: definePropType(String),
		default: void 0
	},
	/**
	* @description same as `name` in native input
	*/
	name: String
});
var inputEmits = {
	[UPDATE_MODEL_EVENT]: (value) => isString(value),
	input: (value) => isString(value),
	change: (value, evt) => isString(value) && (evt instanceof Event || evt === void 0),
	focus: (evt) => evt instanceof FocusEvent,
	blur: (evt) => evt instanceof FocusEvent,
	clear: (evt) => evt === void 0 || evt instanceof MouseEvent,
	mouseleave: (evt) => evt instanceof MouseEvent,
	mouseenter: (evt) => evt instanceof MouseEvent,
	keydown: (evt) => evt instanceof Event,
	compositionstart: (evt) => evt instanceof CompositionEvent,
	compositionupdate: (evt) => evt instanceof CompositionEvent,
	compositionend: (evt) => evt instanceof CompositionEvent
};
markRaw(circle_close_default);
//#endregion
//#region node_modules/element-plus/es/components/input/src/utils.mjs
var hiddenTextarea = void 0;
var HIDDEN_STYLE = {
	height: "0",
	visibility: "hidden",
	overflow: isFirefox() ? "" : "hidden",
	position: "absolute",
	"z-index": "-1000",
	top: "0",
	right: "0"
};
var CONTEXT_STYLE = [
	"letter-spacing",
	"line-height",
	"padding-top",
	"padding-bottom",
	"font-family",
	"font-weight",
	"font-size",
	"text-rendering",
	"text-transform",
	"width",
	"text-indent",
	"padding-left",
	"padding-right",
	"border-width",
	"box-sizing",
	"word-break"
];
var looseToNumber = (val) => {
	const n = Number.parseFloat(val);
	return Number.isNaN(n) ? val : n;
};
function calculateNodeStyling(targetElement) {
	const style = (void 0).getComputedStyle(targetElement);
	const boxSizing = style.getPropertyValue("box-sizing");
	const paddingSize = Number.parseFloat(style.getPropertyValue("padding-bottom")) + Number.parseFloat(style.getPropertyValue("padding-top"));
	const borderSize = Number.parseFloat(style.getPropertyValue("border-bottom-width")) + Number.parseFloat(style.getPropertyValue("border-top-width"));
	return {
		contextStyle: CONTEXT_STYLE.map((name) => [name, style.getPropertyValue(name)]),
		paddingSize,
		borderSize,
		boxSizing
	};
}
function calcTextareaHeight(targetElement, minRows = 1, maxRows) {
	if (!hiddenTextarea) {
		hiddenTextarea = (void 0).createElement("textarea");
		let hostNode = (void 0).body;
		if (!isFirefox() && targetElement.parentNode) hostNode = targetElement.parentNode;
		hostNode.appendChild(hiddenTextarea);
	}
	const { paddingSize, borderSize, boxSizing, contextStyle } = calculateNodeStyling(targetElement);
	contextStyle.forEach(([key, value]) => hiddenTextarea?.style.setProperty(key, value));
	Object.entries(HIDDEN_STYLE).forEach(([key, value]) => hiddenTextarea?.style.setProperty(key, value, "important"));
	hiddenTextarea.value = targetElement.value || targetElement.placeholder || "";
	let height = hiddenTextarea.scrollHeight;
	const result = {};
	if (boxSizing === "border-box") height = height + borderSize;
	else if (boxSizing === "content-box") height = height - paddingSize;
	hiddenTextarea.value = "";
	const singleRowHeight = hiddenTextarea.scrollHeight - paddingSize;
	if (isNumber(minRows)) {
		let minHeight = singleRowHeight * minRows;
		if (boxSizing === "border-box") minHeight = minHeight + paddingSize + borderSize;
		height = Math.max(minHeight, height);
		result.minHeight = `${minHeight}px`;
	}
	if (isNumber(maxRows)) {
		let maxHeight = singleRowHeight * maxRows;
		if (boxSizing === "border-box") maxHeight = maxHeight + paddingSize + borderSize;
		height = Math.min(maxHeight, height);
	}
	result.height = `${height}px`;
	hiddenTextarea.parentNode?.removeChild(hiddenTextarea);
	hiddenTextarea = void 0;
	return result;
}
//#endregion
//#region node_modules/element-plus/es/components/input/src/input.vue_vue_type_script_setup_true_lang.mjs
var _hoisted_1 = [
	"id",
	"name",
	"minlength",
	"maxlength",
	"type",
	"disabled",
	"readonly",
	"autocomplete",
	"tabindex",
	"aria-label",
	"placeholder",
	"form",
	"autofocus",
	"role",
	"inputmode"
];
var _hoisted_2 = [
	"id",
	"name",
	"minlength",
	"maxlength",
	"tabindex",
	"disabled",
	"readonly",
	"autocomplete",
	"aria-label",
	"placeholder",
	"form",
	"autofocus",
	"rows",
	"role",
	"inputmode"
];
//#endregion
//#region node_modules/element-plus/es/components/input/index.mjs
var ElInput = withInstall(/* @__PURE__ */ defineComponent({
	name: "ElInput",
	inheritAttrs: false,
	__name: "input",
	props: inputProps,
	emits: inputEmits,
	setup(__props, { expose: __expose, emit: __emit }) {
		const props = __props;
		const emit = __emit;
		const rawAttrs = useAttrs();
		const slots = useSlots();
		const containerKls = computed(() => [
			props.type === "textarea" ? nsTextarea.b() : nsInput.b(),
			nsInput.m(inputSize.value),
			nsInput.is("disabled", inputDisabled.value),
			nsInput.is("exceed", inputExceed.value),
			{
				[nsInput.b("group")]: slots.prepend || slots.append,
				[nsInput.m("prefix")]: slots.prefix || props.prefixIcon,
				[nsInput.m("suffix")]: slots.suffix || props.suffixIcon || props.clearable || props.showPassword,
				[nsInput.bm("suffix", "password-clear")]: showClear.value && showPwdVisible.value,
				[nsInput.b("hidden")]: props.type === "hidden"
			},
			rawAttrs.class
		]);
		const wrapperKls = computed(() => [nsInput.e("wrapper"), nsInput.is("focus", isFocused.value)]);
		const attrs = useAttrs$1();
		const maxlength = computed(() => props.maxlength?.toString());
		const { form: elForm, formItem: elFormItem } = useFormItem();
		const { inputId } = useFormItemInputId(props, { formItemContext: elFormItem });
		const inputSize = useFormSize();
		const inputDisabled = useFormDisabled();
		const nsInput = useNamespace("input");
		const nsTextarea = useNamespace("textarea");
		const input = shallowRef();
		const textarea = shallowRef();
		const hovering = ref(false);
		const passwordVisible = ref(false);
		const countStyle = ref();
		const clearIconStyle = ref();
		const textareaCalcStyle = shallowRef(props.inputStyle);
		const saveValue = ref("");
		const textareaHeight = ref();
		const _ref = computed(() => input.value || textarea.value);
		const { wrapperRef, isFocused, handleFocus, handleBlur } = useFocusController(_ref, {
			disabled: inputDisabled,
			afterBlur() {
				if (props.validateEvent) elFormItem?.validate?.("blur").catch(NOOP);
			}
		});
		const needStatusIcon = computed(() => elForm?.statusIcon ?? false);
		const validateState = computed(() => elFormItem?.validateState || "");
		const validateIcon = computed(() => validateState.value && ValidateComponentsMap[validateState.value]);
		const passwordIcon = computed(() => passwordVisible.value ? view_default : hide_default);
		const containerStyle = computed(() => [rawAttrs.style]);
		const textareaStyle = computed(() => [
			props.inputStyle,
			textareaCalcStyle.value,
			{ resize: props.resize },
			textareaHeight.value ? { height: textareaHeight.value } : void 0
		]);
		const nativeInputValue = computed(() => isNil(props.modelValue) ? "" : String(props.modelValue));
		const renderClear = computed(() => props.clearable && !inputDisabled.value && !props.readonly);
		const showClear = computed(() => renderClear.value && !!nativeInputValue.value && (isFocused.value || hovering.value));
		const showPwdVisible = computed(() => props.showPassword && !inputDisabled.value && !!nativeInputValue.value);
		const isWordLimitVisible = computed(() => props.showWordLimit && !!maxlength.value && (props.type === "text" || props.type === "textarea") && !inputDisabled.value && !props.readonly && !props.showPassword);
		const textLength = computed(() => {
			if (props.countGraphemes && props.showWordLimit) return props.countGraphemes(nativeInputValue.value);
			return nativeInputValue.value.length;
		});
		const inputExceed = computed(() => !!isWordLimitVisible.value && textLength.value > Number(maxlength.value));
		const suffixVisible = computed(() => !!slots.suffix || !!props.suffixIcon || props.clearable || props.showPassword || isWordLimitVisible.value || !!validateState.value && needStatusIcon.value);
		const hasModelModifiers = computed(() => !!Object.keys(props.modelModifiers).length);
		const [recordCursor, setCursor] = useCursor(input);
		let rAFId;
		useResizeObserver(textarea, (entries) => {
			onceInitSizeTextarea();
			if (!isWordLimitVisible.value && !renderClear.value || props.resize !== "both" && props.resize !== "horizontal") return;
			const { width } = entries[0].target.getBoundingClientRect();
			const updateStyle = () => {
				rAFId = void 0;
				countStyle.value = { 
				/** right: 100% - (width - right(10)) */
right: `calc(100% - ${width - 10}px)` };
				clearIconStyle.value = { 
				/** right: 100% - (width - right(11)) */
right: `calc(100% - ${width - 11}px)` };
			};
			rAFId && cAF(rAFId);
			rAFId = rAF(updateStyle);
		});
		const resizeTextarea = () => {
			const { type, autosize } = props;
			if (!isClient || type !== "textarea" || !textarea.value) return;
			if (autosize) {
				const minRows = isObject(autosize) ? autosize.minRows : void 0;
				const maxRows = isObject(autosize) ? autosize.maxRows : void 0;
				const textareaStyle = calcTextareaHeight(textarea.value, minRows, maxRows);
				textareaCalcStyle.value = {
					overflowY: "hidden",
					...textareaStyle
				};
				nextTick(() => {
					textarea.value.offsetHeight;
					textareaCalcStyle.value = textareaStyle;
				});
			} else textareaCalcStyle.value = { minHeight: calcTextareaHeight(textarea.value).minHeight };
		};
		const createOnceInitResize = (resizeTextarea) => {
			let isInit = false;
			return () => {
				if (isInit || !props.autosize) {
					if (props.resize !== "none") setTimeout(() => {
						textareaHeight.value = textarea.value?.style.height;
					});
					return;
				}
				if (!(textarea.value?.offsetParent === null)) {
					setTimeout(resizeTextarea);
					isInit = true;
				}
			};
		};
		const onceInitSizeTextarea = createOnceInitResize(resizeTextarea);
		const setNativeInputValue = () => {
			const input = _ref.value;
			const formatterValue = props.formatter ? props.formatter(nativeInputValue.value) : nativeInputValue.value;
			if (!input || input.value === formatterValue || props.type === "file") return;
			input.value = formatterValue;
		};
		const formatValue = (value) => {
			const { trim, number } = props.modelModifiers;
			if (trim) value = value.trim();
			if (number) value = `${looseToNumber(value)}`;
			if (props.formatter && props.parser) value = props.parser(value);
			return value;
		};
		const handleInput = async (event) => {
			if (isComposing.value) return;
			const { lazy } = props.modelModifiers;
			let { value } = event.target;
			let shouldForceNativeUpdate = false;
			if (lazy) {
				emit(INPUT_EVENT, value);
				return;
			}
			value = formatValue(value);
			if (props.countGraphemes && maxlength.value != null) {
				const limit = Number(maxlength.value);
				const graphemes = props.countGraphemes(value);
				const saveGraphemes = props.countGraphemes(saveValue.value);
				if (graphemes > limit && graphemes > saveGraphemes) if (saveGraphemes > limit) {
					value = saveValue.value;
					shouldForceNativeUpdate = true;
				} else {
					const prevValue = saveValue.value;
					const nextValue = value;
					let prefixLen = 0;
					while (prefixLen < prevValue.length && prefixLen < nextValue.length && prevValue[prefixLen] === nextValue[prefixLen]) prefixLen++;
					let prevSuffixIndex = prevValue.length;
					let nextSuffixIndex = nextValue.length;
					while (prevSuffixIndex > prefixLen && nextSuffixIndex > prefixLen && prevValue[prevSuffixIndex - 1] === nextValue[nextSuffixIndex - 1]) {
						prevSuffixIndex--;
						nextSuffixIndex--;
					}
					const before = nextValue.slice(0, prefixLen);
					const removed = prevValue.slice(prefixLen, prevSuffixIndex);
					const inserted = nextValue.slice(prefixLen, nextSuffixIndex);
					const after = nextValue.slice(nextSuffixIndex);
					const baseCount = saveGraphemes - props.countGraphemes(removed);
					const availableInserted = Math.max(0, limit - baseCount);
					let acceptedInserted = "";
					if (availableInserted > 0) if (typeof Intl !== "undefined" && "Segmenter" in Intl) {
						const segmenter = new Intl.Segmenter(void 0, { granularity: "grapheme" });
						for (const { segment } of segmenter.segment(inserted)) {
							const candidate = acceptedInserted + segment;
							if (props.countGraphemes(candidate) > availableInserted) break;
							acceptedInserted = candidate;
						}
					} else for (const char of Array.from(inserted)) {
						const candidate = acceptedInserted + char;
						if (props.countGraphemes(candidate) > availableInserted) break;
						acceptedInserted = candidate;
					}
					value = before + acceptedInserted + after;
					shouldForceNativeUpdate = true;
				}
			}
			if (String(value) === nativeInputValue.value) {
				if (props.formatter || shouldForceNativeUpdate) {
					const target = event.target;
					const blockedValue = target.value;
					const selectionStart = target.selectionStart;
					const selectionEnd = target.selectionEnd;
					setNativeInputValue();
					if (shouldForceNativeUpdate && _ref.value && selectionStart != null && selectionEnd != null) {
						const restoredValue = _ref.value.value;
						const afterTxt = blockedValue.slice(Math.max(0, selectionEnd));
						let caretPos = Math.min(selectionStart, restoredValue.length);
						if (afterTxt && restoredValue.endsWith(afterTxt)) caretPos = restoredValue.length - afterTxt.length;
						_ref.value.setSelectionRange(caretPos, caretPos);
					}
				}
				return;
			}
			saveValue.value = value;
			recordCursor();
			emit(UPDATE_MODEL_EVENT, value);
			emit(INPUT_EVENT, value);
			await nextTick();
			if (props.formatter && props.parser || !hasModelModifiers.value) setNativeInputValue();
			setCursor();
		};
		const handleChange = async (event) => {
			let { value } = event.target;
			value = formatValue(value);
			if (props.modelModifiers.lazy) emit(UPDATE_MODEL_EVENT, value);
			emit(CHANGE_EVENT, value, event);
			await nextTick();
			setNativeInputValue();
		};
		const { isComposing, handleCompositionStart, handleCompositionUpdate, handleCompositionEnd } = useComposition({
			emit,
			afterComposition: handleInput
		});
		const handlePasswordVisible = () => {
			passwordVisible.value = !passwordVisible.value;
		};
		const focus = () => _ref.value?.focus();
		const blur = () => _ref.value?.blur();
		const handleMouseLeave = (evt) => {
			hovering.value = false;
			emit("mouseleave", evt);
		};
		const handleMouseEnter = (evt) => {
			hovering.value = true;
			emit("mouseenter", evt);
		};
		const handleKeydown = (evt) => {
			emit("keydown", evt);
		};
		const select = () => {
			_ref.value?.select();
		};
		const clear = (evt) => {
			emit(UPDATE_MODEL_EVENT, "");
			emit(CHANGE_EVENT, "");
			emit("clear", evt);
			emit(INPUT_EVENT, "");
		};
		watch(() => props.modelValue, () => {
			nextTick(() => {
				resizeTextarea();
				if (props.autosize) textareaHeight.value = void 0;
			});
			if (props.validateEvent) elFormItem?.validate?.("change").catch(NOOP);
		});
		watch(() => nativeInputValue.value, (val) => {
			saveValue.value = val;
		}, { immediate: true });
		watch(nativeInputValue, (newValue) => {
			if (!_ref.value) return;
			const { trim, number } = props.modelModifiers;
			const elValue = _ref.value.value;
			const displayValue = (number || props.type === "number") && !/^0\d/.test(elValue) ? `${looseToNumber(elValue)}` : elValue;
			if (displayValue === newValue) return;
			if ((void 0).activeElement === _ref.value && _ref.value.type !== "range") {
				if (trim && displayValue.trim() === newValue) return;
			}
			setNativeInputValue();
		});
		watch(() => props.type, async () => {
			await nextTick();
			setNativeInputValue();
			resizeTextarea();
		});
		__expose({
			/** @description HTML input element */
			input,
			/** @description HTML textarea element */
			textarea,
			/** @description HTML element, input or textarea */
			ref: _ref,
			/** @description style of textarea. */
			textareaStyle,
			/** @description from props (used on unit test) */
			autosize: toRef(props, "autosize"),
			/** @description is input composing */
			isComposing,
			/** @description whether the password is visible */
			passwordVisible,
			/** @description HTML input element native method */
			focus,
			/** @description HTML input element native method */
			blur,
			/** @description HTML input element native method */
			select,
			/** @description clear input value */
			clear,
			/** @description resize textarea. */
			resizeTextarea
		});
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("div", {
				class: normalizeClass([containerKls.value, {
					[unref(nsInput).bm("group", "append")]: _ctx.$slots.append,
					[unref(nsInput).bm("group", "prepend")]: _ctx.$slots.prepend
				}]),
				style: normalizeStyle(containerStyle.value),
				onMouseenter: handleMouseEnter,
				onMouseleave: handleMouseLeave
			}, [createCommentVNode(" input "), __props.type !== "textarea" ? (openBlock(), createElementBlock(Fragment, { key: 0 }, [
				createCommentVNode(" prepend slot "),
				_ctx.$slots.prepend ? (openBlock(), createElementBlock("div", {
					key: 0,
					class: normalizeClass(unref(nsInput).be("group", "prepend"))
				}, [renderSlot(_ctx.$slots, "prepend")], 2)) : createCommentVNode("v-if", true),
				createElementVNode("div", {
					ref_key: "wrapperRef",
					ref: wrapperRef,
					class: normalizeClass(wrapperKls.value)
				}, [
					createCommentVNode(" prefix slot "),
					_ctx.$slots.prefix || __props.prefixIcon ? (openBlock(), createElementBlock("span", {
						key: 0,
						class: normalizeClass(unref(nsInput).e("prefix"))
					}, [createElementVNode("span", { class: normalizeClass(unref(nsInput).e("prefix-inner")) }, [renderSlot(_ctx.$slots, "prefix"), __props.prefixIcon ? (openBlock(), createBlock(unref(ElIcon), {
						key: 0,
						class: normalizeClass(unref(nsInput).e("icon"))
					}, {
						default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(__props.prefixIcon)))]),
						_: 1
					}, 8, ["class"])) : createCommentVNode("v-if", true)], 2)], 2)) : createCommentVNode("v-if", true),
					createElementVNode("input", mergeProps({
						id: unref(inputId),
						ref_key: "input",
						ref: input,
						class: unref(nsInput).e("inner")
					}, unref(attrs), {
						name: __props.name,
						minlength: __props.countGraphemes ? void 0 : __props.minlength,
						maxlength: __props.countGraphemes ? void 0 : maxlength.value,
						type: __props.showPassword ? passwordVisible.value ? "text" : "password" : __props.type,
						disabled: unref(inputDisabled),
						readonly: __props.readonly,
						autocomplete: __props.autocomplete,
						tabindex: __props.tabindex,
						"aria-label": __props.ariaLabel,
						placeholder: __props.placeholder,
						style: __props.inputStyle,
						form: __props.form,
						autofocus: __props.autofocus,
						role: __props.containerRole,
						inputmode: __props.inputmode,
						onCompositionstart: _cache[0] || (_cache[0] = (...args) => unref(handleCompositionStart) && unref(handleCompositionStart)(...args)),
						onCompositionupdate: _cache[1] || (_cache[1] = (...args) => unref(handleCompositionUpdate) && unref(handleCompositionUpdate)(...args)),
						onCompositionend: _cache[2] || (_cache[2] = (...args) => unref(handleCompositionEnd) && unref(handleCompositionEnd)(...args)),
						onInput: handleInput,
						onChange: handleChange,
						onKeydown: handleKeydown
					}), null, 16, _hoisted_1),
					createCommentVNode(" suffix slot "),
					suffixVisible.value ? (openBlock(), createElementBlock("span", {
						key: 1,
						class: normalizeClass(unref(nsInput).e("suffix"))
					}, [createElementVNode("span", { class: normalizeClass(unref(nsInput).e("suffix-inner")) }, [
						renderClear.value ? (openBlock(), createBlock(unref(ElIcon), {
							key: 0,
							class: normalizeClass([unref(nsInput).e("icon"), unref(nsInput).e("clear")]),
							style: normalizeStyle({ visibility: showClear.value ? "visible" : "hidden" }),
							onMousedown: withModifiers(unref(NOOP), ["prevent"]),
							onClick: clear
						}, {
							default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(__props.clearIcon)))]),
							_: 1
						}, 8, [
							"class",
							"style",
							"onMousedown"
						])) : createCommentVNode("v-if", true),
						!showClear.value || !showPwdVisible.value || !isWordLimitVisible.value ? (openBlock(), createElementBlock(Fragment, { key: 1 }, [renderSlot(_ctx.$slots, "suffix"), __props.suffixIcon ? (openBlock(), createBlock(unref(ElIcon), {
							key: 0,
							class: normalizeClass(unref(nsInput).e("icon"))
						}, {
							default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(__props.suffixIcon)))]),
							_: 1
						}, 8, ["class"])) : createCommentVNode("v-if", true)], 64)) : createCommentVNode("v-if", true),
						showPwdVisible.value ? (openBlock(), createBlock(unref(ElIcon), {
							key: 2,
							class: normalizeClass([unref(nsInput).e("icon"), unref(nsInput).e("password")]),
							onClick: handlePasswordVisible,
							onMousedown: withModifiers(unref(NOOP), ["prevent"]),
							onMouseup: withModifiers(unref(NOOP), ["prevent"])
						}, {
							default: withCtx(() => [renderSlot(_ctx.$slots, "password-icon", { visible: passwordVisible.value }, () => [(openBlock(), createBlock(resolveDynamicComponent(passwordIcon.value)))])]),
							_: 3
						}, 8, [
							"class",
							"onMousedown",
							"onMouseup"
						])) : createCommentVNode("v-if", true),
						isWordLimitVisible.value ? (openBlock(), createElementBlock("span", {
							key: 3,
							class: normalizeClass([unref(nsInput).e("count"), unref(nsInput).is("outside", __props.wordLimitPosition === "outside")])
						}, [createElementVNode("span", { class: normalizeClass(unref(nsInput).e("count-inner")) }, toDisplayString(textLength.value) + " / " + toDisplayString(maxlength.value), 3)], 2)) : createCommentVNode("v-if", true),
						validateState.value && validateIcon.value && needStatusIcon.value ? (openBlock(), createBlock(unref(ElIcon), {
							key: 4,
							class: normalizeClass([
								unref(nsInput).e("icon"),
								unref(nsInput).e("validateIcon"),
								unref(nsInput).is("loading", validateState.value === "validating")
							])
						}, {
							default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(validateIcon.value)))]),
							_: 1
						}, 8, ["class"])) : createCommentVNode("v-if", true)
					], 2)], 2)) : createCommentVNode("v-if", true)
				], 2),
				createCommentVNode(" append slot "),
				_ctx.$slots.append ? (openBlock(), createElementBlock("div", {
					key: 1,
					class: normalizeClass(unref(nsInput).be("group", "append"))
				}, [renderSlot(_ctx.$slots, "append")], 2)) : createCommentVNode("v-if", true)
			], 64)) : (openBlock(), createElementBlock(Fragment, { key: 1 }, [
				createCommentVNode(" textarea "),
				createElementVNode("textarea", mergeProps({
					id: unref(inputId),
					ref_key: "textarea",
					ref: textarea,
					class: [
						unref(nsTextarea).e("inner"),
						unref(nsInput).is("focus", unref(isFocused)),
						unref(nsTextarea).is("clearable", __props.clearable)
					]
				}, unref(attrs), {
					name: __props.name,
					minlength: __props.countGraphemes ? void 0 : __props.minlength,
					maxlength: __props.countGraphemes ? void 0 : maxlength.value,
					tabindex: __props.tabindex,
					disabled: unref(inputDisabled),
					readonly: __props.readonly,
					autocomplete: __props.autocomplete,
					style: textareaStyle.value,
					"aria-label": __props.ariaLabel,
					placeholder: __props.placeholder,
					form: __props.form,
					autofocus: __props.autofocus,
					rows: __props.rows,
					role: __props.containerRole,
					inputmode: __props.inputmode,
					onCompositionstart: _cache[3] || (_cache[3] = (...args) => unref(handleCompositionStart) && unref(handleCompositionStart)(...args)),
					onCompositionupdate: _cache[4] || (_cache[4] = (...args) => unref(handleCompositionUpdate) && unref(handleCompositionUpdate)(...args)),
					onCompositionend: _cache[5] || (_cache[5] = (...args) => unref(handleCompositionEnd) && unref(handleCompositionEnd)(...args)),
					onInput: handleInput,
					onFocus: _cache[6] || (_cache[6] = (...args) => unref(handleFocus) && unref(handleFocus)(...args)),
					onBlur: _cache[7] || (_cache[7] = (...args) => unref(handleBlur) && unref(handleBlur)(...args)),
					onChange: handleChange,
					onKeydown: handleKeydown
				}), null, 16, _hoisted_2),
				showClear.value ? (openBlock(), createBlock(unref(ElIcon), {
					key: 0,
					class: normalizeClass([unref(nsTextarea).e("icon"), unref(nsTextarea).e("clear")]),
					style: normalizeStyle(clearIconStyle.value),
					onMousedown: withModifiers(unref(NOOP), ["prevent"]),
					onClick: clear
				}, {
					default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(__props.clearIcon)))]),
					_: 1
				}, 8, [
					"class",
					"style",
					"onMousedown"
				])) : createCommentVNode("v-if", true),
				isWordLimitVisible.value ? (openBlock(), createElementBlock("span", {
					key: 1,
					style: normalizeStyle(countStyle.value),
					class: normalizeClass([unref(nsInput).e("count"), unref(nsInput).is("outside", __props.wordLimitPosition === "outside")])
				}, toDisplayString(textLength.value) + " / " + toDisplayString(maxlength.value), 7)) : createCommentVNode("v-if", true)
			], 64))], 38);
		};
	}
}));
//#endregion
//#region node_modules/element-plus/es/components/dialog/src/dialog-content.mjs
/**
* @deprecated Removed after 3.0.0, Use `DialogContentProps` instead.
*/
var dialogContentProps = buildProps({
	/**
	* @description whether to align the header and footer in center
	*/
	center: Boolean,
	/**
	* @description whether to align the dialog both horizontally and vertically
	*/
	alignCenter: {
		type: Boolean,
		default: void 0
	},
	/**
	* @description custom close icon, default is Close
	*/
	closeIcon: { type: iconPropType },
	/**
	* @description enable dragging feature for Dialog
	*/
	draggable: {
		type: Boolean,
		default: void 0
	},
	/**
	* @description draggable Dialog can overflow the viewport
	*/
	overflow: {
		type: Boolean,
		default: void 0
	},
	/**
	* @description whether the Dialog takes up full screen
	*/
	fullscreen: Boolean,
	/**
	* @description custom class names for header wrapper
	*/
	headerClass: String,
	/**
	* @description custom class names for body wrapper
	*/
	bodyClass: String,
	/**
	* @description custom class names for footer wrapper
	*/
	footerClass: String,
	/**
	* @description whether to show a close button
	*/
	showClose: {
		type: Boolean,
		default: true
	},
	/**
	* @description title of Dialog. Can also be passed with a named slot (see the following table)
	*/
	title: {
		type: String,
		default: ""
	},
	/**
	* @description header's aria-level attribute
	*/
	ariaLevel: {
		type: String,
		default: "2"
	}
});
var dialogContentEmits = { close: () => true };
//#endregion
//#region node_modules/element-plus/es/components/dialog/src/dialog.mjs
/**
* @deprecated Removed after 3.0.0, Use `DialogProps` instead.
*/
var dialogProps = buildProps({
	...dialogContentProps,
	/**
	* @description whether to append Dialog itself to body. A nested Dialog should have this attribute set to `true`
	*/
	appendToBody: Boolean,
	/**
	* @description which element the Dialog appends to
	*/
	appendTo: {
		type: definePropType([String, Object]),
		default: "body"
	},
	/**
	* @description callback before Dialog closes, and it will prevent Dialog from closing, use done to close the dialog
	*/
	beforeClose: { type: definePropType(Function) },
	/**
	* @description destroy elements in Dialog when closed
	*/
	destroyOnClose: Boolean,
	/**
	* @description whether the Dialog can be closed by clicking the mask
	*/
	closeOnClickModal: {
		type: Boolean,
		default: true
	},
	/**
	* @description whether the Dialog can be closed by pressing ESC
	*/
	closeOnPressEscape: {
		type: Boolean,
		default: true
	},
	/**
	* @description whether scroll of body is disabled while Dialog is displayed
	*/
	lockScroll: {
		type: Boolean,
		default: true
	},
	/**
	* @description whether a mask is displayed
	*/
	modal: {
		type: Boolean,
		default: true
	},
	/**
	* @description whether the mask is penetrable
	*/
	modalPenetrable: Boolean,
	/**
	* @description the Time(milliseconds) before open
	*/
	openDelay: {
		type: Number,
		default: 0
	},
	/**
	* @description the Time(milliseconds) before close
	*/
	closeDelay: {
		type: Number,
		default: 0
	},
	/**
	* @description value for `margin-top` of Dialog CSS, default is 15vh
	*/
	top: { type: String },
	/**
	* @description visibility of Dialog
	*/
	modelValue: Boolean,
	/**
	* @description custom class names for mask
	*/
	modalClass: String,
	/**
	* @description custom class names for header wrapper
	*/
	headerClass: String,
	/**
	* @description custom class names for body wrapper
	*/
	bodyClass: String,
	/**
	* @description custom class names for footer wrapper
	*/
	footerClass: String,
	/**
	* @description width of Dialog, default is 50%
	*/
	width: { type: [String, Number] },
	/**
	* @description same as z-index in native CSS, z-order of dialog
	*/
	zIndex: { type: Number },
	trapFocus: Boolean,
	/**
	* @description header's aria-level attribute
	*/
	headerAriaLevel: {
		type: String,
		default: "2"
	},
	/**
	* @description custom transition configuration for dialog animation, it can be a string (transition name) or an object with Vue transition props
	*/
	transition: {
		type: definePropType([String, Object]),
		default: void 0
	}
});
var dialogEmits = {
	open: () => true,
	opened: () => true,
	close: () => true,
	closed: () => true,
	[UPDATE_MODEL_EVENT]: (value) => isBoolean(value),
	openAutoFocus: () => true,
	closeAutoFocus: () => true
};
//#endregion
//#region node_modules/element-plus/es/components/overlay/src/overlay.mjs
var overlayProps = buildProps({
	mask: {
		type: Boolean,
		default: true
	},
	customMaskEvent: Boolean,
	overlayClass: { type: definePropType([
		String,
		Array,
		Object
	]) },
	zIndex: { type: definePropType([String, Number]) }
});
var overlayEmits = { click: (evt) => evt instanceof MouseEvent };
var BLOCK = "overlay";
//#endregion
//#region node_modules/element-plus/es/components/overlay/index.mjs
var ElOverlay = defineComponent({
	name: "ElOverlay",
	props: overlayProps,
	emits: overlayEmits,
	setup(props, { slots, emit }) {
		const ns = useNamespace(BLOCK);
		const onMaskClick = (e) => {
			emit("click", e);
		};
		const { onClick, onMousedown, onMouseup } = useSameTarget(props.customMaskEvent ? void 0 : onMaskClick);
		return () => {
			return props.mask ? createVNode("div", {
				class: [ns.b(), props.overlayClass],
				style: { zIndex: props.zIndex },
				onClick,
				onMousedown,
				onMouseup
			}, [renderSlot(slots, "default")], 14, [
				"onClick",
				"onMouseup",
				"onMousedown"
			]) : h("div", {
				class: props.overlayClass,
				style: {
					zIndex: props.zIndex,
					position: "fixed",
					top: "0px",
					right: "0px",
					bottom: "0px",
					left: "0px"
				}
			}, [renderSlot(slots, "default")]);
		};
	}
});
//#endregion
//#region node_modules/element-plus/es/components/dialog/src/constants.mjs
var dialogInjectionKey = Symbol("dialogInjectionKey");
var DEFAULT_DIALOG_TRANSITION = "dialog-fade";
//#endregion
//#region node_modules/element-plus/es/components/dialog/src/use-dialog.mjs
var COMPONENT_NAME = "ElDialog";
var useDialog = (props, targetRef) => {
	const emit = getCurrentInstance().emit;
	const { nextZIndex } = useZIndex();
	let lastPosition = "";
	const titleId = useId();
	const bodyId = useId();
	const visible = ref(false);
	const closed = ref(false);
	const rendered = ref(false);
	const zIndex = ref(props.zIndex ?? nextZIndex());
	const closing = ref(false);
	let openTimer = void 0;
	let closeTimer = void 0;
	const config = useGlobalConfig();
	const namespace = computed(() => config.value?.namespace ?? "el");
	const globalConfig = computed(() => config.value?.dialog);
	const style = computed(() => {
		const style = {};
		const varPrefix = `--${namespace.value}-dialog`;
		if (!props.fullscreen) {
			if (props.top) style[`${varPrefix}-margin-top`] = props.top;
			const width = addUnit(props.width);
			if (width) style[`${varPrefix}-width`] = width;
		}
		return style;
	});
	const _draggable = computed(() => (props.draggable ?? globalConfig.value?.draggable ?? false) && !props.fullscreen);
	const _alignCenter = computed(() => props.alignCenter ?? globalConfig.value?.alignCenter ?? false);
	const _overflow = computed(() => props.overflow ?? globalConfig.value?.overflow ?? false);
	const penetrable = computed(() => props.modalPenetrable && !props.modal && !props.fullscreen);
	const overlayDialogStyle = computed(() => {
		if (_alignCenter.value) return { display: "flex" };
		return {};
	});
	const transitionConfig = computed(() => {
		const transition = props.transition ?? globalConfig.value?.transition ?? "dialog-fade";
		const baseConfig = {
			name: transition,
			onAfterEnter: afterEnter,
			onBeforeLeave: beforeLeave,
			onAfterLeave: afterLeave
		};
		if (isObject(transition)) {
			const config = { ...transition };
			const _mergeHook = (userHook, defaultHook) => {
				return (el) => {
					if (isArray(userHook)) userHook.forEach((fn) => {
						if (isFunction(fn)) fn(el);
					});
					else if (isFunction(userHook)) userHook(el);
					defaultHook();
				};
			};
			config.onAfterEnter = _mergeHook(config.onAfterEnter, afterEnter);
			config.onBeforeLeave = _mergeHook(config.onBeforeLeave, beforeLeave);
			config.onAfterLeave = _mergeHook(config.onAfterLeave, afterLeave);
			if (!config.name) {
				config.name = DEFAULT_DIALOG_TRANSITION;
				debugWarn(COMPONENT_NAME, `transition.name is missing when using object syntax, fallback to '${DEFAULT_DIALOG_TRANSITION}'`);
			}
			return config;
		}
		return baseConfig;
	});
	function afterEnter() {
		emit("opened");
	}
	function afterLeave() {
		emit("closed");
		emit(UPDATE_MODEL_EVENT, false);
		if (props.destroyOnClose) rendered.value = false;
		closing.value = false;
	}
	function beforeLeave() {
		closing.value = true;
		emit("close");
	}
	function open() {
		closeTimer?.();
		openTimer?.();
		if (props.openDelay && props.openDelay > 0) ({stop: openTimer} = useTimeoutFn(() => doOpen(), props.openDelay));
		else doOpen();
	}
	function close() {
		openTimer?.();
		closeTimer?.();
		if (props.closeDelay && props.closeDelay > 0) ({stop: closeTimer} = useTimeoutFn(() => doClose(), props.closeDelay));
		else doClose();
	}
	function handleClose() {
		function hide(shouldCancel) {
			if (shouldCancel) return;
			closed.value = true;
			visible.value = false;
		}
		if (props.beforeClose) props.beforeClose(hide);
		else close();
	}
	function onModalClick() {
		if (props.closeOnClickModal) handleClose();
	}
	function doOpen() {
		if (!isClient) return;
		visible.value = true;
	}
	function doClose() {
		visible.value = false;
	}
	function onOpenAutoFocus() {
		emit("openAutoFocus");
	}
	function onCloseAutoFocus() {
		emit("closeAutoFocus");
	}
	function onFocusoutPrevented(event) {
		if (event.detail?.focusReason === "pointer") event.preventDefault();
	}
	if (props.lockScroll) useLockscreen(visible);
	function onCloseRequested() {
		if (props.closeOnPressEscape) handleClose();
	}
	function bringToFront() {
		if (!visible.value || !penetrable.value || props.zIndex !== void 0) return;
		zIndex.value = nextZIndex();
	}
	watch(() => props.zIndex, () => {
		zIndex.value = props.zIndex ?? nextZIndex();
	});
	watch(() => props.modelValue, (val) => {
		if (val) {
			closed.value = false;
			closing.value = false;
			open();
			rendered.value = true;
			zIndex.value = props.zIndex ?? nextZIndex();
			nextTick(() => {
				emit("open");
				if (targetRef.value) {
					targetRef.value.parentElement.scrollTop = 0;
					targetRef.value.parentElement.scrollLeft = 0;
					targetRef.value.scrollTop = 0;
				}
			});
		} else if (visible.value) close();
	});
	watch(() => props.fullscreen, (val) => {
		if (!targetRef.value) return;
		if (val) {
			lastPosition = targetRef.value.style.transform;
			targetRef.value.style.transform = "";
		} else targetRef.value.style.transform = lastPosition;
	});
	return {
		afterEnter,
		afterLeave,
		beforeLeave,
		handleClose,
		onModalClick,
		close,
		doClose,
		onOpenAutoFocus,
		onCloseAutoFocus,
		onCloseRequested,
		onFocusoutPrevented,
		bringToFront,
		titleId,
		bodyId,
		closed,
		style,
		overlayDialogStyle,
		rendered,
		visible,
		zIndex,
		transitionConfig,
		_draggable,
		_alignCenter,
		_overflow,
		closing,
		penetrable
	};
};

export { CHANGE_EVENT as C, ElInput as E, INPUT_EVENT as I, UPDATE_MODEL_EVENT as U, ElOverlay as a, useSameTarget as b, dialogProps as c, dialogEmits as d, useDialog as e, dialogInjectionKey as f, dialogContentEmits as g, dialogContentProps as h, useComposition as i, useFocusController as j, rAF as r, scrollIntoView as s, useLockscreen as u };
//# sourceMappingURL=css-BTXcRLa_.mjs.map
