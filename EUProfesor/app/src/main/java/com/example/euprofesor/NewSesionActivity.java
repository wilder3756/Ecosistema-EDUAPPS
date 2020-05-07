package com.example.euprofesor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.ArrayList;

public class NewSesionActivity extends AppCompatActivity {

    private EditText proposito, fechaInicio, fechaCierre;
    private Spinner areas;
    private ArrayList<String> nombreareas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sesion);

        proposito = findViewById(R.id.edit_Proposito);
        areas = findViewById(R.id.sp_areas);
        fechaInicio = findViewById(R.id.edit_fechainicio);
        fechaCierre = findViewById(R.id.edit_fechacierre);

        nombreareas = new ArrayList<>();
        consultar();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, nombreareas);
        areas.setAdapter(adapter);
    }

    /**
     * Metodo para agregar una nueva sesion con 3 actividades y cada una con 3 recursos
     * @param v
     */
    public void btnAgregar(View v){
        String proposito = this.proposito.getText().toString();
        String fechaInicio = this.fechaInicio.getText().toString();
        String fechaCierre = this.fechaCierre.getText().toString();

        if(proposito.isEmpty())
            Toast.makeText(this,"Debe escribir un proposito",Toast.LENGTH_SHORT).show();
        else if(areas.getSelectedItemPosition()==0)
            Toast.makeText(this,"Debe seleccionar un area",Toast.LENGTH_SHORT).show();
        else if(fechaInicio.isEmpty())
            Toast.makeText(this,"Debe escribir una fecha de inicio",Toast.LENGTH_SHORT).show();
        else if(fechaCierre.isEmpty())
            Toast.makeText(this,"Debe escribir una fecha de cierre",Toast.LENGTH_SHORT).show();
        else if(validarFecha(fechaInicio))
            Toast.makeText(this, "La fecha de inicio no tiene el formato correcto (AAAA-MM-DD)", Toast.LENGTH_SHORT).show();
        else if(validarFecha(fechaCierre))
            Toast.makeText(this, "La fecha de cierre no tiene el formato correcto (AAAA-MM-DD)", Toast.LENGTH_SHORT).show();
        else{
            try {
                //Inserto una nueva sesion
                String[][] data = MainActivity.apiRest.setData("sesiones","ID,Area_ID,Descripcion,Fecha_Creacion,Fecha_Cierre",
                        "0,"+MainActivity.idArea+","+proposito+","+fechaInicio+","+fechaCierre);

                String profe="oscar";
                String idSesion= data[0][0];
                String actividad;
                int tiempo = 20;

                //Con el ID de la nueva sesion, inserto las actividades
                for(int i=0; i<3; i++){
                    actividad="Descripcion de la actividad estatica "+(i+1);
                    data = MainActivity.apiRest.setData("actividades","ID,Sesion_ID,Descripcion,Tiempo",
                            "0,"+idSesion+","+actividad+","+tiempo);

                    String idActividad = data[0][0];
                    String recurso1="home/admin/www/"+profe+"/"+idSesion+"/1/imagen.jpg";
                    String recurso2="home/admin/www/"+profe+"/"+idSesion+"/2/documento.docx";
                    String recurso3="home/admin/www/"+profe+"/"+idSesion+"/3/app.apk";

                    //Con el ID de la actividad, inserto los recursos
                    MainActivity.apiRest.setData("recursos","ID,Actividad_ID,Hipervinculo","0,"+idActividad+","+recurso1);
                    MainActivity.apiRest.setData("recursos","ID,Actividad_ID,Hipervinculo","0,"+idActividad+","+recurso2);
                    MainActivity.apiRest.setData("recursos","ID,Actividad_ID,Hipervinculo","0,"+idActividad+","+recurso3);
                }

                Toast.makeText(this,"Sea creado la sesion con ID: "+idSesion,Toast.LENGTH_SHORT).show();
                this.proposito.setText("");
                this.areas.setSelection(0);
                this.fechaInicio.setText("");
                this.fechaCierre.setText("");

            } catch (Exception e) {
                Toast.makeText(this,"Error al insertar: "+e.getMessage(),Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }


        }
    }

    /**
     * Metodo para validar el formato de la fecha AAAA-MM-DD o AAAA/MM/DD y los valores
     * @param fecha
     * @return
     */
    private boolean validarFecha(String fecha){
        String[] formato = fecha.split("-").length <  fecha.split("/") .length ? fecha.split("/") : fecha.split("-");
        return formato.length != 3 || formato[0].length() != 4 || formato[1].length() != 2
                || formato[2].length() != 2 || Integer.parseInt(formato[0]) < 0 || Integer.parseInt(formato[1]) < 0
                || Integer.parseInt(formato[1]) > 12 || Integer.parseInt(formato[2]) < 0 || Integer.parseInt(formato[2]) > 31;
    }

    /**
     * Metodo para consultar y establecer los tipos de areas
     */
    private void consultar(){
        try {
            String[][] data = MainActivity.apiRest.getData("areas","Nombre");
            nombreareas.add("Areas");
            for (int i=0; i<data.length;i++)
                nombreareas.add(data[i][0]);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error ApiRest: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /*Codigo para cargar archivos
    public void btnCargar(final View v){
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("* /*");
        i.addCategory(Intent.CATEGORY_OPENABLE); //Abrir el Choose File del dispositivo movil
        startActivityForResult(Intent.createChooser(i,"Choose File"),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED)
            Toast.makeText(getApplicationContext(), "No se eligió un archivo", Toast.LENGTH_SHORT).show();
        if ((resultCode == RESULT_OK) && (requestCode == 1)) {
            //Procesar el resultado
            Uri uri = data.getData(); //obtener el uri content
            String ftpPath = "/home/admin/wwww";
            String localFile = GetPathUtil.getPath(getApplicationContext(), uri);
            String name = new File(localFile).getName();
            new SimpleTask(new View(getApplicationContext()), false).execute(ftpPath.split("@"),name.split("@"),localFile.split("@"));
        }
    }
    /**/
}