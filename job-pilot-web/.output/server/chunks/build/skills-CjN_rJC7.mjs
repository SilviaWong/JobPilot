import { _ as _plugin_vue_export_helper_default, $ as $fetch$1, u as useNamespace, d as debugWarn, i as isBoolean, t as throwError, a as isNumber } from '../virtual/entry.mjs';
import { E as ElIcon, w as withInstall, b as buildProps, i as iconPropType, d as definePropType, a as addUnit, l as loading_default } from './icon-BTfkZ5_-.mjs';
import { u as useAriaProps } from './arrays-DLJ1qaKZ.mjs';
import { E as ElInput, U as UPDATE_MODEL_EVENT, C as CHANGE_EVENT, I as INPUT_EVENT } from './css-BTXcRLa_.mjs';
import { E as ElButton, a as ElCard, b as ElMessage, u as useFormItem, c as useFormSize, d as useFormItemInputId, e as useFormDisabled } from './css-n73NFgGH.mjs';
import { E as ElDialog, i as isValidComponentSize } from './css-auYq15mM.mjs';
import { E as ElTable, a as ElTableColumn, b as ElPopconfirm, c as ElForm, d as ElFormItem } from './css-Dvk9M-E-.mjs';
import { v as vLoading } from './css-BxzHr8sS.mjs';
import { ref, reactive, mergeProps, withCtx, unref, createVNode, createTextVNode, openBlock, createBlock, toDisplayString, withDirectives, defineComponent, computed, shallowRef, watch, createElementBlock, withModifiers, normalizeClass, createElementVNode, withKeys, renderSlot, resolveDynamicComponent, createCommentVNode, normalizeStyle, nextTick, useSSRContext } from 'vue';
import { ssrRenderAttrs, ssrRenderComponent, ssrGetDirectiveProps, ssrRenderStyle, ssrInterpolate } from 'vue/server-renderer';
import { Plus, Check, Wand2, Edit, Delete, Info } from 'lucide-vue-next';
import dayjs from 'dayjs';
import { NOOP, isPromise, isString } from '@vue/shared';
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
import './typescript-DnijNOm4.mjs';
import '@ctrl/tinycolor';
import './css-20Rh4iVR.mjs';
import '@popperjs/core';
import './css-DCGp0dw5.mjs';
import 'async-validator';
import 'normalize-wheel-es';

//#region node_modules/element-plus/es/components/switch/src/switch.mjs
/**
* @deprecated Removed after 3.0.0, Use `SwitchProps` instead.
*/
var switchProps = buildProps({
	/**
	* @description binding value, it should be equivalent to either `active-value` or `inactive-value`, by default it's `boolean` type
	*/
	modelValue: {
		type: [
			Boolean,
			String,
			Number
		],
		default: false
	},
	/**
	* @description whether Switch is disabled
	*/
	disabled: {
		type: Boolean,
		default: void 0
	},
	/**
	* @description whether Switch is in loading state
	*/
	loading: Boolean,
	/**
	* @description size of Switch
	*/
	size: {
		type: String,
		validator: isValidComponentSize
	},
	/**
	* @description width of Switch
	*/
	width: {
		type: [String, Number],
		default: ""
	},
	/**
	* @description whether icon or text is displayed inside dot, only the first character will be rendered for text
	*/
	inlinePrompt: Boolean,
	/**
	* @description component of the icon displayed in action when in `off` state
	*/
	inactiveActionIcon: { type: iconPropType },
	/**
	* @description component of the icon displayed in action when in `on` state
	*/
	activeActionIcon: { type: iconPropType },
	/**
	* @description component of the icon displayed when in `on` state, overrides `active-text`
	*/
	activeIcon: { type: iconPropType },
	/**
	* @description component of the icon displayed when in `off` state, overrides `inactive-text`
	*/
	inactiveIcon: { type: iconPropType },
	/**
	* @description text displayed when in `on` state
	*/
	activeText: {
		type: String,
		default: ""
	},
	/**
	* @description text displayed when in `off` state
	*/
	inactiveText: {
		type: String,
		default: ""
	},
	/**
	* @description switch value when in `on` state
	*/
	activeValue: {
		type: [
			Boolean,
			String,
			Number
		],
		default: true
	},
	/**
	* @description switch value when in `off` state
	*/
	inactiveValue: {
		type: [
			Boolean,
			String,
			Number
		],
		default: false
	},
	/**
	* @description input name of Switch
	*/
	name: {
		type: String,
		default: ""
	},
	/**
	* @description whether to trigger form validation
	*/
	validateEvent: {
		type: Boolean,
		default: true
	},
	/**
	* @description before-change hook before the switch state changes. If `false` is returned or a `Promise` is returned and then is rejected, will stop switching
	*/
	beforeChange: { type: definePropType(Function) },
	/**
	* @description id for input
	*/
	id: String,
	/**
	* @description tabindex for input
	*/
	tabindex: { type: [String, Number] },
	...useAriaProps(["ariaLabel"])
});
var switchEmits = {
	[UPDATE_MODEL_EVENT]: (val) => isBoolean(val) || isString(val) || isNumber(val),
	[CHANGE_EVENT]: (val) => isBoolean(val) || isString(val) || isNumber(val),
	[INPUT_EVENT]: (val) => isBoolean(val) || isString(val) || isNumber(val)
};
//#endregion
//#region node_modules/element-plus/es/components/switch/src/switch.vue_vue_type_script_setup_true_lang.mjs
var _hoisted_1 = [
	"id",
	"aria-checked",
	"aria-disabled",
	"aria-label",
	"name",
	"true-value",
	"false-value",
	"disabled",
	"tabindex"
];
var _hoisted_2 = ["aria-hidden"];
var _hoisted_3 = { key: 1 };
var _hoisted_4 = { key: 1 };
var _hoisted_5 = ["aria-hidden"];
var COMPONENT_NAME = "ElSwitch";
//#endregion
//#region node_modules/element-plus/es/components/switch/index.mjs
var ElSwitch = withInstall(/* @__PURE__ */ defineComponent({
	name: COMPONENT_NAME,
	__name: "switch",
	props: switchProps,
	emits: switchEmits,
	setup(__props, { expose: __expose, emit: __emit }) {
		const props = __props;
		const emit = __emit;
		const { formItem } = useFormItem();
		const switchSize = useFormSize();
		const ns = useNamespace("switch");
		const { inputId } = useFormItemInputId(props, { formItemContext: formItem });
		const switchDisabled = useFormDisabled(computed(() => {
			if (props.loading) return true;
		}));
		const isControlled = ref(props.modelValue !== false);
		const input = shallowRef();
		const switchKls = computed(() => [
			ns.b(),
			ns.m(switchSize.value),
			ns.is("disabled", switchDisabled.value),
			ns.is("checked", checked.value)
		]);
		const labelLeftKls = computed(() => [
			ns.e("label"),
			ns.em("label", "left"),
			ns.is("active", !checked.value)
		]);
		const labelRightKls = computed(() => [
			ns.e("label"),
			ns.em("label", "right"),
			ns.is("active", checked.value)
		]);
		const coreStyle = computed(() => ({ width: addUnit(props.width) }));
		watch(() => props.modelValue, () => {
			isControlled.value = true;
		});
		const actualValue = computed(() => {
			return isControlled.value ? props.modelValue : false;
		});
		const checked = computed(() => actualValue.value === props.activeValue);
		if (![props.activeValue, props.inactiveValue].includes(actualValue.value)) {
			debugWarn(COMPONENT_NAME, "model-value must be active-value or inactive-value");
			emit(UPDATE_MODEL_EVENT, props.inactiveValue);
			emit(CHANGE_EVENT, props.inactiveValue);
			emit(INPUT_EVENT, props.inactiveValue);
		}
		watch(checked, (val) => {
			input.value.checked = val;
			if (props.validateEvent) formItem?.validate?.("change").catch(NOOP);
		});
		const handleChange = () => {
			const val = checked.value ? props.inactiveValue : props.activeValue;
			emit(UPDATE_MODEL_EVENT, val);
			emit(CHANGE_EVENT, val);
			emit(INPUT_EVENT, val);
			nextTick(() => {
				input.value.checked = checked.value;
			});
		};
		const switchValue = () => {
			if (switchDisabled.value) return;
			const { beforeChange } = props;
			if (!beforeChange) {
				handleChange();
				return;
			}
			const shouldChange = beforeChange();
			if (![isPromise(shouldChange), isBoolean(shouldChange)].includes(true)) throwError(COMPONENT_NAME, "beforeChange must return type `Promise<boolean>` or `boolean`");
			if (isPromise(shouldChange)) shouldChange.then((result) => {
				if (result) handleChange();
			}).catch((e) => {
				debugWarn(COMPONENT_NAME, `some error occurred: ${e}`);
			});
			else if (shouldChange) handleChange();
		};
		const focus = () => {
			input.value?.focus?.();
		};
		__expose({
			/**
			*  @description manual focus to the switch component
			**/
			focus,
			/**
			* @description whether Switch is checked
			*/
			checked
		});
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("div", {
				class: normalizeClass(switchKls.value),
				onClick: withModifiers(switchValue, ["prevent"])
			}, [
				createElementVNode("input", {
					id: unref(inputId),
					ref_key: "input",
					ref: input,
					class: normalizeClass(unref(ns).e("input")),
					type: "checkbox",
					role: "switch",
					"aria-checked": checked.value,
					"aria-disabled": unref(switchDisabled),
					"aria-label": __props.ariaLabel,
					name: __props.name,
					"true-value": __props.activeValue,
					"false-value": __props.inactiveValue,
					disabled: unref(switchDisabled),
					tabindex: __props.tabindex,
					onChange: handleChange,
					onKeydown: withKeys(switchValue, ["enter"])
				}, null, 42, _hoisted_1),
				!__props.inlinePrompt && (__props.inactiveIcon || __props.inactiveText || _ctx.$slots.inactive) ? (openBlock(), createElementBlock("span", {
					key: 0,
					class: normalizeClass(labelLeftKls.value)
				}, [renderSlot(_ctx.$slots, "inactive", {}, () => [__props.inactiveIcon ? (openBlock(), createBlock(unref(ElIcon), { key: 0 }, {
					default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(__props.inactiveIcon)))]),
					_: 1
				})) : createCommentVNode("v-if", true), !__props.inactiveIcon && __props.inactiveText ? (openBlock(), createElementBlock("span", {
					key: 1,
					"aria-hidden": checked.value
				}, toDisplayString(__props.inactiveText), 9, _hoisted_2)) : createCommentVNode("v-if", true)])], 2)) : createCommentVNode("v-if", true),
				createElementVNode("span", {
					class: normalizeClass(unref(ns).e("core")),
					style: normalizeStyle(coreStyle.value)
				}, [__props.inlinePrompt ? (openBlock(), createElementBlock("div", {
					key: 0,
					class: normalizeClass(unref(ns).e("inner"))
				}, [!checked.value ? (openBlock(), createElementBlock("div", {
					key: 0,
					class: normalizeClass(unref(ns).e("inner-wrapper"))
				}, [renderSlot(_ctx.$slots, "inactive", {}, () => [__props.inactiveIcon ? (openBlock(), createBlock(unref(ElIcon), { key: 0 }, {
					default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(__props.inactiveIcon)))]),
					_: 1
				})) : createCommentVNode("v-if", true), !__props.inactiveIcon && __props.inactiveText ? (openBlock(), createElementBlock("span", _hoisted_3, toDisplayString(__props.inactiveText), 1)) : createCommentVNode("v-if", true)])], 2)) : (openBlock(), createElementBlock("div", {
					key: 1,
					class: normalizeClass(unref(ns).e("inner-wrapper"))
				}, [renderSlot(_ctx.$slots, "active", {}, () => [__props.activeIcon ? (openBlock(), createBlock(unref(ElIcon), { key: 0 }, {
					default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(__props.activeIcon)))]),
					_: 1
				})) : createCommentVNode("v-if", true), !__props.activeIcon && __props.activeText ? (openBlock(), createElementBlock("span", _hoisted_4, toDisplayString(__props.activeText), 1)) : createCommentVNode("v-if", true)])], 2))], 2)) : createCommentVNode("v-if", true), createElementVNode("div", { class: normalizeClass(unref(ns).e("action")) }, [__props.loading ? (openBlock(), createBlock(unref(ElIcon), {
					key: 0,
					class: normalizeClass(unref(ns).is("loading"))
				}, {
					default: withCtx(() => [createVNode(unref(loading_default))]),
					_: 1
				}, 8, ["class"])) : checked.value ? renderSlot(_ctx.$slots, "active-action", { key: 1 }, () => [__props.activeActionIcon ? (openBlock(), createBlock(unref(ElIcon), { key: 0 }, {
					default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(__props.activeActionIcon)))]),
					_: 1
				})) : createCommentVNode("v-if", true)]) : !checked.value ? renderSlot(_ctx.$slots, "inactive-action", { key: 2 }, () => [__props.inactiveActionIcon ? (openBlock(), createBlock(unref(ElIcon), { key: 0 }, {
					default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(__props.inactiveActionIcon)))]),
					_: 1
				})) : createCommentVNode("v-if", true)]) : createCommentVNode("v-if", true)], 2)], 6),
				!__props.inlinePrompt && (__props.activeIcon || __props.activeText || _ctx.$slots.active) ? (openBlock(), createElementBlock("span", {
					key: 1,
					class: normalizeClass(labelRightKls.value)
				}, [renderSlot(_ctx.$slots, "active", {}, () => [__props.activeIcon ? (openBlock(), createBlock(unref(ElIcon), { key: 0 }, {
					default: withCtx(() => [(openBlock(), createBlock(resolveDynamicComponent(__props.activeIcon)))]),
					_: 1
				})) : createCommentVNode("v-if", true), !__props.activeIcon && __props.activeText ? (openBlock(), createElementBlock("span", {
					key: 1,
					"aria-hidden": !checked.value
				}, toDisplayString(__props.activeText), 9, _hoisted_5)) : createCommentVNode("v-if", true)])], 2)) : createCommentVNode("v-if", true)
			], 2);
		};
	}
}));
//#endregion
//#region pages/ai/skills.vue
var _sfc_main = {
	__name: "skills",
	__ssrInlineRender: true,
	setup(__props) {
		const skills = ref([]);
		const loading = ref(false);
		const dialogVisible = ref(false);
		const isEdit = ref(false);
		const saving = ref(false);
		const formRef = ref(null);
		const form = reactive({
			id: "",
			name: "",
			description: "",
			systemPrompt: "",
			userPrompt: "",
			isActive: true
		});
		const rules = {
			name: [{
				required: true,
				message: "请输入技能名称",
				trigger: "blur"
			}],
			systemPrompt: [{
				required: true,
				message: "请输入系统设定",
				trigger: "blur"
			}],
			userPrompt: [{
				required: true,
				message: "请输入用户提问",
				trigger: "blur"
			}]
		};
		const fetchSkills = async () => {
			loading.value = true;
			try {
				const res = await $fetch$1("/api/skills/list");
				if (res && res.success) skills.value = res.data;
				else ElMessage.error(res.error || "加载失败");
			} catch (err) {
				ElMessage.error("服务异常");
			} finally {
				loading.value = false;
			}
		};
		const formatDate = (date) => {
			if (!date) return "-";
			return dayjs(date).format("YYYY-MM-DD HH:mm");
		};
		const handleCreate = () => {
			isEdit.value = false;
			form.id = "";
			form.name = "";
			form.description = "";
			form.systemPrompt = "";
			form.userPrompt = "";
			form.isActive = true;
			dialogVisible.value = true;
		};
		const handleEdit = (row) => {
			isEdit.value = true;
			form.id = row.id;
			form.name = row.name;
			form.description = row.description;
			form.systemPrompt = row.systemPrompt;
			form.userPrompt = row.userPrompt;
			form.isActive = row.isActive;
			dialogVisible.value = true;
		};
		const handleStatusChange = async (row) => {
			try {
				await $fetch$1(`/api/skills/${row.id}`, {
					method: "PUT",
					body: { ...row }
				});
				ElMessage.success("状态更新成功");
			} catch (err) {
				row.isActive = !row.isActive;
				ElMessage.error("更新失败");
			}
		};
		const handleDelete = async (row) => {
			try {
				const res = await $fetch$1(`/api/skills/${row.id}`, { method: "DELETE" });
				if (res.success) {
					ElMessage.success("删除成功");
					fetchSkills();
				} else ElMessage.error(res.error || "删除失败");
			} catch (err) {
				ElMessage.error("服务异常");
			}
		};
		const submitForm = async () => {
			if (!formRef.value) return;
			await formRef.value.validate(async (valid) => {
				if (valid) {
					saving.value = true;
					try {
						const url = isEdit.value ? `/api/skills/${form.id}` : "/api/skills/index";
						const method = isEdit.value ? "PUT" : "POST";
						const res = await $fetch$1(url, {
							method,
							body: {
								name: form.name,
								description: form.description,
								systemPrompt: form.systemPrompt,
								userPrompt: form.userPrompt,
								isActive: form.isActive
							}
						});
						if (res.success) {
							ElMessage.success(isEdit.value ? "修改成功" : "新增成功");
							dialogVisible.value = false;
							fetchSkills();
						} else ElMessage.error(res.error || "保存失败");
					} catch (err) {
						ElMessage.error("服务异常");
					} finally {
						saving.value = false;
					}
				}
			});
		};
		return (_ctx, _push, _parent, _attrs) => {
			const _component_el_button = ElButton;
			const _component_el_icon = ElIcon;
			const _component_el_card = ElCard;
			const _component_el_table = ElTable;
			const _component_el_table_column = ElTableColumn;
			const _component_el_switch = ElSwitch;
			const _component_el_popconfirm = ElPopconfirm;
			const _component_el_dialog = ElDialog;
			const _component_el_form = ElForm;
			const _component_el_form_item = ElFormItem;
			const _component_el_input = ElInput;
			const _directive_loading = vLoading;
			_push(`<div${ssrRenderAttrs(mergeProps({ class: "skills-page" }, _attrs))} data-v-fccc53c6><div class="page-header" data-v-fccc53c6><div data-v-fccc53c6><h1 class="page-title" data-v-fccc53c6>Agent 技能管理</h1><p class="page-subtitle" data-v-fccc53c6>动态添加、修改和管理求职 Agent 技能。支持使用占位符变量注入职位 JD 和个人简历。</p></div>`);
			_push(ssrRenderComponent(_component_el_button, {
				type: "primary",
				onClick: handleCreate
			}, {
				default: withCtx((_, _push, _parent, _scopeId) => {
					if (_push) {
						_push(ssrRenderComponent(_component_el_icon, null, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) _push(ssrRenderComponent(unref(Plus), null, null, _parent, _scopeId));
								else return [createVNode(unref(Plus))];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(` 新增技能 `);
					} else return [createVNode(_component_el_icon, null, {
						default: withCtx(() => [createVNode(unref(Plus))]),
						_: 1
					}), createTextVNode("\xA0新增技能 ")];
				}),
				_: 1
			}, _parent));
			_push(`</div>`);
			_push(ssrRenderComponent(_component_el_card, {
				class: "box-card",
				shadow: "never"
			}, {
				default: withCtx((_, _push, _parent, _scopeId) => {
					if (_push) _push(ssrRenderComponent(_component_el_table, mergeProps({
						data: skills.value,
						style: { "width": "100%" }
					}, ssrGetDirectiveProps(_ctx, _directive_loading, loading.value)), {
						default: withCtx((_, _push, _parent, _scopeId) => {
							if (_push) {
								_push(ssrRenderComponent(_component_el_table_column, {
									prop: "name",
									label: "技能名称",
									width: "180"
								}, {
									default: withCtx(({ row }, _push, _parent, _scopeId) => {
										if (_push) {
											_push(`<div style="${ssrRenderStyle({
												"font-weight": "500",
												"color": "#1e293b"
											})}" data-v-fccc53c6${_scopeId}>`);
											if (row.name.includes("匹配")) _push(ssrRenderComponent(_component_el_icon, { color: "#10b981" }, {
												default: withCtx((_, _push, _parent, _scopeId) => {
													if (_push) _push(ssrRenderComponent(unref(Check), null, null, _parent, _scopeId));
													else return [createVNode(unref(Check))];
												}),
												_: 2
											}, _parent, _scopeId));
											else _push(ssrRenderComponent(_component_el_icon, { color: "#0ea5e9" }, {
												default: withCtx((_, _push, _parent, _scopeId) => {
													if (_push) _push(ssrRenderComponent(unref(Wand2), null, null, _parent, _scopeId));
													else return [createVNode(unref(Wand2))];
												}),
												_: 2
											}, _parent, _scopeId));
											_push(`  ${ssrInterpolate(row.name)}</div>`);
										} else return [createVNode("div", { style: {
											"font-weight": "500",
											"color": "#1e293b"
										} }, [row.name.includes("匹配") ? (openBlock(), createBlock(_component_el_icon, {
											key: 0,
											color: "#10b981"
										}, {
											default: withCtx(() => [createVNode(unref(Check))]),
											_: 1
										})) : (openBlock(), createBlock(_component_el_icon, {
											key: 1,
											color: "#0ea5e9"
										}, {
											default: withCtx(() => [createVNode(unref(Wand2))]),
											_: 1
										})), createTextVNode(" \xA0" + toDisplayString(row.name), 1)])];
									}),
									_: 1
								}, _parent, _scopeId));
								_push(ssrRenderComponent(_component_el_table_column, {
									prop: "description",
									label: "描述"
								}, null, _parent, _scopeId));
								_push(ssrRenderComponent(_component_el_table_column, {
									prop: "isActive",
									label: "状态",
									width: "100"
								}, {
									default: withCtx(({ row }, _push, _parent, _scopeId) => {
										if (_push) _push(ssrRenderComponent(_component_el_switch, {
											modelValue: row.isActive,
											"onUpdate:modelValue": ($event) => row.isActive = $event,
											"inline-prompt": "",
											"active-text": "开启",
											"inactive-text": "关闭",
											onChange: ($event) => handleStatusChange(row)
										}, null, _parent, _scopeId));
										else return [createVNode(_component_el_switch, {
											modelValue: row.isActive,
											"onUpdate:modelValue": ($event) => row.isActive = $event,
											"inline-prompt": "",
											"active-text": "开启",
											"inactive-text": "关闭",
											onChange: ($event) => handleStatusChange(row)
										}, null, 8, [
											"modelValue",
											"onUpdate:modelValue",
											"onChange"
										])];
									}),
									_: 1
								}, _parent, _scopeId));
								_push(ssrRenderComponent(_component_el_table_column, {
									prop: "createdAt",
									label: "创建时间",
									width: "180"
								}, {
									default: withCtx(({ row }, _push, _parent, _scopeId) => {
										if (_push) _push(`${ssrInterpolate(formatDate(row.createdAt))}`);
										else return [createTextVNode(toDisplayString(formatDate(row.createdAt)), 1)];
									}),
									_: 1
								}, _parent, _scopeId));
								_push(ssrRenderComponent(_component_el_table_column, {
									label: "操作",
									width: "160",
									fixed: "right"
								}, {
									default: withCtx(({ row }, _push, _parent, _scopeId) => {
										if (_push) {
											_push(ssrRenderComponent(_component_el_button, {
												type: "primary",
												link: "",
												onClick: ($event) => handleEdit(row)
											}, {
												default: withCtx((_, _push, _parent, _scopeId) => {
													if (_push) {
														_push(ssrRenderComponent(_component_el_icon, null, {
															default: withCtx((_, _push, _parent, _scopeId) => {
																if (_push) _push(ssrRenderComponent(unref(Edit), null, null, _parent, _scopeId));
																else return [createVNode(unref(Edit))];
															}),
															_: 2
														}, _parent, _scopeId));
														_push(` 编辑 `);
													} else return [createVNode(_component_el_icon, null, {
														default: withCtx(() => [createVNode(unref(Edit))]),
														_: 1
													}), createTextVNode("\xA0编辑 ")];
												}),
												_: 2
											}, _parent, _scopeId));
											_push(ssrRenderComponent(_component_el_popconfirm, {
												title: "确定要删除这个技能吗？",
												"confirm-button-text": "删除",
												"cancel-button-text": "取消",
												"confirm-button-type": "danger",
												onConfirm: ($event) => handleDelete(row)
											}, {
												reference: withCtx((_, _push, _parent, _scopeId) => {
													if (_push) _push(ssrRenderComponent(_component_el_button, {
														type: "danger",
														link: ""
													}, {
														default: withCtx((_, _push, _parent, _scopeId) => {
															if (_push) {
																_push(ssrRenderComponent(_component_el_icon, null, {
																	default: withCtx((_, _push, _parent, _scopeId) => {
																		if (_push) _push(ssrRenderComponent(unref(Delete), null, null, _parent, _scopeId));
																		else return [createVNode(unref(Delete))];
																	}),
																	_: 2
																}, _parent, _scopeId));
																_push(` 删除 `);
															} else return [createVNode(_component_el_icon, null, {
																default: withCtx(() => [createVNode(unref(Delete))]),
																_: 1
															}), createTextVNode("\xA0删除 ")];
														}),
														_: 2
													}, _parent, _scopeId));
													else return [createVNode(_component_el_button, {
														type: "danger",
														link: ""
													}, {
														default: withCtx(() => [createVNode(_component_el_icon, null, {
															default: withCtx(() => [createVNode(unref(Delete))]),
															_: 1
														}), createTextVNode("\xA0删除 ")]),
														_: 1
													})];
												}),
												_: 2
											}, _parent, _scopeId));
										} else return [createVNode(_component_el_button, {
											type: "primary",
											link: "",
											onClick: ($event) => handleEdit(row)
										}, {
											default: withCtx(() => [createVNode(_component_el_icon, null, {
												default: withCtx(() => [createVNode(unref(Edit))]),
												_: 1
											}), createTextVNode("\xA0编辑 ")]),
											_: 1
										}, 8, ["onClick"]), createVNode(_component_el_popconfirm, {
											title: "确定要删除这个技能吗？",
											"confirm-button-text": "删除",
											"cancel-button-text": "取消",
											"confirm-button-type": "danger",
											onConfirm: ($event) => handleDelete(row)
										}, {
											reference: withCtx(() => [createVNode(_component_el_button, {
												type: "danger",
												link: ""
											}, {
												default: withCtx(() => [createVNode(_component_el_icon, null, {
													default: withCtx(() => [createVNode(unref(Delete))]),
													_: 1
												}), createTextVNode("\xA0删除 ")]),
												_: 1
											})]),
											_: 1
										}, 8, ["onConfirm"])];
									}),
									_: 1
								}, _parent, _scopeId));
							} else return [
								createVNode(_component_el_table_column, {
									prop: "name",
									label: "技能名称",
									width: "180"
								}, {
									default: withCtx(({ row }) => [createVNode("div", { style: {
										"font-weight": "500",
										"color": "#1e293b"
									} }, [row.name.includes("匹配") ? (openBlock(), createBlock(_component_el_icon, {
										key: 0,
										color: "#10b981"
									}, {
										default: withCtx(() => [createVNode(unref(Check))]),
										_: 1
									})) : (openBlock(), createBlock(_component_el_icon, {
										key: 1,
										color: "#0ea5e9"
									}, {
										default: withCtx(() => [createVNode(unref(Wand2))]),
										_: 1
									})), createTextVNode(" \xA0" + toDisplayString(row.name), 1)])]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									prop: "description",
									label: "描述"
								}),
								createVNode(_component_el_table_column, {
									prop: "isActive",
									label: "状态",
									width: "100"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_switch, {
										modelValue: row.isActive,
										"onUpdate:modelValue": ($event) => row.isActive = $event,
										"inline-prompt": "",
										"active-text": "开启",
										"inactive-text": "关闭",
										onChange: ($event) => handleStatusChange(row)
									}, null, 8, [
										"modelValue",
										"onUpdate:modelValue",
										"onChange"
									])]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									prop: "createdAt",
									label: "创建时间",
									width: "180"
								}, {
									default: withCtx(({ row }) => [createTextVNode(toDisplayString(formatDate(row.createdAt)), 1)]),
									_: 1
								}),
								createVNode(_component_el_table_column, {
									label: "操作",
									width: "160",
									fixed: "right"
								}, {
									default: withCtx(({ row }) => [createVNode(_component_el_button, {
										type: "primary",
										link: "",
										onClick: ($event) => handleEdit(row)
									}, {
										default: withCtx(() => [createVNode(_component_el_icon, null, {
											default: withCtx(() => [createVNode(unref(Edit))]),
											_: 1
										}), createTextVNode("\xA0编辑 ")]),
										_: 1
									}, 8, ["onClick"]), createVNode(_component_el_popconfirm, {
										title: "确定要删除这个技能吗？",
										"confirm-button-text": "删除",
										"cancel-button-text": "取消",
										"confirm-button-type": "danger",
										onConfirm: ($event) => handleDelete(row)
									}, {
										reference: withCtx(() => [createVNode(_component_el_button, {
											type: "danger",
											link: ""
										}, {
											default: withCtx(() => [createVNode(_component_el_icon, null, {
												default: withCtx(() => [createVNode(unref(Delete))]),
												_: 1
											}), createTextVNode("\xA0删除 ")]),
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
					else return [withDirectives((openBlock(), createBlock(_component_el_table, {
						data: skills.value,
						style: { "width": "100%" }
					}, {
						default: withCtx(() => [
							createVNode(_component_el_table_column, {
								prop: "name",
								label: "技能名称",
								width: "180"
							}, {
								default: withCtx(({ row }) => [createVNode("div", { style: {
									"font-weight": "500",
									"color": "#1e293b"
								} }, [row.name.includes("匹配") ? (openBlock(), createBlock(_component_el_icon, {
									key: 0,
									color: "#10b981"
								}, {
									default: withCtx(() => [createVNode(unref(Check))]),
									_: 1
								})) : (openBlock(), createBlock(_component_el_icon, {
									key: 1,
									color: "#0ea5e9"
								}, {
									default: withCtx(() => [createVNode(unref(Wand2))]),
									_: 1
								})), createTextVNode(" \xA0" + toDisplayString(row.name), 1)])]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								prop: "description",
								label: "描述"
							}),
							createVNode(_component_el_table_column, {
								prop: "isActive",
								label: "状态",
								width: "100"
							}, {
								default: withCtx(({ row }) => [createVNode(_component_el_switch, {
									modelValue: row.isActive,
									"onUpdate:modelValue": ($event) => row.isActive = $event,
									"inline-prompt": "",
									"active-text": "开启",
									"inactive-text": "关闭",
									onChange: ($event) => handleStatusChange(row)
								}, null, 8, [
									"modelValue",
									"onUpdate:modelValue",
									"onChange"
								])]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								prop: "createdAt",
								label: "创建时间",
								width: "180"
							}, {
								default: withCtx(({ row }) => [createTextVNode(toDisplayString(formatDate(row.createdAt)), 1)]),
								_: 1
							}),
							createVNode(_component_el_table_column, {
								label: "操作",
								width: "160",
								fixed: "right"
							}, {
								default: withCtx(({ row }) => [createVNode(_component_el_button, {
									type: "primary",
									link: "",
									onClick: ($event) => handleEdit(row)
								}, {
									default: withCtx(() => [createVNode(_component_el_icon, null, {
										default: withCtx(() => [createVNode(unref(Edit))]),
										_: 1
									}), createTextVNode("\xA0编辑 ")]),
									_: 1
								}, 8, ["onClick"]), createVNode(_component_el_popconfirm, {
									title: "确定要删除这个技能吗？",
									"confirm-button-text": "删除",
									"cancel-button-text": "取消",
									"confirm-button-type": "danger",
									onConfirm: ($event) => handleDelete(row)
								}, {
									reference: withCtx(() => [createVNode(_component_el_button, {
										type: "danger",
										link: ""
									}, {
										default: withCtx(() => [createVNode(_component_el_icon, null, {
											default: withCtx(() => [createVNode(unref(Delete))]),
											_: 1
										}), createTextVNode("\xA0删除 ")]),
										_: 1
									})]),
									_: 1
								}, 8, ["onConfirm"])]),
								_: 1
							})
						]),
						_: 1
					}, 8, ["data"])), [[_directive_loading, loading.value]])];
				}),
				_: 1
			}, _parent));
			_push(ssrRenderComponent(_component_el_dialog, {
				modelValue: dialogVisible.value,
				"onUpdate:modelValue": ($event) => dialogVisible.value = $event,
				title: isEdit.value ? "编辑技能" : "新增技能",
				width: "650px",
				"destroy-on-close": ""
			}, {
				footer: withCtx((_, _push, _parent, _scopeId) => {
					if (_push) {
						_push(`<div class="dialog-footer" data-v-fccc53c6${_scopeId}>`);
						_push(ssrRenderComponent(_component_el_button, { onClick: ($event) => dialogVisible.value = false }, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) _push(`取消`);
								else return [createTextVNode("取消")];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(ssrRenderComponent(_component_el_button, {
							type: "primary",
							loading: saving.value,
							onClick: submitForm
						}, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) _push(` 确定 `);
								else return [createTextVNode(" 确定 ")];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(`</div>`);
					} else return [createVNode("div", { class: "dialog-footer" }, [createVNode(_component_el_button, { onClick: ($event) => dialogVisible.value = false }, {
						default: withCtx(() => [createTextVNode("取消")]),
						_: 1
					}, 8, ["onClick"]), createVNode(_component_el_button, {
						type: "primary",
						loading: saving.value,
						onClick: submitForm
					}, {
						default: withCtx(() => [createTextVNode(" 确定 ")]),
						_: 1
					}, 8, ["loading"])])];
				}),
				default: withCtx((_, _push, _parent, _scopeId) => {
					if (_push) _push(ssrRenderComponent(_component_el_form, {
						model: form,
						rules,
						ref_key: "formRef",
						ref: formRef,
						"label-width": "100px",
						"label-position": "top"
					}, {
						default: withCtx((_, _push, _parent, _scopeId) => {
							if (_push) {
								_push(ssrRenderComponent(_component_el_form_item, {
									label: "技能名称",
									prop: "name"
								}, {
									default: withCtx((_, _push, _parent, _scopeId) => {
										if (_push) _push(ssrRenderComponent(_component_el_input, {
											modelValue: form.name,
											"onUpdate:modelValue": ($event) => form.name = $event,
											placeholder: "例如：提炼公司亮点"
										}, null, _parent, _scopeId));
										else return [createVNode(_component_el_input, {
											modelValue: form.name,
											"onUpdate:modelValue": ($event) => form.name = $event,
											placeholder: "例如：提炼公司亮点"
										}, null, 8, ["modelValue", "onUpdate:modelValue"])];
									}),
									_: 1
								}, _parent, _scopeId));
								_push(ssrRenderComponent(_component_el_form_item, {
									label: "技能描述",
									prop: "description"
								}, {
									default: withCtx((_, _push, _parent, _scopeId) => {
										if (_push) _push(ssrRenderComponent(_component_el_input, {
											modelValue: form.description,
											"onUpdate:modelValue": ($event) => form.description = $event,
											type: "textarea",
											rows: 2,
											placeholder: "简短描述该技能的用途，会显示在表格中"
										}, null, _parent, _scopeId));
										else return [createVNode(_component_el_input, {
											modelValue: form.description,
											"onUpdate:modelValue": ($event) => form.description = $event,
											type: "textarea",
											rows: 2,
											placeholder: "简短描述该技能的用途，会显示在表格中"
										}, null, 8, ["modelValue", "onUpdate:modelValue"])];
									}),
									_: 1
								}, _parent, _scopeId));
								_push(`<div class="info-alert" data-v-fccc53c6${_scopeId}>`);
								_push(ssrRenderComponent(_component_el_icon, null, {
									default: withCtx((_, _push, _parent, _scopeId) => {
										if (_push) _push(ssrRenderComponent(unref(Info), null, null, _parent, _scopeId));
										else return [createVNode(unref(Info))];
									}),
									_: 1
								}, _parent, _scopeId));
								_push(`<div class="info-content" data-v-fccc53c6${_scopeId}><p data-v-fccc53c6${_scopeId}><strong data-v-fccc53c6${_scopeId}>支持的占位符（发给模型前会自动替换）：</strong></p><ul data-v-fccc53c6${_scopeId}><li data-v-fccc53c6${_scopeId}><code data-v-fccc53c6${_scopeId}>{{JD_TEXT}}</code>：当前职位的完整描述（JD）</li><li data-v-fccc53c6${_scopeId}><code data-v-fccc53c6${_scopeId}>{{RESUME_TEXT}}</code>：系统设置中你当前配置的个人简历</li></ul></div></div>`);
								_push(ssrRenderComponent(_component_el_form_item, {
									label: "系统设定 (System Prompt)",
									prop: "systemPrompt"
								}, {
									default: withCtx((_, _push, _parent, _scopeId) => {
										if (_push) _push(ssrRenderComponent(_component_el_input, {
											modelValue: form.systemPrompt,
											"onUpdate:modelValue": ($event) => form.systemPrompt = $event,
											type: "textarea",
											rows: 6,
											placeholder: "大模型的角色设定。例如：你是一个资深的求职专家..."
										}, null, _parent, _scopeId));
										else return [createVNode(_component_el_input, {
											modelValue: form.systemPrompt,
											"onUpdate:modelValue": ($event) => form.systemPrompt = $event,
											type: "textarea",
											rows: 6,
											placeholder: "大模型的角色设定。例如：你是一个资深的求职专家..."
										}, null, 8, ["modelValue", "onUpdate:modelValue"])];
									}),
									_: 1
								}, _parent, _scopeId));
								_push(ssrRenderComponent(_component_el_form_item, {
									label: "用户提问 (User Prompt)",
									prop: "userPrompt"
								}, {
									default: withCtx((_, _push, _parent, _scopeId) => {
										if (_push) _push(ssrRenderComponent(_component_el_input, {
											modelValue: form.userPrompt,
											"onUpdate:modelValue": ($event) => form.userPrompt = $event,
											type: "textarea",
											rows: 6,
											placeholder: "具体的提问内容。记得使用占位符注入数据..."
										}, null, _parent, _scopeId));
										else return [createVNode(_component_el_input, {
											modelValue: form.userPrompt,
											"onUpdate:modelValue": ($event) => form.userPrompt = $event,
											type: "textarea",
											rows: 6,
											placeholder: "具体的提问内容。记得使用占位符注入数据..."
										}, null, 8, ["modelValue", "onUpdate:modelValue"])];
									}),
									_: 1
								}, _parent, _scopeId));
							} else return [
								createVNode(_component_el_form_item, {
									label: "技能名称",
									prop: "name"
								}, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: form.name,
										"onUpdate:modelValue": ($event) => form.name = $event,
										placeholder: "例如：提炼公司亮点"
									}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, {
									label: "技能描述",
									prop: "description"
								}, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: form.description,
										"onUpdate:modelValue": ($event) => form.description = $event,
										type: "textarea",
										rows: 2,
										placeholder: "简短描述该技能的用途，会显示在表格中"
									}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
									_: 1
								}),
								createVNode("div", { class: "info-alert" }, [createVNode(_component_el_icon, null, {
									default: withCtx(() => [createVNode(unref(Info))]),
									_: 1
								}), createVNode("div", { class: "info-content" }, [createVNode("p", null, [createVNode("strong", null, "支持的占位符（发给模型前会自动替换）：")]), createVNode("ul", null, [createVNode("li", null, [createVNode("code", null, "{{JD_TEXT}}"), createTextVNode("：当前职位的完整描述（JD）")]), createVNode("li", null, [createVNode("code", null, "{{RESUME_TEXT}}"), createTextVNode("：系统设置中你当前配置的个人简历")])])])]),
								createVNode(_component_el_form_item, {
									label: "系统设定 (System Prompt)",
									prop: "systemPrompt"
								}, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: form.systemPrompt,
										"onUpdate:modelValue": ($event) => form.systemPrompt = $event,
										type: "textarea",
										rows: 6,
										placeholder: "大模型的角色设定。例如：你是一个资深的求职专家..."
									}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
									_: 1
								}),
								createVNode(_component_el_form_item, {
									label: "用户提问 (User Prompt)",
									prop: "userPrompt"
								}, {
									default: withCtx(() => [createVNode(_component_el_input, {
										modelValue: form.userPrompt,
										"onUpdate:modelValue": ($event) => form.userPrompt = $event,
										type: "textarea",
										rows: 6,
										placeholder: "具体的提问内容。记得使用占位符注入数据..."
									}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
									_: 1
								})
							];
						}),
						_: 1
					}, _parent, _scopeId));
					else return [createVNode(_component_el_form, {
						model: form,
						rules,
						ref_key: "formRef",
						ref: formRef,
						"label-width": "100px",
						"label-position": "top"
					}, {
						default: withCtx(() => [
							createVNode(_component_el_form_item, {
								label: "技能名称",
								prop: "name"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: form.name,
									"onUpdate:modelValue": ($event) => form.name = $event,
									placeholder: "例如：提炼公司亮点"
								}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "技能描述",
								prop: "description"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: form.description,
									"onUpdate:modelValue": ($event) => form.description = $event,
									type: "textarea",
									rows: 2,
									placeholder: "简短描述该技能的用途，会显示在表格中"
								}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
								_: 1
							}),
							createVNode("div", { class: "info-alert" }, [createVNode(_component_el_icon, null, {
								default: withCtx(() => [createVNode(unref(Info))]),
								_: 1
							}), createVNode("div", { class: "info-content" }, [createVNode("p", null, [createVNode("strong", null, "支持的占位符（发给模型前会自动替换）：")]), createVNode("ul", null, [createVNode("li", null, [createVNode("code", null, "{{JD_TEXT}}"), createTextVNode("：当前职位的完整描述（JD）")]), createVNode("li", null, [createVNode("code", null, "{{RESUME_TEXT}}"), createTextVNode("：系统设置中你当前配置的个人简历")])])])]),
							createVNode(_component_el_form_item, {
								label: "系统设定 (System Prompt)",
								prop: "systemPrompt"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: form.systemPrompt,
									"onUpdate:modelValue": ($event) => form.systemPrompt = $event,
									type: "textarea",
									rows: 6,
									placeholder: "大模型的角色设定。例如：你是一个资深的求职专家..."
								}, null, 8, ["modelValue", "onUpdate:modelValue"])]),
								_: 1
							}),
							createVNode(_component_el_form_item, {
								label: "用户提问 (User Prompt)",
								prop: "userPrompt"
							}, {
								default: withCtx(() => [createVNode(_component_el_input, {
									modelValue: form.userPrompt,
									"onUpdate:modelValue": ($event) => form.userPrompt = $event,
									type: "textarea",
									rows: 6,
									placeholder: "具体的提问内容。记得使用占位符注入数据..."
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
	(ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/ai/skills.vue");
	return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
var skills_default = /*#__PURE__*/ _plugin_vue_export_helper_default(_sfc_main, [["__scopeId", "data-v-fccc53c6"]]);

export { skills_default as default };
//# sourceMappingURL=skills-CjN_rJC7.mjs.map
