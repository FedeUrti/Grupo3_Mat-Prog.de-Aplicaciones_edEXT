# Guía práctica de instalación de edEXT

Esta guía explica cómo instalar edEXT en dos computadoras: una para el Central
y otra para la Web. Los pasos se separan por sistema operativo. Si las dos
computadoras usan sistemas distintos, seguí para cada una la sección de su
propio sistema.

## 1. Qué va en cada computadora

| PC | Instalar | Qué guarda o ejecuta |
|---|---|---|
| **Central** | Java 21 y MySQL 8 | El servicio Central y la base `edext_db` |
| **Web** | Java 21 y Tomcat 10.1 | `web.war` y, opcionalmente, `mobile.war` |

La base de datos queda **solo en la PC Central**. No instales otra base en la
PC Web ni abras el puerto de MySQL para que la Web se conecte. La Web se conecta
al servicio Central por el puerto TCP `9128`; los usuarios abren la Web por el
puerto TCP `8080`.

Antes de empezar:

1. Conectá las dos PCs a la misma red.
2. Averiguá la dirección IP de cada una y reservá una IP fija para la PC
   Central. En los ejemplos, reemplazá `IP_CENTRAL` e `IP_WEB` por las IP
   reales.
3. Tené a mano los archivos de `dist/`: `servidor.jar`, `web.war` y, si vas a
   instalar la versión móvil, `mobile.war`.

Si tenés el código fuente pero no los archivos preparados, compilá en una
computadora con Java 21 y Maven 3.9 o posterior:

- Windows: `.\scripts\build.ps1`
- Ubuntu: `bash scripts/build.sh`

La compilación completa también ejecuta pruebas del Central, que necesitan una
base `edext_test` separada. Esa base es solo para pruebas; la base de uso diario
es `edext_db`. No conectes la Web a MySQL para solucionar un problema de
compilación.

## 2. Instalar en Windows

### PC Central (Windows)

1. Instalá Java 21 y MySQL 8. Iniciá el servicio de MySQL.
2. Abrí una consola MySQL con una cuenta administrativa y creá la base y la
   cuenta que usará edEXT:

   ```sql
   CREATE DATABASE edext_db CHARACTER SET utf8mb4;
   CREATE USER 'edext_app'@'localhost' IDENTIFIED BY 'PONE_UNA_CLAVE_SEGURA';
   GRANT ALL PRIVILEGES ON edext_db.* TO 'edext_app'@'localhost';
   FLUSH PRIVILEGES;
   ```

   Si vas a compilar el proyecto y ejecutar también las pruebas desde esta PC,
   creá una base de pruebas aparte:

   ```sql
   CREATE DATABASE edext_test CHARACTER SET utf8mb4;
   GRANT ALL PRIVILEGES ON edext_test.* TO 'edext_app'@'localhost';
   ```

   Guardá la clave: la vas a escribir también en la configuración del Central.
3. Copiá `dist\servidor.jar` a una carpeta permanente, por ejemplo
   `C:\edext\servidor.jar`. Creá también una carpeta para las imágenes de la
   Estación, por ejemplo `C:\edext\imagenes`.
4. Creá o editá este archivo, usando la cuenta de Windows que iniciará el
   Central:

   ```powershell
   New-Item -ItemType Directory -Force "$HOME\.edEXT" | Out-Null
   notepad "$HOME\.edEXT\servidor.properties"
   ```

   Escribí estas propiedades y reemplazá la clave:

   ```properties
   soap.bind.url=http://0.0.0.0:9128/edext/soap
   jdbc.url=jdbc:mysql://127.0.0.1:3306/edext_db?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true
   jdbc.user=edext_app
   jdbc.password=PONE_UNA_CLAVE_SEGURA
   hbm2ddl.auto=update
   directorios.imagenes=C:/edext/imagenes
   ```

   `0.0.0.0` permite que el Central reciba conexiones de la red. No escribas
   `0.0.0.0` en la URL de la Web.
5. Iniciá el Central desde PowerShell:

   ```powershell
   java -jar C:\edext\servidor.jar
   ```

   Dejá abierta esa ventana. Si aparece un error de conexión, revisá el usuario,
   la clave, la URL de MySQL y que MySQL esté iniciado. Para abrir la Estación
   en otra ventana:

   ```powershell
   java -jar C:\edext\servidor.jar --estacion
   ```
6. Permití el puerto `9128` desde la PC Web. En PowerShell abierto como
   administrador, reemplazá la IP:

   ```powershell
   $IP_WEB = "192.168.1.131"
   New-NetFirewallRule -DisplayName "edEXT Central SOAP" -Direction Inbound `
     -Action Allow -Protocol TCP -LocalPort 9128 -RemoteAddress $IP_WEB `
     -Profile Private
   ```

### PC Web (Windows)

1. Instalá Java 21 y Tomcat 10.1.
2. Copiá `dist\web.war` a la carpeta `webapps` de Tomcat. El nombre
   `web.war` crea el sitio en `/web`. Si querés la versión móvil, copiá también
   `dist\mobile.war`; se publica en `/mobile`.
3. Iniciá Tomcat una vez. Luego configurá el archivo
   `.edEXT\web.properties` del **usuario que ejecuta el servicio de Tomcat**.
   Si no sabés cuál es, abrí `services.msc`, buscá el servicio de Tomcat y
   revisá su pestaña **Iniciar sesión**. No siempre es el mismo usuario con el
   que abriste Windows. Si el servicio usa `Sistema local`, su carpeta de
   usuario suele ser `C:\Windows\System32\config\systemprofile`.

   El archivo queda en `C:\Users\USUARIO_TOMCAT\.edEXT\web.properties`.
   Reemplazá `USUARIO_TOMCAT` por la cuenta que viste en **Iniciar sesión**.
   Para `Sistema local`, usá
   `C:\Windows\System32\config\systemprofile\.edEXT\web.properties`.

   Configuración mínima:

   ```properties
   central.soap.url=http://IP_CENTRAL:9128/edext/soap
   upload.dir=C:/edext/uploads
   imagenes.dir=C:/edext/imagenes
   ```

   Creá las carpetas indicadas y asegurate de que la cuenta del servicio Tomcat
   tenga permiso para escribir en `uploads`. Reiniciá Tomcat después de cambiar
   la configuración.
4. Si el firewall de Windows está activo, permití el puerto `8080` desde la
   red local. Abrí PowerShell como administrador:

   ```powershell
   New-NetFirewallRule -DisplayName "edEXT Web" -Direction Inbound `
     -Action Allow -Protocol TCP -LocalPort 8080 -RemoteAddress LocalSubnet `
     -Profile Private
   ```
5. Desde la PC Web, probá:

   ```powershell
   Test-NetConnection IP_CENTRAL -Port 9128
   ```

   También podés abrir en el navegador
   `http://IP_CENTRAL:9128/edext/soap/curso?wsdl`. Después abrí
   `http://IP_WEB:8080/web/`.

## 3. Instalar en Ubuntu 24.04

### PC Central (Ubuntu)

1. Instalá Java 21 y MySQL:

   ```sh
   sudo apt update
   sudo apt install openjdk-21-jdk mysql-server
   sudo systemctl enable --now mysql
   java -version
   ```

2. Creá la base y el usuario de edEXT:

   ```sh
   sudo mysql
   ```

   En la consola de MySQL:

   ```sql
   CREATE DATABASE edext_db CHARACTER SET utf8mb4;
   CREATE USER 'edext_app'@'localhost' IDENTIFIED BY 'PONE_UNA_CLAVE_SEGURA';
   GRANT ALL PRIVILEGES ON edext_db.* TO 'edext_app'@'localhost';
   FLUSH PRIVILEGES;
   EXIT;
   ```

   Si vas a compilar el proyecto y ejecutar también las pruebas, creá además
   una base de pruebas separada y otorgale permisos:

   ```sh
   sudo mysql -e "CREATE DATABASE edext_test CHARACTER SET utf8mb4;"
   sudo mysql -e "GRANT ALL PRIVILEGES ON edext_test.* TO 'edext_app'@'localhost';"
   ```

3. Copiá `servidor.jar` a una carpeta permanente y creá la carpeta de
   imágenes:

   ```sh
   mkdir -p "$HOME/edext/imagenes" "$HOME/.edEXT"
   cp dist/servidor.jar "$HOME/edext/servidor.jar"
   nano "$HOME/.edEXT/servidor.properties"
   ```

   Pegá lo siguiente y cambiá la clave:

   ```properties
   soap.bind.url=http://0.0.0.0:9128/edext/soap
   jdbc.url=jdbc:mysql://127.0.0.1:3306/edext_db?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true
   jdbc.user=edext_app
   jdbc.password=PONE_UNA_CLAVE_SEGURA
   hbm2ddl.auto=update
   directorios.imagenes=/home/TU_USUARIO/edext/imagenes
   ```

   En `directorios.imagenes`, reemplazá `TU_USUARIO` por el nombre que muestra
   `whoami`.
4. Iniciá el Central:

   ```sh
   java -jar "$HOME/edext/servidor.jar"
   ```

   Dejá abierta la terminal. Para abrir la Estación en otra terminal:

   ```sh
   java -jar "$HOME/edext/servidor.jar" --estacion
   ```

   Si usás los lanzadores de `dist/`, también podés copiar
   `edext-servicio`, `edext-parar-servicio` y `edext-estacion` junto al JAR; el
   lanzador del servicio lo deja funcionando en segundo plano y guarda el log
   en `~/.edEXT/servidor.log`:

   ```sh
   cp dist/edext-servicio dist/edext-parar-servicio dist/edext-estacion "$HOME/edext/"
   chmod +x "$HOME/edext/edext-servicio" "$HOME/edext/edext-parar-servicio" "$HOME/edext/edext-estacion"
   ```
5. Si UFW está activo, permití SOAP solo desde la PC Web:

   ```sh
   sudo ufw allow from IP_WEB to any port 9128 proto tcp
   ```

   No abras el puerto `3306` a la red. MySQL debe seguir atendiendo al Central
   localmente.

### PC Web (Ubuntu)

1. Instalá Java 21 y Tomcat:

   ```sh
   sudo apt update
   sudo apt install openjdk-21-jdk tomcat10
   sudo systemctl enable --now tomcat10
   ```

2. Copiá el WAR desde la computadora donde compilaste. Por ejemplo, con `scp`:

   ```sh
   scp dist/web.war TU_USUARIO@IP_WEB:/tmp/web.war
   ```

   Después, en la PC Web:

   ```sh
   sudo cp /tmp/web.war /var/lib/tomcat10/webapps/web.war
   ```

   Para instalar la versión móvil, repetí el paso con `mobile.war`. Tomcat
   publica los sitios en `/web` y `/mobile`, respectivamente.
3. Configurá la URL del Central y el directorio de cargas de imágenes. Así no
   dependés de cuál sea la carpeta personal (`user.home`) de Tomcat:

   ```sh
   sudo install -d -o tomcat -g tomcat -m 755 /var/lib/tomcat10/uploads
   sudo mkdir -p /etc/systemd/system/tomcat10.service.d
   sudo nano /etc/systemd/system/tomcat10.service.d/edext.conf
   ```

   Pegá este contenido y reemplazá `IP_CENTRAL`:

   ```ini
   [Service]
   Environment="EDEXT_SOAP_BASE_URL=http://IP_CENTRAL:9128/edext/soap"
   Environment="EDEXT_UPLOAD_DIR=/var/lib/tomcat10/uploads"
   ReadWritePaths=/var/lib/tomcat10/uploads
   ```

   Guardá el archivo y reiniciá Tomcat:

   ```sh
   sudo systemctl daemon-reload
   sudo systemctl restart tomcat10
   sudo systemctl status tomcat10 --no-pager
   ```

   Si el usuario de servicio no se llama `tomcat`, usá el usuario y grupo que
   ejecutan Tomcat al crear la carpeta de cargas. El permiso `ReadWritePaths`
   permite escribir ahí aunque el servicio tenga el sistema de archivos
   protegido.
4. Si UFW está activo, permití que los equipos de la red abran la Web. Cambiá
   la subred del ejemplo por la tuya:

   ```sh
   sudo ufw allow from 192.168.1.0/24 to any port 8080 proto tcp
   ```

5. Desde un equipo de la red, abrí `http://IP_WEB:8080/web/`. Si no carga,
   comprobá primero que `http://IP_CENTRAL:9128/edext/soap/curso?wsdl` responda
   desde la PC Web.

## 4. Imágenes guardadas desde la Estación (opcional)

Las imágenes cargadas desde formularios Web se guardan en la PC Web, en la
carpeta `upload.dir` o `EDEXT_UPLOAD_DIR`. Tomcat necesita permiso de escritura
en esa carpeta.

Las imágenes guardadas desde la Estación están en la PC Central. Para que la
Web también pueda mostrarlas, compartí esa carpeta con permiso de **lectura**
para la PC Web y configurá `EDEXT_IMAGES_DIR` en Tomcat. Si no necesitás ver
esas imágenes desde la Web, podés saltear esta sección.

### Ubuntu Central a Ubuntu Web usando NFS

En la PC Central, creá el directorio y compártelo solo con la IP de la PC Web:

```sh
sudo apt install nfs-kernel-server
sudo mkdir -p /srv/edext/imagenes
sudo chown TU_USUARIO:TU_USUARIO /srv/edext/imagenes
sudo nano /etc/exports
```

Reemplazá `TU_USUARIO` por el nombre que muestra `whoami`. La cuenta que inicia
la Estación debe poder guardar archivos ahí; Tomcat necesita permiso de lectura
en las carpetas y archivos compartidos.

Agregá una línea, reemplazando `IP_WEB`:

```text
/srv/edext/imagenes IP_WEB(ro,sync,no_subtree_check)
```

En `/home/TU_USUARIO/.edEXT/servidor.properties`, apuntá la Estación a esa
carpeta:

```properties
directorios.imagenes=/srv/edext/imagenes
```

Reiniciá el Central para que tome la nueva carpeta y aplicá la exportación:

```sh
sudo exportfs -ra
sudo exportfs -v
```

En la PC Web:

```sh
sudo apt install nfs-common
sudo mkdir -p /mnt/edext-imagenes
sudo mount -t nfs -o ro IP_CENTRAL:/srv/edext/imagenes /mnt/edext-imagenes
ls /mnt/edext-imagenes
```

Si los archivos aparecen, agregá esta variable al archivo
`/etc/systemd/system/tomcat10.service.d/edext.conf` de la PC Web:

```ini
Environment="EDEXT_IMAGES_DIR=/mnt/edext-imagenes"
```

Luego ejecutá `sudo systemctl daemon-reload` y
`sudo systemctl restart tomcat10`. Para que el montaje siga activo después de
reiniciar la PC Web, agregá a `/etc/fstab`:

```text
IP_CENTRAL:/srv/edext/imagenes /mnt/edext-imagenes nfs ro,_netdev,nofail 0 0
```

Si el montaje responde “access denied”, revisá que `IP_WEB` en
`/etc/exports` sea correcto y volvé a ejecutar `sudo exportfs -ra` en la PC
Central. No publiques NFS fuera de la red local.

### Central Windows a Web Windows

Compartí la carpeta de imágenes del Central desde Windows con permiso de
lectura para la cuenta que ejecuta el servicio Tomcat. En la PC Web, configurá
`imagenes.dir` en el `web.properties` del usuario de Tomcat con la ruta UNC,
escapando las barras como en este ejemplo:

```properties
imagenes.dir=\\\\IP_CENTRAL\\edext-imagenes
```

Reiniciá Tomcat. La cuenta del servicio debe tener permiso tanto en el recurso
compartido como en los permisos de Windows.

## 5. Cambiar o reemplazar una versión

- **Cambios de la Web**, incluido el acta: compilá con
  `mvn -f ServidorWeb_edEXT/pom.xml clean package` y reemplazá
  `web.war` en la PC Web. No hace falta cambiar el Central ni la base.
- **Cambios del Central:** reemplazá `servidor.jar` en la PC Central y volvé a
  iniciar el servicio. No reemplaces el JAR mientras esté ejecutándose.
- **Cambios de configuración:** reiniciá el proceso al que corresponde.

Hacé una copia del WAR o JAR anterior antes de reemplazarlo, así podés volver
atrás si la nueva versión no inicia.

## 6. Errores comunes

- **La Web dice que no llega al Central:** verificá que el servicio esté
  encendido, que `central.soap.url` o `EDEXT_SOAP_BASE_URL` use la IP real del
  Central y que el puerto `9128` esté permitido desde la PC Web.
- **El Central no conecta a MySQL:** revisá MySQL, `jdbc.url`, `jdbc.user` y
  `jdbc.password` en la PC Central. La PC Web no requiere acceso a MySQL.
- **La Web no puede guardar una imagen:** revisá `upload.dir` o
  `EDEXT_UPLOAD_DIR`, los permisos de Tomcat y, en Ubuntu, el ajuste
  `ReadWritePaths`.
- **Las imágenes de la Estación no aparecen en la Web:** revisá que el
  directorio esté compartido y montado, y que `EDEXT_IMAGES_DIR` apunte al
  montaje correcto.
- **No aparece el acta:** cada curso del programa debe tener una edición
  cerrada para el estudiante. No es necesario que la evaluación sea aprobada.

> **Cuidado:** la opción **Carga Total Automática** reemplaza los datos de la
> base. Usala solo en una base de pruebas.
