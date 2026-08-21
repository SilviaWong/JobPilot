import{St as e,k as t,w as n}from"./v3hOaweP.js";import{_ as r,h as i,i as a,n as o}from"./cz7-9sNG.js";var s={prefix:Math.floor(Math.random()*1e4),current:0},c=Symbol(`elIdInjection`),l=()=>n()?t(c,s):s,u=t=>{let n=l();!r&&n===s&&a(`IdInjection`,`Looks like you are using server rendering, you must provide a id provider to ensure the hydration process to be succeed
usage: app.provide(ID_INJECTION_KEY, {
  prefix: number,
  current: number,
})`);let c=o();return i(()=>e(t)||`${c.value}-id-${n.prefix}-${n.current++}`)};export{u as n,l as r,c as t};