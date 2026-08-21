import '../virtual/entry.mjs';
import { isVNode, Comment } from 'vue';
import { isArray } from '@vue/shared';

//#region node_modules/element-plus/es/utils/vue/vnode.mjs
function isComment(node) {
	return isVNode(node) && node.type === Comment;
}
var flattedChildren = (children) => {
	const vNodes = isArray(children) ? children : [children];
	const result = [];
	vNodes.forEach((child) => {
		if (isArray(child)) result.push(...flattedChildren(child));
		else if (isVNode(child) && child.component?.subTree) result.push(child, ...flattedChildren(child.component.subTree));
		else if (isVNode(child) && isArray(child.children)) result.push(...flattedChildren(child.children));
		else if (isVNode(child) && child.shapeFlag === 2) result.push(...flattedChildren(child.type()));
		else result.push(child);
	});
	return result;
};

export { flattedChildren as f, isComment as i };
//# sourceMappingURL=vnode-BA-uK14B.mjs.map
