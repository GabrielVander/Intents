package vander.gabriel.intents;

import static android.content.Intent.ACTION_PICK;
import static android.content.Intent.ACTION_VIEW;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.PhoneNumberUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.URLUtil;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.File;

import vander.gabriel.intents.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private MainViewModel mainViewModel;

    private ActivityResultLauncher<Intent> showImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vander.gabriel.intents.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        showImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                final Intent pickImageIntent = result.getData();
                if (pickImageIntent != null) {
                    final Uri imageUri = pickImageIntent.getData();
                    launchActionIntent(imageUri.toString(), ACTION_VIEW);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        final int openInBrowserMenuItem = R.id.openInBrowserMenuItem;
        final int exitMenuItem = R.id.exitMenuItem;
        final int dialPhoneNumberMenuItem = R.id.dialPhoneNumberMenuItem;
        final int callPhoneNumberMenuItem = R.id.callPhoneNumberMenuItem;
        final int launchActionMenuItem = R.id.launchActionMenuItem;
        final int pickImageMenuItem = R.id.pickImageMenuItem;

        switch (id) {
            case openInBrowserMenuItem:
                openParameterAsWebLink();
                return true;
            case exitMenuItem:
                finish();
                return true;
            case dialPhoneNumberMenuItem:
                dialParameterAsPhoneNumber();
                return true;
            case callPhoneNumberMenuItem:
                callParameterAsPhoneNumber();
                return true;
            case launchActionMenuItem:
                openSecondFragment();
                return true;
            case pickImageMenuItem:
                pickImage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void pickImage() {
        File directory = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (directory != null) {
            Intent actionIntent = new Intent(ACTION_PICK);
            actionIntent.setDataAndType(Uri.parse(directory.getPath()), "image/*");
            showImageLauncher.launch(actionIntent);
        }
    }

    private void openSecondFragment() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.action_FirstFragment_to_SecondFragment);
    }

    private void callParameterAsPhoneNumber() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            int calPermissionCode = 101;
            ActivityCompat.requestPermissions(this, new String[]{(Manifest.permission.CALL_PHONE)},
                    calPermissionCode);
        } else {
            String parameter = mainViewModel.getParameter().getValue();
            parameter = getParameterAsPhoneNumber(parameter);

            if (parameter != null) {
                launchActionIntent(parameter, Intent.ACTION_CALL);
            }
        }
    }

    private void openParameterAsWebLink() {
        String parameter = mainViewModel.getParameter().getValue();
        parameter = getParameterAsUrl(parameter);

        if (parameter != null) {
            launchActionIntent(parameter, Intent.ACTION_VIEW);
        }
    }

    private void dialParameterAsPhoneNumber() {
        String parameter = mainViewModel.getParameter().getValue();
        parameter = getParameterAsPhoneNumber(parameter);

        if (parameter != null) {
            launchActionIntent(parameter, Intent.ACTION_DIAL);
        }
    }

    private void launchActionIntent(String parameter, String action) {
        Intent actionIntent = new Intent(action);
        actionIntent.setData(Uri.parse(parameter));
        startActivity(actionIntent);
    }

    private String getParameterAsUrl(String parameter) {
        if (parameter == null) return null;

        if (!parameter.startsWith("https://") && !parameter.startsWith("http://")) {
            parameter = "http://" + parameter;
        }

        return URLUtil.isValidUrl(parameter) ? parameter : null;
    }

    private String getParameterAsPhoneNumber(String parameter) {
        if (parameter == null) return null;

        if (!PhoneNumberUtils.isGlobalPhoneNumber(parameter)) return null;

        return String.format("tel: %s", parameter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}