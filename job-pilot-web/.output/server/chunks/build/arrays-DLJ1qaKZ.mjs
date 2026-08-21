import '../virtual/entry.mjs';
import { b as buildProps, M as focusElement } from './icon-BTfkZ5_-.mjs';
import { g as getEventCode, E as EVENT_CODE } from './typescript-DnijNOm4.mjs';
import { ref, defineComponent, provide, watch, unref, renderSlot } from 'vue';
import { isNil, pick } from 'lodash-unified';
import { isArray } from '@vue/shared';

//#region node_modules/element-plus/es/hooks/use-aria/index.mjs
/**
* @deprecated Removed after 3.0.0, Use `AriaProps` instead.
*/
var ariaProps = buildProps({
	/**
	* @description native `aria-label` attribute
	*/
	ariaLabel: String,
	/**
	* @description native `aria-orientation` attribute
	*/
	ariaOrientation: {
		type: String,
		values: [
			"horizontal",
			"vertical",
			"undefined"
		]
	},
	/**
	* @description native `aria-controls` attribute
	*/
	ariaControls: String
});
var useAriaProps = (arias) => {
	return pick(ariaProps, arias);
};
//#endregion
//#region node_modules/element-plus/es/components/focus-trap/src/tokens.mjs
var FOCUSOUT_PREVENTED = "focus-trap.focusout-prevented";
var FOCUSOUT_PREVENTED_OPTS = {
	cancelable: true,
	bubbles: false
};
var ON_TRAP_FOCUS_EVT = "focusAfterTrapped";
var ON_RELEASE_FOCUS_EVT = "focusAfterReleased";
var FOCUS_TRAP_INJECTION_KEY = Symbol("elFocusTrap");
//#endregion
//#region node_modules/element-plus/es/components/focus-trap/src/utils.mjs
var focusReason = ref();
var lastUserFocusTimestamp = ref(0);
var lastAutomatedFocusTimestamp = ref(0);
var obtainAllFocusableElements = (element) => {
	const nodes = [];
	const walker = (void 0).createTreeWalker(element, NodeFilter.SHOW_ELEMENT, { acceptNode: (node) => {
		const isHiddenInput = node.tagName === "INPUT" && node.type === "hidden";
		if (node.disabled || node.hidden || isHiddenInput) return NodeFilter.FILTER_SKIP;
		return node.tabIndex >= 0 || node === (void 0).activeElement ? NodeFilter.FILTER_ACCEPT : NodeFilter.FILTER_SKIP;
	} });
	while (walker.nextNode()) nodes.push(walker.currentNode);
	return nodes;
};
var getVisibleElement = (elements, container) => {
	for (const element of elements) if (!isHidden(element, container)) return element;
};
var isHidden = (element, container) => {
	if (getComputedStyle(element).visibility === "hidden") return true;
	while (element) {
		if (container && element === container) return false;
		if (getComputedStyle(element).display === "none") return true;
		element = element.parentElement;
	}
	return false;
};
var getEdges = (container) => {
	const focusable = obtainAllFocusableElements(container);
	return [getVisibleElement(focusable, container), getVisibleElement(focusable.reverse(), container)];
};
var isSelectable = (element) => {
	return element instanceof HTMLInputElement && "select" in element;
};
var tryFocus = (element, shouldSelect) => {
	if (element) {
		const prevFocusedElement = (void 0).activeElement;
		focusElement(element, { preventScroll: true });
		lastAutomatedFocusTimestamp.value = (void 0).performance.now();
		if (element !== prevFocusedElement && isSelectable(element) && shouldSelect) element.select();
	}
};
var useFocusReason = () => {
	return {
		focusReason,
		lastUserFocusTimestamp,
		lastAutomatedFocusTimestamp
	};
};
var createFocusOutPreventedEvent = (detail) => {
	return new CustomEvent(FOCUSOUT_PREVENTED, {
		...FOCUSOUT_PREVENTED_OPTS,
		detail
	});
};
//#endregion
//#region node_modules/element-plus/es/components/focus-trap/src/focus-trap.vue_vue_type_script_lang.mjs
var focus_trap_vue_vue_type_script_lang_default = defineComponent({
	name: "ElFocusTrap",
	inheritAttrs: false,
	props: {
		loop: Boolean,
		trapped: Boolean,
		focusTrapEl: Object,
		focusStartEl: {
			type: [Object, String],
			default: "first"
		}
	},
	emits: [
		ON_TRAP_FOCUS_EVT,
		ON_RELEASE_FOCUS_EVT,
		"focusin",
		"focusout",
		"focusout-prevented",
		"release-requested"
	],
	setup(props, { emit }) {
		const forwardRef = ref();
		let lastFocusAfterTrapped;
		const { focusReason } = useFocusReason();
		const onKeydown = (e) => {
			if (!props.loop && !props.trapped) return;
			const { altKey, ctrlKey, metaKey, currentTarget, shiftKey } = e;
			const { loop } = props;
			const isTabbing = getEventCode(e) === EVENT_CODE.tab && !altKey && !ctrlKey && !metaKey;
			const currentFocusingEl = (void 0).activeElement;
			if (isTabbing && currentFocusingEl) {
				const container = currentTarget;
				const [first, last] = getEdges(container);
				if (!(first && last)) {
					if (currentFocusingEl === container) {
						const focusoutPreventedEvent = createFocusOutPreventedEvent({ focusReason: focusReason.value });
						emit("focusout-prevented", focusoutPreventedEvent);
						if (!focusoutPreventedEvent.defaultPrevented) e.preventDefault();
					}
				} else if (!shiftKey && currentFocusingEl === last) {
					const focusoutPreventedEvent = createFocusOutPreventedEvent({ focusReason: focusReason.value });
					emit("focusout-prevented", focusoutPreventedEvent);
					if (!focusoutPreventedEvent.defaultPrevented) {
						e.preventDefault();
						if (loop) tryFocus(first, true);
					}
				} else if (shiftKey && [first, container].includes(currentFocusingEl)) {
					const focusoutPreventedEvent = createFocusOutPreventedEvent({ focusReason: focusReason.value });
					emit("focusout-prevented", focusoutPreventedEvent);
					if (!focusoutPreventedEvent.defaultPrevented) {
						e.preventDefault();
						if (loop) tryFocus(last, true);
					}
				}
			}
		};
		provide(FOCUS_TRAP_INJECTION_KEY, {
			focusTrapRef: forwardRef,
			onKeydown
		});
		watch(() => props.focusTrapEl, (focusTrapEl) => {
			if (focusTrapEl) forwardRef.value = focusTrapEl;
		}, { immediate: true });
		watch([forwardRef], ([forwardRef], [oldForwardRef]) => {
			if (forwardRef) {
				forwardRef.addEventListener("keydown", onKeydown);
				forwardRef.addEventListener("focusin", onFocusIn);
				forwardRef.addEventListener("focusout", onFocusOut);
			}
			if (oldForwardRef) {
				oldForwardRef.removeEventListener("keydown", onKeydown);
				oldForwardRef.removeEventListener("focusin", onFocusIn);
				oldForwardRef.removeEventListener("focusout", onFocusOut);
			}
		});
		const onFocusIn = (e) => {
			const trapContainer = unref(forwardRef);
			if (!trapContainer) return;
			const target = e.target;
			const relatedTarget = e.relatedTarget;
			const isFocusedInTrap = target && trapContainer.contains(target);
			if (!props.trapped) {
				if (!(relatedTarget && trapContainer.contains(relatedTarget)));
			}
			if (isFocusedInTrap) emit("focusin", e);
			if (props.trapped) if (isFocusedInTrap) lastFocusAfterTrapped = target;
			else tryFocus(lastFocusAfterTrapped, true);
		};
		const onFocusOut = (e) => {
			const trapContainer = unref(forwardRef);
			if (!trapContainer) return;
			if (props.trapped) {
				const relatedTarget = e.relatedTarget;
				if (!isNil(relatedTarget) && !trapContainer.contains(relatedTarget)) setTimeout(() => {
					if (props.trapped) {
						const focusoutPreventedEvent = createFocusOutPreventedEvent({ focusReason: focusReason.value });
						emit("focusout-prevented", focusoutPreventedEvent);
						if (!focusoutPreventedEvent.defaultPrevented) tryFocus(lastFocusAfterTrapped, true);
					}
				}, 0);
			} else {
				const target = e.target;
				if (!(target && trapContainer.contains(target))) emit("focusout", e);
			}
		};
		return { onKeydown };
	}
});
//#endregion
//#region node_modules/element-plus/es/_virtual/_plugin-vue_export-helper.mjs
var _plugin_vue_export_helper_default = (sfc, props) => {
	const target = sfc.__vccOpts || sfc;
	for (const [key, val] of props) target[key] = val;
	return target;
};
//#endregion
//#region node_modules/element-plus/es/components/focus-trap/src/focus-trap.mjs
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
	return renderSlot(_ctx.$slots, "default", { handleKeydown: _ctx.onKeydown });
}
//#endregion
//#region node_modules/element-plus/es/components/focus-trap/index.mjs
var focus_trap_default$1 = /* @__PURE__ */ _plugin_vue_export_helper_default(focus_trap_vue_vue_type_script_lang_default, [["render", _sfc_render]]);
//#endregion
//#region node_modules/element-plus/es/utils/arrays.mjs
/** like `_.castArray`, except falsy value returns empty array. */
var castArray = (arr) => {
	if (!arr && arr !== 0) return [];
	return isArray(arr) ? arr : [arr];
};

export { FOCUS_TRAP_INJECTION_KEY as F, _plugin_vue_export_helper_default as _, castArray as c, focus_trap_default$1 as f, useAriaProps as u };
//# sourceMappingURL=arrays-DLJ1qaKZ.mjs.map
