package com.odelli.autoupdate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;



public class Updater {

    private static final String RELEASE = "autoupdate.apk"; //nome do arquivo .apk quando ele estiver sendo criado na pasta /Download em seu device android

    private static Context context;
    private ProgressDialog dialog;

    public Updater(Context context) { //recebe o contexto da aplicação
        Updater.setContext(context);
        update();
    }

    public void update() { //metodo principal para ser executado pela outra classe para atualizar

        final String urlDownload="https://drive.google.com/uc?authuser=0&id=15eOij4ZdHwHvkHr2wTbwSzthB6OZzkEO&export=download"; //local do arquivo .apk atualizado
        //final Dialog dialog = new Dialog(getContext());
        //dialog.showDialog(true, "Aguarde! atualizando versão de sistema");

        dialog = ProgressDialog.show(context, "",
                "Aguarde! atualizando versão de sistema...", true);
        dialog.show();



        new Thread (new Runnable() {

            public void run() {
                //baixar arquivo
                try {
                    final String PATH = downloadFile(urlDownload);


                    installApplication(PATH);
                    // unInstallApplication("package:com.rsoftsolucoes.mobile.android");
                } catch (IOException ex) {

                    System.out.println(ex.toString());

                    //Toast.makeText(getContext().getApplicationContext(),
                    //        ex.getMessage(), Toast.LENGTH_LONG).show();
                } finally {
                    dialog.dismiss();


                   // dialog.showDialog(false, "atualizado");
                }
            }
        }).start();





    }
    //faz o download do arquivo especificado na url
    private static String downloadFile(String urlDownload)
            throws MalformedURLException, IOException, ProtocolException,
            FileNotFoundException {
        URL url = new URL(urlDownload);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod("GET");
        c.setDoOutput(true);
        c.connect();

        // final String PATH =
        // "/data/data/com.rsoftsolucoes.mobile.android/files/" + RELEASE;
        final String PATH = "/mnt/sdcard/download/" + RELEASE;
        FileOutputStream fos = new FileOutputStream(new File(PATH));

        String command = "chmod 777 " + PATH;

        Runtime.getRuntime().exec(command);

        InputStream is = c.getInputStream();

        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = is.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
        }
        fos.close();
        is.close();
        return PATH;
    }
    //metodo para instalar o arquivo .apk
    private static void installApplication(final String PATH) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(PATH)),
                "application/vnd.android.package-archive");
        getContext().startActivity(intent);
    }

    //metodo para desinstalar o programa antes de instalar o novo
    public static void unInstallApplication(String packageName)// Specific
// package Name
// Uninstall.
    {
// Uri packageURI = Uri.parse(“package:com.CheckInstallApp”);
        Uri packageURI = Uri.parse(packageName.toString());
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
        getContext().startActivity(uninstallIntent);
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        Updater.context = context;
    }
}