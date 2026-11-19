import BattleShipBoard from "./vendor/BattleShipBoard.js";
import { app, dispatcher, escapeHtml, estadoInfo, iniciarVista, parametros } from "./comun.js";

const batallaId = parametros().batallaId;
let tablero;

function crearTablero() {
  if (tablero) tablero.destroy();
  tablero = new BattleShipBoard(document.getElementById("battle-board"), dispatcher, { tamanoCelda: 30, tamanoFuente: 11, tamanoIcono: 20 });
}

function mostrarBatalla(batalla) {
  const estado = estadoInfo(batalla.estado);
  const resultado = batalla.estado === "GANADA" ? '<div class="battle-result battle-result--won"><strong>Victoria confirmada.</strong></div>' : batalla.estado === "PERDIDA" ? '<div class="battle-result battle-result--lost"><strong>Derrota confirmada.</strong></div>' : "";
  app.innerHTML = `<section class="page-head"><div><p class="eyebrow">Batalla en curso</p><h1>${escapeHtml(batalla.lugar)}</h1><p class="lead">Seleccioná una coordenada en aguas enemigas.</p></div><span class="status ${estado.clase}">${estado.etiqueta}</span></section>${resultado}
  <section class="battle-layout"><div class="panel board-panel"><div id="battle-board"></div></div><aside class="side-panel"><section class="panel side-card"><p class="eyebrow">Turno activo</p><span class="round">Ronda ${batalla.numeroRonda}</span></section><section class="panel side-card"><h2>Bajas enemigas</h2><p class="lead">${escapeHtml(batalla.bajasEnemigas.join(" ") || "Ninguna")}</p></section><section class="panel side-card"><button id="volver" class="button">Volver a la sala</button></section></aside></section>`;
  crearTablero();
  document.getElementById("volver").addEventListener("click", () => dispatcher.submit("/batalla/volver"));
}

iniciarVista("Batalla", { urlInicio: "/batalla/iniciar", urlCierre: "/batalla/finalizar", dataInicio: { batallaId } }, { mostrarBatalla });
