import CommandDispatcher from './CommandDispatcher.js';

export default class ConexionEventos {
  constructor() {
    this.fuente = null;
  }

  conectar(url) {
    this.cerrar();
    this.fuente = new EventSource(url);
    this.fuente.onmessage = evento => this.recibir(JSON.parse(evento.data));
  }

  recibir(sobre) {
    CommandDispatcher.processCommands(sobre);
  }

  cerrar() {
    if (this.fuente) this.fuente.close();
    this.fuente = null;
  }
}
