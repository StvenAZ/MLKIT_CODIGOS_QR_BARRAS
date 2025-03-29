# Detector de Códigos QR y Barras

## Descripción  
¡Una aplicación Android que utiliza **Google ML Kit** para detectar códigos QR y otros códigos de barras en imágenes seleccionadas desde la galería!.

---

## Funcionalidades  

1. **Detección de Códigos QR y Barras**  
   - Utiliza ML Kit para identificar códigos de barra y QR en imágenes.  
   - Compatible con múltiples formatos (QR, Code 128, Code 39, EAN-13, etc.).

2. **Selección de Imagen**  
   - Permite al usuario elegir una imagen desde la galería.  
   - Muestra la imagen seleccionada y presenta los resultados del escaneo en una tarjeta.

3. **Interfaz Intuitiva y Moderna**  
   - Interfaz basada en Material Components con un diseño limpio y atractivo.  
   - Visualización clara de resultados y botones de acción consistentes.

4. **Redirección de URL**  
   - Si se detecta un código de tipo URL, se habilita un botón para que el usuario decida abrir el enlace en el navegador.

---

## Capturas de Pantalla y Explicación  

### 1. Interfaz Principal  
![qr1](https://github.com/user-attachments/assets/3087ad3e-0e54-40a8-b563-dd3e9bede33b)

- **Descripción:** Pantalla principal de la aplicación con la barra superior, imagen de muestra y botones de acción.  
- **Detalles:** La barra superior muestra el título "Detector de Códigos", mientras que los botones permiten seleccionar una imagen y, en caso de detectar un URL, se muestra la opción para abrir el enlace.

### 2. Resultados del Escaneo  
![qr2](https://github.com/user-attachments/assets/78b75ce4-2418-4cc6-8860-1636a8d4f390)
![qr3](https://github.com/user-attachments/assets/406a713d-235f-4c1f-b3c3-7fc435eab732)

- **Descripción:** Área donde se muestran los detalles del código detectado, incluyendo el tipo, formato y valor.  
- **Detalles:** Cuando se detecta un URL, se habilita un botón "Abrir Link" para redireccionar al navegador.

---

## Cómo Funciona  

1. **Flujo de Uso**  
   - Presiona el botón **"Seleccionar Imagen"** para elegir una imagen desde la galería.  
   - La aplicación procesa la imagen utilizando ML Kit y detecta cualquier código presente.  
   - Los resultados se muestran en pantalla; si se detecta un URL, se habilita el botón **"Abrir Link"** para que puedas visitar el enlace.

2. **Tecnologías Clave**  
   - **Google ML Kit:** Procesa imágenes para detectar y leer códigos QR y de barras.  
   - **Material Components:** Se utiliza para lograr una interfaz moderna y consistente.  
   - **Intents de Android:** Permiten la selección de imagen y la apertura de enlaces en el navegador.


