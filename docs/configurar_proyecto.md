---
layout: doc_page
title: 0. Configurar un proyecto
description: Crear un proyecto usando libGDX y trascendentAR
lang: es
ref: simple_app
---

Al final de este tutorial tendrás un proyecto listo para crear una aplicación de realidad aumentada. El código de este tutorial lo puedes encontrar [aquí](https://github.com/Juankz/arSimpleApp)

## Requisitos
Si ya has usado libGDX antes, es posible que ya tengas la mayoría de Requisitos instalados, solo deberás descargar las libraríasRToolKit. Si no has tenido experiencia con libGDX, para poder completar este tutorial, debes tener instalado en la máquina:

* JDK 7 o mayor
* ADB instalado
* Android SDK con:
	* Mínimo SDK instalado debe ser SDK Platform 4.4
	* Android SDK Tools
	* Android SDK Tools Platform
	* Android Build Tools
* IntelliJ IDEA o Android Studio, cada cual tiene ventajas respecto al otro. Para este caso se usó IntelliJ IDEA.
* libGDX setup app, se puede descargar de la [página oficial](http://libgdx.badlogicgames.com/download.html)
* Archivos de configuración Librerías, donde se encuentran las librerías nativas de ARToolKit y el archivo de configuración de la cámara. Descargalo [aquí]({{site.github.url}}/downloads/config_files.zip) <a href="{{site.github.url}}/downloads/config_files.zip" class="icon fa-download"></a>

## Iniciar la aplicación de libGDX
E ingresar los datos correspondientes incluyendo el camino al SDK de Android, revisa la imagen para guiarte. Observa que únicamente dejé la casilla de Android marcada (Bajo Sub projects). También incluí la extension freetypefont para incluir lindas tipografías en mi aplicación.
Más información sobre este paso [aquí](https://github.com/Jackgris/wikiLibGDX_es/wiki/Crear-ejecutar-depurar-y-empaquetar-su-proyecto)


<center>
<img src="images/simpleapp_libgdxsetup.png" alt="Configurar proyecto de libGDX">
</center>

## Abrir en entorno de desarrollo
Mi proyecto fue creado en el directorio arSimpleApp de mi carpeta de usuario, el siguiente paso es abrirlo en nuestro IDE favorito, Android Studio o en mi caso IntelliJ IDEA

<center>
<img src="images/simpleapp_abrirenide.png" alt="Abrir en IDE">
</center>


## MODIFICAR ARCHIVOS

Bien, ahora que tenemos nuestro proyecto debemos realizar ciertos cambios para trabajar con realidad aumentada, el primero de ellos es modificar el archivo AndroidManifest agregando permisos para usar la cámara del dispositivo, modificar la versión sdk mínima de Android en caso de que exista una menor a 15. Y finalmente agregar la actividad de preferencias de cámara que nos permitirá ajustar la resolución deseada.

Si la versión del SDK es menor a 15 también se debe modificar el archivo build.gradle del módulo de android

<span class="image left"><img src="images/simpleapp_files2modify.png" alt="Archivos a modificar" /></span>

En AndroidManifest.xml

```xml
<!--Añadir permisos de cámara-->
<uses-permission android:name="android.permission.CAMERA" />
    <uses-feature android:name="android.hardware.camera.any" />
    <uses-feature
        android:name="android.hardware.camera"
        android:required="false" />
    <uses-feature
        android:name="android.hardware.camera.autofocus"
        android:required="false" />

<!--Modificar versión mínima de android sdk-->
<uses-sdk android:minSdkVersion="15" android:targetSdkVersion="25" />

...

<!-- Añadir Camera Preferences Activity, no tocar nada más -->
<application ... >
        <activity
            ...
        ></activity>
        <activity android:name="org.artoolkit.ar.base.camera.CameraPreferencesActivity"
                  android:label="Camera Prefs"
                  android:screenOrientation="landscape"
                  android:configChanges="keyboard|keyboardHidden|orientation|screenSize">
        </activity>
    </application>
```

En build.gradle

```groovy
defaultConfig {
    ...
    minSdkVersion 15
    ...
}
```

## Añadir las librerías nativas de ARToolKit

<span class="image right"><img src="images/simpleapp_nativelibs.png" alt="Archivos a modificar" /></span>

Simplemente extrae los archivos de configuración descargados en el paso de requerimientos, busca la carpeta _native libs_ y dentro encontrarás las librerías de ARToolKit. Copia las carpetas dentro del módulo android/libs. El resultado deberá ser similar a la imagen, con los archivos *libARWrapper.so* y *libc++_shared.so* en todas las arquitecturas: **arm64-v8a**, **armeabi**, **armeabi-v7a**, **x86** y **x86_64**

## Añadir la carpeta _Data_ al directorio _assets_
La carpeta _Data_ es muy importante, también la encontrarás en la carpeta _config files_ que descargaste, aquí se encuentra el archivo de configuración de la cámara: _camera_para.dat_ y también es donde colocaremos los archivos de marcadores (extensión _.patt_). Solo copiala dentro del directorioo _assets_ en el módulo de android

## Añadir trascendentAR al proyecto

Para esto es necesario modificar el archivo build.gradle que se encuentra en la raíz del proyecto (ver imagen).

<span class="image right"><img src="images/simpleapp_addtrascendentAR.png" alt="Archivos a modificar" /></span>

1. Dentro del archivo, buscar ```project(":android")``` y añadir en dependencies la línea ```compile 'org.glud.trascendentAR:trascendentAR-android:1.0-beta.1'```

2. De igual forma, buscar ```project(":core")``` y añadir en dependencies la línea ```compile 'org.glud.trascendentAR:trascendentAR-core:1.0-beta.1'```

El resultado será algo de este estilo:

```
project(":android") {
    apply plugin: "android"

    configurations { natives }

    dependencies {
        compile project(":core")
        compile 'org.glud.trascendentAR:trascendentAR-android:1.0-beta.1'
        compile "com.badlogicgames.gdx:gdx-backend-android:$gdxVersion"
        .
        .
        .
    }
}
project(":core") {
    apply plugin: "java"

    dependencies {
        compile 'org.glud.trascendentAR:trascendentAR-core:1.0-beta.1'
        compile "com.badlogicgames.gdx:gdx:$gdxVersion"
        compile "com.badlogicgames.gdx:gdx-freetype:$gdxVersion"
    }
}
```
Solo queda sincronizar gradle, un método a prueba de fallos es cerrar y abrir de nuevo el proyecto en el IDE. Probablemente verás un diálogo preguntando si importar cambios en gradle, **presiona importar**