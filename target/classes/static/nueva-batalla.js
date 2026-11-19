import { app, dispatcher, escapeHtml, iniciarVista } from "./comun.js";

function mostrarOpciones(opciones) {
  app.innerHTML = `<section class="page-head"><div><p class="eyebrow">Nueva operación</p><h1>Configurar batalla</h1><p class="lead">Definí el océano, la flota y la táctica de la máquina.</p></div></section>
  <form id="formulario" class="form-layout"><section class="panel form-section"><h2>Teatro de operaciones</h2><div class="field-grid">
  <div class="field field--full"><label for="lugar">Lugar</label><input id="lugar" name="lugar" placeholder="Se genera un código si queda vacío"></div>
  <div class="field"><label for="filas">Filas</label><input id="filas" name="filas" type="number" value="10"></div><div class="field"><label for="columnas">Columnas</label><input id="columnas" name="columnas" type="number" value="10"></div>
  <div class="field field--full"><label for="tactica">Táctica</label><select id="tactica" name="tacticaId">${opciones.tacticas.map((t) => `<option value="${escapeHtml(t.id)}">${escapeHtml(t.nombre)}</option>`).join("")}</select></div></div></section>
  <section class="panel form-section"><h2>Composición de flota</h2><div class="fleet-grid">${opciones.tipos.map((t) => `<label class="quantity-row"><span><strong>${escapeHtml(t.nombre)}</strong><small>${t.tamano} celdas</small></span><input name="cantidad.${escapeHtml(t.id)}" type="number" value="1"></label>`).join("")}</div>
  <div class="form-actions"><button id="volver" class="button" type="button">Volver</button><button class="button button--primary" type="submit">Continuar</button></div></section></form>`;
  document.getElementById("volver").addEventListener("click", () => dispatcher.submit("/nueva-batalla/volver"));
  document.getElementById("formulario").addEventListener("submit", (evento) => { evento.preventDefault(); dispatcher.submit("/nueva-batalla/crear", Object.fromEntries(new FormData(evento.currentTarget).entries())); });
}

iniciarVista("NuevaBatalla", { urlInicio: "/nueva-batalla/iniciar" }, { mostrarOpciones });
