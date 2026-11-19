# Frontend

Cliente web de Batalla Naval construido con HTML, CSS y JavaScript nativos,
organizado con una página por caso de uso como el proyecto Agenda.

- `sala.html`: listado y estadísticas de las batallas.
- `nueva-batalla.html`: formulario de creación.
- `preparacion.html`: despliegue de la flota.
- `batalla.html`: combate y resultado.
- `comun.js`: conexión SSE y comportamiento compartido de las vistas.
- Los archivos JavaScript con el mismo nombre de cada página registran sus comandos.
- `styles.css`: sistema visual responsive y accesible.
- `vendor/`: copias sin modificaciones de `CommandDispatcher.js` y
  `BattleShipBoard.js`, provistas con la letra del obligatorio.

Maven copia esta carpeta completa a los recursos estáticos de Spring Boot. Los
presentadores del servidor son controladores de sesión y cada vista declara sus
endpoints de inicio y finalización.
