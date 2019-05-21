package c.controle.controlevacas;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                //.setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            //Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            System.out.println("permission granted");
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            //Toast.makeText(MainActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            System.out.println("permission denied");
        }


    };

    public void produtores(View v){
        Intent i = new Intent(MainActivity.this,ProdutoresLista.class);
        startActivity(i);
    }
    public void cmt(View v){
        Intent i = new Intent(MainActivity.this,CmtLista.class);
        startActivity(i);
    }
    public void ccs(View v){
        Intent i = new Intent(MainActivity.this,CCSLista.class);
        startActivity(i);

    }
    public void controle_leiteiro(View v){
        Intent i = new Intent(MainActivity.this,ControleLeiteiroLista.class);
        startActivity(i);
    }
    public void sair(View v){
        finish();
    }
}
