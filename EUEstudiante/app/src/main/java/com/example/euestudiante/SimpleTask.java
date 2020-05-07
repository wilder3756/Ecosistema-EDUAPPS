package com.example.euestudiante;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;

public class SimpleTask extends AsyncTask<String[], Float, String> {

    private View item;
    private  boolean isDescarga;
    private ImageButton btnDescarga;
    private AlertDialog dialog;

    public SimpleTask(View item, boolean isDescarga) {
        this.item = item;
        this.isDescarga = isDescarga;
    }

    /**
     * Lo ejecuta el hilo principal antes de que inicie el hilo hijo
     * Deshabilita el boton de descarga y muestra la ventana de dialogo
     */
    @Override
    protected void onPreExecute() {
        try {
            btnDescarga = item.findViewById(R.id.btnDescargar);
            btnDescarga.setEnabled(false);
            alert();
            MainActivity.client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Lo ejecuta el hilo hijo en segundo plano
     * Empieza la descargar o carga de los n archivos
     * @param params
     * @return
     */
    @Override
    protected String doInBackground(final String[][] params) {
        String resul="";
        try {
            float progreso=0.0f;
            for(int i=0; i<params[2].length; i++){//Iterar sobre cada ruta
                if(isDescarga) MainActivity.client.downloadFile(params[0][0],params[1][i],params[2][i]);
                else MainActivity.client.addFile(params[0][0],params[1][i],params[2][i]);
                progreso+=1;
                publishProgress(progreso);
            }
        } catch (IOException e) {
            resul = e.getMessage();
            cancel(true);
            e.printStackTrace();
        } catch (Exception e) {
            resul = e.getMessage();
            cancel(true);
            e.printStackTrace();
        }
        return resul;
    }

    /**
     * Lo ejecuta el hilo hijo despues de terminar su codigo
     * Habilita el boton de descargar y cierra la ventana de dialogo
     * @param result
     */
    @Override
    protected void onPostExecute(String result) {
        try {
            btnDescarga.setEnabled(true);
            if(isDescarga) Toast.makeText(item.getContext(), "Descarga exitosa", Toast.LENGTH_SHORT).show();
            else Toast.makeText(item.getContext(), "Carga exitosa", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            MainActivity.client.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Si se llega cancelar por alguna razon para el hilo hijo
     * Habilita el boton de descargar y cierra la ventana de dialogo
     * @param result
     */
    @Override
    protected void onCancelled(String result) {
        try {
            btnDescarga.setEnabled(true);
            if(isDescarga) Toast.makeText(item.getContext(), "Se canceló la descargar", Toast.LENGTH_SHORT).show();
            else Toast.makeText(item.getContext(), "Se canceló la carga", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            MainActivity.client.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Lo ejecuata el hilo hijo por si se quiere mostrar una barra de progreso
     * @param values
     */
    @Override
    protected void onProgressUpdate(Float... values) {
    }

    /**
     * Metodo auxiliar para mostar la ventana de dialogo por si se desea cancelar la descarga
     */
    private void alert(){
        TextView actividad, id, descripcion;
        Button btnCancelar;

        id = item.findViewById(R.id.text_actividad);

        AlertDialog.Builder message = new AlertDialog.Builder(item.getContext());
        View ventana = LayoutInflater.from(item.getContext()).inflate(R.layout.dialog_descarga,null);
        message.setView(ventana);
        message.setCancelable(false);
        dialog = message.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        actividad = ventana.findViewById(R.id.text_actividad);
        actividad.setText(id.getText().toString());
        descripcion = ventana.findViewById(R.id.text_descripcion);
        descripcion.setText("Descargando los recursos de "+actividad.getText().toString()+"...");
        btnCancelar = ventana.findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel(true);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}