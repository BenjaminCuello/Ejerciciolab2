# Verificador de Arquitectura en Capas

Este proyecto implementa una herramienta en Java para verificar si un sistema respeta una arquitectura en capas definida. Se considera una estructura de tres capas: Presentación (UI), Servicio (Service) y Persistencia (DAO).

## ¿Qué hace?

Analiza el código fuente de un proyecto Java y detecta si existen violaciones a las siguientes reglas arquitectónicas:

1. UI solo puede acceder a Service.  
2. Service solo puede acceder a DAO.  
3. No se permiten accesos directos entre UI y DAO.  
4. No se permiten dependencias cruzadas dentro de una misma capa.

## Tecnologías utilizadas

- JavaParser

## ¿Cómo usarlo?

1. Copiar la ruta de la carpeta 'src/main/java' del proyecto a analizar
2. Ejecuta la clase `VerificadorArquitectura` e insertarla

## Estructura esperada

El sistema analizado debe organizar las clases en paquetes correspondientes a cada capa:

- `ui` para Presentación  
- `service` para Lógica
- `dao` para Persistencia

## Autores

- Branco Abalos
- Benjamin Cuello
