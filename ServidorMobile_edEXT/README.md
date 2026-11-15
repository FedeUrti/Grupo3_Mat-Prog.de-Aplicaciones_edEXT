# ServidorMobile_edEXT

Aplicación web independiente y optimizada para teléfonos y tabletas. Maven
genera `target/mobile.war`; Tomcat la publica en `/mobile`, junto al Web de
escritorio desplegado como `/web` en una instalación nativa. Mobile obtiene
las imágenes de cursos y ediciones desde `/web/imagenes-curso`.

## Casos de uso

- **Inicio de sesión y cierre:** solo cuentas existentes de tipo estudiante.
  El login admite nickname o correo y reutiliza la autenticación SOAP central.
  Un docente autenticado correctamente recibe HTTP 403 en Mobile; no se crean
  cuentas desde esta aplicación. El login Mobile usa una tarjeta translúcida,
  iconografía y fondo luminoso; su formulario conserva los mensajes y el
  flujo de autenticación actuales. Login, catálogo, curso y edición incluyen
  un selector claro/oscuro que persiste en el navegador y solo afecta a Mobile.
- **Consulta de curso:** catálogo y detalle con instituto, descripción,
  duración, horas, créditos, categorías, programas y ediciones.
- **Consulta de edición:** fechas, disponibilidad de cupos, estado y docentes.

No implementa inscripciones, administración, valoraciones, registros nuevos ni
funciones de docentes. Al entrar por `/`, el Web detecta teléfonos/tabletas
mediante el `User-Agent` y, si el navegador oculta ese dato al pedir el “sitio
de escritorio”, mediante las capacidades táctiles y el tamaño de pantalla.
Guarda el resultado en una cookie para conservar la versión móvil. Los
navegadores de PC reconocibles pasan directamente al Web de escritorio, sin
una pantalla intermedia. Si un PC abre `/mobile/`, vuelve a la versión de
escritorio. Esta detección solo enruta la interfaz y no es una barrera de
seguridad contra suplantación.

## SOAP y dependencias

- Solo consume las operaciones publicadas por los WSDL de Usuario, Curso y
  Edición.
- Los stubs se generan en `generate-sources` desde
  `../ServidorWeb_edEXT/src/main/wsdl`. Cuando se actualice el contrato SOAP,
  exportalo primero con `scripts/exportar-contrato.ps1` o
  `scripts/exportar-contrato.sh` en el Web y luego compilá Mobile.
- Los WSDL/XSD se empaquetan en el WAR. El servicio se invoca por
  `EDEXT_SOAP_BASE_URL`, que en Compose apunta a `http://central:9128/edext/soap`.
- Fuera de Compose, el valor predeterminado es
  `http://localhost:9128/edext/soap`, adecuado cuando Mobile y el Central
  corren en la misma máquina. Si están en máquinas distintas, configure
  `EDEXT_SOAP_BASE_URL` con una URL alcanzable desde Tomcat.
- No tiene dependencias de código del Web/Central ni configuración de JDBC o
  credenciales de base de datos.

## Compilar y desplegar

Desde la raíz del repositorio:

```powershell
mvn -f ServidorMobile_edEXT/pom.xml package
docker compose up --build -d
```

El Dockerfile genera el WAR de escritorio y el WAR móvil y los instala en el
mismo Tomcat. Ambos usan el puerto del host `8081`: el escritorio en `/` y
Mobile en `/mobile/`. En este modo, la ruta de imágenes se ajusta a `/imagenes-curso`
mediante `EDEXT_WEB_IMAGE_ENDPOINT`; en la instalación nativa el valor
predeterminado es `/web/imagenes-curso`. `EDEXT_WEB_CONTEXT_PATH=/` hace que un
usuario de escritorio que entra en Mobile vuelva al contexto Web raíz. Al entrar
por `/`, el filtro redirige según el dispositivo; la cookie permite seguir en Mobile aunque se active el modo
“sitio de escritorio” del navegador. No se agrega otro contenedor ni se
publica otro puerto.

En un teléfono o PC en la misma Wi-Fi/LAN, abrí
`http://<IP-LAN-del-PC>:8081/`; la detección redirige a la interfaz adecuada.
También se puede abrir directamente `/mobile/` desde el teléfono. Al cerrar
sesión, se invalida la sesión web de Mobile. Una sesión creada en el Web de
escritorio no se comparte con Mobile.
