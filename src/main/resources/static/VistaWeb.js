import CommandDispatcher from './CommandDispatcher.js';

export default class VistaWeb {
  constructor(idVista = crypto.randomUUID()) {
    this.idVista = idVista;
    this.ultimaVersion = -1;
  }

  iniciar() {
    throw new Error('VistaWeb.iniciar todavía no está implementado');
  }

  ejecutarComando(id, parametro) {
    CommandDispatcher.processCommands({ commands: [{ id, parametro }] });
  }

  enviarAccion(url, datos) {
    return CommandDispatcher.submit(url, datos);
  }

  mostrarError(mensaje) {
    window.alert(mensaje);
  }

  cerrar() {
    throw new Error('VistaWeb.cerrar todavía no está implementado');
  }

  aplicarInstantanea(version, commands) {
    if (version < this.ultimaVersion) return;
    this.ultimaVersion = version;
    CommandDispatcher.processCommands(commands);
  }
}
