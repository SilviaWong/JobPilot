import { _ as _plugin_vue_export_helper_default, $ as $fetch$1 } from '../virtual/entry.mjs';
import { a as ElCard, E as ElButton, b as ElMessage } from './css-n73NFgGH.mjs';
import { ref, mergeProps, withCtx, createVNode, toDisplayString, openBlock, createBlock, Fragment, renderList, withModifiers, createCommentVNode, createTextVNode, useSSRContext } from 'vue';
import { ssrRenderAttrs, ssrRenderComponent, ssrRenderList, ssrInterpolate, ssrRenderStyle } from 'vue/server-renderer';
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
import '@vue/shared';
import '@vueuse/core';
import 'lodash-unified';
import 'unhead/utils';
import './icon-BTfkZ5_-.mjs';
import './typescript-DnijNOm4.mjs';
import '@ctrl/tinycolor';

//#region pages/kanban/index.vue
var _sfc_main = {
	__name: "index",
	__ssrInlineRender: true,
	setup(__props) {
		const loading = ref(false);
		const jobs = ref([]);
		const columns = [
			{
				id: "to-apply",
				title: "待投递"
			},
			{
				id: "applied",
				title: "已投递"
			},
			{
				id: "interview-1",
				title: "一面"
			},
			{
				id: "interview-2",
				title: "二面"
			},
			{
				id: "offer",
				title: "Offer"
			}
		];
		const fetchJobs = async () => {
			loading.value = true;
			try {
				const res = await $fetch$1("/api/jobs?status=all&pageSize=1000");
				if (res.success) {
					const kanbanStatuses = columns.map((c) => c.id);
					const processedJobs = res.data.map((j) => {
						if (!kanbanStatuses.includes(j.status) && j.isFavorited) return {
							...j,
							status: "to-apply"
						};
						return j;
					});
					jobs.value = processedJobs.filter((j) => kanbanStatuses.includes(j.status));
				}
			} catch (error) {
				ElMessage.error("获取职位数据失败");
			} finally {
				loading.value = false;
			}
		};
		const getJobsByStatus = (status) => {
			return jobs.value.filter((job) => job.status === status);
		};
		const onDragStart = (event, job) => {
			event.dataTransfer.dropEffect = "move";
			event.dataTransfer.effectAllowed = "move";
			event.dataTransfer.setData("jobId", job.id);
		};
		const onDrop = async (event, newStatus) => {
			const id = event.dataTransfer.getData("jobId");
			if (!id) return;
			const job = jobs.value.find((j) => j.id === id);
			if (!job || job.status === newStatus) return;
			const oldStatus = job.status;
			job.status = newStatus;
			try {
				if ((await $fetch$1(`/api/jobs/${id}/status`, {
					method: "PUT",
					body: { status: newStatus }
				})).success) ElMessage.success("状态更新成功");
				else {
					job.status = oldStatus;
					ElMessage.error("状态更新失败");
				}
			} catch (error) {
				job.status = oldStatus;
				ElMessage.error("网络请求错误");
			}
		};
		return (_ctx, _push, _parent, _attrs) => {
			const _component_el_card = ElCard;
			const _component_el_button = ElButton;
			_push(`<div${ssrRenderAttrs(mergeProps({ class: "kanban-page" }, _attrs))} data-v-4949bc48>`);
			_push(ssrRenderComponent(_component_el_card, { shadow: "never" }, {
				header: withCtx((_, _push, _parent, _scopeId) => {
					if (_push) {
						_push(`<div class="card-header" data-v-4949bc48${_scopeId}><span style="${ssrRenderStyle({
							"font-size": "18px",
							"font-weight": "600"
						})}" data-v-4949bc48${_scopeId}>📌 投递追踪看板</span>`);
						_push(ssrRenderComponent(_component_el_button, {
							type: "primary",
							size: "small",
							onClick: fetchJobs,
							loading: loading.value
						}, {
							default: withCtx((_, _push, _parent, _scopeId) => {
								if (_push) _push(` 刷新数据 `);
								else return [createTextVNode(" 刷新数据 ")];
							}),
							_: 1
						}, _parent, _scopeId));
						_push(`</div>`);
					} else return [createVNode("div", { class: "card-header" }, [createVNode("span", { style: {
						"font-size": "18px",
						"font-weight": "600"
					} }, "📌 投递追踪看板"), createVNode(_component_el_button, {
						type: "primary",
						size: "small",
						onClick: fetchJobs,
						loading: loading.value
					}, {
						default: withCtx(() => [createTextVNode(" 刷新数据 ")]),
						_: 1
					}, 8, ["loading"])])];
				}),
				default: withCtx((_, _push, _parent, _scopeId) => {
					if (_push) {
						_push(`<div class="kanban-board" data-v-4949bc48${_scopeId}><!--[-->`);
						ssrRenderList(columns, (column) => {
							_push(`<div class="kanban-column" data-v-4949bc48${_scopeId}><div class="column-header" data-v-4949bc48${_scopeId}><h3 data-v-4949bc48${_scopeId}>${ssrInterpolate(column.title)}</h3><span class="count" data-v-4949bc48${_scopeId}>${ssrInterpolate(getJobsByStatus(column.id).length)}</span></div><div class="kanban-cards" data-v-4949bc48${_scopeId}><!--[-->`);
							ssrRenderList(getJobsByStatus(column.id), (job) => {
								_push(ssrRenderComponent(_component_el_card, {
									key: job.jobId,
									class: "job-card",
									shadow: "hover",
									draggable: "true",
									onDragstart: ($event) => onDragStart($event, job)
								}, {
									default: withCtx((_, _push, _parent, _scopeId) => {
										if (_push) _push(`<div class="card-content" data-v-4949bc48${_scopeId}><div class="job-title" data-v-4949bc48${_scopeId}>${ssrInterpolate(job.title)}</div><div class="job-company" data-v-4949bc48${_scopeId}>${ssrInterpolate(job.companyName)}</div><div class="job-salary" data-v-4949bc48${_scopeId}>${ssrInterpolate(job.salary)}</div></div>`);
										else return [createVNode("div", { class: "card-content" }, [
											createVNode("div", { class: "job-title" }, toDisplayString(job.title), 1),
											createVNode("div", { class: "job-company" }, toDisplayString(job.companyName), 1),
											createVNode("div", { class: "job-salary" }, toDisplayString(job.salary), 1)
										])];
									}),
									_: 2
								}, _parent, _scopeId));
							});
							_push(`<!--]-->`);
							if (getJobsByStatus(column.id).length === 0) _push(`<div class="empty-column" data-v-4949bc48${_scopeId}> 拖拽职位到此处 </div>`);
							else _push(`<!---->`);
							_push(`</div></div>`);
						});
						_push(`<!--]--></div>`);
					} else return [createVNode("div", { class: "kanban-board" }, [(openBlock(), createBlock(Fragment, null, renderList(columns, (column) => {
						return createVNode("div", {
							key: column.id,
							class: "kanban-column",
							onDragover: withModifiers(() => {}, ["prevent"]),
							onDragenter: withModifiers(() => {}, ["prevent"]),
							onDrop: ($event) => onDrop($event, column.id)
						}, [createVNode("div", { class: "column-header" }, [createVNode("h3", null, toDisplayString(column.title), 1), createVNode("span", { class: "count" }, toDisplayString(getJobsByStatus(column.id).length), 1)]), createVNode("div", { class: "kanban-cards" }, [(openBlock(true), createBlock(Fragment, null, renderList(getJobsByStatus(column.id), (job) => {
							return openBlock(), createBlock(_component_el_card, {
								key: job.jobId,
								class: "job-card",
								shadow: "hover",
								draggable: "true",
								onDragstart: ($event) => onDragStart($event, job)
							}, {
								default: withCtx(() => [createVNode("div", { class: "card-content" }, [
									createVNode("div", { class: "job-title" }, toDisplayString(job.title), 1),
									createVNode("div", { class: "job-company" }, toDisplayString(job.companyName), 1),
									createVNode("div", { class: "job-salary" }, toDisplayString(job.salary), 1)
								])]),
								_: 2
							}, 1032, ["onDragstart"]);
						}), 128)), getJobsByStatus(column.id).length === 0 ? (openBlock(), createBlock("div", {
							key: 0,
							class: "empty-column"
						}, " 拖拽职位到此处 ")) : createCommentVNode("", true)])], 40, [
							"onDragover",
							"onDragenter",
							"onDrop"
						]);
					}), 64))])];
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
	(ssrContext.modules || (ssrContext.modules = /* @__PURE__ */ new Set())).add("pages/kanban/index.vue");
	return _sfc_setup ? _sfc_setup(props, ctx) : void 0;
};
var kanban_default = /*#__PURE__*/ _plugin_vue_export_helper_default(_sfc_main, [["__scopeId", "data-v-4949bc48"]]);

export { kanban_default as default };
//# sourceMappingURL=kanban-B51syTZD.mjs.map
