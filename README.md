# edEXT

edEXT es un sistema para administrar cursos y programas de extensión. Permite
trabajar desde la aplicación de escritorio y desde una página web; también
incluye una versión móvil para consultar cursos y ediciones.

## Partes del sistema

- **Central:** recibe las solicitudes de la Web y guarda los datos en MySQL.
- **Web:** aplicación que se ejecuta en Tomcat y se abre desde un navegador.
- **Móvil:** versión simple para teléfonos, opcional.
- **Estación de Trabajo:** aplicación de escritorio para administrar los datos.

En una instalación en dos computadoras, normalmente se usa:

| Computadora | Qué se ejecuta |
|---|---|
| Central | MySQL y el servicio Central |
| Web | Tomcat con `web.war` y, si se desea, `mobile.war` |

La base de datos queda en la PC Central. La PC Web se comunica con el Central
por la red; no necesita una copia de la base ni acceso directo a MySQL.

## Requisitos principales

- Java 21.
- MySQL 8 en la PC Central.
- Tomcat 10.1 en la PC Web.
- Maven 3.9 o posterior solamente si se va a compilar el proyecto.

## Compilar

Si recibiste una carpeta `dist` con los archivos preparados, podés usar esos
archivos y saltear este paso. Para compilar desde el código:

- Windows, desde PowerShell en la carpeta del proyecto:
  ```powershell
  .\scripts\build.ps1
  ```
- Ubuntu, desde la carpeta del proyecto:
  ```sh
  bash scripts/build.sh
  ```

Los archivos listos para instalar quedan en `dist/`. La compilación completa
ejecuta pruebas del Central, que usan una base de pruebas llamada
`edext_test`; esa base es distinta de la base de uso diario `edext_db`.

## Iniciar la aplicación de escritorio

En Windows, desde la carpeta del proyecto:

```powershell
.\scripts\iniciar-servicio.ps1
```

Para abrir la Estación de Trabajo en otra ventana de PowerShell:

```powershell
.\scripts\abrir-estacion.ps1
```

En Ubuntu, si están disponibles los lanzadores de `dist/`:

```sh
./dist/edext-servicio
./dist/edext-estacion
```

El servicio Central debe permanecer encendido para que la Web funcione.

## Instalación en la red

La instalación paso a paso para Windows y Ubuntu 24.04 está en la
[guía práctica de instalación](./INSTALACION_PRACTICA.md). También se conserva
la [guía de instalación detallada](./INSTALACION.md).

## Acta de estudio

El acta está disponible cuando hay una edición cerrada para cada curso del
programa. No es necesario haber aprobado los cursos.

## Importante

- No abras el puerto de MySQL a toda la red: la Web habla con el Central, no
  directamente con la base.
- **Carga Total Automática** reemplaza los datos existentes. Usala solo en una
  base de pruebas.
- Las imágenes subidas desde la Web se guardan en la PC Web. Para mostrar en la
  Web las imágenes guardadas desde la Estación, hay que compartir la carpeta de
  imágenes del Central con la PC Web.
