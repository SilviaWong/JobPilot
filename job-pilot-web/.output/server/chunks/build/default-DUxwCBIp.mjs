import { _ as _plugin_vue_export_helper_default, u as useNamespace, t as throwError, c as isUndefined$1 } from '../virtual/entry.mjs';
import { E as ElIcon, w as withInstall, c as withNoopInstall, b as buildProps, i as iconPropType, d as definePropType, x as arrow_down_default, e as arrow_right_default, L as more_default, M as focusElement, y as addClass, B as hasClass, z as removeClass } from './icon-BTfkZ5_-.mjs';
import { E as ElTooltip } from './css-20Rh4iVR.mjs';
import { m as mutable } from './typescript-DnijNOm4.mjs';
import { E as ElCollapseTransition } from './collapse-transition-zZusOYp2.mjs';
import { f as flattedChildren } from './vnode-BA-uK14B.mjs';
import { C as ClickOutside } from './click-outside-mj8goBwo.mjs';
import { computed, mergeProps, withCtx, unref, createVNode, renderSlot, defineComponent, getCurrentInstance, inject, ref, reactive, watch, provide, h, Fragment, withDirectives, vShow, watchEffect, useSSRContext, openBlock, createElementBlock, normalizeClass, normalizeStyle, useSlots, nextTick, createBlock, Transition, createElementVNode, createTextVNode, toDisplayString, toRef } from 'vue';
import { useRoute } from 'vue-router';
import { useResizeObserver, useTimeoutFn, unrefElement } from '@vueuse/core';
import { isNil } from 'lodash-unified';
import { ssrRenderComponent, ssrRenderSlot } from 'vue/server-renderer';
import { TinyColor } from '@ctrl/tinycolor';
import { Briefcase, ClipboardList, Building2, LayoutDashboard, MapPin, Bot, FileText, BookOpen } from 'lucide-vue-next';
import { isString, isObject, isArray } from '@vue/shared';
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
import 'unhead/utils';
import './arrays-DLJ1qaKZ.mjs';
import '@popperjs/core';

//#endregion
//#region node_modules/element-plus/es/components/container/src/container.mjs
var container_default = /* @__PURE__ */ defineComponent({
	name: "ElContainer",
	__name: "container",
	props: { direction: {
		type: String,
		required: false
	} },
	setup(__props) {
		const props = __props;
		const slots = useSlots();
		const ns = useNamespace("container");
		const isVertical = computed(() => {
			if (props.direction === "vertical") return true;
			else if (props.direction === "horizontal") return false;
			if (slots && slots.default) return slots.default().some((vNode) => {
				const tag = vNode.type.name;
				return tag === "ElHeader" || tag === "ElFooter";
			});
			else return false;
		});
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("section", { class: normalizeClass([unref(ns).b(), unref(ns).is("vertical", isVertical.value)]) }, [renderSlot(_ctx.$slots, "default")], 2);
		};
	}
});
//#endregion
//#region node_modules/element-plus/es/components/container/src/aside.mjs
var aside_default = /* @__PURE__ */ defineComponent({
	name: "ElAside",
	__name: "aside",
	props: { width: {
		type: [String, null],
		required: false,
		default: null
	} },
	setup(__props) {
		const props = __props;
		const ns = useNamespace("aside");
		const style = computed(() => props.width ? ns.cssVarBlock({ width: props.width }) : {});
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("aside", {
				class: normalizeClass(unref(ns).b()),
				style: normalizeStyle(style.value)
			}, [renderSlot(_ctx.$slots, "default")], 6);
		};
	}
});
//#endregion
//#region node_modules/element-plus/es/components/container/src/footer.mjs
var footer_default = /* @__PURE__ */ defineComponent({
	name: "ElFooter",
	__name: "footer",
	props: { height: {
		type: [String, null],
		required: false,
		default: null
	} },
	setup(__props) {
		const props = __props;
		const ns = useNamespace("footer");
		const style = computed(() => props.height ? ns.cssVarBlock({ height: props.height }) : {});
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("footer", {
				class: normalizeClass(unref(ns).b()),
				style: normalizeStyle(style.value)
			}, [renderSlot(_ctx.$slots, "default")], 6);
		};
	}
});
//#endregion
//#region node_modules/element-plus/es/components/container/src/header.mjs
var header_default = /* @__PURE__ */ defineComponent({
	name: "ElHeader",
	__name: "header",
	props: { height: {
		type: [String, null],
		required: false,
		default: null
	} },
	setup(__props) {
		const props = __props;
		const ns = useNamespace("header");
		const style = computed(() => {
			return props.height ? ns.cssVarBlock({ height: props.height }) : {};
		});
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("header", {
				class: normalizeClass(unref(ns).b()),
				style: normalizeStyle(style.value)
			}, [renderSlot(_ctx.$slots, "default")], 6);
		};
	}
});
//#endregion
//#region node_modules/element-plus/es/components/container/src/main.mjs
var main_default = /* @__PURE__ */ defineComponent({
	name: "ElMain",
	__name: "main",
	setup(__props) {
		const ns = useNamespace("main");
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("main", { class: normalizeClass(unref(ns).b()) }, [renderSlot(_ctx.$slots, "default")], 2);
		};
	}
});
//#endregion
//#region node_modules/element-plus/es/components/container/index.mjs
var ElContainer = withInstall(container_default, {
	Aside: aside_default,
	Footer: footer_default,
	Header: header_default,
	Main: main_default
});
var ElAside = withNoopInstall(aside_default);
withNoopInstall(footer_default);
var ElHeader = withNoopInstall(header_default);
var ElMain = withNoopInstall(main_default);
//#endregion
//#region node_modules/element-plus/es/components/menu/src/tokens.mjs
var MENU_INJECTION_KEY = "rootMenu";
var SUB_MENU_INJECTION_KEY = "subMenu:";
//#endregion
//#region node_modules/element-plus/es/components/menu/src/use-menu.mjs
function useMenu(instance, currentIndex) {
	const indexPath = computed(() => {
		let parent = instance.parent;
		const path = [currentIndex.value];
		while (parent.type.name !== "ElMenu") {
			if (parent.props.index) path.unshift(parent.props.index);
			parent = parent.parent;
		}
		return path;
	});
	return {
		parentMenu: computed(() => {
			let parent = instance.parent;
			while (parent && !["ElMenu", "ElSubMenu"].includes(parent.type.name)) parent = parent.parent;
			return parent;
		}),
		indexPath
	};
}
//#endregion
//#region node_modules/element-plus/es/components/menu/src/use-menu-color.mjs
function useMenuColor(props) {
	return computed(() => {
		const color = props.backgroundColor;
		return color ? new TinyColor(color).shade(20).toString() : "";
	});
}
//#endregion
//#region node_modules/element-plus/es/components/menu/src/use-menu-css-var.mjs
var useMenuCssVar = (props, level) => {
	const ns = useNamespace("menu");
	return computed(() => ns.cssVarBlock({
		"text-color": props.textColor || "",
		"hover-text-color": props.textColor || "",
		"bg-color": props.backgroundColor || "",
		"hover-bg-color": useMenuColor(props).value || "",
		"active-color": props.activeTextColor || "",
		level: `${level}`
	}));
};
//#endregion
//#region node_modules/element-plus/es/components/menu/src/sub-menu.mjs
var subMenuProps = buildProps({
	/**
	* @description unique identification
	*/
	index: {
		type: String,
		required: true
	},
	/**
	* @description timeout before showing a sub-menu(inherit `show-timeout` of the menu by default.)
	*/
	showTimeout: Number,
	/**
	* @description timeout before hiding a sub-menu(inherit `hide-timeout` of the menu by default.)
	*/
	hideTimeout: Number,
	/**
	* @description custom class name for the popup menu
	*/
	popperClass: String,
	/**
	* @description custom style for the popup menu
	*/
	popperStyle: { type: definePropType([String, Object]) },
	/**
	* @description whether the sub-menu is disabled
	*/
	disabled: Boolean,
	/**
	* @description whether popup menu is teleported to the body
	*/
	teleported: {
		type: Boolean,
		default: void 0
	},
	/**
	* @description offset of the popper (overrides the `popper` of menu)
	*/
	popperOffset: Number,
	/**
	* @description Icon when menu are expanded and submenu are closed, `expand-close-icon` and `expand-open-icon` need to be passed together to take effect
	*/
	expandCloseIcon: { type: iconPropType },
	/**
	* @description Icon when menu are expanded and submenu are opened, `expand-open-icon` and `expand-close-icon` need to be passed together to take effect
	*/
	expandOpenIcon: { type: iconPropType },
	/**
	* @description Icon when menu are collapsed and submenu are closed, `collapse-close-icon` and `collapse-open-icon` need to be passed together to take effect
	*/
	collapseCloseIcon: { type: iconPropType },
	/**
	* @description Icon when menu are collapsed and submenu are opened, `collapse-open-icon` and `collapse-close-icon` need to be passed together to take effect
	*/
	collapseOpenIcon: { type: iconPropType }
});
var COMPONENT_NAME$1 = "ElSubMenu";
var sub_menu_default = defineComponent({
	name: COMPONENT_NAME$1,
	props: subMenuProps,
	setup(props, { slots, expose }) {
		const instance = getCurrentInstance();
		const { indexPath, parentMenu } = useMenu(instance, computed(() => props.index));
		const nsMenu = useNamespace("menu");
		const nsSubMenu = useNamespace("sub-menu");
		const rootMenu = inject(MENU_INJECTION_KEY);
		if (!rootMenu) throwError(COMPONENT_NAME$1, "can not inject root menu");
		const subMenu = inject(`${SUB_MENU_INJECTION_KEY}${parentMenu.value.uid}`);
		if (!subMenu) throwError(COMPONENT_NAME$1, "can not inject sub menu");
		const items = ref({});
		const subMenus = ref({});
		let timeout;
		const mouseInChild = ref(false);
		const verticalTitleRef = ref();
		const vPopper = ref();
		const isFirstLevel = computed(() => subMenu.level === 0);
		const currentPlacement = computed(() => mode.value === "horizontal" && isFirstLevel.value ? "bottom-start" : "right-start");
		const subMenuTitleIcon = computed(() => {
			if (mode.value === "horizontal" && isFirstLevel.value || mode.value === "vertical" && !rootMenu.props.collapse) {
				if (props.expandCloseIcon && props.expandOpenIcon) return opened.value ? props.expandOpenIcon : props.expandCloseIcon;
				return arrow_down_default;
			} else {
				if (props.collapseCloseIcon && props.collapseOpenIcon) return opened.value ? props.collapseOpenIcon : props.collapseCloseIcon;
				return arrow_right_default;
			}
		});
		const appendToBody = computed(() => {
			const value = props.teleported;
			return isUndefined$1(value) ? isFirstLevel.value : value;
		});
		const menuTransitionName = computed(() => rootMenu.props.collapse ? `${nsMenu.namespace.value}-zoom-in-left` : `${nsMenu.namespace.value}-zoom-in-top`);
		const fallbackPlacements = computed(() => mode.value === "horizontal" && isFirstLevel.value ? [
			"bottom-start",
			"bottom-end",
			"top-start",
			"top-end",
			"right-start",
			"left-start"
		] : [
			"right-start",
			"right",
			"right-end",
			"left-start",
			"bottom-start",
			"bottom-end",
			"top-start",
			"top-end"
		]);
		const opened = computed(() => rootMenu.openedMenus.includes(props.index));
		const active = computed(() => [...Object.values(items.value), ...Object.values(subMenus.value)].some(({ active }) => active));
		const mode = computed(() => rootMenu.props.mode);
		const persistent = computed(() => rootMenu.props.persistent);
		reactive({
			index: props.index,
			indexPath,
			active
		});
		const ulStyle = useMenuCssVar(rootMenu.props, subMenu.level + 1);
		const subMenuPopperOffset = computed(() => props.popperOffset ?? rootMenu.props.popperOffset);
		const subMenuPopperClass = computed(() => props.popperClass ?? rootMenu.props.popperClass);
		const subMenuPopperStyle = computed(() => props.popperStyle ?? rootMenu.props.popperStyle);
		const subMenuShowTimeout = computed(() => props.showTimeout ?? rootMenu.props.showTimeout);
		const subMenuHideTimeout = computed(() => props.hideTimeout ?? rootMenu.props.hideTimeout);
		const doDestroy = () => vPopper.value?.popperRef?.popperInstanceRef?.destroy();
		const handleCollapseToggle = (value) => {
			if (!value) doDestroy();
		};
		const handleClick = () => {
			if (rootMenu.props.menuTrigger === "hover" && rootMenu.props.mode === "horizontal" || rootMenu.props.collapse && rootMenu.props.mode === "vertical" || props.disabled) return;
			rootMenu.handleSubMenuClick({
				index: props.index,
				indexPath: indexPath.value,
				active: active.value
			});
		};
		const handleMouseenter = (event, showTimeout = subMenuShowTimeout.value) => {
			if (event.type === "focus") return;
			if (rootMenu.props.menuTrigger === "click" && rootMenu.props.mode === "horizontal" || !rootMenu.props.collapse && rootMenu.props.mode === "vertical" || props.disabled) {
				subMenu.mouseInChild.value = true;
				return;
			}
			subMenu.mouseInChild.value = true;
			timeout?.();
			({stop: timeout} = useTimeoutFn(() => {
				rootMenu.openMenu(props.index, indexPath.value);
			}, showTimeout));
			if (appendToBody.value) parentMenu.value.vnode.el?.dispatchEvent(new MouseEvent("mouseenter"));
			if (event.type === "mouseenter" && event.target) nextTick(() => {
				focusElement(event.target, { preventScroll: true });
			});
		};
		const handleMouseleave = (deepDispatch = false) => {
			if (rootMenu.props.menuTrigger === "click" && rootMenu.props.mode === "horizontal" || !rootMenu.props.collapse && rootMenu.props.mode === "vertical") {
				subMenu.mouseInChild.value = false;
				return;
			}
			timeout?.();
			subMenu.mouseInChild.value = false;
			({stop: timeout} = useTimeoutFn(() => !mouseInChild.value && rootMenu.closeMenu(props.index, indexPath.value), subMenuHideTimeout.value));
			if (appendToBody.value && deepDispatch) subMenu.handleMouseleave?.(true);
		};
		watch(() => rootMenu.props.collapse, (value) => handleCollapseToggle(Boolean(value)));
		{
			const addSubMenu = (item) => {
				subMenus.value[item.index] = item;
			};
			const removeSubMenu = (item) => {
				delete subMenus.value[item.index];
			};
			provide(`${SUB_MENU_INJECTION_KEY}${instance.uid}`, {
				addSubMenu,
				removeSubMenu,
				handleMouseleave,
				mouseInChild,
				level: subMenu.level + 1
			});
		}
		expose({ opened });
		return () => {
			const titleTag = [slots.title?.(), h(ElIcon, {
				class: nsSubMenu.e("icon-arrow"),
				style: { transform: opened.value ? props.expandCloseIcon && props.expandOpenIcon || props.collapseCloseIcon && props.collapseOpenIcon && rootMenu.props.collapse ? "none" : "rotateZ(180deg)" : "none" }
			}, { default: () => isString(subMenuTitleIcon.value) ? h(instance.appContext.components[subMenuTitleIcon.value]) : h(subMenuTitleIcon.value) })];
			const child = rootMenu.isMenuPopup ? h(ElTooltip, {
				ref: vPopper,
				visible: opened.value,
				effect: "light",
				pure: true,
				offset: subMenuPopperOffset.value,
				showArrow: false,
				persistent: persistent.value,
				popperClass: subMenuPopperClass.value,
				popperStyle: subMenuPopperStyle.value,
				placement: currentPlacement.value,
				teleported: appendToBody.value,
				fallbackPlacements: fallbackPlacements.value,
				transition: menuTransitionName.value,
				gpuAcceleration: false
			}, {
				content: () => h("div", {
					class: [
						nsMenu.m(mode.value),
						nsMenu.m("popup-container"),
						subMenuPopperClass.value
					],
					onMouseenter: (evt) => handleMouseenter(evt, 100),
					onMouseleave: () => handleMouseleave(true),
					onFocus: (evt) => handleMouseenter(evt, 100)
				}, [h("ul", {
					class: [
						nsMenu.b(),
						nsMenu.m("popup"),
						nsMenu.m(`popup-${currentPlacement.value}`)
					],
					style: ulStyle.value
				}, [slots.default?.()])]),
				default: () => h("div", {
					class: nsSubMenu.e("title"),
					onClick: handleClick
				}, titleTag)
			}) : h(Fragment, {}, [h("div", {
				class: nsSubMenu.e("title"),
				ref: verticalTitleRef,
				onClick: handleClick
			}, titleTag), h(ElCollapseTransition, {}, { default: () => withDirectives(h("ul", {
				role: "menu",
				class: [nsMenu.b(), nsMenu.m("inline")],
				style: ulStyle.value
			}, [slots.default?.()]), [[vShow, opened.value]]) })]);
			return h("li", {
				class: [
					nsSubMenu.b(),
					nsSubMenu.is("active", active.value),
					nsSubMenu.is("opened", opened.value),
					nsSubMenu.is("disabled", props.disabled)
				],
				role: "menuitem",
				ariaHaspopup: true,
				ariaExpanded: opened.value,
				onMouseenter: handleMouseenter,
				onMouseleave: () => handleMouseleave(),
				onFocus: handleMouseenter
			}, [child]);
		};
	}
});
//#endregion
//#region node_modules/element-plus/es/components/menu/src/menu-collapse-transition.mjs
var menu_collapse_transition_default = /* @__PURE__ */ defineComponent({
	name: "ElMenuCollapseTransition",
	__name: "menu-collapse-transition",
	setup(__props) {
		const ns = useNamespace("menu");
		const listeners = {
			onBeforeEnter: (el) => el.style.opacity = "0.2",
			onEnter(el, done) {
				addClass(el, `${ns.namespace.value}-opacity-transition`);
				el.style.opacity = "1";
				done();
			},
			onAfterEnter(el) {
				removeClass(el, `${ns.namespace.value}-opacity-transition`);
				el.style.opacity = "";
			},
			onBeforeLeave(el) {
				if (!el.dataset) el.dataset = {};
				if (hasClass(el, ns.m("collapse"))) {
					removeClass(el, ns.m("collapse"));
					el.dataset.oldOverflow = el.style.overflow;
					el.dataset.scrollWidth = el.clientWidth.toString();
					addClass(el, ns.m("collapse"));
				} else {
					addClass(el, ns.m("collapse"));
					el.dataset.oldOverflow = el.style.overflow;
					el.dataset.scrollWidth = el.clientWidth.toString();
					removeClass(el, ns.m("collapse"));
				}
				el.style.width = `${el.scrollWidth}px`;
				el.style.overflow = "hidden";
			},
			onLeave(el) {
				addClass(el, "horizontal-collapse-transition");
				el.style.width = `${el.dataset.scrollWidth}px`;
			}
		};
		return (_ctx, _cache) => {
			return openBlock(), createBlock(Transition, mergeProps({ mode: "out-in" }, listeners), {
				default: withCtx(() => [renderSlot(_ctx.$slots, "default")]),
				_: 3
			}, 16);
		};
	}
});
//#endregion
//#region node_modules/element-plus/es/components/menu/src/menu.mjs
var menuProps = buildProps({
	/**
	* @description menu display mode
	*/
	mode: {
		type: String,
		values: ["horizontal", "vertical"],
		default: "vertical"
	},
	/**
	* @description index of active menu on page load
	*/
	defaultActive: {
		type: String,
		default: ""
	},
	/**
	* @description array that contains indexes of currently active sub-menus
	*/
	defaultOpeneds: {
		type: definePropType(Array),
		default: () => mutable([])
	},
	/**
	* @description whether only one sub-menu can be active
	*/
	uniqueOpened: Boolean,
	/**
	* @description whether `vue-router` mode is activated. If true, index will be used as 'path' to activate the route action. Use with `default-active` to set the active item on load.
	*/
	router: Boolean,
	/**
	* @description how sub-menus are triggered, only works when `mode` is 'horizontal'
	*/
	menuTrigger: {
		type: String,
		values: ["hover", "click"],
		default: "hover"
	},
	/**
	* @description whether the menu is collapsed (available only in vertical mode)
	*/
	collapse: Boolean,
	/**
	* @description background color of Menu (hex format) (deprecated, use `--bg-color` instead)
	* @deprecated use `--bg-color` instead
	*/
	backgroundColor: String,
	/**
	* @description text color of Menu (hex format) (deprecated, use `--text-color` instead)
	* @deprecated use `--text-color` instead
	*/
	textColor: String,
	/**
	* @description text color of currently active menu item (hex format) (deprecated, use `--active-color` instead)
	* @deprecated use `--active-color` instead
	*/
	activeTextColor: String,
	/**
	* @description optional, whether menu is collapsed when clicking outside
	*/
	closeOnClickOutside: Boolean,
	/**
	* @description whether to enable the collapse transition
	*/
	collapseTransition: {
		type: Boolean,
		default: true
	},
	/**
	* @description whether the menu is ellipsis (available only in horizontal mode)
	*/
	ellipsis: {
		type: Boolean,
		default: true
	},
	/**
	* @description offset of the popper (effective for all submenus)
	*/
	popperOffset: {
		type: Number,
		default: 6
	},
	/**
	* @description custom ellipsis icon (available only in horizontal mode and ellipsis is true)
	*/
	ellipsisIcon: {
		type: iconPropType,
		default: () => more_default
	},
	/**
	* @description Tooltip theme, built-in theme: `dark` / `light` when menu is collapsed
	*/
	popperEffect: {
		type: definePropType(String),
		default: "dark"
	},
	/**
	* @description custom class name for all popup menus
	*/
	popperClass: String,
	/**
	* @description custom style for all popup menus
	*/
	popperStyle: { type: definePropType([String, Object]) },
	/**
	* @description control timeout for all menus before showing
	*/
	showTimeout: {
		type: Number,
		default: 300
	},
	/**
	* @description control timeout for all menus before hiding
	*/
	hideTimeout: {
		type: Number,
		default: 300
	},
	/**
	* @description when menu inactive and `persistent` is `false` , dropdown menu will be destroyed
	*/
	persistent: {
		type: Boolean,
		default: true
	}
});
var checkIndexPath = (indexPath) => isArray(indexPath) && indexPath.every((path) => isString(path));
var menuEmits = {
	close: (index, indexPath) => isString(index) && checkIndexPath(indexPath),
	open: (index, indexPath) => isString(index) && checkIndexPath(indexPath),
	select: (index, indexPath, item, routerResult) => isString(index) && checkIndexPath(indexPath) && isObject(item) && (isUndefined$1(routerResult) || routerResult instanceof Promise)
};
var DEFAULT_MORE_ITEM_WIDTH = 64;
var menu_default = defineComponent({
	name: "ElMenu",
	props: menuProps,
	emits: menuEmits,
	setup(props, { emit, slots, expose }) {
		const instance = getCurrentInstance();
		const router = instance.appContext.config.globalProperties.$router;
		const menu = ref();
		const subMenu = ref();
		const nsMenu = useNamespace("menu");
		const nsSubMenu = useNamespace("sub-menu");
		let moreItemWidth = DEFAULT_MORE_ITEM_WIDTH;
		const sliceIndex = ref(-1);
		const openedMenus = ref(props.defaultOpeneds && !props.collapse ? props.defaultOpeneds.slice(0) : []);
		const activeIndex = ref(props.defaultActive);
		const items = ref({});
		const subMenus = ref({});
		const isMenuPopup = computed(() => props.mode === "horizontal" || props.mode === "vertical" && props.collapse);
		const initMenu = () => {
			const activeItem = activeIndex.value && items.value[activeIndex.value];
			if (!activeItem || props.mode === "horizontal" || props.collapse) return;
			activeItem.indexPath.forEach((index) => {
				const subMenu = subMenus.value[index];
				subMenu && openMenu(index, subMenu.indexPath);
			});
		};
		const openMenu = (index, indexPath) => {
			if (openedMenus.value.includes(index)) return;
			if (props.uniqueOpened) openedMenus.value = openedMenus.value.filter((index) => indexPath.includes(index));
			openedMenus.value.push(index);
			emit("open", index, indexPath);
		};
		const close = (index) => {
			const i = openedMenus.value.indexOf(index);
			if (i !== -1) openedMenus.value.splice(i, 1);
		};
		const closeMenu = (index, indexPath) => {
			close(index);
			emit("close", index, indexPath);
		};
		const handleSubMenuClick = ({ index, indexPath }) => {
			openedMenus.value.includes(index) ? closeMenu(index, indexPath) : openMenu(index, indexPath);
		};
		const handleMenuItemClick = (menuItem) => {
			if (props.mode === "horizontal" || props.collapse) openedMenus.value = [];
			const { index, indexPath } = menuItem;
			if (isNil(index) || isNil(indexPath)) return;
			if (props.router && router) {
				const route = menuItem.route || index;
				const routerResult = router.push(route).then((res) => {
					if (!res) activeIndex.value = index;
					return res;
				});
				emit("select", index, indexPath, {
					index,
					indexPath,
					route
				}, routerResult);
			} else {
				activeIndex.value = index;
				emit("select", index, indexPath, {
					index,
					indexPath
				});
			}
		};
		const updateActiveIndex = (val) => {
			const itemsInData = items.value;
			activeIndex.value = (itemsInData[val] || activeIndex.value && itemsInData[activeIndex.value] || itemsInData[props.defaultActive])?.index ?? val;
		};
		const calcMenuItemWidth = (menuItem) => {
			const computedStyle = getComputedStyle(menuItem);
			const marginLeft = Number.parseInt(computedStyle.marginLeft, 10);
			const marginRight = Number.parseInt(computedStyle.marginRight, 10);
			return menuItem.offsetWidth + marginLeft + marginRight || 0;
		};
		const calcSliceIndex = () => {
			if (!menu.value) return -1;
			const items = Array.from(menu.value.childNodes).filter((item) => item.nodeName !== "#comment" && (item.nodeName !== "#text" || item.nodeValue));
			const computedMenuStyle = getComputedStyle(menu.value);
			const paddingLeft = Number.parseInt(computedMenuStyle.paddingLeft, 10);
			const paddingRight = Number.parseInt(computedMenuStyle.paddingRight, 10);
			const menuWidth = menu.value.clientWidth - paddingLeft - paddingRight;
			let calcWidth = 0;
			let sliceIndex = 0;
			items.forEach((item, index) => {
				calcWidth += calcMenuItemWidth(item);
				if (calcWidth <= menuWidth - moreItemWidth) sliceIndex = index + 1;
			});
			return sliceIndex === items.length ? -1 : sliceIndex;
		};
		const getIndexPath = (index) => subMenus.value[index].indexPath;
		const debounce = (fn, wait = 33.34) => {
			let timer;
			return () => {
				timer && clearTimeout(timer);
				timer = setTimeout(() => {
					fn();
				}, wait);
			};
		};
		let isFirstTimeRender = true;
		const handleResize = () => {
			const el = unrefElement(subMenu);
			if (el) moreItemWidth = calcMenuItemWidth(el) || DEFAULT_MORE_ITEM_WIDTH;
			if (sliceIndex.value === calcSliceIndex()) return;
			const callback = () => {
				sliceIndex.value = -1;
				nextTick(() => {
					sliceIndex.value = calcSliceIndex();
				});
			};
			isFirstTimeRender ? callback() : debounce(callback)();
			isFirstTimeRender = false;
		};
		watch(() => props.defaultActive, (currentActive) => {
			if (!items.value[currentActive]) activeIndex.value = "";
			updateActiveIndex(currentActive);
		});
		watch(() => props.collapse, (value) => {
			if (value) openedMenus.value = [];
		});
		watch(items.value, initMenu);
		let resizeStopper;
		watchEffect(() => {
			if (props.mode === "horizontal" && props.ellipsis) resizeStopper = useResizeObserver(menu, handleResize).stop;
			else resizeStopper?.();
		});
		const mouseInChild = ref(false);
		{
			const addSubMenu = (item) => {
				subMenus.value[item.index] = item;
			};
			const removeSubMenu = (item) => {
				delete subMenus.value[item.index];
			};
			const addMenuItem = (item) => {
				items.value[item.index] = item;
			};
			const removeMenuItem = (item) => {
				delete items.value[item.index];
			};
			provide(MENU_INJECTION_KEY, reactive({
				props,
				openedMenus,
				items,
				subMenus,
				activeIndex,
				isMenuPopup,
				addMenuItem,
				removeMenuItem,
				addSubMenu,
				removeSubMenu,
				openMenu,
				closeMenu,
				handleMenuItemClick,
				handleSubMenuClick
			}));
			provide(`${SUB_MENU_INJECTION_KEY}${instance.uid}`, {
				addSubMenu,
				removeSubMenu,
				mouseInChild,
				level: 0
			});
		}
		{
			const open = (index) => {
				const { indexPath } = subMenus.value[index];
				indexPath.forEach((i) => openMenu(i, indexPath));
			};
			expose({
				open,
				close,
				updateActiveIndex,
				handleResize
			});
		}
		const ulStyle = useMenuCssVar(props, 0);
		return () => {
			let slot = slots.default?.() ?? [];
			const vShowMore = [];
			if (props.mode === "horizontal" && menu.value) {
				const originalSlot = flattedChildren(slot).filter((vnode) => {
					return vnode?.shapeFlag !== 8;
				});
				const slotDefault = sliceIndex.value === -1 ? originalSlot : originalSlot.slice(0, sliceIndex.value);
				const slotMore = sliceIndex.value === -1 ? [] : originalSlot.slice(sliceIndex.value);
				if (slotMore?.length && props.ellipsis) {
					slot = slotDefault;
					vShowMore.push(h(sub_menu_default, {
						ref: subMenu,
						index: "sub-menu-more",
						class: nsSubMenu.e("hide-arrow"),
						popperOffset: props.popperOffset
					}, {
						title: () => h(ElIcon, { class: nsSubMenu.e("icon-more") }, { default: () => h(props.ellipsisIcon) }),
						default: () => slotMore
					}));
				}
			}
			const directives = props.closeOnClickOutside ? [[ClickOutside, () => {
				if (!openedMenus.value.length) return;
				if (!mouseInChild.value) {
					openedMenus.value.forEach((openedMenu) => emit("close", openedMenu, getIndexPath(openedMenu)));
					openedMenus.value = [];
				}
			}]] : [];
			const vMenu = withDirectives(h("ul", {
				key: String(props.collapse),
				role: "menubar",
				ref: menu,
				style: ulStyle.value,
				class: {
					[nsMenu.b()]: true,
					[nsMenu.m(props.mode)]: true,
					[nsMenu.m("collapse")]: props.collapse
				}
			}, [...slot, ...vShowMore]), directives);
			if (props.collapseTransition && props.mode === "vertical") return h(menu_collapse_transition_default, () => vMenu);
			return vMenu;
		};
	}
});
//#endregion
//#region node_modules/element-plus/es/components/menu/src/menu-item.mjs
/**
* @deprecated Removed after 3.0.0, Use `MenuItemProps` instead.
*/
var menuItemProps = buildProps({
	/**
	* @description unique identification
	*/
	index: {
		type: String,
		required: true
	},
	/**
	* @description Vue Router object
	*/
	route: { type: definePropType([String, Object]) },
	/**
	* @description whether disabled
	*/
	disabled: Boolean
});
var menuItemEmits = { click: (item) => isString(item.index) && isArray(item.indexPath) };
//#endregion
//#region node_modules/element-plus/es/components/menu/src/menu-item.vue_vue_type_script_setup_true_lang.mjs
var COMPONENT_NAME = "ElMenuItem";
//#endregion
//#region node_modules/element-plus/es/components/menu/src/menu-item2.mjs
var menu_item_default = /* @__PURE__ */ defineComponent({
	name: COMPONENT_NAME,
	__name: "menu-item",
	props: menuItemProps,
	emits: menuItemEmits,
	setup(__props, { expose: __expose, emit: __emit }) {
		const props = __props;
		const emit = __emit;
		const instance = getCurrentInstance();
		const rootMenu = inject(MENU_INJECTION_KEY);
		const nsMenu = useNamespace("menu");
		const nsMenuItem = useNamespace("menu-item");
		if (!rootMenu) throwError(COMPONENT_NAME, "can not inject root menu");
		const { parentMenu, indexPath } = useMenu(instance, toRef(props, "index"));
		if (!inject(`subMenu:${parentMenu.value.uid}`)) throwError(COMPONENT_NAME, "can not inject sub menu");
		const active = computed(() => props.index === rootMenu.activeIndex);
		const item = reactive({
			index: props.index,
			indexPath,
			active
		});
		const handleClick = () => {
			if (!props.disabled) {
				rootMenu.handleMenuItemClick({
					index: props.index,
					indexPath: indexPath.value,
					route: props.route
				});
				emit("click", item);
			}
		};
		__expose({
			parentMenu,
			rootMenu,
			active,
			nsMenu,
			nsMenuItem,
			handleClick
		});
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("li", {
				class: normalizeClass([
					unref(nsMenuItem).b(),
					unref(nsMenuItem).is("active", active.value),
					unref(nsMenuItem).is("disabled", __props.disabled)
				]),
				role: "menuitem",
				tabindex: "-1",
				onClick: handleClick
			}, [unref(parentMenu).type.name === "ElMenu" && unref(rootMenu).props.collapse && _ctx.$slots.title ? (openBlock(), createBlock(unref(ElTooltip), {
				key: 0,
				effect: unref(rootMenu).props.popperEffect,
				placement: "right",
				"fallback-placements": ["left"],
				"popper-class": unref(rootMenu).props.popperClass,
				"popper-style": unref(rootMenu).props.popperStyle,
				persistent: unref(rootMenu).props.persistent,
				"focus-on-target": ""
			}, {
				content: withCtx(() => [renderSlot(_ctx.$slots, "title")]),
				default: withCtx(() => [createElementVNode("div", { class: normalizeClass(unref(nsMenu).be("tooltip", "trigger")) }, [renderSlot(_ctx.$slots, "default")], 2)]),
				_: 3
			}, 8, [
				"effect",
				"popper-class",
				"popper-style",
				"persistent"
			])) : (openBlock(), createElementBlock(Fragment, { key: 1 }, [renderSlot(_ctx.$slots, "default"), renderSlot(_ctx.$slots, "title")], 64))], 2);
		};
	}
});
//#endregion
//#region node_modules/element-plus/es/components/menu/src/menu-item-group2.mjs
var menu_item_group_default = /* @__PURE__ */ defineComponent({
	name: "ElMenuItemGroup",
	__name: "menu-item-group",
	props: { 
	/**
	* @description group title
	*/
title: String },
	setup(__props) {
		const ns = useNamespace("menu-item-group");
		return (_ctx, _cache) => {
			return openBlock(), createElementBlock("li", { class: normalizeClass(unref(ns).b()) }, [createElementVNode("div", { class: normalizeClass(unref(ns).e("title")) }, [!_ctx.$slots.title ? (openBlock(), createElementBlock(Fragment, { key: 0 }, [createTextVNode(toDisplayString(__props.title), 1)], 64)) : renderSlot(_ctx.$slots, "title", { key: 1 })], 2), createElementVNode("ul", null, [renderSlot(_ctx.$slots, "default")])], 2);
		};
	}
});
//#endregion
//#region node_modules/element-plus/es/components/menu/index.mjs
var ElMenu = withInstall(menu_default, {
	MenuItem: menu_item_default,
	MenuItemGroup: menu_item_group_default,
	SubMenu: sub_menu_default
});
var ElMenuItem = withNoopInstall(menu_item_default);
withNoopInstall(menu_item_group_default);
withNoopInstall(sub_menu_default);
//#endregion
//#region layouts/default.vue
var _sfc_main = {
	__name: "default",
	__ssrInlineRender: true,
	setup(__props) {
		const route = useRoute();
		const activeMenu = computed(() => {
			if (route.path.startsWith("/jobs")) return "/jobs";
			if (route.path.startsWith("/companies")) return "/companies";
			if (route.path.startsWith("/dashboard")) return "/dashboard";
			if (route.path.startsWith("/kanban")) return "/kanban";
			if (route.path.startsWith("/ai")) return "/ai";
			if (route.path.startsWith("/questions")) return "/questions";
			return "/jobs";
		});
		return (_ctx, _push, _parent, _attrs) => {
			const _component_el_container = ElContainer;
			const _component_el_aside = ElAside;
			const _component_el_menu = ElMenu;
			const _component_el_menu_item = ElMenuItem;
			const _component_el_icon = ElIcon;
			const _component_el_header = ElHeader;
			const _component_el_main = ElMain;
			_push(ssrRenderComponent(_component_el_container, mergeProps({ class: "layout-container" }, _attrs), {
				default: withCtx((_, _push, _parent, _scopeId) => {
					if (_push) {
						_push(ssrRenderComponent(_component_el_aside, {
							width: "240px",
							class: "sidebar"
						}, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) {
									_push(`<div class="logo" data-v-ee89eea7${_scopeId}>`);
									_push(ssrRenderComponent(unref(Briefcase), {
										class: "logo-icon",
										size: 24
									}, null, _parent, _scopeId));
									_push(`<h2 data-v-ee89eea7${_scopeId}>Job Dashboard</h2></div>`);
									_push(ssrRenderComponent(_component_el_menu, {
										"default-active": activeMenu.value,
										class: "el-menu-vertical",
										router: ""
									}, {
										default: withCtx((_, _push, _parent, _scopeId) => {
											if (_push) {
												_push(ssrRenderComponent(_component_el_menu_item, { index: "/jobs" }, {
													default: withCtx((_, _push, _parent, _scopeId) => {
														if (_push) {
															_push(ssrRenderComponent(_component_el_icon, null, {
																default: withCtx((_, _push, _parent, _scopeId) => {
																	if (_push) _push(ssrRenderComponent(unref(ClipboardList), null, null, _parent, _scopeId));
																	else return [createVNode(unref(ClipboardList))];
																}),
																_: 1
															}, _parent, _scopeId));
															_push(`<span data-v-ee89eea7${_scopeId}>职位大厅</span>`);
														} else return [createVNode(_component_el_icon, null, {
															default: withCtx(() => [createVNode(unref(ClipboardList))]),
															_: 1
														}), createVNode("span", null, "职位大厅")];
													}),
													_: 1
												}, _parent, _scopeId));
												_push(ssrRenderComponent(_component_el_menu_item, { index: "/companies" }, {
													default: withCtx((_, _push, _parent, _scopeId) => {
														if (_push) {
															_push(ssrRenderComponent(_component_el_icon, null, {
																default: withCtx((_, _push, _parent, _scopeId) => {
																	if (_push) _push(ssrRenderComponent(unref(Building2), null, null, _parent, _scopeId));
																	else return [createVNode(unref(Building2))];
																}),
																_: 1
															}, _parent, _scopeId));
															_push(`<span data-v-ee89eea7${_scopeId}>企业全景</span>`);
														} else return [createVNode(_component_el_icon, null, {
															default: withCtx(() => [createVNode(unref(Building2))]),
															_: 1
														}), createVNode("span", null, "企业全景")];
													}),
													_: 1
												}, _parent, _scopeId));
												_push(ssrRenderComponent(_component_el_menu_item, { index: "/dashboard" }, {
													default: withCtx((_, _push, _parent, _scopeId) => {
														if (_push) {
															_push(ssrRenderComponent(_component_el_icon, null, {
																default: withCtx((_, _push, _parent, _scopeId) => {
																	if (_push) _push(ssrRenderComponent(unref(LayoutDashboard), null, null, _parent, _scopeId));
																	else return [createVNode(unref(LayoutDashboard))];
																}),
																_: 1
															}, _parent, _scopeId));
															_push(`<span data-v-ee89eea7${_scopeId}>数据看板</span>`);
														} else return [createVNode(_component_el_icon, null, {
															default: withCtx(() => [createVNode(unref(LayoutDashboard))]),
															_: 1
														}), createVNode("span", null, "数据看板")];
													}),
													_: 1
												}, _parent, _scopeId));
												_push(ssrRenderComponent(_component_el_menu_item, { index: "/kanban" }, {
													default: withCtx((_, _push, _parent, _scopeId) => {
														if (_push) {
															_push(ssrRenderComponent(_component_el_icon, null, {
																default: withCtx((_, _push, _parent, _scopeId) => {
																	if (_push) _push(ssrRenderComponent(unref(MapPin), null, null, _parent, _scopeId));
																	else return [createVNode(unref(MapPin))];
																}),
																_: 1
															}, _parent, _scopeId));
															_push(`<span data-v-ee89eea7${_scopeId}>投递追踪</span>`);
														} else return [createVNode(_component_el_icon, null, {
															default: withCtx(() => [createVNode(unref(MapPin))]),
															_: 1
														}), createVNode("span", null, "投递追踪")];
													}),
													_: 1
												}, _parent, _scopeId));
												_push(ssrRenderComponent(_component_el_menu_item, { index: "/ai/model" }, {
													default: withCtx((_, _push, _parent, _scopeId) => {
														if (_push) {
															_push(ssrRenderComponent(_component_el_icon, null, {
																default: withCtx((_, _push, _parent, _scopeId) => {
																	if (_push) _push(ssrRenderComponent(unref(Bot), null, null, _parent, _scopeId));
																	else return [createVNode(unref(Bot))];
																}),
																_: 1
															}, _parent, _scopeId));
															_push(`<span data-v-ee89eea7${_scopeId}>大模型管理</span>`);
														} else return [createVNode(_component_el_icon, null, {
															default: withCtx(() => [createVNode(unref(Bot))]),
															_: 1
														}), createVNode("span", null, "大模型管理")];
													}),
													_: 1
												}, _parent, _scopeId));
												_push(ssrRenderComponent(_component_el_menu_item, { index: "/ai/resume" }, {
													default: withCtx((_, _push, _parent, _scopeId) => {
														if (_push) {
															_push(ssrRenderComponent(_component_el_icon, null, {
																default: withCtx((_, _push, _parent, _scopeId) => {
																	if (_push) _push(ssrRenderComponent(unref(FileText), null, null, _parent, _scopeId));
																	else return [createVNode(unref(FileText))];
																}),
																_: 1
															}, _parent, _scopeId));
															_push(`<span data-v-ee89eea7${_scopeId}>简历管理</span>`);
														} else return [createVNode(_component_el_icon, null, {
															default: withCtx(() => [createVNode(unref(FileText))]),
															_: 1
														}), createVNode("span", null, "简历管理")];
													}),
													_: 1
												}, _parent, _scopeId));
												_push(ssrRenderComponent(_component_el_menu_item, { index: "/questions" }, {
													default: withCtx((_, _push, _parent, _scopeId) => {
														if (_push) {
															_push(ssrRenderComponent(_component_el_icon, null, {
																default: withCtx((_, _push, _parent, _scopeId) => {
																	if (_push) _push(ssrRenderComponent(unref(BookOpen), null, null, _parent, _scopeId));
																	else return [createVNode(unref(BookOpen))];
																}),
																_: 1
															}, _parent, _scopeId));
															_push(`<span data-v-ee89eea7${_scopeId}>题库管理</span>`);
														} else return [createVNode(_component_el_icon, null, {
															default: withCtx(() => [createVNode(unref(BookOpen))]),
															_: 1
														}), createVNode("span", null, "题库管理")];
													}),
													_: 1
												}, _parent, _scopeId));
											} else return [
												createVNode(_component_el_menu_item, { index: "/jobs" }, {
													default: withCtx(() => [createVNode(_component_el_icon, null, {
														default: withCtx(() => [createVNode(unref(ClipboardList))]),
														_: 1
													}), createVNode("span", null, "职位大厅")]),
													_: 1
												}),
												createVNode(_component_el_menu_item, { index: "/companies" }, {
													default: withCtx(() => [createVNode(_component_el_icon, null, {
														default: withCtx(() => [createVNode(unref(Building2))]),
														_: 1
													}), createVNode("span", null, "企业全景")]),
													_: 1
												}),
												createVNode(_component_el_menu_item, { index: "/dashboard" }, {
													default: withCtx(() => [createVNode(_component_el_icon, null, {
														default: withCtx(() => [createVNode(unref(LayoutDashboard))]),
														_: 1
													}), createVNode("span", null, "数据看板")]),
													_: 1
												}),
												createVNode(_component_el_menu_item, { index: "/kanban" }, {
													default: withCtx(() => [createVNode(_component_el_icon, null, {
														default: withCtx(() => [createVNode(unref(MapPin))]),
														_: 1
													}), createVNode("span", null, "投递追踪")]),
													_: 1
												}),
												createVNode(_component_el_menu_item, { index: "/ai/model" }, {
													default: withCtx(() => [createVNode(_component_el_icon, null, {
														default: withCtx(() => [createVNode(unref(Bot))]),
														_: 1
													}), createVNode("span", null, "大模型管理")]),
													_: 1
												}),
												createVNode(_component_el_menu_item, { index: "/ai/resume" }, {
													default: withCtx(() => [createVNode(_component_el_icon, null, {
														default: withCtx(() => [createVNode(unref(FileText))]),
														_: 1
													}), createVNode("span", null, "简历管理")]),
													_: 1
												}),
												createVNode(_component_el_menu_item, { index: "/questions" }, {
													default: withCtx(() => [createVNode(_component_el_icon, null, {
														default: withCtx(() => [createVNode(unref(BookOpen))]),
														_: 1
													}), createVNode("span", null, "题库管理")]),
													_: 1
												})
											];
										}),
										_: 1
									}, _parent, _scopeId));
								} else return [createVNode("div", { class: "logo" }, [createVNode(unref(Briefcase), {
									class: "logo-icon",
									size: 24
								}), createVNode("h2", null, "Job Dashboard")]), createVNode(_component_el_menu, {
									"default-active": activeMenu.value,
									class: "el-menu-vertical",
									router: ""
								}, {
									default: withCtx(() => [
										createVNode(_component_el_menu_item, { index: "/jobs" }, {
											default: withCtx(() => [createVNode(_component_el_icon, null, {
												default: withCtx(() => [createVNode(unref(ClipboardList))]),
												_: 1
											}), createVNode("span", null, "职位大厅")]),
											_: 1
										}),
										createVNode(_component_el_menu_item, { index: "/companies" }, {
											default: withCtx(() => [createVNode(_component_el_icon, null, {
												default: withCtx(() => [createVNode(unref(Building2))]),
												_: 1
											}), createVNode("span", null, "企业全景")]),
											_: 1
										}),
										createVNode(_component_el_menu_item, { index: "/dashboard" }, {
											default: withCtx(() => [createVNode(_component_el_icon, null, {
												default: withCtx(() => [createVNode(unref(LayoutDashboard))]),
												_: 1
											}), createVNode("span", null, "数据看板")]),
											_: 1
										}),
										createVNode(_component_el_menu_item, { index: "/kanban" }, {
											default: withCtx(() => [createVNode(_component_el_icon, null, {
												default: withCtx(() => [createVNode(unref(MapPin))]),
												_: 1
											}), createVNode("span", null, "投递追踪")]),
											_: 1
										}),
										createVNode(_component_el_menu_item, { index: "/ai/model" }, {
											default: withCtx(() => [createVNode(_component_el_icon, null, {
												default: withCtx(() => [createVNode(unref(Bot))]),
												_: 1
											}), createVNode("span", null, "大模型管理")]),
											_: 1
										}),
										createVNode(_component_el_menu_item, { index: "/ai/resume" }, {
											default: withCtx(() => [createVNode(_component_el_icon, null, {
												default: withCtx(() => [createVNode(unref(FileText))]),
												_: 1
											}), createVNode("span", null, "简历管理")]),
											_: 1
										}),
										createVNode(_component_el_menu_item, { index: "/questions" }, {
											default: withCtx(() => [createVNode(_component_el_icon, null, {
												default: withCtx(() => [createVNode(unref(BookOpen))]),
												_: 1
											}), createVNode("span", null, "题库管理")]),
											_: 1
										})
									]),
									_: 1
								}, 8, ["default-active"])];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(ssrRenderComponent(_component_el_container, null, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) {
									_push(ssrRenderComponent(_component_el_header, { class: "header" }, {
										default: withCtx((_, _push, _parent, _scopeId) => {
											if (_push) _push(`<div class="header-content" data-v-ee89eea7${_scopeId}><span data-v-ee89eea7${_scopeId}>招聘数据本地管理后台 v2.0</span></div>`);
											else return [createVNode("div", { class: "header-content" }, [createVNode("span", null, "招聘数据本地管理后台 v2.0")])];
										}),
										_: 1
									}, _parent, _scopeId));
									_push(ssrRenderComponent(_component_el_main, { class: "main-content" }, {
										default: withCtx((_, _push, _parent, _scopeId) => {
											if (_push) ssrRenderSlot(_ctx.$slots, "default", {}, null, _push, _parent, _scopeId);
											else return [renderSlot(_ctx.$slots, "default", {}, void 0, true)];
										}),
										_: 3
									}, _parent, _scopeId));
								} else return [createVNode(_component_el_header, { class: "header" }, {
									default: withCtx(() => [createVNode("div", { class: "header-content" }, [createVNode("span", null, "招聘数据本地管理后台 v2.0")])]),
									_: 1
								}), createVNode(_component_el_main, { class: "main-content" }, {
									default: withCtx(() => [renderSlot(_ctx.$slots, "default", {}, void 0, true)]),
									_: 3
								})];
							}),
							_: 3
						}, _parent, _scopeId));
					} else return [createVNode(_component_el_aside, {
						width: "240px",
						class: "sidebar"
					}, {
						default: withCtx(() => [createVNode("div", { class: "logo" }, [createVNode(unref(Briefcase), {
							class: "logo-icon",
							size: 24
						}), createVNode("h2", null, "Job Dashboard")]), createVNode(_component_el_menu, {
							"default-active": activeMenu.value,
							class: "el-menu-vertical",
							router: ""
						}, {
							default: withCtx(() => [
								createVNode(_component_el_menu_item, { index: "/jobs" }, {
									default: withCtx(() => [createVNode(_component_el_icon, null, {
										default: withCtx(() => [createVNode(unref(ClipboardList))]),
										_: 1
									}), createVNode("span", null, "职位大厅")]),
									_: 1
								}),
								createVNode(_component_el_menu_item, { index: "/companies" }, {
									default: withCtx(() => [createVNode(_component_el_icon, null, {
										default: withCtx(() => [createVNode(unref(Building2))]),
										_: 1
									}), createVNode("span", null, "企业全景")]),
									_: 1
								}),
								createVNode(_component_el_menu_item, { index: "/dashboard" }, {
									default: withCtx(() => [createVNode(_component_el_icon, null, {
										default: withCtx(() => [createVNode(unref(LayoutDashboard))]),
										_: 1
									}), createVNode("span", null, "数据看板")]),
									_: 1
								}),
								createVNode(_component_el_menu_item, { index: "/kanban" }, {
									default: withCtx(() => [createVNode(_component_el_icon, null, {
										default: withCtx(() => [createVNode(unref(MapPin))]),
										_: 1
									}), createVNode("span", null, "投递追踪")]),
									_: 1
								}),
								createVNode(_component_el_menu_item, { index: "/ai/model" }, {
									default: withCtx(() => [createVNode(_component_el_icon, null, {
										default: withCtx(() => [createVNode(unref(Bot))]),
										_: 1
									}), createVNode("span", null, "大模型管理")]),
									_: 1
								}),
								createVNode(_component_el_menu_item, { index: "/ai/resume" }, {
									default: withCtx(() => [createVNode(_component_el_icon, null, {
										default: withCtx(() => [createVNode(unref(FileText))]),
										_: 1
									}), createVNode("span", null, "简历管理")]),
									_: 1
								}),
								createVNode(_component_el_menu_item, { index: "/questions" }, {
									default: withCtx(() => [createVNode(_component_el_icon, null, {
										default: withCtx(() => [createVNode(unref(BookOpen))]),
										_: 1
									}), createVNode("span", null, "题库管理")]),
									_: 1
								})
							]),
							_: 1
						}, 8, ["default-active"])]),
						_: 1
					}), createVNode(_component_el_container, null, {
						default: withCtx(() => [createVNode(_component_el_header, { class: "header" }, {
							default: withCtx(() => [createVNode("div", { class: "header-content" }, [createVNode("span", null, "招聘数据本地管理后台 v2.0")])]),
							_: 1
						}), createVNode(_component_el_main, { class: "main-content" }, {
							default: withCtx(() => [renderSlot(_ctx.$slots, "default", {}, void 0, true)]),
							_: 3
						})]),
						_: 3
					})];
				}),
				_: 3
			}, _parent));
		};
	}
};
var _sfc_setup = _sfc_main.setup;
_sfc_main.setup = (props, ctx) => {
	const ssrContext = useSSRContext();
	(ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("layouts/default.vue");
	return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
var default_default = /*#__PURE__*/ _plugin_vue_export_helper_default(_sfc_main, [["__scopeId", "data-v-ee89eea7"]]);

export { default_default as default };
//# sourceMappingURL=default-DUxwCBIp.mjs.map
