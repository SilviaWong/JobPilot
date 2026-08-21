import { _ as _plugin_vue_export_helper_default, $ as $fetch$1, e as isElement, d as debugWarn, f as useId, u as useNamespace, c as isUndefined$1, a as isNumber, i as isBoolean, g as isPropAbsent } from '../virtual/entry.mjs';
import { E as ElIcon, c as withNoopInstall, b as buildProps, d as definePropType, w as withInstall, f as TypeComponents, l as loading_default, T as TypeComponentsMap, o as obtainAllFocusableElements } from './icon-BTfkZ5_-.mjs';
import { _ as _plugin_vue_export_helper_default$1, u as useAriaProps, f as focus_trap_default$1 } from './arrays-DLJ1qaKZ.mjs';
import { g as getEventCode, E as EVENT_CODE } from './typescript-DnijNOm4.mjs';
import { E as ElInput, a as ElOverlay, u as useLockscreen, b as useSameTarget, C as CHANGE_EVENT, U as UPDATE_MODEL_EVENT } from './css-BTXcRLa_.mjs';
import { a as ElCard, E as ElButton, b as ElMessage, f as useSizeProp, g as useGlobalComponentSettings, u as useFormItem, d as useFormItemInputId, c as useFormSize, e as useFormDisabled, h as useDeprecated } from './css-n73NFgGH.mjs';
import { E as ElSelect, a as ElOption } from './css-CQsUtvuV.mjs';
import { E as ElDialog, i as isValidComponentSize, u as useDraggable } from './css-auYq15mM.mjs';
import { E as ElCheckbox } from './css-DCGp0dw5.mjs';
import { E as ElTag } from './css-B_mwQh58.mjs';
import { E as ElEmpty, J as JobDetailDrawer_default } from './JobDetailDrawer-BOXyODc5.mjs';
import { C as ClientOnly } from './client-only-DkcSMmmB.mjs';
import { shallowRef, ref, watch, mergeProps, withCtx, createVNode, openBlock, createBlock, Fragment, renderList, toDisplayString, createTextVNode, createCommentVNode, withModifiers, unref, withKeys, isVNode, render, markRaw, defineComponent, computed, reactive, nextTick, toRefs, useSSRContext, provide, createElementBlock, normalizeClass, renderSlot, resolveDynamicComponent, withDirectives, createElementVNode, isRef, vModelRadio, normalizeStyle, resolveComponent, Transition, vShow, inject } from 'vue';
import { isEqual, omit } from 'lodash-unified';
import { ssrRenderAttrs, ssrRenderComponent, ssrRenderStyle, ssrInterpolate, ssrRenderList } from 'vue/server-renderer';
import { Loader2, Star, Eye, Ban, Bot } from 'lucide-vue-next';
import { isClient } from '@vueuse/core';
import { isString, hasOwn, isObject, isFunction, NOOP } from '@vue/shared';
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
import 'unhead/utils';
import '@ctrl/tinycolor';
import './css-20Rh4iVR.mjs';
import '@popperjs/core';
import './vnode-BA-uK14B.mjs';
import './click-outside-mj8goBwo.mjs';

//#region node_modules/element-plus/es/directives/trap-focus/index.mjs
var FOCUSABLE_CHILDREN = "_trap-focus-children";
var FOCUS_STACK = [];
var FOCUS_HANDLER = (e) => {
	if (FOCUS_STACK.length === 0) return;
	const code = getEventCode(e);
	const focusableElement = FOCUS_STACK[FOCUS_STACK.length - 1][FOCUSABLE_CHILDREN];
	if (focusableElement.length > 0 && code === EVENT_CODE.tab) {
		if (focusableElement.length === 1) {
			e.preventDefault();
			if ((void 0).activeElement !== focusableElement[0]) focusableElement[0].focus();
			return;
		}
		const goingBackward = e.shiftKey;
		const isFirst = e.target === focusableElement[0];
		const isLast = e.target === focusableElement[focusableElement.length - 1];
		if (isFirst && goingBackward) {
			e.preventDefault();
			focusableElement[focusableElement.length - 1].focus();
		}
		if (isLast && !goingBackward) {
			e.preventDefault();
			focusableElement[0].focus();
		}
	}
};
var TrapFocus = {
	beforeMount(el) {
		el[FOCUSABLE_CHILDREN] = obtainAllFocusableElements(el);
		FOCUS_STACK.push(el);
		if (FOCUS_STACK.length <= 1) (void 0).addEventListener("keydown", FOCUS_HANDLER);
	},
	updated(el) {
		nextTick(() => {
			el[FOCUSABLE_CHILDREN] = obtainAllFocusableElements(el);
		});
	},
	unmounted() {
		FOCUS_STACK.shift();
		if (FOCUS_STACK.length === 0) (void 0).removeEventListener("keydown", FOCUS_HANDLER);
	}
};
//#endregion
//#region node_modules/element-plus/es/components/radio/src/radio.mjs
/**
* @deprecated Removed after 3.0.0, Use `RadioPropsBase` instead.
*/
var radioPropsBase = buildProps({
	/**
	* @description binding value
	*/
	modelValue: {
		type: [
			String,
			Number,
			Boolean
		],
		default: void 0
	},
	/**
	* @description size of the Radio
	*/
	size: useSizeProp,
	/**
	* @description whether Radio is disabled
	*/
	disabled: {
		type: Boolean,
		default: void 0
	},
	/**
	* @description the label of Radio
	*/
	label: {
		type: [
			String,
			Number,
			Boolean
		],
		default: void 0
	},
	/**
	* @description the value of Radio
	*/
	value: {
		type: [
			String,
			Number,
			Boolean
		],
		default: void 0
	},
	/**
	* @description native `name` attribute
	*/
	name: {
		type: String,
		default: void 0
	}
});
/**
* @deprecated Removed after 3.0.0, Use `RadioProps` instead.
*/
var radioProps = buildProps({
	...radioPropsBase,
	/**
	* @description whether to add a border around Radio
	*/
	border: Boolean
});
var radioEmits = {
	[UPDATE_MODEL_EVENT]: (val) => isString(val) || isNumber(val) || isBoolean(val),
	[CHANGE_EVENT]: (val) => isString(val) || isNumber(val) || isBoolean(val)
};
//#endregion
//#region node_modules/element-plus/es/components/radio/src/constants.mjs
var radioGroupKey = Symbol("radioGroupKey");
//#endregion
//#region node_modules/element-plus/es/components/radio/src/radio-button.mjs
/**
* @deprecated Removed after 3.0.0, Use `RadioButtonProps` instead.
*/
var radioButtonProps = buildProps({ ...radioPropsBase });
//#endregion
//#region node_modules/element-plus/es/components/radio/src/radio-group.mjs
var radioDefaultProps = {
	label: "label",
	value: "value",
	disabled: "disabled"
};
/**
* @deprecated Removed after 3.0.0, Use `RadioGroupProps` instead.
*/
var radioGroupProps = buildProps({
	/**
	* @description native `id` attribute
	*/
	id: {
		type: String,
		default: void 0
	},
	/**
	* @description the size of radio buttons or bordered radios
	*/
	size: useSizeProp,
	/**
	* @description whether the nesting radios are disabled
	*/
	disabled: {
		type: Boolean,
		default: void 0
	},
	/**
	* @description binding value
	*/
	modelValue: {
		type: [
			String,
			Number,
			Boolean
		],
		default: void 0
	},
	/**
	* @description border and background color when button is active
	*/
	fill: {
		type: String,
		default: ""
	},
	/**
	* @description font color when button is active
	*/
	textColor: {
		type: String,
		default: ""
	},
	/**
	* @description native `name` attribute
	*/
	name: {
		type: String,
		default: void 0
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
		default: () => radioDefaultProps
	},
	type: {
		type: String,
		values: ["radio", "button"],
		default: "radio"
	},
	...useAriaProps(["ariaLabel"])
});
var radioGroupEmits = radioEmits;
//#endregion
//#region node_modules/element-plus/es/components/radio/src/use-radio.mjs
var useRadio = (props, emit) => {
	const radioRef = ref();
	const radioGroup = inject(radioGroupKey, void 0);
	const isGroup = computed(() => !!radioGroup);
	const actualValue = computed(() => {
		if (!isPropAbsent(props.value)) return props.value;
		return props.label;
	});
	const modelValue = computed({
		get() {
			return isGroup.value ? radioGroup.modelValue : props.modelValue;
		},
		set(val) {
			if (isGroup.value) radioGroup.changeEvent(val);
			else emit && emit("update:modelValue", val);
			radioRef.value.checked = props.modelValue === actualValue.value;
		}
	});
	const size = useFormSize(computed(() => radioGroup?.size));
	const disabled = useFormDisabled(computed(() => radioGroup?.disabled));
	const focus = ref(false);
	const tabIndex = computed(() => {
		return disabled.value || isGroup.value && modelValue.value !== actualValue.value ? -1 : 0;
	});
	useDeprecated({
		from: "label act as value",
		replacement: "value",
		version: "3.0.0",
		scope: "el-radio",
		ref: "https://element-plus.org/en-US/component/radio.html"
	}, computed(() => isGroup.value && isPropAbsent(props.value)));
	return {
		radioRef,
		isGroup,
		radioGroup,
		focus,
		size,
		disabled,
		tabIndex,
		modelValue,
		actualValue
	};
};
//#endregion
//#region node_modules/element-plus/es/components/radio/src/radio.vue_vue_type_script_setup_true_lang.mjs
var _hoisted_1$3 = [
	"value",
	"name",
	"disabled",
	"checked"
];
//#endregion
//#region node_modules/element-plus/es/components/radio/src/radio2.mjs
var radio_default = /* @__PURE__ */ defineComponent({
	name: "ElRadio",
	__name: "radio",
	props: radioProps,
	emits: radioEmits,
	setup(__props, { emit: __emit }) {
		const props = __props;
		const emit = __emit;
		const ns = useNamespace("radio");
		const { radioRef, radioGroup, focus, size, disabled, modelValue, actualValue } = useRadio(props, emit);
		function handleChange() {
			nextTick(() => emit(CHANGE_EVENT, modelValue.value));
		}
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("label", { class: normalizeClass([
				unref(ns).b(),
				unref(ns).is("disabled", unref(disabled)),
				unref(ns).is("focus", unref(focus)),
				unref(ns).is("bordered", __props.border),
				unref(ns).is("checked", unref(modelValue) === unref(actualValue)),
				unref(ns).m(unref(size))
			]) }, [createElementVNode("span", { class: normalizeClass([
				unref(ns).e("input"),
				unref(ns).is("disabled", unref(disabled)),
				unref(ns).is("checked", unref(modelValue) === unref(actualValue))
			]) }, [withDirectives(createElementVNode("input", {
				ref_key: "radioRef",
				ref: radioRef,
				"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => isRef(modelValue) ? modelValue.value = $event : null),
				class: normalizeClass(unref(ns).e("original")),
				value: unref(actualValue),
				name: __props.name || unref(radioGroup)?.name,
				disabled: unref(disabled),
				checked: unref(modelValue) === unref(actualValue),
				type: "radio",
				onFocus: _cache[1] || (_cache[1] = ($event) => focus.value = true),
				onBlur: _cache[2] || (_cache[2] = ($event) => focus.value = false),
				onChange: handleChange,
				onClick: _cache[3] || (_cache[3] = withModifiers(() => {}, ["stop"]))
			}, null, 42, _hoisted_1$3), [[vModelRadio, unref(modelValue)]]), createElementVNode("span", { class: normalizeClass(unref(ns).e("inner")) }, null, 2)], 2), createElementVNode("span", {
				class: normalizeClass(unref(ns).e("label")),
				onKeydown: _cache[4] || (_cache[4] = withModifiers(() => {}, ["stop"]))
			}, [renderSlot(_ctx.$slots, "default", {}, () => [createTextVNode(toDisplayString(__props.label), 1)])], 34)], 2);
		};
	}
});
//#endregion
//#region node_modules/element-plus/es/components/radio/src/radio-button.vue_vue_type_script_setup_true_lang.mjs
var _hoisted_1$2 = [
	"value",
	"name",
	"disabled"
];
//#endregion
//#region node_modules/element-plus/es/components/radio/src/radio-button2.mjs
var radio_button_default = /* @__PURE__ */ defineComponent({
	name: "ElRadioButton",
	__name: "radio-button",
	props: radioButtonProps,
	setup(__props) {
		const props = __props;
		const ns = useNamespace("radio");
		const { radioRef, focus, size, disabled, modelValue, radioGroup, actualValue } = useRadio(props);
		const activeStyle = computed(() => {
			return {
				backgroundColor: radioGroup?.fill || "",
				borderColor: radioGroup?.fill || "",
				boxShadow: radioGroup?.fill ? `-1px 0 0 0 ${radioGroup.fill}` : "",
				color: radioGroup?.textColor || ""
			};
		});
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("label", { class: normalizeClass([
				unref(ns).b("button"),
				unref(ns).is("active", unref(modelValue) === unref(actualValue)),
				unref(ns).is("disabled", unref(disabled)),
				unref(ns).is("focus", unref(focus)),
				unref(ns).bm("button", unref(size))
			]) }, [withDirectives(createElementVNode("input", {
				ref_key: "radioRef",
				ref: radioRef,
				"onUpdate:modelValue": _cache[0] || (_cache[0] = ($event) => isRef(modelValue) ? modelValue.value = $event : null),
				class: normalizeClass(unref(ns).be("button", "original-radio")),
				value: unref(actualValue),
				type: "radio",
				name: __props.name || unref(radioGroup)?.name,
				disabled: unref(disabled),
				onFocus: _cache[1] || (_cache[1] = ($event) => focus.value = true),
				onBlur: _cache[2] || (_cache[2] = ($event) => focus.value = false),
				onClick: _cache[3] || (_cache[3] = withModifiers(() => {}, ["stop"]))
			}, null, 42, _hoisted_1$2), [[vModelRadio, unref(modelValue)]]), createElementVNode("span", {
				class: normalizeClass(unref(ns).be("button", "inner")),
				style: normalizeStyle(unref(modelValue) === unref(actualValue) ? activeStyle.value : {}),
				onKeydown: _cache[4] || (_cache[4] = withModifiers(() => {}, ["stop"]))
			}, [renderSlot(_ctx.$slots, "default", {}, () => [createTextVNode(toDisplayString(__props.label), 1)])], 38)], 2);
		};
	}
});
//#endregion
//#region node_modules/element-plus/es/components/radio/src/radio-group.vue_vue_type_script_setup_true_lang.mjs
var _hoisted_1$1 = [
	"id",
	"aria-label",
	"aria-labelledby"
];
//#endregion
//#region node_modules/element-plus/es/components/radio/src/radio-group2.mjs
var radio_group_default = /* @__PURE__ */ defineComponent({
	name: "ElRadioGroup",
	__name: "radio-group",
	props: radioGroupProps,
	emits: radioGroupEmits,
	setup(__props, { emit: __emit }) {
		const props = __props;
		const emit = __emit;
		const ns = useNamespace("radio");
		const radioId = useId();
		const radioGroupRef = ref();
		const { formItem } = useFormItem();
		const { inputId: groupId, isLabeledByFormItem } = useFormItemInputId(props, { formItemContext: formItem });
		const changeEvent = (value) => {
			emit(UPDATE_MODEL_EVENT, value);
			nextTick(() => emit(CHANGE_EVENT, value));
		};
		const name = computed(() => {
			return props.name || radioId.value;
		});
		const aliasProps = computed(() => ({
			...radioDefaultProps,
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
		const optionComponent = computed(() => props.type === "button" ? radio_button_default : radio_default);
		provide(radioGroupKey, reactive({
			...toRefs(props),
			changeEvent,
			name
		}));
		watch(() => props.modelValue, (newVal, oldValue) => {
			if (props.validateEvent && !isEqual(newVal, oldValue)) formItem?.validate("change").catch(NOOP);
		});
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("div", {
				id: unref(groupId),
				ref_key: "radioGroupRef",
				ref: radioGroupRef,
				class: normalizeClass(unref(ns).b("group")),
				role: "radiogroup",
				"aria-label": !unref(isLabeledByFormItem) ? __props.ariaLabel || "radio-group" : void 0,
				"aria-labelledby": unref(isLabeledByFormItem) ? unref(formItem).labelId : void 0
			}, [renderSlot(_ctx.$slots, "default", {}, () => [(openBlock(true), createElementBlock(Fragment, null, renderList(__props.options, (item, index) => {
				return openBlock(), createBlock(resolveDynamicComponent(optionComponent.value), mergeProps({ key: index }, { ref_for: true }, getOptionProps(item)), null, 16);
			}), 128))])], 10, _hoisted_1$1);
		};
	}
});
withInstall(radio_default, {
	RadioButton: radio_button_default,
	RadioGroup: radio_group_default
});
var ElRadioGroup = withNoopInstall(radio_group_default);
var ElRadioButton = withNoopInstall(radio_button_default);
//#endregion
//#region node_modules/element-plus/es/components/message-box/src/index.vue_vue_type_script_lang.mjs
var index_vue_vue_type_script_lang_default = defineComponent({
	name: "ElMessageBox",
	directives: { TrapFocus },
	components: {
		ElButton,
		ElFocusTrap: focus_trap_default$1,
		ElInput,
		ElOverlay,
		ElIcon,
		...TypeComponents
	},
	inheritAttrs: false,
	props: {
		buttonSize: {
			type: String,
			validator: isValidComponentSize
		},
		modal: {
			type: Boolean,
			default: true
		},
		lockScroll: {
			type: Boolean,
			default: true
		},
		showClose: {
			type: Boolean,
			default: true
		},
		closeOnClickModal: {
			type: Boolean,
			default: true
		},
		closeOnPressEscape: {
			type: Boolean,
			default: true
		},
		closeOnHashChange: {
			type: Boolean,
			default: true
		},
		center: Boolean,
		draggable: Boolean,
		overflow: Boolean,
		roundButton: Boolean,
		container: {
			type: String,
			default: "body"
		},
		boxType: {
			type: String,
			default: ""
		}
	},
	emits: ["vanish", "action"],
	setup(props, { emit }) {
		const { locale, zIndex, ns, size: btnSize } = useGlobalComponentSettings("message-box", computed(() => props.buttonSize));
		const { t } = locale;
		const { nextZIndex } = zIndex;
		const visible = ref(false);
		const state = reactive({
			autofocus: true,
			beforeClose: null,
			callback: null,
			cancelButtonText: "",
			cancelButtonClass: "",
			confirmButtonText: "",
			confirmButtonClass: "",
			cancelButtonType: "",
			confirmButtonType: "primary",
			customClass: "",
			customStyle: {},
			dangerouslyUseHTMLString: false,
			distinguishCancelAndClose: false,
			icon: "",
			closeIcon: "",
			inputPattern: null,
			inputPlaceholder: "",
			inputType: "text",
			inputValue: "",
			inputValidator: void 0,
			inputErrorMessage: "",
			message: "",
			modalFade: true,
			modalClass: "",
			showCancelButton: false,
			showConfirmButton: true,
			type: "",
			title: void 0,
			showInput: false,
			action: "",
			confirmButtonLoading: false,
			cancelButtonLoading: false,
			confirmButtonLoadingIcon: markRaw(loading_default),
			cancelButtonLoadingIcon: markRaw(loading_default),
			confirmButtonDisabled: false,
			editorErrorMessage: "",
			validateError: false,
			zIndex: nextZIndex()
		});
		const typeClass = computed(() => {
			const type = state.type;
			return { [ns.bm("icon", type)]: type && TypeComponentsMap[type] };
		});
		const contentId = useId();
		const inputId = useId();
		const iconComponent = computed(() => {
			const type = state.type;
			return state.icon || type && TypeComponentsMap[type] || "";
		});
		const hasMessage = computed(() => !!state.message);
		const rootRef = ref();
		const headerRef = ref();
		const focusStartRef = ref();
		const inputRef = ref();
		const confirmRef = ref();
		const confirmButtonClasses = computed(() => state.confirmButtonClass);
		watch(() => state.inputValue, async (val) => {
			await nextTick();
			if (props.boxType === "prompt" && val) validate();
		}, { immediate: true });
		watch(() => visible.value, (val) => {
			if (val) {
				if (props.boxType !== "prompt") if (state.autofocus) focusStartRef.value = confirmRef.value?.$el ?? rootRef.value;
				else focusStartRef.value = rootRef.value;
				state.zIndex = nextZIndex();
			}
			if (props.boxType !== "prompt") return;
			if (val) nextTick().then(() => {
				if (inputRef.value && inputRef.value.$el) if (state.autofocus) focusStartRef.value = getInputElement() ?? rootRef.value;
				else focusStartRef.value = rootRef.value;
			});
			else {
				state.editorErrorMessage = "";
				state.validateError = false;
			}
		});
		const { isDragging } = useDraggable(rootRef, headerRef, computed(() => props.draggable), computed(() => props.overflow));
		function doClose() {
			if (!visible.value) return;
			visible.value = false;
			nextTick(() => {
				if (state.action) emit("action", state.action);
			});
		}
		const handleWrapperClick = () => {
			if (props.closeOnClickModal) handleAction(state.distinguishCancelAndClose ? "close" : "cancel");
		};
		const overlayEvent = useSameTarget(handleWrapperClick);
		const handleInputEnter = (e) => {
			if (state.inputType !== "textarea" && !inputRef.value?.isComposing) {
				e.preventDefault();
				return handleAction("confirm");
			}
		};
		const handleAction = (action) => {
			if (props.boxType === "prompt" && action === "confirm" && !validate()) return;
			state.action = action;
			if (state.beforeClose) state.beforeClose?.(action, state, doClose);
			else doClose();
		};
		const validate = () => {
			if (props.boxType === "prompt") {
				const inputPattern = state.inputPattern;
				if (inputPattern && !inputPattern.test(state.inputValue || "")) {
					state.editorErrorMessage = state.inputErrorMessage || t("el.messagebox.error");
					state.validateError = true;
					return false;
				}
				const inputValidator = state.inputValidator;
				if (isFunction(inputValidator)) {
					const validateResult = inputValidator(state.inputValue);
					if (validateResult === false) {
						state.editorErrorMessage = state.inputErrorMessage || t("el.messagebox.error");
						state.validateError = true;
						return false;
					}
					if (isString(validateResult)) {
						state.editorErrorMessage = validateResult;
						state.validateError = true;
						return false;
					}
				}
			}
			state.editorErrorMessage = "";
			state.validateError = false;
			return true;
		};
		const getInputElement = () => {
			const inputRefs = inputRef.value?.$refs;
			return inputRefs?.input ?? inputRefs?.textarea;
		};
		const handleClose = () => {
			handleAction("close");
		};
		const onCloseRequested = () => {
			if (props.closeOnPressEscape) handleClose();
		};
		if (props.lockScroll) useLockscreen(visible, { ns });
		return {
			...toRefs(state),
			ns,
			overlayEvent,
			visible,
			hasMessage,
			typeClass,
			contentId,
			inputId,
			btnSize,
			iconComponent,
			confirmButtonClasses,
			rootRef,
			focusStartRef,
			headerRef,
			inputRef,
			isDragging,
			confirmRef,
			doClose,
			handleClose,
			onCloseRequested,
			handleWrapperClick,
			handleInputEnter,
			handleAction,
			t
		};
	}
});
//#endregion
//#region node_modules/element-plus/es/components/message-box/src/index.mjs
var _hoisted_1 = ["aria-label", "aria-describedby"];
var _hoisted_2 = ["aria-label"];
var _hoisted_3 = ["id"];
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
	const _component_el_icon = resolveComponent("el-icon");
	const _component_el_input = resolveComponent("el-input");
	const _component_el_button = resolveComponent("el-button");
	const _component_el_focus_trap = resolveComponent("el-focus-trap");
	const _component_el_overlay = resolveComponent("el-overlay");
	return openBlock(), createBlock(Transition, {
		name: "fade-in-linear",
		onAfterLeave: _cache[11] || (_cache[11] = ($event) => _ctx.$emit("vanish")),
		persisted: ""
	}, {
		default: withCtx(() => [withDirectives(createVNode(_component_el_overlay, {
			"z-index": _ctx.zIndex,
			"overlay-class": [_ctx.ns.is("message-box"), _ctx.modalClass],
			mask: _ctx.modal
		}, {
			default: withCtx(() => [createElementVNode("div", {
				role: "dialog",
				"aria-label": _ctx.title,
				"aria-modal": "true",
				"aria-describedby": !_ctx.showInput ? _ctx.contentId : void 0,
				class: normalizeClass(`${_ctx.ns.namespace.value}-overlay-message-box`),
				onClick: _cache[8] || (_cache[8] = (...args) => _ctx.overlayEvent.onClick && _ctx.overlayEvent.onClick(...args)),
				onMousedown: _cache[9] || (_cache[9] = (...args) => _ctx.overlayEvent.onMousedown && _ctx.overlayEvent.onMousedown(...args)),
				onMouseup: _cache[10] || (_cache[10] = (...args) => _ctx.overlayEvent.onMouseup && _ctx.overlayEvent.onMouseup(...args))
			}, [createVNode(_component_el_focus_trap, {
				loop: "",
				trapped: _ctx.visible,
				"focus-trap-el": _ctx.rootRef,
				"focus-start-el": _ctx.focusStartRef,
				onReleaseRequested: _ctx.onCloseRequested
			}, {
				default: withCtx(() => [createElementVNode("div", {
					ref: "rootRef",
					class: normalizeClass([
						_ctx.ns.b(),
						_ctx.customClass,
						_ctx.ns.is("draggable", _ctx.draggable),
						_ctx.ns.is("dragging", _ctx.isDragging),
						{ [_ctx.ns.m("center")]: _ctx.center }
					]),
					style: normalizeStyle(_ctx.customStyle),
					tabindex: "-1",
					onClick: _cache[7] || (_cache[7] = withModifiers(() => {}, ["stop"]))
				}, [
					_ctx.title !== null && _ctx.title !== void 0 ? (openBlock(), createElementBlock("div", {
						key: 0,
						ref: "headerRef",
						class: normalizeClass([_ctx.ns.e("header"), { "show-close": _ctx.showClose }])
					}, [createElementVNode("div", { class: normalizeClass(_ctx.ns.e("title")) }, [_ctx.iconComponent && _ctx.center ? (openBlock(), createBlock(_component_el_icon, {
						key: 0,
						class: normalizeClass([_ctx.ns.e("status"), _ctx.typeClass])
					}, {
						default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(_ctx.iconComponent)))]),
						_: 1
					}, 8, ["class"])) : createCommentVNode("v-if", true), createElementVNode("span", null, toDisplayString(_ctx.title), 1)], 2), _ctx.showClose ? (openBlock(), createElementBlock("button", {
						key: 0,
						type: "button",
						class: normalizeClass(_ctx.ns.e("headerbtn")),
						"aria-label": _ctx.t("el.messagebox.close"),
						onClick: _cache[0] || (_cache[0] = ($event) => _ctx.handleAction(_ctx.distinguishCancelAndClose ? "close" : "cancel")),
						onKeydown: _cache[1] || (_cache[1] = withKeys(withModifiers(($event) => _ctx.handleAction(_ctx.distinguishCancelAndClose ? "close" : "cancel"), ["prevent"]), ["enter"]))
					}, [createVNode(_component_el_icon, { class: normalizeClass(_ctx.ns.e("close")) }, {
						default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(_ctx.closeIcon || "close")))]),
						_: 1
					}, 8, ["class"])], 42, _hoisted_2)) : createCommentVNode("v-if", true)], 2)) : createCommentVNode("v-if", true),
					createElementVNode("div", {
						id: _ctx.contentId,
						class: normalizeClass(_ctx.ns.e("content"))
					}, [createElementVNode("div", { class: normalizeClass(_ctx.ns.e("container")) }, [_ctx.iconComponent && !_ctx.center && _ctx.hasMessage ? (openBlock(), createBlock(_component_el_icon, {
						key: 0,
						class: normalizeClass([_ctx.ns.e("status"), _ctx.typeClass])
					}, {
						default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(_ctx.iconComponent)))]),
						_: 1
					}, 8, ["class"])) : createCommentVNode("v-if", true), _ctx.hasMessage ? (openBlock(), createElementBlock("div", {
						key: 1,
						class: normalizeClass(_ctx.ns.e("message"))
					}, [renderSlot(_ctx.$slots, "default", {}, () => [!_ctx.dangerouslyUseHTMLString ? (openBlock(), createBlock(resolveDynamicComponent(_ctx.showInput ? "label" : "p"), {
						key: 0,
						for: _ctx.showInput ? _ctx.inputId : void 0,
						textContent: toDisplayString(_ctx.message)
					}, null, 8, ["for", "textContent"])) : (openBlock(), createBlock(resolveDynamicComponent(_ctx.showInput ? "label" : "p"), {
						key: 1,
						for: _ctx.showInput ? _ctx.inputId : void 0,
						innerHTML: _ctx.message
					}, null, 8, ["for", "innerHTML"]))])], 2)) : createCommentVNode("v-if", true)], 2), withDirectives(createElementVNode("div", { class: normalizeClass(_ctx.ns.e("input")) }, [createVNode(_component_el_input, {
						id: _ctx.inputId,
						ref: "inputRef",
						modelValue: _ctx.inputValue,
						"onUpdate:modelValue": _cache[2] || (_cache[2] = ($event) => _ctx.inputValue = $event),
						type: _ctx.inputType,
						placeholder: _ctx.inputPlaceholder,
						"aria-invalid": _ctx.validateError,
						class: normalizeClass({ invalid: _ctx.validateError }),
						onKeydown: withKeys(_ctx.handleInputEnter, ["enter"])
					}, null, 8, [
						"id",
						"modelValue",
						"type",
						"placeholder",
						"aria-invalid",
						"class",
						"onKeydown"
					]), createElementVNode("div", {
						class: normalizeClass(_ctx.ns.e("errormsg")),
						style: normalizeStyle({ visibility: !!_ctx.editorErrorMessage ? "visible" : "hidden" })
					}, toDisplayString(_ctx.editorErrorMessage), 7)], 2), [[vShow, _ctx.showInput]])], 10, _hoisted_3),
					_ctx.showCancelButton || _ctx.showConfirmButton ? (openBlock(), createElementBlock("div", {
						key: 1,
						class: normalizeClass(_ctx.ns.e("btns"))
					}, [_ctx.showCancelButton ? (openBlock(), createBlock(_component_el_button, {
						key: 0,
						type: _ctx.cancelButtonType === "text" ? "" : _ctx.cancelButtonType,
						text: _ctx.cancelButtonType === "text",
						loading: _ctx.cancelButtonLoading,
						"loading-icon": _ctx.cancelButtonLoadingIcon,
						class: normalizeClass([_ctx.cancelButtonClass]),
						round: _ctx.roundButton,
						size: _ctx.btnSize,
						onClick: _cache[3] || (_cache[3] = ($event) => _ctx.handleAction("cancel")),
						onKeydown: _cache[4] || (_cache[4] = withKeys(withModifiers(($event) => _ctx.handleAction("cancel"), ["prevent"]), ["enter"]))
					}, {
						default: withCtx(() => [createTextVNode(toDisplayString(_ctx.cancelButtonText || _ctx.t("el.messagebox.cancel")), 1)]),
						_: 1
					}, 8, [
						"type",
						"text",
						"loading",
						"loading-icon",
						"class",
						"round",
						"size"
					])) : createCommentVNode("v-if", true), withDirectives(createVNode(_component_el_button, {
						ref: "confirmRef",
						type: _ctx.confirmButtonType === "text" ? "" : _ctx.confirmButtonType,
						text: _ctx.confirmButtonType === "text",
						loading: _ctx.confirmButtonLoading,
						"loading-icon": _ctx.confirmButtonLoadingIcon,
						class: normalizeClass([_ctx.confirmButtonClasses]),
						round: _ctx.roundButton,
						disabled: _ctx.confirmButtonDisabled,
						size: _ctx.btnSize,
						onClick: _cache[5] || (_cache[5] = ($event) => _ctx.handleAction("confirm")),
						onKeydown: _cache[6] || (_cache[6] = withKeys(withModifiers(($event) => _ctx.handleAction("confirm"), ["prevent"]), ["enter"]))
					}, {
						default: withCtx(() => [createTextVNode(toDisplayString(_ctx.confirmButtonText || _ctx.t("el.messagebox.confirm")), 1)]),
						_: 1
					}, 8, [
						"type",
						"text",
						"loading",
						"loading-icon",
						"class",
						"round",
						"disabled",
						"size"
					]), [[vShow, _ctx.showConfirmButton]])], 2)) : createCommentVNode("v-if", true)
				], 6)]),
				_: 3
			}, 8, [
				"trapped",
				"focus-trap-el",
				"focus-start-el",
				"onReleaseRequested"
			])], 42, _hoisted_1)]),
			_: 3
		}, 8, [
			"z-index",
			"overlay-class",
			"mask"
		]), [[vShow, _ctx.visible]])]),
		_: 3
	});
}
var src_default = /* @__PURE__ */ _plugin_vue_export_helper_default$1(index_vue_vue_type_script_lang_default, [["render", _sfc_render]]);
//#endregion
//#region node_modules/element-plus/es/components/message-box/src/messageBox.mjs
var messageInstance = /* @__PURE__ */ new Map();
var getAppendToElement = (props) => {
	let appendTo = (void 0).body;
	if (props.appendTo) {
		if (isString(props.appendTo)) appendTo = (void 0).querySelector(props.appendTo);
		if (isElement(props.appendTo)) appendTo = props.appendTo;
		if (!isElement(appendTo)) {
			debugWarn("ElMessageBox", "the appendTo option is not an HTMLElement. Falling back to document.body.");
			appendTo = (void 0).body;
		}
	}
	return appendTo;
};
var handleAction = (vnode, action) => {
	const vm = vnode.component?.proxy;
	return () => vm.handleAction(action);
};
var initInstance = (props, container, appContext = null) => {
	const vnode = createVNode(src_default, props, isFunction(props.message) || isVNode(props.message) ? { default: isFunction(props.message) ? () => props.message({
		confirm: handleAction(vnode, "confirm"),
		cancel: handleAction(vnode, "cancel"),
		close: handleAction(vnode, "close")
	}) : () => props.message } : null);
	vnode.appContext = appContext;
	render(vnode, container);
	getAppendToElement(props).appendChild(container.firstElementChild);
	return vnode.component;
};
var genContainer = () => {
	return (void 0).createElement("div");
};
var showMessage = (options, appContext) => {
	const container = genContainer();
	options.onVanish = () => {
		render(null, container);
		messageInstance.delete(vm);
	};
	options.onAction = (action) => {
		const currentMsg = messageInstance.get(vm);
		let resolve;
		if (options.showInput) resolve = {
			value: vm.inputValue,
			action
		};
		else resolve = action;
		if (options.callback) options.callback(resolve, instance.proxy);
		else if (action === "cancel" || action === "close") if (options.distinguishCancelAndClose && action !== "cancel") currentMsg.reject("close");
		else currentMsg.reject("cancel");
		else currentMsg.resolve(resolve);
	};
	const instance = initInstance(options, container, appContext);
	const vm = instance.proxy;
	for (const prop in options) if (hasOwn(options, prop) && !hasOwn(vm.$props, prop)) if (prop === "closeIcon" && isObject(options[prop])) vm[prop] = markRaw(options[prop]);
	else vm[prop] = options[prop];
	vm.visible = true;
	return vm;
};
function MessageBox(options, appContext = null) {
	if (!isClient) return Promise.reject();
	let callback;
	if (isString(options) || isVNode(options)) options = { message: options };
	else callback = options.callback;
	return new Promise((resolve, reject) => {
		const vm = showMessage(options, appContext ?? MessageBox._context);
		messageInstance.set(vm, {
			options,
			callback,
			resolve,
			reject
		});
	});
}
var MESSAGE_BOX_VARIANTS = [
	"alert",
	"confirm",
	"prompt"
];
var MESSAGE_BOX_DEFAULT_OPTS = {
	alert: {
		closeOnPressEscape: false,
		closeOnClickModal: false
	},
	confirm: { showCancelButton: true },
	prompt: {
		showCancelButton: true,
		showInput: true
	}
};
MESSAGE_BOX_VARIANTS.forEach((boxType) => {
	MessageBox[boxType] = messageBoxFactory(boxType);
});
function messageBoxFactory(boxType) {
	return (message, title, options, appContext) => {
		let titleOrOpts = "";
		if (isObject(title)) {
			options = title;
			titleOrOpts = "";
		} else if (isUndefined$1(title)) titleOrOpts = "";
		else titleOrOpts = title;
		return MessageBox(Object.assign({
			title: titleOrOpts,
			message,
			type: "",
			...MESSAGE_BOX_DEFAULT_OPTS[boxType]
		}, options, { boxType }), appContext);
	};
}
MessageBox.close = () => {
	messageInstance.forEach((_, vm) => {
		vm.doClose();
	});
	messageInstance.clear();
};
MessageBox._context = null;
//#endregion
//#region node_modules/element-plus/es/components/message-box/index.mjs
var _MessageBox = MessageBox;
_MessageBox.install = (app) => {
	_MessageBox._context = app._context;
	app.config.globalProperties.$msgbox = _MessageBox;
	app.config.globalProperties.$messageBox = _MessageBox;
	app.config.globalProperties.$alert = _MessageBox.alert;
	app.config.globalProperties.$confirm = _MessageBox.confirm;
	app.config.globalProperties.$prompt = _MessageBox.prompt;
};
var ElMessageBox = _MessageBox;
//#endregion
//#region pages/jobs/index.vue
var _sfc_main = {
	__name: "index",
	__ssrInlineRender: true,
	setup(__props) {
		const jobList = shallowRef([]);
		const loading = ref(false);
		const analyzingId = ref(null);
		const batchAnalyzing = ref(false);
		const batchCancelled = ref(false);
		const page = ref(1);
		const pageSize = ref(20);
		const hasMore = ref(true);
		const total = ref(0);
		const statusFilter = ref("normal");
		const filterFavoritesOnly = ref(false);
		const filterShowHidden = ref(false);
		const filterShowBlacklisted = ref(false);
		const platformFilter = ref("all");
		const educationFilter = ref("all");
		const keywordFilter = ref("");
		const aiDiagnosisFilter = ref("all");
		const salaryFilter = ref("all");
		const jobDetailDrawerRef = ref(null);
		const loadMoreTrigger = ref(null);
		let observer = null;
		const fetchJobs = async (isLoadMore = false) => {
			if (!isLoadMore) {
				page.value = 1;
				hasMore.value = true;
			}
			if (!hasMore.value) return;
			loading.value = true;
			try {
				const params = new URLSearchParams({
					page: page.value,
					pageSize: pageSize.value,
					status: statusFilter.value,
					filterFavoritesOnly: filterFavoritesOnly.value,
					filterShowHidden: filterShowHidden.value,
					filterShowBlacklisted: filterShowBlacklisted.value,
					platform: platformFilter.value,
					education: educationFilter.value,
					keyword: keywordFilter.value,
					aiDiagnosisFilter: aiDiagnosisFilter.value,
					salaryFilter: salaryFilter.value
				});
				const res = await $fetch$1(`/api/jobs?${params.toString()}`);
				if (res && res.success && Array.isArray(res.data)) {
					if (isLoadMore) jobList.value = [...jobList.value, ...res.data];
					else jobList.value = res.data;
					hasMore.value = res.hasMore;
					total.value = res.total || 0;
				} else {
					if (!isLoadMore) jobList.value = [];
					hasMore.value = false;
				}
			} catch (error) {
				console.error("Failed to fetch jobs:", error);
				if (!isLoadMore) jobList.value = [];
			} finally {
				loading.value = false;
			}
		};
		const loadMore = () => {
			if (loading.value || !hasMore.value) return;
			page.value++;
			fetchJobs(true);
		};
		const viewDetails = (row) => {
			if (jobDetailDrawerRef.value) jobDetailDrawerRef.value.open(row);
		};
		const openOriginalUrl = (job) => {
			const url = job.normalizedData?.jobUrl;
			if (url) (void 0).open(url, "_blank", "noopener,noreferrer");
			else ElMessage.warning("未找到原网页链接");
		};
		const handleDeleteJob = async (job) => {
			try {
				await ElMessageBox.confirm(`确定要删除职位 "${job.jobName}" 吗？此操作将彻底删除数据，不可恢复。`, "物理删除确认", {
					confirmButtonText: "确定删除",
					cancelButtonText: "取消",
					type: "warning",
					confirmButtonClass: "el-button--danger"
				});
				const res = await $fetch$1(`/api/jobs/${job.id}`, { method: "DELETE" });
				if (res && res.success) {
					ElMessage.success("删除成功");
					jobList.value = jobList.value.filter((item) => item.id !== job.id);
				} else ElMessage.error(res?.error || res?.message || "删除失败");
			} catch (e) {
				if (e !== "cancel") {
					console.error(e);
					ElMessage.error("删除操作出错");
				}
			}
		};
		const handleBatchAiDiagnosis = async () => {
			if (analyzingId.value || batchAnalyzing.value) return;
			if (jobList.value.filter((job) => !job.aiResult || job.aiResult.score === null || job.aiResult.score === void 0).length === 0 && !hasMore.value) {
				ElMessage.info("所有职位都已诊断完毕！");
				return;
			}
			try {
				await ElMessageBox.confirm(`准备开始持续批量诊断。程序会自动向下翻页并诊断所有未处理的职位。是否开始？（将按顺序逐个进行，随时可点击右上角取消）`, "持续批量 AI 诊断", {
					confirmButtonText: "开始诊断",
					cancelButtonText: "取消",
					type: "info"
				});
			} catch {
				return;
			}
			batchAnalyzing.value = true;
			batchCancelled.value = false;
			let successCount = 0;
			let failCount = 0;
			while (!batchCancelled.value) {
				const jobsToAnalyze = jobList.value.filter((job) => !job.aiResult || job.aiResult.score === null || job.aiResult.score === void 0);
				if (jobsToAnalyze.length > 0) {
					const job = jobsToAnalyze[0];
					analyzingId.value = job.jobId;
					try {
						const res = await $fetch$1("/api/jobs/analyze", {
							method: "POST",
							body: { jobId: job.jobId }
						});
						if (res && res.success && res.data) {
							job.aiResult = res.data;
							successCount++;
						} else failCount++;
					} catch (e) {
						console.error(e);
						failCount++;
					} finally {
						analyzingId.value = null;
					}
					await new Promise((resolve) => setTimeout(resolve, 1500));
				} else if (hasMore.value) try {
					await fetchJobs(true);
					await new Promise((resolve) => setTimeout(resolve, 500));
				} catch (e) {
					console.error("加载下一页失败", e);
					break;
				}
				else break;
			}
			batchAnalyzing.value = false;
			if (batchCancelled.value) ElMessage.info(`批量诊断已取消。本次成功: ${successCount}，失败: ${failCount}。`);
			else if (failCount === 0) ElMessage.success(`批量诊断全部完成！成功诊断 ${successCount} 个职位。`);
			else ElMessage.warning(`批量诊断结束。成功: ${successCount}，失败: ${failCount}。`);
		};
		const cancelBatchAiDiagnosis = () => {
			if (!batchAnalyzing.value) return;
			batchCancelled.value = true;
			ElMessage.warning("正在取消，等待当前任务结束后停止...");
		};
		const toggleFavorite = async (job) => {
			try {
				const newStatus = !job.isFavorited;
				const res = await $fetch$1(`/api/jobs/${job.id}`, {
					method: "PUT",
					body: { isFavorited: newStatus }
				});
				if (res && res.success) {
					job.isFavorited = newStatus;
					jobList.value = [...jobList.value];
					ElMessage.success(newStatus ? "已收藏" : "已取消收藏");
				} else ElMessage.error(res?.error || "操作失败");
			} catch (error) {
				console.error("Toggle favorite error:", error);
				ElMessage.error("收藏操作出现异常");
			}
		};
		const handleAiDiagnosis = async (job) => {
			if (analyzingId.value) return;
			analyzingId.value = job.jobId;
			try {
				const res = await $fetch$1("/api/jobs/analyze", {
					method: "POST",
					body: { jobId: job.jobId }
				});
				if (res && res.success && res.data) {
					ElMessage.success("AI诊断完成");
					job.aiResult = res.data;
				} else ElMessage.error(res?.error || "AI诊断失败");
			} catch (error) {
				console.error("AI diagnosis error:", error);
				ElMessage.error("AI诊断出现异常");
			} finally {
				analyzingId.value = null;
			}
		};
		const unsuitableModalVisible = ref(false);
		const currentTaggingJob = ref(null);
		const predefinedTags = [
			"学历不符",
			"经验不符",
			"薪资太低",
			"外包岗位",
			"距离太远",
			"需要外语",
			"岗位与职责不符"
		];
		const selectedTags = ref([]);
		const customTagInput = ref("");
		const savingTags = ref(false);
		const openUnsuitableModal = (job) => {
			currentTaggingJob.value = job;
			selectedTags.value = [];
			if (job.tags) try {
				const parsedTags = typeof job.tags === "string" ? JSON.parse(job.tags) : job.tags;
				if (Array.isArray(parsedTags)) selectedTags.value = [...parsedTags];
			} catch (e) {}
			customTagInput.value = "";
			unsuitableModalVisible.value = true;
		};
		const toggleTag = (tag) => {
			if (selectedTags.value.includes(tag)) selectedTags.value = selectedTags.value.filter((t) => t !== tag);
			else selectedTags.value.push(tag);
		};
		const addCustomTag = () => {
			const tag = customTagInput.value.trim();
			if (tag && !selectedTags.value.includes(tag)) selectedTags.value.push(tag);
			customTagInput.value = "";
		};
		const removeTag = (tag) => {
			selectedTags.value = selectedTags.value.filter((t) => t !== tag);
		};
		const saveUnsuitableTags = async () => {
			if (!currentTaggingJob.value) return;
			savingTags.value = true;
			try {
				const res = await $fetch$1(`/api/jobs/${currentTaggingJob.value.id}`, {
					method: "PUT",
					body: {
						isHidden: true,
						tags: selectedTags.value
					}
				});
				if (res && res.success) {
					ElMessage.success("标记成功并已隐藏职位");
					currentTaggingJob.value.isHidden = true;
					currentTaggingJob.value.tags = [...selectedTags.value];
					if (!filterShowHidden.value) jobList.value = jobList.value.filter((j) => j.jobId !== currentTaggingJob.value.jobId);
					unsuitableModalVisible.value = false;
				} else ElMessage.error(res?.error || res?.message || "保存失败");
			} catch (error) {
				console.error("Failed to save unsuitable tags:", error);
				ElMessage.error("保存失败");
			} finally {
				savingTags.value = false;
			}
		};
		const handleJobStatusChange = (jobData, status) => {
			if (status === "expired") jobList.value = jobList.value.filter((j) => j.jobId !== jobData.jobId);
		};
		const getScoreStyle = (score) => {
			if (!score) return {
				backgroundColor: "#fee2e2",
				color: "#b91c1c"
			};
			if (score >= 80) return {
				backgroundColor: "#dcfce3",
				color: "#15803d"
			};
			if (score >= 60) return {
				backgroundColor: "#ffedd5",
				color: "#c2410c"
			};
			return {
				backgroundColor: "#fee2e2",
				color: "#b91c1c"
			};
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
		return (_ctx, _push, _parent, _attrs) => {
			const _component_el_card = ElCard;
			const _component_el_tag = ElTag;
			const _component_el_input = ElInput;
			const _component_el_select = ElSelect;
			const _component_el_option = ElOption;
			const _component_el_radio_group = ElRadioGroup;
			const _component_el_radio_button = ElRadioButton;
			const _component_el_checkbox = ElCheckbox;
			const _component_el_icon = ElIcon;
			const _component_el_button = ElButton;
			const _component_client_only = ClientOnly;
			const _component_el_empty = ElEmpty;
			const _component_el_dialog = ElDialog;
			const _component_JobDetailDrawer = JobDetailDrawer_default;
			_push(`<div${ssrRenderAttrs(mergeProps({ class: "jobs-page" }, _attrs))} data-v-3ac5c684>`);
			_push(ssrRenderComponent(_component_el_card, {
				shadow: "never",
				class: "table-card"
			}, {
				header: withCtx((_, _push, _parent, _scopeId) => {
					if (_push) {
						_push(`<div class="toolbar" data-v-3ac5c684${_scopeId}><div class="toolbar-row" data-v-3ac5c684${_scopeId}><h2 data-v-3ac5c684${_scopeId}>职位列表 `);
						_push(ssrRenderComponent(_component_el_tag, {
							type: "info",
							size: "small"
						}, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) _push(`${ssrInterpolate(total.value)}`);
								else return [createTextVNode(toDisplayString(total.value), 1)];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(`</h2></div><div class="toolbar-row" data-v-3ac5c684${_scopeId}><div class="filters" data-v-3ac5c684${_scopeId}>`);
						_push(ssrRenderComponent(_component_el_input, {
							modelValue: keywordFilter.value,
							"onUpdate:modelValue": ($event) => keywordFilter.value = $event,
							placeholder: "搜索职位或公司",
							clearable: "",
							style: { "width": "180px" },
							onChange: () => fetchJobs(false)
						}, null, _parent, _scopeId));
						_push(ssrRenderComponent(_component_el_select, {
							modelValue: platformFilter.value,
							"onUpdate:modelValue": ($event) => platformFilter.value = $event,
							placeholder: "全部平台",
							style: { "width": "120px" },
							onChange: () => fetchJobs(false)
						}, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) {
									_push(ssrRenderComponent(_component_el_option, {
										label: "全部平台",
										value: "all"
									}, null, _parent, _scopeId));
									_push(ssrRenderComponent(_component_el_option, {
										label: "Boss直聘",
										value: "Boss直聘"
									}, null, _parent, _scopeId));
									_push(ssrRenderComponent(_component_el_option, {
										label: "前程无忧",
										value: "51job"
									}, null, _parent, _scopeId));
									_push(ssrRenderComponent(_component_el_option, {
										label: "智联招聘",
										value: "智联"
									}, null, _parent, _scopeId));
									_push(ssrRenderComponent(_component_el_option, {
										label: "猎聘",
										value: "猎聘"
									}, null, _parent, _scopeId));
								} else return [
									createVNode(_component_el_option, {
										label: "全部平台",
										value: "all"
									}),
									createVNode(_component_el_option, {
										label: "Boss直聘",
										value: "Boss直聘"
									}),
									createVNode(_component_el_option, {
										label: "前程无忧",
										value: "51job"
									}),
									createVNode(_component_el_option, {
										label: "智联招聘",
										value: "智联"
									}),
									createVNode(_component_el_option, {
										label: "猎聘",
										value: "猎聘"
									})
								];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(ssrRenderComponent(_component_el_select, {
							modelValue: educationFilter.value,
							"onUpdate:modelValue": ($event) => educationFilter.value = $event,
							placeholder: "全部学历",
							style: { "width": "120px" },
							onChange: () => fetchJobs(false)
						}, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) {
									_push(ssrRenderComponent(_component_el_option, {
										label: "全部学历",
										value: "all"
									}, null, _parent, _scopeId));
									_push(ssrRenderComponent(_component_el_option, {
										label: "不限",
										value: "不限"
									}, null, _parent, _scopeId));
									_push(ssrRenderComponent(_component_el_option, {
										label: "大专",
										value: "大专"
									}, null, _parent, _scopeId));
									_push(ssrRenderComponent(_component_el_option, {
										label: "本科",
										value: "本科"
									}, null, _parent, _scopeId));
									_push(ssrRenderComponent(_component_el_option, {
										label: "硕士",
										value: "硕士"
									}, null, _parent, _scopeId));
									_push(ssrRenderComponent(_component_el_option, {
										label: "博士",
										value: "博士"
									}, null, _parent, _scopeId));
								} else return [
									createVNode(_component_el_option, {
										label: "全部学历",
										value: "all"
									}),
									createVNode(_component_el_option, {
										label: "不限",
										value: "不限"
									}),
									createVNode(_component_el_option, {
										label: "大专",
										value: "大专"
									}),
									createVNode(_component_el_option, {
										label: "本科",
										value: "本科"
									}),
									createVNode(_component_el_option, {
										label: "硕士",
										value: "硕士"
									}),
									createVNode(_component_el_option, {
										label: "博士",
										value: "博士"
									})
								];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(ssrRenderComponent(_component_el_select, {
							modelValue: aiDiagnosisFilter.value,
							"onUpdate:modelValue": ($event) => aiDiagnosisFilter.value = $event,
							placeholder: "AI诊断状态",
							style: { "width": "120px" },
							onChange: () => fetchJobs(false)
						}, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) {
									_push(ssrRenderComponent(_component_el_option, {
										label: "全部状态",
										value: "all"
									}, null, _parent, _scopeId));
									_push(ssrRenderComponent(_component_el_option, {
										label: "已诊断",
										value: "diagnosed"
									}, null, _parent, _scopeId));
									_push(ssrRenderComponent(_component_el_option, {
										label: "未诊断",
										value: "undiagnosed"
									}, null, _parent, _scopeId));
								} else return [
									createVNode(_component_el_option, {
										label: "全部状态",
										value: "all"
									}),
									createVNode(_component_el_option, {
										label: "已诊断",
										value: "diagnosed"
									}),
									createVNode(_component_el_option, {
										label: "未诊断",
										value: "undiagnosed"
									})
								];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(ssrRenderComponent(_component_el_select, {
							modelValue: salaryFilter.value,
							"onUpdate:modelValue": ($event) => salaryFilter.value = $event,
							placeholder: "薪资范围",
							style: { "width": "120px" },
							onChange: () => fetchJobs(false)
						}, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) {
									_push(ssrRenderComponent(_component_el_option, {
										label: "全部薪资",
										value: "all"
									}, null, _parent, _scopeId));
									_push(ssrRenderComponent(_component_el_option, {
										label: "10k以下",
										value: "0-10"
									}, null, _parent, _scopeId));
									_push(ssrRenderComponent(_component_el_option, {
										label: "10k-20k",
										value: "10-20"
									}, null, _parent, _scopeId));
									_push(ssrRenderComponent(_component_el_option, {
										label: "20k-30k",
										value: "20-30"
									}, null, _parent, _scopeId));
									_push(ssrRenderComponent(_component_el_option, {
										label: "30k-50k",
										value: "30-50"
									}, null, _parent, _scopeId));
									_push(ssrRenderComponent(_component_el_option, {
										label: "50k以上",
										value: "50-999"
									}, null, _parent, _scopeId));
								} else return [
									createVNode(_component_el_option, {
										label: "全部薪资",
										value: "all"
									}),
									createVNode(_component_el_option, {
										label: "10k以下",
										value: "0-10"
									}),
									createVNode(_component_el_option, {
										label: "10k-20k",
										value: "10-20"
									}),
									createVNode(_component_el_option, {
										label: "20k-30k",
										value: "20-30"
									}),
									createVNode(_component_el_option, {
										label: "30k-50k",
										value: "30-50"
									}),
									createVNode(_component_el_option, {
										label: "50k以上",
										value: "50-999"
									})
								];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(ssrRenderComponent(_component_el_radio_group, {
							modelValue: statusFilter.value,
							"onUpdate:modelValue": ($event) => statusFilter.value = $event,
							size: "small",
							onChange: () => fetchJobs(false)
						}, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) {
									_push(ssrRenderComponent(_component_el_radio_button, { value: "all" }, {
										default: withCtx((_, _push, _parent, _scopeId) => {
											if (_push) _push(`全部`);
											else return [createTextVNode("全部")];
										}),
										_: 1
									}, _parent, _scopeId));
									_push(ssrRenderComponent(_component_el_radio_button, { value: "normal" }, {
										default: withCtx((_, _push, _parent, _scopeId) => {
											if (_push) _push(`常规`);
											else return [createTextVNode("常规")];
										}),
										_: 1
									}, _parent, _scopeId));
								} else return [createVNode(_component_el_radio_button, { value: "all" }, {
									default: withCtx(() => [createTextVNode("全部")]),
									_: 1
								}), createVNode(_component_el_radio_button, { value: "normal" }, {
									default: withCtx(() => [createTextVNode("常规")]),
									_: 1
								})];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(`</div></div><div class="toolbar-row bottom-row" data-v-3ac5c684${_scopeId}><div class="filters-checkboxes" data-v-3ac5c684${_scopeId}>`);
						_push(ssrRenderComponent(_component_el_checkbox, {
							modelValue: filterFavoritesOnly.value,
							"onUpdate:modelValue": ($event) => filterFavoritesOnly.value = $event,
							onChange: () => fetchJobs(false)
						}, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) {
									_push(ssrRenderComponent(_component_el_icon, null, {
										default: withCtx((_, _push, _parent, _scopeId) => {
											if (_push) _push(ssrRenderComponent(unref(Star), null, null, _parent, _scopeId));
											else return [createVNode(unref(Star))];
										}),
										_: 1
									}, _parent, _scopeId));
									_push(` 只看收藏 `);
								} else return [createVNode(_component_el_icon, null, {
									default: withCtx(() => [createVNode(unref(Star))]),
									_: 1
								}), createTextVNode(" 只看收藏 ")];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(ssrRenderComponent(_component_el_checkbox, {
							modelValue: filterShowHidden.value,
							"onUpdate:modelValue": ($event) => filterShowHidden.value = $event,
							onChange: () => fetchJobs(false)
						}, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) {
									_push(ssrRenderComponent(_component_el_icon, null, {
										default: withCtx((_, _push, _parent, _scopeId) => {
											if (_push) _push(ssrRenderComponent(unref(Eye), null, null, _parent, _scopeId));
											else return [createVNode(unref(Eye))];
										}),
										_: 1
									}, _parent, _scopeId));
									_push(` 只看隐藏 `);
								} else return [createVNode(_component_el_icon, null, {
									default: withCtx(() => [createVNode(unref(Eye))]),
									_: 1
								}), createTextVNode(" 只看隐藏 ")];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(ssrRenderComponent(_component_el_checkbox, {
							modelValue: filterShowBlacklisted.value,
							"onUpdate:modelValue": ($event) => filterShowBlacklisted.value = $event,
							onChange: () => fetchJobs(false)
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
									_push(` 显示拉黑 `);
								} else return [createVNode(_component_el_icon, null, {
									default: withCtx(() => [createVNode(unref(Ban))]),
									_: 1
								}), createTextVNode(" 显示拉黑 ")];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(`</div><div class="actions" data-v-3ac5c684${_scopeId}>`);
						if (!batchAnalyzing.value) _push(ssrRenderComponent(_component_el_button, {
							type: "success",
							size: "small",
							onClick: handleBatchAiDiagnosis,
							disabled: loading.value
						}, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) {
									_push(ssrRenderComponent(_component_el_icon, null, {
										default: withCtx((_, _push, _parent, _scopeId) => {
											if (_push) _push(ssrRenderComponent(unref(Bot), null, null, _parent, _scopeId));
											else return [createVNode(unref(Bot))];
										}),
										_: 1
									}, _parent, _scopeId));
									_push(` 批量 AI 诊断 `);
								} else return [createVNode(_component_el_icon, null, {
									default: withCtx(() => [createVNode(unref(Bot))]),
									_: 1
								}), createTextVNode("\xA0批量 AI 诊断 ")];
							}),
							_: 1
						}, _parent, _scopeId));
						else _push(ssrRenderComponent(_component_el_button, {
							type: "danger",
							size: "small",
							onClick: cancelBatchAiDiagnosis
						}, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) {
									_push(ssrRenderComponent(_component_el_icon, null, {
										default: withCtx((_, _push, _parent, _scopeId) => {
											if (_push) _push(ssrRenderComponent(unref(Loader2), { class: "is-loading" }, null, _parent, _scopeId));
											else return [createVNode(unref(Loader2), { class: "is-loading" })];
										}),
										_: 1
									}, _parent, _scopeId));
									_push(` 取消诊断 `);
								} else return [createVNode(_component_el_icon, null, {
									default: withCtx(() => [createVNode(unref(Loader2), { class: "is-loading" })]),
									_: 1
								}), createTextVNode("\xA0取消诊断 ")];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(ssrRenderComponent(_component_el_button, {
							type: "primary",
							size: "small",
							onClick: () => fetchJobs(false),
							loading: loading.value
						}, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) _push(` 刷新数据 `);
								else return [createTextVNode(" 刷新数据 ")];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(`</div></div></div>`);
					} else return [createVNode("div", { class: "toolbar" }, [
						createVNode("div", { class: "toolbar-row" }, [createVNode("h2", null, [createTextVNode("职位列表 "), createVNode(_component_el_tag, {
							type: "info",
							size: "small"
						}, {
							default: withCtx(() => [createTextVNode(toDisplayString(total.value), 1)]),
							_: 1
						})])]),
						createVNode("div", { class: "toolbar-row" }, [createVNode("div", { class: "filters" }, [
							createVNode(_component_el_input, {
								modelValue: keywordFilter.value,
								"onUpdate:modelValue": ($event) => keywordFilter.value = $event,
								placeholder: "搜索职位或公司",
								clearable: "",
								style: { "width": "180px" },
								onChange: () => fetchJobs(false)
							}, null, 8, [
								"modelValue",
								"onUpdate:modelValue",
								"onChange"
							]),
							createVNode(_component_el_select, {
								modelValue: platformFilter.value,
								"onUpdate:modelValue": ($event) => platformFilter.value = $event,
								placeholder: "全部平台",
								style: { "width": "120px" },
								onChange: () => fetchJobs(false)
							}, {
								default: withCtx(() => [
									createVNode(_component_el_option, {
										label: "全部平台",
										value: "all"
									}),
									createVNode(_component_el_option, {
										label: "Boss直聘",
										value: "Boss直聘"
									}),
									createVNode(_component_el_option, {
										label: "前程无忧",
										value: "51job"
									}),
									createVNode(_component_el_option, {
										label: "智联招聘",
										value: "智联"
									}),
									createVNode(_component_el_option, {
										label: "猎聘",
										value: "猎聘"
									})
								]),
								_: 1
							}, 8, [
								"modelValue",
								"onUpdate:modelValue",
								"onChange"
							]),
							createVNode(_component_el_select, {
								modelValue: educationFilter.value,
								"onUpdate:modelValue": ($event) => educationFilter.value = $event,
								placeholder: "全部学历",
								style: { "width": "120px" },
								onChange: () => fetchJobs(false)
							}, {
								default: withCtx(() => [
									createVNode(_component_el_option, {
										label: "全部学历",
										value: "all"
									}),
									createVNode(_component_el_option, {
										label: "不限",
										value: "不限"
									}),
									createVNode(_component_el_option, {
										label: "大专",
										value: "大专"
									}),
									createVNode(_component_el_option, {
										label: "本科",
										value: "本科"
									}),
									createVNode(_component_el_option, {
										label: "硕士",
										value: "硕士"
									}),
									createVNode(_component_el_option, {
										label: "博士",
										value: "博士"
									})
								]),
								_: 1
							}, 8, [
								"modelValue",
								"onUpdate:modelValue",
								"onChange"
							]),
							createVNode(_component_el_select, {
								modelValue: aiDiagnosisFilter.value,
								"onUpdate:modelValue": ($event) => aiDiagnosisFilter.value = $event,
								placeholder: "AI诊断状态",
								style: { "width": "120px" },
								onChange: () => fetchJobs(false)
							}, {
								default: withCtx(() => [
									createVNode(_component_el_option, {
										label: "全部状态",
										value: "all"
									}),
									createVNode(_component_el_option, {
										label: "已诊断",
										value: "diagnosed"
									}),
									createVNode(_component_el_option, {
										label: "未诊断",
										value: "undiagnosed"
									})
								]),
								_: 1
							}, 8, [
								"modelValue",
								"onUpdate:modelValue",
								"onChange"
							]),
							createVNode(_component_el_select, {
								modelValue: salaryFilter.value,
								"onUpdate:modelValue": ($event) => salaryFilter.value = $event,
								placeholder: "薪资范围",
								style: { "width": "120px" },
								onChange: () => fetchJobs(false)
							}, {
								default: withCtx(() => [
									createVNode(_component_el_option, {
										label: "全部薪资",
										value: "all"
									}),
									createVNode(_component_el_option, {
										label: "10k以下",
										value: "0-10"
									}),
									createVNode(_component_el_option, {
										label: "10k-20k",
										value: "10-20"
									}),
									createVNode(_component_el_option, {
										label: "20k-30k",
										value: "20-30"
									}),
									createVNode(_component_el_option, {
										label: "30k-50k",
										value: "30-50"
									}),
									createVNode(_component_el_option, {
										label: "50k以上",
										value: "50-999"
									})
								]),
								_: 1
							}, 8, [
								"modelValue",
								"onUpdate:modelValue",
								"onChange"
							]),
							createVNode(_component_el_radio_group, {
								modelValue: statusFilter.value,
								"onUpdate:modelValue": ($event) => statusFilter.value = $event,
								size: "small",
								onChange: () => fetchJobs(false)
							}, {
								default: withCtx(() => [createVNode(_component_el_radio_button, { value: "all" }, {
									default: withCtx(() => [createTextVNode("全部")]),
									_: 1
								}), createVNode(_component_el_radio_button, { value: "normal" }, {
									default: withCtx(() => [createTextVNode("常规")]),
									_: 1
								})]),
								_: 1
							}, 8, [
								"modelValue",
								"onUpdate:modelValue",
								"onChange"
							])
						])]),
						createVNode("div", { class: "toolbar-row bottom-row" }, [createVNode("div", { class: "filters-checkboxes" }, [
							createVNode(_component_el_checkbox, {
								modelValue: filterFavoritesOnly.value,
								"onUpdate:modelValue": ($event) => filterFavoritesOnly.value = $event,
								onChange: () => fetchJobs(false)
							}, {
								default: withCtx(() => [createVNode(_component_el_icon, null, {
									default: withCtx(() => [createVNode(unref(Star))]),
									_: 1
								}), createTextVNode(" 只看收藏 ")]),
								_: 1
							}, 8, [
								"modelValue",
								"onUpdate:modelValue",
								"onChange"
							]),
							createVNode(_component_el_checkbox, {
								modelValue: filterShowHidden.value,
								"onUpdate:modelValue": ($event) => filterShowHidden.value = $event,
								onChange: () => fetchJobs(false)
							}, {
								default: withCtx(() => [createVNode(_component_el_icon, null, {
									default: withCtx(() => [createVNode(unref(Eye))]),
									_: 1
								}), createTextVNode(" 只看隐藏 ")]),
								_: 1
							}, 8, [
								"modelValue",
								"onUpdate:modelValue",
								"onChange"
							]),
							createVNode(_component_el_checkbox, {
								modelValue: filterShowBlacklisted.value,
								"onUpdate:modelValue": ($event) => filterShowBlacklisted.value = $event,
								onChange: () => fetchJobs(false)
							}, {
								default: withCtx(() => [createVNode(_component_el_icon, null, {
									default: withCtx(() => [createVNode(unref(Ban))]),
									_: 1
								}), createTextVNode(" 显示拉黑 ")]),
								_: 1
							}, 8, [
								"modelValue",
								"onUpdate:modelValue",
								"onChange"
							])
						]), createVNode("div", { class: "actions" }, [!batchAnalyzing.value ? (openBlock(), createBlock(_component_el_button, {
							key: 0,
							type: "success",
							size: "small",
							onClick: handleBatchAiDiagnosis,
							disabled: loading.value
						}, {
							default: withCtx(() => [createVNode(_component_el_icon, null, {
								default: withCtx(() => [createVNode(unref(Bot))]),
								_: 1
							}), createTextVNode("\xA0批量 AI 诊断 ")]),
							_: 1
						}, 8, ["disabled"])) : (openBlock(), createBlock(_component_el_button, {
							key: 1,
							type: "danger",
							size: "small",
							onClick: cancelBatchAiDiagnosis
						}, {
							default: withCtx(() => [createVNode(_component_el_icon, null, {
								default: withCtx(() => [createVNode(unref(Loader2), { class: "is-loading" })]),
								_: 1
							}), createTextVNode("\xA0取消诊断 ")]),
							_: 1
						})), createVNode(_component_el_button, {
							type: "primary",
							size: "small",
							onClick: () => fetchJobs(false),
							loading: loading.value
						}, {
							default: withCtx(() => [createTextVNode(" 刷新数据 ")]),
							_: 1
						}, 8, ["onClick", "loading"])])])
					])];
				}),
				default: withCtx((_, _push, _parent, _scopeId) => {
					if (_push) _push(ssrRenderComponent(_component_client_only, null, { fallback: withCtx((_, _push, _parent, _scopeId) => {
						if (_push) _push(`<div style="${ssrRenderStyle({
							"padding": "20px",
							"text-align": "center"
						})}" data-v-3ac5c684${_scopeId}>加载中...</div>`);
						else return [createVNode("div", { style: {
							"padding": "20px",
							"text-align": "center"
						} }, "加载中...")];
					}) }, _parent, _scopeId));
					else return [createVNode(_component_client_only, null, {
						fallback: withCtx(() => [createVNode("div", { style: {
							"padding": "20px",
							"text-align": "center"
						} }, "加载中...")]),
						default: withCtx(() => [createVNode("div", { class: "job-list-vertical" }, [
							jobList.value.length === 0 && !loading.value ? (openBlock(), createBlock(_component_el_empty, {
								key: 0,
								description: "暂无符合条件的职位"
							})) : (openBlock(true), createBlock(Fragment, { key: 1 }, renderList(jobList.value, (job) => {
								return openBlock(), createBlock("div", {
									class: ["job-card-wide", {
										"is-blacklisted": job.isBlacklisted,
										"is-hidden": job.isHidden
									}],
									key: job.id || job.jobId,
									onClick: ($event) => viewDetails(job)
								}, [
									createVNode("div", { class: "job-header" }, [
										createVNode("span", { class: "job-title" }, toDisplayString(job.title), 1),
										createVNode("span", { class: "job-salary" }, toDisplayString(job.salary), 1),
										job.aiResult ? (openBlock(), createBlock("div", {
											key: 0,
											class: "card-score-badge",
											style: getScoreStyle(job.aiResult.score)
										}, [createTextVNode(toDisplayString(job.aiResult.score), 1), createVNode("span", { style: {
											"font-size": "12px",
											"font-weight": "normal",
											"margin-left": "2px"
										} }, "分")], 4)) : createCommentVNode("", true)
									]),
									createVNode("div", { class: "job-company" }, [createVNode("span", { class: "company-link" }, toDisplayString(job.normalizedData?.companyFullName || job.normalizedData?.brandName || job.companyName), 1)]),
									createVNode("div", { class: "job-tags" }, [
										job.status === "expired" ? (openBlock(), createBlock(_component_el_tag, {
											key: 0,
											type: "danger",
											size: "small",
											effect: "dark",
											style: { "border": "none" }
										}, {
											default: withCtx(() => [createTextVNode("已失效")]),
											_: 1
										})) : createCommentVNode("", true),
										job.isHidden && job.tags && Array.isArray(job.tags) && job.tags.length > 0 ? (openBlock(true), createBlock(Fragment, { key: 1 }, renderList(job.tags, (t, idx) => {
											return openBlock(), createBlock(_component_el_tag, {
												key: "reason-" + idx,
												type: "info",
												size: "small",
												effect: "dark",
												style: { "border": "none" }
											}, {
												default: withCtx(() => [createTextVNode(" 🚫 " + toDisplayString(t), 1)]),
												_: 2
											}, 1024);
										}), 128)) : createCommentVNode("", true),
										createVNode("span", { class: "tag-item" }, toDisplayString(job.platform), 1),
										job.normalizedData?.city || job.location ? (openBlock(), createBlock("span", {
											key: 2,
											class: "tag-item"
										}, toDisplayString([
											job.normalizedData?.city,
											job.normalizedData?.area,
											job.normalizedData?.businessDistrict
										].filter(Boolean).join("·")), 1)) : createCommentVNode("", true),
										createVNode("span", { class: "tag-item" }, toDisplayString(job.normalizedData?.experience || "不限"), 1),
										createVNode("span", { class: "tag-item" }, toDisplayString(job.normalizedData?.degree || "不限"), 1),
										job.normalizedData?.skills?.length ? (openBlock(true), createBlock(Fragment, { key: 3 }, renderList(job.normalizedData.skills.slice(0, 5), (tag, idx) => {
											return openBlock(), createBlock("span", {
												class: "tag-item",
												key: idx
											}, toDisplayString(tag), 1);
										}), 128)) : job.normalizedData?.welfareList?.length ? (openBlock(true), createBlock(Fragment, { key: 4 }, renderList(job.normalizedData.welfareList.slice(0, 5), (tag, idx) => {
											return openBlock(), createBlock("span", {
												class: "tag-item",
												key: idx
											}, toDisplayString(tag), 1);
										}), 128)) : createCommentVNode("", true),
										job.normalizedData?.isHeadhunter ? (openBlock(), createBlock("span", {
											key: 5,
											class: "tag-item",
											style: {
												"background-color": "#fce4ec",
												"color": "#c2185b",
												"border": "1px solid #f8bbd0"
											}
										}, "猎头/代招")) : createCommentVNode("", true)
									]),
									createVNode("div", { class: "job-meta" }, [
										job.normalizedData?.publishDate ? (openBlock(), createBlock("span", {
											key: 0,
											class: "meta-item"
										}, "首发: " + toDisplayString(job.normalizedData.publishDate), 1)) : createCommentVNode("", true),
										job.normalizedData?.updateDate ? (openBlock(), createBlock("span", {
											key: 1,
											class: "meta-item"
										}, "修改: " + toDisplayString(job.normalizedData.updateDate), 1)) : createCommentVNode("", true),
										createVNode("span", { class: "meta-item" }, "状态: " + toDisplayString(job.status || "-"), 1)
									]),
									createVNode("div", { class: "dashed-divider" }),
									createVNode("div", { class: "job-footer" }, [createVNode("div", { class: "footer-left" }, [
										createVNode(_component_el_button, {
											round: "",
											size: "small",
											color: job.isFavorited ? "#ea580c" : "#f1f5f9",
											style: {
												color: job.isFavorited ? "#fff" : "#64748b",
												border: job.isFavorited ? "none" : "1px solid #cbd5e1"
											},
											onClick: withModifiers(($event) => toggleFavorite(job), ["stop"])
										}, {
											default: withCtx(() => [createTextVNode(toDisplayString(job.isFavorited ? "⭐ 已收藏" : "☆ 收藏"), 1)]),
											_: 2
										}, 1032, [
											"color",
											"style",
											"onClick"
										]),
										createVNode(_component_el_button, {
											round: "",
											size: "small",
											color: "#e74c3c",
											style: { "color": "#fff" },
											onClick: withModifiers(($event) => openUnsuitableModal(job), ["stop"])
										}, {
											default: withCtx(() => [createTextVNode("👎 不合适")]),
											_: 1
										}, 8, ["onClick"]),
										createVNode(_component_el_button, {
											round: "",
											size: "small",
											color: "#95a5a6",
											style: { "color": "#fff" },
											onClick: withModifiers(() => {}, ["stop"])
										}, {
											default: withCtx(() => [createTextVNode(toDisplayString(job.isBlacklisted ? "⛔ 已拉黑" : "⛔ 拉黑"), 1)]),
											_: 2
										}, 1032, ["onClick"]),
										createVNode(_component_el_button, {
											round: "",
											size: "small",
											color: "#e74c3c",
											style: { "color": "#fff" },
											onClick: withModifiers(($event) => handleDeleteJob(job), ["stop"])
										}, {
											default: withCtx(() => [createTextVNode("删除")]),
											_: 1
										}, 8, ["onClick"])
									]), createVNode("div", { class: "footer-right" }, [
										createVNode(_component_el_button, {
											round: "",
											size: "small",
											color: "#27ae60",
											style: { "color": "#fff" },
											onClick: withModifiers(() => {}, ["stop"])
										}, {
											default: withCtx(() => [createTextVNode("📅 面试")]),
											_: 1
										}, 8, ["onClick"]),
										createVNode(_component_el_button, {
											round: "",
											size: "small",
											color: "#9b59b6",
											style: { "color": "#fff" },
											loading: analyzingId.value === job.jobId,
											onClick: withModifiers(($event) => handleAiDiagnosis(job), ["stop"])
										}, {
											default: withCtx(() => [createTextVNode("🤖 AI 诊断")]),
											_: 1
										}, 8, ["loading", "onClick"]),
										createVNode(_component_el_button, {
											round: "",
											size: "small",
											color: "#3498db",
											style: { "color": "#fff" },
											onClick: withModifiers(($event) => openOriginalUrl(job), ["stop"])
										}, {
											default: withCtx(() => [createTextVNode("详情 🔗")]),
											_: 1
										}, 8, ["onClick"])
									])])
								], 10, ["onClick"]);
							}), 128)),
							createVNode("div", {
								ref_key: "loadMoreTrigger",
								ref: loadMoreTrigger,
								class: "observer-trigger",
								style: {
									"height": "10px",
									"width": "100%"
								}
							}, null, 512),
							loading.value ? (openBlock(), createBlock("div", {
								key: 2,
								class: "loading-more"
							}, [createVNode(_component_el_icon, { class: "is-loading" }, {
								default: withCtx(() => [createVNode(unref(Loader2))]),
								_: 1
							}), createTextVNode(" 正在加载更多数据... ")])) : createCommentVNode("", true),
							!hasMore.value && jobList.value.length > 0 ? (openBlock(), createBlock("div", {
								key: 3,
								class: "no-more"
							}, " 已经到底啦 ~ ")) : createCommentVNode("", true)
						])]),
						_: 1
					})];
				}),
				_: 1
			}, _parent));
			_push(ssrRenderComponent(_component_el_dialog, {
				modelValue: unsuitableModalVisible.value,
				"onUpdate:modelValue": ($event) => unsuitableModalVisible.value = $event,
				title: "标记为不合适",
				width: "400px",
				"destroy-on-close": ""
			}, {
				footer: withCtx((_, _push, _parent, _scopeId) => {
					if (_push) {
						_push(`<div class="dialog-footer" data-v-3ac5c684${_scopeId}>`);
						_push(ssrRenderComponent(_component_el_button, { onClick: ($event) => unsuitableModalVisible.value = false }, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) _push(`取消`);
								else return [createTextVNode("取消")];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(ssrRenderComponent(_component_el_button, {
							type: "danger",
							onClick: saveUnsuitableTags,
							loading: savingTags.value
						}, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) _push(`保存并隐藏`);
								else return [createTextVNode("保存并隐藏")];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(`</div>`);
					} else return [createVNode("div", { class: "dialog-footer" }, [createVNode(_component_el_button, { onClick: ($event) => unsuitableModalVisible.value = false }, {
						default: withCtx(() => [createTextVNode("取消")]),
						_: 1
					}, 8, ["onClick"]), createVNode(_component_el_button, {
						type: "danger",
						onClick: saveUnsuitableTags,
						loading: savingTags.value
					}, {
						default: withCtx(() => [createTextVNode("保存并隐藏")]),
						_: 1
					}, 8, ["loading"])])];
				}),
				default: withCtx((_, _push, _parent, _scopeId) => {
					if (_push) {
						_push(`<div style="${ssrRenderStyle({
							"margin-bottom": "15px",
							"color": "#7f8c8d",
							"font-size": "14px"
						})}" data-v-3ac5c684${_scopeId}>选择或输入不合适的原因：</div><div style="${ssrRenderStyle({
							"display": "flex",
							"flex-wrap": "wrap",
							"gap": "8px"
						})}" data-v-3ac5c684${_scopeId}><!--[-->`);
						ssrRenderList(predefinedTags, (tag) => {
							_push(ssrRenderComponent(_component_el_button, {
								key: tag,
								size: "small",
								type: selectedTags.value.includes(tag) ? "primary" : "default",
								onClick: ($event) => toggleTag(tag)
							}, {
								default: withCtx((_, _push, _parent, _scopeId) => {
									if (_push) _push(`${ssrInterpolate(tag)}`);
									else return [createTextVNode(toDisplayString(tag), 1)];
								}),
								_: 2
							}, _parent, _scopeId));
						});
						_push(`<!--]--></div><div style="${ssrRenderStyle({ "margin-top": "15px" })}" data-v-3ac5c684${_scopeId}>`);
						_push(ssrRenderComponent(_component_el_input, {
							modelValue: customTagInput.value,
							"onUpdate:modelValue": ($event) => customTagInput.value = $event,
							placeholder: "其他原因 (按回车添加)",
							onKeyup: addCustomTag
						}, null, _parent, _scopeId));
						_push(`</div>`);
						if (selectedTags.value.length > 0) {
							_push(`<div style="${ssrRenderStyle({
								"margin-top": "15px",
								"display": "flex",
								"flex-wrap": "wrap",
								"gap": "8px"
							})}" data-v-3ac5c684${_scopeId}><!--[-->`);
							ssrRenderList(selectedTags.value, (tag) => {
								_push(ssrRenderComponent(_component_el_tag, {
									key: tag,
									closable: "",
									type: "primary",
									onClose: ($event) => removeTag(tag)
								}, {
									default: withCtx((_, _push, _parent, _scopeId) => {
										if (_push) _push(`${ssrInterpolate(tag)}`);
										else return [createTextVNode(toDisplayString(tag), 1)];
									}),
									_: 2
								}, _parent, _scopeId));
							});
							_push(`<!--]--></div>`);
						} else _push(`<!---->`);
					} else return [
						createVNode("div", { style: {
							"margin-bottom": "15px",
							"color": "#7f8c8d",
							"font-size": "14px"
						} }, "选择或输入不合适的原因："),
						createVNode("div", { style: {
							"display": "flex",
							"flex-wrap": "wrap",
							"gap": "8px"
						} }, [(openBlock(), createBlock(Fragment, null, renderList(predefinedTags, (tag) => {
							return createVNode(_component_el_button, {
								key: tag,
								size: "small",
								type: selectedTags.value.includes(tag) ? "primary" : "default",
								onClick: ($event) => toggleTag(tag)
							}, {
								default: withCtx(() => [createTextVNode(toDisplayString(tag), 1)]),
								_: 2
							}, 1032, ["type", "onClick"]);
						}), 64))]),
						createVNode("div", { style: { "margin-top": "15px" } }, [createVNode(_component_el_input, {
							modelValue: customTagInput.value,
							"onUpdate:modelValue": ($event) => customTagInput.value = $event,
							placeholder: "其他原因 (按回车添加)",
							onKeyup: withKeys(addCustomTag, ["enter"])
						}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
						selectedTags.value.length > 0 ? (openBlock(), createBlock("div", {
							key: 0,
							style: {
								"margin-top": "15px",
								"display": "flex",
								"flex-wrap": "wrap",
								"gap": "8px"
							}
						}, [(openBlock(true), createBlock(Fragment, null, renderList(selectedTags.value, (tag) => {
							return openBlock(), createBlock(_component_el_tag, {
								key: tag,
								closable: "",
								type: "primary",
								onClose: ($event) => removeTag(tag)
							}, {
								default: withCtx(() => [createTextVNode(toDisplayString(tag), 1)]),
								_: 2
							}, 1032, ["onClose"]);
						}), 128))])) : createCommentVNode("", true)
					];
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
	(ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/jobs/index.vue");
	return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
var jobs_default = /*#__PURE__*/ _plugin_vue_export_helper_default(_sfc_main, [["__scopeId", "data-v-3ac5c684"]]);

export { jobs_default as default };
//# sourceMappingURL=jobs-BJ5Su2t4.mjs.map
