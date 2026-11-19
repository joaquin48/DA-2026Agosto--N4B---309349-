import { app, dispatcher, escapeHtml, estadoInfo, iniciarVista } from "./comun.js";

function porcentaje(valor, total) { return total > 0 ? Math.round(100 * valor / total) : 0; }

function filaBatalla(batalla) {
  const estado = estadoInfo(batalla.estado);
  return `<article class="battle-row"><div><h3>${escapeHtml(batalla.lugar)}</h3><span class="meta">${batalla.columnas} × ${batalla.filas} celdas</span></div>
    <span class="status ${estado.clase}">${estado.etiqueta}</span>
    <div class="integrity"><span>Propia ${batalla.sanasPropias}/${batalla.totalPropias} (${porcentaje(batalla.sanasPropias, batalla.totalPropias)}%)</span><span>Enemiga ${batalla.sanasEnemigas}/${batalla.totalEnemigas}</span></div>
    <button class="button" data-batalla="${batalla.id}">Entrar</button></article>`;
}

function mostrarSala(sala) {
  const batallas = sala.batallas ?? [];
  app.innerHTML = `<section class="page-head"><div><p class="eyebrow">Sala de situación</p><h1>Campaña naval</h1><p class="lead">Supervisá tus frentes activos o prepará una nueva flota.</p></div><button id="nueva" class="button button--primary">Nueva batalla</button></section>
    <section class="stats"><article class="panel stat"><span class="stat__label">Victorias</span><strong class="stat__value">${sala.ganadas}</strong></article><article class="panel stat"><span class="stat__label">Derrotas</span><strong class="stat__value">${sala.perdidas}</strong></article><article class="panel stat"><span class="stat__label">Efectividad</span><strong class="stat__value">${Number(sala.exito).toFixed(1)}%</strong></article></section>
    <section class="panel section-panel"><div class="section-title"><div><p class="eyebrow">Operaciones</p><h2>Batallas registradas</h2></div></div><div class="battle-list">${batallas.length ? batallas.map(filaBatalla).join("") : '<div class="empty"><h3>Todavía no hay batallas</h3></div>'}</div></section>`;
  document.getElementById("nueva").addEventListener("click", () => dispatcher.submit("/sala/nuevaBatalla"));
  app.querySelectorAll("[data-batalla]").forEach((boton) => boton.addEventListener("click", () => dispatcher.submit("/sala/entrar", { batallaId: boton.dataset.batalla })));
}

iniciarVista("Sala", { urlInicio: "/sala/iniciar", urlCierre: "/sala/finalizar" }, { mostrarSala });
