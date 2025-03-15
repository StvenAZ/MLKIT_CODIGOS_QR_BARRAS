// MainActivity.java
package com.example.mlkit_codigos_qr_barras;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView resultTextView;
    private Button uploadButton;
    private Button openLinkButton;
    private BarcodeScanner barcodeScanner;
    private String detectedUrl = null;

    // Lanzador para seleccionar imágenes
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        loadImageAndDetectBarcodes(imageUri);
                    }
                }
            }
    );

    // Lanzador para solicitar permisos
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    openImagePicker();
                } else {
                    Toast.makeText(MainActivity.this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        resultTextView = findViewById(R.id.resultTextView);
        uploadButton = findViewById(R.id.uploadButton);
        openLinkButton = findViewById(R.id.openLinkButton);

        // Configurar opciones del escáner para detectar todos los formatos
        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build();

        barcodeScanner = BarcodeScanning.getClient(options);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reiniciar la URL detectada y ocultar el botón
                detectedUrl = null;
                openLinkButton.setVisibility(View.GONE);
                checkPermissionAndPickImage();
            }
        });
    }

    private void checkPermissionAndPickImage() {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openImagePicker();
        } else if (shouldShowRequestPermissionRationale(permission)) {
            Toast.makeText(this, "Se necesita permiso para acceder a las imágenes", Toast.LENGTH_LONG).show();
            requestPermissionLauncher.launch(permission);
        } else {
            requestPermissionLauncher.launch(permission);
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void loadImageAndDetectBarcodes(Uri imageUri) {
        try {
            // Mostrar la imagen seleccionada
            imageView.setImageURI(imageUri);

            // Crear InputImage para ML Kit
            InputImage inputImage = InputImage.fromFilePath(this, imageUri);

            // Informar al usuario que se está analizando la imagen
            resultTextView.setText("Analizando imagen...");

            barcodeScanner.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                        @Override
                        public void onSuccess(List<Barcode> barcodes) {
                            if (barcodes.isEmpty()) {
                                resultTextView.setText("No se detectaron códigos en la imagen");
                            } else {
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int i = 0; i < barcodes.size(); i++) {
                                    Barcode barcode = barcodes.get(i);
                                    int valueType = barcode.getValueType();
                                    String typeStr;

                                    switch (valueType) {
                                        case Barcode.TYPE_URL:
                                            typeStr = "URL";
                                            String urlString = barcode.getRawValue();
                                            // Solo se maneja el primer URL detectado
                                            if (urlString != null && detectedUrl == null) {
                                                detectedUrl = urlString;
                                                // Mostrar el botón para abrir el link y asignar su acción
                                                openLinkButton.setVisibility(View.VISIBLE);
                                                openLinkButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        try {
                                                            Uri uri = Uri.parse(detectedUrl);
                                                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                            startActivity(intent);
                                                        } catch (Exception e) {
                                                            Toast.makeText(MainActivity.this, "URL inválida: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                            break;
                                        case Barcode.TYPE_CONTACT_INFO:
                                            typeStr = "Contacto";
                                            break;
                                        case Barcode.TYPE_EMAIL:
                                            typeStr = "Email";
                                            break;
                                        case Barcode.TYPE_PHONE:
                                            typeStr = "Teléfono";
                                            break;
                                        case Barcode.TYPE_SMS:
                                            typeStr = "SMS";
                                            break;
                                        case Barcode.TYPE_TEXT:
                                            typeStr = "Texto";
                                            break;
                                        case Barcode.TYPE_WIFI:
                                            typeStr = "WiFi";
                                            break;
                                        case Barcode.TYPE_GEO:
                                            typeStr = "Ubicación";
                                            break;
                                        case Barcode.TYPE_CALENDAR_EVENT:
                                            typeStr = "Evento";
                                            break;
                                        case Barcode.TYPE_DRIVER_LICENSE:
                                            typeStr = "Licencia";
                                            break;
                                        case Barcode.TYPE_ISBN:
                                            typeStr = "ISBN";
                                            break;
                                        case Barcode.TYPE_PRODUCT:
                                            typeStr = "Producto";
                                            break;
                                        default:
                                            typeStr = "Desconocido";
                                            break;
                                    }

                                    String formatStr;
                                    switch (barcode.getFormat()) {
                                        case Barcode.FORMAT_QR_CODE:
                                            formatStr = "QR Code";
                                            break;
                                        case Barcode.FORMAT_CODE_128:
                                            formatStr = "Code 128";
                                            break;
                                        case Barcode.FORMAT_CODE_39:
                                            formatStr = "Code 39";
                                            break;
                                        case Barcode.FORMAT_EAN_13:
                                            formatStr = "EAN-13";
                                            break;
                                        case Barcode.FORMAT_EAN_8:
                                            formatStr = "EAN-8";
                                            break;
                                        case Barcode.FORMAT_UPC_A:
                                            formatStr = "UPC-A";
                                            break;
                                        case Barcode.FORMAT_UPC_E:
                                            formatStr = "UPC-E";
                                            break;
                                        case Barcode.FORMAT_PDF417:
                                            formatStr = "PDF417";
                                            break;
                                        case Barcode.FORMAT_AZTEC:
                                            formatStr = "Aztec";
                                            break;
                                        case Barcode.FORMAT_DATA_MATRIX:
                                            formatStr = "Data Matrix";
                                            break;
                                        case Barcode.FORMAT_ITF:
                                            formatStr = "ITF";
                                            break;
                                        case Barcode.FORMAT_CODABAR:
                                            formatStr = "Codabar";
                                            break;
                                        default:
                                            formatStr = "Desconocido";
                                            break;
                                    }

                                    stringBuilder.append("Código ").append(i + 1).append(":\n");
                                    stringBuilder.append("Formato: ").append(formatStr).append("\n");
                                    stringBuilder.append("Tipo: ").append(typeStr).append("\n");
                                    stringBuilder.append("Valor: ").append(barcode.getRawValue()).append("\n\n");
                                }
                                resultTextView.setText(stringBuilder.toString());
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            resultTextView.setText("Error al procesar la imagen: " + e.getMessage());
                        }
                    });

        } catch (IOException e) {
            Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        barcodeScanner.close();
    }
}