import { p as isAndroid } from '../virtual/entry.mjs';

//#region node_modules/element-plus/es/constants/aria.mjs
var EVENT_CODE = {
	tab: "Tab",
	enter: "Enter",
	space: "Space",
	left: "ArrowLeft",
	up: "ArrowUp",
	right: "ArrowRight",
	down: "ArrowDown",
	esc: "Escape",
	delete: "Delete",
	backspace: "Backspace",
	numpadEnter: "NumpadEnter",
	pageUp: "PageUp",
	pageDown: "PageDown",
	home: "Home",
	end: "End"
};
//#endregion
//#region node_modules/element-plus/es/utils/dom/event.mjs
var composeEventHandlers = (theirsHandler, oursHandler, { checkForDefaultPrevented = true } = {}) => {
	const handleEvent = (event) => {
		const shouldPrevent = theirsHandler?.(event);
		if (checkForDefaultPrevented === false || !shouldPrevent) return oursHandler?.(event);
	};
	return handleEvent;
};
var getEventCode = (event) => {
	if (event.code && event.code !== "Unidentified") return event.code;
	const key = getEventKey(event);
	if (key) {
		if (Object.values(EVENT_CODE).includes(key)) return key;
		switch (key) {
			case " ": return EVENT_CODE.space;
			default: return "";
		}
	}
	return "";
};
var getEventKey = (event) => {
	let key = event.key && event.key !== "Unidentified" ? event.key : "";
	if (!key && event.type === "keyup" && isAndroid()) {
		const target = event.target;
		key = target.value.charAt(target.selectionStart - 1);
	}
	return key;
};
//#endregion
//#region node_modules/element-plus/es/components/form/src/constants.mjs
var formContextKey = Symbol("formContextKey");
var formItemContextKey = Symbol("formItemContextKey");
//#endregion
//#region node_modules/element-plus/es/utils/typescript.mjs
var mutable = (val) => val;

export { EVENT_CODE as E, formItemContextKey as a, composeEventHandlers as c, formContextKey as f, getEventCode as g, mutable as m };
//# sourceMappingURL=typescript-DnijNOm4.mjs.map
