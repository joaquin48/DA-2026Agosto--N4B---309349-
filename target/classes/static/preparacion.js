import BattleShipBoard from "./vendor/BattleShipBoard.js";
import { app, dispatcher, escapeHtml, iniciarVista, parametros } from "./comun.js";

const batallaId = parametros().batallaId;
let tablero;

function crearTablero() {
  if (tablero) tablero.destroy();
  tablero = new BattleShipBoard(document.getElementById("battle-board"), dispatcher, { tamanoCelda: 30, tamanoFuente: 11, tamanoIcono: 20 });
}

function mostrarPreparacion(datos) {
  const batalla = datos.batalla;
  const pendientes = batalla.barcosPendientes ?? [];
  const elegido = pendientes.find((barco) => barco.id === datos.barcoSeleccionado) ?? pendientes[0];
  const instruccion = elegido ? `Posicione su ${escapeHtml(elegido.nombre.toLowerCase())} en forma ${datos.orientacion === "VERTICAL" ? "vertical" : "horizontal"}` : "Batalla preparada";
  app.innerHTML = `<section class="page-head"><div><p class="eyebrow">Desplegando la flota</p><h1>${escapeHtml(batalla.lugar)}</h1><p class="lead">Elegí un barco, definí su orientación y marcá la proa.</p></div></section>
  <section class="battle-layout"><div class="panel board-panel"><div id="battle-board"></div></div><aside class="side-panel">
  <section class="panel side-card"><h2>Barcos pendientes</h2><div class="ship-list">${pendientes.map((barco) => `<button class="ship-option ${barco.id === datos.barcoSeleccionado ? "is-selected" : ""}" data-barco="${barco.id}"><strong>${escapeHtml(barco.nombre)}</strong><span>${barco.tamano} celdas</span></button>`).join("") || '<p class="meta">Flota desplegada.</p>'}</div></section>
  <section class="panel side-card"><h2>Orientación</h2><div class="segmented"><button class="button ${datos.orientacion === "HORIZONTAL" ? "is-selected" : ""}" data-orientacion="HORIZONTAL">Horizontal</button><button class="button ${datos.orientacion === "VERTICAL" ? "is-selected" : ""}" data-orientacion="VERTICAL">Vertical</button></div></section>
  <section class="panel side-card"><h2>Acción</h2><p class="deployment-instruction">${instruccion}</p><div class="form-actions"><button id="volver" class="button">Volver</button><button id="librar" class="button button--primary">Librar batalla</button></div></section></aside></section>`;
  crearTablero();
  app.querySelectorAll("[data-barco]").forEach((boton) => boton.addEventListener("click", () => dispatcher.submit("/preparacion/seleccionarBarco", { barcoId: boton.dataset.barco })));
  app.querySelectorAll("[data-orientacion]").forEach((boton) => boton.addEventListener("click", () => dispatcher.submit("/preparacion/elegirOrientacion", { orientacion: boton.dataset.orientacion })));
  document.getElementById("volver").addEventListener("click", () => dispatcher.submit("/preparacion/volver"));
  document.getElementById("librar").addEventListener("click", () => dispatcher.submit("/preparacion/librarBatalla", { version: batalla.version }));
}

iniciarVista("Preparacion", { urlInicio: "/preparacion/iniciar", urlCierre: "/preparacion/finalizar", dataInicio: { batallaId } }, { mostrarPreparacion });
