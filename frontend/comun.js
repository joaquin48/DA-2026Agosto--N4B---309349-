import dispatcher from "./vendor/CommandDispatcher.js";

export { dispatcher };

export const app = document.getElementById("app");

export function escapeHtml(valor) {
  return String(valor ?? "")
    .replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;").replaceAll("'", "&#039;");
}

export function parametros() {
  return Object.fromEntries(new URLSearchParams(window.location.search).entries());
}

export function mostrarError(mensaje) {
  let region = document.getElementById("toast-region");
  if (!region) {
    region = document.createElement("div");
    region.id = "toast-region";
    region.className = "toast-region";
    document.body.appendChild(region);
  }
  const aviso = document.createElement("div");
  aviso.className = "toast";
  aviso.setAttribute("role", "alert");
  aviso.textContent = mensaje || "No fue posible completar la operación.";
  region.replaceChildren(aviso);
  window.setTimeout(() => aviso.remove(), 6500);
}

export function iniciarVista(nombre, configuracion, manejadores) {
  const vista = dispatcher.registerView(nombre, {
    ...configuracion,
    onError: (_estado, texto) => mostrarError(texto),
    onException: mostrarError,
  });
  vista.handler("navegar", (destino) => { window.location.href = destino; });
  vista.handler("mostrarError", mostrarError);
  Object.entries(manejadores).forEach(([comando, manejador]) => vista.handler(comando, manejador));

  const eventos = new EventSource("/sse/registrarSSE", { withCredentials: true });
  let iniciada = false;
  eventos.onopen = () => {
    if (!iniciada) {
      iniciada = true;
      dispatcher.startView(vista);
    } else if (configuracion.urlInicio) {
      // Una reconexión recupera los cambios ocurridos mientras SSE estuvo cortado.
      dispatcher.submit(configuracion.urlInicio, configuracion.dataInicio ?? {});
    }
  };
  eventos.onmessage = (evento) => {
    try {
      dispatcher.processCommands(JSON.parse(evento.data));
    } catch (_error) {
      mostrarError("Se recibió una actualización inválida.");
    }
  };
  window.addEventListener("pagehide", () => eventos.close());
  return vista;
}

export function estadoInfo(estado) {
  return {
    EN_PREPARACION: { etiqueta: "En preparación", clase: "status--prepare" },
    EN_COMBATE: { etiqueta: "En combate", clase: "status--combat" },
    GANADA: { etiqueta: "Ganada", clase: "status--won" },
    PERDIDA: { etiqueta: "Perdida", clase: "status--lost" },
  }[estado] ?? { etiqueta: estado, clase: "" };
}
